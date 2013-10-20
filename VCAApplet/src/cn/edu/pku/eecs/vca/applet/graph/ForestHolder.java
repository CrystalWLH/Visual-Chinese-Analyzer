package cn.edu.pku.eecs.vca.applet.graph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;

import cn.edu.pku.eecs.vca.core.DependencyTree;
import cn.edu.pku.eecs.vca.core.Forest;
import cn.edu.pku.eecs.vca.util.Operation;






public class ForestHolder extends ContainerHolder			//森林容器
{
	private static final long serialVersionUID = 3549603018400394291L;
	
	private Forest forest;
	
	private List<TreeHolder> lstTreeHolder = new ArrayList<TreeHolder>();
	
	public ForestHolder(Forest forest)
	{
		this.forest = forest;
		
		setBackground(Color.white);							//白色背景
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		if (forest == null) return;
		
		for (DependencyTree tree : forest.getTrees())		//一个新建一个已有树容器相间排布
		{
			lstTreeHolder.add(new TreeHolder(this, null));
			lstTreeHolder.add(new TreeHolder(this, tree));
		}
		lstTreeHolder.add(new TreeHolder(this, null));
		
		rebuild();											//重构
	}
	
	@Override
	public void disableHolder()								//禁用
	{
		setEnabled(false);
		for (TreeHolder treeHolder : lstTreeHolder) treeHolder.disableHolder();
	}
	
	@Override
	public void enableHolder()								//启用
	{
		setEnabled(true);
		for (TreeHolder treeHolder : lstTreeHolder) treeHolder.enableHolder();
	}

	@Override
	public void insert(JComponent com)						//插入，将待新建的空树转化为有一个根结点的树
	{
		TreeHolder treeHolder = (TreeHolder)com;
		int nIndex = lstTreeHolder.indexOf(treeHolder);
				
		DependencyTree tree = new DependencyTree();
		forest.insert(nIndex / 2, tree);
		treeHolder = new TreeHolder(this, tree);
		
		lstTreeHolder.set(nIndex, treeHolder);
		lstTreeHolder.add(nIndex + 1, new TreeHolder(this, null));
		lstTreeHolder.add(nIndex, new TreeHolder(this, null));
		
		document.addOperation(new Operation("Create New tree"));//添加操作
		
		program.setStatus("Create new tree.");				//显示提示
		
		rebuild();											//重构
		graph.refresh();									//刷新
		graph.setCurrentHolder(treeHolder);					//设置选中对象
	}

	@Override
	public void rebuild()									//重构
	{
		removeAll();										//删除所有树容器重新加入
		for (TreeHolder treeHolder : lstTreeHolder) add(treeHolder);
	}
	
	@Override
	public void refresh()									//刷新
	{		
		for (TreeHolder treeHolder : lstTreeHolder) treeHolder.refresh();
		
		validate();
		repaint();
	}

	@Override
	public void remove(JComponent com)						//删除
	{
		TreeHolder treeHolder = (TreeHolder) com;
		DependencyTree tree = treeHolder.getTree();
				
		int nIndex = lstTreeHolder.indexOf(treeHolder);
		
		forest.remove(tree);
		
		lstTreeHolder.remove(nIndex + 1);					//删除树及其后的空树
		lstTreeHolder.remove(nIndex);
		
		document.addOperation(new Operation("Delete Tree: " + tree.toString()));//添加操作
		
		program.setStatus("Delete tree " + tree.toString() + ".");//显示提示
		
		rebuild();											//重构
		graph.refresh();									//刷新
		graph.setCurrentHolder(null);						//设置无选中对象
	}
}
