/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Calendar;

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
public class CPAttachmentFileEntryInfoItemDetailsProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.fetchGroup(TestPropsValues.getGroupId());

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_group.getGroupId(), "simple", true, false);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, RandomTestUtil.nextDate(), serviceContext);

		Calendar displayDateCalendar = Calendar.getInstance();

		displayDateCalendar.setTime(RandomTestUtil.nextDate());

		Calendar expirationDateCalendar = Calendar.getInstance();

		expirationDateCalendar.setTime(RandomTestUtil.nextDate());

		_cpAttachmentFileEntry =
			_cpAttachmentFileEntryLocalService.addCPAttachmentFileEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(),
				_classNameLocalService.getClassNameId(CPDefinition.class),
				cpDefinition.getCPDefinitionId(), fileEntry.getFileEntryId(),
				false, null, displayDateCalendar.get(Calendar.MONTH),
				displayDateCalendar.get(Calendar.DAY_OF_MONTH),
				displayDateCalendar.get(Calendar.YEAR),
				displayDateCalendar.get(Calendar.HOUR),
				displayDateCalendar.get(Calendar.MINUTE),
				expirationDateCalendar.get(Calendar.MONTH),
				expirationDateCalendar.get(Calendar.DAY_OF_MONTH),
				expirationDateCalendar.get(Calendar.YEAR),
				expirationDateCalendar.get(Calendar.HOUR),
				expirationDateCalendar.get(Calendar.MINUTE), true, true,
				RandomTestUtil.randomLocaleStringMap(), null,
				RandomTestUtil.nextDouble(),
				CPAttachmentFileEntryConstants.TYPE_OTHER, serviceContext);
	}

	@Test
	public void testGetInfoItemDetails() {
		InfoItemDetailsProvider<Object> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class,
				CPAttachmentFileEntry.class.getName());

		InfoItemDetails infoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(_cpAttachmentFileEntry);

		Assert.assertEquals(
			CPAttachmentFileEntry.class.getName(),
			infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				CPAttachmentFileEntry.class.getName(),
				_cpAttachmentFileEntry.getCPAttachmentFileEntryId()),
			infoItemDetails.getInfoItemReference());

		infoItemDetails = infoItemDetailsProvider.getInfoItemDetails(
			_group.getGroupId(), ERCInfoItemIdentifier.class,
			_cpAttachmentFileEntry);

		Assert.assertEquals(
			CPAttachmentFileEntry.class.getName(),
			infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				CPAttachmentFileEntry.class.getName(),
				new ERCInfoItemIdentifier(
					_cpAttachmentFileEntry.getExternalReferenceCode())),
			infoItemDetails.getInfoItemReference());

		infoItemDetails = infoItemDetailsProvider.getInfoItemDetails(
			RandomTestUtil.randomLong(), ERCInfoItemIdentifier.class,
			_cpAttachmentFileEntry);

		Assert.assertEquals(
			CPAttachmentFileEntry.class.getName(),
			infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				CPAttachmentFileEntry.class.getName(),
				new ERCInfoItemIdentifier(
					_cpAttachmentFileEntry.getExternalReferenceCode(),
					_group.getExternalReferenceCode())),
			infoItemDetails.getInfoItemReference());
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private CPAttachmentFileEntry _cpAttachmentFileEntry;

	@Inject
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}