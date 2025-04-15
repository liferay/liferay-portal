package ${configYAML.apiPackagePath}.internal.graphql.servlet.${escapedVersion};

<#list allExternalSchemas?keys as schemaName>
	import ${configYAML.apiPackagePath}.internal.resource.${escapedVersion}.${schemaName}ResourceImpl;
	import ${configYAML.apiPackagePath}.resource.${escapedVersion}.${schemaName}Resource;
</#list>

<#list freeMarkerTool.getSchemas(openAPIYAML)?keys as schemaName>
	import ${configYAML.apiPackagePath}.internal.resource.${escapedVersion}.${schemaName}ResourceImpl;
	import ${configYAML.apiPackagePath}.resource.${escapedVersion}.${schemaName}Resource;
</#list>

import ${configYAML.apiPackagePath}.internal.graphql.mutation.${escapedVersion}.Mutation;
import ${configYAML.apiPackagePath}.internal.graphql.query.${escapedVersion}.Query;

import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author ${configYAML.author}
 * @generated
 */
@Component(<#if configYAML.liferayEnterpriseApp>enabled = false,</#if> service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	<#assign
		mutationJavaMethodSignatures = freeMarkerTool.getGraphQLJavaMethodSignatures(configYAML, "mutation", openAPIYAML)

		mutationSchemaNames = freeMarkerTool.getGraphQLSchemaNames(mutationJavaMethodSignatures)

		queryJavaMethodSignatures = freeMarkerTool.getGraphQLJavaMethodSignatures(configYAML, "query", openAPIYAML)

		querySchemaNames = freeMarkerTool.getGraphQLSchemaNames(queryJavaMethodSignatures)
	/>

	@Activate
	public void activate(BundleContext bundleContext) {
		<#list mutationSchemaNames as schemaName>
			Mutation.set${schemaName}ResourceComponentServiceObjects(_${freeMarkerTool.getSchemaVarName(schemaName)}ResourceComponentServiceObjects);
		</#list>

		<#list querySchemaNames as schemaName>
			Query.set${schemaName}ResourceComponentServiceObjects(_${freeMarkerTool.getSchemaVarName(schemaName)}ResourceComponentServiceObjects);
		</#list>
	}

	public String getApplicationName() {
		return "${configYAML.application.name}";
	}

	<#if configYAML.graphQLNamespace??>
		@Override
		public String getGraphQLNamespace() {
			return "${configYAML.graphQLNamespace}";
		}
	</#if>

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "${configYAML.application.baseURI}-graphql/${escapedVersion}";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(String methodName, boolean mutation) {
		if (mutation) {
			return _resourceMethodObjectValuePairs.get("mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>> _resourceMethodObjectValuePairs = new HashMap<String, ObjectValuePair<Class<?>, String>>() {
		{
			<#list mutationJavaMethodSignatures as javaMethodSignature>
				put("mutation#${freeMarkerTool.getGraphQLMutationName(javaMethodSignature.methodName)}", new ObjectValuePair<>(${javaMethodSignature.schemaName}ResourceImpl.class, "${javaMethodSignature.methodName}"));
			</#list>

			<#list queryJavaMethodSignatures as javaMethodSignature>
				put("query#${freeMarkerTool.getGraphQLPropertyName(javaMethodSignature, queryJavaMethodSignatures)}", new ObjectValuePair<>(${javaMethodSignature.schemaName}ResourceImpl.class, "${javaMethodSignature.methodName}"));
			</#list>

			<#list freeMarkerTool.getGraphQLRelationJavaMethodSignatures(configYAML, "query", openAPIYAML) as javaMethodSignature>
				put("query#${javaMethodSignature.parentSchemaName}.${freeMarkerTool.getGraphQLRelationName(javaMethodSignature, queryJavaMethodSignatures)}", new ObjectValuePair<>(${javaMethodSignature.schemaName}ResourceImpl.class, "${javaMethodSignature.methodName}"));
			</#list>

			<#list freeMarkerTool.getParentGraphQLRelationJavaMethodSignatures(configYAML, "query", openAPIYAML) as javaMethodSignature>
				put("query#${javaMethodSignature.parentSchemaName}.parent${javaMethodSignature.parentSchemaName}", new ObjectValuePair<>(${javaMethodSignature.schemaName}ResourceImpl.class, "${javaMethodSignature.methodName}"));
			</#list>
		}
	};

	<#assign schemaNames = mutationSchemaNames />

	<#list querySchemaNames as schemaName>
		<#if !schemaNames?seq_contains(schemaName)>
			<#assign schemaNames = schemaNames + [schemaName] />
		</#if>
	</#list>

	<#list schemaNames as schemaName>
		@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
		private ComponentServiceObjects<${schemaName}Resource> _${freeMarkerTool.getSchemaVarName(schemaName)}ResourceComponentServiceObjects;
	</#list>

}