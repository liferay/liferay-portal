/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.internal.cart.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.price.list.test.util.CommercePriceEntryTestUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.Cookie;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CommerceCartResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		_cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = _cpInstance.getCPDefinition();

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, cpDefinition.getCProductId(),
			_cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(),
			BigDecimal.valueOf(RandomTestUtil.randomInt()));

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			TestPropsValues.getUserId(), commerceInventoryWarehouse,
			BigDecimal.valueOf(RandomTestUtil.randomInt()),
			_cpInstance.getSku(), StringPool.BLANK);
	}

	@Test
	public void testUpdateOrderItem() throws Exception {
		CommerceOrder commerceOrder1 = _addGuestCommerceOrder();
		Cookie[] cookies = _getCookies();

		_http.URLtoString(_createOptions(commerceOrder1, cookies));

		Assert.assertEquals(
			0,
			_commerceOrderItemLocalService.getCommerceOrderItemsCount(
				commerceOrder1.getCommerceOrderId()));

		_http.URLtoString(
			_createOptions(
				commerceOrder1,
				ArrayUtil.append(
					cookies,
					_createCookie(
						_commerceOrderHttpHelper.getCookieName(
							commerceOrder1.getGroupId()),
						commerceOrder1.getUuid() + StringPool.PIPE +
							RandomTestUtil.randomString()))));

		Assert.assertEquals(
			1,
			_commerceOrderItemLocalService.getCommerceOrderItemsCount(
				commerceOrder1.getCommerceOrderId()));

		CommerceOrder commerceOrder2 = _addGuestCommerceOrder();

		Http.Options options = _createOptions(commerceOrder2, cookies);

		options.addHeader("Commerce-Cart-UUID", commerceOrder2.getUuid());

		_http.URLtoString(options);

		Assert.assertEquals(
			1,
			_commerceOrderItemLocalService.getCommerceOrderItemsCount(
				commerceOrder2.getCommerceOrderId()));
	}

	private CommerceOrder _addGuestCommerceOrder() throws Exception {
		return _commerceOrderLocalService.addCommerceOrder(
			TestPropsValues.getUserId(), _commerceChannel.getGroupId(),
			AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
			_commerceCurrency.getCode(), 0);
	}

	private Cookie _createCookie(String name, String value) {
		Cookie cookie = new Cookie(name, value);

		cookie.setDomain("localhost");
		cookie.setPath(StringPool.SLASH);

		return cookie;
	}

	private Http.Options _createOptions(
		CommerceOrder commerceOrder, Cookie[] cookies) {

		Http.Options options = new Http.Options();

		options.addPart(
			"commerceAccountId",
			String.valueOf(AccountConstants.ACCOUNT_ENTRY_ID_GUEST));
		options.addPart("groupId", String.valueOf(_group.getGroupId()));
		options.addPart("languageId", LocaleUtil.toLanguageId(LocaleUtil.US));
		options.addPart(
			"orderId", String.valueOf(commerceOrder.getCommerceOrderId()));
		options.addPart(
			"productId", String.valueOf(_cpInstance.getCPInstanceId()));
		options.addPart("quantity", "1");
		options.addPart("unitOfMeasureKey", StringPool.BLANK);
		options.setCookies(cookies);
		options.setLocation(_getPortalURL() + "/o/commerce-ui/cart-item");
		options.setPost(true);

		return options;
	}

	private Cookie[] _getCookies() throws Exception {
		Http.Options options = new Http.Options();

		options.setCookies(
			new Cookie[] {
				_createCookie(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString())
			});
		options.setLocation(_getPortalURL());

		_http.URLtoString(options);

		return TransformUtil.transform(
			_http.getCookies(),
			cookie -> _createCookie(cookie.getName(), cookie.getValue()),
			Cookie.class);
	}

	private String _getPortalURL() {
		return "http://localhost:" + PortalUtil.getPortalServerPort(false);
	}

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private CommerceChannel _commerceChannel;
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Inject
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	private CPInstance _cpInstance;
	private Group _group;

	@Inject
	private Http _http;

}