/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.notifications;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseModelUserNotificationHandler;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.WindowState;

/**
 * @author Feliphe Marinho
 */
public class ObjectUserNotificationsHandler
	extends BaseModelUserNotificationHandler {

	public ObjectUserNotificationsHandler(
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		ObjectDefinition objectDefinition) {

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_objectDefinition = objectDefinition;

		setPortletId(objectDefinition.getPortletId());
	}

	@Override
	protected String getBody(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		return _getMessage(
			JSONFactoryUtil.createJSONObject(
				userNotificationEvent.getPayload()));
	}

	@Override
	protected String getLink(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			userNotificationEvent.getPayload());

		if (GetterUtil.getBoolean(jsonObject.get("exceedsObjectEntryLimit"))) {
			RequestBackedPortletURLFactory requestBackedPortletURLFactory =
				RequestBackedPortletURLFactoryUtil.create(
					serviceContext.getRequest());

			return PortletURLBuilder.create(
				requestBackedPortletURLFactory.createActionURL(
					ConfigurationAdminPortletKeys.INSTANCE_SETTINGS)
			).setMVCRenderCommandName(
				"/configuration_admin/edit_configuration"
			).setRedirect(
				serviceContext.getCurrentURL()
			).setParameter(
				"factoryPid",
				"com.liferay.object.configuration.ObjectConfiguration"
			).setWindowState(
				WindowState.MAXIMIZED
			).buildString();
		}

		if (serviceContext.getThemeDisplay() != null) {
			String friendlyURL =
				_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
					new InfoItemReference(
						_objectDefinition.getClassName(),
						new ClassPKInfoItemIdentifier(
							jsonObject.getLong("classPK"))),
					serviceContext.getThemeDisplay());

			if (friendlyURL != null) {
				return friendlyURL;
			}
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				serviceContext.getRequest(), serviceContext.getScopeGroup(),
				jsonObject.getString("portletId"), 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/object_entries/edit_object_entry"
		).setParameter(
			"externalReferenceCode",
			jsonObject.getString("externalReferenceCode")
		).setParameter(
			"objectDefinitionId", _objectDefinition.getObjectDefinitionId()
		).setWindowState(
			WindowState.MAXIMIZED
		).buildString();
	}

	@Override
	protected String getTitle(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		return _getMessage(
			JSONFactoryUtil.createJSONObject(
				userNotificationEvent.getPayload()));
	}

	private String _getMessage(JSONObject jsonObject) {
		return HtmlUtil.escape(jsonObject.getString("notificationMessage"));
	}

	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final ObjectDefinition _objectDefinition;

}