package com.example.repetodo.templateinsert

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.repetodo.R
import android.view.KeyEvent
import com.example.repetodo.Utils.ItemActionListener
import com.example.repetodo.database.Template
import kotlinx.android.synthetic.main.fragment_template_list_insert_item.view.*

class TemplateInsertRecyclerAdapter(private var itemActionListener: ItemActionListener):
    RecyclerView.Adapter<TemplateInsertRecyclerAdapter.MyViewHolder>() {
    var myDataset = listOf<TemplateInsert>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_template_list_insert_item, parent, false) as View
        // set the view's size, margins, paddings and layout parameters

        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var id = myDataset[position].templateId
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.view.templateInsertTitle.text = myDataset[position].templateTitle
        holder.view.templateInsertInfo.text = myDataset[position].templateBrief

//        holder.view.templateInsertTitle.apply {
//            // show the task title
//            setText(myDataset[position].templateTitle)
//            // focus cursor to the end of task title
//            setSelection(myDataset[position].templateTitle.length)
//        }

//        holder.view.templateInsertTitle.setOnFocusChangeListener { _, hasFocus ->
//            if (!hasFocus) {
//                var newTitle = holder.view.templateItemTitle.text.toString()
//                itemActionListener.onItemUpdate(id, newTitle)
//            }
//        }
//
//        holder.view.templateItemTitle.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
//            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
//                Log.i("TemplateRecyclerAdapter", "Heard an Enter Key")
//                var newTitle = holder.view.templateItemTitle.text.toString()
//                itemActionListener.onItemUpdate(id, newTitle)
//                //Perform Code
//                itemActionListener.hideSoftKeyboard()
//            }
//            false
//        })
    }

    fun getIdFromPosition(position: Int): Long {
        return myDataset[position].templateId
    }
    fun removeItem(position: Int) {
        itemActionListener.onItemDelete(myDataset[position].templateId)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}