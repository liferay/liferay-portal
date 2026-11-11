/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util.v1_0;

import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.CPDefinitionOptionRelService;
import com.liferay.commerce.product.service.CPOptionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductOption;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Allen Ziegenfus
 */
public class ProductOptionUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAddOrUpdateCPDefinitionOptionRelWhenOptionIdIsNull()
		throws Exception {

		CPDefinitionOptionRel cpDefinitionOptionRel = Mockito.mock(
			CPDefinitionOptionRel.class);
		CPDefinitionOptionRelService cpDefinitionOptionRelService =
			Mockito.mock(CPDefinitionOptionRelService.class);
		CPOption cpOption = Mockito.mock(CPOption.class);
		CPOptionService cpOptionService = Mockito.mock(CPOptionService.class);

		long cpOptionId = RandomTestUtil.randomLong();

		Mockito.when(
			cpOption.getCPOptionId()
		).thenReturn(
			cpOptionId
		);

		String optionExternalReferenceCode = RandomTestUtil.randomString();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(RandomTestUtil.randomLong());

		Mockito.when(
			cpOptionService.fetchCPOptionByExternalReferenceCode(
				optionExternalReferenceCode, serviceContext.getCompanyId())
		).thenReturn(
			cpOption
		);

		long cpDefinitionOptionRelId = RandomTestUtil.randomLong();

		Mockito.when(
			cpDefinitionOptionRel.getCPDefinitionOptionRelId()
		).thenReturn(
			cpDefinitionOptionRelId
		);

		long cpDefinitionId = RandomTestUtil.randomLong();

		Mockito.when(
			cpDefinitionOptionRelService.fetchCPDefinitionOptionRel(
				cpDefinitionId, cpOptionId)
		).thenReturn(
			cpDefinitionOptionRel
		);

		ProductOption productOption = new ProductOption();

		productOption.setOptionExternalReferenceCode(
			optionExternalReferenceCode);

		ProductOptionUtil.addOrUpdateCPDefinitionOptionRel(
			cpDefinitionOptionRelService, cpOptionService, productOption,
			cpDefinitionId, serviceContext);

		Mockito.verify(
			cpDefinitionOptionRelService
		).updateCPDefinitionOptionRel(
			Mockito.eq(cpDefinitionOptionRelId), Mockito.eq(cpOptionId),
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
			Mockito.anyDouble(), Mockito.anyBoolean(), Mockito.anyBoolean(),
			Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.any(),
			Mockito.any(), Mockito.eq(serviceContext)
		);
	}

}