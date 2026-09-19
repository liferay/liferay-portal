/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.catalog;

import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPSku;
import com.liferay.commerce.product.helper.CPInstanceHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.Locale;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
public class DatabaseCPCatalogEntryImpl implements CPCatalogEntry {

	public DatabaseCPCatalogEntryImpl(
		CPDefinition cpDefinition,
		CPDefinitionOptionRelLocalService cpDefinitionOptionRelLocalService,
		CPInstanceHelper cpInstanceHelper,
		CPInstanceLocalService cpInstanceLocalService, Locale locale) {

		_cpDefinition = cpDefinition;
		_cpDefinitionOptionRelLocalService = cpDefinitionOptionRelLocalService;
		_cpInstanceHelper = cpInstanceHelper;
		_cpInstanceLocalService = cpInstanceLocalService;

		_languageId = LanguageUtil.getLanguageId(locale);
	}

	@Override
	public long getCPDefinitionId() {
		return _cpDefinition.getCPDefinitionId();
	}

	@Override
	public List<CPDefinitionOptionRel> getCPDefinitionOptionRels() {
		return _cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<CPSku> getCPSkus() {
		return TransformUtil.transform(
			_cpInstanceLocalService.getCPDefinitionInstances(
				_cpDefinition.getCPDefinitionId(),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null),
			cpInstance -> new CPSkuImpl(
				cpInstance,
				_cpInstanceHelper.fetchCPInstanceUnitPrice(cpInstance),
				_cpInstanceHelper.fetchCPInstanceUnitPromoPrice(cpInstance)));
	}

	@Override
	public long getCProductId() {
		return _cpDefinition.getCProductId();
	}

	@Override
	public double getDepth() {
		return _cpDefinition.getDepth();
	}

	@Override
	public String getDescription() {
		return _cpDefinition.getDescription(_languageId);
	}

	@Override
	public long getGroupId() {
		return _cpDefinition.getGroupId();
	}

	@Override
	public double getHeight() {
		return _cpDefinition.getHeight();
	}

	@Override
	public String getMetaDescription(String languageId) {
		return _cpDefinition.getMetaDescription(languageId);
	}

	@Override
	public String getMetaKeywords(String languageId) {
		return _cpDefinition.getMetaKeywords(languageId);
	}

	@Override
	public String getMetaTitle(String languageId) {
		return _cpDefinition.getMetaTitle(languageId);
	}

	@Override
	public String getName() {
		return _cpDefinition.getName(_languageId);
	}

	@Override
	public String getProductTypeName() {
		return _cpDefinition.getProductTypeName();
	}

	@Override
	public String getShortDescription() {
		return _cpDefinition.getShortDescription(_languageId);
	}

	@Override
	public String getUrl() {
		return _cpDefinition.getURL(_languageId);
	}

	@Override
	public boolean isIgnoreSKUCombinations() {
		return _cpDefinition.isIgnoreSKUCombinations();
	}

	private final CPDefinition _cpDefinition;
	private final CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;
	private final CPInstanceHelper _cpInstanceHelper;
	private final CPInstanceLocalService _cpInstanceLocalService;
	private final String _languageId;

}