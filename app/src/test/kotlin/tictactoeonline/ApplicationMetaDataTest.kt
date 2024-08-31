package tictactoeonline

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class ApplicationMetaDataTest {
    @Test
    fun `check application meta data`() {
        assertEquals("jdbc:h2:.\\build\\db.mv.db", ApplicationMetaData.h2FileJdbcUrl)
    }


}