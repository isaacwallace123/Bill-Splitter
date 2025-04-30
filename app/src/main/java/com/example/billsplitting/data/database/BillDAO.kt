import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface BillDAO {
    // GET

    @Transaction
    @Query("SELECT * FROM billDatabase")
    suspend fun getAllBills(): List<Bill>

    @Query("SELECT * FROM billDatabase WHERE id = :billId")
    suspend fun getBillById(billId: Int): Bill?

    @Transaction
    @Query("SELECT * FROM billDatabase WHERE id = :id")
    suspend fun getBillWithPaymentsById(id: Int): Bill?

    @Query("SELECT * FROM userDatabase WHERE billId = :billId")
    suspend fun getPaymentsForBill(billId: Int): List<UserPayment>

    // PUT

    @Update
    suspend fun updatePayment(payment: UserPayment)

    // POST

    @Insert
    suspend fun insertBill(bill: Bill): Long

    @Insert
    suspend fun insertPayments(payments: List<UserPayment>)

    // DELETE

    @Delete
    suspend fun deleteBill(bill: Bill)

    @Query("DELETE FROM userDatabase WHERE billId = :billId")
    suspend fun deletePaymentsByBillId(billId: Int)

    @Transaction
    suspend fun deleteBillWithPayments(bill: Bill) {
        deleteBill(bill)
        deletePaymentsByBillId(bill.id)
    }
}
