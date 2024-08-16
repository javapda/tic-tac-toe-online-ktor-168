package tictactoeonline.util

import io.ktor.application.*
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.testing.*
import io.ktor.utils.io.streams.*
import org.junit.jupiter.api.Disabled
import tictactoeonline.module
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UploadFileTest {
    @Test
    fun `no authentication POST upload of an image saves to app build uploads`() {
        withTestApplication(Application::module) {
            with(handleRequest(HttpMethod.Post, "/upload") {
                (environment.config as MapApplicationConfig).apply {
                    put("upload.dir", "uploads")
                }
//                addHeader("Monkey", "from the jungle")
//                addHeader("Pig", "from the farm")
                val boundary = "WebAppBoundary"
                addHeader(
                    HttpHeaders.ContentType,
                    ContentType.MultiPart.FormData.withParameter("boundary", boundary).toString()
                )

                val testResourcesDir = File("src/test/resources")
                assertTrue(testResourcesDir.exists())
                assertTrue(testResourcesDir.isDirectory)
                val fileBytes = File(testResourcesDir, "ktor_logo.png").readBytes()
                setBody(boundary, listOf(
                    PartData.FormItem(
                        "Ktor logo", {}, headersOf(
                            HttpHeaders.ContentDisposition,
                            ContentDisposition.Inline
                                .withParameter(ContentDisposition.Parameters.Name, "description")
                                .toString()
                        )
                    ),
                    PartData.FileItem({ fileBytes.inputStream().asInput() }, {}, headersOf(
                        HttpHeaders.ContentDisposition,
                        ContentDisposition.File
                            .withParameter(ContentDisposition.Parameters.Name, "image")
                            .withParameter(ContentDisposition.Parameters.FileName, "ktor_logo.png")
                            .toString()
                    )
                    )
                )
                )

            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue(response.content!!.contains("""POST: upload, /upload"""))
                assertTrue(response.content!!.contains("""app\build\uploads\ktor_logo.png"""))

            }) {
                println(
                    """
                    AFTER WITH
                    response.status(): ${response.status()}
                    response.content:  ${response.content}
                """.trimIndent()
                )
            }
        }

    }


    @Disabled
    @Test
    fun `authentication REQUIRED POST upload of an image saves to app build uploads`() {
        withTestApplication(Application::module) {
            with(handleRequest(HttpMethod.Post, "/uploadSecure") {
                (environment.config as MapApplicationConfig).apply {
                    put("upload.dir", "uploads")
                }
                val boundary = "WebAppBoundary"
                addHeader(
                    HttpHeaders.ContentType,
                    ContentType.MultiPart.FormData.withParameter("boundary", boundary).toString()
                )

                val testResourcesDir = File("src/test/resources")
                assertTrue(testResourcesDir.exists())
                assertTrue(testResourcesDir.isDirectory)
                val fileBytes = File(testResourcesDir, "ktor_logo.png").readBytes()
                setBody(boundary, listOf(
                    PartData.FormItem(
                        "Ktor logo", {}, headersOf(
                            HttpHeaders.ContentDisposition,
                            ContentDisposition.Inline
                                .withParameter(ContentDisposition.Parameters.Name, "description")
                                .toString()
                        )
                    ),
                    PartData.FileItem({ fileBytes.inputStream().asInput() }, {}, headersOf(
                        HttpHeaders.ContentDisposition,
                        ContentDisposition.File
                            .withParameter(ContentDisposition.Parameters.Name, "image")
                            .withParameter(ContentDisposition.Parameters.FileName, "ktor_logo.png")
                            .toString()
                    )
                    )
                )
                )

            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue(response.content!!.contains("""POST: upload, /upload"""))
                assertTrue(response.content!!.contains("""app\build\uploads\ktor_logo.png"""))

            }) {
                println(
                    """
                    AFTER WITH
                    response.status(): ${response.status()}
                    response.content:  ${response.content}
                """.trimIndent()
                )
            }
        }

    }

}