/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.web.internal.helper.PublicationHelper;
import com.liferay.change.tracking.web.internal.security.permission.resource.CTCollectionPermission;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/get_share_publication_link"
	},
	service = MVCResourceCommand.class
)
public class GetSharePublicationLinkMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		long ctCollectionId = ParamUtil.getLong(
			resourceRequest, "ctCollectionId");

		CTCollection ctCollection = _ctCollectionLocalService.getCTCollection(
			ctCollectionId);

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			AuthTokenUtil.checkCSRFToken(
				_portal.getHttpServletRequest(resourceRequest),
				GetSharePublicationLinkMVCResourceCommand.class.getName());

			CTCollectionPermission.check(
				themeDisplay.getPermissionChecker(), ctCollection,
				CTActionKeys.INVITE_USERS);
		}
		catch (PrincipalException principalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(principalException);
			}

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"errorMessage",
					_language.get(
						themeDisplay.getLocale(),
						"you-do-not-have-the-required-permissions")));

			return;
		}

		boolean shareable = ParamUtil.getBoolean(resourceRequest, "shareable");

		if (ctCollection.isShareable() != shareable) {
			ctCollection.setShareable(shareable);

			ctCollection = _ctCollectionLocalService.updateCTCollection(
				ctCollection);
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"shareable", ctCollection.isShareable()
			).put(
				"sharePublicationLink",
				_publicationHelper.getShareURL(
					ctCollection.getCtCollectionId(), resourceRequest)
			));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetSharePublicationLinkMVCResourceCommand.class);

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PublicationHelper _publicationHelper;

}