package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIconHelper {
    fun getIconVector(iconName: String): ImageVector {
        return when (iconName) {
            "restaurant" -> Icons.Filled.Restaurant
            "local_cafe" -> Icons.Filled.LocalCafe
            "local_dining" -> Icons.Filled.LocalDining
            "local_bar" -> Icons.Filled.LocalBar
            "local_pizza" -> Icons.Filled.LocalPizza
            "local_drink" -> Icons.Filled.LocalDrink
            "local_grocery_store" -> Icons.Filled.LocalGroceryStore
            "directions_bus" -> Icons.Filled.DirectionsBus
            "directions_car" -> Icons.Filled.DirectionsCar
            "directions_run" -> Icons.Filled.DirectionsRun
            "directions_walk" -> Icons.Filled.DirectionsWalk
            "flight" -> Icons.Filled.Flight
            "local_gas_station" -> Icons.Filled.LocalGasStation
            "local_shipping" -> Icons.Filled.LocalShipping
            "local_taxi" -> Icons.Filled.LocalTaxi
            "subway" -> Icons.Filled.Subway
            "local_parking" -> Icons.Filled.LocalParking
            "shopping_bag" -> Icons.Filled.ShoppingBag
            "shopping_cart" -> Icons.Filled.ShoppingCart
            "local_mall" -> Icons.Filled.LocalMall
            "local_offer" -> Icons.Filled.LocalOffer
            "store" -> Icons.Filled.Store
            "style" -> Icons.Filled.Style
            "content_cut" -> Icons.Filled.ContentCut
            "brush" -> Icons.Filled.Brush
            "sports_esports" -> Icons.Filled.SportsEsports
            "movie" -> Icons.Filled.Movie
            "music_note" -> Icons.Filled.MusicNote
            "headset" -> Icons.Filled.Headset
            "tv" -> Icons.Filled.Tv
            "local_activity" -> Icons.Filled.LocalActivity
            "palette" -> Icons.Filled.Palette
            "park" -> Icons.Filled.Park
            "photo_camera" -> Icons.Filled.PhotoCamera
            "camera_alt" -> Icons.Filled.CameraAlt
            "casino" -> Icons.Filled.Casino
            "fitness_center" -> Icons.Filled.FitnessCenter
            "sports_basketball" -> Icons.Filled.SportsBasketball
            "sports_soccer" -> Icons.Filled.SportsSoccer
            "receipt_long" -> Icons.Filled.ReceiptLong
            "receipt" -> Icons.Filled.Receipt
            "payments" -> Icons.Filled.Payments
            "payment" -> Icons.Filled.Payment
            "card_giftcard" -> Icons.Filled.CardGiftcard
            "trending_up" -> Icons.Filled.TrendingUp
            "savings" -> Icons.Filled.Savings
            "account_balance_wallet" -> Icons.Filled.AccountBalanceWallet
            "account_balance" -> Icons.Filled.AccountBalance
            "credit_card" -> Icons.Filled.CreditCard
            "qr_code" -> Icons.Filled.QrCode
            "contactless" -> Icons.Filled.Contactless
            "attach_money" -> Icons.Filled.AttachMoney
            "local_atm" -> Icons.Filled.LocalAtm
            "monetization_on" -> Icons.Filled.MonetizationOn
            "euro" -> Icons.Filled.Euro
            "sell" -> Icons.Filled.Sell
            "home" -> Icons.Filled.Home
            "hotel" -> Icons.Filled.Hotel
            "local_hotel" -> Icons.Filled.LocalHotel
            "local_laundry_service" -> Icons.Filled.LocalLaundryService
            "pets" -> Icons.Filled.Pets
            "water_drop" -> Icons.Filled.WaterDrop
            "bolt" -> Icons.Filled.Bolt
            "lightbulb" -> Icons.Filled.Lightbulb
            "wifi" -> Icons.Filled.Wifi
            "phone" -> Icons.Filled.Phone
            "phone_android" -> Icons.Filled.PhoneAndroid
            "smartphone" -> Icons.Filled.Smartphone
            "call" -> Icons.Filled.Call
            "build" -> Icons.Filled.Build
            "construction" -> Icons.Filled.Construction
            "handyman" -> Icons.Filled.Handyman
            "medical_services" -> Icons.Filled.MedicalServices
            "local_hospital" -> Icons.Filled.LocalHospital
            "local_pharmacy" -> Icons.Filled.LocalPharmacy
            "health_and_safety" -> Icons.Filled.HealthAndSafety
            "school" -> Icons.Filled.School
            "menu_book" -> Icons.Filled.MenuBook
            "auto_stories" -> Icons.Filled.AutoStories
            "psychology" -> Icons.Filled.Psychology
            "work" -> Icons.Filled.Work
            "computer" -> Icons.Filled.Computer
            "laptop" -> Icons.Filled.Laptop
            "print" -> Icons.Filled.Print
            "local_printshop" -> Icons.Filled.LocalPrintshop
            "email" -> Icons.Filled.Email
            "send" -> Icons.Filled.Send
            "calendar_today" -> Icons.Filled.CalendarToday
            "event" -> Icons.Filled.Event
            "schedule" -> Icons.Filled.Schedule
            "watch" -> Icons.Filled.Watch
            "alarm" -> Icons.Filled.Alarm
            "lock" -> Icons.Filled.Lock
            "key" -> Icons.Filled.Key
            "shield" -> Icons.Filled.Shield
            "star" -> Icons.Filled.Star
            "favorite" -> Icons.Filled.Favorite
            "auto_awesome" -> Icons.Filled.AutoAwesome
            "place" -> Icons.Filled.Place
            "more_horiz" -> Icons.Filled.MoreHoriz
            else -> Icons.Filled.Category
        }
    }

    val AVAILABLE_ICONS = listOf(
        "restaurant", "local_cafe", "local_dining", "local_bar", "local_pizza",
        "local_drink", "local_grocery_store", "directions_bus", "directions_car", "directions_run",
        "directions_walk", "flight", "local_gas_station", "local_shipping", "local_taxi",
        "subway", "local_parking", "shopping_bag", "shopping_cart", "local_mall",
        "local_offer", "store", "style", "content_cut", "brush",
        "sports_esports", "movie", "music_note", "headset", "tv",
        "local_activity", "palette", "park", "photo_camera", "camera_alt",
        "casino", "fitness_center", "sports_basketball", "sports_soccer", "receipt_long",
        "receipt", "payments", "payment", "card_giftcard", "trending_up",
        "savings", "account_balance_wallet", "account_balance", "credit_card", "qr_code",
        "contactless", "attach_money", "local_atm", "monetization_on", "euro",
        "sell", "home", "hotel", "local_hotel", "local_laundry_service",
        "pets", "water_drop", "bolt", "lightbulb", "wifi",
        "phone", "phone_android", "smartphone", "call", "build",
        "construction", "handyman", "medical_services", "local_hospital", "local_pharmacy",
        "health_and_safety", "school", "menu_book", "auto_stories", "psychology",
        "work", "computer", "laptop", "print", "local_printshop",
        "email", "send", "calendar_today", "event", "schedule",
        "watch", "alarm", "lock", "key", "shield",
        "star", "favorite", "auto_awesome", "place", "more_horiz"
    )

    val PRESET_COLORS = listOf(
        0xFFFF5252, // Red
        0xFFFB8C00, // Orange
        0xFFFFB300, // Amber
        0xFF10B981, // Emerald Green
        0xFF29B6F6, // Light Blue
        0xFF3B82F6, // Blue
        0xFF7E57C2, // Purple
        0xFFAB47BC, // Magenta
        0xFFEC4899, // Pink
        0xFF8B5CF6, // Violet
        0xFF26A69A, // Teal
        0xFF78909C  // Blue Grey
    )

    val EXPANDED_COLORS = listOf(
        0xFFD32F2F, 0xFFC2185B, 0xFFE53935, 0xFFFF1744, 0xFF880E4F,
        0xFFF57C00, 0xFFFF6D00, 0xFFE65100, 0xFFFF7043, 0xFFFFAB91,
        0xFFFBC02D, 0xFFF57F17, 0xFFFFD600, 0xFFFFF176, 0xFFAFB42B,
        0xFF388E3C, 0xFF66BB6A, 0xFF00E676, 0xFF00C853, 0xFF1B5E20,
        0xFF009688, 0xFF00B4D8, 0xFF00E5FF, 0xFF00838F, 0xFF80DEEA,
        0xFF1976D2, 0xFF0288D1, 0xFF2962FF, 0xFF0D47A1, 0xFF90CAF9,
        0xFF512DA8, 0xFF303F9F, 0xFF6200EA, 0xFF9C27B0, 0xFFD1C4E9,
        0xFFC2185B, 0xFFF50057, 0xFFFF4081, 0xFFF8BBD0, 0xFFAD1457,
        0xFF5D4037, 0xFF8D6E63, 0xFFA1887F, 0xFFD7CCC8, 0xFFBF360C,
        0xFF455A64, 0xFF37474F, 0xFF607D8B, 0xFF90A4AE, 0xFF212121
    )
}
