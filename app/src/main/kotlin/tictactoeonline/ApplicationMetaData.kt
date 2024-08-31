package tictactoeonline

import java.io.File

class ApplicationMetaData {
    companion object {
        val h2FileBuildDbMvDb = "./build/db"
        val h2FileOnFileSystemBuildDbMvDb = File(h2FileBuildDbMvDb + ".mv.db")
        val h2FileJdbcUrl ="jdbc:h2:$h2FileBuildDbMvDb"

    }

}