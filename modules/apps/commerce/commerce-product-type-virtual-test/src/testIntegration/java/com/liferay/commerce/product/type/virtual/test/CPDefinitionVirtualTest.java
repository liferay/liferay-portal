/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christian Chiappa
 */
@RunWith(Arquillian.class)
public class CPDefinitionVirtualTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.product.type.virtual.service"));

	@Before
	public void setUp() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			company.getGroupId(), TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_commerceCatalog = CommerceCatalogLocalServiceUtil.addCommerceCatalog(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US.getDisplayLanguage(), _serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPDefinition> iterator = _cpDefinitions.iterator();

		while (iterator.hasNext()) {
			CPDefinition cpDefinitionToDelete = iterator.next();

			_cpDefinitionLocalService.deleteCPDefinition(
				cpDefinitionToDelete.getCPDefinitionId());

			iterator.remove();
		}
	}

	@Test
	public void testCopyCPDefinition() throws Exception {
		frutillaRule.scenario(
			"Copy a product"
		).given(
			"A product definition"
		).when(
			"the copy method is run"
		).then(
			"the copy is created without exception"
		).and(
			"Virtual file settings is cloned too"
		);

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId(), VirtualCPTypeConstants.NAME);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _commerceCatalog.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".jpg", ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(
				CPDefinitionVirtualTest.class, "dependencies/image.jpg"),
			null, null, null, _serviceContext);

		_cpDefinitionVirtualSettingLocalService.addCPDefinitionVirtualSetting(
			cpDefinition1.getModelClassName(),
			cpDefinition1.getCPDefinitionId(), fileEntry.getFileEntryId(), null,
			1, 0, RandomTestUtil.randomInt(), true, 0, "https://liferay.com",
			false, null, 0, false, _serviceContext);

		CPDefinition cpDefinition2 = _cpDefinitionLocalService.copyCPDefinition(
			cpDefinition1.getCPDefinitionId());

		Assert.assertNotNull(
			_cpDefinitionVirtualSettingLocalService.
				fetchCPDefinitionVirtualSetting(
					cpDefinition2.getModelClassName(),
					cpDefinition2.getCPDefinitionId()));
	}

	@Test
	public void testCreate() throws Exception {
		frutillaRule.scenario(
			"Add product definition"
		).given(
			"I add a virtual product definition"
		).when(
			"ignoreSKUCombinations is true"
		).and(
			"hasDefaultInstance is true"
		).and(
			"shippable is true"
		).then(
			"product definition should be APPROVED"
		).and(
			"shippable should be false"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId(), VirtualCPTypeConstants.NAME);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpDefinition.getStatus());

		Assert.assertEquals("virtual", cpDefinition.getProductTypeName());
		Assert.assertFalse(cpDefinition.isShippable());
	}

	@Test
	public void testUpdate() throws Exception {
		frutillaRule.scenario(
			"Update virtual product with shippable true"
		).given(
			"I add a a virtual product definition with shippable false"
		).when(
			"shippable is now set to true"
		).then(
			"product definition should have shippable false"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId(), VirtualCPTypeConstants.NAME);

		long cpDefinitionId = cpDefinition.getCPDefinitionId();

		Date displayDate = cpDefinition.getDisplayDate();
		Date expirationDate = cpDefinition.getExpirationDate();

		_cpDefinitionLocalService.updateCPDefinition(
			cpDefinitionId, cpDefinition.getNameMap(),
			cpDefinition.getShortDescriptionMap(),
			cpDefinition.getDescriptionMap(), cpDefinition.getUrlTitleMap(),
			cpDefinition.getMetaTitleMap(),
			cpDefinition.getMetaDescriptionMap(),
			cpDefinition.getMetaKeywordsMap(),
			cpDefinition.isIgnoreSKUCombinations(), true, true, true,
			cpDefinition.getShippingExtraPrice(), cpDefinition.getWidth(),
			cpDefinition.getHeight(), cpDefinition.getDepth(),
			cpDefinition.getWeight(), cpDefinition.getCPTaxCategoryId(),
			cpDefinition.isTaxExempt(), cpDefinition.isTelcoOrElectronics(),
			cpDefinition.getDDMStructureKey(), cpDefinition.isPublished(),
			displayDate.getMonth(), displayDate.getDate(),
			displayDate.getYear(), displayDate.getHours(),
			displayDate.getMinutes(), expirationDate.getMonth(),
			expirationDate.getDate(), expirationDate.getYear(),
			expirationDate.getHours(), expirationDate.getMinutes(), true,
			_serviceContext);

		cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		Assert.assertEquals("virtual", cpDefinition.getProductTypeName());
		Assert.assertFalse(cpDefinition.isShippable());
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	private final List<CPDefinition> _cpDefinitions = new ArrayList<>();

	@Inject
	private CPDefinitionVirtualSettingLocalService
		_cpDefinitionVirtualSettingLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private ServiceContext _serviceContext;

}