package cn.edu.pku.eecs.vca.applet.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cn.edu.pku.eecs.vca.applet.VisualChineseAnalyzer;
import cn.edu.pku.eecs.vca.util.IrreversibleOperation;
import cn.edu.pku.eecs.vca.util.Operation;






public class OperationPanel extends FrameworkPanel implements ActionListener, ListSelectionListener
{																			//操作记录面板
	public static VisualChineseAnalyzer program;
	public static DocumentPanel document;
	
	private static final long serialVersionUID = 7402042110042270491L;
	
	private AquaButton btnUndo, btnRedo;									//撤销，恢复按钮
	private JList lstOperation;												//操作列表
	private DefaultListModel dlmOperation;
	
	private int nLastIndex;													//上次选中项目
	
	public OperationPanel()
	{
		super("Operation", new JList(), 60);								//创建一个标题为Operation内容为JList的框架面版
		
		lstOperation = (JList) getMain();
		dlmOperation = new DefaultListModel();
		
		lstOperation.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	//只能选择一项
		lstOperation.setModel(dlmOperation);
		lstOperation.setFocusable(false);									//不可获得焦点
		
		lstOperation.addListSelectionListener(this);
		
		btnUndo = new AquaButton("Undo");									//添加按钮，默认均为禁用
		btnUndo.addActionListener(this);
		btnUndo.setEnabled(false);
		addTitleButton(btnUndo);
		
		btnRedo = new AquaButton("Redo");
		btnRedo.addActionListener(this);
		btnRedo.setEnabled(false);
		addTitleButton(btnRedo);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == btnUndo)
		{
			lstOperation.setSelectedIndex(nLastIndex - 1);					//撤销按钮，选中的操作编号减一
			return;
		}
		
		if (e.getSource() == btnRedo)
		{
			lstOperation.setSelectedIndex(nLastIndex + 1);					//撤销按钮，选中的操作编号加一
			return;
		}
	}
	
	public void addOperation(Operation operation)							//添加操作
	{
		if (operation instanceof IrreversibleOperation)	dlmOperation.clear();//创建新文档或者打开文档，清空列表
		
		for (int i = dlmOperation.size() - 1; i > nLastIndex; --i) dlmOperation.remove(i);//删掉选中条目以后的操作
		
		dlmOperation.addElement(operation);									//添加操作
		nLastIndex = dlmOperation.size() - 1;								//更新上次选中项目
		
		lstOperation.setSelectedValue(operation, true);						//选中最新项目
	}
	
	public void valueChanged(ListSelectionEvent e)							//选中项目发生改变
	{		
		if (lstOperation.isSelectionEmpty())								//什么也没选中
		{
			btnUndo.setEnabled(false);										//禁用两个按钮，返回
			btnRedo.setEnabled(false);
			return;
		}
		
		int nIndex, nSize;
		nIndex = lstOperation.getSelectedIndex();							//当前选中项目
		nSize = dlmOperation.size();										//项目总数
		
		btnUndo.setEnabled(nIndex > 0);										//不是第一个项目则启用撤销
		btnRedo.setEnabled(nIndex < nSize - 1);								//不是最后一个项目则启用恢复
		
		if (nIndex == nLastIndex) return;									//不需要撤销或恢复操作
		
		if (nIndex < nLastIndex)											//撤消倒数第二步操作，显示状态提示
		{
			((Operation) dlmOperation.get(nIndex)).redo();
			program.setStatus("Undo " + (nLastIndex - nIndex) + " operations.");
			document.refresh();
		}
		else																//回复最后一步操作，显示状态提示
		{
			((Operation) dlmOperation.get(nIndex)).redo();
			program.setStatus("Redo " + (nIndex - nLastIndex) + " operations.");
			document.refresh();
		}
		
		nLastIndex = nIndex;												//更新上次选中项目
	}
}