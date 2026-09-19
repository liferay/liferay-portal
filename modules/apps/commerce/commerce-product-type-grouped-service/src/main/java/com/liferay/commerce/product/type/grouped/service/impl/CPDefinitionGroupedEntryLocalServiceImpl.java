/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.grouped.service.impl;

import com.liferay.commerce.product.exception.CPDefinitionProductTypeNameException;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.type.grouped.constants.GroupedCPTypeConstants;
import com.liferay.commerce.product.type.grouped.exception.CPDefinitionGroupedEntryQuantityException;
import com.liferay.commerce.product.type.grouped.exception.DuplicateCPDefinitionGroupedEntryException;
import com.liferay.commerce.product.type.grouped.model.CPDefinitionGroupedEntry;
import com.liferay.commerce.product.type.grouped.service.base.CPDefinitionGroupedEntryLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.type.grouped.model.CPDefinitionGroupedEntry",
	service = AopService.class
)
public class CPDefinitionGroupedEntryLocalServiceImpl
	extends CPDefinitionGroupedEntryLocalServiceBaseImpl {

	@Override
	public void addCPDefinitionGroupedEntries(
			long cpDefinitionId, long[] entryCProductIds,
			ServiceContext serviceContext)
		throws PortalException {

		for (long entryCProductId : entryCProductIds) {
			cpDefinitionGroupedEntryLocalService.addCPDefinitionGroupedEntry(
				cpDefinitionId, entryCProductId, 0, 1, serviceContext);
		}
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDefinitionGroupedEntry addCPDefinitionGroupedEntry(
			long cpDefinitionId, long entryCProductId, double priority,
			int quantity, ServiceContext serviceContext)
		throws PortalException {

		CPDefinitionGroupedEntry cpDefinitionGroupedEntry =
			cpDefinitionGroupedEntryPersistence.fetchByC_E(
				cpDefinitionId, entryCProductId);

		if (cpDefinitionGroupedEntry != null) {
			throw new DuplicateCPDefinitionGroupedEntryException();
		}

		_validate(cpDefinitionId, entryCProductId, quantity);

		cpDefinitionGroupedEntry = cpDefinitionGroupedEntryPersistence.create(
			counterLocalService.increment());

		CPDefinition cpDefinition = null;

		if (_cpDefinitionLocalService.isVersionable(cpDefinitionId)) {
			cpDefinition = _cpDefinitionLocalService.copyCPDefinition(
				cpDefinitionId);
		}
		else {
			cpDefinition = _cpDefinitionLocalService.getCPDefinition(
				cpDefinitionId);
		}

		cpDefinitionGroupedEntry.setGroupId(cpDefinition.getGroupId());

		User user = _userLocalService.getUser(serviceContext.getUserId());

		cpDefinitionGroupedEntry.setCompanyId(user.getCompanyId());
		cpDefinitionGroupedEntry.setUserId(user.getUserId());
		cpDefinitionGroupedEntry.setUserName(user.getFullName());

		cpDefinitionGroupedEntry.setCPDefinitionId(
			cpDefinition.getCPDefinitionId());
		cpDefinitionGroupedEntry.setEntryCProductId(entryCProductId);
		cpDefinitionGroupedEntry.setPriority(priority);
		cpDefinitionGroupedEntry.setQuantity(quantity);

		return cpDefinitionGroupedEntryPersistence.update(
			cpDefinitionGroupedEntry);
	}

	@Override
	public void cloneCPDefinitionGroupedEntries(
		long cpDefinitionId, long newCPDefinitionId) {

		List<CPDefinitionGroupedEntry> cpDefinitionGroupedEntries =
			cpDefinitionGroupedEntryLocalService.
				getCPDefinitionGroupedEntriesByCPDefinitionId(cpDefinitionId);

		for (CPDefinitionGroupedEntry cpDefinitionGroupedEntry :
				cpDefinitionGroupedEntries) {

			CPDefinitionGroupedEntry newCPDefinitionGroupedEntry =
				(CPDefinitionGroupedEntry)cpDefinitionGroupedEntry.clone();

			newCPDefinitionGroupedEntry.setUuid(PortalUUIDUtil.generate());
			newCPDefinitionGroupedEntry.setCPDefinitionGroupedEntryId(
				counterLocalService.increment());
			newCPDefinitionGroupedEntry.setCPDefinitionId(newCPDefinitionId);

			cpDefinitionGroupedEntryLocalService.addCPDefinitionGroupedEntry(
				newCPDefinitionGroupedEntry);
		}
	}

	@Override
	public void deleteCPDefinitionGroupedEntries(long cpDefinitionId) {
		if (_cpDefinitionLocalService.isVersionable(cpDefinitionId)) {
			try {
				CPDefinition newCPDefinition =
					_cpDefinitionLocalService.copyCPDefinition(cpDefinitionId);

				cpDefinitionId = newCPDefinition.getCPDefinitionId();
			}
			catch (PortalException portalException) {
				throw new SystemException(portalException);
			}
		}

		cpDefinitionGroupedEntryPersistence.removeByCPDefinitionId(
			cpDefinitionId);
	}

	@Override
	public void deleteCPDefinitionGroupedEntriesByEntryCProductId(
		long entryCProductId) {

		cpDefinitionGroupedEntryPersistence.removeByEntryCProductId(
			entryCProductId);
	}

	@Override
	public CPDefinitionGroupedEntry fetchCPDefinitionGroupedEntry(
		long cpDefinitionId, long entryCProductId) {

		return cpDefinitionGroupedEntryPersistence.fetchByC_E(
			cpDefinitionId, entryCProductId);
	}

	@Override
	public List<CPDefinitionGroupedEntry> getCPDefinitionGroupedEntries(
		long cpDefinitionId) {

		return cpDefinitionGroupedEntryPersistence.findByCPDefinitionId(
			cpDefinitionId);
	}

	@Override
	public List<CPDefinitionGroupedEntry> getCPDefinitionGroupedEntries(
		long cpDefinitionId, int start, int end,
		OrderByComparator<CPDefinitionGroupedEntry> orderByComparator) {

		return cpDefinitionGroupedEntryPersistence.findByCPDefinitionId(
			cpDefinitionId, start, end, orderByComparator);
	}

	@Override
	public List<CPDefinitionGroupedEntry> getCPDefinitionGroupedEntries(
			long companyId, long cpDefinitionId, String keywords, int start,
			int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, cpDefinitionId, keywords, start, end, sort);

		BaseModelSearchResult<CPDefinitionGroupedEntry> baseModelSearchResult =
			cpDefinitionGroupedEntryLocalService.
				searchCPDefinitionGroupedEntries(searchContext);

		return baseModelSearchResult.getBaseModels();
	}

	@Override
	public List<CPDefinitionGroupedEntry>
		getCPDefinitionGroupedEntriesByCPDefinitionId(long cpDefinitionId) {

		return cpDefinitionGroupedEntryPersistence.findByCPDefinitionId(
			cpDefinitionId);
	}

	@Override
	public int getCPDefinitionGroupedEntriesCount(long cpDefinitionId) {
		return cpDefinitionGroupedEntryPersistence.countByCPDefinitionId(
			cpDefinitionId);
	}

	@Override
	public int getCPDefinitionGroupedEntriesCount(
			long companyId, long cpDefinitionId, String keywords)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, cpDefinitionId, keywords, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		return cpDefinitionGroupedEntryLocalService.
			searchCPDefinitionGroupedEntriesCount(searchContext);
	}

	@Override
	public List<CPDefinitionGroupedEntry>
			getEntryCProductCPDefinitionGroupedEntries(
				long entryCProductId, int start, int end,
				OrderByComparator<CPDefinitionGroupedEntry> orderByComparator)
		throws PortalException {

		return cpDefinitionGroupedEntryPersistence.findByEntryCProductId(
			entryCProductId, start, end, orderByComparator);
	}

	@Override
	public BaseModelSearchResult<CPDefinitionGroupedEntry>
			searchCPDefinitionGroupedEntries(SearchContext searchContext)
		throws PortalException {

		Indexer<CPDefinitionGroupedEntry> indexer =
			_indexerRegistry.nullSafeGetIndexer(CPDefinitionGroupedEntry.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext);

			List<CPDefinitionGroupedEntry> cpDefinitionGroupedEntries =
				_getCPDefinitionGroupedEntries(hits);

			if (cpDefinitionGroupedEntries != null) {
				return new BaseModelSearchResult<>(
					cpDefinitionGroupedEntries, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	@Override
	public int searchCPDefinitionGroupedEntriesCount(
			SearchContext searchContext)
		throws PortalException {

		Indexer<CPDefinitionGroupedEntry> indexer =
			_indexerRegistry.nullSafeGetIndexer(CPDefinitionGroupedEntry.class);

		return GetterUtil.getInteger(indexer.searchCount(searchContext));
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDefinitionGroupedEntry updateCPDefinitionGroupedEntry(
			long cpDefinitionGroupedEntryId, double priority, int quantity)
		throws PortalException {

		CPDefinitionGroupedEntry cpDefinitionGroupedEntry =
			cpDefinitionGroupedEntryPersistence.findByPrimaryKey(
				cpDefinitionGroupedEntryId);

		if (_cpDefinitionLocalService.isVersionable(
				cpDefinitionGroupedEntry.getCPDefinitionId())) {

			CPDefinition newCPDefinition =
				_cpDefinitionLocalService.copyCPDefinition(
					cpDefinitionGroupedEntry.getCPDefinitionId());

			cpDefinitionGroupedEntry =
				cpDefinitionGroupedEntryPersistence.findByC_E(
					newCPDefinition.getCPDefinitionId(),
					cpDefinitionGroupedEntry.getEntryCProductId());
		}

		_validate(
			cpDefinitionGroupedEntry.getCPDefinitionId(),
			cpDefinitionGroupedEntry.getEntryCProductId(), quantity);

		cpDefinitionGroupedEntry.setPriority(priority);
		cpDefinitionGroupedEntry.setQuantity(quantity);

		return cpDefinitionGroupedEntryPersistence.update(
			cpDefinitionGroupedEntry);
	}

	private SearchContext _buildSearchContext(
		long companyId, long cpDefinitionId, String keywords, int start,
		int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		if (cpDefinitionId > 0) {
			searchContext.setAttribute("cpDefinitionId", cpDefinitionId);
		}

		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setKeywords(keywords);
		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		return searchContext;
	}

	private List<CPDefinitionGroupedEntry> _getCPDefinitionGroupedEntries(
			Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<CPDefinitionGroupedEntry> cpDefinitionGroupedEntries =
			new ArrayList<>(documents.size());

		for (Document document : documents) {
			long cpDefinitionGroupedEntryId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CPDefinitionGroupedEntry cpDefinitionGroupedEntry =
				fetchCPDefinitionGroupedEntry(cpDefinitionGroupedEntryId);

			if (cpDefinitionGroupedEntry == null) {
				cpDefinitionGroupedEntries = null;

				Indexer<CPDefinitionGroupedEntry> indexer =
					_indexerRegistry.getIndexer(CPDefinitionGroupedEntry.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (cpDefinitionGroupedEntries != null) {
				cpDefinitionGroupedEntries.add(cpDefinitionGroupedEntry);
			}
		}

		return cpDefinitionGroupedEntries;
	}

	private void _validate(
			long cpDefinitionId, long entryCProductId, int quantity)
		throws PortalException {

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		if (!GroupedCPTypeConstants.NAME.equals(
				cpDefinition.getProductTypeName())) {

			throw new CPDefinitionProductTypeNameException();
		}

		CProduct entryCProduct = _cProductLocalService.getCProduct(
			entryCProductId);

		CPDefinition entryCPDefinition =
			_cpDefinitionLocalService.getCPDefinition(
				entryCProduct.getPublishedCPDefinitionId());

		if ((cpDefinitionId == entryCProduct.getPublishedCPDefinitionId()) ||
			GroupedCPTypeConstants.NAME.equals(
				entryCPDefinition.getProductTypeName())) {

			throw new NoSuchCPDefinitionException();
		}

		if (quantity <= 0) {
			throw new CPDefinitionGroupedEntryQuantityException();
		}
	}

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private UserLocalService _userLocalService;

}