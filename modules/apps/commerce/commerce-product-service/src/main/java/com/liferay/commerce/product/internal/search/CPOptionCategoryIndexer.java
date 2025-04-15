/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.search;

import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.service.CPOptionCategoryLocalService;
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
import com.liferay.portal.kernel.util.Localization;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(service = Indexer.class)
public class CPOptionCategoryIndexer extends BaseIndexer<CPOptionCategory> {

	public static final String CLASS_NAME = CPOptionCategory.class.getName();

	public static final String FIELD_KEY = "key";

	public CPOptionCategoryIndexer() {
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.MODIFIED_DATE, Field.NAME, Field.UID, FIELD_KEY);
		setFilterSearch(true);
		setPermissionAware(true);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		addSearchLocalizedTerm(
			searchQuery, searchContext, Field.DESCRIPTION, false);
		addSearchTerm(searchQuery, searchContext, Field.ENTRY_CLASS_PK, false);
		addSearchTerm(searchQuery, searchContext, Field.TITLE, false);
		addSearchLocalizedTerm(searchQuery, searchContext, Field.TITLE, false);
		addSearchTerm(searchQuery, searchContext, Field.USER_NAME, false);
		addSearchTerm(searchQuery, searchContext, FIELD_KEY, false);
	}

	@Override
	protected void doDelete(CPOptionCategory cpOptionCategory)
		throws Exception {

		deleteDocument(
			cpOptionCategory.getCompanyId(),
			cpOptionCategory.getCPOptionCategoryId());
	}

	@Override
	protected Document doGetDocument(CPOptionCategory cpOptionCategory)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Indexing commerce product option category " +
					cpOptionCategory);
		}

		Document document = getBaseModelDocument(CLASS_NAME, cpOptionCategory);

		String cpOptionCategoryDefaultLanguageId =
			_localization.getDefaultLanguageId(cpOptionCategory.getTitle());

		String[] languageIds = _localization.getAvailableLanguageIds(
			cpOptionCategory.getTitle());

		for (String languageId : languageIds) {
			String description = cpOptionCategory.getDescription(languageId);

			String title = cpOptionCategory.getTitle(languageId);

			document.addText(Field.CONTENT, title);

			document.addText(
				_localization.getLocalizedName(Field.DESCRIPTION, languageId),
				description);
			document.addText(
				_localization.getLocalizedName(Field.TITLE, languageId), title);
			document.addText(FIELD_KEY, cpOptionCategory.getKey());

			if (languageId.equals(cpOptionCategoryDefaultLanguageId)) {
				document.addText(Field.DESCRIPTION, description);
				document.addText(Field.TITLE, title);
				document.addText("defaultLanguageId", languageId);
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Commerce product option category " + cpOptionCategory +
					" indexed successfully");
		}

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletRequest portletRequest, PortletResponse portletResponse) {

		Summary summary = createSummary(
			document, Field.TITLE, Field.DESCRIPTION);

		summary.setMaxContentLength(200);

		return summary;
	}

	@Override
	protected void doReindex(CPOptionCategory cpOptionCategory)
		throws Exception {

		_indexWriterHelper.updateDocument(
			cpOptionCategory.getCompanyId(), getDocument(cpOptionCategory));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		doReindex(_cpOptionCategoryLocalService.getCPOptionCategory(classPK));
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		_reindexCPOptionCategorys(companyId);
	}

	private void _reindexCPOptionCategorys(long companyId) throws Exception {
		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_cpOptionCategoryLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(CPOptionCategory cpOptionCategory) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(cpOptionCategory));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index commerce product option " +
								"category " + cpOptionCategory,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPOptionCategoryIndexer.class);

	@Reference
	private CPOptionCategoryLocalService _cpOptionCategoryLocalService;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private Localization _localization;

}