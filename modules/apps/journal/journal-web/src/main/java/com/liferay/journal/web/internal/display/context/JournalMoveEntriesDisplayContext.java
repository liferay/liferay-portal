/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleServiceUtil;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.journal.service.JournalFolderServiceUtil;
import com.liferay.journal.web.internal.security.permission.resource.JournalArticlePermission;
import com.liferay.journal.web.internal.security.permission.resource.JournalFolderPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Julio Camarero
 */
public class JournalMoveEntriesDisplayContext {

	public JournalMoveEntriesDisplayContext(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException {

		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);

		processFolders(getMoveFolders());
		processArticles(getMoveArticles());

		setViewAttributes();
	}

	public List<JournalArticle> getInvalidMoveArticles() {
		return _invalidMoveArticles;
	}

	public List<JournalFolder> getInvalidMoveFolders() {
		return _invalidMoveFolders;
	}

	public List<JournalArticle> getMoveArticles() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<JournalArticle> articles = new ArrayList<>();

		String[] articleIds = ParamUtil.getStringValues(
			_liferayPortletRequest, "rowIdsJournalArticle");

		for (String articleId : articleIds) {
			JournalArticle article = JournalArticleServiceUtil.fetchArticle(
				themeDisplay.getScopeGroupId(), articleId);

			if (article != null) {
				articles.add(article);
			}
		}

		return articles;
	}

	public List<JournalFolder> getMoveFolders() throws PortalException {
		long[] folderIds = ParamUtil.getLongValues(
			_liferayPortletRequest, "rowIdsJournalFolder");

		List<JournalFolder> folders = new ArrayList<>();

		for (long folderId : folderIds) {
			JournalFolder folder = JournalFolderServiceUtil.fetchFolder(
				folderId);

			if (folder != null) {
				folders.add(folder);
			}
		}

		return folders;
	}

	public long getNewFolderId() {
		if (_newFolderId > 0) {
			return _newFolderId;
		}

		_newFolderId = ParamUtil.getLong(_liferayPortletRequest, "newFolderId");

		return _newFolderId;
	}

	public String getNewFolderName() throws PortalException {
		if (Validator.isNotNull(_newFolderName)) {
			return _newFolderName;
		}

		if (getNewFolderId() > 0) {
			JournalFolder folder = JournalFolderLocalServiceUtil.getFolder(
				getNewFolderId());

			_newFolderName = folder.getName();
		}
		else {
			_newFolderName = LanguageUtil.get(_httpServletRequest, "home");
		}

		return _newFolderName;
	}

	public PermissionChecker getPermissionChecker() {
		if (_permissionChecker != null) {
			return _permissionChecker;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_permissionChecker = themeDisplay.getPermissionChecker();

		return _permissionChecker;
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_liferayPortletRequest, "redirect");

		return _redirect;
	}

	public List<JournalArticle> getValidMoveArticles() {
		return _validMoveArticles;
	}

	public List<JournalFolder> getValidMoveFolders() {
		return _validMoveFolders;
	}

	public void processArticles(List<JournalArticle> articles)
		throws PortalException {

		_validMoveArticles = new ArrayList<>();
		_invalidMoveArticles = new ArrayList<>();

		for (JournalArticle curArticle : articles) {
			boolean hasUpdatePermission = JournalArticlePermission.contains(
				getPermissionChecker(), curArticle, ActionKeys.UPDATE);

			if (hasUpdatePermission) {
				_validMoveArticles.add(curArticle);
			}
			else {
				_invalidMoveArticles.add(curArticle);
			}
		}
	}

	public void processFolders(List<JournalFolder> folders)
		throws PortalException {

		_validMoveFolders = new ArrayList<>();
		_invalidMoveFolders = new ArrayList<>();

		for (JournalFolder curFolder : folders) {
			boolean hasUpdatePermission = JournalFolderPermission.contains(
				getPermissionChecker(), curFolder, ActionKeys.UPDATE);

			if (hasUpdatePermission) {
				_validMoveFolders.add(curFolder);
			}
			else {
				_invalidMoveFolders.add(curFolder);
			}
		}
	}

	public void setViewAttributes() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(getRedirect());
		portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

		if (_liferayPortletResponse instanceof RenderResponse) {
			RenderResponse renderResponse =
				(RenderResponse)_liferayPortletResponse;

			renderResponse.setTitle(
				LanguageUtil.get(_httpServletRequest, "move-web-content"));
		}
	}

	private final HttpServletRequest _httpServletRequest;
	private List<JournalArticle> _invalidMoveArticles;
	private List<JournalFolder> _invalidMoveFolders;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private long _newFolderId;
	private String _newFolderName;
	private PermissionChecker _permissionChecker;
	private String _redirect;
	private List<JournalArticle> _validMoveArticles;
	private List<JournalFolder> _validMoveFolders;

}