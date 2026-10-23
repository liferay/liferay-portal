/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMFormConstants;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.exception.FormInstanceSubmissionLimitException;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Rodrigo Paulino
 */
@RunWith(Arquillian.class)
public class DDMFormInstanceRecordLocalServiceTest
	extends BaseDDMServiceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddFormInstanceRecordWithFileEntry() throws Exception {
		User user = TestPropsValues.getUser();

		DDMFormInstance ddmFormInstance = _addDDMFormInstance(user);

		FileEntry tempFileEntry = _addTempFileEntry();

		DDMFormInstanceRecord ddmFormInstanceRecord =
			_ddmFormInstanceRecordLocalService.addFormInstanceRecord(
				user.getUserId(), group.getGroupId(),
				ddmFormInstance.getFormInstanceId(),
				_createDDMFormValues(
					ddmFormInstance.getDDMForm(), tempFileEntry),
				ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		Assert.assertNull(
			_dlFileEntryLocalService.fetchDLFileEntry(
				tempFileEntry.getFileEntryId()));

		JSONObject fileEntryJSONObject = _getFileEntryJSONObject(
			ddmFormInstanceRecord);

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.getDLFileEntry(
			fileEntryJSONObject.getLong("fileEntryId"));

		Assert.assertEquals(
			DDMFormInstanceRecord.class.getName(), dlFileEntry.getClassName());
		Assert.assertEquals(
			ddmFormInstanceRecord.getFormInstanceRecordId(),
			dlFileEntry.getClassPK());
		Assert.assertEquals(
			_ddmFormFolder.getFolderId(), dlFileEntry.getFolderId());
		Assert.assertEquals("file.txt", dlFileEntry.getTitle());

		User ddmFormDefaultUser = DDMFormUtil.getDDMFormDefaultUser(
			user.getCompanyId());

		Assert.assertEquals(
			ddmFormDefaultUser.getUserId(), dlFileEntry.getUserId());

		Assert.assertEquals("file.txt", fileEntryJSONObject.getString("title"));
		Assert.assertEquals(
			dlFileEntry.getUuid(), fileEntryJSONObject.getString("uuid"));

		Role role = _roleLocalService.getRole(
			user.getCompanyId(), RoleConstants.GUEST);

		Assert.assertTrue(
			ListUtil.isEmpty(
				_resourcePermissionLocalService.
					getAvailableResourcePermissionActionIds(
						user.getCompanyId(), DLFileEntry.class.getName(),
						ResourceConstants.SCOPE_INDIVIDUAL,
						String.valueOf(dlFileEntry.getFileEntryId()),
						role.getRoleId(),
						_resourceActions.getModelResourceActions(
							DLFileEntry.class.getName()))));
	}

	@Test
	public void testAddFormInstanceRecordWithLimitToOneSubmissionPerUser()
		throws Exception {

		User user = TestPropsValues.getUser();

		DDMFormInstance ddmFormInstance = _addDDMFormInstance(true, user);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				SqlExceptionHelper.class.getName(), LoggerTestUtil.OFF)) {

			_addFormInstanceRecords(ddmFormInstance, user);
		}

		Assert.assertEquals(
			1,
			_ddmFormInstanceRecordLocalService.getFormInstanceRecordsCount(
				ddmFormInstance.getFormInstanceId(), user.getUserId()));

		AssertUtils.assertFailure(
			FormInstanceSubmissionLimitException.class,
			StringBundler.concat(
				"User ", user.getUserId(), " has already submitted a form ",
				"instance record for form instance ",
				ddmFormInstance.getFormInstanceId()),
			() -> _ddmFormInstanceRecordLocalService.addFormInstanceRecord(
				user.getUserId(), user.getGroupId(),
				ddmFormInstance.getFormInstanceId(),
				new DDMFormValues(ddmFormInstance.getDDMForm()),
				new ServiceContext()));
	}

	@Test
	public void testAddFormInstanceRecordWithWorkflow() throws Exception {
		User user = TestPropsValues.getUser();

		DDMFormInstance ddmFormInstance =
			DDMFormInstanceTestUtil.addDDMFormInstance(
				user.getGroup(), user.getUserId());

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			user.getUserId(), TestPropsValues.getCompanyId(),
			ddmFormInstance.getGroupId(), DDMFormInstance.class.getName(),
			ddmFormInstance.getFormInstanceId(), 0, "Single Approver@1");

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setRequest(_getHttpServletRequest());

		DDMFormInstanceRecord ddmFormInstanceRecord =
			_ddmFormInstanceRecordLocalService.addFormInstanceRecord(
				user.getUserId(), user.getGroupId(),
				ddmFormInstance.getFormInstanceId(),
				DDMFormValuesTestUtil.createDDMFormValues(
					ddmFormInstance.getDDMForm()),
				serviceContext);

		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
			ddmFormInstanceRecord.getFormInstanceRecordVersion();

		WorkflowInstanceLink workflowInstanceLink =
			_workflowInstanceLinkLocalService.getWorkflowInstanceLink(
				ddmFormInstance.getCompanyId(), ddmFormInstance.getGroupId(),
				DDMFormInstanceRecord.class.getName(),
				ddmFormInstanceRecordVersion.getFormInstanceRecordVersionId());

		WorkflowInstance workflowInstance =
			_workflowInstanceManager.getWorkflowInstance(
				ddmFormInstance.getCompanyId(),
				workflowInstanceLink.getWorkflowInstanceId());

		Map<String, Serializable> workflowContext =
			workflowInstance.getWorkflowContext();

		Assert.assertEquals(
			ddmFormInstance.getName(user.getLocale()),
			workflowContext.get("entryTitle"));
		Assert.assertEquals(
			ddmFormInstance.getName(), workflowContext.get("entryTitleXML"));
		Assert.assertEquals(
			StringBundler.concat(
				"http://localhost:", PortalUtil.getPortalServerPort(false),
				"/path-friendly-url-public/forms/shared/-/form/",
				ddmFormInstance.getFormInstanceId(), "?_",
				DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM,
				"_formInstanceRecordId=",
				ddmFormInstanceRecord.getFormInstanceRecordId()),
			workflowContext.get(WorkflowConstants.CONTEXT_URL));
	}

	@Test
	public void testUpdateFormInstanceRecord() throws Exception {
		User user = TestPropsValues.getUser();

		DDMFormInstance ddmFormInstance =
			DDMFormInstanceTestUtil.addDDMFormInstance(
				user.getGroup(), user.getUserId());

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmFormInstance.getDDMForm());

		String string1 = RandomTestUtil.randomString();

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createLocalizedDDMFormFieldValue(
				"text", string1);

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DDMFormInstanceRecord ddmFormInstanceRecord =
			_ddmFormInstanceRecordLocalService.addFormInstanceRecord(
				user.getUserId(), user.getGroupId(),
				ddmFormInstance.getFormInstanceId(), ddmFormValues,
				serviceContext);

		_assertDDMFormInstanceRecord(ddmFormInstanceRecord, "1.0", string1);

		Value value = ddmFormFieldValue.getValue();

		String string2 = RandomTestUtil.randomString();

		value.addString(value.getDefaultLocale(), string2);

		_assertDDMFormInstanceRecord(
			_ddmFormInstanceRecordLocalService.updateFormInstanceRecord(
				user.getUserId(),
				ddmFormInstanceRecord.getFormInstanceRecordId(), false,
				ddmFormValues, serviceContext),
			"1.0", string2);

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		_ddmFormInstanceLocalService.updateFormInstance(
			ddmFormInstance.getFormInstanceId(), ddmStructure.getStructureId(),
			ddmFormInstance.getNameMap(), ddmFormInstance.getDescriptionMap(),
			ddmFormInstance.getSettingsDDMFormValues(), serviceContext);

		String string3 = RandomTestUtil.randomString();

		value.addString(value.getDefaultLocale(), string3);

		_assertDDMFormInstanceRecord(
			_ddmFormInstanceRecordLocalService.updateFormInstanceRecord(
				user.getUserId(),
				ddmFormInstanceRecord.getFormInstanceRecordId(), false,
				ddmFormValues, serviceContext),
			"1.1", string3);
	}

	private DDMFormInstance _addDDMFormInstance(
			boolean limitToOneSubmissionPerUser, User user)
		throws Exception {

		DDMFormInstance ddmFormInstance =
			DDMFormInstanceTestUtil.addDDMFormInstance(
				user.getGroup(), user.getUserId());

		DDMFormValues ddmFormValues =
			ddmFormInstance.getSettingsDDMFormValues();

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"limitToOneSubmissionPerUser",
				String.valueOf(limitToOneSubmissionPerUser)));

		return _ddmFormInstanceLocalService.updateFormInstance(
			ddmFormInstance.getFormInstanceId(), ddmFormValues);
	}

	private DDMFormInstance _addDDMFormInstance(User user) throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormValuesTestUtil.createAvailableLocales(LocaleUtil.US),
			LocaleUtil.US);

		DDMFormTestUtil.addDocumentLibraryDDMFormField(ddmForm, "upload");

		Repository repository = _portletFileRepository.addPortletRepository(
			group.getGroupId(), DDMFormConstants.SERVICE_NAME,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		_ddmFormFolder = _portletFileRepository.addPortletFolder(
			user.getUserId(), repository.getRepositoryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			DDMFormConstants.DDM_FORM_UPLOADED_FILES_FOLDER_NAME,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		return DDMFormInstanceTestUtil.addDDMFormInstance(
			ddmForm, group, user.getUserId());
	}

	private void _addFormInstanceRecords(
			DDMFormInstance ddmFormInstance, User user)
		throws Exception {

		ExecutorService executorService = Executors.newFixedThreadPool(
			_THREAD_COUNT);

		try {
			CountDownLatch finishCountDownLatch = new CountDownLatch(
				_THREAD_COUNT);
			CountDownLatch readyCountDownLatch = new CountDownLatch(
				_THREAD_COUNT);
			CountDownLatch startCountDownLatch = new CountDownLatch(1);

			for (int i = 0; i < _THREAD_COUNT; i++) {
				executorService.submit(
					new CompanyInheritableThreadLocalCallable<Runnable>(
						() -> {
							try {
								readyCountDownLatch.countDown();

								Assert.assertTrue(
									startCountDownLatch.await(
										30, TimeUnit.SECONDS));

								_ddmFormInstanceRecordLocalService.
									addFormInstanceRecord(
										user.getUserId(), user.getGroupId(),
										ddmFormInstance.getFormInstanceId(),
										new DDMFormValues(
											ddmFormInstance.getDDMForm()),
										new ServiceContext());
							}
							catch (FormInstanceSubmissionLimitException
										formInstanceSubmissionLimitException) {
							}
							finally {
								finishCountDownLatch.countDown();
							}

							return null;
						}));
			}

			Assert.assertTrue(readyCountDownLatch.await(30, TimeUnit.SECONDS));

			startCountDownLatch.countDown();

			Assert.assertTrue(finishCountDownLatch.await(60, TimeUnit.SECONDS));

			executorService.awaitTermination(30, TimeUnit.SECONDS);
		}
		finally {
			executorService.shutdown();
		}
	}

	private FileEntry _addTempFileEntry() throws Exception {
		User ddmFormDefaultUser = DDMFormUtil.getDDMFormDefaultUser(
			TestPropsValues.getCompanyId());

		return TempFileEntryUtil.addTempFileEntry(
			group.getGroupId(), ddmFormDefaultUser.getUserId(),
			RandomTestUtil.randomString(),
			TempFileEntryUtil.getTempFileName("file.txt"),
			new ByteArrayInputStream(TestDataConstants.TEST_BYTE_ARRAY),
			ContentTypes.TEXT_PLAIN);
	}

	private void _assertDDMFormInstanceRecord(
			DDMFormInstanceRecord ddmFormInstanceRecord,
			String expectedFormInstanceVersion, String expectedValue)
		throws Exception {

		Assert.assertEquals(
			expectedFormInstanceVersion,
			ddmFormInstanceRecord.getFormInstanceVersion());

		DDMFormValues ddmFormValues = ddmFormInstanceRecord.getDDMFormValues();

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(true);

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			"text");

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		Value value = ddmFormFieldValue.getValue();

		Assert.assertEquals(
			expectedValue, value.getString(value.getDefaultLocale()));
	}

	private DDMFormValues _createDDMFormValues(
		DDMForm ddmForm, FileEntry fileEntry) {

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createLocalizedDDMFormFieldValue(
				"upload",
				JSONUtil.put(
					"fileEntryId", fileEntry.getFileEntryId()
				).put(
					"groupId", fileEntry.getGroupId()
				).put(
					"title", "file.txt"
				).put(
					"uuid", fileEntry.getUuid()
				).toString()));

		return ddmFormValues;
	}

	private JSONObject _getFileEntryJSONObject(
			DDMFormInstanceRecord ddmFormInstanceRecord)
		throws Exception {

		DDMFormValues ddmFormValues = ddmFormInstanceRecord.getDDMFormValues();

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(true);

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			"upload");

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		Value value = ddmFormFieldValue.getValue();

		return JSONFactoryUtil.createJSONObject(
			value.getString(value.getDefaultLocale()));
	}

	private HttpServletRequest _getHttpServletRequest() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());

		int portalServerPort = PortalUtil.getPortalServerPort(false);

		httpServletRequest.setAttribute(
			WebKeys.CURRENT_URL,
			"http://localhost:" + portalServerPort + "/currentURL");

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPathFriendlyURLPublic("/path-friendly-url-public");
		themeDisplay.setPortalURL("http://localhost:" + portalServerPort);
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setResponse(new MockHttpServletResponse());
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return httpServletRequest;
	}

	private static final int _THREAD_COUNT = 8;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Folder _ddmFormFolder;

	@Inject
	private DDMFormInstanceLocalService _ddmFormInstanceLocalService;

	@Inject
	private DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private ResourceActions _resourceActions;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Inject
	private WorkflowInstanceManager _workflowInstanceManager;

}