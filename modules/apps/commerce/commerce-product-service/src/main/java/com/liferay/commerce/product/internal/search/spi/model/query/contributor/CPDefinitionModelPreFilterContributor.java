/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.search.spi.model.query.contributor;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.util.RangeParserUtil;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.RangeTermFilter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian I. Kim
 * @author Petteri Karttunen
 */
@Component(
	property = {
		"indexer.class.name=com.liferay.commerce.product.model.CPDefinition",
		"indexer.clauses.mandatory=true"
	},
	service = ModelPreFilterContributor.class
)
public class CPDefinitionModelPreFilterContributor
	implements ModelPreFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings,
		SearchContext searchContext) {

		booleanFilter.addRequiredTerm(Field.HIDDEN, false);

		_filterByBasePrice(booleanFilter, searchContext);
		_filterByDefinitionLinks(booleanFilter, searchContext);
		_filterByExcludedCPDefinitionId(booleanFilter, searchContext);
		_filterByPublished(booleanFilter, searchContext);
		_filterByStatuses(booleanFilter, searchContext);
		_filterBySubscriptionEnabled(booleanFilter, searchContext);

		if (GetterUtil.getBoolean(searchContext.getAttribute("secure"))) {
			_filterByAccountGroupIds(booleanFilter, searchContext);
			_filterByCommerceChannelId(booleanFilter, searchContext);
		}

		if (_isIndexersSuppressed(searchContext)) {
			_filterByCommerceCatalogIds(booleanFilter, searchContext);
		}
	}

	private void _filterByAccountGroupIds(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		BooleanFilter accountGroupsBooleanFilter = new BooleanFilter();

		BooleanFilter accountGroupsFilterEnableBooleanFilter =
			new BooleanFilter();

		accountGroupsFilterEnableBooleanFilter.addTerm(
			CPField.ACCOUNT_GROUP_FILTER_ENABLED, Boolean.TRUE.toString(),
			BooleanClauseOccur.MUST);

		long[] accountGroupIds = GetterUtil.getLongValues(
			searchContext.getAttribute("commerceAccountGroupIds"), null);

		if ((accountGroupIds != null) && (accountGroupIds.length > 0)) {
			BooleanFilter accountGroupIdsBooleanFilter = new BooleanFilter();

			for (long accountGroupId : accountGroupIds) {
				accountGroupIdsBooleanFilter.add(
					new TermFilter(
						"commerceAccountGroupIds",
						String.valueOf(accountGroupId)),
					BooleanClauseOccur.SHOULD);
			}

			accountGroupsFilterEnableBooleanFilter.add(
				accountGroupIdsBooleanFilter, BooleanClauseOccur.MUST);
		}
		else {
			accountGroupsFilterEnableBooleanFilter.addTerm(
				"commerceAccountGroupIds", "-1", BooleanClauseOccur.MUST);
		}

		accountGroupsBooleanFilter.add(
			accountGroupsFilterEnableBooleanFilter, BooleanClauseOccur.SHOULD);
		accountGroupsBooleanFilter.addTerm(
			CPField.ACCOUNT_GROUP_FILTER_ENABLED, Boolean.FALSE.toString(),
			BooleanClauseOccur.SHOULD);

		boolean ignoreAccountGroup = GetterUtil.getBoolean(
			searchContext.getAttribute("ignoreCommerceAccountGroup"));

		if (!ignoreAccountGroup) {
			booleanFilter.add(
				accountGroupsBooleanFilter, BooleanClauseOccur.MUST);
		}
	}

	private void _filterByBasePrice(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		String[] basePriceRanges = GetterUtil.getStringValues(
			searchContext.getAttribute(CPField.BASE_PRICE));

		if (basePriceRanges.length < 1) {
			return;
		}

		BooleanFilter priceRangeBooleanFilter = new BooleanFilter();

		for (String basePriceRange : basePriceRanges) {
			String[] basePriceRangeParts = RangeParserUtil.parserRange(
				basePriceRange);

			RangeTermFilter rangeTermFilter = new RangeTermFilter(
				CPField.BASE_PRICE, true, true, basePriceRangeParts[0],
				basePriceRangeParts[1]);

			priceRangeBooleanFilter.add(
				rangeTermFilter, BooleanClauseOccur.SHOULD);
		}

		booleanFilter.add(priceRangeBooleanFilter, BooleanClauseOccur.MUST);
	}

	private void _filterByCommerceCatalogIds(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		long[] commerceCatalogIds = _getCommerceCatalogIds(searchContext);

		if (commerceCatalogIds.length > 0) {
			TermsFilter termsFilter = new TermsFilter("commerceCatalogId");

			termsFilter.addValues(ArrayUtil.toStringArray(commerceCatalogIds));

			booleanFilter.add(termsFilter, BooleanClauseOccur.MUST);
		}
		else {
			if (ArrayUtil.isEmpty(searchContext.getGroupIds())) {
				booleanFilter.addTerm(
					Field.GROUP_ID, "-1", BooleanClauseOccur.MUST);
			}
		}
	}

	private void _filterByCommerceChannelId(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		BooleanFilter commerceChannelBooleanFilter = new BooleanFilter();

		BooleanFilter commerceChannelFilterEnableBooleanFilter =
			new BooleanFilter();

		commerceChannelFilterEnableBooleanFilter.addTerm(
			CPField.CHANNEL_FILTER_ENABLED, Boolean.TRUE.toString(),
			BooleanClauseOccur.MUST);

		long accountEntryId = GetterUtil.getLong(
			searchContext.getAttribute("accountEntryId"));

		long commerceChannelGroupId = GetterUtil.getLong(
			searchContext.getAttribute("commerceChannelGroupId"));

		long[] commerceChannelGroupIds = GetterUtil.getLongValues(
			searchContext.getAttribute("commerceChannelGroupIds"),
			new long[] {commerceChannelGroupId});

		for (long groupId : commerceChannelGroupIds) {
			if (groupId > 0) {
				CommerceChannel commerceChannel =
					_commerceChannelLocalService.
						fetchCommerceChannelByGroupClassPK(groupId);

				if (commerceChannel == null) {
					commerceChannelFilterEnableBooleanFilter.addTerm(
						CPField.COMMERCE_CHANNEL_GROUP_IDS, "-1",
						BooleanClauseOccur.MUST);

					continue;
				}

				int count =
					_commerceChannelAccountEntryRelLocalService.
						getCommerceChannelAccountEntryRelsCount(
							commerceChannel.getCommerceChannelId(), null,
							CommerceChannelAccountEntryRelConstants.
								TYPE_ELIGIBILITY);
				CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
					_commerceChannelAccountEntryRelLocalService.
						fetchCommerceChannelAccountEntryRel(
							accountEntryId,
							commerceChannel.getCommerceChannelId(),
							CommerceChannelAccountEntryRelConstants.
								TYPE_ELIGIBILITY);

				if ((count == 0) || (commerceChannelAccountEntryRel != null)) {
					commerceChannelFilterEnableBooleanFilter.addTerm(
						CPField.COMMERCE_CHANNEL_GROUP_IDS,
						String.valueOf(groupId), BooleanClauseOccur.MUST);
				}
				else {
					commerceChannelFilterEnableBooleanFilter.addTerm(
						CPField.COMMERCE_CHANNEL_GROUP_IDS, "-1",
						BooleanClauseOccur.MUST);
				}
			}
			else {
				commerceChannelFilterEnableBooleanFilter.addTerm(
					CPField.COMMERCE_CHANNEL_GROUP_IDS, "-1",
					BooleanClauseOccur.MUST);
			}
		}

		commerceChannelBooleanFilter.add(
			commerceChannelFilterEnableBooleanFilter,
			BooleanClauseOccur.SHOULD);
		commerceChannelBooleanFilter.addTerm(
			CPField.CHANNEL_FILTER_ENABLED, Boolean.FALSE.toString(),
			BooleanClauseOccur.SHOULD);

		booleanFilter.add(
			commerceChannelBooleanFilter, BooleanClauseOccur.MUST);
	}

	private void _filterByDefinitionLinks(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		String definitionLinkType = GetterUtil.getString(
			searchContext.getAttribute("definitionLinkType"));

		long definitionLinkCPDefinitionId = GetterUtil.getLong(
			searchContext.getAttribute("definitionLinkCPDefinitionId"));

		if (Validator.isNotNull(definitionLinkType) &&
			(definitionLinkCPDefinitionId > 0)) {

			TermsFilter termsFilter = new TermsFilter(definitionLinkType);

			termsFilter.addValue(String.valueOf(definitionLinkCPDefinitionId));

			booleanFilter.add(termsFilter, BooleanClauseOccur.MUST);
		}
	}

	private void _filterByExcludedCPDefinitionId(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		long excludedCPDefinitionId = GetterUtil.getLong(
			searchContext.getAttribute("excludedCPDefinitionId"));

		if (excludedCPDefinitionId > 0) {
			booleanFilter.addTerm(
				Field.ENTRY_CLASS_PK, String.valueOf(excludedCPDefinitionId),
				BooleanClauseOccur.MUST_NOT);
		}
	}

	private void _filterByPublished(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		boolean published = GetterUtil.getBoolean(
			searchContext.getAttribute(CPField.PUBLISHED));

		if (published) {
			booleanFilter.addRequiredTerm(CPField.PUBLISHED, true);
		}
	}

	private void _filterByStatuses(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		int[] statuses = GetterUtil.getIntegerValues(
			searchContext.getAttribute(Field.STATUS));

		if (ArrayUtil.isEmpty(statuses)) {
			int status = GetterUtil.getInteger(
				searchContext.getAttribute(Field.STATUS),
				WorkflowConstants.STATUS_APPROVED);

			statuses = new int[] {status};
		}

		if (!ArrayUtil.contains(statuses, WorkflowConstants.STATUS_ANY)) {
			TermsFilter termsFilter = new TermsFilter(Field.STATUS);

			termsFilter.addValues(ArrayUtil.toStringArray(statuses));

			booleanFilter.add(termsFilter, BooleanClauseOccur.MUST);
		}
	}

	private void _filterBySubscriptionEnabled(
		BooleanFilter booleanFilter, SearchContext searchContext) {

		boolean subscriptionEnabled = GetterUtil.getBoolean(
			searchContext.getAttribute(CPField.SUBSCRIPTION_ENABLED));

		if (subscriptionEnabled) {
			booleanFilter.addRequiredTerm(CPField.SUBSCRIPTION_ENABLED, true);
		}
	}

	private long[] _getCommerceCatalogIds(SearchContext searchContext) {
		return TransformUtil.transformToLongArray(
			_commerceCatalogService.getCommerceCatalogs(
				searchContext.getCompanyId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			CommerceCatalog::getCommerceCatalogId);
	}

	private boolean _isIndexersSuppressed(SearchContext searchContext) {
		return GetterUtil.getBoolean(
			searchContext.getAttribute(
				"search.full.query.suppress.indexer.provided.clauses"));
	}

	@Reference
	private CommerceCatalogService _commerceCatalogService;

	@Reference
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

}