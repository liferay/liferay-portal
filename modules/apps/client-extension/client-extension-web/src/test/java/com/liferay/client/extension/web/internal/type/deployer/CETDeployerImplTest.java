/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.web.internal.type.deployer;

import com.liferay.application.list.PanelApp;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.type.CustomElementCET;
import com.liferay.client.extension.type.JSImportMapsEntryCET;
import com.liferay.frontend.js.importmaps.extender.JSImportMapsContributor;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;

/**
 * @author Iván Zaera Avellón
 */
public class CETDeployerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDeployCustomElementCET() {
		List<Dictionary<String, Object>> dictionaries = new ArrayList<>();

		BundleContext bundleContext = (BundleContext)ProxyUtil.newProxyInstance(
			BundleContext.class.getClassLoader(),
			new Class<?>[] {BundleContext.class},
			(proxy, method, arguments) -> {
				if (arguments[0] == PanelApp.class) {
					dictionaries.add((Dictionary<String, Object>)arguments[2]);
				}

				return null;
			});

		CETDeployerImpl cetDeployerImpl = new CETDeployerImpl();

		cetDeployerImpl.activate(bundleContext);

		PortalUtil portalUtil = new PortalUtil();

		Portal portal = new PortalImpl();

		ReflectionTestUtil.setFieldValue(cetDeployerImpl, "_portal", portal);

		portalUtil.setPortal(portal);

		CustomElementCET customElementCET =
			(CustomElementCET)ProxyUtil.newProxyInstance(
				CustomElementCET.class.getClassLoader(),
				new Class<?>[] {CustomElementCET.class},
				(proxy, method, arguments) -> {
					String methodName = method.getName();

					if (methodName.equals("getPanelAppOrder")) {
						return "300";
					}

					if (methodName.equals("getPanelCategoryKey")) {
						return RandomTestUtil.randomString();
					}

					if (methodName.equals("getType")) {
						return ClientExtensionEntryConstants.
							TYPE_CUSTOM_ELEMENT;
					}

					Class<?> returnType = method.getReturnType();

					if (returnType == boolean.class) {
						return false;
					}

					if (returnType == long.class) {
						return 0L;
					}

					if (returnType == String.class) {
						return "";
					}

					return null;
				});

		cetDeployerImpl.deploy(customElementCET);

		Assert.assertEquals(dictionaries.toString(), 1, dictionaries.size());

		Dictionary<String, Object> dictionary = dictionaries.get(0);

		Assert.assertEquals(
			dictionary.toString(), 300, dictionary.get("panel.app.order"));
	}

	@Test
	public void testDeployJSImportMapsEntryCET() {
		CETDeployerImpl cetDeployerImpl = new CETDeployerImpl();

		BundleContext bundleContext = Mockito.spy(BundleContext.class);

		cetDeployerImpl.activate(bundleContext);

		ReflectionTestUtil.setFieldValue(
			cetDeployerImpl, "_jsonFactory", new JSONFactoryImpl());

		JSImportMapsEntryCET jsImportMapsEntryCET = Mockito.mock(
			JSImportMapsEntryCET.class);

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			jsImportMapsEntryCET.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			jsImportMapsEntryCET.getType()
		).thenReturn(
			ClientExtensionEntryConstants.TYPE_JS_IMPORT_MAPS_ENTRY
		);

		cetDeployerImpl.deploy(jsImportMapsEntryCET);

		Mockito.verify(
			bundleContext
		).registerService(
			Mockito.eq(JSImportMapsContributor.class),
			Mockito.isA(JSImportMapsContributor.class),
			Mockito.argThat(
				dictionary -> Objects.equals(
					dictionary.get(
						"com.liferay.frontend.js.importmaps.company.id"),
					companyId))
		);
	}

}