# [Stage 4/5:Adding private rooms](https://hyperskill.org/projects/366/stages/2169/implement) | [main README.md](../readme.md)

### John's Notes
#### resources
* [medium article on Exposed](https://medium.com/@patricktaddei/ktor-and-exposed-the-new-golden-stack-in-the-kotlin-world-b2752e5374fb)
* [medium article's github](https://github.com/pattad/ktor-exposed)
#### steps
* DONE add dependencies for persistence (exposed, h2, etc.)
* DONE add some DB setup and write to file ./build/db.mv.db
* DONE create some basic tests for persistence
---

### Description
Your game already has authorization and a game room system. It's time to implement some other vital mechanisms to improve our game.

First, at the moment, all information about users and games is stored in RAM. It means it will be lost at the first server restart, and the players must register in the game again. This is not convenient. To fix this, you should store the data of users and games in a database.

Secondly, now the rooms can be joined by any authorized players, and room creators have no control over this. You should allow creating private rooms, which can only be joined by an invitation link.

### Objectives
In this stage, move the data into the database and implement the mechanism of private rooms.

* Run a H2 database and connect the application to it. Ensure that data about users and games are 
not lost when the server is restarted. The Ktor documentation has [a tutorial](https://ktor.io/docs/interactive-website-add-persistence.html) on how to
connect the H2 database and interact with it. All data will be stored in the _**build/db.mv.db**_ file and will not be lost during a server restart.
* Edit `POST /game`. In the request body, add the `private` field (accepting `True`/`False` values), which determines whether the created game will be private.
* The Request _body_ looks like this:
```
{
"player1": <email of 1st player or "">,
"player2": <email of 2nd player or "">,
"size": <field size>,
"private": <true/false>
}
```
If `private` is `true`, add a `token` field to the Response _body_, using which another player can join the game room. Generate a random 32-character string as a token. The response should be as follows:

Response code: `200 OK`

Response body:
```
{
"game_id": <new game id>,
"status": "New game started",
"player1": <email of 1st player or "">,
"player2": <email of 2nd player or "">,
"size": <field size>,
"private": <true/false>,
"token": <32-character string (if private=false leave empty string)>
}
```
Leave the error response on game creation (and the condition under which game creation is considered unsuccessful) unchanged.

* Edit `POST /game/<game_id>/join`
Change the endpoint address so that it contains the token of the game you want to join:
`POST /game/<game_id>/join/<token>`

If the user tries to join a private game with an incorrect/missing token, the 
server must send an error response (the same as in the previous stage). If the room is not private, there is no need to provide a token, and the request route should look the same as before. The query body parameter remains the same. The response body is also left unchanged.

* Edit `GET /games`
In the returned game objects, add the private field that indicates whether the game is private or not:

Response body:
```
[
{
"game_id": <game id>,
"player1": <email of 1st player or "">,
"player2": <email of 2nd player or "">,
"size": <field size>,
"private": <true/false>
},
...
]
```
* Edit `GET /game/<game_id>/status`
In the returned status object, add a private field that indicates whether the game is private or not:

Response body:
```
{
"game_id": <game id>,
"game_status": <status code>,
"field": <field array>,
"player1": <email of 1st player or "">,
"player2": <email of 2nd player or "">,
"size": <field size>,
"private": <true/false>,
"token": <32-character string (if private=false leave empty string)>
}
```
Since only users in the game room can get the status, it is safe to provide the token field.

Also note the following points:

* First player to start is always player 1 (X)
* There is no need to link the user's e-mail in the Games table with the Users table. That's because after testing the authorization system, we reset the database. Then we test the other routers and provide them with the necessary JWT tokens to authorize users (while the Users table is empty after the reset).
### Examples
#### Example 1:

1. Request: `POST /signup`

Request body:
```
{
"email": "carl@example.com",
"password": "1111"
}
```
Response code: `200 OK`

Response body:
```
{
"status": "Signed Up"
}
```
2. Request: `POST /signup`

Request body:
```
{
"email": "mike@example.com",
"password": "2222"
}
```
Response code: `200 OK`

Response body:
```
{
"status": "Signed Up"
}
```
3. Request: `POST /signin`

Request body:
```
{
"email": "carl@example.com",
"password": "1111"
}
```
Response code: `200 OK`

Response body:
```
{
"status": "Signed In",
"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImNhcmxAZXhhbXBsZS5jb20ifQ.Zt8gtkUjEffgODauxx9gwQ1cnKueML-5ESBkJW2g5AY"
}
```
4. Request: `POST /signin`

Request body:
```
{
"email": "mike@example.com",
"password": "2222"
}
```
Response code: `200 OK`

Response body:
```
{
"status": "Signed In",
"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pa2VAZXhhbXBsZS5jb20ifQ.yw_YW7lY77UncwcPNOxego1l1TcP80o1gHhrdWeg0JU"
}
```
5. Request: `POST /game`

_Authorization header:_
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImNhcmxAZXhhbXBsZS5jb20ifQ.Zt8gtkUjEffgODauxx9gwQ1cnKueML-5ESBkJW2g5AY
```
Request body:
```
{
"player1": "carl@example.com",
"player2": "",
"size": "4x3",
"private": true
}
```
Response code: `200 OK`
Response body:
```
{
"game_id": 1,
"status": "New game started",
"player1": "carl@example.com",
"player2": "",
"size": "4x3",
"private": true,
"token": "fr67sl4g5fltwwsgjl4ftyj9t20062ia"
}
```
6. Request: `POST /game/1/join`

_Authorization header:_
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pa2VAZXhhbXBsZS5jb20ifQ.yw_YW7lY77UncwcPNOxego1l1TcP80o1gHhrdWeg0JU
```
Request body:
```
{ }
```

Response code: 403 Forbidden
Response body:
{
"status": "Joining the game failed"
}

7. Request: `POST /game/1/join/fr67sl4g5fltwwsgjl4ftyj9t20062ia`

_Authorization header:_
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pa2VAZXhhbXBsZS5jb20ifQ.yw_YW7lY77UncwcPNOxego1l1TcP80o1gHhrdWeg0JU
```
Request body:
```
{ }
```
Response code: `200 OK`
Response body:
```
{
"status": "Joining the game succeeded"
}
```
8. Request: `GET /game/1/status`

_Authorization header:_
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImNhcmxAZXhhbXBsZS5jb20ifQ.Zt8gtkUjEffgODauxx9gwQ1cnKueML-5ESBkJW2g5AY
```
Request body:
```
{ }
```
Response code: `200 OK`
Response body:
```
{
"game_id": 1,
"game_status": "1st player's move",
"field": [
[" ", " ", " ", " "],
[" ", " ", " ", " "],
[" ", " ", " ", " "]
],
"player1": "carl@example.com",
"player2": "mike@example.com",
"size": "4x3",
"private": true,
"token": "fr67sl4g5fltwwsgjl4ftyj9t20062ia"
}
```
#### Example 2:

1. Request: `POST /signup`

Request body:
```
{
"email": "artem@hyperskill.org",
"password": "1234"
}
```
Response code: `200 OK`

Response body:
```
{
"status": "Signed Up"
}
```
2. **The server reboots.**

   That is, the ktor application closes and starts up again.

3. Request: `POST /signin`

Request body:
```
{
"email": "artem@hyperskill.org",
"password": "1234"
}
```
Response code: `200 OK`

Response body:
```
{
"status": "Signed In",
"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFydGVtQGh5cGVyc2tpbGwub3JnIn0.EYNWizxGYKbvbF1ah4EC4TTnyqbquElEzpJqE5jdwrM"
}
```
4. Request: `POST /game`

_Authorization header:_
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFydGVtQGh5cGVyc2tpbGwub3JnIn0.EYNWizxGYKbvbF1ah4EC4TTnyqbquElEzpJqE5jdwrM
```
Request body:
```
{
"player1": "",
"player2": "artem@hyperskill.org",
"size": "3x3",
"private": false
}
```
Response code: `200 OK`

Response body:
```
{
"game_id": 1,
"status": "New game started",
"player1": "",
"player2": "artem@hyperskill.org",
"size": "3x3",
"private": false,
"token": ""
}
```
5. **The server reboots.**

   That is, the ktor application closes and starts up again.

6. Request: `GET /games`

_Authorization header:_
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFydGVtQGh5cGVyc2tpbGwub3JnIn0.EYNWizxGYKbvbF1ah4EC4TTnyqbquElEzpJqE5jdwrM
```
Request body:
```
{ }
```
Response code: `200 OK`

Response body:
```
[
{
"game_id": 1,
"player1": "",
"player2": "artem@hyperskill.org",
"size": "3x3",
"private": false
}
]
```
