package com.example.prayerstimesaap.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.example.prayerstimesaap.databinding.PrayersItemBinding
import com.example.prayerstimesaap.prayer.Data
import com.example.prayerstimesaap.ui.fragments.prayersfrag.PrayersFragment
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)

class PrayersAdapter(private val context: PrayersFragment): BaseAdapter<Data>() {

     var prayersResponse: Data? = null
     var prayerTimings: List<LocalTime>? = null

    inner class NewsViewHolder (private val binding :PrayersItemBinding):GenericViewHolder<Data>(binding.root) {
        override fun onBind(item: Data) {
            binding.apply {
                fajrTimer.text = formatTime(item.timings.Fajr)
                dhuhrTimer.text = formatTime(item.timings.Dhuhr)
                asrTimer.text =formatTime( item.timings.Asr)
                maghribTimer.text =formatTime( item.timings.Maghrib)
                ishaTimer.text = formatTime(item.timings.Isha)

                prayersResponse = item
                prayerTimings = prayersResponse?.toLocalTimeList()
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