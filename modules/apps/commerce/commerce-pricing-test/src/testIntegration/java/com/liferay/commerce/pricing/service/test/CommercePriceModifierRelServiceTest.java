/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.pricing.constants.CommercePriceModifierConstants;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.service.CommercePriceModifierRelService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.price.list.CommercePriceListTestUtil;
import com.liferay.commerce.test.util.pricing.CommercePriceModifierRelTestUtil;
import com.liferay.commerce.test.util.pricing.CommercePriceModifierTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class CommercePriceModifierRelServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetCPDefinitionsCommercePriceModifierRelsCount()
		throws Exception {

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(_group.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getCompanyId(), _group.getGroupId(),
				TestPropsValues.getUserId());

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(), commerceCurrency.getCode(),
				LocaleUtil.US.getDisplayLanguage(), serviceContext);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				commerceCatalog.getGroupId(), TestPropsValues.getUser(),
				commerceCurrency, 0.0, serviceContext);

		CommercePriceModifier commercePriceModifier =
			CommercePriceModifierTestUtil.addCommercePriceModifier(
				commerceCatalog.getGroupId(), TestPropsValues.getUser(),
				commercePriceList.getCommercePriceListId(),
				CommercePriceModifierConstants.TARGET_PRODUCTS,
				BigDecimal.valueOf(RandomTestUtil.randomDouble()),
				CommercePriceModifierConstants.MODIFIER_TYPE_REPLACE, true,
				serviceContext);

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinition(
			commerceCatalog.getGroupId());

		CommercePriceModifierRelTestUtil.addCommercePriceModifierRel(
			commercePriceModifier.getCommercePriceModifierId(),
			CPDefinition.class.getName(), cpDefinition1.getCPDefinitionId(),
			serviceContext);

		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinition(
			commerceCatalog.getGroupId());

		CommercePriceModifierRelTestUtil.addCommercePriceModifierRel(
			commercePriceModifier.getCommercePriceModifierId(),
			CPDefinition.class.getName(), cpDefinition2.getCPDefinitionId(),
			serviceContext);

		String languageId = LocaleUtil.toLanguageId(LocaleUtil.getDefault());

		Assert.assertEquals(
			1,
			_commercePriceModifierRelService.
				getCPDefinitionsCommercePriceModifierRelsCount(
					commercePriceModifier.getCommercePriceModifierId(),
					cpDefinition1.getName(languageId), languageId));
		Assert.assertEquals(
			2,
			_commercePriceModifierRelService.
				getCPDefinitionsCommercePriceModifierRelsCount(
					commercePriceModifier.getCommercePriceModifierId(), null,
					languageId));
	}

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CommercePriceModifierRelService _commercePriceModifierRelService;

	@DeleteAfterTestRun
	private Group _group;

}