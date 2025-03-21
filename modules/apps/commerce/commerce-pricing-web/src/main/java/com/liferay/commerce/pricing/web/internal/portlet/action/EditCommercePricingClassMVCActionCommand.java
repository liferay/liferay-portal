/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.portlet.action;

import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.service.CommercePricingClassService;
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
 * @author Riccardo Alberti
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePricingPortletKeys.COMMERCE_PRICING_CLASSES,
		"mvc.command.name=/commerce_pricing_classes/edit_commerce_pricing_class"
	},
	service = MVCActionCommand.class
)
public class EditCommercePricingClassMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				updateCommercePricingClass(actionRequest);
			}
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");
		}
	}

	protected CommercePricingClass updateCommercePricingClass(
			ActionRequest actionRequest)
		throws Exception {

		CommercePricingClass commercePricingClass = null;

		Map<Locale, String> titleMap = _localization.getLocalizationMap(
			actionRequest, "title");

		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CommercePricingClass.class.getName(), actionRequest);

		long commercePricingClassId = ParamUtil.getLong(
			actionRequest, "commercePricingClassId");

		if (commercePricingClassId <= 0) {
			commercePricingClass =
				_commercePricingClassService.addCommercePricingClass(
					null, titleMap, descriptionMap, serviceContext);
		}
		else {
			commercePricingClass =
				_commercePricingClassService.updateCommercePricingClass(
					commercePricingClassId, titleMap, descriptionMap,
					serviceContext);
		}

		return commercePricingClass;
	}

	@Reference
	private CommercePricingClassService _commercePricingClassService;

	@Reference
	private Localization _localization;

}