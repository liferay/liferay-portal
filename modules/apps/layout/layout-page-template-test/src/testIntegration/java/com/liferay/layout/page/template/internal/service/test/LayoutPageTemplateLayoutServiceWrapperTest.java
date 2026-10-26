/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@FeatureFlag("LPD-57283")
@RunWith(Arquillian.class)
public class LayoutPageTemplateLayoutServiceWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddLayout() throws Exception {
		DepotEntry depotEntry1 = _addDepotEntry();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry1.getDepotEntryId(), _group.getGroupId());

		LayoutPageTemplateEntry layoutPageTemplateEntry1 =
			_addLayoutPageTemplateEntry(depotEntry1.getGroupId());

		DepotEntry depotEntry2 = _addDepotEntry();

		LayoutPageTemplateEntry layoutPageTemplateEntry2 =
			_addLayoutPageTemplateEntry(depotEntry2.getGroupId());

		Group group = GroupTestUtil.addGroup();

		LayoutPageTemplateEntry layoutPageTemplateEntry3 =
			_addLayoutPageTemplateEntry(group.getGroupId());

		LayoutPageTemplateEntry layoutPageTemplateEntry4 =
			_addLayoutPageTemplateEntry(_group.getGroupId());

		Layout layout = _addLayout(
			layoutPageTemplateEntry1.getLayoutPageTemplateEntryId());

		Assert.assertEquals(
			layoutPageTemplateEntry1.getLayoutPageTemplateEntryId(),
			layout.getClassPK());

		Assert.assertThrows(
			PrincipalException.MustHavePermission.class,
			() -> _addLayout(
				layoutPageTemplateEntry2.getLayoutPageTemplateEntryId()));
		Assert.assertThrows(
			PrincipalException.MustHavePermission.class,
			() -> _addLayout(
				layoutPageTemplateEntry3.getLayoutPageTemplateEntryId()));

		layout = _addLayout(
			layoutPageTemplateEntry4.getLayoutPageTemplateEntryId());

		Assert.assertEquals(
			layoutPageTemplateEntry4.getLayoutPageTemplateEntryId(),
			layout.getClassPK());
	}

	@Test
	public void testAddLayoutWithoutPermissions() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addLayoutPageTemplateEntry(_group.getGroupId());

		Role guestRole = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.removeResourcePermission(
			_group.getCompanyId(), LayoutPageTemplateEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()),
			guestRole.getRoleId(), ActionKeys.VIEW);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		User user = UserTestUtil.addUser();

		_roleLocalService.addUserRole(user.getUserId(), role.getRoleId());

		RoleTestUtil.addResourcePermission(
			role, Group.class.getName(), ResourceConstants.SCOPE_GROUP,
			String.valueOf(_group.getGroupId()), ActionKeys.ADD_LAYOUT);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _addLayout(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));

			_resourcePermissionLocalService.setResourcePermissions(
				_group.getCompanyId(), LayoutPageTemplateEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId()),
				role.getRoleId(), new String[] {ActionKeys.VIEW});

			Layout layout = _addLayout(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

			Assert.assertEquals(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				layout.getClassPK());
		}
	}

	@Test
	public void testConvertEmptyLayout() throws Exception {
		DepotEntry depotEntry1 = _addDepotEntry();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry1.getDepotEntryId(), _group.getGroupId());

		LayoutPageTemplateEntry layoutPageTemplateEntry1 =
			_addLayoutPageTemplateEntry(depotEntry1.getGroupId());

		DepotEntry depotEntry2 = _addDepotEntry();

		LayoutPageTemplateEntry layoutPageTemplateEntry2 =
			_addLayoutPageTemplateEntry(depotEntry2.getGroupId());

		Group group = GroupTestUtil.addGroup();

		LayoutPageTemplateEntry layoutPageTemplateEntry3 =
			_addLayoutPageTemplateEntry(group.getGroupId());

		LayoutPageTemplateEntry layoutPageTemplateEntry4 =
			_addLayoutPageTemplateEntry(_group.getGroupId());

		Layout layout = _convertEmptyLayout(
			layoutPageTemplateEntry1.getLayoutPageTemplateEntryId(),
			LayoutTestUtil.addTypeEmptyLayout(_group));

		Assert.assertEquals(
			layoutPageTemplateEntry1.getLayoutPageTemplateEntryId(),
			layout.getClassPK());

		Layout emptyLayout = LayoutTestUtil.addTypeEmptyLayout(_group);

		Assert.assertThrows(
			PrincipalException.MustHavePermission.class,
			() -> _convertEmptyLayout(
				layoutPageTemplateEntry2.getLayoutPageTemplateEntryId(),
				emptyLayout));
		Assert.assertThrows(
			PrincipalException.MustHavePermission.class,
			() -> _convertEmptyLayout(
				layoutPageTemplateEntry3.getLayoutPageTemplateEntryId(),
				emptyLayout));

		layout = _convertEmptyLayout(
			layoutPageTemplateEntry4.getLayoutPageTemplateEntryId(),
			emptyLayout);

		Assert.assertEquals(
			layoutPageTemplateEntry4.getLayoutPageTemplateEntryId(),
			layout.getClassPK());
	}

	private DepotEntry _addDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext());
	}

	private Layout _addLayout(long classPK) throws Exception {
		return _layoutService.addLayout(
			null, _group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			_portal.getClassNameId(LayoutPageTemplateEntry.class), classPK,
			RandomTestUtil.randomLocaleStringMap(), null, null, null, null,
			LayoutConstants.TYPE_CONTENT, StringPool.BLANK, false, false,
			new HashMap<>(), null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private LayoutPageTemplateEntry _addLayoutPageTemplateEntry(long groupId)
		throws Exception {

		return LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			groupId, LayoutPageTemplateEntryTypeConstants.BASIC,
			WorkflowConstants.STATUS_APPROVED);
	}

	private Layout _convertEmptyLayout(long classPK, Layout layout)
		throws Exception {

		return _layoutService.convertEmptyLayout(
			layout.getPlid(), layout.getNameMap(), LayoutConstants.TYPE_CONTENT,
			_portal.getClassNameId(LayoutPageTemplateEntry.class), classPK,
			null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;

	@Inject
	private LayoutService _layoutService;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}