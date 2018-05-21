/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.droidteahouse.cooperhewitt.repository

import android.arch.paging.PagedList
import android.arch.paging.PagingRequestHelper
import android.support.annotation.MainThread
import com.droidteahouse.cooperhewitt.api.ArtAPI
import com.droidteahouse.cooperhewitt.util.createStatusLiveData
import com.droidteahouse.cooperhewitt.vo.ArtObject
import com.droidteahouse.cooperhewitt.vo.ArtObjects
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This boundary callback gets notified when user reaches to the edges of the list such that the
 * database cannot provide any more data.
 * <p>
 * The boundary callback might be called multiple times for the same direction so it does its own
 * rate limiting using the PagingRequestHelper class.
 */
@Singleton
class ArtBoundaryCallback @Inject constructor(
        var ioExecutor: Executor,
        var webservice: ArtAPI
) : PagedList.BoundaryCallback<ArtObject>() {
    lateinit var handleResponse: (ArtObjects?) -> Unit
    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()

    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            webservice.japaneseDesign(
                    page = "1")
                    //per_page = networkPageSize)
                    .enqueue(createWebserviceCallback(it))
        }
    }

    /**
     * User reached to the end of the list.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: ArtObject) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            webservice.japaneseDesign(
                    page = (itemAtEnd.page + 1).toString())
                    //per_page = networkPageSize)
                    .enqueue(createWebserviceCallback(it))
        }
    }

    /**
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    private fun insertItemsIntoDb(
            response: Response<ArtObjects>,
            it: PagingRequestHelper.Request.Callback) {
        ioExecutor.execute {
            handleResponse(response.body())
            it.recordSuccess()
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: ArtObject) {
        // ignored, since we only ever append to what's in the DB
    }

    private fun createWebserviceCallback(it: PagingRequestHelper.Request.Callback)
            : Callback<ArtObjects> {
        return object : Callback<ArtObjects> {
            override fun onFailure(
                    call: Call<ArtObjects>,
                    t: Throwable) {
                it.recordFailure(t)
            }

            override fun onResponse(
                    call: Call<ArtObjects>,
                    response: Response<ArtObjects>) {
                //filter(response)
                insertItemsIntoDb(response, it)
            }
        }
    }

    //@todo
    private fun filter(response: Response<ArtObjects>): Response<ArtObjects> {
        var objs = response.body()
        response.body()?.objects?.filter {
            !it.title.equals("Fragment(Japan)")
        }

        return response
    }
}