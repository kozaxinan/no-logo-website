package app

import react.*
import react.dom.*
import logo.*
import chooser.*

class App : RComponent<RProps, RState>() {
    override fun RBuilder.render() {
        div("App-header") {
            logo()
            h2 {
                +"No Logos No Toilets"
            }
        }
        p {
            fileUploader()
        }
    }
}

fun RBuilder.app() = child(App::class) {}
