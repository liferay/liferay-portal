/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.search;

import com.liferay.osb.patcher.constants.PatcherFixConstants;
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
import com.liferay.portal.kernel.search.filter.BooleanFilter;
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
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		long patcherProductVersionId = GetterUtil.getLong(
			searchContext.getAttribute("patcherProductVersionId"));

		if (patcherProductVersionId > 0) {
			contextBooleanFilter.addRequiredTerm(
				"patcherProductVersionId", patcherProductVersionId);
		}

		if (GetterUtil.getBoolean(searchContext.getAttribute("viewSearch"))) {
			String key = GetterUtil.getString(
				searchContext.getAttribute("key"));

			if (Validator.isNotNull(key)) {
				contextBooleanFilter.addRequiredTerm("key", key);
			}

			contextBooleanFilter.addTerm(
				"type",
				String.valueOf(
					PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC),
				BooleanClauseOccur.MUST_NOT);

			return;
		}

		boolean hideOldFixVersions = GetterUtil.getBoolean(
			searchContext.getAttribute("hideOldFixVersions"), true);

		if (hideOldFixVersions) {
			contextBooleanFilter.addRequiredTerm(
				"latestFix", String.valueOf(hideOldFixVersions));
		}

		long patcherProjectVersionIdFilter = GetterUtil.getLong(
			searchContext.getAttribute("patcherProjectVersionId"));

		if (patcherProjectVersionIdFilter > 0) {
			contextBooleanFilter.addRequiredTerm(
				"patcherProjectVersionId", patcherProjectVersionIdFilter);
		}

		int statusFilter = GetterUtil.getInteger(
			searchContext.getAttribute("status"));

		if (statusFilter > 0) {
			contextBooleanFilter.addRequiredTerm("status", statusFilter);
		}

		int typeFilter = GetterUtil.getInteger(
			searchContext.getAttribute("type"), PatcherFixConstants.TYPE_ANY);

		if (typeFilter >= 0) {
			contextBooleanFilter.addRequiredTerm("type", typeFilter);
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

		addSearchTerm(searchQuery, searchContext, Field.ENTRY_CLASS_PK, false);
		addSearchTerm(searchQuery, searchContext, "patcherFixName", true);
		addSearchTerm(
			searchQuery, searchContext, "patcherProjectVersionName", true);
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