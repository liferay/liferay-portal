/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.storage.salesforce.internal.rest.manager.v1_0;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.rest.manager.http.ObjectEntryManagerHttp;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Pedro Leite
 */
public class SalesforceObjectEntryManagerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpAccountEntryLocalService();
		_setUpFilterFactory();
		_setUpInlineSQLHelper();
		_setUpModelResourcePermissionRegistryUtilMockedStatic();
		_setUpObjectEntryManagerHttp();
		_setUpObjectFieldLocalService();
		_setUpObjectScopeProviderRegistry();
		_setUpPermissionCheckerFactory();
	}

	@After
	public void tearDown() {
		_httpComponentsUtilMockedStatic.close();
		_modelResourcePermissionRegistryUtilMockedStatic.close();
	}

	@Test
	public void testGetObjectEntries() throws Exception {
		_testGetObjectEntriesWithAccountEntryRestriction();
		_testGetObjectEntriesWithSearch("test\\\\", "test\\");
		_testGetObjectEntriesWithSearch("test\\}", "test}");
	}

	private AccountEntry _mockAccountEntry(String externalReferenceCode) {
		AccountEntry accountEntry = Mockito.mock(AccountEntry.class);

		Mockito.when(
			accountEntry.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		return accountEntry;
	}

	private ObjectDefinition _mockObjectDefinition(
		boolean accountEntryRestricted) {

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			objectDefinition.isAccountEntryRestricted()
		).thenReturn(
			accountEntryRestricted
		);

		return objectDefinition;
	}

	private void _setUpAccountEntryLocalService() {
		ReflectionTestUtil.setFieldValue(
			_salesforceObjectEntryManagerImpl, "_accountEntryLocalService",
			_accountEntryLocalService);
	}

	private void _setUpFilterFactory() {
		ReflectionTestUtil.setFieldValue(
			_salesforceObjectEntryManagerImpl, "_filterFactory",
			Mockito.mock(FilterFactory.class));
	}

	private void _setUpInlineSQLHelper() {
		ReflectionTestUtil.setFieldValue(
			_salesforceObjectEntryManagerImpl, "_inlineSQLHelper",
			_inlineSQLHelper);
	}

	private void _setUpModelResourcePermissionRegistryUtilMockedStatic() {
		ModelResourcePermission<?> modelResourcePermission = Mockito.mock(
			ModelResourcePermission.class);

		Mockito.doReturn(
			Mockito.mock(PortletResourcePermission.class)
		).when(
			modelResourcePermission
		).getPortletResourcePermission();

		_modelResourcePermissionRegistryUtilMockedStatic.when(
			() ->
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					Mockito.any())
		).thenReturn(
			modelResourcePermission
		);
	}

	private void _setUpObjectEntryManagerHttp() {
		ReflectionTestUtil.setFieldValue(
			_salesforceObjectEntryManagerImpl, "_objectEntryManagerHttp",
			Mockito.mock(ObjectEntryManagerHttp.class));
	}

	private void _setUpObjectFieldLocalService() {
		ReflectionTestUtil.setFieldValue(
			_salesforceObjectEntryManagerImpl, "_objectFieldLocalService",
			_objectFieldLocalService);
	}

	private void _setUpObjectScopeProviderRegistry() {
		ObjectScopeProviderRegistry objectScopeProviderRegistry = Mockito.mock(
			ObjectScopeProviderRegistry.class);

		Mockito.when(
			objectScopeProviderRegistry.getObjectScopeProvider(Mockito.any())
		).thenReturn(
			Mockito.mock(ObjectScopeProvider.class)
		);

		ReflectionTestUtil.setFieldValue(
			_salesforceObjectEntryManagerImpl, "objectScopeProviderRegistry",
			objectScopeProviderRegistry);
	}

	private void _setUpPermissionCheckerFactory() {
		ReflectionTestUtil.setFieldValue(
			_salesforceObjectEntryManagerImpl, "permissionCheckerFactory",
			Mockito.mock(PermissionCheckerFactory.class));
	}

	private void _testGetObjectEntriesWithAccountEntryRestriction()
		throws Exception {

		ObjectDefinition objectDefinition = _mockObjectDefinition(true);

		Mockito.when(
			_inlineSQLHelper.isEnabled(Mockito.anyLong(), Mockito.anyLong())
		).thenReturn(
			true
		);

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_objectFieldLocalService.getObjectField(Mockito.anyLong())
		).thenReturn(
			objectField
		);

		List<AccountEntry> accountEntries = Arrays.asList(
			_mockAccountEntry("test's"), _mockAccountEntry("test\\"));

		Mockito.when(
			_accountEntryLocalService.getUserAccountEntries(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.any(),
				Mockito.any(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyInt())
		).thenReturn(
			accountEntries
		);

		_salesforceObjectEntryManagerImpl.getObjectEntries(
			RandomTestUtil.randomLong(), objectDefinition,
			RandomTestUtil.randomString(), null,
			Mockito.mock(DTOConverterContext.class), null, Pagination.of(1, 20),
			null, null);

		_httpComponentsUtilMockedStatic.verify(
			() -> HttpComponentsUtil.addParameter(
				"query", "q",
				StringBundler.concat(
					"SELECT FIELDS(ALL) FROM ",
					objectDefinition.getExternalReferenceCode(), " WHERE ",
					objectField.getExternalReferenceCode(),
					" IN ('test\\'s', 'test\\\\') LIMIT 20 OFFSET 0")));
	}

	private void _testGetObjectEntriesWithSearch(
			String expectedSearch, String search)
		throws Exception {

		ObjectDefinition objectDefinition = _mockObjectDefinition(false);

		_salesforceObjectEntryManagerImpl.getObjectEntries(
			RandomTestUtil.randomLong(), objectDefinition,
			RandomTestUtil.randomString(), null,
			Mockito.mock(DTOConverterContext.class), null, Pagination.of(1, 20),
			search, null);

		_httpComponentsUtilMockedStatic.verify(
			() -> HttpComponentsUtil.addParameter(
				"search", "q",
				StringBundler.concat(
					"FIND {`", expectedSearch, "`} IN ALL FIELDS RETURNING ",
					objectDefinition.getExternalReferenceCode(),
					"(FIELDS(ALL) LIMIT 20 OFFSET 0)")));
	}

	private final AccountEntryLocalService _accountEntryLocalService =
		Mockito.mock(AccountEntryLocalService.class);
	private final MockedStatic<HttpComponentsUtil>
		_httpComponentsUtilMockedStatic = Mockito.mockStatic(
			HttpComponentsUtil.class);
	private final InlineSQLHelper _inlineSQLHelper = Mockito.mock(
		InlineSQLHelper.class);
	private final MockedStatic<ModelResourcePermissionRegistryUtil>
		_modelResourcePermissionRegistryUtilMockedStatic = Mockito.mockStatic(
			ModelResourcePermissionRegistryUtil.class);
	private final ObjectFieldLocalService _objectFieldLocalService =
		Mockito.mock(ObjectFieldLocalService.class);
	private final SalesforceObjectEntryManagerImpl
		_salesforceObjectEntryManagerImpl =
			new SalesforceObjectEntryManagerImpl();

}