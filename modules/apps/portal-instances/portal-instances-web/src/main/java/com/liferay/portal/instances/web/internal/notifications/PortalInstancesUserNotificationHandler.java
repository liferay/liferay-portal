/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.notifications;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.instances.constants.PortalInstancesNotificationConstants;
import com.liferay.portal.instances.constants.PortalInstancesPortletKeys;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Ortiz
 */
@Component(
	property = "jakarta.portlet.name=" + PortalInstancesPortletKeys.PORTAL_INSTANCES,
	service = UserNotificationHandler.class
)
public class PortalInstancesUserNotificationHandler
	extends BaseUserNotificationHandler {

	public PortalInstancesUserNotificationHandler() {
		setPortletId(PortalInstancesPortletKeys.PORTAL_INSTANCES);
	}

	@Override
	protected String getBody(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		return StringBundler.concat(
			"<h2 class=\"title\">", _getTitle(jsonObject, serviceContext),
			"</h2><div class=\"body\">", _getBody(jsonObject, serviceContext),
			"</div>");
	}

	@Override
	protected String getLink(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		return _portal.getControlPanelPortletURL(
			serviceContext.getRequest(), serviceContext.getScopeGroup(),
			PortalInstancesPortletKeys.PORTAL_INSTANCES, 0, 0,
			PortletRequest.RENDER_PHASE
		).toString();
	}

	@Override
	protected String getTitle(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		return _getTitle(jsonObject, serviceContext);
	}

	private String _getBody(
		JSONObject jsonObject, ServiceContext serviceContext) {

		String operationType = jsonObject.getString("operationType");
		String status = jsonObject.getString("status");

		if (operationType.equals(
				PortalInstancesNotificationConstants.OPERATION_TYPE_ADD) ||
			operationType.equals(
				PortalInstancesNotificationConstants.OPERATION_TYPE_COPY)) {

			if (status.equals(
					PortalInstancesNotificationConstants.STATUS_SUCCESS)) {

				return serviceContext.translate(
					"the-instance-x-is-ready-to-use",
					jsonObject.getString("portalInstanceId"));
			}

			return serviceContext.translate(
				jsonObject.getString("errorMessageKey"));
		}

		if (operationType.equals(
				PortalInstancesNotificationConstants.OPERATION_TYPE_DELETE)) {

			if (status.equals(
					PortalInstancesNotificationConstants.STATUS_SUCCESS)) {

				return serviceContext.translate(
					"the-instance-x-is-no-longer-available",
					jsonObject.getString("portalInstanceId"));
			}

			return serviceContext.translate(
				jsonObject.getString("errorMessageKey"));
		}

		if (operationType.equals(
				PortalInstancesNotificationConstants.OPERATION_TYPE_EXPORT)) {

			if (status.equals(
					PortalInstancesNotificationConstants.STATUS_SUCCESS)) {

				return serviceContext.translate(
					"the-instance-was-exported-to-the-schema-x",
					jsonObject.getString("schemaName"));
			}

			return serviceContext.translate(
				jsonObject.getString("errorMessageKey"),
				jsonObject.getString("schemaName"));
		}

		if (operationType.equals(
				PortalInstancesNotificationConstants.OPERATION_TYPE_IMPORT)) {

			if (status.equals(
					PortalInstancesNotificationConstants.STATUS_SUCCESS)) {

				return serviceContext.translate(
					"the-instance-x-is-ready-to-use",
					jsonObject.getString("portalInstanceId"));
			}

			return serviceContext.translate(
				jsonObject.getString("errorMessageKey"));
		}

		throw new IllegalArgumentException(
			StringBundler.concat(
				"No portal instances user notification found for operation ",
				"type ", operationType, " and status ", status));
	}

	private String _getTitle(
		JSONObject jsonObject, ServiceContext serviceContext) {

		String operationType = jsonObject.getString("operationType");
		String status = jsonObject.getString("status");

		if (operationType.equals(
				PortalInstancesNotificationConstants.OPERATION_TYPE_ADD)) {

			if (status.equals(
					PortalInstancesNotificationConstants.STATUS_SUCCESS)) {

				return serviceContext.translate(
					"the-instance-x-was-created",
					jsonObject.getString("portalInstanceId"));
			}

			return serviceContext.translate(
				"the-instance-x-could-not-be-created",
				jsonObject.getString("portalInstanceId"));
		}

		if (operationType.equals(
				PortalInstancesNotificationConstants.OPERATION_TYPE_COPY)) {

			if (status.equals(
					PortalInstancesNotificationConstants.STATUS_SUCCESS)) {

				return serviceContext.translate(
					"the-instance-x-was-copied-to-y",
					jsonObject.getString("sourcePortalInstanceId"),
					jsonObject.getString("portalInstanceId"));
			}

			return serviceContext.translate(
				"the-instance-x-could-not-be-copied-to-y",
				jsonObject.getString("sourcePortalInstanceId"),
				jsonObject.getString("portalInstanceId"));
		}

		if (operationType.equals(
				PortalInstancesNotificationConstants.OPERATION_TYPE_DELETE)) {

			if (status.equals(
					PortalInstancesNotificationConstants.STATUS_SUCCESS)) {

				return serviceContext.translate(
					"the-instance-x-was-deleted",
					jsonObject.getString("portalInstanceId"));
			}

			return serviceContext.translate(
				"the-instance-x-could-not-be-deleted",
				jsonObject.getString("portalInstanceId"));
		}

		if (operationType.equals(
				PortalInstancesNotificationConstants.OPERATION_TYPE_EXPORT)) {

			if (status.equals(
					PortalInstancesNotificationConstants.STATUS_SUCCESS)) {

				return serviceContext.translate(
					"the-instance-x-was-exported",
					jsonObject.getString("portalInstanceId"));
			}

			return serviceContext.translate(
				"the-instance-x-could-not-be-exported",
				jsonObject.getString("portalInstanceId"));
		}

		if (operationType.equals(
				PortalInstancesNotificationConstants.OPERATION_TYPE_IMPORT)) {

			if (status.equals(
					PortalInstancesNotificationConstants.STATUS_SUCCESS)) {

				return serviceContext.translate(
					"the-instance-x-was-imported",
					jsonObject.getString("portalInstanceId"));
			}

			return serviceContext.translate(
				"the-instance-could-not-be-imported-from-the-schema-x",
				jsonObject.getString("schemaName"));
		}

		throw new IllegalArgumentException(
			StringBundler.concat(
				"No portal instances user notification found for operation ",
				"type ", operationType, " and status ", status));
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}