/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.search;

import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherFixComponentLocalService;
import com.liferay.osb.patcher.service.PatcherFixPackLocalService;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalService;
import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
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
public class PatcherFixPackIndexer extends BaseIndexer<PatcherFixPack> {

	public static final String CLASS_NAME = PatcherFixPack.class.getName();

	@Override
	public String getClassName() {
		return CLASS_NAME;
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
	protected void doDelete(PatcherFixPack patcherFixPack) throws Exception {
		deleteDocument(
			patcherFixPack.getCompanyId(),
			patcherFixPack.getPatcherFixPackId());
	}

	@Override
	protected Document doGetDocument(PatcherFixPack patcherFixPack)
		throws Exception {

		Document document = getBaseModelDocument(CLASS_NAME, patcherFixPack);

		document.addText("name", patcherFixPack.getName());
		document.addKeyword("name_sortable", patcherFixPack.getName());

		long patcherFixComponentId = patcherFixPack.getPatcherFixComponentId();

		document.addKeyword("patcherFixComponentId", patcherFixComponentId);

		PatcherFixComponent patcherFixComponent =
			_patcherFixComponentLocalService.getPatcherFixComponent(
				patcherFixComponentId);

		document.addKeyword(
			"patcherFixComponentName", patcherFixComponent.getName());

		long patcherProjectVersionId =
			patcherFixPack.getPatcherProjectVersionId();

		document.addKeyword("patcherProjectVersionId", patcherProjectVersionId);

		PatcherProjectVersion patcherProjectVersion =
			_patcherProjectVersionLocalService.getPatcherProjectVersion(
				patcherProjectVersionId);

		document.addText(
			"patcherProjectVersionName", patcherProjectVersion.getName());

		document.addKeyword("status", patcherFixPack.getStatus());

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
	protected void doReindex(PatcherFixPack patcherFixPack) throws Exception {
		_indexWriterHelper.updateDocument(
			patcherFixPack.getCompanyId(), getDocument(patcherFixPack));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		PatcherFixPack patcherFixPack =
			_patcherFixPackLocalService.fetchPatcherFixPack(classPK);

		if (patcherFixPack != null) {
			doReindex(patcherFixPack);
		}
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_patcherFixPackLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(PatcherFixPack patcherFixPack) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(patcherFixPack));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index patcher fix pack " +
								patcherFixPack,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherFixPackIndexer.class);

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private PatcherFixComponentLocalService _patcherFixComponentLocalService;

	@Reference
	private PatcherFixPackLocalService _patcherFixPackLocalService;

	@Reference
	private PatcherProjectVersionLocalService
		_patcherProjectVersionLocalService;

}