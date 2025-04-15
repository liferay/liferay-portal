/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.CPDefinitionLinkExpirationDateException;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionLinkException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.model.CPDefinitionLinkModel;
import com.liferay.commerce.product.service.CPDefinitionLinkService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_cp_definition_link"
	},
	service = MVCActionCommand.class
)
public class EditCPDefinitionLinkMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		try {
			if (cmd.equals(Constants.ADD)) {
				_addCPDefinitionLinks(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCPDefinitionLinks(actionRequest);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				_updateCPDefinitionLink(actionRequest);
			}

			sendRedirect(actionRequest, actionResponse, redirect);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchCPDefinitionLinkException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof
						CPDefinitionLinkExpirationDateException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else {
				throw exception;
			}
		}
	}

	private void _addCPDefinitionLinks(ActionRequest actionRequest)
		throws Exception {

		long[] cpDefinitionIds2 = null;

		long cpDefinitionId = ParamUtil.getLong(
			actionRequest, "cpDefinitionId");
		long cpDefinitionLinkId = ParamUtil.getLong(
			actionRequest, "cpDefinitionLinkId");

		String type = ParamUtil.getString(actionRequest, "type");

		if (cpDefinitionLinkId > 0) {
			cpDefinitionIds2 = new long[] {cpDefinitionLinkId};
		}
		else {
			cpDefinitionIds2 = StringUtil.split(
				ParamUtil.getString(actionRequest, "cpDefinitionIds"), 0L);
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPDefinitionLink.class.getName(), actionRequest);

		long[] cProductIds = ListUtil.toLongArray(
			_cpDefinitionLinkService.getCPDefinitionLinks(cpDefinitionId, type),
			CPDefinitionLinkModel::getCProductId);

		boolean successMessage = false;

		for (long curCPDefinitionId : cpDefinitionIds2) {
			CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
				curCPDefinitionId);

			long cProductId = cpDefinition.getCProductId();

			if (!ArrayUtil.contains(cProductIds, cProductId)) {
				Calendar calendar = new GregorianCalendar();

				calendar.setTime(new Date());

				_cpDefinitionLinkService.addCPDefinitionLink(
					cpDefinitionId, cProductId, calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH),
					calendar.get(Calendar.YEAR),
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true, 0.0,
					type, serviceContext);

				successMessage = true;
			}
		}

		if (!successMessage) {
			hideDefaultSuccessMessage(actionRequest);
		}
	}

	private void _deleteCPDefinitionLinks(ActionRequest actionRequest)
		throws Exception {

		long[] deleteCPDefinitionLinkIds = null;

		long cpDefinitionLinkId = ParamUtil.getLong(
			actionRequest, "cpDefinitionLinkId");

		if (cpDefinitionLinkId > 0) {
			deleteCPDefinitionLinkIds = new long[] {cpDefinitionLinkId};
		}
		else {
			deleteCPDefinitionLinkIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "cpdefinitionLinkId"), 0L);
		}

		for (long deleteCPDefinitionLinkId : deleteCPDefinitionLinkIds) {
			_cpDefinitionLinkService.deleteCPDefinitionLink(
				deleteCPDefinitionLinkId);
		}
	}

	private CPDefinitionLink _updateCPDefinitionLink(
			ActionRequest actionRequest)
		throws Exception {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPDefinitionLink.class.getName(), actionRequest);

		long cpDefinitionLinkId = ParamUtil.getLong(
			actionRequest, "cpDefinitionLinkId");

		int displayDateMonth = ParamUtil.getInteger(
			actionRequest, "displayDateMonth");
		int displayDateDay = ParamUtil.getInteger(
			actionRequest, "displayDateDay");
		int displayDateYear = ParamUtil.getInteger(
			actionRequest, "displayDateYear");
		int displayDateHour = ParamUtil.getInteger(
			actionRequest, "displayDateHour");
		int displayDateMinute = ParamUtil.getInteger(
			actionRequest, "displayDateMinute");
		int displayDateAmPm = ParamUtil.getInteger(
			actionRequest, "displayDateAmPm");

		if (displayDateAmPm == Calendar.PM) {
			displayDateHour += 12;
		}

		int expirationDateMonth = ParamUtil.getInteger(
			actionRequest, "expirationDateMonth");
		int expirationDateDay = ParamUtil.getInteger(
			actionRequest, "expirationDateDay");
		int expirationDateYear = ParamUtil.getInteger(
			actionRequest, "expirationDateYear");
		int expirationDateHour = ParamUtil.getInteger(
			actionRequest, "expirationDateHour");
		int expirationDateMinute = ParamUtil.getInteger(
			actionRequest, "expirationDateMinute");
		int expirationDateAmPm = ParamUtil.getInteger(
			actionRequest, "expirationDateAmPm");

		if (expirationDateAmPm == Calendar.PM) {
			expirationDateHour += 12;
		}

		boolean neverExpire = ParamUtil.getBoolean(
			actionRequest, "neverExpire");
		double priority = ParamUtil.getDouble(actionRequest, "priority");

		return _cpDefinitionLinkService.updateCPDefinitionLink(
			cpDefinitionLinkId, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire, priority,
			serviceContext);
	}

	@Reference
	private CPDefinitionLinkService _cpDefinitionLinkService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

}