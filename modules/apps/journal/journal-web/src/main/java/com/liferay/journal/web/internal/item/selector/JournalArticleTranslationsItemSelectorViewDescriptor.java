/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.item.selector;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.TableItemView;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.web.internal.util.JournalArticleTranslation;
import com.liferay.journal.web.internal.util.JournalArticleTranslationRowChecker;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

/**
 * @author Barbara Cabrera
 */
public class JournalArticleTranslationsItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<JournalArticleTranslation> {

	public JournalArticleTranslationsItemSelectorViewDescriptor(
		JournalArticleTranslationsItemSelectorCriterion
			journalArticleTranslationsItemSelectorCriterion,
		HttpServletRequest httpServletRequest, PortletURL portletURL) {

		_journalArticleTranslationsItemSelectorCriterion =
			journalArticleTranslationsItemSelectorCriterion;
		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;

		_portletRequest = (PortletRequest)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST);
		_portletResponse = (PortletResponse)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE);
	}

	@Override
	public String getDefaultDisplayStyle() {
		return "list";
	}

	@Override
	public String[] getDisplayViews() {
		return new String[] {"list"};
	}

	@Override
	public ItemDescriptor getItemDescriptor(
		JournalArticleTranslation articleTranslation) {

		return new JournalArticleTranslationsItemDescriptor(articleTranslation);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new UUIDItemSelectorReturnType();
	}

	@Override
	public SearchContainer<JournalArticleTranslation> getSearchContainer()
		throws PortalException {

		if (_articleTranslationsSearchContainer != null) {
			return _articleTranslationsSearchContainer;
		}

		SearchContainer<JournalArticleTranslation>
			articleTranslationsSearchContainer = new SearchContainer<>(
				_portletRequest, _portletURL, null, null);

		articleTranslationsSearchContainer.setId("articleTranslations");

		JournalArticle article =
			JournalArticleLocalServiceUtil.getLatestArticle(
				_journalArticleTranslationsItemSelectorCriterion.getGroupId(),
				_journalArticleTranslationsItemSelectorCriterion.
					getArticleId());

		String keywords = _getKeywords();

		articleTranslationsSearchContainer.setResultsAndTotal(
			TransformUtil.transformToList(
				article.getAvailableLanguageIds(),
				languageId -> {
					JournalArticleTranslation articleTranslation =
						new JournalArticleTranslation(
							StringUtil.equalsIgnoreCase(
								article.getDefaultLanguageId(), languageId),
							LocaleUtil.fromLanguageId(languageId));

					if (Validator.isNotNull(keywords) &&
						!StringUtil.containsIgnoreCase(
							LocaleUtil.getLongDisplayName(
								articleTranslation.getLocale(),
								Collections.emptySet()),
							keywords, StringPool.BLANK)) {

						return null;
					}

					return articleTranslation;
				}));

		articleTranslationsSearchContainer.setRowChecker(
			new JournalArticleTranslationRowChecker(_portletResponse));

		_articleTranslationsSearchContainer =
			articleTranslationsSearchContainer;

		return _articleTranslationsSearchContainer;
	}

	public TableItemView getTableItemView(
		JournalArticleTranslation articleTranslation) {

		return new JournalArticleTranslationsTableItemView(articleTranslation);
	}

	@Override
	public boolean isMultipleSelection() {
		return true;
	}

	@Override
	public boolean isShowBreadcrumb() {
		return false;
	}

	@Override
	public boolean isShowSearch() {
		return true;
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private SearchContainer<JournalArticleTranslation>
		_articleTranslationsSearchContainer;
	private final HttpServletRequest _httpServletRequest;
	private final JournalArticleTranslationsItemSelectorCriterion
		_journalArticleTranslationsItemSelectorCriterion;
	private String _keywords;
	private final PortletRequest _portletRequest;
	private final PortletResponse _portletResponse;
	private final PortletURL _portletURL;

}