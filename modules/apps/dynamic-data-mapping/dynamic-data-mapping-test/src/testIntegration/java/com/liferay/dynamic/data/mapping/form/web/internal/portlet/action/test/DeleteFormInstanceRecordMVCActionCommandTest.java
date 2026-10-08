/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceReport;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceReportLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceRecordTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Akhash Ramprakash
 */
@RunWith(Arquillian.class)
public class DeleteFormInstanceRecordMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_ddmFormInstance = DDMFormInstanceTestUtil.addDDMFormInstance(
			_group, TestPropsValues.getUserId());
	}

	@After
	public void tearDown() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testProcessAction() throws Exception {
		DDMFormInstanceRecord ddmFormInstanceRecord1 =
			DDMFormInstanceRecordTestUtil.
				addDDMFormInstanceRecordWithRandomValues(
					_ddmFormInstance, _group, TestPropsValues.getUserId());
		DDMFormInstanceRecord ddmFormInstanceRecord2 =
			DDMFormInstanceRecordTestUtil.
				addDDMFormInstanceRecordWithRandomValues(
					_ddmFormInstance, _group, TestPropsValues.getUserId());
		DDMFormInstanceRecord ddmFormInstanceRecord3 =
			DDMFormInstanceRecordTestUtil.
				addDDMFormInstanceRecordWithRandomValues(
					_ddmFormInstance, _group, TestPropsValues.getUserId());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(TestPropsValues.getUser());

		mockLiferayPortletActionRequest.addParameter(
			"deleteFormInstanceRecordIds",
			StringUtil.merge(
				new long[] {
					ddmFormInstanceRecord1.getFormInstanceRecordId(),
					ddmFormInstanceRecord2.getFormInstanceRecordId(),
					ddmFormInstanceRecord3.getFormInstanceRecordId()
				}));

		Bundle bundle = FrameworkUtil.getBundle(
			DeleteFormInstanceRecordMVCActionCommandTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			(Class<ModelListener<DDMFormInstanceRecord>>)
				(Class<?>)ModelListener.class,
			new BaseModelListener<DDMFormInstanceRecord>() {

				@Override
				public void onBeforeRemove(
						DDMFormInstanceRecord ddmFormInstanceRecord)
					throws ModelListenerException {

					if (ddmFormInstanceRecord.getFormInstanceRecordId() ==
							ddmFormInstanceRecord2.getFormInstanceRecordId()) {

						throw new ModelListenerException();
					}
				}

			},
			null);

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertNull(
			_ddmFormInstanceRecordLocalService.fetchFormInstanceRecord(
				ddmFormInstanceRecord1.getFormInstanceRecordId()));
		Assert.assertNotNull(
			_ddmFormInstanceRecordLocalService.fetchFormInstanceRecord(
				ddmFormInstanceRecord2.getFormInstanceRecordId()));
		Assert.assertNull(
			_ddmFormInstanceRecordLocalService.fetchFormInstanceRecord(
				ddmFormInstanceRecord3.getFormInstanceRecordId()));
	}

	@Test
	public void testProcessActionWithSelectAll() throws Exception {

		// With permission

		for (int i = 0; i < 3; i++) {
			DDMFormInstanceRecordTestUtil.
				addDDMFormInstanceRecordWithRandomValues(
					_ddmFormInstance, _group, TestPropsValues.getUserId());
		}

		DDMFormInstance otherDDMFormInstance =
			DDMFormInstanceTestUtil.addDDMFormInstance(
				_group, TestPropsValues.getUserId());

		DDMFormInstanceRecordTestUtil.addDDMFormInstanceRecordWithRandomValues(
			otherDDMFormInstance, _group, TestPropsValues.getUserId());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(TestPropsValues.getUser());

		String redirect = StringPool.SLASH + RandomTestUtil.randomString();

		mockLiferayPortletActionRequest.addParameter("redirect", redirect);

		mockLiferayPortletActionRequest.addParameter("selectAll", "true");

		MockActionResponse mockActionResponse = new MockActionResponse();

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest, mockActionResponse);

		Assert.assertEquals(redirect, mockActionResponse.getRedirectedUrl());

		Assert.assertEquals(
			0,
			_ddmFormInstanceRecordLocalService.getFormInstanceRecordsCount(
				_ddmFormInstance.getFormInstanceId()));
		Assert.assertEquals(
			1,
			_ddmFormInstanceRecordLocalService.getFormInstanceRecordsCount(
				otherDDMFormInstance.getFormInstanceId()));

		DDMFormInstanceReport ddmFormInstanceReport =
			_ddmFormInstanceReportLocalService.
				getFormInstanceReportByFormInstanceId(
					_ddmFormInstance.getFormInstanceId());

		JSONObject ddmFormInstanceReportDataJSONObject =
			JSONFactoryUtil.createJSONObject(ddmFormInstanceReport.getData());

		Assert.assertEquals(
			0, ddmFormInstanceReportDataJSONObject.getInt("totalItems"));

		// Without permission

		DDMFormInstanceRecordTestUtil.addDDMFormInstanceRecordWithRandomValues(
			_ddmFormInstance, _group, TestPropsValues.getUserId());

		_user = UserTestUtil.addUser();

		mockLiferayPortletActionRequest = _getMockLiferayPortletActionRequest(
			_user);

		mockLiferayPortletActionRequest.addParameter("selectAll", "true");

		MockLiferayPortletActionRequest finalMockLiferayPortletActionRequest =
			mockLiferayPortletActionRequest;

		AssertUtils.assertFailure(
			PortletException.class,
			StringBundler.concat(
				PrincipalException.MustHavePermission.class.getName(),
				": User ", _user.getUserId(), " must have DELETE permission ",
				"for ", DDMFormInstance.class.getName(), " ",
				_ddmFormInstance.getFormInstanceId()),
			() -> _mvcActionCommand.processAction(
				finalMockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse()));

		Assert.assertEquals(
			1,
			_ddmFormInstanceRecordLocalService.getFormInstanceRecordsCount(
				_ddmFormInstance.getFormInstanceId()));
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"formInstanceId",
			String.valueOf(_ddmFormInstance.getFormInstanceId()));

		Portlet portlet = _portletLocalService.getPortletById(
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN);

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			PortletConfigFactoryUtil.create(portlet, null));

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN);

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(_group.getCompanyId()), _group,
			LayoutTestUtil.addTypePortletLayout(_group.getGroupId()));

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setUser(user);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletActionRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private DDMFormInstance _ddmFormInstance;

	@Inject
	private DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;

	@Inject
	private DDMFormInstanceReportLocalService
		_ddmFormInstanceReportLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "mvc.command.name=/dynamic_data_mapping_form/delete_form_instance_record"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private PortletLocalService _portletLocalService;

	private ServiceRegistration<ModelListener<DDMFormInstanceRecord>>
		_serviceRegistration;

	@DeleteAfterTestRun
	private User _user;

}