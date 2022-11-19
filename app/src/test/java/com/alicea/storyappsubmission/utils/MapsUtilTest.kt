package com.alicea.storyappsubmission.utils

import org.junit.Assert
import org.junit.Test

class MapsUtilTest {
    @Test
    fun `given correct Latitude and Longitude range then should return true`() {
        val latitude = -6.9600243
        val longitude = 109.1406238
        Assert.assertTrue(MapsUtil.coordinatesValid(latitude, longitude))
    }

    @Test
    fun `given wrong Latitude or Longitude format then should return false`() {
        val wrongLatitude = 100.2
        val wrongLongitude = 200.5
        val validLatitude = -6.9600243
        val validLongitude = 109.1406238
        Assert.assertFalse(MapsUtil.coordinatesValid(wrongLatitude, wrongLongitude))
        Assert.assertFalse(MapsUtil.coordinatesValid(wrongLatitude, validLongitude))
        Assert.assertFalse(MapsUtil.coordinatesValid(validLatitude, wrongLongitude))
        Assert.assertFalse(MapsUtil.coordinatesValid(validLongitude, validLatitude))
    }
}