/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.layout.display.page.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
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
public class CommerceOrderLayoutDisplayPageProviderTest {

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

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.addCommerceChannel(
				StringPool.BLANK, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
				_group.getGroupId(),
				_group.getName(serviceContext.getLanguageId()) + " Portal",
				CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
				StringPool.BLANK, serviceContext);

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(_group.getCompanyId());

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addPersonAccountEntry(
				TestPropsValues.getUserId(), serviceContext);

		_commerceOrder = _commerceOrderLocalService.addCommerceOrder(
			TestPropsValues.getUserId(), commerceChannel.getGroupId(),
			accountEntry.getAccountEntryId(), commerceCurrency.getCode(), 0);
	}

	@Test
	public void testGetLayoutDisplayPageObjectProvider() throws Exception {
		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					CommerceOrder.class.getName());

		Assert.assertEquals(
			CommerceOrder.class.getName(),
			layoutDisplayPageProvider.getClassName());

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_commerceOrder.getGroupId(),
				new InfoItemReference(
					CommerceOrder.class.getName(),
					new ClassPKInfoItemIdentifier(
						_commerceOrder.getCommerceOrderId())));

		Assert.assertEquals(
			CommerceOrder.class.getName(),
			layoutDisplayPageObjectProvider.getClassName());
		Assert.assertEquals(
			_commerceOrder, layoutDisplayPageObjectProvider.getDisplayObject());

		layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_commerceOrder.getGroupId(),
				new InfoItemReference(
					CommerceOrder.class.getName(),
					new ERCInfoItemIdentifier(
						_commerceOrder.getExternalReferenceCode())));

		Assert.assertEquals(
			CommerceOrder.class.getName(),
			layoutDisplayPageObjectProvider.getClassName());
		Assert.assertEquals(
			_commerceOrder, layoutDisplayPageObjectProvider.getDisplayObject());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group companyGroup = company.getGroup();

		layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				companyGroup.getGroupId(),
				new InfoItemReference(
					CommerceOrder.class.getName(),
					new ERCInfoItemIdentifier(
						_commerceOrder.getExternalReferenceCode())));

		Assert.assertEquals(
			CommerceOrder.class.getName(),
			layoutDisplayPageObjectProvider.getClassName());
		Assert.assertEquals(
			_commerceOrder, layoutDisplayPageObjectProvider.getDisplayObject());

		layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				companyGroup.getGroupId(),
				new InfoItemReference(
					CommerceOrder.class.getName(),
					new ERCInfoItemIdentifier(
						_commerceOrder.getExternalReferenceCode(),
						_group.getExternalReferenceCode())));

		Assert.assertEquals(
			CommerceOrder.class.getName(),
			layoutDisplayPageObjectProvider.getClassName());
		Assert.assertEquals(
			_commerceOrder, layoutDisplayPageObjectProvider.getDisplayObject());

		layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				RandomTestUtil.randomLong(),
				new InfoItemReference(
					CommerceOrder.class.getName(),
					new ERCInfoItemIdentifier(
						_commerceOrder.getExternalReferenceCode(),
						_group.getExternalReferenceCode())));

		Assert.assertEquals(
			CommerceOrder.class.getName(),
			layoutDisplayPageObjectProvider.getClassName());
		Assert.assertEquals(
			_commerceOrder, layoutDisplayPageObjectProvider.getDisplayObject());

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
					CommerceOrder.class.getName(),
					new ERCInfoItemIdentifier(
						_commerceOrder.getExternalReferenceCode(),
						groupExternalReferenceCode))));
	}

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

}