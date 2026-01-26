package com.example.layer_app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //layout
        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editSenha = findViewById<EditText>(R.id.editSenha)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val linkEsqueceuSenha = findViewById<TextView>(R.id.linkEsqueceuSenha)
        val linkCadastrar = findViewById<TextView>(R.id.linkCadastrar)

        // Entrar no app
        btnEntrar.setOnClickListener {
            val email = editEmail.text.toString()
            val senha = editSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else {
                // Aqui você pode validar o login ou navegar para outra tela
                Toast.makeText(this, "Login realizado com: $email", Toast.LENGTH_SHORT).show()
            }
        }

        // Esquecer a senha
        linkEsqueceuSenha.setOnClickListener {
            Toast.makeText(this, "Redirecionar para recuperação de senha", Toast.LENGTH_SHORT).show()
        }

        // Cadastrar novo usuario
        linkCadastrar.setOnClickListener {
            Toast.makeText(this, "Redirecionar para tela de cadastro", Toast.LENGTH_SHORT).show()
        }
    }
}
