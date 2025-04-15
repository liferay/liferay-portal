/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.data.set.provider;

import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryService;
import com.liferay.commerce.pricing.web.internal.constants.CommercePricingFDSNames;
import com.liferay.commerce.pricing.web.internal.model.InstancePriceEntry;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CPDefinition;
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
import com.liferay.portal.kernel.security.permission.PermissionChecker;
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
	property = "fds.data.provider.key=" + CommercePricingFDSNames.INSTANCE_PRICE_ENTRIES,
	service = FDSActionProvider.class
)
public class CPInstancePriceEntryFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		InstancePriceEntry instancePriceEntry = (InstancePriceEntry)model;

		long cpDefinitionId = ParamUtil.getLong(
			httpServletRequest, "cpDefinitionId");
		long cpInstanceId = ParamUtil.getLong(
			httpServletRequest, "cpInstanceId");

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryService.getCommercePriceEntry(
				instancePriceEntry.getPriceEntryId());

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return DropdownItemListBuilder.add(
			() -> _commercePriceListModelResourcePermission.contains(
				permissionChecker, commercePriceEntry.getCommercePriceListId(),
				ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setHref(
					_getInstancePriceEntryEditURL(
						commercePriceEntry, cpDefinitionId, cpInstanceId,
						httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
				dropdownItem.setTarget("sidePanel");
			}
		).add(
			() -> _commercePriceListModelResourcePermission.contains(
				permissionChecker, commercePriceEntry.getCommercePriceListId(),
				ActionKeys.DELETE),
			dropdownItem -> {
				dropdownItem.setHref(
					_getInstancePriceEntryDeleteURL(
						commercePriceEntry, cpDefinitionId, cpInstanceId,
						httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "delete"));
			}
		).build();
	}

	private PortletURL _getInstancePriceEntryDeleteURL(
			CommercePriceEntry commercePriceEntry, long cpDefinitionId,
			long cpInstanceId, HttpServletRequest httpServletRequest)
		throws PortalException {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, CPPortletKeys.CP_DEFINITIONS,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/cp_definitions/edit_cp_instance_commerce_price_entry"
		).setCMD(
			Constants.DELETE
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				_portal.getCurrentURL(httpServletRequest))
		).setParameter(
			"commercePriceEntryId", commercePriceEntry.getCommercePriceEntryId()
		).setParameter(
			"cpDefinitionId", cpDefinitionId
		).setParameter(
			"cpInstanceId", cpInstanceId
		).buildPortletURL();
	}

	private PortletURL _getInstancePriceEntryEditURL(
			CommercePriceEntry commercePriceEntry, long cpDefinitionId,
			long cpInstanceId, HttpServletRequest httpServletRequest)
		throws PortalException {

		PortletURL portletURL = PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, CPDefinition.class.getName(),
				PortletProvider.Action.MANAGE)
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_instance_commerce_price_entry"
		).setRedirect(
			"/cp_definitions/edit_cp_instance_commerce_price_entry"
		).setParameter(
			"commercePriceEntryId", commercePriceEntry.getCommercePriceEntryId()
		).setParameter(
			"cpDefinitionId", cpDefinitionId
		).setParameter(
			"cpInstanceId", cpInstanceId
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
		CPInstancePriceEntryFDSActionProvider.class);

	@Reference
	private CommercePriceEntryService _commercePriceEntryService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceList)"
	)
	private ModelResourcePermission<CommercePriceList>
		_commercePriceListModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}