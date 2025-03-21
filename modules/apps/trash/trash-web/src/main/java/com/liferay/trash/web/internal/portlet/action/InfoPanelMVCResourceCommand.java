/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashPortletKeys;
import com.liferay.trash.web.internal.constants.TrashWebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides an implementation of <code>MVCResourceCommand</code> (in
 * <code>com.liferay.portal.kernel</code>) to allow Recycle Bin entries selected
 * in the Recycle Bin portlet to render an information panel.
 *
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + TrashPortletKeys.TRASH,
		"mvc.command.name=/trash/info_panel"
	},
	service = MVCResourceCommand.class
)
public class InfoPanelMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		resourceRequest.setAttribute(
			TrashWebKeys.TRASH_ENTRIES,
			ActionUtil.getTrashEntries(resourceRequest));
		resourceRequest.setAttribute(TrashWebKeys.TRASH_HELPER, _trashHelper);

		include(resourceRequest, resourceResponse, "/info_panel.jsp");
	}

	@Reference
	private TrashHelper _trashHelper;

}