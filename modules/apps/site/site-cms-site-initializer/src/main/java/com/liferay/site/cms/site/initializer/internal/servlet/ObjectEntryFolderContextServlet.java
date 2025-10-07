/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.servlet;

import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderService;
import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.site.cms.site.initializer.internal.servlet.ObjectEntryFolderContextServlet",
		"osgi.http.whiteboard.servlet.pattern=/cms/object-entry-folder-context/*",
		"servlet.init.httpMethods=GET"
	},
	service = Servlet.class
)
public class ObjectEntryFolderContextServlet extends HttpServlet {

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		_process(httpServletRequest, httpServletResponse);

		super.service(httpServletRequest, httpServletResponse);
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		long objectEntryFolderId = ParamUtil.getLong(
			httpServletRequest, "objectEntryFolderId");

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		ServletResponseUtil.write(
			httpServletResponse,
			JSONUtil.put(
				"label",
				() -> {
					ObjectEntryFolder objectEntryFolder =
						_objectEntryFolderService.getObjectEntryFolder(
							objectEntryFolderId);

					return objectEntryFolder.getLabel(
						_language.getLanguageId(httpServletRequest));
				}
			).put(
				"viewFolderURL",
				ActionUtil.getViewFolderURL(
					objectEntryFolderId,
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY))
			).toString());
	}

	private void _process(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			EventsProcessorUtil.process(
				PropsKeys.SERVLET_SERVICE_EVENTS_PRE,
				PropsValues.SERVLET_SERVICE_EVENTS_PRE, httpServletRequest,
				httpServletResponse);
		}
		catch (ActionException actionException) {
			if (_log.isDebugEnabled()) {
				_log.debug(actionException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryFolderContextServlet.class);

	@Reference
	private Language _language;

	@Reference
	private ObjectEntryFolderService _objectEntryFolderService;

}