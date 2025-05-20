/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.BaseAlloyIndexer;
import com.liferay.osb.patcher.model.PatcherProductVersion;
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
public class PatcherProductVersionIndexer extends BaseAlloyIndexer {

	public static PatcherProductVersionIndexer getInstance() {
		return _instance;
	}

	public PatcherProductVersionIndexer() {
		setClassName(PatcherProductVersion.class.getName());
		setPortletId(PortletKeys.OSB_PATCHER);
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
		PatcherProductVersion patcherProductVersion =
			(PatcherProductVersion)obj;

		Document document = getBaseModelDocument(
			portletId, patcherProductVersion);

		document.addText("name", patcherProductVersion.getName());
		document.addKeyword("name_sortable", patcherProductVersion.getName());

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
			"/WEB-INF/jsp/osb_patcher/views/product_versions/view.jsp");

		String patcherProductVersionId = document.get(Field.ENTRY_CLASS_PK);

		portletURL.setParameter("id", patcherProductVersionId);

		return new Summary(title, content, portletURL);
	}

	private static final PatcherProductVersionIndexer _instance =
		new PatcherProductVersionIndexer();

}