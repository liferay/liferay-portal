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
		_classPK = RandomTestUtil.randomString();
		_jsonObject = JSONUtil.put(
			"billingAddressId", RandomTestUtil.randomLong());

		Mockito.when(
			_accountEntry.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		_accountEntryValidatorConfiguration = Mockito.mock(
			AccountEntryValidatorConfiguration.class);
		_accountEntryValidatorResultManager = Mockito.mock(
			AccountEntryValidatorResultManager.class);
	}

	@Test
	public void testValidate() throws Exception {
		_testValidateWhenAccountEntryNull();
		_testValidateWhenClassPKNull();
		_testValidateWhenConfigurationDisabled();
		_testValidateWhenConfigurationNull();
		_testValidateWhenHistoryHit();
		_testValidateWhenHistoryMiss();
		_testValidateWhenResultNull();
	}

	private TestAccountEntryValidator _createTestAccountEntryValidator(
		AccountEntryValidatorConfiguration accountEntryValidatorConfiguration,
		String classPK,
		AccountEntryValidatorResult accountEntryValidatorResult) {

		TestAccountEntryValidator testAccountEntryValidator =
			new TestAccountEntryValidator(
				accountEntryValidatorConfiguration, classPK,
				accountEntryValidatorResult);

		testAccountEntryValidator.accountEntryValidatorResultManager =
			_accountEntryValidatorResultManager;

		return testAccountEntryValidator;
	}

	private void _resetMocks() {
		Mockito.reset(
			_accountEntryValidatorConfiguration,
			_accountEntryValidatorResultManager);

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
	}

	private void _testValidateWhenAccountEntryNull() throws Exception {
		_resetMocks();

		TestAccountEntryValidator testAccountEntryValidator =
			_createTestAccountEntryValidator(
				_accountEntryValidatorConfiguration, _classPK,
				AccountEntryValidatorResult.builder(
					_classPK
				).build());

		Assert.assertNull(
			testAccountEntryValidator.validate(null, _jsonObject));
		Assert.assertEquals(0, testAccountEntryValidator.getDoValidateCount());

		Mockito.verifyNoInteractions(_accountEntryValidatorResultManager);
	}

	private void _testValidateWhenClassPKNull() throws Exception {
		_resetMocks();

		AccountEntryValidatorResult accountEntryValidatorResult =
			AccountEntryValidatorResult.builder(
				RandomTestUtil.randomString()
			).build();

		TestAccountEntryValidator testAccountEntryValidator =
			_createTestAccountEntryValidator(
				_accountEntryValidatorConfiguration, null,
				accountEntryValidatorResult);

		Assert.assertSame(
			accountEntryValidatorResult,
			testAccountEntryValidator.validate(_accountEntry, _jsonObject));
		Assert.assertEquals(1, testAccountEntryValidator.getDoValidateCount());

		Mockito.verifyNoInteractions(_accountEntryValidatorResultManager);
	}

	private void _testValidateWhenConfigurationDisabled() throws Exception {
		_resetMocks();

		Mockito.when(
			_accountEntryValidatorConfiguration.enabled()
		).thenReturn(
			false
		);

		TestAccountEntryValidator testAccountEntryValidator =
			_createTestAccountEntryValidator(
				_accountEntryValidatorConfiguration, _classPK,
				AccountEntryValidatorResult.builder(
					_classPK
				).build());

		Assert.assertNull(
			testAccountEntryValidator.validate(_accountEntry, _jsonObject));
		Assert.assertEquals(0, testAccountEntryValidator.getDoValidateCount());

		Mockito.verifyNoInteractions(_accountEntryValidatorResultManager);
	}

	private void _testValidateWhenConfigurationNull() throws Exception {
		_resetMocks();

		TestAccountEntryValidator testAccountEntryValidator =
			_createTestAccountEntryValidator(
				null, _classPK,
				AccountEntryValidatorResult.builder(
					_classPK
				).build());

		Assert.assertNull(
			testAccountEntryValidator.validate(_accountEntry, _jsonObject));
		Assert.assertEquals(0, testAccountEntryValidator.getDoValidateCount());

		Mockito.verifyNoInteractions(_accountEntryValidatorResultManager);
	}

	private void _testValidateWhenHistoryHit() throws Exception {
		_resetMocks();

		AccountEntryValidatorResult cachedAccountEntryValidatorResult =
			AccountEntryValidatorResult.builder(
				_classPK
			).build();

		TestAccountEntryValidator testAccountEntryValidator =
			_createTestAccountEntryValidator(
				_accountEntryValidatorConfiguration, _classPK, null);

		Mockito.when(
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, testAccountEntryValidator.getClassName(),
					_classPK, 30)
		).thenReturn(
			cachedAccountEntryValidatorResult
		);

		Assert.assertSame(
			cachedAccountEntryValidatorResult,
			testAccountEntryValidator.validate(_accountEntry, _jsonObject));
		Assert.assertEquals(0, testAccountEntryValidator.getDoValidateCount());

		Mockito.verify(
			_accountEntryValidatorResultManager, Mockito.never()
		).addAccountEntryValidatorResult(
			Mockito.any(), Mockito.anyString(), Mockito.any()
		);
	}

	private void _testValidateWhenHistoryMiss() throws Exception {
		_resetMocks();

		AccountEntryValidatorResult accountEntryValidatorResult =
			AccountEntryValidatorResult.builder(
				_classPK
			).build();

		TestAccountEntryValidator testAccountEntryValidator =
			_createTestAccountEntryValidator(
				_accountEntryValidatorConfiguration, _classPK,
				accountEntryValidatorResult);

		Mockito.when(
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, testAccountEntryValidator.getClassName(),
					_classPK, 30)
		).thenReturn(
			null
		);

		Assert.assertSame(
			accountEntryValidatorResult,
			testAccountEntryValidator.validate(_accountEntry, _jsonObject));
		Assert.assertEquals(1, testAccountEntryValidator.getDoValidateCount());

		Mockito.verify(
			_accountEntryValidatorResultManager
		).addAccountEntryValidatorResult(
			_accountEntry, testAccountEntryValidator.getClassName(),
			accountEntryValidatorResult
		);
	}

	private void _testValidateWhenResultNull() throws Exception {
		_resetMocks();

		TestAccountEntryValidator testAccountEntryValidator =
			_createTestAccountEntryValidator(
				_accountEntryValidatorConfiguration, _classPK, null);

		Mockito.when(
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, testAccountEntryValidator.getClassName(),
					_classPK, 30)
		).thenReturn(
			null
		);

		Assert.assertNull(
			testAccountEntryValidator.validate(_accountEntry, _jsonObject));
		Assert.assertEquals(1, testAccountEntryValidator.getDoValidateCount());

		Mockito.verify(
			_accountEntryValidatorResultManager, Mockito.never()
		).addAccountEntryValidatorResult(
			Mockito.any(), Mockito.anyString(), Mockito.any()
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

		@Override
		protected AccountEntryValidatorResult doValidate(
			AccountEntry accountEntry, JSONObject jsonObject) {

			_doValidateCount++;

			return _accountEntryValidatorResult;
		}

		private TestAccountEntryValidator(
			AccountEntryValidatorConfiguration
				accountEntryValidatorConfiguration,
			String classPK,
			AccountEntryValidatorResult accountEntryValidatorResult) {

			_accountEntryValidatorConfiguration =
				accountEntryValidatorConfiguration;
			_classPK = classPK;
			_accountEntryValidatorResult = accountEntryValidatorResult;
		}

		private final AccountEntryValidatorConfiguration
			_accountEntryValidatorConfiguration;
		private final AccountEntryValidatorResult _accountEntryValidatorResult;
		private final String _classPK;
		private int _doValidateCount;

	}

}