/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.events;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Balazs Breier
 */
@Component(
	property = "key=servlet.service.events.pre", service = LifecycleAction.class
)
public class ArchivedRoomServicePreAction extends Action {

	@Override
	public void run(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			_run(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		Group group = _groupLocalService.fetchGroup(
			_portal.getScopeGroupId(httpServletRequest));

		if (group == null) {
			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", group.getCompanyId());

		if ((objectDefinition == null) ||
			!Objects.equals(
				group.getClassName(), objectDefinition.getClassName())) {

			return;
		}

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			group.getClassPK());

		if ((objectEntry == null) ||
			(MapUtil.getInteger(objectEntry.getValues(), "roomStatus") !=
				WorkflowConstants.STATUS_INACTIVE)) {

			return;
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((permissionChecker != null) &&
			(permissionChecker.isCompanyAdmin() ||
			 permissionChecker.isGroupOwner(group.getGroupId()))) {

			return;
		}

		httpServletResponse.sendRedirect(
			_portal.getPortalURL(httpServletRequest));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ArchivedRoomServicePreAction.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Portal _portal;

}