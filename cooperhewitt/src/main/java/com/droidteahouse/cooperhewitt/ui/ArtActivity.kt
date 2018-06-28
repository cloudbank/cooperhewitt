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

package com.droidteahouse.cooperhewitt.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.droidteahouse.cooperhewitt.*
import com.droidteahouse.cooperhewitt.repository.NetworkState
import com.droidteahouse.cooperhewitt.vo.ArtObject
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_art.*
import java.util.*
import javax.inject.Inject

/**
 *
 */
class ArtActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val artViewModel: ArtViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[ArtViewModel::class.java]
    }
    private var mLayoutManager: LinearLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_art)
        createViews()
        initSwipeToRefresh()
        //initSearch()
    }

    //@todo refactor and opts
    private fun createViews() {
        val glide = GlideApp.with(this)
        val adapter = ArtObjectAdapter(glide) {
            artViewModel.retry()
        }
        //@todo rv opts
        rvArt.adapter = adapter
        configRV()
        artViewModel.artObjects.observe(this, Observer<PagedList<ArtObject>> {
            val modelProvider = MyPreloadModelProvider(this, it.orEmpty())

            val preloader = RecyclerViewPreloader(
                    glide, modelProvider, ViewPreloadSizeProvider(), 30)
            rvArt?.addOnScrollListener(preloader);
            adapter.submitList(it)
        })
        artViewModel.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })
    }

    private fun configRV() {
        rvArt?.setHasFixedSize(true)
        rvArt?.setDrawingCacheEnabled(true)
        rvArt?.setItemViewCacheSize(9)
        rvArt?.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH)
        mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvArt?.setLayoutManager(mLayoutManager)
        val itemDecor = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDecor.setDrawable(ContextCompat.getDrawable(this, R.drawable.brush)!!)
        val addItemDecoration = rvArt?.addItemDecoration(itemDecor)
    }

    private fun initSwipeToRefresh() {
        artViewModel.refreshState.observe(this, Observer {
            swipe_refresh.isRefreshing = it == NetworkState.LOADING
        })
        swipe_refresh.setOnRefreshListener {
            artViewModel.refresh()
        }
    }


//setOnFlingListener


    //@todo
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    private class MyPreloadModelProvider(val context: Context, val objects: List<ArtObject>) : ListPreloaderHasher.PreloadModelProvider<ArtObject> {
        @Override
        @NonNull
        public override fun getPreloadItems(position: Int): List<ArtObject> {

            if (objects.isEmpty() || position >= objects.size) {
                return emptyList()
            } else {

                return Collections.singletonList(objects.get(position))
            }
        }

        @Override
        @Nullable
        public override fun getPreloadRequestBuilder(art: ArtObject): GlideRequest<Drawable> {

            return GlideApp.with(context).load(art?.imageUrl).centerCrop()
        }
    }


}
