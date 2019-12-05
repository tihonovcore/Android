package tihonov.calc

import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import expression.parser.ExpressionParser
import expression.parser.Parser
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class MainActivity : AppCompatActivity() {
    private var expression = "0"
    private lateinit var monitor: TextView
    private var afterResult: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        monitor = findViewById(R.id.monitor)

        val digIds = listOf(R.id.d0, R.id.d1, R.id.d2, R.id.d3, R.id.d4,
                R.id.d5, R.id.d6, R.id.d7, R.id.d8, R.id.d9)

        for (curr in digIds.indices) {
            val tmp: TextView = findViewById(digIds[curr])
            tmp.setOnClickListener {
                if (afterResult) {
                    expression = ""
                }
                print('0' + curr)
                afterResult = false
            }
        }

        val opIds = listOf(R.id.multiply, R.id.divide, R.id.del, R.id.sub, R.id.add,
                R.id.result, R.id.clear, R.id.openBrace, R.id.closeBrace, R.id.point)

        val map = mapOf(
                R.id.multiply to '*',
                R.id.add to '+',
                R.id.divide to '/',
                R.id.del to '<',
                R.id.sub to '-',
                R.id.result to '=',
                R.id.clear to '~',
                R.id.openBrace to '(',
                R.id.closeBrace to ')',
                R.id.point to '.')

        for (currId in opIds) {
            val tmp: TextView = findViewById(currId)
            tmp.setOnClickListener {
                when (map[currId]) {
                    '<' -> {
                        afterResult = false
                        expression = expression.substring(0, expression.length - 1)
                        if (expression.isEmpty()) {
                            expression = "0"
                        }
                        print()
                    }
                    '~' -> {
                        expression = "0"
                        print()
                    }
                    '=' -> {
                        afterResult = true
                        parse()
                    }
                    else -> {
                        val braceSet = setOf('(', ')')
                        val prev = expression[expression.length - 1]
                        if (map.containsValue(prev) && prev !in braceSet && map[currId] !in braceSet) {
                            expression = expression.substring(0, expression.length - 1)
                        }

                        if (afterResult && map[currId] in braceSet) {
                            expression = ""
                            afterResult = false
                        }
                        print(map[currId])
                        afterResult = false
                    }
                }
            }
        }

        if (savedInstanceState != null) {
            expression = savedInstanceState.getString(EXPR)
            afterResult = savedInstanceState.getBoolean(AFTER_RES)
            print()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(EXPR, expression)
        outState.putBoolean(AFTER_RES, afterResult)
        super.onSaveInstanceState(outState)
    }

    private fun print() {
        val maxLen = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            17
        } else {
            22
        }

        val len = expression.length
        monitor.text = if (len >= maxLen) {
            expression.substring(len - maxLen + 1, len)
        } else {
            expression
        }
    }

    private fun print(char: Char?) {
        if (char != null && expression == "0" && (char.isDigit() || char == '(')) {
            expression = ""
        }
        expression += char
        print()
    }

    private fun Double.format(): String {
        val otherSymbols = DecimalFormatSymbols(Locale.US)
        val decimalFormat = DecimalFormat("##0.######", otherSymbols)
        return decimalFormat.format(this)
    }

    private fun parse() {
        val parser: Parser = ExpressionParser()
        try {
            expression = parser.parse(expression).evaluate().format()
        } catch (e: Exception) {
            expression = "0"
            monitor.text = "ERROR"
            return
        }
        print()
    }

    companion object {
        private val EXPR = MainActivity::class.java.name + ".expr"
        private val AFTER_RES = MainActivity::class.java.name + ".after_res"
    }
}