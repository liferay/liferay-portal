/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.search;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(service = Indexer.class)
public class CPDefinitionOptionRelIndexer
	extends BaseIndexer<CPDefinitionOptionRel> {

	public static final String CLASS_NAME =
		CPDefinitionOptionRel.class.getName();

	public CPDefinitionOptionRelIndexer() {
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.GROUP_ID, Field.MODIFIED_DATE, Field.NAME,
			Field.SCOPE_GROUP_ID, Field.UID);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		long cpDefinitionId = GetterUtil.getLong(
			searchContext.getAttribute(CPField.CP_DEFINITION_ID));

		if (cpDefinitionId > 0) {
			contextBooleanFilter.addRequiredTerm(
				CPField.CP_DEFINITION_ID, cpDefinitionId);
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		addSearchTerm(
			searchQuery, searchContext,
			CPField.DEFINITION_OPTION_VALUE_REL_NAME, false);
		addSearchLocalizedTerm(
			searchQuery, searchContext,
			CPField.DEFINITION_OPTION_VALUE_REL_NAME, false);
		addSearchLocalizedTerm(
			searchQuery, searchContext, Field.CONTENT, false);
		addSearchLocalizedTerm(
			searchQuery, searchContext, Field.DESCRIPTION, false);
		addSearchTerm(searchQuery, searchContext, Field.ENTRY_CLASS_PK, false);
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
	protected void doDelete(CPDefinitionOptionRel cpDefinitionOptionRel)
		throws Exception {

		deleteDocument(
			cpDefinitionOptionRel.getCompanyId(),
			cpDefinitionOptionRel.getCPDefinitionOptionRelId());
	}

	@Override
	protected Document doGetDocument(
			CPDefinitionOptionRel cpDefinitionOptionRel)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Indexing commerce product definition option relationship " +
					cpDefinitionOptionRel);
		}

		Document document = getBaseModelDocument(
			CLASS_NAME, cpDefinitionOptionRel);

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			cpDefinitionOptionRel.getCPDefinitionOptionValueRels();

		String cpDefinitionOptionRelDefaultLanguageId =
			_localization.getDefaultLanguageId(cpDefinitionOptionRel.getName());

		String[] languageIds = _localization.getAvailableLanguageIds(
			cpDefinitionOptionRel.getName());

		for (String languageId : languageIds) {
			String description = cpDefinitionOptionRel.getDescription(
				languageId);
			String name = cpDefinitionOptionRel.getName(languageId);

			List<String> cpDefinitionOptionValueRelNamesList =
				new ArrayList<>();

			for (CPDefinitionOptionValueRel cpDefinitionOptionValueRel :
					cpDefinitionOptionValueRels) {

				cpDefinitionOptionValueRelNamesList.add(
					cpDefinitionOptionValueRel.getName(languageId));
			}

			document.addKeyword(
				CPField.CP_DEFINITION_ID,
				cpDefinitionOptionRel.getCPDefinitionId());

			String[] cpDefinitionOptionValueRelNames =
				cpDefinitionOptionValueRelNamesList.toArray(new String[0]);

			document.addText(
				_localization.getLocalizedName(
					CPField.DEFINITION_OPTION_VALUE_REL_NAME, languageId),
				cpDefinitionOptionValueRelNames);

			document.addText(Field.CONTENT, name);
			document.addText(
				_localization.getLocalizedName(Field.DESCRIPTION, languageId),
				description);
			document.addText(
				_localization.getLocalizedName(Field.NAME, languageId), name);

			if (languageId.equals(cpDefinitionOptionRelDefaultLanguageId)) {
				document.addText(
					CPField.DEFINITION_OPTION_VALUE_REL_NAME,
					cpDefinitionOptionValueRelNames);
				document.addText(Field.DESCRIPTION, description);
				document.addText(Field.NAME, name);
				document.addText("defaultLanguageId", languageId);
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Commerce product definition option relationship " +
					cpDefinitionOptionRel + " indexed successfully");
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
	protected void doReindex(CPDefinitionOptionRel cpDefinitionOptionRel)
		throws Exception {

		_indexWriterHelper.updateDocument(
			cpDefinitionOptionRel.getCompanyId(),
			getDocument(cpDefinitionOptionRel));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		doReindex(
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRel(
				classPK));
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		_reindexCPDefinitionOptionRels(companyId);
	}

	private void _reindexCPDefinitionOptionRels(long companyId)
		throws Exception {

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_cpDefinitionOptionRelLocalService.
				getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(CPDefinitionOptionRel cpDefinitionOptionRel) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(cpDefinitionOptionRel));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index commerce product definition " +
								"option relationship " + cpDefinitionOptionRel,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionOptionRelIndexer.class);

	@Reference
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private Localization _localization;

}