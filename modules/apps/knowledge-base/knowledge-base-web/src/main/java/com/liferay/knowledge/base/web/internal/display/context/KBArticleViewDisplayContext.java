/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.knowledge.base.configuration.KBServiceConfigurationProvider;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleServiceUtil;
import com.liferay.knowledge.base.service.KBFolderServiceUtil;
import com.liferay.knowledge.base.web.internal.util.KBDropdownItemsProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.Date;
import java.util.List;

/**
 * @author Alicia García
 */
public class KBArticleViewDisplayContext {

	public KBArticleViewDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		RenderResponse renderResponse, TrashHelper trashHelper) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;

		_kbDropdownItemsProvider = new KBDropdownItemsProvider(
			liferayPortletRequest, liferayPortletResponse, trashHelper);
	}

	public int getChildKBArticlesCount(long groupId, KBArticle kbArticle) {
		return KBArticleServiceUtil.getKBArticlesCount(
			groupId, kbArticle.getResourcePrimKey(),
			WorkflowConstants.STATUS_ANY);
	}

	public String getChildKBArticlesURLString(
		String currentURL, KBArticle kbArticle) {

		return PortletURLBuilder.create(
			_renderResponse.createRenderURL()
		).setMVCPath(
			"/admin/view_kb_articles.jsp"
		).setRedirect(
			currentURL
		).setParameter(
			"parentResourceClassNameId", kbArticle.getClassNameId()
		).setParameter(
			"parentResourcePrimKey", kbArticle.getResourcePrimKey()
		).setParameter(
			"resourceClassNameId", kbArticle.getClassNameId()
		).setParameter(
			"resourcePrimKey", kbArticle.getResourcePrimKey()
		).setParameter(
			"selectedItemId", kbArticle.getResourcePrimKey()
		).buildString();
	}

	public List<DropdownItem> getKBArticleDropdownItems(KBArticle kbArticle) {
		return _kbDropdownItemsProvider.getKBArticleDropdownItems(kbArticle);
	}

	public int getKBFolderKBArticlesCount(long groupId, long kbFolderId)
		throws PortalException {

		int foldersAndArticlesCount =
			KBFolderServiceUtil.getKBFoldersAndKBArticlesCount(
				groupId, kbFolderId, WorkflowConstants.STATUS_ANY);

		return foldersAndArticlesCount -
			KBFolderServiceUtil.getKBFoldersCount(groupId, kbFolderId);
	}

	public int getKBFoldersCount(long groupId, long kbFolderId)
		throws PortalException {

		return KBFolderServiceUtil.getKBFoldersCount(groupId, kbFolderId);
	}

	public String getModifiedDateDescription(KBArticle kbArticle) {
		Date modifiedDate = kbArticle.getModifiedDate();

		return LanguageUtil.getTimeDescription(
			_httpServletRequest,
			System.currentTimeMillis() - modifiedDate.getTime(), true);
	}

	public boolean isExpiringSoon(KBArticle kbArticle)
		throws ConfigurationException {

		Date expirationDate = kbArticle.getExpirationDate();

		if (kbArticle.isDraft() || kbArticle.isExpired() ||
			kbArticle.isPending() || kbArticle.isScheduled() ||
			(expirationDate == null)) {

			return false;
		}

		Instant instant = expirationDate.toInstant();

		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

		LocalDateTime expirationDateLocalDateTime =
			zonedDateTime.toLocalDateTime();

		LocalDateTime nowLocalDateTime = LocalDateTime.now();

		KBServiceConfigurationProvider kbServiceConfigurationProvider =
			_kbServiceConfigurationProviderSnapshot.get();

		return nowLocalDateTime.isAfter(
			expirationDateLocalDateTime.minusWeeks(
				kbServiceConfigurationProvider.
					getExpirationDateNotificationDateWeeks()));
	}

	private static final Snapshot<KBServiceConfigurationProvider>
		_kbServiceConfigurationProviderSnapshot = new Snapshot<>(
			KBArticleViewDisplayContext.class,
			KBServiceConfigurationProvider.class);

	private final HttpServletRequest _httpServletRequest;
	private final KBDropdownItemsProvider _kbDropdownItemsProvider;
	private final RenderResponse _renderResponse;

}