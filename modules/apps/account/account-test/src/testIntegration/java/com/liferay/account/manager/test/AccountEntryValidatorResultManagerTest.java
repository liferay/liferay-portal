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
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.account.validator.BaseAccountEntryValidator;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Assume;
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
		_companyId = TestPropsValues.getCompanyId();

		_accountEntry = AccountEntryTestUtil.addAccountEntry();
	}

	@Test
	public void testAddThenGetValidReturnsResult() throws Exception {
		_assumeObjectDefinitionDeployed();

		String className = RandomTestUtil.randomString();
		String classPK = RandomTestUtil.randomString();

		_accountEntryValidatorResultManager.addAccountEntryValidatorResult(
			_accountEntry, className,
			AccountEntryValidatorResult.builder(
				classPK
			).resultMessage(
				"message"
			).resultStatus(
				AccountEntryValidatorConstants.RESULT_SUCCESS
			).build());

		AccountEntryValidatorResult accountEntryValidatorResult =
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, className, classPK, 30);

		Assert.assertNotNull(accountEntryValidatorResult);
		Assert.assertEquals(classPK, accountEntryValidatorResult.getClassPK());
		Assert.assertEquals(
			"message", accountEntryValidatorResult.getResultMessage());
		Assert.assertEquals(
			AccountEntryValidatorConstants.RESULT_SUCCESS,
			accountEntryValidatorResult.getResultStatus());
	}

	@Test
	public void testBaseValidatorPersistsThenSkips() throws Exception {
		_assumeObjectDefinitionDeployed();

		String classPK = RandomTestUtil.randomString();

		TestAccountEntryValidator testAccountEntryValidator =
			new TestAccountEntryValidator(classPK);

		ReflectionTestUtil.setFieldValue(
			testAccountEntryValidator, "accountEntryValidatorResultManager",
			_accountEntryValidatorResultManager);

		JSONObject jsonObject = JSONUtil.put("billingAddressId", 1L);

		AccountEntryValidatorResult accountEntryValidatorResult =
			testAccountEntryValidator.validate(_accountEntry, jsonObject);

		Assert.assertNotNull(accountEntryValidatorResult);

		Assert.assertEquals(1, testAccountEntryValidator.getDoValidateCount());

		testAccountEntryValidator.validate(_accountEntry, jsonObject);

		Assert.assertEquals(1, testAccountEntryValidator.getDoValidateCount());
	}

	@Test
	public void testGetValidReturnsNullWhenNeverRun() throws Exception {
		Assert.assertNull(
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), 30));
	}

	private void _assumeObjectDefinitionDeployed() {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_ACCOUNT_VALIDATOR_RESULT", _companyId);

		Assume.assumeNotNull(objectDefinition);
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryValidatorResultManager
		_accountEntryValidatorResultManager;

	private long _companyId;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

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