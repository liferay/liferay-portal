/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.validator;

import com.liferay.account.configuration.AccountEntryValidatorConfiguration;
import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidator;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory.ServiceWrapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Tancredi Covioli
 */
public class AccountEntryValidatorRegistryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_accountEntryValidatorRegistryImpl, "_serviceTrackerMap",
			_serviceTrackerMap);
	}

	@Test
	public void testGetAccountEntryValidator() {
		AccountEntryValidator accountEntryValidator = Mockito.mock(
			AccountEntryValidator.class);

		ServiceWrapper<AccountEntryValidator> serviceWrapper =
			_getServiceWrapper(accountEntryValidator);

		Mockito.when(
			_serviceTrackerMap.getService("key")
		).thenReturn(
			serviceWrapper
		);

		Assert.assertNull(
			_accountEntryValidatorRegistryImpl.getAccountEntryValidator(
				StringPool.BLANK));

		Mockito.verifyNoInteractions(_serviceTrackerMap);

		Assert.assertSame(
			accountEntryValidator,
			_accountEntryValidatorRegistryImpl.getAccountEntryValidator("key"));

		Mockito.when(
			_serviceTrackerMap.getService("key")
		).thenReturn(
			null
		);

		Assert.assertNull(
			_accountEntryValidatorRegistryImpl.getAccountEntryValidator("key"));
	}

	@Test
	public void testGetAccountEntryValidators() {
		AccountEntryValidator accountEntryValidator1 = Mockito.mock(
			AccountEntryValidator.class);
		AccountEntryValidator accountEntryValidator2 = Mockito.mock(
			AccountEntryValidator.class);

		List<ServiceWrapper<AccountEntryValidator>> serviceWrappers =
			Arrays.asList(
				_getServiceWrapper(accountEntryValidator1),
				_getServiceWrapper(accountEntryValidator2));

		Mockito.when(
			_serviceTrackerMap.values()
		).thenReturn(
			serviceWrappers
		);

		List<AccountEntryValidator> accountEntryValidators =
			_accountEntryValidatorRegistryImpl.getAccountEntryValidators();

		Assert.assertEquals(
			accountEntryValidators.toString(), 2,
			accountEntryValidators.size());
		Assert.assertSame(
			accountEntryValidator1, accountEntryValidators.get(0));
		Assert.assertSame(
			accountEntryValidator2, accountEntryValidators.get(1));
	}

	@Test
	public void testValidate() throws Exception {
		List<AccountEntryValidatorResult> accountEntryValidatorResults =
			_accountEntryValidatorRegistryImpl.validate(null, null);

		Assert.assertTrue(accountEntryValidatorResults.isEmpty());

		Mockito.verifyNoInteractions(_serviceTrackerMap);

		AccountEntryValidatorConfiguration
			enabledAccountEntryValidatorConfiguration = Mockito.mock(
				AccountEntryValidatorConfiguration.class);

		Mockito.when(
			enabledAccountEntryValidatorConfiguration.enabled()
		).thenReturn(
			true
		);

		AccountEntryValidatorConfiguration
			disabledAccountEntryValidatorConfiguration = Mockito.mock(
				AccountEntryValidatorConfiguration.class);

		Mockito.when(
			disabledAccountEntryValidatorConfiguration.enabled()
		).thenReturn(
			false
		);

		AccountEntryValidator accountEntryValidator1 = Mockito.mock(
			AccountEntryValidator.class);

		AccountEntry accountEntry = Mockito.mock(AccountEntry.class);

		long companyId = accountEntry.getCompanyId();

		Mockito.when(
			accountEntryValidator1.getAccountEntryValidatorConfiguration(
				companyId)
		).thenReturn(
			enabledAccountEntryValidatorConfiguration
		);

		JSONObject jsonObject = JSONUtil.put(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		AccountEntryValidatorResult accountEntryValidatorResult1 =
			AccountEntryValidatorResult.builder(
				RandomTestUtil.randomString()
			).build();

		Mockito.when(
			accountEntryValidator1.validate(accountEntry, jsonObject)
		).thenReturn(
			accountEntryValidatorResult1
		);

		AccountEntryValidator accountEntryValidator2 = Mockito.mock(
			AccountEntryValidator.class);

		Mockito.when(
			accountEntryValidator2.getAccountEntryValidatorConfiguration(
				companyId)
		).thenReturn(
			enabledAccountEntryValidatorConfiguration
		);

		AccountEntryValidatorResult accountEntryValidatorResult2 =
			AccountEntryValidatorResult.builder(
				RandomTestUtil.randomString()
			).resultStatus(
				AccountEntryValidatorConstants.RESULT_FAILURE
			).build();

		Mockito.when(
			accountEntryValidator2.validate(accountEntry, jsonObject)
		).thenReturn(
			accountEntryValidatorResult2
		);

		AccountEntryValidator accountEntryValidator3 = Mockito.mock(
			AccountEntryValidator.class);

		Mockito.when(
			accountEntryValidator3.getAccountEntryValidatorConfiguration(
				companyId)
		).thenReturn(
			disabledAccountEntryValidatorConfiguration
		);

		List<ServiceWrapper<AccountEntryValidator>> serviceWrappers =
			Arrays.asList(
				_getServiceWrapper(accountEntryValidator1),
				_getServiceWrapper(accountEntryValidator2),
				_getServiceWrapper(accountEntryValidator3));

		Mockito.when(
			_serviceTrackerMap.values()
		).thenReturn(
			serviceWrappers
		);

		accountEntryValidatorResults =
			_accountEntryValidatorRegistryImpl.validate(
				accountEntry, jsonObject);

		Mockito.verify(
			accountEntryValidator1
		).validate(
			accountEntry, jsonObject
		);

		Mockito.verify(
			accountEntryValidator2
		).validate(
			accountEntry, jsonObject
		);

		Mockito.verify(
			accountEntryValidator3, Mockito.never()
		).validate(
			Mockito.any(), Mockito.any()
		);

		Assert.assertEquals(
			accountEntryValidatorResults.toString(), 2,
			accountEntryValidatorResults.size());
		Assert.assertSame(
			accountEntryValidatorResult1, accountEntryValidatorResults.get(0));
		Assert.assertSame(
			accountEntryValidatorResult2, accountEntryValidatorResults.get(1));
	}

	private ServiceWrapper<AccountEntryValidator> _getServiceWrapper(
		AccountEntryValidator accountEntryValidator) {

		ServiceWrapper<AccountEntryValidator> serviceWrapper = Mockito.mock(
			ServiceWrapper.class);

		Mockito.when(
			serviceWrapper.getService()
		).thenReturn(
			accountEntryValidator
		);

		return serviceWrapper;
	}

	private final AccountEntryValidatorRegistryImpl
		_accountEntryValidatorRegistryImpl =
			new AccountEntryValidatorRegistryImpl();
	private final ServiceTrackerMap
		<String, ServiceWrapper<AccountEntryValidator>> _serviceTrackerMap =
			Mockito.mock(ServiceTrackerMap.class);

}