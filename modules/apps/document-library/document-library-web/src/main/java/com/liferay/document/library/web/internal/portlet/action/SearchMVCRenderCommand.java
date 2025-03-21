/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.web.internal.constants.DLWebKeys;
import com.liferay.document.library.web.internal.display.context.DLAdminDisplayContext;
import com.liferay.document.library.web.internal.display.context.DLAdminDisplayContextProvider;
import com.liferay.document.library.web.internal.display.context.DLAdminManagementToolbarDisplayContext;
import com.liferay.document.library.web.internal.helper.DLTrashHelper;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"mvc.command.name=/document_library/search"
	},
	service = MVCRenderCommand.class
)
public class SearchMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		DLAdminDisplayContext dlAdminDisplayContext =
			_dlAdminDisplayContextProvider.getDLAdminDisplayContext(
				_portal.getHttpServletRequest(renderRequest),
				_portal.getHttpServletResponse(renderResponse));

		renderRequest.setAttribute(
			DLAdminDisplayContext.class.getName(), dlAdminDisplayContext);
		renderRequest.setAttribute(
			DLAdminManagementToolbarDisplayContext.class.getName(),
			_dlAdminDisplayContextProvider.
				getDLAdminManagementToolbarDisplayContext(
					_portal.getHttpServletRequest(renderRequest),
					_portal.getHttpServletResponse(renderResponse),
					dlAdminDisplayContext));

		renderRequest.setAttribute(
			DLWebKeys.DOCUMENT_LIBRARY_PORTLET_TOOLBAR_CONTRIBUTOR,
			_dlPortletToolbarContributor);
		renderRequest.setAttribute(
			DLWebKeys.DOCUMENT_LIBRARY_TRASH_HELPER, _dlTrashHelper);

		return "/document_library/view.jsp";
	}

	@Reference
	private DLAdminDisplayContextProvider _dlAdminDisplayContextProvider;

	@Reference(
		target = "(jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY + ")"
	)
	private PortletToolbarContributor _dlPortletToolbarContributor;

	@Reference
	private DLTrashHelper _dlTrashHelper;

	@Reference
	private Portal _portal;

}