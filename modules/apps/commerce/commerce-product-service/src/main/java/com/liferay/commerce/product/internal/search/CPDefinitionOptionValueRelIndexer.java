/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.search;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelLocalService;
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
import com.liferay.portal.kernel.util.LocaleUtil;
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
public class CPDefinitionOptionValueRelIndexer
	extends BaseIndexer<CPDefinitionOptionValueRel> {

	public static final String CLASS_NAME =
		CPDefinitionOptionValueRel.class.getName();

	public CPDefinitionOptionValueRelIndexer() {
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.GROUP_ID, Field.MODIFIED_DATE, Field.NAME,
			Field.SCOPE_GROUP_ID, Field.UID, CPField.KEY);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		long cpDefinitionOptionRelId = GetterUtil.getLong(
			searchContext.getAttribute(CPField.CP_DEFINITION_OPTION_REL_ID));

		if (cpDefinitionOptionRelId > 0) {
			contextBooleanFilter.addRequiredTerm(
				CPField.CP_DEFINITION_OPTION_REL_ID, cpDefinitionOptionRelId);
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		addSearchTerm(searchQuery, searchContext, CPField.KEY, false);
		addSearchTerm(searchQuery, searchContext, Field.ENTRY_CLASS_PK, false);
		addSearchLocalizedTerm(searchQuery, searchContext, Field.NAME, false);
		addSearchTerm(searchQuery, searchContext, Field.USER_NAME, false);
		addSearchTerm(searchQuery, searchContext, "sku", false);

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
	protected void doDelete(
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel)
		throws Exception {

		deleteDocument(
			cpDefinitionOptionValueRel.getCompanyId(),
			cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId());
	}

	@Override
	protected Document doGetDocument(
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Indexing commerce product definition option value " +
					"relationship " + cpDefinitionOptionValueRel);
		}

		Document document = getBaseModelDocument(
			CLASS_NAME, cpDefinitionOptionValueRel);

		document.addKeyword(
			CPField.CP_DEFINITION_OPTION_REL_ID,
			cpDefinitionOptionValueRel.getCPDefinitionOptionRelId());
		document.addNumber(
			Field.PRIORITY, cpDefinitionOptionValueRel.getPriority());

		String cpDefinitionOptionValueRelDefaultLanguageId =
			_localization.getDefaultLanguageId(
				cpDefinitionOptionValueRel.getName());

		Locale locale = LocaleUtil.fromLanguageId(
			cpDefinitionOptionValueRelDefaultLanguageId);

		addLocalizedField(
			document, Field.TITLE, locale,
			cpDefinitionOptionValueRel.getNameMap());

		CPInstance cpInstance = cpDefinitionOptionValueRel.fetchCPInstance();

		if (cpInstance != null) {
			document.addKeyword("sku", cpInstance.getSku());
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Commerce product definition option value relationship " +
					cpDefinitionOptionValueRel + " indexed successfully");
		}

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletRequest portletRequest, PortletResponse portletResponse) {

		Summary summary = createSummary(document, Field.NAME, CPField.KEY);

		summary.setMaxContentLength(200);

		return summary;
	}

	@Override
	protected void doReindex(
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel)
		throws Exception {

		_indexWriterHelper.updateDocument(
			cpDefinitionOptionValueRel.getCompanyId(),
			getDocument(cpDefinitionOptionValueRel));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		doReindex(
			_cpDefinitionOptionValueRelLocalService.
				getCPDefinitionOptionValueRel(classPK));
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		_reindexCPDefinitionOptionValueRels(companyId);
	}

	private void _reindexCPDefinitionOptionValueRels(long companyId)
		throws Exception {

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_cpDefinitionOptionValueRelLocalService.
				getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(CPDefinitionOptionValueRel cpDefinitionOptionValueRel) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(cpDefinitionOptionValueRel));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index commerce product definition " +
								"option value relationship " +
									cpDefinitionOptionValueRel,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionOptionValueRelIndexer.class);

	@Reference
	private CPDefinitionOptionValueRelLocalService
		_cpDefinitionOptionValueRelLocalService;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private Localization _localization;

}