/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Eudaldo Alonso
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
@Sync
public class ViewFilesSectionDisplayContextTest
	extends BaseDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Ignore
	@Test
	@TestInfo("LPD-50664")
	public void testGetCreationMenu() throws Exception {
		Map<String, String> expectedResultMap = LinkedHashMapBuilder.put(
			"folder", StringPool.BLANK
		).put(
			"Basic Document",
			getHref(
				objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						"L_BASIC_DOCUMENT", TestPropsValues.getCompanyId()))
		).build();

		testGetCreationMenu(
			ReflectionTestUtil.invoke(
				_getViewFilesSectionDisplayContext(getMockHttpServletRequest()),
				"getCreationMenu", new Class<?>[0]),
			expectedResultMap);

		ObjectFolder objectFolder =
			objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES,
				TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition = addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), true, true,
			ObjectDefinitionConstants.SCOPE_SITE,
			WorkflowConstants.STATUS_APPROVED);

		expectedResultMap.put(
			objectDefinition.getLabel(LocaleUtil.US),
			getHref(objectDefinition));

		addCustomObjectDefinition(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			false, true, ObjectDefinitionConstants.SCOPE_SITE,
			WorkflowConstants.STATUS_APPROVED);
		addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), false, true,
			ObjectDefinitionConstants.SCOPE_SITE,
			WorkflowConstants.STATUS_APPROVED);
		addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), true, false,
			ObjectDefinitionConstants.SCOPE_SITE,
			WorkflowConstants.STATUS_APPROVED);
		addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), true, true,
			ObjectDefinitionConstants.SCOPE_COMPANY,
			WorkflowConstants.STATUS_APPROVED);
		addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), true, true,
			ObjectDefinitionConstants.SCOPE_SITE,
			WorkflowConstants.STATUS_DRAFT);

		testGetCreationMenu(
			ReflectionTestUtil.invoke(
				_getViewFilesSectionDisplayContext(getMockHttpServletRequest()),
				"getCreationMenu", new Class<?>[0]),
			expectedResultMap);
	}

	@Test
	public void testGetCreationMenuWithAddEntryPermission() throws Exception {
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			ObjectEntryFolder objectEntryFolder =
				_objectEntryFolderLocalService.addObjectEntryFolder(
					null, TestPropsValues.getUserId(), group.getGroupId(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					RandomTestUtil.randomString(),
					HashMapBuilder.put(
						LocaleUtil.getDefault(), StringUtil.randomString()
					).build(),
					StringUtil.randomString(),
					ServiceContextTestUtil.getServiceContext());

			_setUser(UserTestUtil.addUser());

			CreationMenu creationMenu = _getCreationMenu(objectEntryFolder);

			List<DropdownItem> primaryItems =
				(List<DropdownItem>)creationMenu.get("primaryItems");

			Assert.assertEquals(
				primaryItems.toString(), 1, primaryItems.size());

			Role role = _roleLocalService.getRole(
				TestPropsValues.getCompanyId(), RoleConstants.USER);

			_resourcePermissionLocalService.setResourcePermissions(
				TestPropsValues.getCompanyId(),
				ObjectEntryFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntryFolder.getObjectEntryFolderId()),
				role.getRoleId(), new String[] {ActionKeys.ADD_ENTRY});

			creationMenu = _getCreationMenu(objectEntryFolder);

			primaryItems = (List<DropdownItem>)creationMenu.get("primaryItems");

			Assert.assertTrue(primaryItems.size() > 1);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	private CreationMenu _getCreationMenu(ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_getViewFilesSectionDisplayContext(
				_getMockHttpServletRequest(objectEntryFolder)),
			"getCreationMenu", new Class<?>[0]);
	}

	private HttpServletRequest _getMockHttpServletRequest(
			ObjectEntryFolder objectEntryFolder)
		throws Exception {

		HttpServletRequest httpServletRequest = getMockHttpServletRequest();

		httpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, objectEntryFolder);

		return httpServletRequest;
	}

	private Object _getViewFilesSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		Object filesSectionDisplayContext = httpServletRequest.getAttribute(
			"com.liferay.site.cms.site.initializer.internal.display.context." +
				"ViewFilesSectionDisplayContext");

		Assert.assertNotNull(filesSectionDisplayContext);

		return filesSectionDisplayContext;
	}

	private void _setUser(User user) throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());
	}

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewFilesJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}