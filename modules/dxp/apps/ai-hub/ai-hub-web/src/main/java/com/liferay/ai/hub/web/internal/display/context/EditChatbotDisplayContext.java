/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.ai.hub.util.AccountEntryUtil;
import com.liferay.ai.hub.web.internal.util.DisplayContextUtil;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author José Abelenda
 */
public class EditChatbotDisplayContext {

	public EditChatbotDisplayContext(
		HttpServletRequest httpServletRequest, Language language,
		ObjectFieldSettingLocalService objectFieldSettingLocalService) {

		_httpServletRequest = httpServletRequest;
		_language = language;
		_objectFieldSettingLocalService = objectFieldSettingLocalService;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getReactData() throws Exception {
		ObjectField objectField = _getAvatarObjectField();

		String acceptedFileExtensions = "";
		long maximumFileSize = 0;

		if (objectField != null) {
			acceptedFileExtensions = ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS,
				objectField);

			ObjectFieldSetting objectFieldSetting =
				_objectFieldSettingLocalService.fetchObjectFieldSetting(
					objectField.getObjectFieldId(),
					ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE);

			maximumFileSize = GetterUtil.getLong(objectFieldSetting.getValue());
		}

		return HashMapBuilder.<String, Object>put(
			"accountEntryExternalReferenceCode",
			() -> {
				AccountEntry accountEntry =
					AccountEntryUtil.getUserAccountEntry(
						_themeDisplay.getUserId());

				if (accountEntry == null) {
					return null;
				}

				return accountEntry.getExternalReferenceCode();
			}
		).put(
			"avatarAcceptedFileExtensions", acceptedFileExtensions
		).put(
			"avatarMaximumFileSize", maximumFileSize
		).put(
			"avatarMaximumFileSizeLabel",
			_language.formatStorageSize(
				maximumFileSize, _themeDisplay.getLocale())
		).put(
			"avatarUploadTip",
			_language.format(
				_themeDisplay.getLocale(), "upload-a-x-no-larger-than-x",
				new Object[] {
					acceptedFileExtensions,
					_language.formatStorageSize(
						maximumFileSize, _themeDisplay.getLocale())
				})
		).put(
			"backURL",
			() -> {
				String backURL = PortalUtil.escapeRedirect(
					_httpServletRequest.getParameter("backURL"));

				if (Validator.isNotNull(backURL)) {
					return backURL;
				}

				return DisplayContextUtil.getAIHubURL(_themeDisplay) +
					"/chatbots";
			}
		).put(
			"externalReferenceCode",
			_httpServletRequest.getParameter("externalReferenceCode")
		).put(
			"portalURL", _themeDisplay.getPortalURL()
		).put(
			"readOnly",
			DisplayContextUtil.isReadOnly(
				_themeDisplay.getCompanyId(),
				_httpServletRequest.getParameter("externalReferenceCode"),
				"L_AI_HUB_CHATBOT")
		).build();
	}

	private ObjectField _getAvatarObjectField() {
		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_CHATBOT", _themeDisplay.getCompanyId());

		if (objectDefinition == null) {
			return null;
		}

		return ObjectFieldLocalServiceUtil.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), "avatar");
	}

	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final ObjectFieldSettingLocalService
		_objectFieldSettingLocalService;
	private final ThemeDisplay _themeDisplay;

}