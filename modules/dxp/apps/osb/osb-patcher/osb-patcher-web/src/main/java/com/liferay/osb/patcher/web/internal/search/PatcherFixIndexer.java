/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.search;

import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherFixLocalService;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalService;
import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Zsolt Balogh
 */
@Component(service = Indexer.class)
public class PatcherFixIndexer extends BaseIndexer<PatcherFix> {

	public static final String CLASS_NAME = PatcherFix.class.getName();

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void postProcessContextQuery(
			BooleanQuery contextQuery, SearchContext searchContext)
		throws Exception {

		long patcherProductVersionId = GetterUtil.getLong(
			searchContext.getAttribute("patcherProductVersionId"));

		if (patcherProductVersionId > 0) {
			contextQuery.addRequiredTerm(
				"patcherProductVersionId", patcherProductVersionId);
		}

		BooleanQuery booleanQuery = new BooleanQueryImpl();

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

			BooleanQuery subbooleanQuery = new BooleanQueryImpl();

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

			BooleanQuery subbooleanQuery = new BooleanQueryImpl();

			subbooleanQuery.addExactTerm(
				"status", WorkflowConstants.STATUS_FIX_CONFLICT);

			booleanQuery.add(subbooleanQuery, BooleanClauseOccur.MUST_NOT);

			booleanClauseOccur = BooleanClauseOccur.MUST_NOT;
		}

		if (booleanQuery.hasClauses()) {
			contextQuery.add(booleanQuery, booleanClauseOccur);
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		if (PatcherUtil.isPatcherTickets(searchContext.getKeywords())) {
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
	protected void doDelete(PatcherFix patcherFix) throws Exception {
		deleteDocument(patcherFix.getCompanyId(), patcherFix.getPatcherFixId());
	}

	@Override
	protected Document doGetDocument(PatcherFix patcherFix) throws Exception {
		Document document = getBaseModelDocument(CLASS_NAME, patcherFix);

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
			_patcherProjectVersionLocalService.getPatcherProjectVersion(
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
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		return createSummary(document, Field.ENTRY_CLASS_PK, null);
	}

	@Override
	protected void doReindex(PatcherFix patcherFix) throws Exception {
		_indexWriterHelper.updateDocument(
			patcherFix.getCompanyId(), getDocument(patcherFix));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		PatcherFix patcherFix = _patcherFixLocalService.fetchPatcherFix(
			classPK);

		if (patcherFix != null) {
			doReindex(patcherFix);
		}
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_patcherFixLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(PatcherFix patcherFix) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(patcherFix));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index patcher fix " + patcherFix,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
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

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherFixIndexer.class);

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private PatcherFixLocalService _patcherFixLocalService;

	@Reference
	private PatcherProjectVersionLocalService
		_patcherProjectVersionLocalService;

}