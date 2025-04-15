/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.portlet.action;

import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.display.context.ObjectEntryDisplayContextFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Marco Leo
 */
public class EditObjectEntryMVCRenderCommand implements MVCRenderCommand {

	public EditObjectEntryMVCRenderCommand(
		ObjectEntryDisplayContextFactory objectEntryDisplayContextFactory,
		Portal portal) {

		_objectEntryDisplayContextFactory = objectEntryDisplayContextFactory;
		_portal = portal;
	}

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);

		httpServletRequest.setAttribute(
			ObjectWebKeys.OBJECT_ENTRY_READ_ONLY, Boolean.FALSE);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			_objectEntryDisplayContextFactory.create(httpServletRequest));

		return "/object_entries/edit_object_entry.jsp";
	}

	private final ObjectEntryDisplayContextFactory
		_objectEntryDisplayContextFactory;
	private final Portal _portal;

}