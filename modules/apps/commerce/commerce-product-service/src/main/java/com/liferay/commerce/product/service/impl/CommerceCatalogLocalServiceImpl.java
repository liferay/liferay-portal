/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.exception.AccountEntryStatusException;
import com.liferay.account.exception.AccountEntryTypeException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.product.constants.CommerceCatalogConstants;
import com.liferay.commerce.product.exception.CommerceCatalogProductsException;
import com.liferay.commerce.product.exception.CommerceCatalogSystemException;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.base.CommerceCatalogLocalServiceBaseImpl;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
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
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CommerceCatalog",
	service = AopService.class
)
public class CommerceCatalogLocalServiceImpl
	extends CommerceCatalogLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceCatalog addCommerceCatalog(
			String externalReferenceCode, long accountEntryId, String name,
			String commerceCurrencyCode, String catalogDefaultLanguageId,
			boolean system, ServiceContext serviceContext)
		throws PortalException {

		_validateAccountEntry(accountEntryId);

		User user = _userLocalService.getUser(serviceContext.getUserId());

		long commerceCatalogId = counterLocalService.increment();

		CommerceCatalog commerceCatalog = commerceCatalogPersistence.create(
			commerceCatalogId);

		commerceCatalog.setExternalReferenceCode(externalReferenceCode);
		commerceCatalog.setCompanyId(user.getCompanyId());
		commerceCatalog.setUserId(user.getUserId());
		commerceCatalog.setUserName(user.getFullName());
		commerceCatalog.setAccountEntryId(accountEntryId);
		commerceCatalog.setName(name);
		commerceCatalog.setCommerceCurrencyCode(commerceCurrencyCode);
		commerceCatalog.setCatalogDefaultLanguageId(catalogDefaultLanguageId);
		commerceCatalog.setSystem(system);

		// Group

		_groupLocalService.addGroup(
			user.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
			CommerceCatalog.class.getName(), commerceCatalogId,
			GroupConstants.DEFAULT_LIVE_GROUP_ID, getLocalizationMap(name),
			null, GroupConstants.TYPE_SITE_PRIVATE, false,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, false, true,
			null);

		commerceCatalog = commerceCatalogPersistence.update(commerceCatalog);

		// Resources

		_resourceLocalService.addModelResources(
			commerceCatalog, serviceContext);

		// Commerce product configuration list

		Date date = new Date();

		Calendar calendar = CalendarFactoryUtil.getCalendar(date.getTime());

		int displayDateHour = calendar.get(Calendar.HOUR);

		if (calendar.get(Calendar.AM_PM) == Calendar.PM) {
			displayDateHour += 12;
		}

		_cpConfigurationListLocalService.addCPConfigurationList(
			null, user.getUserId(), commerceCatalog.getGroupId(), 0, true,
			_language.format(
				LocaleUtil.fromLanguageId(
					commerceCatalog.getCatalogDefaultLanguageId()),
				"master-configuration-x", commerceCatalog.getName(), false),
			0D, calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR),
			displayDateHour, calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0,
			true);

		return commerceCatalog;
	}

	@Override
	public CommerceCatalog addCommerceCatalog(
			String externalReferenceCode, String name,
			String commerceCurrencyCode, String catalogDefaultLanguageId,
			ServiceContext serviceContext)
		throws PortalException {

		return commerceCatalogLocalService.addCommerceCatalog(
			externalReferenceCode, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			name, commerceCurrencyCode, catalogDefaultLanguageId, false,
			serviceContext);
	}

	@Override
	public CommerceCatalog addDefaultCommerceCatalog(long companyId)
		throws PortalException {

		Company company = _companyLocalService.getCompany(companyId);

		User guestUser = company.getGuestUser();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(company.getCompanyId());
		serviceContext.setUserId(guestUser.getUserId());
		serviceContext.setUuid(PortalUUIDUtil.generate());

		return commerceCatalogLocalService.addCommerceCatalog(
			null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			CommerceCatalogConstants.MASTER_COMMERCE_CATALOG,
			CommerceCatalogConstants.MASTER_COMMERCE_DEFAULT_CURRENCY,
			guestUser.getLanguageId(), true, serviceContext);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceCatalog deleteCommerceCatalog(
			CommerceCatalog commerceCatalog)
		throws PortalException {

		_validate(commerceCatalog);

		long groupId = commerceCatalog.getGroupId();

		// Commerce catalog

		commerceCatalogPersistence.remove(commerceCatalog);

		// Group

		_groupLocalService.deleteGroup(groupId);

		// Resources

		_resourceLocalService.deleteResource(
			commerceCatalog, ResourceConstants.SCOPE_INDIVIDUAL);

		return commerceCatalog;
	}

	@Override
	public CommerceCatalog deleteCommerceCatalog(long commerceCatalogId)
		throws PortalException {

		CommerceCatalog commerceCatalog =
			commerceCatalogPersistence.findByPrimaryKey(commerceCatalogId);

		return commerceCatalogLocalService.deleteCommerceCatalog(
			commerceCatalog);
	}

	@Override
	public void deleteCommerceCatalogs(long companyId) throws PortalException {
		List<CommerceCatalog> commerceCatalogs =
			commerceCatalogPersistence.findByCompanyId(companyId);

		for (CommerceCatalog commerceCatalog : commerceCatalogs) {
			commerceCatalogLocalService.forceDeleteCommerceCatalog(
				commerceCatalog);
		}
	}

	@Override
	public CommerceCatalog fetchCommerceCatalogByGroupId(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if ((group != null) &&
			(group.getClassNameId() == _classNameLocalService.getClassNameId(
				CommerceCatalog.class))) {

			return fetchCommerceCatalog(group.getClassPK());
		}

		return null;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceCatalog forceDeleteCommerceCatalog(
			CommerceCatalog commerceCatalog)
		throws PortalException {

		// Commerce catalog

		commerceCatalogPersistence.remove(commerceCatalog);

		// Resources

		_resourceLocalService.deleteResource(
			commerceCatalog, ResourceConstants.SCOPE_INDIVIDUAL);

		// Group

		Group group = _groupLocalService.fetchGroup(
			commerceCatalog.getCompanyId(),
			_classNameLocalService.getClassNameId(
				CommerceCatalog.class.getName()),
			commerceCatalog.getCommerceCatalogId());

		if (group != null) {
			for (CPConfigurationList cpConfigurationList :
					_cpConfigurationListLocalService.getCPConfigurationLists(
						group.getGroupId(), commerceCatalog.getCompanyId())) {

				_cpConfigurationListLocalService.deleteCPConfigurationList(
					cpConfigurationList, true);
			}

			_groupLocalService.deleteGroup(group);
		}

		return commerceCatalog;
	}

	@Override
	public Group getCommerceCatalogGroup(long commerceCatalogId)
		throws PortalException {

		CommerceCatalog commerceCatalog =
			commerceCatalogLocalService.getCommerceCatalog(commerceCatalogId);

		Group group = _groupLocalService.fetchGroup(
			commerceCatalog.getCompanyId(),
			_classNameLocalService.getClassNameId(
				CommerceCatalog.class.getName()),
			commerceCatalogId);

		if (group != null) {
			return group;
		}

		throw new PortalException();
	}

	@Override
	public List<CommerceCatalog> getCommerceCatalogs(long companyId) {
		return commerceCatalogPersistence.findByCompanyId(companyId);
	}

	@Override
	public List<CommerceCatalog> getCommerceCatalogs(
		long companyId, boolean system) {

		return commerceCatalogPersistence.findByC_S(companyId, system);
	}

	@Override
	public List<CommerceCatalog> getCommerceCatalogsByAccountEntryId(
		long accountEntryId) {

		return commerceCatalogPersistence.findByAccountEntryId(accountEntryId);
	}

	@Override
	public List<CommerceCatalog> search(long companyId) throws PortalException {
		return search(
			companyId, StringPool.BLANK, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	@Override
	public List<CommerceCatalog> search(
			long companyId, String keywords, int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, start, end, sort);

		searchContext.setKeywords(keywords);

		return _search(searchContext);
	}

	@Override
	public int searchCommerceCatalogsCount(long companyId, String keywords)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		searchContext.setKeywords(keywords);

		return _searchCommerceCatalogsCount(searchContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceCatalog updateCommerceCatalog(
			long commerceCatalogId, long accountEntryId, String name,
			String commerceCurrencyCode, String catalogDefaultLanguageId)
		throws PortalException {

		_validateAccountEntry(accountEntryId);

		CommerceCatalog commerceCatalog =
			commerceCatalogPersistence.findByPrimaryKey(commerceCatalogId);

		commerceCatalog.setAccountEntryId(accountEntryId);
		commerceCatalog.setName(name);
		commerceCatalog.setCommerceCurrencyCode(commerceCurrencyCode);
		commerceCatalog.setCatalogDefaultLanguageId(catalogDefaultLanguageId);

		return commerceCatalogPersistence.update(commerceCatalog);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceCatalog updateCommerceCatalogExternalReferenceCode(
			String externalReferenceCode, long commerceCatalogId)
		throws PortalException {

		CommerceCatalog commerceCatalog =
			commerceCatalogPersistence.findByPrimaryKey(commerceCatalogId);

		commerceCatalog.setExternalReferenceCode(externalReferenceCode);

		return commerceCatalogPersistence.update(commerceCatalog);
	}

	private SearchContext _buildSearchContext(
		long companyId, int start, int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private List<CommerceCatalog> _getCommerceCatalogs(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<CommerceCatalog> commerceCatalogs = new ArrayList<>(
			documents.size());

		for (Document document : documents) {
			long commerceCatalogId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CommerceCatalog commerceCatalog = fetchCommerceCatalog(
				commerceCatalogId);

			if (commerceCatalog == null) {
				commerceCatalogs = null;

				Indexer<CommerceCatalog> indexer =
					IndexerRegistryUtil.getIndexer(CommerceCatalog.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (commerceCatalogs != null) {
				commerceCatalogs.add(commerceCatalog);
			}
		}

		return commerceCatalogs;
	}

	private List<CommerceCatalog> _search(SearchContext searchContext)
		throws PortalException {

		Indexer<CommerceCatalog> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommerceCatalog.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext, _SELECTED_FIELD_NAMES);

			List<CommerceCatalog> commerceCatalogs = _getCommerceCatalogs(hits);

			if (commerceCatalogs != null) {
				return commerceCatalogs;
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	private int _searchCommerceCatalogsCount(SearchContext searchContext)
		throws PortalException {

		Indexer<CommerceCatalog> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommerceCatalog.class);

		return GetterUtil.getInteger(indexer.searchCount(searchContext));
	}

	private void _validate(CommerceCatalog commerceCatalog)
		throws PortalException {

		if (commerceCatalog.isSystem()) {
			throw new CommerceCatalogSystemException();
		}

		int cpDefinitionsCount =
			_cpDefinitionLocalService.getCPDefinitionsCount(
				commerceCatalog.getGroupId(), WorkflowConstants.STATUS_ANY);

		if (cpDefinitionsCount > 0) {
			throw new CommerceCatalogProductsException();
		}
	}

	private void _validateAccountEntry(long accountEntryId)
		throws PortalException {

		if (accountEntryId == 0) {
			return;
		}

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			accountEntryId);

		if (!StringUtil.equals(
				accountEntry.getType(),
				AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER)) {

			throw new AccountEntryTypeException(
				"Commerce catalogs can only be assigned with an account " +
					"entry type:" +
						AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER);
		}

		if (accountEntry.getStatus() != WorkflowConstants.STATUS_APPROVED) {
			throw new AccountEntryStatusException(
				"Commerce catalogs can only be assigned with an approved " +
					"account entry");
		}
	}

	private static final String[] _SELECTED_FIELD_NAMES = {
		Field.ENTRY_CLASS_PK, Field.COMPANY_ID
	};

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}