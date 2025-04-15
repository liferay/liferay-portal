/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.portlet.action;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherPortletInstanceConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class AssetPublisherConfigurationActionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_groupLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testUpdateAssetListEntryPreferencesWithDifferentGroup()
		throws Exception {

		AssetListEntry assetListEntry = _getAssetListEntry();

		Group group = _getGroup(assetListEntry.getGroupId());

		AssetPublisherConfigurationAction assetPublisherConfigurationAction =
			_getAssetPublisherConfigurationAction(assetListEntry, group);

		Map<String, String[]> portletPreferencesMap = new HashMap<>();
		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);

		assetPublisherConfigurationAction.updateAssetListEntryPreferences(
			_getMockActionRequest(
				assetListEntry.getExternalReferenceCode(),
				assetListEntry.getAssetListEntryId(),
				group.getExternalReferenceCode(), StringPool.BLANK,
				portletPreferencesMap,
				_getThemeDisplay(RandomTestUtil.randomLong())),
			portletPreferences);

		Mockito.verify(
			assetPublisherConfigurationAction.assetListEntryLocalService
		).fetchAssetListEntry(
			assetListEntry.getAssetListEntryId()
		);

		Mockito.verify(
			assetPublisherConfigurationAction.groupLocalService
		).getGroup(
			assetListEntry.getGroupId()
		);

		Assert.assertArrayEquals(
			MapUtil.toString(portletPreferencesMap),
			new String[] {assetListEntry.getExternalReferenceCode()},
			portletPreferencesMap.get("assetListEntryExternalReferenceCode"));
		Assert.assertArrayEquals(
			MapUtil.toString(portletPreferencesMap),
			new String[] {group.getExternalReferenceCode()},
			portletPreferencesMap.get(
				"assetListEntryGroupExternalReferenceCode"));
	}

	@Test
	public void testUpdateAssetListEntryPreferencesWithNoSelection()
		throws Exception {

		AssetPublisherConfigurationAction assetPublisherConfigurationAction =
			_getAssetPublisherConfigurationAction(null, null);

		Map<String, String[]> portletPreferencesMap = new HashMap<>();
		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);

		assetPublisherConfigurationAction.updateAssetListEntryPreferences(
			_getMockActionRequest(
				StringPool.BLANK, 0, StringPool.BLANK, StringPool.BLANK,
				portletPreferencesMap,
				_getThemeDisplay(RandomTestUtil.randomLong())),
			portletPreferences);

		Mockito.verify(
			assetPublisherConfigurationAction.assetListEntryLocalService
		).fetchAssetListEntry(
			0
		);

		Mockito.verifyNoInteractions(
			assetPublisherConfigurationAction.groupLocalService);
		Mockito.verify(
			portletPreferences
		).reset(
			"assetListEntryExternalReferenceCode"
		);

		Mockito.verify(
			portletPreferences
		).reset(
			"assetListEntryGroupExternalReferenceCode"
		);

		Assert.assertTrue(
			MapUtil.toString(portletPreferencesMap),
			MapUtil.isEmpty(portletPreferencesMap));
	}

	@Test
	public void testUpdateAssetListEntryPreferencesWithSameGroup()
		throws Exception {

		AssetListEntry assetListEntry = _getAssetListEntry();

		Group group = _getGroup(assetListEntry.getGroupId());

		AssetPublisherConfigurationAction assetPublisherConfigurationAction =
			_getAssetPublisherConfigurationAction(assetListEntry, group);

		Map<String, String[]> portletPreferencesMap = new HashMap<>();
		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);

		assetPublisherConfigurationAction.updateAssetListEntryPreferences(
			_getMockActionRequest(
				assetListEntry.getExternalReferenceCode(),
				assetListEntry.getAssetListEntryId(),
				group.getExternalReferenceCode(), StringPool.BLANK,
				portletPreferencesMap,
				_getThemeDisplay(assetListEntry.getGroupId())),
			portletPreferences);

		Mockito.verifyNoInteractions(
			assetPublisherConfigurationAction.groupLocalService);

		Mockito.verify(
			portletPreferences
		).reset(
			"assetListEntryGroupExternalReferenceCode"
		);

		Assert.assertArrayEquals(
			MapUtil.toString(portletPreferencesMap),
			new String[] {assetListEntry.getExternalReferenceCode()},
			portletPreferencesMap.get("assetListEntryExternalReferenceCode"));
		Assert.assertFalse(
			MapUtil.toString(portletPreferencesMap),
			portletPreferencesMap.containsKey(
				"assetListEntryGroupExternalReferenceCode"));
	}

	@Test
	public void testUpdateDisplayStyleGroupPreferencesWithDifferentGroup()
		throws Exception {

		Group group = _getGroup(RandomTestUtil.randomLong());

		_setUpGroupLocalServiceUtil(group);

		AssetPublisherConfigurationAction assetPublisherConfigurationAction =
			_getAssetPublisherConfigurationAction(null, null);

		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);

		assetPublisherConfigurationAction.postProcess(
			0L,
			_getMockActionRequest(
				StringPool.BLANK, 0, StringPool.BLANK, group.getGroupKey(),
				new HashMap<>(), _getThemeDisplay(RandomTestUtil.randomLong())),
			portletPreferences);

		_groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroup(0, group.getGroupKey()));

		Mockito.verify(
			portletPreferences
		).setValue(
			"displayStyleGroupExternalReferenceCode",
			group.getExternalReferenceCode()
		);
	}

	@Test
	public void testUpdateDisplayStyleGroupPreferencesWithNoSelection()
		throws Exception {

		_setUpGroupLocalServiceUtil(null);

		AssetPublisherConfigurationAction assetPublisherConfigurationAction =
			_getAssetPublisherConfigurationAction(null, null);

		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);

		assetPublisherConfigurationAction.postProcess(
			0L,
			_getMockActionRequest(
				StringPool.BLANK, 0, StringPool.BLANK, StringPool.BLANK,
				new HashMap<>(), _getThemeDisplay(RandomTestUtil.randomLong())),
			portletPreferences);

		_groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroup(0, StringPool.BLANK));

		Mockito.verify(
			portletPreferences
		).reset(
			"displayStyleGroupExternalReferenceCode"
		);
	}

	@Test
	public void testUpdateDisplayStyleGroupPreferencesWithSameGroup()
		throws Exception {

		Group group = _getGroup(RandomTestUtil.randomLong());

		_setUpGroupLocalServiceUtil(group);

		AssetPublisherConfigurationAction assetPublisherConfigurationAction =
			_getAssetPublisherConfigurationAction(null, null);

		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);

		assetPublisherConfigurationAction.postProcess(
			0L,
			_getMockActionRequest(
				StringPool.BLANK, 0, StringPool.BLANK, group.getGroupKey(),
				new HashMap<>(), _getThemeDisplay(group.getGroupId())),
			portletPreferences);

		_groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroup(0, group.getGroupKey()));

		Mockito.verify(
			portletPreferences
		).reset(
			"displayStyleGroupExternalReferenceCode"
		);

		Mockito.verify(
			portletPreferences
		).reset(
			"displayStyleGroupExternalReferenceCode"
		);
	}

	private AssetListEntry _getAssetListEntry() {
		AssetListEntry assetListEntry = Mockito.mock(AssetListEntry.class);

		Mockito.when(
			assetListEntry.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			assetListEntry.getAssetListEntryId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			assetListEntry.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		return assetListEntry;
	}

	private AssetListEntryLocalService _getAssetListEntryLocalService(
		AssetListEntry assetListEntry) {

		AssetListEntryLocalService assetListEntryLocalService = Mockito.mock(
			AssetListEntryLocalService.class);

		if (assetListEntry == null) {
			Mockito.when(
				assetListEntryLocalService.fetchAssetListEntry(
					Mockito.anyLong())
			).thenReturn(
				null
			);
		}
		else {
			Mockito.when(
				assetListEntryLocalService.fetchAssetListEntry(
					assetListEntry.getAssetListEntryId())
			).thenReturn(
				assetListEntry
			);
		}

		return assetListEntryLocalService;
	}

	private AssetPublisherConfigurationAction
			_getAssetPublisherConfigurationAction(
				AssetListEntry assetListEntry, Group group)
		throws Exception {

		AssetPublisherConfigurationAction assetPublisherConfigurationAction =
			new AssetPublisherConfigurationAction();

		assetPublisherConfigurationAction.assetListEntryLocalService =
			_getAssetListEntryLocalService(assetListEntry);
		assetPublisherConfigurationAction.configurationProvider =
			_getConfigurationProvider();
		assetPublisherConfigurationAction.groupLocalService =
			_getGroupLocalService(group);

		return assetPublisherConfigurationAction;
	}

	private ConfigurationProvider _getConfigurationProvider() throws Exception {
		ConfigurationProvider configurationProvider = Mockito.mock(
			ConfigurationProvider.class);

		AssetPublisherPortletInstanceConfiguration
			assetPublisherPortletInstanceConfiguration = Mockito.mock(
				AssetPublisherPortletInstanceConfiguration.class);

		LocalizedValuesMap localizedValuesMap = Mockito.mock(
			LocalizedValuesMap.class);

		Mockito.when(
			localizedValuesMap.get(Mockito.any(Locale.class))
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			assetPublisherPortletInstanceConfiguration.
				emailAssetEntryAddedBody()
		).thenReturn(
			localizedValuesMap
		);

		Mockito.when(
			assetPublisherPortletInstanceConfiguration.
				emailAssetEntryAddedSubject()
		).thenReturn(
			localizedValuesMap
		);

		Mockito.when(
			configurationProvider.getSystemConfiguration(
				AssetPublisherPortletInstanceConfiguration.class)
		).thenReturn(
			assetPublisherPortletInstanceConfiguration
		);

		return configurationProvider;
	}

	private Group _getGroup(long groupId) {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			group.getGroupKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return group;
	}

	private GroupLocalService _getGroupLocalService(Group group)
		throws Exception {

		GroupLocalService groupLocalService = Mockito.mock(
			GroupLocalService.class);

		if (group == null) {
			Mockito.when(
				groupLocalService.fetchGroup(
					Mockito.anyLong(), Mockito.anyString())
			).thenReturn(
				null
			);

			Mockito.when(
				groupLocalService.getGroup(Mockito.anyLong())
			).thenReturn(
				null
			);
		}
		else {
			Mockito.when(
				groupLocalService.fetchGroup(0, group.getGroupKey())
			).thenReturn(
				group
			);

			Mockito.when(
				groupLocalService.getGroup(group.getGroupId())
			).thenReturn(
				group
			);
		}

		return groupLocalService;
	}

	private MockActionRequest _getMockActionRequest(
		String assetListEntryExternalReferenceCode, long assetListEntryId,
		String assetListEntryGroupExternalReferenceCode,
		String displayStyleGroupKey,
		Map<String, String[]> portletPreferencesMap,
		ThemeDisplay themeDisplay) {

		MockActionRequest mockActionRequest = new MockActionRequest();

		mockActionRequest.setAttribute(
			WebKeys.PORTLET_PREFERENCES_MAP, portletPreferencesMap);
		mockActionRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);
		mockActionRequest.setParameter(
			"preferences--assetListEntryExternalReferenceCode--",
			assetListEntryExternalReferenceCode);
		mockActionRequest.setParameter(
			"preferences--assetListEntryId--",
			String.valueOf(assetListEntryId));
		mockActionRequest.setParameter(
			"preferences--assetListEntryGroupExternalReferenceCode--",
			assetListEntryGroupExternalReferenceCode);
		mockActionRequest.setParameter(
			"preferences--displayStyleGroupKey--", displayStyleGroupKey);

		return mockActionRequest;
	}

	private ThemeDisplay _getThemeDisplay(long groupId) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(Mockito.mock(Company.class));
		themeDisplay.setScopeGroupId(groupId);

		return themeDisplay;
	}

	private void _setUpGroupLocalServiceUtil(Group group) throws Exception {
		_groupLocalServiceUtilMockedStatic.reset();

		if (group == null) {
			_groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.fetchGroup(
					Mockito.anyLong(), Mockito.anyString())
			).thenReturn(
				null
			);

			_groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.getGroup(Mockito.anyLong())
			).thenReturn(
				null
			);
		}
		else {
			_groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.fetchGroup(0L, group.getGroupKey())
			).thenReturn(
				group
			);

			_groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.getGroup(group.getGroupId())
			).thenReturn(
				group
			);
		}
	}

	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);

}