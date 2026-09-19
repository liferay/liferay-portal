/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.push.notifications.sender.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.push.notifications.exception.PushNotificationsException;
import com.liferay.push.notifications.service.PushNotificationsDeviceLocalService;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.mockserver.verify.VerificationTimes;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class FirebasePushNotificationsSenderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_clientAndServer = ClientAndServer.startClientAndServer();
	}

	@After
	public void tearDown() throws Exception {
		_clientAndServer.stop();

		ConfigurationTestUtil.deleteConfiguration(_PID);
	}

	@Test
	public void testSendEmptyNotificationWithOneDestination() throws Exception {
		_saveConfiguration();

		String accessToken = _mockAccessTokenRequest(true);

		_mockSendNotificationRequest(accessToken, true);

		String destinationToken = RandomTestUtil.randomString();

		_pushNotificationsDeviceLocalService.sendPushNotification(
			_PLATFORM, Arrays.asList(destinationToken),
			JSONFactoryUtil.createJSONObject());

		_verifyAccessTokenRequest();
		_verifyGroupRequestInteractions(accessToken, false, false);
		_verifySendNotificationRequest(
			accessToken,
			_getExpectedEmptyNotificationJSONObject(destinationToken));
	}

	@Test
	public void testSendEmptyNotificationWithSeveralDestinations()
		throws Exception {

		_saveConfiguration();

		String accessToken = _mockAccessTokenRequest(true);
		String groupId = RandomTestUtil.randomString();

		_mockGroupRequest(accessToken, true, groupId, true);

		_mockSendNotificationRequest(accessToken, true);

		List<String> destinationTokens = Arrays.asList(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		_pushNotificationsDeviceLocalService.sendPushNotification(
			_PLATFORM, destinationTokens, JSONFactoryUtil.createJSONObject());

		_verifyAccessTokenRequest();

		_verifyGroupRequestInteractions(accessToken, true, true);

		_verifySendNotificationRequest(
			accessToken, _getExpectedEmptyNotificationJSONObject(groupId));

		_verifyRemoveGroupRequest(
			accessToken, destinationTokens, groupId,
			_verifyCreateGroupRequest(accessToken, destinationTokens));
	}

	@Test
	public void testSendErrorWhenGettingToken() throws Exception {
		_saveConfiguration();

		AssertUtils.assertFailure(
			PushNotificationsException.class, "Unable to get access token",
			() -> _pushNotificationsDeviceLocalService.sendPushNotification(
				_PLATFORM, Arrays.asList(RandomTestUtil.randomString()),
				_getRandomNotificationJSONObject()));

		_verifyAccessTokenRequest();
		_verifyGroupRequestInteractions(
			_mockAccessTokenRequest(false), false, false);
	}

	@Test
	public void testSendWithOneDestination() throws Exception {
		_saveConfiguration();

		String accessToken = _mockAccessTokenRequest(true);

		_mockSendNotificationRequest(accessToken, true);

		String destinationToken = RandomTestUtil.randomString();

		JSONObject jsonObject = _getRandomNotificationJSONObject();

		_pushNotificationsDeviceLocalService.sendPushNotification(
			_PLATFORM, Arrays.asList(destinationToken), jsonObject);

		_verifyAccessTokenRequest();
		_verifyGroupRequestInteractions(accessToken, false, false);
		_verifySendNotificationRequest(
			accessToken,
			_getExpectedNotificationJSONObject(destinationToken, jsonObject));
	}

	@Test
	public void testSendWithOneDestinationAndErrorWhenSendingNotification()
		throws Exception {

		_saveConfiguration();

		String accessToken = _mockAccessTokenRequest(true);

		_mockSendNotificationRequest(accessToken, false);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.push.notifications.sender.firebase.internal." +
					"FirebasePushNotificationsSender",
				LoggerTestUtil.ERROR)) {

			String destinationToken = RandomTestUtil.randomString();

			AssertUtils.assertFailure(
				PushNotificationsException.class,
				"Unable to send push notification",
				() -> _pushNotificationsDeviceLocalService.sendPushNotification(
					_PLATFORM, Arrays.asList(destinationToken),
					_getExpectedNotificationJSONObject(
						destinationToken, _getRandomNotificationJSONObject())));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Unable to send notification with token ", destinationToken,
					" and reason REASON"),
				logEntry.getMessage());
		}

		_verifyAccessTokenRequest();
		_verifyGroupRequestInteractions(accessToken, false, false);
	}

	@Test
	public void testSendWithSeveralDestinations() throws Exception {
		_saveConfiguration();

		String accessToken = _mockAccessTokenRequest(true);

		String groupId = RandomTestUtil.randomString();

		_mockGroupRequest(accessToken, true, groupId, true);

		_mockSendNotificationRequest(accessToken, true);

		List<String> destinationTokens = Arrays.asList(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		JSONObject jsonObject = _getRandomNotificationJSONObject();

		_pushNotificationsDeviceLocalService.sendPushNotification(
			_PLATFORM, destinationTokens, jsonObject);

		_verifyAccessTokenRequest();

		_verifyGroupRequestInteractions(accessToken, true, true);

		_verifySendNotificationRequest(
			accessToken,
			_getExpectedNotificationJSONObject(groupId, jsonObject));

		_verifyRemoveGroupRequest(
			accessToken, destinationTokens, groupId,
			_verifyCreateGroupRequest(accessToken, destinationTokens));
	}

	@Test
	public void testSendWithSeveralDestinationsAndErrorWhenCreatingGroup()
		throws Exception {

		_saveConfiguration();

		String accessToken = _mockAccessTokenRequest(true);

		_mockGroupRequest(
			accessToken, false, RandomTestUtil.randomString(), true);

		List<String> destinationTokens = Arrays.asList(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		AssertUtils.assertFailure(
			PushNotificationsException.class,
			"Unable to create notification group",
			() -> _pushNotificationsDeviceLocalService.sendPushNotification(
				_PLATFORM, destinationTokens,
				_getRandomNotificationJSONObject()));

		_verifyAccessTokenRequest();
		_verifyCreateGroupRequest(accessToken, destinationTokens);
		_verifyGroupRequestInteractions(accessToken, true, false);
	}

	@Test
	public void testSendWithSeveralDestinationsAndErrorWhenRemovingGroup()
		throws Exception {

		_saveConfiguration();

		String accessToken = _mockAccessTokenRequest(true);

		String groupId = RandomTestUtil.randomString();

		_mockGroupRequest(accessToken, true, groupId, false);

		_mockSendNotificationRequest(accessToken, true);

		List<String> destinationTokens = Arrays.asList(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		JSONObject jsonObject = _getRandomNotificationJSONObject();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.push.notifications.sender.firebase.internal." +
					"FirebasePushNotificationsSender",
				LoggerTestUtil.ERROR)) {

			_pushNotificationsDeviceLocalService.sendPushNotification(
				_PLATFORM, destinationTokens, jsonObject);

			_verifyAccessTokenRequest();

			_verifySendNotificationRequest(
				accessToken,
				_getExpectedNotificationJSONObject(groupId, jsonObject));

			String groupName = _verifyCreateGroupRequest(
				accessToken, destinationTokens);

			_verifyRemoveGroupRequest(
				accessToken, destinationTokens, groupId, groupName);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Unable to remove notification group with ID ", groupId,
					" and name ", groupName),
				logEntry.getMessage());
		}
	}

	@Test
	public void testSendWithSeveralDestinationsAndErrorWhenSendingNotification()
		throws Exception {

		_saveConfiguration();

		String accessToken = _mockAccessTokenRequest(true);

		String groupId = RandomTestUtil.randomString();

		_mockGroupRequest(accessToken, true, groupId, false);

		_mockSendNotificationRequest(accessToken, false);

		List<String> destinationTokens = Arrays.asList(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		JSONObject jsonObject = _getRandomNotificationJSONObject();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.push.notifications.sender.firebase.internal." +
					"FirebasePushNotificationsSender",
				LoggerTestUtil.ERROR)) {

			AssertUtils.assertFailure(
				PushNotificationsException.class,
				"Unable to send push notification",
				() -> _pushNotificationsDeviceLocalService.sendPushNotification(
					_PLATFORM, destinationTokens, jsonObject));
			_verifyAccessTokenRequest();

			String groupName = _verifyCreateGroupRequest(
				accessToken, destinationTokens);

			_verifySendNotificationRequest(
				accessToken,
				_getExpectedNotificationJSONObject(groupId, jsonObject));

			_verifyRemoveGroupRequest(
				accessToken, destinationTokens, groupId, groupName);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Unable to send notification with token ", groupId,
					" and reason REASON"),
				logEntry.getMessage());

			logEntry = logEntries.get(1);

			Assert.assertEquals(
				StringBundler.concat(
					"Unable to remove notification group with ID ", groupId,
					" and name ", groupName),
				logEntry.getMessage());
		}
	}

	@Test
	public void testSendWithoutConfiguration() {
		AssertUtils.assertFailure(
			PushNotificationsException.class,
			"Firebase push notifications sender is not configured properly",
			() -> _pushNotificationsDeviceLocalService.sendPushNotification(
				_PLATFORM, Arrays.asList(RandomTestUtil.randomString()),
				JSONFactoryUtil.createJSONObject()));
	}

	private int _getCode(boolean success) {
		if (success) {
			return 200;
		}

		return 401;
	}

	private JSONObject _getExpectedEmptyNotificationJSONObject(
		String destinationToken) {

		return JSONUtil.put(
			"message",
			JSONUtil.put(
				"android",
				JSONUtil.put("notification", JSONFactoryUtil.createJSONObject())
			).put(
				"data",
				JSONUtil.put(
					"payload",
					JSONFactoryUtil.createJSONObject(
					).toString())
			).put(
				"token", destinationToken
			));
	}

	private JSONObject _getExpectedNotificationJSONObject(
		String destinationToken, JSONObject jsonObject) {

		return JSONUtil.put(
			"message",
			JSONUtil.put(
				"android",
				JSONUtil.put(
					"notification",
					JSONUtil.put(
						"body", jsonObject.getString("body")
					).put(
						"body_loc_args",
						jsonObject.getJSONArray("bodyLocalizedArguments")
					).put(
						"body_loc_key", jsonObject.getString("bodyLocalizedKey")
					).put(
						"notification_count", jsonObject.getInt("badge")
					).put(
						"sound", jsonObject.getString("sound")
					).put(
						"title", jsonObject.getString("title")
					).put(
						"title_loc_args",
						jsonObject.getJSONArray("titleLocalizedArguments")
					).put(
						"title_loc_key",
						jsonObject.getString("titleLocalizedKey")
					))
			).put(
				"data",
				JSONUtil.put(
					"payload",
					JSONUtil.put(
						"customField1", jsonObject.getString("customField1")
					).put(
						"customField2", jsonObject.getString("customField2")
					).put(
						"customField3", jsonObject.getInt("customField3")
					).put(
						"customFieldN", jsonObject.getString("customFieldN")
					).put(
						"title", jsonObject.getString("title")
					).put(
						"titleLocalizedArguments",
						jsonObject.getJSONArray("titleLocalizedArguments")
					).put(
						"titleLocalizedKey",
						jsonObject.getString("titleLocalizedKey")
					).toString())
			).put(
				"token", destinationToken
			));
	}

	private JSONObject _getRandomNotificationJSONObject() {
		return JSONUtil.put(
			"badge", RandomTestUtil.randomInt()
		).put(
			"body", RandomTestUtil.randomString()
		).put(
			"bodyLocalizedArguments",
			JSONFactoryUtil.createJSONArray(
				Arrays.asList(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString()))
		).put(
			"bodyLocalizedKey", RandomTestUtil.randomString()
		).put(
			"customField1", RandomTestUtil.randomString()
		).put(
			"customField2", RandomTestUtil.randomString()
		).put(
			"customField3", RandomTestUtil.randomInt()
		).put(
			"customFieldN", RandomTestUtil.randomString()
		).put(
			"silent", true
		).put(
			"sound", RandomTestUtil.randomString()
		).put(
			"title", RandomTestUtil.randomString()
		).put(
			"titleLocalizedArguments",
			JSONFactoryUtil.createJSONArray(
				Arrays.asList(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString()))
		).put(
			"titleLocalizedKey", RandomTestUtil.randomString()
		);
	}

	private String _mockAccessTokenRequest(boolean success) {
		String accessToken = RandomTestUtil.randomString();

		_clientAndServer.when(
			HttpRequest.request(
			).withMethod(
				"POST"
			).withPath(
				"/token"
			)
		).respond(
			HttpResponse.response(
			).withBody(
				JSONUtil.put(
					"access_token", accessToken
				).put(
					"expires_in", RandomTestUtil.randomInt()
				).put(
					"scope", RandomTestUtil.randomString()
				).put(
					"token_type", "Bearer"
				).toString()
			).withStatusCode(
				_getCode(success)
			)
		);

		return accessToken;
	}

	private void _mockGroupRequest(
		String accessToken, boolean createGroupSuccess, String groupId,
		boolean removeGroupSuccess) {

		int[] invocations = {0};

		_clientAndServer.when(
			HttpRequest.request(
			).withHeader(
				"Authorization", "Bearer " + accessToken
			).withHeader(
				"Content-type", "application/json"
			).withHeader(
				"access_token_auth", "true"
			).withHeader(
				"project_id", _PROJECT_NUMBER
			).withMethod(
				"POST"
			).withPath(
				"/fcm/notification"
			)
		).respond(
			httpRequest -> {
				invocations[0]++;

				if (((invocations[0] - 1) % 2) == 0) {
					if (createGroupSuccess) {
						return HttpResponse.response(
						).withBody(
							JSONUtil.put(
								"notification_key", groupId
							).toString()
						).withContentType(
							MediaType.APPLICATION_JSON
						).withStatusCode(
							_getCode(true)
						);
					}

					return HttpResponse.response(
					).withStatusCode(
						_getCode(false)
					);
				}

				return HttpResponse.response(
				).withStatusCode(
					_getCode(removeGroupSuccess)
				);
			}
		);
	}

	private void _mockSendNotificationRequest(
		String accessToken, boolean success) {

		_clientAndServer.when(
			HttpRequest.request(
			).withHeader(
				"Authorization", "Bearer " + accessToken
			).withHeader(
				"Content-type", "application/json"
			).withMethod(
				"POST"
			).withPath(
				"/v1/projects/" + _PROJECT_ID + "/messages:send"
			)
		).respond(
			HttpResponse.response(
			).withBody(
				"REASON"
			).withStatusCode(
				_getCode(success)
			)
		);
	}

	private void _saveConfiguration() throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			_PID,
			HashMapDictionaryBuilder.<String, Object>put(
				"firebaseCloudMessagingURL",
				"http://localhost:" + _clientAndServer.getPort()
			).put(
				"projectNumber", _PROJECT_NUMBER
			).put(
				"serviceAccountKey",
				StringUtil.replace(
					new String(
						FileUtil.getBytes(
							getClass(),
							"dependencies/service-account-key.json")),
					new String[] {"${URL}", "${PROJECT_ID}"},
					new String[] {
						"http://localhost:" + _clientAndServer.getPort(),
						_PROJECT_ID
					})
			).build());
	}

	private void _verifyAccessTokenRequest() {
		_clientAndServer.verify(
			HttpRequest.request(
			).withMethod(
				"POST"
			).withPath(
				"/token"
			),
			VerificationTimes.once());
	}

	private String _verifyCreateGroupRequest(
			String accessToken, List<String> destinationTokens)
		throws Exception {

		HttpRequest[] httpRequests = _clientAndServer.retrieveRecordedRequests(
			HttpRequest.request(
			).withMethod(
				"POST"
			).withPath(
				"/fcm/notification"
			));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			httpRequests[0].getBodyAsString());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"operation", "create"
			).put(
				"registration_ids", destinationTokens
			).toString(),
			jsonObject.toString(), JSONCompareMode.STRICT_ORDER);

		_clientAndServer.verify(
			HttpRequest.request(
			).withBody(
				jsonObject.toString()
			).withHeader(
				"Authorization", "Bearer " + accessToken
			).withHeader(
				"Content-Type", "application/json"
			).withHeader(
				"access_token_auth", "true"
			).withHeader(
				"project_id", _PROJECT_NUMBER
			).withMethod(
				"POST"
			).withPath(
				"/fcm/notification"
			),
			VerificationTimes.once());

		return jsonObject.getString("notification_key_name");
	}

	private void _verifyGroupRequestInteractions(
		String accessToken, boolean createGroupInteraction,
		boolean removeGroupInteraction) {

		int times = 0;

		if (createGroupInteraction) {
			times++;
		}

		if (removeGroupInteraction) {
			times++;
		}

		_clientAndServer.verify(
			HttpRequest.request(
			).withHeader(
				"Authorization", "Bearer " + accessToken
			).withHeader(
				"Content-Type", "application/json"
			).withHeader(
				"access_token_auth", "true"
			).withHeader(
				"project_id", _PROJECT_NUMBER
			).withMethod(
				"POST"
			).withPath(
				"/fcm/notification"
			),
			VerificationTimes.exactly(times));
	}

	private void _verifyRemoveGroupRequest(
		String accessToken, List<String> destinationTokens, String groupId,
		String groupName) {

		_clientAndServer.verify(
			HttpRequest.request(
			).withBody(
				JSONUtil.put(
					"notification_key", groupId
				).put(
					"notification_key_name", groupName
				).put(
					"operation", "remove"
				).put(
					"registration_ids", destinationTokens
				).toString()
			).withHeader(
				"Authorization", "Bearer " + accessToken
			).withHeader(
				"Content-type", "application/json"
			).withHeader(
				"access_token_auth", "true"
			).withHeader(
				"project_id", _PROJECT_NUMBER
			).withMethod(
				"POST"
			).withPath(
				"/fcm/notification"
			),
			VerificationTimes.once());
	}

	private void _verifySendNotificationRequest(
		String accessToken, JSONObject notificationJSONObject) {

		_clientAndServer.verify(
			HttpRequest.request(
			).withBody(
				notificationJSONObject.toString()
			).withHeader(
				"Authorization", "Bearer " + accessToken
			).withHeader(
				"Content-Type", "application/json"
			).withMethod(
				"POST"
			).withPath(
				"/v1/projects/" + _PROJECT_ID + "/messages:send"
			),
			VerificationTimes.once());
	}

	private static final String _PID =
		"com.liferay.push.notifications.sender.firebase.internal." +
			"configuration.FirebasePushNotificationsSenderConfiguration";

	private static final String _PLATFORM = "firebase";

	private static final String _PROJECT_ID = RandomTestUtil.randomString();

	private static final String _PROJECT_NUMBER = RandomTestUtil.randomString();

	private ClientAndServer _clientAndServer;

	@Inject
	private PushNotificationsDeviceLocalService
		_pushNotificationsDeviceLocalService;

}