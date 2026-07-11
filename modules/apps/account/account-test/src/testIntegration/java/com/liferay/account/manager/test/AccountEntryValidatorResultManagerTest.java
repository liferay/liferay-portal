/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.manager.test;

import com.liferay.account.configuration.AccountEntryValidatorConfiguration;
import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.account.manager.AccountEntryValidatorResultManager;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.test.util.AccountEntryTestUtil;
import com.liferay.account.service.test.util.AccountEntryValidatorResultTestUtil;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.account.validator.BaseAccountEntryValidator;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tancredi Covioli
 */
@FeatureFlag("LPD-89850")
@RunWith(Arquillian.class)
public class AccountEntryValidatorResultManagerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		AccountEntryValidatorResultTestUtil.getOrAddObjectDefinition(
			AccountEntryValidatorResultManagerTest.class);

		_accountEntry = AccountEntryTestUtil.addAccountEntry();
	}

	@Test
	public void testGetValidAccountEntryValidatorResult() throws Exception {
		String className = RandomTestUtil.randomString();
		String classPK = RandomTestUtil.randomString();

		Assert.assertNull(
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, 30, className, classPK));

		_accountEntryValidatorResultManager.addAccountEntryValidatorResult(
			_accountEntry,
			AccountEntryValidatorResult.builder(
				classPK
			).resultStatus(
				AccountEntryValidatorConstants.RESULT_FAILURE
			).build(),
			className);

		Assert.assertNull(
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, 30, className, classPK));

		_accountEntryValidatorResultManager.addAccountEntryValidatorResult(
			_accountEntry,
			AccountEntryValidatorResult.builder(
				classPK
			).resultStatus(
				AccountEntryValidatorConstants.RESULT_MANUAL
			).build(),
			className);

		AccountEntryValidatorResult manualAccountEntryValidatorResult =
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, 30, className, classPK);

		Assert.assertEquals(
			AccountEntryValidatorConstants.RESULT_MANUAL,
			manualAccountEntryValidatorResult.getResultStatus());

		String resultMessage = RandomTestUtil.randomString();

		_accountEntryValidatorResultManager.addAccountEntryValidatorResult(
			_accountEntry,
			AccountEntryValidatorResult.builder(
				classPK
			).resultMessage(
				resultMessage
			).resultStatus(
				AccountEntryValidatorConstants.RESULT_SUCCESS
			).build(),
			className);

		Assert.assertNull(
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, 0, className, classPK));
		Assert.assertNull(
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, 30, RandomTestUtil.randomString(), classPK));
		Assert.assertNull(
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, 30, className,
					RandomTestUtil.randomString()));

		AccountEntryValidatorResult accountEntryValidatorResult =
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, 30, className, classPK);

		Assert.assertEquals(classPK, accountEntryValidatorResult.getClassPK());
		Assert.assertEquals(
			resultMessage, accountEntryValidatorResult.getResultMessage());
		Assert.assertEquals(
			AccountEntryValidatorConstants.RESULT_SUCCESS,
			accountEntryValidatorResult.getResultStatus());
	}

	@Test
	public void testValidate() throws Exception {
		TestAccountEntryValidator testAccountEntryValidator =
			new TestAccountEntryValidator(RandomTestUtil.randomString());

		ReflectionTestUtil.setFieldValue(
			testAccountEntryValidator, "accountEntryValidatorResultManager",
			_accountEntryValidatorResultManager);

		JSONObject jsonObject = JSONUtil.put("billingAddressId", 1L);

		testAccountEntryValidator.validate(_accountEntry, jsonObject);

		Assert.assertEquals(1, testAccountEntryValidator.getDoValidateCount());

		testAccountEntryValidator.validate(_accountEntry, jsonObject);

		Assert.assertEquals(1, testAccountEntryValidator.getDoValidateCount());
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryValidatorResultManager
		_accountEntryValidatorResultManager;

	private static class TestAccountEntryValidator
		extends BaseAccountEntryValidator {

		@Override
		public AccountEntryValidatorConfiguration
			getAccountEntryValidatorConfiguration(long companyId) {

			return new AccountEntryValidatorConfiguration() {

				@Override
				public int checkInterval() {
					return 30;
				}

				@Override
				public boolean enabled() {
					return true;
				}

			};
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

			return AccountEntryValidatorResult.builder(
				_classPK
			).resultStatus(
				AccountEntryValidatorConstants.RESULT_SUCCESS
			).build();
		}

		private TestAccountEntryValidator(String classPK) {
			_classPK = classPK;
		}

		private final String _classPK;
		private int _doValidateCount;

	}

}