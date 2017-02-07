package com.vkrish11_caakula_vaniredd.gae;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

public class FileCreateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		resp.getWriter().println("Creating file..");
		
		String filename = req.getParameter("createfilename");
		String filedescription = req.getParameter("createfiledescription");
		
		GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams
				.getDefaultInstance());
		AppIdentityService appIdentity = AppIdentityServiceFactory
				.getAppIdentityService();

		
		byte[] data = filedescription.getBytes("UTF-8");
		int len = data.length;

		long start = System.currentTimeMillis();
		if (len >= 1024 * 1024) {
			GcsFilename asdf = new GcsFilename(
					appIdentity.getDefaultGcsBucketName(), filename);
			GcsOutputChannel outputChannel = gcsService
					.createOrReplace(asdf, GcsFileOptions.getDefaultInstance());
			outputChannel.write(ByteBuffer.wrap(data));
			outputChannel.close();
			long laps = System.currentTimeMillis() - start;
			resp.getWriter().println("<h3>File : '" + filename + "' Uploaded to Bucket in "+laps+" MilliSeconds</h3>");
		} else {
		MemcacheService mservice;
		mservice = MemcacheServiceFactory.getMemcacheService();
		
		mservice.put(filename, filedescription, null, MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
		
		long laps = System.currentTimeMillis() - start;
		resp.getWriter().println("<h3>File : '" + filename + "' Uploaded to MemCache in "+laps+" MilliSeconds</h3>");
		}
	}
}
