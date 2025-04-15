/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.web.internal.portlet.action;

import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryService;
import com.liferay.client.extension.web.internal.constants.ClientExtensionAdminPortletKeys;
import com.liferay.client.extension.web.internal.constants.ClientExtensionAdminWebConstants;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import java.text.DateFormat;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ClientExtensionAdminPortletKeys.CLIENT_EXTENSION_ADMIN,
		"mvc.command.name=/client_extension_admin/export_client_extension_entry"
	},
	service = MVCResourceCommand.class
)
public class ExportClientExtensionEntryMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		try {
			Company company = _portal.getCompany(resourceRequest);

			String externalReferenceCode = resourceRequest.getParameter(
				"externalReferenceCode");

			ClientExtensionEntry clientExtensionEntry =
				_clientExtensionEntryService.
					fetchClientExtensionEntryByExternalReferenceCode(
						externalReferenceCode, company.getCompanyId());

			if (clientExtensionEntry == null) {
				resourceResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

				return;
			}

			String json = JSONUtil.put(
				"clientExtensionEntries",
				_getClientExtensionEntriesJSONObject(clientExtensionEntry)
			).put(
				"company",
				JSONUtil.put(
					"id", company.getCompanyId()
				).put(
					"name", company.getName()
				).put(
					"virtualHostName", company.getVirtualHostname()
				).put(
					"webId", company.getWebId()
				)
			).put(
				"exportDate",
				() -> {
					DateFormat dateFormat = DateUtil.getISO8601Format();

					return dateFormat.format(new Date());
				}
			).put(
				"user",
				() -> {
					User user = _portal.getUser(resourceRequest);

					return JSONUtil.put(
						"fullName", user.getFullName()
					).put(
						"id", user.getUserId()
					).put(
						"screenName", user.getScreenName()
					);
				}
			).put(
				"version", ClientExtensionAdminWebConstants.EXPORT_VERSION
			).toString();

			resourceResponse.setContentLength(json.length());

			resourceResponse.setContentType(ContentTypes.APPLICATION_JSON);

			LiferayPortletResponse liferayPortletResponse =
				_portal.getLiferayPortletResponse(resourceResponse);

			liferayPortletResponse.setHeader(
				"Content-Disposition",
				"attachment; filename=\"client-extension-entry." +
					clientExtensionEntry.getExternalReferenceCode() +
						".json\"");

			PrintWriter printWriter = resourceResponse.getWriter();

			printWriter.write(json);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			resourceResponse.setStatus(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private JSONObject _getClientExtensionEntriesJSONObject(
		ClientExtensionEntry... clientExtensionEntries) {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		for (ClientExtensionEntry clientExtensionEntry :
				clientExtensionEntries) {

			jsonObject.put(
				clientExtensionEntry.getExternalReferenceCode(),
				JSONUtil.put(
					"description", clientExtensionEntry.getDescription()
				).put(
					"name", clientExtensionEntry.getName()
				).put(
					"properties", clientExtensionEntry.getProperties()
				).put(
					"sourceCodeURL", clientExtensionEntry.getSourceCodeURL()
				).put(
					"type", clientExtensionEntry.getType()
				).put(
					"typeSettings", clientExtensionEntry.getTypeSettings()
				));
		}

		return jsonObject;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportClientExtensionEntryMVCResourceCommand.class);

	@Reference
	private ClientExtensionEntryService _clientExtensionEntryService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}