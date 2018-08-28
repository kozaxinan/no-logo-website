package chooser

import axios.AxiosConfigSettings
import axios.AxiosResponse
import kotlinext.js.*
import kotlinx.html.*
import kotlinx.html.js.*
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.files.*
import react.*
import react.dom.*
import kotlin.js.*

interface FileUploaderProps : RProps

interface FileUploaderState : RState {
    var filePath: File?
    var result: String
}

// Import the axios library (run "npm install axios --save" to install)
@JsModule("axios")
external fun <T> axios(config: AxiosConfigSettings): Promise<AxiosResponse<T>>

class FileUploader(props: FileUploaderProps) : RComponent<FileUploaderProps, FileUploaderState>(props) {

    override fun FileUploaderState.init(props: FileUploaderProps) {
        result = "Waiting"
    }

    override fun RBuilder.render() {
        p {
            fileChooser(object : FileChooserProps {
                override fun onChangeFunction() = fun(event: Event) {
                    setState {
                        val target = event.target
                        if (target is HTMLInputElement) {
                            filePath = target.files?.get(0)
                        }
                    }
                }
            })
        }

        p {
            button {
                +"Upload"
                attrs {
                    onClickFunction = uploadHandler()
                }
            }
        }
    }

    private fun uploadHandler(): (Event) -> Unit = { event: Event ->
        val config: AxiosConfigSettings = jsObject {
            url = "http://ziptasticapi.com/"
        }

        axios<Any>(config).then {
            setState {
                result = it.data.toString()
            }
        }.catch {
            setState {
                result = "Error!!!"
            }
            console.log(it)
        }
    }
}

fun RBuilder.fileUploader() = child(FileUploader::class) {}
