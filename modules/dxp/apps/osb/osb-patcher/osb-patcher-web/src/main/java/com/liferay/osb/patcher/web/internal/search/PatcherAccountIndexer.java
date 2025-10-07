/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.search;

import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.service.PatcherAccountLocalService;
import com.liferay.osb.patcher.util.PatcherProductVersionUtil;
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

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Zsolt Balogh
 */
@Component(service = Indexer.class)
public class PatcherAccountIndexer extends BaseIndexer<PatcherAccount> {

	public static final String CLASS_NAME = PatcherAccount.class.getName();

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		addSearchTerm(searchQuery, searchContext, "accountEntryCode", true);
		addSearchTerm(searchQuery, searchContext, "patcherBuildName", true);
		addSearchTerm(
			searchQuery, searchContext, "patcherBuildSupportTicket", true);
		addSearchTerm(
			searchQuery, searchContext, "patcherProjectVersionName", true);
	}

	@Override
	protected void doDelete(PatcherAccount patcherAccount) throws Exception {
		deleteDocument(
			patcherAccount.getCompanyId(),
			patcherAccount.getPatcherAccountId());
	}

	@Override
	protected Document doGetDocument(PatcherAccount patcherAccount)
		throws Exception {

		Document document = getBaseModelDocument(CLASS_NAME, patcherAccount);

		document.addText(
			"accountEntryCode", patcherAccount.getAccountEntryCode());

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
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		return createSummary(document, Field.ENTRY_CLASS_PK, null);
	}

	@Override
	protected void doReindex(PatcherAccount patcherAccount) throws Exception {
		_indexWriterHelper.updateDocument(
			patcherAccount.getCompanyId(), getDocument(patcherAccount));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		PatcherAccount patcherAccount =
			_patcherAccountLocalService.fetchPatcherAccount(classPK);

		if (patcherAccount != null) {
			doReindex(patcherAccount);
		}
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_patcherAccountLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(PatcherAccount patcherAccount) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(patcherAccount));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index patcher account " + patcherAccount,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherAccountIndexer.class);

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private PatcherAccountLocalService _patcherAccountLocalService;

}