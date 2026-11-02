/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.commerce.pricing.service.CommercePricingClassCPDefinitionRelLocalService;
import com.liferay.commerce.pricing.service.CommercePricingClassLocalService;
import com.liferay.commerce.pricing.service.persistence.CommercePricingClassCPDefinitionRelUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
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
 * @author Lianne Louie
 */
@RunWith(Arquillian.class)
public class CPDefinitionModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testOnBeforeRemove() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		CommercePricingClass commercePricingClass =
			_commercePricingClassLocalService.addCommercePricingClass(
				TestPropsValues.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), serviceContext);

		CommerceCatalog commerceCatalog =
			CommerceCatalogLocalServiceUtil.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(), serviceContext);

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), "simple", true, false);

		_commercePricingClassCPDefinitionRelLocalService.
			addCommercePricingClassCPDefinitionRel(
				commercePricingClass.getCommercePricingClassId(),
				cpDefinition1.getCPDefinitionId(), serviceContext);

		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), "simple", true, false);

		_commercePricingClassCPDefinitionRelLocalService.
			addCommercePricingClassCPDefinitionRel(
				commercePricingClass.getCommercePricingClassId(),
				cpDefinition2.getCPDefinitionId(), serviceContext);

		Assert.assertEquals(
			Integer.valueOf(2),
			TransactionTestUtil.invoke(
				CommercePricingClassCPDefinitionRelUtil.getPersistence(),
				commercePricingClassCPDefinitionRelPersistence ->
					commercePricingClassCPDefinitionRelPersistence.
						countByCommercePricingClassId(
							commercePricingClass.getCommercePricingClassId())));

		_cpDefinitionLocalService.deleteCPDefinition(
			cpDefinition2.getCPDefinitionId());

		List<CommercePricingClassCPDefinitionRel>
			commercePricingClassCPDefinitionRels = TransactionTestUtil.invoke(
				CommercePricingClassCPDefinitionRelUtil.getPersistence(),
				commercePricingClassCPDefinitionRelPersistence ->
					commercePricingClassCPDefinitionRelPersistence.
						findByCommercePricingClassId(
							commercePricingClass.getCommercePricingClassId()));

		Assert.assertEquals(
			commercePricingClassCPDefinitionRels.toString(), 1,
			commercePricingClassCPDefinitionRels.size());

		CommercePricingClassCPDefinitionRel
			actualCommercePricingClassCPDefinitionRel =
				commercePricingClassCPDefinitionRels.get(0);

		Assert.assertEquals(
			cpDefinition1.getCPDefinitionId(),
			actualCommercePricingClassCPDefinitionRel.getCPDefinitionId());
	}

	@Inject
	private CommercePricingClassCPDefinitionRelLocalService
		_commercePricingClassCPDefinitionRelLocalService;

	@Inject
	private CommercePricingClassLocalService _commercePricingClassLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

}