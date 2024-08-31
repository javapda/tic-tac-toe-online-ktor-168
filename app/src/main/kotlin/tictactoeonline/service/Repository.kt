package tictactoeonline.service


interface Repository<T> {
    fun all(): List<T>
}
