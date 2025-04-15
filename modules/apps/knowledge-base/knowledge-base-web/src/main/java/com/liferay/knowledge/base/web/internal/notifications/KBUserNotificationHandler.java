/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.notifications;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseModelUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(
	property = "jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
	service = UserNotificationHandler.class
)
public class KBUserNotificationHandler
	extends BaseModelUserNotificationHandler {

	public KBUserNotificationHandler() {
		setPortletId(KBPortletKeys.KNOWLEDGE_BASE_ADMIN);
	}

	@Override
	protected String getTitle(
		JSONObject jsonObject, AssetRenderer<?> assetRenderer,
		UserNotificationEvent userNotificationEvent,
		ServiceContext serviceContext) {

		String message = StringPool.BLANK;

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				assetRenderer.getClassName());

		String typeName = assetRendererFactory.getTypeName(
			serviceContext.getLocale());

		int notificationType = jsonObject.getInt("notificationType");

		if (notificationType ==
				UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY) {

			message = "x-added-a-new-x";
		}
		else if (notificationType ==
					UserNotificationDefinition.
						NOTIFICATION_TYPE_EXPIRED_ENTRY) {

			String command = jsonObject.getString("command");

			if (Objects.equals(command, Constants.EXPIRE)) {
				message = "x-was-expired-by-x";
			}
			else {
				return _getFormattedMessage(
					serviceContext, "x-has-expired", typeName);
			}
		}
		else if (notificationType ==
					UserNotificationDefinition.NOTIFICATION_TYPE_REVIEW_ENTRY) {

			return _getFormattedMessage(
				serviceContext, "x-needs-review", typeName);
		}
		else if (notificationType ==
					UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY) {

			message = "x-updated-a-x";
		}

		return getFormattedMessage(
			jsonObject, serviceContext, message, typeName);
	}

	private String _getFormattedMessage(
		ServiceContext serviceContext, String message, String typeName) {

		return _language.format(
			serviceContext.getLocale(), message,
			StringUtil.toLowerCase(HtmlUtil.escape(typeName)));
	}

	@Reference
	private Language _language;

}