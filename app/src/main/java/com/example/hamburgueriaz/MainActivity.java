package com.example.hamburgueriaz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private int quantidade = 0;

    // Views da interface
    private TextView textoQuantidade;
    private EditText editTextNome;
    private CheckBox checkboxBacon, checkboxQueijo, checkboxOnion;
    private TextView textoTotal, textoResumo;
    private Button botaoEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajuste para barra de status
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Vinculação de Views
        editTextNome = findViewById(R.id.editTextNome);
        checkboxBacon = findViewById(R.id.checkboxBacon);
        checkboxQueijo = findViewById(R.id.checkboxQueijo);
        checkboxOnion = findViewById(R.id.checkboxOnion);
        textoTotal = findViewById(R.id.textoTotal);
        textoResumo = findViewById(R.id.textoResumo);
        textoQuantidade = findViewById(R.id.textoQuantidade);
        botaoEnviar = findViewById(R.id.botaoEnviar);
        Button botaoMais = findViewById(R.id.botaoMais);
        Button botaoMenos = findViewById(R.id.botaoMenos);

        // Ações dos botões de quantidade
        botaoMais.setOnClickListener(v -> {
            somar();
            atualizarPrecoTotal();
        });

        botaoMenos.setOnClickListener(v -> {
            subtrair();
            atualizarPrecoTotal();
        });

        // Ação do botão Enviar Pedido
        botaoEnviar.setOnClickListener(v -> enviarPedido());

        // Atualiza preço ao alterar adicionais
        checkboxBacon.setOnCheckedChangeListener((buttonView, isChecked) -> atualizarPrecoTotal());
        checkboxQueijo.setOnCheckedChangeListener((buttonView, isChecked) -> atualizarPrecoTotal());
        checkboxOnion.setOnCheckedChangeListener((buttonView, isChecked) -> atualizarPrecoTotal());

        atualizarPrecoTotal(); // Inicializa preço como R$ 0
    }

    private void somar() {
        quantidade++;
        atualizarQuantidade();
    }

    private void subtrair() {
        if (quantidade > 0) {
            quantidade--;
        }
        atualizarQuantidade();
    }

    private void atualizarQuantidade() {
        textoQuantidade.setText(String.valueOf(quantidade));
    }

    private void atualizarPrecoTotal() {
        boolean temBacon = checkboxBacon.isChecked();
        boolean temQueijo = checkboxQueijo.isChecked();
        boolean temOnion = checkboxOnion.isChecked();
        int precoFinal = calcularTotal(temBacon, temQueijo, temOnion, quantidade);
        textoTotal.setText("Preço Total: R$ " + precoFinal);
    }

    private void enviarPedido() {
        String nome = editTextNome.getText().toString().trim();

        if (nome.isEmpty()) {
            Toast.makeText(this, "Por favor, digite o nome do cliente.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantidade == 0) {
            Toast.makeText(this, "Escolha pelo menos 1 hambúrguer.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean temBacon = checkboxBacon.isChecked();
        boolean temQueijo = checkboxQueijo.isChecked();
        boolean temOnion = checkboxOnion.isChecked();

        int precoFinal = calcularTotal(temBacon, temQueijo, temOnion, quantidade);

        String resumo = "Nome do cliente: " + nome + "\n" +
                "Tem Bacon? " + (temBacon ? "Sim" : "Não") + "\n" +
                "Tem Queijo? " + (temQueijo ? "Sim" : "Não") + "\n" +
                "Tem Onion Rings? " + (temOnion ? "Sim" : "Não") + "\n" +
                "Quantidade: " + quantidade + "\n" +
                "Preço final: R$ " + precoFinal;

        textoResumo.setText(resumo);

        // Intent para enviar e-mail
        Intent intentEmail = new Intent(Intent.ACTION_SENDTO);
        intentEmail.setData(Uri.parse("mailto:")); // Só apps de e-mail vão responder
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Pedido de " + nome);
        intentEmail.putExtra(Intent.EXTRA_TEXT, resumo);

        if (intentEmail.resolveActivity(getPackageManager()) != null) {
            startActivity(intentEmail);
        } else {
            Toast.makeText(this, "Nenhum app de e-mail encontrado.", Toast.LENGTH_SHORT).show();
        }
    }

    private int calcularTotal(boolean bacon, boolean queijo, boolean onion, int qtd) {
        int precoUnitario = 20;
        if (bacon) precoUnitario += 2;
        if (queijo) precoUnitario += 2;
        if (onion) precoUnitario += 3;
        return precoUnitario * qtd;
    }
}
