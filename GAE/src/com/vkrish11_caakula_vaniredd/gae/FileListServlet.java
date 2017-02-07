package com.vkrish11_caakula_vaniredd.gae;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.*;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.memcache.Stats;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.ListItem;
import com.google.appengine.tools.cloudstorage.ListOptions;
import com.google.appengine.tools.cloudstorage.ListResult;
import com.google.appengine.tools.cloudstorage.RetryParams;

@SuppressWarnings("serial")
public class FileListServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		PrintWriter pw = resp.getWriter();
		
		GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
		AppIdentityService appIdentity = AppIdentityServiceFactory.getAppIdentityService();
		long start = System.currentTimeMillis();
		ListResult result = gcsService.list(appIdentity.getDefaultGcsBucketName(), ListOptions.DEFAULT);
		int i =1;
		MemcacheService mservice;
		mservice = MemcacheServiceFactory.getMemcacheService();
		Stats stats = mservice.getStatistics();
		pw.println("<h3>No of file in MemCache : "+stats.getItemCount()+"</h3>");
		pw.println("<hr><h4>List of file in Bucket</h4>");
		pw.println("<table>");
		while (result.hasNext()){
		    ListItem l = result.next();
		    String name = l.getName();
		    pw.println("<tr>");
		    pw.println("<td>"+i+"</td>");
		    pw.println("<td>"+name+"</td>");
		    pw.println("<td><a href='/FileView?fname="+name+"&from=bucket'>view</a><td>");
		    pw.println("<td><a href='/FileDelete?fname="+name+"'>delete</a><td>");
		    pw.println("</tr>");
		    i++;
		}
		pw.println("</table>");
		long laps = System.currentTimeMillis()- start;
		pw.println("<br>");
		pw.println("<h3>Total Time Taken To list Files From Bucket: "+laps+" MilliSeconds</h3>");
	}
}
