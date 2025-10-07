/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sites.kernel.util.Sites;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class GroupCTTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, GroupCTTest.class.getName(), null);
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddGroupFromLayoutSetPrototype() throws Exception {
		LayoutSetPrototype layoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		Group layoutSetPrototypeGroup =
			_groupLocalService.getLayoutSetPrototypeGroup(
				TestPropsValues.getCompanyId(),
				layoutSetPrototype.getLayoutSetPrototypeId());

		Layout layout1 = LayoutTestUtil.addTypeContentLayout(
			layoutSetPrototypeGroup, true, false);
		Layout layout2 = LayoutTestUtil.addTypeContentLayout(
			layoutSetPrototypeGroup, true, false);

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, GroupCTTest.class.getName(), null);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			Group group = GroupTestUtil.addGroup();

			_sites.updateLayoutSetPrototypesLinks(
				group, layoutSetPrototype.getLayoutSetPrototypeId(), 0, true,
				false);

			List<String> layoutNames = ListUtil.toList(
				_layoutLocalService.getLayouts(
					group.getGroupId(), false, LayoutConstants.TYPE_CONTENT),
				Layout::getName);

			Assert.assertTrue(layoutNames.contains(layout1.getName()));
			Assert.assertTrue(layoutNames.contains(layout2.getName()));
		}
	}

	@Test
	public void testGetUserSitesGroups() throws Exception {
		Group group = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			group = GroupTestUtil.addGroup();

			List<Group> groups = _groupService.getUserSitesGroups(
				TestPropsValues.getUserId(), null, QueryUtil.ALL_POS);

			Assert.assertTrue(groups.toString(), groups.contains(_group));
			Assert.assertTrue(groups.toString(), groups.contains(group));
		}

		List<Group> groups = _groupService.getUserSitesGroups(
			TestPropsValues.getUserId(), null, QueryUtil.ALL_POS);

		Assert.assertTrue(groups.toString(), groups.contains(_group));
		Assert.assertFalse(groups.toString(), groups.contains(group));
	}

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private static GroupService _groupService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Sites _sites;

}