/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.push.notifications.sender.microsoft.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.push.notifications.constants.PushNotificationsConstants;
import com.liferay.push.notifications.sender.PushNotificationsSender;

import java.util.Iterator;
import java.util.List;

import org.jboss.aerogear.windows.mpns.MPNS;
import org.jboss.aerogear.windows.mpns.MpnsNotificationBuilder;
import org.jboss.aerogear.windows.mpns.MpnsService;
import org.jboss.aerogear.windows.mpns.MpnsServiceBuilder;
import org.jboss.aerogear.windows.mpns.notifications.TileNotification;
import org.jboss.aerogear.windows.mpns.notifications.ToastNotification;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Gamarra
 * @author Salva Tejero
 */
@Component(
	property = "platform=" + MicrosoftPushNotificationsSender.PLATFORM,
	service = PushNotificationsSender.class
)
public class MicrosoftPushNotificationsSender
	implements PushNotificationsSender {

	public static final String PLATFORM = "microsoft";

	@Override
	public void send(List<String> tokens, JSONObject payloadJSONObject)
		throws Exception {

		String from = StringPool.BLANK;

		if (payloadJSONObject.has(PushNotificationsConstants.KEY_FROM)) {
			from = payloadJSONObject.getString(
				PushNotificationsConstants.KEY_FROM);
		}

		String body = payloadJSONObject.getString(
			PushNotificationsConstants.KEY_BODY);

		JSONObject newPayloadJSONObject = _jsonFactory.createJSONObject();

		Iterator<String> iterator = payloadJSONObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			if (key.equals(PushNotificationsConstants.KEY_FROM) ||
				key.equals(PushNotificationsConstants.KEY_BODY)) {

				continue;
			}

			newPayloadJSONObject.put(key, payloadJSONObject.get(key));
		}

		String attributes = _getAttributes(newPayloadJSONObject);

		TileNotification tileNotification = _buildTileNotification(
			from, body, attributes);
		ToastNotification toastNotification = _buildToastNotification(
			from, body, attributes);

		for (String token : tokens) {
			_mpnsService.push(token, tileNotification);
			_mpnsService.push(token, toastNotification);
		}
	}

	@Activate
	protected void activate() {
		MpnsServiceBuilder mpnsServiceBuilder = MPNS.newService();

		_mpnsService = mpnsServiceBuilder.build();
	}

	@Deactivate
	protected void deactivate() {
		_mpnsService = null;
	}

	private TileNotification _buildTileNotification(
		String from, String body, String attributes) {

		MpnsNotificationBuilder mpnsNotificationBuilder =
			MPNS.newNotification();

		TileNotification.Builder builder = mpnsNotificationBuilder.tile();

		builder.backContent(body);
		builder.backTitle(from);
		builder.callbackUri(attributes);
		builder.count(1);
		builder.title(from);

		return builder.build();
	}

	private ToastNotification _buildToastNotification(
		String from, String body, String attributes) {

		MpnsNotificationBuilder mpnsNotificationBuilder =
			MPNS.newNotification();

		ToastNotification.Builder builder = mpnsNotificationBuilder.toast();

		builder.parameter(attributes);
		builder.subtitle(body);
		builder.title(from);

		return builder.build();
	}

	private String _getAttributes(JSONObject payloadJSONObject) {
		StringBundler sb = new StringBundler();

		Iterator<String> iterator = payloadJSONObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			sb.append(key);

			sb.append(CharPool.EQUAL);
			sb.append(payloadJSONObject.getString(key));
		}

		return sb.toString();
	}

	@Reference
	private JSONFactory _jsonFactory;

	private volatile MpnsService _mpnsService;

}