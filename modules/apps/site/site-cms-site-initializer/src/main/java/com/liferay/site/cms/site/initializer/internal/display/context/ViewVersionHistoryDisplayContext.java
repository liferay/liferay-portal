/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Mikel Lorza
 */
public class ViewVersionHistoryDisplayContext {

	public ViewVersionHistoryDisplayContext(
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinition objectDefinition, ObjectEntry objectEntry) {

		_httpServletRequest = httpServletRequest;
		_language = language;
		_objectDefinition = objectDefinition;
		_objectEntry = objectEntry;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() throws PortalException {
		return StringBundler.concat(
			"/o", _objectDefinition.getRESTContextPath(), StringPool.SLASH,
			_objectEntry.getObjectEntryId(), "/versions");
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				"{file.link.href}", "download", "download",
				_language.get(_httpServletRequest, "download"), "get", null,
				"link"),
			new FDSActionDropdownItem(
				StringBundler.concat(
					_themeDisplay.getPortalURL(), _themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?objectEntryId={id}",
					"&p_l_mode=read&p_p_state=", LiferayWindowState.POP_UP,
					"&redirect=", _themeDisplay.getURLCurrent()),
				"view", "view-content",
				LanguageUtil.get(_httpServletRequest, "view"), null, null, null,
				HashMapBuilder.<String, Object>put(
					"objectEntryFolderExternalReferenceCode", "L_CONTENTS"
				).build()),
			new FDSActionDropdownItem(
				StringPool.BLANK, "view", "view-file",
				_language.get(_httpServletRequest, "view"), null, null, null,
				HashMapBuilder.<String, Object>put(
					"objectEntryFolderExternalReferenceCode", "L_FILES"
				).build()),
			new FDSActionDropdownItem(
				"{actions.restore.href}", "restore", "restore",
				_language.get(_httpServletRequest, "restore"), "put", "restore",
				null),
			new FDSActionDropdownItem(
				"{actions.expire.href}", "time", "expire",
				_language.get(_httpServletRequest, "expire"), "post", "expire",
				null),
			new FDSActionDropdownItem(
				"{actions.copy.href}", "copy", "copy",
				_language.get(_httpServletRequest, "make-a-copy"), "post",
				"copy", null),
			new FDSActionDropdownItem(
				"{actions.delete.href}", "trash", "delete",
				_language.get(_httpServletRequest, "delete"), "delete",
				"delete", null));
	}

	public Map<String, Object> getProps() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"backURL", ParamUtil.getString(_httpServletRequest, "backURL")
		).put(
			"objectEntryTitle",
			_objectEntry.getTitleValue(_themeDisplay.getLanguageId())
		).put(
			"title",
			StringBundler.concat(
				StringPool.QUOTE,
				_objectEntry.getTitleValue(_themeDisplay.getLanguageId(), true),
				"\" ", _language.get(_themeDisplay.getLocale(), "history"))
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final ObjectDefinition _objectDefinition;
	private final ObjectEntry _objectEntry;
	private final ThemeDisplay _themeDisplay;

}