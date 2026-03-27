/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.deploy.hot.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.deploy.hot.PortletHotDeployListener;
import com.liferay.portal.kernel.deploy.hot.HotDeployEvent;
import com.liferay.portal.kernel.deploy.hot.HotDeployUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletCategory;
import com.liferay.portal.kernel.servlet.ServletContextClassLoaderPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.WebAppPool;

import jakarta.servlet.ServletContext;

import java.io.InputStream;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockServletContext;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class PortletHotDeployListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testPortletCategoryNames() throws Exception {
		_testPortletCategoryNames(
			Collections.singletonMap(
				_PORTLET_NAME_PREFIX + RandomTestUtil.randomString(), null));
		_testPortletCategoryNames(
			HashMapBuilder.put(
				_PORTLET_NAME_PREFIX + RandomTestUtil.randomString(),
				_CATEGORY_NAME_PREFIX + RandomTestUtil.randomString()
			).put(
				_PORTLET_NAME_PREFIX + RandomTestUtil.randomString(),
				StringBundler.concat(
					_CATEGORY_NAME_PREFIX, RandomTestUtil.randomString(),
					StringPool.DOUBLE_SLASH, _CATEGORY_NAME_PREFIX,
					RandomTestUtil.randomString())
			).build());
	}

	private String _createLiferayDisplayXML(Map<String, String> categoryNames) {
		boolean createLiferayDisplayXML = false;

		for (Object value : categoryNames.values()) {
			if (value != null) {
				createLiferayDisplayXML = true;

				break;
			}
		}

		if (!createLiferayDisplayXML) {
			return null;
		}

		StringBundler sb = new StringBundler();

		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<!DOCTYPE display PUBLIC ");
		sb.append("\"-//Liferay//DTD Display 7.4.0//EN\" ");
		sb.append("\"http://www.liferay.com/dtd/liferay-display_7_4_0.dtd\">");
		sb.append("<display>");

		for (Map.Entry<String, String> entry : categoryNames.entrySet()) {
			sb.append("<category name=\"");
			sb.append(entry.getValue());
			sb.append("\"><portlet id=\"");
			sb.append(entry.getKey());
			sb.append("\" /></category>");
		}

		sb.append("</display>");

		return sb.toString();
	}

	private String _createPortletXML(Set<String> portletNames) {
		StringBundler sb = new StringBundler(15);

		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<portlet-app version=\"3.0\" ");
		sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
		sb.append("xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/portlet ");
		sb.append("http://xmlns.jcp.org/xml/ns/portlet/portlet-app_3_0.xsd\">");

		for (String portletName : portletNames) {
			sb.append("<portlet><portlet-name>");
			sb.append(portletName);
			sb.append("</portlet-name><display-name>");
			sb.append(portletName);
			sb.append("</display-name><portlet-class>");
			sb.append("com.liferay.portal.kernel.portlet.LiferayPortlet");
			sb.append("</portlet-class><supports><mime-type>text/html");
			sb.append("</mime-type></supports><portlet-info><title>");
			sb.append(portletName);
			sb.append("</title></portlet-info></portlet>");
		}

		sb.append("</portlet-app>");

		return sb.toString();
	}

	private ServletContext _createServletContext(
		String liferayDisplayXML, String portletXML) {

		String servletContextName = RandomTestUtil.randomString();

		return new MockServletContext() {

			@Override
			public ClassLoader getClassLoader() {
				return AggregateClassLoader.getAggregateClassLoader(
					PortletHotDeployListenerTest.class.getClassLoader(),
					PortalClassLoaderUtil.getClassLoader());
			}

			@Override
			public InputStream getResourceAsStream(String path) {
				if (path.equals(
						"/WEB-INF/" + Portal.PORTLET_XML_FILE_NAME_STANDARD)) {

					return new UnsyncByteArrayInputStream(
						portletXML.getBytes());
				}
				else if (path.equals("/WEB-INF/liferay-display.xml")) {
					if (liferayDisplayXML == null) {
						return null;
					}

					return new UnsyncByteArrayInputStream(
						liferayDisplayXML.getBytes());
				}
				else if (path.equals("/WEB-INF/web.xml")) {
					StringBundler sb = new StringBundler(9);

					sb.append("<?xml version=\"1.0\"?>");
					sb.append("<web-app version=\"3.0\" ");
					sb.append("xmlns=\"http://java.sun.com/xml/ns/javaee\" ");
					sb.append("xmlns:xsi=\"");
					sb.append("http://www.w3.org/2001/XMLSchema-instance\" ");
					sb.append("xsi:schemaLocation=");
					sb.append("\"http://java.sun.com/xml/ns/javaee ");
					sb.append("http://java.sun.com/xml/ns/javaee/web-app_3_0.");
					sb.append("xsd\"></web-app>");

					String webXML = sb.toString();

					return new UnsyncByteArrayInputStream(webXML.getBytes());
				}

				return null;
			}

			public String getServletContextName() {
				return servletContextName;
			}

		};
	}

	private void _testPortletCategoryNames(
			Map<String, String> portletCategoryNames)
		throws Exception {

		Set<String> portletNames = portletCategoryNames.keySet();

		ServletContext servletContext = _createServletContext(
			_createLiferayDisplayXML(portletCategoryNames),
			_createPortletXML(portletNames));

		ServletContextClassLoaderPool.register(
			servletContext.getServletContextName(),
			servletContext.getClassLoader());

		HotDeployEvent hotDeployEvent = new HotDeployEvent(servletContext);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.plugin.PluginPackageUtil",
				LoggerTestUtil.ERROR)) {

			HotDeployUtil.fireDeployEvent(hotDeployEvent);

			Map<String, List<Portlet>> portlets =
				ReflectionTestUtil.getFieldValue(
					PortletHotDeployListener.class, "_portlets");

			List<Portlet> portletList = portlets.get(
				servletContext.getServletContextName());

			Assert.assertEquals(
				portletList.toString(), portletCategoryNames.size(),
				portletList.size());

			PortletCategory rootPortletCategory =
				(PortletCategory)WebAppPool.get(
					TestPropsValues.getCompanyId(), WebKeys.PORTLET_CATEGORY);

			for (Portlet portlet : portletList) {
				Assert.assertTrue(
					portletNames.contains(portlet.getPortletName()));

				String categoryName = portletCategoryNames.get(
					portlet.getPortletName());

				Set<String> categoryNames = portlet.getCategoryNames();

				if (categoryName == null) {
					Assert.assertEquals(
						categoryNames.toString(),
						Collections.singleton("category.undefined"),
						categoryNames);

					PortletCategory portletCategory =
						rootPortletCategory.getCategory("category.undefined");

					Assert.assertEquals(
						"category.undefined", portletCategory.getName());
					Assert.assertEquals(
						"root//category.undefined", portletCategory.getPath());

					Set<String> portletIds = portletCategory.getPortletIds();

					Assert.assertTrue(
						portletIds.toString(),
						portletIds.contains(portlet.getPortletId()));
				}
				else {
					Assert.assertEquals(
						categoryNames.toString(),
						Collections.singleton(categoryName), categoryNames);

					String[] nestedCategoryNames = StringUtil.split(
						categoryName, StringPool.DOUBLE_SLASH);

					PortletCategory portletCategory = rootPortletCategory;

					StringBundler sb = new StringBundler();

					sb.append("root");

					for (String nestedCategoryName : nestedCategoryNames) {
						portletCategory = portletCategory.getCategory(
							nestedCategoryName);

						Assert.assertEquals(
							nestedCategoryName, portletCategory.getName());

						sb.append(StringPool.DOUBLE_SLASH);
						sb.append(nestedCategoryName);

						Assert.assertEquals(
							sb.toString(), portletCategory.getPath());
					}

					Set<String> portletIds = portletCategory.getPortletIds();

					Assert.assertTrue(
						portletIds.toString(),
						portletIds.contains(portlet.getPortletId()));
				}
			}
		}
		finally {
			HotDeployUtil.fireUndeployEvent(hotDeployEvent);
		}
	}

	private static final String _CATEGORY_NAME_PREFIX = "Category_";

	private static final String _PORTLET_NAME_PREFIX = "Portlet_";

}