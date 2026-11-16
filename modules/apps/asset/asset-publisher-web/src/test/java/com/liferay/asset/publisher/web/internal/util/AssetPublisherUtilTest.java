/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.util;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalServiceUtil;
import com.liferay.asset.list.service.AssetListEntryServiceUtil;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherSelectionStyleConfigurationUtil;
import com.liferay.asset.publisher.web.internal.constants.AssetPublisherSelectionStyleConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.MockPortletPreferences;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;

import java.util.Collections;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class AssetPublisherUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_assetListEntryLocalServiceUtilMockedStatic.close();
		_assetListEntryServiceUtilMockedStatic.close();
		_assetPublisherSelectionStyleConfigurationUtilMockedStatic.close();
		_groupLocalServiceUtilMockedStatic.close();
		_portalUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_assetListEntryLocalServiceUtilMockedStatic.reset();
		_assetListEntryServiceUtilMockedStatic.reset();
		_groupLocalServiceUtilMockedStatic.reset();
		_portalUtilMockedStatic.reset();

		_assetPublisherSelectionStyleConfigurationUtilMockedStatic.when(
			AssetPublisherSelectionStyleConfigurationUtil::defaultSelectionStyle
		).thenReturn(
			AssetPublisherSelectionStyleConstants.TYPE_ASSET_LIST
		);
	}

	@Test
	public void testGetAssetListEntryWithDifferentScope()
		throws PortalException {

		AssetListEntry assetListEntry = _getAssetListEntry();

		Group group = _getGroup();

		_setUpFetchAssetListEntryByExternalReferenceCode(
			assetListEntry, false, group);

		_assertGetAssetListEntry(
			null,
			_getPortletPreferencesMap(
				assetListEntry.getExternalReferenceCode(),
				assetListEntry.getAssetListEntryId(),
				RandomTestUtil.randomString()));

		Map<String, String> portletPreferencesMap = _getPortletPreferencesMap(
			assetListEntry.getExternalReferenceCode(),
			assetListEntry.getAssetListEntryId(),
			group.getExternalReferenceCode());

		_assertGetAssetListEntry(assetListEntry, false, portletPreferencesMap);
		_assertGetAssetListEntry(null, true, portletPreferencesMap);

		_setUpFetchAssetListEntryByExternalReferenceCode(
			assetListEntry, true, group);

		_assertGetAssetListEntry(null, false, portletPreferencesMap);
		_assertGetAssetListEntry(assetListEntry, true, portletPreferencesMap);
	}

	@Test
	public void testGetAssetListEntryWithNoAssetTypeSelection()
		throws PortalException {

		_assetPublisherSelectionStyleConfigurationUtilMockedStatic.when(
			AssetPublisherSelectionStyleConfigurationUtil::defaultSelectionStyle
		).thenReturn(
			AssetPublisherSelectionStyleConstants.TYPE_DYNAMIC
		);

		AssetListEntry assetListEntry = _getAssetListEntry();

		_setUpFetchAssetListEntryByExternalReferenceCode(
			assetListEntry, false, null);

		_assertGetAssetListEntry(
			null,
			_getPortletPreferencesMap(
				assetListEntry.getExternalReferenceCode(),
				assetListEntry.getAssetListEntryId(), null));
	}

	@Test
	public void testGetAssetListEntryWithNoSelection() throws PortalException {
		_setUpFetchAssetListEntry(_getAssetListEntry(), true);

		_assertGetAssetListEntry(null, Collections.emptyMap());
		_assertGetAssetListEntry(
			null, _getPortletPreferencesMap(RandomTestUtil.randomLong()));
	}

	@Test
	public void testGetAssetListEntryWithSameScope() throws PortalException {
		AssetListEntry assetListEntry = _getAssetListEntry();

		_setUpFetchAssetListEntryByExternalReferenceCode(
			assetListEntry, false, null);

		_assertGetAssetListEntry(
			null,
			_getPortletPreferencesMap(
				assetListEntry.getExternalReferenceCode(),
				assetListEntry.getAssetListEntryId(),
				RandomTestUtil.randomString()));

		Map<String, String> portletPreferencesMap = _getPortletPreferencesMap(
			assetListEntry.getExternalReferenceCode(),
			assetListEntry.getAssetListEntryId(), null);

		_assertGetAssetListEntry(assetListEntry, false, portletPreferencesMap);
		_assertGetAssetListEntry(null, true, portletPreferencesMap);

		_setUpFetchAssetListEntryByExternalReferenceCode(
			assetListEntry, false, null);
	}

	@Test
	public void testGetDisplayStyleGroupIdWithDisplayStyleGroupExternalReferenceCode() {
		Group group = _getGroup();

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				group.getExternalReferenceCode(), _COMPANY_ID)
		).thenReturn(
			group
		);

		Assert.assertEquals(
			group.getGroupId(),
			AssetPublisherUtil.getDisplayStyleGroupId(
				_COMPANY_ID, _GROUP_ID,
				_getMockPortletPreferences(
					HashMapBuilder.put(
						"displayStyleGroupExternalReferenceCode",
						group.getExternalReferenceCode()
					).put(
						"displayStyleGroupId",
						String.valueOf(RandomTestUtil.randomLong())
					).build())));

		_groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				group.getExternalReferenceCode(), _COMPANY_ID));
	}

	@Test
	public void testGetDisplayStyleGroupIdWithMissingDisplayStyleGroupExternalReferenceCode() {
		String displayStyleGroupExternalReferenceCode =
			RandomTestUtil.randomString();

		Assert.assertEquals(
			0,
			AssetPublisherUtil.getDisplayStyleGroupId(
				_COMPANY_ID, _GROUP_ID,
				_getMockPortletPreferences(
					HashMapBuilder.put(
						"displayStyleGroupExternalReferenceCode",
						displayStyleGroupExternalReferenceCode
					).put(
						"displayStyleGroupId",
						String.valueOf(RandomTestUtil.randomLong())
					).build())));

		_groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode, _COMPANY_ID));
	}

	@Test
	public void testGetDisplayStyleGroupIdWithoutDisplayStyleGroupExternalReferenceCode() {
		Assert.assertEquals(
			_GROUP_ID,
			AssetPublisherUtil.getDisplayStyleGroupId(
				_COMPANY_ID, _GROUP_ID,
				_getMockPortletPreferences(
					HashMapBuilder.put(
						"displayStyleGroupId",
						String.valueOf(RandomTestUtil.randomLong())
					).build())));

		_groupLocalServiceUtilMockedStatic.verifyNoInteractions();
	}

	@Test
	public void testGetRedirect() {
		PortletRequest portletRequest = Mockito.mock(PortletRequest.class);

		String url = "/web/guest/" + RandomTestUtil.randomString();

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getCurrentURL(portletRequest)
		).thenReturn(
			StringBundler.concat(
				url, "?x&redirect=%2Fa&_",
				AssetPublisherPortletKeys.ASSET_PUBLISHER, "_INSTANCE_",
				RandomTestUtil.randomString(), "_redirect=%2Fb&_",
				AssetPublisherPortletKeys.RELATED_ASSETS,
				"_redirect=%2Fc&_com_liferay_blogs_web_portlet_BlogsPortlet_",
				"redirect=%2Fd&t=abc==#anchor?redirect=e")
		);

		Assert.assertEquals(
			StringBundler.concat(
				url, "?x&_com_liferay_blogs_web_portlet_BlogsPortlet_",
				"redirect=%2Fd&t=abc==#anchor?redirect=e"),
			AssetPublisherUtil.getRedirect(portletRequest));

		Mockito.when(
			portletRequest.getParameter("redirect")
		).thenReturn(
			url + "?redirect=%2Fa"
		);

		Assert.assertEquals(
			url, AssetPublisherUtil.getRedirect(portletRequest));
	}

	private void _assertGetAssetListEntry(
			AssetListEntry assetListEntry, boolean checkPermissions,
			Map<String, String> portletPreferencesMap)
		throws PortalException {

		Assert.assertEquals(
			assetListEntry,
			AssetPublisherUtil.getAssetListEntry(
				checkPermissions, _COMPANY_ID, _GROUP_ID,
				_getMockPortletPreferences(portletPreferencesMap)));
	}

	private void _assertGetAssetListEntry(
			AssetListEntry assetListEntry,
			Map<String, String> portletPreferencesMap)
		throws PortalException {

		_assertGetAssetListEntry(assetListEntry, false, portletPreferencesMap);
		_assertGetAssetListEntry(assetListEntry, true, portletPreferencesMap);
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

		return assetListEntry;
	}

	private Group _getGroup() {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		return group;
	}

	private MockPortletPreferences _getMockPortletPreferences(
		Map<String, String> portletPreferencesMap) {

		MockPortletPreferences mockPortletPreferences =
			new MockPortletPreferences();

		for (Map.Entry<String, String> entry :
				portletPreferencesMap.entrySet()) {

			mockPortletPreferences.setValue(entry.getKey(), entry.getValue());
		}

		return mockPortletPreferences;
	}

	private Map<String, String> _getPortletPreferencesMap(
		long assetListEntryId) {

		return _getPortletPreferencesMap(
			RandomTestUtil.randomString(), assetListEntryId,
			RandomTestUtil.randomString());
	}

	private Map<String, String> _getPortletPreferencesMap(
		String assetListEntryExternalReferenceCode, long assetListEntryId,
		String assetListEntryGroupExternalReferenceCode) {

		return HashMapBuilder.put(
			"assetListEntryExternalReferenceCode",
			assetListEntryExternalReferenceCode
		).put(
			"assetListEntryGroupExternalReferenceCode",
			assetListEntryGroupExternalReferenceCode
		).put(
			"assetListEntryId", String.valueOf(assetListEntryId)
		).build();
	}

	private void _setUpFetchAssetListEntry(
		AssetListEntry assetListEntry, boolean checkPermissions) {

		if (checkPermissions) {
			_assetListEntryServiceUtilMockedStatic.when(
				() -> AssetListEntryServiceUtil.fetchAssetListEntry(
					assetListEntry.getAssetListEntryId())
			).thenReturn(
				assetListEntry
			);

			_assetListEntryLocalServiceUtilMockedStatic.when(
				() -> AssetListEntryLocalServiceUtil.fetchAssetListEntry(
					Mockito.anyLong())
			).thenReturn(
				null
			);

			return;
		}

		_assetListEntryServiceUtilMockedStatic.when(
			() -> AssetListEntryServiceUtil.fetchAssetListEntry(
				Mockito.anyLong())
		).thenReturn(
			null
		);

		_assetListEntryLocalServiceUtilMockedStatic.when(
			() -> AssetListEntryLocalServiceUtil.fetchAssetListEntry(
				assetListEntry.getAssetListEntryId())
		).thenReturn(
			assetListEntry
		);
	}

	private void _setUpFetchAssetListEntryByExternalReferenceCode(
		AssetListEntry assetListEntry, boolean checkPermissions, Group group) {

		long groupId;

		if (group != null) {
			_groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
					group.getExternalReferenceCode(), _COMPANY_ID)
			).thenReturn(
				group
			);

			groupId = group.getGroupId();
		}
		else {
			_groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
					Mockito.anyString(), Mockito.anyLong())
			).thenReturn(
				null
			);

			groupId = _GROUP_ID;
		}

		if (checkPermissions) {
			_assetListEntryServiceUtilMockedStatic.when(
				() ->
					AssetListEntryServiceUtil.
						fetchAssetListEntryByExternalReferenceCode(
							assetListEntry.getExternalReferenceCode(), groupId)
			).thenReturn(
				assetListEntry
			);

			_assetListEntryLocalServiceUtilMockedStatic.when(
				() ->
					AssetListEntryLocalServiceUtil.
						fetchAssetListEntryByExternalReferenceCode(
							Mockito.anyString(), Mockito.anyLong())
			).thenReturn(
				null
			);

			return;
		}

		_assetListEntryServiceUtilMockedStatic.when(
			() ->
				AssetListEntryServiceUtil.
					fetchAssetListEntryByExternalReferenceCode(
						Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			null
		);

		_assetListEntryLocalServiceUtilMockedStatic.when(
			() ->
				AssetListEntryLocalServiceUtil.
					fetchAssetListEntryByExternalReferenceCode(
						assetListEntry.getExternalReferenceCode(), groupId)
		).thenReturn(
			assetListEntry
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final MockedStatic<AssetListEntryLocalServiceUtil>
		_assetListEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
			AssetListEntryLocalServiceUtil.class);
	private static final MockedStatic<AssetListEntryServiceUtil>
		_assetListEntryServiceUtilMockedStatic = Mockito.mockStatic(
			AssetListEntryServiceUtil.class);
	private static final MockedStatic
		<AssetPublisherSelectionStyleConfigurationUtil>
			_assetPublisherSelectionStyleConfigurationUtilMockedStatic =
				Mockito.mockStatic(
					AssetPublisherSelectionStyleConfigurationUtil.class);
	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);
	private static final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);

}