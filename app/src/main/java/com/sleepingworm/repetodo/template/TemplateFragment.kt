package com.sleepingworm.repetodo.template

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sleepingworm.repetodo.R
import com.sleepingworm.repetodo.Utils.ItemActionListener
import com.sleepingworm.repetodo.Utils.SwipeToDeleteCallback
import com.sleepingworm.repetodo.database.TaskDatabase
import com.sleepingworm.repetodo.databinding.FragmentTemplateBinding

class TemplateFragment : Fragment(), ItemActionListener {
    private lateinit var binding: FragmentTemplateBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewModel: TemplateViewModel


    override var updatingPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_template, container, false)
        setHasOptionsMenu(true)

        var args = TemplateFragmentArgs.fromBundle(arguments!!)
        Log.i("TemplateFragment", "xin chao ${args.fragmentId}")

        // Database & view model
        val application = requireNotNull(this.activity).application
        val dataSource = TaskDatabase.getInstance(application).templateDao
        val viewModelFactory =
            TemplateViewModelFactory(
                dataSource,
                application,
                args.fragmentId
            )

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TemplateViewModel::class.java)

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)


        binding.templateNameText.text = args.fragmentName

        viewManager = LinearLayoutManager(this.context)
        var viewAdapter = TemplateRecyclerAdapter(this)
        binding.templateItemRecyclerView.adapter = viewAdapter

        // update data set inside the adapter
        viewModel.templateTaskList.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewAdapter.myDataset = it
            }
        })

        recyclerView = binding.templateItemRecyclerView.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }

        // swipe to delete task
        val swipeHandler = object : SwipeToDeleteCallback(context!!) {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                if (viewHolder.adapterPosition == updatingPosition) return 0
                else return super.getMovementFlags(recyclerView, viewHolder)
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as TemplateRecyclerAdapter
                adapter.removeItem(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // add a new task
        binding.addFloatButtonTpl.setOnClickListener{
            viewModel.addNewTask("")
        }

        // if Done button is pressed, clear focus of RecyclerView will activate the "updateItem" of adapter, then hide Done button & show Add button
        binding.doneBtnTpl.setOnClickListener {
            binding.templateItemRecyclerView.clearFocus()
            changeAddButtonVisibility(false)
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.template_over_flow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            view!!.findNavController())
                || super.onOptionsItemSelected(item)
    }

    override fun onItemDelete(id: Long) {
        viewModel.deleteItem(id)
    }

    override fun onItemUpdate(id: Long, title: String) {
        viewModel.updateItem(id, title)
        hideSoftKeyboard(activity!!, view!!)
    }

    override fun changeAddButtonVisibility(hideAddButton: Boolean) {
        if (hideAddButton) {
            binding.addFloatButtonTpl.hide()
            binding.doneBtnTpl.visibility = View.VISIBLE
        } else {
            binding.addFloatButtonTpl.show()
            binding.doneBtnTpl.visibility = View.INVISIBLE
        }
    }


}