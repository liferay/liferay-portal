/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action;

import com.liferay.frontend.token.definition.FrontendToken;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryService;
import com.liferay.style.book.web.internal.handler.StyleBookEntryExceptionRequestHandlerUtil;
import com.liferay.style.book.web.internal.util.StyleBookFrontendTokenDefinitionUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Lima
 * @author Thiago Buarque
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StyleBookPortletKeys.STYLE_BOOK,
		"mvc.command.name=/style_book/add_style_book_entry_frontend_token"
	},
	service = MVCActionCommand.class
)
public class AddStyleBookEntryFrontendTokenMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			StyleBookEntry styleBookEntry = _addFrontendToken(actionRequest);

			JSONObject jsonObject = JSONUtil.put(
				"customFrontendTokenDefinition",
				StyleBookFrontendTokenDefinitionUtil.
					getCustomFrontendTokenDefinitionJSONObject(
						themeDisplay.getLocale(), styleBookEntry));

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);
		}
		catch (PortalException portalException) {
			hideDefaultErrorMessage(actionRequest);

			StyleBookEntryExceptionRequestHandlerUtil.handlePortalException(
				actionRequest, actionResponse, portalException);
		}
	}

	private StyleBookEntry _addFrontendToken(ActionRequest actionRequest)
		throws PortalException {

		String label = StringUtil.trim(
			ParamUtil.getString(actionRequest, "label"));

		String frontendTokenName =
			"token" + DigesterUtil.digestHex(DigesterUtil.SHA_256, label);

		String categoryName = ParamUtil.getString(
			actionRequest, "categoryName");

		return _styleBookEntryService.addFrontendToken(
			ParamUtil.getLong(actionRequest, "styleBookEntryId"),
			frontendTokenName,
			ParamUtil.getString(actionRequest, "defaultValue"),
			ParamUtil.getString(actionRequest, "editorType"),
			ParamUtil.getString(actionRequest, "categoryLabel", categoryName),
			categoryName, ParamUtil.getString(actionRequest, "description"),
			label, frontendTokenName,
			ParamUtil.getString(actionRequest, "tokenSetDescription"),
			ParamUtil.getString(actionRequest, "tokenSetLabel"),
			ParamUtil.getString(actionRequest, "tokenSetName"),
			ParamUtil.getString(
				actionRequest, "type", FrontendToken.Type.STRING.getValue()),
			ServiceContextFactory.getInstance(actionRequest));
	}

	@Reference
	private StyleBookEntryService _styleBookEntryService;

}