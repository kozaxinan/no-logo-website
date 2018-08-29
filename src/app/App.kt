package app

import react.*
import react.dom.*
import logo.*
import chooser.*

class App : RComponent<RProps, RState>() {
    override fun RBuilder.render() {
        div("App-header") {
            logo()
        }

        p {}

        fileUploader()

    }
}

fun RBuilder.app() = child(App::class) {}
