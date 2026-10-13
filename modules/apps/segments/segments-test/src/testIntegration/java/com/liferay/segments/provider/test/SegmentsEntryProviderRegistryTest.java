/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.cache.test.util.TestPortalCacheListener;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.odata.normalizer.Normalizer;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.context.Context;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.provider.SegmentsEntryProviderRegistry;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
@Sync
public class SegmentsEntryProviderRegistryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo({"LPD-86011", "LPD-109786"})
	public void testGetSegmentsEntryIds() throws Exception {
		SegmentsEntry segmentsEntry1 = _addSegmentsEntry(
			RandomTestUtil.randomString());

		User user = TestPropsValues.getUser();

		SegmentsEntry segmentsEntry2 = _addSegmentsEntry(user.getFirstName());

		long[] segmentsEntryIds =
			_segmentsEntryProviderRegistry.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				TestPropsValues.getUserId(), new Context());

		Assert.assertEquals(
			Arrays.toString(segmentsEntryIds), 1, segmentsEntryIds.length);
		Assert.assertFalse(
			ArrayUtil.contains(
				segmentsEntryIds, segmentsEntry1.getSegmentsEntryId()));
		Assert.assertTrue(
			ArrayUtil.contains(
				segmentsEntryIds, segmentsEntry2.getSegmentsEntryId()));

		segmentsEntryIds = _segmentsEntryProviderRegistry.getSegmentsEntryIds(
			_group.getGroupId(), User.class.getName(),
			TestPropsValues.getUserId(), new Context());

		Assert.assertEquals(
			Arrays.toString(segmentsEntryIds), 1, segmentsEntryIds.length);
		Assert.assertFalse(
			ArrayUtil.contains(
				segmentsEntryIds, segmentsEntry1.getSegmentsEntryId()));
		Assert.assertTrue(
			ArrayUtil.contains(
				segmentsEntryIds, segmentsEntry2.getSegmentsEntryId()));

		PortalCache<String, Map<String, long[]>> portalCache =
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.MULTI_VM,
				"com.liferay.segments.internal.cache.SegmentsEntryCacheUtil");

		TestPortalCacheListener<String, Map<String, long[]>>
			testPortalCacheListener = new TestPortalCacheListener<>();

		portalCache.registerPortalCacheListener(testPortalCacheListener);

		Context context = new Context();

		context.put(Context.URL, RandomTestUtil.randomString());

		try {
			_segmentsEntryProviderRegistry.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				TestPropsValues.getUserId(), context);
		}
		finally {
			portalCache.unregisterPortalCacheListener(testPortalCacheListener);
		}

		testPortalCacheListener.assertActionsCount(0);
	}

	@Test
	@TestInfo("LPD-86253")
	public void testGetSegmentsEntryIdsWithCustomFieldAfterUpdateValue()
		throws Exception {

		Criteria criteria = new Criteria();

		User user = UserTestUtil.addUser();

		ExpandoTable expandoTable = _expandoTableLocalService.addDefaultTable(
			user.getCompanyId(), User.class.getName());

		ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
			expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.BOOLEAN);

		_expandoValueLocalService.addValue(
			user.getCompanyId(), User.class.getName(), expandoTable.getName(),
			expandoColumn.getName(), user.getUserId(), true);

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			StringBundler.concat(
				"(customField/_", expandoColumn.getColumnId(), "_",
				Normalizer.normalizeIdentifier(expandoColumn.getName()),
				" eq true)"),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Assert.assertArrayEquals(
			new long[] {segmentsEntry.getSegmentsEntryId()},
			_segmentsEntryProviderRegistry.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(), user.getUserId(),
				new Context()));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setExpandoBridgeAttributes(
			HashMapBuilder.<String, Serializable>put(
				expandoColumn.getName(), false
			).build());

		user.setExpandoBridgeAttributes(serviceContext);

		user = _userLocalService.updateUser(user);

		Assert.assertTrue(
			ArrayUtil.isEmpty(
				_segmentsEntryProviderRegistry.getSegmentsEntryIds(
					_group.getGroupId(), User.class.getName(), user.getUserId(),
					new Context())));
	}

	private SegmentsEntry _addSegmentsEntry(String firstName) throws Exception {
		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria, String.format("(firstName eq '%s')", firstName),
			Criteria.Conjunction.AND);

		return SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));
	}

	@Inject
	private ExpandoTableLocalService _expandoTableLocalService;

	@Inject
	private ExpandoValueLocalService _expandoValueLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private SegmentsEntryProviderRegistry _segmentsEntryProviderRegistry;

	@Inject
	private UserLocalService _userLocalService;

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _userSegmentsCriteriaContributor;

}