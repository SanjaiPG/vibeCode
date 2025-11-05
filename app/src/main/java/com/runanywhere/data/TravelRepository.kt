package com.runanywhere.startup_hackathon20.data

import com.runanywhere.startup_hackathon20.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TravelRepository {
    private val destinations = listOf(
        // France
        Destination(
            id = "paris",
            name = "Paris",
            country = "France",
            lat = 48.8566,
            lng = 2.3522,
            imageUrl = "https://picsum.photos/600/400?1",
            currencyCode = "EUR",
            rating = 4.9,
            reviewCount = 5432,
            description = "The City of Light captivates with its iconic Eiffel Tower, world-class museums like the Louvre, charming cafés, and romantic Seine River cruises. Experience art, fashion, and gourmet cuisine in this timeless metropolis.",
            hotels = listOf(
                Hotel(
                    "The Ritz Paris",
                    4.9,
                    "€500-800",
                    listOf("Pool", "Spa", "Fine Dining", "WiFi"),
                    "🏨"
                ),
                Hotel(
                    "Le Bristol Paris",
                    4.8,
                    "€600-900",
                    listOf("Garden", "Spa", "Michelin Star", "Bar"),
                    "🏨"
                ),
                Hotel(
                    "Hotel Plaza Athénée",
                    4.9,
                    "€700-1000",
                    listOf("Spa", "Restaurant", "Concierge", "WiFi"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Le Jules Verne", 4.8, "French Fine Dining", "€€€€", "🍽️"),
                Restaurant("L'Astrance", 4.9, "Contemporary French", "€€€€", "🍽️"),
                Restaurant("Café de Flore", 4.6, "French Bistro", "€€", "☕")
            ),
            topReviews = listOf(
                Review(
                    "Emma Wilson",
                    5,
                    "Absolutely magical! The Eiffel Tower at sunset is breathtaking. The food, culture, and architecture exceeded all expectations.",
                    "2024-10-15",
                    "👩"
                ),
                Review(
                    "James Chen",
                    5,
                    "Perfect romantic getaway. Louvre Museum was incredible, and the Seine River cruise was unforgettable.",
                    "2024-09-22",
                    "👨"
                )
            )
        ),
        Destination(
            id = "nice",
            name = "Nice",
            country = "France",
            lat = 43.7102,
            lng = 7.2620,
            imageUrl = "https://picsum.photos/600/400?2",
            currencyCode = "EUR",
            rating = 4.7,
            reviewCount = 3218,
            description = "Nestled on the French Riviera, Nice offers stunning Mediterranean beaches, the famous Promenade des Anglais, vibrant markets, and a perfect blend of French and Italian culture.",
            hotels = listOf(
                Hotel(
                    "Hotel Negresco",
                    4.7,
                    "€300-500",
                    listOf("Beach Access", "Restaurant", "Bar", "WiFi"),
                    "🏨"
                ),
                Hotel(
                    "Le Meridien Nice",
                    4.6,
                    "€250-400",
                    listOf("Pool", "Sea View", "Spa", "WiFi"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("La Petite Maison", 4.7, "Mediterranean", "€€€", "🍽️"),
                Restaurant("Le Chantecler", 4.8, "French Fine Dining", "€€€€", "🍽️")
            ),
            topReviews = listOf(
                Review(
                    "Sophie Martin",
                    5,
                    "Beautiful coastal city! The beaches are pristine and the old town is charming.",
                    "2024-08-10",
                    "👩"
                ),
                Review(
                    "Lucas Dubois",
                    4,
                    "Great weather, excellent food, and wonderful atmosphere. Highly recommended!",
                    "2024-07-18",
                    "👨"
                )
            )
        ),
        Destination(
            id = "lyon",
            name = "Lyon",
            country = "France",
            lat = 45.7640,
            lng = 4.8357,
            imageUrl = "https://picsum.photos/600/400?3",
            currencyCode = "EUR",
            rating = 4.6,
            reviewCount = 2876,
            description = "France's gastronomic capital, Lyon boasts Renaissance architecture, traboules (secret passages), and world-renowned cuisine. Explore its historic districts, vibrant cultural scene, and the confluence of the Rhône and Saône rivers.",
            hotels = listOf(
                Hotel(
                    "Villa Florentine",
                    4.8,
                    "€280-450",
                    listOf("Spa", "Restaurant", "City View", "WiFi"),
                    "🏨"
                ),
                Hotel(
                    "InterContinental Lyon",
                    4.7,
                    "€200-350",
                    listOf("Pool", "Spa", "Bar", "WiFi"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Paul Bocuse", 4.9, "French Fine Dining", "€€€€", "🍽️"),
                Restaurant("Bouchon Les Lyonnais", 4.6, "Traditional Lyonnaise", "€€", "🍽️")
            ),
            topReviews = listOf(
                Review(
                    "Marie Laurent",
                    5,
                    "Food lover's paradise! Every meal was exceptional. The old town is beautiful.",
                    "2024-09-05",
                    "👩"
                ),
                Review(
                    "Pierre Rousseau",
                    5,
                    "Amazing city with rich history and incredible restaurants. Don't miss it!",
                    "2024-08-28",
                    "👨"
                )
            )
        ),

        // Japan
        Destination(
            id = "tokyo",
            name = "Tokyo",
            country = "Japan",
            lat = 35.6762,
            lng = 139.6503,
            imageUrl = "https://picsum.photos/600/400?4",
            currencyCode = "JPY",
            rating = 4.9,
            reviewCount = 8765,
            description = "A dazzling blend of ultra-modern and traditional, Tokyo offers neon-lit streets, ancient temples, world-class sushi, cherry blossoms, and efficient transport. Experience cutting-edge technology alongside timeless Japanese culture.",
            hotels = listOf(
                Hotel(
                    "Park Hyatt Tokyo",
                    4.9,
                    "¥50,000-80,000",
                    listOf("Pool", "Spa", "Bar", "City View"),
                    "🏨"
                ),
                Hotel(
                    "The Peninsula Tokyo",
                    4.8,
                    "¥60,000-90,000",
                    listOf("Spa", "Fine Dining", "Concierge", "WiFi"),
                    "🏨"
                ),
                Hotel(
                    "Aman Tokyo",
                    4.9,
                    "¥80,000-120,000",
                    listOf("Spa", "Pool", "Restaurant", "Lounge"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Sukiyabashi Jiro", 4.9, "Sushi", "¥¥¥¥", "🍣"),
                Restaurant("Narisawa", 4.8, "Contemporary Japanese", "¥¥¥¥", "🍽️"),
                Restaurant("Ichiran Ramen", 4.7, "Ramen", "¥", "🍜")
            ),
            topReviews = listOf(
                Review(
                    "Yuki Tanaka",
                    5,
                    "Tokyo is incredible! Perfect mix of modern and traditional. The food is outstanding and people are so polite.",
                    "2024-10-20",
                    "👩"
                ),
                Review(
                    "Alex Johnson",
                    5,
                    "Best city I've ever visited! Technology, culture, food - everything is world-class. Can't wait to return!",
                    "2024-10-08",
                    "👨"
                )
            )
        ),
        Destination(
            id = "kyoto",
            name = "Kyoto",
            country = "Japan",
            lat = 35.0116,
            lng = 135.7681,
            imageUrl = "https://picsum.photos/600/400?5",
            currencyCode = "JPY",
            rating = 4.9,
            reviewCount = 6543,
            description = "Japan's cultural heart, Kyoto enchants with thousands of temples, traditional geisha districts, serene bamboo groves, and exquisite kaiseki cuisine. Experience authentic Japanese traditions in this ancient imperial capital.",
            hotels = listOf(
                Hotel(
                    "The Ritz-Carlton Kyoto",
                    4.9,
                    "¥60,000-100,000",
                    listOf("Spa", "River View", "Restaurant", "Garden"),
                    "🏨"
                ),
                Hotel(
                    "Four Seasons Kyoto",
                    4.8,
                    "¥55,000-95,000",
                    listOf("Pool", "Spa", "Tea House", "WiFi"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Kikunoi", 4.9, "Kaiseki", "¥¥¥¥", "🍽️"),
                Restaurant("Nishiki Market", 4.7, "Street Food", "¥", "🍢")
            ),
            topReviews = listOf(
                Review(
                    "Sakura Yamamoto",
                    5,
                    "Absolutely stunning! The temples are breathtaking and the traditional atmosphere is magical.",
                    "2024-09-15",
                    "👩"
                ),
                Review(
                    "David Lee",
                    5,
                    "Perfect cultural experience. Fushimi Inari shrine is a must-see. Beautiful city!",
                    "2024-08-30",
                    "👨"
                )
            )
        ),
        Destination(
            id = "osaka",
            name = "Osaka",
            country = "Japan",
            lat = 34.6937,
            lng = 135.5023,
            imageUrl = "https://picsum.photos/600/400?6",
            currencyCode = "JPY",
            rating = 4.7,
            reviewCount = 5432,
            description = "Known as Japan's kitchen, Osaka offers vibrant street food culture, historic Osaka Castle, lively nightlife, and friendly locals. Experience takoyaki, okonomiyaki, and the energetic Dotonbori district.",
            hotels = listOf(
                Hotel(
                    "The St. Regis Osaka",
                    4.8,
                    "¥40,000-70,000",
                    listOf("Spa", "Restaurant", "Bar", "WiFi"),
                    "🏨"
                ),
                Hotel(
                    "Conrad Osaka",
                    4.7,
                    "¥35,000-60,000",
                    listOf("Pool", "Spa", "City View", "WiFi"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Hajime", 4.9, "French-Japanese Fusion", "¥¥¥¥", "🍽️"),
                Restaurant("Dotonbori Street Food", 4.6, "Street Food", "¥", "🍢")
            ),
            topReviews = listOf(
                Review(
                    "Kenji Nakamura",
                    5,
                    "Food heaven! Every street corner has amazing food. The castle is impressive too.",
                    "2024-10-01",
                    "👨"
                ),
                Review(
                    "Lisa Anderson",
                    5,
                    "Loved the energy and the food! Dotonbori at night is spectacular.",
                    "2024-09-12",
                    "👩"
                )
            )
        ),

        // Indonesia
        Destination(
            id = "bali",
            name = "Bali",
            country = "Indonesia",
            lat = -8.3405,
            lng = 115.0920,
            imageUrl = "https://picsum.photos/600/400?7",
            currencyCode = "IDR",
            rating = 4.8,
            reviewCount = 9876,
            description = "The Island of the Gods offers pristine beaches, lush rice terraces, ancient temples, yoga retreats, and vibrant culture. Experience spiritual serenity, adventure sports, and world-class surfing in this tropical paradise.",
            hotels = listOf(
                Hotel(
                    "Four Seasons Jimbaran",
                    4.9,
                    "Rp 8,000,000-12,000,000",
                    listOf("Beach", "Spa", "Pool", "Villas"),
                    "🏨"
                ),
                Hotel(
                    "The Mulia Nusa Dua",
                    4.8,
                    "Rp 5,000,000-9,000,000",
                    listOf("Beach", "Spa", "Pool", "Restaurant"),
                    "🏨"
                ),
                Hotel(
                    "Hanging Gardens Ubud",
                    4.9,
                    "Rp 6,000,000-10,000,000",
                    listOf("Infinity Pool", "Spa", "Jungle View", "Restaurant"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Locavore", 4.9, "Contemporary Indonesian", "Rp Rp Rp", "🍽️"),
                Restaurant("Warung Babi Guling", 4.7, "Balinese", "Rp", "🍖"),
                Restaurant("Swept Away", 4.6, "International", "Rp Rp", "🍽️")
            ),
            topReviews = listOf(
                Review(
                    "Sarah Thompson",
                    5,
                    "Paradise on earth! Beautiful beaches, amazing culture, and the most welcoming people. Ubud is magical!",
                    "2024-10-18",
                    "👩"
                ),
                Review(
                    "Mike Rodriguez",
                    5,
                    "Perfect for both relaxation and adventure. Temples are stunning, food is incredible, and beaches are pristine.",
                    "2024-09-25",
                    "👨"
                )
            )
        ),
        Destination(
            id = "jakarta",
            name = "Jakarta",
            country = "Indonesia",
            lat = -6.2088,
            lng = 106.8456,
            imageUrl = "https://picsum.photos/600/400?8",
            currencyCode = "IDR",
            rating = 4.5,
            reviewCount = 3456,
            description = "Indonesia's bustling capital blends modern skyscrapers with colonial architecture, vibrant markets, diverse street food, and rich cultural heritage. Explore museums, shopping malls, and the historic Old Town.",
            hotels = listOf(
                Hotel(
                    "The Ritz-Carlton Jakarta",
                    4.7,
                    "Rp 3,000,000-5,000,000",
                    listOf("Pool", "Spa", "Restaurant", "WiFi"),
                    "🏨"
                ),
                Hotel(
                    "Mandarin Oriental Jakarta",
                    4.6,
                    "Rp 2,500,000-4,500,000",
                    listOf("Spa", "Pool", "Bar", "WiFi"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Namaaz Dining", 4.8, "Modern Indonesian", "Rp Rp Rp Rp", "🍽️"),
                Restaurant("Sate Khas Senayan", 4.6, "Indonesian", "Rp Rp", "🍢")
            ),
            topReviews = listOf(
                Review(
                    "Andi Wijaya",
                    4,
                    "Vibrant city with great food and shopping. Traffic can be challenging but worth it!",
                    "2024-09-08",
                    "👨"
                ),
                Review(
                    "Rachel Green",
                    4,
                    "Interesting mix of old and new. Great street food scene!",
                    "2024-08-20",
                    "👩"
                )
            )
        ),
        Destination(
            id = "lombok",
            name = "Lombok",
            country = "Indonesia",
            lat = -8.6500,
            lng = 116.3242,
            imageUrl = "https://picsum.photos/600/400?9",
            currencyCode = "IDR",
            rating = 4.7,
            reviewCount = 4321,
            description = "Bali's quieter neighbor, Lombok offers pristine beaches, Mount Rinjani trekking, traditional Sasak villages, and the stunning Gili Islands. Experience authentic island life with fewer crowds.",
            hotels = listOf(
                Hotel(
                    "The Oberoi Lombok",
                    4.8,
                    "Rp 4,000,000-7,000,000",
                    listOf("Beach", "Spa", "Pool", "Villas"),
                    "🏨"
                ),
                Hotel(
                    "Jeeva Beloam",
                    4.7,
                    "Rp 3,000,000-5,000,000",
                    listOf("Beach", "Spa", "Restaurant", "WiFi"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Pearl Beach Lounge", 4.6, "Seafood", "Rp Rp", "🦞"),
                Restaurant("Milk Espresso", 4.5, "Café", "Rp", "☕")
            ),
            topReviews = listOf(
                Review(
                    "Nina Patel",
                    5,
                    "More peaceful than Bali! Beautiful beaches and amazing snorkeling. Mount Rinjani trek was challenging but rewarding.",
                    "2024-10-05",
                    "👩"
                ),
                Review(
                    "Tom Wilson",
                    5,
                    "Hidden gem! Gili Islands are paradise. Less touristy and more authentic.",
                    "2024-09-18",
                    "👨"
                )
            )
        ),

        // Italy
        Destination(
            id = "rome",
            name = "Rome",
            country = "Italy",
            lat = 41.9028,
            lng = 12.4964,
            imageUrl = "https://picsum.photos/600/400?10",
            currencyCode = "EUR",
            rating = 4.9,
            reviewCount = 12345,
            description = "The Eternal City mesmerizes with the Colosseum, Vatican City, Trevi Fountain, and centuries of history. Experience incredible art, architecture, and authentic Italian cuisine in this ancient metropolis.",
            hotels = listOf(
                Hotel(
                    "Hotel Hassler Roma",
                    4.9,
                    "€500-900",
                    listOf("Spa", "Restaurant", "Bar", "City View"),
                    "🏨"
                ),
                Hotel(
                    "The St. Regis Rome",
                    4.8,
                    "€600-1000",
                    listOf("Spa", "Butler", "Fine Dining", "WiFi"),
                    "🏨"
                ),
                Hotel(
                    "Hotel de Russie",
                    4.8,
                    "€450-800",
                    listOf("Garden", "Spa", "Restaurant", "WiFi"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("La Pergola", 4.9, "Italian Fine Dining", "€€€€", "🍽️"),
                Restaurant("Trattoria Da Enzo", 4.7, "Roman Cuisine", "€€", "🍝"),
                Restaurant("Roscioli", 4.8, "Italian Deli", "€€", "🧀")
            ),
            topReviews = listOf(
                Review(
                    "Isabella Romano",
                    5,
                    "Rome is breathtaking! Every corner is a piece of history. The Colosseum at sunset is unforgettable.",
                    "2024-10-12",
                    "👩"
                ),
                Review(
                    "Marco Bianchi",
                    5,
                    "Incredible city! Vatican Museums are a must. The food is outstanding everywhere you go!",
                    "2024-09-28",
                    "👨"
                )
            )
        ),
        Destination(
            id = "venice",
            name = "Venice",
            country = "Italy",
            lat = 45.4408,
            lng = 12.3155,
            imageUrl = "https://picsum.photos/600/400?11",
            currencyCode = "EUR",
            rating = 4.8,
            reviewCount = 8765,
            description = "The floating city enchants with romantic gondola rides, St. Mark's Basilica, winding canals, and Renaissance art. Experience the unique beauty of this car-free architectural masterpiece.",
            hotels = listOf(
                Hotel(
                    "Aman Venice",
                    4.9,
                    "€800-1500",
                    listOf("Canal View", "Spa", "Restaurant", "Concierge"),
                    "🏨"
                ),
                Hotel(
                    "The Gritti Palace",
                    4.8,
                    "€700-1200",
                    listOf("Canal View", "Restaurant", "Bar", "WiFi"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Osteria alle Testiere", 4.8, "Venetian Seafood", "€€€", "🦐"),
                Restaurant("Antiche Carampane", 4.7, "Traditional Venetian", "€€", "🍽️")
            ),
            topReviews = listOf(
                Review(
                    "Giulia Ferrari",
                    5,
                    "Simply magical! The gondola ride through the canals is romantic and beautiful. St. Mark's Square is stunning.",
                    "2024-09-20",
                    "👩"
                ),
                Review(
                    "Antonio Rossi",
                    5,
                    "Unique city like no other. The architecture and atmosphere are incredible. A must-visit!",
                    "2024-08-15",
                    "👨"
                )
            )
        ),
        Destination(
            id = "milan",
            name = "Milan",
            country = "Italy",
            lat = 45.4642,
            lng = 9.1900,
            imageUrl = "https://picsum.photos/600/400?12",
            currencyCode = "EUR",
            rating = 4.7,
            reviewCount = 6543,
            description = "Italy's fashion capital combines high-end shopping, Leonardo da Vinci's Last Supper, the magnificent Duomo cathedral, and vibrant nightlife. Experience cutting-edge design alongside historical treasures.",
            hotels = listOf(
                Hotel(
                    "Armani Hotel Milano",
                    4.9,
                    "€400-700",
                    listOf("Spa", "Restaurant", "Bar", "Design"),
                    "🏨"
                ),
                Hotel(
                    "Bulgari Hotel Milano",
                    4.8,
                    "€500-900",
                    listOf("Garden", "Spa", "Restaurant", "WiFi"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Cracco", 4.8, "Contemporary Italian", "€€€€", "🍽️"),
                Restaurant("Luini Panzerotti", 4.6, "Street Food", "€", "🥐")
            ),
            topReviews = listOf(
                Review(
                    "Francesca Conti",
                    5,
                    "Fashion lover's dream! Shopping is incredible, Duomo is magnificent, and the food is amazing.",
                    "2024-10-03",
                    "👩"
                ),
                Review(
                    "Lorenzo Moretti",
                    4,
                    "Great city for culture and shopping. The Last Supper is worth the visit alone!",
                    "2024-09-10",
                    "👨"
                )
            )
        ),

        // United States
        Destination(
            id = "newyork",
            name = "New York",
            country = "USA",
            lat = 40.7128,
            lng = -74.0060,
            imageUrl = "https://picsum.photos/600/400?13",
            currencyCode = "USD",
            rating = 4.9,
            reviewCount = 15678,
            description = "The city that never sleeps offers iconic landmarks like Times Square, Statue of Liberty, Central Park, world-class museums, Broadway shows, and diverse cuisine from every culture.",
            hotels = listOf(
                Hotel(
                    "The Plaza Hotel",
                    4.9,
                    "$600-1200",
                    listOf("Spa", "Restaurant", "Bar", "Luxury"),
                    "🏨"
                ),
                Hotel(
                    "The St. Regis New York",
                    4.8,
                    "$700-1400",
                    listOf("Butler", "Spa", "Fine Dining", "Bar"),
                    "🏨"
                ),
                Hotel(
                    "The Peninsula New York",
                    4.9,
                    "$800-1500",
                    listOf("Spa", "Rooftop", "Restaurant", "Concierge"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Eleven Madison Park", 4.9, "Contemporary American", "$$$$", "🍽️"),
                Restaurant("Katz's Delicatessen", 4.7, "Deli", "$$", "🥪"),
                Restaurant("Peter Luger", 4.8, "Steakhouse", "$$$", "🥩")
            ),
            topReviews = listOf(
                Review(
                    "Emily Davis",
                    5,
                    "NYC is incredible! So much to see and do. Broadway shows were amazing, and the energy is unmatched!",
                    "2024-10-22",
                    "👩"
                ),
                Review(
                    "John Smith",
                    5,
                    "Best city in the world! Central Park, museums, food - everything is world-class. Can't wait to go back!",
                    "2024-10-10",
                    "👨"
                )
            )
        ),
        Destination(
            id = "losangeles",
            name = "Los Angeles",
            country = "USA",
            lat = 34.0522,
            lng = -118.2437,
            imageUrl = "https://picsum.photos/600/400?14",
            currencyCode = "USD",
            rating = 4.7,
            reviewCount = 9876,
            description = "The entertainment capital features Hollywood, beautiful beaches, perfect weather, diverse neighborhoods, and celebrity culture. Experience movie studios, surfing, and world-class dining.",
            hotels = listOf(
                Hotel(
                    "The Beverly Hills Hotel",
                    4.9,
                    "$700-1300",
                    listOf("Pool", "Spa", "Restaurant", "Luxury"),
                    "🏨"
                ),
                Hotel(
                    "Shutters on the Beach",
                    4.8,
                    "$500-900",
                    listOf("Beach", "Spa", "Restaurant", "Ocean View"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Providence", 4.9, "Seafood", "$$$$", "🦞"),
                Restaurant("In-N-Out Burger", 4.7, "Burgers", "$", "🍔")
            ),
            topReviews = listOf(
                Review(
                    "Ashley Martinez",
                    5,
                    "Love LA! Beaches are amazing, weather is perfect, and Hollywood is so cool. Highly recommend Venice Beach!",
                    "2024-09-30",
                    "👩"
                ),
                Review(
                    "Chris Taylor",
                    5,
                    "Great city with so much to offer. Studio tours were fantastic, and the food scene is incredible!",
                    "2024-08-25",
                    "👨"
                )
            )
        ),
        Destination(
            id = "miami",
            name = "Miami",
            country = "USA",
            lat = 25.7617,
            lng = -80.1918,
            imageUrl = "https://picsum.photos/600/400?15",
            currencyCode = "USD",
            rating = 4.8,
            reviewCount = 7654,
            description = "Tropical paradise with Art Deco architecture, vibrant nightlife, pristine beaches, Cuban culture, and year-round sunshine. Experience South Beach, Little Havana, and luxury waterfront living.",
            hotels = listOf(
                Hotel(
                    "Fontainebleau Miami Beach",
                    4.8,
                    "$400-800",
                    listOf("Beach", "Pool", "Spa", "Nightclub"),
                    "🏨"
                ),
                Hotel(
                    "The Setai Miami Beach",
                    4.9,
                    "$600-1100",
                    listOf("Beach", "Spa", "Pool", "Restaurant"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Zuma Miami", 4.8, "Japanese", "$$$", "🍣"),
                Restaurant("Joe's Stone Crab", 4.7, "Seafood", "$$$", "🦀")
            ),
            topReviews = listOf(
                Review(
                    "Maria Garcia",
                    5,
                    "Miami is paradise! Beautiful beaches, amazing nightlife, and great Cuban food. South Beach is stunning!",
                    "2024-10-15",
                    "👩"
                ),
                Review(
                    "Robert Johnson",
                    5,
                    "Perfect vacation spot! Weather is always great, beaches are pristine, and the energy is amazing!",
                    "2024-09-05",
                    "👨"
                )
            )
        ),

        // Thailand
        Destination(
            id = "bangkok",
            name = "Bangkok",
            country = "Thailand",
            lat = 13.7563,
            lng = 100.5018,
            imageUrl = "https://picsum.photos/600/400?16",
            currencyCode = "THB",
            rating = 4.8,
            reviewCount = 11234,
            description = "Thailand's vibrant capital offers ornate temples, floating markets, street food paradise, luxury malls, and legendary nightlife. Experience the Grand Palace, river cruises, and Thai massage.",
            hotels = listOf(
                Hotel(
                    "Mandarin Oriental Bangkok",
                    4.9,
                    "฿8,000-15,000",
                    listOf("River View", "Spa", "Restaurant", "Pool"),
                    "🏨"
                ),
                Hotel(
                    "The Peninsula Bangkok",
                    4.8,
                    "฿7,000-12,000",
                    listOf("River View", "Spa", "Pool", "Restaurant"),
                    "🏨"
                ),
                Hotel(
                    "Lebua at State Tower",
                    4.8,
                    "฿6,000-11,000",
                    listOf("Rooftop Bar", "Pool", "Restaurant", "City View"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Gaggan Anand", 4.9, "Progressive Indian", "฿฿฿฿", "🍽️"),
                Restaurant("Street Food Yaowarat", 4.7, "Thai Street Food", "฿", "🍜"),
                Restaurant("Blue Elephant", 4.6, "Royal Thai", "฿฿฿", "🍽️")
            ),
            topReviews = listOf(
                Review(
                    "Somchai Patel",
                    5,
                    "Bangkok is incredible! Temples are stunning, street food is amazing, and people are so friendly. Must visit Grand Palace!",
                    "2024-10-08",
                    "👨"
                ),
                Review(
                    "Jennifer Lee",
                    5,
                    "Loved everything about Bangkok! Shopping is great, food is delicious, and massages are heavenly. Can't wait to return!",
                    "2024-09-22",
                    "👩"
                )
            )
        ),
        Destination(
            id = "phuket",
            name = "Phuket",
            country = "Thailand",
            lat = 7.8804,
            lng = 98.3923,
            imageUrl = "https://picsum.photos/600/400?17",
            currencyCode = "THB",
            rating = 4.7,
            reviewCount = 8765,
            description = "Thailand's largest island paradise offers stunning beaches, crystal-clear waters, water sports, vibrant nightlife, and island hopping. Experience Patong Beach, Phi Phi Islands, and sunset views.",
            hotels = listOf(
                Hotel(
                    "Amanpuri",
                    4.9,
                    "฿20,000-40,000",
                    listOf("Beach", "Private Villa", "Spa", "Pool"),
                    "🏨"
                ),
                Hotel(
                    "The Slate Phuket",
                    4.7,
                    "฿5,000-10,000",
                    listOf("Beach", "Pool", "Spa", "Restaurant"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("PRU", 4.9, "Farm to Table", "฿฿฿฿", "🍽️"),
                Restaurant("Baan Rim Pa", 4.7, "Thai Cuisine", "฿฿฿", "🍽️")
            ),
            topReviews = listOf(
                Review(
                    "Nong Williams",
                    5,
                    "Beach paradise! Water is crystal clear, beaches are beautiful, and island tours are amazing. Perfect tropical getaway!",
                    "2024-09-15",
                    "👩"
                ),
                Review(
                    "Steve Brown",
                    5,
                    "Incredible destination! Great for water sports, beautiful scenery, and excellent seafood. Highly recommended!",
                    "2024-08-30",
                    "👨"
                )
            )
        ),
        Destination(
            id = "chiangmai",
            name = "Chiang Mai",
            country = "Thailand",
            lat = 18.7883,
            lng = 98.9853,
            imageUrl = "https://picsum.photos/600/400?18",
            currencyCode = "THB",
            rating = 4.8,
            reviewCount = 6543,
            description = "Northern Thailand's cultural heart offers ancient temples, elephant sanctuaries, night markets, cooking classes, and mountain adventures. Experience authentic Thai culture and natural beauty.",
            hotels = listOf(
                Hotel(
                    "137 Pillars House",
                    4.9,
                    "฿7,000-13,000",
                    listOf("Pool", "Spa", "Restaurant", "Garden"),
                    "🏨"
                ),
                Hotel(
                    "Four Seasons Chiang Mai",
                    4.8,
                    "฿8,000-15,000",
                    listOf("Rice Field View", "Spa", "Pool", "Activities"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("David's Kitchen", 4.8, "French-Thai Fusion", "฿฿฿", "🍽️"),
                Restaurant("Khao Soi Khun Yai", 4.7, "Northern Thai", "฿", "🍜")
            ),
            topReviews = listOf(
                Review(
                    "Anna Schmidt",
                    5,
                    "Loved Chiang Mai! Temples are beautiful, cooking class was fun, and elephant sanctuary was ethical and amazing!",
                    "2024-10-01",
                    "👩"
                ),
                Review(
                    "Peter Hansen",
                    5,
                    "Perfect cultural experience! Night markets are great, people are wonderful, and the mountains are stunning!",
                    "2024-09-12",
                    "👨"
                )
            )
        ),

        // Spain
        Destination(
            id = "barcelona",
            name = "Barcelona",
            country = "Spain",
            lat = 41.3851,
            lng = 2.1734,
            imageUrl = "https://picsum.photos/600/400?19",
            currencyCode = "EUR",
            rating = 4.9,
            reviewCount = 13456,
            description = "Gaudí's masterpiece city combines stunning architecture, Mediterranean beaches, tapas culture, and vibrant nightlife. Explore Sagrada Família, Park Güell, and Las Ramblas in this Catalan capital.",
            hotels = listOf(
                Hotel(
                    "Hotel Arts Barcelona",
                    4.9,
                    "€350-700",
                    listOf("Beach", "Spa", "Pool", "Michelin Star"),
                    "🏨"
                ),
                Hotel(
                    "Majestic Hotel & Spa",
                    4.8,
                    "€300-600",
                    listOf("Rooftop Pool", "Spa", "Restaurant", "WiFi"),
                    "🏨"
                ),
                Hotel(
                    "El Palace Barcelona",
                    4.8,
                    "€280-550",
                    listOf("Spa", "Restaurant", "Bar", "Historic"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Tickets Bar", 4.9, "Tapas", "€€€", "🍽️"),
                Restaurant("Cervecería Catalana", 4.7, "Tapas", "€€", "🍤"),
                Restaurant("Disfrutar", 4.9, "Contemporary Spanish", "€€€€", "🍽️")
            ),
            topReviews = listOf(
                Review(
                    "Carmen Lopez",
                    5,
                    "Barcelona is magical! Gaudí's architecture is breathtaking. Beach, food, culture - everything is perfect!",
                    "2024-10-18",
                    "👩"
                ),
                Review(
                    "Miguel Torres",
                    5,
                    "Amazing city! Sagrada Família left me speechless. Tapas are delicious and nightlife is fantastic!",
                    "2024-09-25",
                    "👨"
                )
            )
        ),
        Destination(
            id = "madrid",
            name = "Madrid",
            country = "Spain",
            lat = 40.4168,
            lng = -3.7038,
            imageUrl = "https://picsum.photos/600/400?20",
            currencyCode = "EUR",
            rating = 4.8,
            reviewCount = 9876,
            description = "Spain's elegant capital offers world-class museums like the Prado, Royal Palace, Retiro Park, and legendary nightlife. Experience flamenco, tapas bars, and grand boulevards.",
            hotels = listOf(
                Hotel(
                    "The Westin Palace Madrid",
                    4.8,
                    "€250-500",
                    listOf("Historic", "Restaurant", "Bar", "WiFi"),
                    "🏨"
                ),
                Hotel(
                    "Villa Magna Madrid",
                    4.7,
                    "€300-600",
                    listOf("Spa", "Restaurant", "Bar", "Concierge"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("DiverXO", 4.9, "Avant-Garde", "€€€€", "🍽️"),
                Restaurant("Mercado de San Miguel", 4.6, "Market Food", "€€", "🍢")
            ),
            topReviews = listOf(
                Review(
                    "Sofia Ramirez",
                    5,
                    "Love Madrid! Museums are world-class, parks are beautiful, and the food scene is incredible. Great nightlife too!",
                    "2024-09-20",
                    "👩"
                ),
                Review(
                    "Carlos Mendez",
                    5,
                    "Fantastic city! Prado Museum is a must-see. Tapas hopping is so much fun. Highly recommend!",
                    "2024-08-15",
                    "👨"
                )
            )
        ),
        Destination(
            id = "seville",
            name = "Seville",
            country = "Spain",
            lat = 37.3891,
            lng = -5.9845,
            imageUrl = "https://picsum.photos/600/400?21",
            currencyCode = "EUR",
            rating = 4.8,
            reviewCount = 7654,
            description = "Andalusia's heart offers passionate flamenco, Moorish architecture, orange-tree-lined streets, and tapas culture. Experience the Alcázar, Gothic cathedral, and authentic Spanish charm.",
            hotels = listOf(
                Hotel(
                    "Hotel Alfonso XIII",
                    4.9,
                    "€300-600",
                    listOf("Historic", "Pool", "Spa", "Restaurant"),
                    "🏨"
                ),
                Hotel(
                    "EME Catedral Hotel",
                    4.7,
                    "€200-400",
                    listOf("Rooftop Pool", "Cathedral View", "Restaurant", "WiFi"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Abantal", 4.8, "Contemporary Andalusian", "€€€", "🍽️"),
                Restaurant("El Rinconcillo", 4.6, "Traditional Tapas", "€€", "🍢")
            ),
            topReviews = listOf(
                Review(
                    "Isabel Moreno",
                    5,
                    "Seville is enchanting! Alcázar is stunning, flamenco shows are passionate, and the food is amazing!",
                    "2024-10-05",
                    "👩"
                ),
                Review(
                    "Antonio Gomez",
                    5,
                    "Beautiful city with so much character! Cathedral is magnificent and tapas culture is wonderful!",
                    "2024-09-18",
                    "👨"
                )
            )
        ),

        // United Kingdom
        Destination(
            id = "london",
            name = "London",
            country = "UK",
            lat = 51.5074,
            lng = -0.1278,
            imageUrl = "https://picsum.photos/600/400?22",
            currencyCode = "GBP",
            rating = 4.9,
            reviewCount = 18765,
            description = "Britain's iconic capital offers Big Ben, Buckingham Palace, Tower Bridge, world-class museums, West End theaters, and diverse cuisine. Experience royal history alongside modern culture.",
            hotels = listOf(
                Hotel(
                    "The Savoy",
                    4.9,
                    "£400-800",
                    listOf("Thames View", "Spa", "Restaurant", "Historic"),
                    "🏨"
                ),
                Hotel(
                    "The Ritz London",
                    4.9,
                    "£500-1000",
                    listOf("Afternoon Tea", "Spa", "Fine Dining", "Luxury"),
                    "🏨"
                ),
                Hotel(
                    "Claridge's",
                    4.8,
                    "£450-900",
                    listOf("Spa", "Restaurant", "Art Deco", "Concierge"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("The Ledbury", 4.9, "Contemporary European", "££££", "🍽️"),
                Restaurant("Dishoom", 4.7, "Indian", "££", "🍛"),
                Restaurant("Sketch", 4.8, "Contemporary", "£££", "🍽️")
            ),
            topReviews = listOf(
                Review(
                    "Emma Watson",
                    5,
                    "London is incredible! So much history and culture. Museums are free and world-class. Big Ben is iconic!",
                    "2024-10-20",
                    "👩"
                ),
                Review(
                    "Oliver Smith",
                    5,
                    "Best city in Europe! Theater, food, shopping - everything is amazing. Tower of London tour was fascinating!",
                    "2024-10-08",
                    "👨"
                )
            )
        ),
        Destination(
            id = "edinburgh",
            name = "Edinburgh",
            country = "UK",
            lat = 55.9533,
            lng = -3.1883,
            imageUrl = "https://picsum.photos/600/400?23",
            currencyCode = "GBP",
            rating = 4.8,
            reviewCount = 6543,
            description = "Scotland's historic capital offers Edinburgh Castle, Royal Mile, dramatic landscapes, whisky culture, and festivals. Experience medieval charm and Scottish hospitality.",
            hotels = listOf(
                Hotel(
                    "The Balmoral",
                    4.9,
                    "£300-600",
                    listOf("Castle View", "Spa", "Restaurant", "Historic"),
                    "🏨"
                ),
                Hotel(
                    "The Witchery",
                    4.7,
                    "£250-500",
                    listOf("Historic", "Restaurant", "Gothic", "Romantic"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("The Kitchin", 4.9, "Scottish Fine Dining", "££££", "🍽️"),
                Restaurant("The Scran & Scallie", 4.6, "Gastropub", "££", "🍺")
            ),
            topReviews = listOf(
                Review(
                    "Fiona MacLeod",
                    5,
                    "Edinburgh is magical! Castle is stunning, Old Town is charming, and whisky tours are fantastic!",
                    "2024-09-15",
                    "👩"
                ),
                Review(
                    "Andrew Campbell",
                    5,
                    "Beautiful city with rich history! Arthur's Seat hike offers amazing views. Loved every minute!",
                    "2024-08-30",
                    "👨"
                )
            )
        ),
        Destination(
            id = "manchester",
            name = "Manchester",
            country = "UK",
            lat = 53.4808,
            lng = -2.2426,
            imageUrl = "https://picsum.photos/600/400?24",
            currencyCode = "GBP",
            rating = 4.6,
            reviewCount = 4321,
            description = "Industrial heritage meets modern culture in this vibrant city. Experience world-famous football, music scene, museums, and Northern Quarter's independent spirit.",
            hotels = listOf(
                Hotel(
                    "The Lowry Hotel",
                    4.7,
                    "£150-300",
                    listOf("River View", "Spa", "Restaurant", "Modern"),
                    "🏨"
                ),
                Hotel(
                    "Hotel Gotham",
                    4.6,
                    "£120-250",
                    listOf("Club Lounge", "Bar", "Art Deco", "WiFi"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Adam Reid at The French", 4.8, "British Fine Dining", "£££", "🍽️"),
                Restaurant("Hawksmoor Manchester", 4.7, "Steakhouse", "£££", "🥩")
            ),
            topReviews = listOf(
                Review(
                    "Lucy Brown",
                    4,
                    "Great city with so much energy! Football museum is a must for fans. Northern Quarter is cool and quirky!",
                    "2024-10-01",
                    "👩"
                ),
                Review(
                    "James Walker",
                    4,
                    "Loved the music scene and nightlife! People are friendly and there's always something happening!",
                    "2024-09-12",
                    "👨"
                )
            )
        ),

        // United Arab Emirates
        Destination(
            id = "dubai",
            name = "Dubai",
            country = "UAE",
            lat = 25.2048,
            lng = 55.2708,
            imageUrl = "https://picsum.photos/600/400?25",
            currencyCode = "AED",
            rating = 4.9,
            reviewCount = 16789,
            description = "Futuristic metropolis featuring Burj Khalifa, luxury shopping, desert safaris, pristine beaches, and extravagant hotels. Experience ultra-modern architecture and traditional souks.",
            hotels = listOf(
                Hotel(
                    "Burj Al Arab",
                    5.0,
                    "AED 5,000-15,000",
                    listOf("7-Star", "Beach", "Spa", "Butler"),
                    "🏨"
                ),
                Hotel(
                    "Atlantis The Palm",
                    4.8,
                    "AED 1,500-4,000",
                    listOf("Aquarium", "Waterpark", "Beach", "Spa"),
                    "🏨"
                ),
                Hotel(
                    "Armani Hotel Dubai",
                    4.9,
                    "AED 2,000-5,000",
                    listOf("Burj Khalifa", "Spa", "Restaurant", "Design"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant(
                    "At.mosphere",
                    4.8,
                    "International Fine Dining",
                    "AED AED AED AED",
                    "🍽️"
                ),
                Restaurant("Pierchic", 4.9, "Seafood", "AED AED AED", "🦞"),
                Restaurant("Al Mallah", 4.6, "Lebanese", "AED", "🥙")
            ),
            topReviews = listOf(
                Review(
                    "Fatima Al Zarooni",
                    5,
                    "Dubai is incredible! Burj Khalifa views are breathtaking. Shopping is world-class and desert safari was thrilling!",
                    "2024-10-22",
                    "👩"
                ),
                Review(
                    "Ahmed Hassan",
                    5,
                    "Futuristic and luxurious! Everything is modern and efficient. Great for families with kids. Highly recommend!",
                    "2024-10-10",
                    "👨"
                )
            )
        ),
        Destination(
            id = "abudhabi",
            name = "Abu Dhabi",
            country = "UAE",
            lat = 24.4539,
            lng = 54.3773,
            imageUrl = "https://picsum.photos/600/400?26",
            currencyCode = "AED",
            rating = 4.8,
            reviewCount = 8765,
            description = "UAE's capital combines modern architecture, Sheikh Zayed Grand Mosque, Louvre Abu Dhabi, Ferrari World, and cultural heritage. Experience luxury and tradition in this coastal city.",
            hotels = listOf(
                Hotel(
                    "Emirates Palace",
                    4.9,
                    "AED 2,500-6,000",
                    listOf("Beach", "Spa", "Gold ATM", "Luxury"),
                    "🏨"
                ),
                Hotel(
                    "The St. Regis Saadiyat Island",
                    4.8,
                    "AED 1,800-4,000",
                    listOf("Beach", "Golf", "Spa", "Restaurant"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Hakkasan", 4.8, "Modern Cantonese", "AED AED AED", "🍽️"),
                Restaurant("Li Beirut", 4.7, "Lebanese", "AED AED", "🥙")
            ),
            topReviews = listOf(
                Review(
                    "Sara Mahmoud",
                    5,
                    "Grand Mosque is stunning! Louvre is world-class. More cultural than Dubai and less crowded. Loved it!",
                    "2024-09-30",
                    "👩"
                ),
                Review(
                    "Omar Abdullah",
                    5,
                    "Beautiful city! Ferrari World is a blast. Clean, safe, and lots to see. Great family destination!",
                    "2024-08-25",
                    "👨"
                )
            )
        ),
        Destination(
            id = "sharjah",
            name = "Sharjah",
            country = "UAE",
            lat = 25.3463,
            lng = 55.4209,
            imageUrl = "https://picsum.photos/600/400?27",
            currencyCode = "AED",
            rating = 4.5,
            reviewCount = 4321,
            description = "UAE's cultural capital offers museums, art galleries, traditional souks, and Islamic architecture. Experience authentic Emirati culture and heritage in this family-friendly emirate.",
            hotels = listOf(
                Hotel(
                    "Sharjah Grand Hotel",
                    4.6,
                    "AED 400-800",
                    listOf("Pool", "Restaurant", "Beach Access", "WiFi"),
                    "🏨"
                ),
                Hotel(
                    "Radisson Blu Resort Sharjah",
                    4.5,
                    "AED 500-900",
                    listOf("Beach", "Pool", "Restaurant", "Spa"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Al Qasba", 4.5, "International", "AED AED", "🍽️"),
                Restaurant("Turkish Village", 4.6, "Turkish", "AED AED", "🥙")
            ),
            topReviews = listOf(
                Review(
                    "Aisha Mohammed",
                    4,
                    "Cultural and family-friendly! Museums are great and much more affordable than Dubai. Nice for a day trip!",
                    "2024-10-05",
                    "👩"
                ),
                Review(
                    "Khalid Ibrahim",
                    4,
                    "Good for families! Islamic museums are informative. Souks are authentic and less touristy.",
                    "2024-09-18",
                    "👨"
                )
            )
        ),

        // India
        Destination(
            id = "delhi",
            name = "Delhi",
            country = "India",
            lat = 28.7041,
            lng = 77.1025,
            imageUrl = "https://picsum.photos/600/400?28",
            currencyCode = "INR",
            rating = 4.6,
            reviewCount = 11234,
            description = "India's capital blends ancient monuments, bustling bazaars, Mughal architecture, and street food paradise. Experience Red Fort, Qutub Minar, and diverse cultures in this historic metropolis.",
            hotels = listOf(
                Hotel(
                    "The Leela Palace New Delhi",
                    4.8,
                    "₹15,000-30,000",
                    listOf("Spa", "Pool", "Restaurant", "Luxury"),
                    "🏨"
                ),
                Hotel(
                    "The Oberoi Delhi",
                    4.7,
                    "₹12,000-25,000",
                    listOf("Pool", "Spa", "Restaurant", "Golf"),
                    "🏨"
                ),
                Hotel(
                    "Taj Palace New Delhi",
                    4.7,
                    "₹10,000-20,000",
                    listOf("Pool", "Spa", "Restaurant", "Bar"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Indian Accent", 4.9, "Contemporary Indian", "₹₹₹₹", "🍽️"),
                Restaurant("Karim's", 4.6, "Mughlai", "₹", "🍛"),
                Restaurant("Bukhara", 4.8, "North Indian", "₹₹₹", "🍽️")
            ),
            topReviews = listOf(
                Review(
                    "Priya Sharma",
                    5,
                    "Delhi is fascinating! Rich history everywhere you look. Street food is incredible and people are warm and welcoming!",
                    "2024-10-18",
                    "👩"
                ),
                Review(
                    "Raj Kapoor",
                    4,
                    "Great city with amazing food and culture! Traffic can be crazy but worth it for the experience. Red Fort is magnificent!",
                    "2024-09-25",
                    "👨"
                )
            )
        ),
        Destination(
            id = "mumbai",
            name = "Mumbai",
            country = "India",
            lat = 19.0760,
            lng = 72.8777,
            imageUrl = "https://picsum.photos/600/400?29",
            currencyCode = "INR",
            rating = 4.7,
            reviewCount = 13456,
            description = "India's financial capital offers Bollywood glamour, Gateway of India, Marine Drive, street food culture, and vibrant nightlife. Experience the city that never sleeps.",
            hotels = listOf(
                Hotel(
                    "The Taj Mahal Palace",
                    4.9,
                    "₹18,000-35,000",
                    listOf("Historic", "Sea View", "Spa", "Pool"),
                    "🏨"
                ),
                Hotel(
                    "The Oberoi Mumbai",
                    4.8,
                    "₹15,000-28,000",
                    listOf("Sea View", "Spa", "Restaurant", "Pool"),
                    "🏨"
                )
            ),
            restaurants = listOf(
                Restaurant("Trishna", 4.8, "Seafood", "₹₹₹", "🦀"),
                Restaurant("Britannia & Co.", 4.6, "Parsi", "₹", "🍛")
            ),
            topReviews = listOf(
                Review(
                    "Ananya Desai",
                    5,
                    "Mumbai has incredible energy! Marine Drive sunset is beautiful. Street food is amazing and Bollywood tours are fun!",
                    "2024-10-12",
                    "👩"
                ),
                Review(
                    "Arjun Patel",
                    5,
                    "City of dreams indeed! So much to see and do. Gateway of India is iconic. Food scene is unbeatable!",
                    "2024-09-28",
                    "👨"
                )
            )
        ),
        Destination(
            id = "goa",
            name = "Goa",
            country = "India",
            lat = 15.2993,
            lng = 74.1240,
            imageUrl = "https://picsum.photos/600/400?30",
            currencyCode = "INR",
            rating = 4.8,
            reviewCount = 15678,
            description = "India's beach paradise offers golden sands, Portuguese architecture, water sports, vibrant nightlife, and seafood delights. Experience laid-back beach culture and spiritual retreats.",
            hotels = listOf(
                Hotel(
                    "Taj Exotica Resort & Spa",
                    4.8,
                    "₹12,000-22,000",
                    listOf("Beach", "Pool", "Spa", "Golf"),
                    "🏨"
                ),
                Hotel(
                    "Alila Diwa Goa",
                    4.7,
                    "₹10,000-18,000",
                    listOf("Beach", "Pool", "Spa", "Restaurant"),
                    "🏨"
                ),
                Hotel("W Goa", 4.8, "₹15,000-28,000", listOf("Beach", "Pool", "Bar", "Modern"), "🏨")
            ),
            restaurants = listOf(
                Restaurant("Thalassa", 4.8, "Greek", "₹₹₹", "🍽️"),
                Restaurant("Fisherman's Wharf", 4.6, "Seafood", "₹₹", "🦐"),
                Restaurant("Gunpowder", 4.7, "South Indian", "₹₹", "🍛")
            ),
            topReviews = listOf(
                Review(
                    "Neha Reddy",
                    5,
                    "Goa is paradise! Beaches are beautiful, nightlife is amazing, and the vibe is so relaxed. Perfect vacation spot!",
                    "2024-10-20",
                    "👩"
                ),
                Review(
                    "Vikram Singh",
                    5,
                    "Loved every moment! Water sports are fun, seafood is fresh and delicious. Great mix of party and peace!",
                    "2024-10-08",
                    "👨"
                )
            )
        )
    )

    // Use StateFlow for reactive updates
    private val _likedDestinations = MutableStateFlow<Set<String>>(emptySet())
    val likedDestinations: StateFlow<Set<String>> = _likedDestinations.asStateFlow()

    private val _likedPlans = MutableStateFlow<Set<String>>(emptySet())
    val likedPlans: StateFlow<Set<String>> = _likedPlans.asStateFlow()

    private val plans = mutableMapOf<String, Plan>()
    private val _plansVersion = MutableStateFlow(0) // Trigger for plan changes
    val plansVersion: StateFlow<Int> = _plansVersion.asStateFlow()

    // User management with password
    private var currentUser: User? = null
    private val registeredUsers =
        mutableMapOf<String, Pair<User, String>>()  // username -> (User, password)

    fun getPopularDestinations(): List<Destination> = destinations
    fun getDestination(id: String): Destination? = destinations.find { it.id == id }

    fun likeDestination(id: String) {
        _likedDestinations.value = _likedDestinations.value + id
    }

    fun unlikeDestination(id: String) {
        _likedDestinations.value = _likedDestinations.value - id
    }

    fun isDestinationLiked(id: String): Boolean = _likedDestinations.value.contains(id)

    fun getLikedDestinations(): List<Destination> =
        _likedDestinations.value.mapNotNull { getDestination(it) }

    fun savePlan(plan: Plan) {
        plans[plan.id] = plan
        _plansVersion.value += 1 // Increment version to trigger updates
    }
    fun getPlan(planId: String): Plan? = plans[planId]
    fun getAllPlans(): List<Plan> = plans.values.toList()

    fun likePlan(planId: String) {
        _likedPlans.value = _likedPlans.value + planId
    }

    fun unlikePlan(planId: String) {
        _likedPlans.value = _likedPlans.value - planId
    }

    fun isPlanLiked(planId: String): Boolean = _likedPlans.value.contains(planId)

    fun getLikedPlans(): List<Plan> =
        _likedPlans.value.mapNotNull { plans[it] }

    // User functions with password
    fun registerUser(
        username: String,
        password: String,
        name: String,
        email: String,
        countryCode: String,
        phone: String
    ): Boolean {
        // Check if username already exists
        if (registeredUsers.containsKey(username)) {
            return false // Username already taken
        }
        val user = User(username, name, email, countryCode, phone)
        registeredUsers[username] = Pair(user, password)
        currentUser = user
        return true
    }

    fun loginUser(username: String, password: String): Boolean {
        val userPair = registeredUsers[username]
        if (userPair != null && userPair.second == password) {
            currentUser = userPair.first
            return true
        }
        return false
    }

    fun getCurrentUser(): User? = currentUser

    fun updateUser(user: User) {
        currentUser = user
        val existingPassword = registeredUsers[user.username]?.second ?: ""
        registeredUsers[user.username] = Pair(user, existingPassword)
    }

    fun updatePassword(username: String, oldPassword: String, newPassword: String): Boolean {
        val userPair = registeredUsers[username]
        if (userPair != null && userPair.second == oldPassword) {
            registeredUsers[username] = Pair(userPair.first, newPassword)
            return true
        }
        return false
    }

    fun isLoggedIn(): Boolean = currentUser != null
}
