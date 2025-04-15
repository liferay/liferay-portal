/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ratings.transformer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.ratings.kernel.definition.PortletRatingsDefinitionUtil;
import com.liferay.ratings.kernel.definition.PortletRatingsDefinitionValues;
import com.liferay.ratings.kernel.transformer.RatingsDataTransformer;
import com.liferay.ratings.kernel.transformer.RatingsDataTransformerUtil;

import jakarta.portlet.PortletPreferences;

import java.util.Map;
import java.util.Objects;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Leon Chi
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class RatingsDataTransformerUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		_portletRatingsDefinitionValuesMap =
			PortletRatingsDefinitionUtil.getPortletRatingsDefinitionValuesMap();

		Bundle bundle = FrameworkUtil.getBundle(
			RatingsDataTransformerUtilTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			RatingsDataTransformer.class,
			(RatingsDataTransformer)ProxyUtil.newProxyInstance(
				RatingsDataTransformer.class.getClassLoader(),
				new Class<?>[] {RatingsDataTransformer.class},
				(proxy, method, args) -> {
					if (Objects.equals(
							method.getName(), "transformRatingsData")) {

						_calledTransformRatingsData = true;
					}

					return null;
				}),
			MapUtil.singletonDictionary("service.ranking", Integer.MAX_VALUE));
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testTransformCompanyRatingsData() throws Exception {
		_calledTransformRatingsData = false;

		RatingsDataTransformerUtil.transformCompanyRatingsData(
			1, _createPortletPreferences("like"),
			_createUnicodeProperties("stars"));

		Assert.assertTrue(_calledTransformRatingsData);
	}

	@Test
	public void testTransformGroupRatingsData() throws Exception {
		_calledTransformRatingsData = false;

		RatingsDataTransformerUtil.transformGroupRatingsData(
			1, _createUnicodeProperties("like"),
			_createUnicodeProperties("stars"));

		Assert.assertTrue(_calledTransformRatingsData);
	}

	private PortletPreferences _createPortletPreferences(String value)
		throws Exception {

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		for (Map.Entry<String, PortletRatingsDefinitionValues> entry :
				_portletRatingsDefinitionValuesMap.entrySet()) {

			String className = entry.getKey();

			portletPreferences.setValue(
				RatingsDataTransformerUtil.getPropertyKey(className), value);
		}

		return portletPreferences;
	}

	private UnicodeProperties _createUnicodeProperties(String value) {
		UnicodeProperties unicodeProperties = new UnicodeProperties();

		for (Map.Entry<String, PortletRatingsDefinitionValues> entry :
				_portletRatingsDefinitionValuesMap.entrySet()) {

			String className = entry.getKey();

			unicodeProperties.put(
				RatingsDataTransformerUtil.getPropertyKey(className), value);
		}

		return unicodeProperties;
	}

	private static boolean _calledTransformRatingsData;
	private static Map<String, PortletRatingsDefinitionValues>
		_portletRatingsDefinitionValuesMap;
	private static ServiceRegistration<RatingsDataTransformer>
		_serviceRegistration;

}