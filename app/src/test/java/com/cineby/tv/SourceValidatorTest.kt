package com.cineby.tv

import com.cineby.tv.util.SourceValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SourceValidatorTest {
    @Test
    fun validatesHttpsUrl() {
        assertTrue(SourceValidator.isValidHttpsUrl("https://cineby.at"))
    }

    @Test
    fun rejectsInvalidUrl() {
        assertFalse(SourceValidator.isValidHttpsUrl("http://cineby.at"))
        assertFalse(SourceValidator.isValidHttpsUrl("not-a-url"))
    }
}
