/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.configuration.CPOptionConfiguration;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.exception.CPOptionKeyException;
import com.liferay.commerce.product.exception.CPOptionSKUContributorException;
import com.liferay.commerce.product.exception.RequiredCPOptionException;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPOptionValueLocalService;
import com.liferay.commerce.product.service.base.CPOptionLocalServiceBaseImpl;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Igor Beslic
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPOption",
	service = AopService.class
)
public class CPOptionLocalServiceImpl extends CPOptionLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPOption addCPOption(
			String externalReferenceCode, long userId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			String commerceOptionTypeKey, boolean facetable, boolean required,
			boolean skuContributor, String key, ServiceContext serviceContext)
		throws PortalException {

		if (!_emptyModelManager.isEmptyModel()) {
			_validateCommerceOptionTypeKey(
				commerceOptionTypeKey, skuContributor);
		}

		User user = _userLocalService.getUser(userId);

		key = _friendlyURLNormalizer.normalizeWithPeriodsAndSlashes(key);

		_validateCPOptionKey(0, user.getCompanyId(), key);

		long cpOptionId = counterLocalService.increment();

		CPOption cpOption = cpOptionPersistence.create(cpOptionId);

		cpOption.setExternalReferenceCode(externalReferenceCode);
		cpOption.setCompanyId(user.getCompanyId());
		cpOption.setUserId(user.getUserId());
		cpOption.setUserName(user.getFullName());
		cpOption.setNameMap(nameMap);
		cpOption.setDescriptionMap(descriptionMap);
		cpOption.setCommerceOptionTypeKey(commerceOptionTypeKey);
		cpOption.setFacetable(facetable);
		cpOption.setRequired(required);
		cpOption.setSkuContributor(skuContributor);
		cpOption.setKey(key);

		if (_emptyModelManager.isEmptyModel()) {
			cpOption.setStatus(WorkflowConstants.STATUS_EMPTY);
		}
		else {
			cpOption.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		cpOption.setExpandoBridgeAttributes(serviceContext);

		cpOption = cpOptionPersistence.update(cpOption);

		// Resources

		_resourceLocalService.addModelResources(cpOption, serviceContext);

		return cpOption;
	}

	@Override
	public CPOption addOrUpdateCPOption(
			String externalReferenceCode, long userId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			String commerceOptionTypeKey, boolean facetable, boolean required,
			boolean skuContributor, String key, ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isNotNull(externalReferenceCode)) {
			CPOption cpOption = cpOptionPersistence.fetchByERC_C(
				externalReferenceCode, serviceContext.getCompanyId());

			if (cpOption != null) {
				return updateCPOption(
					cpOption.getCPOptionId(), nameMap, descriptionMap,
					commerceOptionTypeKey, facetable, required, skuContributor,
					key, serviceContext);
			}
		}

		return addCPOption(
			externalReferenceCode, userId, nameMap, descriptionMap,
			commerceOptionTypeKey, facetable, required, skuContributor, key,
			serviceContext);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPOption deleteCPOption(CPOption cpOption) throws PortalException {

		// Commerce product option rels

		CPDefinitionOptionRelLocalService cpDefinitionOptionRelLocalService =
			_cpDefinitionOptionRelLocalServiceSnapshot.get();

		int count =
			cpDefinitionOptionRelLocalService.
				getCPOptionCPDefinitionOptionRelsCount(
					cpOption.getCPOptionId());

		if (count > 0) {
			throw new RequiredCPOptionException();
		}

		// Commerce product option

		cpOptionPersistence.remove(cpOption);

		// Commerce product option values

		_cpOptionValueLocalService.deleteCPOptionValues(
			cpOption.getCPOptionId());

		// Resources

		_resourceLocalService.deleteResource(
			cpOption, ResourceConstants.SCOPE_INDIVIDUAL);

		// Expando

		_expandoRowLocalService.deleteRows(cpOption.getCPOptionId());

		return cpOption;
	}

	@Override
	public CPOption deleteCPOption(long cpOptionId) throws PortalException {
		CPOption cpOption = cpOptionPersistence.findByPrimaryKey(cpOptionId);

		return cpOptionLocalService.deleteCPOption(cpOption);
	}

	@Override
	public void deleteCPOptions(long companyId) throws PortalException {
		List<CPOption> cpOptions = cpOptionPersistence.findByCompanyId(
			companyId);

		for (CPOption cpOption : cpOptions) {
			cpOptionLocalService.deleteCPOption(cpOption);
		}
	}

	@Override
	public CPOption fetchCPOption(long companyId, String key) {
		return cpOptionPersistence.fetchByC_K(companyId, key);
	}

	@Override
	public List<CPOption> findCPOptionByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPOption> orderByComparator) {

		return cpOptionPersistence.filterFindByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public CPOption getCPOption(long companyId, String key)
		throws PortalException {

		return cpOptionPersistence.findByC_K(companyId, key);
	}

	@Override
	public int getCPOptionsCount(long companyId) {
		return cpOptionPersistence.countByCompanyId(companyId);
	}

	@Override
	public CPOption getOrAddEmptyCPOption(
			String externalReferenceCode, long companyId, long userId)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		return _emptyModelManager.getOrAddEmptyModel(
			CPOption.class, companyId,
			() -> cpOptionLocalService.addCPOption(
				externalReferenceCode, userId,
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), externalReferenceCode),
				null, StringPool.BLANK, false, false, false,
				externalReferenceCode, serviceContext),
			externalReferenceCode, this::fetchCPOptionByExternalReferenceCode,
			this::getCPOptionByExternalReferenceCode, CPOption.class.getName());
	}

	@Override
	public BaseModelSearchResult<CPOption> searchCPOptions(
			long companyId, String keywords, int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, keywords, start, end, sort);

		return _searchCPOptions(searchContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPOption updateCPOption(
			long cpOptionId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, String commerceOptionTypeKey,
			boolean facetable, boolean required, boolean skuContributor,
			String key, ServiceContext serviceContext)
		throws PortalException {

		_validateCommerceOptionTypeKey(commerceOptionTypeKey, skuContributor);

		CPOption cpOption = cpOptionPersistence.findByPrimaryKey(cpOptionId);

		key = _friendlyURLNormalizer.normalizeWithPeriodsAndSlashes(key);

		_validateCPOptionKey(
			cpOption.getCPOptionId(), cpOption.getCompanyId(), key);

		cpOption.setNameMap(nameMap);
		cpOption.setDescriptionMap(descriptionMap);
		cpOption.setCommerceOptionTypeKey(commerceOptionTypeKey);
		cpOption.setFacetable(facetable);
		cpOption.setRequired(required);
		cpOption.setSkuContributor(skuContributor);
		cpOption.setKey(key);
		cpOption.setStatus(
			_emptyModelManager.solveEmptyModel(
				cpOption.getExternalReferenceCode(),
				cpOption.getModelClassName(), cpOption.getCompanyId(), 0,
				cpOption.getStatus(), () -> WorkflowConstants.STATUS_APPROVED));
		cpOption.setExpandoBridgeAttributes(serviceContext);

		return cpOptionPersistence.update(cpOption);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPOption updateCPOptionExternalReferenceCode(
			String externalReferenceCode, long cpOptionId)
		throws PortalException {

		CPOption cpOption = cpOptionPersistence.findByPrimaryKey(cpOptionId);

		cpOption.setExternalReferenceCode(externalReferenceCode);

		return cpOptionPersistence.update(cpOption);
	}

	private SearchContext _buildSearchContext(
		long companyId, String keywords, int start, int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				CPField.KEY, keywords
			).put(
				Field.CONTENT, keywords
			).put(
				Field.DESCRIPTION, keywords
			).put(
				Field.ENTRY_CLASS_PK, keywords
			).put(
				Field.NAME, keywords
			).put(
				"params",
				LinkedHashMapBuilder.<String, Object>put(
					"keywords", keywords
				).build()
			).build());
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private CPOptionConfiguration _getCPOptionConfiguration()
		throws ConfigurationException {

		return _configurationProvider.getConfiguration(
			CPOptionConfiguration.class,
			new SystemSettingsLocator(CPConstants.SERVICE_NAME_CP_OPTION));
	}

	private List<CPOption> _getCPOptions(Hits hits) throws PortalException {
		List<Document> documents = hits.toList();

		List<CPOption> cpOptions = new ArrayList<>(documents.size());

		for (Document document : documents) {
			long cpOptionId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CPOption cpOption = fetchCPOption(cpOptionId);

			if (cpOption == null) {
				cpOptions = null;

				Indexer<CPOption> indexer = IndexerRegistryUtil.getIndexer(
					CPOption.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (cpOptions != null) {
				cpOptions.add(cpOption);
			}
		}

		return cpOptions;
	}

	private BaseModelSearchResult<CPOption> _searchCPOptions(
			SearchContext searchContext)
		throws PortalException {

		Indexer<CPOption> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			CPOption.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext, _SELECTED_FIELD_NAMES);

			List<CPOption> cpOptions = _getCPOptions(hits);

			if (cpOptions != null) {
				return new BaseModelSearchResult<>(cpOptions, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	private void _validateCommerceOptionTypeKey(
			String commerceOptionTypeKey, boolean skuContributor)
		throws PortalException {

		if (Validator.isNull(commerceOptionTypeKey)) {
			throw new CPOptionSKUContributorException();
		}

		CPOptionConfiguration cpOptionConfiguration =
			_getCPOptionConfiguration();

		String[] allowedCommerceOptionTypes =
			cpOptionConfiguration.allowedCommerceOptionTypes();

		if (skuContributor) {
			allowedCommerceOptionTypes =
				CPConstants.PRODUCT_OPTION_SKU_CONTRIBUTOR_FIELD_TYPES;
		}

		if (ArrayUtil.contains(
				allowedCommerceOptionTypes, commerceOptionTypeKey)) {

			return;
		}

		throw new CPOptionSKUContributorException();
	}

	private void _validateCPOptionKey(
			long cpOptionId, long companyId, String key)
		throws PortalException {

		CPOption cpOption = cpOptionPersistence.fetchByC_K(companyId, key);

		if ((cpOption != null) && (cpOption.getCPOptionId() != cpOptionId)) {
			throw new CPOptionKeyException();
		}
	}

	private static final String[] _SELECTED_FIELD_NAMES = {
		Field.ENTRY_CLASS_PK, Field.COMPANY_ID, Field.UID
	};

	private static final Snapshot<CPDefinitionOptionRelLocalService>
		_cpDefinitionOptionRelLocalServiceSnapshot = new Snapshot<>(
			CPOptionLocalServiceImpl.class,
			CPDefinitionOptionRelLocalService.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPOptionValueLocalService _cpOptionValueLocalService;

	@Reference
	private EmptyModelManager _emptyModelManager;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}