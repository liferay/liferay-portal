/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.util;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryServiceUtil;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.util.comparator.EntryDisplayDateComparator;
import com.liferay.blogs.util.comparator.EntryTitleComparator;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.dao.search.SearchContainerResults;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Thiago Moreira
 */
public class BlogsUtil {

	public static final String DISPLAY_STYLE_ABSTRACT = "abstract";

	public static final String DISPLAY_STYLE_FULL_CONTENT = "full-content";

	public static final String DISPLAY_STYLE_TITLE = "title";

	public static int getCommentsCount(BlogsEntry entry) {
		return CommentManagerUtil.getCommentsCount(
			BlogsEntry.class.getName(), entry.getEntryId());
	}

	public static Map<String, String> getEmailDefinitionTerms(
		PortletRequest portletRequest, String emailFromAddress,
		String emailFromName) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return LinkedHashMapBuilder.put(
			"[$BLOGS_ENTRY_CONTENT$]",
			LanguageUtil.get(themeDisplay.getLocale(), "the-blog-entry-content")
		).put(
			"[$BLOGS_ENTRY_CREATE_DATE$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-date-the-blog-entry-was-created")
		).put(
			"[$BLOGS_ENTRY_DESCRIPTION$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-blog-entry-description")
		).put(
			"[$BLOGS_ENTRY_SITE_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-name-of-the-site-where-the-blog-entry-was-created")
		).put(
			"[$BLOGS_ENTRY_STATUS_BY_USER_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-user-who-updated-the-blog-entry")
		).put(
			"[$BLOGS_ENTRY_TITLE$]",
			LanguageUtil.get(themeDisplay.getLocale(), "the-blog-entry-title")
		).put(
			"[$BLOGS_ENTRY_UPDATE_COMMENT$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-comment-of-the-user-who-updated-the-blog-entry")
		).put(
			"[$BLOGS_ENTRY_USER_ADDRESS$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-email-address-of-the-user-who-added-the-blog-entry")
		).put(
			"[$BLOGS_ENTRY_USER_PORTRAIT_URL$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-portrait-url-of-the-user-who-added-the-blog-entry")
		).put(
			"[$BLOGS_ENTRY_USER_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-user-who-added-the-blog-entry")
		).put(
			"[$BLOGS_ENTRY_USER_URL$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-public-site-url-of-the-user-who-added-the-blog-entry")
		).put(
			"[$BLOGS_ENTRY_URL$]",
			LanguageUtil.get(themeDisplay.getLocale(), "the-blog-entry-url")
		).put(
			"[$COMPANY_ID$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-id-associated-with-the-blog")
		).put(
			"[$COMPANY_MX$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-mx-associated-with-the-blog")
		).put(
			"[$COMPANY_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-name-associated-with-the-blog")
		).put(
			"[$FROM_ADDRESS$]", HtmlUtil.escape(emailFromAddress)
		).put(
			"[$FROM_NAME$]", HtmlUtil.escape(emailFromName)
		).put(
			"[$PORTAL_URL$]",
			() -> {
				Company company = themeDisplay.getCompany();

				return company.getVirtualHostname();
			}
		).put(
			"[$PORTLET_NAME$]",
			() -> {
				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				return HtmlUtil.escape(portletDisplay.getTitle());
			}
		).put(
			"[$SITE_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-site-name-associated-with-the-blog")
		).put(
			"[$TO_ADDRESS$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-address-of-the-email-recipient")
		).put(
			"[$TO_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-name-of-the-email-recipient")
		).put(
			"[$UNSUBSCRIBE_URL$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-url-to-unsubscribe-the-user")
		).build();
	}

	public static Map<String, String> getEmailFromDefinitionTerms(
		PortletRequest portletRequest, String emailFromAddress,
		String emailFromName) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return LinkedHashMapBuilder.put(
			"[$BLOGS_ENTRY_USER_ADDRESS$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-email-address-of-the-user-who-added-the-blog-entry")
		).put(
			"[$BLOGS_ENTRY_USER_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-user-who-added-the-blog-entry")
		).put(
			"[$COMPANY_ID$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-id-associated-with-the-blog")
		).put(
			"[$COMPANY_MX$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-mx-associated-with-the-blog")
		).put(
			"[$COMPANY_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-name-associated-with-the-blog")
		).put(
			"[$PORTLET_NAME$]",
			() -> {
				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				return HtmlUtil.escape(portletDisplay.getTitle());
			}
		).put(
			"[$SITE_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-site-name-associated-with-the-blog")
		).build();
	}

	public static OrderByComparator<BlogsEntry> getOrderByComparator(
		String orderByCol, String orderByType) {

		OrderByComparator<BlogsEntry> orderByComparator = null;

		boolean orderByAsc = true;

		if (orderByType.equals("desc")) {
			orderByAsc = false;
		}

		if (orderByCol.equals("display-date")) {
			orderByComparator = EntryDisplayDateComparator.getInstance(
				orderByAsc);
		}
		else {
			orderByComparator = EntryTitleComparator.getInstance(orderByAsc);
		}

		return orderByComparator;
	}

	public static SearchContainerResults<AssetEntry> getSearchContainerResults(
			SearchContainer<?> searchContainer)
		throws PortalException {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery(
			BlogsEntry.class.getName(), searchContainer);

		assetEntryQuery.setEnablePermissions(true);
		assetEntryQuery.setExcludeZeroViewCount(false);
		assetEntryQuery.setOrderByCol1("publishDate");
		assetEntryQuery.setVisible(Boolean.TRUE);

		int total = AssetEntryServiceUtil.getEntriesCount(assetEntryQuery);

		assetEntryQuery.setEnd(searchContainer.getEnd());
		assetEntryQuery.setStart(searchContainer.getStart());

		List<AssetEntry> assetEntries = AssetEntryServiceUtil.getEntries(
			assetEntryQuery);

		return new SearchContainerResults<>(assetEntries, total);
	}

	public static String getUrlTitle(long entryId, String title) {
		if (title == null) {
			return String.valueOf(entryId);
		}

		title = StringUtil.toLowerCase(title.trim());

		if (Validator.isNull(title) || Validator.isNumber(title) ||
			title.equals("rss")) {

			title = String.valueOf(entryId);
		}
		else {
			title = FriendlyURLNormalizerUtil.normalizeWithPeriodsAndSlashes(
				title);
		}

		return ModelHintsUtil.trimString(
			BlogsEntry.class.getName(), "urlTitle", title);
	}

}