/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.web.internal.security.permission.resource.JournalArticlePermission;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class JournalHistoryManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public JournalHistoryManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, JournalArticle article,
		JournalHistoryDisplayContext journalHistoryDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			journalHistoryDisplayContext.getArticleSearchContainer());

		_article = article;
		_journalHistoryDisplayContext = journalHistoryDisplayContext;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> JournalArticlePermission.contains(
							themeDisplay.getPermissionChecker(), _article,
							ActionKeys.EXPIRE),
						dropdownItem -> {
							dropdownItem.putData("action", "expireArticles");
							dropdownItem.putData(
								"expireArticlesURL",
								PortletURLBuilder.createActionURL(
									liferayPortletResponse
								).setActionName(
									"/journal/expire_articles"
								).setRedirect(
									themeDisplay.getURLCurrent()
								).buildString());
							dropdownItem.setIcon("time");
							dropdownItem.setLabel(
								LanguageUtil.get(httpServletRequest, "expire"));
							dropdownItem.setQuickAction(true);
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> JournalArticlePermission.contains(
							themeDisplay.getPermissionChecker(), _article,
							ActionKeys.DELETE),
						dropdownItem -> {
							dropdownItem.putData("action", "deleteArticles");
							dropdownItem.putData(
								"deleteArticlesURL",
								PortletURLBuilder.createActionURL(
									liferayPortletResponse
								).setActionName(
									"/journal/delete_articles"
								).setRedirect(
									themeDisplay.getURLCurrent()
								).buildString());
							dropdownItem.setIcon("trash");
							dropdownItem.setLabel(
								LanguageUtil.get(httpServletRequest, "delete"));
							dropdownItem.setQuickAction(true);
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	public String getAvailableActions(JournalArticle article)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<String> availableActions = new ArrayList<>();

		if (JournalArticlePermission.contains(
				themeDisplay.getPermissionChecker(), article,
				ActionKeys.DELETE)) {

			availableActions.add("deleteArticles");
		}

		if (JournalArticlePermission.contains(
				themeDisplay.getPermissionChecker(), article,
				ActionKeys.EXPIRE) &&
			(article.getStatus() == WorkflowConstants.STATUS_APPROVED)) {

			availableActions.add("expireArticles");
		}

		return StringUtil.merge(availableActions, StringPool.COMMA);
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	@Override
	public String getComponentId() {
		return "journalHistoryManagementToolbar";
	}

	@Override
	public String getSearchContainerId() {
		return "articleVersions";
	}

	@Override
	protected String getDisplayStyle() {
		return _journalHistoryDisplayContext.getDisplayStyle();
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list", "descriptive", "icon"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"version", "display-date", "modified-date"};
	}

	private final JournalArticle _article;
	private final JournalHistoryDisplayContext _journalHistoryDisplayContext;

}