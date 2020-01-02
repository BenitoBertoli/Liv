package com.benitobertoli.liv.scheduler

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface SchedulerProvider {
    val foregroundScheduler: Scheduler
    val backgroundScheduler: Scheduler
}

object SchedulerProviderImpl : SchedulerProvider {
    override val foregroundScheduler: Scheduler = AndroidSchedulers.mainThread()
    override val backgroundScheduler: Scheduler = Schedulers.io()
}