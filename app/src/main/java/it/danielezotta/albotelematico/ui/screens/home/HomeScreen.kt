package it.danielezotta.albotelematico.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import it.danielezotta.albotelematico.data.model.Municipality
import it.danielezotta.albotelematico.data.model.Notice
import it.danielezotta.albotelematico.data.model.NoticeDataSource
import it.danielezotta.albotelematico.data.model.NoticeFilter
import it.danielezotta.albotelematico.data.model.Territory
import it.danielezotta.albotelematico.ui.theme.ExpressiveTheme
import it.danielezotta.albotelematico.ui.theme.ExpressiveTheme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNoticeClick: (Notice) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val uiState = viewModel.uiState
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState is HomeUiState.Loading)
    var filterSheetVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var pendingFilter by remember { mutableStateOf(viewModel.currentFilter()) }
    val appliedFilter = viewModel.currentFilter()
    val isFilterApplied = appliedFilter != NoticeFilter()
    val territories = viewModel.territories
    val isTerritoriesLoading = viewModel.isTerritoriesLoading
    val territoryError = viewModel.territoryError
    val municipalities = viewModel.municipalities
    val isMunicipalitiesLoading = viewModel.isMunicipalitiesLoading
    val municipalityError = viewModel.municipalityError
    val titleColor = MaterialTheme.colorScheme.onSurface
    val subtitleColor = MaterialTheme.colorScheme.onSurfaceVariant
    val spacing = ExpressiveTheme.spacing

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
                            ) {
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                    contentColor = MaterialTheme.colorScheme.primary,
                                    tonalElevation = 0.dp
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Domain,
                                        contentDescription = null,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "Albo Telematico",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = titleColor
                                    )
                                    Text(
                                        text = "Provincia Autonoma di Trento",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = subtitleColor
                                    )
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                                IconButton(onClick = {
                                    pendingFilter = viewModel.currentFilter()
                                    filterSheetVisible = true
                                }) {
                                    Icon(Icons.Default.FilterList, contentDescription = "Filtra")
                                }
                                IconButton(onClick = onSearchClick) {
                                    Icon(Icons.Default.Search, contentDescription = "Cerca")
                                }
                            }
                        }
                        if (isFilterApplied) {
                            AssistChip(
                                onClick = {
                                    pendingFilter = appliedFilter
                                    filterSheetVisible = true
                                },
                                label = { Text("Filtri attivi") },
                                leadingIcon = {
                                    Icon(Icons.Default.FilterList, contentDescription = null)
                                }
                            )
                        }
                    }
                },
                navigationIcon = {},
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ),
                scrollBehavior = scrollBehavior
            )
        },
        content = { paddingValues ->
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = viewModel::refresh,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (uiState) {
                    is HomeUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(spacing.huge),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is HomeUiState.Error -> {
                        HomeErrorState(
                            message = uiState.message,
                            onRetry = viewModel::refresh
                        )
                    }
                    is HomeUiState.Success -> {
                        HomeContent(
                            notices = uiState.notices,
                            hasMore = uiState.hasMore,
                            onNoticeClick = onNoticeClick,
                            onLoadMore = viewModel::loadMore
                        )
                    }
                }
            }
        }
    )

    if (filterSheetVisible) {
        FilterSheet(
            initialFilter = pendingFilter,
            territories = territories,
            isTerritoriesLoading = isTerritoriesLoading,
            territoryError = territoryError,
            municipalities = municipalities,
            isMunicipalitiesLoading = isMunicipalitiesLoading,
            municipalityError = municipalityError,
            onLoadMunicipalities = { slug, force -> viewModel.loadMunicipalities(slug, force) },
            onRetryTerritories = { viewModel.ensureTerritoriesLoaded(force = true) },
            onDismiss = { filterSheetVisible = false },
            onApply = { newFilter ->
                pendingFilter = newFilter
                filterSheetVisible = false
                coroutineScope.launch {
                    viewModel.applyFilter(newFilter)
                }
            }
        )
    }
}

private data class NoticeTypeOption(
    val id: String,
    val label: String,
    val tipologia: String,
    val forcedDataSource: NoticeDataSource? = null
)

@Composable
private fun DatasetChoiceRow(
    options: List<Pair<NoticeDataSource, String>>,
    selected: NoticeDataSource,
    onSelected: (NoticeDataSource) -> Unit
) {
    val spacing = ExpressiveTheme.spacing
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        options.forEach { (source, label) ->
            val isSelected = source == selected
            AssistChip(
                onClick = { onSelected(source) },
                label = { Text(label) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    leadingIconContentColor = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun FilterChoiceRow(
    options: List<Pair<String, String>>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = ExpressiveTheme.spacing
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        options.forEach { (value, label) ->
            val isSelected = selected == value
            AssistChip(
                onClick = { onSelected(value) },
                label = { Text(label) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    leadingIconContentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSheet(
    initialFilter: NoticeFilter,
    territories: List<Territory>,
    isTerritoriesLoading: Boolean,
    territoryError: String?,
    municipalities: List<Municipality>,
    isMunicipalitiesLoading: Boolean,
    municipalityError: String?,
    onLoadMunicipalities: (String, Boolean) -> Unit,
    onRetryTerritories: () -> Unit,
    onDismiss: () -> Unit,
    onApply: (NoticeFilter) -> Unit
) {
    val typologyOptions = remember {
        listOf(
            NoticeTypeOption(id = "tutti", label = "Tutti", tipologia = "tutti"),
            NoticeTypeOption(id = "determine", label = "Determine", tipologia = "determine"),
            NoticeTypeOption(id = "delibere", label = "Delibere", tipologia = "delibere"),
            NoticeTypeOption(id = "decreti", label = "Decreti e ordinanze", tipologia = "decreti-ordinanze"),
            NoticeTypeOption(id = "bandi_pub", label = "Bandi di Gara", tipologia = "bandi-di-gara-altre-procedure", forcedDataSource = NoticeDataSource.Published),
            NoticeTypeOption(id = "bandi_arch", label = "Bandi Scaduti", tipologia = "bandi-di-gara-altre-procedure", forcedDataSource = NoticeDataSource.Archived),
            NoticeTypeOption(id = "concorsi", label = "Concorsi", tipologia = "concorsi", forcedDataSource = NoticeDataSource.Published)
        )
    }
    var dataSource by remember { mutableStateOf(initialFilter.dataSource) }
    var tipologia by remember { mutableStateOf(initialFilter.tipologia) }
    var selectedTerritory by remember { mutableStateOf(initialFilter.territory) }
    var selectedTerritoryName by remember { mutableStateOf(initialFilter.territoryName) }
    var selectedMunicipality by remember { mutableStateOf(initialFilter.ente) }
    var selectedMunicipalityName by remember { mutableStateOf(initialFilter.enteName) }
    var filtroEnte by remember { mutableStateOf(initialFilter.filtroEnte) }
    var organo by remember { mutableStateOf(initialFilter.organo) }
    var search by remember { mutableStateOf(initialFilter.search) }
    var dateFrom by remember { mutableStateOf(initialFilter.dateFrom) }
    var dateTo by remember { mutableStateOf(initialFilter.dateTo) }
    var territoryExpanded by remember { mutableStateOf(false) }
    var municipalityExpanded by remember { mutableStateOf(false) }
    val selectedTypologyId = remember(dataSource, tipologia) {
        typologyOptions.firstOrNull { option ->
            option.tipologia == tipologia && (option.forcedDataSource == null || option.forcedDataSource == dataSource)
        }?.id ?: typologyOptions.first().id
    }

    LaunchedEffect(selectedTerritory) {
        territoryExpanded = false
        municipalityExpanded = false
        if (selectedTerritory.isNotBlank()) {
            onLoadMunicipalities(selectedTerritory, false)
        }
    }

    LaunchedEffect(municipalities) {
        if (selectedMunicipality.isNotBlank() && municipalities.none { it.slug == selectedMunicipality }) {
            selectedMunicipality = ""
            selectedMunicipalityName = ""
        }
        municipalityExpanded = false
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.extraLarge, vertical = spacing.large)
                .verticalScroll(rememberScrollState())
                .padding(bottom = spacing.large),
            verticalArrangement = Arrangement.spacedBy(spacing.large)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtri",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Chiudi")
                }
            }

            Text(
                text = "Stato pubblicazione",
                style = MaterialTheme.typography.labelLarge
            )
            DatasetChoiceRow(
                options = listOf(
                    NoticeDataSource.Published to "Pubblicati",
                    NoticeDataSource.Archived to "Archivio"
                ),
                selected = dataSource,
                onSelected = { selectedSource ->
                    dataSource = selectedSource
                    val compatible = typologyOptions.firstOrNull { option ->
                        option.tipologia == tipologia && (option.forcedDataSource == null || option.forcedDataSource == selectedSource)
                    }
                    if (compatible == null) {
                        val fallback = typologyOptions.firstOrNull { option -> option.forcedDataSource == null }
                        tipologia = (fallback ?: typologyOptions.first()).tipologia
                    }
                }
            )

            Text(
                text = "Tipologia",
                style = MaterialTheme.typography.labelLarge
            )
            FilterChoiceRow(
                options = typologyOptions.map { it.id to it.label },
                selected = selectedTypologyId,
                onSelected = { selectedId ->
                    val option = typologyOptions.firstOrNull { it.id == selectedId } ?: return@FilterChoiceRow
                    tipologia = option.tipologia
                    option.forcedDataSource?.let { forced -> dataSource = forced }
                }
            )

            when {
                isTerritoriesLoading -> {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                        CircularProgressIndicator(modifier = Modifier.height(24.dp))
                        Text(text = "Caricamento territori…")
                    }
                }
                territoryError != null -> {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                        Text(text = territoryError, color = MaterialTheme.colorScheme.error)
                        OutlinedButton(onClick = onRetryTerritories) {
                            Text("Riprova")
                        }
                    }
                }
                else -> {
                    val territoryOptions = remember(territories) {
                        listOf("" to "Tutti i territori") + territories.map { territory ->
                            val label = buildString {
                                append(territory.name)
                                territory.municipalitiesCount?.let { count ->
                                    append(" (")
                                    append(count)
                                    append(")")
                                }
                            }
                            territory.slug to label
                        }
                    }
                    val selectedTerritoryLabel = territoryOptions.firstOrNull { it.first == selectedTerritory }?.second
                        ?: territoryOptions.first().second
                    ExposedDropdownMenuBox(
                        expanded = territoryExpanded,
                        onExpandedChange = { expanded ->
                            territoryExpanded = if (territoryOptions.isNotEmpty()) expanded else false
                        }
                    ) {
                        OutlinedTextField(
                            value = selectedTerritoryLabel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Territorio") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = territoryExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = territoryExpanded,
                            onDismissRequest = { territoryExpanded = false }
                        ) {
                            territoryOptions.forEach { (slug, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        selectedTerritory = slug
                                        selectedTerritoryName = territories.firstOrNull { it.slug == slug }?.name ?: ""
                                        selectedMunicipality = ""
                                        selectedMunicipalityName = ""
                                        territoryExpanded = false
                                        if (slug.isNotBlank()) {
                                            onLoadMunicipalities(slug, false)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (selectedTerritory.isNotBlank()) {
                when {
                    isMunicipalitiesLoading -> {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                            CircularProgressIndicator(modifier = Modifier.height(24.dp))
                            Text(text = "Caricamento comuni…")
                        }
                    }
                    municipalityError != null -> {
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                            Text(text = municipalityError, color = MaterialTheme.colorScheme.error)
                            OutlinedButton(onClick = { onLoadMunicipalities(selectedTerritory, true) }) {
                                Text("Riprova")
                            }
                        }
                    }
                    municipalities.isEmpty() -> {
                        Text(
                            text = "Nessun ente disponibile per il territorio selezionato",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    else -> {
                        val municipalityOptions = remember(municipalities) {
                            listOf("" to "Tutti gli enti") + municipalities.map { it.slug to it.name }
                        }
                        val selectedMunicipalityLabel = municipalityOptions.firstOrNull { it.first == selectedMunicipality }?.second
                            ?: municipalityOptions.first().second
                        ExposedDropdownMenuBox(
                            expanded = municipalityExpanded,
                            onExpandedChange = { expanded ->
                                municipalityExpanded = if (municipalityOptions.isNotEmpty()) expanded else false
                            }
                        ) {
                            OutlinedTextField(
                                value = selectedMunicipalityLabel,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Comune/Ente") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = municipalityExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = municipalityExpanded,
                                onDismissRequest = { municipalityExpanded = false }
                            ) {
                                municipalityOptions.forEach { (slug, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            selectedMunicipality = slug
                                            selectedMunicipalityName = municipalities.firstOrNull { it.slug == slug }?.name ?: ""
                                            municipalityExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        if (selectedMunicipalityName.isNotBlank()) {
                            Text(
                                text = "Selezionato: $selectedMunicipalityName",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Text(
                text = "Filtro ente",
                style = MaterialTheme.typography.labelLarge
            )
            FilterChoiceRow(
                options = listOf(
                    "tutti" to "Tutti",
                    "comuni" to "Comuni",
                    "comunita" to "Comunità di valle",
                    "pat" to "Provincia"
                ),
                selected = filtroEnte,
                onSelected = { filtroEnte = it }
            )

            Text(
                text = "Organo",
                style = MaterialTheme.typography.labelLarge
            )
            OutlinedTextField(
                value = organo,
                onValueChange = { organo = it },
                placeholder = { Text("Organo/ufficio (opzionale)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Ricerca testuale") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(spacing.medium)) {
                OutlinedTextField(
                    value = dateFrom,
                    onValueChange = { dateFrom = it },
                    label = { Text("Data da (gg/mm/aaaa)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = dateTo,
                    onValueChange = { dateTo = it },
                    label = { Text("Data a") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                OutlinedButton(
                    onClick = {
                        tipologia = "tutti"
                        dataSource = NoticeDataSource.Published
                        selectedTerritory = ""
                        selectedTerritoryName = ""
                        selectedMunicipality = ""
                        selectedMunicipalityName = ""
                        filtroEnte = "tutti"
                        organo = "tutti"
                        search = ""
                        dateFrom = ""
                        dateTo = ""
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reimposta")
                }
                FilledTonalButton(
                    onClick = {
                        onApply(
                            NoticeFilter(
                                tipologia = tipologia,
                                territory = selectedTerritory,
                                territoryName = selectedTerritoryName,
                                ente = selectedMunicipality,
                                enteName = selectedMunicipalityName,
                                filtroEnte = filtroEnte,
                                organo = organo,
                                search = search,
                                dateFrom = dateFrom,
                                dateTo = dateTo,
                                dataSource = dataSource
                            )
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Applica filtri")
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    notices: List<Notice>,
    hasMore: Boolean,
    onNoticeClick: (Notice) -> Unit,
    onLoadMore: () -> Unit
) {
    val spacing = ExpressiveTheme.spacing
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.extraLarge, vertical = spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(spacing.extraLarge)
    ) {
        if (notices.isNotEmpty()) {
            items(notices, key = { it.id }) { notice ->
                NoticeCard(notice = notice, onClick = { onNoticeClick(notice) })
            }
            if (hasMore) {
                item {
                    AssistChip(
                        onClick = onLoadMore,
                        label = { Text("Carica altri") },
                        leadingIcon = {
                            Icon(Icons.AutoMirrored.Default.ArrowForward, contentDescription = null)
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        } else {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.large),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Nessun avviso disponibile",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Prova a modificare i filtri o la sorgente dei dati.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun NoticeCard(
    notice: Notice,
    onClick: () -> Unit
) {
    val spacing = ExpressiveTheme.spacing
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.large + spacing.micro),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            Text(
                text = notice.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = notice.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!notice.publishDate.isNullOrBlank() || !notice.expiryDate.isNullOrBlank()) {
                val dateLabel = buildString {
                    if (!notice.publishDate.isNullOrBlank()) {
                        append("Pubblicato il ")
                        append(notice.publishDate)
                    }
                    if (!notice.expiryDate.isNullOrBlank()) {
                        if (isNotEmpty()) append(" · ")
                        append("Visibile fino al ")
                        append(notice.expiryDate)
                    }
                }
                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    text = dateLabel
                )
            }
            if (!notice.municipality.isNullOrBlank()) {
                InfoRow(
                    icon = Icons.Default.LocationOn,
                    text = notice.municipality
                )
            }
            if (!notice.publisher.isNullOrBlank()) {
                InfoRow(
                    icon = Icons.Default.Domain,
                    text = "Atto pubblicato da ${notice.publisher}"
                )
            }
            AssistChip(
                onClick = {},
                label = { Text(notice.category ?: "Altro") },
                leadingIcon = {
                    Icon(Icons.Default.Notifications, contentDescription = null)
                }
            )
            if (notice.attachments.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Allegati",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        notice.attachments.forEach { attachment ->
                            AssistChip(
                                onClick = {},
                                label = { Text(attachment.name) },
                                leadingIcon = {
                                    Icon(Icons.Default.Attachment, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun HomeErrorState(
    message: String,
    onRetry: () -> Unit
) {
    val spacing = ExpressiveTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Button(onClick = onRetry) {
            Text("Riprova")
        }
    }
}
