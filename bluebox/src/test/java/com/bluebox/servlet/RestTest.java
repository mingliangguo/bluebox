package com.bluebox.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.mortbay.jetty.testing.HttpTester;

import com.bluebox.Utils;
import com.bluebox.rest.json.JSONAutoCompleteHandler;
import com.bluebox.rest.json.JSONFolderHandler;
import com.bluebox.smtp.Inbox;
import com.bluebox.smtp.storage.BlueboxMessage;
import com.bluebox.smtp.storage.BlueboxMessage.State;

public class RestTest extends BaseServletTest {

	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAutocomplete() throws IOException, Exception {
		String url = "/"+JSONAutoCompleteHandler.JSON_ROOT+"?start=0&count=10&name=*";
		JSONObject js = getRestJSON(url);
		JSONArray ja = js.getJSONArray("items");
		for (int i = 0; i < ja.length();i++) {
			assertNotSame("Full name not found",ja.getJSONObject(i).getString("name"),ja.getJSONObject(i).getString("label"));
		}
		log.info(js.toString(3));
	}

	public void testInbox() throws Exception {


		// first we check directly
		Utils.waitFor(COUNT);

		assertEquals("Missing mails",COUNT,getMailCount(BlueboxMessage.State.NORMAL));

		// now hit the REST web service
		String inboxURL = "/"+JSONFolderHandler.JSON_ROOT;
		log.info("Checking URL:"+inboxURL);
		JSONObject js = getRestJSON(inboxURL);
		JSONArray items = js.getJSONArray("items");
		for (int i = 0; i < items.length(); i++) {
			JSONObject item = items.getJSONObject(i);
			assertTrue(item.has("id"));
			assertTrue(item.has("name"));
			assertTrue(item.has("type"));
			assertTrue(item.has("style"));
			JSONArray children = item.getJSONArray("children");
			for (int j = 0; j < children.length();j++) {
				JSONObject child = children.getJSONObject(j);
				assertTrue(child.has("id"));
				assertTrue(child.has("name"));
				assertTrue(child.has("style"));
				assertTrue(child.has("count"));
				assertTrue(child.has("state"));
				assertTrue(child.has("email"));
				if ((child.get("id").equals("All"))||(child.get("id").equals("Inbox"))) {
					assertEquals("Missing mails",COUNT,child.getInt("count"));
				}
				else {
					assertEquals("Missing mails",0,child.getInt("count"));					
				}
			}
		}
		log.info(js.toString(3));

		//		{
		//			   "identifier": "id",
		//			   "label": "name",
		//			   "items": [{
		//			      "id": "Overview",
		//			      "name": "Inbox for \/NORMAL@XHOSA",
		//			      "type": "folder",
		//			      "style": "rootFolder",
		//			      "children": [
		//			         {
		//			            "id": "Inbox",
		//			            "name": "Inbox (0)",
		//			            "count": 0,
		//			            "email": "\/NORMAL@XHOSA",
		//			            "state": "NORMAL",
		//			            "style": "inboxFolder"
		//			         },
		//			         {
		//			            "id": "Trash",
		//			            "name": "Trash (0)",
		//			            "count": 0,
		//			            "email": "\/NORMAL@XHOSA",
		//			            "state": "DELETED",
		//			            "style": "trashFolder"
		//			         },
		//			         {
		//			            "id": "All",
		//			            "name": "All documents (0)",
		//			            "count": 0,
		//			            "email": "\/NORMAL@XHOSA",
		//			            "state": "ANY",
		//			            "style": "allFolder"
		//			         }
		//			      ]
		//			   }]
		//			}		
	}

	public void testAttatchmentHandler() throws Exception {
		Inbox.getInstance().deleteAll();
		Utils.waitFor(0);
		InputStream emlStream = new FileInputStream("src/test/resources"+File.separator+"test-data"+File.separator+"inlineattachments.eml");
		Utils.uploadEML(emlStream);
		Utils.waitFor(1);
		assertEquals("Mail was not delivered",1,Inbox.getInstance().getMailCount(State.ANY));
		
		// now retrieve the atachment
		List<BlueboxMessage> messages = Inbox.getInstance().listInbox(null, BlueboxMessage.State.ANY, 0, 5, BlueboxMessage.RECEIVED, true);
		BlueboxMessage msg = messages.get(0);
		HttpTester request = new HttpTester();
		request.setMethod("GET");
		request.setHeader("HOST","127.0.0.1");
		request.setURI(getBaseURL()+"/rest/json/inbox/attachment/"+msg.getIdentifier()+"/0/ISM%20Open%20Tickets%20Report%20-%2003-13-2012-DOW.zip");
		request.setVersion("HTTP/1.0");

		HttpTester response = new HttpTester();
		response.parse(getTester().getResponses(request.generate()));

		assertEquals(200,response.getStatus());
	}

}
