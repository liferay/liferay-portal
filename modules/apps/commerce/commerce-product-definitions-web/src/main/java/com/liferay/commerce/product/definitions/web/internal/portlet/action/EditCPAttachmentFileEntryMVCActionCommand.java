/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.CPAttachmentFileEntryExpirationDateException;
import com.liferay.commerce.product.exception.DuplicateCPAttachmentFileEntryException;
import com.liferay.commerce.product.exception.NoSuchCPAttachmentFileEntryException;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPAttachmentFileEntryService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_cp_attachment_file_entry"
	},
	service = MVCActionCommand.class
)
public class EditCPAttachmentFileEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCPAttachmentFileEntry(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCPAttachmentFileEntry(actionRequest);
			}

			sendRedirect(actionRequest, actionResponse, redirect);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchCPAttachmentFileEntryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof
						CPAttachmentFileEntryExpirationDateException ||
					 exception instanceof
						 DuplicateCPAttachmentFileEntryException ||
					 exception instanceof NoSuchFileEntryException) {

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

	private void _deleteCPAttachmentFileEntry(ActionRequest actionRequest)
		throws Exception {

		long[] deleteCPAttachmentFileEntryIds = null;

		long cpAttachmentFileEntryId = ParamUtil.getLong(
			actionRequest, "cpAttachmentFileEntryId");

		if (cpAttachmentFileEntryId > 0) {
			deleteCPAttachmentFileEntryIds = new long[] {
				cpAttachmentFileEntryId
			};
		}
		else {
			deleteCPAttachmentFileEntryIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "deleteCPAttachmentFileEntryIds"),
				0L);
		}

		for (long deleteCPAttachmentFileEntryId :
				deleteCPAttachmentFileEntryIds) {

			_cpAttachmentFileEntryService.deleteCPAttachmentFileEntry(
				deleteCPAttachmentFileEntryId);
		}
	}

	private void _updateCPAttachmentFileEntry(ActionRequest actionRequest)
		throws Exception {

		long cpAttachmentFileEntryId = ParamUtil.getLong(
			actionRequest, "cpAttachmentFileEntryId");

		long cpDefinitionId = ParamUtil.getLong(
			actionRequest, "cpDefinitionId");
		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		String cdnURL = ParamUtil.getString(actionRequest, "cdnURL");

		boolean cdnEnabled = Validator.isNotNull(cdnURL);

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

		boolean galleryEnabled = ParamUtil.getBoolean(
			actionRequest, "galleryEnabled");
		boolean neverExpire = ParamUtil.getBoolean(
			actionRequest, "neverExpire");
		String cpInstanceOptions = ParamUtil.getString(
			actionRequest, "cpInstanceOptions");
		Map<Locale, String> titleMap = _localization.getLocalizationMap(
			actionRequest, "title");
		double priority = ParamUtil.getDouble(actionRequest, "priority");
		int type = ParamUtil.getInteger(actionRequest, "type");

		CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
			cpDefinitionId);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPAttachmentFileEntry.class.getName(), actionRequest);

		serviceContext.setScopeGroupId(cpDefinition.getGroupId());

		if (cpAttachmentFileEntryId > 0) {
			_cpAttachmentFileEntryService.updateCPAttachmentFileEntry(
				cpAttachmentFileEntryId, fileEntryId, cdnEnabled, cdnURL,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, galleryEnabled, titleMap,
				cpInstanceOptions, priority, type, serviceContext);
		}
		else {
			_cpAttachmentFileEntryService.addCPAttachmentFileEntry(
				serviceContext.getScopeGroupId(),
				_portal.getClassNameId(CPDefinition.class), cpDefinitionId,
				fileEntryId, cdnEnabled, cdnURL, displayDateMonth,
				displayDateDay, displayDateYear, displayDateHour,
				displayDateMinute, expirationDateMonth, expirationDateDay,
				expirationDateYear, expirationDateHour, expirationDateMinute,
				neverExpire, galleryEnabled, titleMap, cpInstanceOptions,
				priority, type, serviceContext);
		}
	}

	@Reference
	private CPAttachmentFileEntryService _cpAttachmentFileEntryService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}