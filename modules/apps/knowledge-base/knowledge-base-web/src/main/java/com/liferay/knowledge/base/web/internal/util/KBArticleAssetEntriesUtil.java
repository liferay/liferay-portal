/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.util;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetEntryServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagServiceUtil;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;

import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Peter Shin
 */
public class KBArticleAssetEntriesUtil {

	public static List<AssetEntry> getAssetEntries(
			long[] groupIds, long[] classNameIds, long[] assetTagIds,
			long resourcePrimKey, int start, int end, String orderByColumn)
		throws PortalException {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setAnyTagIds(assetTagIds);
		assetEntryQuery.setClassNameIds(classNameIds);
		assetEntryQuery.setEnablePermissions(true);
		assetEntryQuery.setEnd(end + 1);
		assetEntryQuery.setGroupIds(groupIds);
		assetEntryQuery.setOrderByCol1(orderByColumn);
		assetEntryQuery.setStart(start);

		List<AssetEntry> assetEntries = ListUtil.copy(
			AssetEntryServiceUtil.getEntries(assetEntryQuery));

		AssetEntry assetEntry = null;

		for (AssetEntry curAssetEntry : assetEntries) {
			if (curAssetEntry.getClassPK() == resourcePrimKey) {
				assetEntry = curAssetEntry;
			}
		}

		assetEntries.remove(assetEntry);

		return ListUtil.subList(assetEntries, 0, 10);
	}

	public static long[] getAssetTagIds(long[] groupIds, KBArticle kbArticle)
		throws PortalException {

		List<AssetTag> assetTags = AssetTagServiceUtil.getTags(
			KBArticle.class.getName(), kbArticle.getClassPK());

		long[] tagIds = AssetTagLocalServiceUtil.getTagIds(
			groupIds, StringUtil.split(ListUtil.toString(assetTags, "name")));

		Set<Long> filteredTagIds = new LinkedHashSet<>();

		for (long tagId : tagIds) {
			try {
				AssetTagServiceUtil.getTag(tagId);
			}
			catch (PrincipalException principalException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(principalException);
				}

				continue;
			}

			filteredTagIds.add(tagId);
		}

		return StringUtil.split(StringUtil.merge(filteredTagIds), 0L);
	}

	public static long[] getGroupIds(Group companyGroup, KBArticle kbArticle) {
		return new long[] {kbArticle.getGroupId(), companyGroup.getGroupId()};
	}

	public static String getURL(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
			AssetRendererFactory<?> assetRendererFactory,
			AssetRenderer<?> assetRenderer)
		throws Exception {

		long classPK = assetRenderer.getClassPK();

		String className = assetRendererFactory.getClassName();

		String portletId = PortletProviderUtil.getPortletId(
			className, PortletProvider.Action.VIEW);

		PortletURL portletURL = null;

		if (className.equals(BlogsEntry.class.getName())) {
			portletURL = PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					httpServletRequest, portletId, PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/blogs/view_entry"
			).setParameter(
				"entryId", classPK
			).buildPortletURL();
		}
		else if (className.equals(JournalArticle.class.getName())) {
			JournalArticle journalArticle =
				JournalArticleLocalServiceUtil.getLatestArticle(classPK);

			portletURL = PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					httpServletRequest, portletId, PortletRequest.RENDER_PHASE)
			).setParameter(
				"articleId", journalArticle.getArticleId()
			).setParameter(
				"groupId", journalArticle.getGroupId()
			).setParameter(
				"struts_action", "/journal_content/view"
			).buildPortletURL();
		}
		else if (className.equals(KBArticle.class.getName())) {
			portletURL = PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					httpServletRequest,
					KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/knowledge_base/view_kb_article"
			).setParameter(
				"resourcePrimKey", classPK
			).buildPortletURL();
		}
		else if (className.equals(MBMessage.class.getName())) {
			portletURL = PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					httpServletRequest, portletId, PortletRequest.RENDER_PHASE)
			).setParameter(
				"messageId", classPK
			).setParameter(
				"struts_action", "/message_boards/view_message"
			).buildPortletURL();
		}
		else if (className.equals(WikiPage.class.getName())) {
			WikiPage wikiPage = WikiPageLocalServiceUtil.getPage(classPK);

			portletURL = PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					httpServletRequest, portletId, PortletRequest.RENDER_PHASE)
			).setParameter(
				"nodeId", wikiPage.getNodeId()
			).setParameter(
				"struts_action", "/wiki/view"
			).setParameter(
				"title", wikiPage.getTitle()
			).buildPortletURL();
		}

		String currentURL = PortalUtil.getCurrentURL(httpServletRequest);

		if (portletURL == null) {
			return currentURL;
		}

		portletURL.setWindowState(WindowState.MAXIMIZED);
		portletURL.setPortletMode(PortletMode.VIEW);
		portletURL.setParameter("returnToFullPageURL", currentURL);

		return portletURL.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KBArticleAssetEntriesUtil.class);

}