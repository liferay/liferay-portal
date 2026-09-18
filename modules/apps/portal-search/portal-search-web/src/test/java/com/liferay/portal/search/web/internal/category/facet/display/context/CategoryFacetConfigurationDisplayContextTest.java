/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.category.facet.display.context;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Akhash Ramprakash
 */
public class CategoryFacetConfigurationDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_assetVocabularyService.getGroupVocabularies(
				Mockito.any(long[].class))
		).thenReturn(
			Collections.emptyList()
		);

		_categoryFacetConfigurationDisplayContext =
			new CategoryFacetConfigurationDisplayContext(
				_assetVocabularyService, _groupLocalService, _groupService,
				new JSONFactoryImpl(), LocaleUtil.US,
				_siteConnectedGroupGroupProvider);
	}

	@Test
	public void testGetGroupsJSONArrayListsConnectedAssetLibrariesAfterSites()
		throws Exception {

		Group depotGroup = _mockGroup(true);
		Group otherSiteGroup = _mockGroup(false);
		Group siteGroup = _mockGroup(false);

		_mockConnectedGroups(
			new Group[] {siteGroup, otherSiteGroup}, depotGroup);

		_mockUserSitesGroups(siteGroup, otherSiteGroup);

		JSONArray jsonArray =
			_categoryFacetConfigurationDisplayContext.getGroupsJSONArray();

		Assert.assertEquals(jsonArray.toString(), 3, jsonArray.length());

		_assertSite(jsonArray.getJSONObject(0), siteGroup);
		_assertSite(jsonArray.getJSONObject(1), otherSiteGroup);

		JSONObject depotJSONObject = jsonArray.getJSONObject(2);

		Assert.assertEquals(
			depotGroup.getGroupKey(),
			depotJSONObject.getString("assetLibraryKey"));
		Assert.assertEquals(
			depotGroup.getExternalReferenceCode(),
			depotJSONObject.getString("externalReferenceCode"));
		Assert.assertEquals(
			depotGroup.getGroupId(), depotJSONObject.getLong("groupId"));
		Assert.assertEquals(
			depotGroup.getDescriptiveName(LocaleUtil.US),
			depotJSONObject.getString("name"));
	}

	@Test
	public void testGetGroupsJSONArrayNestsVocabulariesUnderTheirOwningGroup()
		throws Exception {

		Group depotGroup = _mockGroup(true);
		Group siteGroup = _mockGroup(false);

		_mockConnectedGroups(new Group[] {siteGroup}, depotGroup);

		_mockUserSitesGroups(siteGroup);

		AssetVocabulary depotAssetVocabulary = _mockAssetVocabulary(depotGroup);
		AssetVocabulary siteAssetVocabulary = _mockAssetVocabulary(siteGroup);

		Mockito.when(
			_assetVocabularyService.getGroupVocabularies(
				new long[] {siteGroup.getGroupId(), depotGroup.getGroupId()})
		).thenReturn(
			Arrays.asList(siteAssetVocabulary, depotAssetVocabulary)
		);

		JSONArray jsonArray =
			_categoryFacetConfigurationDisplayContext.getGroupsJSONArray();

		_assertVocabularyChild(
			siteAssetVocabulary, siteGroup, jsonArray.getJSONObject(0));
		_assertVocabularyChild(
			depotAssetVocabulary, depotGroup, jsonArray.getJSONObject(1));
	}

	@Test
	public void testGetGroupsJSONArrayReturnsEmptyArrayWhenGroupResolutionFails()
		throws Exception {

		Mockito.when(
			_groupService.getUserSitesGroups()
		).thenThrow(
			new PortalException()
		);

		JSONArray jsonArray =
			_categoryFacetConfigurationDisplayContext.getGroupsJSONArray();

		Assert.assertEquals(jsonArray.toString(), 0, jsonArray.length());
	}

	@Test
	public void testGetGroupsJSONArraySkipsConnectedGroupsThatNoLongerExist()
		throws Exception {

		Group siteGroup = _mockGroup(false);

		Mockito.when(
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(
					new long[] {siteGroup.getGroupId()})
		).thenReturn(
			new long[] {RandomTestUtil.randomLong()}
		);

		_mockUserSitesGroups(siteGroup);

		JSONArray jsonArray =
			_categoryFacetConfigurationDisplayContext.getGroupsJSONArray();

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		_assertSite(jsonArray.getJSONObject(0), siteGroup);
	}

	@Test
	public void testGetGroupsJSONArraySkipsNonsiteGroupsAndAncestorSites()
		throws Exception {

		Group ancestorSiteGroup = _mockGroup(false);
		Group siteGroup = _mockGroup(false);

		_mockConnectedGroups(new Group[] {siteGroup}, ancestorSiteGroup);

		Group nonsiteGroup = _mockGroup(false);

		Mockito.when(
			nonsiteGroup.isSite()
		).thenReturn(
			false
		);

		_mockUserSitesGroups(siteGroup, nonsiteGroup);

		JSONArray jsonArray =
			_categoryFacetConfigurationDisplayContext.getGroupsJSONArray();

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		_assertSite(jsonArray.getJSONObject(0), siteGroup);
	}

	private void _assertSite(JSONObject jsonObject, Group group)
		throws Exception {

		Assert.assertFalse(jsonObject.has("assetLibraryKey"));
		Assert.assertEquals(
			group.getExternalReferenceCode(),
			jsonObject.getString("externalReferenceCode"));
		Assert.assertEquals(group.getGroupId(), jsonObject.getLong("groupId"));
		Assert.assertEquals(
			group.getDescriptiveName(LocaleUtil.US),
			jsonObject.getString("name"));
	}

	private void _assertVocabularyChild(
			AssetVocabulary assetVocabulary, Group group,
			JSONObject groupJSONObject)
		throws Exception {

		JSONArray childrenJSONArray = groupJSONObject.getJSONArray("children");

		Assert.assertEquals(
			childrenJSONArray.toString(), 1, childrenJSONArray.length());

		JSONObject assetVocabularyJSONObject = childrenJSONArray.getJSONObject(
			0);

		Assert.assertEquals(
			group.getExternalReferenceCode() + "&&" +
				assetVocabulary.getExternalReferenceCode(),
			assetVocabularyJSONObject.getString("externalReferenceCode"));
		Assert.assertEquals(
			assetVocabulary.getVocabularyId(),
			assetVocabularyJSONObject.getLong("id"));
		Assert.assertEquals(
			assetVocabulary.getTitle(LocaleUtil.US),
			assetVocabularyJSONObject.getString("name"));
	}

	private AssetVocabulary _mockAssetVocabulary(Group group) {
		AssetVocabulary assetVocabulary = Mockito.mock(AssetVocabulary.class);

		long groupId = group.getGroupId();

		Mockito.when(
			assetVocabulary.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			assetVocabulary.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			assetVocabulary.getTitle(LocaleUtil.US)
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			assetVocabulary.getVocabularyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		return assetVocabulary;
	}

	private void _mockConnectedGroups(Group[] siteGroups, Group... groups)
		throws Exception {

		long[] siteGroupIds = new long[siteGroups.length];

		for (int i = 0; i < siteGroups.length; i++) {
			siteGroupIds[i] = siteGroups[i].getGroupId();
		}

		long[] groupIds = new long[groups.length];

		for (int i = 0; i < groups.length; i++) {
			groupIds[i] = groups[i].getGroupId();
		}

		Mockito.when(
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(siteGroupIds)
		).thenReturn(
			groupIds
		);
	}

	private Group _mockGroup(boolean depot) throws Exception {
		Group group = Mockito.mock(Group.class);

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			group.getDescriptiveName(LocaleUtil.US)
		).thenReturn(
			RandomTestUtil.randomString()
		);

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

		Mockito.when(
			group.getName(LocaleUtil.US)
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			group.isDepot()
		).thenReturn(
			depot
		);

		Mockito.when(
			group.isSite()
		).thenReturn(
			!depot
		);

		Mockito.when(
			_groupLocalService.fetchGroup(groupId)
		).thenReturn(
			group
		);

		return group;
	}

	private void _mockUserSitesGroups(Group... groups) throws Exception {
		Mockito.when(
			_groupService.getUserSitesGroups()
		).thenReturn(
			Arrays.asList(groups)
		);
	}

	private final AssetVocabularyService _assetVocabularyService = Mockito.mock(
		AssetVocabularyService.class);
	private CategoryFacetConfigurationDisplayContext
		_categoryFacetConfigurationDisplayContext;
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final GroupService _groupService = Mockito.mock(GroupService.class);
	private final SiteConnectedGroupGroupProvider
		_siteConnectedGroupGroupProvider = Mockito.mock(
			SiteConnectedGroupGroupProvider.class);

}