/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.frontend.data.set.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.payment.integration.CommercePaymentIntegration;
import com.liferay.commerce.payment.integration.CommercePaymentIntegrationRegistry;
import com.liferay.commerce.payment.method.CommercePaymentMethod;
import com.liferay.commerce.payment.method.CommercePaymentMethodRegistry;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.FDSDataProviderRegistry;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSKeywordsFactory;
import com.liferay.frontend.data.set.provider.search.FDSKeywordsFactoryRegistry;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Danny Situ
 */
@RunWith(Arquillian.class)
public class CommercePaymentMethodFDSDataProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_fdsDataProvider = _fdsDataProviderRegistry.getFDSDataProvider(
			_FDS_NAME);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLocale(LocaleUtil.US);

		Group group = GroupTestUtil.addGroup();

		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteGroupId(group.getGroupId());

		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(group.getCompanyId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			group.getGroupId(), commerceCurrency.getCode());

		_mockHttpServletRequest.setParameter(
			"commerceChannelId",
			String.valueOf(commerceChannel.getCommerceChannelId()));

		FDSKeywordsFactory fdsKeywordsFactory =
			_fdsKeywordsFactoryRegistry.getFDSKeywordsFactory(_FDS_NAME);

		_fdsKeywords = fdsKeywordsFactory.create(_mockHttpServletRequest);
	}

	@Test
	public void testGetItems() throws Exception {
		Map<String, CommercePaymentIntegration> commercePaymentIntegrations =
			_commercePaymentIntegrationRegistry.
				getCommercePaymentIntegrations();
		Map<String, CommercePaymentMethod> commercePaymentMethods =
			_commercePaymentMethodRegistry.getCommercePaymentMethods();

		int size =
			commercePaymentIntegrations.size() + commercePaymentMethods.size();

		List<?> items = _fdsDataProvider.getItems(
			_fdsKeywords, _createFDSPagination(0, size),
			_mockHttpServletRequest, null);

		Assert.assertEquals(items.toString(), size, items.size());

		items = _fdsDataProvider.getItems(
			_fdsKeywords, _createFDSPagination(0, 1), _mockHttpServletRequest,
			null);

		Assert.assertEquals(items.toString(), 1, items.size());
	}

	@Test
	public void testGetItemsCount() throws Exception {
		Map<String, CommercePaymentIntegration> commercePaymentIntegrations =
			_commercePaymentIntegrationRegistry.
				getCommercePaymentIntegrations();

		Assert.assertFalse(commercePaymentIntegrations.isEmpty());

		Map<String, CommercePaymentMethod> commercePaymentMethods =
			_commercePaymentMethodRegistry.getCommercePaymentMethods();

		Assert.assertEquals(
			commercePaymentIntegrations.size() + commercePaymentMethods.size(),
			_fdsDataProvider.getItemsCount(
				_fdsKeywords, _mockHttpServletRequest));
	}

	private FDSPagination _createFDSPagination(
		int startPosition, int endPosition) {

		return new FDSPagination() {

			@Override
			public int getEndPosition() {
				return endPosition;
			}

			@Override
			public int getPage() {
				return 1;
			}

			@Override
			public int getPageSize() {
				return endPosition - startPosition;
			}

			@Override
			public int getStartPosition() {
				return startPosition;
			}

		};
	}

	private static final String _FDS_NAME =
		CPPortletKeys.COMMERCE_CHANNELS + "-paymentMethod";

	@Inject
	private CommercePaymentIntegrationRegistry
		_commercePaymentIntegrationRegistry;

	@Inject
	private CommercePaymentMethodRegistry _commercePaymentMethodRegistry;

	private FDSDataProvider<?> _fdsDataProvider;

	@Inject
	private FDSDataProviderRegistry _fdsDataProviderRegistry;

	private FDSKeywords _fdsKeywords;

	@Inject
	private FDSKeywordsFactoryRegistry _fdsKeywordsFactoryRegistry;

	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();

}