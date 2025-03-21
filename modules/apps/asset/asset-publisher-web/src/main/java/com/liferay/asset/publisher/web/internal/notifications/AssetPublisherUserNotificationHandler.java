/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.notifications;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseModelUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = "jakarta.portlet.name=" + AssetPublisherPortletKeys.ASSET_PUBLISHER,
	service = UserNotificationHandler.class
)
public class AssetPublisherUserNotificationHandler
	extends BaseModelUserNotificationHandler {

	public AssetPublisherUserNotificationHandler() {
		setPortletId(AssetPublisherPortletKeys.ASSET_PUBLISHER);
	}

	@Override
	protected String getBody(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		return StringUtil.replace(
			getBodyTemplate(), new String[] {"[$BODY$]", "[$TITLE$]"},
			new String[] {
				getBodyContent(
					_jsonFactory.createJSONObject(
						userNotificationEvent.getPayload())),
				getTitle(userNotificationEvent, serviceContext)
			});
	}

	@Override
	protected String getBodyContent(JSONObject jsonObject) {
		JSONObject contextJSONObject = jsonObject.getJSONObject("context");

		JSONObject assetEntriesJSONObject = contextJSONObject.getJSONObject(
			"[$ASSET_ENTRIES$]");

		return assetEntriesJSONObject.getString("escapedValue");
	}

	@Override
	protected String getTitle(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		Map<String, String> localizedJsonSubjectMap =
			(Map)_jsonFactory.looseDeserialize(
				String.valueOf(jsonObject.get("localizedSubjectMap")));

		String subject = localizedJsonSubjectMap.get(
			serviceContext.getLanguageId());

		return StringUtil.replace(
			subject, new String[] {"[$PORTLET_TITLE$]"},
			new String[] {
				HtmlUtil.escape(
					_portal.getPortletTitle(
						AssetPublisherPortletKeys.ASSET_PUBLISHER,
						serviceContext.getLanguageId()))
			});
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}