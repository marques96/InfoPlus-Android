package br.com.infoplus.infoplus.features.report

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import br.com.infoplus.infoplus.features.report.model.ReportStep
import br.com.infoplus.infoplus.features.report.steps.ReportContextStep
import br.com.infoplus.infoplus.features.report.steps.ReportDetailsStep
import br.com.infoplus.infoplus.features.report.steps.ReportFinalStep
import br.com.infoplus.infoplus.features.report.steps.ReportIntroStep
import br.com.infoplus.infoplus.navigation.Routes

@Composable
fun ReportScreen(
    navController: NavHostController,
    vm: ReportViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val shouldShowIntro by vm.shouldShowIntro.collectAsState()

    var currentStep by rememberSaveable { mutableStateOf(ReportStep.INFO_BASICA) }
    var hasLocationPermission by rememberSaveable { mutableStateOf(false) }
    var pendingLocationCapture by rememberSaveable { mutableStateOf(false) }
    var introDismissedThisSession by rememberSaveable { mutableStateOf(false) }
    var dontShowIntroAgain by rememberSaveable { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { res ->
        val granted =
            (res[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                    (res[Manifest.permission.ACCESS_COARSE_LOCATION] == true)

        hasLocationPermission = granted

        if (pendingLocationCapture) {
            pendingLocationCapture = false
            vm.captureLocation(granted)
        }
    }

    val showIntroScreen = shouldShowIntro == true && !introDismissedThisSession

    BackHandler {
        if (showIntroScreen) {
            navController.popBackStack()
            return@BackHandler
        }

        currentStep = previousStepOrNull(currentStep) ?: run {
            navController.popBackStack()
            return@BackHandler
        }
    }

    if (shouldShowIntro == null) return

    if (showIntroScreen) {
        ReportIntroStep(
            dontShowAgainChecked = dontShowIntroAgain,
            onDontShowAgainChange = { dontShowIntroAgain = it },
            onNext = {
                if (dontShowIntroAgain) {
                    vm.setSkipReportIntro(true)
                }
                introDismissedThisSession = true
            },
            onBack = {
                navController.popBackStack()
            }
        )
        return
    }

    AnimatedContent(
        targetState = currentStep,
        transitionSpec = {
            val movingForward = targetState.ordinal > initialState.ordinal

            if (movingForward) {
                (slideInHorizontally { fullWidth -> fullWidth / 3 } + fadeIn())
                    .togetherWith(
                        slideOutHorizontally { fullWidth -> -fullWidth / 4 } + fadeOut()
                    )
            } else {
                (slideInHorizontally { fullWidth -> -fullWidth / 3 } + fadeIn())
                    .togetherWith(
                        slideOutHorizontally { fullWidth -> fullWidth / 4 } + fadeOut()
                    )
            }.using(SizeTransform(clip = false))
        },
        label = "report-step-transition"
    ) { step ->
        when (step) {
            ReportStep.INFO_BASICA -> {
                ReportContextStep(
                    selectedCategory = state.draft.category,
                    onSelectCategory = vm::setCategory,
                    selectedVictimType = state.draft.victimType,
                    onSelectVictimType = vm::setVictimType,
                    selectedGender = state.draft.victimGender,
                    onSelectGender = vm::setVictimGender,
                    onNext = { currentStep = ReportStep.RELATO },
                    onBack = { navController.popBackStack() }
                )
            }

            ReportStep.RELATO -> {
                ReportDetailsStep(
                    title = state.draft.title,
                    onTitleChange = vm::setTitle,
                    description = state.draft.description,
                    onDescriptionChange = vm::setDescription,
                    attachments = state.draft.attachments,
                    onAddAttachment = { uri, type -> vm.addAttachment(uri, type) },
                    onRemoveAttachment = { uri -> vm.removeAttachment(uri) },
                    onNext = { currentStep = ReportStep.LOCAL_PRIVACIDADE },
                    onBack = { currentStep = ReportStep.INFO_BASICA }
                )
            }

            ReportStep.LOCAL_PRIVACIDADE -> {
                ReportFinalStep(
                    draft = state.draft,
                    useCurrentLocation = state.draft.useCurrentLocation,
                    onUseCurrentLocationChange = vm::setUseCurrentLocation,
                    isGettingLocation = state.isGettingLocation,
                    onCaptureLocation = {
                        if (hasLocationPermission) {
                            vm.captureLocation(true)
                        } else {
                            pendingLocationCapture = true
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    isAnonymous = state.draft.isAnonymous,
                    onAnonymousChange = vm::setAnonymous,
                    acceptedTerms = state.draft.acceptedTerms,
                    onAcceptedTermsChange = vm::setTerms,
                    canSubmit = state.canSubmit,
                    isSubmitting = state.isSubmitting,
                    onSubmit = {
                        val offline = !state.isOnline
                        vm.submit {
                            navController.navigate("${Routes.REPORT_SUCCESS}?offline=$offline") {
                                popUpTo(Routes.REPORT) { inclusive = true }
                            }
                        }
                    },
                    onBack = { currentStep = ReportStep.RELATO }
                )
            }
        }
    }
}

private fun previousStepOrNull(
    step: ReportStep
): ReportStep? {
    return when (step) {
        ReportStep.INFO_BASICA -> null
        ReportStep.RELATO -> ReportStep.INFO_BASICA
        ReportStep.LOCAL_PRIVACIDADE -> ReportStep.RELATO
    }
}
