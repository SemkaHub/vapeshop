package com.example.vapeshop.presentation.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.vapeshop.databinding.ItemOrderBinding
import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.domain.model.DeliveryMethod
import com.example.vapeshop.domain.model.Order
import com.example.vapeshop.domain.model.OrderStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrdersAdapter(
    val strings: OrdersAdapterStrings,
    val colors: OrdersStatusColors,
    val onItemClick: (List<CartItem>) -> Unit = {}
) :
    RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    private var orders: List<Order> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrdersViewHolder {
        val binding =
            ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrdersViewHolder(binding, strings)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    fun setList(orders: List<Order>) {
        val diffCallback = OrdersDiffCallback(this.orders, orders)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.orders = orders
        diffResult.dispatchUpdatesTo(this)
    }

    inner class OrdersViewHolder(
        private val binding: ItemOrderBinding,
        private val strings: OrdersAdapterStrings
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            if (order.status != null)
                setOrderStatus(order.status)
            with(binding) {
                orderIdTextView.text = String.format(strings.orderId, order.id)
                orderDateTextView.text =
                    String.format(strings.orderDate, getStringDate(order.timestamp))
                orderTotalPrice.text = String.format(strings.orderTotalPrice, order.totalPrice)
                orderDeliveryMethod.text = when (order.deliveryMethod) {
                    DeliveryMethod.COURIER -> strings.orderDeliveryCourier
                    DeliveryMethod.PICKUP -> strings.orderDeliveryPickup
                }

                root.setOnClickListener {
                    onItemClick(order.items)
                }
            }
        }

        private fun setOrderStatus(status: OrderStatus) {
            when (status) {
                OrderStatus.PENDING -> setPendingStatus()
                OrderStatus.PROCESSING -> setProcessingStatus()
                OrderStatus.SHIPPED -> setShippedStatus()
                OrderStatus.DELIVERED -> setDeliveredStatus()
                OrderStatus.CANCELLED -> setCancelledStatus()
            }
        }

        private fun setPendingStatus() {
            with(binding) {
                orderStatus.statusFrame.setBackgroundColor(colors.backgroundPending)
                orderStatus.statusTextView.setTextColor(colors.textPending)
                orderStatus.statusTextView.text = strings.orderStatus[OrderStatus.PENDING]
            }
        }

        private fun setProcessingStatus() {
            with(binding) {
                orderStatus.statusFrame.setBackgroundColor(colors.backgroundProcessing)
                orderStatus.statusTextView.setTextColor(colors.textProcessing)
                orderStatus.statusTextView.text = strings.orderStatus[OrderStatus.PROCESSING]
            }
        }

        private fun setShippedStatus() {
            with(binding) {
                orderStatus.statusFrame.setBackgroundColor(colors.backgroundShipped)
                orderStatus.statusTextView.setTextColor(colors.textShipped)
                orderStatus.statusTextView.text = strings.orderStatus[OrderStatus.SHIPPED]
            }
        }

        private fun setDeliveredStatus() {
            with(binding) {
                orderStatus.statusFrame.setBackgroundColor(colors.backgroundDelivered)
                orderStatus.statusTextView.setTextColor(colors.textDelivered)
                orderStatus.statusTextView.text = strings.orderStatus[OrderStatus.DELIVERED]
            }
        }

        private fun setCancelledStatus() {
            with(binding) {
                orderStatus.statusFrame.setBackgroundColor(colors.backgroundCancelled)
                orderStatus.statusTextView.setTextColor(colors.textCancelled)
                orderStatus.statusTextView.text = strings.orderStatus[OrderStatus.CANCELLED]
            }
        }

        private fun getStringDate(timestamp: Long): String {
            val date = Date(timestamp)
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            return dateFormat.format(date)
        }
    }
}