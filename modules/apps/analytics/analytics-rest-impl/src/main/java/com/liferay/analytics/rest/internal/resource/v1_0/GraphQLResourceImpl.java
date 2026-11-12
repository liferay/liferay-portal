/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.rest.internal.resource.v1_0;

import com.liferay.analytics.rest.resource.v1_0.GraphQLResource;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.security.key.secret.SecretResolverUtil;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Marcos Martins
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/graph-ql.properties",
	scope = ServiceScope.PROTOTYPE, service = GraphQLResource.class
)
public class GraphQLResourceImpl extends BaseGraphQLResourceImpl {

	@Override
	public Response postGraphQL(String body) throws Exception {
		try {
			return _executeGraphQL(
				_analyticsSettingsManager.getAnalyticsConfiguration(
					contextCompany.getCompanyId()),
				body);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to proxy GraphQL request to Analytics Cloud",
				exception);

			JSONObject errorJSONObject = _jsonFactory.createJSONObject();

			errorJSONObject.put(
				"error", "Unable to reach Analytics Cloud"
			).put(
				"message", exception.getMessage()
			);

			return Response.status(
				Response.Status.BAD_GATEWAY
			).entity(
				errorJSONObject.toString()
			).type(
				MediaType.APPLICATION_JSON
			).build();
		}
	}

	private Response _executeGraphQL(
			AnalyticsConfiguration analyticsConfiguration, String body)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addHeader("Content-Type", ContentTypes.APPLICATION_JSON);
		options.addHeader(
			"OSB-Asah-Data-Source-ID",
			analyticsConfiguration.liferayAnalyticsDataSourceId());
		options.addHeader(
			"OSB-Asah-Faro-Backend-Security-Signature",
			SecretResolverUtil.resolve(
				CompanyThreadLocal.getCompanyId(),
				analyticsConfiguration.
					liferayAnalyticsFaroBackendSecuritySignature()));
		options.addHeader(
			"OSB-Asah-Project-ID",
			analyticsConfiguration.liferayAnalyticsProjectId());
		options.setBody(body, ContentTypes.APPLICATION_JSON, StringPool.UTF8);
		options.setLocation(
			analyticsConfiguration.liferayAnalyticsFaroBackendURL() +
				"/api/1.0/graphql");
		options.setPost(true);

		String responseString = _http.URLtoString(options);

		Http.Response response = options.getResponse();

		return Response.status(
			response.getResponseCode()
		).entity(
			responseString
		).type(
			MediaType.APPLICATION_JSON
		).build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GraphQLResourceImpl.class);

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

}