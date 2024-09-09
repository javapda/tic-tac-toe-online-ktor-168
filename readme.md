# readme for [tic-tac-toe-online-ktor-168](https://github.com/javapda/tic-tac-toe-online-ktor-168)

## local
* [setup](docs/setup-project.md)
* [dependencies](docs/project-dependencies.md)
* [ktor experimentation](docs/ktor-experimentation.md)
* [issues](docs/support/issues.md)
* [Stage 3 Tests](docs/support/stage_3_tests.md)
* [Exposed learning](docs/exposed-learning.md)
* [postgres](docs/postgres.md)
* [Microservices](docs/ktor-microservices.md)
* [Cucumber](docs/cucumber.md)
* [gradle notes](docs/gradle-notes.md)

## Tic-Tac-Toe Online URL's
* [help](http://localhost:28852/help)
* [info](http://localhost:28852/info)
* [Tic-Tac-Toe Online Project](https://hyperskill.org/projects/366)

# Stages

## Stage 5/5 :
## [Stage 4/5 : Adding private rooms](docs/stage_4_of_5_adding_private_rooms.md)
## [Stage 3/5 : Adding authorization and room mechanism](docs/stage_3_of_5_adding_authorization_mechanism.md)
## [Stage 2/5 : Moving to the server](docs/stage_2_of_5_moving_to_the_server.md)
## [Stage 1/5 : Console game](docs/stage_1_of_5_console_game.md)



## Solve in IDE Notes : TicTacToeOnlineTest
* go to the `Tests` view in IntelliJ IDEA. (i.e. not the Course view) - it is selected from a dropdown list box at top
left of the caption bar
* look for `Tic-Tac-Toe_Online -> Tic-Tac-Toe_Online-task -> test -> Tic-Tac-Toe_Online/task/test -> TicTacToeOnlineTest`
* TicTacToeOnlineTest : appears to be a `StageTest` for the project

## additional resources
* [forum entry slack-chats](https://slack-chats.kotlinlang.org/t/461392/hi-all-i-am-following-https-ktor-io-docs-testing-html-hocon-)

## Why?
When working on the [Tic-Tac-Toe Online Project](https://hyperskill.org/projects/366)
I found that while running `Solve in IDE` there were version issues. Namely, I was using
more modern versions ([Ktor version 2.3.12](https://ktor.io/docs/welcome.html)) of Ktor, 
but the Hyperskill `Solve in IDE` appeared to use
[Ktor version 1.6.8](https://ktor.io/docs/old/welcome.html).

This alternate version of the [Tic-Tac-Toe Online Project](https://hyperskill.org/projects/366)
attempts to remedy the problem by re-writing, starting at [Stage 3/5](https://hyperskill.org/projects/366/stages/2168/implement) of the project to see if
I have better luck.