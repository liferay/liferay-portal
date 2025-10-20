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
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.product.channel.CommerceChannelTypeRegistry;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.exception.CommerceChannelTypeException;
import com.liferay.commerce.product.exception.DuplicateCommerceChannelAccountEntryIdException;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRelTable;
import com.liferay.commerce.product.model.CommerceChannelTable;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.product.service.base.CommerceChannelLocalServiceBaseImpl;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
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
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CommerceChannel",
	service = AopService.class
)
public class CommerceChannelLocalServiceImpl
	extends CommerceChannelLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceChannel addCommerceChannel(
			String externalReferenceCode, long accountEntryId, long siteGroupId,
			String name, String type,
			UnicodeProperties typeSettingsUnicodeProperties,
			String commerceCurrencyCode, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(serviceContext.getUserId());

		_validateAccountEntry(0, accountEntryId);
		_validateType(type);

		long commerceChannelId = counterLocalService.increment();

		CommerceChannel commerceChannel = commerceChannelPersistence.create(
			commerceChannelId);

		commerceChannel.setExternalReferenceCode(externalReferenceCode);
		commerceChannel.setCompanyId(user.getCompanyId());
		commerceChannel.setUserId(user.getUserId());
		commerceChannel.setUserName(user.getFullName());
		commerceChannel.setAccountEntryId(accountEntryId);
		commerceChannel.setSiteGroupId(siteGroupId);
		commerceChannel.setName(name);
		commerceChannel.setType(type);
		commerceChannel.setTypeSettingsUnicodeProperties(
			typeSettingsUnicodeProperties);
		commerceChannel.setCommerceCurrencyCode(commerceCurrencyCode);
		commerceChannel.setPriceDisplayType(
			CommercePricingConstants.TAX_EXCLUDED_FROM_PRICE);
		commerceChannel.setDiscountsTargetNetPrice(true);

		commerceChannel = commerceChannelPersistence.update(commerceChannel);

		// Group

		Map<Locale, String> nameMap = Collections.singletonMap(
			serviceContext.getLocale(), name);

		String typeSettings = null;

		if (CommerceChannelConstants.CHANNEL_TYPE_SITE.equals(type)) {
			typeSettings = _getGroupTypeSettings(null, siteGroupId);
		}

		_groupLocalService.addGroup(
			StringPool.BLANK, user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			CommerceChannel.class.getName(), commerceChannelId,
			GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap, null,
			GroupConstants.TYPE_SITE_PRIVATE, typeSettings, false,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, false, false,
			true, null);

		// Repository

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		PortletFileRepositoryUtil.addPortletRepository(
			commerceChannel.getGroupId(),
			CommerceConstants.SERVICE_NAME_COMMERCE_ORDER, serviceContext);

		// Resources

		_resourceLocalService.addModelResources(
			commerceChannel, serviceContext);

		return commerceChannel;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceChannel addOrUpdateCommerceChannel(
			long userId, String externalReferenceCode, long accountEntryId,
			long siteGroupId, String name, String type,
			UnicodeProperties typeSettingsUnicodeProperties,
			String commerceCurrencyCode, ServiceContext serviceContext)
		throws PortalException {

		_validateType(type);

		CommerceChannel commerceChannel = null;

		if (Validator.isNotNull(externalReferenceCode)) {
			User user = _userLocalService.getUser(userId);

			commerceChannel =
				commerceChannelLocalService.
					fetchCommerceChannelByExternalReferenceCode(
						externalReferenceCode, user.getCompanyId());
		}

		if (commerceChannel == null) {
			return commerceChannelLocalService.addCommerceChannel(
				externalReferenceCode, accountEntryId, siteGroupId, name, type,
				typeSettingsUnicodeProperties, commerceCurrencyCode,
				serviceContext);
		}

		return commerceChannelLocalService.updateCommerceChannel(
			commerceChannel.getCommerceChannelId(), accountEntryId, siteGroupId,
			name, type, typeSettingsUnicodeProperties, commerceCurrencyCode,
			commerceChannel.getPriceDisplayType(),
			commerceChannel.isDiscountsTargetNetPrice());
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceChannel deleteCommerceChannel(
			CommerceChannel commerceChannel)
		throws PortalException {

		commerceChannelPersistence.remove(commerceChannel);

		_resourceLocalService.deleteResource(
			commerceChannel, ResourceConstants.SCOPE_INDIVIDUAL);

		Group group = _groupLocalService.fetchGroup(
			commerceChannel.getCompanyId(),
			_classNameLocalService.getClassNameId(
				CommerceChannel.class.getName()),
			commerceChannel.getCommerceChannelId());

		if (group != null) {
			_groupLocalService.deleteGroup(group);
			_repositoryLocalService.deleteRepositories(group.getGroupId());
		}

		_commerceChannelAccountEntryRelLocalService.
			deleteCommerceChannelAccountEntryRelsByCommerceChannelId(
				commerceChannel.getCommerceChannelId());

		_commerceChannelRelLocalService.deleteCommerceChannelRels(
			commerceChannel.getCommerceChannelId());

		return commerceChannel;
	}

	@Override
	public CommerceChannel deleteCommerceChannel(long commerceChannelId)
		throws PortalException {

		CommerceChannel commerceChannel =
			commerceChannelPersistence.findByPrimaryKey(commerceChannelId);

		return commerceChannelLocalService.deleteCommerceChannel(
			commerceChannel);
	}

	@Override
	public void deleteCommerceChannels(long companyId) throws PortalException {
		List<CommerceChannel> commerceChannels =
			commerceChannelPersistence.findByCompanyId(companyId);

		for (CommerceChannel commerceChannel : commerceChannels) {
			commerceChannelLocalService.deleteCommerceChannel(commerceChannel);
		}
	}

	@Override
	public CommerceChannel fetchCommerceChannelByGroupClassPK(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return null;
		}

		return commerceChannelLocalService.fetchCommerceChannel(
			group.getClassPK());
	}

	@Override
	public CommerceChannel fetchCommerceChannelBySiteGroupId(long siteGroupId) {
		return commerceChannelPersistence.fetchBySiteGroupId(siteGroupId);
	}

	@Override
	public Group fetchCommerceChannelGroup(long commerceChannelId)
		throws PortalException {

		CommerceChannel commerceChannel =
			commerceChannelLocalService.getCommerceChannel(commerceChannelId);

		return _groupLocalService.fetchGroup(
			commerceChannel.getCompanyId(),
			_classNameLocalService.getClassNameId(
				CommerceChannel.class.getName()),
			commerceChannelId);
	}

	@Override
	public CommerceChannel getCommerceChannelByGroupId(long groupId)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		return commerceChannelLocalService.getCommerceChannel(
			group.getClassPK());
	}

	@Override
	public CommerceChannel getCommerceChannelByOrderGroupId(long orderGroupId)
		throws PortalException {

		return commerceChannelLocalService.getCommerceChannelByGroupId(
			orderGroupId);
	}

	@Override
	public Group getCommerceChannelGroup(long commerceChannelId)
		throws PortalException {

		Group group = commerceChannelLocalService.fetchCommerceChannelGroup(
			commerceChannelId);

		if (group != null) {
			return group;
		}

		throw new PortalException();
	}

	@Override
	public long getCommerceChannelGroupIdBySiteGroupId(long siteGroupId)
		throws PortalException {

		CommerceChannel commerceChannel =
			commerceChannelPersistence.fetchBySiteGroupId(siteGroupId);

		if (commerceChannel == null) {
			return 0;
		}

		Group group = commerceChannelLocalService.getCommerceChannelGroup(
			commerceChannel.getCommerceChannelId());

		return group.getGroupId();
	}

	@Override
	public List<CommerceChannel> getCommerceChannels(long companyId) {
		return commerceChannelPersistence.findByCompanyId(companyId);
	}

	@Override
	public List<CommerceChannel> getCommerceChannels(
			long companyId, String keywords, int start, int end)
		throws PortalException {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceChannelTable.INSTANCE
				).from(
					CommerceChannelTable.INSTANCE
				),
				companyId, keywords, CommerceChannelTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public List<CommerceChannel> getCommerceChannelsByAccountEntryId(
		long accountEntryId) {

		return commerceChannelPersistence.findByAccountEntryId(accountEntryId);
	}

	@Override
	public int getCommerceChannelsCount(long companyId, String keywords)
		throws PortalException {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					CommerceChannelTable.INSTANCE.commerceChannelId
				).from(
					CommerceChannelTable.INSTANCE
				),
				companyId, keywords, CommerceChannelTable.INSTANCE.name));
	}

	@Override
	public List<CommerceChannel> getEligibleCommerceChannels(
			long accountEntryId, String name, int start, int end)
		throws PortalException {

		return dslQuery(
			DSLQueryFactoryUtil.selectDistinct(
				CommerceChannelTable.INSTANCE
			).from(
				CommerceChannelTable.INSTANCE
			).leftJoinOn(
				CommerceChannelAccountEntryRelTable.INSTANCE,
				CommerceChannelTable.INSTANCE.commerceChannelId.eq(
					CommerceChannelAccountEntryRelTable.INSTANCE.
						commerceChannelId
				).and(
					CommerceChannelAccountEntryRelTable.INSTANCE.type.eq(
						CommerceChannelAccountEntryRelConstants.
							TYPE_ELIGIBILITY)
				)
			).where(
				() -> {
					Predicate predicate =
						CommerceChannelAccountEntryRelTable.INSTANCE.
							accountEntryId.eq(accountEntryId);

					predicate = predicate.or(
						CommerceChannelAccountEntryRelTable.INSTANCE.
							accountEntryId.isNull());

					if (!Validator.isBlank(name)) {
						predicate = CommerceChannelTable.INSTANCE.name.like(
							name);
					}

					return predicate;
				}
			).limit(
				start, end
			));
	}

	@Override
	public List<CommerceChannel> search(long companyId) throws PortalException {
		return search(
			companyId, StringPool.BLANK, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	@Override
	public List<CommerceChannel> search(
			long companyId, String keywords, int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, start, end, sort);

		searchContext.setKeywords(keywords);

		return _search(searchContext);
	}

	@Override
	public int searchCommerceChannelsCount(long companyId, String keywords)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		searchContext.setKeywords(keywords);

		return _searchCommerceChannelsCount(searchContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceChannel updateCommerceChannel(
			long commerceChannelId, long accountEntryId, long siteGroupId,
			String name, String type,
			UnicodeProperties typeSettingsUnicodeProperties,
			String commerceCurrencyCode, String priceDisplayType,
			boolean discountsTargetNetPrice)
		throws PortalException {

		_validateAccountEntry(commerceChannelId, accountEntryId);
		_validateType(type);

		CommerceChannel commerceChannel =
			commerceChannelPersistence.findByPrimaryKey(commerceChannelId);

		long oldSiteGroupId = commerceChannel.getSiteGroupId();

		commerceChannel.setAccountEntryId(accountEntryId);
		commerceChannel.setSiteGroupId(siteGroupId);
		commerceChannel.setName(name);
		commerceChannel.setType(type);
		commerceChannel.setTypeSettingsUnicodeProperties(
			typeSettingsUnicodeProperties);
		commerceChannel.setCommerceCurrencyCode(commerceCurrencyCode);
		commerceChannel.setPriceDisplayType(priceDisplayType);
		commerceChannel.setDiscountsTargetNetPrice(discountsTargetNetPrice);

		commerceChannel = commerceChannelPersistence.update(commerceChannel);

		if (CommerceChannelConstants.CHANNEL_TYPE_SITE.equals(type) &&
			(siteGroupId != oldSiteGroupId)) {

			Group group = commerceChannel.getGroup();

			_groupLocalService.updateGroup(
				group.getGroupId(), _getGroupTypeSettings(group, siteGroupId));
		}

		return commerceChannel;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceChannel updateCommerceChannelExternalReferenceCode(
			String externalReferenceCode, long commerceChannelId)
		throws PortalException {

		CommerceChannel commerceChannel =
			commerceChannelPersistence.findByPrimaryKey(commerceChannelId);

		commerceChannel.setExternalReferenceCode(externalReferenceCode);

		return commerceChannelPersistence.update(commerceChannel);
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

	private List<CommerceChannel> _getCommerceChannels(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<CommerceChannel> commerceChannels = new ArrayList<>(
			documents.size());

		for (Document document : documents) {
			long commerceChannelId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CommerceChannel commerceChannel = fetchCommerceChannel(
				commerceChannelId);

			if (commerceChannel == null) {
				commerceChannels = null;

				Indexer<CommerceChannel> indexer =
					IndexerRegistryUtil.getIndexer(CommerceChannel.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (commerceChannels != null) {
				commerceChannels.add(commerceChannel);
			}
		}

		return commerceChannels;
	}

	private GroupByStep _getGroupByStep(
			JoinStep joinStep, Long companyId, String keywords,
			Expression<String> keywordsPredicateExpression)
		throws PortalException {

		return joinStep.where(
			CommerceChannelTable.INSTANCE.companyId.eq(
				companyId
			).and(
				() -> {
					if (Validator.isNull(keywords)) {
						return null;
					}

					return Predicate.withParentheses(
						_customSQL.getKeywordsPredicate(
							DSLFunctionFactoryUtil.lower(
								keywordsPredicateExpression),
							_customSQL.keywords(keywords, true)));
				}
			));
	}

	private String _getGroupTypeSettings(Group group, long siteGroupId) {
		UnicodeProperties unicodeProperties = new UnicodeProperties(true);

		if (group != null) {
			unicodeProperties = group.getTypeSettingsProperties();
		}

		unicodeProperties.put("siteGroupId", String.valueOf(siteGroupId));

		return unicodeProperties.toString();
	}

	private List<CommerceChannel> _search(SearchContext searchContext)
		throws PortalException {

		Indexer<CommerceChannel> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommerceChannel.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext, _SELECTED_FIELD_NAMES);

			List<CommerceChannel> commerceChannels = _getCommerceChannels(hits);

			if (commerceChannels != null) {
				return commerceChannels;
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	private int _searchCommerceChannelsCount(SearchContext searchContext)
		throws PortalException {

		Indexer<CommerceChannel> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommerceChannel.class);

		return GetterUtil.getInteger(indexer.searchCount(searchContext));
	}

	private void _validateAccountEntry(
			long commerceChannelId, long accountEntryId)
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
				"Channel can only be assigned with an account entry type:" +
					AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER);
		}

		if (accountEntry.getStatus() != WorkflowConstants.STATUS_APPROVED) {
			throw new AccountEntryStatusException(
				"Channel can only be assigned with an approved account entry");
		}

		List<CommerceChannel> commerceChannels =
			getCommerceChannelsByAccountEntryId(accountEntryId);

		if (ListUtil.isNotEmpty(commerceChannels)) {
			if (commerceChannelId > 0) {
				for (CommerceChannel commerceChannel : commerceChannels) {
					if (commerceChannel.getCommerceChannelId() !=
							commerceChannelId) {

						throw new DuplicateCommerceChannelAccountEntryIdException(
							"There is another commerce channel with account " +
								"entry ID " + accountEntryId);
					}
				}
			}
			else {
				throw new DuplicateCommerceChannelAccountEntryIdException(
					"There is another commerce channel with account entry ID " +
						accountEntryId);
			}
		}
	}

	private void _validateType(String type) throws PortalException {
		if (Validator.isNull(type) ||
			(_commerceChannelTypeRegistry.getCommerceChannelType(type) ==
				null)) {

			throw new CommerceChannelTypeException();
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
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	@Reference
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@Reference
	private CommerceChannelTypeRegistry _commerceChannelTypeRegistry;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private RepositoryLocalService _repositoryLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}