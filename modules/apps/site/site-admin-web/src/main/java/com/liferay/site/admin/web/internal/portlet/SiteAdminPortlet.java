/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.portlet;

import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.exportimport.kernel.exception.RemoteExportException;
import com.liferay.portal.kernel.exception.DuplicateGroupException;
import com.liferay.portal.kernel.exception.GroupFriendlyURLException;
import com.liferay.portal.kernel.exception.GroupInheritContentException;
import com.liferay.portal.kernel.exception.GroupKeyException;
import com.liferay.portal.kernel.exception.GroupNameException;
import com.liferay.portal.kernel.exception.GroupParentException;
import com.liferay.portal.kernel.exception.LayoutSetVirtualHostException;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.NoSuchBackgroundTaskException;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchLayoutSetException;
import com.liferay.portal.kernel.exception.PendingBackgroundTaskException;
import com.liferay.portal.kernel.exception.RemoteOptionsException;
import com.liferay.portal.kernel.exception.RequiredGroupException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.RemoteAuthException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.site.admin.web.internal.constants.SiteAdminPortletKeys;
import com.liferay.site.constants.SiteWebKeys;
import com.liferay.site.initializer.SiteInitializerRegistry;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 * @author Marco Leo
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-site-admin",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.icon=/icons/site_admin.png",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Sites Admin",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + SiteAdminPortletKeys.SITE_ADMIN,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SiteAdminPortlet extends MVCPortlet {

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			SiteWebKeys.SITE_INITIALIZER_REGISTRY, siteInitializerRegistry);

		if (SessionErrors.contains(
				renderRequest, NoSuchBackgroundTaskException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, NoSuchGroupException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PrincipalException.getNestedClasses())) {

			include("/error.jsp", renderRequest, renderResponse);
		}
		else if (SessionErrors.contains(
					renderRequest, NoSuchLayoutSetException.class.getName())) {

			include("/view.jsp", renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof AssetCategoryException ||
			throwable instanceof AssetTagException ||
			throwable instanceof AuthException ||
			throwable instanceof DuplicateGroupException ||
			throwable instanceof GroupFriendlyURLException ||
			throwable instanceof GroupInheritContentException ||
			throwable instanceof GroupKeyException ||
			throwable instanceof GroupNameException ||
			throwable instanceof GroupParentException ||
			throwable instanceof LayoutSetVirtualHostException ||
			throwable instanceof LocaleException ||
			throwable instanceof NoSuchBackgroundTaskException ||
			throwable instanceof NoSuchLayoutSetException ||
			throwable instanceof PendingBackgroundTaskException ||
			throwable instanceof RemoteAuthException ||
			throwable instanceof RemoteExportException ||
			throwable instanceof RemoteOptionsException ||
			throwable instanceof RequiredGroupException ||
			throwable instanceof SystemException ||
			super.isSessionErrorException(throwable)) {

			return true;
		}

		return false;
	}

	@Reference
	protected SiteInitializerRegistry siteInitializerRegistry;

}