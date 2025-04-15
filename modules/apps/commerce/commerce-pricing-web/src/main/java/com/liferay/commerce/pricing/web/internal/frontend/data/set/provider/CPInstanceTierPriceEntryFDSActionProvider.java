/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.data.set.provider;

import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.service.CommerceTierPriceEntryService;
import com.liferay.commerce.pricing.web.internal.constants.CommercePricingFDSNames;
import com.liferay.commerce.pricing.web.internal.model.InstanceTierPriceEntry;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommercePricingFDSNames.INSTANCE_TIER_PRICE_ENTRIES,
	service = FDSActionProvider.class
)
public class CPInstanceTierPriceEntryFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		InstanceTierPriceEntry instanceTierPriceEntry =
			(InstanceTierPriceEntry)model;

		CommerceTierPriceEntry commerceTierPriceEntry =
			_commerceTierPriceEntryService.getCommerceTierPriceEntry(
				instanceTierPriceEntry.getTierPriceEntryId());

		CommercePriceEntry commercePriceEntry =
			commerceTierPriceEntry.getCommercePriceEntry();

		return DropdownItemListBuilder.add(
			() -> _commercePriceListModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				commercePriceEntry.getCommercePriceListId(), ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setHref(
					_getInstanceTierPriceEntryEditURL(
						commerceTierPriceEntry, httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
				dropdownItem.setTarget("modal");
			}
		).add(
			() -> _commercePriceListModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				commercePriceEntry.getCommercePriceListId(), ActionKeys.DELETE),
			dropdownItem -> {
				dropdownItem.setHref(
					_getInstanceTierPriceEntryDeleteURL(
						commerceTierPriceEntry, httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "delete"));
			}
		).build();
	}

	private PortletURL _getInstanceTierPriceEntryDeleteURL(
			CommerceTierPriceEntry commerceTierPriceEntry,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			commerceTierPriceEntry.getCommercePriceEntry();

		CPInstance cpInstance = _cpInstanceLocalService.fetchCProductInstance(
			commercePriceEntry.getCProductId(),
			commercePriceEntry.getCPInstanceUuid());

		if (cpInstance == null) {
			return null;
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, CPPortletKeys.CP_DEFINITIONS,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/cp_definitions/edit_cp_instance_commerce_tier_price_entry"
		).setCMD(
			Constants.DELETE
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				_portal.getCurrentURL(httpServletRequest))
		).setParameter(
			"commercePriceEntryId",
			commerceTierPriceEntry.getCommercePriceEntryId()
		).setParameter(
			"commerceTierPriceEntryId",
			commerceTierPriceEntry.getCommerceTierPriceEntryId()
		).setParameter(
			"cpDefinitionId", cpInstance.getCPDefinitionId()
		).setParameter(
			"cpInstanceId", cpInstance.getCPInstanceId()
		).buildPortletURL();
	}

	private PortletURL _getInstanceTierPriceEntryEditURL(
			CommerceTierPriceEntry commerceTierPriceEntry,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			commerceTierPriceEntry.getCommercePriceEntry();

		CPInstance cpInstance = _cpInstanceLocalService.fetchCProductInstance(
			commercePriceEntry.getCProductId(),
			commercePriceEntry.getCPInstanceUuid());

		if (cpInstance == null) {
			return null;
		}

		PortletURL portletURL = PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, CPDefinition.class.getName(),
				PortletProvider.Action.MANAGE)
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_instance_commerce_tier_price_entry"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"commercePriceEntryId",
			commerceTierPriceEntry.getCommercePriceEntryId()
		).setParameter(
			"commerceTierPriceEntryId",
			commerceTierPriceEntry.getCommerceTierPriceEntryId()
		).setParameter(
			"cpDefinitionId", cpInstance.getCPDefinitionId()
		).setParameter(
			"cpInstanceId", cpInstance.getCPInstanceId()
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		return portletURL;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPInstanceTierPriceEntryFDSActionProvider.class);

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceList)"
	)
	private ModelResourcePermission<CommercePriceList>
		_commercePriceListModelResourcePermission;

	@Reference
	private CommerceTierPriceEntryService _commerceTierPriceEntryService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}