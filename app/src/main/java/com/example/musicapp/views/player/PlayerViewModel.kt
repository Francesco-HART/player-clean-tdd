package com.example.musicapp.player

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.musicapp.v2.loader.application.LoadMusicDomainService
import com.example.musicapp.v2.loader.application.usecases.PreLoadMusicsUsecase
import com.example.musicapp.v2.loader.domain.FileLoaderRepository
import com.example.musicapp.v2.loader.infra.InMemoryFileLoaderRepository
import com.example.musicapp.v2.player.application.AppEvenBus
import com.example.musicapp.v2.player.application.ChangePlayerStateEvent
import com.example.musicapp.v2.player.application.EventBusProvider
import com.example.musicapp.v2.player.application.PlayerProvider
import com.example.musicapp.v2.player.application.SavePlayerStateDomainService
import com.example.musicapp.v2.player.application.usecases.AddAnalyticCommand
import com.example.musicapp.v2.player.application.usecases.AddAnalyticUsecase
import com.example.musicapp.v2.player.application.usecases.ChangePlayerTimeLineCommand
import com.example.musicapp.v2.player.application.usecases.ChangePlayerTimeLineUsecase
import com.example.musicapp.v2.player.application.usecases.PausePlayerUsecase
import com.example.musicapp.v2.player.application.usecases.PlayNextMusicUsecase
import com.example.musicapp.v2.player.application.usecases.PlayPreviousMusicUsecase
import com.example.musicapp.v2.player.domain.ChangeMusicNotification
import com.example.musicapp.v2.player.domain.Player
import com.example.musicapp.v2.player.domain.PlayerBuilder
import com.example.musicapp.v2.player.domain.PlayerStatus
import com.example.musicapp.v2.player.infra.DateNowProvider
import com.example.musicapp.v2.player.infra.InMemoryAnalytic
import com.example.musicapp.v2.player.infra.LocalFileLoaderRepository
import com.example.musicapp.v2.player.infra.StubPlayerProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


enum class PlayerActions {
    PLAY,
    PAUSE,
    NEXT,
    PREVIOUS,
    CHANGE_TIME_LINE
}

sealed class PlayerViewModelState(
    open val playerIsCheap: Boolean = false,
    open val error: String? = null,
) {

    object NeverPlayed : PlayerViewModelState()
    data class Loading(val player: Player, override val playerIsCheap: Boolean) :
        PlayerViewModelState(
            playerIsCheap = playerIsCheap
        )

    data class Failed(
        val player: Player,
        override val error: String
    ) : PlayerViewModelState(
        error = error,
        playerIsCheap = true
    )

    data class Success(
        val player: Player,
        override val playerIsCheap: Boolean,
        override val error: String?
    ) :
        PlayerViewModelState(
            playerIsCheap = playerIsCheap,
            error = error,
        )
}

class PlayerViewModel(
    private val eventBus: EventBusProvider,
    private val pausePlayerUsecase: PausePlayerUsecase,
    private val playerNextMusicUseCase: PlayNextMusicUsecase,
    private val playPreviousMusicUsecase: PlayPreviousMusicUsecase,
    private val changePlayerTimeLineUsecase: ChangePlayerTimeLineUsecase,
    private val addAnaliticsUsecase: AddAnalyticUsecase,
    private val preLoadMusicsUsecase: PreLoadMusicsUsecase

) : ViewModel() {
    private val state = MutableLiveData<PlayerViewModelState>()
    lateinit var medialPlayer: MediaPlayer
    fun getState(): LiveData<PlayerViewModelState> = state

    // Use for test
    fun setState(state: PlayerViewModelState) {
        this.state.postValue(state)
    }


    fun initMediaPlayer() {
        val media = MediaPlayer()
        AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        medialPlayer = media
    }

    suspend fun initViewModel() {

        // Need to justify this line with test but have no time
        state.postValue(
            PlayerViewModelState.Loading(
                player = PlayerBuilder().withStatus(
                    PlayerStatus.PAUSED
                ).build(),
                playerIsCheap = false
            )
        )

        subscribeToMusicChangeEvent()
        subscribeToChangePlayerStateEvent()
    }

    private fun subscribeToChangePlayerStateEvent() {
        GlobalScope.launch(Dispatchers.Default) {
            eventBus.subscribe<ChangePlayerStateEvent> { it
                ->
                if (it is ChangePlayerStateEvent) {
                    async { preLoadMusicsUsecase.execute() }
                    setState(
                        PlayerViewModelState.Success(
                            player = it.player,
                            playerIsCheap = if (state.value != null) state.value!!.playerIsCheap else false,
                            error = null
                        )
                    )

                }
            }
        }
    }

    private fun subscribeToMusicChangeEvent() {
        GlobalScope.launch(
            Dispatchers.Default
        ) {

            eventBus.subscribe<ChangeMusicNotification> {
                // TODO: Test logic of this subscription
                if (it is ChangeMusicNotification)
                    async {
                        addAnaliticsUsecase.execute(
                            AddAnalyticCommand(
                                it.player.listeningTimeInMs,
                                "robin",
                                it.player.musics[it.player.playedIndex].name,
                            )
                        )
                    }
            }
        }
    }

    fun toggleDisplay() {
        if (state.value is PlayerViewModelState.Success) {
            setState(
                PlayerViewModelState.Success(
                    player = (state.value as PlayerViewModelState.Success).player,
                    playerIsCheap = !(state.value as PlayerViewModelState.Success).playerIsCheap,
                    error = null
                )
            )
        } else if (state.value is PlayerViewModelState.Loading) {
            setState(
                PlayerViewModelState.Loading(
                    player = (state.value as PlayerViewModelState.Loading).player,
                    playerIsCheap = !(state.value as PlayerViewModelState.Loading).playerIsCheap,
                )
            )
        }
    }


    fun setMediaPlayer(player: Player) {
        when (player.status) {
            PlayerStatus.PLAYING -> playMediaPlayer(player.file)
            PlayerStatus.PAUSED -> pauseMediaPlayer()
            PlayerStatus.STOPPED -> stopMediaPlayer()
        }
    }

    fun playMediaPlayer(path: String) {
        medialPlayer.setDataSource(path)
        medialPlayer.start()
    }

    fun pauseMediaPlayer() {
        medialPlayer.pause()
    }

    fun stopMediaPlayer() {
        medialPlayer.stop()
    }

    fun releaseMediaPlayer() {
        medialPlayer.release()
    }

    // le reste des methodes sont deja test en usecase et ne peuvent pas etre teste ici car nous n'arrivont pas à tester la reception d'event
    suspend fun pausePlayer() {
        GlobalScope.launch(Dispatchers.Default) {
            pausePlayerUsecase.execute()
        }
    }


    fun optimisticByAction(
        player: Player, action: PlayerActions
    ) {
        val newPlayer = Player.fromData(player.data())
        when (action) {
            PlayerActions.PREVIOUS -> {
                newPlayer.playingTimeInMs = 0
            }

            PlayerActions.NEXT -> {
                newPlayer.playingTimeInMs = 0
                if (newPlayer.playedIndex == newPlayer.musics.size - 1) {
                    newPlayer.playingTimeInMs = 0
                } else {
                    newPlayer.playedIndex = newPlayer.playedIndex + 1
                }
            }

            else -> {}
        }

        setState(
            PlayerViewModelState.Loading(
                player = newPlayer,
                playerIsCheap = if (state.value != null) state.value!!.playerIsCheap else false
            )
        )
    }

    private fun optimisticUpdateState(
        action: PlayerActions
    ) {
        if (state.value is PlayerViewModelState.Success)
            optimisticByAction((state.value as PlayerViewModelState.Success).player, action)
        else if (
            state.value is PlayerViewModelState.Loading
        ) {
            optimisticByAction((state.value as PlayerViewModelState.Loading).player, action)
        } else if (
            state.value is PlayerViewModelState.Failed
        ) {
            optimisticByAction((state.value as PlayerViewModelState.Failed).player, action)
        }
    }

    suspend fun playNextMusic() {
        optimisticUpdateState(
            PlayerActions.NEXT
        )
        GlobalScope.async(Dispatchers.Default) {
            playerNextMusicUseCase.execute()
        }
    }

    suspend fun playPreviousMusic() {
        optimisticUpdateState(
            PlayerActions.PREVIOUS
        )
        GlobalScope.async(Dispatchers.Default) {
            playPreviousMusicUsecase.execute()
        }
    }

    suspend fun changePlayerTimeLine(timeInMs: Long) {
        GlobalScope.async(Dispatchers.Default) {
            changePlayerTimeLineUsecase.execute(ChangePlayerTimeLineCommand(timeInMs))
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])

                // Create a SavedStateHandle for this ViewModel from extras
                val savedStateHandle = extras.createSavedStateHandle()


                // need to use global app injector of dependency
                val eventBus = AppEvenBus()

                val analitycRepository = InMemoryAnalytic()

                val playerProvider = StubPlayerProvider()

                val dateProvider = DateNowProvider()

                val fileLoaderRepository = LocalFileLoaderRepository(
                    application.applicationContext
                )

                val savePlayerDomainService = SavePlayerStateDomainService(
                    playerProvider,
                    eventBus
                )
                val loadMusicDomainService = LoadMusicDomainService(
                    fileLoaderRepository
                )


                return PlayerViewModel(
                    AppEvenBus(),
                    PausePlayerUsecase(
                        playerProvider,
                        dateProvider,
                        savePlayerDomainService
                    ),
                    PlayNextMusicUsecase(
                        playerProvider,
                        dateProvider,
                        eventBus,
                        loadMusicDomainService,
                        savePlayerDomainService
                    ),
                    PlayPreviousMusicUsecase(
                        playerProvider,
                        dateProvider,
                        eventBus,
                        loadMusicDomainService,
                        savePlayerDomainService
                    ),
                    ChangePlayerTimeLineUsecase(
                        playerProvider,
                        savePlayerDomainService
                    ),
                    AddAnalyticUsecase(
                        analitycRepository,
                        dateProvider
                    ),
                    PreLoadMusicsUsecase(
                        playerProvider,
                        fileLoaderRepository,
                        loadMusicDomainService
                    ),
                ) as T
            }
        }
    }
}