/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class EditContentItemStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testExecute() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		ObjectEntry objectEntry = _addObjectEntry(depotEntry);

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS));
		long messageId = RandomTestUtil.randomLong();

		MockHttpServletResponse mockHttpServletResponse = _execute(
			layout, messageId, objectEntry, TestPropsValues.getUser());

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());
		String redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertEquals(
			StringBundler.concat(
				"/web/cms/e/basic-web-content/",
				_portal.getClassNameId(objectDefinition.getClassName()), "/",
				objectEntry.getObjectEntryId()),
			HttpComponentsUtil.getPath(redirectedURL));
		Assert.assertEquals(
			String.valueOf(messageId),
			HttpComponentsUtil.getParameter(redirectedURL, "messageId", false));
		Assert.assertEquals(
			Constants.READ,
			HttpComponentsUtil.getParameter(redirectedURL, "p_l_mode", false));

		_layoutLocalService.deleteLayout(layout);

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	@Test
	@TestInfo("LPD-113989")
	public void testExecuteWhenObjectEntryIsDeleted() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		ObjectEntry objectEntry = _addObjectEntry(depotEntry);

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS));

		MockHttpServletResponse mockHttpServletResponse = _execute(
			layout, RandomTestUtil.randomLong(), objectEntry,
			TestPropsValues.getUser());

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND,
			mockHttpServletResponse.getStatus());

		_layoutLocalService.deleteLayout(layout);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	@Test
	@TestInfo("LPD-113989")
	public void testExecuteWithoutViewPermission() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		ObjectEntry objectEntry = _addObjectEntry(depotEntry);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());
		Role role = _roleLocalService.getRole(
			objectEntry.getCompanyId(), RoleConstants.USER);

		_resourcePermissionLocalService.setResourcePermissions(
			objectEntry.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry.getObjectEntryId()), role.getRoleId(),
			new String[0]);

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS));
		User user = UserTestUtil.addUser();

		MockHttpServletResponse mockHttpServletResponse = _execute(
			layout, RandomTestUtil.randomLong(), objectEntry, user);

		Assert.assertEquals(
			HttpServletResponse.SC_FORBIDDEN,
			mockHttpServletResponse.getStatus());

		_layoutLocalService.deleteLayout(layout);

		_userLocalService.deleteUser(user);

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	private DepotEntry _addDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _addObjectEntry(DepotEntry depotEntry)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", depotEntry.getCompanyId());
		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					"L_CONTENTS", depotEntry.getGroupId(),
					depotEntry.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(),
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.put(
					LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
					RandomTestUtil.randomString()
				).build()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				depotEntry.getGroupId(), TestPropsValues.getUserId()));
	}

	private MockHttpServletResponse _execute(
			Layout layout, long messageId, ObjectEntry objectEntry, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				layout.getGroup(), layout);

		mockHttpServletRequest.setParameter(
			"messageId", String.valueOf(messageId));
		mockHttpServletRequest.setParameter(
			"objectEntryId", String.valueOf(objectEntry.getObjectEntryId()));
		mockHttpServletRequest.setParameter("p_l_mode", Constants.READ);
		mockHttpServletRequest.setRequestURI(layout.getFriendlyURL());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		themeDisplay.setPermissionChecker(permissionChecker);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			_editContentItemStrutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse);
		}

		return mockHttpServletResponse;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(filter = "path=/cms/edit_content_item")
	private StrutsAction _editContentItemStrutsAction;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}