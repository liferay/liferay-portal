/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.importer.type.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.importer.item.CommerceOrderImporterItem;
import com.liferay.commerce.order.importer.type.CommerceOrderImporterType;
import com.liferay.commerce.order.importer.type.CommerceOrderImporterTypeRegistry;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.product.constants.CPInstanceConstants;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayInputStream;
import java.io.File;

import java.math.BigDecimal;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
@Sync
public class CommerceOrderImporterTypeTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		PrincipalThreadLocal.setName(_user.getUserId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		_group = GroupTestUtil.addGroup(
			_user.getCompanyId(), _user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_user.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_user.getCompanyId(), _group.getGroupId(), _user.getUserId());

		_commerceChannel = CommerceChannelLocalServiceUtil.addCommerceChannel(
			null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			_group.getGroupId(), "Test Channel",
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			_commerceCurrency.getCode(), _serviceContext);

		_accountEntry = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), new long[] {_user.getUserId()}, null,
			_serviceContext);

		_commerceOrder = CommerceTestUtil.addB2BCommerceOrder(
			_group.getGroupId(), _commerceChannel.getUserId(),
			_accountEntry.getAccountEntryId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceContext = new TestCommerceContext(
			_commerceOrder.getAccountEntry(), _commerceCurrency,
			_commerceChannel, _user, _group, _commerceOrder);
	}

	@After
	public void tearDown() throws PortalException {
		_commerceOrderLocalService.deleteCommerceOrder(_commerceOrder);
	}

	@Test
	public void testFailedCSVImport() throws Exception {
		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CPTestUtil.addCPInstanceFromCatalogWithERC(
			commerceCatalog.getGroupId(), "erc-test", new BigDecimal(100),
			CPInstanceConstants.DEFAULT_SKU);

		CommerceOrderImporterType commerceOrderImporterType =
			_commerceOrderImporterTypeRegistry.getCommerceOrderImporterType(
				"csv");

		String fileName = "test_failed_csv_import.csv";

		String fileContent = "sku,quantity\nerc-test-fail,1\nerc-test,0";

		File file = FileUtil.createTempFile(fileContent.getBytes());

		List<CommerceOrderImporterItem> commerceOrderImporterItems =
			commerceOrderImporterType.getCommerceOrderImporterItems(
				_commerceOrderLocalService.addCommerceOrder(
					_user.getUserId(), _commerceChannel.getGroupId(),
					_accountEntry.getAccountEntryId(),
					_commerceCurrency.getCode(), 0),
				null,
				DLAppLocalServiceUtil.addFileEntry(
					null, _serviceContext.getUserId(),
					_serviceContext.getScopeGroupId(),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
					MimeTypesUtil.getContentType(file, fileName), fileName,
					StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, file,
					null, null, null, _serviceContext));

		Assert.assertEquals(
			commerceOrderImporterItems.toString(), 2,
			commerceOrderImporterItems.size());

		for (CommerceOrderImporterItem commerceOrderImporterItem :
				commerceOrderImporterItems) {

			Assert.assertNotNull(commerceOrderImporterItem.getErrorMessages());
		}
	}

	@Test
	public void testGetCommerceOrderImporterItem() throws Exception {
		CommerceOrderImporterType commerceOrderImporterType =
			_commerceOrderImporterTypeRegistry.getCommerceOrderImporterType(
				"csv");

		FileEntry fileEntry1 = DLAppLocalServiceUtil.addFileEntry(
			null, _user.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".csv", ContentTypes.TEXT_CSV,
			_CSV_CONTENT.getBytes(), null, null, null, _serviceContext);

		Company company = _companyLocalService.getCompany(_user.getCompanyId());

		Assert.assertThrows(
			NoSuchModelException.class,
			() -> _getCommerceOrderImporterItem(
				commerceOrderImporterType, company,
				fileEntry1.getFileEntryId()));

		User user = UserTestUtil.addUser();

		FileEntry fileEntry2 = _addTempFileEntry(
			company.getGroupId(), user.getUserId());

		Assert.assertThrows(
			NoSuchModelException.class,
			() -> _getCommerceOrderImporterItem(
				commerceOrderImporterType, company,
				fileEntry2.getFileEntryId()));

		FileEntry fileEntry3 = _addTempFileEntry(
			company.getGroupId(), _user.getUserId());

		FileEntry commerceOrderImporterItem =
			(FileEntry)_getCommerceOrderImporterItem(
				commerceOrderImporterType, company,
				fileEntry3.getFileEntryId());

		Assert.assertEquals(
			fileEntry3.getFileEntryId(),
			commerceOrderImporterItem.getFileEntryId());
	}

	@Test
	public void testSuccessfulCSVImport() throws Exception {
		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				_serviceContext);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalogWithERC(
			commerceCatalog.getGroupId(), "erc-test2", new BigDecimal(100),
			CPInstanceConstants.DEFAULT_SKU);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				_serviceContext);

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), commerceInventoryWarehouse,
			BigDecimal.valueOf(100), cpInstance.getSku(), StringPool.BLANK);

		CPTestUtil.addBaseCommerceCatalogCommercePriceList(
			commerceCatalog.getGroupId(), _commerceCurrency.getCode(),
			CommercePriceListConstants.TYPE_PRICE_LIST, _serviceContext);

		CPTestUtil.addBaseCommerceCatalogCommercePriceList(
			commerceCatalog.getGroupId(), _commerceCurrency.getCode(),
			CommercePriceListConstants.TYPE_PROMOTION, _serviceContext);

		CommerceOrderImporterType commerceOrderImporterType =
			_commerceOrderImporterTypeRegistry.getCommerceOrderImporterType(
				"csv");

		String fileName = "test_successful_csv_import.csv";

		String fileContent = "sku,quantity\nerc-test2,1";

		File file = FileUtil.createTempFile(fileContent.getBytes());

		List<CommerceOrderImporterItem> commerceOrderImporterItems =
			commerceOrderImporterType.getCommerceOrderImporterItems(
				_commerceOrderLocalService.addCommerceOrder(
					_user.getUserId(), _commerceChannel.getGroupId(),
					_accountEntry.getAccountEntryId(),
					_commerceCurrency.getCode(), 0),
				null,
				DLAppLocalServiceUtil.addFileEntry(
					null, _serviceContext.getUserId(),
					_serviceContext.getScopeGroupId(),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
					MimeTypesUtil.getContentType(file, fileName), fileName,
					StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, file,
					null, null, null, _serviceContext));

		Assert.assertEquals(
			commerceOrderImporterItems.toString(), 1,
			commerceOrderImporterItems.size());

		CommerceOrderImporterItem commerceOrderImporterItem =
			commerceOrderImporterItems.get(0);

		Assert.assertNull(commerceOrderImporterItem.getErrorMessages());
	}

	private FileEntry _addTempFileEntry(long groupId, long userId)
		throws Exception {

		return TempFileEntryUtil.addTempFileEntry(
			groupId, userId,
			"com.liferay.commerce.order.content.web.internal.portlet.action." +
				"ImportCSVMVCActionCommand",
			RandomTestUtil.randomString() + ".csv",
			new ByteArrayInputStream(_CSV_CONTENT.getBytes()),
			ContentTypes.TEXT_CSV);
	}

	private Object _getCommerceOrderImporterItem(
			CommerceOrderImporterType commerceOrderImporterType,
			Company company, long fileEntryId)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(company);
		themeDisplay.setUser(_user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"fileEntryId", String.valueOf(fileEntryId));

		return commerceOrderImporterType.getCommerceOrderImporterItem(
			mockHttpServletRequest);
	}

	private static final String _CSV_CONTENT = "sku,quantity\nerc-test,1";

	private AccountEntry _accountEntry;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	private CommerceContext _commerceContext;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderImporterTypeRegistry
		_commerceOrderImporterTypeRegistry;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;
	private User _user;

}