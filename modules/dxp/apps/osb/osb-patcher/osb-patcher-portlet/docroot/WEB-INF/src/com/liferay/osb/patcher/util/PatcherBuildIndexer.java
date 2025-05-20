/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.BaseAlloyIndexer;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
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
public class PatcherBuildIndexer extends BaseAlloyIndexer {

	public static PatcherBuildIndexer getInstance() {
		return _instance;
	}

	public PatcherBuildIndexer() {
		setClassName(PatcherBuild.class.getName());
		setPortletId(PortletKeys.OSB_PATCHER);
	}

	@Override
	public void postProcessContextQuery(
			BooleanQuery contextQuery, SearchContext searchContext)
		throws Exception {

		super.postProcessContextQuery(contextQuery, searchContext);

		BooleanQuery booleanQuery = BooleanQueryFactoryUtil.create(
			searchContext);

		BooleanClauseOccur booleanClauseOccur = BooleanClauseOccur.MUST;

		long patcherProductVersionId = GetterUtil.getLong(
			searchContext.getAttribute("patcherProductVersionId"));

		if (patcherProductVersionId > 0) {
			contextQuery.addRequiredTerm(
				"patcherProductVersionId", patcherProductVersionId);
		}

		if (GetterUtil.getBoolean(
				searchContext.getAttribute("advancedSearch"))) {

			contextQuery.addRequiredTerm("childBuild", false);

			if (PortletPropsValues.OSB_PATCHER_SCANNING_ENABLED) {
				contextQuery.addRequiredTerm("latestSupportTicketBuild", true);
			}
			else {
				contextQuery.addRequiredTerm("latestKeyBuild", true);
			}

			setBooleanQuery(booleanQuery, searchContext);
		}
		else if (GetterUtil.getBoolean(
					searchContext.getAttribute("buildsViewSearch"))) {

			String key = GetterUtil.getString(
				searchContext.getAttribute("key"));

			if (Validator.isNotNull(key)) {
				booleanQuery.addRequiredTerm("key", key, false);
			}
		}
		else if (GetterUtil.getBoolean(
					searchContext.getAttribute("buildsIndexSearch"))) {

			contextQuery.addRequiredTerm("childBuild", false);

			if (PortletPropsValues.OSB_PATCHER_SCANNING_ENABLED) {
				contextQuery.addRequiredTerm("latestSupportTicketBuild", true);
			}
			else {
				contextQuery.addRequiredTerm("latestKeyBuild", true);
			}

			booleanQuery.addExactTerm(
				"qaStatus",
				WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED);
			booleanQuery.addExactTerm(
				"qaStatus",
				WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY);
			booleanQuery.addExactTerm(
				"qaStatus", WorkflowConstants.STATUS_BUILD_QA_FAILED_MANUALLY);
			booleanQuery.addExactTerm(
				"qaStatus",
				WorkflowConstants.STATUS_BUILD_QA_FAILED_MANUALLY_SMOKE_ONLY);
			booleanQuery.addExactTerm(
				"qaStatus", WorkflowConstants.STATUS_BUILD_QA_PASSED_MANUALLY);
			booleanQuery.addExactTerm(
				"qaStatus",
				WorkflowConstants.STATUS_BUILD_QA_PASSED_MANUALLY_SMOKE_ONLY);
			booleanQuery.addExactTerm(
				"qaStatus",
				WorkflowConstants.STATUS_BUILD_QA_PENDING_SMOKE_ONLY);
			booleanQuery.addExactTerm(
				"qaStatus", WorkflowConstants.STATUS_BUILD_QA_TESTING_SKIPPED);
			booleanQuery.addExactTerm(
				"qaStatus",
				WorkflowConstants.STATUS_BUILD_QA_TESTING_SKIPPED_SMOKE_ONLY);
			booleanQuery.addExactTerm(
				"qaStatus", WorkflowConstants.STATUS_PENDING);
			booleanQuery.addExactTerm(
				"type", PatcherBuildConstants.TYPE_FIX_PACK);

			booleanClauseOccur = BooleanClauseOccur.MUST_NOT;
		}
		else if (GetterUtil.getBoolean(
					searchContext.getAttribute("viewMostRecent"))) {

			contextQuery.addRequiredTerm("childBuild", false);

			booleanQuery.addExactTerm(
				"type", PatcherBuildConstants.TYPE_FIX_PACK);

			booleanClauseOccur = BooleanClauseOccur.MUST_NOT;
		}
		else {
			contextQuery.addRequiredTerm("childBuild", false);

			if (PortletPropsValues.OSB_PATCHER_SCANNING_ENABLED) {
				contextQuery.addRequiredTerm("latestSupportTicketBuild", true);
			}
			else {
				contextQuery.addRequiredTerm("latestKeyBuild", true);
			}

			String patcherBuildAccountEntryCode = GetterUtil.getString(
				searchContext.getAttribute("patcherBuildAccountEntryCode"));

			if (Validator.isNotNull(patcherBuildAccountEntryCode)) {
				contextQuery.addRequiredTerm(
					"patcherBuildAccountEntryCode",
					patcherBuildAccountEntryCode);
			}

			booleanQuery.addExactTerm(
				"type", PatcherBuildConstants.TYPE_FIX_PACK);

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
			addSearchTerm(searchQuery, searchContext, "patcherBuildName", true);

			return;
		}

		boolean advancedSearch = GetterUtil.getBoolean(
			searchContext.getAttribute("advancedSearch"));

		if (!advancedSearch) {
			addSearchTerm(
				searchQuery, searchContext, Field.ENTRY_CLASS_PK, false);
			addSearchTerm(searchQuery, searchContext, "patcherBuildName", true);
			addSearchTerm(
				searchQuery, searchContext, "patcherProjectVersionName", true);
			addSearchTerm(searchQuery, searchContext, "supportTicket", true);
		}
	}

	@Override
	protected Document doGetDocument(Object obj) throws Exception {
		PatcherBuild patcherBuild = (PatcherBuild)obj;

		Document document = getBaseModelDocument(portletId, patcherBuild);

		document.addKeyword(Field.USER_ID, patcherBuild.getUserId());
		document.addKeyword(Field.USER_NAME, patcherBuild.getUserName());

		document.addKeyword("childBuild", patcherBuild.isChildBuild());
		document.addText("comments", patcherBuild.getComments());
		document.addText(
			"downloadURL",
			PortletPropsValues.OSB_PATCHER_BUILD_DOWNLOAD_URL +
				StringPool.SLASH + patcherBuild.getFileName());

		if (Validator.isNotNull(patcherBuild.getFileName())) {
			document.addKeyword("hotfixId", patcherBuild.getHotfixId());
		}

		document.addText("key", patcherBuild.getKey());
		document.addKeyword("keyVersion", patcherBuild.getKeyVersion());
		document.addKeyword("latestKeyBuild", patcherBuild.isLatestKeyBuild());
		document.addKeyword(
			"latestSupportTicketBuild",
			patcherBuild.isLatestSupportTicketBuild());

		PatcherAccount patcherAccount =
			PatcherAccountLocalServiceUtil.getPatcherAccount(
				patcherBuild.getPatcherAccountId());

		document.addText(
			"patcherBuildAccountEntryCode",
			patcherAccount.getAccountEntryCode());

		document.addKeyword("patcherBuildId", patcherBuild.getPatcherBuildId());
		document.addText("patcherBuildName", patcherBuild.getName());
		document.addKeyword("patcherFixId", patcherBuild.getPatcherFixId());
		document.addKeyword(
			"patcherProductVersionId",
			patcherBuild.getPatcherProductVersionId());
		document.addKeyword(
			"patcherProjectVersionId",
			patcherBuild.getPatcherProjectVersionId());

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
				patcherBuild.getPatcherProjectVersionId());

		document.addText(
			"patcherProjectVersionName", patcherProjectVersion.getName());

		document.addText("qaComments", patcherBuild.getQaComments());
		document.addKeyword("qaStatus", patcherBuild.getQaStatus());
		document.addText(
			"qaStatusLabel",
			WorkflowConstants.getStatusLabel(patcherBuild.getQaStatus()));
		document.addText("requestKey", patcherBuild.getRequestKey());
		document.addKeyword("status", patcherBuild.getStatus());
		document.addDate("statusDate", patcherBuild.getStatusDate());
		document.addText(
			"statusLabel",
			WorkflowConstants.getStatusLabel(patcherBuild.getStatus()));
		document.addText("supportTicket", patcherBuild.getSupportTicket());
		document.addKeyword(
			"supportTicketVersion", patcherBuild.getSupportTicketVersion());
		document.addKeyword("ticketEntryId", patcherBuild.getTicketEntryId());
		document.addKeyword("type", patcherBuild.getType());

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletURL portletURL) {

		String title = document.get(Field.ENTRY_CLASS_PK);

		String content = null;

		portletURL.setParameter(
			"mvcPath", "/WEB-INF/jsp/osb_patcher/views/builds/view.jsp");

		String patcherBuildId = document.get(Field.ENTRY_CLASS_PK);

		portletURL.setParameter("id", patcherBuildId);

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

		String patcherBuildName = GetterUtil.getString(
			searchContext.getAttribute("patcherBuildName"));

		if (Validator.isNotNull(patcherBuildName)) {
			setBooleanQueryIsAndSearch(
				booleanQuery, searchContext, "patcherBuildName",
				PatcherUtil.prepareKeywords(patcherBuildName), true);
		}

		String supportTicket = GetterUtil.getString(
			searchContext.getAttribute("supportTicket"));

		if (Validator.isNotNull(supportTicket)) {
			setBooleanQueryIsAndSearch(
				booleanQuery, searchContext, "supportTicket",
				PatcherUtil.prepareKeywords(supportTicket), true);
		}

		String patcherBuildAccountEntryCode = GetterUtil.getString(
			searchContext.getAttribute("patcherBuildAccountEntryCode"));

		if (Validator.isNotNull(patcherBuildAccountEntryCode)) {
			setBooleanQueryIsAndSearch(
				booleanQuery, searchContext, "patcherBuildAccountEntryCode",
				PatcherUtil.prepareKeywords(patcherBuildAccountEntryCode),
				true);
		}

		long patcherProjectVersionIdFilter = GetterUtil.getLong(
			searchContext.getAttribute("patcherProjectVersionIdFilter"));

		if (patcherProjectVersionIdFilter > 0) {
			setBooleanQueryIsAndSearch(
				booleanQuery, searchContext, "patcherProjectVersionId",
				patcherProjectVersionIdFilter);
		}

		int qaStatusFilter = GetterUtil.getInteger(
			searchContext.getAttribute("qaStatusFilter"));

		if (qaStatusFilter > 0) {
			setBooleanQueryIsAndSearch(
				booleanQuery, searchContext, "qaStatus", qaStatusFilter);
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

	private static final PatcherBuildIndexer _instance =
		new PatcherBuildIndexer();

}