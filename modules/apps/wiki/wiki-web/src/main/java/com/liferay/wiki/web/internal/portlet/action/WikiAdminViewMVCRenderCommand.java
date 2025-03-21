/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.wiki.constants.WikiPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Iván Zaera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"mvc.command.name=/", "mvc.command.name=/wiki_admin/view"
	},
	service = MVCRenderCommand.class
)
public class WikiAdminViewMVCRenderCommand
	extends BaseViewPageMVCRenderCommand {

	@Override
	protected String getPath() {
		return "/wiki_admin/view.jsp";
	}

}