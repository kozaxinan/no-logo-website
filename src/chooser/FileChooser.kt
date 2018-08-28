package chooser

import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.events.Event
import react.*
import react.dom.*

interface FileChooserProps : RProps {
    fun onChangeFunction(): (Event) -> Unit
}

fun RBuilder.fileChooser(props: FileChooserProps) = input(type = InputType.file) {
    attrs {
        onChangeFunction = props.onChangeFunction()
    }
}
