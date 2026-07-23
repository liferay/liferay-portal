/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.security.permission.resource.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.HttpMethod;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Manuele Castro
 */
@RunWith(Arquillian.class)
public class ObjectDLFileEntryModelResourcePermissionConfiguratorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		_objectField = ObjectFieldUtil.createObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
			ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
			RandomTestUtil.randomString(), "attachment",
			Arrays.asList(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS
				).value(
					"jpg, jpeg, png, svg, txt"
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_FILE_SOURCE
				).value(
					ObjectFieldSettingConstants.
						VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
				).value(
					"100"
				).build()),
			false);

		_objectField.setExternalReferenceCode(RandomTestUtil.randomString());

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(_objectField));

		FileEntry tempFileEntry = TempFileEntryUtil.addTempFileEntry(
			TestPropsValues.getGroupId(), TestPropsValues.getUserId(),
			_objectDefinition.getPortletId(),
			TempFileEntryUtil.getTempFileName("image.jpg"),
			FileUtil.createTempFile(RandomTestUtil.randomBytes()),
			ContentTypes.APPLICATION_TEXT);

		_objectEntry = _objectEntryLocalService.addOrUpdateObjectEntry(
			RandomTestUtil.randomString(), 0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			HashMapBuilder.<String, Serializable>put(
				"attachment", tempFileEntry.getFileEntryId()
			).build(),
			serviceContext);

		Map<String, Serializable> values = _objectEntry.getValues();

		long fileEntryId = GetterUtil.getLong(values.get("attachment"));

		_dlFileEntry = _dlFileEntryLocalService.getDLFileEntry(fileEntryId);

		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		_role = _roleLocalService.getRole(
			_company.getCompanyId(), RoleConstants.GUEST);

		_themeDisplay.setCompany(_company);

		_user = UserLocalServiceUtil.getGuestUser(_company.getCompanyId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		PrincipalThreadLocal.setName(_user.getUserId());

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(
				HttpMethod.GET,
				ObjectFieldUtil.getAttachmentDownloadURL(
					_dlURLHelper, fileEntry, 0,
					_objectDefinition.getExternalReferenceCode(), _objectEntry,
					_objectEntryService, _objectField,
					GuestOrUserUtil.getPermissionChecker(), _themeDisplay));

		mockHttpServletRequest.addParameter("download", "true");
		mockHttpServletRequest.addParameter(
			"objectDefinitionExternalReferenceCode",
			_objectDefinition.getExternalReferenceCode());
		mockHttpServletRequest.addParameter(
			"objectEntryExternalReferenceCode",
			_objectEntry.getExternalReferenceCode());
		mockHttpServletRequest.addParameter(
			"objectFieldExternalReferenceCode",
			_objectField.getExternalReferenceCode());

		serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	@After
	public void tearDown() throws PortalException {
		_objectDefinitionLocalService.deleteObjectDefinition(_objectDefinition);

		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		PrincipalThreadLocal.setName(_originalName);

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testContains() throws Exception {
		String attachmentDownloadActionKey =
			_objectField.getAttachmentDownloadActionKey();

		Assert.assertFalse(
			_dlFileEntryModelResourcePermission.contains(
				PermissionCheckerFactoryUtil.create(_user), _dlFileEntry,
				ActionKeys.DOWNLOAD));

		_testContains(new String[] {ActionKeys.VIEW}, false);
		_testContains(new String[] {attachmentDownloadActionKey}, false);
		_testContains(
			new String[] {ActionKeys.VIEW, attachmentDownloadActionKey}, true);

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(HttpMethod.GET, StringPool.BLANK);

		mockHttpServletRequest.addParameter("download", "true");

		serviceContext.setRequest(mockHttpServletRequest);

		_testContains(new String[0], false);
		_testContains(new String[] {ActionKeys.VIEW}, false);
		_testContains(new String[] {attachmentDownloadActionKey}, false);
		_testContains(
			new String[] {ActionKeys.VIEW, attachmentDownloadActionKey}, true);

		_resourcePermissionLocalService.setResourcePermissions(
			_company.getCompanyId(), DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_dlFileEntry.getFileEntryId()), _role.getRoleId(),
			new String[0]);

		Assert.assertFalse(
			_dlFileEntryModelResourcePermission.contains(
				GuestOrUserUtil.getPermissionChecker(), _dlFileEntry,
				ActionKeys.DOWNLOAD));
	}

	private void _testContains(String[] actionIds, boolean expectedResult)
		throws Exception {

		_resourcePermissionLocalService.setResourcePermissions(
			_company.getCompanyId(), _objectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_objectEntry.getObjectEntryId()), _role.getRoleId(),
			actionIds);

		Assert.assertEquals(
			expectedResult,
			_dlFileEntryModelResourcePermission.contains(
				GuestOrUserUtil.getPermissionChecker(), _dlFileEntry,
				ActionKeys.DOWNLOAD));
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private DLFileEntry _dlFileEntry;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject(
		filter = "model.class.name=com.liferay.document.library.kernel.model.DLFileEntry"
	)
	private ModelResourcePermission<DLFileEntry>
		_dlFileEntryModelResourcePermission;

	@Inject
	private DLURLHelper _dlURLHelper;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectEntry _objectEntry;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectEntryService _objectEntryService;

	private ObjectField _objectField;
	private String _originalName;
	private PermissionChecker _originalPermissionChecker;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	private final ThemeDisplay _themeDisplay = new ThemeDisplay();
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}