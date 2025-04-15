/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.search;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.commerce.product.service.CPOptionValueLocalService;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
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
public class CPOptionValueIndexer extends BaseIndexer<CPOptionValue> {

	public static final String CLASS_NAME = CPOptionValue.class.getName();

	public CPOptionValueIndexer() {
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.GROUP_ID, Field.MODIFIED_DATE, Field.NAME,
			Field.SCOPE_GROUP_ID, Field.UID, CPField.CP_OPTION_ID, CPField.KEY);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		long cpOptionId = GetterUtil.getLong(
			searchContext.getAttribute(CPField.CP_OPTION_ID));

		if (cpOptionId > 0) {
			contextBooleanFilter.addRequiredTerm(
				CPField.CP_OPTION_ID, cpOptionId);
		}
	}

	@Override
	protected void doDelete(CPOptionValue cpOptionValue) throws Exception {
		deleteDocument(
			cpOptionValue.getCompanyId(), cpOptionValue.getCPOptionValueId());
	}

	@Override
	protected Document doGetDocument(CPOptionValue cpOptionValue)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Indexing commerce product option value " + cpOptionValue);
		}

		Document document = getBaseModelDocument(CLASS_NAME, cpOptionValue);

		String cpOptionValueDefaultLanguageId =
			_localization.getDefaultLanguageId(cpOptionValue.getName());

		String[] languageIds = _localization.getAvailableLanguageIds(
			cpOptionValue.getName());

		for (String languageId : languageIds) {
			String name = cpOptionValue.getName(languageId);

			document.addNumber(
				CPField.CP_OPTION_ID, cpOptionValue.getCPOptionId());
			document.addText(CPField.KEY, cpOptionValue.getKey());
			document.addText(Field.CONTENT, name);
			document.addText(
				_localization.getLocalizedName(Field.NAME, languageId), name);
			document.addNumber(Field.PRIORITY, cpOptionValue.getPriority());

			if (languageId.equals(cpOptionValueDefaultLanguageId)) {
				document.addText(Field.NAME, name);
				document.addText("defaultLanguageId", languageId);
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Commerce product option value " + cpOptionValue +
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
	protected void doReindex(CPOptionValue cpOptionValue) throws Exception {
		_indexWriterHelper.updateDocument(
			cpOptionValue.getCompanyId(), getDocument(cpOptionValue));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		doReindex(_cpOptionValueLocalService.getCPOptionValue(classPK));
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		_reindexCPOptionValues(companyId);
	}

	private void _reindexCPOptionValues(long companyId) throws Exception {
		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_cpOptionValueLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(CPOptionValue cpOptionValue) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(cpOptionValue));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index commerce product option value " +
								cpOptionValue,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPOptionValueIndexer.class);

	@Reference
	private CPOptionValueLocalService _cpOptionValueLocalService;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private Localization _localization;

}