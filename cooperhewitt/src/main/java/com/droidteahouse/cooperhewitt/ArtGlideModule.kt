package com.droidteahouse.cooperhewitt

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule


@GlideModule
class ArtGlideModule  : AppGlideModule() {

    public  override fun applyOptions( context: Context,  builder: GlideBuilder) {
        val diskCacheSizeBytes = Long.MAX_VALUE // 800 MB
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, diskCacheSizeBytes))
    }
}