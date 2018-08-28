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
    var file: File?
    var fileUrl: String
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

        fileChooser(object : FileChooserProps {
            override fun onChangeFunction() = fun(event: Event) {
                console.log("ajnsfas")
                val target = event.target

                val newFile = if (target is HTMLInputElement) {
                    target.files?.get(0)
                } else null

                val reader = FileReader()
                reader.onloadend = {
                    setState {
                        file = newFile
                        fileUrl = reader.result as String
                    }
                }
                newFile?.let {
                    reader.readAsDataURL(it)
                }
            }
        })

        button {
            +"Upload"
            attrs {
                onClickFunction = uploadHandler()
            }
        }

        p { }

        if (state.file != null) {
            img { attrs { src = state.fileUrl ?: "" } }
        } else {
            div {
                +"Please select an Image for Preview"
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
