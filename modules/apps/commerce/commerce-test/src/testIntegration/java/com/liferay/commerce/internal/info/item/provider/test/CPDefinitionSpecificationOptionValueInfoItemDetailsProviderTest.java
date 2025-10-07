/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
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
public class CPDefinitionSpecificationOptionValueInfoItemDetailsProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.fetchGroup(TestPropsValues.getGroupId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(), serviceContext);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		CPSpecificationOption cpSpecificationOption =
			CPTestUtil.addCPSpecificationOption(
				commerceCatalog.getGroupId(), false);

		_cpDefinitionSpecificationOptionValue =
			_cpDefinitionSpecificationOptionValueLocalService.
				addCPDefinitionSpecificationOptionValue(
					RandomTestUtil.randomString(),
					cpDefinition.getCPDefinitionId(),
					cpSpecificationOption.getCPSpecificationOptionId(),
					cpSpecificationOption.getCPOptionCategoryId(),
					RandomTestUtil.randomDouble(),
					RandomTestUtil.randomLocaleStringMap(), true,
					serviceContext);
	}

	@Test
	public void testGetInfoItemDetails() {
		InfoItemDetailsProvider<Object> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class,
				CPDefinitionSpecificationOptionValue.class.getName());

		InfoItemDetails infoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				_cpDefinitionSpecificationOptionValue);

		Assert.assertEquals(
			CPDefinitionSpecificationOptionValue.class.getName(),
			infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				CPDefinitionSpecificationOptionValue.class.getName(),
				_cpDefinitionSpecificationOptionValue.
					getCPDefinitionSpecificationOptionValueId()),
			infoItemDetails.getInfoItemReference());

		Group group = _groupLocalService.fetchGroup(
			_cpDefinitionSpecificationOptionValue.getGroupId());

		infoItemDetails = infoItemDetailsProvider.getInfoItemDetails(
			group.getGroupId(), ERCInfoItemIdentifier.class,
			_cpDefinitionSpecificationOptionValue);

		Assert.assertEquals(
			CPDefinitionSpecificationOptionValue.class.getName(),
			infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				CPDefinitionSpecificationOptionValue.class.getName(),
				new ERCInfoItemIdentifier(
					_cpDefinitionSpecificationOptionValue.
						getExternalReferenceCode())),
			infoItemDetails.getInfoItemReference());

		infoItemDetails = infoItemDetailsProvider.getInfoItemDetails(
			RandomTestUtil.randomLong(), ERCInfoItemIdentifier.class,
			_cpDefinitionSpecificationOptionValue);

		Assert.assertEquals(
			CPDefinitionSpecificationOptionValue.class.getName(),
			infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				CPDefinitionSpecificationOptionValue.class.getName(),
				new ERCInfoItemIdentifier(
					_cpDefinitionSpecificationOptionValue.
						getExternalReferenceCode(),
					group.getExternalReferenceCode())),
			infoItemDetails.getInfoItemReference());
	}

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private CPDefinitionSpecificationOptionValue
		_cpDefinitionSpecificationOptionValue;

	@Inject
	private CPDefinitionSpecificationOptionValueLocalService
		_cpDefinitionSpecificationOptionValueLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}