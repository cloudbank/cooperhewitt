package com.droidteahouse.cooperhewitt.vo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import android.support.annotation.NonNull
import com.droidteahouse.cooperhewitt.db.Converters
import com.google.gson.annotations.SerializedName


@Entity(tableName = "artObjects")
data class ArtObject(@SerializedName("date")
                     val date: String? = "",


                     @SerializedName("description")
                     val description: String? = "",

                     @SerializedName("medium")
                     val medium: String? = "",
                     @SerializedName("title")
                     val title: String? = "",
                     @SerializedName("type")
                     val type: String? = "",


                     @SerializedName("id")
                     @PrimaryKey
                     @NonNull
                     val id: String = "",

                     @SerializedName("images")
                     @TypeConverters(Converters::class)
                     val images: List<ImagesItem>?,


                     @SerializedName("gallery_text")
                     val galleryText: String? = "") {

    var page = 0;
    //@todo add unique constraint
    var imageUrl = images?.get(0)?.n?.url

}
