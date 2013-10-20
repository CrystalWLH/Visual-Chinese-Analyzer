package edu.pku.java.g3l;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class GraphPanel extends FrameworkPanel implements ActionListener	//图像面板
{
	public static VisualChineseAnalyzer program;							//程序
	public static DocumentPanel document;									//文档
	
	private static final long serialVersionUID = -1688292615232323008L;
	
	private CanvasPanel panCanvas;											//画板
	private HasContainerHolder holder;										//当前操作对象
	
	private AquaButton btnSave, btnGraphics;								//保存按钮，截图按钮
	private AquaButton btnNode, btnTree, btnRelation;						//结点、树、关系按钮
	
	private SmartPanel panChangeableTitleButton;							//可变按钮栏
	private RelationPanel panRelation;										//关系设置栏
	
	public GraphPanel()
	{
		super("Dependency Trees", new CanvasPanel(), 80);
		
		setStatic();														//设置静态变量
		setFocusable(true);
		
		panCanvas = (CanvasPanel) getMain();								//获取主面板
		
		SmartPanel panTitleButton = getTitleButtonBar();					//标题栏
		panTitleButton.setLayout(new BorderLayout());
		
		SmartPanel panSolidTitleButton = new SmartPanel();					//固定按钮栏
		panSolidTitleButton.setLayout(new GridLayout());
		
		btnSave = new AquaButton("Save Trees");								//保存
		btnSave.addActionListener(this);
		btnSave.setPreferredSize(new Dimension(80, 16));
		panSolidTitleButton.add(btnSave);
		
		btnGraphics = new AquaButton("Save PNG");							//截图
		btnGraphics.addActionListener(this);
		btnGraphics.setPreferredSize(new Dimension(80, 16));
		panSolidTitleButton.add(btnGraphics);
		
		panChangeableTitleButton = new SmartPanel();						//可变按钮栏
		panChangeableTitleButton.setLayout(new GridLayout());
		
		panTitleButton.add(panSolidTitleButton, BorderLayout.EAST);
		panTitleButton.add(panChangeableTitleButton, BorderLayout.CENTER);
		
		btnNode = new AquaButton("");										//结点按钮
		btnNode.addActionListener(this);
		
		btnTree = new AquaButton("");										//树按钮
		btnTree.addActionListener(this);
		
		btnRelation = new AquaButton("Set Relation");						//关系按钮
		btnRelation.addActionListener(this);
		
		panRelation = new RelationPanel();
		add(panRelation, BorderLayout.SOUTH);								//关系栏
	}
	
	@Override
	public void update(Graphics g)//双缓冲
	{	
		Image bufferImg = null;
		if (bufferImg == null) bufferImg = createImage(getWidth(), getHeight());
		
		Graphics buffer = bufferImg.getGraphics();
		paint(buffer);
		
		g.drawImage(bufferImg, 0, 0, null);
	}

	@Override
	public void actionPerformed(ActionEvent e)								//处理按钮按下
	{
		if (e.getSource() == btnSave)										//保存
		{
			document.saveDocument();
			return;
		}
		
		if (e.getSource() == btnGraphics)									//截图
		{
			setCurrentHolder(null);											//取消选中
			validate();
			
			BufferedImage image = new BufferedImage(panCanvas.getWidth(), panCanvas.getHeight(), BufferedImage.TYPE_INT_RGB);
			panCanvas.paint(image.getGraphics());							//获取图像
			
			String szFilename = document.getID() + ".png";					//以ID为文件名
			try
			{
				ImageIO.write(image, "png", new File(szFilename));			//保存并提示
				program.setStatus("Graphics saved to " + szFilename + " successfully.");
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
				program.setStatus("Failed to save the graphics.");
			}
			
			return;
		}
		
		if (e.getSource() == btnNode)										//结点按钮
		{
			if (!(holder instanceof NodeHolder)) return;
			NodeHolder nodeHolder = (NodeHolder) holder;
			
			if (btnNode.getText().equals("Delete Node"))
			{
				TreeHolder treeHolder = (TreeHolder) nodeHolder.getContainerHolder();
				treeHolder.remove(nodeHolder);								//删除结点
			}
			else
			{
				nodeHolder.confirm();										//创建结点
			}
			
			return;
		}
		
		if (e.getSource() == btnTree)										//树
		{
			if (!(holder instanceof TreeHolder)) return;
			TreeHolder treeHolder = (TreeHolder)holder;
			ForestHolder forestHolder = (ForestHolder) treeHolder.getContainerHolder();
			
			if (btnTree.getText().equals("Create Tree"))
			{
				forestHolder.insert(treeHolder);							//创建树
			}
			else
			{
				forestHolder.remove(treeHolder);							//删除树
			}
			return;
		}
		
		if (e.getSource() == btnRelation)									//关系
		{
			if (!(holder instanceof HasContainerHolder)) return;
			
			panChangeableTitleButton.removeAll();
			validate();
			repaint();
			
			panCanvas.disableHolder();										//禁用各容器
			panRelation.setVisible(true);									//显示关系蓝
			
			return;
		}
	}
	
	public void addTitleButton(AquaButton button, int nWidth)				//添加标题按钮
	{
		panChangeableTitleButton.add(button);
		button.setPreferredSize(new Dimension(nWidth, 16));					//设置大小
		
		validate();															//重绘
		repaint();
	}
	
	public CanvasPanel getCanvas()											//返回画布
	{
		return panCanvas;
	}
	
	public HasContainerHolder getCurrentHolder()							//获取当前对象
	{
		return holder;
	}
	
	public void rebuild()													//重构
	{
		panCanvas.rebuild();
		panRelation.setVisible(false);
		refresh();
	}
	
	public void refresh()													//刷新
	{
		panCanvas.refresh();
		validate();
		repaint();
	}
	
	public void setCurrentHolder(HasContainerHolder obj)					//设置当前对象
	{
		if (obj != null && (!obj.getContainerHolder().isEnabled() || obj instanceof JComponent && !((JComponent)obj).isEnabled())) return;
																			//对象被禁用或其父容器被禁用
		holder = obj;
		panChangeableTitleButton.removeAll();								//删除可变按钮
		
		if (holder instanceof NodeHolder)									//选中了结点
		{
			NodeHolder nodeHolder = (NodeHolder) holder;
			if (nodeHolder.getNode() == null)
			{		
				btnNode.setText("Create Node");								//空结点显示创建按钮
			}
			else
			{
				if (nodeHolder.getNode() instanceof RootNode)				//根结点什么也不做
				{
					validate();
					repaint();
					
					return;
				}
				addTitleButton(btnRelation, 80);							//显示关系按钮
				btnNode.setText("Delete Node");
			}
			
			addTitleButton(btnNode, 80);									//显示创建/删除结点按钮
			return;
		}
		
		if (holder instanceof TreeHolder)									//选中了树
		{
			TreeHolder treeHolder = (TreeHolder) holder;
			if (treeHolder.getTree() == null)
			{
				btnTree.setText("Create Tree");								//空树显示创建
			}
			else
			{
				btnTree.setText("Delete Tree");								//有结点的树显示删除
			}
			
			addTitleButton(btnTree, 80);									//显示创建/删除树按钮
			return;
		}
		
		if (holder instanceof ArcHolder)									//选中了弧线
		{
			addTitleButton(btnRelation, 80);								//显示关系按钮
			return;
		}
		
		validate();
		repaint();
	}
	
	private void setStatic()												//设置静态变量
	{
		ContainerHolder.graph = this;
		NodeHolder.graph = this;
		ArcHolder.graph = this;
		RelationPanel.graph = this;
	}
}
