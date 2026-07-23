/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.NoSuchCPTaxCategoryException;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class CPTaxCategoryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();
	}

	@Test
	public void testGetOrAddEmptyCPTaxCategory() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		try {
			_cpTaxCategoryLocalService.getOrAddEmptyCPTaxCategory(
				externalReferenceCode, _user.getCompanyId(), _user.getUserId());

			Assert.fail();
		}
		catch (NoSuchCPTaxCategoryException noSuchCPTaxCategoryException) {
			Assert.assertNotNull(noSuchCPTaxCategoryException);
		}

		CPTaxCategory cpTaxCategory = null;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			cpTaxCategory =
				_cpTaxCategoryLocalService.getOrAddEmptyCPTaxCategory(
					externalReferenceCode, _user.getCompanyId(),
					_user.getUserId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, cpTaxCategory.getStatus());
			Assert.assertEquals(
				externalReferenceCode,
				cpTaxCategory.getExternalReferenceCode());

			CPTaxCategory resolvedCPTaxCategory =
				_cpTaxCategoryLocalService.getOrAddEmptyCPTaxCategory(
					externalReferenceCode, _user.getCompanyId(),
					_user.getUserId());

			Assert.assertEquals(
				cpTaxCategory.getCPTaxCategoryId(),
				resolvedCPTaxCategory.getCPTaxCategoryId());
		}

		cpTaxCategory = _cpTaxCategoryLocalService.updateCPTaxCategory(
			cpTaxCategory.getExternalReferenceCode(),
			cpTaxCategory.getCPTaxCategoryId(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap());

		Assert.assertNotEquals(
			WorkflowConstants.STATUS_EMPTY, cpTaxCategory.getStatus());

		Map<Locale, String> nameMap = cpTaxCategory.getNameMap();

		Assert.assertFalse(nameMap.isEmpty());
	}

	@Inject
	private CPTaxCategoryLocalService _cpTaxCategoryLocalService;

	private User _user;

}