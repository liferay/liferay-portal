/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.exception.ExportArticleTargetExtensionException;
import com.liferay.journal.util.ExportArticleHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Farache
 * @author Eduardo García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/export_article"
	},
	service = MVCResourceCommand.class
)
public class ExportArticleMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		try {
			String targetExtension = ParamUtil.getString(
				resourceRequest, "targetExtension");

			PortletPreferences portletPreferences =
				resourceRequest.getPreferences();

			String portletResource = ParamUtil.getString(
				resourceRequest, "portletResource");

			if (Validator.isNotNull(portletResource)) {
				long plid = ParamUtil.getLong(resourceRequest, "plid");

				Layout layout = _layoutLocalService.fetchLayout(plid);

				if (layout != null) {
					portletPreferences =
						PortletPreferencesFactoryUtil.getExistingPortletSetup(
							layout, portletResource);
				}
			}

			String[] allowedExtensions = portletPreferences.getValues(
				"extensions", null);

			if (ArrayUtil.isNotEmpty(allowedExtensions) &&
				(allowedExtensions.length == 1)) {

				allowedExtensions = StringUtil.split(
					portletPreferences.getValue("extensions", null));
			}

			if (ArrayUtil.contains(allowedExtensions, targetExtension, true)) {
				_exportArticleHelper.sendFile(
					targetExtension, resourceRequest, resourceResponse);
			}
			else {
				throw new ExportArticleTargetExtensionException(
					"Target extension " + targetExtension + " is not allowed");
			}
		}
		catch (Exception exception) {
			_log.error("Unable to export article", exception);

			_portal.sendError(
				exception, _portal.getHttpServletRequest(resourceRequest),
				_portal.getHttpServletResponse(resourceResponse));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportArticleMVCResourceCommand.class);

	@Reference
	private ExportArticleHelper _exportArticleHelper;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}