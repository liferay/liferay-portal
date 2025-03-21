/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.tax.category.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.CPTaxCategoryNameException;
import com.liferay.commerce.product.exception.DuplicateCPTaxCategoryException;
import com.liferay.commerce.product.exception.NoSuchCPTaxCategoryException;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.CPTaxCategoryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_TAX_CATEGORY,
		"mvc.command.name=/cp_tax_category/edit_cp_tax_category"
	},
	service = MVCActionCommand.class
)
public class EditCPTaxCategoryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteCPTaxCategories(actionRequest);
			}
			else if (cmd.equals(Constants.ADD) ||
					 cmd.equals(Constants.UPDATE)) {

				_addOrUpdateCPTaxCategory(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchCPTaxCategoryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof CPTaxCategoryNameException ||
					 exception instanceof DuplicateCPTaxCategoryException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				if (cmd.equals(Constants.ADD)) {
					actionResponse.setRenderParameter(
						"mvcRenderCommandName",
						"/cp_tax_category/add_cp_tax_category");
				}
				else {
					actionResponse.setRenderParameter(
						"mvcRenderCommandName",
						"/cp_tax_category/edit_cp_tax_category");
				}
			}
			else {
				throw exception;
			}
		}
	}

	private void _addOrUpdateCPTaxCategory(ActionRequest actionRequest)
		throws PortalException {

		long cpTaxCategoryId = ParamUtil.getLong(
			actionRequest, "cpTaxCategoryId");

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");

		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");

		if (cpTaxCategoryId <= 0) {
			String externalReferenceCode = ParamUtil.getString(
				actionRequest, "externalReferenceCode");

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				CPTaxCategory.class.getName(), actionRequest);

			_cpTaxCategoryService.addCPTaxCategory(
				externalReferenceCode, nameMap, descriptionMap, serviceContext);
		}
		else {
			CPTaxCategory cpTaxCategory =
				_cpTaxCategoryService.getCPTaxCategory(cpTaxCategoryId);

			_cpTaxCategoryService.updateCPTaxCategory(
				cpTaxCategory.getExternalReferenceCode(), cpTaxCategoryId,
				nameMap, descriptionMap);
		}
	}

	private void _deleteCPTaxCategories(ActionRequest actionRequest)
		throws PortalException {

		long[] deleteCPTaxCategoryIds = null;

		long cpTaxCategoryId = ParamUtil.getLong(
			actionRequest, "cpTaxCategoryId");

		if (cpTaxCategoryId > 0) {
			deleteCPTaxCategoryIds = new long[] {cpTaxCategoryId};
		}
		else {
			deleteCPTaxCategoryIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		for (long deleteCPTaxCategoryId : deleteCPTaxCategoryIds) {
			_cpTaxCategoryService.deleteCPTaxCategory(deleteCPTaxCategoryId);
		}
	}

	@Reference
	private CPTaxCategoryService _cpTaxCategoryService;

	@Reference
	private Localization _localization;

}