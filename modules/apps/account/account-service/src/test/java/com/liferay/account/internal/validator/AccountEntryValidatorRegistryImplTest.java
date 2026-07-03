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
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory.ServiceWrapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
			_accountEntryValidatorRegistryImpl, "_filterFactory",
			_filterFactory);
		ReflectionTestUtil.setFieldValue(
			_accountEntryValidatorRegistryImpl, "_objectDefinitionLocalService",
			_objectDefinitionLocalService);
		ReflectionTestUtil.setFieldValue(
			_accountEntryValidatorRegistryImpl, "_objectEntryLocalService",
			_objectEntryLocalService);
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
	public void testGetLastAccountEntryValidatorResultsMap() throws Exception {
		Map<String, AccountEntryValidatorResult>
			accountEntryValidatorResultsMap =
				_accountEntryValidatorRegistryImpl.
					getLastAccountEntryValidatorResultsMap(null, null);

		Assert.assertTrue(accountEntryValidatorResultsMap.isEmpty());

		Mockito.verifyNoInteractions(_objectDefinitionLocalService);
		Mockito.verifyNoInteractions(_objectEntryLocalService);

		AccountEntry accountEntry = Mockito.mock(AccountEntry.class);

		Mockito.when(
			accountEntry.getCompanyId()
		).thenReturn(
			1L
		);

		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_ACCOUNT_VALIDATOR_RESULT", 1L)
		).thenReturn(
			null
		);

		Mockito.when(
			_serviceTrackerMap.values()
		).thenReturn(
			Collections.emptyList()
		);

		accountEntryValidatorResultsMap =
			_accountEntryValidatorRegistryImpl.
				getLastAccountEntryValidatorResultsMap(accountEntry, null);

		Assert.assertTrue(accountEntryValidatorResultsMap.isEmpty());

		Mockito.when(
			accountEntry.getAccountEntryId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			accountEntry.getUserId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		AccountEntryValidator accountEntryValidator = Mockito.mock(
			AccountEntryValidator.class);
		AccountEntryValidatorConfiguration accountEntryValidatorConfiguration =
			Mockito.mock(AccountEntryValidatorConfiguration.class);

		Mockito.when(
			accountEntryValidator.getAccountEntryValidatorConfiguration(1L)
		).thenReturn(
			accountEntryValidatorConfiguration
		);

		String classPK = RandomTestUtil.randomString();

		Mockito.when(
			accountEntryValidator.getClassPK(accountEntry, null)
		).thenReturn(
			classPK
		);

		Mockito.when(
			accountEntryValidatorConfiguration.enabled()
		).thenReturn(
			false
		);

		Mockito.when(
			_filterFactory.create(Mockito.anyString(), Mockito.any())
		).thenReturn(
			null
		);

		List<ServiceWrapper<AccountEntryValidator>> serviceWrappers =
			Arrays.asList(_getServiceWrapper(accountEntryValidator));

		Mockito.when(
			_serviceTrackerMap.values()
		).thenReturn(
			serviceWrappers
		);

		Class<? extends AccountEntryValidator> accountEntryValidatorClass =
			accountEntryValidator.getClass();

		String className = accountEntryValidatorClass.getName();

		accountEntryValidatorResultsMap =
			_accountEntryValidatorRegistryImpl.
				getLastAccountEntryValidatorResultsMap(accountEntry, null);

		Assert.assertTrue(accountEntryValidatorResultsMap.isEmpty());

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_ACCOUNT_VALIDATOR_RESULT", 1L)
		).thenReturn(
			objectDefinition
		);

		Mockito.when(
			objectDefinition.getObjectDefinitionId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_objectEntryLocalService.getValuesList(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.any(), Mockito.any(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Sort[].class))
		).thenReturn(
			Collections.emptyList()
		);

		accountEntryValidatorResultsMap =
			_accountEntryValidatorRegistryImpl.
				getLastAccountEntryValidatorResultsMap(accountEntry, null);

		Assert.assertTrue(accountEntryValidatorResultsMap.isEmpty());

		Mockito.verify(
			accountEntryValidator, Mockito.times(0)
		).getClassPK(
			Mockito.any(), Mockito.any()
		);

		Mockito.when(
			accountEntryValidatorConfiguration.enabled()
		).thenReturn(
			true
		);

		accountEntryValidatorResultsMap =
			_accountEntryValidatorRegistryImpl.
				getLastAccountEntryValidatorResultsMap(accountEntry, null);

		Assert.assertEquals(
			accountEntryValidatorResultsMap.toString(), 1,
			accountEntryValidatorResultsMap.size());
		Assert.assertTrue(
			accountEntryValidatorResultsMap.containsKey(className));
		Assert.assertNull(accountEntryValidatorResultsMap.get(className));

		String resultMessage = RandomTestUtil.randomString();

		Mockito.when(
			_objectEntryLocalService.getValuesList(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.any(), Mockito.any(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Sort[].class))
		).thenReturn(
			Collections.singletonList(
				HashMapBuilder.<String, Serializable>put(
					"resultStatus",
					AccountEntryValidatorConstants.RESULT_FAILURE
				).build())
		);

		accountEntryValidatorResultsMap =
			_accountEntryValidatorRegistryImpl.
				getLastAccountEntryValidatorResultsMap(accountEntry, null);

		AccountEntryValidatorResult accountEntryValidatorResult =
			accountEntryValidatorResultsMap.get(className);

		Assert.assertEquals(
			AccountEntryValidatorConstants.RESULT_FAILURE,
			accountEntryValidatorResult.getResultStatus());

		Mockito.when(
			_objectEntryLocalService.getValuesList(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.any(), Mockito.any(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Sort[].class))
		).thenReturn(
			Collections.singletonList(
				HashMapBuilder.<String, Serializable>put(
					"resultMessage", resultMessage
				).put(
					"resultStatus",
					AccountEntryValidatorConstants.RESULT_SUCCESS
				).build())
		);

		accountEntryValidatorResultsMap =
			_accountEntryValidatorRegistryImpl.
				getLastAccountEntryValidatorResultsMap(accountEntry, null);

		accountEntryValidatorResult = accountEntryValidatorResultsMap.get(
			className);

		Assert.assertEquals(classPK, accountEntryValidatorResult.getKey());
		Assert.assertEquals(
			resultMessage, accountEntryValidatorResult.getResultMessage());
		Assert.assertEquals(
			AccountEntryValidatorConstants.RESULT_SUCCESS,
			accountEntryValidatorResult.getResultStatus());
	}

	@Test
	public void testValidate() throws Exception {
		List<AccountEntryValidatorResult> accountEntryValidatorResults =
			_accountEntryValidatorRegistryImpl.validate(null, null);

		Assert.assertTrue(accountEntryValidatorResults.isEmpty());

		Mockito.verifyNoInteractions(_serviceTrackerMap);

		AccountEntryValidatorConfiguration
			enabledAccountEntryValidatorConfiguration1 = Mockito.mock(
				AccountEntryValidatorConfiguration.class);

		Mockito.when(
			enabledAccountEntryValidatorConfiguration1.enabled()
		).thenReturn(
			true
		);

		AccountEntryValidator accountEntryValidator1 = Mockito.mock(
			AccountEntryValidator.class);

		AccountEntry accountEntry = Mockito.mock(AccountEntry.class);

		long companyId = accountEntry.getCompanyId();

		Mockito.when(
			accountEntryValidator1.getAccountEntryValidatorConfiguration(
				companyId)
		).thenReturn(
			enabledAccountEntryValidatorConfiguration1
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
			enabledAccountEntryValidatorConfiguration1
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

		AccountEntryValidatorConfiguration accountEntryValidatorConfiguration2 =
			Mockito.mock(AccountEntryValidatorConfiguration.class);

		Mockito.when(
			accountEntryValidatorConfiguration2.enabled()
		).thenReturn(
			false
		);

		Mockito.when(
			accountEntryValidator3.getAccountEntryValidatorConfiguration(
				companyId)
		).thenReturn(
			accountEntryValidatorConfiguration2
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
	private final FilterFactory<Predicate> _filterFactory = Mockito.mock(
		FilterFactory.class);
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);
	private final ServiceTrackerMap
		<String, ServiceWrapper<AccountEntryValidator>> _serviceTrackerMap =
			Mockito.mock(ServiceTrackerMap.class);

}