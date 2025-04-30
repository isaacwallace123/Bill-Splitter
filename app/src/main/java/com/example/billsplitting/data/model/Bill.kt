import androidx.room.*

@Entity(tableName = "billDatabase")
data class Bill(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val restaurantName: String,
    val totalAmount: Double,
    val date: Long,

    val latitude: Double,
    val longitude: Double
) {
    @Ignore
    var payments: List<UserPayment> = emptyList()
}
