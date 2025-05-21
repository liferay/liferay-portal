/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.search;

import com.liferay.alloy.mvc.BaseAlloyIndexer;
import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.petra.string.StringPool;
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
public class PatcherFixIndexer extends BaseAlloyIndexer {

	public static PatcherFixIndexer getInstance() {
		return _instance;
	}

	@Override
	public void postProcessContextQuery(
			BooleanQuery contextQuery, SearchContext searchContext)
		throws Exception {

		super.postProcessContextQuery(contextQuery, searchContext);

		long patcherProductVersionId = GetterUtil.getLong(
			searchContext.getAttribute("patcherProductVersionId"));

		if (patcherProductVersionId > 0) {
			contextQuery.addRequiredTerm(
				"patcherProductVersionId", patcherProductVersionId);
		}

		BooleanQuery booleanQuery = BooleanQueryFactoryUtil.create(
			searchContext);

		BooleanClauseOccur booleanClauseOccur = BooleanClauseOccur.MUST;

		if (GetterUtil.getBoolean(
				searchContext.getAttribute("advancedSearch"))) {

			setBooleanQuery(booleanQuery, searchContext);
		}
		else if (GetterUtil.getBoolean(
					searchContext.getAttribute("viewSearch"))) {

			String key = GetterUtil.getString(
				searchContext.getAttribute("key"));

			if (Validator.isNotNull(key)) {
				booleanQuery.addRequiredTerm("key", key, false);
			}

			BooleanQuery subbooleanQuery = BooleanQueryFactoryUtil.create(
				searchContext);

			subbooleanQuery.addExactTerm(
				"type", PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC);

			booleanQuery.add(subbooleanQuery, BooleanClauseOccur.MUST_NOT);
		}
		else {
			contextQuery.addRequiredTerm("latestFix", true);

			booleanQuery.addExactTerm(
				"type", PatcherFixConstants.TYPE_FIX_PACK);
			booleanQuery.addExactTerm(
				"type", PatcherFixConstants.TYPE_GENERATED);
			booleanQuery.addExactTerm(
				"type", PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC);

			BooleanQuery subbooleanQuery = BooleanQueryFactoryUtil.create(
				searchContext);

			subbooleanQuery.addExactTerm(
				"status", WorkflowConstants.STATUS_FIX_CONFLICT);

			booleanQuery.add(subbooleanQuery, BooleanClauseOccur.MUST_NOT);

			booleanClauseOccur = BooleanClauseOccur.MUST_NOT;
		}

		List<BooleanClause> booleanClauses = booleanQuery.clauses();

		if (!booleanClauses.isEmpty()) {
			contextQuery.add(booleanQuery, booleanClauseOccur);
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		String keywords = searchContext.getKeywords();

		if (PatcherUtil.isPatcherTickets(keywords)) {
			addSearchTerm(searchQuery, searchContext, "patcherFixName", true);

			return;
		}

		boolean advancedSearch = GetterUtil.getBoolean(
			searchContext.getAttribute("advancedSearch"));

		if (!advancedSearch) {
			addSearchTerm(
				searchQuery, searchContext, Field.ENTRY_CLASS_PK, false);
			addSearchTerm(searchQuery, searchContext, "patcherFixName", true);
			addSearchTerm(
				searchQuery, searchContext, "patcherProjectVersionName", true);
		}
	}

	@Override
	protected Document doGetDocument(Object obj) throws Exception {
		PatcherFix patcherFix = (PatcherFix)obj;

		Document document = getBaseModelDocument(portletId, patcherFix);

		document.addText("jenkinsResults", patcherFix.getJenkinsResults());
		document.addText("key", patcherFix.getKey());
		document.addKeyword("keyVersion", patcherFix.getKeyVersion());
		document.addKeyword("latestFix", patcherFix.isLatestFix());

		String patcherFixName = patcherFix.getName();

		patcherFixName = patcherFixName.replaceAll("#[^,#]+", StringPool.BLANK);

		document.addText("patcherFixName", patcherFixName);

		document.addKeyword(
			"patcherProjectVersionId", patcherFix.getPatcherProjectVersionId());

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
				patcherFix.getPatcherProjectVersionId());

		document.addText(
			"patcherProjectVersionName", patcherProjectVersion.getName());
		document.addKeyword(
			"patcherProductVersionId",
			patcherProjectVersion.getPatcherProductVersionId());

		document.addText("requestKey", patcherFix.getRequestKey());
		document.addKeyword("status", patcherFix.getStatus());
		document.addKeyword("type", patcherFix.getType());

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletURL portletURL) {

		String title = document.get(Field.ENTRY_CLASS_PK);

		String content = null;

		portletURL.setParameter(
			"mvcPath", "/WEB-INF/jsp/osb_patcher/views/fixes/view.jsp");

		String patcherFixId = document.get(Field.ENTRY_CLASS_PK);

		portletURL.setParameter("id", patcherFixId);

		return new Summary(title, content, portletURL);
	}

	protected void setBooleanQuery(
			BooleanQuery booleanQuery, SearchContext searchContext)
		throws Exception {

		long entryClassPK = GetterUtil.getLong(
			searchContext.getAttribute(Field.ENTRY_CLASS_PK));

		if (entryClassPK > 0) {
			setBooleanQueryIsAndSearch(
				booleanQuery, searchContext, Field.ENTRY_CLASS_PK,
				entryClassPK);
		}

		boolean hideOldFixVersions = GetterUtil.getBoolean(
			searchContext.getAttribute("hideOldFixVersions"), true);

		if (hideOldFixVersions) {
			setBooleanQueryIsAndSearch(
				booleanQuery, searchContext, "latestFix",
				String.valueOf(hideOldFixVersions));
		}

		String patcherFixName = GetterUtil.getString(
			searchContext.getAttribute("patcherFixName"));

		if (Validator.isNotNull(patcherFixName)) {
			setBooleanQueryIsAndSearch(
				booleanQuery, searchContext, "patcherFixName",
				PatcherUtil.prepareKeywords(patcherFixName), true);
		}

		long patcherProjectVersionIdFilter = GetterUtil.getLong(
			searchContext.getAttribute("patcherProjectVersionIdFilter"));

		if (patcherProjectVersionIdFilter > 0) {
			setBooleanQueryIsAndSearch(
				booleanQuery, searchContext, "patcherProjectVersionId",
				patcherProjectVersionIdFilter);
		}

		int statusFilter = GetterUtil.getInteger(
			searchContext.getAttribute("statusFilter"));

		if (statusFilter > 0) {
			setBooleanQueryIsAndSearch(
				booleanQuery, searchContext, "status", statusFilter);
		}

		int typeFilter = GetterUtil.getInteger(
			searchContext.getAttribute("typeFilter"),
			PatcherFixConstants.TYPE_ANY);

		if (typeFilter >= 0) {
			setBooleanQueryIsAndSearch(
				booleanQuery, searchContext, "type", typeFilter);
		}
	}

	protected void setBooleanQueryIsAndSearch(
			BooleanQuery booleanQuery, SearchContext searchContext,
			BooleanQuery query)
		throws Exception {

		if (searchContext.isAndSearch()) {
			booleanQuery.add(query, BooleanClauseOccur.MUST);
		}
		else {
			booleanQuery.add(query, BooleanClauseOccur.SHOULD);
		}
	}

	protected void setBooleanQueryIsAndSearch(
			BooleanQuery booleanQuery, SearchContext searchContext,
			String field, Object value)
		throws Exception {

		setBooleanQueryIsAndSearch(
			booleanQuery, searchContext, field, value, false);
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

	private PatcherFixIndexer() {
		setClassName(PatcherFix.class.getName());
		setPortletId(PatcherPortletKeys.PATCHER);
	}

	private static final PatcherFixIndexer _instance = new PatcherFixIndexer();

}