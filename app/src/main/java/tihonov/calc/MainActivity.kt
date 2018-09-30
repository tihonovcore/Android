package tihonov.calc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import expression.parser.ExpressionParser
import expression.parser.Parser

class MainActivity : AppCompatActivity() {
    private var expression = "0"
    private lateinit var monitor: TextView
    private var afterResult: Boolean = false
    private var afterOperation: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        monitor = findViewById(R.id.monitor)

        val digIds = listOf(R.id.d0, R.id.d1, R.id.d2, R.id.d3, R.id.d4, R.id.d5, R.id.d6,
                R.id.d7, R.id.d8, R.id.d9)

        for (curr in digIds.indices) {
            val tmp: TextView = findViewById(digIds[curr])
            tmp.setOnClickListener {
                if (afterResult) {
                    expression = ""
                }
                print(('0' + curr).toString())
                afterOperation = false
                afterResult = false
            }
        }

        val opIds = listOf(R.id.multiply, R.id.divide, R.id.del, R.id.sub, R.id.add, R.id.result,
                R.id.clear, R.id.openBrace, R.id.closeBrace, R.id.point)
        val map = mutableMapOf(
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
                        if (!expression.isEmpty()) {
                            expression = expression.substring(0, expression.length - 1)
                        }
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
                        parse()
                        afterResult = true
                        afterOperation = false
                    }
                    else -> {
                        if (afterOperation && (expression[expression.length - 1] == '/'
                                        || expression[expression.length - 1] == '*'
                                        || expression[expression.length - 1] == '.')
                                && (map[currId] == '/' || map[currId] == '*' || map[currId] == '.')) {
                            expression = expression.substring(0, expression.length - 1)
                        }
                        print(map[currId].toString())
                        afterOperation = true
                        afterResult = false
                    }
                }
            }
        }

        if (savedInstanceState != null) {
            expression = savedInstanceState.getString(EXPR)
            monitor.text = expression
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(EXPR, expression)
        super.onSaveInstanceState(outState)
    }

    private fun print() {
        monitor.text = if (expression.length >= 11) {
            expression.substring(expression.length - 10, expression.length)
        } else {
            expression
        }
    }

    private fun print(str: String) {
        if (expression == "0" && str != ".") {
            expression = ""
        }
        expression += str

        print()
    }


    private fun parse() {
        var parser: Parser = ExpressionParser()
        try {
            //remove back zeroes
            expression = "%.10f".format(parser.parse(expression).evaluate())
            for (curr in expression.length - 1 downTo 0) {
                if (expression[curr] != '0') {
                    expression = expression.substring(0, curr + 1)
                    break
                }
            }

            //, -> .
            for (curr in expression.indices) {
                if (expression[curr] == ',') {
                    var temp = expression.substring(0, curr) + "."
                    if (curr + 1 < expression.length) {
                        temp += expression.substring(curr + 1, expression.length)
                    }
                    expression = temp
                    break
                }
            }

            //remove .0 or . suffix
            expression = if (expression.endsWith(".0")) {
                expression.substring(0, expression.length - 2)
            } else if (expression.endsWith(".")) {
                expression.substring(0, expression.length - 1)
            } else {
                expression
            }
            print()
        } catch (e: Exception) {
            expression = "0"
            monitor.text = "ERROR"
        }
    }

    companion object {
        private val EXPR = MainActivity::class.java.name + ".expr"
    }
}