/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.portlet.action;

import com.liferay.audiences.constants.AudiencesPortletKeys;
import com.liferay.audiences.exception.AudiencesEntryJSONException;
import com.liferay.audiences.exception.AudiencesEntryNameException;
import com.liferay.audiences.exception.DuplicateAudiencesEntryExternalReferenceCodeException;
import com.liferay.audiences.exception.NoSuchAudiencesEntryException;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryService;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AudiencesPortletKeys.AUDIENCES,
		"mvc.command.name=/audiences/update_audiences_entry"
	},
	service = MVCActionCommand.class
)
public class UpdateAudiencesEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long audiencesEntryId = ParamUtil.getLong(
			actionRequest, "audiencesEntryId");

		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");
		String json = ParamUtil.getString(actionRequest, "json");
		String name = ParamUtil.getString(actionRequest, "name");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			AudiencesEntry.class.getName(), actionRequest);

		try {
			AudiencesEntry audiencesEntry = null;

			if (audiencesEntryId <= 0) {
				audiencesEntry = _audiencesEntryService.addAudiencesEntry(
					externalReferenceCode, json, name, serviceContext);
			}
			else {
				audiencesEntry = _audiencesEntryService.updateAudiencesEntry(
					audiencesEntryId, externalReferenceCode, json, name);
			}

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			if (Validator.isNotNull(redirect)) {
				redirect = HttpComponentsUtil.setParameter(
					redirect, "audiencesEntryId",
					audiencesEntry.getAudiencesEntryId());
			}

			boolean saveAndContinue = ParamUtil.get(
				actionRequest, "saveAndContinue", false);

			if (saveAndContinue) {
				redirect = _getSaveAndContinueRedirect(
					actionRequest, audiencesEntry, redirect);
			}

			sendRedirect(actionRequest, actionResponse, redirect);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchAudiencesEntryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof AudiencesEntryJSONException ||
					 exception instanceof AudiencesEntryNameException ||
					 exception instanceof
						 DuplicateAudiencesEntryExternalReferenceCodeException) {

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);

				actionResponse.setRenderParameter(
					"mvcRenderCommandName", "/audiences/edit_audiences_entry");
			}
			else {
				throw exception;
			}
		}
	}

	private String _getSaveAndContinueRedirect(
		ActionRequest actionRequest, AudiencesEntry audiencesEntry,
		String redirect) {

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(actionRequest);

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createRenderURL(
				_portal.getPortletId(actionRequest))
		).setMVCRenderCommandName(
			"/audiences/edit_audiences_entry"
		).setCMD(
			Constants.UPDATE
		).setRedirect(
			redirect
		).setParameter(
			"audiencesEntryId", audiencesEntry.getAudiencesEntryId()
		).setWindowState(
			actionRequest.getWindowState()
		).buildString();
	}

	@Reference
	private AudiencesEntryService _audiencesEntryService;

	@Reference
	private Portal _portal;

}