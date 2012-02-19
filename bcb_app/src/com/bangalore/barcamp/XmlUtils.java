/*
 * Copyright (C) 2012 Saurabh Minni <http://100rabh.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bangalore.barcamp;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.bangalore.barcamp.data.BarcampData;
import com.bangalore.barcamp.data.Session;
import com.bangalore.barcamp.data.Slot;

public class XmlUtils {
	public static BarcampData parseBCBXML(String xml)
			throws ParserConfigurationException, FactoryConfigurationError,
			SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		InputStream str = new StringBufferInputStream(xml);
		Document doc = builder.parse(str);
		NodeList list = doc.getElementsByTagName("barcamp-data");
		int size = list.getLength();
		BarcampData data = null;
		List<Session> sessionList = null;
		List<Slot> slotList = null;
		for (int i = 0; i < size; i++) {
			data = new BarcampData();
			Node barcampNode = list.item(i);
			NodeList slots = barcampNode.getChildNodes();
			slotList = new ArrayList<Slot>();
			for (int j = 0; j < slots.getLength(); j++) {
				Node slotNode = slots.item(j);
				Slot s = new Slot();
				System.out.println("Node Name " + slotNode.getNodeName());
				if (!slotNode.getNodeName().equals("slot"))
					continue;
				NodeList sessionNodeList = slotNode.getChildNodes();
				sessionList = new ArrayList<Session>();
				NamedNodeMap attributes = slotNode.getAttributes();
				s.id = Integer.parseInt(slotNode.getAttributes()
						.getNamedItem("id").getNodeValue());
				s.startTime = Integer.parseInt(attributes.getNamedItem(
						"startTime").getNodeValue());
				s.endTime = Integer.parseInt(attributes.getNamedItem("endTime")
						.getNodeValue());
				s.type = attributes.getNamedItem("type").getNodeValue();
				s.name = attributes.getNamedItem("name").getNodeValue();
				for (int k = 0; k < sessionNodeList.getLength(); k++) {
					Node sessionNode = sessionNodeList.item(k);
					if (!sessionNode.getNodeName().equals("session"))
						continue;
					NodeList children = sessionNode.getChildNodes();
					Session session = new Session();
					for (int x = 0; x < children.getLength(); x++) {
						Node n = children.item(x);
						String value = "";
						if (n.getNodeName().equals("time")) {
							value = n.getFirstChild().getNodeValue();
							session.time = value.trim();
							System.out.println("Node Value: " + value);
						} else if (n.getNodeName().equals("title")) {
							value = n.getFirstChild().getNodeValue();
							session.title = value.trim();
							System.out.println("Node Value: " + value);
						} else if (n.getNodeName().equals("location")) {
							value = n.getFirstChild().getNodeValue();
							session.location = value.trim();
							System.out.println("Node Value: " + value);
						} else if (n.getNodeName().equals("presenter")) {
							value = n.getFirstChild().getNodeValue();
							session.presenter = value.trim();
							System.out.println("Node Value: " + value);
						} else if (n.getNodeName().equals("id")) {
							value = n.getFirstChild().getNodeValue();
							session.id = value.trim();
							System.out.println("Node Value: " + value);
						}
					}
					session.pos = sessionList.size();
					sessionList.add(session);
				}
				s.sessionsArray = sessionList;
				s.pos = slotList.size();
				slotList.add(s);
			}
			data.slotsArray = slotList;

		}
		return data;
	}
}
