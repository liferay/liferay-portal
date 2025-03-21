/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.resource.actions.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.ClassLoaderPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletBag;
import com.liferay.portal.kernel.portlet.PortletBagPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.Portlet;

import java.io.InputStream;

import java.net.URL;

import java.util.Dictionary;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class ResourceActionsDefinitionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testResourceActions() throws Exception {
		Bundle currentBundle = FrameworkUtil.getBundle(
			ResourceActionsDefinitionTest.class);

		BundleContext bundleContext = currentBundle.getBundleContext();

		StringBundler sb = new StringBundler();

		for (Bundle bundle : bundleContext.getBundles()) {
			Dictionary<String, String> headers = bundle.getHeaders("");

			if (Objects.equals(headers.get("Eclipse-BuddyPolicy"), "parent")) {
				continue;
			}

			BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

			ClassLoader bundleClassLoader = bundleWiring.getClassLoader();

			if (bundleClassLoader == null) {
				continue;
			}

			URL url = bundleClassLoader.getResource("portlet.properties");

			if (url == null) {
				url = bundleClassLoader.getResource("portal.properties");
			}

			if (url == null) {
				continue;
			}

			Properties properties = PropertiesUtil.load(url);

			for (String resourceActionConfig :
					StringUtil.split(
						properties.getProperty(
							PropsKeys.RESOURCE_ACTIONS_CONFIGS))) {

				_collectResourceActionsError(
					resourceActionConfig, bundle, bundleClassLoader, sb);
			}
		}

		String errors = sb.toString();

		Assert.assertTrue(
			"The following resource action issues are found:".concat(errors),
			errors.isEmpty());
	}

	private void _collectResourceActionsError(
			String resourceActionConfig, Bundle bundle,
			ClassLoader bundleClassLoader, StringBundler sb)
		throws Exception {

		if (bundle.getBundleId() != 0) {
			int index = resourceActionConfig.indexOf("resource-actions/");

			if (index != 0) {
				sb.append("\n\t\t");
				sb.append(bundle.getSymbolicName());
				sb.append(": resource action definition file should be in ");
				sb.append(resourceActionConfig.substring(index));
				sb.append(" rather than ");
				sb.append(resourceActionConfig);

				return;
			}
		}

		URL url = bundleClassLoader.getResource(resourceActionConfig);

		if (url == null) {
			if (bundle.getBundleId() != 0) {
				sb.append("\n\t\t");
				sb.append(bundle.getSymbolicName());
				sb.append(": resource action definition file ");
				sb.append(resourceActionConfig);
				sb.append(" is not found.");
			}

			return;
		}

		Document document = null;

		try (InputStream inputStream = url.openStream()) {
			document = UnsecureSAXReaderUtil.read(inputStream, false);
		}

		Element rootElement = document.getRootElement();

		for (Element resourceElement : rootElement.elements("resource")) {
			_collectResourceActionsError(
				StringUtil.trim(resourceElement.attributeValue("file")), bundle,
				bundleClassLoader, sb);
		}

		_collectResourceActionsErrorForModelResources(rootElement, bundle, sb);

		_collectResourceActionsErrorForPortletResources(
			rootElement, bundle, bundleClassLoader, sb);
	}

	private void _collectResourceActionsErrorForModelResources(
		Element rootElement, Bundle bundle, StringBundler sb) {

		if (bundle.getBundleId() == 0) {
			return;
		}

		List<Element> modelResourceElements = rootElement.elements(
			"model-resource");

		if (modelResourceElements.isEmpty()) {
			return;
		}

		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		if (headers.get("Liferay-Service") == null) {
			sb.append("\n\t\tModel resources are defined in bundle ");
			sb.append(bundle.getSymbolicName());
			sb.append(" which is not liferay service bundle");
		}
	}

	private void _collectResourceActionsErrorForPortletResources(
		Element rootElement, Bundle bundle, ClassLoader bundleClassLoader,
		StringBundler sb) {

		for (Element portletResourceElement :
				rootElement.elements("portlet-resource")) {

			Element portletNameElement = portletResourceElement.element(
				"portlet-name");

			String portletName = portletNameElement.getTextTrim();

			PortletBag portletBag = PortletBagPool.get(portletName);

			if (portletBag == null) {
				sb.append("\n\t\tPortlet resource ");
				sb.append(portletName);
				sb.append(" is defined in bundle ");
				sb.append(bundle.getSymbolicName());
				sb.append(", but the corresponding portlet does not exist");

				continue;
			}

			Portlet portlet = portletBag.getPortletInstance();

			Class<?> clazz = portlet.getClass();

			ClassLoader classLoader = clazz.getClassLoader();

			if ((bundle.getBundleId() == 0) &&
				(classLoader == PortalClassLoaderUtil.getClassLoader())) {

				return;
			}

			if (classLoader != bundleClassLoader) {
				sb.append("\n\t\tPortlet resource ");
				sb.append(portletName);
				sb.append(" is defined in bundle ");
				sb.append(bundle.getSymbolicName());
				sb.append(
					", but the corresponding portlet is in another bundle ");
				sb.append(
					ClassLoaderPool.getContextName(clazz.getClassLoader()));
			}
		}
	}

}