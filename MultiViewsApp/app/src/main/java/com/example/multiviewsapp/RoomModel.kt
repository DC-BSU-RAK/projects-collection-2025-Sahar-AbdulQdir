data class StudyRoom(
    val id: String,
    val name: String,
    val bgColor: String = "#FFFFFF", // Only used if no image
    val bgImageUri: String? = null,  // For custom image backgrounds
    val widgetColor: String = "white",
    val fontColor: String = "black",
    val imageResName: String = ""    // Used for predefined drawable rooms
)
