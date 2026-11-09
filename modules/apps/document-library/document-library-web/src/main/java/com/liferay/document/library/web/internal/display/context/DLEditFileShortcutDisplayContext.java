/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.file.criterion.FileItemSelectorCriterion;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.WebKeys;

/**
 * @author Cristina González
 */
public class DLEditFileShortcutDisplayContext {

	public DLEditFileShortcutDisplayContext(
		DLAppService dlAppService, ItemSelector itemSelector, Language language,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_dlAppService = dlAppService;
		_itemSelector = itemSelector;
		_language = language;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
	}

	public String getEditFileShortcutURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/document_library/edit_file_shortcut"
		).setCMD(
			() -> {
				if (_getFileShortcut() == null) {
					return Constants.ADD;
				}

				return Constants.UPDATE;
			}
		).buildString();
	}

	public long getFileShortcutId() {
		return BeanParamUtil.getLong(
			_getFileShortcut(), _liferayPortletRequest, "fileShortcutId");
	}

	public long getFolderId() {
		return BeanParamUtil.getLong(
			_getFileShortcut(), _liferayPortletRequest, "folderId");
	}

	public String getItemSelectorURL() {
		FileItemSelectorCriterion fileItemSelectorCriterion =
			new FileItemSelectorCriterion();

		fileItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new FileEntryItemSelectorReturnType());

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(_liferayPortletRequest);

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory,
				_liferayPortletResponse.getNamespace() +
					"toFileEntrySelectedItem",
				fileItemSelectorCriterion));
	}

	public long getRepositoryId() {
		return BeanParamUtil.getLong(
			_getFileShortcut(), _liferayPortletRequest, "repositoryId");
	}

	public String getTitle() {
		FileShortcut fileShortcut = _getFileShortcut();

		if (fileShortcut != null) {
			return _language.format(
				_liferayPortletRequest.getHttpServletRequest(), "shortcut-to-x",
				fileShortcut.getToTitle(), false);
		}

		return _language.get(
			_liferayPortletRequest.getHttpServletRequest(),
			"new-file-shortcut");
	}

	public long getToFileEntryId() {
		return BeanParamUtil.getLong(
			_getFileShortcut(), _liferayPortletRequest, "toFileEntryId");
	}

	public String getToFileEntryTitle() {
		long toFileEntryId = getToFileEntryId();

		if (toFileEntryId > 0) {
			try {
				FileEntry fileEntry = _dlAppService.getFileEntry(toFileEntryId);

				return fileEntry.getTitle();
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		return StringPool.BLANK;
	}

	public boolean isPermissionConfigurable() {
		if (_getFileShortcut() == null) {
			return true;
		}

		return false;
	}

	private FileShortcut _getFileShortcut() {
		return (FileShortcut)_liferayPortletRequest.getAttribute(
			WebKeys.DOCUMENT_LIBRARY_FILE_SHORTCUT);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLEditFileShortcutDisplayContext.class);

	private final DLAppService _dlAppService;
	private final ItemSelector _itemSelector;
	private final Language _language;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;

}