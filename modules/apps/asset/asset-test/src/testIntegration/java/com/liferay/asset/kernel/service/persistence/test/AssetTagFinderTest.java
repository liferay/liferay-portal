/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.persistence.AssetTagFinder;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class AssetTagFinderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), TransactionalTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		String name = RandomTestUtil.randomString();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		_scopeGroup = GroupLocalServiceUtil.addGroup(
			StringPool.BLANK, TestPropsValues.getUserId(),
			_group.getParentGroupId(), Layout.class.getName(), layout.getPlid(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), name
			).build(),
			RandomTestUtil.randomLocaleStringMap(),
			GroupConstants.TYPE_SITE_OPEN, null, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
			StringPool.SLASH + FriendlyURLNormalizerUtil.normalize(name), false,
			false, true, _serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		GroupLocalServiceUtil.deleteGroup(_scopeGroup);

		GroupLocalServiceUtil.deleteGroup(_group);
	}

	@Test
	public void testCountByG_C_N_WithCaseSensitiveTags() throws Exception {
		_assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(), "tag1",
			_serviceContext);

		_testCountByG_C_N("Tag1", _portal.getClassNameId(MBMessage.class));
		_testCountByG_C_N("Tag1", 0);
	}

	@Test
	public void testCountByG_C_N_WithClassNameId() throws Exception {
		_testCountByG_C_N(
			RandomTestUtil.randomString(),
			_portal.getClassNameId(MBMessage.class));
	}

	@Test
	public void testCountByG_C_N_WithoutClassNameId() throws Exception {
		_testCountByG_C_N(RandomTestUtil.randomString(), 0);
	}

	@Test
	public void testFindByG_C_N() throws Exception {
		_testFindByG_C_N(
			RandomTestUtil.randomString(),
			_portal.getClassNameId(MBMessage.class));
	}

	@Test
	public void testFindByG_C_N_WithCaseSensitiveTags() throws Exception {
		_assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(), "tag1",
			_serviceContext);

		_serviceContext.setAssetTagNames(new String[] {"tag1"});

		JournalTestUtil.addArticle(
			_group.getGroupId(), "New journal article",
			RandomTestUtil.randomString(), _serviceContext);

		_testFindByG_C_N("Tag1", _portal.getClassNameId(MBMessage.class));
	}

	protected void addMBMessage(long groupId, String assetTagName)
		throws Exception {

		_serviceContext.setAssetTagNames(new String[] {assetTagName});

		MBTestUtil.addMessageWithWorkflow(
			groupId, MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			_serviceContext);
	}

	private void _testCountByG_C_N(String assetTagName, long classNameId)
		throws Exception {

		int initialScopeGroupAssetTagsCount = _assetTagFinder.countByG_C_N(
			_scopeGroup.getGroupId(), classNameId, assetTagName);
		int initialSiteGroupAssetTagsCount = _assetTagFinder.countByG_C_N(
			_scopeGroup.getParentGroupId(), classNameId, assetTagName);

		addMBMessage(_scopeGroup.getGroupId(), assetTagName);

		int scopeGroupAssetTagsCount = _assetTagFinder.countByG_C_N(
			_scopeGroup.getGroupId(), classNameId, assetTagName);

		Assert.assertEquals(
			initialScopeGroupAssetTagsCount + 1, scopeGroupAssetTagsCount);

		int siteGroupAssetTagsCount = _assetTagFinder.countByG_C_N(
			_scopeGroup.getParentGroupId(), classNameId, assetTagName);

		Assert.assertEquals(
			initialSiteGroupAssetTagsCount, siteGroupAssetTagsCount);
	}

	private void _testFindByG_C_N(String assetTagName, long classNameId)
		throws Exception {

		List<AssetTag> initialScopeGroupAssetTags = _assetTagFinder.findByG_C_N(
			_scopeGroup.getGroupId(), classNameId, assetTagName,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		List<AssetTag> initialSiteGroupAssetTags = _assetTagFinder.findByG_C_N(
			_scopeGroup.getParentGroupId(), classNameId, assetTagName,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		addMBMessage(_scopeGroup.getGroupId(), assetTagName);

		List<AssetTag> scopeGroupAssetTags = _assetTagFinder.findByG_C_N(
			_scopeGroup.getGroupId(), classNameId, assetTagName,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			scopeGroupAssetTags.toString(),
			initialScopeGroupAssetTags.size() + 1, scopeGroupAssetTags.size());

		List<AssetTag> siteGroupAssetTags = _assetTagFinder.findByG_C_N(
			_scopeGroup.getParentGroupId(), classNameId, assetTagName,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			siteGroupAssetTags.toString(), initialSiteGroupAssetTags.size(),
			siteGroupAssetTags.size());
	}

	@Inject
	private AssetTagFinder _assetTagFinder;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	private Group _group;

	@Inject
	private Portal _portal;

	private Group _scopeGroup;
	private ServiceContext _serviceContext;

}