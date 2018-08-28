package chooser

import axios.AxiosConfigSettings
import axios.AxiosResponse
import kotlinext.js.*
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
    var result: Result
}

enum class Result {
    recommended,
    not_recommended,
    nogo,
    error
}

// Import the axios library (run "npm install axios --save" to install)
@JsModule("axios")
external fun <T> axios(config: AxiosConfigSettings): Promise<AxiosResponse<T>>

class FileUploader(props: FileUploaderProps) : RComponent<FileUploaderProps, FileUploaderState>(props) {

    override fun FileUploaderState.init(props: FileUploaderProps) {
        result = Result.recommended
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
                    upload(it)
                }
            }
        })

        p { }

        if (state.file != null) {
            img {
                attrs {
                    src = when (state.result) {
                        Result.recommended -> "src/chooser/cover_recommended.png"
                        Result.not_recommended -> "src/chooser/not_recommended.png"
                        Result.nogo -> "src/chooser/nogo.png"
                        Result.error -> "src/chooser/error.png"
                    }
                }
            }
            img { attrs { src = state.fileUrl ?: "" } }
        } else {
            div {
                +"Please select an Image for Preview"
            }
        }
    }

    private fun upload(file: File) {
        val config: AxiosConfigSettings = jsObject {
            url = "https://6lcmpdwp72.execute-api.eu-west-1.amazonaws.com/live/post-image?fileName=" + file.name
            data = file
            method = "Post"
            headers = mapOf(
                    "Accept" to "Application",
                    "Content-Type" to "image/png"
            )
        }

        axios<String>(config).then {
            console.log(it.data)
            setState {
                result = Result.recommended
            }
        }.catch {
            console.log(it.message)
            console.log(it)
            setState {
                result = Result.error
            }
        }
    }
}

fun RBuilder.fileUploader() = child(FileUploader::class) {}
