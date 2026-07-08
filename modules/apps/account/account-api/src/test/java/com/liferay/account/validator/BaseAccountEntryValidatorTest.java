/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.validator;

import com.liferay.account.configuration.AccountEntryValidatorConfiguration;
import com.liferay.account.manager.AccountEntryValidatorResultManager;
import com.liferay.account.model.AccountEntry;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Tancredi Covioli
 */
public class BaseAccountEntryValidatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_accountEntry = Mockito.mock(AccountEntry.class);

		Mockito.when(
			_accountEntry.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		_accountEntryValidatorConfiguration = Mockito.mock(
			AccountEntryValidatorConfiguration.class);
		_accountEntryValidatorResultManager = Mockito.mock(
			AccountEntryValidatorResultManager.class);
		_classPK = RandomTestUtil.randomString();
		_jsonObject = JSONUtil.put(
			"billingAddressId", RandomTestUtil.randomLong());
	}

	@Test
	public void testValidate() throws Exception {
		Mockito.when(
			_accountEntryValidatorConfiguration.checkInterval()
		).thenReturn(
			30
		);

		Mockito.when(
			_accountEntryValidatorConfiguration.enabled()
		).thenReturn(
			true
		);

		AccountEntryValidatorResult accountEntryValidatorResult1 =
			AccountEntryValidatorResult.builder(
				_classPK
			).build();

		TestAccountEntryValidator testAccountEntryValidator =
			new TestAccountEntryValidator();

		testAccountEntryValidator.accountEntryValidatorResultManager =
			_accountEntryValidatorResultManager;

		testAccountEntryValidator.setAccountEntryValidatorConfiguration(
			_accountEntryValidatorConfiguration);
		testAccountEntryValidator.setAccountEntryValidatorResult(
			accountEntryValidatorResult1);
		testAccountEntryValidator.setClassPK(_classPK);

		Assert.assertNull(
			testAccountEntryValidator.validate(null, _jsonObject));
		Assert.assertEquals(0, testAccountEntryValidator.getDoValidateCount());

		testAccountEntryValidator.setAccountEntryValidatorConfiguration(null);

		Assert.assertNull(
			testAccountEntryValidator.validate(_accountEntry, _jsonObject));
		Assert.assertEquals(0, testAccountEntryValidator.getDoValidateCount());

		testAccountEntryValidator.setAccountEntryValidatorConfiguration(
			_accountEntryValidatorConfiguration);

		Mockito.when(
			_accountEntryValidatorConfiguration.enabled()
		).thenReturn(
			false
		);

		Assert.assertNull(
			testAccountEntryValidator.validate(_accountEntry, _jsonObject));
		Assert.assertEquals(0, testAccountEntryValidator.getDoValidateCount());

		Mockito.when(
			_accountEntryValidatorConfiguration.enabled()
		).thenReturn(
			true
		);

		testAccountEntryValidator.setClassPK(null);

		Assert.assertSame(
			accountEntryValidatorResult1,
			testAccountEntryValidator.validate(_accountEntry, _jsonObject));
		Assert.assertEquals(1, testAccountEntryValidator.getDoValidateCount());

		Mockito.verifyNoInteractions(_accountEntryValidatorResultManager);

		testAccountEntryValidator.setClassPK(_classPK);

		AccountEntryValidatorResult accountEntryValidatorResult2 =
			AccountEntryValidatorResult.builder(
				_classPK
			).build();

		Mockito.when(
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, 30, testAccountEntryValidator.getClassName(),
					_classPK)
		).thenReturn(
			accountEntryValidatorResult2
		);

		Assert.assertSame(
			accountEntryValidatorResult2,
			testAccountEntryValidator.validate(_accountEntry, _jsonObject));

		Assert.assertEquals(1, testAccountEntryValidator.getDoValidateCount());

		Mockito.when(
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, 30, testAccountEntryValidator.getClassName(),
					_classPK)
		).thenReturn(
			null
		);

		Assert.assertSame(
			accountEntryValidatorResult1,
			testAccountEntryValidator.validate(_accountEntry, _jsonObject));
		Assert.assertEquals(2, testAccountEntryValidator.getDoValidateCount());

		Mockito.verify(
			_accountEntryValidatorResultManager
		).addAccountEntryValidatorResult(
			_accountEntry, accountEntryValidatorResult1,
			testAccountEntryValidator.getClassName()
		);

		testAccountEntryValidator.setAccountEntryValidatorResult(null);

		Assert.assertNull(
			testAccountEntryValidator.validate(_accountEntry, _jsonObject));
		Assert.assertEquals(3, testAccountEntryValidator.getDoValidateCount());

		Mockito.verify(
			_accountEntryValidatorResultManager, Mockito.times(1)
		).addAccountEntryValidatorResult(
			_accountEntry, accountEntryValidatorResult1,
			testAccountEntryValidator.getClassName()
		);
	}

	private AccountEntry _accountEntry;
	private AccountEntryValidatorConfiguration
		_accountEntryValidatorConfiguration;
	private AccountEntryValidatorResultManager
		_accountEntryValidatorResultManager;
	private String _classPK;
	private JSONObject _jsonObject;

	private static class TestAccountEntryValidator
		extends BaseAccountEntryValidator {

		@Override
		public AccountEntryValidatorConfiguration
			getAccountEntryValidatorConfiguration(long companyId) {

			return _accountEntryValidatorConfiguration;
		}

		public String getClassName() {
			Class<?> clazz = getClass();

			return clazz.getName();
		}

		@Override
		public String getClassPK(
			AccountEntry accountEntry, JSONObject jsonObject) {

			return _classPK;
		}

		public int getDoValidateCount() {
			return _doValidateCount;
		}

		public void setAccountEntryValidatorConfiguration(
			AccountEntryValidatorConfiguration
				accountEntryValidatorConfiguration) {

			_accountEntryValidatorConfiguration =
				accountEntryValidatorConfiguration;
		}

		public void setAccountEntryValidatorResult(
			AccountEntryValidatorResult accountEntryValidatorResult) {

			_accountEntryValidatorResult = accountEntryValidatorResult;
		}

		public void setClassPK(String classPK) {
			_classPK = classPK;
		}

		@Override
		protected AccountEntryValidatorResult doValidate(
			AccountEntry accountEntry, JSONObject jsonObject) {

			_doValidateCount++;

			return _accountEntryValidatorResult;
		}

		private AccountEntryValidatorConfiguration
			_accountEntryValidatorConfiguration;
		private AccountEntryValidatorResult _accountEntryValidatorResult;
		private String _classPK;
		private int _doValidateCount;

	}

}