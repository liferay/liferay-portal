package ${configYAML.apiPackagePath}.internal.graphql.query.${escapedVersion};

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

<#assign
	javaMethodSignatures = freeMarkerTool.getGraphQLJavaMethodSignatures(configYAML, "query", openAPIYAML)

	generateAggregationFunction = freeMarkerTool.containsParameterType(javaMethodSignatures, "com.liferay.portal.vulcan.aggregation.Aggregation")
/>

<#if generateAggregationFunction>
	import com.liferay.portal.vulcan.aggregation.Aggregation;
	import com.liferay.portal.vulcan.aggregation.Facet;
</#if>

import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLTypeExtension;
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
import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author ${configYAML.author}
 * @generated
 */
@Generated("")
public class Query {

	<#assign
		schemaNames = freeMarkerTool.getGraphQLSchemaNames(javaMethodSignatures)
	/>

	<#list schemaNames as schemaName>
		public static void set${schemaName}ResourceComponentServiceObjects(ComponentServiceObjects<${schemaName}Resource> ${freeMarkerTool.getSchemaVarName(schemaName)}ResourceComponentServiceObjects) {
			_${freeMarkerTool.getSchemaVarName(schemaName)}ResourceComponentServiceObjects = ${freeMarkerTool.getSchemaVarName(schemaName)}ResourceComponentServiceObjects;
		}
	</#list>

	<#list javaMethodSignatures as javaMethodSignature>
		/**
		 * ${freeMarkerTool.getGraphQLMethodJavadoc(javaMethodSignature, javaMethodSignatures, openAPIYAML)}
		 */
		${freeMarkerTool.getGraphQLMethodAnnotations(javaMethodSignature)}
		public

		<#if javaMethodSignature.returnType?contains("Collection<")>
			${javaMethodSignature.schemaName}Page
		<#else>
			${javaMethodSignature.returnType}
		</#if>

		${freeMarkerTool.getGraphQLPropertyName(javaMethodSignature, javaMethodSignatures)}(${freeMarkerTool.getGraphQLParameters(javaMethodSignature.javaMethodParameters, javaMethodSignature.operation, true)}) throws Exception {
			<#assign arguments = freeMarkerTool.getGraphQLArguments(javaMethodSignature.javaMethodParameters, freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)) />

			<#if javaMethodSignature.returnType?contains("Collection<")>
				return _applyComponentServiceObjects(
					_${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}ResourceComponentServiceObjects,
					this::_populateResourceContext,
					${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}Resource ->
						new ${javaMethodSignature.schemaName}Page(
							${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}Resource.${javaMethodSignature.methodName}(${arguments}))
					);
			<#else>
				return _applyComponentServiceObjects(_${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}ResourceComponentServiceObjects, this::_populateResourceContext, ${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}Resource -> ${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}Resource.${javaMethodSignature.methodName}(${arguments}));
			</#if>
		}
	</#list>

	<#list freeMarkerTool.getGraphQLRelationJavaMethodSignatures(configYAML, "query", openAPIYAML) as javaMethodSignature>
		@GraphQLTypeExtension(${javaMethodSignature.parentSchemaName}.class)
		public class ${javaMethodSignature.methodName?cap_first}TypeExtension {

			public ${javaMethodSignature.methodName?cap_first}TypeExtension(${javaMethodSignature.parentSchemaName} ${javaMethodSignature.parentSchemaName?uncap_first}) {
				_${javaMethodSignature.parentSchemaName?uncap_first} = ${javaMethodSignature.parentSchemaName?uncap_first};
			}

			${freeMarkerTool.getGraphQLMethodAnnotations(javaMethodSignature)}
			public

			<#if javaMethodSignature.returnType?contains("Collection<")>
				${javaMethodSignature.schemaName}Page
			<#else>
				${javaMethodSignature.returnType}
			</#if>

			${freeMarkerTool.getGraphQLRelationName(javaMethodSignature, javaMethodSignatures)}(${freeMarkerTool.getGraphQLParameters(javaMethodSignature.javaMethodParameters[1..*(javaMethodSignature.javaMethodParameters?size - 1)], javaMethodSignature.operation, true)}) throws Exception {
				<#assign arguments = freeMarkerTool.getGraphQLArguments(javaMethodSignature.javaMethodParameters[1..*(javaMethodSignature.javaMethodParameters?size - 1)], freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)) />

				<#if javaMethodSignature.returnType?contains("Collection<")>
					return _applyComponentServiceObjects(
						_${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}ResourceComponentServiceObjects,
						Query.this::_populateResourceContext,
						${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}Resource ->
							new ${javaMethodSignature.schemaName}Page(
								${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}Resource.${javaMethodSignature.methodName}(_${javaMethodSignature.parentSchemaName?uncap_first}.get${freeMarkerTool.getGraphQLJavaParameterName(configYAML, openAPIYAML, javaMethodSignature.parentSchemaName, allSchemas, javaMethodSignature.javaMethodParameters[0])}()

								<#if arguments?has_content>
									, ${arguments}
								</#if>)));
				<#else>
					return _applyComponentServiceObjects(_${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}ResourceComponentServiceObjects, Query.this::_populateResourceContext, ${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}Resource -> ${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}Resource.${javaMethodSignature.methodName}(
						_${javaMethodSignature.parentSchemaName?uncap_first}.get${freeMarkerTool.getGraphQLJavaParameterName(configYAML, openAPIYAML, javaMethodSignature.parentSchemaName, allSchemas, javaMethodSignature.javaMethodParameters[0])}()

						<#if arguments?has_content>
							, ${arguments}
						</#if>));
				</#if>
			}

			private ${javaMethodSignature.parentSchemaName} _${javaMethodSignature.parentSchemaName?uncap_first};

		}
	</#list>

	<#list schemaNames as schemaName>
		@GraphQLName("${schemaName}Page")
		public class ${schemaName}Page {

			public ${schemaName}Page(Page ${freeMarkerTool.getSchemaVarName(schemaName)}Page) {
				actions = ${freeMarkerTool.getSchemaVarName(schemaName)}Page.getActions();

				<#if generateAggregationFunction>
					facets = ${freeMarkerTool.getSchemaVarName(schemaName)}Page.getFacets();
				</#if>

				items = ${freeMarkerTool.getSchemaVarName(schemaName)}Page.getItems();
				lastPage = ${freeMarkerTool.getSchemaVarName(schemaName)}Page.getLastPage();
				page = ${freeMarkerTool.getSchemaVarName(schemaName)}Page.getPage();
				pageSize = ${freeMarkerTool.getSchemaVarName(schemaName)}Page.getPageSize();
				totalCount = ${freeMarkerTool.getSchemaVarName(schemaName)}Page.getTotalCount();
			}

			@GraphQLField
			protected Map<String, Map<String, String>> actions;

			<#if generateAggregationFunction>
				@GraphQLField
				protected List<Facet> facets;
			</#if>

			@GraphQLField
			protected java.util.Collection<
					<#if !stringUtil.startsWith(javaDataTypeMap[schemaName], configYAML.apiPackagePath)>
						${javaDataTypeMap[schemaName]}
					<#else>
						${schemaName}
					</#if>
				> items;

			@GraphQLField
			protected long lastPage;

			@GraphQLField
			protected long page;

			@GraphQLField
			protected long pageSize;

			@GraphQLField
			protected long totalCount;

		}
	</#list>

	<#list freeMarkerTool.getParentGraphQLRelationJavaMethodSignatures(configYAML, "query", openAPIYAML) as javaMethodSignature>
		@GraphQLTypeExtension(${javaMethodSignature.parentSchemaName}.class)
		public class Parent${javaMethodSignature.parentSchemaName}${javaMethodSignature.javaMethodParameters[0].parameterName?cap_first}TypeExtension {

			public Parent${javaMethodSignature.parentSchemaName}${javaMethodSignature.javaMethodParameters[0].parameterName?cap_first}TypeExtension(${javaMethodSignature.parentSchemaName} ${javaMethodSignature.parentSchemaName?uncap_first}) {
				_${javaMethodSignature.parentSchemaName?uncap_first} = ${javaMethodSignature.parentSchemaName?uncap_first};
			}

			${freeMarkerTool.getGraphQLMethodAnnotations(javaMethodSignature)}
			public ${javaMethodSignature.returnType} parent${javaMethodSignature.parentSchemaName}(${freeMarkerTool.getGraphQLParameters(javaMethodSignature.javaMethodParameters[1..*(javaMethodSignature.javaMethodParameters?size - 1)], javaMethodSignature.operation, true)}) throws Exception {
				<#assign arguments = freeMarkerTool.getGraphQLArguments(javaMethodSignature.javaMethodParameters[1..*(javaMethodSignature.javaMethodParameters?size - 1)], freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)) />

				if (_${javaMethodSignature.parentSchemaName?uncap_first}.getParent${javaMethodSignature.javaMethodParameters[0].parameterName?cap_first}() == null) {
					return null;
				}

				return _applyComponentServiceObjects(_${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}ResourceComponentServiceObjects, Query.this::_populateResourceContext, ${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}Resource -> ${freeMarkerTool.getSchemaVarName(javaMethodSignature.schemaName)}Resource.${javaMethodSignature.methodName}(
					_${javaMethodSignature.parentSchemaName?uncap_first}.getParent${javaMethodSignature.javaMethodParameters[0].parameterName?cap_first}()

					<#if arguments?has_content>
						, ${arguments}
					</#if>));
			}

			private ${javaMethodSignature.parentSchemaName} _${javaMethodSignature.parentSchemaName?uncap_first};

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

	<#list schemaNames as schemaName>
		private void _populateResourceContext(${schemaName}Resource ${freeMarkerTool.getSchemaVarName(schemaName)}Resource) throws Exception {
			${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setContextAcceptLanguage(_acceptLanguage);
			${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setContextCompany(_company);
			${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setContextHttpServletRequest(_httpServletRequest);
			${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setContextHttpServletResponse(_httpServletResponse);
			${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setContextUriInfo(_uriInfo);
			${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setContextUser(_user);
			${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setGroupLocalService(_groupLocalService);
			${freeMarkerTool.getSchemaVarName(schemaName)}Resource.setRoleLocalService(_roleLocalService);
		}
	</#list>

	<#list schemaNames as schemaName>
		private static ComponentServiceObjects<${schemaName}Resource> _${freeMarkerTool.getSchemaVarName(schemaName)}ResourceComponentServiceObjects;
	</#list>

	private AcceptLanguage _acceptLanguage;

	<#if generateAggregationFunction>
		private BiFunction<Object, List<String>, Aggregation> _aggregationBiFunction;
	</#if>

	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.filter.Filter> _filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]> _sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}