package io.userfeeds.widget

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.userfeeds.sdk.core.ranking.RankingItem

internal class LinksRecyclerAdapter(private val items: List<RankingItem>, private val listener: Listener) : RecyclerView.Adapter<LinksRecyclerAdapter.Holder>() {

    interface Listener {

        fun onLinkClick(item: RankingItem)
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.userfeeds_link_view_full, parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        val titleView = holder.itemView.findViewById(R.id.userfeeds_link_title) as TextView
        titleView.text = item.title
        val summaryView = holder.itemView.findViewById(R.id.userfeeds_link_summary) as TextView
        summaryView.text = item.summary
        val urlView = holder.itemView.findViewById(R.id.userfeeds_link_url) as TextView
        urlView.text = item.target
        holder.itemView.setOnClickListener {
            listener.onLinkClick(item)
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
