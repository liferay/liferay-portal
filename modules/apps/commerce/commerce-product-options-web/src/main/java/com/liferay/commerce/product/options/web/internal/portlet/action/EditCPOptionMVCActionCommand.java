/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.CPOptionKeyException;
import com.liferay.commerce.product.exception.CPOptionSKUContributorException;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.CPOptionService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
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
 * @author Marco Leo
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_OPTIONS,
		"mvc.command.name=/cp_options/edit_cp_option"
	},
	service = MVCActionCommand.class
)
public class EditCPOptionMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		long cpOptionId = ParamUtil.getLong(actionRequest, "cpOptionId");

		try {
			if (cmd.equals(Constants.UPDATE)) {
				_updateCPOption(cpOptionId, actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof CPOptionKeyException ||
				exception instanceof CPOptionSKUContributorException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				_log.error(exception);

				throw new Exception(exception);
			}
		}
	}

	private CPOption _updateCPOption(
			long cpOptionId, ActionRequest actionRequest)
		throws Exception {

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");
		String commerceOptionTypeKey = ParamUtil.getString(
			actionRequest, "commerceOptionTypeKey");
		boolean facetable = ParamUtil.getBoolean(actionRequest, "facetable");
		boolean required = ParamUtil.getBoolean(actionRequest, "required");
		boolean skuContributor = ParamUtil.getBoolean(
			actionRequest, "skuContributor");
		String key = ParamUtil.getString(actionRequest, "key");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPOption.class.getName(), actionRequest);

		return _cpOptionService.updateCPOption(
			cpOptionId, nameMap, descriptionMap, commerceOptionTypeKey,
			facetable, required, skuContributor, key, serviceContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCPOptionMVCActionCommand.class);

	@Reference
	private CPOptionService _cpOptionService;

	@Reference
	private Localization _localization;

}