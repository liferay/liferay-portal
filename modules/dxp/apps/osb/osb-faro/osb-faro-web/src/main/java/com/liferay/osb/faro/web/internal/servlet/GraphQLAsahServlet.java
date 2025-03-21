/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.servlet;

import com.liferay.osb.faro.engine.client.constants.OSBAsahHeaderConstants;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.util.FaroPermissionChecker;
import com.liferay.osb.faro.web.internal.util.FaroProjectThreadLocal;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/cerebro/graphql",
		"osgi.http.whiteboard.servlet.name=com.liferay.osb.faro.web.internal.servlet.GraphQLServlet",
		"osgi.http.whiteboard.servlet.pattern=/cerebro/graphql/*"
	},
	service = Servlet.class
)
public class GraphQLAsahServlet extends BaseAsahServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		try (CloseableHttpClient closeableHttpClient =
				httpClientBuilder.build()) {

			URI uri = buildURI(httpServletRequest, "/graphql");

			HttpGet httpGet = new HttpGet(uri);

			httpGet.setHeader(
				OSBAsahHeaderConstants.PROJECT_ID, getProjectId());
			httpGet.setHeader(
				OSBAsahHeaderConstants.FARO_BACKEND_SECURITY_SIGNATURE,
				getSecuritySignature(uri));

			CloseableHttpResponse closeableHttpResponse =
				closeableHttpClient.execute(httpGet);

			HttpEntity entity = closeableHttpResponse.getEntity();

			ServletResponseUtil.write(httpServletResponse, entity.getContent());
		}
		catch (URISyntaxException uriSyntaxException) {
			if (_log.isDebugEnabled()) {
				_log.debug(uriSyntaxException);
			}
		}
	}

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		String body = StringUtil.read(httpServletRequest.getInputStream());

		if (!_hasPermission(body)) {
			httpServletResponse.sendError(
				HttpServletResponse.SC_FORBIDDEN,
				"You do not have the required permissions");

			return;
		}

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		try (CloseableHttpClient closeableHttpClient =
				httpClientBuilder.build()) {

			URI uri = buildURI(httpServletRequest, "/graphql");

			HttpPost httpPost = new HttpPost(uri);

			HttpEntity postEntity = new ByteArrayEntity(
				body.getBytes(StandardCharsets.UTF_8));

			httpPost.setEntity(postEntity);

			httpPost.setHeader(
				OSBAsahHeaderConstants.FARO_BACKEND_SECURITY_SIGNATURE,
				getSecuritySignature(uri));
			httpPost.setHeader(
				OSBAsahHeaderConstants.PROJECT_ID, getProjectId());
			httpPost.setHeader("content-type", "application/json");

			CloseableHttpResponse closeableHttpResponse =
				closeableHttpClient.execute(httpPost);

			HttpEntity responseEntity = closeableHttpResponse.getEntity();

			ServletResponseUtil.write(
				httpServletResponse, responseEntity.getContent());
		}
		catch (URISyntaxException uriSyntaxException) {
			if (_log.isDebugEnabled()) {
				_log.debug(uriSyntaxException);
			}
		}
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

	private boolean _hasPermission(String body) {
		FaroProject faroProject = FaroProjectThreadLocal.getFaroProject();

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
		GraphQLAsahServlet.class);

	private static final Pattern _pattern = Pattern.compile(
		"(channelId):(( )|\\r|\\n|\\t)*(\\\"(\\d+)\\\")");
	private static final List<String> _restrictedGraphQLMethodNames =
		Arrays.asList(
			"createJob", "deleteJobs", "preference", "runJob", "updateJob");

	@Reference
	private FaroChannelLocalService _faroChannelLocalService;

}