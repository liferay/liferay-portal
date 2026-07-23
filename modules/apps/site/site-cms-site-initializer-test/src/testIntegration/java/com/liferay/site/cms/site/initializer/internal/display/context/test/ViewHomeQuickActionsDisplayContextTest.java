/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.definition.setting.builder.ObjectDefinitionSettingBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolder;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class ViewHomeQuickActionsDisplayContextTest
	extends BaseDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-90042")
	public void testGetQuickActions() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		ObjectFolder objectFolder =
			objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				TestPropsValues.getCompanyId());

		ObjectDefinition customObjectDefinition = addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), true, true,
			Collections.singletonList(
				new ObjectDefinitionSettingBuilder(
				).name(
					ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS
				).value(
					String.valueOf(depotEntry.getGroupId())
				).build()),
			ObjectDefinitionConstants.SCOPE_DEPOT,
			WorkflowConstants.STATUS_APPROVED);

		User user = UserTestUtil.addUser();

		UserTestUtil.setUser(user);

		List<Map<String, Object>> quickActions = _getQuickActions(user);

		Assert.assertFalse(
			quickActions.toString(),
			_hasQuickActionForObjectDefinition(
				quickActions, customObjectDefinition));

		_groupLocalService.addUserGroup(
			user.getUserId(), depotEntry.getGroup());

		quickActions = _getQuickActions(user);

		Assert.assertFalse(
			quickActions.toString(),
			_hasQuickActionForObjectDefinition(
				quickActions, customObjectDefinition));

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);

		_userGroupRoleLocalService.addUserGroupRoles(
			user.getUserId(), depotEntry.getGroupId(),
			new long[] {role.getRoleId()});

		quickActions = _getQuickActions(user);

		Assert.assertTrue(
			quickActions.toString(),
			_hasQuickActionForObjectDefinition(
				quickActions, customObjectDefinition));
	}

	private DepotEntry _addDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private List<Map<String, Object>> _getQuickActions(User user)
		throws Exception {

		mockHttpServletRequest = getMockHttpServletRequest(user);

		_fragmentRenderer.render(
			null, mockHttpServletRequest, new MockHttpServletResponse());

		Object displayContext = mockHttpServletRequest.getAttribute(
			"com.liferay.site.cms.site.initializer.internal.display.context." +
				"ViewHomeQuickActionsDisplayContext");

		Map<String, Object> props = ReflectionTestUtil.invoke(
			displayContext, "getProps", new Class<?>[0]);

		return (List<Map<String, Object>>)props.get("quickActions");
	}

	private boolean _hasQuickActionForObjectDefinition(
		List<Map<String, Object>> quickActions,
		ObjectDefinition objectDefinition) {

		String title = objectDefinition.getLabel(LocaleUtil.getDefault());

		for (Map<String, Object> quickAction : quickActions) {
			if (Objects.equals(quickAction.get("title"), title)) {
				return true;
			}
		}

		return false;
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewHomeQuickActionsJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}