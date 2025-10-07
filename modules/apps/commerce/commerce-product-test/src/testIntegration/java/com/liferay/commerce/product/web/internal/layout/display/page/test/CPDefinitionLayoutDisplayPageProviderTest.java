/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.web.internal.layout.display.page.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balazs Breier
 */
@RunWith(Arquillian.class)
public class CPDefinitionLayoutDisplayPageProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.fetchGroup(TestPropsValues.getGroupId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(), serviceContext);

		_cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);
	}

	@Test
	public void testGetLayoutDisplayPageObjectProvider() throws Exception {
		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					CPDefinition.class.getName());

		Assert.assertEquals(
			CPDefinition.class.getName(),
			layoutDisplayPageProvider.getClassName());

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_cpDefinition.getGroupId(),
				new InfoItemReference(
					CPDefinition.class.getName(),
					new ClassPKInfoItemIdentifier(
						_cpDefinition.getCPDefinitionId())));

		Assert.assertEquals(
			CPDefinition.class.getName(),
			layoutDisplayPageObjectProvider.getClassName());
		Assert.assertEquals(
			_cpDefinition, layoutDisplayPageObjectProvider.getDisplayObject());

		CProduct cProduct = _cpDefinition.getCProduct();

		String productExternalReferenceCode =
			cProduct.getExternalReferenceCode();

		layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_cpDefinition.getGroupId(),
				new InfoItemReference(
					CPDefinition.class.getName(),
					new ERCInfoItemIdentifier(productExternalReferenceCode)));

		Assert.assertEquals(
			CPDefinition.class.getName(),
			layoutDisplayPageObjectProvider.getClassName());
		Assert.assertEquals(
			_cpDefinition, layoutDisplayPageObjectProvider.getDisplayObject());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group companyGroup = company.getGroup();

		layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				companyGroup.getGroupId(),
				new InfoItemReference(
					CPDefinition.class.getName(),
					new ERCInfoItemIdentifier(productExternalReferenceCode)));

		Assert.assertEquals(
			CPDefinition.class.getName(),
			layoutDisplayPageObjectProvider.getClassName());
		Assert.assertEquals(
			_cpDefinition, layoutDisplayPageObjectProvider.getDisplayObject());

		layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				companyGroup.getGroupId(),
				new InfoItemReference(
					CPDefinition.class.getName(),
					new ERCInfoItemIdentifier(
						productExternalReferenceCode,
						_group.getExternalReferenceCode())));

		Assert.assertEquals(
			CPDefinition.class.getName(),
			layoutDisplayPageObjectProvider.getClassName());
		Assert.assertEquals(
			_cpDefinition, layoutDisplayPageObjectProvider.getDisplayObject());

		layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				RandomTestUtil.randomLong(),
				new InfoItemReference(
					CPDefinition.class.getName(),
					new ERCInfoItemIdentifier(
						productExternalReferenceCode,
						_group.getExternalReferenceCode())));

		Assert.assertEquals(
			CPDefinition.class.getName(),
			layoutDisplayPageObjectProvider.getClassName());
		Assert.assertEquals(
			_cpDefinition, layoutDisplayPageObjectProvider.getDisplayObject());

		String groupExternalReferenceCode = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			NoSuchGroupException.class,
			StringBundler.concat(
				"No Group exists with the key {externalReferenceCode=",
				groupExternalReferenceCode, ", companyId=",
				company.getCompanyId(), "}"),
			() -> layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				companyGroup.getGroupId(),
				new InfoItemReference(
					CPDefinition.class.getName(),
					new ERCInfoItemIdentifier(
						productExternalReferenceCode,
						groupExternalReferenceCode))));
	}

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	private CPDefinition _cpDefinition;
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

}