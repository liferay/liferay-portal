/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.subscription.web.internal.frontend.data.set.provider;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceSubscriptionEntryConstants;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceSubscriptionEntryService;
import com.liferay.commerce.subscription.web.internal.constants.CommerceSubscriptionFDSNames;
import com.liferay.commerce.subscription.web.internal.model.Label;
import com.liferay.commerce.subscription.web.internal.model.Link;
import com.liferay.commerce.subscription.web.internal.model.SubscriptionEntry;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceSubscriptionFDSNames.SUBSCRIPTION_ENTRIES,
	service = FDSDataProvider.class
)
public class CommerceSubscriptionEntryFDSDataProvider
	implements FDSDataProvider<SubscriptionEntry> {

	@Override
	public List<SubscriptionEntry> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		List<SubscriptionEntry> subscriptionEntries = new ArrayList<>();

		BaseModelSearchResult<CommerceSubscriptionEntry> baseModelSearchResult =
			_getBaseModelSearchResult(
				fdsKeywords, fdsPagination, httpServletRequest, sort);

		for (CommerceSubscriptionEntry commerceSubscriptionEntry :
				baseModelSearchResult.getBaseModels()) {

			CommerceOrderItem commerceOrderItem =
				_commerceOrderItemService.getCommerceOrderItem(
					commerceSubscriptionEntry.getCommerceOrderItemId());

			String commerceOrderIdString = String.valueOf(
				commerceOrderItem.getCommerceOrderId());

			CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

			AccountEntry accountEntry = commerceOrder.getAccountEntry();

			String accountEntryIdString = String.valueOf(
				accountEntry.getAccountEntryId());

			SubscriptionEntry subscriptionEntry = new SubscriptionEntry(
				commerceSubscriptionEntry.getCommerceSubscriptionEntryId(),
				new Link(
					commerceOrderIdString,
					_getEditCommerceOrderURL(
						commerceOrder.getCommerceOrderId(),
						httpServletRequest)),
				new Link(
					accountEntryIdString,
					_getEditAccountURL(
						accountEntry.getAccountEntryId(), httpServletRequest)),
				_getSubscriptionStatus(commerceSubscriptionEntry),
				accountEntry.getName());

			subscriptionEntries.add(subscriptionEntry);
		}

		return subscriptionEntries;
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		BaseModelSearchResult<CommerceSubscriptionEntry> baseModelSearchResult =
			_getBaseModelSearchResult(
				fdsKeywords, null, httpServletRequest, null);

		return baseModelSearchResult.getLength();
	}

	private BaseModelSearchResult<CommerceSubscriptionEntry>
			_getBaseModelSearchResult(
				FDSKeywords fdsKeywords, FDSPagination fdsPagination,
				HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		int start = QueryUtil.ALL_POS;
		int end = QueryUtil.ALL_POS;

		if (fdsPagination != null) {
			start = fdsPagination.getStartPosition();
			end = fdsPagination.getEndPosition();
		}

		return _commerceSubscriptionEntryService.
			searchCommerceSubscriptionEntries(
				_portal.getCompanyId(httpServletRequest), null, null,
				fdsKeywords.getKeywords(), start, end, sort);
	}

	private String _getEditAccountURL(
			long accountEntryId, HttpServletRequest httpServletRequest)
		throws PortalException {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/account_admin/edit_account_entry"
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				_portal.getCurrentURL(httpServletRequest))
		).setParameter(
			"accountEntryId", accountEntryId
		).buildString();
	}

	private String _getEditCommerceOrderURL(
			long commerceOrderId, HttpServletRequest httpServletRequest)
		throws PortalException {

		CPRequestHelper cpRequestHelper = new CPRequestHelper(
			httpServletRequest);

		ThemeDisplay themeDisplay = cpRequestHelper.getThemeDisplay();

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, themeDisplay.getScopeGroup(),
				CommerceOrder.class.getName(), PortletProvider.Action.MANAGE)
		).setMVCRenderCommandName(
			"/commerce_open_order_content/edit_commerce_order"
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				_portal.getCurrentURL(httpServletRequest))
		).setParameter(
			"commerceOrderId", commerceOrderId
		).buildString();
	}

	private Label _getSubscriptionStatus(
		CommerceSubscriptionEntry commerceSubscriptionEntry) {

		String subscriptionStatusLabel =
			CommerceSubscriptionEntryConstants.getSubscriptionStatusLabel(
				CommerceSubscriptionEntryConstants.
					SUBSCRIPTION_STATUS_COMPLETED);
		String label = Label.INFO;

		if (Objects.equals(
				commerceSubscriptionEntry.getSubscriptionStatus(),
				CommerceSubscriptionEntryConstants.
					SUBSCRIPTION_STATUS_ACTIVE) ||
			Objects.equals(
				commerceSubscriptionEntry.getDeliverySubscriptionStatus(),
				CommerceSubscriptionEntryConstants.
					SUBSCRIPTION_STATUS_ACTIVE)) {

			subscriptionStatusLabel =
				CommerceSubscriptionEntryConstants.getSubscriptionStatusLabel(
					CommerceSubscriptionEntryConstants.
						SUBSCRIPTION_STATUS_ACTIVE);
			label = Label.SUCCESS;
		}
		else if (Objects.equals(
					commerceSubscriptionEntry.getSubscriptionStatus(),
					CommerceSubscriptionEntryConstants.
						SUBSCRIPTION_STATUS_INACTIVE) &&
				 Objects.equals(
					 commerceSubscriptionEntry.getDeliverySubscriptionStatus(),
					 CommerceSubscriptionEntryConstants.
						 SUBSCRIPTION_STATUS_INACTIVE)) {

			subscriptionStatusLabel =
				CommerceSubscriptionEntryConstants.getSubscriptionStatusLabel(
					CommerceSubscriptionEntryConstants.
						SUBSCRIPTION_STATUS_INACTIVE);
			label = Label.WARNING;
		}
		else if (Objects.equals(
					commerceSubscriptionEntry.getSubscriptionStatus(),
					CommerceSubscriptionEntryConstants.
						SUBSCRIPTION_STATUS_SUSPENDED) &&
				 Objects.equals(
					 commerceSubscriptionEntry.getDeliverySubscriptionStatus(),
					 CommerceSubscriptionEntryConstants.
						 SUBSCRIPTION_STATUS_SUSPENDED)) {

			subscriptionStatusLabel =
				CommerceSubscriptionEntryConstants.getSubscriptionStatusLabel(
					CommerceSubscriptionEntryConstants.
						SUBSCRIPTION_STATUS_SUSPENDED);
			label = Label.WARNING;
		}
		else if (Objects.equals(
					commerceSubscriptionEntry.getSubscriptionStatus(),
					CommerceSubscriptionEntryConstants.
						SUBSCRIPTION_STATUS_CANCELLED) &&
				 Objects.equals(
					 commerceSubscriptionEntry.getDeliverySubscriptionStatus(),
					 CommerceSubscriptionEntryConstants.
						 SUBSCRIPTION_STATUS_CANCELLED)) {

			subscriptionStatusLabel =
				CommerceSubscriptionEntryConstants.getSubscriptionStatusLabel(
					CommerceSubscriptionEntryConstants.
						SUBSCRIPTION_STATUS_CANCELLED);
			label = Label.DANGER;
		}
		else if ((Objects.equals(
					commerceSubscriptionEntry.getSubscriptionStatus(),
					CommerceSubscriptionEntryConstants.
						SUBSCRIPTION_STATUS_INACTIVE) &&
				  Objects.equals(
					  commerceSubscriptionEntry.getDeliverySubscriptionStatus(),
					  CommerceSubscriptionEntryConstants.
						  SUBSCRIPTION_STATUS_SUSPENDED)) ||
				 (Objects.equals(
					 commerceSubscriptionEntry.getSubscriptionStatus(),
					 CommerceSubscriptionEntryConstants.
						 SUBSCRIPTION_STATUS_SUSPENDED) &&
				  Objects.equals(
					  commerceSubscriptionEntry.getDeliverySubscriptionStatus(),
					  CommerceSubscriptionEntryConstants.
						  SUBSCRIPTION_STATUS_INACTIVE))) {

			subscriptionStatusLabel =
				CommerceSubscriptionEntryConstants.getSubscriptionStatusLabel(
					CommerceSubscriptionEntryConstants.
						SUBSCRIPTION_STATUS_SUSPENDED);
			label = Label.WARNING;
		}
		else if ((Objects.equals(
					commerceSubscriptionEntry.getSubscriptionStatus(),
					CommerceSubscriptionEntryConstants.
						SUBSCRIPTION_STATUS_INACTIVE) &&
				  Objects.equals(
					  commerceSubscriptionEntry.getDeliverySubscriptionStatus(),
					  CommerceSubscriptionEntryConstants.
						  SUBSCRIPTION_STATUS_CANCELLED)) ||
				 (Objects.equals(
					 commerceSubscriptionEntry.getSubscriptionStatus(),
					 CommerceSubscriptionEntryConstants.
						 SUBSCRIPTION_STATUS_CANCELLED) &&
				  Objects.equals(
					  commerceSubscriptionEntry.getDeliverySubscriptionStatus(),
					  CommerceSubscriptionEntryConstants.
						  SUBSCRIPTION_STATUS_INACTIVE))) {

			subscriptionStatusLabel =
				CommerceSubscriptionEntryConstants.getSubscriptionStatusLabel(
					CommerceSubscriptionEntryConstants.
						SUBSCRIPTION_STATUS_CANCELLED);
			label = Label.DANGER;
		}
		else if ((Objects.equals(
					commerceSubscriptionEntry.getSubscriptionStatus(),
					CommerceSubscriptionEntryConstants.
						SUBSCRIPTION_STATUS_SUSPENDED) &&
				  Objects.equals(
					  commerceSubscriptionEntry.getDeliverySubscriptionStatus(),
					  CommerceSubscriptionEntryConstants.
						  SUBSCRIPTION_STATUS_CANCELLED)) ||
				 (Objects.equals(
					 commerceSubscriptionEntry.getSubscriptionStatus(),
					 CommerceSubscriptionEntryConstants.
						 SUBSCRIPTION_STATUS_CANCELLED) &&
				  Objects.equals(
					  commerceSubscriptionEntry.getDeliverySubscriptionStatus(),
					  CommerceSubscriptionEntryConstants.
						  SUBSCRIPTION_STATUS_SUSPENDED))) {

			subscriptionStatusLabel =
				CommerceSubscriptionEntryConstants.getSubscriptionStatusLabel(
					CommerceSubscriptionEntryConstants.
						SUBSCRIPTION_STATUS_CANCELLED);
			label = Label.DANGER;
		}

		return new Label(subscriptionStatusLabel, label);
	}

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceSubscriptionEntryService _commerceSubscriptionEntryService;

	@Reference
	private Portal _portal;

}