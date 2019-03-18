package hu.bme.aut.siris.placelist.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.net.Uri;

@Entity(tableName = "placeitem", indices={@Index(value="placeid", unique = true)})
public class PlaceItem {
    public static class UriConverter{
        @TypeConverter
        public Uri buildUri(String uri){
            Uri.Builder builder = new Uri.Builder();
            builder.path(uri);
            return uri.equals("")? null : builder.build();
        }

        @TypeConverter
        public String uriToString(Uri uri){ return uri == null? "" : Uri.decode(uri.toString());}
    }

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "placeid")
    public String placeid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "phonenumber")
    public String phoneNumber;

    @ColumnInfo(name = "websiteuri")
    public Uri websiteUri;

    @ColumnInfo(name = "rating")
    public float rating;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;
    @ColumnInfo(name = "note")
    public String note;
}