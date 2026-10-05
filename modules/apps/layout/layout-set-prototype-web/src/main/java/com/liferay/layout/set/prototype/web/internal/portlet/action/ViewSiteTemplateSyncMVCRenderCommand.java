/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.web.internal.portlet.action;

import com.liferay.exportimport.rest.resource.v1_0.SyncPreviewResource;
import com.liferay.layout.set.prototype.constants.LayoutSetPrototypePortletKeys;
import com.liferay.layout.set.prototype.web.internal.constants.LayoutSetPrototypeWebKeys;
import com.liferay.layout.set.prototype.web.internal.display.context.SiteTemplateSyncDisplayContext;
import com.liferay.layout.set.prototype.web.internal.util.SiteTemplateSyncUtil;
import com.liferay.portal.kernel.exception.NoSuchLayoutSetPrototypeException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.permission.LayoutSetPrototypePermissionUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jaime Leon
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutSetPrototypePortletKeys.LAYOUT_SET_PROTOTYPE,
		"mvc.command.name=/layout_set_prototype/view_site_template_sync"
	},
	service = MVCRenderCommand.class
)
public class ViewSiteTemplateSyncMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		if (!FeatureFlagManagerUtil.isEnabled(
				_portal.getCompanyId(renderRequest), "LPD-82107")) {

			SessionErrors.add(
				renderRequest, UnsupportedOperationException.class);

			return "/error.jsp";
		}

		long layoutSetPrototypeId = ParamUtil.getLong(
			renderRequest, "layoutSetPrototypeId");

		try {
			LayoutSetPrototypePermissionUtil.check(
				PermissionThreadLocal.getPermissionChecker(),
				layoutSetPrototypeId, ActionKeys.UPDATE);

			LayoutSetPrototype layoutSetPrototype =
				_layoutSetPrototypeLocalService.getLayoutSetPrototype(
					layoutSetPrototypeId);

			if (!layoutSetPrototype.isActive()) {
				SessionErrors.add(
					renderRequest, UnsupportedOperationException.class);

				return "/error.jsp";
			}

			if (SiteTemplateSyncUtil.isPublicationsEnabled(
					layoutSetPrototype.getCompanyId())) {

				SessionErrors.add(renderRequest, "publicationsEnabled");

				return "/error.jsp";
			}

			renderRequest.setAttribute(
				LayoutSetPrototypeWebKeys.SITE_TEMPLATE_SYNC_DISPLAY_CONTEXT,
				new SiteTemplateSyncDisplayContext(
					_portal.getHttpServletRequest(renderRequest),
					layoutSetPrototype,
					_portal.getLiferayPortletResponse(renderResponse),
					_syncPreviewResourceFactory));
		}
		catch (NoSuchLayoutSetPrototypeException | PrincipalException
					exception) {

			SessionErrors.add(renderRequest, exception.getClass());

			return "/error.jsp";
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}

		return "/site_template_sync.jsp";
	}

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SyncPreviewResource.Factory _syncPreviewResourceFactory;

}