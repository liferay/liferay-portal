/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.subscription.web.internal.display.context;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.service.CommerceSubscriptionEntryService;
import com.liferay.commerce.subscription.web.internal.display.context.helper.CommerceSubscriptionDisplayContextHelper;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.commerce.util.comparator.CommerceSubscriptionEntryCreateDateComparator;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceSubscriptionContentDisplayContext {

	public CommerceSubscriptionContentDisplayContext(
		CommercePaymentMethodGroupRelLocalService
			commercePaymentMethodGroupRelLocalService,
		CPDefinitionHelper cpDefinitionHelper,
		CPInstanceHelper cpInstanceHelper,
		CommerceSubscriptionEntryService commerceSubscriptionEntryService,
		ConfigurationProvider configurationProvider,
		HttpServletRequest httpServletRequest) {

		_commercePaymentMethodGroupRelLocalService =
			commercePaymentMethodGroupRelLocalService;
		_cpDefinitionHelper = cpDefinitionHelper;
		_cpInstanceHelper = cpInstanceHelper;
		_commerceSubscriptionEntryService = commerceSubscriptionEntryService;
		_configurationProvider = configurationProvider;

		_cpRequestHelper = new CPRequestHelper(httpServletRequest);
	}

	public DropdownItemList getCommerceSubscriptionEntryActionItemList(
			CommerceSubscriptionEntry commerceSubscriptionEntry,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortalException {

		CommerceSubscriptionDisplayContextHelper
			commerceSubscriptionDisplayContextHelper =
				new CommerceSubscriptionDisplayContextHelper(
					commerceSubscriptionEntry, _configurationProvider,
					portletRequest, portletResponse);

		return commerceSubscriptionDisplayContextHelper.
			getCommerceSubscriptionEntryActionItemList();
	}

	public String getCommerceSubscriptionEntryThumbnailSrc(
			CommerceSubscriptionEntry commerceSubscriptionEntry)
		throws Exception {

		HttpServletRequest httpServletRequest = _cpRequestHelper.getRequest();

		return _cpInstanceHelper.getCPInstanceThumbnailSrc(
			CommerceUtil.getCommerceAccountId(
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT)),
			commerceSubscriptionEntry.getCPInstanceId());
	}

	public String getCPDefinitionURL(
			CommerceSubscriptionEntry commerceSubscriptionEntry,
			ThemeDisplay themeDisplay)
		throws PortalException {

		return _cpDefinitionHelper.getFriendlyURL(
			commerceSubscriptionEntry.getCPDefinitionId(), themeDisplay);
	}

	public List<KeyValuePair> getKeyValuePairs(
			CommerceSubscriptionEntry commerceSubscriptionEntry)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceSubscriptionEntry.fetchCommerceOrderItem();

		CPInstance cpInstance = commerceOrderItem.fetchCPInstance();

		if (cpInstance == null) {
			return Collections.emptyList();
		}

		return _cpInstanceHelper.getKeyValuePairs(
			cpInstance.getCPDefinitionId(), commerceOrderItem.getJson(),
			_cpRequestHelper.getLocale());
	}

	public PortletURL getPortletURL() throws PortalException {
		LiferayPortletResponse liferayPortletResponse =
			_cpRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		HttpServletRequest httpServletRequest = _cpRequestHelper.getRequest();

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		String delta = ParamUtil.getString(httpServletRequest, "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String deltaEntry = ParamUtil.getString(
			httpServletRequest, "deltaEntry");

		if (Validator.isNotNull(deltaEntry)) {
			portletURL.setParameter("deltaEntry", deltaEntry);
		}

		return portletURL;
	}

	public SearchContainer<CommerceSubscriptionEntry> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer<>(
			_cpRequestHelper.getLiferayPortletRequest(), getPortletURL(), null,
			"there-are-no-subscriptions");

		_searchContainer.setResultsAndTotal(
			() ->
				_commerceSubscriptionEntryService.
					getCommerceSubscriptionEntries(
						_cpRequestHelper.getCompanyId(),
						_cpRequestHelper.getCommerceChannelGroupId(),
						_cpRequestHelper.getUserId(),
						_searchContainer.getStart(), _searchContainer.getEnd(),
						CommerceSubscriptionEntryCreateDateComparator.
							getInstance(false)),
			_commerceSubscriptionEntryService.
				getCommerceSubscriptionEntriesCount(
					_cpRequestHelper.getCompanyId(),
					_cpRequestHelper.getCommerceChannelGroupId(),
					_cpRequestHelper.getUserId()));

		return _searchContainer;
	}

	public boolean hasCommerceChannel() throws PortalException {
		HttpServletRequest httpServletRequest = _cpRequestHelper.getRequest();

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		if (commerceContext == null) {
			return false;
		}

		long commerceChannelId = commerceContext.getCommerceChannelId();

		if (commerceChannelId > 0) {
			return true;
		}

		return false;
	}

	public boolean isPaymentMethodActive(String engineKey) {
		try {
			CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
				_commercePaymentMethodGroupRelLocalService.
					fetchCommercePaymentMethodGroupRel(
						_cpRequestHelper.getCommerceChannelGroupId(),
						engineKey);

			if (commercePaymentMethodGroupRel == null) {
				return false;
			}

			return commercePaymentMethodGroupRel.isActive();
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceSubscriptionContentDisplayContext.class);

	private final CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;
	private final CommerceSubscriptionEntryService
		_commerceSubscriptionEntryService;
	private final ConfigurationProvider _configurationProvider;
	private final CPDefinitionHelper _cpDefinitionHelper;
	private final CPInstanceHelper _cpInstanceHelper;
	private final CPRequestHelper _cpRequestHelper;
	private SearchContainer<CommerceSubscriptionEntry> _searchContainer;

}