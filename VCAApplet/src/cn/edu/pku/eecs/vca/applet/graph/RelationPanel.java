package cn.edu.pku.eecs.vca.applet.graph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import cn.edu.pku.eecs.vca.applet.VisualChineseAnalyzer;
import cn.edu.pku.eecs.vca.applet.ui.AquaButton;
import cn.edu.pku.eecs.vca.applet.ui.DocumentPanel;
import cn.edu.pku.eecs.vca.core.DependencyTree;
import cn.edu.pku.eecs.vca.core.Node;
import cn.edu.pku.eecs.vca.core.RootNode;
import cn.edu.pku.eecs.vca.util.Operation;






public class RelationPanel extends JPanel implements ActionListener			//关系提示栏
{
	public static VisualChineseAnalyzer program;							//程序
	public static DocumentPanel document;									//文档
	public static GraphPanel graph;											//图像
	public static CanvasPanel canvas;										//画布
	
	private static boolean isDelimiter(char c)								//检查是否标点符号
	{
		if (c >= 0x4E00 && c <= 0x9FA5) return false;						//汉字
		if ((c >= 0x0041 && c <= 0x005A) || (c >= 0x0061 && c <= 0x007A)) return false;//字母
		if (c >= 0x0030 && c <= 0x0039) return false;						//数字
		if ((c >= 0xFF21 && c <= 0xFF3A) || (c >= 0xFF41 && c <= 0xFF5A)) return false;//全角字母
		if (c >= 0xFF10 && c <= 0xFF19) return false;						//全角数字
		return true;
	}
	
	private static final long serialVersionUID = -3504743347539214389L;

	private TreeHolder treeHolder;											//结点所在树面板
	private DependencyTree tree;											//结点所在树
	private Node node;														//要设置关系的结点
	
	private JLabel lblRelation, lblParent;									//标签
	private JTextField txtRelation;											//关系文本框
	
	private JComboBox cmbParent;											//父结点组合框
	private DefaultComboBoxModel dcmParent;
	
	private AquaButton btnSet, btnDelete, btnCancel;
	
	public RelationPanel()
	{		
		lblRelation = new JLabel("Relation:");								//关系标签
		txtRelation = new JTextField(5);
		txtRelation.getDocument().addDocumentListener(new DocumentListener()//文本框内容空时禁用set
		{
			public void changedUpdate(DocumentEvent e)
			{
				btnSet.setEnabled(!(txtRelation.getText().trim().equals("")));
			}

			public void insertUpdate(DocumentEvent e)
			{
				btnSet.setEnabled(!(txtRelation.getText().trim().equals("")));
			}

			public void removeUpdate(DocumentEvent e)
			{
				btnSet.setEnabled(!(txtRelation.getText().trim().equals("")));
			}
			
		});
		
		JPanel panRelation = new JPanel();									//各面板控件组织
		panRelation.add(lblRelation);
		panRelation.add(txtRelation);
		
		lblParent = new JLabel("Parent:");
		cmbParent = new JComboBox();
		
		dcmParent = new DefaultComboBoxModel();
		cmbParent.setModel(dcmParent);
		
		JPanel panParent = new JPanel();		
		panParent.add(lblParent);
		panParent.add(cmbParent);
		
		JPanel panWest = new JPanel();
		panWest.setLayout(new GridLayout());
		
		panWest.add(panRelation);
		panWest.add(panParent);
		
		btnSet = new AquaButton("Set");										//设置按钮
		btnSet.addActionListener(this);
		btnSet.setPreferredSize(new Dimension(70, 20));
		
		btnDelete = new AquaButton("Delete");								//删除按钮
		btnDelete.addActionListener(this);
		btnDelete.setPreferredSize(new Dimension(70, 20));
		
		btnCancel = new AquaButton("Cancel");								//取消按钮
		btnCancel.addActionListener(this);
		btnCancel.setPreferredSize(new Dimension(70, 20));
		
		JPanel panEast = new JPanel();
		panEast.add(btnSet);
		panEast.add(btnDelete);
		panEast.add(btnCancel);
		
		setLayout(new BorderLayout());
		add(panWest, BorderLayout.WEST);
		add(panEast, BorderLayout.EAST);
	}

	public void actionPerformed(ActionEvent e)								//检测按钮点击
	{
		if (e.getSource() == btnSet)										//设置按钮
		{
			String szText = txtRelation.getText().trim();					//去掉首尾空格
			
			Node parent, newParent;
			parent = node.getParent();
			newParent = (Node) cmbParent.getSelectedItem();
			
			if (parent != newParent || node.getReln() == null || !node.getReln().equals(szText))//父结点或者关系发生改变
			{			
				node.setRelation(szText);									//添加操作，设置关系和父结点
				node.setParent(newParent);
				
				document.addOperation(new Operation("Set Node Relation: " + node.getWord()));
				program.setStatus("Set node relation " + node.getWord() + ".");
				
				treeHolder.rebuild();
				graph.refresh();
			}
			setVisible(false);												//调用重载后的函数隐藏面板
			return;
		}	
		
		if (e.getSource() == btnDelete)										//删除关系
		{
			node.setRelation(null);											//添加操作
			node.setParent(null);
			
			document.addOperation(new Operation("Delelte Node Relation: " + node.getWord()));
			program.setStatus("Delete node relation " + node.getWord() + ".");

			treeHolder.rebuild();
			graph.refresh();
						
			setVisible(false);												//隐藏
			return;
		}	
		
		if (e.getSource() == btnCancel)										//取消
		{
			setVisible(false);												//隐藏
			return;
		}		
	}
	
	@Override
	public void setVisible(boolean bVisible)								//重载设置可视
	{
		super.setVisible(bVisible);											//父类的可见设置函数
		
		if (bVisible)														//设置可见
		{
			HasContainerHolder holder = graph.getCurrentHolder();			//当前选中项目
			node = holder.getNode();										//结点
			treeHolder = (TreeHolder) holder.getContainerHolder();			//树面板
			tree = treeHolder.getTree();									//获取树
			
			btnDelete.setEnabled(node.getParent() != null);					//没有关系，无法删除
			if (node.getReln() != null)
			{
				txtRelation.setText(node.getReln());						//文本为初始时的关系
				btnSet.setEnabled(!(txtRelation.getText().trim().equals("")));//如果关系非空则启用设置按钮
			}
			else
			{
				txtRelation.setText("");									//文本为空
				btnSet.setEnabled(false);									//暂时禁用设置
			}
			
			dcmParent.removeAllElements();									//删除组合框内结点
			for (Node newParent : tree.getNodes())
			{
				if (newParent == node) continue;							//父结点不能等于自身
				
				if (!(newParent instanceof RootNode) && newParent.getParent() == null)//无父节点的非根结点
				{
					String szWord = newParent.getWord();
					if (szWord.length() == 0) continue;						//长度为0
					
					if (isDelimiter(szWord.charAt(0))) continue;			//标点符号
				}
				
				boolean bCyclic = false;									//依存树必须是有向无环图
				for (Node ponParent = newParent.getParent(); ponParent != null; ponParent = ponParent.getParent())
				{
					if (ponParent == node)
					{
						bCyclic = true;										//如果此结点作为父结点将会产生环
						break;
					}
				}
				if (bCyclic) continue;
				
				dcmParent.addElement(newParent);							//添加结点
			}
			
			Node parent = node.getParent();
			if (dcmParent.getIndexOf(parent) >= 0)	dcmParent.setSelectedItem(parent);//设置初始选中项目
			
			canvas.disableHolder();											//停用画板
		}
		else
		{
			graph.setCurrentHolder(treeHolder);								//设置选中容器为树
			if (node != null)canvas.enableHolder();							//启用画板
		}
		
		validate();
		repaint();
	}
}