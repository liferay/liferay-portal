/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.graphql.data.processor;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResourceFactory;
import com.liferay.portal.vulcan.graphql.contributor.GraphQLContributor;
import com.liferay.portal.vulcan.internal.accept.language.AcceptLanguageImpl;
import com.liferay.portal.vulcan.internal.graphql.util.GraphQLUtil;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.AggregationContextProvider;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.ContextProviderUtil;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.FilterContextProvider;
import com.liferay.portal.vulcan.internal.jaxrs.validation.ValidationUtil;
import com.liferay.portal.vulcan.internal.multipart.MultipartUtil;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.pagination.provider.PaginationProvider;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.portal.vulcan.util.SortUtil;

import graphql.annotations.processor.util.NamingKit;
import graphql.annotations.processor.util.ReflectionKit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.jaxrs.impl.UriInfoImpl;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Carlos Correa
 */
public class LiferayMethodDataFetchingProcessor {

	public LiferayMethodDataFetchingProcessor(
		BundleContext bundleContext, CompanyLocalService companyLocalService,
		DepotEntryLocalService depotEntryLocalService,
		ExpressionConvert<Filter> expressionConvert,
		FilterParserProvider filterParserProvider,
		ServiceTrackerList<GraphQLContributor>
			graphQLContributorServiceTrackerList,
		GroupLocalService groupLocalService, Language language,
		PaginationProvider paginationProvider, Portal portal,
		ResourceActionLocalService resourceActionLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService,
		SortParserProvider sortParserProvider,
		VulcanBatchEngineImportTaskResourceFactory
			vulcanBatchEngineImportTaskResourceFactory) {

		_bundleContext = bundleContext;
		_companyLocalService = companyLocalService;
		_depotEntryLocalService = depotEntryLocalService;
		_expressionConvert = expressionConvert;
		_filterParserProvider = filterParserProvider;
		_graphQLContributorServiceTrackerList =
			graphQLContributorServiceTrackerList;
		_groupLocalService = groupLocalService;
		_language = language;
		_paginationProvider = paginationProvider;
		_portal = portal;
		_resourceActionLocalService = resourceActionLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;
		_sortParserProvider = sortParserProvider;
		_vulcanBatchEngineImportTaskResourceFactory =
			vulcanBatchEngineImportTaskResourceFactory;
	}

	public Object process(
			Map<String, Object> arguments, String fieldName,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Method method, Object root,
			Object source)
		throws Exception {

		Pagination pagination = _paginationProvider.getPagination(
			_portal.getCompanyId(httpServletRequest),
			_getIntegerValue(arguments, "page"),
			_getIntegerValue(arguments, "pageSize"));

		Parameter[] parameters = method.getParameters();

		Object[] argumentArray = new Object[parameters.length];

		MultivaluedMap<String, String> instanceArguments =
			new MultivaluedHashMap<>();

		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];

			String parameterName = null;

			String graphQLName = GraphQLUtil.getGraphQLNameValue(parameter);

			if (graphQLName == null) {
				parameterName = NamingKit.toGraphqlName(parameter.getName());
			}
			else {
				parameterName = NamingKit.toGraphqlName(graphQLName);
			}

			Object argument = arguments.get(parameterName);

			if ((argument == null) &&
				parameter.isAnnotationPresent(NotNull.class)) {

				throw new ValidationException(parameterName + " is null");
			}

			if (parameterName.equals("assetLibraryId") && (argument != null)) {
				try {
					argument = String.valueOf(
						GroupUtil.getDepotGroupId(
							(String)argument, CompanyThreadLocal.getCompanyId(),
							_depotEntryLocalService, _groupLocalService));

					instanceArguments.putSingle(
						"assetLibraryId", (String)argument);
				}
				catch (Exception exception) {
					throw new Exception(
						"Unable to convert asset library \"" + argument +
							"\" to group ID",
						exception);
				}
			}

			if (parameterName.equals("page")) {
				argument = pagination.getPage();
			}

			if (parameterName.equals("pageSize")) {
				argument = pagination.getPageSize();
			}

			if (parameterName.equals("siteKey") && (argument != null)) {
				try {
					argument = String.valueOf(
						GroupUtil.getGroupId(
							CompanyThreadLocal.getCompanyId(), (String)argument,
							_groupLocalService));

					instanceArguments.putSingle("siteId", (String)argument);
				}
				catch (Exception exception) {
					throw new Exception(
						"Unable to convert site key \"" + argument +
							"\" to group ID",
						exception);
				}
			}

			if (MultipartUtil.isMultipartBody(parameter)) {
				List<Part> parts = (List<Part>)argument;

				if ((parts != null) && !parts.isEmpty()) {
					Map<String, BinaryFile> binaryFiles = HashMapBuilder.put(
						"file",
						() -> {
							Part part = parts.get(0);

							return new BinaryFile(
								part.getContentType(),
								MultipartUtil.getFileName(part),
								part.getInputStream(), part.getSize());
						}
					).build();

					Map<String, String> values = new HashMap<>();

					if (parts.size() > 1) {
						Part metadataPart = parts.get(1);

						String metadata = StringUtil.read(
							metadataPart.getInputStream());

						int index = metadata.indexOf("=");

						if (index != -1) {
							values.put(
								metadata.substring(0, index),
								metadata.substring(index + 1));
						}
					}

					argument = MultipartBody.of(
						binaryFiles, __ -> ObjectMapperHolder._objectMapper,
						values);
				}
			}

			Class<? extends Parameter> parameterClass = parameter.getClass();

			if ((argument instanceof Map) &&
				!parameterClass.isAssignableFrom(Map.class)) {

				ObjectMapper objectMapper = ObjectMapperHolder._objectMapper;

				argument = objectMapper.convertValue(
					argument, parameter.getType());

				ValidationUtil.validate(argument);
			}

			argumentArray[i] = argument;
		}

		// Instance

		Object instance = null;

		Class<?> declaringClass = method.getDeclaringClass();

		Class<?> contributorClass = _getContributorClass(declaringClass);

		if (contributorClass != null) {
			instance = _getContributorInstance(
				arguments, contributorClass, declaringClass, httpServletRequest,
				httpServletResponse, instanceArguments, source);
		}
		else {
			Field field = _getThisField(declaringClass);

			if ((root == source) || Objects.equals(fieldName, "graphQLNode") ||
				(field == null)) {

				instance = _fillQueryInstance(
					arguments, httpServletRequest, httpServletResponse,
					declaringClass.newInstance(), instanceArguments);
			}
			else {
				Constructor<?>[] constructors =
					declaringClass.getConstructors();

				Class<?> typeClass = field.getType();

				Object queryInstance = _fillQueryInstance(
					arguments, httpServletRequest, httpServletResponse,
					typeClass.newInstance(), instanceArguments);

				instance = ReflectionKit.constructNewInstance(
					constructors[0], queryInstance, source);
			}
		}

		ValidationUtil.validateArguments(instance, method, argumentArray);

		return method.invoke(instance, argumentArray);
	}

	private Message _createMessage(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		Message message = new MessageImpl();

		String requestURL = String.valueOf(httpServletRequest.getRequestURL());

		message.put(Message.ENDPOINT_ADDRESS, requestURL);

		String contextPath = GetterUtil.getString(
			httpServletRequest.getContextPath());
		String servletPath = GetterUtil.getString(
			httpServletRequest.getServletPath());

		message.put(
			Message.PATH_INFO,
			contextPath + servletPath + httpServletRequest.getPathInfo());

		message.put(Message.QUERY_STRING, httpServletRequest.getQueryString());
		message.put("Accept", httpServletRequest.getHeader("Accept"));
		message.put("Content-Type", httpServletRequest.getContentType());
		message.put("HTTP.REQUEST", httpServletRequest);
		message.put("HTTP.RESPONSE", httpServletResponse);
		message.put("org.apache.cxf.async.post.response.dispatch", true);
		message.put(
			"org.apache.cxf.request.method", httpServletRequest.getMethod());
		message.put(
			"org.apache.cxf.request.uri", httpServletRequest.getRequestURI());
		message.put("org.apache.cxf.request.url", requestURL);
		message.put(
			"http.base.path",
			_getBasePath(
				contextPath, httpServletRequest.getRequestURI(), requestURL,
				servletPath));

		message.setExchange(new ExchangeImpl());

		return message;
	}

	private Object _fillQueryInstance(
			Map<String, Object> arguments,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Object instance,
			MultivaluedMap<String, String> instanceArguments)
		throws Exception {

		Class<?> clazz = instance.getClass();

		AcceptLanguage acceptLanguage = new AcceptLanguageImpl(
			httpServletRequest, _language, _portal);

		for (Field field : clazz.getDeclaredFields()) {
			if (Modifier.isFinal(field.getModifiers()) ||
				Modifier.isStatic(field.getModifiers())) {

				continue;
			}

			Class<?> fieldClass = field.getType();

			if (fieldClass.equals(Object.class) &&
				Objects.equals(field.getName(), "contextScopeChecker")) {

				field.setAccessible(true);

				field.set(instance, _getScopeChecker());

				continue;
			}

			if (fieldClass.isAssignableFrom(AcceptLanguage.class)) {
				field.setAccessible(true);

				field.set(instance, acceptLanguage);
			}
			else if (fieldClass.isAssignableFrom(Company.class)) {
				field.setAccessible(true);

				field.set(
					instance,
					_companyLocalService.getCompany(
						CompanyThreadLocal.getCompanyId()));
			}
			else if (fieldClass.isAssignableFrom(GroupLocalService.class)) {
				field.setAccessible(true);

				field.set(instance, _groupLocalService);
			}
			else if (fieldClass.isAssignableFrom(HttpServletRequest.class)) {
				field.setAccessible(true);

				field.set(instance, httpServletRequest);
			}
			else if (fieldClass.isAssignableFrom(HttpServletResponse.class)) {
				field.setAccessible(true);

				field.set(instance, httpServletResponse);
			}
			else if (fieldClass.isAssignableFrom(
						ResourceActionLocalService.class)) {

				field.setAccessible(true);

				field.set(instance, _resourceActionLocalService);
			}
			else if (fieldClass.isAssignableFrom(
						ResourcePermissionLocalService.class)) {

				field.setAccessible(true);

				field.set(instance, _resourcePermissionLocalService);
			}
			else if (fieldClass.isAssignableFrom(RoleLocalService.class)) {
				field.setAccessible(true);

				field.set(instance, _roleLocalService);
			}
			else if (fieldClass.isAssignableFrom(UriInfo.class)) {
				field.setAccessible(true);

				field.set(
					instance,
					new UriInfoImpl(
						_createMessage(httpServletRequest, httpServletResponse),
						instanceArguments));
			}
			else if (fieldClass.isAssignableFrom(User.class)) {
				field.setAccessible(true);

				field.set(instance, _portal.getUser(httpServletRequest));
			}
			else if (fieldClass.isAssignableFrom(
						VulcanBatchEngineImportTaskResource.class)) {

				field.setAccessible(true);

				field.set(
					instance,
					_vulcanBatchEngineImportTaskResourceFactory.create());
			}
			else {
				Map<String, String[]> parameterMap = new HashMap<>(
					httpServletRequest.getParameterMap());

				for (Map.Entry<String, Object> entry : arguments.entrySet()) {
					parameterMap.put(
						entry.getKey(),
						new String[] {String.valueOf(entry.getValue())});
				}

				if (Objects.equals(field.getName(), "_aggregationBiFunction")) {
					field.setAccessible(true);

					BiFunction<Object, List<String>, Aggregation>
						aggregationBiFunction =
							(resource, aggregationStrings) -> {
								try {
									return _getAggregation(
										acceptLanguage, aggregationStrings,
										_getEntityModel(
											resource, parameterMap));
								}
								catch (Exception exception) {
									throw new BadRequestException(exception);
								}
							};

					field.set(instance, aggregationBiFunction);
				}
				else if (Objects.equals(field.getName(), "_filterBiFunction")) {
					field.setAccessible(true);

					BiFunction<Object, String, Filter> filterBiFunction =
						(resource, filterString) -> {
							try {
								return _getFilter(
									acceptLanguage,
									_getEntityModel(resource, parameterMap),
									filterString);
							}
							catch (Exception exception) {
								throw new BadRequestException(exception);
							}
						};

					field.set(instance, filterBiFunction);
				}
				else if (Objects.equals(field.getName(), "_sortsBiFunction")) {
					field.setAccessible(true);

					BiFunction<Object, String, Sort[]> sortsBiFunction =
						(resource, sortsString) -> {
							try {
								EntityModel entityModel = _getEntityModel(
									resource, parameterMap);

								return SortUtil.getSorts(
									acceptLanguage, entityModel,
									_sortParserProvider.provide(entityModel),
									sortsString);
							}
							catch (Exception exception) {
								throw new BadRequestException(exception);
							}
						};

					field.set(instance, sortsBiFunction);
				}
			}
		}

		return instance;
	}

	private Aggregation _getAggregation(
		AcceptLanguage acceptLanguage, List<String> aggregationStrings,
		EntityModel entityModel) {

		if (aggregationStrings == null) {
			return null;
		}

		AggregationContextProvider aggregationContextProvider =
			new AggregationContextProvider(_language, _portal);

		return aggregationContextProvider.createContext(
			acceptLanguage, aggregationStrings.toArray(new String[0]),
			entityModel);
	}

	private String _getBasePath(
		String contextPath, String requestURI, String requestURL,
		String servletPath) {

		if (!StringUtils.isEmpty(requestURI)) {
			int index = requestURL.indexOf(requestURI);

			if (index > 0) {
				return requestURL.substring(0, index) + contextPath;
			}
		}
		else if (!StringUtils.isEmpty(servletPath) &&
				 requestURL.endsWith(servletPath)) {

			int index = requestURL.lastIndexOf(servletPath);

			if (index > 0) {
				return requestURL.substring(0, index);
			}
		}

		return null;
	}

	private Class<?> _getContributorClass(Class<?> clazz) {
		Class<?> enclosingClass = clazz.getEnclosingClass();

		if (enclosingClass == null) {
			if (GraphQLContributor.class.isAssignableFrom(clazz)) {
				return clazz;
			}

			return null;
		}

		return _getContributorClass(enclosingClass);
	}

	private Object _getContributorInstance(
			Map<String, Object> arguments, Class<?> contributorClass,
			Class<?> declaringClass, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			MultivaluedMap<String, String> instanceArguments, Object source)
		throws Exception {

		Class<?> queryClass = declaringClass.getEnclosingClass();

		Constructor<?> constructor = queryClass.getConstructors()[0];

		Object[] args = null;

		if (constructor.getParameterCount() == 0) {
			args = new Object[0];
		}
		else {
			args = new Object[] {
				_fillQueryInstance(
					arguments, httpServletRequest, httpServletResponse,
					_getService(contributorClass), instanceArguments)
			};
		}

		Object query = ReflectionKit.constructNewInstance(constructor, args);

		constructor = declaringClass.getConstructors()[0];

		if (constructor.getParameterCount() == 1) {
			args = new Object[] {source};
		}
		else {
			args = new Object[] {query, source};
		}

		return ReflectionKit.constructNewInstance(constructor, args);
	}

	private EntityModel _getEntityModel(
			Object resource, Map<String, String[]> parameterMap)
		throws Exception {

		if (!(resource instanceof EntityModelResource)) {
			return null;
		}

		EntityModelResource entityModelResource = (EntityModelResource)resource;

		return entityModelResource.getEntityModel(
			ContextProviderUtil.getMultivaluedHashMap(parameterMap));
	}

	private Filter _getFilter(
			AcceptLanguage acceptLanguage, EntityModel entityModel,
			String filterString)
		throws Exception {

		FilterContextProvider filterContextProvider = new FilterContextProvider(
			_expressionConvert, _filterParserProvider, _language, _portal);

		return filterContextProvider.createContext(
			acceptLanguage, entityModel, filterString);
	}

	private Integer _getIntegerValue(
		Map<String, Object> arguments, String key) {

		Object value = arguments.get(key);

		if (Validator.isNotNull(value)) {
			return GetterUtil.getInteger(value);
		}

		return null;
	}

	private Object _getScopeChecker() {
		ServiceReference<?> serviceReference =
			_bundleContext.getServiceReference(
				"com.liferay.oauth2.provider.scope.ScopeChecker");

		if (serviceReference != null) {
			return _bundleContext.getService(serviceReference);
		}

		return null;
	}

	private Object _getService(Class<?> clazz) {
		for (Object service : _graphQLContributorServiceTrackerList) {
			if (clazz.isAssignableFrom(service.getClass())) {
				return service;
			}
		}

		return null;
	}

	private Field _getThisField(Class<?> clazz) {
		try {
			return clazz.getDeclaredField("this$0");
		}
		catch (NoSuchFieldException noSuchFieldException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFieldException);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayMethodDataFetchingProcessor.class);

	private final BundleContext _bundleContext;
	private final CompanyLocalService _companyLocalService;
	private final DepotEntryLocalService _depotEntryLocalService;
	private final ExpressionConvert<Filter> _expressionConvert;
	private final FilterParserProvider _filterParserProvider;
	private final ServiceTrackerList<GraphQLContributor>
		_graphQLContributorServiceTrackerList;
	private final GroupLocalService _groupLocalService;
	private final Language _language;
	private final PaginationProvider _paginationProvider;
	private final Portal _portal;
	private final ResourceActionLocalService _resourceActionLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final RoleLocalService _roleLocalService;
	private final SortParserProvider _sortParserProvider;
	private final VulcanBatchEngineImportTaskResourceFactory
		_vulcanBatchEngineImportTaskResourceFactory;

	private static class ObjectMapperHolder {

		private static final ObjectMapper _objectMapper = new ObjectMapper();

	}

}