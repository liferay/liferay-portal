/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryLocalService;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
public class CSDiagramEntryInfoItemDetailsProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.fetchGroup(TestPropsValues.getGroupId());

		CPInstance cpInstance = CPTestUtil.addCPInstance(_group.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		_csDiagramEntry = _csDiagramEntryLocalService.addCSDiagramEntry(
			TestPropsValues.getUserId(), cpInstance.getCPDefinitionId(),
			cpInstance.getCPInstanceId(), cpDefinition.getCProductId(), false,
			0, null, cpInstance.getSku(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test
	public void testGetInfoItemDetails() {
		InfoItemDetailsProvider<Object> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, CSDiagramEntry.class.getName());

		InfoItemDetails infoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(_csDiagramEntry);

		Assert.assertEquals(
			CSDiagramEntry.class.getName(), infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				CSDiagramEntry.class.getName(),
				_csDiagramEntry.getCSDiagramEntryId()),
			infoItemDetails.getInfoItemReference());

		CProduct cProduct = _cProductLocalService.fetchCProduct(
			_csDiagramEntry.getCProductId());

		Group group = _groupLocalService.fetchGroup(cProduct.getGroupId());

		infoItemDetails = infoItemDetailsProvider.getInfoItemDetails(
			group.getGroupId(), ERCInfoItemIdentifier.class, _csDiagramEntry);

		Assert.assertEquals(
			CSDiagramEntry.class.getName(), infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				CSDiagramEntry.class.getName(),
				new ERCInfoItemIdentifier(
					_csDiagramEntry.getExternalReferenceCode())),
			infoItemDetails.getInfoItemReference());

		infoItemDetails = infoItemDetailsProvider.getInfoItemDetails(
			RandomTestUtil.randomLong(), ERCInfoItemIdentifier.class,
			_csDiagramEntry);

		Assert.assertEquals(
			CSDiagramEntry.class.getName(), infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				CSDiagramEntry.class.getName(),
				new ERCInfoItemIdentifier(
					_csDiagramEntry.getExternalReferenceCode(),
					group.getExternalReferenceCode())),
			infoItemDetails.getInfoItemReference());
	}

	@Inject
	private CProductLocalService _cProductLocalService;

	private CSDiagramEntry _csDiagramEntry;

	@Inject
	private CSDiagramEntryLocalService _csDiagramEntryLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}