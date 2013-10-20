package cn.edu.pku.eecs.vca.servlet.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

import cn.edu.pku.eecs.vca.core.Forest;
import cn.edu.pku.eecs.vca.util.LibraryItem;

public abstract class Database extends HttpServlet
{
	private static final long serialVersionUID = -2516959194081363288L;
	
	private Connection getConnection() throws SQLException, NamingException
	{
		Context initCtx = new InitialDirContext();
		DataSource dataSource = (DataSource) initCtx.lookup("java:comp/env/jdbc/vca");
		
		if (dataSource == null) throw new SQLException();
		else 
			return dataSource.getConnection();
	}
	
	private void closeConnection(Connection connection)
	{
		if (connection == null) return;
		
		try
		{
			connection.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void closeResultSet(ResultSet resultSet)
	{
		if (resultSet == null) return;
		
		try
		{
			resultSet.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void closeStatement(Statement statement)
	{
		if (statement == null) return;
		
		try
		{
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
			
	public boolean delete(int nId)					//删除记录
	{
		Connection connection = null;
		PreparedStatement statement = null;
		
		try
		{
			connection = getConnection();
			statement = connection.prepareStatement("delete from library where id = ?;");
			
			statement.setInt(1, nId);				//传入要删除的记录ID
			return statement.executeUpdate() > LibraryItem.NEW;
		}
		catch(Exception e)
		{
			closeStatement(statement);
			closeConnection(connection);
			
			return false;
		}
	}
	
	public Forest queryById(int nId)				//根据ID查询记录，返回文档（森林）
	{
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try
		{
			connection = getConnection();
			statement = connection.prepareStatement("select tree from library where id = ?;");
			
			Forest forest = null;
			
			statement.setInt(1, nId);				//传入参数，ID
			resultSet = statement.executeQuery();
			
			if (resultSet.next()) forest = Forest.createFromBytes(resultSet.getBytes("tree"));
													//通过字节数组创建新森林
			resultSet.close();						//关闭结果集
			return forest;							//返回森林
		}
		catch (Exception e)
		{
			closeResultSet(resultSet);
			closeStatement(statement);
			closeConnection(connection);
			
			return null;
		}
	}
	
	public Vector<LibraryItem> queryAll()			//查询所有记录
	{
		Connection connection = null;
		PreparedStatement statement = null;
				
		try
		{
			connection = getConnection();
			statement = connection.prepareStatement("select id, original from library;");
			
			Vector<LibraryItem> lstResult = query(statement);
			
			if (lstResult == null) throw new Exception();
			return lstResult;
		}
		catch (Exception e)
		{
			closeStatement(statement);
			closeConnection(connection);
			
			return null;
		}
	}
	
	public Vector<LibraryItem> queryByText(String szText)
	{
		Connection connection = null;
		PreparedStatement statement = null;
				
		try
		{
			connection = getConnection();
			statement = connection.prepareStatement("select id, original from library where original like ?;");
			
			statement.setString(1, "%" + szText + "%");
			Vector<LibraryItem> lstResult = query(statement);			//传入模糊查询的文本作为参数
			
			if (lstResult == null) throw new Exception();
			return lstResult;
		}
		catch (Exception e)
		{
			closeStatement(statement);
			closeConnection(connection);
			
			return null;
		}
	}
	
	public int save(int nId, Forest forest)		//保存，返回记录的ID
	{
		Connection connection = null;
		PreparedStatement statement = null;
				
		try
		{
			connection = getConnection();
			statement = connection.prepareStatement("update library set original = ? , tree = ? where id = ?;");
			
			statement.setString(1, forest.getOriginal());
			statement.setBytes(2, forest.getBytes());
			statement.setInt(3, nId);				//设置好各参数
			
			if (statement.executeUpdate() > LibraryItem.NEW) return nId;
			else return insert(forest, connection);	
		}
		catch (Exception e)
		{
			closeStatement(statement);
			closeConnection(connection);
			
			return LibraryItem.NEW;
		}
	}

	private int insert(Forest forest, Connection connection)			//插入纪录，返回ID
	{ 
		PreparedStatement statement = null;
		PreparedStatement statement2 = null;
		ResultSet resultSet = null;
		
		try
		{
			statement = connection.prepareStatement("insert into library(original, tree) values (?, ?);");
			statement2 = connection.prepareStatement("select @@identity;");
						
			statement.setString(1, forest.getOriginal());
			statement.setBytes(2, forest.getBytes());
			
			statement.executeUpdate();					//插入记录
			
			resultSet = statement2.executeQuery();
			resultSet.next();
			
			return resultSet.getInt(1);
		}
		catch(Exception e)
		{
			closeResultSet(resultSet);
			closeStatement(statement);
			closeStatement(statement2);
			
			return LibraryItem.NEW;
		}		
	}
	
	private Vector<LibraryItem> query(PreparedStatement preparedStatement)
	{													//查询记录，返回LibraryItem
		ResultSet resultSet = null;
		
		try
		{
			Vector<LibraryItem> lstResult = new Vector<LibraryItem>();
			
			resultSet = preparedStatement.executeQuery();
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
			closeResultSet(resultSet);
			
			return null;
		}
	}
}