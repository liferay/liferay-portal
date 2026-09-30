/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.notifications;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.instances.constants.PortalInstancesNotificationConstants;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Luis Ortiz
 */
public class PortalInstancesUserNotificationHandlerTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_portalInstancesUserNotificationHandler, "_jsonFactory",
			new JSONFactoryImpl());

		Mockito.when(
			_serviceContext.translate(Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(0)
		);

		Mockito.when(
			_serviceContext.translate(
				Mockito.anyString(), Mockito.<Object>any())
		).thenAnswer(
			invocationOnMock -> _toTranslation(
				invocationOnMock.getArgument(0),
				invocationOnMock.getArgument(1))
		);
	}

	@Test
	public void testGetBodyForTheAddOperation() throws Exception {
		String portalInstanceId = RandomTestUtil.randomString();

		Assert.assertEquals(
			_toBodyHTML(
				"please-enter-a-valid-web-id",
				_toTranslation(
					"the-instance-x-could-not-be-created", portalInstanceId)),
			_getBody(
				_toPayloadJSONObject(
					"please-enter-a-valid-web-id",
					PortalInstancesNotificationConstants.OPERATION_TYPE_ADD,
					portalInstanceId, null,
					PortalInstancesNotificationConstants.STATUS_FAILED)));
		Assert.assertEquals(
			_toBodyHTML(
				_toTranslation(
					"the-instance-x-is-ready-to-use", portalInstanceId),
				_toTranslation("the-instance-x-was-created", portalInstanceId)),
			_getBody(
				_toPayloadJSONObject(
					null,
					PortalInstancesNotificationConstants.OPERATION_TYPE_ADD,
					portalInstanceId, null,
					PortalInstancesNotificationConstants.STATUS_SUCCESS)));
	}

	@Test
	public void testGetBodyForTheDeleteOperation() throws Exception {
		String portalInstanceId = RandomTestUtil.randomString();

		Assert.assertEquals(
			_toBodyHTML(
				"the-default-instance-cannot-be-deleted",
				_toTranslation(
					"the-instance-x-could-not-be-deleted", portalInstanceId)),
			_getBody(
				_toPayloadJSONObject(
					"the-default-instance-cannot-be-deleted",
					PortalInstancesNotificationConstants.OPERATION_TYPE_DELETE,
					portalInstanceId, null,
					PortalInstancesNotificationConstants.STATUS_FAILED)));
		Assert.assertEquals(
			_toBodyHTML(
				_toTranslation(
					"the-instance-x-is-no-longer-available", portalInstanceId),
				_toTranslation("the-instance-x-was-deleted", portalInstanceId)),
			_getBody(
				_toPayloadJSONObject(
					null,
					PortalInstancesNotificationConstants.OPERATION_TYPE_DELETE,
					portalInstanceId, null,
					PortalInstancesNotificationConstants.STATUS_SUCCESS)));
	}

	@Test
	public void testGetBodyForTheExportOperation() throws Exception {
		String portalInstanceId = RandomTestUtil.randomString();
		String schemaName = RandomTestUtil.randomString();

		Assert.assertEquals(
			_toBodyHTML(
				_toTranslation(
					"the-exported-schema-x-already-exists", schemaName),
				_toTranslation(
					"the-instance-x-could-not-be-exported", portalInstanceId)),
			_getBody(
				_toPayloadJSONObject(
					"the-exported-schema-x-already-exists",
					PortalInstancesNotificationConstants.OPERATION_TYPE_EXPORT,
					portalInstanceId, schemaName,
					PortalInstancesNotificationConstants.STATUS_FAILED)));
		Assert.assertEquals(
			_toBodyHTML(
				_toTranslation(
					"the-instance-was-exported-to-the-schema-x", schemaName),
				_toTranslation(
					"the-instance-x-was-exported", portalInstanceId)),
			_getBody(
				_toPayloadJSONObject(
					null,
					PortalInstancesNotificationConstants.OPERATION_TYPE_EXPORT,
					portalInstanceId, schemaName,
					PortalInstancesNotificationConstants.STATUS_SUCCESS)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetBodyWithUnknownOperationType() throws Exception {
		_getBody(
			_toPayloadJSONObject(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				PortalInstancesNotificationConstants.STATUS_SUCCESS));
	}

	@Test
	public void testGetTitleForTheAddOperation() throws Exception {
		String portalInstanceId = RandomTestUtil.randomString();

		Assert.assertEquals(
			_toTranslation(
				"the-instance-x-could-not-be-created", portalInstanceId),
			_getTitle(
				_toPayloadJSONObject(
					"please-enter-a-valid-web-id",
					PortalInstancesNotificationConstants.OPERATION_TYPE_ADD,
					portalInstanceId, null,
					PortalInstancesNotificationConstants.STATUS_FAILED)));
		Assert.assertEquals(
			_toTranslation("the-instance-x-was-created", portalInstanceId),
			_getTitle(
				_toPayloadJSONObject(
					null,
					PortalInstancesNotificationConstants.OPERATION_TYPE_ADD,
					portalInstanceId, null,
					PortalInstancesNotificationConstants.STATUS_SUCCESS)));
	}

	@Test
	public void testGetTitleForTheDeleteOperation() throws Exception {
		String portalInstanceId = RandomTestUtil.randomString();

		Assert.assertEquals(
			_toTranslation(
				"the-instance-x-could-not-be-deleted", portalInstanceId),
			_getTitle(
				_toPayloadJSONObject(
					"the-default-instance-cannot-be-deleted",
					PortalInstancesNotificationConstants.OPERATION_TYPE_DELETE,
					portalInstanceId, null,
					PortalInstancesNotificationConstants.STATUS_FAILED)));
		Assert.assertEquals(
			_toTranslation("the-instance-x-was-deleted", portalInstanceId),
			_getTitle(
				_toPayloadJSONObject(
					null,
					PortalInstancesNotificationConstants.OPERATION_TYPE_DELETE,
					portalInstanceId, null,
					PortalInstancesNotificationConstants.STATUS_SUCCESS)));
	}

	@Test
	public void testGetTitleForTheExportOperation() throws Exception {
		String portalInstanceId = RandomTestUtil.randomString();

		Assert.assertEquals(
			_toTranslation(
				"the-instance-x-could-not-be-exported", portalInstanceId),
			_getTitle(
				_toPayloadJSONObject(
					"the-exported-schema-x-already-exists",
					PortalInstancesNotificationConstants.OPERATION_TYPE_EXPORT,
					portalInstanceId, null,
					PortalInstancesNotificationConstants.STATUS_FAILED)));
		Assert.assertEquals(
			_toTranslation("the-instance-x-was-exported", portalInstanceId),
			_getTitle(
				_toPayloadJSONObject(
					null,
					PortalInstancesNotificationConstants.OPERATION_TYPE_EXPORT,
					portalInstanceId, null,
					PortalInstancesNotificationConstants.STATUS_SUCCESS)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetTitleWithUnknownOperationType() throws Exception {
		_getTitle(
			_toPayloadJSONObject(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				PortalInstancesNotificationConstants.STATUS_SUCCESS));
	}

	private String _getBody(JSONObject payloadJSONObject) throws Exception {
		return _portalInstancesUserNotificationHandler.getBody(
			_toUserNotificationEvent(payloadJSONObject), _serviceContext);
	}

	private String _getTitle(JSONObject payloadJSONObject) throws Exception {
		return _portalInstancesUserNotificationHandler.getTitle(
			_toUserNotificationEvent(payloadJSONObject), _serviceContext);
	}

	private String _toBodyHTML(String body, String title) {
		return StringBundler.concat(
			"<h2 class=\"title\">", title, "</h2><div class=\"body\">", body,
			"</div>");
	}

	private JSONObject _toPayloadJSONObject(
		String errorMessageKey, String operationType, String portalInstanceId,
		String schemaName, String status) {

		return JSONUtil.put(
			"errorMessageKey", errorMessageKey
		).put(
			"operationType", operationType
		).put(
			"portalInstanceId", portalInstanceId
		).put(
			"schemaName", schemaName
		).put(
			"status", status
		);
	}

	private String _toTranslation(String key, String argument) {
		return StringBundler.concat(key, StringPool.COLON, argument);
	}

	private UserNotificationEvent _toUserNotificationEvent(
		JSONObject payloadJSONObject) {

		UserNotificationEvent userNotificationEvent = Mockito.mock(
			UserNotificationEvent.class);

		Mockito.when(
			userNotificationEvent.getPayload()
		).thenReturn(
			payloadJSONObject.toString()
		);

		return userNotificationEvent;
	}

	private final PortalInstancesUserNotificationHandler
		_portalInstancesUserNotificationHandler =
			new PortalInstancesUserNotificationHandler();
	private final ServiceContext _serviceContext = Mockito.mock(
		ServiceContext.class);

}