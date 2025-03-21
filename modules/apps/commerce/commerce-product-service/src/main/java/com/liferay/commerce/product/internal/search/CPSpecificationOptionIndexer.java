/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.search;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.CPOptionCategoryLocalService;
import com.liferay.commerce.product.service.CPSpecificationOptionLocalService;
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
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = Indexer.class)
public class CPSpecificationOptionIndexer
	extends BaseIndexer<CPSpecificationOption> {

	public static final String CLASS_NAME =
		CPSpecificationOption.class.getName();

	public CPSpecificationOptionIndexer() {
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.MODIFIED_DATE, Field.TITLE, Field.UID, CPField.KEY);
		setFilterSearch(true);
		setPermissionAware(true);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		Map<String, Serializable> attributes = searchContext.getAttributes();

		if (attributes.containsKey(CPField.FACETABLE)) {
			boolean facetable = GetterUtil.getBoolean(
				attributes.get(CPField.FACETABLE));

			contextBooleanFilter.addRequiredTerm(CPField.FACETABLE, facetable);
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		addSearchTerm(
			searchQuery, searchContext, CPField.CP_OPTION_CATEGORY_ID, false);
		addSearchTerm(
			searchQuery, searchContext, CPField.CP_OPTION_CATEGORY_TITLE,
			false);
		addSearchLocalizedTerm(
			searchQuery, searchContext, CPField.CP_OPTION_CATEGORY_TITLE,
			false);
		addSearchTerm(searchQuery, searchContext, CPField.KEY, false);
		addSearchLocalizedTerm(
			searchQuery, searchContext, Field.DESCRIPTION, false);
		addSearchTerm(searchQuery, searchContext, Field.ENTRY_CLASS_PK, false);
		addSearchTerm(searchQuery, searchContext, Field.TITLE, false);
		addSearchLocalizedTerm(searchQuery, searchContext, Field.TITLE, false);
		addSearchTerm(searchQuery, searchContext, Field.USER_NAME, false);

		LinkedHashMap<String, Object> params =
			(LinkedHashMap<String, Object>)searchContext.getAttribute("params");

		if (params != null) {
			String expandoAttributes = (String)params.get("expandoAttributes");

			if (Validator.isNotNull(expandoAttributes)) {
				addSearchExpando(searchQuery, searchContext, expandoAttributes);
			}
		}
	}

	@Override
	protected void doDelete(CPSpecificationOption cpSpecificationOption)
		throws Exception {

		deleteDocument(
			cpSpecificationOption.getCompanyId(),
			cpSpecificationOption.getCPSpecificationOptionId());
	}

	@Override
	protected Document doGetDocument(
			CPSpecificationOption cpSpecificationOption)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Indexing commerce product specification option " +
					cpSpecificationOption);
		}

		Document document = getBaseModelDocument(
			CLASS_NAME, cpSpecificationOption);

		CPOptionCategory cpOptionCategory =
			_cpOptionCategoryLocalService.fetchCPOptionCategory(
				cpSpecificationOption.getCPOptionCategoryId());

		String[] languageIds = _localization.getAvailableLanguageIds(
			cpSpecificationOption.getTitle());

		for (String languageId : languageIds) {
			if (cpOptionCategory != null) {
				document.addKeyword(
					CPField.CP_OPTION_CATEGORY_ID,
					cpOptionCategory.getCPOptionCategoryId());
				document.addText(
					_localization.getLocalizedName(
						CPField.CP_OPTION_CATEGORY_TITLE, languageId),
					cpOptionCategory.getTitle(languageId));
			}

			document.addKeyword(
				CPField.FACETABLE, cpSpecificationOption.isFacetable());
			document.addText(CPField.KEY, cpSpecificationOption.getKey());

			String title = cpSpecificationOption.getTitle(languageId);

			document.addText(Field.CONTENT, title);

			String description = cpSpecificationOption.getDescription(
				languageId);

			document.addText(
				_localization.getLocalizedName(Field.DESCRIPTION, languageId),
				description);

			document.addText(
				_localization.getLocalizedName(Field.TITLE, languageId), title);
		}

		document.addNumberSortable(
			Field.PRIORITY, cpSpecificationOption.getPriority());

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Commerce product specification option " +
					cpSpecificationOption + " indexed successfully");
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
	protected void doReindex(CPSpecificationOption cpSpecificationOption)
		throws Exception {

		_indexWriterHelper.updateDocument(
			cpSpecificationOption.getCompanyId(),
			getDocument(cpSpecificationOption));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		doReindex(
			_cpSpecificationOptionLocalService.getCPSpecificationOption(
				classPK));
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		_reindexCPSpecificationOptions(companyId);
	}

	private void _reindexCPSpecificationOptions(long companyId)
		throws Exception {

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_cpSpecificationOptionLocalService.
				getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(CPSpecificationOption cpSpecificationOption) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(cpSpecificationOption));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index commerce product specification " +
								"option " + cpSpecificationOption,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPSpecificationOptionIndexer.class);

	@Reference
	private CPOptionCategoryLocalService _cpOptionCategoryLocalService;

	@Reference
	private CPSpecificationOptionLocalService
		_cpSpecificationOptionLocalService;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private Localization _localization;

}