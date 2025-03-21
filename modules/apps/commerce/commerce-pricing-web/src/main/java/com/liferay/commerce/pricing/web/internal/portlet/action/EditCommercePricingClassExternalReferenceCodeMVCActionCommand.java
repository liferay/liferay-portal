/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.portlet.action;

import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.commerce.pricing.exception.DuplicateCommercePricingClassExternalReferenceCodeException;
import com.liferay.commerce.pricing.exception.NoSuchPricingClassException;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.service.CommercePricingClassService;
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
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePricingPortletKeys.COMMERCE_PRICING_CLASSES,
		"mvc.command.name=/commerce_pricing_classes/edit_commerce_pricing_class_external_reference_code"
	},
	service = MVCActionCommand.class
)
public class EditCommercePricingClassExternalReferenceCodeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_updateCommercePricingClassExternalReferenceCode(actionRequest);
		}
		catch (Exception exception) {
			if (exception instanceof
					DuplicateCommercePricingClassExternalReferenceCodeException ||
				exception instanceof NoSuchPricingClassException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				_log.error(exception);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
	}

	private void _updateCommercePricingClassExternalReferenceCode(
			ActionRequest actionRequest)
		throws Exception {

		long commercePricingClassId = ParamUtil.getLong(
			actionRequest, "commercePricingClassId");

		CommercePricingClass commercePricingClass =
			_commercePricingClassService.getCommercePricingClass(
				commercePricingClassId);

		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");

		_commercePricingClassService.
			updateCommercePricingClassExternalReferenceCode(
				externalReferenceCode,
				commercePricingClass.getCommercePricingClassId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCommercePricingClassExternalReferenceCodeMVCActionCommand.class);

	@Reference
	private CommercePricingClassService _commercePricingClassService;

}