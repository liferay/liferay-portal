/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class AddFormInstanceRecordMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), 0);
		_mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setRequest(
			_mockLiferayPortletActionRequest.getHttpServletRequest());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testProcessAction() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormTestUtil.createAvailableLocales(
				LocaleUtil.BRAZIL, LocaleUtil.US),
			LocaleUtil.US);

		DDMFormTestUtil.addDDMFormRule(
			Collections.singletonList("setVisible('TextField2', true)"),
			"not(isEmpty(getValue('TextField1')))", ddmForm);
		DDMFormTestUtil.addTextDDMFormFields(
			ddmForm, "TextField1", "TextField2");

		DDMFormInstance ddmFormInstance =
			DDMFormInstanceTestUtil.addDDMFormInstance(
				ddmForm, _group,
				DDMFormInstanceTestUtil.createSettingsDDMFormValues(false),
				TestPropsValues.getUserId());

		String value1 = RandomTestUtil.randomString();

		_mockLiferayPortletActionRequest.addParameter(
			"ddm$$TextField1$1$0$$pt_BR", value1);

		String value2 = RandomTestUtil.randomString();

		_mockLiferayPortletActionRequest.addParameter(
			"ddm$$TextField2$2$0$$pt_BR", value2);

		_mockLiferayPortletActionRequest.addParameter(
			"defaultLanguageId", "pt_BR");
		_mockLiferayPortletActionRequest.addParameter(
			"formInstanceId",
			String.valueOf(ddmFormInstance.getFormInstanceId()));

		_addFormInstanceRecordMVCActionCommand.processAction(
			_mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		List<DDMFormInstanceRecord> ddmFormInstanceRecords =
			_ddmFormInstanceRecordLocalService.getFormInstanceRecords(
				ddmFormInstance.getFormInstanceId());

		Assert.assertEquals(
			String.valueOf(ddmFormInstanceRecords), 1,
			ddmFormInstanceRecords.size());

		DDMFormInstanceRecord ddmFormInstanceRecord =
			ddmFormInstanceRecords.get(0);

		DDMFormValues ddmFormValues = ddmFormInstanceRecord.getDDMFormValues();

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(false);

		_assertValue("TextField1", ddmFormFieldValuesMap, value1);
		_assertValue("TextField2", ddmFormFieldValuesMap, value2);

		_mockLiferayPortletActionRequest.addParameter(
			"ddm$$TextField1$1$0$$pt_BR", StringPool.BLANK);

		String value3 = RandomTestUtil.randomString();

		_mockLiferayPortletActionRequest.addParameter(
			"ddm$$TextField2$2$0$$pt_BR", value3);

		_mockLiferayPortletActionRequest.addParameter(
			"formInstanceRecordId",
			String.valueOf(ddmFormInstanceRecord.getFormInstanceRecordId()));

		_addFormInstanceRecordMVCActionCommand.processAction(
			_mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		ddmFormInstanceRecords =
			_ddmFormInstanceRecordLocalService.getFormInstanceRecords(
				ddmFormInstance.getFormInstanceId());

		ddmFormInstanceRecord = ddmFormInstanceRecords.get(0);

		ddmFormValues = ddmFormInstanceRecord.getDDMFormValues();

		_assertValue(
			"TextField2", ddmFormValues.getDDMFormFieldValuesMap(false),
			value2);
	}

	private void _assertValue(
		String ddmFormFieldName,
		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap,
		String value) {

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			ddmFormFieldName);

		Assert.assertEquals(
			String.valueOf(ddmFormFieldValues), 1, ddmFormFieldValues.size());

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		LocalizedValue localizedValue =
			(LocalizedValue)ddmFormFieldValue.getValue();

		Assert.assertEquals(value, localizedValue.getString(LocaleUtil.BRAZIL));
	}

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequest()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		themeDisplay.setCompany(company);

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId());

		themeDisplay.setLayout(layout);

		themeDisplay.setLayoutSet(
			_layoutSetLocalService.getLayoutSet(_group.getGroupId(), false));
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());
		themeDisplay.setLocale(LocaleUtil.getSiteDefault());
		themeDisplay.setPortalDomain(company.getVirtualHostname());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setTimeZone(TimeZoneUtil.getDefault());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject(
		filter = "mvc.command.name=/dynamic_data_mapping_form/add_form_instance_record"
	)
	private MVCActionCommand _addFormInstanceRecordMVCActionCommand;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;

	private Group _group;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	private MockLiferayPortletActionRequest _mockLiferayPortletActionRequest;

}