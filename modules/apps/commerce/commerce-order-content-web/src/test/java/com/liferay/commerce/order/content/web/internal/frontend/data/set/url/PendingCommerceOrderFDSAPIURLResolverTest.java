/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.url;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Balazs Breier
 */
public class PendingCommerceOrderFDSAPIURLResolverTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		Mockito.when(
			_commerceContext.getAccountEntry()
		).thenReturn(
			_accountEntry
		);

		Mockito.when(
			_accountEntry.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_accountEntry.getAccountEntryId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_commerceChannelLocalService.fetchCommerceChannel(Mockito.anyLong())
		).thenReturn(
			_commerceChannel
		);

		Mockito.when(
			_commerceChannel.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_commerceContext.getCommerceChannelId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_commerceContext.getCommerceOrder()
		).thenReturn(
			_commerceOrder
		);

		Mockito.when(
			_commerceOrder.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_commerceOrder.getCommerceOrderId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		_mockHttpServletRequest.setAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT, _commerceContext);
	}

	@Test
	public void testResolve() throws PortalException {
		Assert.assertEquals(
			_pendingCommerceOrderFDSAPIURLResolver.resolve(
				StringBundler.concat(
					"/v1.0/channels/by-externalReferenceCode",
					"/{accountExternalReferenceCode}/{accountId}/{cartId}",
					"/{channelExternalReferenceCode}/{channelId}",
					"/{externalReferenceCode}"),
				_mockHttpServletRequest),
			StringBundler.concat(
				"/v1.0/channels/by-externalReferenceCode/",
				_accountEntry.getExternalReferenceCode(), "/",
				_accountEntry.getAccountEntryId(), "/",
				_commerceOrder.getCommerceOrderId(), "/",
				_commerceChannel.getExternalReferenceCode(), "/",
				_commerceChannel.getCommerceChannelId(), "/",
				_commerceOrder.getExternalReferenceCode()));
	}

	@Mock
	private AccountEntry _accountEntry;

	@Mock
	private CommerceChannel _commerceChannel;

	@Mock
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Mock
	private CommerceContext _commerceContext;

	@Mock
	private CommerceOrder _commerceOrder;

	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();

	@InjectMocks
	private PendingCommerceOrderFDSAPIURLResolver
		_pendingCommerceOrderFDSAPIURLResolver;

}