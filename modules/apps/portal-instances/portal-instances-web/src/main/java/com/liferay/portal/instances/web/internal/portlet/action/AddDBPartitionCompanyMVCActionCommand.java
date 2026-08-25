/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.portlet.action;

import com.liferay.portal.instances.web.internal.constants.PortalInstancesPortletKeys;
import com.liferay.portal.kernel.exception.CompanyNameException;
import com.liferay.portal.kernel.exception.CompanyVirtualHostException;
import com.liferay.portal.kernel.exception.CompanyWebIdException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
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
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

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
		"mvc.command.name=/portal_instances/add_db_partition_company"
	},
	service = MVCActionCommand.class
)
public class AddDBPartitionCompanyMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-11342")) {

			throw new UnsupportedOperationException();
		}

		try {
			Company company = _addDBPartitionCompany(actionRequest);

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
					actionRequest.getLocale(), "the-instance-was-imported-to-x",
					company.getWebId()));

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put("companyId", company.getCompanyId()));
		}
		catch (Exception exception) {
			String errorMessage = _getErrorMessage(exception);

			if (errorMessage.equals("an-unexpected-error-occurred")) {
				_log.error("Unable to import portal instance", exception);
			}
			else if (_log.isDebugEnabled()) {
				_log.debug("Unable to import portal instance", exception);
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"error",
					_language.get(actionRequest.getLocale(), errorMessage)));

			hideDefaultSuccessMessage(actionRequest);
		}
	}

	private Company _addDBPartitionCompany(ActionRequest actionRequest)
		throws Exception {

		String name = ParamUtil.getString(actionRequest, "name");
		String schemaName = ParamUtil.getString(actionRequest, "schemaName");
		String virtualHostname = ParamUtil.getString(
			actionRequest, "virtualHostname");
		String webId = ParamUtil.getString(actionRequest, "webId");

		return _companyService.addDBPartitionCompany(
			schemaName, name, virtualHostname, webId);
	}

	private String _getErrorMessage(Exception exception) {
		if (exception instanceof IllegalArgumentException) {
			String message = GetterUtil.getString(exception.getMessage());

			if (message.startsWith("Database partition ")) {
				return "an-instance-for-this-schema-already-exists";
			}

			if (message.startsWith(
					"Unable to insert the database partition ")) {

				return "the-exported-schema-does-not-exist";
			}

			if (message.startsWith("Invalid schema name ") ||
				message.endsWith(" is the default company ID")) {

				return "please-enter-a-valid-schema-name";
			}

			return "an-unexpected-error-occurred";
		}

		if (exception instanceof UnsupportedOperationException) {
			String message = GetterUtil.getString(exception.getMessage());

			if (message.equals("Database partitioning must be enabled")) {
				return "database-partitioning-must-be-enabled";
			}

			if (message.equals(
					"Company in import process company ID is not null")) {

				return "importing-an-instance-is-already-in-progress";
			}

			return "an-unexpected-error-occurred";
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

		return "an-unexpected-error-occurred";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddDBPartitionCompanyMVCActionCommand.class);

	@Reference
	private CompanyService _companyService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}