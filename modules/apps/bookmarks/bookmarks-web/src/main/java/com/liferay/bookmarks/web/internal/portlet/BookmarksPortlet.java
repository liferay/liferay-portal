/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.portlet;

import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.util.TrashWebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-bookmarks",
		"com.liferay.portlet.display-category=category.community",
		"com.liferay.portlet.header-portlet-css=/bookmarks/css/main.css",
		"com.liferay.portlet.icon=/bookmarks/icons/bookmarks.png",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.struts-path=bookmarks",
		"jakarta.portlet.display-name=Bookmarks",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.always-display-default-configuration-icons=true",
		"jakarta.portlet.init-param.mvc-command-names-default-views=/bookmarks/view",
		"jakarta.portlet.init-param.portlet-title-based-navigation=false",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.supported-public-render-parameter=categoryId",
		"jakarta.portlet.supported-public-render-parameter=resetCur",
		"jakarta.portlet.supported-public-render-parameter=tag",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class BookmarksPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(TrashWebKeys.TRASH_HELPER, _trashHelper);

		super.render(renderRequest, renderResponse);
	}

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.bookmarks.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

	@Reference
	private TrashHelper _trashHelper;

}