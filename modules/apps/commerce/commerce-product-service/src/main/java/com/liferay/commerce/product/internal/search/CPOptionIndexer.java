/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.search;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.CPOptionLocalService;
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

import java.util.LinkedHashMap;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(service = Indexer.class)
public class CPOptionIndexer extends BaseIndexer<CPOption> {

	public static final String CLASS_NAME = CPOption.class.getName();

	public CPOptionIndexer() {
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.MODIFIED_DATE, Field.NAME, Field.UID, CPField.KEY);
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

		addSearchTerm(searchQuery, searchContext, CPField.KEY, false);
		addSearchTerm(
			searchQuery, searchContext, CPField.OPTION_VALUE_NAME, false);
		addSearchLocalizedTerm(
			searchQuery, searchContext, CPField.OPTION_VALUE_NAME, false);
		addSearchLocalizedTerm(
			searchQuery, searchContext, Field.DESCRIPTION, false);
		addSearchTerm(searchQuery, searchContext, Field.ENTRY_CLASS_PK, false);
		addSearchTerm(searchQuery, searchContext, Field.NAME, false);
		addSearchLocalizedTerm(searchQuery, searchContext, Field.NAME, false);
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
	protected void doDelete(CPOption cpOption) throws Exception {
		deleteDocument(cpOption.getCompanyId(), cpOption.getCPOptionId());
	}

	@Override
	protected Document doGetDocument(CPOption cpOption) throws Exception {
		if (_log.isDebugEnabled()) {
			_log.debug("Indexing commerce product option " + cpOption);
		}

		Document document = getBaseModelDocument(CLASS_NAME, cpOption);

		String cpOptionDefaultLanguageId = _localization.getDefaultLanguageId(
			cpOption.getName());

		String[] languageIds = _localization.getAvailableLanguageIds(
			cpOption.getName());

		for (String languageId : languageIds) {
			String description = cpOption.getDescription(languageId);
			String name = cpOption.getName(languageId);

			if (languageId.equals(cpOptionDefaultLanguageId)) {
				document.addText(Field.DESCRIPTION, description);
				document.addText(Field.NAME, name);
				document.addText("defaultLanguageId", languageId);
			}

			document.addText(
				CPField.COMMERCE_OPTION_TYPE_KEY,
				cpOption.getCommerceOptionTypeKey());
			document.addText(CPField.KEY, cpOption.getKey());
			document.addText(Field.CONTENT, name);
			document.addText(
				_localization.getLocalizedName(Field.DESCRIPTION, languageId),
				description);
			document.addText(
				_localization.getLocalizedName(Field.NAME, languageId), name);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Commerce product option " + cpOption +
					" indexed successfully");
		}

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletRequest portletRequest, PortletResponse portletResponse) {

		Summary summary = createSummary(
			document, Field.NAME, Field.DESCRIPTION);

		summary.setMaxContentLength(200);

		return summary;
	}

	@Override
	protected void doReindex(CPOption cpOption) throws Exception {
		_indexWriterHelper.updateDocument(
			cpOption.getCompanyId(), getDocument(cpOption));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		doReindex(_cpOptionLocalService.getCPOption(classPK));
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		_reindexCPOptions(companyId);
	}

	private void _reindexCPOptions(long companyId) throws Exception {
		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_cpOptionLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(CPOption cpOption) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(cpOption));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index commerce product option " +
								cpOption,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPOptionIndexer.class);

	@Reference
	private CPOptionLocalService _cpOptionLocalService;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private Localization _localization;

}