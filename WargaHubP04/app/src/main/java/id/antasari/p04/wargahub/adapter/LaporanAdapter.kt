package id.antasari.p04.wargahub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.antasari.p04.wargahub.R
import id.antasari.p04.wargahub.data.Laporan
import id.antasari.p04.wargahub.databinding.ItemLaporanBinding

class LaporanAdapter(
    private val onItemClick: (Laporan) -> Unit
) : ListAdapter<Laporan, LaporanAdapter.LaporanHolder>(Pembanding) {

    class LaporanHolder(
        private val binding: ItemLaporanBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Laporan, onClick: (Laporan) -> Unit) {
            val context = binding.root.context
            binding.tvJudul.text = item.judul
            binding.tvKategori.text = item.kategori
            binding.tvStatus.text = context.getString(
                R.string.row_status, item.status
            )
            binding.root.contentDescription = context.getString(
                R.string.row_description,
                item.judul, item.kategori, item.status
            )
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): LaporanHolder {
        val binding = ItemLaporanBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LaporanHolder(binding)
    }

    override fun onBindViewHolder(holder: LaporanHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    companion object {
        private val Pembanding = object : DiffUtil.ItemCallback<Laporan>() {
            override fun areItemsTheSame(a: Laporan, b: Laporan) =
                a.id == b.id
            override fun areContentsTheSame(a: Laporan, b: Laporan) =
                a == b
        }
    }
}
