/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.push.notifications.sender.apple.internal;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.push.notifications.constants.PushNotificationsConstants;
import com.liferay.push.notifications.constants.PushNotificationsDestinationNames;
import com.liferay.push.notifications.exception.PushNotificationsException;
import com.liferay.push.notifications.sender.PushNotificationsSender;
import com.liferay.push.notifications.sender.apple.internal.configuration.ApplePushNotificationsSenderConfiguration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.time.Instant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Silvio Santos
 * @author Bruno Farache
 */
@Component(
	configurationPid = "com.liferay.push.notifications.sender.apple.internal.configuration.ApplePushNotificationsSenderConfiguration",
	property = "platform=" + ApplePushNotificationsSender.PLATFORM,
	service = PushNotificationsSender.class
)
public class ApplePushNotificationsSender implements PushNotificationsSender {

	public static final String PLATFORM = "apple";

	@Override
	public void send(List<String> tokens, JSONObject payloadJSONObject)
		throws Exception {

		if (_apnsClient == null) {
			throw new PushNotificationsException(
				"Apple push notifications sender is not configured properly");
		}

		String payload = _buildPayload(payloadJSONObject);

		for (String token : tokens) {
			_handleNotificationResponse(
				_apnsClient.sendNotification(
					new SimpleApnsPushNotification(token, _topic, payload)));
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) throws Exception {
		ApplePushNotificationsSenderConfiguration
			applePushNotificationsSenderConfiguration =
				ConfigurableUtil.createConfigurable(
					ApplePushNotificationsSenderConfiguration.class,
					properties);

		String appId = applePushNotificationsSenderConfiguration.appId();
		String certificatePassword = _secretResolver.resolve(
			CompanyConstants.SYSTEM,
			applePushNotificationsSenderConfiguration.certificatePassword());
		String certificatePath =
			applePushNotificationsSenderConfiguration.certificatePath();

		if (Validator.isNull(appId) || Validator.isNull(certificatePath) ||
			Validator.isNull(certificatePassword)) {

			_apnsClient = null;
			_topic = "";

			return;
		}

		ApnsClientBuilder apnsClientBuilder = new ApnsClientBuilder();

		try (InputStream inputStream = _getCertificateInputStream(
				certificatePath)) {

			if (inputStream == null) {
				throw new IllegalArgumentException(
					"Unable to find Apple certificate at " + certificatePath);
			}

			apnsClientBuilder.setClientCredentials(
				inputStream, certificatePassword);
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn(ioException);
			}
		}

		if (applePushNotificationsSenderConfiguration.sandbox()) {
			apnsClientBuilder.setApnsServer(
				ApnsClientBuilder.DEVELOPMENT_APNS_HOST);
		}
		else {
			apnsClientBuilder.setApnsServer(
				ApnsClientBuilder.PRODUCTION_APNS_HOST);
		}

		_apnsClient = apnsClientBuilder.build();

		_topic = appId;
	}

	@Deactivate
	protected void deactivate() {
		if (_apnsClient != null) {
			CompletableFuture<Void> close = _apnsClient.close();

			close.join();
		}
	}

	private String _buildPayload(JSONObject payloadJSONObject) {
		SimpleApnsPayloadBuilder simpleApnsPayloadBuilder =
			new SimpleApnsPayloadBuilder();

		JSONObject newPayloadJSONObject = _jsonFactory.createJSONObject();

		Iterator<String> iterator = payloadJSONObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			if (!key.equals(PushNotificationsConstants.KEY_BADGE) &&
				!key.equals(PushNotificationsConstants.KEY_BODY) &&
				!key.equals(PushNotificationsConstants.KEY_BODY_LOCALIZED) &&
				!key.equals(
					PushNotificationsConstants.KEY_BODY_LOCALIZED_ARGUMENTS) &&
				!key.equals(PushNotificationsConstants.KEY_SOUND) &&
				!key.equals(PushNotificationsConstants.KEY_SILENT) &&
				!key.equals(PushNotificationsConstants.KEY_TITLE) &&
				!key.equals(PushNotificationsConstants.KEY_TITLE_LOCALIZED) &&
				!key.equals(
					PushNotificationsConstants.KEY_TITLE_LOCALIZED_ARGUMENTS)) {

				newPayloadJSONObject.put(key, payloadJSONObject.get(key));
			}
		}

		simpleApnsPayloadBuilder.addCustomProperty(
			PushNotificationsConstants.KEY_PAYLOAD,
			newPayloadJSONObject.toString());

		String body = payloadJSONObject.getString(
			PushNotificationsConstants.KEY_BODY);

		if (Validator.isNotNull(body)) {
			simpleApnsPayloadBuilder.setAlertBody(body);
		}

		String title = payloadJSONObject.getString(
			PushNotificationsConstants.KEY_TITLE);

		if (Validator.isNotNull(title)) {
			simpleApnsPayloadBuilder.setAlertTitle(title);
		}

		if (payloadJSONObject.has(PushNotificationsConstants.KEY_BADGE)) {
			simpleApnsPayloadBuilder.setBadgeNumber(
				payloadJSONObject.getInt(PushNotificationsConstants.KEY_BADGE));
		}

		String bodyLocalizedKey = payloadJSONObject.getString(
			PushNotificationsConstants.KEY_BODY_LOCALIZED);

		if (Validator.isNotNull(bodyLocalizedKey)) {
			JSONArray bodyLocalizedArgumentsJSONArray =
				payloadJSONObject.getJSONArray(
					PushNotificationsConstants.KEY_BODY_LOCALIZED_ARGUMENTS);

			if (bodyLocalizedArgumentsJSONArray != null) {
				List<String> localizedArguments = new ArrayList<>();

				for (int i = 0; i < bodyLocalizedArgumentsJSONArray.length();
					 i++) {

					localizedArguments.add(
						bodyLocalizedArgumentsJSONArray.getString(i));
				}

				simpleApnsPayloadBuilder.setLocalizedAlertMessage(
					bodyLocalizedKey,
					localizedArguments.toArray(new String[0]));
			}
			else {
				simpleApnsPayloadBuilder.setLocalizedAlertMessage(
					bodyLocalizedKey);
			}
		}

		String titleLocalizedKey = payloadJSONObject.getString(
			PushNotificationsConstants.KEY_TITLE_LOCALIZED);

		if (Validator.isNotNull(titleLocalizedKey)) {
			JSONArray titleLocalizedArgumentsJSONArray =
				payloadJSONObject.getJSONArray(
					PushNotificationsConstants.KEY_TITLE_LOCALIZED_ARGUMENTS);

			if (titleLocalizedArgumentsJSONArray != null) {
				List<String> localizedArguments = new ArrayList<>();

				for (int i = 0; i < titleLocalizedArgumentsJSONArray.length();
					 i++) {

					localizedArguments.add(
						titleLocalizedArgumentsJSONArray.getString(i));
				}

				simpleApnsPayloadBuilder.setLocalizedAlertTitle(
					titleLocalizedKey,
					localizedArguments.toArray(new String[0]));
			}
			else {
				simpleApnsPayloadBuilder.setLocalizedAlertTitle(
					titleLocalizedKey);
			}
		}

		boolean silent = payloadJSONObject.getBoolean(
			PushNotificationsConstants.KEY_SILENT);

		if (silent) {
			simpleApnsPayloadBuilder.setContentAvailable(true);
		}

		String sound = payloadJSONObject.getString(
			PushNotificationsConstants.KEY_SOUND);

		if (Validator.isNotNull(sound)) {
			simpleApnsPayloadBuilder.setSound(sound);
		}

		return simpleApnsPayloadBuilder.build();
	}

	private InputStream _getCertificateInputStream(String certificatePath) {
		try {
			return new FileInputStream(certificatePath);
		}
		catch (FileNotFoundException fileNotFoundException) {
			if (_log.isDebugEnabled()) {
				_log.debug(fileNotFoundException);
			}

			ClassLoader classLoader =
				ApplePushNotificationsSender.class.getClassLoader();

			return classLoader.getResourceAsStream(certificatePath);
		}
	}

	private void _handleNotificationResponse(
		CompletableFuture<PushNotificationResponse<SimpleApnsPushNotification>>
			completableFuture) {

		completableFuture.whenComplete(
			(simpleApnsPushNotification, throwable) -> {
				if (simpleApnsPushNotification == null) {
					_sendResponse(new AppleResponse(null, throwable));

					return;
				}

				if (!simpleApnsPushNotification.isAccepted() &&
					_log.isWarnEnabled()) {

					String timestamp = String.valueOf(Instant.parse(""));

					Optional<Instant> optional =
						simpleApnsPushNotification.
							getTokenInvalidationTimestamp();

					if (optional.isPresent()) {
						timestamp = String.valueOf(optional.get());
					}

					_log.warn(
						StringBundler.concat(
							"The token is invalid as of ", timestamp, ": ",
							simpleApnsPushNotification.getRejectionReason()));
				}

				_sendResponse(
					new AppleResponse(
						simpleApnsPushNotification.getPushNotification(),
						false));
			});
	}

	private void _sendResponse(AppleResponse appleResponse) {
		Message message = new Message();

		message.setPayload(appleResponse);

		_messageBus.sendMessage(
			PushNotificationsDestinationNames.PUSH_NOTIFICATION_RESPONSE,
			message);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ApplePushNotificationsSender.class);

	private volatile ApnsClient _apnsClient;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private MessageBus _messageBus;

	@Reference
	private SecretResolver _secretResolver;

	private volatile String _topic;

}