/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.web.internal.portlet.action;

import com.liferay.commerce.term.constants.CommerceTermEntryPortletKeys;
import com.liferay.commerce.term.exception.DuplicateCommerceTermEntryExternalReferenceCodeException;
import com.liferay.commerce.term.exception.NoSuchTermEntryException;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommerceTermEntryPortletKeys.COMMERCE_TERM_ENTRY,
		"mvc.command.name=/commerce_term_entry/edit_commerce_term_entry_external_reference_code"
	},
	service = MVCActionCommand.class
)
public class EditCommerceTermEntryExternalReferenceCodeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			long commerceTermEntryId = ParamUtil.getLong(
				actionRequest, "commerceTermEntryId");

			CommerceTermEntry commerceTermEntry =
				_commerceTermEntryService.getCommerceTermEntry(
					commerceTermEntryId);

			String externalReferenceCode = ParamUtil.getString(
				actionRequest, "externalReferenceCode");

			_commerceTermEntryService.
				updateCommerceTermEntryExternalReferenceCode(
					externalReferenceCode,
					commerceTermEntry.getCommerceTermEntryId());
		}
		catch (Exception exception) {
			if (exception instanceof
					DuplicateCommerceTermEntryExternalReferenceCodeException ||
				exception instanceof NoSuchTermEntryException) {

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

	private static final Log _log = LogFactoryUtil.getLog(
		EditCommerceTermEntryExternalReferenceCodeMVCActionCommand.class);

	@Reference
	private CommerceTermEntryService _commerceTermEntryService;

}