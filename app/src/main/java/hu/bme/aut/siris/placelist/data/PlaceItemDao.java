package hu.bme.aut.siris.placelist.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PlaceItemDao {
    @Query("SELECT * FROM placeitem")
    List<PlaceItem> getAll();

    @Insert
    long insert(PlaceItem placeItem);

    @Update
    void update(PlaceItem placeItem);

    @Delete
    void deleteItem(PlaceItem placeItem);
}