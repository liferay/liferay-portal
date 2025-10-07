/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.item.selector.provider.test;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.item.selector.provider.GroupItemSelectorProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Roberto Díaz
 */
public abstract class BaseDepotGroupItemSelectorProviderTestCase {

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetGroups() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());

		List<Group> groups = getGroupItemSelectorProvider().getGroups(
			_group.getCompanyId(), _group.getGroupId(), null, 0, 20);

		Assert.assertEquals(groups.toString(), 1, groups.size());
	}

	@Test
	public void testGetGroupsCount() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());

		Assert.assertEquals(
			1,
			getGroupItemSelectorProvider().getGroupsCount(
				_group.getCompanyId(), _group.getGroupId(), null));
	}

	@Test
	public void testGetGroupsCountStaging() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());

		GroupTestUtil.enableLocalStaging(_group);

		Group stagingGroup = _group.getStagingGroup();

		Assert.assertEquals(
			1,
			getGroupItemSelectorProvider().getGroupsCount(
				stagingGroup.getCompanyId(), stagingGroup.getGroupId(), null));
	}

	@Test
	public void testGetGroupsStaging() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());

		GroupTestUtil.enableLocalStaging(_group);

		Group stagingGroup = _group.getStagingGroup();

		List<Group> groups = getGroupItemSelectorProvider().getGroups(
			stagingGroup.getCompanyId(), stagingGroup.getGroupId(), null, 0,
			20);

		Assert.assertEquals(groups.toString(), 1, groups.size());
	}

	@Test
	public void testGetIcon() {
		Assert.assertEquals("books", getGroupItemSelectorProvider().getIcon());
	}

	@Test
	public void testGetLabel() {
		Assert.assertEquals(
			getLabel(), getGroupItemSelectorProvider().getLabel(LocaleUtil.US));
	}

	protected abstract int getDepotType();

	protected abstract GroupItemSelectorProvider getGroupItemSelectorProvider();

	protected abstract String getLabel();

	private DepotEntry _addDepotEntry() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			getDepotType(), ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

}