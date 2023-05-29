package com.example.prayerstimesaap.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.example.prayerstimesaap.databinding.PrayersItemBinding
import com.example.prayerstimesaap.prayer.Data
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)

class PrayersAdapter : BaseAdapter<Data>() {

    inner class NewsViewHolder (private val binding :PrayersItemBinding):GenericViewHolder<Data>(binding.root) {
        override fun onBind(item: Data) {
            binding.apply {
                fajrTimer.text = formatTime(item.timings.Fajr)
                dhuhrTimer.text = formatTime(item.timings.Dhuhr)
                asrTimer.text =formatTime( item.timings.Asr)
                maghribTimer.text =formatTime( item.timings.Maghrib)
                ishaTimer.text = formatTime(item.timings.Isha)
                dayDateLayout.dateOfDay.text=item.date.gregorian.date
                dayDateLayout.dayTitle.text=item.date.gregorian.weekday.en

            }
        }
    }

    private val differenceCallBack=object : DiffUtil.ItemCallback<Data>(){
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
           return oldItem.date==newItem.date
        }
        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem==newItem
        }
    }
    val difference= AsyncListDiffer(this,differenceCallBack)

    override fun setViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Data> {
        return NewsViewHolder(PrayersItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun setItem(): MutableList<Data> {
       return difference.currentList
    }

    private fun formatTime(time: String?): String {
        time?.let {
            val localTime = LocalTime.parse(it.replaceFirst(" \\(.*\\)".toRegex(), ""))
            return localTime.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH))
        }
        return ""
    }

}