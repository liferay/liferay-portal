/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.service.CPDefinitionOptionRelService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_cp_definition_option_rel"
	},
	service = MVCActionCommand.class
)
public class EditCPDefinitionOptionRelMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		long cpDefinitionOptionRelId = ParamUtil.getLong(
			actionRequest, "cpDefinitionOptionRelId");

		try {
			if (cmd.equals(Constants.ADD) ||
				cmd.equals(Constants.ADD_MULTIPLE)) {

				_addCPDefinitionOptionRels(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCPDefinitionOptionRels(
					cpDefinitionOptionRelId, actionRequest);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				_updateCPDefinitionOptionRel(
					cpDefinitionOptionRelId, actionRequest);
			}
		}
		catch (Exception exception) {
			hideDefaultErrorMessage(actionRequest);

			SessionErrors.add(actionRequest, exception.getClass(), exception);

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			sendRedirect(actionRequest, actionResponse, redirect);
		}
	}

	private void _addCPDefinitionOptionRels(ActionRequest actionRequest)
		throws Exception {

		long[] addCPOptionIds = null;

		long cpDefinitionId = ParamUtil.getLong(
			actionRequest, "cpDefinitionId");

		long cpOptionId = ParamUtil.getLong(actionRequest, "cpOptionId");

		if (cpOptionId > 0) {
			addCPOptionIds = new long[] {cpOptionId};
		}
		else {
			addCPOptionIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "cpOptionIds"), 0L);
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPDefinitionOptionRel.class.getName(), actionRequest);

		for (long addCPOptionId : addCPOptionIds) {
			_cpDefinitionOptionRelService.addCPDefinitionOptionRel(
				cpDefinitionId, addCPOptionId, serviceContext);
		}
	}

	private void _deleteCPDefinitionOptionRels(
			long cpDefinitionOptionRelId, ActionRequest actionRequest)
		throws Exception {

		long[] deleteCPDefinitionOptionRelIds = null;

		if (cpDefinitionOptionRelId > 0) {
			deleteCPDefinitionOptionRelIds = new long[] {
				cpDefinitionOptionRelId
			};
		}
		else {
			deleteCPDefinitionOptionRelIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "deleteCPDefinitionOptionRelIds"),
				0L);
		}

		for (long deleteCPDefinitionOptionRelId :
				deleteCPDefinitionOptionRelIds) {

			_cpDefinitionOptionRelService.deleteCPDefinitionOptionRel(
				deleteCPDefinitionOptionRelId);
		}
	}

	private CPDefinitionOptionRel _updateCPDefinitionOptionRel(
			long cpDefinitionOptionRelId, ActionRequest actionRequest)
		throws Exception {

		long cpOptionId = ParamUtil.getLong(actionRequest, "cpOptionId");
		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");
		String commerceOptionTypeKey = ParamUtil.getString(
			actionRequest, "commerceOptionTypeKey");
		String infoItemServiceKey = ParamUtil.getString(
			actionRequest, "infoItemServiceKey");
		double priority = ParamUtil.getDouble(actionRequest, "priority");
		boolean definedExternally = ParamUtil.getBoolean(
			actionRequest, "definedExternally");
		boolean facetable = ParamUtil.getBoolean(actionRequest, "facetable");
		boolean required = ParamUtil.getBoolean(actionRequest, "required");
		boolean skuContributor = ParamUtil.getBoolean(
			actionRequest, "skuContributor");
		String priceType = ParamUtil.getString(actionRequest, "priceType");

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelService.getCPDefinitionOptionRel(
				cpDefinitionOptionRelId);

		UnicodeProperties typeSettingsUnicodeProperties =
			cpDefinitionOptionRel.getTypeSettingsUnicodeProperties();

		long[] categoryIds = ParamUtil.getLongValues(
			actionRequest, "categoryIds");

		typeSettingsUnicodeProperties.put(
			"categoryIds", StringUtil.merge(categoryIds));

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPDefinitionOptionRel.class.getName(), actionRequest);

		return _cpDefinitionOptionRelService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRelId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, infoItemServiceKey, priority,
			definedExternally, facetable, required, skuContributor, priceType,
			typeSettingsUnicodeProperties.toString(), serviceContext);
	}

	@Reference
	private CPDefinitionOptionRelService _cpDefinitionOptionRelService;

	@Reference
	private Localization _localization;

}