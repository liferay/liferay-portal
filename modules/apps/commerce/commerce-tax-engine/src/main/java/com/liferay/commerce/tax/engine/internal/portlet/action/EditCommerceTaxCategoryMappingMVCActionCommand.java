/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.product.exception.NoSuchCPTaxCategoryException;
import com.liferay.commerce.tax.exception.DuplicateCommerceTaxCategoryMappingExternalReferenceCodeException;
import com.liferay.commerce.tax.exception.NoSuchTaxCategoryMappingException;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxCategoryMappingService;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.portal.kernel.exception.PortalException;
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
 * @author Ivica Cardic
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_TAX_METHODS,
		"mvc.command.name=/commerce_tax_methods/edit_commerce_tax_category_mapping"
	},
	service = MVCActionCommand.class
)
public class EditCommerceTaxCategoryMappingMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCommerceTaxCategoryMapping(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceTaxCategoryMappings(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof
					DuplicateCommerceTaxCategoryMappingExternalReferenceCodeException ||
				exception instanceof NoSuchCPTaxCategoryException ||
				exception instanceof NoSuchTaxCategoryMappingException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				throw exception;
			}
		}
	}

	private void _deleteCommerceTaxCategoryMappings(ActionRequest actionRequest)
		throws PortalException {

		long[] deleteCommerceTaxCategoryMappingIds = null;

		long commerceTaxCategoryMappingId = ParamUtil.getLong(
			actionRequest, "commerceTaxCategoryMappingId");

		if (commerceTaxCategoryMappingId > 0) {
			deleteCommerceTaxCategoryMappingIds = new long[] {
				commerceTaxCategoryMappingId
			};
		}
		else {
			deleteCommerceTaxCategoryMappingIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "deleteCommerceTaxCategoryMappingIds"),
				0L);
		}

		for (long deleteCommerceTaxCategoryMappingId :
				deleteCommerceTaxCategoryMappingIds) {

			_commerceTaxCategoryMappingService.deleteCommerceTaxCategoryMapping(
				deleteCommerceTaxCategoryMappingId);
		}
	}

	private void _updateCommerceTaxCategoryMapping(ActionRequest actionRequest)
		throws Exception {

		long commerceTaxCategoryMappingId = ParamUtil.getLong(
			actionRequest, "commerceTaxCategoryMappingId");
		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");

		if (commerceTaxCategoryMappingId > 0) {
			_commerceTaxCategoryMappingService.updateExternalReferenceCode(
				commerceTaxCategoryMappingId, externalReferenceCode);
		}
		else {
			long commerceTaxMethodId = ParamUtil.getLong(
				actionRequest, "commerceTaxMethodId");
			long cpTaxCategoryId = ParamUtil.getLong(
				actionRequest, "CPTaxCategoryId");

			CommerceTaxMethod commerceTaxMethod =
				_commerceTaxMethodService.getCommerceTaxMethod(
					commerceTaxMethodId);

			_commerceTaxCategoryMappingService.addCommerceTaxCategoryMapping(
				externalReferenceCode, commerceTaxMethod.getGroupId(),
				commerceTaxMethod.getCommerceTaxMethodId(), cpTaxCategoryId);
		}
	}

	@Reference
	private CommerceTaxCategoryMappingService
		_commerceTaxCategoryMappingService;

	@Reference
	private CommerceTaxMethodService _commerceTaxMethodService;

}