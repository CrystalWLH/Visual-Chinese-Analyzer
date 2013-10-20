package cn.edu.pku.eecs.vca.servlet.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.pku.eecs.vca.util.LibraryItem;


public class DbQueryByText extends Database
{
	private static final long serialVersionUID = -2026865619251046388L;

	@Override
    protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	try
    	{
    		response.setContentType("application/octet-stream");  
            response.setHeader("Cache-Control", "no-cache"); 
            
	    	InputStream in = request.getInputStream();
	    	ObjectInputStream oin = new ObjectInputStream(in);
	    	String szText = (String) oin.readObject();
	    	oin.close();
	    	in.close();
	    	
	    	Vector<LibraryItem> ret = queryByText(szText);
			
	        OutputStream out = response.getOutputStream();
	    	ObjectOutputStream oout = new ObjectOutputStream(out);
	        oout.writeObject(ret);
	    	oout.flush();
	    	oout.close();
	    	out.close();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}    	
    }
}
