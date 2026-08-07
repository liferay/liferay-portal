/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.NoSuchCatalogException;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CommerceCatalogLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testAddCommerceCatalogWithEmptyAccountEntry() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			AccountEntry accountEntry =
				_accountEntryLocalService.getOrAddEmptyAccountEntry(
					externalReferenceCode, TestPropsValues.getCompanyId(),
					TestPropsValues.getUserId(), externalReferenceCode,
					AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER);

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, accountEntry.getStatus());

			CommerceCatalog commerceCatalog =
				_commerceCatalogLocalService.addCommerceCatalog(
					RandomTestUtil.randomString(),
					accountEntry.getAccountEntryId(),
					RandomTestUtil.randomString(), "USD", "en_US", false,
					ServiceContextTestUtil.getServiceContext());

			Assert.assertEquals(
				accountEntry.getAccountEntryId(),
				commerceCatalog.getAccountEntryId());
		}
	}

	@Test
	public void testForceDeleteCommerceCatalog() throws Exception {
		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				RandomTestUtil.randomString(),
				AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
				RandomTestUtil.randomString(), "USD", "en_US", false,
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getCompanyId(),
					TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		List<CPConfigurationList> cpConfigurationLists =
			_cpConfigurationListLocalService.getCPConfigurationLists(
				commerceCatalog.getGroupId(), TestPropsValues.getCompanyId());

		Assert.assertFalse(cpConfigurationLists.isEmpty());

		_commerceCatalogLocalService.forceDeleteCommerceCatalog(
			commerceCatalog);

		cpConfigurationLists =
			_cpConfigurationListLocalService.getCPConfigurationLists(
				commerceCatalog.getGroupId(), TestPropsValues.getCompanyId());

		Assert.assertTrue(cpConfigurationLists.isEmpty());
	}

	@Test
	public void testGetOrAddEmptyCommerceCatalog() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		try {
			_commerceCatalogLocalService.getOrAddEmptyCommerceCatalog(
				externalReferenceCode, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId());

			Assert.fail();
		}
		catch (NoSuchCatalogException noSuchCatalogException) {
			Assert.assertNotNull(noSuchCatalogException);
		}

		CommerceCatalog commerceCatalog = null;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			commerceCatalog =
				_commerceCatalogLocalService.getOrAddEmptyCommerceCatalog(
					externalReferenceCode, TestPropsValues.getCompanyId(),
					TestPropsValues.getUserId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, commerceCatalog.getStatus());
			Assert.assertEquals(
				externalReferenceCode,
				commerceCatalog.getExternalReferenceCode());

			CommerceCatalog resolvedCommerceCatalog =
				_commerceCatalogLocalService.getOrAddEmptyCommerceCatalog(
					externalReferenceCode, TestPropsValues.getCompanyId(),
					TestPropsValues.getUserId());

			Assert.assertEquals(
				commerceCatalog.getCommerceCatalogId(),
				resolvedCommerceCatalog.getCommerceCatalogId());
		}

		commerceCatalog = _commerceCatalogLocalService.updateCommerceCatalog(
			commerceCatalog.getCommerceCatalogId(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), "USD", "en_US");

		Assert.assertNotEquals(
			WorkflowConstants.STATUS_EMPTY, commerceCatalog.getStatus());
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

}