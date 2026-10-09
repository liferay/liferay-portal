/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.search.spi.model.query.contributor;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.configuration.CommerceAccountGroupServiceConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.util.AccountEntryAllowedTypesUtil;
import com.liferay.commerce.util.CommerceChannelConfigurationUtil;
import com.liferay.commerce.util.CommerceGroupThreadLocal;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.MissingFilter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = "indexer.class.name=com.liferay.commerce.model.CommerceOrder",
	service = ModelPreFilterContributor.class
)
public class CommerceOrderModelPreFilterContributor
	implements ModelPreFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings,
		SearchContext searchContext) {

		_filterByCommerceAccountIds(booleanFilter, searchContext);
		_filterByGroupIds(booleanFilter, searchContext);
		_filterByOrderStatuses(booleanFilter, searchContext);
		_filterByUserId(booleanFilter);
	}

	private void _filterByCommerceAccountIds(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		long[] commerceAccountIds = GetterUtil.getLongValues(
			searchContext.getAttribute("commerceAccountIds"), null);

		if (commerceAccountIds == null) {
			return;
		}

		BooleanFilter commerceAccountIdBooleanFilter = new BooleanFilter();
		BooleanFilter nestedBooleanFilter = new BooleanFilter();

		for (int i = 0; i < commerceAccountIds.length; i++) {
			nestedBooleanFilter.add(
				new TermFilter(
					"commerceAccountId", String.valueOf(commerceAccountIds[i])),
				BooleanClauseOccur.SHOULD);

			if (((i + 1) % _MAX_CLAUSES_COUNT) != 0) {
				continue;
			}

			commerceAccountIdBooleanFilter.add(
				nestedBooleanFilter, BooleanClauseOccur.SHOULD);

			nestedBooleanFilter = new BooleanFilter();
		}

		if (nestedBooleanFilter.hasClauses()) {
			commerceAccountIdBooleanFilter.add(
				nestedBooleanFilter, BooleanClauseOccur.SHOULD);
		}

		commerceAccountIdBooleanFilter.add(
			new MissingFilter("commerceAccountId"), BooleanClauseOccur.SHOULD);

		booleanFilter.add(
			commerceAccountIdBooleanFilter, BooleanClauseOccur.MUST);
	}

	private void _filterByGroupIds(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		if (ArrayUtil.isEmpty(searchContext.getGroupIds())) {
			booleanFilter.addTerm(
				Field.GROUP_ID, "-1", BooleanClauseOccur.MUST);
		}
	}

	private void _filterByOrderStatuses(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		int[] orderStatuses = GetterUtil.getIntegerValues(
			searchContext.getAttribute("orderStatuses"), null);

		if (orderStatuses == null) {
			return;
		}

		BooleanFilter orderStatusesBooleanFilter = new BooleanFilter();

		for (long orderStatus : orderStatuses) {
			orderStatusesBooleanFilter.add(
				new TermFilter("orderStatus", String.valueOf(orderStatus)),
				BooleanClauseOccur.SHOULD);
		}

		orderStatusesBooleanFilter.add(
			new MissingFilter("orderStatus"), BooleanClauseOccur.SHOULD);

		if (GetterUtil.getBoolean(
				searchContext.getAttribute("negateOrderStatuses"))) {

			booleanFilter.add(
				orderStatusesBooleanFilter, BooleanClauseOccur.MUST_NOT);
		}
		else {
			booleanFilter.add(
				orderStatusesBooleanFilter, BooleanClauseOccur.MUST);
		}
	}

	private void _filterBySupplierAccountEntryType(
		AccountEntry accountEntry, BooleanFilter booleanFilter) {

		for (CommerceChannel commerceChannel :
				_commerceChannelLocalService.
					getCommerceChannelsByAccountEntryId(
						accountEntry.getAccountEntryId())) {

			booleanFilter.add(
				new TermFilter(
					Field.GROUP_ID,
					String.valueOf(commerceChannel.getGroupId())),
				BooleanClauseOccur.SHOULD);
		}
	}

	private void _filterByUserId(BooleanFilter booleanFilter) {
		try {
			Group group = CommerceGroupThreadLocal.get();

			if (group == null) {
				return;
			}

			CommerceChannel commerceChannel =
				_commerceChannelLocalService.fetchCommerceChannelByGroupClassPK(
					group.getGroupId());

			if (commerceChannel == null) {
				commerceChannel =
					_commerceChannelLocalService.
						fetchCommerceChannelBySiteGroupId(group.getGroupId());
			}

			if ((commerceChannel == null) ||
				(!CommerceOrderConstants.ORDER_VISIBILITY_SCOPE_USER.equals(
					CommerceChannelConfigurationUtil.
						getOpenCommerceOrderVisibilityScope(
							commerceChannel.getGroupId())) &&
				 !CommerceOrderConstants.ORDER_VISIBILITY_SCOPE_USER.equals(
					 CommerceChannelConfigurationUtil.
						 getPlacedCommerceOrderVisibilityScope(
							 commerceChannel.getGroupId())))) {

				return;
			}

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			User user = _userLocalService.fetchUser(
				permissionChecker.getUserId());

			if (user == null) {
				return;
			}

			BooleanFilter userVisibilityScopeBooleanFilter =
				new BooleanFilter();

			CommerceAccountGroupServiceConfiguration
				commerceAccountGroupServiceConfiguration =
					_configurationProvider.getConfiguration(
						CommerceAccountGroupServiceConfiguration.class,
						new GroupServiceSettingsLocator(
							commerceChannel.getGroupId(),
							CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT));

			for (AccountEntry accountEntry :
					_accountEntryLocalService.getUserAccountEntries(
						user.getUserId(),
						AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
						StringPool.BLANK,
						AccountEntryAllowedTypesUtil.getAllowedTypes(
							commerceAccountGroupServiceConfiguration.
								commerceSiteType()),
						QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

				if (AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER.equals(
						accountEntry.getType())) {

					_filterBySupplierAccountEntryType(
						accountEntry, userVisibilityScopeBooleanFilter);
				}
				else if (_hasPermission(
							permissionChecker,
							accountEntry.getAccountEntryGroupId(),
							CommerceOrderActionKeys.
								APPROVE_OPEN_COMMERCE_ORDERS,
							CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS,
							CommerceOrderActionKeys.
								VIEW_ORGANIZATION_COMMERCE_ORDERS)) {

					userVisibilityScopeBooleanFilter.add(
						new TermFilter(
							"commerceAccountId",
							String.valueOf(accountEntry.getAccountEntryId())),
						BooleanClauseOccur.SHOULD);
				}
				else {
					userVisibilityScopeBooleanFilter.add(
						_getUserVisibilityScopeBooleanFilter(
							accountEntry, commerceChannel, user),
						BooleanClauseOccur.SHOULD);
				}
			}

			booleanFilter.add(
				userVisibilityScopeBooleanFilter, BooleanClauseOccur.MUST);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}
	}

	private BooleanFilter _getUserVisibilityScopeBooleanFilter(
		AccountEntry accountEntry, CommerceChannel commerceChannel, User user) {

		BooleanFilter booleanFilter = new BooleanFilter();

		if (CommerceOrderConstants.ORDER_VISIBILITY_SCOPE_USER.equals(
				CommerceChannelConfigurationUtil.
					getOpenCommerceOrderVisibilityScope(
						commerceChannel.getGroupId())) &&
			!CommerceOrderConstants.ORDER_VISIBILITY_SCOPE_USER.equals(
				CommerceChannelConfigurationUtil.
					getPlacedCommerceOrderVisibilityScope(
						commerceChannel.getGroupId()))) {

			BooleanFilter openCommerceOrderBooleanFilter = new BooleanFilter();

			openCommerceOrderBooleanFilter.add(
				new TermFilter(
					"commerceAccountId",
					String.valueOf(accountEntry.getAccountEntryId())),
				BooleanClauseOccur.MUST);
			openCommerceOrderBooleanFilter.add(
				new TermFilter(Field.USER_ID, String.valueOf(user.getUserId())),
				BooleanClauseOccur.MUST);
			openCommerceOrderBooleanFilter.add(
				new TermFilter(
					"orderStatus",
					String.valueOf(CommerceOrderConstants.ORDER_STATUS_OPEN)),
				BooleanClauseOccur.MUST);

			booleanFilter.add(
				openCommerceOrderBooleanFilter, BooleanClauseOccur.SHOULD);

			BooleanFilter placedCommerceOrderBooleanFilter =
				new BooleanFilter();

			placedCommerceOrderBooleanFilter.add(
				new TermFilter(
					"orderStatus",
					String.valueOf(CommerceOrderConstants.ORDER_STATUS_OPEN)),
				BooleanClauseOccur.MUST_NOT);

			booleanFilter.add(
				placedCommerceOrderBooleanFilter, BooleanClauseOccur.SHOULD);

			return booleanFilter;
		}

		if (!CommerceOrderConstants.ORDER_VISIBILITY_SCOPE_USER.equals(
				CommerceChannelConfigurationUtil.
					getOpenCommerceOrderVisibilityScope(
						commerceChannel.getGroupId())) &&
			CommerceOrderConstants.ORDER_VISIBILITY_SCOPE_USER.equals(
				CommerceChannelConfigurationUtil.
					getPlacedCommerceOrderVisibilityScope(
						commerceChannel.getGroupId()))) {

			BooleanFilter placedCommerceOrderBooleanFilter =
				new BooleanFilter();

			placedCommerceOrderBooleanFilter.add(
				new TermFilter(
					"commerceAccountId",
					String.valueOf(accountEntry.getAccountEntryId())),
				BooleanClauseOccur.MUST);
			placedCommerceOrderBooleanFilter.add(
				new TermFilter(Field.USER_ID, String.valueOf(user.getUserId())),
				BooleanClauseOccur.MUST);
			placedCommerceOrderBooleanFilter.add(
				new TermFilter(
					"orderStatus",
					String.valueOf(CommerceOrderConstants.ORDER_STATUS_OPEN)),
				BooleanClauseOccur.MUST_NOT);

			booleanFilter.add(
				placedCommerceOrderBooleanFilter, BooleanClauseOccur.SHOULD);

			booleanFilter.add(
				new TermFilter(
					"orderStatus",
					String.valueOf(CommerceOrderConstants.ORDER_STATUS_OPEN)),
				BooleanClauseOccur.SHOULD);

			return booleanFilter;
		}

		booleanFilter.add(
			new TermFilter(
				"commerceAccountId",
				String.valueOf(accountEntry.getAccountEntryId())),
			BooleanClauseOccur.MUST);
		booleanFilter.add(
			new TermFilter(Field.USER_ID, String.valueOf(user.getUserId())),
			BooleanClauseOccur.MUST);

		return booleanFilter;
	}

	private boolean _hasPermission(
		PermissionChecker permissionChecker, long groupId,
		String... actionIds) {

		for (String actionId : actionIds) {
			if (_portletResourcePermission.contains(
					permissionChecker, groupId, actionId)) {

				return true;
			}
		}

		return false;
	}

	private static final int _MAX_CLAUSES_COUNT = 1024;

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderModelPreFilterContributor.class);

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference(
		target = "(resource.name=" + CommerceOrderConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private UserLocalService _userLocalService;

}