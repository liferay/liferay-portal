/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.util;

import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.journal.util.JournalHelper;
import com.liferay.journal.util.comparator.ArticleCreateDateComparator;
import com.liferay.journal.util.comparator.ArticleDisplayDateComparator;
import com.liferay.journal.util.comparator.ArticleIDComparator;
import com.liferay.journal.util.comparator.ArticleModifiedDateComparator;
import com.liferay.journal.util.comparator.ArticleReviewDateComparator;
import com.liferay.journal.util.comparator.ArticleTitleComparator;
import com.liferay.journal.util.comparator.ArticleVersionComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryBuilder;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryListBuilder;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Wesley Gong
 * @author Angelo Jefferson
 * @author Hugo Huijser
 * @author Eduardo García
 */
public class JournalPortletUtil {

	public static String getAddMenuFavItemKey(
			JournalHelper journalHelper, PortletRequest portletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long folderId = ParamUtil.getLong(portletRequest, "folderId");

		String key =
			"journal-add-menu-fav-items-" + themeDisplay.getScopeGroupId();

		folderId = _getAddMenuFavItemFolderId(folderId, journalHelper);

		if (folderId <= 0) {
			return key;
		}

		return key + StringPool.DASH + folderId;
	}

	public static OrderByComparator<JournalArticle> getArticleOrderByComparator(
		String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<JournalArticle> orderByComparator = null;

		if (orderByCol.equals("create-date")) {
			orderByComparator = ArticleCreateDateComparator.getInstance(
				orderByAsc);
		}
		else if (orderByCol.equals("display-date")) {
			orderByComparator = ArticleDisplayDateComparator.getInstance(
				orderByAsc);
		}
		else if (orderByCol.equals("id")) {
			orderByComparator = ArticleIDComparator.getInstance(orderByAsc);
		}
		else if (orderByCol.equals("modified-date")) {
			orderByComparator = ArticleModifiedDateComparator.getInstance(
				orderByAsc);
		}
		else if (orderByCol.equals("review-date")) {
			orderByComparator = ArticleReviewDateComparator.getInstance(
				orderByAsc);
		}
		else if (orderByCol.equals("title")) {
			orderByComparator = ArticleTitleComparator.getInstance(orderByAsc);
		}
		else if (orderByCol.equals("version")) {
			orderByComparator = ArticleVersionComparator.getInstance(
				orderByAsc);
		}

		return orderByComparator;
	}

	public static String getEditArticlePortletURL(
		JournalArticle article, HttpServletRequest httpServletRequest,
		PortletDisplay portletDisplay, String redirect,
		String referringPortletResource) {

		return PortletURLBuilder.create(
			_getPortletURL(httpServletRequest)
		).setMVCRenderCommandName(
			"/journal/edit_article"
		).setRedirect(
			redirect
		).setParameter(
			"articleId", article.getArticleId()
		).setParameter(
			"backURLTitle", portletDisplay.getPortletDisplayName()
		).setParameter(
			"folderId", article.getFolderId()
		).setParameter(
			"groupId", article.getGroupId()
		).setParameter(
			"referringPortletResource", referringPortletResource
		).setParameter(
			"version", article.getVersion()
		).buildString();
	}

	public static List<BreadcrumbEntry> getPortletBreadcrumbEntries(
		JournalFolder folder, HttpServletRequest httpServletRequest,
		boolean lastElementLinkable,
		LiferayPortletResponse liferayPortletResponse) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(
					LanguageUtil.get(httpServletRequest, "home"));

				if ((folder != null) || lastElementLinkable) {
					breadcrumbEntry.setURL(
						PortletURLBuilder.createRenderURL(
							liferayPortletResponse
						).buildString());
				}
			}
		).addAll(
			() -> folder != null,
			() -> {
				List<JournalFolder> ancestorFolders = folder.getAncestors();

				Collections.reverse(ancestorFolders);

				return TransformUtil.transform(
					ancestorFolders,
					ancestorFolder -> {
						if (permissionChecker.hasPermission(
								ancestorFolder.getGroupId(),
								JournalFolder.class.getName(),
								ancestorFolder.getFolderId(),
								ActionKeys.VIEW)) {

							return BreadcrumbEntryBuilder.setTitle(
								ancestorFolder.getName()
							).setURL(
								PortletURLBuilder.createRenderURL(
									liferayPortletResponse
								).setParameter(
									"folderId", ancestorFolder.getFolderId()
								).buildString()
							).build();
						}

						return BreadcrumbEntryBuilder.setTitle(
							StringPool.TRIPLE_PERIOD
						).build();
					});
			}
		).add(
			() -> folder != null,
			breadcrumbEntry -> {
				if (permissionChecker.hasPermission(
						folder.getGroupId(), JournalFolder.class.getName(),
						folder.getFolderId(), ActionKeys.VIEW)) {

					JournalFolder unescapedFolder = folder.toUnescapedModel();

					breadcrumbEntry.setTitle(unescapedFolder.getName());

					if (lastElementLinkable) {
						breadcrumbEntry.setURL(
							PortletURLBuilder.createRenderURL(
								liferayPortletResponse
							).setParameter(
								"folderId", folder.getFolderId()
							).buildString());
					}
				}
				else {
					breadcrumbEntry.setTitle(StringPool.TRIPLE_PERIOD);
				}
			}
		).build();
	}

	private static long _getAddMenuFavItemFolderId(
			long folderId, JournalHelper journalHelper)
		throws PortalException {

		if (folderId <= 0) {
			return 0;
		}

		JournalFolder folder = JournalFolderLocalServiceUtil.fetchFolder(
			folderId);

		while (folder != null) {
			int restrictionType = journalHelper.getRestrictionType(
				folder.getFolderId());

			if (restrictionType ==
					JournalFolderConstants.
						RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW) {

				return folder.getFolderId();
			}

			folder = folder.getParentFolder();
		}

		return 0;
	}

	private static PortletURL _getPortletURL(
		HttpServletRequest httpServletRequest) {

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletURL portletURL = PortalUtil.getControlPanelPortletURL(
			portletRequest, themeDisplay.getScopeGroup(),
			JournalPortletKeys.JOURNAL, 0, 0, PortletRequest.RENDER_PHASE);

		try {
			portletURL.setWindowState(portletRequest.getWindowState());
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		return portletURL;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalPortletUtil.class);

}