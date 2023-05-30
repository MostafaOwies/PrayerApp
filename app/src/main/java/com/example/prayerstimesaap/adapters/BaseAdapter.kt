package com.example.prayerstimesaap.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


abstract class BaseAdapter<T>:RecyclerView.Adapter<BaseAdapter.GenericViewHolder<T>>() {
   abstract class GenericViewHolder<T>( itemView :View):RecyclerView.ViewHolder(itemView),
       BindViewHolder<T> {
       abstract override fun onBind(item:T)
   }

    abstract fun setItem():MutableList<T>

    abstract fun setViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<T>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<T> {
        return setViewHolder(parent,viewType)
    }

    override fun onBindViewHolder(holder: GenericViewHolder<T>, position: Int) {
       // val article=difference.currentList[position]
        holder.onBind(setItem()[position])
    }

    override fun getItemCount(): Int {
        return setItem().size
    }
}