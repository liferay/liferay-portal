/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action;

import com.liferay.frontend.token.definition.FrontendToken;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.zip.processor.StyleBookEntryZipProcessor;
import com.liferay.style.book.zip.processor.StyleBookEntryZipProcessorImportResultEntry;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.File;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StyleBookPortletKeys.STYLE_BOOK,
		"mvc.command.name=/style_book/import_style_book_entries"
	},
	service = MVCActionCommand.class
)
public class ImportStyleBookEntriesMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void addSuccessMessage(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		String successMessage = _language.get(
			_portal.getHttpServletRequest(actionRequest),
			"the-files-were-imported-correctly");

		SessionMessages.add(actionRequest, "requestProcessed", successMessage);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		File file = uploadPortletRequest.getFile("file");

		boolean overwrite = ParamUtil.getBoolean(
			actionRequest, "overwrite", true);

		try {
			List<StyleBookEntryZipProcessorImportResultEntry>
				styleBookEntryZipProcessorImportResultEntries =
					_importStyleBookEntries(
						themeDisplay.getUserId(),
						themeDisplay.getScopeGroupId(), file, overwrite);

			if (ListUtil.isNotEmpty(
					styleBookEntryZipProcessorImportResultEntries)) {

				SessionMessages.add(
					actionRequest,
					"styleBookEntryZipProcessorImportResultEntries",
					styleBookEntryZipProcessorImportResultEntries);
			}

			SessionMessages.add(actionRequest, "success");

			for (StyleBookEntryZipProcessorImportResultEntry
					styleBookEntryZipProcessorImportResultEntry :
						styleBookEntryZipProcessorImportResultEntries) {

				StyleBookEntry styleBookEntry =
					styleBookEntryZipProcessorImportResultEntry.
						getStyleBookEntry();

				if ((styleBookEntryZipProcessorImportResultEntry.getStatus() !=
						StyleBookEntryZipProcessorImportResultEntry.Status.
							INVALID) &&
					(styleBookEntry != null) &&
					!_isValidFrontendTokenDefinition(
						_getFrontendTokenNames(
							themeDisplay, styleBookEntry.getThemeId()),
						styleBookEntry)) {

					SessionMessages.add(
						actionRequest,
						"styleBookFrontendTokensValuesNotValidated");

					break;
				}
			}
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, exception.getClass(), exception);
		}

		sendRedirect(actionRequest, actionResponse);
	}

	private Set<String> _getFrontendTokenNames(
		ThemeDisplay themeDisplay, String themeId) {

		Set<String> frontendTokenNames = new HashSet<>();

		FrontendTokenDefinition frontendTokenDefinition = null;

		if (FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-30204")) {

			frontendTokenDefinition =
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
					themeDisplay.getCompanyId(), themeId);
		}
		else {
			frontendTokenDefinition =
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
					_layoutSetLocalService.fetchLayoutSet(
						themeDisplay.getSiteGroupId(), false));
		}

		if (frontendTokenDefinition != null) {
			Collection<FrontendToken> frontendTokens =
				frontendTokenDefinition.getFrontendTokens();

			for (FrontendToken frontendToken : frontendTokens) {
				frontendTokenNames.add(frontendToken.getName());
			}
		}

		return frontendTokenNames;
	}

	private List<StyleBookEntryZipProcessorImportResultEntry>
			_importStyleBookEntries(
				long userId, long groupId, File file, boolean overwrite)
		throws Exception {

		return _styleBookEntryZipProcessor.importStyleBookEntries(
			userId, groupId, file, overwrite);
	}

	private boolean _isValidFrontendTokenDefinition(
			Set<String> frontendTokenNames, StyleBookEntry styleBookEntry)
		throws JSONException {

		JSONObject frontendTokensValuesJSONObject =
			_jsonFactory.createJSONObject(
				styleBookEntry.getFrontendTokensValues());

		for (String key : frontendTokensValuesJSONObject.keySet()) {
			if (!frontendTokenNames.contains(key)) {
				return false;
			}
		}

		return true;
	}

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private StyleBookEntryZipProcessor _styleBookEntryZipProcessor;

}