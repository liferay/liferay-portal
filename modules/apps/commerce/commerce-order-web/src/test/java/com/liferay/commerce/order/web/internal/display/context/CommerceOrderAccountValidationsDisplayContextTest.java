/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.display.context;

import com.liferay.account.validator.AccountEntryValidator;
import com.liferay.account.validator.AccountEntryValidatorRegistry;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Crescenzo Rega
 */
public class CommerceOrderAccountValidationsDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		_commerceOrderAccountValidationsDisplayContext =
			new CommerceOrderAccountValidationsDisplayContext(
				_accountEntryValidatorRegistry, _commerceOrder, _locale);
	}

	@Test
	public void testGetAdditionalProps() {
		try (MockedStatic<LanguageUtil> languageUtilMockedStatic =
				Mockito.mockStatic(LanguageUtil.class)) {

			languageUtilMockedStatic.when(
				() -> LanguageUtil.get(Mockito.eq(_locale), Mockito.anyString())
			).thenAnswer(
				invocation -> "translated-" + invocation.getArgument(1)
			);

			Mockito.when(
				_accountEntryValidatorRegistry.getAccountEntryValidators()
			).thenReturn(
				Collections.emptyList()
			);

			Map<String, Object> additionalProps =
				_commerceOrderAccountValidationsDisplayContext.
					getAdditionalProps();

			Assert.assertEquals(
				Collections.emptyMap(), additionalProps.get("resultMessages"));

			AccountEntryValidator accountEntryValidator1 =
				_mockAccountEntryValidator(
					Collections.singleton("account-validation-failed"));
			AccountEntryValidator accountEntryValidator2 =
				_mockAccountEntryValidator(
					Set.of(
						"account-validation-failed",
						"the-account-is-missing-a-tax-id"));
			AccountEntryValidator accountEntryValidator3 =
				_mockAccountEntryValidator(Collections.emptySet());

			Mockito.when(
				_accountEntryValidatorRegistry.getAccountEntryValidators()
			).thenReturn(
				List.of(
					accountEntryValidator1, accountEntryValidator2,
					accountEntryValidator3)
			);

			additionalProps =
				_commerceOrderAccountValidationsDisplayContext.
					getAdditionalProps();

			Assert.assertEquals(
				HashMapBuilder.put(
					"account-validation-failed",
					"translated-account-validation-failed"
				).put(
					"the-account-is-missing-a-tax-id",
					"translated-the-account-is-missing-a-tax-id"
				).build(),
				additionalProps.get("resultMessages"));
		}
	}

	private AccountEntryValidator _mockAccountEntryValidator(
		Set<String> resultMessages) {

		AccountEntryValidator accountEntryValidator = Mockito.mock(
			AccountEntryValidator.class);

		Mockito.when(
			accountEntryValidator.getResultMessages()
		).thenReturn(
			resultMessages
		);

		return accountEntryValidator;
	}

	@Mock
	private AccountEntryValidatorRegistry _accountEntryValidatorRegistry;

	@Mock
	private CommerceOrder _commerceOrder;

	private CommerceOrderAccountValidationsDisplayContext
		_commerceOrderAccountValidationsDisplayContext;
	private final Locale _locale = LocaleUtil.US;

}