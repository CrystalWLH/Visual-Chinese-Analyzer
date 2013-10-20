package edu.pku.java.g3l;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class TreeHolder extends ContainerHolder implements HasContainerHolder	//依存树容器
{
	private static final long serialVersionUID = 1040348780038724596L;
	private static int BLANK_ARCS = 25;											//给弧线上留空
	
	private boolean bHover;														//悬停与否

	private DependencyTree tree;												//依存树
	private ContainerHolder container;											//父容器
	private JPanel panNode, panCenter;											//结点面板，中心模板
	private ArcPanel panArc;													//弧线面板
	
	private List<NodeHolder> lstNodeHolder = new ArrayList<NodeHolder>();		//结点列表
	private List<ArcHolder> lstArcHolder = new ArrayList<ArcHolder>();			//弧线列表
	
	private Map<Node, NodeHolder> mapNode = new HashMap<Node, NodeHolder>();	//结点到对应容器的映射
	
	public TreeHolder(ContainerHolder container, DependencyTree tree)			//构造函数，参数为父容器和依存树
	{	
		this.container = container;
		setBackground(Color.white);												//背景色白
		setLayout(new BorderLayout());
		
		addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				graph.setCurrentHolder(TreeHolder.this);						//获得焦点后设置当前处理对象
				refresh();
			}
		});
		
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				bHover = true;													//悬停
				refresh();
			}
			
			@Override
			public void mouseExited(MouseEvent e)
			{
				bHover = false;													//移出
				refresh();
			}
		});
		
		this.tree = tree;
		if (tree == null)
		{
			setPreferredSize(new Dimension(0, 10));								//无树
		}
		else
		{
			panNode = new JPanel();
			panNode.setBackground(Color.white);									//结点面板
			
			panArc = new ArcPanel(this);										//弧线面板
			
			JPanel panSouth;
			
			panSouth = new JPanel(new BorderLayout());
			panSouth.setBackground(Color.white);
			add(panSouth, BorderLayout.SOUTH);
			
			panCenter = new JPanel(new BorderLayout());
			panCenter.setBackground(Color.white);
			add(panCenter, BorderLayout.CENTER);
			
			panCenter.add(panArc, BorderLayout.WEST);			
			panSouth.add(panNode, BorderLayout.WEST);
			
			panNode.setLayout(new BoxLayout(panNode, BoxLayout.X_AXIS));		//将其布置到指定位置

			for (Node node : tree.getNodes())
			{
				lstNodeHolder.add(new NodeHolder(this, null));						//空结点以便插入
				NodeHolder nodeHolder = new NodeHolder(this, node);					//结点容器
				
				lstNodeHolder.add(nodeHolder);										//添加结点容器
				mapNode.put(node, nodeHolder);										//添加结点-容器映射
			}
			
			rebuild();	
		}
	}
	
	@Override
	public void disableHolder()													//禁用
	{
		setEnabled(false);
		if (tree == null) return;
		
		for (NodeHolder nodeHolder : lstNodeHolder) nodeHolder.setEnabled(false);//禁用结点
		panArc.setEnabled(false);												//禁用弧线
	}
	
	@Override
	public void enableHolder()													//启用
	{
		setEnabled(true);
		if (tree == null) return;
		
		for (NodeHolder nodeHolder : lstNodeHolder) nodeHolder.setEnabled(true);//启用结点
		panArc.setEnabled(true);												//启用弧线
	}
	
	public List<ArcHolder> getArcHoders()										//返回弧线列表
	{
		return lstArcHolder;
	}
	
	@Override
	public ContainerHolder getContainerHolder()									//获取父容器
	{
		return container;
	}
	
	@Override
	public Node getNode()														//什么也不做
	{
		return null;
	}
	
	public int getPanArcHeight()												//获取弧线列表高度
	{
		return panArc.getHeight();
	}
	
	public DependencyTree getTree()												//获取树
	{
		return tree;
	}

	@Override
	public void insert(JComponent com)											//将空的新建结点转换成新结点并插入树中
	{
		NodeHolder nodeHolder = (NodeHolder) com;
		int nIndex = lstNodeHolder.indexOf(nodeHolder);							//获取编号
		nodeHolder.removeFocusListener(nodeHolder.focusAdapter);				//去掉监听
		
		String szText = nodeHolder.getText();
		
		Operation operation = new Operation("Create Node: " + szText, document.getForest());//创建插入操作
		
		Node node = new Node(szText, null, null);
		tree.insert(nIndex / 2, node);											//插入结点
		nodeHolder = new NodeHolder(this, node);
		
		mapNode.put(node, nodeHolder);
		lstArcHolder.add(nIndex / 2, new ArcHolder(this, nodeHolder, null));	//添加弧
		
		lstNodeHolder.set(nIndex, nodeHolder);									//替换结点容器
		lstNodeHolder.add(nIndex + 1, new NodeHolder(this, null));				//两边插入两个空结点容器
		lstNodeHolder.add(nIndex, new NodeHolder(this, null));
		
		operation.setNewForest(document.getForest());
		document.addOperation(operation);										//加入操作列表
		
		program.setStatus("Create new node " + szText + ".");					//显示提示
		
		rebuild();
		graph.refresh();
		graph.setCurrentHolder(this);											//设置当前选中容器
	}
	
	@Override
	public void paintComponent(Graphics g)										//重绘
	{
		if (this == graph.getCurrentHolder())
		{
			setBorder(BorderFactory.createLineBorder(Color.darkGray));			//选中状态边界
		}
		else
		{
			if (bHover)
			{
				setBorder(BorderFactory.createLineBorder(Color.lightGray));		//悬停状态边界
			}
			else
			{
				setBorder(BorderFactory.createLineBorder(Color.white));			//空白边界
			}
		}
		
		super.paintComponent(g);
	}
	
	@Override
	public void rebuild()														//刷新
	{
		panNode.removeAll();													//删除所有结点容器
		for (NodeHolder nodeHolder : lstNodeHolder) panNode.add(nodeHolder);	//添加结点容器
		
		lstArcHolder.clear();													//清除弧线列表
		for (Node node : tree.getNodes()) lstArcHolder.add(new ArcHolder(this, mapNode.get(node), mapNode.get(node.getParent())));
	}
	
	@Override
	public void refresh()
	{
		if (tree == null) return;
		
		int nMaxRadius = 0;
		for (ArcHolder arcHolder : lstArcHolder)
		{
			if (arcHolder.getRadius() > nMaxRadius) nMaxRadius = arcHolder.getRadius();//更新最大半圆半径
		}
		
		nMaxRadius += BLANK_ARCS;												//预留一定高度
		
		panArc = new ArcPanel(this);
		panArc.setPreferredSize(new Dimension(getWidth(), nMaxRadius));			//设置大小
		
		panCenter.removeAll();
		panCenter.add(panArc, BorderLayout.WEST);
		
		getContainerHolder().validate();
		
		for (NodeHolder nodeHolder : lstNodeHolder)
		{
			nodeHolder.validate();
			nodeHolder.repaint();
		}
		
		validate();
		repaint();
	}
	
	@Override
	public void remove(JComponent com)											//删除结点
	{
		NodeHolder nodeHolder = (NodeHolder) com;
		Node node = nodeHolder.getNode();
		
		for (int i = 1; i < lstNodeHolder.size(); i += 2)						//检查是否有结点依存要删除节点
		{
			NodeHolder otherHolder = lstNodeHolder.get(i);
			Node otherNode = otherHolder.getNode();
			if (otherNode == null) continue;
			if (node == otherNode.getParent())
			{
				program.setStatus("Only leaf nodes can be deleted.");			//只有叶节点可被删除
				nodeHolder.restore();
				return;
			}
		}
		
		int nIndex = lstNodeHolder.indexOf(nodeHolder);
		String szText = node.getWord();
		Operation operation = new Operation("Delete Node: " + szText, document.getForest());//创建删除操作
		
		tree.remove(node);														//删除结点
		nodeHolder.removeFocusListener(nodeHolder.focusAdapter);				//去掉监听
		
		mapNode.remove(node);													//去除映射
		lstArcHolder.remove(nIndex / 2);										//删掉弧
		
		lstNodeHolder.remove(nIndex + 1);										//删除结点容器及之后的空结点容器
		lstNodeHolder.remove(nIndex);
		
		operation.setNewForest(document.getForest());
		document.addOperation(operation);
		
		program.setStatus("Delete node " + szText + ".");
		
		rebuild();
		graph.refresh();
		graph.setCurrentHolder(this);
	}
	
	public void setHover(boolean bHover)										//设置悬停
	{
		this.bHover = bHover;
		repaint();
	}
}
