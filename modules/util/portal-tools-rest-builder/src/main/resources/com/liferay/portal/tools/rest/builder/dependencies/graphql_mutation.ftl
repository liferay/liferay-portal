package ${configYAML.apiPackagePath}.internal.graphql.mutation.${escapedVersion};

<#list allExternalSchemas?keys as schemaName>
	import ${configYAML.apiPackagePath}.resource.${escapedVersion}.${schemaName}Resource;
</#list>

<#list allSchemas?keys as schemaName>
	import ${configYAML.apiPackagePath}.dto.${escapedVersion}.${schemaName};
	import ${configYAML.apiPackagePath}.resource.${escapedVersion}.${schemaName}Resource;
</#list>

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.NotEmpty;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author ${configYAML.author}
 * @generated
 */
@Generated("")
public class Mutation {

	<#assign
		javaMethodSignatures = freeMarkerTool.getGraphQLJavaMethodSignatures(configYAML, "mutation", openAPIYAML)

		schemaNames = freeMarkerTool.getGraphQLSchemaNames(javaMethodSignatures)
	/>

	<#list schemaNames as schemaName>
		public static void set${schemaName}ResourceComponentServiceObjects(ComponentServiceObjects<${schemaName}Resource> ${freeMarkerTool.getSchemaVarName(schemaName)}ResourceComponentServiceObjects) {
			_${freeMarkerTool.getSchemaVarName(schemaName)}ResourceComponentServiceObjects = ${freeMarkerTool.getSchemaVarName(schemaName)}ResourceComponentServiceObjects;
		}
	</#list>

	<#list javaMethodSignatures as javaMethodSignature>
		${freeMarkerTool.getGraphQLMethodAnnotations(javaMethodSignature)}
		public

		<#if javaMethodSignature.returnType?contains("void")>
			boolean
		<#else>
			${javaMethodSignature.returnType}
		</#if>

		${freeMarkerTool.getGraphQLMutationName(javaMethodSignature.methodName)}(${freeMarkerTool.getGraphQLParameters(javaMethodSignature.javaMethodParameters, javaMethodSignature.operation, true)}) throws Exception {
			<#assign arguments = freeMarkerTool.getGraphQLArguments(javaMethodSignature.javaMethodParameters, freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)) />

			<#if javaMethodSignature.returnType?contains("java.util.Collection<")>
				return _applyComponentServiceObjects(
					_${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}ResourceComponentServiceObjects, this::_populateResourceContext,
					${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}Resource -> {

						Page paginationPage = ${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}Resource.${javaMethodSignature.methodName}(${arguments});

						return paginationPage.getItems();
					});
			<#elseif javaMethodSignature.returnType?contains("void")>
				_applyVoidComponentServiceObjects(_${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}ResourceComponentServiceObjects, this::_populateResourceContext,${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}Resource -> ${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}Resource.${javaMethodSignature.methodName}(${arguments}));

				return true;
			<#else>
				return _applyComponentServiceObjects(_${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}ResourceComponentServiceObjects, this::_populateResourceContext,${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}Resource -> ${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}Resource.${javaMethodSignature.methodName}(${arguments}));
			</#if>
		}
	</#list>

	private <T, R, E1 extends Throwable, E2 extends Throwable> R _applyComponentServiceObjects(ComponentServiceObjects<T> componentServiceObjects, UnsafeConsumer<T, E1> unsafeConsumer, UnsafeFunction<T, R, E2> unsafeFunction) throws E1, E2 {
		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private <T, E1 extends Throwable, E2 extends Throwable> void _applyVoidComponentServiceObjects(ComponentServiceObjects<T> componentServiceObjects, UnsafeConsumer<T, E1> unsafeConsumer, UnsafeConsumer<T, E2> unsafeFunction) throws E1, E2 {
		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	<#list schemaNames as schemaName>
		<#assign
			javaDataType = freeMarkerTool.getJavaDataType(configYAML, openAPIYAML, schemaName)!""

			generateBatch = freeMarkerTool.generateBatch(configYAML, javaDataType, freeMarkerTool.getResourceJavaMethodSignatures(configYAML, openAPIYAML, schemaName), schemaName)
		/>

		private void _populateResourceContext(${schemaName}Resource ${freeMarkerTool.getSchemaVarName(schemaName)}Resource) throws Exception {
			${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setContextAcceptLanguage(_acceptLanguage);
			${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setContextCompany(_company);
			${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setContextHttpServletRequest(_httpServletRequest);
			${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setContextHttpServletResponse(_httpServletResponse);
			${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setContextUriInfo(_uriInfo);
			${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setContextUser(_user);
			${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setGroupLocalService(_groupLocalService);
			${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setRoleLocalService(_roleLocalService);

			<#if generateBatch>
				<#assign useVulcanBatchEngineTaskResources = true />

				<#if freeMarkerTool.isVersionCompatible(configYAML, 2)>
					${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setVulcanBatchEngineExportTaskResource(_vulcanBatchEngineExportTaskResource);
				</#if>

				${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setVulcanBatchEngineImportTaskResource(_vulcanBatchEngineImportTaskResource);
			</#if>
		}
	</#list>

	<#list schemaNames as schemaName>
		private static ComponentServiceObjects<${schemaName}Resource> _${freeMarkerTool.getSchemaVarName(schemaName)}ResourceComponentServiceObjects;
	</#list>

	private AcceptLanguage _acceptLanguage;

	<#if freeMarkerTool.isVersionCompatible(configYAML, 2) && freeMarkerTool.containsParameterType(javaMethodSignatures, "com.liferay.portal.vulcan.aggregation.Aggregation")>
		private BiFunction<Object, List<String>, Aggregation> _aggregationBiFunction;
	</#if>

	private com.liferay.portal.kernel.model.Company _company;

	<#if freeMarkerTool.isVersionCompatible(configYAML, 2) && freeMarkerTool.containsParameterType(javaMethodSignatures, "com.liferay.portal.kernel.search.filter.Filter")>
		private BiFunction<Object, String, com.liferay.portal.kernel.search.filter.Filter> _filterBiFunction;
	</#if>

	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]> _sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

	<#if useVulcanBatchEngineTaskResources??>
		<#if freeMarkerTool.isVersionCompatible(configYAML, 2)>
			private VulcanBatchEngineExportTaskResource _vulcanBatchEngineExportTaskResource;
		</#if>

		private VulcanBatchEngineImportTaskResource _vulcanBatchEngineImportTaskResource;
	</#if>

}