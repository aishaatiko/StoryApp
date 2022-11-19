package com.alicea.storyappsubmission.utils

import org.junit.Assert
import org.junit.Test

class EmailValidatorTest {
    @Test
    fun `given correct Email format then should return true`() {
        val email = "aisya_h@outlook.com"
        Assert.assertTrue(EmailValidator.isValidEmail(email))
    }

    @Test
    fun `given wrong Email format then should return false`() {
        val wrongFormat = "aisyahoutlookcom"
        Assert.assertFalse(EmailValidator.isValidEmail(wrongFormat))
    }

}