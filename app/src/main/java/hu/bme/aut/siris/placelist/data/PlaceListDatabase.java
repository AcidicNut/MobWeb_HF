package hu.bme.aut.siris.placelist.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(
        entities = {PlaceItem.class},
        version = 1
)

@TypeConverters(value = {PlaceItem.UriConverter.class})
public abstract class PlaceListDatabase  extends RoomDatabase{
    public abstract PlaceItemDao placeItemDao();
}