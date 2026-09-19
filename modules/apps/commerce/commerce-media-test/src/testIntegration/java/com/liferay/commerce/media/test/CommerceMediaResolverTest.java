/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.media.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.media.constants.CommerceMediaConstants;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemLocalService;
import com.liferay.commerce.product.type.virtual.order.util.CommerceVirtualOrderItemChecker;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
public class CommerceMediaResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, _serviceContext);

		CommerceAccountTestUtil.addAccountGroupAndAccountRel(
			_user.getCompanyId(), RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_GROUP_TYPE_STATIC,
			_accountEntry.getAccountEntryId(), _serviceContext);

		_commerceCurrency = _commerceCurrencyLocalService.addCommerceCurrency(
			null, _user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(), BigDecimal.ONE,
			RandomTestUtil.randomLocaleStringMap(), 2, 2, "HALF_EVEN", false,
			RandomTestUtil.nextDouble(), true);

		_commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId(),
			_commerceCurrency.getCode());

		_commerceChannel = _commerceChannelLocalService.addCommerceChannel(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT, _group.getGroupId(),
			RandomTestUtil.randomString(),
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			_commerceCurrency.getCode(), _serviceContext);
		_commerceOrder = CommerceTestUtil.addB2BCommerceOrder(
			_group.getGroupId(), _user.getUserId(),
			_accountEntry.getAccountEntryId(),
			_commerceCurrency.getCommerceCurrencyId());
	}

	@Test
	public void testGetDownloadVirtualOrderItemURL() throws Exception {
		frutillaRule.scenario(
			"Test commerce media resolver URL"
		).given(
			"A commerce virtual order item id"
		).when(
			"I invoke getDownloadVirtualOrderItemURL method"
		).then(
			"I expect the URL to be formatted correctly"
		);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, _user.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".jpg", ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(
				CommerceMediaResolverTest.class, "dependencies/image.jpg"),
			null, null, null, _serviceContext);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), VirtualCPTypeConstants.NAME, true,
			true);

		_commerceCPDefinitions.add(cpDefinition);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstance cpInstance = cpInstances.get(0);

		_commerceCPInstances.addAll(cpInstances);

		_cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingLocalService.
				addCPDefinitionVirtualSetting(
					cpDefinition.getModelClassName(),
					cpDefinition.getCPDefinitionId(),
					fileEntry.getFileEntryId(), null,
					CommerceOrderConstants.ORDER_STATUS_PENDING, 0,
					RandomTestUtil.randomInt(), true, 0, "https://liferay.com",
					false, null, 0, false, _serviceContext);

		CommerceTestUtil.updateBackOrderCPDefinitionInventory(cpDefinition);

		int quantity = RandomTestUtil.randomInt(1, 100);

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.addCommerceOrderItem(
				_user.getUserId(), _commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), null,
				BigDecimal.valueOf(quantity), 0, BigDecimal.valueOf(quantity),
				StringPool.BLANK,
				new TestCommerceContext(
					_accountEntry, _commerceCurrency, _commerceChannel, _user,
					_group, _commerceOrder),
				_serviceContext);

		_commerceOrderItems.add(commerceOrderItem);

		_commerceVirtualOrderItemChecker.checkCommerceVirtualOrderItems(
			_commerceOrder.getCommerceOrderId());

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			_commerceVirtualOrderItemLocalService.
				fetchCommerceVirtualOrderItemByCommerceOrderItemId(
					commerceOrderItem.getCommerceOrderItemId());

		List<CommerceVirtualOrderItemFileEntry>
			commerceVirtualOrderItemFileEntries =
				commerceVirtualOrderItem.
					getCommerceVirtualOrderItemFileEntries();

		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			commerceVirtualOrderItemFileEntries.get(0);

		String downloadVirtualOrderItemURL =
			_commerceMediaResolver.getDownloadVirtualOrderItemURL(
				commerceVirtualOrderItem.getCommerceVirtualOrderItemId(),
				commerceVirtualOrderItemFileEntry.getFileEntryId());

		String expectedVirtualOrderItemURL = StringBundler.concat(
			_portal.getPathModule(), StringPool.SLASH,
			CommerceMediaConstants.SERVLET_PATH,
			CommerceMediaConstants.URL_SEPARATOR_VIRTUAL_ORDER_ITEM,
			commerceVirtualOrderItem.getCommerceVirtualOrderItemId(),
			CommerceMediaConstants.URL_SEPARATOR_FILE,
			fileEntry.getFileEntryId());

		Assert.assertEquals(
			expectedVirtualOrderItemURL, downloadVirtualOrderItemURL);
	}

	@Test
	public void testGetURL() throws Exception {
		frutillaRule.scenario(
			"Test commerce media resolver URL"
		).given(
			"A commerce product attachment file entry"
		).when(
			"I invoke getURL method"
		).then(
			"I expect the URL to be formatted correctly"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId());

		_commerceCPDefinitions.add(cpDefinition);

		long now = System.currentTimeMillis();

		Date displayDate = new Date(now - Time.HOUR);
		Date expirationDate = new Date(now + Time.DAY);

		Calendar displayCal = CalendarFactoryUtil.getCalendar(
			_user.getTimeZone());

		displayCal.setTime(displayDate);

		int displayDateMonth = displayCal.get(Calendar.MONTH);
		int displayDateDay = displayCal.get(Calendar.DATE);
		int displayDateYear = displayCal.get(Calendar.YEAR);
		int displayDateHour = displayCal.get(Calendar.HOUR);
		int displayDateMinute = displayCal.get(Calendar.MINUTE);

		if (displayCal.get(Calendar.AM_PM) == Calendar.PM) {
			displayDateHour += 12;
		}

		Calendar expirationCal = CalendarFactoryUtil.getCalendar(
			_user.getTimeZone());

		expirationCal.setTime(expirationDate);

		int expirationDateMonth = expirationCal.get(Calendar.MONTH);
		int expirationDateDay = expirationCal.get(Calendar.DATE);
		int expirationDateYear = expirationCal.get(Calendar.YEAR);
		int expirationDateHour = expirationCal.get(Calendar.HOUR);
		int expirationDateMinute = expirationCal.get(Calendar.MINUTE);

		if (expirationCal.get(Calendar.AM_PM) == Calendar.PM) {
			expirationDateHour += 12;
		}

		DLFolder dlFolder = DLTestUtil.addDLFolder(
			_commerceCatalog.getGroupId());

		DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		CPAttachmentFileEntry cpAttachmentFileEntry =
			_cpAttachmentFileEntryLocalService.addCPAttachmentFileEntry(
				null, _user.getUserId(), _group.getGroupId(),
				PortalUtil.getClassNameId(CPDefinition.class.getName()),
				cpDefinition.getCPDefinitionId(), dlFileEntry.getFileEntryId(),
				false, null, displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, true, true,
				RandomTestUtil.randomLocaleStringMap(), null, 0D,
				CPAttachmentFileEntryConstants.TYPE_IMAGE, _serviceContext);

		Assert.assertEquals(
			StringBundler.concat(
				PortalUtil.getPathModule(), StringPool.SLASH,
				CommerceMediaConstants.SERVLET_PATH, "/accounts/",
				_accountEntry.getAccountEntryId(), "/images/",
				cpAttachmentFileEntry.getCPAttachmentFileEntryId(),
				"?download=false"),
			_commerceMediaResolver.getURL(
				_accountEntry.getAccountEntryId(),
				cpAttachmentFileEntry.getCPAttachmentFileEntryId()));
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@DeleteAfterTestRun
	private final List<CPDefinition> _commerceCPDefinitions = new ArrayList<>();

	@DeleteAfterTestRun
	private final List<CPInstance> _commerceCPInstances = new ArrayList<>();

	private CommerceCatalog _commerceCatalog;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Inject
	private CommerceMediaResolver _commerceMediaResolver;

	@DeleteAfterTestRun
	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@DeleteAfterTestRun
	private final List<CommerceOrderItem> _commerceOrderItems =
		new ArrayList<>();

	@Inject
	private CommerceVirtualOrderItemChecker _commerceVirtualOrderItemChecker;

	@Inject
	private CommerceVirtualOrderItemLocalService
		_commerceVirtualOrderItemLocalService;

	@Inject
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@DeleteAfterTestRun
	private CPDefinitionVirtualSetting _cpDefinitionVirtualSetting;

	@Inject
	private CPDefinitionVirtualSettingLocalService
		_cpDefinitionVirtualSettingLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;
	private User _user;

}