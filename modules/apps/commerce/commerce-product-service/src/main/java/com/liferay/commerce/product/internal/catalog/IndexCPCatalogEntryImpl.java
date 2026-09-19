/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.catalog;

import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPSku;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.helper.CPInstanceHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
public class IndexCPCatalogEntryImpl implements CPCatalogEntry {

	public IndexCPCatalogEntryImpl(
		Document document, CPDefinitionLocalService cpDefinitionLocalService,
		CPDefinitionOptionRelLocalService cpDefinitionOptionRelLocalService,
		CPInstanceHelper cpInstanceHelper,
		CPInstanceLocalService cpInstanceLocalService, Locale locale) {

		_document = document;
		_cpDefinitionLocalService = cpDefinitionLocalService;
		_cpDefinitionOptionRelLocalService = cpDefinitionOptionRelLocalService;
		_cpInstanceHelper = cpInstanceHelper;
		_cpInstanceLocalService = cpInstanceLocalService;
		_locale = locale;
	}

	@Override
	public long getCPDefinitionId() {
		return GetterUtil.getLong(_document.get(Field.ENTRY_CLASS_PK));
	}

	@Override
	public List<CPDefinitionOptionRel> getCPDefinitionOptionRels() {
		return _cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<CPSku> getCPSkus() {
		List<CPSku> cpSkus = new ArrayList<>();

		CPDefinition cpDefinition = _cpDefinitionLocalService.fetchCPDefinition(
			getCPDefinitionId());

		if (cpDefinition == null) {
			return cpSkus;
		}

		List<CPInstance> cpInstances =
			_cpInstanceLocalService.getCPDefinitionInstances(
				cpDefinition.getCPDefinitionId(),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		for (CPInstance cpInstance : cpInstances) {
			cpSkus.add(
				new CPSkuImpl(
					cpInstance,
					_cpInstanceHelper.fetchCPInstanceUnitPrice(cpInstance),
					_cpInstanceHelper.fetchCPInstanceUnitPromoPrice(
						cpInstance)));
		}

		return cpSkus;
	}

	@Override
	public long getCProductId() {
		return GetterUtil.getLong(_document.get(CPField.PRODUCT_ID));
	}

	@Override
	public double getDepth() {
		return GetterUtil.getDouble(_document.get(CPField.DEPTH));
	}

	@Override
	public String getDescription() {
		return _document.get(_locale, Field.DESCRIPTION);
	}

	@Override
	public long getGroupId() {
		return GetterUtil.getLong(_document.get(Field.GROUP_ID));
	}

	@Override
	public double getHeight() {
		return GetterUtil.getDouble(_document.get(CPField.HEIGHT));
	}

	@Override
	public String getMetaDescription(String languageId) {
		return _document.get(_locale, CPField.META_DESCRIPTION);
	}

	@Override
	public String getMetaKeywords(String languageId) {
		return _document.get(_locale, CPField.META_KEYWORDS);
	}

	@Override
	public String getMetaTitle(String languageId) {
		return _document.get(_locale, CPField.META_TITLE);
	}

	@Override
	public String getName() {
		return _document.get(_locale, Field.NAME);
	}

	@Override
	public String getProductTypeName() {
		return _document.get(CPField.PRODUCT_TYPE_NAME);
	}

	@Override
	public String getShortDescription() {
		return _document.get(_locale, CPField.SHORT_DESCRIPTION);
	}

	@Override
	public String getUrl() {
		return _document.get(_locale, Field.URL);
	}

	@Override
	public boolean isIgnoreSKUCombinations() {
		return GetterUtil.getBoolean(
			_document.get(CPField.IS_IGNORE_SKU_COMBINATIONS));
	}

	private final CPDefinitionLocalService _cpDefinitionLocalService;
	private final CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;
	private final CPInstanceHelper _cpInstanceHelper;
	private final CPInstanceLocalService _cpInstanceLocalService;
	private final Document _document;
	private final Locale _locale;

}