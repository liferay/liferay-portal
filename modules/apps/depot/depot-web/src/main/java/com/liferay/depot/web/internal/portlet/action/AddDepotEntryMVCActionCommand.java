/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.portlet.action;

import com.liferay.depot.exception.DepotEntryNameException;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.depot.web.internal.constants.DepotEntryConstants;
import com.liferay.depot.web.internal.constants.DepotPortletKeys;
import com.liferay.depot.web.internal.util.DepotEntryURLUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.DuplicateGroupException;
import com.liferay.portal.kernel.exception.GroupKeyException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DepotPortletKeys.DEPOT_ADMIN,
		"mvc.command.name=/depot/add_depot_entry"
	},
	service = MVCActionCommand.class
)
public class AddDepotEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String name = ParamUtil.getString(actionRequest, "name");
		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");

		if (Validator.isNotNull(name)) {
			nameMap.put(LocaleUtil.getDefault(), name);
		}

		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");

		try {
			MultiSessionMessages.add(
				actionRequest,
				DepotPortletKeys.DEPOT_ADMIN + "requestProcessed");

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"redirectURL",
					() -> {
						DepotEntry depotEntry =
							_depotEntryService.addDepotEntry(
								nameMap, descriptionMap,
								ServiceContextFactory.getInstance(
									DepotEntry.class.getName(), actionRequest));

						return String.valueOf(
							DepotEntryURLUtil.getEditDepotEntryPortletURL(
								depotEntry,
								ParamUtil.getString(actionRequest, "redirect"),
								_portal.getLiferayPortletRequest(
									actionRequest)));
					}));
		}
		catch (Exception exception) {
			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"error",
					_getErrorMessage(
						(ThemeDisplay)actionRequest.getAttribute(
							WebKeys.THEME_DISPLAY),
						exception.getCause())));
		}
	}

	private String _getErrorMessage(
		ThemeDisplay themeDisplay, Throwable throwable) {

		if (throwable instanceof DepotEntryNameException) {
			return _language.get(
				themeDisplay.getRequest(), "please-enter-a-name");
		}

		if (throwable instanceof DuplicateGroupException) {
			return _language.get(
				themeDisplay.getRequest(), "please-enter-a-unique-name");
		}

		if (throwable instanceof GroupKeyException) {
			return _handleGroupKeyException(themeDisplay);
		}

		return _language.get(
			themeDisplay.getRequest(), "an-unexpected-error-occurred");
	}

	private String _handleGroupKeyException(ThemeDisplay themeDisplay) {
		StringBundler sb = new StringBundler(5);

		sb.append(
			_language.format(
				themeDisplay.getRequest(),
				"the-x-cannot-be-x-or-a-reserved-word-such-as-x",
				new String[] {
					DepotEntryConstants.NAME_LABEL,
					DepotEntryConstants.getNameGeneralRestrictions(
						themeDisplay.getLocale()),
					DepotEntryConstants.NAME_RESERVED_WORDS
				}));

		sb.append(StringPool.SPACE);

		sb.append(
			_language.format(
				themeDisplay.getRequest(),
				"the-x-cannot-contain-the-following-invalid-characters-x",
				new String[] {
					DepotEntryConstants.NAME_LABEL,
					DepotEntryConstants.NAME_INVALID_CHARACTERS
				}));

		sb.append(StringPool.SPACE);

		int groupKeyMaxLength = ModelHintsUtil.getMaxLength(
			Group.class.getName(), "groupKey");

		sb.append(
			_language.format(
				themeDisplay.getRequest(),
				"the-x-cannot-contain-more-than-x-characters",
				new String[] {
					DepotEntryConstants.NAME_LABEL,
					String.valueOf(groupKeyMaxLength)
				}));

		return sb.toString();
	}

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}