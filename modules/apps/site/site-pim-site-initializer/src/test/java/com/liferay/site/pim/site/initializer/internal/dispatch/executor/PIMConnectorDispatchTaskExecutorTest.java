/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.dispatch.executor;

import com.liferay.dispatch.executor.DispatchTaskExecutorOutput;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.exception.PIMConnectorException;

import java.io.Serializable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class PIMConnectorDispatchTaskExecutorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_serviceTrackerFactoryMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_dispatchTrigger.getDispatchTaskSettingsUnicodeProperties()
		).thenReturn(
			UnicodePropertiesBuilder.put(
				"pimConnectorObjectEntryId", _OBJECT_ENTRY_ID
			).build()
		);

		Mockito.when(
			_dispatchTrigger.getUserId()
		).thenReturn(
			_USER_ID
		);

		Mockito.when(
			_language.get(Mockito.eq(LocaleUtil.US), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> "Localized " + invocationOnMock.getArgument(1)
		);

		Mockito.when(
			_objectEntryLocalService.getObjectEntry(_OBJECT_ENTRY_ID)
		).thenReturn(
			_objectEntry
		);

		Mockito.when(
			_permissionCheckerFactory.create(_user)
		).thenReturn(
			_permissionChecker
		);

		Mockito.when(
			_pimConnectorRegistry.getPIMConnector(_KEY)
		).thenReturn(
			_pimConnector
		);

		ReflectionTestUtil.setFieldValue(
			_pimConnectorDispatchTaskExecutor, "_language", _language);
		ReflectionTestUtil.setFieldValue(
			_pimConnectorDispatchTaskExecutor, "_objectEntryLocalService",
			_objectEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimConnectorDispatchTaskExecutor, "_permissionCheckerFactory",
			_permissionCheckerFactory);
		ReflectionTestUtil.setFieldValue(
			_pimConnectorDispatchTaskExecutor, "_pimConnectorRegistry",
			_pimConnectorRegistry);
		ReflectionTestUtil.setFieldValue(
			_pimConnectorDispatchTaskExecutor, "_userLocalService",
			_userLocalService);

		Mockito.when(
			_user.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			_user.getUserId()
		).thenReturn(
			_USER_ID
		);

		Mockito.when(
			_userLocalService.getUser(_USER_ID)
		).thenReturn(
			_user
		);
	}

	@Test
	public void testDoExecute() throws Exception {
		_testDoExecute();
		_testDoExecuteWithInactiveConnector();
		_testDoExecuteWithPIMConnectorException();
		_testDoExecuteWithPIMConnectorExceptionOutput();
		_testDoExecuteWithoutPIMConnector();
	}

	@Test
	public void testIsClusterModeSingle() {
		Assert.assertTrue(
			_pimConnectorDispatchTaskExecutor.isClusterModeSingle());
	}

	@Test
	public void testIsHiddenInUI() {
		Assert.assertTrue(_pimConnectorDispatchTaskExecutor.isHiddenInUI());
	}

	private void _mockObjectEntryValues(boolean active) {
		Mockito.when(
			_objectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"active", active
			).put(
				"key", _KEY
			).build()
		);
	}

	private void _testDoExecute() throws Exception {
		_mockObjectEntryValues(true);

		Mockito.when(
			_pimConnector.execute(_objectEntry)
		).thenAnswer(
			invocationOnMock -> {
				Assert.assertEquals(
					_permissionChecker,
					PermissionThreadLocal.getPermissionChecker());
				Assert.assertEquals(_USER_ID, PrincipalThreadLocal.getUserId());

				return "[]";
			}
		);

		String originalName = PrincipalThreadLocal.getName();
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		DispatchTaskExecutorOutput dispatchTaskExecutorOutput =
			new DispatchTaskExecutorOutput();

		_pimConnectorDispatchTaskExecutor.doExecute(
			_dispatchTrigger, dispatchTaskExecutorOutput);

		Assert.assertNull(dispatchTaskExecutorOutput.getError());
		Assert.assertEquals("[]", dispatchTaskExecutorOutput.getOutput());

		Assert.assertEquals(originalName, PrincipalThreadLocal.getName());
		Assert.assertEquals(
			originalPermissionChecker,
			PermissionThreadLocal.getPermissionChecker());
	}

	private void _testDoExecuteWithInactiveConnector() throws Exception {
		_mockObjectEntryValues(false);

		Mockito.clearInvocations(_pimConnector);

		DispatchTaskExecutorOutput dispatchTaskExecutorOutput =
			new DispatchTaskExecutorOutput();

		_pimConnectorDispatchTaskExecutor.doExecute(
			_dispatchTrigger, dispatchTaskExecutorOutput);

		Assert.assertEquals(
			"PIM connector is inactive",
			dispatchTaskExecutorOutput.getOutput());

		Mockito.verifyNoInteractions(_pimConnector);
	}

	private void _testDoExecuteWithPIMConnectorException() throws Exception {
		_mockObjectEntryValues(true);

		Mockito.doThrow(
			new PIMConnectorException("a-required-channel-field-is-not-mapped")
		).when(
			_pimConnector
		).execute(
			_objectEntry
		);

		DispatchTaskExecutorOutput dispatchTaskExecutorOutput =
			new DispatchTaskExecutorOutput();

		try {
			_pimConnectorDispatchTaskExecutor.doExecute(
				_dispatchTrigger, dispatchTaskExecutorOutput);

			Assert.fail();
		}
		catch (PIMConnectorException pimConnectorException) {
			Assert.assertEquals(
				"a-required-channel-field-is-not-mapped",
				pimConnectorException.getMessage());
		}

		Assert.assertEquals(
			"Localized a-required-channel-field-is-not-mapped",
			dispatchTaskExecutorOutput.getError());
		Assert.assertNull(dispatchTaskExecutorOutput.getOutput());
	}

	private void _testDoExecuteWithPIMConnectorExceptionOutput()
		throws Exception {

		_mockObjectEntryValues(true);

		Mockito.doThrow(
			new PIMConnectorException(
				"unable-to-connect-to-the-remote-instance", "{\"log\": []}",
				null)
		).when(
			_pimConnector
		).execute(
			_objectEntry
		);

		DispatchTaskExecutorOutput dispatchTaskExecutorOutput =
			new DispatchTaskExecutorOutput();

		try {
			_pimConnectorDispatchTaskExecutor.doExecute(
				_dispatchTrigger, dispatchTaskExecutorOutput);

			Assert.fail();
		}
		catch (PIMConnectorException pimConnectorException) {
			Assert.assertEquals(
				"unable-to-connect-to-the-remote-instance",
				pimConnectorException.getMessage());
		}

		Assert.assertEquals(
			"Localized unable-to-connect-to-the-remote-instance",
			dispatchTaskExecutorOutput.getError());
		Assert.assertEquals(
			"{\"log\": []}", dispatchTaskExecutorOutput.getOutput());
	}

	private void _testDoExecuteWithoutPIMConnector() throws Exception {
		_mockObjectEntryValues(true);

		Mockito.when(
			_pimConnectorRegistry.getPIMConnector(_KEY)
		).thenReturn(
			null
		);

		DispatchTaskExecutorOutput dispatchTaskExecutorOutput =
			new DispatchTaskExecutorOutput();

		try {
			_pimConnectorDispatchTaskExecutor.doExecute(
				_dispatchTrigger, dispatchTaskExecutorOutput);

			Assert.fail();
		}
		catch (PIMConnectorException pimConnectorException) {
			Assert.assertEquals(
				"unable-to-get-a-pim-connector-with-the-given-key",
				pimConnectorException.getMessage());
		}

		Assert.assertEquals(
			"Localized unable-to-get-a-pim-connector-with-the-given-key",
			dispatchTaskExecutorOutput.getError());
	}

	private static final String _KEY = RandomTestUtil.randomString();

	private static final long _OBJECT_ENTRY_ID = RandomTestUtil.randomLong();

	private static final long _USER_ID = RandomTestUtil.randomLong();

	private static final MockedStatic<ServiceTrackerFactory>
		_serviceTrackerFactoryMockedStatic = Mockito.mockStatic(
			ServiceTrackerFactory.class);

	private final DispatchTrigger _dispatchTrigger = Mockito.mock(
		DispatchTrigger.class);
	private final Language _language = Mockito.mock(Language.class);
	private final ObjectEntry _objectEntry = Mockito.mock(ObjectEntry.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final PermissionCheckerFactory _permissionCheckerFactory =
		Mockito.mock(PermissionCheckerFactory.class);
	private final PIMConnector _pimConnector = Mockito.mock(PIMConnector.class);
	private final PIMConnectorDispatchTaskExecutor
		_pimConnectorDispatchTaskExecutor =
			new PIMConnectorDispatchTaskExecutor();
	private final PIMConnectorRegistry _pimConnectorRegistry = Mockito.mock(
		PIMConnectorRegistry.class);
	private final User _user = Mockito.mock(User.class);
	private final UserLocalService _userLocalService = Mockito.mock(
		UserLocalService.class);

}