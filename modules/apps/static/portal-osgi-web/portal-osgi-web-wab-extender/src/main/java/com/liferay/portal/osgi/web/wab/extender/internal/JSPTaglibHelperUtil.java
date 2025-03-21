/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.extender.internal;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import jakarta.servlet.ServletContext;

import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Tina Tian
 */
public class JSPTaglibHelperUtil {

	public static void scanTLDs(
		Bundle bundle, ServletContext servletContext,
		List<String> listenerClassNames) {

		Boolean analyzedTlds = (Boolean)servletContext.getAttribute(
			_ANALYZED_TLDS);

		if ((analyzedTlds != null) && analyzedTlds.booleanValue()) {
			return;
		}

		servletContext.setAttribute(_ANALYZED_TLDS, Boolean.TRUE);

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		Collection<String> resources = bundleWiring.listResources(
			"META-INF/", "*.tld", BundleWiring.LISTRESOURCES_RECURSE);

		for (String resource : resources) {
			URL url = bundle.getResource(resource);

			if (url == null) {
				continue;
			}

			_parseTLD(listenerClassNames, servletContext, url);
		}

		Collection<URL> urls = new ArrayList<>(
			bundleWiring.findEntries(
				"META-INF/", "*.tld", BundleWiring.LISTRESOURCES_RECURSE));

		urls.addAll(
			bundleWiring.findEntries(
				"WEB-INF/", "*.tld", BundleWiring.LISTRESOURCES_RECURSE));

		for (URL url : urls) {
			_parseTLD(listenerClassNames, servletContext, url);
		}
	}

	private static void _parseTLD(
		List<String> listenerClassNames, ServletContext servletContext,
		URL url) {

		try (InputStream inputStream = url.openStream()) {
			Document document = SAXReaderUtil.read(inputStream);

			Element rootElement = document.getRootElement();

			for (Element listenerElement : rootElement.elements("listener")) {
				String listenerClassName = listenerElement.elementText(
					"listener-class");

				if (Validator.isNull(listenerClassName) ||
					listenerClassNames.contains(listenerClassName)) {

					continue;
				}

				listenerClassNames.add(listenerClassName);
			}
		}
		catch (Exception exception) {
			servletContext.log(exception.getMessage(), exception);
		}
	}

	private static final String _ANALYZED_TLDS =
		JSPTaglibHelperUtil.class.getName() + "#ANALYZED_TLDS";

}