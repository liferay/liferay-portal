/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/publish_ct_collection"
	},
	service = MVCActionCommand.class
)
public class PublishCTCollectionMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long ctCollectionId = ParamUtil.getLong(
			actionRequest, "ctCollectionId");

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-39203")) {

			try {
				_ctPreferencesLocalService.resetCTPreferences(ctCollectionId);

				CTCollection ctCollection =
					_ctCollectionLocalService.getCTCollection(ctCollectionId);

				CTPreferences ctPreferences =
					_ctPreferencesLocalService.getCTPreferences(
						ctCollection.getCompanyId(),
						_userLocalService.getGuestUserId(
							ctCollection.getCompanyId()));

				ctPreferences.setCtCollectionId(ctCollectionId);
				ctPreferences.setPreviousCtCollectionId(
					CTConstants.CT_COLLECTION_ID_PRODUCTION);

				_ctPreferencesLocalService.updateCTPreferences(ctPreferences);

				if (_log.isInfoEnabled()) {
					_log.info(
						"Using publication " + ctCollection.getName() +
							" temporarily in place of production");
				}
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to instantly publish publication. Attempting " +
							"to publish normally.",
						portalException);
				}
			}
		}

		String name = ParamUtil.getString(actionRequest, "name");

		try {
			_ctCollectionService.publishCTCollection(
				themeDisplay.getUserId(), ctCollectionId);
		}
		catch (PortalException portalException) {
			SessionErrors.add(actionRequest, portalException.getClass());
		}

		SessionMessages.add(
			actionRequest, "requestProcessed",
			_language.format(
				_portal.getHttpServletRequest(actionRequest),
				"publishing-x-has-started-successfully", new Object[] {name},
				false));

		sendRedirect(
			actionRequest, actionResponse,
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					actionRequest,
					_groupLocalService.getGroup(
						themeDisplay.getCompanyId(),
						GroupConstants.CONTROL_PANEL),
					CTPortletKeys.PUBLICATIONS, 0, 0,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/change_tracking/view_history"
			).buildString());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PublishCTCollectionMVCActionCommand.class);

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTCollectionService _ctCollectionService;

	@Reference
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}