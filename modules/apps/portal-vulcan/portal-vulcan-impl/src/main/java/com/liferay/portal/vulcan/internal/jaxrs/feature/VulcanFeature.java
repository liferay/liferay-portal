/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.feature;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jakarta.rs.xml.JacksonXMLProvider;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResourceFactory;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResourceFactory;
import com.liferay.portal.vulcan.extension.ExtensionProviderRegistry;
import com.liferay.portal.vulcan.internal.jaxrs.container.request.filter.CTContainerRequestFilter;
import com.liferay.portal.vulcan.internal.jaxrs.container.request.filter.ContextContainerRequestFilter;
import com.liferay.portal.vulcan.internal.jaxrs.container.request.filter.LogContainerRequestFilter;
import com.liferay.portal.vulcan.internal.jaxrs.container.request.filter.NestedFieldsContainerRequestFilter;
import com.liferay.portal.vulcan.internal.jaxrs.container.request.filter.TransactionContainerRequestFilter;
import com.liferay.portal.vulcan.internal.jaxrs.container.response.filter.CacheContainerResponseFilter;
import com.liferay.portal.vulcan.internal.jaxrs.container.response.filter.EntityExtensionContainerResponseFilter;
import com.liferay.portal.vulcan.internal.jaxrs.container.response.filter.EntityFieldsPreSerializerContainerResponseFilter;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.AcceptLanguageContextProvider;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.AggregationContextProvider;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.CompanyContextProvider;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.FieldsQueryParamContextProvider;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.PaginationContextProvider;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.RestrictFieldsQueryParamContextProvider;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.SortContextProvider;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.UserContextProvider;
import com.liferay.portal.vulcan.internal.jaxrs.context.resolver.EntityExtensionHandlerContextResolver;
import com.liferay.portal.vulcan.internal.jaxrs.context.resolver.ObjectMapperContextResolver;
import com.liferay.portal.vulcan.internal.jaxrs.context.resolver.XmlMapperContextResolver;
import com.liferay.portal.vulcan.internal.jaxrs.dynamic.feature.StatusDynamicFeature;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.DocumentFileExtensionExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.DocumentFileNameExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.DocumentFileSizeExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.DuplicateExternalReferenceCodeExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.ExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.IllegalArgumentExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.InvalidFilterExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.InvalidFormatExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.JsonMappingExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.JsonParseExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.NoSuchModelExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.NotAcceptableExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.NotFoundExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.PrincipalExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.SQLIntegrityConstraintViolationExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.UnrecognizedPropertyExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.UnsupportedOperationExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.ValidationExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.exception.mapper.WebApplicationExceptionMapper;
import com.liferay.portal.vulcan.internal.jaxrs.message.body.JSONMessageBodyReader;
import com.liferay.portal.vulcan.internal.jaxrs.message.body.JSONMessageBodyWriter;
import com.liferay.portal.vulcan.internal.jaxrs.message.body.MultipartBodyMessageBodyReader;
import com.liferay.portal.vulcan.internal.jaxrs.message.body.XMLMessageBodyReader;
import com.liferay.portal.vulcan.internal.jaxrs.message.body.XMLMessageBodyWriter;
import com.liferay.portal.vulcan.internal.jaxrs.param.converter.provider.SiteParamConverterProvider;
import com.liferay.portal.vulcan.internal.jaxrs.validation.BeanValidationInterceptor;
import com.liferay.portal.vulcan.internal.jaxrs.writer.interceptor.EntityExtensionWriterInterceptor;
import com.liferay.portal.vulcan.internal.jaxrs.writer.interceptor.NestedFieldsWriterInterceptor;
import com.liferay.portal.vulcan.internal.jaxrs.writer.interceptor.PageEntityExtensionWriterInterceptor;
import com.liferay.portal.vulcan.internal.jaxrs.writer.interceptor.ProblemWriterInterceptor;
import com.liferay.portal.vulcan.internal.param.converter.provider.DateParamConverterProvider;
import com.liferay.portal.vulcan.jaxrs.context.ContextDataInjectorBuilderFactory;
import com.liferay.portal.vulcan.pagination.provider.PaginationProvider;

import javax.ws.rs.Priorities;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * An {@code Application} requesting this feature will include all the different
 * extensions provided by this module.
 *
 * @author Alejandro Hernández
 * @review
 */
@Component(
	property = {
		JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT + "=(osgi.jaxrs.extension.select=\\(osgi.jaxrs.name=Liferay.Vulcan\\))",
		JaxrsWhiteboardConstants.JAX_RS_EXTENSION + "=true",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=Liferay.Vulcan"
	},
	scope = ServiceScope.PROTOTYPE, service = Feature.class
)
public class VulcanFeature implements Feature {

	@Override
	public boolean configure(FeatureContext featureContext) {
		featureContext.register(BeanValidationInterceptor.class);
		featureContext.register(CacheContainerResponseFilter.class);
		featureContext.register(CTContainerRequestFilter.class);
		featureContext.register(DateParamConverterProvider.class);
		featureContext.register(DocumentFileExtensionExceptionMapper.class);
		featureContext.register(DocumentFileNameExceptionMapper.class);
		featureContext.register(DocumentFileSizeExceptionMapper.class);
		featureContext.register(
			EntityExtensionContainerResponseFilter.class, Priorities.USER + 10);
		featureContext.register(EntityExtensionWriterInterceptor.class);
		featureContext.register(
			EntityFieldsPreSerializerContainerResponseFilter.class,
			Priorities.USER + 11);
		featureContext.register(ExceptionMapper.class);
		featureContext.register(FieldsQueryParamContextProvider.class);
		featureContext.register(IllegalArgumentExceptionMapper.class);
		featureContext.register(InvalidFilterExceptionMapper.class);
		featureContext.register(InvalidFormatExceptionMapper.class);
		featureContext.register(JSONMessageBodyReader.class);
		featureContext.register(JSONMessageBodyWriter.class);
		featureContext.register(JacksonJsonProvider.class);
		featureContext.register(JacksonXMLProvider.class);
		featureContext.register(JsonMappingExceptionMapper.class);
		featureContext.register(JsonParseExceptionMapper.class);
		featureContext.register(LogContainerRequestFilter.class);
		featureContext.register(NestedFieldsContainerRequestFilter.class);
		featureContext.register(NoSuchModelExceptionMapper.class);
		featureContext.register(NotAcceptableExceptionMapper.class);
		featureContext.register(NotFoundExceptionMapper.class);
		featureContext.register(ObjectMapperContextResolver.class);
		featureContext.register(PageEntityExtensionWriterInterceptor.class);
		featureContext.register(PrincipalExceptionMapper.class);
		featureContext.register(ProblemWriterInterceptor.class);
		featureContext.register(RestrictFieldsQueryParamContextProvider.class);
		featureContext.register(
			SQLIntegrityConstraintViolationExceptionMapper.class);
		featureContext.register(StatusDynamicFeature.class);
		featureContext.register(
			TransactionContainerRequestFilter.class, Priorities.USER - 10);
		featureContext.register(UnrecognizedPropertyExceptionMapper.class);
		featureContext.register(UnsupportedOperationExceptionMapper.class);
		featureContext.register(ValidationExceptionMapper.class);
		featureContext.register(WebApplicationExceptionMapper.class);
		featureContext.register(XMLMessageBodyReader.class);
		featureContext.register(XMLMessageBodyWriter.class);
		featureContext.register(XmlMapperContextResolver.class);

		featureContext.register(
			new AcceptLanguageContextProvider(_language, _portal));
		featureContext.register(
			new AggregationContextProvider(_language, _portal));
		featureContext.register(new CompanyContextProvider(_portal));
		featureContext.register(
			new ContextContainerRequestFilter(
				_configurationAdmin, _contextDataInjectorBuilderFactory,
				_expressionConvert, _filterParserProvider, _groupLocalService,
				_language, _portal, _resourceActionLocalService,
				_resourcePermissionLocalService, _roleLocalService,
				_getScopeChecker(), _sortParserProvider,
				_vulcanBatchEngineExportTaskResourceFactory,
				_vulcanBatchEngineImportTaskResourceFactory));
		featureContext.register(
			new DuplicateExternalReferenceCodeExceptionMapper(_language));
		featureContext.register(
			new EntityExtensionHandlerContextResolver(
				_extensionProviderRegistry));
		featureContext.register(new MultipartBodyMessageBodyReader());

		_nestedFieldsWriterInterceptor = new NestedFieldsWriterInterceptor(
			_bundleContext);

		featureContext.register(
			_nestedFieldsWriterInterceptor, Priorities.USER - 10);

		featureContext.register(
			new PaginationContextProvider(_paginationProvider, _portal));
		featureContext.register(
			new SiteParamConverterProvider(
				_depotEntryLocalService, _groupLocalService));
		featureContext.register(
			new SortContextProvider(_language, _portal, _sortParserProvider));
		featureContext.register(new UserContextProvider(_portal));

		return false;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Deactivate
	protected void deactivate() {
		if (_nestedFieldsWriterInterceptor != null) {
			_nestedFieldsWriterInterceptor.destroy();
		}
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

	private BundleContext _bundleContext;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ContextDataInjectorBuilderFactory
		_contextDataInjectorBuilderFactory;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference(
		target = "(result.class.name=com.liferay.portal.kernel.search.filter.Filter)"
	)
	private ExpressionConvert<Filter> _expressionConvert;

	@Reference
	private ExtensionProviderRegistry _extensionProviderRegistry;

	@Reference
	private FilterParserProvider _filterParserProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	private NestedFieldsWriterInterceptor _nestedFieldsWriterInterceptor;

	@Reference
	private PaginationProvider _paginationProvider;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SortParserProvider _sortParserProvider;

	@Reference
	private VulcanBatchEngineExportTaskResourceFactory
		_vulcanBatchEngineExportTaskResourceFactory;

	@Reference
	private VulcanBatchEngineImportTaskResourceFactory
		_vulcanBatchEngineImportTaskResourceFactory;

}