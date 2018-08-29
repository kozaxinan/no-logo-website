package chooser

import axios.Axios
import axios.AxiosRequestConfig
import chooser.Result.*
import kotlinext.js.*
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.files.*
import react.*
import react.dom.*
import kotlin.js.*

interface FileUploaderProps : RProps

interface FileUploaderState : RState {
    var fileUrl: String
    var result: Result
}

enum class Result {
    RECOMMENDED,
    NOT_RECOMMENDED,
    NOGO,
    ERROR
}

class FileUploader(props: FileUploaderProps) : RComponent<FileUploaderProps, FileUploaderState>(props) {

    override fun FileUploaderState.init(props: FileUploaderProps) {
        fileUrl = ""
        result = RECOMMENDED
    }

    override fun RBuilder.render() {

        fileChooser(chooserProps)

        p {}

        if (state.fileUrl.isNotEmpty()) {
            img {
                attrs {
                    src = getResultImage()
                }
            }

            p {}

            img {
                attrs {
                    src = state.fileUrl
                }
            }
        } else {
            div { +"Please select an Image for Preview" }
        }
    }

    private fun getResultImage(): String = when (state.result) {
        RECOMMENDED -> "src/chooser/cover_recommended.png"
        NOT_RECOMMENDED -> "src/chooser/not_recommended.png"
        NOGO -> "src/chooser/nogo.png"
        ERROR -> "src/chooser/error.png"
    }

    private val chooserProps: FileChooserProps =
            object : FileChooserProps {
                override val onChangeFunction = fun(event: Event) {
                    val target = event.target as HTMLInputElement

                    val selectedFile = target.files?.get(0)

                    selectedFile?.let {
                        val reader = FileReader()
                        reader.onloadend = {
                            setState {
                                fileUrl = reader.result as String
                            }
                        }

                        reader.readAsDataURL(it)
                        upload(it)
                    }
                }
            }

    private fun upload(file: File) {
        val config: AxiosRequestConfig = jsObject {
            method = "post"
            baseURL = "https://6lcmpdwp72.execute-api.eu-west-1.amazonaws.com"
            headers = json(
                    "Content-Type" to "image/png",
                    "Access-Control-Allow-Origin" to "*"
            )
        }

        val url = "https://6lcmpdwp72.execute-api.eu-west-1.amazonaws.com/live/post-image?fileName=" + file.name
        Axios.post<String>(url, file, config).then {
            console.log(it.data)
            setState {
                result = RECOMMENDED
            }
        }.catch {
            console.log(it.message)
            console.log(it)
            setState {
                result = Result.ERROR
            }
        }
    }
}

fun RBuilder.fileUploader() = child(FileUploader::class) {}
