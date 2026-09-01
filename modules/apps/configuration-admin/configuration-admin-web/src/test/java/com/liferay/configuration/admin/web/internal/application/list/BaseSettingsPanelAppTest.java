/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.application.list;

import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.configuration.admin.web.internal.display.ConfigurationCategoryDisplay;
import com.liferay.configuration.admin.web.internal.display.ConfigurationCategoryMenuDisplay;
import com.liferay.configuration.admin.web.internal.display.ConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.ConfigurationScopeDisplay;
import com.liferay.configuration.admin.web.internal.util.ConfigurationEntryRetriever;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mario Leandro
 */
public class BaseSettingsPanelAppTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_baseSettingsPanelApp, "configurationEntryRetriever",
			_configurationEntryRetriever);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		Mockito.when(
			_themeDisplay.getLanguageId()
		).thenReturn(
			LocaleUtil.toLanguageId(LocaleUtil.US)
		);

		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		_setUpConfigurationEntryRetriever();
	}

	@Test
	public void testGetPanelAppNavigationItems() throws Exception {
		List<PanelAppNavigationItem> panelAppNavigationItems =
			_baseSettingsPanelApp.getPanelAppNavigationItems(
				_httpServletRequest);

		Assert.assertEquals(
			panelAppNavigationItems.toString(), 2,
			panelAppNavigationItems.size());

		PanelAppNavigationItem panelAppNavigationItem =
			panelAppNavigationItems.get(0);

		Assert.assertEquals("Factory", panelAppNavigationItem.getLabel());

		String href = panelAppNavigationItem.getHref();

		Assert.assertTrue(href, href.contains("factoryPid=factoryPidValue"));

		panelAppNavigationItem = panelAppNavigationItems.get(1);

		Assert.assertEquals("Screen", panelAppNavigationItem.getLabel());

		href = panelAppNavigationItem.getHref();

		Assert.assertTrue(
			href, href.contains("configurationScreenKey=screenKeyValue"));
	}

	@Test
	public void testGetPanelAppNavigationItemsHasNoSharedParameters()
		throws Exception {

		List<PanelAppNavigationItem> panelAppNavigationItems =
			_baseSettingsPanelApp.getPanelAppNavigationItems(
				_httpServletRequest);

		PanelAppNavigationItem panelAppNavigationItem =
			panelAppNavigationItems.get(1);

		String href = panelAppNavigationItem.getHref();

		Assert.assertFalse(href, href.contains("factoryPid=factoryPidValue"));
	}

	@Test
	public void testGetPanelAppNavigationItemsIncludesConfigurationEntries()
		throws Exception {

		ConfigurationEntry betaConfigurationEntry = _getConfigurationEntry(
			"betaKey", "Beta", "factoryPid", "betaPidValue");
		ConfigurationEntry releaseConfigurationEntry = _getConfigurationEntry(
			"releaseKey", "Release", "factoryPid", "releasePidValue");

		_configurationCategoryMenuDisplays.put(
			"factory",
			_getConfigurationCategoryMenuDisplay(
				"Factory", releaseConfigurationEntry, betaConfigurationEntry));

		List<PanelAppNavigationItem> panelAppNavigationItems =
			_baseSettingsPanelApp.getPanelAppNavigationItems(
				_httpServletRequest);

		Assert.assertEquals(
			panelAppNavigationItems.toString(), 4,
			panelAppNavigationItems.size());

		PanelAppNavigationItem panelAppNavigationItem =
			panelAppNavigationItems.get(0);

		Assert.assertEquals("Factory", panelAppNavigationItem.getLabel());
		Assert.assertNull(panelAppNavigationItem.getParentLabel());

		panelAppNavigationItem = panelAppNavigationItems.get(1);

		Assert.assertEquals("Release", panelAppNavigationItem.getLabel());
		Assert.assertEquals("Factory", panelAppNavigationItem.getParentLabel());

		String href = panelAppNavigationItem.getHref();

		Assert.assertTrue(href, href.contains("factoryPid=releasePidValue"));

		panelAppNavigationItem = panelAppNavigationItems.get(2);

		Assert.assertEquals("Beta", panelAppNavigationItem.getLabel());
		Assert.assertEquals("Factory", panelAppNavigationItem.getParentLabel());
		Assert.assertEquals("Beta", panelAppNavigationItem.getCanonicalName());

		panelAppNavigationItem = panelAppNavigationItems.get(3);

		Assert.assertEquals("Screen", panelAppNavigationItem.getLabel());
	}

	@Test
	public void testGetPanelAppNavigationItemsOmitsSingleConfigurationEntryCategories()
		throws Exception {

		List<PanelAppNavigationItem> panelAppNavigationItems =
			_baseSettingsPanelApp.getPanelAppNavigationItems(
				_httpServletRequest);

		Assert.assertEquals(
			panelAppNavigationItems.toString(), 2,
			panelAppNavigationItems.size());

		for (PanelAppNavigationItem panelAppNavigationItem :
				panelAppNavigationItems) {

			Assert.assertNull(
				panelAppNavigationItem.getLabel(),
				panelAppNavigationItem.getParentLabel());
		}
	}

	@Test
	public void testGetPanelAppNavigationItemsSkipsEmptyCategories()
		throws Exception {

		Mockito.when(
			_factoryConfigurationCategoryMenuDisplay.isEmpty()
		).thenReturn(
			true
		);

		List<PanelAppNavigationItem> panelAppNavigationItems =
			_baseSettingsPanelApp.getPanelAppNavigationItems(
				_httpServletRequest);

		Assert.assertEquals(
			panelAppNavigationItems.toString(), 1,
			panelAppNavigationItems.size());
	}

	private ConfigurationCategoryDisplay _getConfigurationCategoryDisplay(
		String categoryLabel) {

		ConfigurationCategoryDisplay configurationCategoryDisplay =
			Mockito.mock(ConfigurationCategoryDisplay.class);

		Mockito.when(
			configurationCategoryDisplay.getCategoryLabel(
				Mockito.any(Locale.class))
		).thenReturn(
			categoryLabel
		);

		return configurationCategoryDisplay;
	}

	private ConfigurationCategoryMenuDisplay
		_getConfigurationCategoryMenuDisplay(
			String categoryLabel, ConfigurationEntry... configurationEntries) {

		ConfigurationCategoryMenuDisplay configurationCategoryMenuDisplay =
			Mockito.mock(ConfigurationCategoryMenuDisplay.class);

		ConfigurationCategoryDisplay configurationCategoryDisplay =
			_getConfigurationCategoryDisplay(categoryLabel);

		Mockito.when(
			configurationCategoryMenuDisplay.getConfigurationCategoryDisplay()
		).thenReturn(
			configurationCategoryDisplay
		);

		ConfigurationScopeDisplay configurationScopeDisplay = Mockito.mock(
			ConfigurationScopeDisplay.class);

		Mockito.when(
			configurationScopeDisplay.getConfigurationEntries()
		).thenReturn(
			Arrays.asList(configurationEntries)
		);

		Mockito.when(
			configurationCategoryMenuDisplay.getConfigurationScopeDisplays()
		).thenReturn(
			Arrays.asList(configurationScopeDisplay)
		);

		Mockito.when(
			configurationCategoryMenuDisplay.getFirstConfigurationEntry()
		).thenReturn(
			configurationEntries[0]
		);

		return configurationCategoryMenuDisplay;
	}

	private ConfigurationEntry _getConfigurationEntry(
		String key, String name, String parameterName, String parameterValue) {

		ConfigurationEntry configurationEntry = Mockito.mock(
			ConfigurationEntry.class);

		Mockito.when(
			configurationEntry.getEditURLParameters()
		).thenReturn(
			HashMapBuilder.put(
				parameterName, parameterValue
			).build()
		);

		Mockito.when(
			configurationEntry.getKey()
		).thenReturn(
			key
		);

		Mockito.when(
			configurationEntry.getName(LocaleUtil.ENGLISH)
		).thenReturn(
			name
		);

		Mockito.when(
			configurationEntry.getName(LocaleUtil.US)
		).thenReturn(
			name
		);

		return configurationEntry;
	}

	private void _setUpConfigurationEntryRetriever() {
		_factoryConfigurationCategoryMenuDisplay =
			_getConfigurationCategoryMenuDisplay(
				"Factory",
				_getConfigurationEntry(
					"factoryKey", "Factory Entry", "factoryPid",
					"factoryPidValue"));

		_configurationCategoryMenuDisplays = LinkedHashMapBuilder.put(
			"factory", _factoryConfigurationCategoryMenuDisplay
		).put(
			"screen",
			_getConfigurationCategoryMenuDisplay(
				"Screen",
				_getConfigurationEntry(
					"screenKey", "Screen Entry", "configurationScreenKey",
					"screenKeyValue"))
		).build();

		Mockito.when(
			_configurationEntryRetriever.getConfigurationCategoryMenuDisplays(
				Mockito.anyString(), Mockito.any(), Mockito.any())
		).thenReturn(
			_configurationCategoryMenuDisplays
		);
	}

	private final BaseSettingsPanelApp _baseSettingsPanelApp =
		new BaseSettingsPanelApp() {

			@Override
			public String getIcon() {
				return "cog";
			}

			@Override
			public Portlet getPortlet() {
				return null;
			}

			@Override
			public String getPortletId() {
				return RandomTestUtil.randomString();
			}

			@Override
			public PortletURL getPortletURL(
				HttpServletRequest httpServletRequest) {

				return new MockLiferayPortletURL();
			}

		};

	private Map<String, ConfigurationCategoryMenuDisplay>
		_configurationCategoryMenuDisplays;
	private final ConfigurationEntryRetriever _configurationEntryRetriever =
		Mockito.mock(ConfigurationEntryRetriever.class);
	private ConfigurationCategoryMenuDisplay
		_factoryConfigurationCategoryMenuDisplay;
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}