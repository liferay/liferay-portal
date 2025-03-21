/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.api;

import com.liferay.oauth2.provider.scope.RequiresNoScope;
import com.liferay.osb.faro.engine.client.constants.OSBAsahHeaderConstants;
import com.liferay.osb.faro.engine.client.util.EngineServiceURLUtil;
import com.liferay.osb.faro.engine.client.util.TokenUtil;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.util.FaroPermissionChecker;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.osb.faro.web.internal.context.GroupInfo;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MapUtil;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.net.URISyntaxException;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
@Component(service = GraphQLController.class)
@Path("/graphql")
@Produces(MediaType.APPLICATION_JSON)
@RequiresNoScope
public class GraphQLController extends BaseFaroController {

	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	public String post(@Context GroupInfo groupInfo, String requestBody)
		throws Exception {

		if (!FaroPropsValues.GRAPHQL_API_ENABLED) {
			throw new WebApplicationException(
				Response.status(
					Response.Status.NOT_FOUND
				).build());
		}

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(
				groupInfo.getGroupId());

		if (!_hasPermission(faroProject, requestBody)) {
			throw new WebApplicationException(
				Response.status(
					Response.Status.FORBIDDEN
				).build());
		}

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		try (CloseableHttpClient closeableHttpClient =
				httpClientBuilder.build()) {

			URIBuilder uriBuilder = new URIBuilder(
				EngineServiceURLUtil.getBackendURL(faroProject, "/graphql"));

			URI uri = uriBuilder.build();

			HttpPost httpPost = new HttpPost(uri);

			httpPost.setEntity(
				new ByteArrayEntity(
					requestBody.getBytes(StandardCharsets.UTF_8)));
			httpPost.setHeader(
				OSBAsahHeaderConstants.FARO_BACKEND_SECURITY_SIGNATURE,
				getSecuritySignature(uri));
			httpPost.setHeader(
				OSBAsahHeaderConstants.PROJECT_ID, faroProject.getProjectId());
			httpPost.setHeader("content-type", "application/json");

			CloseableHttpResponse closeableHttpResponse =
				closeableHttpClient.execute(httpPost);

			HttpEntity httpEntity = closeableHttpResponse.getEntity();

			return IOUtils.toString(httpEntity.getContent(), "UTF-8");
		}
		catch (URISyntaxException uriSyntaxException) {
			if (_log.isDebugEnabled()) {
				_log.debug(uriSyntaxException);
			}

			throw new WebApplicationException(
				Response.status(
					Response.Status.BAD_REQUEST
				).build());
		}
	}

	protected String getSecuritySignature(URI uri) {
		String url = uri.toString();

		return DigestUtils.sha256Hex(
			TokenUtil.getOSBAsahSecurityToken() +
				url.substring(0, url.lastIndexOf(uri.getPath())));
	}

	private boolean _hasChannelPermission(
		FaroProject faroProject, String channelId) {

		if ((channelId != null) && !channelId.isEmpty()) {
			List<FaroChannel> faroChannels = _faroChannelLocalService.search(
				faroProject.getGroupId(), null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

			for (FaroChannel faroChannel : faroChannels) {
				if (Objects.equals(faroChannel.getChannelId(), channelId)) {
					return true;
				}
			}

			return false;
		}

		return true;
	}

	private boolean _hasPermission(FaroProject faroProject, String body) {
		try {
			Map<String, Object> map = JSONUtil.readValue(body, Map.class);

			Map<String, Object> variablesMap = (Map<String, Object>)map.get(
				"variables");

			if (variablesMap != null) {
				String channelId = MapUtil.getString(variablesMap, "channelId");

				if (!_hasChannelPermission(faroProject, channelId)) {
					return false;
				}
			}

			String query = MapUtil.getString(map, "query");

			Matcher matcher = _pattern.matcher(query);

			if (matcher.find()) {
				String channelId = matcher.group(_CHANNEL_ID_INDEX);

				if (!_hasChannelPermission(faroProject, channelId)) {
					return false;
				}
			}

			if (FaroPermissionChecker.isGroupMember(faroProject.getGroupId()) ||
				!query.contains("mutation")) {

				return true;
			}

			for (String restrictedGraphQLMethodName :
					_restrictedGraphQLMethodNames) {

				if (query.contains(restrictedGraphQLMethodName)) {
					return false;
				}
			}

			return true;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Invalid request: " + body, exception);
			}

			return false;
		}
	}

	private static final int _CHANNEL_ID_INDEX = 5;

	private static final Log _log = LogFactoryUtil.getLog(
		GraphQLController.class);

	private static final Pattern _pattern = Pattern.compile(
		"(channelId):(( )|\\r|\\n|\\t)*(\\\"(\\d+)\\\")");
	private static final List<String> _restrictedGraphQLMethodNames =
		Arrays.asList(
			"createJob", "deleteJobs", "preference", "runJob", "updateJob");

	@Reference
	private FaroChannelLocalService _faroChannelLocalService;

}