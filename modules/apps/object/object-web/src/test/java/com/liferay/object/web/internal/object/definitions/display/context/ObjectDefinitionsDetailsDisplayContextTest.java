/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.display.context;

import com.liferay.application.list.BasePanelCategory;
import com.liferay.application.list.PanelCategory;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Gabriel Prates
 */
public class ObjectDefinitionsDetailsDisplayContextTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_frameworkUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_frameworkUtilMockedStatic.when(
			() -> FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			bundleContext.getBundle()
		);

		_registerPanelCategory(
			bundleContext, _EMPTY_ROOT_PANEL_CATEGORY_KEY, 0, null);
		_registerPanelCategory(
			bundleContext, _OTHER_PANEL_CATEGORY_KEY, 200,
			_ROOT_PANEL_CATEGORY_KEY);
		_registerPanelCategory(
			bundleContext, _PANEL_CATEGORY_KEY, 100, _ROOT_PANEL_CATEGORY_KEY);
		_registerPanelCategory(
			bundleContext, _ROOT_PANEL_CATEGORY_KEY, 0, null);

		ObjectScopeProvider objectScopeProvider = Mockito.mock(
			ObjectScopeProvider.class);

		Mockito.when(
			objectScopeProvider.getRootPanelCategoryKeys()
		).thenReturn(
			new String[] {
				_EMPTY_ROOT_PANEL_CATEGORY_KEY, _ROOT_PANEL_CATEGORY_KEY
			}
		);

		Mockito.when(
			_objectScopeProviderRegistry.getObjectScopeProvider(_SCOPE)
		).thenReturn(
			objectScopeProvider
		);
	}

	@After
	public void tearDown() {
		for (ServiceRegistration<PanelCategory> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	@Test
	@TestInfo("LPD-103873")
	public void testGetScopeJSONArray() {
		ObjectDefinitionsDetailsDisplayContext
			objectDefinitionsDetailsDisplayContext =
				new ObjectDefinitionsDetailsDisplayContext(
					Mockito.mock(ConfigurationProvider.class),
					_getHttpServletRequest(),
					Mockito.mock(ModelResourcePermission.class),
					Mockito.mock(ObjectEntryManagerRegistry.class),
					Mockito.mock(ObjectFolderLocalService.class),
					_objectScopeProviderRegistry);

		JSONArray jsonArray =
			objectDefinitionsDetailsDisplayContext.getScopeJSONArray(_SCOPE);

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(
			_getLabel(_ROOT_PANEL_CATEGORY_KEY), jsonObject.getString("label"));

		Map<String, String> panelCategoryLabels = new HashMap<>();
		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		for (int i = 0; i < itemsJSONArray.length(); i++) {
			JSONObject itemJSONObject = itemsJSONArray.getJSONObject(i);

			panelCategoryLabels.put(
				itemJSONObject.getString("value"),
				itemJSONObject.getString("label"));
		}

		Assert.assertEquals(
			panelCategoryLabels.toString(), 2, panelCategoryLabels.size());
		Assert.assertEquals(
			_getLabel(_OTHER_PANEL_CATEGORY_KEY),
			panelCategoryLabels.get(_OTHER_PANEL_CATEGORY_KEY));
		Assert.assertEquals(
			_getLabel(_PANEL_CATEGORY_KEY),
			panelCategoryLabels.get(_PANEL_CATEGORY_KEY));
	}

	private static String _getLabel(String panelCategoryKey) {
		return panelCategoryKey + "_label";
	}

	private HttpServletRequest _getHttpServletRequest() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLocale(LocaleUtil.US);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return httpServletRequest;
	}

	private void _registerPanelCategory(
		BundleContext bundleContext, String panelCategoryKey,
		int panelCategoryOrder, String parentPanelCategoryKey) {

		Dictionary<String, Object> properties = null;

		if (parentPanelCategoryKey != null) {
			properties = HashMapDictionaryBuilder.<String, Object>put(
				"panel.category.key", parentPanelCategoryKey
			).put(
				"panel.category.order", panelCategoryOrder
			).build();
		}

		_serviceRegistrations.add(
			bundleContext.registerService(
				PanelCategory.class, new TestPanelCategory(panelCategoryKey),
				properties));
	}

	private static final String _EMPTY_ROOT_PANEL_CATEGORY_KEY =
		RandomTestUtil.randomString();

	private static final String _OTHER_PANEL_CATEGORY_KEY =
		RandomTestUtil.randomString();

	private static final String _PANEL_CATEGORY_KEY =
		RandomTestUtil.randomString();

	private static final String _ROOT_PANEL_CATEGORY_KEY =
		RandomTestUtil.randomString();

	private static final String _SCOPE = RandomTestUtil.randomString();

	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);

	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry =
		Mockito.mock(ObjectScopeProviderRegistry.class);
	private final List<ServiceRegistration<PanelCategory>>
		_serviceRegistrations = new ArrayList<>();

	private static class TestPanelCategory extends BasePanelCategory {

		public TestPanelCategory(String key) {
			_key = key;
		}

		@Override
		public String getKey() {
			return _key;
		}

		@Override
		public String getLabel(Locale locale) {
			return _getLabel(_key);
		}

		private final String _key;

	}

}