/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinitionField;
import com.liferay.data.engine.rest.resource.exception.DataDefinitionValidationException;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.data.engine.rest.test.util.DataDefinitionTestUtil;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryTypeException;
import com.liferay.document.library.kernel.exception.RequiredFileEntryTypeException;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.Portlet;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class EditFileEntryTypeMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_dataDefinitionJSON = new String(
			FileUtil.getBytes(getClass(), "dependencies/data_definition.json"));
		_group = GroupTestUtil.addGroup();

		_dataDefinition = DataDefinitionTestUtil.addDataDefinition(
			"document-library", _dataDefinitionResourceFactory,
			_group.getGroupId(), _dataDefinitionJSON,
			TestPropsValues.getUser());

		_dlFileEntryType = _dlFileEntryTypeLocalService.addFileEntryType(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_dataDefinition.getId(), null,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test
	public void testProcessAction() throws Exception {
		_testProcessActionDataDefinitionValidationException();
		_testProcessActionNoSuchFileEntryTypeException();
		_testProcessActionNoSuchStructureException();
		_testProcessActionPrincipalException();
		_testProcessActionRequiredFileEntryTypeException();
	}

	private void _assertDLFileEntryType() {
		DDMStructure ddmStructure = _ddmStructureLocalService.fetchStructure(
			_dataDefinition.getId());

		Assert.assertNotNull(ddmStructure);

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.fetchFileEntryType(
				_dlFileEntryType.getFileEntryTypeId());

		Assert.assertEquals(
			_dlFileEntryType.getName(LocaleUtil.US),
			dlFileEntryType.getName(LocaleUtil.US));
	}

	private void _assertProcessAction(
			Class<?> exceptionClass, String mvcPath, String navigation,
			Map<String, String> parameters)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			mockLiferayPortletActionRequest.addParameter(
				entry.getKey(), entry.getValue());
		}

		mockLiferayPortletActionRequest.addParameter(
			"redirect", "/" + RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter(
			ActionRequest.ACTION_NAME,
			"/document_library/edit_file_entry_type");

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setScopeGroupId(_group.getGroupId());

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		themeDisplay.setUser(permissionChecker.getUser());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		RecordingMockLiferayPortletActionResponse
			recordingMockLiferayPortletActionResponse =
				new RecordingMockLiferayPortletActionResponse();

		_portlet.processAction(
			mockLiferayPortletActionRequest,
			recordingMockLiferayPortletActionResponse);

		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletActionRequest, exceptionClass));

		Assert.assertEquals(
			mvcPath,
			recordingMockLiferayPortletActionResponse.getRenderParameter(
				"mvcPath"));
		Assert.assertEquals(
			navigation,
			recordingMockLiferayPortletActionResponse.getRenderParameter(
				"navigation"));
		Assert.assertNull(
			recordingMockLiferayPortletActionResponse.getRedirect());
	}

	private Map<String, String> _getUpdateParameters(
		DataDefinition dataDefinition, long dataDefinitionId,
		long fileEntryTypeId) {

		return HashMapBuilder.put(
			Constants.CMD, Constants.UPDATE
		).put(
			"dataDefinition", dataDefinition.toString()
		).put(
			"dataDefinitionId", String.valueOf(dataDefinitionId)
		).put(
			"dataLayout", String.valueOf(_dataDefinition.getDefaultDataLayout())
		).put(
			"fileEntryTypeId", String.valueOf(fileEntryTypeId)
		).put(
			"name_en_US", RandomTestUtil.randomString()
		).build();
	}

	private void _testProcessActionDataDefinitionValidationException()
		throws Exception {

		DataDefinition dataDefinition = DataDefinition.toDTO(
			_dataDefinitionJSON);

		dataDefinition.setDataDefinitionFields(new DataDefinitionField[0]);

		_assertProcessAction(
			DataDefinitionValidationException.MustSetFields.class, null, null,
			HashMapBuilder.put(
				Constants.CMD, Constants.ADD
			).put(
				"dataDefinition", dataDefinition.toString()
			).put(
				"name_en_US", RandomTestUtil.randomString()
			).build());

		List<DLFileEntryType> dlFileEntryTypes =
			_dlFileEntryTypeLocalService.getFileEntryTypes(
				new long[] {_group.getGroupId()});

		Assert.assertEquals(
			dlFileEntryTypes.toString(), 1, dlFileEntryTypes.size());
	}

	private void _testProcessActionNoSuchFileEntryTypeException()
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			_dataDefinition.getId());

		String description = ddmStructure.getDescription(LocaleUtil.US);

		DataDefinition dataDefinition = DataDefinition.toDTO(
			_dataDefinitionJSON);

		dataDefinition.setDescription(
			HashMapBuilder.<String, Object>put(
				LocaleUtil.toLanguageId(LocaleUtil.US),
				RandomTestUtil.randomString()
			).build());

		_assertProcessAction(
			NoSuchFileEntryTypeException.class, "/document_library/error.jsp",
			null,
			_getUpdateParameters(
				dataDefinition, _dataDefinition.getId(),
				RandomTestUtil.nextLong()));

		ddmStructure = _ddmStructureLocalService.getStructure(
			_dataDefinition.getId());

		Assert.assertEquals(
			description, ddmStructure.getDescription(LocaleUtil.US));
	}

	private void _testProcessActionNoSuchStructureException() throws Exception {
		_assertProcessAction(
			NoSuchStructureException.class, "/document_library/error.jsp", null,
			_getUpdateParameters(
				DataDefinition.toDTO(_dataDefinitionJSON),
				RandomTestUtil.nextLong(),
				_dlFileEntryType.getFileEntryTypeId()));

		_assertDLFileEntryType();
	}

	private void _testProcessActionPrincipalException() throws Exception {
		_user = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_assertProcessAction(
				PrincipalException.MustHavePermission.class,
				"/document_library/error.jsp", null,
				HashMapBuilder.put(
					Constants.CMD, Constants.DELETE
				).put(
					"fileEntryTypeId",
					String.valueOf(_dlFileEntryType.getFileEntryTypeId())
				).build());
		}

		_assertDLFileEntryType();
	}

	private void _testProcessActionRequiredFileEntryTypeException()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAttribute(
			"fileEntryTypeId", _dlFileEntryType.getFileEntryTypeId());

		_dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, RandomTestUtil.randomBytes(), null, null, null,
			serviceContext);

		_assertProcessAction(
			RequiredFileEntryTypeException.class, null, "file_entry_types",
			HashMapBuilder.put(
				Constants.CMD, Constants.DELETE
			).put(
				"fileEntryTypeId",
				String.valueOf(_dlFileEntryType.getFileEntryTypeId())
			).build());

		_assertDLFileEntryType();
	}

	private DataDefinition _dataDefinition;
	private String _dataDefinitionJSON;

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private DLFileEntryType _dlFileEntryType;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN
	)
	private Portlet _portlet;

	@DeleteAfterTestRun
	private User _user;

	private static class RecordingMockLiferayPortletActionResponse
		extends MockLiferayPortletActionResponse {

		public String getRedirect() {
			return _redirect;
		}

		public String getRenderParameter(String name) {
			String[] values = _renderParameterMap.get(name);

			if (values == null) {
				return null;
			}

			return values[0];
		}

		@Override
		public Map<String, String[]> getRenderParameterMap() {
			return _renderParameterMap;
		}

		@Override
		public void sendRedirect(String location) {
			_redirect = location;
		}

		@Override
		public void setRenderParameter(String name, String value) {
			_renderParameterMap.put(name, new String[] {value});
		}

		@Override
		public void setRenderParameter(String name, String... values) {
			_renderParameterMap.put(name, values);
		}

		private String _redirect;
		private final Map<String, String[]> _renderParameterMap =
			new HashMap<>();

	}

}