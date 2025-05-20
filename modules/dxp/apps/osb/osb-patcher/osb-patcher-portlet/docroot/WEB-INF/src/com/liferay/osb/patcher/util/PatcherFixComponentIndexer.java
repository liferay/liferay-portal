/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.BaseAlloyIndexer;
import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Locale;

import javax.portlet.PortletURL;

/**
 * @author Zsolt Balogh
 */
public class PatcherFixComponentIndexer extends BaseAlloyIndexer {

	public static PatcherFixComponentIndexer getInstance() {
		return _instance;
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		long entryClassPK = GetterUtil.getLong(
			searchContext.getAttribute(Field.ENTRY_CLASS_PK));

		if (entryClassPK > 0) {
			searchQuery.addRequiredTerm(Field.ENTRY_CLASS_PK, entryClassPK);
		}

		addSearchTerm(searchQuery, searchContext, "name", true);
	}

	@Override
	protected Document doGetDocument(Object obj) throws Exception {
		PatcherFixComponent patcherFixComponent = (PatcherFixComponent)obj;

		Document document = getBaseModelDocument(
			portletId, patcherFixComponent);

		document.addText("name", patcherFixComponent.getName());
		document.addKeyword("name_sortable", patcherFixComponent.getName());

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletURL portletURL) {

		String title = document.get(Field.ENTRY_CLASS_PK);

		String content = null;

		portletURL.setParameter(
			"mvcPath",
			"/WEB-INF/jsp/osb_patcher/views/fix_components/view.jsp");

		String patcherFixComponentId = document.get(Field.ENTRY_CLASS_PK);

		portletURL.setParameter("id", patcherFixComponentId);

		return new Summary(title, content, portletURL);
	}

	private PatcherFixComponentIndexer() {
		setClassName(PatcherFixComponent.class.getName());
		setPortletId(PortletKeys.OSB_PATCHER);
	}

	private static final PatcherFixComponentIndexer _instance =
		new PatcherFixComponentIndexer();

}