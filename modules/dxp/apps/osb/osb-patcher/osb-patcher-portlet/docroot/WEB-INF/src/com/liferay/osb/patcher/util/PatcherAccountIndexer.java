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
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.BooleanQueryFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Locale;

import javax.portlet.PortletURL;

/**
 * @author Zsolt Balogh
 */
public class PatcherAccountIndexer extends BaseAlloyIndexer {

	public static PatcherAccountIndexer getInstance() {
		return _instance;
	}

	public PatcherAccountIndexer() {
		setClassName(PatcherAccount.class.getName());
		setPortletId(PortletKeys.OSB_PATCHER);
	}

	@Override
	public void postProcessContextQuery(
			BooleanQuery contextQuery, SearchContext searchContext)
		throws Exception {

		super.postProcessContextQuery(contextQuery, searchContext);

		BooleanQuery booleanQuery = BooleanQueryFactoryUtil.create(
			searchContext);

		String accountEntryCode = GetterUtil.getString(
			searchContext.getAttribute("accountEntryCode"));

		if (Validator.isNotNull(accountEntryCode)) {
			booleanQuery.addExactTerm("accountEntryCode", accountEntryCode);
		}

		if (GetterUtil.getBoolean(
				searchContext.getAttribute("advancedSearch"))) {

			setBooleanQuery(booleanQuery, searchContext);
		}

		List<BooleanClause> booleanClauses = booleanQuery.clauses();

		if (!booleanClauses.isEmpty()) {
			contextQuery.add(booleanQuery, BooleanClauseOccur.MUST);
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		addSearchTerm(searchQuery, searchContext, "accountEntryCode", true);
		addSearchTerm(searchQuery, searchContext, "patcherBuildName", true);
		addSearchTerm(
			searchQuery, searchContext, "patcherBuildSupportTicket", true);
		addSearchTerm(
			searchQuery, searchContext, "patcherProjectVersionName", true);
	}

	@Override
	protected Document doGetDocument(Object obj) throws Exception {
		PatcherAccount patcherAccount = (PatcherAccount)obj;

		Document document = getBaseModelDocument(portletId, patcherAccount);

		String accountEntryCode = patcherAccount.getAccountEntryCode();

		document.addText("accountEntryCode", accountEntryCode);

		List<PatcherProductVersion> patcherProductVersions =
			PatcherProductVersionUtil.getPatcherProductVersions(patcherAccount);

		long[] patcherProductVersionIds =
			new long[patcherProductVersions.size()];

		for (int i = 0; i < patcherProductVersions.size(); i++) {
			PatcherProductVersion patcherProductVersion =
				patcherProductVersions.get(i);

			patcherProductVersionIds[i] =
				patcherProductVersion.getPatcherProductVersionId();
		}

		document.addKeyword(
			"patcherProductVersionId", patcherProductVersionIds);

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletURL portletURL) {

		String title = document.get(Field.ENTRY_CLASS_PK);

		String content = null;

		portletURL.setParameter(
			"mvcPath", "/WEB-INF/jsp/patcher/views/accounts/view.jsp");

		String patcherAccountId = document.get(Field.ENTRY_CLASS_PK);

		portletURL.setParameter("id", patcherAccountId);

		return new Summary(title, content, portletURL);
	}

	protected void setBooleanQuery(
			BooleanQuery booleanQuery, SearchContext searchContext)
		throws Exception {

		String accountEntryCode = GetterUtil.getString(
			searchContext.getAttribute("accountEntryCode"));

		if (Validator.isNotNull(accountEntryCode)) {
			setBooleanQueryIsAndSearch(
				booleanQuery, searchContext, "accountEntryCode",
				PatcherUtil.prepareKeywords(accountEntryCode), false);
		}

		String patcherBuildName = GetterUtil.getString(
			searchContext.getAttribute("patcherBuildName"));

		if (Validator.isNotNull(patcherBuildName)) {
			setBooleanQueryIsAndSearch(
				booleanQuery, searchContext, "patcherBuildName",
				PatcherUtil.prepareKeywords(patcherBuildName), true);
		}

		String patcherBuildSupportTicket = GetterUtil.getString(
			searchContext.getAttribute("patcherBuildSupportTicket"));

		if (Validator.isNotNull(patcherBuildSupportTicket)) {
			setBooleanQueryIsAndSearch(
				booleanQuery, searchContext, "patcherBuildSupportTicket",
				PatcherUtil.prepareKeywords(patcherBuildSupportTicket), true);
		}

		long patcherProjectVersionIdFilter = GetterUtil.getLong(
			searchContext.getAttribute("patcherProjectVersionIdFilter"));

		if (patcherProjectVersionIdFilter > 0) {
			setBooleanQueryIsAndSearch(
				booleanQuery, searchContext, "patcherProjectVersionId",
				patcherProjectVersionIdFilter, true);
		}
	}

	protected void setBooleanQueryIsAndSearch(
			BooleanQuery booleanQuery, SearchContext searchContext,
			String field, Object value, boolean like)
		throws Exception {

		if (searchContext.isAndSearch()) {
			booleanQuery.addRequiredTerm(field, String.valueOf(value), like);
		}
		else {
			booleanQuery.addTerm(field, String.valueOf(value), like);
		}
	}

	private static PatcherAccountIndexer _instance =
		new PatcherAccountIndexer();

}