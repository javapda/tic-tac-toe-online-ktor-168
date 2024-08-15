# issues | [main README](../../README.md)

## root not found
* was getting 404 errors because in the routing configuration, the paths (get, post)
were not enclosed in a `route { }` block

## problem
* sometimes when configuring routing you (or me) may setup a placeholder for a route and 
place some provisional data in the response, that may or may not coincide with what the
final intent will be. In that situation you may get a [`JsonDecodingException`](https://discuss.kotlinlang.org/t/kotlinx-serialization-json-internal-jsondecodingexception-expected-start-of-the-object-but-had-eof-instead/23192) like that shown below:
```
Expected start of the object '{', but had 'EOF' instead
JSON input: GAME-TIME-HERE
kotlinx.serialization.json.internal.JsonDecodingException: Expected start of the object '{', but had 'EOF' instead
JSON input: GAME-TIME-HERE
	at kotlinx.serialization.json.internal.JsonExceptionsKt.JsonDecodingException(JsonExceptions.kt:24)
	at kotlinx.serialization.json.internal.JsonExceptionsKt.JsonDecodingException(JsonExceptions.kt:32)
	at kotlinx.serialization.json.internal.AbstractJsonLexer.fail(AbstractJsonLexer.kt:514)
```

## missing [ContentNegotiation](https://ktor.io/docs/serialization.html)
* the ContentNegotiation plugin:
    * negotiates media types between the client and the server. For this, it uses the [Accept](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept) and [Content-Type](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Type) headers
    * serializing/deserializing the content in a specific format
    * Ktor supports the following formats out-of-the-box:
        * [JSON](https://developer.mozilla.org/en-US/docs/Learn/JavaScript/Objects/JSON) - io.ktor:ktor-serialization-kotlinx-json
        * [XML](https://developer.mozilla.org/en-US/docs/Web/XML/XML_introduction) - io.ktor:ktor-serialization-kotlinx-xml
        * [CBOR](https://cbor.io/) - io.ktor:ktor-serialization-kotlinx-cbor
        * ProtoBuf - io.ktor:ktor-serialization-kotlinx-protobuf
* You configure the ContentNegotiation in your Ktor Application module
* If not configured and you attempt to, for example, deserialize an object to JSON you may get an error like
  following:
```
Response pipeline couldn't transform 'class tictactoeonline.HelpPayload' to the OutgoingContent
java.lang.IllegalArgumentException: Response pipeline couldn't transform 'class tictactoeonline.HelpPayload' to the OutgoingContent
	at io.ktor.server.engine.BaseApplicationResponse$Companion$setupSendPipeline$1.invokeSuspend(BaseApplicationResponse.kt:311)
	at io.ktor.server.engine.BaseApplicationResponse$Companion$setupSendPipeline$1.invoke(BaseApplicationResponse.kt)
	at io.ktor.server.engine.BaseApplicationResponse$Companion$setupSendPipeline$1.invoke(BaseApplicationResponse.kt)

```