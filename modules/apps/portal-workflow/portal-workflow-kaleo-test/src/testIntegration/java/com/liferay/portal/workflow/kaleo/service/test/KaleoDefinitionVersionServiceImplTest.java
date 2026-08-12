/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionService;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Selton Guedes
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class KaleoDefinitionVersionServiceImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetKaleoDefinitionVersion() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		PortalInstances.initCompany(company);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		KaleoDefinition kaleoDefinition =
			_kaleoDefinitionLocalService.addKaleoDefinition(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_read(), StringPool.BLANK, false, 1, serviceContext);

		_kaleoDefinitionLocalService.activateKaleoDefinition(
			kaleoDefinition.getKaleoDefinitionId(), serviceContext);

		User user = UserTestUtil.addUser(
			company.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new long[0], serviceContext);

		_setUpPermissionThreadLocal(user);

		AssertUtils.assertFailure(
			PrincipalException.MustBeCompanyAdmin.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must be the company ",
				"administrator to perform the action"),
			() -> _kaleoDefinitionVersionService.getKaleoDefinitionVersion(
				kaleoDefinition.getCompanyId(), kaleoDefinition.getName(),
				_getVersion(kaleoDefinition.getVersion())));

		User companyAdminUser = UserTestUtil.addCompanyAdminUser(company);

		_setUpPermissionThreadLocal(companyAdminUser);

		Assert.assertNotNull(
			_kaleoDefinitionVersionService.getKaleoDefinitionVersion(
				kaleoDefinition.getCompanyId(), kaleoDefinition.getName(),
				_getVersion(kaleoDefinition.getVersion())));
	}

	private String _getVersion(int version) {
		return version + StringPool.PERIOD + 0;
	}

	private String _read() throws Exception {
		ClassLoader classLoader =
			BaseKaleoLocalServiceTestCase.class.getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(
				"com/liferay/portal/workflow/kaleo/dependencies" +
					"/legal-marketing-workflow-definition.xml")) {

			return StringUtil.read(inputStream);
		}
	}

	private void _setUpPermissionThreadLocal(User user) {
		PrincipalThreadLocal.setName(user.getUserId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
	}

	@Inject
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Inject
	private KaleoDefinitionVersionService _kaleoDefinitionVersionService;

}