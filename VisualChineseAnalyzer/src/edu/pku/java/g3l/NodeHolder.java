package edu.pku.java.g3l;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.border.Border;

public class NodeHolder extends JTextArea implements HasContainerHolder
{
	public static VisualChineseAnalyzer program;				//程序
	public static DocumentPanel document;						//文档
	public static GraphPanel graph;								//图像
	
	private static final long serialVersionUID = -1482302639822321800L;
	
	private static final Color clrTransparent = new Color(0, 0, 0, 0);//未选中或悬停时透明背景
	private static final Color clrBackground = new Color(227, 236, 244);//选中或悬停时淡蓝背景
	private static final Color clrBorder = new Color(187, 207, 229);//选中或悬停时蓝色边框颜色
	
	private static final Border bdrFocused = BorderFactory.createLineBorder(clrBorder);//选中或悬停时边框
	private static final Border bdrNotFocused = BorderFactory.createLineBorder(clrTransparent);//其他时刻无色边框
	
	public FocusAdapter focusAdapter;							//焦点监听，当结点被删除时须事先移除
	private boolean bHover;										//鼠标是否悬停
	
	private TreeHolder treeHolder;								//父容器
	private Node node;											//结点
	
	public NodeHolder(ContainerHolder container, Node node)		//构造函数
	{
		setBorder(bdrNotFocused);								//初始状态无边框
		setFocusable(true);
		setFont(new Font("system", Font.PLAIN, 13));			//设置字体
		
		setColumns(0);											//最窄
		setRows(1);												//仅一行
		
		bHover = false;											//未悬停
		focusAdapter = new FocusAdapter()						//焦点监视
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				graph.setCurrentHolder(NodeHolder.this);		//获得焦点，图像当前为结点容器
				repaint();
			}
			
			@Override
			public void focusLost(FocusEvent e)
			{
				confirm();										//失去焦点时确认输入
				repaint();
			}
		};
		
		addFocusListener(focusAdapter);
		
		addKeyListener(new KeyAdapter()							//键盘监听
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				switch (e.getKeyCode())							//输入回车或换行符
				{
				case 10:confirm();								//换行符确认(由于使用换行符确认，本程序无法在MAC下键入Enter确认)
				case 13:e.consume();							//禁止在文本中添加换行或回车符
				}
			}
		});
		
		addMouseListener(new MouseAdapter()						//鼠标监听
		{
			@Override
			public void mouseEntered(MouseEvent e)				//移入
			{
				bHover = true;									//悬停
				repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent e)				//移出
			{
				bHover = false;									//不悬停
				repaint();
			}
		});
		
		treeHolder = (TreeHolder) container;					//树容器
		this.node = node;
		
		if (node == null)										//设置文本
		{
			setText("");
		}
		else
		{
			restore();
		}
		
		setEditable(!(node instanceof RootNode));				//根结点不可编辑
	}
	
	public void confirm()										//确认
	{
		String szText = getText();
		setText(szText);										//更新下文本，去除多余空格（左右各留一个）
		
		if (node == null)										//原结点为空
		{
			if (szText.equals(""))								//无内容
			{
				program.setStatus("Please input the text of this new node.");
			}
			else
			{
				getContainerHolder().insert(this);				//插入新结点
			}
			return;
		}
		
		if (szText.equals(node.getWord())) return;				//文本与原来结点单词相同，直接返回
		
		if (!szText.equals(""))									//有文本
		{
			Operation operation = new Operation("Change Node Text: " + szText, document.getForest());
			node.setWord(szText);								//进行文本更新操作
			
			operation.setNewForest(document.getForest());
			document.addOperation(operation);
			
			program.setStatus("Change node text " + szText + ".");
			graph.refresh();
		}
		else
		{
			treeHolder.remove(this);							//文本为空，删除结点
		}
	}
	
	@Override
	public ContainerHolder getContainerHolder()					//获取父容器
	{
		return treeHolder;
	}
	
	@Override
	public Node getNode()										//获取结点
	{
		return node;
	}
	
	@Override
	public String getText()										//获取文本，去除左右多余空格
	{
		return super.getText().trim();
	}
	
	@Override
	public void paintComponent(Graphics g)						//重绘，根据不同状态设置不同边框和背景颜色
	{
		if (this == graph.getCurrentHolder() || bHover)			//悬停或者为选中控件
		{
			setBackground(clrBackground);
			setBorder(bdrFocused);
		}
		else													//普通状态
		{
			setBackground(Color.white);
			setBorder(bdrNotFocused);
		}
		
		super.paintComponent(g);
	}
	
	public void restore()										//恢复文本
	{
		if (node == null)
		{
			setText("");
		}
		else
		{
			setText(node.getWord());
		}
	}
	
	@Override
	public void setText(String szText)							//设置文本，左右添加空格以隔开
	{
		super.setText(" " + szText.trim() + " ");
	}
}