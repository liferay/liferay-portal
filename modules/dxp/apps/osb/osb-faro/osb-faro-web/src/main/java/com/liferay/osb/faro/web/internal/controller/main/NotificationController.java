/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.osb.faro.constants.FaroNotificationConstants;
import com.liferay.osb.faro.engine.client.CerebroEngineClient;
import com.liferay.osb.faro.model.FaroNotification;
import com.liferay.osb.faro.service.FaroNotificationLocalService;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.model.display.contacts.NotificationDisplay;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Geyson Silva
 */
@Component(service = {FaroController.class, NotificationController.class})
@Path("/{groupId}/notification")
@Produces(MediaType.APPLICATION_JSON)
public class NotificationController extends BaseFaroController {

	@DELETE
	@Path("/{id}")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public void deleteNotification(
			@PathParam("groupId") long groupId, @PathParam("id") long id)
		throws PortalException {

		_faroNotificationLocalService.deleteFaroNotification(id);
	}

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<NotificationDisplay> getNotificationDisplays(
			@PathParam("groupId") long groupId, @QueryParam("type") String type)
		throws Exception {

		if (StringUtil.equals(FaroNotificationConstants.TYPE_ALERT, type)) {
			boolean customEventsLimitReached =
				_cerebroEngineClient.isCustomEventsLimitReached(
					faroProjectLocalService.getFaroProjectByGroupId(groupId));

			long faroNotificationsLast30DaysCount =
				_faroNotificationLocalService.
					getFaroNotificationsLast30DaysCount(
						groupId,
						FaroNotificationConstants.SUBTYPE_BLOCKED_EVENTS_LIMIT,
						type, getUserId());

			if (customEventsLimitReached &&
				(faroNotificationsLast30DaysCount == 0)) {

				_faroNotificationLocalService.addFaroNotification(
					getUserId(), groupId, getUserId(),
					FaroNotificationConstants.SCOPE_USER, type,
					FaroNotificationConstants.SUBTYPE_BLOCKED_EVENTS_LIMIT);
			}
			else if (!customEventsLimitReached &&
					 (faroNotificationsLast30DaysCount > 0)) {

				_faroNotificationLocalService.deleteFaroNotifications(
					groupId, type,
					FaroNotificationConstants.SUBTYPE_BLOCKED_EVENTS_LIMIT,
					getUserId());
			}
		}

		return TransformUtil.transform(
			_faroNotificationLocalService.findFaroNotificationsLast30Days(
				groupId, type, getUserId()),
			NotificationDisplay::new);
	}

	@Path("/{id}/read")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public void readNotification(
			@PathParam("groupId") long groupId, @PathParam("id") long id)
		throws PortalException {

		FaroNotification faroNotification =
			_faroNotificationLocalService.getFaroNotification(id);

		faroNotification.setRead(true);

		_faroNotificationLocalService.updateFaroNotification(faroNotification);
	}

	@Reference
	private CerebroEngineClient _cerebroEngineClient;

	@Reference
	private FaroNotificationLocalService _faroNotificationLocalService;

}