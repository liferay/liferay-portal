/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Feliphe Marinho
 */
@RunWith(Arquillian.class)
public class EditObjectEntryRelatedModelMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testDoProcessAction() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			Constants.CMD, Constants.ASSIGN);

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectEntry objectEntry1 = _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), 0,
			objectDefinition1.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext());

		mockLiferayPortletActionRequest.addParameter(
			"objectEntryId", String.valueOf(objectEntry1.getObjectEntryId()));

		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinition1,
				objectDefinition2);

		mockLiferayPortletActionRequest.addParameter(
			"objectRelationshipId",
			String.valueOf(objectRelationship.getObjectRelationshipId()));

		ObjectEntry objectEntry2 = _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), 0,
			objectDefinition2.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext());

		mockLiferayPortletActionRequest.addParameter(
			"objectRelationshipPrimaryKey2",
			String.valueOf(objectEntry2.getObjectEntryId()));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinition2.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		User user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.USER_ID, user.getUserId());

		Bundle bundle = FrameworkUtil.getBundle(
			EditObjectEntryRelatedModelMVCActionCommandTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		List<ServiceReference<MVCActionCommand>> serviceReferences =
			new ArrayList<>(
				bundleContext.getServiceReferences(
					MVCActionCommand.class,
					StringBundler.concat(
						"(&(jakarta.portlet.name=",
						objectDefinition2.getPortletId(),
						")(mvc.command.name=/object_entries",
						"/edit_object_entry_related_model))")));

		Assert.assertEquals(
			serviceReferences.toString(), 1, serviceReferences.size());

		ReflectionTestUtil.invoke(
			bundleContext.getService(serviceReferences.get(0)),
			"doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletActionRequest,
				PrincipalException.MustHavePermission.class.getName()));
		Assert.assertNotNull(
			SessionMessages.get(
				mockLiferayPortletActionRequest,
				_portal.getPortletId(mockLiferayPortletActionRequest) +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private UserLocalService _userLocalService;

}