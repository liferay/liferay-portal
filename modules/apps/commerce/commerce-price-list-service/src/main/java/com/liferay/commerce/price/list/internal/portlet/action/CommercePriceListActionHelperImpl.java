/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.internal.portlet.action;

import com.liferay.commerce.price.list.constants.CommercePriceListWebKeys;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.portlet.action.CommercePriceListActionHelper;
import com.liferay.commerce.price.list.service.CommercePriceEntryService;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.price.list.service.CommerceTierPriceEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(service = CommercePriceListActionHelper.class)
public class CommercePriceListActionHelperImpl
	implements CommercePriceListActionHelper {

	@Override
	public List<CommercePriceEntry> getCommercePriceEntries(
			PortletRequest portletRequest)
		throws PortalException {

		List<CommercePriceEntry> commercePriceEntries = new ArrayList<>();

		long[] commercePriceEntryIds = ParamUtil.getLongValues(
			portletRequest, "rowIds");

		for (long commercePriceEntryId : commercePriceEntryIds) {
			CommercePriceEntry commercePriceEntry =
				_commercePriceEntryService.fetchCommercePriceEntry(
					commercePriceEntryId);

			if (commercePriceEntry != null) {
				commercePriceEntries.add(commercePriceEntry);
			}
		}

		return commercePriceEntries;
	}

	@Override
	public CommercePriceEntry getCommercePriceEntry(RenderRequest renderRequest)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			(CommercePriceEntry)renderRequest.getAttribute(
				CommercePriceListWebKeys.COMMERCE_PRICE_ENTRY);

		if (commercePriceEntry != null) {
			return commercePriceEntry;
		}

		long commercePriceEntryId = ParamUtil.getLong(
			renderRequest, "commercePriceEntryId");

		if (commercePriceEntryId > 0) {
			commercePriceEntry =
				_commercePriceEntryService.fetchCommercePriceEntry(
					commercePriceEntryId);
		}

		if (commercePriceEntry != null) {
			renderRequest.setAttribute(
				CommercePriceListWebKeys.COMMERCE_PRICE_ENTRY,
				commercePriceEntry);
		}

		return commercePriceEntry;
	}

	@Override
	public CommercePriceList getCommercePriceList(PortletRequest portletRequest)
		throws PortalException {

		CommercePriceList commercePriceList =
			(CommercePriceList)portletRequest.getAttribute(
				CommercePriceListWebKeys.COMMERCE_PRICE_LIST);

		if (commercePriceList != null) {
			return commercePriceList;
		}

		long commercePriceListId = ParamUtil.getLong(
			portletRequest, "commercePriceListId");

		if (commercePriceListId > 0) {
			commercePriceList =
				_commercePriceListService.fetchCommercePriceList(
					commercePriceListId);
		}

		if (commercePriceList != null) {
			portletRequest.setAttribute(
				CommercePriceListWebKeys.COMMERCE_PRICE_LIST,
				commercePriceList);
		}

		return commercePriceList;
	}

	@Override
	public List<CommercePriceList> getCommercePriceLists(
			PortletRequest portletRequest)
		throws PortalException {

		List<CommercePriceList> commercePriceLists = new ArrayList<>();

		long[] commercePriceListIds = ParamUtil.getLongValues(
			portletRequest, "rowIds");

		for (long commercePriceListId : commercePriceListIds) {
			CommercePriceList commercePriceList =
				_commercePriceListService.fetchCommercePriceList(
					commercePriceListId);

			if (commercePriceList != null) {
				commercePriceLists.add(commercePriceList);
			}
		}

		return commercePriceLists;
	}

	@Override
	public List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
			PortletRequest portletRequest)
		throws PortalException {

		List<CommerceTierPriceEntry> commerceTierPriceEntries =
			new ArrayList<>();

		long[] commerceTierPriceEntryIds = ParamUtil.getLongValues(
			portletRequest, "rowIds");

		for (long commerceTierPriceEntryId : commerceTierPriceEntryIds) {
			CommerceTierPriceEntry commerceTierPriceEntry =
				_commerceTierPriceEntryService.fetchCommerceTierPriceEntry(
					commerceTierPriceEntryId);

			if (commerceTierPriceEntry != null) {
				commerceTierPriceEntries.add(commerceTierPriceEntry);
			}
		}

		return commerceTierPriceEntries;
	}

	@Override
	public CommerceTierPriceEntry getCommerceTierPriceEntry(
			RenderRequest renderRequest)
		throws PortalException {

		long commerceTierPriceEntryId = ParamUtil.getLong(
			renderRequest, "commerceTierPriceEntryId");

		return _commerceTierPriceEntryService.fetchCommerceTierPriceEntry(
			commerceTierPriceEntryId);
	}

	@Reference
	private CommercePriceEntryService _commercePriceEntryService;

	@Reference
	private CommercePriceListService _commercePriceListService;

	@Reference
	private CommerceTierPriceEntryService _commerceTierPriceEntryService;

}