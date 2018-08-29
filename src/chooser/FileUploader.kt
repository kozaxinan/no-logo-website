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

@JsModule("src/chooser/error.png")
external val errorEmogi: dynamic
@JsModule("src/chooser/cover_recommended.png")
external val coverEmogi: dynamic
@JsModule("src/chooser/nogo.png")
external val nogoEmogi: dynamic
@JsModule("src/chooser/not_recommended.png")
external val notRecommendedEmogi: dynamic

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

class FileUploader(props: RProps) : RComponent<RProps, FileUploaderState>(props) {

    override fun FileUploaderState.init(props: RProps) {
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

            img(classes = "uploaded_image") {
                attrs {
                    src = state.fileUrl
                }
            }
        } else {
            div { +"Please select an Image for Preview" }
        }
    }

    private fun getResultImage(): String = when (state.result) {
        RECOMMENDED -> coverEmogi
        NOT_RECOMMENDED -> notRecommendedEmogi
        NOGO -> nogoEmogi
        ERROR -> errorEmogi
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
