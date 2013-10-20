package edu.pku.java.g3l;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class LibraryItem										//在树库面板中显示的项目
{
	public static final int NEW = 0;					//新创建记录的ID为0
	
	private int nId;									//保存ID和原始文本
	private String szOriginal;
	
	public LibraryItem(int nId, String szOriginal)		//构造函数
	{
		this.nId = nId;
		this.szOriginal = szOriginal;
	}
	
	public int getId()									//返回ID
	{
		return nId;
	}
	
	@Override
	public String toString()							//转换字符串
	{
		return nId + " : " + szOriginal;
	}
}

public class Library									//利用SQLite数据库存储树库
{
	private static final String FILE_NAME = "nottm.db";	//数据库名， for Nottm Forest
	
	private static Connection connection;				//连接
	
	private static PreparedStatement insertStatement;	//插入语句
	private static PreparedStatement lastInsertIdStatement;//查询最后插入记录ID语句
	
	private static PreparedStatement deleteStatement;	//删除语句
	private static PreparedStatement updateStatement;	//更新语句
	
	private static PreparedStatement queryAllStatement;	//查询所有记录语句
	private static PreparedStatement queryByIdStatement;//根据ID查询语句
	private static PreparedStatement queryByTextStatement;//根据原始文本模糊查询语句
	
	public static void close()							//关闭数据库连接
	{
		try
		{
			connection.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean delete(int nId)				//删除记录
	{
		try
		{
			deleteStatement.setInt(1, nId);				//传入要删除的记录ID
			
			return deleteStatement.executeUpdate() > LibraryItem.NEW;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	public static boolean init()						//初始化
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + FILE_NAME);
														//连接数据库，不存在该文件则创建之
			Statement statement = connection.createStatement();//如果不存在创建表，字段分别为ID, 原始文本，依存森林
			statement.executeUpdate("create table if not exists library (id integer primary key, original, tree);");

			insertStatement = connection.prepareStatement("insert into library values (null, ?, ?);");
			lastInsertIdStatement = connection.prepareStatement("select last_insert_rowid() from library");
			
			deleteStatement = connection.prepareStatement("delete from library where id = ?;");
			updateStatement = connection.prepareStatement("update library set original = ? , tree = ? where id = ?;");
			
			queryAllStatement = connection.prepareStatement("select * from library;");
			queryByIdStatement = connection.prepareStatement("select * from library where id = ?;");
			queryByTextStatement = connection.prepareStatement("select * from library where original like ?;");
														//设置各语句
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static List<LibraryItem> queryAll()			//查询所有记录
	{
		return query(queryAllStatement);
	}
	
	public static Forest queryById(int nId)				//根据ID查询记录，返回文档（森林）
	{
		try
		{
			Forest forest = null;
			
			queryByIdStatement.setInt(1, nId);			//传入参数，ID
			ResultSet resultSet = queryByIdStatement.executeQuery();
			if (resultSet.next()) forest = Forest.createFromBytes(resultSet.getBytes("tree"));
														//通过字节数组创建新森林
			resultSet.close();							//关闭结果集
			return forest;								//返回森林
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<LibraryItem> queryByText(String szText)
	{
		try
		{
			queryByTextStatement.setString(1, "%" + szText + "%");
			return query(queryByTextStatement);			//传入模糊查询的文本作为参数
		}
		catch(SQLException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static int save(int nId, Forest forest)		//保存，返回记录的ID
	{
		try
		{
			updateStatement.setString(1, forest.getOriginal());
			updateStatement.setBytes(2, forest.getBytes());
			updateStatement.setInt(3, nId);				//设置好各参数
			
			if (updateStatement.executeUpdate() > LibraryItem.NEW)
			{
				return nId;								//更新成功
			}
			else
			{
				return insert(forest);					//插入新记录
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return LibraryItem.NEW;
		}
	}

	private static int insert(Forest forest)			//插入纪录，返回ID
	{
		int nId = LibraryItem.NEW; 
		try
		{
			insertStatement.setString(1, forest.getOriginal());
			insertStatement.setBytes(2, forest.getBytes());
			
			insertStatement.executeUpdate();			//插入记录
			
			ResultSet resultSet = lastInsertIdStatement.executeQuery();
			if (resultSet.next()) nId = resultSet.getInt(1);
			
			resultSet.close();							//查询上次插入记录的ID
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return nId;
	}
	
	private static List<LibraryItem> query(PreparedStatement preparedStatement)
	{													//查询记录，返回LibraryItem
		try
		{
			List<LibraryItem> lstResult = new ArrayList<LibraryItem>();
			
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next())					//添加查找到的记录
			{	
				int nId = resultSet.getInt("id");
				String szOriginal = resultSet.getString("original");
				lstResult.add(new LibraryItem(nId, szOriginal));		
			}
			resultSet.close();							//关闭结果集
			
			return lstResult;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}