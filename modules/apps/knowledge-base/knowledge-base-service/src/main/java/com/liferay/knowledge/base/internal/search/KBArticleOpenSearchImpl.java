/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.search;

import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.HitsOpenSearchImpl;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.OpenSearch;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import org.osgi.service.component.annotations.Component;

/**
 * @author Peter Shin
 * @author Brian Wing Shun Chan
 */
@Component(service = OpenSearch.class)
public class KBArticleOpenSearchImpl extends HitsOpenSearchImpl {

	public static final String SEARCH_PATH = "/c/knowledge_base/open_search";

	public static final String TITLE = "Liferay Knowledge Base Search: ";

	@Override
	public String getClassName() {
		return KBArticle.class.getName();
	}

	@Override
	public Indexer<KBArticle> getIndexer() {
		return IndexerRegistryUtil.getIndexer(KBArticle.class);
	}

	@Override
	public String getSearchPath() {
		return SEARCH_PATH;
	}

	@Override
	public String getTitle(String keywords) {
		return TITLE + keywords;
	}

	@Override
	protected String getURL(
		ThemeDisplay themeDisplay, long groupId, Document result,
		PortletURL portletURL) {

		long resourcePrimKey = GetterUtil.getLong(
			result.get(Field.ENTRY_CLASS_PK));
		int status = WorkflowConstants.STATUS_APPROVED;

		WindowState windowState = portletURL.getWindowState();

		return KnowledgeBaseUtil.getKBArticleURL(
			themeDisplay.getPlid(), resourcePrimKey, status,
			themeDisplay.getPortalURL(),
			windowState.equals(LiferayWindowState.MAXIMIZED));
	}

}