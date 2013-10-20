package edu.pku.java.g3l;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

public class TextMenu extends JPopupMenu implements ActionListener, MouseListener	//文本框右键菜单
{
	private static final long serialVersionUID = -4972842171827555016L;
	
	private JMenuItem itemCut, itemCopy, itemPaste, itemDelete, itemSelectAll;		//剪切、复制、粘贴、删除、全选
	
	public TextMenu()
	{	
		itemCut = new JMenuItem("Cut");												//剪切项目
		itemCut.setMnemonic('T');
		itemCut.addActionListener(this);
		add(itemCut);
		
		itemCopy = new JMenuItem("Copy");											//复制项目
		itemCopy.setMnemonic('C');
		itemCopy.addActionListener(this);
		add(itemCopy);
		
		itemPaste = new JMenuItem("Paste");											//粘贴项目
		itemPaste.addActionListener(this);
		itemPaste.setMnemonic('P');
		add(itemPaste);
		
		itemDelete = new JMenuItem("Delete");										//删除项目
		itemDelete.setMnemonic('D');
		itemDelete.addActionListener(this);
		add(itemDelete);
		
		addSeparator();																//添加分割线
		
		itemSelectAll = new JMenuItem("Select All");								//全选项目
		itemSelectAll.setMnemonic('A');
		itemSelectAll.addActionListener(this);
		add(itemSelectAll);
	}
	
	public void actionPerformed(ActionEvent e)										//菜单项被点击
	{
		JTextComponent txtComponent = (JTextComponent) getInvoker();  				//获取菜单项

		if (e.getSource() == itemCut)												//点击剪切，剪切选中文本  
		{
			txtComponent.cut();
			return;
		}
		
		if (e.getSource() == itemCopy)												//点击复制，复制选中文本
		{
			txtComponent.copy();
			return;
		}

		if (e.getSource() == itemPaste)												//点击粘贴，粘贴剪贴板中文本
		{  
			txtComponent.paste();
			return;
		}
		
		if (e.getSource() == itemDelete)											//点击删除
		{
			try
			{
				Document document = txtComponent.getDocument();
                Position posStart, posEnd; 
                
                posStart = document.createPosition(txtComponent.getSelectionStart());//选中文本开始位置
                posEnd = document.createPosition(txtComponent.getSelectionEnd());	//选中文本结束位置

                if ((posStart != null) && (posEnd   !=   null) && (posStart.getOffset() != posEnd.getOffset()))
                {  
                	document.remove(posStart.getOffset(), posEnd.getOffset() - posStart.getOffset());
                }																	//删除选中部分
			}
			catch (BadLocationException e1) { }
			
			return;
		}
		
		if (e.getSource() == itemSelectAll)											//点击全选，选中全部文本
		{
			txtComponent.selectAll();
			return;
		}	
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{  
		if (e.isPopupTrigger() && e.getSource() instanceof JTextComponent)			//触发右键菜单且控件为文本控件
		{
			JTextComponent txtComponent = (JTextComponent)e.getSource();
			txtComponent.requestFocusInWindow();									//获取焦点

			String szSelected = txtComponent.getSelectedText();						//获取选中文字
			boolean hasSelectedText = szSelected != null && !szSelected.equals("");//选中文字是否为空
				
			itemCut.setEnabled(hasSelectedText);									//选中文字非空启用剪切
			itemCopy.setEnabled(hasSelectedText);									//选中文字非空启用复制
			itemDelete.setEnabled(hasSelectedText);									//选中文字非空启用删除
			
			show(txtComponent, e.getX(), e.getY());									//显示菜单
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) { }
	@Override
	public void mouseEntered(MouseEvent e) { }
	@Override
	public void mouseExited(MouseEvent e) { }
	@Override
	public void mousePressed(MouseEvent e) { }	
}