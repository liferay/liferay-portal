/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.category.facet.display.context;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.petra.function.transform.TransformUtil;
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
 * @author Shrilakshmi Reddy
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
				LocaleUtil.US, _siteConnectedGroupGroupProvider);
	}

	@Test
	public void testGetGroupsJSONArrayListsConnectedAssetLibrariesAfterSites()
		throws Exception {

		Group depotGroup = _mockGroup(true);

		Mockito.when(
			depotGroup.getName(LocaleUtil.US, true)
		).thenReturn(
			"A" + RandomTestUtil.randomString()
		);

		Group otherDepotGroup = _mockGroup(true);

		Mockito.when(
			otherDepotGroup.getName(LocaleUtil.US, true)
		).thenReturn(
			"B" + RandomTestUtil.randomString()
		);

		Group otherSiteGroup = _mockGroup(false);
		Group siteGroup = _mockGroup(false);

		_mockConnectedGroups(
			new Group[] {siteGroup, otherSiteGroup}, otherDepotGroup,
			depotGroup);

		_mockUserSitesGroups(siteGroup, otherSiteGroup);

		JSONArray jsonArray =
			_categoryFacetConfigurationDisplayContext.getGroupsJSONArray();

		Assert.assertEquals(jsonArray.toString(), 4, jsonArray.length());

		_assertSite(siteGroup, jsonArray.getJSONObject(0));
		_assertSite(otherSiteGroup, jsonArray.getJSONObject(1));
		_assertAssetLibrary(depotGroup, jsonArray.getJSONObject(2));
		_assertAssetLibrary(otherDepotGroup, jsonArray.getJSONObject(3));
	}

	@Test
	public void testGetGroupsJSONArrayListsConnectedAssetLibrariesOnce()
		throws Exception {

		Group depotGroup = _mockGroup(true);
		Group otherSiteGroup = _mockGroup(false);
		Group siteGroup = _mockGroup(false);

		_mockConnectedGroups(
			new Group[] {siteGroup, otherSiteGroup}, depotGroup, depotGroup);

		_mockUserSitesGroups(siteGroup, otherSiteGroup);

		JSONArray jsonArray =
			_categoryFacetConfigurationDisplayContext.getGroupsJSONArray();

		Assert.assertEquals(jsonArray.toString(), 3, jsonArray.length());

		_assertSite(siteGroup, jsonArray.getJSONObject(0));
		_assertSite(otherSiteGroup, jsonArray.getJSONObject(1));
		_assertAssetLibrary(depotGroup, jsonArray.getJSONObject(2));
	}

	@Test
	public void testGetGroupsJSONArrayNestsAssetVocabulariesUnderTheirOwningGroup()
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

		_assertAssetVocabularyChild(
			siteAssetVocabulary, siteGroup, jsonArray.getJSONObject(0));
		_assertAssetVocabularyChild(
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

		_assertSite(siteGroup, jsonArray.getJSONObject(0));
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

		_assertSite(siteGroup, jsonArray.getJSONObject(0));
	}

	private void _assertAssetLibrary(Group group, JSONObject jsonObject)
		throws Exception {

		Assert.assertEquals(
			group.getGroupKey(), jsonObject.getString("assetLibraryKey"));

		_assertGroup(group, jsonObject);
	}

	private void _assertAssetVocabularyChild(
		AssetVocabulary assetVocabulary, Group group,
		JSONObject groupJSONObject) {

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

	private void _assertGroup(Group group, JSONObject jsonObject)
		throws Exception {

		Assert.assertEquals(
			group.getExternalReferenceCode(),
			jsonObject.getString("externalReferenceCode"));
		Assert.assertEquals(group.getGroupId(), jsonObject.getLong("groupId"));
		Assert.assertEquals(
			group.getDescriptiveName(LocaleUtil.US),
			jsonObject.getString("name"));
	}

	private void _assertSite(Group group, JSONObject jsonObject)
		throws Exception {

		Assert.assertFalse(jsonObject.has("assetLibraryKey"));

		_assertGroup(group, jsonObject);
	}

	private AssetVocabulary _mockAssetVocabulary(Group group) {
		AssetVocabulary assetVocabulary = Mockito.mock(AssetVocabulary.class);

		Mockito.when(
			assetVocabulary.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		long groupId = group.getGroupId();

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

		long[] groupIds = TransformUtil.transformToLongArray(
			groups, Group::getGroupId);
		long[] siteGroupIds = TransformUtil.transformToLongArray(
			siteGroups, Group::getGroupId);

		Mockito.when(
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(siteGroupIds)
		).thenReturn(
			groupIds
		);
	}

	private Group _mockGroup(boolean depot) throws Exception {
		Group group = Mockito.mock(Group.class);

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

		long groupId = RandomTestUtil.randomLong();

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
			group.getName(LocaleUtil.US, true)
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