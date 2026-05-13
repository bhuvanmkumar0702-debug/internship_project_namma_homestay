package com.example.namma_homestay

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PropertyAdapter(
    private val list: ArrayList<Property>
) : RecyclerView.Adapter<PropertyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageView)
        val title: TextView = view.findViewById(R.id.titleTv)
        val location: TextView = view.findViewById(R.id.locationTv)
        val capacity: TextView = view.findViewById(R.id.capacityTv)
        val amenities: TextView = view.findViewById(R.id.amenitiesTv)
        val price: TextView = view.findViewById(R.id.priceTv)
        val wishlistBtn: ImageButton = view.findViewById(R.id.wishlistBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_property, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val property = list[position]
        holder.title.text = property.name
        holder.location.text = property.location
        holder.capacity.text = "Per ${property.capacity} persons"
        holder.amenities.text = if (property.isAc) "A/C available" else "Non A/C"
        holder.price.text = "₹${property.price}"

        Glide.with(holder.itemView.context)
            .load(property.image)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PropertyDetailsActivity::class.java)
            intent.putExtra("NAME", property.name)
            intent.putExtra("LOCATION", property.location)
            intent.putExtra("PRICE", property.price)
            intent.putExtra("IMAGE", property.image)
            intent.putExtra("CAPACITY", property.capacity)
            intent.putExtra("IS_AC", property.isAc)
            holder.itemView.context.startActivity(intent)
        }

        checkWishlistStatus(property, holder.wishlistBtn)

        holder.wishlistBtn.setOnClickListener {
            toggleWishlist(property, holder.wishlistBtn)
        }
    }

    private fun checkWishlistStatus(property: Property, btn: ImageButton) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("wishlist")
            .document(user.uid)
            .collection("my_favorites")
            .whereEqualTo("name", property.name)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    btn.setImageResource(R.drawable.ic_favorite)
                    btn.setColorFilter(android.graphics.Color.RED)
                } else {
                    btn.setImageResource(R.drawable.ic_favorite)
                    btn.setColorFilter(android.graphics.Color.GRAY)
                }
            }
    }

    private fun toggleWishlist(property: Property, btn: ImageButton) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()
        val favRef = db.collection("wishlist")
            .document(user.uid)
            .collection("my_favorites")

        favRef.whereEqualTo("name", property.name)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    favRef.add(property).addOnSuccessListener {
                        btn.setColorFilter(android.graphics.Color.RED)
                        Toast.makeText(btn.context, "Added to Wishlist!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    for (doc in result) {
                        doc.reference.delete().addOnSuccessListener {
                            btn.setColorFilter(android.graphics.Color.GRAY)
                            Toast.makeText(btn.context, "Removed from Wishlist", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }

    private fun addToWishlist(property: Property, view: View) {
        // Method replaced by toggleWishlist
    }
}
