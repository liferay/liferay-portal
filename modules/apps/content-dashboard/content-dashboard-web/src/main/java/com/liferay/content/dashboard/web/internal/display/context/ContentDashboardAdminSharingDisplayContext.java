/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.display.context;

import com.liferay.content.dashboard.item.ContentDashboardItem;
import com.liferay.content.dashboard.item.ContentDashboardItemFactory;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItemFactoryRegistry;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Cristina González
 */
public class ContentDashboardAdminSharingDisplayContext {

	public ContentDashboardAdminSharingDisplayContext(
		ContentDashboardItemFactoryRegistry contentDashboardItemFactoryRegistry,
		HttpServletRequest httpServletRequest,
		InfoSearchClassMapperRegistry infoSearchClassMapperRegistry) {

		_contentDashboardItemFactoryRegistry =
			contentDashboardItemFactoryRegistry;
		_httpServletRequest = httpServletRequest;
		_infoSearchClassMapperRegistry = infoSearchClassMapperRegistry;
	}

	public String getClassName() {
		return _infoSearchClassMapperRegistry.getSearchClassName(
			_getClassName());
	}

	public long getClassPK() {
		return ParamUtil.getLong(_httpServletRequest, "classPK");
	}

	public boolean isSharingButtonVisible() throws PortalException {
		ContentDashboardItemFactory<?> contentDashboardItemFactory =
			_contentDashboardItemFactoryRegistry.getContentDashboardItemFactory(
				_getClassName());

		if (contentDashboardItemFactory == null) {
			return false;
		}

		ContentDashboardItem<?> contentDashboardItem = _toContentDashboardItem(
			contentDashboardItemFactory, getClassPK());

		if (contentDashboardItem == null) {
			return false;
		}

		ContentDashboardItemAction contentDashboardItemAction =
			_getSharingContentDashboardItemAction(contentDashboardItem);

		if (contentDashboardItemAction.getURL() != null) {
			return true;
		}

		return false;
	}

	public boolean isSharingCollaboratorsVisible() throws PortalException {
		ContentDashboardItemFactory<?> contentDashboardItemFactory =
			_contentDashboardItemFactoryRegistry.getContentDashboardItemFactory(
				_getClassName());

		if (contentDashboardItemFactory == null) {
			return false;
		}

		ContentDashboardItem<?> contentDashboardItem = _toContentDashboardItem(
			contentDashboardItemFactory, getClassPK());

		if (contentDashboardItem == null) {
			return false;
		}

		ContentDashboardItemAction contentDashboardItemAction =
			_getSharingCollaboratorsContentDashboardItemAction(
				contentDashboardItem);

		if (contentDashboardItemAction.getURL() != null) {
			return true;
		}

		return false;
	}

	private String _getClassName() {
		return ParamUtil.getString(_httpServletRequest, "className");
	}

	private ContentDashboardItemAction
		_getSharingCollaboratorsContentDashboardItemAction(
			ContentDashboardItem<?> contentDashboardItem) {

		List<ContentDashboardItemAction> contentDashboardItemActions =
			contentDashboardItem.getContentDashboardItemActions(
				_httpServletRequest,
				ContentDashboardItemAction.Type.SHARING_COLLABORATORS);

		if (ListUtil.isNotEmpty(contentDashboardItemActions)) {
			return contentDashboardItemActions.get(0);
		}

		return null;
	}

	private ContentDashboardItemAction _getSharingContentDashboardItemAction(
		ContentDashboardItem<?> contentDashboardItem) {

		List<ContentDashboardItemAction> contentDashboardItemActions =
			contentDashboardItem.getContentDashboardItemActions(
				_httpServletRequest,
				ContentDashboardItemAction.Type.SHARING_BUTTON);

		if (ListUtil.isNotEmpty(contentDashboardItemActions)) {
			return contentDashboardItemActions.get(0);
		}

		return null;
	}

	private ContentDashboardItem<?> _toContentDashboardItem(
		ContentDashboardItemFactory<?> contentDashboardItemFactory,
		long classPK) {

		try {
			return contentDashboardItemFactory.create(
				GetterUtil.getLong(classPK));
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentDashboardAdminSharingDisplayContext.class);

	private final ContentDashboardItemFactoryRegistry
		_contentDashboardItemFactoryRegistry;
	private final HttpServletRequest _httpServletRequest;
	private final InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;

}