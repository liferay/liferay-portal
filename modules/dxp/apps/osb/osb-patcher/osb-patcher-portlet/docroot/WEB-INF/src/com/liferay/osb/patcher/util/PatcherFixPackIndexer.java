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
import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherFixComponentLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;

import javax.portlet.PortletURL;

/**
 * @author Zsolt Balogh
 */
public class PatcherFixPackIndexer extends BaseAlloyIndexer {

	public static PatcherFixPackIndexer getInstance() {
		return _instance;
	}

	public PatcherFixPackIndexer() {
		setClassName(PatcherFixPack.class.getName());
		setPortletId(PortletKeys.OSB_PATCHER);
	}

	@Override
	public void postProcessContextQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		long entryClassPK = GetterUtil.getLong(
			searchContext.getAttribute(Field.ENTRY_CLASS_PK));

		if (entryClassPK > 0) {
			if (searchContext.isAndSearch()) {
				searchQuery.addRequiredTerm(Field.ENTRY_CLASS_PK, entryClassPK);
			}
			else {
				searchQuery.addTerm(Field.ENTRY_CLASS_PK, entryClassPK);
			}
		}

		String name = GetterUtil.getString(searchContext.getAttribute("name"));

		if (Validator.isNotNull(name)) {
			if (searchContext.isAndSearch()) {
				searchQuery.addRequiredTerm("name", name);
			}
			else {
				searchQuery.addTerm("name", name);
			}
		}

		long patcherFixComponentIdFilter = GetterUtil.getLong(
			searchContext.getAttribute("patcherFixComponentIdFilter"));

		if (patcherFixComponentIdFilter > 0) {
			if (searchContext.isAndSearch()) {
				searchQuery.addRequiredTerm(
					"patcherFixComponentId", patcherFixComponentIdFilter);
			}
			else {
				searchQuery.addTerm(
					"patcherFixComponentId", patcherFixComponentIdFilter);
			}
		}

		long patcherProjectVersionIdFilter = GetterUtil.getLong(
			searchContext.getAttribute("patcherProjectVersionIdFilter"));

		if (patcherProjectVersionIdFilter > 0) {
			if (searchContext.isAndSearch()) {
				searchQuery.addRequiredTerm(
					"patcherProjectVersionId", patcherProjectVersionIdFilter);
			}
			else {
				searchQuery.addTerm(
					"patcherProjectVersionId", patcherProjectVersionIdFilter);
			}
		}

		int statusFilter = GetterUtil.getInteger(
			searchContext.getAttribute("statusFilter"));

		if (statusFilter > 0) {
			if (searchContext.isAndSearch()) {
				searchQuery.addRequiredTerm("status", statusFilter);
			}
			else {
				searchQuery.addTerm("status", statusFilter);
			}
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		String keywords = searchContext.getKeywords();

		if (PatcherUtil.isPatcherTickets(keywords)) {
			addSearchTerm(searchQuery, searchContext, "name", true);

			return;
		}

		boolean advancedSearch = GetterUtil.getBoolean(
			searchContext.getAttribute("advancedSearch"));

		if (!advancedSearch) {
			addSearchTerm(
				searchQuery, searchContext, Field.ENTRY_CLASS_PK, false);
			addSearchTerm(searchQuery, searchContext, "name", true);
			addSearchTerm(
				searchQuery, searchContext, "patcherFixComponentName", true);
			addSearchTerm(
				searchQuery, searchContext, "patcherProjectVersionName", true);
		}
	}

	@Override
	protected Document doGetDocument(Object obj) throws Exception {
		PatcherFixPack patcherFixPack = (PatcherFixPack)obj;

		Document document = getBaseModelDocument(portletId, patcherFixPack);

		document.addText("name", patcherFixPack.getName());
		document.addKeyword("name_sortable", patcherFixPack.getName());

		long patcherFixComponentId = patcherFixPack.getPatcherFixComponentId();

		document.addKeyword("patcherFixComponentId", patcherFixComponentId);

		PatcherFixComponent patcherFixComponent =
			PatcherFixComponentLocalServiceUtil.getPatcherFixComponent(
				patcherFixComponentId);

		document.addKeyword(
			"patcherFixComponentName", patcherFixComponent.getName());

		long patcherProjectVersionId =
			patcherFixPack.getPatcherProjectVersionId();

		document.addKeyword("patcherProjectVersionId", patcherProjectVersionId);

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
				patcherProjectVersionId);

		document.addText(
			"patcherProjectVersionName", patcherProjectVersion.getName());

		document.addKeyword("status", patcherFixPack.getStatus());

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletURL portletURL) {

		String title = document.get(Field.ENTRY_CLASS_PK);

		String content = null;

		portletURL.setParameter(
			"mvcPath", "/WEB-INF/jsp/osb_patcher/views/fix_packs/view.jsp");

		String patcherFixPackId = document.get(Field.ENTRY_CLASS_PK);

		portletURL.setParameter("id", patcherFixPackId);

		return new Summary(title, content, portletURL);
	}

	private static final PatcherFixPackIndexer _instance =
		new PatcherFixPackIndexer();

}