/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.company.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.db.partition.test.util.BaseDBPartitionTestCase;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.service.SXPBlueprintLocalService;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class CompanyLocalServiceDBPartitionSXPBlueprintTest
	extends BaseDBPartitionTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			TransactionalTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDBPartitionTestCase.setUpClass();

		_safeCloseable = CompanyThreadLocal.setCompanyIdWithSafeCloseable(
			PortalInstancePool.getDefaultCompanyId());
	}

	@AfterClass
	public static void tearDownClass() {
		if (_safeCloseable != null) {
			_safeCloseable.close();
		}
	}

	@Test
	public void testCopyDBPartitionCompany() throws Exception {
		Company copiedCompany = null;

		try (SafeCloseable safeCloseable1 =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					TestPropsValues.getCompanyId())) {

			_objectDefinition =
				ObjectDefinitionTestUtil.publishObjectDefinition();

			_sxpBlueprint = _sxpBlueprintLocalService.addSXPBlueprint(
				null, TestPropsValues.getUserId(),
				JSONUtil.put(
					"generalConfiguration",
					JSONUtil.put(
						"searchableAssetTypes",
						JSONUtil.put(_objectDefinition.getClassName()))
				).toString(),
				Collections.emptyMap(), StringPool.BLANK, StringPool.BLANK,
				RandomTestUtil.randomLocaleStringMap(),
				ServiceContextTestUtil.getServiceContext());

			copiedCompany = companyLocalService.copyDBPartitionCompany(
				TestPropsValues.getCompanyId(), null,
				RandomTestUtil.randomString(),
				StringUtil.toLowerCase(RandomTestUtil.randomString()),
				RandomTestUtil.randomString());

			try (SafeCloseable safeCloseable2 =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						copiedCompany.getCompanyId())) {

				ObjectDefinition copiedObjectDefinition =
					_objectDefinitionLocalService.fetchObjectDefinition(
						_objectDefinition.getObjectDefinitionId());

				SXPBlueprint copiedSXPBlueprint =
					_sxpBlueprintLocalService.getSXPBlueprint(
						_sxpBlueprint.getSXPBlueprintId());

				Assert.assertEquals(
					StringUtil.replace(
						_sxpBlueprint.getConfigurationJSON(),
						_objectDefinition.getClassName(),
						copiedObjectDefinition.getClassName()),
					copiedSXPBlueprint.getConfigurationJSON());
			}
		}
		finally {
			if (copiedCompany != null) {
				companyLocalService.deleteCompany(copiedCompany.getCompanyId());
			}
		}
	}

	private static SafeCloseable _safeCloseable;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private SXPBlueprint _sxpBlueprint;

	@Inject
	private SXPBlueprintLocalService _sxpBlueprintLocalService;

}