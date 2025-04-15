/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.resource.v1_0;

import com.liferay.account.exception.NoSuchEntryException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPQuery;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.exception.NoSuchCProductException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.ProductDTOConverterContext;
import com.liferay.headless.commerce.delivery.catalog.internal.odata.entity.v1_0.ProductEntityModel;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.ProductResource;
import com.liferay.headless.common.spi.odata.entity.EntityFieldsUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.MatchAllQuery;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.expando.ExpandoBridgeIndexer;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.MultivaluedMap;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 * @author Eduardo Diniz
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/product.properties",
	scope = ServiceScope.PROTOTYPE, service = ProductResource.class
)
@CTAware
public class ProductResourceImpl extends BaseProductResourceImpl {

	@Override
	public Product getChannelProduct(
			Long channelId, Long productId, Long accountId)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionLocalService.fetchCPDefinitionByCProductId(productId);

		if (cpDefinition == null) {
			throw new NoSuchCProductException();
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(channelId);

		Long commerceAccountId = _getCommerceAccountId(
			accountId, commerceChannel);

		if (!_isAccountEntryEligible(
				commerceAccountId, commerceChannel.getCommerceChannelId())) {

			return null;
		}

		_commerceProductViewPermission.check(
			PermissionThreadLocal.getPermissionChecker(), commerceAccountId,
			commerceChannel.getGroupId(), cpDefinition.getCPDefinitionId());

		CommerceContext commerceContext = _commerceContextFactory.create(
			commerceAccountId, commerceChannel.getGroupId(), null, 0,
			contextCompany.getCompanyId());

		if (FeatureFlagManagerUtil.isEnabled("LPD-10889") &&
			!cpDefinition.isVisible(
				commerceContext.getCPConfigurationListId(
					cpDefinition.getGroupId()))) {

			return null;
		}

		return _toProduct(commerceContext, cpDefinition);
	}

	@Override
	public Product getChannelProductByFriendlyUrlPath(
			Long channelId, String friendlyUrlPath, Long accountId)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(channelId);

		Long commerceAccountId = _getCommerceAccountId(
			accountId, commerceChannel);

		if (!_isAccountEntryEligible(
				commerceAccountId, commerceChannel.getCommerceChannelId())) {

			return null;
		}

		Group group = _groupLocalService.getCompanyGroup(
			commerceChannel.getCompanyId());

		CPDefinition cpDefinition =
			_cpDefinitionLocalService.fetchCPDefinitionByFriendlyURL(
				group.getGroupId(), friendlyUrlPath);

		if (cpDefinition == null) {
			throw new NoSuchCProductException();
		}

		_commerceProductViewPermission.check(
			PermissionThreadLocal.getPermissionChecker(), commerceAccountId,
			commerceChannel.getGroupId(), cpDefinition.getCPDefinitionId());

		return _toProduct(
			_commerceContextFactory.create(
				commerceAccountId, commerceChannel.getGroupId(), null, 0,
				contextCompany.getCompanyId()),
			cpDefinition);
	}

	@Override
	public Page<Product> getChannelProductsPage(
			Long channelId, Long accountId, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(channelId);

		Long commerceAccountId = _getCommerceAccountId(
			accountId, commerceChannel);

		if (!_isAccountEntryEligible(
				commerceAccountId, commerceChannel.getCommerceChannelId())) {

			return Page.of(
				Collections.emptyList(),
				Pagination.of(pagination.getPage(), pagination.getPageSize()),
				0);
		}

		SearchContext searchContext = new SearchContext();

		CommerceContext commerceContext = _commerceContextFactory.create(
			commerceAccountId, commerceChannel.getGroupId(), null, 0,
			contextCompany.getCompanyId());

		if (FeatureFlagManagerUtil.isEnabled("LPD-10889")) {
			searchContext.setAttribute(
				CPField.CP_CONFIGURATION_LIST_IDS,
				commerceContext.getCPConfigurationListIds());
		}

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				Field.STATUS, WorkflowConstants.STATUS_APPROVED
			).put(
				"accountEntryId", commerceAccountId
			).put(
				"commerceAccountGroupIds",
				_accountGroupLocalService.getAccountGroupIds(commerceAccountId)
			).put(
				"commerceChannelGroupId", commerceChannel.getGroupId()
			).build());

		searchContext.setBooleanClauses(
			new BooleanClause[] {
				_getBooleanClause(
					booleanQuery -> booleanQuery.getPreBooleanFilter(), filter)
			});
		searchContext.setCompanyId(contextCompany.getCompanyId());
		searchContext.setKeywords(search);

		CPQuery cpQuery = new CPQuery();

		cpQuery.setOrderByCol1("title");
		cpQuery.setOrderByCol2("modifiedDate");
		cpQuery.setOrderByType1("ASC");
		cpQuery.setOrderByType2("DESC");

		return Page.of(
			_toProducts(
				commerceContext,
				_cpDefinitionHelper.search(
					commerceChannel.getGroupId(), searchContext, cpQuery,
					pagination.getStartPosition(),
					pagination.getEndPosition())),
			pagination,
			_cpDefinitionHelper.searchCount(
				commerceChannel.getGroupId(), searchContext, cpQuery));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return new ProductEntityModel(
			EntityFieldsUtil.getEntityFields(
				_portal.getClassNameId(CPDefinition.class.getName()),
				contextCompany.getCompanyId(), _expandoBridgeIndexer,
				_expandoColumnLocalService, _expandoTableLocalService));
	}

	private BooleanClause<Query> _getBooleanClause(
			UnsafeConsumer<BooleanQuery, Exception> booleanQueryUnsafeConsumer,
			Filter filter)
		throws Exception {

		BooleanQuery booleanQuery = new BooleanQueryImpl() {
			{
				add(new MatchAllQuery(), BooleanClauseOccur.MUST);

				BooleanFilter booleanFilter = new BooleanFilter();

				if (filter != null) {
					booleanFilter.add(filter, BooleanClauseOccur.MUST);
				}

				setPreBooleanFilter(booleanFilter);
			}
		};

		booleanQueryUnsafeConsumer.accept(booleanQuery);

		return BooleanClauseFactoryUtil.create(
			booleanQuery, BooleanClauseOccur.MUST.getName());
	}

	private Long _getCommerceAccountId(
			Long accountId, CommerceChannel commerceChannel)
		throws Exception {

		int countUserCommerceAccounts =
			_commerceAccountHelper.countUserCommerceAccounts(
				contextUser.getUserId(), commerceChannel.getGroupId());

		if (countUserCommerceAccounts > 1) {
			if (accountId == null) {
				throw new NoSuchEntryException();
			}
		}
		else {
			long[] commerceAccountIds =
				_commerceAccountHelper.getUserCommerceAccountIds(
					contextUser.getUserId(), commerceChannel.getGroupId());

			if (commerceAccountIds.length == 0) {
				AccountEntry accountEntry =
					_accountEntryLocalService.getGuestAccountEntry(
						contextCompany.getCompanyId());

				commerceAccountIds = new long[] {
					accountEntry.getAccountEntryId()
				};
			}

			return commerceAccountIds[0];
		}

		return accountId;
	}

	private boolean _isAccountEntryEligible(
		long accountEntryId, long commerceChannelId) {

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_commerceChannelAccountEntryRelLocalService.
				fetchCommerceChannelAccountEntryRel(
					accountEntryId, commerceChannelId,
					CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY);

		int count =
			_commerceChannelAccountEntryRelLocalService.
				getCommerceChannelAccountEntryRelsCount(
					commerceChannelId, null,
					CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY);

		if ((commerceChannelAccountEntryRel == null) && (count > 0)) {
			return false;
		}

		return true;
	}

	private Product _toProduct(
			CommerceContext commerceContext, CPDefinition cpDefinition)
		throws Exception {

		return _productDTOConverter.toDTO(
			new ProductDTOConverterContext(
				commerceContext, cpDefinition, cpDefinition.getCPDefinitionId(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	private List<Product> _toProducts(
			CommerceContext commerceContext,
			CPDataSourceResult cpDataSourceResult)
		throws Exception {

		List<Product> products = new ArrayList<>();

		for (CPCatalogEntry cpCatalogEntry :
				cpDataSourceResult.getCPCatalogEntries()) {

			products.add(
				_productDTOConverter.toDTO(
					new ProductDTOConverterContext(
						commerceContext, cpCatalogEntry,
						cpCatalogEntry.getCPDefinitionId(),
						contextAcceptLanguage.getPreferredLocale())));
		}

		return products;
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private ExpandoBridgeIndexer _expandoBridgeIndexer;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.ProductDTOConverter)"
	)
	private DTOConverter<CPDefinition, Product> _productDTOConverter;

}