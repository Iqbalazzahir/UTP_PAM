package com.example.utp_pam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.utp_pam.ui.theme.UTP_PAMTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UTP_PAMTheme(dynamicColor = false) {
                WarungSambalGamiApp()
            }
        }
    }
}

private data class MenuItem(
    val id: Int,
    val name: String,
    val price: Int,
    val note: String,
    val accent: Color,
    val imageUrl: String,
    val prepMinutes: Int
)

private enum class SpiceLevel(val label: String, val extraMinutes: Int) {
    TIDAK_PEDAS("Tidak pedas", 0),
    SEDANG("Sedang", 2),
    PEDAS("Pedas", 4),
    SANGAT_PEDAS("Sangat pedas", 6)
}

private data class OrderStatus(
    val queueNumber: Int,
    val readyTime: String,
    val paymentConfirmed: Boolean
)

private val menuItems = listOf(
    MenuItem(
        id = 1,
        name = "Sambal Gami Ayam",
        price = 23000,
        note = "Ayam juicy dengan sambal fresh khas warung.",
        accent = Color(0xFFB23A48),
        imageUrl = "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?auto=format&fit=crop&w=900&q=80",
        prepMinutes = 16
    ),
    MenuItem(
        id = 2,
        name = "Sambal Gami Cumi",
        price = 26000,
        note = "Cumi empuk dengan rasa gurih pedas yang kuat.",
        accent = Color(0xFF6C584C),
        imageUrl = "https://images.unsplash.com/photo-1617692855027-33b14f061079?auto=format&fit=crop&w=900&q=80",
        prepMinutes = 18
    ),
    MenuItem(
        id = 3,
        name = "Sambal Gami Ikan",
        price = 25000,
        note = "Ikan goreng hangat dengan sambal wajan panas.",
        accent = Color(0xFF386641),
        imageUrl = "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?auto=format&fit=crop&w=900&q=80",
        prepMinutes = 17
    ),
    MenuItem(
        id = 4,
        name = "Telur Dadar Gami",
        price = 18000,
        note = "Telur lembut, gurih, cocok buat menu cepat.",
        accent = Color(0xFFE09F3E),
        imageUrl = "https://images.unsplash.com/photo-1525351484163-7529414344d8?auto=format&fit=crop&w=900&q=80",
        prepMinutes = 12
    )
)

private val intListSaver = Saver<SnapshotStateList<Int>, ArrayList<Int>>(
    save = { ArrayList(it) },
    restore = { restored -> mutableStateListOf<Int>().apply { addAll(restored) } }
)

private val stringListSaver = listSaver<SnapshotStateList<String>, String>(
    save = { it.toList() },
    restore = { restored -> mutableStateListOf<String>().apply { addAll(restored) } }
)

private val booleanListSaver = listSaver<SnapshotStateList<Boolean>, Boolean>(
    save = { it.toList() },
    restore = { restored -> mutableStateListOf<Boolean>().apply { addAll(restored) } }
)

@Composable
private fun WarungSambalGamiApp() {
    val navController = rememberNavController()
    val quantities = rememberSaveable(saver = intListSaver) {
        mutableStateListOf(*IntArray(menuItems.size).toTypedArray())
    }
    val spiceLevels = rememberSaveable(saver = stringListSaver) {
        mutableStateListOf(*Array(menuItems.size) { SpiceLevel.SEDANG.name })
    }
    val riceSelections = rememberSaveable(saver = booleanListSaver) {
        mutableStateListOf(*Array(menuItems.size) { true })
    }
    var queueCounter by rememberSaveable { mutableIntStateOf(17) }
    var activeStatus by remember { mutableStateOf<OrderStatus?>(null) }

    val totalItems = quantities.sum()
    val totalPrice = menuItems.indices.sumOf { index ->
        val riceFee = if (riceSelections[index]) 4000 else 0
        quantities[index] * (menuItems[index].price + riceFee)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceContainerLowest
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "menu"
            ) {
                composable("menu") {
                    MenuListScreen(
                        items = menuItems,
                        quantities = quantities,
                        totalItems = totalItems,
                        totalPrice = totalPrice,
                        activeStatus = activeStatus,
                        onIncrease = { itemId -> updateQuantity(quantities, itemId, 1) },
                        onDecrease = { itemId -> updateQuantity(quantities, itemId, -1) },
                        onOpenDetail = { itemId -> navController.navigate("detail/$itemId") }
                    )
                }
                composable(
                    route = "detail/{itemId}",
                    arguments = listOf(navArgument("itemId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val itemId = backStackEntry.arguments?.getInt("itemId") ?: menuItems.first().id
                    val index = itemId - 1
                    val item = menuItems[index]

                    MenuDetailScreen(
                        item = item,
                        quantity = quantities[index],
                        spiceLevel = SpiceLevel.valueOf(spiceLevels[index]),
                        withRice = riceSelections[index],
                        totalItems = totalItems,
                        totalPrice = totalPrice,
                        activeStatus = activeStatus,
                        onBack = { navController.popBackStack() },
                        onIncrease = { updateQuantity(quantities, itemId, 1) },
                        onDecrease = { updateQuantity(quantities, itemId, -1) },
                        onSpiceSelected = { spiceLevels[index] = it.name },
                        onRiceChange = { riceSelections[index] = it },
                        onCreateQueue = {
                            if (quantities[index] > 0) {
                                queueCounter += 1
                                activeStatus = buildOrderStatus(
                                    queueNumber = queueCounter,
                                    item = item,
                                    quantity = quantities[index],
                                    spiceLevel = SpiceLevel.valueOf(spiceLevels[index]),
                                    withRice = riceSelections[index]
                                )
                            }
                        },
                        onConfirmPayment = {
                            activeStatus = activeStatus?.copy(paymentConfirmed = true)
                        }
                    )
                }
            }
        }
    }
}

private fun updateQuantity(quantities: SnapshotStateList<Int>, itemId: Int, delta: Int) {
    val index = itemId - 1
    quantities[index] = (quantities[index] + delta).coerceAtLeast(0)
}

private fun buildOrderStatus(
    queueNumber: Int,
    item: MenuItem,
    quantity: Int,
    spiceLevel: SpiceLevel,
    withRice: Boolean
): OrderStatus {
    val totalMinutes = item.prepMinutes + (quantity.coerceAtLeast(1) - 1) * 3 + spiceLevel.extraMinutes + if (withRice) 4 else 0
    val readyTime = LocalTime.now()
        .plusMinutes(totalMinutes.toLong())
        .format(DateTimeFormatter.ofPattern("HH:mm"))

    return OrderStatus(
        queueNumber = queueNumber,
        readyTime = readyTime,
        paymentConfirmed = false
    )
}

@Composable
private fun MenuListScreen(
    items: List<MenuItem>,
    quantities: List<Int>,
    totalItems: Int,
    totalPrice: Int,
    activeStatus: OrderStatus?,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onOpenDetail: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        HeaderSection(totalItems = totalItems, totalPrice = totalPrice, activeStatus = activeStatus)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 108.dp)
        ) {
            items(items) { item ->
                val quantity = quantities[item.id - 1]
                MenuCard(
                    item = item,
                    quantity = quantity,
                    onOpenDetail = { onOpenDetail(item.id) },
                    onIncrease = { onIncrease(item.id) },
                    onDecrease = { onDecrease(item.id) }
                )
            }
        }

        OrderSummaryFooter(
            totalItems = totalItems,
            totalPrice = totalPrice,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun HeaderSection(totalItems: Int, totalPrice: Int, activeStatus: OrderStatus?) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Warung Sambal Gami",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Daftar menu, detail item, ringkasan pesanan realtime, plus fitur pedas, nasi, antrian, dan konfirmasi bayar.",
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.84f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryChip(label = "Item", value = totalItems.toString())
                SummaryChip(label = "Total", value = formatRupiah(totalPrice))
                SummaryChip(
                    label = "Antrian",
                    value = activeStatus?.let { "A-${it.queueNumber}" } ?: "-"
                )
            }
        }
    }
}

@Composable
private fun SummaryChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MenuCard(
    item: MenuItem,
    quantity: Int,
    onOpenDetail: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            )
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = item.note,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    PriceBadge(price = formatRupiah(item.price), accent = item.accent)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Jumlah pesanan", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$quantity porsi", fontWeight = FontWeight.Bold)
                    }
                    QuantitySelector(
                        quantity = quantity,
                        accent = item.accent,
                        onIncrease = onIncrease,
                        onDecrease = onDecrease
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Estimasi dasar ${item.prepMinutes} menit",
                        color = item.accent,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(onClick = onOpenDetail) {
                        Text("Lihat detail")
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceBadge(price: String, accent: Color) {
    Surface(
        color = accent.copy(alpha = 0.12f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = price,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            color = accent,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    accent: Color,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SmallActionButton(
            text = "-",
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
            onClick = onDecrease
        )
        Text(
            text = quantity.toString(),
            modifier = Modifier.width(24.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        SmallActionButton(
            text = "+",
            containerColor = accent,
            contentColor = Color.White,
            onClick = onIncrease
        )
    }
}

@Composable
private fun SmallActionButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(38.dp),
        contentPadding = PaddingValues(0.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MenuDetailScreen(
    item: MenuItem,
    quantity: Int,
    spiceLevel: SpiceLevel,
    withRice: Boolean,
    totalItems: Int,
    totalPrice: Int,
    activeStatus: OrderStatus?,
    onBack: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onSpiceSelected: (SpiceLevel) -> Unit,
    onRiceChange: (Boolean) -> Unit,
    onCreateQueue: () -> Unit,
    onConfirmPayment: () -> Unit
) {
    val itemTotal = quantity * (item.price + if (withRice) 4000 else 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("Kembali ke menu")
        }

        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                )
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = item.note, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    PriceBadge(price = formatRupiah(item.price), accent = item.accent)
                }
            }
        }

        DetailCard(title = "Atur pesanan") {
            SummaryRow("Harga", formatRupiah(item.price))
            SummaryRow("Jumlah pesanan", "$quantity porsi")
            QuantitySelector(
                quantity = quantity,
                accent = item.accent,
                onIncrease = onIncrease,
                onDecrease = onDecrease
            )
        }

        DetailCard(title = "Level pedas") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SpiceLevel.entries.forEach { level ->
                    FilterChip(
                        selected = level == spiceLevel,
                        onClick = { onSpiceSelected(level) },
                        label = { Text(level.label) }
                    )
                }
            }
        }

        DetailCard(title = "Pilihan nasi") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pakai nasi")
                    Text(
                        text = "Tambahan Rp4.000 per porsi",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Checkbox(
                    checked = withRice,
                    onCheckedChange = onRiceChange
                )
            }
        }

        DetailCard(title = "Ringkasan pesanan") {
            SummaryRow("Total item dipesan", "$totalItems item")
            SummaryRow("Total harga seluruh pesanan", formatRupiah(totalPrice))
            SummaryRow("Subtotal menu ini", formatRupiah(itemTotal))
        }

        DetailCard(title = "Status order") {
            if (quantity == 0) {
                Text("Tambah jumlah pesanan dulu sebelum membuat antrian.")
            } else {
                SummaryRow(
                    "Nomor antrian",
                    activeStatus?.let { "A-${it.queueNumber.toString().padStart(3, '0')}" } ?: "Belum dibuat"
                )
                SummaryRow(
                    "Estimasi jadi",
                    activeStatus?.readyTime ?: "Belum ada"
                )
                SummaryRow(
                    "Pembayaran",
                    if (activeStatus?.paymentConfirmed == true) "Sudah dikonfirmasi" else "Bayar di kasir"
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = onCreateQueue) {
                        Text("Buat antrian")
                    }
                    Button(
                        onClick = onConfirmPayment,
                        enabled = activeStatus != null && !activeStatus.paymentConfirmed,
                        colors = ButtonDefaults.buttonColors(containerColor = item.accent)
                    ) {
                        Text("Konfirmasi bayar")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailCard(title: String, content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun OrderSummaryFooter(
    totalItems: Int,
    totalPrice: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.inverseSurface,
        tonalElevation = 6.dp
    ) {
        if (totalItems == 0) {
            Text(
                text = "No orders yet",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface,
                textAlign = TextAlign.Center
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pesanan aktif",
                        color = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "$totalItems item",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.inverseOnSurface
                    )
                }
                Text(
                    text = formatRupiah(totalPrice),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
    }
}

private fun formatRupiah(value: Int): String =
    "Rp${value.toString().reversed().chunked(3).joinToString(".").reversed()}"

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WarungSambalGamiPreview() {
    UTP_PAMTheme(dynamicColor = false) {
        WarungSambalGamiApp()
    }
}
