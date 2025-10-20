/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
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
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

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
				"http://localhost:8080/path-friendly-url-public/forms/shared/-",
				"/form/", ddmFormInstance.getFormInstanceId(), "?_",
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

	private HttpServletRequest _getHttpServletRequest() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());
		httpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, "http://localhost:8080/currentURL");

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPathFriendlyURLPublic("/path-friendly-url-public");
		themeDisplay.setPortalURL("http://localhost:8080");
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setResponse(new MockHttpServletResponse());
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return httpServletRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DDMFormInstanceLocalService _ddmFormInstanceLocalService;

	@Inject
	private DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Inject
	private WorkflowInstanceManager _workflowInstanceManager;

}