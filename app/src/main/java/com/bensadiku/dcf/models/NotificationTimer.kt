package com.bensadiku.dcf.models

import java.util.concurrent.TimeUnit

/**
 * Created by Behxhet Sadiku on 1/11/2020.
 */
data class NotificationTimer(val interval: Int = 12, val timeUnit: TimeUnit = TimeUnit.HOURS)