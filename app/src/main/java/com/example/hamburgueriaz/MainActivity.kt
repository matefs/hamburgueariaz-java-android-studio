package com.example.hamburgueriaz

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hamburgueriaz.ui.theme.HamburgueriaZTheme
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { HamburgueriaZApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HamburgueriaZApp() {
    HamburgueriaZTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("HamburgueriaZ") }) }
        ) { innerPadding ->
            PedidoContent(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun PedidoContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var nome by remember { mutableStateOf("") }
    var bacon by remember { mutableStateOf(false) }
    var queijo by remember { mutableStateOf(false) }
    var onion by remember { mutableStateOf(false) }
    var quantidade by remember { mutableStateOf(0) }
    var mostrarResumo by remember { mutableStateOf(false) }

    // Preços por unidade
    val precoBase = 20.0
    val precoBacon = 3.0
    val precoQueijo = 2.0
    val precoOnion = 4.0

    val extrasPorUnidade =
        (if (bacon) precoBacon else 0.0) +
                (if (queijo) precoQueijo else 0.0) +
                (if (onion) precoOnion else 0.0)

    val total: Double = quantidade.toDouble() * (precoBase + extrasPorUnidade)
    val moedaBR = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val totalFmt = moedaBR.format(total)

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logomarca como componente na tela
        Image(
            painter = painterResource(id = R.drawable.logomarca),
            contentDescription = "Logomarca",
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .padding(top = 8.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            "FAÇA SEU PEDIDO!",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Nome
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Text("Nome", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Checkboxes
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = bacon, onCheckedChange = { bacon = it })
                Text("Bacon")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = queijo, onCheckedChange = { queijo = it })
                Text("Queijo")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = onion, onCheckedChange = { onion = it })
                Text("Onion Rings")
            }
        }

        // Quantidade
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Text("QUANTIDADE", style = MaterialTheme.typography.labelLarge)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(onClick = { if (quantidade > 0) quantidade-- }) { Text("-") }
                Text(
                    quantidade.toString(),
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                FilledTonalButton(onClick = { if (quantidade < 99) quantidade++ }) { Text("+") }
            }
        }

        // Resumo
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Text("RESUMO DO PEDIDO", style = MaterialTheme.typography.labelLarge)
            Text(totalFmt, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            if (mostrarResumo) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Nome do cliente: ${nome.ifBlank { "—" }}")
                        Text("Tem Bacon? ${if (bacon) "Sim" else "Não"}")
                        Text("Tem Queijo? ${if (queijo) "Sim" else "Não"}")
                        Text("Tem Onion Rings? ${if (onion) "Sim" else "Não"}")
                        Text("Quantidade: $quantidade")
                        Text("Preço final: $totalFmt", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Button(
            onClick = {
                mostrarResumo = true
                enviarPedido(
                    // Você pode preencher um destinatário padrão aqui, se quiser
                    to = "", // ex.: "contato@hamburgueriaz.com"
                    nome = nome,
                    bacon = bacon,
                    queijo = queijo,
                    onion = onion,
                    quantidade = quantidade,
                    totalFmt = totalFmt,
                    context = context
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("FAZER PEDIDO")
        }

        Spacer(Modifier.height(8.dp))
    }
}

/**
 * Cria um Intent ACTION_SENDTO (mailto:) para enviar o pedido por e-mail.
 *
 * Subject: "Pedido de (nome do cliente)"
 * Body: resumo com as linhas especificadas.
 */
private fun enviarPedido(
    to: String = "",               // pode deixar vazio para o usuário escolher
    nome: String,
    bacon: Boolean,
    queijo: Boolean,
    onion: Boolean,
    quantidade: Int,
    totalFmt: String,
    context: android.content.Context
) {
    val subject = "Pedido de ${nome.ifBlank { "Cliente" }}"
    val body = buildString {
        appendLine("Nome do cliente: ${nome.ifBlank { "—" }}")
        appendLine("Tem Bacon? ${if (bacon) "Sim" else "Não"}")
        appendLine("Tem Queijo? ${if (queijo) "Sim" else "Não"}")
        appendLine("Tem Onion Rings? ${if (onion) "Sim" else "Não"}")
        appendLine("Quantidade: $quantidade")
        appendLine("Preço final: $totalFmt")
    }

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // garante que só apps de e-mail lidem
        if (to.isNotBlank()) putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }

    // Verifica se existe app de e-mail para tratar
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(Intent.createChooser(intent, "Enviar pedido"))
    } else {
        Toast.makeText(context, "Nenhum app de e-mail encontrado.", Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewPedido() {
    HamburgueriaZApp()
}
