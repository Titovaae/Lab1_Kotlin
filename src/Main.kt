enum class Category(val displayName: String) {
    FOOD("Еда"),
    ELECTRONICS("Электроника"),
    BOOKS("Книги"),
    OTHER("Другое")
}

sealed class OrderStatus {
    data object Created : OrderStatus()

    data class Paid(val transactionId: String) : OrderStatus()

    data class Cancelled(val reason: String) : OrderStatus()

    data object Delivered : OrderStatus()

    val displayName: String
        get() = when (this) {
            is Created -> "Создан"
            is Paid -> "Оплачен, транзакция: $transactionId"
            is Cancelled -> "Отменён, причина: $reason"
            is Delivered -> "Доставлен"
        }
}

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: Category
)

data class OrderItem(
    val product: Product,
    val count: Int
) {
    val totalPrice: Double
        get() = product.price * count
}

data class Customer(
    val name: String,
    val email: String,
    val discount: Double = 0.0
)

data class Order(
    val id: Int,
    val customer: Customer,
    val items: List<OrderItem>,
    val status: OrderStatus
) {
    val subtotal: Double
        get() = items.sumOf { it.totalPrice }

    val discountAmount: Double
        get() = subtotal * customer.discount

    val total: Double
        get() = subtotal - discountAmount
}

fun List<Order>.toReceipt(): String = buildString {
    this@toReceipt.forEachIndexed { index, order ->
        if (index > 0) appendLine()

        appendLine("Заказ #${order.id}")
        appendLine("Покупатель: ${order.customer.name} <${order.customer.email}>")
        appendLine("Статус: ${order.status.displayName}")
        appendLine()

        order.items.forEach { item ->
            val shortName = when (item.product.category) {
                Category.BOOKS -> "Книга ${item.product.name}"
                Category.FOOD -> "Кофе"
                Category.ELECTRONICS -> "Наушники"
                Category.OTHER -> item.product.name
            }
            appendLine("$shortName × ${item.count} = ${"%.2f".format(item.totalPrice)}")
        }

        appendLine()

        if (order.customer.discount > 0) {
            val discountPercent = (order.customer.discount * 100).toInt()
            appendLine("Скидка: $discountPercent%")
        }

        appendLine("Итого: ${"%.2f".format(order.total)}")
    }
}

fun main() {
    val kotlinBook = Product(
        id = 1,
        name = "Kotlin in Action",
        price = 1_500.0,
        category = Category.BOOKS,
    )

    val coffee = Product(
        id = 2,
        name = "Coffee",
        price = 500.0,
        category = Category.FOOD,
    )

    val headphones = Product(
        id = 3,
        name = "Headphones",
        price = 8_000.0,
        category = Category.ELECTRONICS,
    )

    val customer = Customer(
        name = "Иван",
        email = "ivan@example.com",
        discount = 0.10,
    )

    val orders = listOf(
        Order(
            id = 1,
            customer = customer,
            items = listOf(
                OrderItem(kotlinBook, count = 2),
                OrderItem(coffee, count = 1),
            ),
            status = OrderStatus.Paid(
                transactionId = "TX-123",
            ),
        ),
    )

    println(orders.toReceipt())
}