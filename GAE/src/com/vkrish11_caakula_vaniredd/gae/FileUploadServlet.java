package com.vkrish11_caakula_vaniredd.gae;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.nio.ByteBuffer;

import javax.servlet.ServletException;
import javax.servlet.http.*;

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

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// private StorageService storage = new StorageService();
	private static final int BUFFER_SIZE = 1024;
	private static final Logger log = Logger.getLogger(FileUploadServlet.class
			.getName());
	private ByteArrayOutputStream bao;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		resp.getWriter().println("Files are uploading...<br>");

		ServletFileUpload upload = new ServletFileUpload();

		try {

			FileItemIterator iter = upload.getItemIterator(req);

			GcsService gcsService = GcsServiceFactory
					.createGcsService(RetryParams.getDefaultInstance());
			AppIdentityService appIdentity = AppIdentityServiceFactory
					.getAppIdentityService();

			MemcacheService mservice;
			mservice = MemcacheServiceFactory.getMemcacheService();
			
			long start = System.currentTimeMillis();
			
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				String fileName = item.getName();
				String mime = item.getContentType();
				InputStream is = item.openStream();
				GcsFileOptions options = new GcsFileOptions.Builder()
						.mimeType(mime).acl("public-read").build();
				byte[] buff = new byte[BUFFER_SIZE];
				int bytesRead = 0;
				bao = new ByteArrayOutputStream();
				while ((bytesRead = is.read(buff)) != -1) {
					bao.write(buff, 0, bytesRead);
				}
				byte[] data = bao.toByteArray();
				int len = data.length;
				if (len >= 1024 * 1024) {
					GcsFilename asdf = new GcsFilename(
							appIdentity.getDefaultGcsBucketName(), fileName);
					GcsOutputChannel outputChannel = gcsService
							.createOrReplace(asdf, options);
					outputChannel.write(ByteBuffer.wrap(data));
					outputChannel.close();
					
					resp.getWriter().println(
							"File '" + fileName + "' of size " + len
									+ "KB Uploaded to bucket<br>");
				} else {
					//resp.getWriter().println(len);
					
					mservice.put(fileName, new String(data, "UTF-8"), null,
							MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
					resp.getWriter().println(
							"File : '" + fileName + "' Uploaded to MemCache<br>");
				}
			}
			long laps = System.currentTimeMillis() - start;
			resp.getWriter().println("<h3>Total Time Taken : "+laps+" MilliSeconds</h3>");
			
		} catch (FileUploadException e) {
			resp.getWriter().println("FileUploadException::" + e.getMessage());
			log.severe(this.getServletName() + ":FileUploadException::"
					+ e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.severe(this.getServletName() + ":Exception::" + e.getMessage());
			resp.getWriter().println("Exception::" + e.getMessage());
			e.printStackTrace();
		}
	}
}
