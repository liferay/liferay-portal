/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.CPSpecificationOptionKeyException;
import com.liferay.commerce.product.exception.NoSuchCPSpecificationOptionException;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.CPSpecificationOptionService;
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
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_SPECIFICATION_OPTIONS,
		"mvc.command.name=/cp_specification_options/edit_cp_specification_option"
	},
	service = MVCActionCommand.class
)
public class EditCPSpecificationOptionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteCPSpecificationOptions(actionRequest);
			}
			else if (cmd.equals(Constants.ADD) ||
					 cmd.equals(Constants.UPDATE)) {

				_updateCPSpecificationOption(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchCPSpecificationOptionException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof CPSpecificationOptionKeyException) {
				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcRenderCommandName",
					"/cp_specification_options/edit_cp_specification_option");
			}
			else {
				throw exception;
			}
		}
	}

	private void _deleteCPSpecificationOptions(ActionRequest actionRequest)
		throws Exception {

		long[] deleteCPSpecificationOptionIds = null;

		long cpSpecificationOptionId = ParamUtil.getLong(
			actionRequest, "cpSpecificationOptionId");

		if (cpSpecificationOptionId > 0) {
			deleteCPSpecificationOptionIds = new long[] {
				cpSpecificationOptionId
			};
		}
		else {
			deleteCPSpecificationOptionIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		for (long deleteCPSpecificationOptionId :
				deleteCPSpecificationOptionIds) {

			_cpSpecificationOptionService.deleteCPSpecificationOption(
				deleteCPSpecificationOptionId);
		}
	}

	private CPSpecificationOption _updateCPSpecificationOption(
			ActionRequest actionRequest)
		throws Exception {

		long cpSpecificationOptionId = ParamUtil.getLong(
			actionRequest, "cpSpecificationOptionId");

		long cpOptionCategoryId = ParamUtil.getLong(
			actionRequest, "CPOptionCategoryId");
		Map<Locale, String> titleMap = _localization.getLocalizationMap(
			actionRequest, "title");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");
		boolean facetable = ParamUtil.getBoolean(actionRequest, "facetable");
		String key = ParamUtil.getString(actionRequest, "key");
		double priority = ParamUtil.getDouble(actionRequest, "priority");
		boolean visible = ParamUtil.getBoolean(actionRequest, "visible");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPSpecificationOption.class.getName(), actionRequest);

		CPSpecificationOption cpSpecificationOption = null;

		if (cpSpecificationOptionId <= 0) {

			// Add commerce product specification option

			cpSpecificationOption =
				_cpSpecificationOptionService.addCPSpecificationOption(
					null, cpOptionCategoryId, null, titleMap, descriptionMap,
					facetable, key, priority, visible, serviceContext);
		}
		else {

			// Update commerce product specification option

			cpSpecificationOption =
				_cpSpecificationOptionService.updateCPSpecificationOption(
					null, cpSpecificationOptionId, cpOptionCategoryId, null,
					titleMap, descriptionMap, facetable, key, priority, visible,
					serviceContext);
		}

		return cpSpecificationOption;
	}

	@Reference
	private CPSpecificationOptionService _cpSpecificationOptionService;

	@Reference
	private Localization _localization;

}