/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.display.context;

import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleServiceUtil;
import com.liferay.knowledge.base.service.KBFolderServiceUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Sergio González
 */
public class KBAdminViewDisplayContext {

	public KBAdminViewDisplayContext(
		long parentResourceClassNameId, long parentResourcePrimKey,
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_parentResourceClassNameId = parentResourceClassNameId;
		_parentResourcePrimKey = parentResourcePrimKey;
		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
	}

	public void populatePortletBreadcrumbEntries(PortletURL portletURL)
		throws Exception {

		_populatePortletBreadcrumbEntries(
			_parentResourceClassNameId, _parentResourcePrimKey, portletURL);
	}

	private void _populatePortletBreadcrumbEntries(
			long parentResourceClassNameId, long parentResourcePrimKey,
			PortletURL portletURL)
		throws Exception {

		long kbFolderClassNameId = PortalUtil.getClassNameId(
			KBFolderConstants.getClassName());

		if (parentResourcePrimKey ==
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			PortalUtil.addPortletBreadcrumbEntry(
				_httpServletRequest, themeDisplay.translate("home"),
				String.valueOf(_liferayPortletResponse.createRenderURL()));
		}
		else if (parentResourceClassNameId == kbFolderClassNameId) {
			KBFolder kbFolder = KBFolderServiceUtil.getKBFolder(
				parentResourcePrimKey);

			_populatePortletBreadcrumbEntries(
				kbFolder.getClassNameId(), kbFolder.getParentKBFolderId(),
				portletURL);

			PortalUtil.addPortletBreadcrumbEntry(
				_httpServletRequest, kbFolder.getName(),
				PortletURLBuilder.create(
					PortletURLUtil.clone(portletURL, _liferayPortletResponse)
				).setMVCPath(
					"/admin/view_kb_folders.jsp"
				).setParameter(
					"parentResourceClassNameId", parentResourceClassNameId
				).setParameter(
					"parentResourcePrimKey", parentResourcePrimKey
				).setParameter(
					"selectedItemId", parentResourcePrimKey
				).buildString());
		}
		else {
			KBArticle kbArticle = KBArticleServiceUtil.getLatestKBArticle(
				parentResourcePrimKey, WorkflowConstants.STATUS_ANY);

			_populatePortletBreadcrumbEntries(
				kbArticle.getParentResourceClassNameId(),
				kbArticle.getParentResourcePrimKey(), portletURL);

			PortalUtil.addPortletBreadcrumbEntry(
				_httpServletRequest, kbArticle.getTitle(),
				PortletURLBuilder.create(
					PortletURLUtil.clone(portletURL, _liferayPortletResponse)
				).setParameter(
					"parentResourceClassNameId", parentResourceClassNameId
				).setParameter(
					"parentResourcePrimKey", parentResourcePrimKey
				).setParameter(
					"selectedItemId", parentResourcePrimKey
				).buildString());
		}
	}

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final long _parentResourceClassNameId;
	private final long _parentResourcePrimKey;

}