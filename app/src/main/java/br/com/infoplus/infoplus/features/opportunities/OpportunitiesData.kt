package br.com.infoplus.infoplus.features.opportunities

/**
 * MVP ético e defensável:
 * - Não “promete” vaga
 * - Aponta para fontes e buscas confiáveis
 * - Separado por categoria
 * - Permite evoluir para integração com backend/feeds e curadoria institucional
 */

enum class OpportunityCategory(val title: String, val subtitle: String) {
    JOBS("Vagas inclusivas", "Emprego, inclusão e programas de contratação"),
    GRANTS("Editais e fomento", "Incentivos, chamadas e bolsas"),
    TRAINING("Cursos e capacitação", "Formação, empregabilidade e trilhas"),
    PUBLIC("Programas públicos", "Serviços e iniciativas governamentais")
}

data class OpportunityItem(
    val title: String,
    val description: String,
    val url: String? = null,
    val searchQuery: String? = null,
    val tags: List<String> = emptyList()
)

object OpportunitiesData {

    fun items(category: OpportunityCategory): List<OpportunityItem> = when (category) {
        OpportunityCategory.JOBS -> jobs()
        OpportunityCategory.GRANTS -> grants()
        OpportunityCategory.TRAINING -> training()
        OpportunityCategory.PUBLIC -> publicPrograms()
    }

    private fun jobs() = listOf(
        OpportunityItem(
            title = "Vagas afirmativas / diversidade (buscar)",
            description = "Pesquise vagas com políticas de diversidade e inclusão (incluindo trans/travesti).",
            searchQuery = "vagas afirmativas diversidade inclusão trans",
            tags = listOf("Inclusão", "Emprego")
        ),
        OpportunityItem(
            title = "LinkedIn — diversidade (buscar)",
            description = "Busque por “diversidade”, “inclusão” e filtros por localidade.",
            searchQuery = "site:linkedin.com vagas diversidade inclusão",
            tags = listOf("Rede", "Localidade")
        ),
        OpportunityItem(
            title = "Portais de vagas (buscar)",
            description = "Use portais conhecidos e busque termos como 'diversidade', 'inclusão', 'afirmativo'.",
            searchQuery = "vagas diversidade inclusão afirmativo",
            tags = listOf("Busca", "Emprego")
        )
    )

    private fun grants() = listOf(
        OpportunityItem(
            title = "Editais culturais e sociais (buscar)",
            description = "Encontre editais municipais/estaduais/federais. Dica: filtre por 'diversidade' e 'direitos humanos'.",
            searchQuery = "editais diversidade direitos humanos 2026",
            tags = listOf("Fomento", "Editais")
        ),
        OpportunityItem(
            title = "Bolsas e auxílios (buscar)",
            description = "Pesquise bolsas para permanência, formação e inclusão em instituições.",
            searchQuery = "bolsas inclusão diversidade edital",
            tags = listOf("Bolsas", "Inclusão")
        )
    )

    private fun training() = listOf(
        OpportunityItem(
            title = "Cursos gratuitos (buscar)",
            description = "Capacitação para empregabilidade, tecnologia e serviços. Filtre por 'gratuito' e sua cidade.",
            searchQuery = "cursos gratuitos capacitação empregabilidade",
            tags = listOf("Gratuito", "Capacitação")
        ),
        OpportunityItem(
            title = "Trilhas de tecnologia (buscar)",
            description = "Formação em tecnologia pode ampliar oportunidades e autonomia financeira.",
            searchQuery = "trilha tecnologia curso gratuito programação",
            tags = listOf("Tecnologia", "Carreira")
        )
    )

    private fun publicPrograms() = listOf(
        OpportunityItem(
            title = "CRAS/CREAS — serviços e encaminhamentos (buscar)",
            description = "Programas e serviços municipais de assistência social e encaminhamento.",
            searchQuery = "CRAS CREAS serviços perto de mim",
            tags = listOf("Assistência", "Encaminhamento")
        ),
        OpportunityItem(
            title = "Centros de referência LGBTQIA+ (buscar)",
            description = "Serviços e redes locais de acolhimento e encaminhamento para trabalho/assistência.",
            searchQuery = "centro de referência LGBTQIA+ perto de mim",
            tags = listOf("Acolhimento", "Direitos")
        )
    )
}