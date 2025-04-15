/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.trash;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFileShortcutConstants;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.trash.BaseTrashRenderer;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Adolfo Pérez
 */
public class DLFileShortcutTrashRenderer extends BaseTrashRenderer {

	public DLFileShortcutTrashRenderer(
		DLFileShortcut dlFileShortcut, TrashHelper trashHelper) {

		_dlFileShortcut = dlFileShortcut;
		_trashHelper = trashHelper;
	}

	@Override
	public String getClassName() {
		return DLFileShortcutConstants.getClassName();
	}

	@Override
	public long getClassPK() {
		return _dlFileShortcut.getFileShortcutId();
	}

	@Override
	public String getPortletId() {
		return DLPortletKeys.DOCUMENT_LIBRARY;
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return getTitle(null);
	}

	@Override
	public String getTitle(Locale locale) {
		return _trashHelper.getOriginalTitle(_dlFileShortcut.getToTitle());
	}

	@Override
	public String getType() {
		return "shortcut";
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			DLFileEntryConstants.getClassName());

		TrashRenderer trashRenderer = trashHandler.getTrashRenderer(
			_dlFileShortcut.getToFileEntryId());

		return trashRenderer.include(
			httpServletRequest, httpServletResponse, template);
	}

	private final DLFileShortcut _dlFileShortcut;
	private final TrashHelper _trashHelper;

}