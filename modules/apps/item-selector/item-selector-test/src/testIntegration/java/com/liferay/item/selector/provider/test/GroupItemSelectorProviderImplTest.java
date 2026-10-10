/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.item.selector.provider.GroupItemSelectorProvider;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class GroupItemSelectorProviderImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-110411")
	public void testGetGroups() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addUser())) {

			List<Group> groups = _groupItemSelectorProvider.getGroups(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				null, 0, 20);

			Company company = CompanyLocalServiceUtil.getCompany(
				TestPropsValues.getCompanyId());

			Assert.assertTrue(groups.contains(company.getGroup()));
		}

		List<Group> groups = _groupItemSelectorProvider.getGroups(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(), null,
			0, 20);

		int originalGroupsSize = groups.size();

		Assert.assertEquals(
			originalGroupsSize,
			_groupItemSelectorProvider.getGroupsCount(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				null));

		Group childGroup = GroupTestUtil.addGroup(TestPropsValues.getGroupId());

		groups = _groupItemSelectorProvider.getGroups(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(), null,
			0, 20);

		Assert.assertEquals(
			groups.toString(), originalGroupsSize + 1, groups.size());

		Assert.assertEquals(
			originalGroupsSize + 1,
			_groupItemSelectorProvider.getGroupsCount(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				null));

		childGroup.setActive(false);

		childGroup = _groupLocalService.updateGroup(childGroup);

		groups = _groupItemSelectorProvider.getGroups(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(), null,
			0, 20);

		Assert.assertEquals(
			groups.toString(), originalGroupsSize, groups.size());

		Assert.assertEquals(
			originalGroupsSize,
			_groupItemSelectorProvider.getGroupsCount(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				null));

		GroupTestUtil.deleteGroup(childGroup);
	}

	@Test
	public void testGetIcon() {
		Assert.assertEquals("sites", _groupItemSelectorProvider.getIcon());
	}

	@FeatureFlag("LPD-81022")
	@Test
	@TestInfo("LPD-109452")
	public void testGetLabel() {
		Assert.assertEquals(
			"Sites", _groupItemSelectorProvider.getLabel(LocaleUtil.US));
	}

	@Test
	@TestInfo("LPD-109452")
	public void testIsGroupType() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));

		Assert.assertTrue(
			_groupItemSelectorProvider.isGroupType(
				_groupLocalService.getGroup(TestPropsValues.getGroupId())));
		Assert.assertFalse(
			_groupItemSelectorProvider.isGroupType(depotEntry.getGroup()));
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.item.selector.internal.provider.GroupItemSelectorProviderImpl"
	)
	private GroupItemSelectorProvider _groupItemSelectorProvider;

	@Inject
	private GroupLocalService _groupLocalService;

}