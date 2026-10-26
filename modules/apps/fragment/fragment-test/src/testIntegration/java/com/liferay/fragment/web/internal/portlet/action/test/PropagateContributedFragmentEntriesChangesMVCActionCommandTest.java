/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.fragment.configuration.FragmentServiceConfiguration;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class PropagateContributedFragmentEntriesChangesMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testProcessActionWithCompanyScope() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_processAction(
				ExtendedObjectClassDefinition.Scope.COMPANY.getValue(),
				company.getCompanyId());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(PrincipalException.class, throwable.getClass());
			Assert.assertEquals(
				"Invalid scope primary key for company " +
					TestPropsValues.getCompanyId(),
				throwable.getMessage());
		}

		FragmentServiceConfiguration fragmentServiceConfiguration =
			_configurationProvider.getCompanyConfiguration(
				FragmentServiceConfiguration.class, company.getCompanyId());

		Assert.assertFalse(fragmentServiceConfiguration.propagateChanges());

		_processAction(
			ExtendedObjectClassDefinition.Scope.COMPANY.getValue(),
			TestPropsValues.getCompanyId());

		fragmentServiceConfiguration =
			_configurationProvider.getCompanyConfiguration(
				FragmentServiceConfiguration.class,
				TestPropsValues.getCompanyId());

		Assert.assertTrue(fragmentServiceConfiguration.propagateChanges());

		_configurationProvider.deleteCompanyConfiguration(
			FragmentServiceConfiguration.class, TestPropsValues.getCompanyId());

		_companyLocalService.deleteCompany(company);
	}

	@Test
	public void testProcessActionWithSystemScope() throws Exception {
		User user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_processAction(
				ExtendedObjectClassDefinition.Scope.SYSTEM.getValue(), 0);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(
				PrincipalException.MustBeOmniadmin.class, throwable.getClass());
		}

		FragmentServiceConfiguration fragmentServiceConfiguration =
			_configurationProvider.getSystemConfiguration(
				FragmentServiceConfiguration.class);

		Assert.assertFalse(fragmentServiceConfiguration.propagateChanges());
	}

	private void _processAction(String scope, long scopePK) throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			PortletConfigFactoryUtil.create(
				_portletLocalService.getPortletById(
					ConfigurationAdminPortletKeys.INSTANCE_SETTINGS),
				null));
		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());
		mockLiferayPortletActionRequest.setParameter(
			"propagateChanges", Boolean.TRUE.toString());
		mockLiferayPortletActionRequest.setParameter("scope", scope);
		mockLiferayPortletActionRequest.setParameter(
			"scopePK", String.valueOf(scopePK));

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "processAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());
	}

	private static final String _CLASS_NAME =
		"com.liferay.fragment.web.internal.portlet.action." +
			"PropagateContributedFragmentEntriesChangesMVCActionCommand";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationProvider _configurationProvider;

	@Inject(
		filter = "mvc.command.name=/instance_settings/propagate_contributed_fragment_entries_changes"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private PortletLocalService _portletLocalService;

}