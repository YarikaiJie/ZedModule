package com.module.kotlin.dialog

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.module.kotlin.R
import com.module.kotlin.databinding.DialogCheckListBinding
import com.module.kotlin.databinding.ItemChooseListBinding
import com.module.kotlin.recyclerview.BindRcvAdapter
import com.module.kotlin.recyclerview.BindViewHolder
import com.module.kotlin.utils.addDividingWithLinearLayoutManager
import com.module.kotlin.utils.dp
import com.module.kotlin.utils.getColorRes
import com.module.kotlin.utils.onClick

/**
 * 单项选择弹窗：参照 CenterDialog 实现
 * - 中间内容为可勾选列表（RecyclerView），右侧显示主题色勾选图标
 * - 底部保留取消和确认按钮
 * - 支持传入初始选中值 index
 */
open class CheckListDialog : BaseDialog<DialogCheckListBinding>() {
    companion object {
        /**
         * 展示单选列表弹窗
         * @param manager FragmentManager
         * @param title 标题
         * @param items 字符串列表
         * @param selectedValue 选中值
         * @param cancelAble 是否可点空白取消
         * @param sureText 确认按钮文本，可空
         * @param cancelClick 取消回调
         * @param onSelectedConfirm 确认回调 (index, value)
         */
        fun show(
            manager: FragmentManager,
            title: String?,
            items: List<String>,
            selectedValue: String? =null,
            limitHeight: Int? =null,
            cancelAble: Boolean = true,
            sureText: String? = null,
            iconRes: Int = R.mipmap.ic_selected_primary,
            cancelClick: ((CheckListDialog) -> Unit)? = null,
            onSelectedConfirm: ((value: String?) -> Unit)?
        ): CheckListDialog {
            val dialog = CheckListDialog().apply {
                // 调整对话框位置与边距
                onDialogViewLayoutParamBlock = { _, newLp ->
                    newLp.gravity = Gravity.CENTER
                    limitHeight?.let { newLp.height = it.dp }
                    newLp.marginStart = 26.dp
                    newLp.marginEnd = 26.dp
                }
                // 初始化内容
                onShownBlock = {
                    val d = it as CheckListDialog
                    d.isCancelable = cancelAble
                    // 标题
                    d.mBinding.tvTitle.text = title
                    // 列表
                    d.mBinding.rcvList.layoutManager = LinearLayoutManager(d.requireContext())
                    val adapter = SelectListAdapter(selectAction = { _, _ -> },iconRes).apply {
                        submitList(items, true)
                        setSelectedValue(selectedValue)
                    }
                    d.mBinding.rcvList.addDividingWithLinearLayoutManager(1.dp, getColorRes(R.color.color_des))
                    d.mBinding.rcvList.adapter = adapter
                    // 底部按钮
                    sureText?.run { d.mBinding.btnSure.text = this }
                    d.mBinding.btnSure.onClick(null) {
                        onSelectedConfirm?.invoke(adapter.getSelectedValue())
                        d.dismiss()
                    }
                    d.mBinding.btnCancel.onClick {
                        d.dismiss()
                        cancelClick?.invoke(d)
                    }
                }
            }
            dialog.show(manager, "CheckListDialog")
            return dialog
        }
    }

    /**
     * 列表适配器（单一 ViewHolder，无类型判断）
     * 左侧展示字符串，右侧选中显示主题色勾图标
     */
    class SelectListAdapter(val selectAction: (String?, Int) -> Unit,val iconRes: Int = R.mipmap.ic_selected_primary): BindRcvAdapter<String, SelectListAdapter.SelectHolder>() {
        // 以“值”为准记录选中项，避免仅依赖下标导致的错选
        private var selectedIndex: Int = -1
        private var selectedValue: String? = null

        fun setSelectedValue(value: String?) {
            value?.let {
                val index = datas.indexOf(it)
                selectedIndex = index
                notifyDataSetChanged()
                selectedValue = it
            }
        }

        fun getSelectedIndex(): Int {
            return if (selectedValue == null) -1 else datas.indexOf(selectedValue!!)
        }

        fun getSelectedValue(): String? = selectedValue

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): SelectHolder {
            return SelectHolder(selectAction, iconRes, create(parent))
        }

        class SelectHolder(
            selectAction: (String?, Int) -> Unit,
            val iconRes: Int,
            binding: ItemChooseListBinding
        ) :
            BindViewHolder<String, ItemChooseListBinding>(binding) {
            init {
                // 点击事件只绑定一次，避免bindData重复绑定导致多次触发
                setHolderClickWithAdapterPosition { data, pos ->
                    val adapter = (bindingAdapter as SelectListAdapter)
                    val oldIndex = adapter.getSelectedIndex()
                    // 以“值”为准更新选中项
                    adapter.setSelectedValue(data)
                    val newIndex = adapter.getSelectedIndex()
                    if (oldIndex >= 0) adapter.notifyItemChanged(oldIndex)
                    if (newIndex >= 0) adapter.notifyItemChanged(newIndex)
                    // 向外部回传选择的值和新的下标
                    selectAction.invoke(data, newIndex)
                }
            }
            override fun bindData(bean: String) {
                super.bindData(bean)
                binding.tvCommonContent.text = bean
                // 以“值”比较是否选中，避免因为排序变化导致的错位
                val isSelected = (bean == (bindingAdapter as SelectListAdapter).getSelectedValue())
                if (isSelected) {
                    binding.ivChooseState.visibility = View.VISIBLE
                    binding.ivChooseState.setImageResource(iconRes)
                } else {
                    binding.ivChooseState.visibility = View.GONE
                }
            }
        }
    }
}
