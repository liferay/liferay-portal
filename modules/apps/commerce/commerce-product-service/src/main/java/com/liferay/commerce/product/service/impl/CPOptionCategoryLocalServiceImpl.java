/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.constants.CPOptionCategoryConstants;
import com.liferay.commerce.product.exception.CPOptionCategoryKeyException;
import com.liferay.commerce.product.exception.CPOptionCategoryTitleException;
import com.liferay.commerce.product.internal.search.CPOptionCategoryIndexer;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueLocalService;
import com.liferay.commerce.product.service.CPSpecificationOptionLocalService;
import com.liferay.commerce.product.service.base.CPOptionCategoryLocalServiceBaseImpl;
import com.liferay.commerce.product.service.persistence.CPDefinitionSpecificationOptionValuePersistence;
import com.liferay.commerce.product.service.persistence.CPSpecificationOptionPersistence;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
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
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPOptionCategory",
	service = AopService.class
)
public class CPOptionCategoryLocalServiceImpl
	extends CPOptionCategoryLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPOptionCategory addCPOptionCategory(
			String externalReferenceCode, long userId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			double priority, String key, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		key = _friendlyURLNormalizer.normalize(key);

		_validate(0, user.getCompanyId(), key, titleMap);

		long cpOptionCategoryId = counterLocalService.increment();

		CPOptionCategory cpOptionCategory = cpOptionCategoryPersistence.create(
			cpOptionCategoryId);

		cpOptionCategory.setExternalReferenceCode(externalReferenceCode);
		cpOptionCategory.setCompanyId(user.getCompanyId());
		cpOptionCategory.setUserId(user.getUserId());
		cpOptionCategory.setUserName(user.getFullName());
		cpOptionCategory.setTitleMap(titleMap);
		cpOptionCategory.setDescriptionMap(descriptionMap);
		cpOptionCategory.setPriority(priority);
		cpOptionCategory.setKey(key);

		if (_emptyModelManager.isEmptyModel()) {
			cpOptionCategory.setStatus(WorkflowConstants.STATUS_EMPTY);
		}
		else {
			cpOptionCategory.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		cpOptionCategory = cpOptionCategoryPersistence.update(cpOptionCategory);

		// Resources

		_resourceLocalService.addModelResources(
			cpOptionCategory, serviceContext);

		return cpOptionCategory;
	}

	@Override
	public CPOptionCategory addOrUpdateCPOptionCategory(
			String externalReferenceCode, long userId, long cpOptionCategoryId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			double priority, String key, ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isNotNull(externalReferenceCode)) {
			CPOptionCategory cpOptionCategory =
				cpOptionCategoryPersistence.fetchByERC_C(
					externalReferenceCode, serviceContext.getCompanyId());

			if ((cpOptionCategory == null) && (cpOptionCategoryId > 0)) {
				cpOptionCategory =
					cpOptionCategoryPersistence.fetchByPrimaryKey(
						cpOptionCategoryId);
			}

			if (cpOptionCategory != null) {
				return cpOptionCategoryLocalService.updateCPOptionCategory(
					externalReferenceCode,
					cpOptionCategory.getCPOptionCategoryId(), titleMap,
					descriptionMap, priority, key);
			}
		}

		return cpOptionCategoryLocalService.addCPOptionCategory(
			externalReferenceCode, userId, titleMap, descriptionMap, priority,
			key, serviceContext);
	}

	@Override
	public void deleteCPOptionCategories(long companyId)
		throws PortalException {

		List<CPOptionCategory> cpOptionCategories =
			cpOptionCategoryPersistence.findByCompanyId(companyId);

		for (CPOptionCategory cpOptionCategory : cpOptionCategories) {
			cpOptionCategoryLocalService.deleteCPOptionCategory(
				cpOptionCategory);
		}
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPOptionCategory deleteCPOptionCategory(
			CPOptionCategory cpOptionCategory)
		throws PortalException {

		// Commerce product option category

		cpOptionCategoryPersistence.remove(cpOptionCategory);

		// Resources

		_resourceLocalService.deleteResource(
			cpOptionCategory, ResourceConstants.SCOPE_INDIVIDUAL);

		// Commerce product specification options

		List<CPSpecificationOption> cpSpecificationOptions =
			_cpSpecificationOptionPersistence.findByCPOptionCategoryId(
				cpOptionCategory.getCPOptionCategoryId());

		for (CPSpecificationOption cpSpecificationOption :
				cpSpecificationOptions) {

			_cpSpecificationOptionLocalService.updateCPOptionCategoryId(
				cpSpecificationOption.getCPSpecificationOptionId(),
				CPOptionCategoryConstants.DEFAULT_CP_OPTION_CATEGORY_ID);
		}

		// Commerce product definition specification option values

		List<CPDefinitionSpecificationOptionValue>
			cpDefinitionSpecificationOptionValues =
				_cpDefinitionSpecificationOptionValuePersistence.
					findByCPOptionCategoryId(
						cpOptionCategory.getCPOptionCategoryId());

		for (CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue :
					cpDefinitionSpecificationOptionValues) {

			_cpDefinitionSpecificationOptionValueLocalService.
				updateCPOptionCategoryId(
					cpDefinitionSpecificationOptionValue.
						getCPDefinitionSpecificationOptionValueId(),
					CPOptionCategoryConstants.DEFAULT_CP_OPTION_CATEGORY_ID);
		}

		return cpOptionCategory;
	}

	@Override
	public CPOptionCategory deleteCPOptionCategory(long cpOptionCategoryId)
		throws PortalException {

		CPOptionCategory cpOptionCategory =
			cpOptionCategoryPersistence.findByPrimaryKey(cpOptionCategoryId);

		return cpOptionCategoryLocalService.deleteCPOptionCategory(
			cpOptionCategory);
	}

	@Override
	public CPOptionCategory fetchCPOptionCategory(long companyId, String key) {
		return cpOptionCategoryPersistence.fetchByC_K(companyId, key);
	}

	@Override
	public List<CPOptionCategory> getCPOptionCategories(
		long companyId, int start, int end) {

		return cpOptionCategoryPersistence.findByCompanyId(
			companyId, start, end);
	}

	@Override
	public CPOptionCategory getCPOptionCategory(long companyId, String key)
		throws PortalException {

		return cpOptionCategoryPersistence.findByC_K(companyId, key);
	}

	@Override
	public CPOptionCategory getOrAddEmptyCPOptionCategory(
			String externalReferenceCode, long companyId, long userId)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		return _emptyModelManager.getOrAddEmptyModel(
			CPOptionCategory.class, companyId,
			() -> cpOptionCategoryLocalService.addCPOptionCategory(
				externalReferenceCode, userId,
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), externalReferenceCode),
				null, 0, externalReferenceCode, serviceContext),
			externalReferenceCode,
			this::fetchCPOptionCategoryByExternalReferenceCode,
			this::getCPOptionCategoryByExternalReferenceCode,
			CPOptionCategory.class.getName());
	}

	@Override
	public BaseModelSearchResult<CPOptionCategory> searchCPOptionCategories(
			long companyId, String keywords, int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, keywords, start, end, sort);

		return _searchCPOptionCategories(searchContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPOptionCategory updateCPOptionCategory(
			String externalReferenceCode, long cpOptionCategoryId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			double priority, String key)
		throws PortalException {

		CPOptionCategory cpOptionCategory =
			cpOptionCategoryPersistence.findByPrimaryKey(cpOptionCategoryId);

		key = _friendlyURLNormalizer.normalize(key);

		_validate(
			cpOptionCategory.getCPOptionCategoryId(),
			cpOptionCategory.getCompanyId(), key, titleMap);

		cpOptionCategory.setExternalReferenceCode(externalReferenceCode);
		cpOptionCategory.setTitleMap(titleMap);
		cpOptionCategory.setDescriptionMap(descriptionMap);
		cpOptionCategory.setPriority(priority);
		cpOptionCategory.setKey(key);
		cpOptionCategory.setStatus(
			_emptyModelManager.solveEmptyModel(
				cpOptionCategory.getExternalReferenceCode(),
				cpOptionCategory.getModelClassName(),
				cpOptionCategory.getCompanyId(), 0,
				cpOptionCategory.getStatus(),
				() -> WorkflowConstants.STATUS_APPROVED));

		return cpOptionCategoryPersistence.update(cpOptionCategory);
	}

	private SearchContext _buildSearchContext(
		long companyId, String keywords, int start, int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				CPOptionCategoryIndexer.FIELD_KEY, keywords
			).put(
				Field.DESCRIPTION, keywords
			).put(
				Field.ENTRY_CLASS_PK, keywords
			).put(
				Field.TITLE, keywords
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

	private List<CPOptionCategory> _getCPOptionCategories(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		return TransformUtil.transform(
			documents,
			document -> {
				long cpOptionCategoryId = GetterUtil.getLong(
					document.get(Field.ENTRY_CLASS_PK));

				CPOptionCategory cpOptionCategory = fetchCPOptionCategory(
					cpOptionCategoryId);

				if (cpOptionCategory != null) {
					return cpOptionCategory;
				}

				Indexer<CPOptionCategory> indexer =
					IndexerRegistryUtil.getIndexer(CPOptionCategory.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());

				return null;
			});
	}

	private BaseModelSearchResult<CPOptionCategory> _searchCPOptionCategories(
			SearchContext searchContext)
		throws PortalException {

		Indexer<CPOptionCategory> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CPOptionCategory.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext, _SELECTED_FIELD_NAMES);

			return new BaseModelSearchResult<>(
				_getCPOptionCategories(hits), hits.getLength());
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	private void _validate(
			long cpOptionCategoryId, long companyId, String key,
			Map<Locale, String> titleMap)
		throws PortalException {

		if (Validator.isNull(key)) {
			throw new CPOptionCategoryKeyException("Key is null");
		}

		CPOptionCategory cpOptionCategory =
			cpOptionCategoryPersistence.fetchByC_K(companyId, key);

		if ((cpOptionCategory != null) &&
			(cpOptionCategory.getCPOptionCategoryId() != cpOptionCategoryId)) {

			throw new CPOptionCategoryKeyException();
		}

		if (MapUtil.isEmpty(titleMap)) {
			throw new CPOptionCategoryTitleException("Title is empty");
		}
	}

	private static final String[] _SELECTED_FIELD_NAMES = {
		Field.ENTRY_CLASS_PK, Field.COMPANY_ID, Field.UID
	};

	@Reference
	private CPDefinitionSpecificationOptionValueLocalService
		_cpDefinitionSpecificationOptionValueLocalService;

	@Reference
	private CPDefinitionSpecificationOptionValuePersistence
		_cpDefinitionSpecificationOptionValuePersistence;

	@Reference
	private CPSpecificationOptionLocalService
		_cpSpecificationOptionLocalService;

	@Reference
	private CPSpecificationOptionPersistence _cpSpecificationOptionPersistence;

	@Reference
	private EmptyModelManager _emptyModelManager;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}