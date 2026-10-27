/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.exception.CommerceDiscountRelUnitOfMeasureKeyException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.commerce.discount.service.CommerceDiscountRelLocalService;
import com.liferay.commerce.discount.test.util.CommerceDiscountTestUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import org.frutilla.FrutillaRule;

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
public class CommerceDiscountRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		CommerceCurrencyTestUtil.addCommerceCurrency(_group.getCompanyId());

		_commerceDiscount = CommerceDiscountTestUtil.addFixedCommerceDiscount(
			_group.getGroupId(), BigDecimal.TEN,
			CommerceDiscountConstants.TARGET_SKUS, null);
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());
	}

	@Test
	public void testAddCommerceDiscountRel() throws Exception {
		frutillaRule.scenario(
			"Add a commerce discount rel for a commerce product instance"
		).given(
			"A commerce discount that targets SKUs"
		).when(
			"The commerce product instance has more than one unit of measure"
		).then(
			"A blank or unknown unit of measure key throws a " +
				"CommerceDiscountRelUnitOfMeasureKeyException"
		).and(
			"A blank unit of measure key is accepted when the commerce " +
				"product instance has at most one unit of measure"
		).and(
			"An empty commerce product instance is not validated while lazy " +
				"referencing is enabled"
		);

		_testAddCommerceDiscountRelWithAtMostOneCPInstanceUnitOfMeasure();
		_testAddCommerceDiscountRelWithEmptyCPInstance();
		_testAddCommerceDiscountRelWithMultipleCPInstanceUnitsOfMeasure();
	}

	@Test
	public void testUpdateTypeSettings() throws Exception {
		frutillaRule.scenario(
			"Update the type settings of a commerce discount rel"
		).given(
			"A commerce discount rel for a commerce product instance with " +
				"more than one unit of measure"
		).when(
			"The unit of measure key is removed"
		).then(
			"A CommerceDiscountRelUnitOfMeasureKeyException is thrown"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			_group.getGroupId());

		String unitOfMeasureKey = _addCPInstanceUnitOfMeasure(cpInstance);

		_addCPInstanceUnitOfMeasure(cpInstance);

		CommerceDiscountRel commerceDiscountRel = _addCommerceDiscountRel(
			cpInstance, unitOfMeasureKey);

		try {
			_commerceDiscountRelLocalService.updateTypeSettings(
				commerceDiscountRel.getCommerceDiscountRelId(),
				new UnicodeProperties());

			Assert.fail();
		}
		catch (CommerceDiscountRelUnitOfMeasureKeyException
					commerceDiscountRelUnitOfMeasureKeyException) {

			Assert.assertNotNull(commerceDiscountRelUnitOfMeasureKeyException);
		}
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private String _addCPInstanceUnitOfMeasure(CPInstance cpInstance)
		throws Exception {

		String key = RandomTestUtil.randomString();

		CPTestUtil.addCPInstanceUnitOfMeasure(
			cpInstance.getGroupId(), cpInstance.getCPInstanceId(), key,
			BigDecimal.ONE, cpInstance.getSku());

		return key;
	}

	private CommerceDiscountRel _addCommerceDiscountRel(
			CPInstance cpInstance, String unitOfMeasureKey)
		throws Exception {

		UnicodeProperties typeSettingsUnicodeProperties = null;

		if (unitOfMeasureKey != null) {
			typeSettingsUnicodeProperties = UnicodePropertiesBuilder.put(
				"unitOfMeasureKey", unitOfMeasureKey
			).build();
		}

		return _commerceDiscountRelLocalService.addCommerceDiscountRel(
			_commerceDiscount.getCommerceDiscountId(),
			CPInstance.class.getName(), cpInstance.getCPInstanceId(),
			typeSettingsUnicodeProperties, _serviceContext);
	}

	private void _assertAddCommerceDiscountRelFails(
			CPInstance cpInstance, String unitOfMeasureKey)
		throws Exception {

		try {
			_addCommerceDiscountRel(cpInstance, unitOfMeasureKey);

			Assert.fail();
		}
		catch (CommerceDiscountRelUnitOfMeasureKeyException
					commerceDiscountRelUnitOfMeasureKeyException) {

			Assert.assertNotNull(commerceDiscountRelUnitOfMeasureKeyException);
		}
	}

	private void _testAddCommerceDiscountRelWithAtMostOneCPInstanceUnitOfMeasure()
		throws Exception {

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			_group.getGroupId());

		_addCommerceDiscountRel(cpInstance, null);

		_addCPInstanceUnitOfMeasure(cpInstance);

		_addCommerceDiscountRel(cpInstance, null);
	}

	private void _testAddCommerceDiscountRelWithEmptyCPInstance()
		throws Exception {

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_group.getGroupId());

		CPInstance cpInstance = null;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			cpInstance = _cpInstanceLocalService.getOrAddEmptyCPInstance(
				RandomTestUtil.randomString(), cpDefinition.getCPDefinitionId(),
				cpDefinition.getGroupId(), cpDefinition.getCompanyId(),
				cpDefinition.getUserId());

			_addCPInstanceUnitOfMeasure(cpInstance);
			_addCPInstanceUnitOfMeasure(cpInstance);

			_addCommerceDiscountRel(cpInstance, null);
		}

		_assertAddCommerceDiscountRelFails(cpInstance, null);
	}

	private void _testAddCommerceDiscountRelWithMultipleCPInstanceUnitsOfMeasure()
		throws Exception {

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			_group.getGroupId());

		String unitOfMeasureKey = _addCPInstanceUnitOfMeasure(cpInstance);

		_addCPInstanceUnitOfMeasure(cpInstance);

		_assertAddCommerceDiscountRelFails(
			cpInstance, RandomTestUtil.randomString());
		_assertAddCommerceDiscountRelFails(cpInstance, null);

		CommerceDiscountRel commerceDiscountRel = _addCommerceDiscountRel(
			cpInstance, unitOfMeasureKey);

		UnicodeProperties typeSettingsUnicodeProperties =
			commerceDiscountRel.getTypeSettingsUnicodeProperties();

		Assert.assertEquals(
			unitOfMeasureKey,
			typeSettingsUnicodeProperties.getProperty("unitOfMeasureKey"));
	}

	private CommerceDiscount _commerceDiscount;

	@Inject
	private CommerceDiscountRelLocalService _commerceDiscountRelLocalService;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	private Group _group;
	private ServiceContext _serviceContext;

}