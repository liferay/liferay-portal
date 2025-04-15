/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.DuplicateCProductExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCProductException;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CProductLocalService;
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
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_c_product_external_reference_code"
	},
	service = MVCActionCommand.class
)
public class EditCProductExternalReferenceCodeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_updateCProductExternalReferenceCode(actionRequest);
		}
		catch (Exception exception) {
			if (exception instanceof
					DuplicateCProductExternalReferenceCodeException ||
				exception instanceof NoSuchCProductException ||
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

	private void _updateCProductExternalReferenceCode(
			ActionRequest actionRequest)
		throws Exception {

		long cProductId = ParamUtil.getLong(actionRequest, "cProductId");

		CProduct cProduct = _cProductLocalService.getCProduct(cProductId);

		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");

		_cProductLocalService.updateCProductExternalReferenceCode(
			externalReferenceCode, cProduct.getCProductId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCProductExternalReferenceCodeMVCActionCommand.class);

	@Reference
	private CProductLocalService _cProductLocalService;

}