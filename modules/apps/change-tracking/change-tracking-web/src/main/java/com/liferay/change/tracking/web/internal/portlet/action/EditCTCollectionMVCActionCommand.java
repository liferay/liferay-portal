/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/edit_ct_collection"
	},
	service = MVCActionCommand.class
)
public class EditCTCollectionMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long ctCollectionId = ParamUtil.getLong(
			actionRequest, "ctCollectionId");

		long ctRemoteId = ParamUtil.getLong(actionRequest, "ctRemoteId");
		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");

		try {
			if (ctCollectionId > 0) {
				_ctCollectionService.updateCTCollection(
					themeDisplay.getUserId(), ctCollectionId, name,
					description);
			}
			else {
				CTCollection ctCollection =
					_ctCollectionService.addCTCollection(
						null, themeDisplay.getCompanyId(),
						themeDisplay.getUserId(), ctRemoteId, name,
						description);

				CTPreferences ctPreferences =
					_ctPreferencesLocalService.getCTPreferences(
						themeDisplay.getCompanyId(), themeDisplay.getUserId());

				ctPreferences.setCtCollectionId(
					ctCollection.getCtCollectionId());
				ctPreferences.setPreviousCtCollectionId(
					CTCollectionThreadLocal.getCTCollectionId());

				_ctPreferencesLocalService.updateCTPreferences(ctPreferences);

				ctCollectionId = ctCollection.getCtCollectionId();
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"ctCollectionId", String.valueOf(ctCollectionId)
				).put(
					"redirect", true
				));
		}
		catch (Exception exception) {
			if (exception instanceof ModelListenerException ||
				exception instanceof PortalException) {

				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse,
					JSONUtil.put(
						"errorMessage",
						_language.get(
							_portal.getHttpServletRequest(actionRequest),
							"an-unexpected-error-occurred")));
			}
			else {
				throw new SystemException(exception);
			}
		}

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			hideDefaultSuccessMessage(actionRequest);

			sendRedirect(actionRequest, actionResponse, redirect);
		}
	}

	@Reference
	private CTCollectionService _ctCollectionService;

	@Reference
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}