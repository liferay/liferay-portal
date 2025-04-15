/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.jsp.engine.internal.delegate;

import com.liferay.shielded.container.ShieldedContainerInitializer;

import jakarta.servlet.ServletContext;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import jakarta.servlet.descriptor.TaglibDescriptor;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tomcat.util.descriptor.web.JspConfigDescriptorImpl;
import org.apache.tomcat.util.descriptor.web.TaglibDescriptorImpl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Shuyang Zhou
 */
public class JspConfigDescriptorServletContextDelegate {

	public JspConfigDescriptorServletContextDelegate(
		ServletContext servletContext) {

		_servletContext = servletContext;
	}

	public JspConfigDescriptor getJspConfigDescriptor() {
		List<TaglibDescriptor> taglibDescriptors = new ArrayList<>();

		DocumentBuilderFactory documentBuilderFactory =
			DocumentBuilderFactory.newInstance();

		try (InputStream inputStream = _servletContext.getResourceAsStream(
				ShieldedContainerInitializer.SHIELDED_CONTAINER_WEB_XML)) {

			DocumentBuilder documentBuilder =
				documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse(inputStream);

			NodeList taglibNodeList = document.getElementsByTagName("taglib");

			for (int i = 0; i < taglibNodeList.getLength(); i++) {
				Element taglibElement = (Element)taglibNodeList.item(i);

				NodeList taglibLocationNodeList =
					taglibElement.getElementsByTagName("taglib-location");

				Node taglibLocationNode = taglibLocationNodeList.item(0);

				NodeList taglibURINodeList = taglibElement.getElementsByTagName(
					"taglib-uri");

				Node taglibURINode = taglibURINodeList.item(0);

				taglibDescriptors.add(
					new TaglibDescriptorImpl(
						taglibLocationNode.getTextContent(),
						taglibURINode.getTextContent()));
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		return new JspConfigDescriptorImpl(
			Collections.emptySet(), taglibDescriptors);
	}

	private final ServletContext _servletContext;

}