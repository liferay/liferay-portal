/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.exception.NoSuchEntryGroupRelException;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sites.kernel.util.Sites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class DepotEntryGroupRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();
		_group2 = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddDepotEntryGroupRel() throws Exception {
		DepotEntry depotEntry1 = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		DepotEntryGroupRel depotEntryGroupRel1 =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry1.getDepotEntryId(), _group1.getGroupId());

		Assert.assertEquals(
			depotEntry1.getDepotEntryId(),
			depotEntryGroupRel1.getDepotEntryId());
		Assert.assertEquals(
			_group1.getGroupId(), depotEntryGroupRel1.getToGroupId());
		Assert.assertEquals(
			depotEntry1.getType(), depotEntryGroupRel1.getType());

		DepotEntry depotEntry2 = _addDepotEntry(DepotConstants.TYPE_SPACE);

		DepotEntryGroupRel depotEntryGroupRel2 =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry2.getDepotEntryId(), _group1.getGroupId());

		Assert.assertEquals(
			depotEntry2.getType(), depotEntryGroupRel2.getType());
	}

	@Test
	public void testAddDepotEntryGroupRelWithARepeatedDepotEntryGroupRel()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		DepotEntryGroupRel originalDepotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), _group1.getGroupId());

		DepotEntryGroupRel finalDepotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), _group1.getGroupId());

		Assert.assertEquals(
			originalDepotEntryGroupRel.getDepotEntryGroupRelId(),
			finalDepotEntryGroupRel.getDepotEntryGroupRelId());
		Assert.assertEquals(
			originalDepotEntryGroupRel.getDepotEntryId(),
			finalDepotEntryGroupRel.getDepotEntryId());
		Assert.assertEquals(
			originalDepotEntryGroupRel.getToGroupId(),
			finalDepotEntryGroupRel.getToGroupId());
	}

	@Test
	public void testAddDepotEntryGroupRelWithLayoutSetPrototypeWithoutPropagation()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.addLayoutSetPrototype(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				null, true, true, false,
				ServiceContextTestUtil.getServiceContext());

		Group group = _setUpLayoutSetPrototypeGroup(layoutSetPrototype);

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), group.getGroupId());

		Assert.assertTrue(depotEntryGroupRel.getDepotEntryId() > 0);

		Assert.assertNull(
			_depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					depotEntry.getDepotEntryId(), _group1.getGroupId()));
		Assert.assertNull(
			_depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					depotEntry.getDepotEntryId(), _group2.getGroupId()));
	}

	@Test
	public void testAddDepotEntryGroupRelWithLayoutSetPrototypeWithPropagation()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.addLayoutSetPrototype(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				null, true, true, ServiceContextTestUtil.getServiceContext());

		Group group = _setUpLayoutSetPrototypeGroup(layoutSetPrototype);

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), group.getGroupId());

		Assert.assertTrue(depotEntryGroupRel.getDepotEntryId() > 0);

		Assert.assertNotNull(
			_depotEntryGroupRelLocalService.
				getDepotEntryGroupRelByDepotEntryIdToGroupId(
					depotEntry.getDepotEntryId(), _group1.getGroupId()));
		Assert.assertNotNull(
			_depotEntryGroupRelLocalService.
				getDepotEntryGroupRelByDepotEntryIdToGroupId(
					depotEntry.getDepotEntryId(), _group2.getGroupId()));
	}

	@Test
	public void testDeleteDepotEntryGroupRel() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), _group1.getGroupId());

		_depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
			depotEntryGroupRel.getDepotEntryGroupRelId());

		AssertUtils.assertFailure(
			NoSuchEntryGroupRelException.class,
			"No DepotEntryGroupRel exists with the primary key " +
				depotEntryGroupRel.getDepotEntryGroupRelId(),
			() -> _depotEntryGroupRelLocalService.getDepotEntryGroupRel(
				depotEntryGroupRel.getDepotEntryGroupRelId()));
	}

	@Test
	public void testDeleteDepotEntryGroupRelWithLayoutSetPrototypeWithoutPropagation()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.addLayoutSetPrototype(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				null, true, true, ServiceContextTestUtil.getServiceContext());

		Group group = _setUpLayoutSetPrototypeGroup(layoutSetPrototype);

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), group.getGroupId());

		Assert.assertEquals(
			3,
			_depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount(
				depotEntry));

		_layoutSetPrototypeLocalService.updateLayoutSetPrototype(
			layoutSetPrototype.getLayoutSetPrototypeId(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			null, true, true, false,
			ServiceContextTestUtil.getServiceContext());

		int systemEventsCount = _systemEventLocalService.getSystemEventsCount();

		_depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
			depotEntryGroupRel.getDepotEntryGroupRelId());

		Assert.assertNull(
			_depotEntryGroupRelLocalService.fetchDepotEntryGroupRel(
				depotEntryGroupRel.getDepotEntryGroupRelId()));
		Assert.assertNotNull(
			_depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					depotEntry.getDepotEntryId(), _group1.getGroupId()));
		Assert.assertNotNull(
			_depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					depotEntry.getDepotEntryId(), _group2.getGroupId()));
		Assert.assertEquals(
			2,
			_depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount(
				depotEntry));
		Assert.assertEquals(
			systemEventsCount + 1,
			_systemEventLocalService.getSystemEventsCount());
	}

	@Test
	public void testDeleteDepotEntryGroupRelWithLayoutSetPrototypeWithPropagation()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.addLayoutSetPrototype(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				null, true, true, ServiceContextTestUtil.getServiceContext());

		Group group = _setUpLayoutSetPrototypeGroup(layoutSetPrototype);

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), group.getGroupId());

		Assert.assertEquals(
			3,
			_depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount(
				depotEntry));

		int systemEventsCount = _systemEventLocalService.getSystemEventsCount();

		_depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
			depotEntryGroupRel.getDepotEntryGroupRelId());

		Assert.assertNull(
			_depotEntryGroupRelLocalService.fetchDepotEntryGroupRel(
				depotEntryGroupRel.getDepotEntryGroupRelId()));
		Assert.assertNull(
			_depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					depotEntry.getDepotEntryId(), _group1.getGroupId()));
		Assert.assertNull(
			_depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					depotEntry.getDepotEntryId(), _group2.getGroupId()));
		Assert.assertEquals(
			0,
			_depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount(
				depotEntry));
		Assert.assertEquals(
			systemEventsCount + 3,
			_systemEventLocalService.getSystemEventsCount());
	}

	@Test
	public void testDeleteGroup() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		Group group = _groupLocalService.addGroup(
			TestPropsValues.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
			null, 0, 0,
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			GroupConstants.TYPE_SITE_OPEN, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, true, true,
			ServiceContextTestUtil.getServiceContext());

		int depotEntryGroupRelsCount =
			_depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), group.getGroupId());

		Assert.assertEquals(
			depotEntryGroupRelsCount + 1,
			_depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount());

		_groupLocalService.deleteGroup(group.getGroupId());

		Assert.assertEquals(
			depotEntryGroupRelsCount,
			_depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount());
	}

	@Test
	public void testGetDepotEntryGroupRel() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		DepotEntryGroupRel originalDepotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), _group1.getGroupId());

		DepotEntryGroupRel finalDepotEntryGroupRel =
			_depotEntryGroupRelLocalService.getDepotEntryGroupRel(
				originalDepotEntryGroupRel.getDepotEntryGroupRelId());

		Assert.assertEquals(
			originalDepotEntryGroupRel.getDepotEntryGroupRelId(),
			finalDepotEntryGroupRel.getDepotEntryGroupRelId());
		Assert.assertEquals(
			originalDepotEntryGroupRel.getDepotEntryId(),
			finalDepotEntryGroupRel.getDepotEntryId());
		Assert.assertEquals(
			originalDepotEntryGroupRel.getToGroupId(),
			finalDepotEntryGroupRel.getToGroupId());
	}

	@Test
	public void testGetDepotEntryGroupRels() throws Exception {
		DepotEntry depotEntry1 = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);
		DepotEntry depotEntry2 = _addDepotEntry(DepotConstants.TYPE_SPACE);

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry1.getDepotEntryId(), _group1.getGroupId());
		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry2.getDepotEntryId(), _group1.getGroupId());

		List<DepotEntryGroupRel> depotEntryGroupRels1 =
			_depotEntryGroupRelLocalService.getDepotEntryGroupRels(
				_group1.getGroupId(), DepotConstants.TYPE_ASSET_LIBRARY, 0, 20);

		Assert.assertEquals(
			depotEntryGroupRels1.toString(), 1, depotEntryGroupRels1.size());

		DepotEntryGroupRel depotEntryGroupRel1 = depotEntryGroupRels1.get(0);

		Assert.assertEquals(
			depotEntry1.getDepotEntryId(),
			depotEntryGroupRel1.getDepotEntryId());
		Assert.assertEquals(
			_group1.getGroupId(), depotEntryGroupRel1.getToGroupId());

		List<DepotEntryGroupRel> depotEntryGroupRels2 =
			_depotEntryGroupRelLocalService.getDepotEntryGroupRels(
				_group1.getGroupId(), DepotConstants.TYPE_SPACE, 0, 20);

		Assert.assertEquals(
			depotEntryGroupRels2.toString(), 1, depotEntryGroupRels2.size());

		DepotEntryGroupRel depotEntryGroupRel2 = depotEntryGroupRels2.get(0);

		Assert.assertEquals(
			depotEntry2.getDepotEntryId(),
			depotEntryGroupRel2.getDepotEntryId());
		Assert.assertEquals(
			_group1.getGroupId(), depotEntryGroupRel2.getToGroupId());

		Set<DepotEntryGroupRel> expectedDepotEntryGroupRels = new HashSet<>();

		expectedDepotEntryGroupRels.addAll(depotEntryGroupRels1);
		expectedDepotEntryGroupRels.addAll(depotEntryGroupRels2);

		Assert.assertEquals(
			expectedDepotEntryGroupRels,
			SetUtil.fromCollection(
				_depotEntryGroupRelLocalService.getDepotEntryGroupRels(
					_group1.getGroupId(), DepotConstants.TYPE_ANY, 0, 20)));
	}

	@Test
	public void testGetDepotEntryGroupRelsCount() throws Exception {
		DepotEntry depotEntry1 = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry1.getDepotEntryId(), _group1.getGroupId());

		DepotEntry depotEntry2 = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry2.getDepotEntryId(), _group1.getGroupId());

		Assert.assertEquals(
			2,
			_depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount(
				_group1.getGroupId(), DepotConstants.TYPE_ASSET_LIBRARY));
		Assert.assertEquals(
			0,
			_depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount(
				RandomTestUtil.randomInt(), DepotConstants.TYPE_ASSET_LIBRARY));
	}

	@Test
	public void testGetSearchableDepotEntryGroupRels() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), _group1.getGroupId());

		int searchableDepotEntryGroupRelsCount =
			_depotEntryGroupRelLocalService.
				getSearchableDepotEntryGroupRelsCount(_group1.getGroupId());

		Assert.assertEquals(1, searchableDepotEntryGroupRelsCount);

		List<DepotEntryGroupRel> searchableDepotEntryGroupRels =
			_depotEntryGroupRelLocalService.getSearchableDepotEntryGroupRels(
				_group1.getGroupId(), 0, searchableDepotEntryGroupRelsCount);

		Assert.assertEquals(
			searchableDepotEntryGroupRels.toString(), 1,
			searchableDepotEntryGroupRels.size());
		Assert.assertEquals(
			depotEntryGroupRel, searchableDepotEntryGroupRels.get(0));
	}

	@Test
	public void testGetSearchableDepotEntryGroupRelsWithAnUnsearchableDepotEntryGroupRel()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group1.getGroupId(), false);

		int searchableDepotEntryGroupRelsCount =
			_depotEntryGroupRelLocalService.
				getSearchableDepotEntryGroupRelsCount(_group1.getGroupId());

		Assert.assertEquals(0, searchableDepotEntryGroupRelsCount);

		List<DepotEntryGroupRel> searchableDepotEntryGroupRels =
			_depotEntryGroupRelLocalService.getSearchableDepotEntryGroupRels(
				_group1.getGroupId(), 0, searchableDepotEntryGroupRelsCount);

		Assert.assertTrue(
			searchableDepotEntryGroupRels.toString(),
			searchableDepotEntryGroupRels.isEmpty());
	}

	@Test
	public void testUpdateSearchable() throws Exception {
		DepotEntry depotEntry1 = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry1.getDepotEntryId(), _group1.getGroupId());

		Assert.assertTrue(depotEntryGroupRel.isSearchable());

		depotEntryGroupRel = _depotEntryGroupRelLocalService.updateSearchable(
			depotEntryGroupRel.getDepotEntryGroupRelId(), false);

		Assert.assertFalse(depotEntryGroupRel.isSearchable());

		depotEntryGroupRel = _depotEntryGroupRelLocalService.updateSearchable(
			depotEntryGroupRel.getDepotEntryGroupRelId(), true);

		Assert.assertTrue(depotEntryGroupRel.isSearchable());
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			type, ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private Group _setUpLayoutSetPrototypeGroup(
			LayoutSetPrototype layoutSetPrototype)
		throws Exception {

		_sites.updateLayoutSetPrototypesLinks(
			_group1, layoutSetPrototype.getLayoutSetPrototypeId(), 0, true,
			false);
		_sites.updateLayoutSetPrototypesLinks(
			_group2, layoutSetPrototype.getLayoutSetPrototypeId(), 0, true,
			false);

		return _groupLocalService.getLayoutSetPrototypeGroup(
			TestPropsValues.getCompanyId(),
			layoutSetPrototype.getLayoutSetPrototypeId());
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group1;

	private Group _group2;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Inject
	private Sites _sites;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

}