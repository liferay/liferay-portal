/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.manager;

import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.account.manager.AccountEntryValidatorResultManager;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Tancredi Covioli
 */
public class AccountEntryValidatorResultManagerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_accountEntry = Mockito.mock(AccountEntry.class);
		_accountEntryId = RandomTestUtil.randomLong();
		_accountEntryValidatorResultManager =
			new AccountEntryValidatorResultManagerImpl();
		_className = RandomTestUtil.randomString();
		_classPK = RandomTestUtil.randomString();
		_companyId = RandomTestUtil.randomLong();
		_jsonFactory = Mockito.mock(JSONFactory.class);
		_objectDefinitionLocalService = Mockito.mock(
			ObjectDefinitionLocalService.class);
		_objectEntryLocalService = Mockito.mock(ObjectEntryLocalService.class);
		_objectFieldLocalService = Mockito.mock(ObjectFieldLocalService.class);
		_userId = RandomTestUtil.randomLong();

		Mockito.when(
			_accountEntry.getAccountEntryId()
		).thenReturn(
			_accountEntryId
		);

		Mockito.when(
			_accountEntry.getCompanyId()
		).thenReturn(
			_companyId
		);

		Mockito.when(
			_accountEntry.getUserId()
		).thenReturn(
			_userId
		);

		ReflectionTestUtil.setFieldValue(
			_accountEntryValidatorResultManager, "_jsonFactory", _jsonFactory);
		ReflectionTestUtil.setFieldValue(
			_accountEntryValidatorResultManager,
			"_objectDefinitionLocalService", _objectDefinitionLocalService);
		ReflectionTestUtil.setFieldValue(
			_accountEntryValidatorResultManager, "_objectEntryLocalService",
			_objectEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_accountEntryValidatorResultManager, "_objectFieldLocalService",
			_objectFieldLocalService);
	}

	@Test
	public void testAddAccountEntryValidatorResult() throws Exception {
		_testAddAccountEntryValidatorResult();
		_testAddAccountEntryValidatorResultWhenObjectDefinitionMissing();
	}

	@Test
	public void testGetValidAccountEntryValidatorResult() throws Exception {
		_testGetValidAccountEntryValidatorResultWhenFailure();
		_testGetValidAccountEntryValidatorResultWhenFreshManual();
		_testGetValidAccountEntryValidatorResultWhenFreshSuccess();
		_testGetValidAccountEntryValidatorResultWhenNeverRun();
		_testGetValidAccountEntryValidatorResultWhenObjectDefinitionMissing();
		_testGetValidAccountEntryValidatorResultWhenStale();
	}

	private ObjectEntry _mockObjectEntry(
		String resultStatus, String resultMessage, Date createDate) {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getCreateDate()
		).thenReturn(
			createDate
		);

		Mockito.when(
			objectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"resultMessage", resultMessage
			).put(
				"resultStatus", resultStatus
			).build()
		);

		return objectEntry;
	}

	private void _resetMocks() {
		Mockito.reset(
			_jsonFactory, _objectDefinitionLocalService,
			_objectEntryLocalService, _objectFieldLocalService);
	}

	private void _setUpObjectEntry(ObjectEntry objectEntry) throws Exception {
		long objectDefinitionId = RandomTestUtil.randomLong();

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getObjectDefinitionId()
		).thenReturn(
			objectDefinitionId
		);

		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_ACCOUNT_VALIDATOR_RESULT", _companyId)
		).thenReturn(
			objectDefinition
		);

		Column<?, Object> column = Mockito.mock(Column.class);
		Predicate predicate = Mockito.mock(Predicate.class);

		Mockito.when(
			column.eq(Mockito.any(Object.class))
		).thenReturn(
			predicate
		);

		Mockito.when(
			predicate.and(Mockito.any(Predicate.class))
		).thenReturn(
			predicate
		);

		Mockito.doReturn(
			column
		).when(
			_objectFieldLocalService
		).getColumn(
			Mockito.eq(objectDefinitionId), Mockito.anyString()
		);

		List<Long> primaryKeys = Collections.emptyList();

		if (objectEntry != null) {
			long objectEntryId = RandomTestUtil.randomLong();

			primaryKeys = Arrays.asList(objectEntryId);

			Mockito.when(
				_objectEntryLocalService.fetchObjectEntry(objectEntryId)
			).thenReturn(
				objectEntry
			);
		}

		Mockito.when(
			_objectEntryLocalService.getPrimaryKeys(
				Mockito.any(Long[].class), Mockito.eq(_companyId),
				Mockito.eq(_userId), Mockito.eq(objectDefinitionId),
				Mockito.any(), Mockito.anyBoolean(), Mockito.isNull(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Sort[].class))
		).thenReturn(
			primaryKeys
		);
	}

	private void _testAddAccountEntryValidatorResult() throws Exception {
		_resetMocks();

		long objectDefinitionId = RandomTestUtil.randomLong();

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getObjectDefinitionId()
		).thenReturn(
			objectDefinitionId
		);

		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_ACCOUNT_VALIDATOR_RESULT", _companyId)
		).thenReturn(
			objectDefinition
		);

		_accountEntryValidatorResultManager.addAccountEntryValidatorResult(
			_accountEntry, _className,
			AccountEntryValidatorResult.builder(
				_classPK
			).resultMessage(
				"message"
			).resultStatus(
				AccountEntryValidatorConstants.RESULT_SUCCESS
			).build());

		ArgumentCaptor<Map<String, Serializable>> valuesArgumentCaptor =
			ArgumentCaptor.forClass(Map.class);

		Mockito.verify(
			_objectEntryLocalService
		).addObjectEntry(
			Mockito.eq(0L), Mockito.eq(_userId), Mockito.eq(objectDefinitionId),
			Mockito.eq(0L), Mockito.isNull(), valuesArgumentCaptor.capture(),
			Mockito.any(ServiceContext.class)
		);

		Map<String, Serializable> values = valuesArgumentCaptor.getValue();

		Assert.assertEquals(_className, values.get("className"));
		Assert.assertEquals(_classPK, values.get("classPK"));
		Assert.assertEquals(
			_accountEntryId,
			values.get("r_accountToAccountValidatorResults_accountEntryId"));
		Assert.assertEquals("message", values.get("resultMessage"));
		Assert.assertEquals(
			AccountEntryValidatorConstants.RESULT_SUCCESS,
			values.get("resultStatus"));
	}

	private void _testAddAccountEntryValidatorResultWhenObjectDefinitionMissing()
		throws Exception {

		_resetMocks();

		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_ACCOUNT_VALIDATOR_RESULT", _companyId)
		).thenReturn(
			null
		);

		_accountEntryValidatorResultManager.addAccountEntryValidatorResult(
			_accountEntry, _className,
			AccountEntryValidatorResult.builder(
				_classPK
			).build());

		Mockito.verifyNoInteractions(_objectEntryLocalService);
	}

	private void _testGetValidAccountEntryValidatorResultWhenFailure()
		throws Exception {

		_resetMocks();

		_setUpObjectEntry(
			_mockObjectEntry(
				AccountEntryValidatorConstants.RESULT_FAILURE, "message",
				new Date()));

		Assert.assertNull(
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, _className, _classPK, 30));
	}

	private void _testGetValidAccountEntryValidatorResultWhenFreshManual()
		throws Exception {

		_resetMocks();

		_setUpObjectEntry(
			_mockObjectEntry(
				AccountEntryValidatorConstants.RESULT_MANUAL, "message",
				new Date()));

		AccountEntryValidatorResult accountEntryValidatorResult =
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, _className, _classPK, 30);

		Assert.assertNotNull(accountEntryValidatorResult);
		Assert.assertEquals(
			AccountEntryValidatorConstants.RESULT_MANUAL,
			accountEntryValidatorResult.getResultStatus());
	}

	private void _testGetValidAccountEntryValidatorResultWhenFreshSuccess()
		throws Exception {

		_resetMocks();

		_setUpObjectEntry(
			_mockObjectEntry(
				AccountEntryValidatorConstants.RESULT_SUCCESS, "message",
				new Date()));

		AccountEntryValidatorResult accountEntryValidatorResult =
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, _className, _classPK, 30);

		Assert.assertNotNull(accountEntryValidatorResult);
		Assert.assertEquals(_classPK, accountEntryValidatorResult.getKey());
		Assert.assertEquals(
			"message", accountEntryValidatorResult.getResultMessage());
		Assert.assertEquals(
			AccountEntryValidatorConstants.RESULT_SUCCESS,
			accountEntryValidatorResult.getResultStatus());
	}

	private void _testGetValidAccountEntryValidatorResultWhenNeverRun()
		throws Exception {

		_resetMocks();

		_setUpObjectEntry(null);

		Assert.assertNull(
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, _className, _classPK, 30));
	}

	private void _testGetValidAccountEntryValidatorResultWhenObjectDefinitionMissing()
		throws Exception {

		_resetMocks();

		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_ACCOUNT_VALIDATOR_RESULT", _companyId)
		).thenReturn(
			null
		);

		Assert.assertNull(
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, _className, _classPK, 30));

		Mockito.verifyNoInteractions(_objectEntryLocalService);
	}

	private void _testGetValidAccountEntryValidatorResultWhenStale()
		throws Exception {

		_resetMocks();

		_setUpObjectEntry(
			_mockObjectEntry(
				AccountEntryValidatorConstants.RESULT_SUCCESS, "message",
				new Date(System.currentTimeMillis() - (31 * Time.DAY))));

		Assert.assertNull(
			_accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					_accountEntry, _className, _classPK, 30));
	}

	private AccountEntry _accountEntry;
	private long _accountEntryId;
	private AccountEntryValidatorResultManager
		_accountEntryValidatorResultManager;
	private String _className;
	private String _classPK;
	private long _companyId;
	private JSONFactory _jsonFactory;
	private ObjectDefinitionLocalService _objectDefinitionLocalService;
	private ObjectEntryLocalService _objectEntryLocalService;
	private ObjectFieldLocalService _objectFieldLocalService;
	private long _userId;

}