package io.github.zncmn.kdocx

import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.MapContext

class Context {
    private val jexl = JexlBuilder().cache(512).strict(true).silent(false).create()
    private var context = MapContext()

    fun putVar(name: String, value: Any?) {
        context[name] = value
    }

    fun clear() {
        context.clear()
    }

    fun evaluate(expression: String): Any? {
        return jexl.createExpression(expression).evaluate(context)
    }
}