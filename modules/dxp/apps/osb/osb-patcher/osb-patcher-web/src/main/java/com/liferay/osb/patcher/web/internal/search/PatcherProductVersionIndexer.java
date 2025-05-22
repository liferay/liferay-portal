/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.search;

import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalService;
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
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Zsolt Balogh
 */
@Component(service = Indexer.class)
public class PatcherProductVersionIndexer
	extends BaseIndexer<PatcherProductVersion> {

	public static final String CLASS_NAME =
		PatcherProductVersion.class.getName();

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		long entryClassPK = GetterUtil.getLong(
			searchContext.getAttribute(Field.ENTRY_CLASS_PK));

		if (entryClassPK > 0) {
			searchQuery.addRequiredTerm(Field.ENTRY_CLASS_PK, entryClassPK);
		}

		addSearchTerm(searchQuery, searchContext, "name", true);
	}

	@Override
	protected void doDelete(PatcherProductVersion patcherProductVersion)
		throws Exception {

		deleteDocument(
			patcherProductVersion.getCompanyId(),
			patcherProductVersion.getPatcherProductVersionId());
	}

	@Override
	protected Document doGetDocument(
			PatcherProductVersion patcherProductVersion)
		throws Exception {

		Document document = getBaseModelDocument(
			CLASS_NAME, patcherProductVersion);

		document.addText("name", patcherProductVersion.getName());
		document.addKeyword("name_sortable", patcherProductVersion.getName());

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
	protected void doReindex(PatcherProductVersion patcherProductVersion)
		throws Exception {

		_indexWriterHelper.updateDocument(
			patcherProductVersion.getCompanyId(),
			getDocument(patcherProductVersion));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		PatcherProductVersion patcherProductVersion =
			_patcherProductVersionLocalService.fetchPatcherProductVersion(
				classPK);

		if (patcherProductVersion != null) {
			doReindex(patcherProductVersion);
		}
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_patcherProductVersionLocalService.
				getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(PatcherProductVersion patcherProductVersion) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(patcherProductVersion));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index patcher product version " +
								patcherProductVersion,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherProductVersionIndexer.class);

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private PatcherProductVersionLocalService
		_patcherProductVersionLocalService;

}