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
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Calendar;

import org.junit.After;
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
public class CPAttachmentFileEntryInfoItemObjectProviderTest {

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

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

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

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetInfoItem() throws Exception {
		long groupId = RandomTestUtil.randomLong();

		AssertUtils.assertFailure(
			NoSuchInfoItemException.class,
			"No group found with group ID " + groupId,
			() -> _cpAttachmentFileEntryInfoItemObjectProvider.getInfoItem(
				groupId,
				new ERCInfoItemIdentifier(
					_cpAttachmentFileEntry.getExternalReferenceCode())));

		Assert.assertEquals(
			_cpAttachmentFileEntry,
			_cpAttachmentFileEntryInfoItemObjectProvider.getInfoItem(
				_group.getGroupId(),
				new ClassPKInfoItemIdentifier(
					_cpAttachmentFileEntry.getCPAttachmentFileEntryId())));
		Assert.assertEquals(
			_cpAttachmentFileEntry,
			_cpAttachmentFileEntryInfoItemObjectProvider.getInfoItem(
				_group.getGroupId(),
				new ERCInfoItemIdentifier(
					_cpAttachmentFileEntry.getExternalReferenceCode())));
		Assert.assertEquals(
			_cpAttachmentFileEntry,
			_cpAttachmentFileEntryInfoItemObjectProvider.getInfoItem(
				RandomTestUtil.randomLong(),
				new ERCInfoItemIdentifier(
					_cpAttachmentFileEntry.getExternalReferenceCode(),
					_group.getExternalReferenceCode())));
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private CPAttachmentFileEntry _cpAttachmentFileEntry;

	@Inject(
		filter = "component.name=com.liferay.commerce.product.content.web.internal.info.item.provider.CPAttachmentFileEntryInfoItemObjectProvider"
	)
	private InfoItemObjectProvider<CPAttachmentFileEntry>
		_cpAttachmentFileEntryInfoItemObjectProvider;

	@Inject
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

}