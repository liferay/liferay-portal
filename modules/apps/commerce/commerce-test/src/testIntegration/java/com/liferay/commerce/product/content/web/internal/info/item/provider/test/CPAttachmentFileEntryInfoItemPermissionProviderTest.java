/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.info.item.provider.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.util.CommerceContextThreadLocal;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CPAttachmentFileEntryInfoItemPermissionProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPD-117098")
	public void testHasPermission() throws Exception {
		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(
				TestPropsValues.getCompanyId());

		CommerceCatalog commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), commerceCurrency.getCode());

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				commerceCatalog.getGroupId(), TestPropsValues.getUserId());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			commerceCatalog.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, RandomTestUtil.nextDate(), serviceContext);

		Calendar displayDateCalendar = Calendar.getInstance();

		displayDateCalendar.setTime(RandomTestUtil.nextDate());

		Calendar expirationDateCalendar = Calendar.getInstance();

		expirationDateCalendar.setTime(RandomTestUtil.nextDate());

		CPAttachmentFileEntry cpAttachmentFileEntry =
			_cpAttachmentFileEntryLocalService.addCPAttachmentFileEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				commerceCatalog.getGroupId(),
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

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser());

		Assert.assertTrue(
			_infoItemPermissionProvider.hasPermission(
				permissionChecker, cpAttachmentFileEntry, ActionKeys.DELETE));
		Assert.assertTrue(
			_infoItemPermissionProvider.hasPermission(
				permissionChecker, cpAttachmentFileEntry, ActionKeys.UPDATE));

		InfoItemReference infoItemReference = new InfoItemReference(
			CPAttachmentFileEntry.class.getName(),
			cpAttachmentFileEntry.getCPAttachmentFileEntryId());

		Assert.assertTrue(
			_infoItemPermissionProvider.hasPermission(
				permissionChecker, infoItemReference, ActionKeys.DELETE));
		Assert.assertTrue(
			_infoItemPermissionProvider.hasPermission(
				permissionChecker, infoItemReference, ActionKeys.UPDATE));

		User user = UserTestUtil.addUser();

		AccountEntry accountEntry =
			CommerceAccountTestUtil.getPersonAccountEntry(user.getUserId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			TestPropsValues.getGroupId(), commerceCurrency.getCode());

		permissionChecker = PermissionCheckerFactoryUtil.create(user);

		try (SafeCloseable safeCloseable =
				CommerceContextThreadLocal.setCommerceContextWithSafeCloseable(
					_commerceContextFactory.create(
						accountEntry.getAccountEntryId(),
						commerceChannel.getGroupId(),
						commerceCurrency.getCode(), 0,
						TestPropsValues.getCompanyId()))) {

			Assert.assertFalse(
				_infoItemPermissionProvider.hasPermission(
					permissionChecker, cpAttachmentFileEntry,
					ActionKeys.DELETE));
			Assert.assertFalse(
				_infoItemPermissionProvider.hasPermission(
					permissionChecker, cpAttachmentFileEntry,
					ActionKeys.UPDATE));
			Assert.assertTrue(
				_infoItemPermissionProvider.hasPermission(
					permissionChecker, cpAttachmentFileEntry, ActionKeys.VIEW));
			Assert.assertFalse(
				_infoItemPermissionProvider.hasPermission(
					permissionChecker, infoItemReference, ActionKeys.DELETE));
			Assert.assertFalse(
				_infoItemPermissionProvider.hasPermission(
					permissionChecker, infoItemReference, ActionKeys.UPDATE));
			Assert.assertTrue(
				_infoItemPermissionProvider.hasPermission(
					permissionChecker, infoItemReference, ActionKeys.VIEW));
		}
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CommerceContextFactory _commerceContextFactory;

	@Inject
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject(
		filter = "component.name=com.liferay.commerce.product.content.web.internal.info.item.provider.CPAttachmentFileEntryInfoItemPermissionProvider"
	)
	private InfoItemPermissionProvider<CPAttachmentFileEntry>
		_infoItemPermissionProvider;

}