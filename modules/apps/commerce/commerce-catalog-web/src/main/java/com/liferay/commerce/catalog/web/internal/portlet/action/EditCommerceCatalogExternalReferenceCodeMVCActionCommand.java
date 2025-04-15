/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.catalog.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.DuplicateCommerceCatalogExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCatalogException;
import com.liferay.commerce.product.service.CommerceCatalogService;
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
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_CATALOGS,
		"mvc.command.name=/commerce_catalogs/edit_commerce_catalog_external_reference_code"
	},
	service = MVCActionCommand.class
)
public class EditCommerceCatalogExternalReferenceCodeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_updateCommerceCatalogExternalReferenceCode(actionRequest);
		}
		catch (Exception exception) {
			if (exception instanceof
					DuplicateCommerceCatalogExternalReferenceCodeException ||
				exception instanceof NoSuchCatalogException ||
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

	private void _updateCommerceCatalogExternalReferenceCode(
			ActionRequest actionRequest)
		throws Exception {

		long commerceCatalogId = ParamUtil.getLong(
			actionRequest, "commerceCatalogId");

		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");

		_commerceCatalogService.updateCommerceCatalogExternalReferenceCode(
			externalReferenceCode, commerceCatalogId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCommerceCatalogExternalReferenceCodeMVCActionCommand.class);

	@Reference
	private CommerceCatalogService _commerceCatalogService;

}