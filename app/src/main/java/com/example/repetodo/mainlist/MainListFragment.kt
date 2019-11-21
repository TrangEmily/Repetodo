package com.example.repetodo.mainlist

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repetodo.R
import com.example.repetodo.database.TaskDatabase
import com.example.repetodo.databinding.FragmentMainListBinding
import android.view.inputmethod.InputMethodManager
import android.content.Context;
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.repetodo.Utils.ItemActionListener
import com.example.repetodo.Utils.TasksFilterType
import com.google.android.material.snackbar.Snackbar

class MainListFragment : Fragment(), ItemActionListener {
    private lateinit var binding: FragmentMainListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewModel: MainListViewModel
    private lateinit var sharedPreferences: SharedPreferences
    val preferenceName = "FilterType"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_list, container, false)
        setHasOptionsMenu(true)

        // get the sharedPreference
        val currentFilter = getCurrentFilterOption()

        // Database & view model
        val application = requireNotNull(this.activity).application
        val dataSource = TaskDatabase.getInstance(application).taskListDao

        val viewModelFactory =
            MainListViewModelFactory(
                dataSource,
                application,
                currentFilter
            )
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainListViewModel::class.java)

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)


        // RecyclerView - layout manager & adapter
        viewManager = LinearLayoutManager(this.context)
        var viewAdapter = MainTaskRecyclerAdapter(this)
        binding.taskRecyclerView.adapter = viewAdapter

        recyclerView = binding.taskRecyclerView.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        // update myDataset of Adapter whenever the Live Data inside the View Model is updated
        viewModel.taskList.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewAdapter.myDataset = it
            }
        })

        // update the Filter Option
        viewModel.taskFilterStatus.observe(viewLifecycleOwner, Observer {
            val editor = sharedPreferences.edit()
            editor.putString(preferenceName, it.name)
            editor.commit()
        })

        // add a new task if the FAB is clicked
        binding.addFloatButton.setOnClickListener {
            viewModel.addNewTask("")
        }

        // swipe to delete task
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder) : Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                viewAdapter.removeItem(viewHolder.adapterPosition)
            }
        }

        var itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)


        return binding.root
    }

    private fun getCurrentFilterOption() : TasksFilterType {
        sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)!!
        val currentFilterString = sharedPreferences.getString(preferenceName, null)
        return when (currentFilterString) {
            TasksFilterType.COMPLETED_TASKS.name -> TasksFilterType.COMPLETED_TASKS
            TasksFilterType.ALL_TASKS.name -> TasksFilterType.ALL_TASKS
            else -> TasksFilterType.ACTIVE_TASKS
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.filter_tasks -> {
            showFilteringPopUpMenu()
            true
        }

        else -> {
            NavigationUI.onNavDestinationSelected(item,
            view!!.findNavController())
                ||super.onOptionsItemSelected(item)
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.filter_tasks) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_task_menu, menu)

            setOnMenuItemClickListener {
                lateinit var selectedOption: TasksFilterType
                binding.viewModel?.run {

                    selectedOption =
                        when (it.itemId) {
                            R.id.active -> TasksFilterType.ACTIVE_TASKS
                            R.id.completed -> TasksFilterType.COMPLETED_TASKS
                            else -> TasksFilterType.ALL_TASKS
                        }

                    // update the data inside viewModel
                    changeFilterSetting(selectedOption)
                }
                true
            }
            show()
        }
    }


    override fun onItemDelete(id: Long) {
        viewModel.deleteTask(id)
        // show the informed text and Undo
        val snackbar = Snackbar.make(view!!, R.string.removeInformText, Snackbar.LENGTH_SHORT)
        snackbar.view.setBackgroundColor(Color.parseColor("#008200"))
        snackbar.show()
    }

    override fun onItemUpdate(id: Long, title: String) {
        viewModel.updateTask(id, title)
        hideSoftKeyboard(activity!!, view!!)
    }

    override fun onItemCheckUpdate(id: Long, checked: Boolean) {
        viewModel.updateTaskStatus(id, checked)
    }
}