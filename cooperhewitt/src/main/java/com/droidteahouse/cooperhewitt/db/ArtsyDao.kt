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

package com.droidteahouse.cooperhewitt.db

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.droidteahouse.cooperhewitt.vo.ArtObject

@Dao
interface ArtDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(artItems: List<ArtObject>)

    @Query("SELECT * FROM artObjects ORDER BY page ASC")
    fun artObjects(): DataSource.Factory<Int, ArtObject>

    //@todo w transaction
    @Query("DELETE FROM artObjects ")
    fun deleteArt()

    @Query("SELECT MAX(page) + 1 FROM artObjects")
    fun getNextPageInArt(): Int
}