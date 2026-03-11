package br.com.infoplus.infoplus.features.report

import android.Manifest
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import br.com.infoplus.infoplus.features.report.model.ReportStep
import br.com.infoplus.infoplus.features.report.model.VictimType
import br.com.infoplus.infoplus.features.report.steps.ReportAttachmentsStep
import br.com.infoplus.infoplus.features.report.steps.ReportCategoryStep
import br.com.infoplus.infoplus.features.report.steps.ReportDescriptionStep
import br.com.infoplus.infoplus.features.report.steps.ReportIntroStep
import br.com.infoplus.infoplus.features.report.steps.ReportLocationStep
import br.com.infoplus.infoplus.features.report.steps.ReportPrivacyStep
import br.com.infoplus.infoplus.features.report.steps.ReportReviewStep
import br.com.infoplus.infoplus.features.report.steps.ReportVictimGenderStep
import br.com.infoplus.infoplus.features.report.steps.ReportVictimTypeStep
import br.com.infoplus.infoplus.navigation.Routes

@Composable
fun ReportScreen(
    navController: NavHostController,
    vm: ReportViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    var currentStep by remember { mutableStateOf(ReportStep.INTRO) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var pendingLocationCapture by remember { mutableStateOf(false) }

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

    fun nextFromVictimType() {
        currentStep = if (state.draft.victimType == VictimType.OTHER) {
            ReportStep.VICTIM_GENDER
        } else {
            ReportStep.DESCRIPTION
        }
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
            }.using(
                SizeTransform(clip = false)
            )
        },
        label = "report-step-transition"
    ) { step ->

        when (step) {
            ReportStep.INTRO -> {
                ReportIntroStep(
                    onNext = { currentStep = ReportStep.CATEGORY }
                )
            }

            ReportStep.CATEGORY -> {
                ReportCategoryStep(
                    selectedCategory = state.draft.category,
                    onSelectCategory = vm::setCategory,
                    onNext = { currentStep = ReportStep.VICTIM_TYPE },
                    onBack = { currentStep = ReportStep.INTRO }
                )
            }

            ReportStep.VICTIM_TYPE -> {
                ReportVictimTypeStep(
                    selectedVictimType = state.draft.victimType,
                    onSelectVictimType = vm::setVictimType,
                    onNext = { nextFromVictimType() },
                    onBack = { currentStep = ReportStep.CATEGORY }
                )
            }

            ReportStep.VICTIM_GENDER -> {
                ReportVictimGenderStep(
                    selectedGender = state.draft.victimGender,
                    onSelectGender = vm::setVictimGender,
                    onNext = { currentStep = ReportStep.DESCRIPTION },
                    onBack = { currentStep = ReportStep.VICTIM_TYPE }
                )
            }

            ReportStep.DESCRIPTION -> {
                ReportDescriptionStep(
                    title = state.draft.title,
                    description = state.draft.description,
                    onTitleChange = vm::setTitle,
                    onDescriptionChange = vm::setDescription,
                    onNext = { currentStep = ReportStep.LOCATION },
                    onBack = {
                        currentStep = if (state.draft.victimType == VictimType.OTHER) {
                            ReportStep.VICTIM_GENDER
                        } else {
                            ReportStep.VICTIM_TYPE
                        }
                    }
                )
            }

            ReportStep.LOCATION -> {
                ReportLocationStep(
                    useCurrentLocation = state.draft.useCurrentLocation,
                    manualLocationText = state.draft.manualLocationText,
                    street = state.draft.street,
                    number = state.draft.number,
                    district = state.draft.district,
                    city = state.draft.city,
                    isGettingLocation = state.isGettingLocation,
                    hasValidLocation = state.hasValidLocation,
                    errorMessage = state.errorMessage,
                    onUseCurrentLocationChange = vm::setUseCurrentLocation,
                    onManualLocationChange = vm::setManualLocation,
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
                    onNext = { currentStep = ReportStep.ATTACHMENTS },
                    onBack = { currentStep = ReportStep.DESCRIPTION }
                )
            }

            ReportStep.ATTACHMENTS -> {
                ReportAttachmentsStep(
                    attachments = state.draft.attachments,
                    onAddAttachment = vm::addAttachment,
                    onRemoveAttachment = vm::removeAttachment,
                    onError = vm::setError,
                    onNext = { currentStep = ReportStep.PRIVACY },
                    onBack = { currentStep = ReportStep.LOCATION }
                )
            }

            ReportStep.PRIVACY -> {
                ReportPrivacyStep(
                    isAnonymous = state.draft.isAnonymous,
                    acceptedTerms = state.draft.acceptedTerms,
                    onAnonymousChange = vm::setAnonymous,
                    onAcceptedTermsChange = vm::setTerms,
                    onNext = { currentStep = ReportStep.REVIEW },
                    onBack = { currentStep = ReportStep.ATTACHMENTS }
                )
            }

            ReportStep.REVIEW -> {
                ReportReviewStep(
                    draft = state.draft,
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
                    onBack = { currentStep = ReportStep.PRIVACY }
                )
            }
        }
    }
}