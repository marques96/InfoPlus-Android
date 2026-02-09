package br.com.infoplus.infoplus.features.support

/**
 * MVP defensável:
 * - Recursos nacionais com telefone conhecido (ex.: 100, 180, 188, 136, 190, 192)
 * - Serviços que variam por estado/cidade entram como "Buscar unidade" (abre web/mapa),
 *   evitando inventar endereço/telefone local.
 *
 * Ética:
 * - Não expor dados sensíveis
 * - Evitar sugerir soluções punitivas como padrão
 * - Oferecer acolhimento e direitos primeiro
 */

enum class SupportCategory(val title: String, val subtitle: String) {
    LEGAL("Assistência Jurídica", "Direitos, medidas protetivas e orientação legal"),
    PSYCHO("Assistência Psicológica", "Acolhimento emocional e acompanhamento"),
    WELCOME("Canais de Acolhimento", "Canais oficiais e redes de proteção")
}

data class SupportResource(
    val name: String,
    val description: String,
    val phone: String? = null,   // números nacionais quando aplicável
    val url: String? = null,     // links oficiais quando estáveis
    val searchQuery: String? = null, // para abrir busca (estado/cidade)
    val tags: List<String> = emptyList()
)

object SupportData {

    fun resources(category: SupportCategory): List<SupportResource> = when (category) {
        SupportCategory.LEGAL -> legal()
        SupportCategory.PSYCHO -> psycho()
        SupportCategory.WELCOME -> welcome()
    }

    private fun legal() = listOf(
        SupportResource(
            name = "Defensoria Pública (buscar unidade)",
            description = "Atendimento jurídico gratuito. Procure a unidade do seu estado/município.",
            searchQuery = "Defensoria Pública atendimento perto de mim",
            tags = listOf("Gratuito", "Direitos", "Medidas protetivas")
        ),
        SupportResource(
            name = "Ministério Público (buscar unidade)",
            description = "Pode orientar e encaminhar casos e violações de direitos.",
            searchQuery = "Ministério Público atendimento ao cidadão perto de mim",
            tags = listOf("Direitos", "Encaminhamento")
        ),
        SupportResource(
            name = "OAB — Assistência/Orientação (buscar seccional)",
            description = "Orienta sobre advocacia pro bono e canais de atendimento da seccional.",
            searchQuery = "OAB seccional atendimento ao cidadão",
            tags = listOf("Orientação", "Advocacia")
        ),
        SupportResource(
            name = "Delegacias especializadas (buscar unidade)",
            description = "Para registro oficial e encaminhamentos. Procure a delegacia especializada da sua região.",
            searchQuery = "Delegacia especializada atendimento perto de mim",
            tags = listOf("Registro", "Encaminhamento")
        )
    )

    private fun psycho() = listOf(
        SupportResource(
            name = "CVV — Centro de Valorização da Vida",
            description = "Apoio emocional 24h. Se você estiver em sofrimento intenso, fale com alguém agora.",
            phone = "188",
            tags = listOf("24h", "Apoio emocional")
        ),
        SupportResource(
            name = "CAPS (buscar unidade)",
            description = "Atendimento em saúde mental pelo SUS. Procure o CAPS da sua região.",
            searchQuery = "CAPS saúde mental perto de mim",
            tags = listOf("SUS", "Saúde mental")
        ),
        SupportResource(
            name = "Disque Saúde",
            description = "Informações e orientações sobre serviços do SUS e encaminhamentos.",
            phone = "136",
            tags = listOf("SUS", "Informação")
        ),
        SupportResource(
            name = "Centros de referência (buscar unidade)",
            description = "CRAS/CREAS e serviços municipais podem oferecer suporte e encaminhamento.",
            searchQuery = "CRAS CREAS perto de mim",
            tags = listOf("Acolhimento", "Encaminhamento")
        )
    )

    private fun welcome() = listOf(
        SupportResource(
            name = "Disque 100 — Direitos Humanos",
            description = "Canal nacional para denúncias e orientações sobre violações de direitos.",
            phone = "100",
            tags = listOf("Oficial", "Direitos")
        ),
        SupportResource(
            name = "Central de Atendimento à Mulher (Disque 180)",
            description = "Orientação e encaminhamento em casos de violência. Útil para redes de proteção e serviços.",
            phone = "180",
            tags = listOf("Oficial", "Proteção")
        ),
        SupportResource(
            name = "SAMU",
            description = "Emergência médica.",
            phone = "192",
            tags = listOf("Emergência")
        ),
        SupportResource(
            name = "Polícia",
            description = "Emergência policial.",
            phone = "190",
            tags = listOf("Emergência")
        ),
        SupportResource(
            name = "ONGs e redes locais (buscar)",
            description = "Procure coletivos e ONGs LGBTQIA+ na sua cidade para acolhimento e suporte comunitário.",
            searchQuery = "ONG LGBTQIA+ acolhimento perto de mim",
            tags = listOf("Comunidade", "Acolhimento")
        )
    )
}
