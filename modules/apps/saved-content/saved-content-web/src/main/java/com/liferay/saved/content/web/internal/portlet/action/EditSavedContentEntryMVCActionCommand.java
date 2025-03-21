/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saved.content.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.saved.content.constants.MySavedContentPortletKeys;
import com.liferay.saved.content.model.SavedContentEntry;
import com.liferay.saved.content.service.SavedContentEntryService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MySavedContentPortletKeys.MY_SAVED_CONTENT,
		"mvc.command.name=/saved_content/edit_saved_content_entry"
	},
	service = MVCActionCommand.class
)
public class EditSavedContentEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse,
			_updateSavedContentEntry(actionRequest));
	}

	private JSONObject _updateSavedContentEntry(ActionRequest actionRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String className = ParamUtil.getString(actionRequest, "className");
		long classPK = ParamUtil.getLong(actionRequest, "classPK");

		try {
			SavedContentEntry savedContentEntry =
				_savedContentEntryService.fetchSavedContentEntry(
					themeDisplay.getScopeGroupId(), className, classPK);

			if (savedContentEntry == null) {
				_savedContentEntryService.addSavedContentEntry(
					themeDisplay.getScopeGroupId(), className, classPK,
					ServiceContextFactory.getInstance(
						SavedContentEntry.class.getName(), actionRequest));

				return JSONUtil.put("saved", Boolean.TRUE);
			}

			_savedContentEntryService.deleteSavedContentEntry(
				savedContentEntry);

			return JSONUtil.put("saved", Boolean.FALSE);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return JSONUtil.put(
				"errorMessage",
				_language.get(
					themeDisplay.getLocale(), "an-unexpected-error-occurred"));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditSavedContentEntryMVCActionCommand.class);

	@Reference
	private Language _language;

	@Reference
	private SavedContentEntryService _savedContentEntryService;

}