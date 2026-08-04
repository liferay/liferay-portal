/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.exception.CPOptionValueKeyException;
import com.liferay.commerce.product.exception.CPOptionValueNameException;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.commerce.product.service.base.CPOptionValueLocalServiceBaseImpl;
import com.liferay.commerce.product.service.persistence.CPOptionPersistence;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPOptionValue",
	service = AopService.class
)
public class CPOptionValueLocalServiceImpl
	extends CPOptionValueLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPOptionValue addCPOptionValue(
			long cpOptionId, Map<Locale, String> nameMap, double priority,
			String key, ServiceContext serviceContext)
		throws PortalException {

		return cpOptionValueLocalService.addCPOptionValue(
			StringPool.BLANK, cpOptionId, nameMap, priority, key,
			serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPOptionValue addCPOptionValue(
			String externalReferenceCode, long cpOptionId,
			Map<Locale, String> nameMap, double priority, String key,
			ServiceContext serviceContext)
		throws PortalException {

		// Commerce product option value

		User user = _userLocalService.getUser(serviceContext.getUserId());

		CPOption cpOption = _cpOptionPersistence.findByPrimaryKey(cpOptionId);

		key = _friendlyURLNormalizer.normalize(key);

		if (!_emptyModelManager.isEmptyModel()) {
			_validateKey(cpOption, 0, key);
		}

		_validateName(nameMap);

		long cpOptionValueId = counterLocalService.increment();

		CPOptionValue cpOptionValue = cpOptionValuePersistence.create(
			cpOptionValueId);

		cpOptionValue.setExternalReferenceCode(externalReferenceCode);
		cpOptionValue.setCompanyId(user.getCompanyId());
		cpOptionValue.setUserId(user.getUserId());
		cpOptionValue.setUserName(user.getFullName());
		cpOptionValue.setCPOptionId(cpOptionId);
		cpOptionValue.setNameMap(nameMap);
		cpOptionValue.setPriority(priority);
		cpOptionValue.setKey(key);

		if (_emptyModelManager.isEmptyModel()) {
			cpOptionValue.setStatus(WorkflowConstants.STATUS_EMPTY);
		}
		else {
			cpOptionValue.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		cpOptionValue.setExpandoBridgeAttributes(serviceContext);

		cpOptionValue = cpOptionValuePersistence.update(cpOptionValue);

		_reindexCPOption(cpOptionId);

		return cpOptionValue;
	}

	@Override
	public CPOptionValue addOrUpdateCPOptionValue(
			String externalReferenceCode, long cpOptionId,
			Map<Locale, String> nameMap, double priority, String key,
			ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isNotNull(externalReferenceCode)) {
			CPOptionValue cpOptionValue = cpOptionValuePersistence.fetchByERC_C(
				externalReferenceCode, serviceContext.getCompanyId());

			if (cpOptionValue != null) {
				return cpOptionValueLocalService.updateCPOptionValue(
					cpOptionValue.getCPOptionValueId(), nameMap, priority, key,
					serviceContext);
			}
		}

		return cpOptionValueLocalService.addCPOptionValue(
			externalReferenceCode, cpOptionId, nameMap, priority, key,
			serviceContext);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPOptionValue deleteCPOptionValue(CPOptionValue cpOptionValue)
		throws PortalException {

		// Commerce product option value

		cpOptionValuePersistence.remove(cpOptionValue);

		// Expando

		_expandoRowLocalService.deleteRows(cpOptionValue.getCPOptionValueId());

		_reindexCPOption(cpOptionValue.getCPOptionId());

		return cpOptionValue;
	}

	@Override
	public CPOptionValue deleteCPOptionValue(long cpOptionValueId)
		throws PortalException {

		CPOptionValue cpOptionValue = cpOptionValuePersistence.findByPrimaryKey(
			cpOptionValueId);

		return cpOptionValueLocalService.deleteCPOptionValue(cpOptionValue);
	}

	@Override
	public void deleteCPOptionValues(long cpOptionId) throws PortalException {
		List<CPOptionValue> cpOptionValues =
			cpOptionValuePersistence.findByCPOptionId(
				cpOptionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CPOptionValue cpOptionValue : cpOptionValues) {
			cpOptionValueLocalService.deleteCPOptionValue(cpOptionValue);
		}
	}

	@Override
	public CPOptionValue getCPOptionValue(long cpOptionId, String key)
		throws PortalException {

		return cpOptionValuePersistence.findByC_K(cpOptionId, key);
	}

	@Override
	public List<CPOptionValue> getCPOptionValues(
		long cpOptionId, int start, int end) {

		return cpOptionValuePersistence.findByCPOptionId(
			cpOptionId, start, end);
	}

	@Override
	public List<CPOptionValue> getCPOptionValues(
		long cpOptionId, int start, int end,
		OrderByComparator<CPOptionValue> orderByComparator) {

		return cpOptionValuePersistence.findByCPOptionId(
			cpOptionId, start, end, orderByComparator);
	}

	@Override
	public int getCPOptionValuesCount(long cpOptionId) {
		return cpOptionValuePersistence.countByCPOptionId(cpOptionId);
	}

	@Override
	public CPOptionValue getOrAddEmptyCPOptionValue(
			String externalReferenceCode, long cpOptionId, long companyId,
			long userId)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		return _emptyModelManager.getOrAddEmptyModel(
			CPOptionValue.class, companyId,
			() -> cpOptionValueLocalService.addCPOptionValue(
				externalReferenceCode, cpOptionId,
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), externalReferenceCode),
				0, externalReferenceCode, serviceContext),
			externalReferenceCode,
			this::fetchCPOptionValueByExternalReferenceCode,
			this::getCPOptionValueByExternalReferenceCode,
			CPOptionValue.class.getName());
	}

	@Override
	public Hits search(SearchContext searchContext) {
		try {
			Indexer<CPOptionValue> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(CPOptionValue.class);

			return indexer.search(searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public BaseModelSearchResult<CPOptionValue> searchCPOptionValues(
			long companyId, long cpOptionId, String keywords, int start,
			int end, Sort[] sorts)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, cpOptionId, keywords, start, end, sorts);

		return _searchCPOptionValues(searchContext);
	}

	@Override
	public int searchCPOptionValuesCount(
			long companyId, long cpOptionId, String keywords)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, cpOptionId, keywords, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		return _searchCPOptionValuesCount(searchContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPOptionValue updateCPOptionValue(
			long cpOptionValueId, Map<Locale, String> nameMap, double priority,
			String key, ServiceContext serviceContext)
		throws PortalException {

		// Commerce product option value

		CPOptionValue cpOptionValue = cpOptionValuePersistence.findByPrimaryKey(
			cpOptionValueId);

		key = _friendlyURLNormalizer.normalize(key);

		_validateKey(
			cpOptionValue.getCPOption(), cpOptionValue.getCPOptionValueId(),
			key);

		_validateName(nameMap);

		cpOptionValue.setNameMap(nameMap);
		cpOptionValue.setPriority(priority);
		cpOptionValue.setKey(key);
		cpOptionValue.setStatus(
			_emptyModelManager.solveEmptyModel(
				cpOptionValue.getExternalReferenceCode(),
				cpOptionValue.getModelClassName(), cpOptionValue.getCompanyId(),
				0, cpOptionValue.getStatus(),
				() -> WorkflowConstants.STATUS_APPROVED));
		cpOptionValue.setExpandoBridgeAttributes(serviceContext);

		cpOptionValue = cpOptionValuePersistence.update(cpOptionValue);

		_reindexCPOption(cpOptionValue.getCPOptionId());

		return cpOptionValue;
	}

	private SearchContext _buildSearchContext(
		long companyId, long cpOptionId, String keywords, int start, int end,
		Sort[] sorts) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				_FIELD_KEY, keywords
			).put(
				Field.CONTENT, keywords
			).put(
				Field.ENTRY_CLASS_PK, keywords
			).put(
				Field.NAME, keywords
			).put(
				"CPOptionId", cpOptionId
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

		if (sorts != null) {
			searchContext.setSorts(sorts);
		}

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private List<CPOptionValue> _getCPOptionValues(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<CPOptionValue> cpOptionValues = new ArrayList<>(documents.size());

		for (Document document : documents) {
			long cpOptionValueId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CPOptionValue cpOptionValue = fetchCPOptionValue(cpOptionValueId);

			if (cpOptionValue == null) {
				cpOptionValues = null;

				Indexer<CPOptionValue> indexer = IndexerRegistryUtil.getIndexer(
					CPOptionValue.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (cpOptionValues != null) {
				cpOptionValues.add(cpOptionValue);
			}
		}

		return cpOptionValues;
	}

	private String _getTimeZone(String[] splits) {
		if ((splits == null) || (splits.length < 7) || splits[7].isEmpty()) {
			return StringPool.BLANK;
		}

		if (splits.length == 8) {
			return splits[7].toUpperCase();
		}

		String timeZone = StringBundler.concat(
			StringUtil.upperCaseFirstLetter(splits[7]),
			StringPool.FORWARD_SLASH,
			StringUtil.upperCaseFirstLetter(splits[8]));

		if ((splits.length > 9) && Validator.isNotNull(splits[9])) {
			return StringBundler.concat(
				timeZone, StringPool.UNDERLINE,
				StringUtil.upperCaseFirstLetter(splits[9]));
		}

		return timeZone;
	}

	private void _reindexCPOption(long cpOptionId) throws PortalException {
		Indexer<CPOption> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			CPOption.class);

		indexer.reindex(CPOption.class.getName(), cpOptionId);
	}

	private BaseModelSearchResult<CPOptionValue> _searchCPOptionValues(
			SearchContext searchContext)
		throws PortalException {

		Indexer<CPOptionValue> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			CPOptionValue.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext, _SELECTED_FIELD_NAMES);

			List<CPOptionValue> cpOptionValues = _getCPOptionValues(hits);

			if (cpOptionValues != null) {
				return new BaseModelSearchResult<>(
					cpOptionValues, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	private int _searchCPOptionValuesCount(SearchContext searchContext)
		throws PortalException {

		Indexer<CPOptionValue> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			CPOptionValue.class);

		return GetterUtil.getInteger(indexer.searchCount(searchContext));
	}

	private void _validateKey(
			CPOption cpOption, long cpOptionValueId, String key)
		throws PortalException {

		if (Validator.isBlank(key)) {
			throw new CPOptionValueKeyException("Key is null");
		}

		CPOptionValue cpOptionValue = cpOptionValuePersistence.fetchByC_K(
			cpOption.getCPOptionId(), key);

		if ((cpOptionValue != null) &&
			(cpOptionValue.getCPOptionValueId() != cpOptionValueId)) {

			throw new CPOptionValueKeyException("Duplicate key " + key);
		}

		if (Objects.equals(
				CPConstants.PRODUCT_OPTION_SELECT_DATE_KEY,
				cpOption.getCommerceOptionTypeKey())) {

			if (key == null) {
				throw new CPOptionValueKeyException("Key is mandatory");
			}

			if (!key.matches("^[a-z0-9-]*$")) {
				throw new CPOptionValueKeyException("Invalid key");
			}

			String[] splits = key.split(StringPool.DASH);

			Integer month = 0;
			Integer day = 0;
			Integer year = 0;
			Integer hour = 0;
			Integer minute = 0;

			try {
				month = Integer.valueOf(splits[0]);
				day = Integer.valueOf(splits[1]);
				year = Integer.valueOf(splits[2]);
				hour = Integer.valueOf(splits[3]);
				minute = Integer.valueOf(splits[4]);
				Integer.valueOf(splits[5]);
			}
			catch (NumberFormatException numberFormatException) {
				throw new CPOptionValueKeyException(
					"Invalid date", numberFormatException);
			}

			_portal.getDate(
				month - 1, day, year, hour, minute,
				TimeZoneUtil.getTimeZone(_getTimeZone(splits)),
				CPOptionValueKeyException.class);

			if (!Objects.equals(CPConstants.DAYS_DURATION_TYPE, splits[6]) &&
				!Objects.equals(CPConstants.HOURS_DURATION_TYPE, splits[6])) {

				throw new CPOptionValueKeyException("Invalid duration type");
			}
		}
	}

	private void _validateName(Map<Locale, String> nameMap)
		throws PortalException {

		Locale locale = LocaleUtil.getSiteDefault();

		if ((nameMap == null) || Validator.isNull(nameMap.get(locale))) {
			throw new CPOptionValueNameException(
				"Name is null for locale " + locale.getDisplayName());
		}
	}

	private static final String _FIELD_KEY = "key";

	private static final String[] _SELECTED_FIELD_NAMES = {
		Field.ENTRY_CLASS_PK, Field.COMPANY_ID, Field.GROUP_ID, Field.UID
	};

	@Reference
	private CPOptionPersistence _cpOptionPersistence;

	@Reference
	private EmptyModelManager _emptyModelManager;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}