/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.portlet.action;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.BaseJSPSettingsConfigurationAction;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN,
	service = ConfigurationAction.class
)
public class MBAdminConfigurationAction
	extends BaseJSPSettingsConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/message_boards_admin/configuration_browse.jsp";
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		validateEmail(actionRequest, "emailMessageAdded");
		validateEmail(actionRequest, "emailMessageUpdated");

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	@Override
	protected void updateMultiValuedKeys(ActionRequest actionRequest) {
		super.updateMultiValuedKeys(actionRequest);

		_updateThreadPriorities(actionRequest);
		_updateUserRanks(actionRequest);
	}

	private boolean _isValidUserRank(String rank) {
		if ((StringUtil.count(rank, CharPool.EQUAL) != 1) ||
			rank.startsWith(StringPool.EQUAL) ||
			rank.endsWith(StringPool.EQUAL)) {

			return false;
		}

		return true;
	}

	private void _updateThreadPriorities(ActionRequest actionRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		for (Locale locale :
				_language.getAvailableLocales(themeDisplay.getSiteGroupId())) {

			String languageId = LocaleUtil.toLanguageId(locale);

			List<String> priorities = new ArrayList<>();

			for (int j = 0; j < 10; j++) {
				String name = ParamUtil.getString(
					actionRequest,
					StringBundler.concat("priorityName", j, "_", languageId));
				String image = ParamUtil.getString(
					actionRequest,
					StringBundler.concat("priorityImage", j, "_", languageId));
				double value = ParamUtil.getDouble(
					actionRequest,
					StringBundler.concat("priorityValue", j, "_", languageId));

				if (Validator.isNotNull(name) || Validator.isNotNull(image) ||
					(value != 0.0)) {

					priorities.add(
						StringBundler.concat(
							name, StringPool.PIPE, image, StringPool.PIPE,
							value));
				}
			}

			String preferenceName = _localization.getLocalizedName(
				"priorities", languageId);

			setPreference(
				actionRequest, preferenceName,
				priorities.toArray(new String[0]));
		}
	}

	private void _updateUserRanks(ActionRequest actionRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		for (Locale locale :
				_language.getAvailableLocales(themeDisplay.getSiteGroupId())) {

			String languageId = LocaleUtil.toLanguageId(locale);

			String[] ranks = StringUtil.splitLines(
				ParamUtil.getString(actionRequest, "ranks_" + languageId));

			Map<String, String> map = new TreeMap<>(
				new NaturalOrderStringComparator());

			for (String rank : ranks) {
				if (!_isValidUserRank(rank)) {
					SessionErrors.add(actionRequest, "userRank");

					return;
				}

				String[] kvp = StringUtil.split(rank, CharPool.EQUAL);

				String kvpName = kvp[0];
				String kvpValue = kvp[1];

				map.put(kvpValue, kvpName);
			}

			ranks = new String[map.size()];

			int count = 0;

			for (Map.Entry<String, String> entry : map.entrySet()) {
				String kvpValue = entry.getKey();
				String kvpName = entry.getValue();

				ranks[count++] = kvpName + StringPool.EQUAL + kvpValue;
			}

			String preferenceName = _localization.getLocalizedName(
				"ranks", languageId);

			setPreference(actionRequest, preferenceName, ranks);
		}
	}

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

}