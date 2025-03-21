/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.portlet.shared.search;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.search.web.internal.configuration.CPSearchResultsPortletInstanceConfiguration;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.generic.BooleanClauseImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

import java.util.NoSuchElementException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_SEARCH_RESULTS,
	service = PortletSharedSearchContributor.class
)
public class CPSearchResultsPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		try {
			_contribute(portletSharedSearchSettings);

			String paginationStartParameterName =
				portletSharedSearchSettings.getPaginationStartParameterName();

			if (paginationStartParameterName == null) {
				throw new NoSuchElementException(
					"Pagination start parameter name is null for portlet ID " +
						portletSharedSearchSettings.getPortletId());
			}

			SearchRequestBuilder searchRequestBuilder =
				portletSharedSearchSettings.getSearchRequestBuilder();

			searchRequestBuilder.paginationStartParameterName(
				paginationStartParameterName);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	private void _contribute(
			PortletSharedSearchSettings portletSharedSearchSettings)
		throws PortalException {

		RenderRequest renderRequest =
			portletSharedSearchSettings.getRenderRequest();

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		portletSharedSearchSettings.setKeywords(
			GetterUtil.getString(
				portletSharedSearchSettings.getParameter("q")));

		portletSharedSearchSettings.addCondition(
			new BooleanClauseImpl<Query>(
				new TermQueryImpl(
					Field.ENTRY_CLASS_NAME, CPDefinition.class.getName()),
				BooleanClauseOccur.MUST));

		AssetCategory assetCategory = (AssetCategory)renderRequest.getAttribute(
			WebKeys.ASSET_CATEGORY);

		if (assetCategory != null) {
			portletSharedSearchSettings.addCondition(
				new BooleanClauseImpl<Query>(
					new TermQueryImpl(
						Field.ASSET_CATEGORY_IDS,
						String.valueOf(assetCategory.getCategoryId())),
					BooleanClauseOccur.MUST));
		}

		SearchContext searchContext =
			portletSharedSearchSettings.getSearchContext();

		searchContext.setAttribute(CPField.PUBLISHED, Boolean.TRUE);
		searchContext.setEntryClassNames(
			new String[] {CPDefinition.class.getName()});

		if (FeatureFlagManagerUtil.isEnabled("LPD-10889")) {
			CommerceContext commerceContext =
				(CommerceContext)renderRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);

			searchContext.setAttribute(
				CPField.CP_CONFIGURATION_LIST_IDS,
				commerceContext.getCPConfigurationListIds());
		}
		else {
			CommerceChannel commerceChannel =
				_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
					themeDisplay.getScopeGroupId());

			if (commerceChannel != null) {
				searchContext.setAttribute(
					"commerceChannelGroupId", commerceChannel.getGroupId());

				AccountEntry accountEntry =
					_commerceAccountHelper.getCurrentAccountEntry(
						commerceChannel.getGroupId(),
						_portal.getHttpServletRequest(renderRequest));

				if (accountEntry != null) {
					searchContext.setAttribute(
						"accountEntryId", accountEntry.getAccountEntryId());
					searchContext.setAttribute(
						"commerceAccountGroupIds",
						_accountGroupLocalService.getAccountGroupIds(
							accountEntry.getAccountEntryId()));
				}
			}

			searchContext.setAttribute("secure", Boolean.TRUE);
		}

		QueryConfig queryConfig = portletSharedSearchSettings.getQueryConfig();

		queryConfig.setHighlightEnabled(false);

		CPSearchResultsPortletInstanceConfiguration
			cpSearchResultsPortletInstanceConfiguration =
				_configurationProvider.getPortletInstanceConfiguration(
					CPSearchResultsPortletInstanceConfiguration.class,
					themeDisplay);

		_paginate(
			cpSearchResultsPortletInstanceConfiguration,
			portletSharedSearchSettings);
	}

	private void _paginate(
		CPSearchResultsPortletInstanceConfiguration
			cpSearchResultsPortletInstanceConfiguration,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		String paginationStartParameterName = "start";

		portletSharedSearchSettings.setPaginationStartParameterName(
			paginationStartParameterName);

		String paginationStartParameterValue =
			portletSharedSearchSettings.getParameter(
				paginationStartParameterName);

		if (paginationStartParameterValue != null) {
			portletSharedSearchSettings.setPaginationStart(
				Integer.valueOf(paginationStartParameterValue));
		}

		String paginationDeltaParameterValue =
			portletSharedSearchSettings.getParameter("delta");

		if (paginationDeltaParameterValue != null) {
			portletSharedSearchSettings.setPaginationDelta(
				Integer.valueOf(paginationDeltaParameterValue));
		}
		else {
			int configurationPaginationDelta =
				cpSearchResultsPortletInstanceConfiguration.paginationDelta();

			PortletPreferences portletPreferences =
				portletSharedSearchSettings.getPortletPreferences();

			if (portletPreferences != null) {
				configurationPaginationDelta = GetterUtil.getInteger(
					portletPreferences.getValue("paginationDelta", null),
					configurationPaginationDelta);
			}

			portletSharedSearchSettings.setPaginationDelta(
				configurationPaginationDelta);
		}

		SearchContext searchContext =
			portletSharedSearchSettings.getSearchContext();

		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			GetterUtil.getInteger(
				portletSharedSearchSettings.getPaginationStart()),
			GetterUtil.getInteger(
				portletSharedSearchSettings.getPaginationDelta()));

		searchContext.setEnd(startAndEnd[1]);
		searchContext.setStart(startAndEnd[0]);
	}

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

}