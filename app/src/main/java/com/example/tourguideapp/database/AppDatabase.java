package com.example.tourguideapp.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.tourguideapp.models.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Database(
        entities = {Place.class},
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract PlaceDao placeDao();

    public static AppDatabase getInstance(Context context) {

        if (INSTANCE == null) {

            synchronized (AppDatabase.class) {

                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "tour_guide_database"
                            )
                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {

                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);

                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        AppDatabase database = INSTANCE;

                                        if (database != null) {
                                            seedDatabase(database.placeDao());
                                        }
                                    });
                                }

                            })
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    private static void seedDatabase(PlaceDao dao) {

        // Prevent duplicate seed
        if (dao.getPlaceCount() > 0) {
            return;
        }

        List<Place> places = new ArrayList<>();

        // =========================================================
        // TOURIST SPOTS - HISTORICAL
        // =========================================================

        places.add(new Place(
                "Jagannath Temple",
                "One of the famous temples and tourist attractions in Ranchi.",
                "Temple",
                "Tourist Spot",
                "Dhursi, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.3441,
                85.3096,
                "5:00 AM - 9:00 PM",
                "Free",
                4.5f,
                "jagannath_temple",
                false
        ));

        places.add(new Place(
                "Ranchi Fort",
                "A historical attraction for visitors interested in local heritage.",
                "Historical",
                "Tourist Spot",
                "Ranchi",
                "Ranchi",
                "Jharkhand",
                23.3500,
                85.3200,
                "9:00 AM - 6:00 PM",
                "Free",
                4.1f,
                "ranchi_fort",
                false
        ));

        places.add(new Place(
                "Tribal Museum",
                "Museum showcasing tribal culture, traditions and history.",
                "Historical",
                "Tourist Spot",
                "Ranchi",
                "Ranchi",
                "Jharkhand",
                23.3700,
                85.3300,
                "10:00 AM - 5:00 PM",
                "₹20",
                4.4f,
                "tribal_museum",
                false
        ));

        places.add(new Place(
                "Tagore Hill",
                "Historic and scenic hill associated with Rabindranath Tagore.",
                "Historical",
                "Tourist Spot",
                "Morabadi, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.3940,
                85.3340,
                "6:00 AM - 6:00 PM",
                "Free",
                4.3f,
                "tagore_hill",
                false
        ));

        places.add(new Place(
                "Rock Garden",
                "A popular landscaped tourist attraction in Ranchi.",
                "Historical",
                "Tourist Spot",
                "Kanke Road, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.4140,
                85.3350,
                "9:00 AM - 6:00 PM",
                "₹30",
                4.2f,
                "rock_garden",
                false
        ));

        // =========================================================
        // TOURIST SPOTS - NATURE
        // =========================================================

        places.add(new Place(
                "Dassam Falls",
                "Beautiful waterfall and one of Jharkhand's popular nature destinations.",
                "Nature",
                "Tourist Spot",
                "Taimara, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.0110,
                85.4140,
                "6:00 AM - 6:00 PM",
                "Free",
                4.6f,
                "dassam_falls",
                false
        ));

        places.add(new Place(
                "Hundru Falls",
                "Spectacular waterfall surrounded by natural scenery.",
                "Nature",
                "Tourist Spot",
                "Hundru, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.4300,
                85.6000,
                "6:00 AM - 6:00 PM",
                "₹10",
                4.7f,
                "hundru_falls",
                false
        ));

        places.add(new Place(
                "Jonha Falls",
                "A scenic waterfall surrounded by forests and hills.",
                "Nature",
                "Tourist Spot",
                "Jonha, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.2550,
                85.6580,
                "6:00 AM - 6:00 PM",
                "₹10",
                4.5f,
                "jonha_falls",
                false
        ));

        places.add(new Place(
                "Birsa Zoological Park",
                "A nature and wildlife destination near Ranchi.",
                "Nature",
                "Tourist Spot",
                "Ormanjhi, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.4500,
                85.4300,
                "9:00 AM - 5:00 PM",
                "₹30",
                4.2f,
                "zoological_park",
                false
        ));

        places.add(new Place(
                "Kanke Dam",
                "Peaceful natural destination suitable for sightseeing.",
                "Nature",
                "Tourist Spot",
                "Kanke, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.4330,
                85.3200,
                "6:00 AM - 7:00 PM",
                "Free",
                4.1f,
                "kanke_dam",
                false
        ));

        // =========================================================
        // TOURIST SPOTS - ADVENTURE
        // =========================================================

        places.add(new Place(
                "Netarhat Adventure Point",
                "Popular destination for scenic views and outdoor activities.",
                "Adventure",
                "Tourist Spot",
                "Netarhat",
                "Latehar",
                "Jharkhand",
                23.4800,
                84.2700,
                "6:00 AM - 6:00 PM",
                "Varies",
                4.5f,
                "netarhat",
                false
        ));

        places.add(new Place(
                "Patratu Valley",
                "Scenic valley popular for road trips and outdoor exploration.",
                "Adventure",
                "Tourist Spot",
                "Patratu",
                "Ramgarh",
                "Jharkhand",
                23.6500,
                85.2800,
                "Open all day",
                "Free",
                4.7f,
                "patratu_valley",
                false
        ));

        places.add(new Place(
                "Pahari Mandir Trek",
                "Hilltop attraction involving a short uphill walk.",
                "Adventure",
                "Tourist Spot",
                "Ranchi",
                "Ranchi",
                "Jharkhand",
                23.3650,
                85.3350,
                "5:00 AM - 8:00 PM",
                "Free",
                4.4f,
                "pahari_mandir",
                false
        ));

        places.add(new Place(
                "Lodh Falls",
                "A remote waterfall destination for nature and adventure lovers.",
                "Adventure",
                "Tourist Spot",
                "Latehar",
                "Latehar",
                "Jharkhand",
                23.4200,
                84.0900,
                "6:00 AM - 5:00 PM",
                "Free",
                4.6f,
                "lodh_falls",
                false
        ));

        // =========================================================
        // TOURIST SPOTS - PICNIC
        // =========================================================

        places.add(new Place(
                "Morabadi Ground",
                "Open public area suitable for casual outdoor activities.",
                "Picnic",
                "Tourist Spot",
                "Morabadi, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.4000,
                85.3300,
                "6:00 AM - 8:00 PM",
                "Free",
                4.0f,
                "morabadi_ground",
                false
        ));

        places.add(new Place(
                "Kanke Dam Picnic Spot",
                "Popular location for relaxed outings and photography.",
                "Picnic",
                "Tourist Spot",
                "Kanke, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.4300,
                85.3250,
                "6:00 AM - 7:00 PM",
                "Free",
                4.2f,
                "kanke_picnic",
                false
        ));

        places.add(new Place(
                "Patratu Lake",
                "Scenic destination suitable for day trips and picnics.",
                "Picnic",
                "Tourist Spot",
                "Patratu",
                "Ramgarh",
                "Jharkhand",
                23.6700,
                85.2800,
                "6:00 AM - 7:00 PM",
                "Free",
                4.6f,
                "patratu_lake",
                false
        ));

        // =========================================================
        // FOOD
        // =========================================================

        places.add(new Place(
                "Kaveri Restaurant",
                "Popular restaurant serving Indian and local dishes.",
                "Food",
                "Food",
                "Main Road, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.3600,
                85.3250,
                "11:00 AM - 10:30 PM",
                "₹₹",
                4.3f,
                "kaveri",
                false
        ));

        places.add(new Place(
                "Nuska Kitchen",
                "Casual dining restaurant with a variety of food options.",
                "Food",
                "Food",
                "Ranchi",
                "Ranchi",
                "Jharkhand",
                23.3700,
                85.3200,
                "11:00 AM - 10:00 PM",
                "₹₹",
                4.2f,
                "nuska",
                false
        ));

        places.add(new Place(
                "Local Food Corner",
                "Street food and local snacks.",
                "Food",
                "Food",
                "Harmu Road, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.3900,
                85.3300,
                "10:00 AM - 9:00 PM",
                "₹",
                4.1f,
                "food_corner",
                false
        ));

        places.add(new Place(
                "Ranchi Cafe",
                "Cafe serving snacks, beverages and quick meals.",
                "Food",
                "Food",
                "Lalpur, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.3700,
                85.3150,
                "9:00 AM - 10:00 PM",
                "₹₹",
                4.0f,
                "ranchi_cafe",
                false
        ));

        places.add(new Place(
                "Jharkhand Kitchen",
                "Restaurant specializing in regional cuisine.",
                "Food",
                "Food",
                "Doranda, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.3250,
                85.3150,
                "11:00 AM - 10:00 PM",
                "₹₹",
                4.4f,
                "jharkhand_kitchen",
                false
        ));

        // =========================================================
        // PETROL PUMP
        // =========================================================

        places.add(new Place(
                "IndianOil Main Road",
                "Fuel station on Main Road.",
                "Petrol Pump",
                "Petrol Pump",
                "Main Road, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.3600,
                85.3200,
                "Open 24 hours",
                "Fuel Price",
                4.1f,
                "petrol",
                false
        ));

        places.add(new Place(
                "HP Petrol Pump Harmu",
                "Petrol and diesel station.",
                "Petrol Pump",
                "Petrol Pump",
                "Harmu Road, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.3900,
                85.3300,
                "Open 24 hours",
                "Fuel Price",
                4.0f,
                "petrol",
                false
        ));

        places.add(new Place(
                "Bharat Petroleum Lalpur",
                "Convenient fuel station in Lalpur.",
                "Petrol Pump",
                "Petrol Pump",
                "Lalpur, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.3700,
                85.3100,
                "Open 24 hours",
                "Fuel Price",
                4.1f,
                "petrol",
                false
        ));

        // =========================================================
        // STAY
        // =========================================================

        places.add(new Place(
                "City View Hotel",
                "Comfortable accommodation near the city center.",
                "Stay",
                "Stay",
                "Ranchi",
                "Ranchi",
                "Jharkhand",
                23.3650,
                85.3250,
                "24 hours",
                "₹₹",
                4.0f,
                "hotel",
                false
        ));

        places.add(new Place(
                "Capital Residency",
                "Hotel offering rooms and basic amenities.",
                "Stay",
                "Stay",
                "Main Road, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.3600,
                85.3300,
                "24 hours",
                "₹₹₹",
                4.2f,
                "hotel",
                false
        ));

        places.add(new Place(
                "Green Park Hotel",
                "Comfortable stay option for tourists.",
                "Stay",
                "Stay",
                "Doranda, Ranchi",
                "Ranchi",
                "Jharkhand",
                23.3200,
                85.3100,
                "24 hours",
                "₹₹",
                4.1f,
                "hotel",
                false
        ));

        dao.insertAll(places);
    }
}
