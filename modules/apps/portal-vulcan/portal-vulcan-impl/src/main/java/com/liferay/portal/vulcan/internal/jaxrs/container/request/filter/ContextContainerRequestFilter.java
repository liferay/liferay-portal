/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.request.filter;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResourceFactory;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResourceFactory;
import com.liferay.portal.vulcan.internal.accept.language.AcceptLanguageImpl;
import com.liferay.portal.vulcan.internal.configuration.util.ConfigurationUtil;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.ContextProviderUtil;
import com.liferay.portal.vulcan.jaxrs.context.ContextDataInjector;
import com.liferay.portal.vulcan.jaxrs.context.ContextDataInjectorBuilderFactory;
import com.liferay.portal.vulcan.util.UriInfoUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

import java.io.IOException;

import java.lang.reflect.Method;

import java.util.Set;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxrs.impl.UriInfoImpl;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;

import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Javier Gamarra
 */
@Provider
public class ContextContainerRequestFilter
	implements ContainerRequestFilter, ContainerResponseFilter {

	public ContextContainerRequestFilter(
		ConfigurationAdmin configurationAdmin,
		ContextDataInjectorBuilderFactory contextDataInjectorBuilderFactory,
		ExpressionConvert<Filter> expressionConvert,
		FilterParserProvider filterParserProvider,
		GroupLocalService groupLocalService, Language language, Portal portal,
		ResourceActionLocalService resourceActionLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService, Object scopeChecker,
		SortParserProvider sortParserProvider,
		VulcanBatchEngineExportTaskResourceFactory
			vulcanBatchEngineExportTaskResourceFactory,
		VulcanBatchEngineImportTaskResourceFactory
			vulcanBatchEngineImportTaskResourceFactory) {

		_configurationAdmin = configurationAdmin;
		_contextDataInjectorBuilderFactory = contextDataInjectorBuilderFactory;
		_expressionConvert = expressionConvert;
		_filterParserProvider = filterParserProvider;
		_groupLocalService = groupLocalService;
		_language = language;
		_portal = portal;
		_resourceActionLocalService = resourceActionLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;
		_scopeChecker = scopeChecker;
		_sortParserProvider = sortParserProvider;
		_vulcanBatchEngineExportTaskResourceFactory =
			vulcanBatchEngineExportTaskResourceFactory;
		_vulcanBatchEngineImportTaskResourceFactory =
			vulcanBatchEngineImportTaskResourceFactory;
	}

	@Override
	public void filter(ContainerRequestContext containerRequestContext) {
		handleMessage(
			containerRequestContext, PhaseInterceptorChain.getCurrentMessage());
	}

	@Override
	public void filter(
			ContainerRequestContext containerRequestContext,
			ContainerResponseContext containerResponseContext)
		throws IOException {

		Message message = JAXRSUtils.getContextMessage(
			JAXRSUtils.getCurrentMessage());

		SseEventSink sseEventSink = message.get(SseEventSink.class);

		if (sseEventSink != null) {
			SseBroadcaster sseBroadcaster = _sse.newBroadcaster();

			sseBroadcaster.register(sseEventSink);

			sseBroadcaster.onClose(
				__ -> {
					ContextProviderUtil.releaseResourceInstance(message);

					sseBroadcaster.close();
				});

			return;
		}

		ContextProviderUtil.releaseResourceInstance(message);
	}

	public void handleMessage(
			ContainerRequestContext containerRequestContext, Message message)
		throws Fault {

		try {
			_handleMessage(containerRequestContext, message);
		}
		catch (Exception exception) {
			throw new Fault(exception);
		}
	}

	private void _filterExcludedOperationIds(
			ContainerRequestContext containerRequestContext,
			HttpServletRequest httpServletRequest, Message message)
		throws Exception {

		Set<String> excludedOperationIds =
			ConfigurationUtil.getExcludedOperationIds(
				_portal.getCompanyId(httpServletRequest), _configurationAdmin,
				_getPath(message));

		Method method = (Method)message.get("org.apache.cxf.resource.method");

		if (excludedOperationIds.contains(method.getName())) {
			containerRequestContext.abortWith(
				Response.status(
					Response.Status.CONFLICT
				).entity(
					"Conflict with " + method.getName()
				).build());
		}
	}

	private String _getPath(Message message) {
		String path = StringUtil.removeFirst(
			(String)message.get(Message.BASE_PATH), "/o");

		return StringUtil.replaceLast(path, '/', "");
	}

	private void _handleMessage(
			ContainerRequestContext containerRequestContext, Message message)
		throws Exception {

		Object instance = ContextProviderUtil.getMatchedResource(message);

		if (instance == null) {
			return;
		}

		HttpServletRequest httpServletRequest =
			ContextProviderUtil.getHttpServletRequest(message);

		if (!ConfigurationUtil.isRESTEnabled(
				_portal.getCompany(httpServletRequest), _configurationAdmin,
				_getPath(message))) {

			containerRequestContext.abortWith(
				Response.status(
					Response.Status.NOT_FOUND
				).build());

			return;
		}

		_filterExcludedOperationIds(
			containerRequestContext, httpServletRequest, message);

		ContextDataInjector contextDataInjector =
			_contextDataInjectorBuilderFactory.builder(
			).acceptLanguage(
				new AcceptLanguageImpl(httpServletRequest, _language, _portal)
			).company(
				_portal.getCompany(httpServletRequest)
			).expressionConvert(
				_expressionConvert
			).filterParserProvider(
				_filterParserProvider
			).groupLocalService(
				_groupLocalService
			).httpServletRequest(
				httpServletRequest
			).httpServletResponse(
				(HttpServletResponse)message.getContextualProperty(
					"HTTP.RESPONSE")
			).resourceActionLocalService(
				_resourceActionLocalService
			).resourcePermissionLocalService(
				_resourcePermissionLocalService
			).roleLocalService(
				_roleLocalService
			).scopeChecker(
				_scopeChecker
			).sortParserProvider(
				_sortParserProvider
			).uriInfo(
				UriInfoUtil.getVulcanUriInfo(
					httpServletRequest, new UriInfoImpl(message))
			).user(
				_portal.getUser(httpServletRequest)
			).vulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResourceFactory.create()
			).vulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResourceFactory.create()
			).build();

		contextDataInjector.inject(instance);
	}

	private final ConfigurationAdmin _configurationAdmin;
	private final ContextDataInjectorBuilderFactory
		_contextDataInjectorBuilderFactory;
	private final ExpressionConvert<Filter> _expressionConvert;
	private final FilterParserProvider _filterParserProvider;
	private final GroupLocalService _groupLocalService;
	private final Language _language;
	private final Portal _portal;
	private final ResourceActionLocalService _resourceActionLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final RoleLocalService _roleLocalService;
	private final Object _scopeChecker;
	private final SortParserProvider _sortParserProvider;

	@Context
	private Sse _sse;

	private final VulcanBatchEngineExportTaskResourceFactory
		_vulcanBatchEngineExportTaskResourceFactory;
	private final VulcanBatchEngineImportTaskResourceFactory
		_vulcanBatchEngineImportTaskResourceFactory;

}