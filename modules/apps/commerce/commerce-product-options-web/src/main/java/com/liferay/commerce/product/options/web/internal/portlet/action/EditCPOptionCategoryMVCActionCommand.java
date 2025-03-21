/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.CPOptionCategoryKeyException;
import com.liferay.commerce.product.exception.NoSuchCPOptionCategoryException;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.service.CPOptionCategoryService;
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
		"jakarta.portlet.name=" + CPPortletKeys.CP_SPECIFICATION_OPTIONS,
		"mvc.command.name=/cp_specification_options/edit_cp_option_category"
	},
	service = MVCActionCommand.class
)
public class EditCPOptionCategoryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteCPOptionCategories(actionRequest);
			}
			else if (cmd.equals(Constants.ADD) ||
					 cmd.equals(Constants.UPDATE)) {

				_updateCPOptionCategory(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchCPOptionCategoryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof CPOptionCategoryKeyException) {
				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcRenderCommandName",
					"/cp_specification_options/edit_cp_option_category");
			}
			else {
				throw exception;
			}
		}
	}

	private void _deleteCPOptionCategories(ActionRequest actionRequest)
		throws Exception {

		long[] deleteCPOptionCategoryIds = null;

		long cpOptionCategoryId = ParamUtil.getLong(
			actionRequest, "cpOptionCategoryId");

		if (cpOptionCategoryId > 0) {
			deleteCPOptionCategoryIds = new long[] {cpOptionCategoryId};
		}
		else {
			deleteCPOptionCategoryIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		for (long deleteCPOptionCategoryId : deleteCPOptionCategoryIds) {
			_cpOptionCategoryService.deleteCPOptionCategory(
				deleteCPOptionCategoryId);
		}
	}

	private CPOptionCategory _updateCPOptionCategory(
			ActionRequest actionRequest)
		throws Exception {

		long cpOptionCategoryId = ParamUtil.getLong(
			actionRequest, "cpOptionCategoryId");

		Map<Locale, String> titleMap = _localization.getLocalizationMap(
			actionRequest, "title");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");
		double priority = ParamUtil.getDouble(actionRequest, "priority");
		String key = ParamUtil.getString(actionRequest, "key");

		CPOptionCategory cpOptionCategory = null;

		if (cpOptionCategoryId <= 0) {

			// Add commerce product option category

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				CPOptionCategory.class.getName(), actionRequest);

			cpOptionCategory = _cpOptionCategoryService.addCPOptionCategory(
				null, titleMap, descriptionMap, priority, key, serviceContext);
		}
		else {

			// Update commerce product option category

			cpOptionCategory = _cpOptionCategoryService.updateCPOptionCategory(
				null, cpOptionCategoryId, titleMap, descriptionMap, priority,
				key);
		}

		return cpOptionCategory;
	}

	@Reference
	private CPOptionCategoryService _cpOptionCategoryService;

	@Reference
	private Localization _localization;

}