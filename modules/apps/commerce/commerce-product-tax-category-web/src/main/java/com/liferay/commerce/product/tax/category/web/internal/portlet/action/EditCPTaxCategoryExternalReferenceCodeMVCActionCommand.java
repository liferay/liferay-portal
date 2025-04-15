/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.tax.category.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.DuplicateCPTaxCategoryException;
import com.liferay.commerce.product.exception.NoSuchCPTaxCategoryException;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_TAX_CATEGORY,
		"mvc.command.name=/cp_tax_category/edit_cp_tax_category_external_reference_code"
	},
	service = MVCActionCommand.class
)
public class EditCPTaxCategoryExternalReferenceCodeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_updateCPTaxCategoryExternalReferenceCode(actionRequest);
		}
		catch (Exception exception) {
			if (exception instanceof DuplicateCPTaxCategoryException ||
				exception instanceof NoSuchCPTaxCategoryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcPath",
					"/cp_tax_category" +
						"/edit_cp_tax_category_external_reference_code.jsp");

				hideDefaultErrorMessage(actionRequest);
			}
			else {
				_log.error(exception);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
	}

	private void _updateCPTaxCategoryExternalReferenceCode(
			ActionRequest actionRequest)
		throws Exception {

		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");

		long cpTaxCategoryId = ParamUtil.getLong(
			actionRequest, "cpTaxCategoryId");

		CPTaxCategory cpTaxCategory =
			_cpTaxCategoryLocalService.getCPTaxCategory(cpTaxCategoryId);

		_cpTaxCategoryLocalService.updateCPTaxCategory(
			externalReferenceCode, cpTaxCategoryId, cpTaxCategory.getNameMap(),
			cpTaxCategory.getDescriptionMap());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCPTaxCategoryExternalReferenceCodeMVCActionCommand.class);

	@Reference
	private CPTaxCategoryLocalService _cpTaxCategoryLocalService;

}