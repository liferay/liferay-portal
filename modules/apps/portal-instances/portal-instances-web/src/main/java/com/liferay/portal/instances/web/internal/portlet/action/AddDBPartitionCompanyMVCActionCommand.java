/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.portlet.action;

import com.liferay.batch.engine.jaxrs.uri.BatchEngineUriInfo;
import com.liferay.headless.portal.instances.resource.v1_0.PortalInstanceImportResource;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.instances.constants.PortalInstancesPortletKeys;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResourceFactory;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

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

		hideDefaultSuccessMessage(actionRequest);

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			_validateSchemaName(
				ParamUtil.getString(actionRequest, "schemaName"));

			_importPortalInstance(actionRequest);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			jsonObject.put(
				"error",
				_language.get(
					actionRequest.getLocale(), _getErrorMessageKey(exception)));
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private AcceptLanguage _getAcceptLanguage(ActionRequest actionRequest) {
		Locale locale = _portal.getLocale(actionRequest);

		return new AcceptLanguage() {

			@Override
			public List<Locale> getLocales() {
				return Collections.singletonList(locale);
			}

			@Override
			public String getPreferredLanguageId() {
				return LocaleUtil.toLanguageId(locale);
			}

			@Override
			public Locale getPreferredLocale() {
				return locale;
			}

		};
	}

	private String _getErrorMessageKey(Exception exception) {
		if (exception instanceof IllegalArgumentException) {
			return "please-enter-a-valid-schema-name";
		}

		return "an-unexpected-error-occurred";
	}

	private HttpServletRequest _getHttpServletRequest(
		ActionRequest actionRequest) {

		return new HttpServletRequestWrapper(
			_portal.getHttpServletRequest(actionRequest)) {

			@Override
			public String getHeader(String name) {
				if (StringUtil.equalsIgnoreCase(
						name, HttpHeaders.CONTENT_TYPE)) {

					return ContentTypes.APPLICATION_JSON;
				}

				return super.getHeader(name);
			}

		};
	}

	private void _importPortalInstance(ActionRequest actionRequest)
		throws Exception {

		PortalInstanceImportResource portalInstanceImportResource =
			_componentServiceObjects.getService();

		try {
			portalInstanceImportResource.setContextAcceptLanguage(
				_getAcceptLanguage(actionRequest));
			portalInstanceImportResource.setContextCompany(
				_portal.getCompany(actionRequest));
			portalInstanceImportResource.setContextHttpServletRequest(
				_getHttpServletRequest(actionRequest));
			portalInstanceImportResource.setContextUriInfo(
				new BatchEngineUriInfo.Builder(
				).build());
			portalInstanceImportResource.setContextUser(
				_portal.getUser(actionRequest));
			portalInstanceImportResource.setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResourceFactory.create());

			portalInstanceImportResource.postPortalInstanceImportBatch(
				null,
				Collections.singletonList(
					HashMapBuilder.put(
						"name", ParamUtil.getString(actionRequest, "name")
					).put(
						"schemaName",
						ParamUtil.getString(actionRequest, "schemaName")
					).put(
						"virtualHost",
						ParamUtil.getString(actionRequest, "virtualHostname")
					).put(
						"webId", ParamUtil.getString(actionRequest, "webId")
					).build()));
		}
		finally {
			_componentServiceObjects.ungetService(portalInstanceImportResource);
		}
	}

	private void _validateSchemaName(String schemaName) {
		String databaseExportedPartitionSchemaNamePrefix =
			DBPartitionUtil.DATABASE_EXPORTED_PARTITION_SCHEMA_NAME_PREFIX;

		if (!StringUtil.startsWith(
				schemaName, databaseExportedPartitionSchemaNamePrefix)) {

			throw new IllegalArgumentException(
				"Invalid schema name \"" + schemaName + "\"");
		}

		long companyId = GetterUtil.getLong(
			schemaName.substring(
				databaseExportedPartitionSchemaNamePrefix.length()));

		if ((companyId <= 0) ||
			(companyId == PortalInstancePool.getDefaultCompanyId())) {

			throw new IllegalArgumentException(
				"Invalid schema name \"" + schemaName + "\"");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddDBPartitionCompanyMVCActionCommand.class);

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PortalInstanceImportResource>
		_componentServiceObjects;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private VulcanBatchEngineImportTaskResourceFactory
		_vulcanBatchEngineImportTaskResourceFactory;

}