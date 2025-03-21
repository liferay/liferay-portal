/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.NoSuchCPConfigurationEntryException;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.service.CPConfigurationEntryService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_CONFIGURATION_LISTS,
		"mvc.command.name=/cp_configuration_lists/edit_cp_configuration_entry"
	},
	service = MVCActionCommand.class
)
public class EditCPConfigurationEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals("setVisible")) {
				_updateCPConfigurationEntries(actionRequest, true);
			}
			else if (cmd.equals("setHidden")) {
				_updateCPConfigurationEntries(actionRequest, false);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchCPConfigurationEntryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				_log.error(exception);
			}
		}
	}

	private void _updateCPConfigurationEntries(
			ActionRequest actionRequest, boolean visible)
		throws Exception {

		long[] updateCPConfigurationEntryIds = null;

		long cpConfigurationEntryId = ParamUtil.getLong(
			actionRequest, "cpConfigurationEntryId");

		if (cpConfigurationEntryId > 0) {
			updateCPConfigurationEntryIds = new long[] {cpConfigurationEntryId};
		}
		else {
			updateCPConfigurationEntryIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "id"), 0L);
		}

		for (long updateCPConfigurationEntryId :
				updateCPConfigurationEntryIds) {

			CPConfigurationEntry cpConfigurationEntry =
				_cpConfigurationEntryService.getCPConfigurationEntry(
					updateCPConfigurationEntryId);

			_cpConfigurationEntryService.updateCPConfigurationEntry(
				cpConfigurationEntry.getExternalReferenceCode(),
				cpConfigurationEntry.getCPConfigurationEntryId(),
				cpConfigurationEntry.getCPTaxCategoryId(),
				cpConfigurationEntry.getAllowedOrderQuantities(),
				cpConfigurationEntry.isBackOrders(),
				cpConfigurationEntry.getCommerceAvailabilityEstimateId(),
				cpConfigurationEntry.getCPDefinitionInventoryEngine(),
				cpConfigurationEntry.getDepth(),
				cpConfigurationEntry.isDisplayAvailability(),
				cpConfigurationEntry.isDisplayStockQuantity(),
				cpConfigurationEntry.isFreeShipping(),
				cpConfigurationEntry.getHeight(),
				cpConfigurationEntry.getLowStockActivity(),
				cpConfigurationEntry.getMaxOrderQuantity(),
				cpConfigurationEntry.getMinOrderQuantity(),
				cpConfigurationEntry.getMinStockQuantity(),
				cpConfigurationEntry.getMultipleOrderQuantity(),
				cpConfigurationEntry.isPurchasable(),
				cpConfigurationEntry.isShippable(),
				cpConfigurationEntry.getShippingExtraPrice(),
				cpConfigurationEntry.isShipSeparately(),
				cpConfigurationEntry.isTaxExempt(), visible,
				cpConfigurationEntry.getWeight(),
				cpConfigurationEntry.getWidth());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCPConfigurationEntryMVCActionCommand.class);

	@Reference
	private CPConfigurationEntryService _cpConfigurationEntryService;

}