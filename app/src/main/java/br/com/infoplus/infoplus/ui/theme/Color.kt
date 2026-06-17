package br.com.infoplus.infoplus.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta INFO+ (Violeta como identidade)
 *
 * Heurísticas atendidas:
 * - Identidade do projeto: VIOLETA (primary)
 * - Seriedade institucional sem "police-state look": Navy em surfaces/containers (apoio, não dominante)
 * - Tema escuro com contraste e conforto visual
 * - Semântica de status: sucesso/aviso para sincronização offline-first
 */

// ===== Brand =====
// Azul (identidade) - Inspirado no Waze, transmite clareza e acolhimento
val InfoPrimary = Color(0xFF0091EA)        // Azul vibrante e profissional
val InfoPrimaryDark = Color(0xFF01579B)    // Azul profundo

// Navy (apoio institucional / credibilidade)
val InfoNavy = Color(0xFF0B1F3B)
val InfoNavyDeep = Color(0xFF08162B)

// Acentos de acolhimento (Estilo Waze - tons mais vivos e amigáveis)
val InfoSecondary = Color(0xFF33CCFF)      // Azul Celeste (o azul do escudo que o usuário gostou)
val InfoTertiary = Color(0xFFFFDD00)       // Amarelo atenção/alerta

// ===== Light =====
val InfoBgLight = Color(0xFFF8FAFC)         // Cinza azulado bem claro e limpo
val InfoSurfaceLight = Color(0xFFFFFFFF)
val InfoSurfaceVariantLight = Color(0xFFF1F5F9)
val InfoOnLight = Color(0xFF0F172A)
val InfoOnLightMuted = Color(0xFF64748B)

// Containers
val InfoPrimaryContainerLight = Color(0xFFE0F2FE)
val InfoOnPrimaryContainerLight = Color(0xFF0369A1)

val InfoSecondaryContainerLight = Color(0xFFE0F7FA)
val InfoOnSecondaryContainerLight = Color(0xFF006064)

// ===== Dark =====
val InfoBgDark = Color(0xFF0F0F16)                  // escuro confortável (não preto puro)
val InfoSurfaceDark = Color(0xFF161622)
val InfoSurfaceVariantDark = Color(0xFF1F1F30)
val InfoOnDark = Color(0xFFEAEAF2)
val InfoOnDarkMuted = Color(0xFFB9B9C9)

val InfoPrimaryContainerDark = Color(0xFF2A2758)    // violeta “container”
val InfoOnPrimaryContainerDark = Color(0xFFEAEAF2)

val InfoSecondaryContainerDark = Color(0xFF14233E)  // navy container (seriedade)
val InfoOnSecondaryContainerDark = Color(0xFFEAF0FF)

// ===== Feedback / Semânticas =====
val InfoError = Color(0xFFB3261E)
val InfoSuccess = Color(0xFF1B9E77)   // "Sincronizado"
val InfoWarning = Color(0xFFF59E0B)   // "Salvo localmente / Pendente"

val InfoOutlineLight = Color(0xFFD3D2E6)
val InfoOutlineDark = Color(0xFF3A3A4A)
