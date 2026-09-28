/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.internal.batch.engine.exception.handler;

import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstance;
import com.liferay.portal.instances.constants.PortalInstancesPortletKeys;
import com.liferay.portal.kernel.exception.CompanyMaxUsersException;
import com.liferay.portal.kernel.exception.CompanyMxException;
import com.liferay.portal.kernel.exception.CompanyVirtualHostException;
import com.liferay.portal.kernel.exception.CompanyWebIdException;
import com.liferay.portal.kernel.exception.ContactNameException;
import com.liferay.portal.kernel.exception.RequiredCompanyException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.security.auth.FullNameValidator;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Luis Ortiz
 */
public class PortalInstanceBatchEngineImportTaskExceptionHandlerTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_portalInstanceBatchEngineImportTaskExceptionHandler,
			"_userNotificationEventLocalService",
			_userNotificationEventLocalService);

		Mockito.when(
			_batchEngineImportTask.getOperation()
		).thenReturn(
			BatchEngineTaskOperation.DELETE.name()
		);

		Mockito.when(
			_batchEngineImportTask.getUserId()
		).thenReturn(
			_USER_ID
		);
	}

	@Test
	public void testHandleIgnoresAnotherOperation() {
		Mockito.when(
			_batchEngineImportTask.getOperation()
		).thenReturn(
			BatchEngineTaskOperation.UPDATE.name()
		);

		_handle(new RequiredCompanyException(), RandomTestUtil.randomString());

		Mockito.verifyNoInteractions(_userNotificationEventLocalService);
	}

	@Test
	public void testHandleIgnoresItemsOfAnotherType() {
		_portalInstanceBatchEngineImportTaskExceptionHandler.handle(
			_batchEngineImportTask, null, new RequiredCompanyException(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Mockito.verifyNoInteractions(_userNotificationEventLocalService);
	}

	@Test
	public void testHandleMapsExceptions() throws Exception {
		_assertErrorMessageKey(
			"please-enter-a-valid-email-address",
			new UserEmailAddressException.MustNotBeNull());
		_assertErrorMessageKey(
			"please-enter-a-valid-first-middle-and-last-name",
			new ContactNameException.MustHaveValidFullName(
				Mockito.mock(FullNameValidator.class)));
		_assertErrorMessageKey(
			"please-enter-a-valid-first-name",
			new ContactNameException.MustHaveFirstName());
		_assertErrorMessageKey(
			"please-enter-a-valid-last-name",
			new ContactNameException.MustHaveLastName());
		_assertErrorMessageKey(
			"please-enter-a-valid-mail-domain", new CompanyMxException());
		_assertErrorMessageKey(
			"please-enter-a-valid-max-users", new CompanyMaxUsersException());
		_assertErrorMessageKey(
			"please-enter-a-valid-middle-name",
			new ContactNameException.MustHaveMiddleName());
		_assertErrorMessageKey(
			"please-enter-a-valid-password",
			new UserPasswordException.MustHaveMoreNumbers(
				RandomTestUtil.randomInt()));
		_assertErrorMessageKey(
			"please-enter-a-valid-screen-name",
			new UserScreenNameException.MustNotBeNull());
		_assertErrorMessageKey(
			"please-enter-a-valid-virtual-host",
			new CompanyVirtualHostException());
		_assertErrorMessageKey(
			"please-enter-a-valid-web-id", new CompanyWebIdException());
		_assertErrorMessageKey(
			"the-default-instance-cannot-be-deleted",
			new RequiredCompanyException());
	}

	@Test
	public void testHandleMapsUnknownExceptionToTheDefaultMessage()
		throws Exception {

		_assertErrorMessageKey("an-unexpected-error-occurred", new Exception());
	}

	@Test
	public void testHandleSendsUserNotificationEvent() throws Exception {
		String portalInstanceId = RandomTestUtil.randomString();

		_handle(new RequiredCompanyException(), portalInstanceId);

		JSONObject payloadJSONObject = _capturePayloadJSONObject();

		Assert.assertEquals(
			"DELETE", payloadJSONObject.getString("operationType"));
		Assert.assertEquals("FAILED", payloadJSONObject.getString("status"));
		Assert.assertEquals(
			portalInstanceId, payloadJSONObject.getString("portalInstanceId"));
	}

	@Test
	public void testHandleSendsUserNotificationEventForTheAddOperation()
		throws Exception {

		Mockito.when(
			_batchEngineImportTask.getOperation()
		).thenReturn(
			BatchEngineTaskOperation.CREATE.name()
		);

		String portalInstanceId = RandomTestUtil.randomString();

		_handle(new CompanyWebIdException(), portalInstanceId);

		JSONObject payloadJSONObject = _capturePayloadJSONObject();

		Assert.assertEquals(
			"ADD", payloadJSONObject.getString("operationType"));
		Assert.assertEquals("FAILED", payloadJSONObject.getString("status"));
		Assert.assertEquals(
			portalInstanceId, payloadJSONObject.getString("portalInstanceId"));
	}

	private void _assertErrorMessageKey(
			String errorMessageKey, Exception exception)
		throws Exception {

		Mockito.clearInvocations(_userNotificationEventLocalService);

		_handle(exception, RandomTestUtil.randomString());

		JSONObject payloadJSONObject = _capturePayloadJSONObject();

		Assert.assertEquals(
			errorMessageKey, payloadJSONObject.getString("errorMessageKey"));
	}

	private JSONObject _capturePayloadJSONObject() throws Exception {
		ArgumentCaptor<JSONObject> argumentCaptor = ArgumentCaptor.forClass(
			JSONObject.class);

		Mockito.verify(
			_userNotificationEventLocalService
		).sendUserNotificationEvents(
			Mockito.eq(_USER_ID),
			Mockito.eq(PortalInstancesPortletKeys.PORTAL_INSTANCES),
			Mockito.eq(UserNotificationDeliveryConstants.TYPE_WEBSITE),
			argumentCaptor.capture()
		);

		return argumentCaptor.getValue();
	}

	private void _handle(Exception exception, String portalInstanceId) {
		PortalInstance portalInstance = new PortalInstance();

		portalInstance.setPortalInstanceId(() -> portalInstanceId);

		_portalInstanceBatchEngineImportTaskExceptionHandler.handle(
			_batchEngineImportTask, null, exception, portalInstance,
			RandomTestUtil.randomString());
	}

	private static final long _USER_ID = RandomTestUtil.randomLong();

	private final BatchEngineImportTask _batchEngineImportTask = Mockito.mock(
		BatchEngineImportTask.class);
	private final PortalInstanceBatchEngineImportTaskExceptionHandler
		_portalInstanceBatchEngineImportTaskExceptionHandler =
			new PortalInstanceBatchEngineImportTaskExceptionHandler();
	private final UserNotificationEventLocalService
		_userNotificationEventLocalService = Mockito.mock(
			UserNotificationEventLocalService.class);

}