/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.renderer;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.renderer.SharingEntryViewRenderer;
import com.liferay.sharing.web.internal.display.context.ViewSharedAssetsDisplayContext;
import com.liferay.sharing.web.internal.display.context.ViewSharingEntryAssetEntryDisplayContext;
import com.liferay.sharing.web.internal.util.AssetRendererSharingUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Alejandro Tardín
 */
public class AssetRendererSharingEntryViewRenderer
	implements SharingEntryViewRenderer {

	public AssetRendererSharingEntryViewRenderer(
		ServletContext servletContext) {

		_servletContext = servletContext;
	}

	@Override
	public void render(
			SharingEntry sharingEntry, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, PortalException {

		try {
			httpServletRequest.setAttribute(
				ViewSharingEntryAssetEntryDisplayContext.class.getName(),
				new ViewSharingEntryAssetEntryDisplayContext(
					AssetRendererSharingUtil.getAssetRenderer(sharingEntry),
					PortalUtil.getLiferayPortletRequest(
						(PortletRequest)httpServletRequest.getAttribute(
							JavaConstants.JAKARTA_PORTLET_REQUEST)),
					PortalUtil.getLiferayPortletResponse(
						(PortletResponse)httpServletRequest.getAttribute(
							JavaConstants.JAKARTA_PORTLET_RESPONSE)),
					sharingEntry,
					(ViewSharedAssetsDisplayContext)
						httpServletRequest.getAttribute(
							ViewSharedAssetsDisplayContext.class.getName())));

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(_JSP_PATH);

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (IOException | ServletException exception) {
			_log.error("Unable to include JSP " + _JSP_PATH, exception);

			throw new IOException(
				"Unable to include JSP " + _JSP_PATH, exception);
		}
	}

	private static final String _JSP_PATH =
		"/shared_assets/view_asset_entry_sharing_entry.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		AssetRendererSharingEntryViewRenderer.class);

	private final ServletContext _servletContext;

}