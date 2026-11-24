/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.sidebar.panel;

import com.liferay.layout.content.page.editor.sidebar.panel.ContentPageEditorSidebarPanel;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "service.ranking:Integer=100",
	service = ContentPageEditorSidebarPanel.class
)
public class CommentsContentPageEditorSidebarPanel
	implements ContentPageEditorSidebarPanel {

	@Override
	public String getIcon() {
		return "comments";
	}

	@Override
	public String getId() {
		return "comments";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "comments");
	}

	@Override
	public boolean isVisible(
		PermissionChecker permissionChecker, long plid, int layoutType) {

		try {
			Layout layout = _layoutLocalService.getLayout(plid);

			if (layout.getLaunchSetId() > 0) {
				return false;
			}

			if (_layoutPermission.containsLayoutUpdatePermission(
					permissionChecker, layout)) {

				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommentsContentPageEditorSidebarPanel.class);

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPermission _layoutPermission;

}