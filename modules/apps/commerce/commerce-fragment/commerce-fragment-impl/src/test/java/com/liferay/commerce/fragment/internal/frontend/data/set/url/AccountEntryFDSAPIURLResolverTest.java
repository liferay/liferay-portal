/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.internal.frontend.data.set.url;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
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
 * @author Alessio Antonio Rendina
 */
public class AccountEntryFDSAPIURLResolverTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		Mockito.when(
			_commerceContext.getAccountEntryAllowedTypes()
		).thenReturn(
			new String[] {RandomTestUtil.randomString()}
		);

		Mockito.when(
			_commerceContext.getCommerceChannelId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		_mockHttpServletRequest.setAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT, _commerceContext);
	}

	@Test
	public void testResolve() throws PortalException {
		String[] accountEntryAllowedTypes =
			_commerceContext.getAccountEntryAllowedTypes();

		Assert.assertEquals(
			_accountEntryFDSAPIURLResolver.resolve(
				"/v1.0/channels/{channelId}/accounts" +
					"?filter=type in ({filter})&sort=name:asc",
				_mockHttpServletRequest),
			StringBundler.concat(
				"/v1.0/channels/", _commerceContext.getCommerceChannelId(),
				"/accounts?filter=type in ('", accountEntryAllowedTypes[0],
				"')&sort=name:asc"));
	}

	@InjectMocks
	private AccountEntryFDSAPIURLResolver _accountEntryFDSAPIURLResolver;

	@Mock
	private CommerceContext _commerceContext;

	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();

}