/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.portlet.action;

import com.liferay.portal.instances.web.internal.constants.PortalInstancesPortletKeys;
import com.liferay.portal.kernel.exception.CompanyNameException;
import com.liferay.portal.kernel.exception.CompanyVirtualHostException;
import com.liferay.portal.kernel.exception.CompanyWebIdException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Avalos
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortalInstancesPortletKeys.PORTAL_INSTANCES,
		"mvc.command.name=/portal_instances/copy_db_partition_company"
	},
	service = MVCActionCommand.class
)
public class CopyDBPartitionCompanyMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			Company company = _copyDBPartitionCompany(actionRequest);

			if (SessionMessages.contains(
					actionRequest,
					_portal.getPortletId(actionRequest) +
						SessionMessages.
							KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE)) {

				SessionMessages.clear(actionRequest);
			}

			SessionMessages.add(
				actionRequest, "requestProcessed",
				_language.format(
					actionRequest.getLocale(), "the-instance-was-copied-to-x",
					company.getWebId()));

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put("companyId", company.getCompanyId()));
		}
		catch (Exception exception) {
			String errorMessage = _getErrorMessage(exception);

			if (errorMessage.equals(_ERROR_UNEXPECTED)) {
				_log.error("Unable to copy portal instance", exception);
			}
			else if (_log.isDebugEnabled()) {
				_log.debug("Unable to copy portal instance", exception);
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"error",
					_language.get(actionRequest.getLocale(), errorMessage)));

			hideDefaultSuccessMessage(actionRequest);
		}
	}

	private Company _copyDBPartitionCompany(ActionRequest actionRequest)
		throws Exception {

		String name = ParamUtil.getString(actionRequest, "name");

		if (Validator.isNull(name)) {
			throw new CompanyNameException();
		}

		String virtualHostname = ParamUtil.getString(
			actionRequest, "virtualHostname");

		if (Validator.isNull(virtualHostname)) {
			throw new CompanyVirtualHostException();
		}

		String webId = ParamUtil.getString(actionRequest, "webId");

		if (Validator.isNull(webId)) {
			throw new CompanyWebIdException();
		}

		long sourceCompanyId = ParamUtil.getLong(
			actionRequest, "sourceCompanyId");

		return _companyService.copyDBPartitionCompany(
			sourceCompanyId, _getDestinationCompanyId(actionRequest), name,
			virtualHostname, webId);
	}

	private Long _getDestinationCompanyId(ActionRequest actionRequest) {
		String destinationCompanyId = ParamUtil.getString(
			actionRequest, "destinationCompanyId");

		if (Validator.isNull(destinationCompanyId)) {
			return null;
		}

		if (!Validator.isNumber(destinationCompanyId)) {
			throw new IllegalArgumentException();
		}

		try {
			return Long.parseLong(destinationCompanyId);
		}
		catch (NumberFormatException numberFormatException) {
			throw new IllegalArgumentException(numberFormatException);
		}
	}

	private String _getErrorMessage(Exception exception) {
		String message = GetterUtil.getString(exception.getMessage());

		if (exception instanceof IllegalArgumentException) {
			if (message.endsWith("is the default company ID")) {
				return "the-default-instance-cannot-be-copied";
			}

			return "please-enter-a-valid-destination-company-id";
		}

		if (exception instanceof UnsupportedOperationException) {
			if (message.equals(
					"Company in copy process company ID is not null")) {

				return "copying-an-instance-is-already-in-progress";
			}

			if (message.equals("Database partitioning must be enabled")) {
				return "database-partitioning-must-be-enabled";
			}

			return _ERROR_UNEXPECTED;
		}

		Throwable throwable = exception.getCause();

		if ((exception instanceof CompanyNameException) ||
			(throwable instanceof CompanyNameException)) {

			return "please-enter-a-valid-name";
		}

		if ((exception instanceof CompanyVirtualHostException) ||
			(throwable instanceof CompanyVirtualHostException)) {

			return "please-enter-a-valid-virtual-host";
		}

		if ((exception instanceof CompanyWebIdException) ||
			(throwable instanceof CompanyWebIdException)) {

			return "please-enter-a-valid-web-id";
		}

		return _ERROR_UNEXPECTED;
	}

	private static final String _ERROR_UNEXPECTED =
		"an-unexpected-error-occurred";

	private static final Log _log = LogFactoryUtil.getLog(
		CopyDBPartitionCompanyMVCActionCommand.class);

	@Reference
	private CompanyService _companyService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}