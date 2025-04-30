import androidx.room.*

@Entity(tableName = "userDatabase")
data class UserPayment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val billId: Int,

    val name: String,
    val amount: Double,
    var isPaid: Boolean,
)