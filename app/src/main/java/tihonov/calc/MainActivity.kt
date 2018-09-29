package tihonov.calc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private var expression = "0"
    private lateinit var monitor: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        monitor = findViewById(R.id.monitor)

        val digIds = listOf(R.id.d0, R.id.d1, R.id.d2, R.id.d3, R.id.d4, R.id.d5, R.id.d6,
                R.id.d7, R.id.d8, R.id.d9)

        for (curr in digIds.indices) {
            val tmp: TextView = findViewById(digIds[curr])
            tmp.setOnClickListener {
                print(('0' + curr).toString())
            }
        }

        val opIds = listOf(R.id.multiply, R.id.divide, R.id.del, R.id.sub, R.id.add, R.id.result)
        val map = mutableMapOf(
                R.id.multiply to '*',
                R.id.add to '+',
                R.id.divide to '/',
                R.id.del to '<',
                R.id.sub to '-',
                R.id.result to '=')

        for (currId in opIds) {
            val tmp: TextView = findViewById(currId)
            tmp.setOnClickListener {
                when (map[currId]) {
                    '<' -> {
                        if (!expression.isEmpty()) {
                            expression = expression.substring(0, expression.length - 1)
                        }
                        if (expression.isEmpty()) {
                            expression = "0"
                        }
                        print()
                    }
                    '=' -> {
                        //expression = expression.parse()
                        print()
                    }
                    else -> {
                        print(map[currId].toString())
                    }
                }
            }
        }
    }

    private fun print() {
        //*,-,/ symb????
        monitor.text = if (expression.length >= 13) {
            expression.substring(expression.length - 12, expression.length)
        } else {
            expression
        }
    }

    private fun print(str: String) {
        if (expression == "0" && !str.isEmpty()) {
            expression = ""
        }
        expression += str

        print()
    }
}

fun parse() {

}
