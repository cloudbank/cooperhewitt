package com.droidteahouse.cooperhewitt.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagingRequestHelper
import com.droidteahouse.cooperhewitt.repository.NetworkState

private fun getErrorMessage(report: PagingRequestHelper.StatusReport): String {
  return PagingRequestHelper.RequestType.values().mapNotNull {
    report.getErrorFor(it)?.message
  }.first()
}

fun PagingRequestHelper.createStatusLiveData(): LiveData<NetworkState> {
  val liveData = MutableLiveData<NetworkState>()
  addListener { report ->
    when {
      report.hasRunning() -> liveData.postValue(NetworkState.LOADING)
      report.hasError() -> liveData.postValue(
          NetworkState.error(getErrorMessage(report)))
      else -> liveData.postValue(NetworkState.LOADED)
    }
  }
  return liveData
}
