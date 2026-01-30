package com.example.layer_app

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
        val editEmail = findViewById<EditText>(R.id.input_Email)
        val editSenha = findViewById<EditText>(R.id.input_Senha)
        val btnEntrar = findViewById<Button>(R.id.btn1)
        val linkEsqueceuSenha = findViewById<TextView>(R.id.login_Esqueceu)
        val linkCadastrar = findViewById<TextView>(R.id.login_Text_Register2)




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
