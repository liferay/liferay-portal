package ${configYAML.apiPackagePath}.internal.resource.${escapedVersion};

<#list allSchemas?keys as schemaName>
	import ${configYAML.apiPackagePath}.dto.${escapedVersion}.${schemaName};
</#list>

import ${configYAML.apiPackagePath}.resource.${escapedVersion}.${schemaName}Resource;

import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;

<#if freeMarkerTool.isVersionCompatible(configYAML, 2)>
	import com.liferay.petra.function.transform.TransformUtil;

<#else>
	import com.liferay.portal.vulcan.util.TransformUtil;
</#if>

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Resource;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PermissionServiceUtil;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParser;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortField;
import com.liferay.portal.odata.sort.SortParser;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.permission.ModelPermissionsUtil;
import com.liferay.portal.vulcan.permission.Permission;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.vulcan.util.ActionUtil;
import com.liferay.portal.vulcan.util.LocalDateTimeUtil;
import com.liferay.portal.vulcan.util.UriInfoUtil;
import ${configYAML.javaEEPackage}.annotation.Generated;

import ${configYAML.javaEEPackage}.servlet.ServletContext;
import ${configYAML.javaEEPackage}.servlet.http.HttpServletRequest;
import ${configYAML.javaEEPackage}.servlet.http.HttpServletResponse;

import ${configYAML.javaEEPackage}.ws.rs.NotSupportedException;
import ${configYAML.javaEEPackage}.ws.rs.core.MultivaluedHashMap;
import ${configYAML.javaEEPackage}.ws.rs.core.MultivaluedMap;
import ${configYAML.javaEEPackage}.ws.rs.core.Response;
import ${configYAML.javaEEPackage}.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author ${configYAML.author}
 * @generated
 */
@Generated("")
<#if configYAML.application??>
	@${configYAML.javaEEPackage}.ws.rs.Path("/${openAPIYAML.info.version}")
</#if>
public abstract class Base${schemaName}ResourceImpl
	implements ${schemaName}Resource

	<#assign
		javaDataType = freeMarkerTool.getJavaDataType(configYAML, openAPIYAML, schemaName)!""
		javaMethodSignatures = freeMarkerTool.getResourceJavaMethodSignatures(configYAML, openAPIYAML, schemaName)
		generateBatch = freeMarkerTool.generateBatch(configYAML, javaDataType, javaMethodSignatures, schemaName)
		generateCRUD = freeMarkerTool.generateCRUD(configYAML, javaMethodSignatures, schemaName)
		properties = freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, schema, allSchemas)
	/>

	<#if generateBatch>
		, EntityModelResource, VulcanBatchEngineTaskItemDelegate<${javaDataType}>
	</#if>

	<#if generateCRUD>
		, VulcanCRUDItemDelegate<${javaDataType}>
	</#if>
{

	<#assign
		generateGetPermissionCheckerMethods = false
		generateMultipartBodyClasses = []
		generatePatchMethods = false
		getParentBatchJavaMethodSignatures = []
		postParentBatchJavaMethodSignatures = []
		postParentByExternalReferenceCodeBatchJavaMethodSignatures = []
	/>

	<#list javaMethodSignatures as javaMethodSignature>
		<#if javaMethodSignature.requestBodyMediaTypes?seq_contains("multipart/form-data") && freeMarkerTool.getMultipartBodySchemas(javaMethodSignature)??>
			<#assign generateMultipartBodyClasses = generateMultipartBodyClasses + [javaMethodSignature] />
		</#if>

		<#assign
			generatePermissions = freeMarkerTool.isGeneratePermissions(configYAML, javaMethodSignature, javaMethodSignatures, schema, schemaName)
			httpMethod = freeMarkerTool.getHTTPMethod(javaMethodSignature.operation)
			parentSchemaName = javaMethodSignature.parentSchemaName!
		/>

		<#if stringUtil.equals(javaMethodSignature.methodName, "delete" + schemaName)>
			<#assign deleteByIdBatchJavaMethodSignature = javaMethodSignature />
		<#elseif stringUtil.equals(javaMethodSignature.methodName, "deleteAssetLibrary" + schemaName)>
			<#assign deleteAssetLibraryBatchJavaMethodSignature = javaMethodSignature />
		<#elseif stringUtil.equals(javaMethodSignature.methodName, "deleteByExternalReferenceCode") || stringUtil.equals(javaMethodSignature.methodName, "delete" + schemaName + "ByExternalReferenceCode")>
			<#assign deleteByExternalReferenceCodeBatchJavaMethodSignature = javaMethodSignature />
		<#elseif stringUtil.equals(javaMethodSignature.methodName, "deleteSite" + schemaName)>
			<#assign deleteSiteBatchJavaMethodSignature = javaMethodSignature />
		<#elseif stringUtil.equals(javaMethodSignature.methodName, "get" + schemaName)>
			<#assign getByIdJavaMethodSignature = javaMethodSignature />
		<#elseif stringUtil.equals(javaMethodSignature.methodName, "get" + schemaName + "ByExternalReferenceCode") || stringUtil.equals(javaMethodSignature.methodName, "get" + parentSchemaName + schemaName + "ByExternalReferenceCode")>
			<#assign getByExternalReferenceCodeBatchJavaMethodSignature = javaMethodSignature />
		<#elseif stringUtil.equals(javaMethodSignature.methodName, "get" + parentSchemaName + schemaNames + "Page")>
			<#if stringUtil.equals(javaMethodSignature.methodName, "getAssetLibrary" + schemaNames + "Page")>
				<#assign getAssetLibraryBatchJavaMethodSignature = javaMethodSignature />
			<#elseif stringUtil.equals(javaMethodSignature.methodName, "getSite" + schemaNames + "Page")>
				<#assign getSiteBatchJavaMethodSignature = javaMethodSignature />
			<#elseif stringUtil.equals(javaMethodSignature.methodName, "get" + parentSchemaName + schemaNames + "Page")>
				<#if parentSchemaName?has_content>
					<#assign getParentBatchJavaMethodSignatures = getParentBatchJavaMethodSignatures + [javaMethodSignature] />
				<#else>
					<#assign getBatchJavaMethodSignature = javaMethodSignature />
				</#if>
			</#if>
		<#elseif stringUtil.equals(javaMethodSignature.methodName, "patch" + schemaName)>
			<#assign patchBatchJavaMethodSignature = javaMethodSignature />
		<#elseif stringUtil.equals(javaMethodSignature.methodName, "post" + parentSchemaName + schemaName)>
			<#if stringUtil.equals(javaMethodSignature.methodName, "postAssetLibrary" + schemaName)>
				<#assign postAssetLibraryBatchJavaMethodSignature = javaMethodSignature />
			<#elseif stringUtil.equals(javaMethodSignature.methodName, "postSite" + schemaName)>
				<#assign postSiteBatchJavaMethodSignature = javaMethodSignature />
			<#elseif stringUtil.equals(javaMethodSignature.methodName, "post" + parentSchemaName + schemaName)>
				<#if parentSchemaName?has_content>
					<#assign postParentBatchJavaMethodSignatures = postParentBatchJavaMethodSignatures + [javaMethodSignature] />
				<#else>
					<#assign postBatchJavaMethodSignature = javaMethodSignature />
				</#if>
			</#if>
		<#elseif stringUtil.equals(javaMethodSignature.methodName, "post" + parentSchemaName + "ByExternalReferenceCode" + schemaName)>
			<#assign postParentByExternalReferenceCodeBatchJavaMethodSignatures = postParentByExternalReferenceCodeBatchJavaMethodSignatures + [javaMethodSignature] />
		<#elseif stringUtil.equals(javaMethodSignature.methodName, "put" + schemaName)>
			<#assign putBatchJavaMethodSignature = javaMethodSignature />
		<#elseif stringUtil.equals(javaMethodSignature.methodName, "putByExternalReferenceCode") || stringUtil.equals(javaMethodSignature.methodName, "put" + schemaName + "ByExternalReferenceCode") || stringUtil.equals(javaMethodSignature.methodName, "put" + parentSchemaName + schemaName + "ByExternalReferenceCode")>
			<#assign putByExternalReferenceCodeBatchJavaMethodSignature = javaMethodSignature />
		</#if>

		<#if generatePermissions>
			protected abstract ${javaMethodSignature.returnType} do${stringUtil.upperCaseFirstLetter(javaMethodSignature.methodName)}(${freeMarkerTool.getResourceParameters(configYAML, javaMethodSignature.javaMethodParameters, javaMethodSignature.operation, allSchemas, false)}) throws Exception;

			<#if configYAML.application??>
				/**
				 * ${freeMarkerTool.getRESTMethodJavadoc(configYAML, javaMethodSignature, openAPIYAML)}
				 */
			</#if>
			@Override
			${freeMarkerTool.getResourceMethodAnnotations(configYAML, javaMethodSignature)}
			public final ${javaMethodSignature.returnType} ${javaMethodSignature.methodName}(${freeMarkerTool.getResourceParameters(configYAML, javaMethodSignature.javaMethodParameters, javaMethodSignature.operation, allSchemas, true)}) throws Exception {
				<#if stringUtil.equals(httpMethod, "get")>
					<#if javaMethodSignature.returnType?contains("Page<")>
						${javaMethodSignature.returnType} ${schemaVarNames}Page =
					<#else>
						${javaMethodSignature.returnType} ${httpMethod}${schemaName} =
					</#if>

					do${stringUtil.upperCaseFirstLetter(javaMethodSignature.methodName)}(

					<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
						${javaMethodParameter.parameterName}

						<#sep>, </#sep>
					</#list>

					);

					<#if javaMethodSignature.returnType?contains("Page<")>
						<#if properties?keys?seq_contains("permissions")>
							for (${schemaName} ${schemaVarName} : ${schemaVarNames}Page.getItems()) {
								${schemaVarName}.setPermissions(
									() -> NestedFieldsSupplier.supply("permissions", nestedField -> {
										Page<Permission> permissionsPage = get${schemaName}PermissionsPage(
											<#if properties?keys?seq_contains("id")>
												${schemaVarName}.getId()
											<#elseif properties?keys?seq_contains(schemaVarName + "Id")>
												${schemaVarName}.get${schemaVarName}Id()
											<#else>
												${schemaVarName}Id
											</#if>

											, null);

										Collection<Permission> permissions = permissionsPage.getItems();

										return permissions.toArray(new Permission[permissions.size()]);
									}));
							}
						</#if>

						return ${schemaVarNames}Page;
					<#else>
						<#if properties?keys?seq_contains("permissions")>
							${httpMethod}${schemaName}.setPermissions(
								() -> NestedFieldsSupplier.supply("permissions", nestedField -> {
										Page<Permission> permissionsPage = get${schemaName}PermissionsPage(
											<#if properties?keys?seq_contains("id")>
												${httpMethod}${schemaName}.getId()
											<#elseif properties?keys?seq_contains(schemaVarName + "Id")>
												${httpMethod}${schemaName}.get${schemaVarName}Id()
											<#else>
												${schemaVarName}Id
											</#if>

											, null);

										Collection<Permission> permissions = permissionsPage.getItems();

										return permissions.toArray(new Permission[permissions.size()]);
								}));

							return ${httpMethod}${schemaName};
						</#if>
					</#if>
				<#else>
					Permission[] permissions = ${schemaVarName}.getPermissions();

					${javaMethodSignature.returnType} ${httpMethod}${schemaName} =
						do${stringUtil.upperCaseFirstLetter(javaMethodSignature.methodName)}(

						<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
							${javaMethodParameter.parameterName}

							<#sep>, </#sep>
						</#list>

						);

					if (permissions != null) {
						Page<Permission> permissionsPage = put${schemaName}PermissionsPage(
							<#if properties?keys?seq_contains("id")>
								${httpMethod}${schemaName}.getId()
							<#elseif properties?keys?seq_contains(schemaVarName + "Id")>
								${httpMethod}${schemaName}.get${schemaVarName}Id()
							<#else>
								${schemaVarName}Id
							</#if>

							, permissions);

						${httpMethod}${schemaName}.setPermissions(
							() -> NestedFieldsSupplier.supply("permissions", nestedField -> {
								Collection<Permission> collection = permissionsPage.getItems();

								return collection.toArray(new Permission[collection.size()]);
							}));
					}

					return ${httpMethod}${schemaName};
				</#if>

			}

			<#continue>
		</#if>

		<#if configYAML.application??>
			/**
			 * ${freeMarkerTool.getRESTMethodJavadoc(configYAML, javaMethodSignature, openAPIYAML)}
			 */
		</#if>
		@Override
		${freeMarkerTool.getResourceMethodAnnotations(configYAML, javaMethodSignature)}
		public <#if generatePermissions>final</#if> ${javaMethodSignature.returnType} ${javaMethodSignature.methodName}(${freeMarkerTool.getResourceParameters(configYAML, javaMethodSignature.javaMethodParameters, javaMethodSignature.operation, allSchemas, true)}) throws Exception {
			<#if stringUtil.equals(javaMethodSignature.returnType, "boolean")>
				return false;
			<#elseif generateBatch && stringUtil.equals(javaMethodSignature.methodName, "delete" + schemaName + "Batch")>
				vulcanBatchEngineImportTaskResource.setContextAcceptLanguage(contextAcceptLanguage);
				vulcanBatchEngineImportTaskResource.setContextCompany(contextCompany);
				vulcanBatchEngineImportTaskResource.setContextHttpServletRequest(contextHttpServletRequest);
				vulcanBatchEngineImportTaskResource.setContextUriInfo(contextUriInfo);
				vulcanBatchEngineImportTaskResource.setContextUser(contextUser);

				${configYAML.javaEEPackage}.ws.rs.core.Response.ResponseBuilder responseBuilder = ${configYAML.javaEEPackage}.ws.rs.core.Response.accepted();

				return responseBuilder.entity(
					vulcanBatchEngineImportTaskResource.deleteImportTask(${javaDataType}.class.getName(), callbackURL, object)
				).build();
			<#elseif generateBatch && stringUtil.equals(javaMethodSignature.methodName, "post" + parentSchemaName + schemaNames + "PageExportBatch")>
				vulcanBatchEngineExportTaskResource.setContextAcceptLanguage(contextAcceptLanguage);
				vulcanBatchEngineExportTaskResource.setContextCompany(contextCompany);
				vulcanBatchEngineExportTaskResource.setContextHttpServletRequest(contextHttpServletRequest);
				vulcanBatchEngineExportTaskResource.setContextUriInfo(contextUriInfo);
				vulcanBatchEngineExportTaskResource.setContextUser(contextUser);
				vulcanBatchEngineExportTaskResource.setGroupLocalService(groupLocalService);

				${configYAML.javaEEPackage}.ws.rs.core.Response.ResponseBuilder responseBuilder = ${configYAML.javaEEPackage}.ws.rs.core.Response.accepted();

				return responseBuilder.entity(
					vulcanBatchEngineExportTaskResource.postExportTask(${javaDataType}.class.getName(), callbackURL, contentType, fieldNames)
				).build();
			<#elseif generateBatch && (stringUtil.equals(javaMethodSignature.methodName, "post" + parentSchemaName + schemaName + "Batch") || stringUtil.equals(javaMethodSignature.methodName, "post" + parentSchemaName + "Id" + schemaName + "Batch"))>
				vulcanBatchEngineImportTaskResource.setContextAcceptLanguage(contextAcceptLanguage);
				vulcanBatchEngineImportTaskResource.setContextCompany(contextCompany);
				vulcanBatchEngineImportTaskResource.setContextHttpServletRequest(contextHttpServletRequest);
				vulcanBatchEngineImportTaskResource.setContextUriInfo(contextUriInfo);
				vulcanBatchEngineImportTaskResource.setContextUser(contextUser);

				${configYAML.javaEEPackage}.ws.rs.core.Response.ResponseBuilder responseBuilder = ${configYAML.javaEEPackage}.ws.rs.core.Response.accepted();

				return responseBuilder.entity(
					vulcanBatchEngineImportTaskResource.postImportTask(${javaDataType}.class.getName(), callbackURL, null, object)
				).build();
			<#elseif generateBatch && stringUtil.equals(javaMethodSignature.methodName, "put" + schemaName + "Batch")>
				vulcanBatchEngineImportTaskResource.setContextAcceptLanguage(contextAcceptLanguage);
				vulcanBatchEngineImportTaskResource.setContextCompany(contextCompany);
				vulcanBatchEngineImportTaskResource.setContextHttpServletRequest(contextHttpServletRequest);
				vulcanBatchEngineImportTaskResource.setContextUriInfo(contextUriInfo);
				vulcanBatchEngineImportTaskResource.setContextUser(contextUser);

				${configYAML.javaEEPackage}.ws.rs.core.Response.ResponseBuilder responseBuilder = ${configYAML.javaEEPackage}.ws.rs.core.Response.accepted();

				return responseBuilder.entity(
					vulcanBatchEngineImportTaskResource.putImportTask(${javaDataType}.class.getName(), callbackURL, object)
				).build();
			<#elseif stringUtil.equals(javaMethodSignature.methodName, "get" + schemaName + "PermissionsPage")>
				<#if freeMarkerTool.hasParameter(javaMethodSignature, schemaVarName + "Id")>
					<#assign generateGetPermissionCheckerMethods = true />

					String resourceName = getPermissionCheckerResourceName(${schemaVarName}Id);
					Long resourceId = getPermissionCheckerResourceId(${schemaVarName}Id);

					PermissionServiceUtil.checkPermission(getPermissionCheckerGroupId(${schemaVarName}Id), resourceName, resourceId);

					return toPermissionPage(
						<@getActions
							resourceId="resourceId"
							resourceName="resourceName"
							source=schemaName
						/>,
						resourceId, resourceName, roleNames);
				<#else>
					throw new UnsupportedOperationException("This method needs to be implemented");
				</#if>
			<#elseif stringUtil.equals(javaMethodSignature.methodName, "getAssetLibrary" + schemaName + "PermissionsPage")>
				<#assign generateGetPermissionCheckerMethods = true />

				String portletName = getPermissionCheckerPortletName(assetLibraryId);

				PermissionServiceUtil.checkPermission(assetLibraryId, portletName, assetLibraryId);

				return toPermissionPage(
					<@getActions
						resourceId="assetLibraryId"
						resourceName="portletName"
						source="AssetLibrary" + schemaName
					/>,
					assetLibraryId, portletName, roleNames);
			<#elseif stringUtil.equals(javaMethodSignature.methodName, "getSite" + schemaName + "PermissionsPage")>
				<#if freeMarkerTool.hasParameter(javaMethodSignature, "siteId")>
					<#assign generateGetPermissionCheckerMethods = true />

					String portletName = getPermissionCheckerPortletName(siteId);

					PermissionServiceUtil.checkPermission(siteId, portletName, siteId);

					return toPermissionPage(
						<@getActions
							resourceId="siteId"
							resourceName="portletName"
							source="Site" + schemaName
						/>,
						siteId, portletName, roleNames);
				<#else>
					throw new UnsupportedOperationException("This method needs to be implemented");
				</#if>
			<#elseif stringUtil.equals(javaMethodSignature.methodName, "put" + schemaName + "PermissionsPage")>
				<#if freeMarkerTool.hasParameter(javaMethodSignature, schemaVarName + "Id")>
					<#assign generateGetPermissionCheckerMethods = true />

					String resourceName = getPermissionCheckerResourceName(${schemaVarName}Id);
					Long resourceId = getPermissionCheckerResourceId(${schemaVarName}Id);

					<@updateResourcePermissions
						groupId = "getPermissionCheckerGroupId(${schemaVarName}Id)"
						resourceId = "resourceId"
						resourceName = "resourceName"
					>
						<@getActions
							resourceId="resourceId"
							resourceName="resourceName"
							source=schemaName
						/>
					</@updateResourcePermissions>
				<#else>
					throw new UnsupportedOperationException("This method needs to be implemented");
				</#if>
			<#elseif stringUtil.equals(javaMethodSignature.methodName, "putAssetLibrary" + schemaName + "PermissionsPage")>
				<#assign generateGetPermissionCheckerMethods = true />

				String portletName = getPermissionCheckerPortletName(assetLibraryId);

				<@updateResourcePermissions
					groupId = "assetLibraryId"
					resourceId = "assetLibraryId"
					resourceName = "portletName"
				>
					<@getActions
						resourceId="assetLibraryId"
						resourceName="portletName"
						source="AssetLibrary" + schemaName
					/>
				</@updateResourcePermissions>
			<#elseif stringUtil.equals(javaMethodSignature.methodName, "putSite" + schemaName + "PermissionsPage")>
				<#if freeMarkerTool.hasParameter(javaMethodSignature, "siteId")>
					<#assign generateGetPermissionCheckerMethods = true />

					String portletName = getPermissionCheckerPortletName(siteId);

					<@updateResourcePermissions
						groupId = "siteId"
						resourceId = "siteId"
						resourceName = "portletName"
					>
						<@getActions
							resourceId="siteId"
							resourceName="portletName"
							source="Site" + schemaName
						/>
					</@updateResourcePermissions>
				<#else>
					throw new UnsupportedOperationException("This method needs to be implemented");
				</#if>
			<#elseif stringUtil.equals(javaMethodSignature.returnType, "java.lang.Boolean")>
				return false;
			<#elseif stringUtil.equals(javaMethodSignature.returnType, "java.lang.Double") ||
					 stringUtil.equals(javaMethodSignature.returnType, "java.lang.Number")>

				return 0.0;
			<#elseif stringUtil.equals(javaMethodSignature.returnType, "java.lang.Float")>
				return 0f;
			<#elseif stringUtil.equals(javaMethodSignature.returnType, "java.lang.Integer")>
				return 0;
			<#elseif stringUtil.equals(javaMethodSignature.returnType, "java.lang.Long")>
				return 0L;
			<#elseif stringUtil.equals(javaMethodSignature.returnType, "java.lang.Object")>
				return null;
			<#elseif stringUtil.equals(javaMethodSignature.returnType, "java.lang.String")>
				return StringPool.BLANK;
			<#elseif stringUtil.equals(javaMethodSignature.returnType, "java.math.BigDecimal")>
				return java.math.BigDecimal.ZERO;
			<#elseif stringUtil.equals(javaMethodSignature.returnType, "java.util.Date")>
				return new java.util.Date();
			<#elseif stringUtil.equals(javaMethodSignature.returnType, configYAML.javaEEPackage + ".ws.rs.core.Response")>
				${configYAML.javaEEPackage}.ws.rs.core.Response.ResponseBuilder responseBuilder = ${configYAML.javaEEPackage}.ws.rs.core.Response.ok();

				return responseBuilder.build();
			<#elseif stringUtil.equals(javaMethodSignature.returnType, "void")>
			<#elseif javaMethodSignature.returnType?contains("Page<")>
				return Page.of(Collections.emptyList());
			<#elseif freeMarkerTool.hasHTTPMethod(javaMethodSignature, "patch") &&
					 freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "get" + javaMethodSignature.methodName?remove_beginning("patch")) &&
					 freeMarkerTool.hasJavaMethodSignature(javaMethodSignatures, "put" + javaMethodSignature.methodName?remove_beginning("patch")) &&
					 !freeMarkerTool.hasRequestBodyMediaType(javaMethodSignature, "multipart/form-data") &&
					 !freeMarkerTool.hasRequestBodyMediaType(freeMarkerTool.getJavaMethodSignature(javaMethodSignatures, "put" + javaMethodSignature.methodName?remove_beginning("patch")), "multipart/form-data")>
				<#assign
					generatePatchMethods = true
					getJavaMethodSignature = freeMarkerTool.getJavaMethodSignature(javaMethodSignatures, "get" + javaMethodSignature.methodName?remove_beginning("patch"))
					javaMethodParameters = javaMethodSignature.javaMethodParameters[0..javaMethodSignature.javaMethodParameters?size-2]
				/>

				${javaDataType} existing${schemaName} = ${getJavaMethodSignature.methodName}(
					<#assign firstParameter = true />

					<#list javaMethodParameters as javaMethodParameter>
						<#if !freeMarkerTool.hasParameter(getJavaMethodSignature, javaMethodParameter.parameterName)>
							<#continue>
						</#if>

						<#if firstParameter>
							<#assign firstParameter = false />
						<#else>
							,
						</#if>

						${javaMethodParameter.parameterName}
					</#list>
				);

				<#assign writableDTOProperties = freeMarkerTool.getWritableDTOProperties(configYAML, openAPIYAML, schema, allSchemas) />

				<#list writableDTOProperties?keys as propertyName>
					<#if !freeMarkerTool.isDTOSchemaProperty(configYAML, propertyName, schema, allSchemas) && !stringUtil.equals(propertyName, "id")>
						if (${schemaVarName}.get${propertyName?cap_first}() != null) {
							<#assign dtoPropertySchema = freeMarkerTool.getDTOPropertySchema(configYAML, propertyName, schema, allSchemas) />

							<#if dtoPropertySchema.isJsonMap()>
								${writableDTOProperties[propertyName]} ${propertyName} = existing${schemaName}.get${propertyName?cap_first}();

								${propertyName}.putAll(${schemaVarName}.get${propertyName?cap_first}());

								existing${schemaName}.set${propertyName?cap_first}(${propertyName});
							<#else>
								existing${schemaName}.set${propertyName?cap_first}(${schemaVarName}.get${propertyName?cap_first}());
							</#if>
						}
					<#elseif stringUtil.equals(writableDTOProperties[propertyName], "CustomField[]")>
						existing${schemaName}.set${propertyName?cap_first}(${schemaVarName}.get${propertyName?cap_first}());
					</#if>
				</#list>

				preparePatch(${schemaVarName}, existing${schemaName});

				<#assign javaMethodParameterName = javaMethodSignature.methodName?replace("patch", "put") />

				return ${javaMethodParameterName}(
					<#list javaMethodParameters as javaMethodParameter>
						${javaMethodParameter.parameterName}

						<#sep>, </#sep>
					</#list>

					, existing${schemaName}
				);
			<#else>
				<#assign returnTypeSchema = allSchemas[javaMethodSignature.returnType?substring(javaMethodSignature.returnType?last_index_of('.') + 1)]! />

				<#if returnTypeSchema.discriminator?has_content>
			   		return null;
				<#else>
					return new ${javaMethodSignature.returnType}();
				</#if>
			</#if>
		}
	</#list>

	<#if generateBatch>
		<#assign
			createStrategies = freeMarkerTool.getVulcanBatchImplementationCreateStrategies(javaMethodSignatures, properties)
			updateStrategies = freeMarkerTool.getVulcanBatchImplementationUpdateStrategies(javaMethodSignatures)

			parserMethodDataTypes = []
		/>
		@Override
		@SuppressWarnings("PMD.UnusedLocalVariable")
		public void create(Collection<${javaDataType}> ${schemaVarNames}, Map<String, Serializable> parameters) throws Exception {
			<#if createStrategies?has_content>
				UnsafeFunction<${javaDataType}, ${javaDataType}, Exception> ${schemaVarName}UnsafeFunction = null;

				String createStrategy = (String)parameters.getOrDefault("createStrategy", "INSERT");
			</#if>

			<#if createStrategies?seq_contains("INSERT")>
				<#assign parentParameterNames = [] />

				if (StringUtil.equalsIgnoreCase(createStrategy, "INSERT")) {
					<#if postBatchJavaMethodSignature??>
						<#if stringUtil.equals(javaDataType, postBatchJavaMethodSignature.returnType)>
							${schemaVarName}UnsafeFunction = ${schemaVarName} -> ${postBatchJavaMethodSignature.methodName}(
						<#else>
							${schemaVarName}UnsafeFunction = ${schemaVarName} -> {
								${postBatchJavaMethodSignature.methodName}(
						</#if>

						<@getPOSTBatchJavaMethodParameters
							javaMethodParameters = postBatchJavaMethodSignature.javaMethodParameters
							schemaVarName = schemaVarName
						/>

						);

						<#if !stringUtil.equals(javaDataType, postBatchJavaMethodSignature.returnType)>
								return null;
							};
						</#if>
					</#if>

					<#if postParentBatchJavaMethodSignatures?has_content>
						<#list postParentBatchJavaMethodSignatures as postParentBatchJavaMethodSignature>
							<#assign parentParameterNames = parentParameterNames + [postParentBatchJavaMethodSignature.parentSchemaName!?uncap_first + "Id"] />

							if (parameters.containsKey("${postParentBatchJavaMethodSignature.parentSchemaName?uncap_first}Id")) {
								<#if stringUtil.equals(javaDataType, postParentBatchJavaMethodSignature.returnType)>
									${schemaVarName}UnsafeFunction = ${schemaVarName} -> ${postParentBatchJavaMethodSignature.methodName}(
								<#else>
									${schemaVarName}UnsafeFunction = ${schemaVarName} -> {
										${postParentBatchJavaMethodSignature.methodName}(
								</#if>

								<@getPOSTBatchJavaMethodParameters
									javaMethodParameters = postParentBatchJavaMethodSignature.javaMethodParameters
									schemaVarName = schemaVarName
								/>

								);

								<#if !stringUtil.equals(javaDataType, postParentBatchJavaMethodSignature.returnType)>
										return null;
									};
								</#if>
							}

							<#if postParentBatchJavaMethodSignature?has_next>
								else
							</#if>
						</#list>
					</#if>

					<#if postParentByExternalReferenceCodeBatchJavaMethodSignatures?has_content>
						<#list postParentByExternalReferenceCodeBatchJavaMethodSignatures as postParentByExternalReferenceCodeBatchJavaMethodSignature>
							<#assign parentParameterNames = parentParameterNames + [postParentByExternalReferenceCodeBatchJavaMethodSignature.javaMethodParameters[0].parameterName] />

							<#if postParentBatchJavaMethodSignatures?has_content>
								else
							</#if>

							if (parameters.containsKey("${postParentByExternalReferenceCodeBatchJavaMethodSignature.javaMethodParameters[0].parameterName}")) {
								<#if stringUtil.equals(javaDataType, postParentByExternalReferenceCodeBatchJavaMethodSignature.returnType)>
									${schemaVarName}UnsafeFunction = ${schemaVarName} -> ${postParentByExternalReferenceCodeBatchJavaMethodSignature.methodName}(
								<#else>
									${schemaVarName}UnsafeFunction = ${schemaVarName} -> {
										${postParentByExternalReferenceCodeBatchJavaMethodSignature.methodName}(
								</#if>

								<@getPOSTBatchJavaMethodParameters
									javaMethodParameters = postParentByExternalReferenceCodeBatchJavaMethodSignature.javaMethodParameters
									schemaVarName = schemaVarName
								/>

								);

								<#if !stringUtil.equals(javaDataType, postParentByExternalReferenceCodeBatchJavaMethodSignature.returnType)>
										return null;
									};
								</#if>
							}

							<#if postParentByExternalReferenceCodeBatchJavaMethodSignature?has_next>
								else
							</#if>
						</#list>
					</#if>

					<#if postAssetLibraryBatchJavaMethodSignature??>
						<#assign parentParameterNames = parentParameterNames + ["assetLibraryId"] />

						<#if postParentBatchJavaMethodSignatures?has_content || postParentByExternalReferenceCodeBatchJavaMethodSignatures?has_content>
							else
						</#if>

						if (parameters.containsKey("assetLibraryId")) {
							<#if stringUtil.equals(javaDataType, postAssetLibraryBatchJavaMethodSignature.returnType)>
								${schemaVarName}UnsafeFunction = ${schemaVarName} -> ${postAssetLibraryBatchJavaMethodSignature.methodName}(
							<#else>
								${schemaVarName}UnsafeFunction = ${schemaVarName} -> { ${postAssetLibraryBatchJavaMethodSignature.methodName}(
							</#if>

							<@getPOSTBatchJavaMethodParameters
								javaMethodParameters = postAssetLibraryBatchJavaMethodSignature.javaMethodParameters
								schemaVarName = schemaVarName
							/>

							);

							<#if !stringUtil.equals(javaDataType, postAssetLibraryBatchJavaMethodSignature.returnType)>
									return null;
								};
							</#if>
						}
					</#if>

					<#if postSiteBatchJavaMethodSignature??>
						<#assign parentParameterNames = parentParameterNames + ["siteId"] />

						<#if postParentBatchJavaMethodSignatures?has_content || postParentByExternalReferenceCodeBatchJavaMethodSignatures?has_content || postAssetLibraryBatchJavaMethodSignature??>
							else
						</#if>

						if (parameters.containsKey("siteId")) {
							<#if stringUtil.equals(javaDataType, postSiteBatchJavaMethodSignature.returnType)>
								${schemaVarName}UnsafeFunction = ${schemaVarName} -> ${postSiteBatchJavaMethodSignature.methodName}(
							<#else>
								${schemaVarName}UnsafeFunction = ${schemaVarName} -> { ${postSiteBatchJavaMethodSignature.methodName}(
							</#if>

							<@getPOSTBatchJavaMethodParameters
								javaMethodParameters = postSiteBatchJavaMethodSignature.javaMethodParameters
								schemaVarName = schemaVarName
							/>

							);

							<#if !stringUtil.equals(javaDataType, postSiteBatchJavaMethodSignature.returnType)>
									return null;
								};
							</#if>
						}
					</#if>

					<#if !postBatchJavaMethodSignature?? && parentParameterNames?has_content>
						else {
							throw new NotSupportedException("One of the following parameters must be specified: [${parentParameterNames?join(", ")}]");
						}
					</#if>
				}
			</#if>

			<#if createStrategies?seq_contains("UPSERT")>
				if (StringUtil.equalsIgnoreCase(createStrategy, "UPSERT")) {
					String updateStrategy = (String)parameters.getOrDefault("updateStrategy", "UPDATE");

					<#if getByExternalReferenceCodeBatchJavaMethodSignature?? && patchBatchJavaMethodSignature?? && (postAssetLibraryBatchJavaMethodSignature?? || postBatchJavaMethodSignature?? || postParentBatchJavaMethodSignatures?has_content || postParentByExternalReferenceCodeBatchJavaMethodSignatures?has_content || postSiteBatchJavaMethodSignature??)>
						if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
							${schemaVarName}UnsafeFunction = ${schemaVarName} -> {
								${schemaName} persisted${schemaName} = null;

								try {
									${schemaName} get${schemaName} = ${getByExternalReferenceCodeBatchJavaMethodSignature.methodName}(

									<#list getByExternalReferenceCodeBatchJavaMethodSignature.javaMethodParameters as javaMethodParameter>
										<#if stringUtil.equals(javaMethodParameter.parameterName, "externalReferenceCode")>
											${schemaVarName}.get${javaMethodParameter.parameterName?cap_first}()
										<#elseif getByExternalReferenceCodeBatchJavaMethodSignature.parentSchemaName?? && stringUtil.equals(javaMethodParameter.parameterName, getByExternalReferenceCodeBatchJavaMethodSignature.parentSchemaName!?uncap_first + "Id")>
											<#if properties?keys?seq_contains(javaMethodParameter.parameterName)>
												${schemaVarName}.get${javaMethodParameter.parameterName?cap_first}() != null ?
												${schemaVarName}.get${javaMethodParameter.parameterName?cap_first}() :
											</#if>

											<@castParameters
												type = javaMethodParameter.parameterType
												value = javaMethodParameter.parameterName
											/>
										<#elseif stringUtil.equals(javaMethodParameter.parameterName, schemaVarName)>
											${schemaVarName}
										<#else>
											null
										</#if>

										<#sep>, </#sep>
									</#list>

									);

									<#if stringUtil.equals(javaDataType, patchBatchJavaMethodSignature.returnType)>
										persisted${schemaName} = patch${schemaName}(
									<#else>
										patch${schemaName}(
									</#if>

									<#list patchBatchJavaMethodSignature.javaMethodParameters as javaMethodParameter>
										<#if stringUtil.equals(javaMethodParameter.parameterName, schemaVarName)>
											${schemaVarName}
										<#elseif stringUtil.equals(javaMethodParameter.parameterName, schemaVarName + "Id") || stringUtil.equals(javaMethodParameter.parameterName, "id")>
											<#if properties?keys?seq_contains("id")>
												get${schemaName}.getId() != null ? get${schemaName}.getId() :
											<#elseif properties?keys?seq_contains(schemaVarName + "Id")>
												(get${schemaName}.get${schemaName}Id() != null) ? get${schemaName}.get${schemaName}Id() :
											</#if>

											<@castParameters
												type = javaMethodParameter.parameterType
												value = "${schemaVarName}Id"
											/>
										<#elseif stringUtil.equals(javaMethodParameter.parameterName, "multipartBody")>
											null
										<#else>
											${javaMethodParameter.parameterName}
										</#if>

										<#sep>, </#sep>
									</#list>

									);
								}
								catch (NoSuchModelException noSuchModelException) {
									<#assign parentParameterNames = [] />

									<#if postBatchJavaMethodSignature?? && !postParentBatchJavaMethodSignatures?has_content && !postParentByExternalReferenceCodeBatchJavaMethodSignatures?has_content>
										persisted${schemaName} = ${postBatchJavaMethodSignature.methodName}(

										<@getPOSTBatchJavaMethodParameters
											javaMethodParameters = postBatchJavaMethodSignature.javaMethodParameters
											schemaVarName = schemaVarName
										/>

										);
									</#if>

									<#if postParentBatchJavaMethodSignatures?has_content || postParentByExternalReferenceCodeBatchJavaMethodSignatures?has_content>
										<#list postParentBatchJavaMethodSignatures as postParentBatchJavaMethodSignature>
											<#assign parentParameterNames = parentParameterNames + [postParentBatchJavaMethodSignature.parentSchemaName!?uncap_first + "Id"] />

											if (parameters.containsKey("${postParentBatchJavaMethodSignature.parentSchemaName?uncap_first}Id")) {
												persisted${schemaName} = ${postParentBatchJavaMethodSignature.methodName}(

												<@getPOSTBatchJavaMethodParameters
													javaMethodParameters = postParentBatchJavaMethodSignature.javaMethodParameters
													schemaVarName = schemaVarName
												/>

												);

											}

											<#if postParentBatchJavaMethodSignature?has_next>
												else
											</#if>
										</#list>

										<#list postParentByExternalReferenceCodeBatchJavaMethodSignatures as postParentByExternalReferenceCodeBatchJavaMethodSignature>
											<#assign parentParameterNames = parentParameterNames + [postParentByExternalReferenceCodeBatchJavaMethodSignature.javaMethodParameters[0].parameterName] />

											<#if postParentBatchJavaMethodSignatures?has_content>
												else
											</#if>

											if (parameters.containsKey("${postParentByExternalReferenceCodeBatchJavaMethodSignature.javaMethodParameters[0].parameterName}")) {
												persisted${schemaName} = ${postParentByExternalReferenceCodeBatchJavaMethodSignature.methodName}(

												<@getPOSTBatchJavaMethodParameters
													javaMethodParameters = postParentByExternalReferenceCodeBatchJavaMethodSignature.javaMethodParameters
													schemaVarName = schemaVarName
												/>

												);
											}

											<#if postParentByExternalReferenceCodeBatchJavaMethodSignature?has_next>
												else
											</#if>
										</#list>

										<#if postBatchJavaMethodSignature??>
											else {
												persisted${schemaName} = ${postBatchJavaMethodSignature.methodName}(

												<@getPOSTBatchJavaMethodParameters
													javaMethodParameters = postBatchJavaMethodSignature.javaMethodParameters
													schemaVarName = schemaVarName
												/>

												);
											}
										</#if>
									</#if>

									<#if postAssetLibraryBatchJavaMethodSignature??>
										<#assign parentParameterNames = parentParameterNames + ["assetLibraryId"] />

										<#if postParentBatchJavaMethodSignatures?has_content || postParentByExternalReferenceCodeBatchJavaMethodSignatures?has_content>
											else
										</#if>

										if (parameters.containsKey("assetLibraryId")) {
											persisted${schemaName} = ${postAssetLibraryBatchJavaMethodSignature.methodName}(

											<@getPOSTBatchJavaMethodParameters
												javaMethodParameters = postAssetLibraryBatchJavaMethodSignature.javaMethodParameters
												schemaVarName = schemaVarName
											/>

											);
										}
									</#if>

									<#if postSiteBatchJavaMethodSignature??>
										<#if postParentBatchJavaMethodSignatures?has_content || postParentByExternalReferenceCodeBatchJavaMethodSignatures?has_content || postAssetLibraryBatchJavaMethodSignature??>
											else
										</#if>

										if (parameters.containsKey("siteId")) {
											persisted${schemaName} = ${postSiteBatchJavaMethodSignature.methodName}(

											<@getPOSTBatchJavaMethodParameters
												javaMethodParameters = postSiteBatchJavaMethodSignature.javaMethodParameters
												schemaVarName = schemaVarName
											/>

											);
										}
									</#if>

									<#if !postBatchJavaMethodSignature?? && parentParameterNames?has_content>
										else {
											throw new NotSupportedException("One of the following parameters must be specified: [${parentParameterNames?join(", ")}]");
										}
									</#if>
								}

								return persisted${schemaName};
							};
						}
					</#if>

					<#if putByExternalReferenceCodeBatchJavaMethodSignature??>
						if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
							<#if stringUtil.equals(javaDataType, putByExternalReferenceCodeBatchJavaMethodSignature.returnType)>
								${schemaVarName}UnsafeFunction = ${schemaVarName} -> ${putByExternalReferenceCodeBatchJavaMethodSignature.methodName}(
							<#else>
								${schemaVarName}UnsafeFunction = ${schemaVarName} -> { ${putByExternalReferenceCodeBatchJavaMethodSignature.methodName}(
							</#if>

							<#list putByExternalReferenceCodeBatchJavaMethodSignature.javaMethodParameters as javaMethodParameter>
								<#if stringUtil.equals(javaMethodParameter.parameterName, "externalReferenceCode")>
									${schemaVarName}.get${javaMethodParameter.parameterName?cap_first}()
								<#elseif putByExternalReferenceCodeBatchJavaMethodSignature.parentSchemaName?? && stringUtil.equals(javaMethodParameter.parameterName, putByExternalReferenceCodeBatchJavaMethodSignature.parentSchemaName!?uncap_first + "Id")>
									<#if properties?keys?seq_contains(javaMethodParameter.parameterName)>
										${schemaVarName}.get${javaMethodParameter.parameterName?cap_first}() != null ?
										${schemaVarName}.get${javaMethodParameter.parameterName?cap_first}() :
									</#if>

									<@castParameters
										type = javaMethodParameter.parameterType
										value = javaMethodParameter.parameterName
									/>
								<#elseif stringUtil.equals(javaMethodParameter.parameterName, schemaVarName)>
									${schemaVarName}
								<#else>
									null
								</#if>

								<#sep>, </#sep>
							</#list>
							);

							<#if !stringUtil.equals(javaDataType, putByExternalReferenceCodeBatchJavaMethodSignature.returnType)>
									return null;
								};
							</#if>
						}
					</#if>
				}
			</#if>

			<#if createStrategies?has_content>
				if (${schemaVarName}UnsafeFunction == null) {
					throw new NotSupportedException("Create strategy \"" + createStrategy + "\" is not supported for ${schemaVarName?cap_first}");
				}

				if (contextBatchUnsafeBiConsumer != null) {
					contextBatchUnsafeBiConsumer.accept(${schemaVarNames}, ${schemaVarName}UnsafeFunction);
				}
				else if (contextBatchUnsafeConsumer != null) {
					contextBatchUnsafeConsumer.accept(${schemaVarNames}, ${schemaVarName}UnsafeFunction::apply);
				}
				else {
					for (${javaDataType} ${schemaVarName} : ${schemaVarNames}) {
						${schemaVarName}UnsafeFunction.apply(${schemaVarName});
					}
				}
			<#else>
				throw new UnsupportedOperationException("This method needs to be implemented");
			</#if>
		}

		@Override
		public void delete(Collection<${javaDataType}> ${schemaVarNames}, Map<String, Serializable> parameters) throws Exception {
			<#assign
				useDeleteAssetLibrary = deleteAssetLibraryBatchJavaMethodSignature?? && properties?keys?seq_contains("externalReferenceCode")
				useDeleteByExternalReferenceCode = deleteByExternalReferenceCodeBatchJavaMethodSignature?? && properties?keys?seq_contains("externalReferenceCode")
				useDeleteById = deleteByIdBatchJavaMethodSignature?? && (properties?keys?seq_contains("id") || properties?keys?seq_contains(schemaVarName + "Id"))
				useDeleteSite = deleteSiteBatchJavaMethodSignature?? && properties?keys?seq_contains("externalReferenceCode")
			/>

			<#if useDeleteAssetLibrary || useDeleteByExternalReferenceCode || useDeleteById || useDeleteSite>
				UnsafeFunction<${javaDataType}, ${javaDataType}, Exception> ${schemaVarName}UnsafeFunction = ${schemaVarName} -> {
					<#if useDeleteById>
						<#assign getterMethodName = properties?keys?seq_contains("id")?then("getId", "get" + schemaName + "Id") />

						<#if useDeleteAssetLibrary || useDeleteByExternalReferenceCode || useDeleteSite>
							if (${schemaVarName}.${getterMethodName}() != null) {
								try {
						</#if>

						${deleteByIdBatchJavaMethodSignature.methodName}(${schemaVarName}.${getterMethodName}()

						<#list deleteByIdBatchJavaMethodSignature.javaMethodParameters as javaMethodParameter>
							<#if freeMarkerTool.isQueryParameter(javaMethodParameter, deleteByIdBatchJavaMethodSignature.operation)>
								,

								<@castParameters
									type = javaMethodParameter.parameterType
									value = javaMethodParameter.parameterName
								/>
							</#if>
						</#list>

						);

						return ${schemaVarName};

						<#if useDeleteAssetLibrary || useDeleteByExternalReferenceCode || useDeleteSite>
							}
							catch (Exception exception) {
								if (${schemaVarName}.getExternalReferenceCode() != null) {
									<#if useDeleteByExternalReferenceCode>
												${deleteByExternalReferenceCodeBatchJavaMethodSignature.methodName}(${schemaVarName}.getExternalReferenceCode());

												return ${schemaVarName};
											}
										}
									<#else>
										<#if useDeleteAssetLibrary>
											if (parameters.containsKey("assetLibraryExternalReferenceCode")) {
												${deleteAssetLibraryBatchJavaMethodSignature.methodName}(
													<@getDELETEBatchJavaMethodParameters javaMethodParameters = deleteAssetLibraryBatchJavaMethodSignature.javaMethodParameters />
												);

												return ${schemaVarName};
											}
										</#if>

										<#if useDeleteSite>
											if (parameters.containsKey("siteExternalReferenceCode")) {
												${deleteSiteBatchJavaMethodSignature.methodName}(
													<@getDELETEBatchJavaMethodParameters javaMethodParameters = deleteSiteBatchJavaMethodSignature.javaMethodParameters />
												);

												return ${schemaVarName};
											}
										</#if>

										}
									</#if>
								}
						</#if>
					</#if>

					<#if useDeleteAssetLibrary>
						<#if useDeleteById>else</#if>

						if (parameters.containsKey("assetLibraryExternalReferenceCode")) {
							${deleteAssetLibraryBatchJavaMethodSignature.methodName}(
								<@getDELETEBatchJavaMethodParameters javaMethodParameters = deleteAssetLibraryBatchJavaMethodSignature.javaMethodParameters />
							);

							return ${schemaVarName};
						}
					</#if>

					<#if useDeleteByExternalReferenceCode>
						<#if useDeleteAssetLibrary || useDeleteById>else</#if>

						if (${schemaVarName}.getExternalReferenceCode() != null) {
							${deleteByExternalReferenceCodeBatchJavaMethodSignature.methodName}(${schemaVarName}.getExternalReferenceCode());

							return ${schemaVarName};
						}
					</#if>

					<#if useDeleteSite>
						<#if useDeleteAssetLibrary || useDeleteByExternalReferenceCode || useDeleteById>else</#if>

						if (parameters.containsKey("siteExternalReferenceCode")) {
							${deleteSiteBatchJavaMethodSignature.methodName}(
								<@getDELETEBatchJavaMethodParameters javaMethodParameters = deleteSiteBatchJavaMethodSignature.javaMethodParameters />
							);

							return ${schemaVarName};
						}
					</#if>

					<#if useDeleteAssetLibrary || useDeleteByExternalReferenceCode || useDeleteSite>
						throw new UnsupportedOperationException("Unable to delete by external reference code or ID");
					</#if>
				};

				if (contextBatchUnsafeBiConsumer != null) {
					contextBatchUnsafeBiConsumer.accept(${schemaVarNames}, ${schemaVarName}UnsafeFunction);
				}
				else if (contextBatchUnsafeConsumer != null) {
					contextBatchUnsafeConsumer.accept(${schemaVarNames}, ${schemaVarName}UnsafeFunction::apply);
				}
				else {
					for (${javaDataType} ${schemaVarName} : ${schemaVarNames}) {
						${schemaVarName}UnsafeFunction.apply(${schemaVarName});
					}
				}
			<#else>
				throw new UnsupportedOperationException("This method needs to be implemented");
			</#if>
		}

		public Set<String> getAvailableCreateStrategies() {
			return SetUtil.fromArray(
				<#if createStrategies?has_content>
					"${createStrategies?join("\", \"")}"
				</#if>
			);
		}

		public Set<String> getAvailableUpdateStrategies() {
			return SetUtil.fromArray(
				<#if updateStrategies?has_content>
					"${updateStrategies?join("\", \"")}"
				</#if>
			);
		}

		@Override
		public EntityModel getEntityModel(Map<String, List<String>> multivaluedMap) throws Exception {
			return getEntityModel(new MultivaluedHashMap<String, Object>(multivaluedMap));
		}

		@Override
		public EntityModel getEntityModel(MultivaluedMap multivaluedMap) throws Exception {
			return null;
		}

		public String getResourceName() {
			return "${schemaName}";
		}

		public String getVersion() {
			return "${freeMarkerTool.getVersion(openAPIYAML)}";
		}

		@Override
		public Page<${javaDataType}> read(com.liferay.portal.kernel.search.filter.Filter filter, Pagination pagination, com.liferay.portal.kernel.search.Sort[] sorts, Map<String, Serializable> parameters, String search) throws Exception {
			<#if freeMarkerTool.hasReadVulcanBatchImplementation(javaMethodSignatures)>
				<#assign parentParameterNames = [] />

				<#if getAssetLibraryBatchJavaMethodSignature??>
					<#assign parentParameterNames = parentParameterNames + ["assetLibraryId"] />

					if (parameters.containsKey("assetLibraryId")) {
						return ${getAssetLibraryBatchJavaMethodSignature.methodName}(
							<@getGETBatchJavaMethodParameters javaMethodParameters = getAssetLibraryBatchJavaMethodSignature.javaMethodParameters />
						);
					}
					else
				</#if>

				<#if getSiteBatchJavaMethodSignature??>
					<#assign parentParameterNames = parentParameterNames + ["siteId"] />

					if (parameters.containsKey("siteId")) {
						return ${getSiteBatchJavaMethodSignature.methodName}(
							<@getGETBatchJavaMethodParameters javaMethodParameters = getSiteBatchJavaMethodSignature.javaMethodParameters />
						);
					}
					else
				</#if>

				<#if getParentBatchJavaMethodSignatures?has_content>
					<#list getParentBatchJavaMethodSignatures as parentBatchJavaMethodSignature>
						<#assign
							parentParameterNames = parentParameterNames + [parentBatchJavaMethodSignature.parentSchemaName!?uncap_first + "Id"]
						/>

						if (parameters.containsKey("${parentBatchJavaMethodSignature.parentSchemaName!?uncap_first + "Id"}")) {
							return ${parentBatchJavaMethodSignature.methodName}(
								<@getGETBatchJavaMethodParameters javaMethodParameters = parentBatchJavaMethodSignature.javaMethodParameters />
							);
						}
						else
					</#list>
				</#if>

				<#if getBatchJavaMethodSignature??>
					<#if getAssetLibraryBatchJavaMethodSignature?? || getSiteBatchJavaMethodSignature?? || getParentBatchJavaMethodSignatures?has_content>
						{
					</#if>

					return ${getBatchJavaMethodSignature.methodName}(
						<@getGETBatchJavaMethodParameters javaMethodParameters = getBatchJavaMethodSignature.javaMethodParameters />
					);

					<#if getAssetLibraryBatchJavaMethodSignature?? || getSiteBatchJavaMethodSignature?? || getParentBatchJavaMethodSignatures?has_content>
						}
					</#if>
				<#else>
					{
						throw new NotSupportedException("One of the following parameters must be specified: [${parentParameterNames?join(", ")}]");
					}
				</#if>
			<#else>
				throw new UnsupportedOperationException("This method needs to be implemented");
			</#if>
		}

		@Override
		public void setLanguageId(String languageId) {
			this.contextAcceptLanguage = new AcceptLanguage() {

				@Override
				public List<Locale> getLocales() {
					return null;
				}

				@Override
				public String getPreferredLanguageId() {
					return languageId;
				}

				@Override
				public Locale getPreferredLocale() {
					return LocaleUtil.fromLanguageId(languageId);
				}

			};
		}

		@Override
		public void update(Collection<${javaDataType}> ${schemaVarNames}, Map<String, Serializable> parameters) throws Exception {
			<#if updateStrategies?has_content>
				UnsafeFunction<${javaDataType}, ${javaDataType}, Exception> ${schemaVarName}UnsafeFunction = null;

				String updateStrategy = (String)parameters.getOrDefault("updateStrategy", "UPDATE");
			</#if>

			<#if updateStrategies?seq_contains("PARTIAL_UPDATE")>
				if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
					<#if stringUtil.equals(javaDataType, patchBatchJavaMethodSignature.returnType)>
						${schemaVarName}UnsafeFunction = ${schemaVarName} -> patch${schemaName}(
					<#else>
						${schemaVarName}UnsafeFunction = ${schemaVarName} -> { patch${schemaName}(
					</#if>

					<#list patchBatchJavaMethodSignature.javaMethodParameters as javaMethodParameter>
						<#if stringUtil.equals(javaMethodParameter.parameterName, schemaVarName)>
							${schemaVarName}
						<#elseif stringUtil.equals(javaMethodParameter.parameterName, schemaVarName + "Id") || stringUtil.equals(javaMethodParameter.parameterName, "id")>
							<#if properties?keys?seq_contains("id")>
								${schemaVarName}.getId() != null ? ${schemaVarName}.getId() :
							<#elseif properties?keys?seq_contains(schemaVarName + "Id")>
								(${schemaVarName}.get${schemaName}Id() != null) ? ${schemaVarName}.get${schemaName}Id() :
							</#if>

							<@castParameters
								type = javaMethodParameter.parameterType
								value = "${schemaVarName}Id"
							/>
						<#elseif stringUtil.equals(javaMethodParameter.parameterName, "multipartBody")>
							null
						<#else>
							<@castParameters
								type = javaMethodParameter.parameterType
								value = javaMethodParameter.parameterName
							/>
						</#if>

						<#sep>, </#sep>
					</#list>

					);

					<#if !stringUtil.equals(javaDataType, patchBatchJavaMethodSignature.returnType)>
							return null;
						};
					</#if>
				}
			</#if>

			<#if updateStrategies?seq_contains("UPDATE")>
				if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
					<#if stringUtil.equals(javaDataType, putBatchJavaMethodSignature.returnType)>
						${schemaVarName}UnsafeFunction = ${schemaVarName} -> put${schemaName}(
					<#else>
						${schemaVarName}UnsafeFunction = ${schemaVarName} -> { put${schemaName}(
					</#if>

					<#list putBatchJavaMethodSignature.javaMethodParameters as javaMethodParameter>
						<#if stringUtil.equals(javaMethodParameter.parameterName, "flatten")>
							(Boolean)parameters.get("flatten")
						<#elseif stringUtil.equals(javaMethodParameter.parameterName, schemaVarName)>
							${schemaVarName}
						<#elseif stringUtil.equals(javaMethodParameter.parameterName, schemaVarName + "Id") || stringUtil.equals(javaMethodParameter.parameterName, "id")>
							<#if properties?keys?seq_contains("id")>
								${schemaVarName}.getId() != null ? ${schemaVarName}.getId() :
							<#elseif properties?keys?seq_contains(schemaVarName + "Id")>
								(${schemaVarName}.get${schemaName}Id() != null) ? ${schemaVarName}.get${schemaName}Id() :
							</#if>

							<@castParameters
								type = javaMethodParameter.parameterType
								value = "${schemaVarName}Id"
							/>
						<#elseif putBatchJavaMethodSignature.parentSchemaName?? && stringUtil.equals(javaMethodParameter.parameterName, putBatchJavaMethodSignature.parentSchemaName?uncap_first + "Id")>
							<@castParameters
								type = javaMethodParameter.parameterType
								value = "${javaMethodSignature.parentSchemaName?uncap_first}Id"
							/>
						<#elseif stringUtil.equals(javaMethodParameter.parameterName, "multipartBody")>
							null
						<#else>
							<@castParameters
								type = javaMethodParameter.parameterType
								value = javaMethodParameter.parameterName
							/>
						</#if>

						<#sep>, </#sep>
					</#list>

					);

					<#if !stringUtil.equals(javaDataType, putBatchJavaMethodSignature.returnType)>
							return null;
						};
					</#if>
				}
			</#if>

			<#if updateStrategies?has_content>
				if (${schemaVarName}UnsafeFunction == null) {
					throw new NotSupportedException("Update strategy \"" + updateStrategy + "\" is not supported for ${schemaVarName?cap_first}");
				}

				if (contextBatchUnsafeBiConsumer != null) {
					contextBatchUnsafeBiConsumer.accept(${schemaVarNames}, ${schemaVarName}UnsafeFunction);
				}
				else if (contextBatchUnsafeConsumer != null) {
					contextBatchUnsafeConsumer.accept(${schemaVarNames}, ${schemaVarName}UnsafeFunction::apply);
				}
				else {
					for (${javaDataType} ${schemaVarName} : ${schemaVarNames}) {
						${schemaVarName}UnsafeFunction.apply(${schemaVarName});
					}
				}
			<#else>
				throw new UnsupportedOperationException("This method needs to be implemented");
			</#if>
		}

		<#list freeMarkerTool.distinct(parserMethodDataTypes) as parserMethodDataType>
			private ${parserMethodDataType} _parse${parserMethodDataType}(String value){
				if (value != null){
					<#if stringUtil.equals(parserMethodDataType, "Date")>
						return new Date(value);
					<#elseif stringUtil.equals(parserMethodDataType, "Integer")>
						return Integer.parseInt(value);
					<#else>
						return ${parserMethodDataType}.parse${parserMethodDataType}(value);
					</#if>
				}

				return null;
			}
		</#list>
	</#if>

	<#if generateCRUD>
		@Override
		public ${schemaName} getItem(Long id) throws Exception {
			return ${getByIdJavaMethodSignature.methodName}(
			<#list getByIdJavaMethodSignature.javaMethodParameters as javaMethodParameter>
				<#if freeMarkerTool.isIdParameter(javaMethodParameter, schemaName)>
					id
				<#else>
					null
				</#if>

				<#sep>, </#sep>
			</#list>
			);
		}
	</#if>

	<#if generateGetPermissionCheckerMethods>
		protected String getPermissionCheckerActionsResourceName(Object id) throws Exception {
			return getPermissionCheckerResourceName(id);
		}

		protected Long getPermissionCheckerGroupId(Object id) throws Exception {
			throw new UnsupportedOperationException("This method needs to be implemented");
		}

		protected String getPermissionCheckerPortletName(Object id) throws Exception {
			throw new UnsupportedOperationException("This method needs to be implemented");
		}

		protected Long getPermissionCheckerResourceId(Object id) throws Exception {
			return GetterUtil.getLong(id);
		}

		protected String getPermissionCheckerResourceName(Object id) throws Exception {
			throw new UnsupportedOperationException("This method needs to be implemented");
		}

		protected Page<com.liferay.portal.vulcan.permission.Permission> toPermissionPage(Map<String, Map<String, String>> actions, long id, String resourceName, String roleNames) throws Exception {
			List<ResourceAction> resourceActions = resourceActionLocalService.getResourceActions(resourceName);

			if (Validator.isNotNull(roleNames)) {
				return Page.of(actions, _getPermissions(contextCompany.getCompanyId(), resourceActions, id, resourceName, StringUtil.split(roleNames)));
			}

			return Page.of(actions, _getPermissions(contextCompany.getCompanyId(), resourceActions, id, resourceName, null));
		}

		/**
	 	 * @see com.liferay.portal.vulcan.permission.PermissionUtil#getPermissions(long, List, long, String, String[])
	  	 */
		private Collection<Permission> _getPermissions(long companyId, List<ResourceAction> resourceActions, long resourceId, String resourceName, String[] roleNames) throws Exception {
			Map<String, Permission> permissions = new HashMap<>();

			int count = resourcePermissionLocalService.getResourcePermissionsCount(companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(resourceId));

			if (count == 0) {
				ResourceLocalServiceUtil.addResources(companyId, resourceId, 0, resourceName, String.valueOf(resourceId), false, true, true);
			}

			List<String> actionIds = transform(resourceActions, resourceAction -> resourceAction.getActionId());

			Set<ResourcePermission> resourcePermissions = new HashSet<>();

			resourcePermissions.addAll(resourcePermissionLocalService.getResourcePermissions(companyId, resourceName, ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId)));
			resourcePermissions.addAll(resourcePermissionLocalService.getResourcePermissions(companyId, resourceName, ResourceConstants.SCOPE_GROUP, String.valueOf(GroupThreadLocal.getGroupId())));
			resourcePermissions.addAll(resourcePermissionLocalService.getResourcePermissions(companyId, resourceName, ResourceConstants.SCOPE_GROUP_TEMPLATE, "0"));
			resourcePermissions.addAll(resourcePermissionLocalService.getResourcePermissions(companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(resourceId)));

			List<Resource> resources = transform(resourcePermissions, resourcePermission -> ResourceLocalServiceUtil.getResource(resourcePermission.getCompanyId(), resourcePermission.getName(), resourcePermission.getScope(), resourcePermission.getPrimKey()));

			Set<com.liferay.portal.kernel.model.Role> roles = new HashSet<>();

			if (roleNames != null) {
				for (String roleName: roleNames) {
					roles.add(roleLocalService.getRole(companyId, roleName));
				}
			}
			else {
				for (ResourcePermission resourcePermission : resourcePermissions) {
					com.liferay.portal.kernel.model.Role role = roleLocalService.getRole(resourcePermission.getRoleId());

					roles.add(role);
				}
			}

			for (com.liferay.portal.kernel.model.Role role : roles) {
				Set<String> actionsIdsSet = new HashSet<>();

				for (Resource resource : resources) {
					actionsIdsSet.addAll(resourcePermissionLocalService.getAvailableResourcePermissionActionIds(resource.getCompanyId(), resource.getName(), ResourceConstants.SCOPE_COMPANY, String.valueOf(resource.getCompanyId()), role.getRoleId(), actionIds));
					actionsIdsSet.addAll(resourcePermissionLocalService.getAvailableResourcePermissionActionIds(resource.getCompanyId(), resource.getName(), ResourceConstants.SCOPE_GROUP, String.valueOf(GroupThreadLocal.getGroupId()), role.getRoleId(), actionIds));
					actionsIdsSet.addAll(resourcePermissionLocalService.getAvailableResourcePermissionActionIds(resource.getCompanyId(), resource.getName(), ResourceConstants.SCOPE_GROUP_TEMPLATE, "0", role.getRoleId(), actionIds));
					actionsIdsSet.addAll(resourcePermissionLocalService.getAvailableResourcePermissionActionIds(resource.getCompanyId(), resource.getName(), resource.getScope(), resource.getPrimKey(), role.getRoleId(), actionIds));
				}

				if (actionsIdsSet.isEmpty()) {
					continue;
				}

				Permission permission = new Permission() {
					{
						actionIds = actionsIdsSet.toArray(new String[0]);
						roleName = role.getName();
					}
				};

				permissions.put(role.getName(), permission);
			}

			return permissions.values();
		}
	</#if>

	public void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage) {
		this.contextAcceptLanguage = contextAcceptLanguage;
	}

	<#if generateBatch>
		public void setContextBatchUnsafeBiConsumer(UnsafeBiConsumer<Collection<${javaDataType}>, UnsafeFunction<${javaDataType}, ${javaDataType}, Exception>, Exception> contextBatchUnsafeBiConsumer) {
			this.contextBatchUnsafeBiConsumer = contextBatchUnsafeBiConsumer;
		}

		public void setContextBatchUnsafeConsumer(UnsafeBiConsumer<Collection<${javaDataType}>, UnsafeConsumer<${javaDataType}, Exception>, Exception> contextBatchUnsafeConsumer) {
			this.contextBatchUnsafeConsumer = contextBatchUnsafeConsumer;
		}
	</#if>

	public void setContextCompany(com.liferay.portal.kernel.model.Company contextCompany) {
		this.contextCompany = contextCompany;
	}

	public void setContextHttpServletRequest(HttpServletRequest contextHttpServletRequest) {
		<#if !freeMarkerTool.isVersionCompatible(configYAML, 6)>
			if ((contextHttpServletRequest != null) && (contextHttpServletRequest.getAttribute(WebKeys.CTX) == null)) {
				contextHttpServletRequest.setAttribute(WebKeys.CTX, ServletContextPool.get(StringPool.BLANK));
			}
		</#if>

		this.contextHttpServletRequest = contextHttpServletRequest;
	}

	public void setContextHttpServletResponse(HttpServletResponse contextHttpServletResponse) {
		this.contextHttpServletResponse = contextHttpServletResponse;
	}

	public void setContextUriInfo(UriInfo contextUriInfo) {
		<#if freeMarkerTool.isVersionCompatible(configYAML, 7)>
			this.contextUriInfo = UriInfoUtil.getVulcanUriInfo(getApplicationPath(), contextUriInfo);
		<#else>
			this.contextUriInfo = contextUriInfo;
		</#if>
	}

	public void setContextUser(com.liferay.portal.kernel.model.User contextUser) {
		this.contextUser = contextUser;
	}

	public void setExpressionConvert(ExpressionConvert<com.liferay.portal.kernel.search.filter.Filter> expressionConvert) {
		this.expressionConvert = expressionConvert;
	}

	public void setFilterParserProvider(FilterParserProvider filterParserProvider) {
		this.filterParserProvider = filterParserProvider;
	}

	public void setGroupLocalService(GroupLocalService groupLocalService) {
		this.groupLocalService = groupLocalService;
	}

	public void setResourceActionLocalService(ResourceActionLocalService resourceActionLocalService) {
		this.resourceActionLocalService = resourceActionLocalService;
	}

	public void setResourcePermissionLocalService(ResourcePermissionLocalService resourcePermissionLocalService) {
		this.resourcePermissionLocalService = resourcePermissionLocalService;
	}

	public void setRoleLocalService(RoleLocalService roleLocalService) {
		this.roleLocalService = roleLocalService;
	}

	public void setSortParserProvider(SortParserProvider sortParserProvider) {
		this.sortParserProvider = sortParserProvider;
	}

	<#if freeMarkerTool.isVersionCompatible(configYAML, 7)>
		protected String getApplicationPath() {
			<#if configYAML.application??>
				return "${stringUtil.removeFirst(configYAML.application.baseURI, "/")}";
			<#else>
				return null;
			</#if>
		}
	</#if>

	<#if generateBatch>
		<#if freeMarkerTool.isVersionCompatible(configYAML, 2)>
			public void setVulcanBatchEngineExportTaskResource(VulcanBatchEngineExportTaskResource vulcanBatchEngineExportTaskResource) {
				this.vulcanBatchEngineExportTaskResource = vulcanBatchEngineExportTaskResource;
			}
		</#if>

		public void setVulcanBatchEngineImportTaskResource(VulcanBatchEngineImportTaskResource vulcanBatchEngineImportTaskResource) {
			this.vulcanBatchEngineImportTaskResource = vulcanBatchEngineImportTaskResource;
		}

		@Override
		public com.liferay.portal.kernel.search.filter.Filter toFilter(String filterString, Map<String, List<String>> multivaluedMap) {
			try {
				EntityModel entityModel = getEntityModel(multivaluedMap);

				FilterParser filterParser = filterParserProvider.provide(entityModel);

				com.liferay.portal.odata.filter.Filter oDataFilter = new com.liferay.portal.odata.filter.Filter(filterParser.parse(filterString));

				return expressionConvert.convert(oDataFilter.getExpression(), contextAcceptLanguage.getPreferredLocale(), entityModel);
			}
			catch (Exception exception) {
				_log.error("Invalid filter " + filterString, exception);

				return null;
			}
		}

		@Override
		public com.liferay.portal.kernel.search.Sort[] toSorts(String sortString) {
			if (Validator.isNull(sortString)) {
				return null;
			}

			try {
				SortParser sortParser = sortParserProvider.provide(getEntityModel(Collections.emptyMap()));

				if (sortParser == null) {
					return null;
				}

				com.liferay.portal.odata.sort.Sort oDataSort = new com.liferay.portal.odata.sort.Sort(sortParser.parse(sortString));

				List<SortField> sortFields = oDataSort.getSortFields();
				com.liferay.portal.kernel.search.Sort[] sorts = new com.liferay.portal.kernel.search.Sort[sortFields.size()];

				for (int i = 0; i < sortFields.size(); i++) {
					SortField sortField = sortFields.get(i);

					sorts[i] = new com.liferay.portal.kernel.search.Sort(sortField.getSortableFieldName(contextAcceptLanguage.getPreferredLocale()), !sortField.isAscending());
				}

				return sorts;
			}
			catch (Exception exception) {
				_log.error("Invalid sort " + sortString, exception);

				return new com.liferay.portal.kernel.search.Sort[0];
			}
		}
	</#if>

	protected Map<String, String> addAction(String actionName, com.liferay.portal.kernel.model.GroupedModel groupedModel, String methodName) {
		return ActionUtil.addAction(actionName, getClass(), groupedModel, methodName, contextScopeChecker, contextUriInfo);
	}

	protected Map<String, String> addAction(String actionName, Long id, String methodName, Long ownerId, String permissionName, Long siteId) {
		return ActionUtil.addAction(actionName, getClass(), id, methodName, contextScopeChecker, ownerId, permissionName, siteId, contextUriInfo);
	}

	protected Map<String, String> addAction(String actionName, Long id, String methodName, ModelResourcePermission modelResourcePermission) {
		return ActionUtil.addAction(actionName, getClass(), id, methodName, contextScopeChecker, modelResourcePermission, contextUriInfo);
	}

	protected Map<String, String> addAction(String actionName, String methodName, String permissionName, Long siteId) {
		return addAction(actionName, siteId, methodName, null, permissionName, siteId);
	}

	<#if generatePatchMethods>
		protected void preparePatch(${javaDataType} ${schemaVarName}, ${javaDataType} existing${schemaVarName?cap_first}) {
		}
	</#if>

	protected <T, R, E extends Throwable> List<R> transform(Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {
		return TransformUtil.transform(collection, unsafeFunction);
	}

	protected <T, R, E extends Throwable> R[] transform(T[] array, UnsafeFunction<T, R, E> unsafeFunction, Class<? extends R> clazz) {
		return TransformUtil.transform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] transformToArray(Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction, Class<? extends R> clazz) {
		return TransformUtil.transformToArray(collection, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> List<R> transformToList(T[] array, UnsafeFunction<T, R, E> unsafeFunction) {
		return TransformUtil.transformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> long[] transformToLongArray(Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {
		<#if freeMarkerTool.isVersionCompatible(configYAML, 2)>
			return TransformUtil.transformToLongArray(collection, unsafeFunction);
		<#else>
			try {
				return unsafeTransformToLongArray(collection, unsafeFunction);
			}
			catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
		</#if>
	}

	protected <T, R, E extends Throwable> List<R> unsafeTransform(Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) throws E {
		return TransformUtil.unsafeTransform(collection, unsafeFunction);
	}

	protected <T, R, E extends Throwable> R[] unsafeTransform(T[] array, UnsafeFunction<T, R, E> unsafeFunction, Class<? extends R> clazz) throws E {
		return TransformUtil.unsafeTransform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] unsafeTransformToArray(Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction, Class<? extends R> clazz) throws E {
		return TransformUtil.unsafeTransformToArray(collection, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> List<R> unsafeTransformToList(T[] array, UnsafeFunction<T, R, E> unsafeFunction) throws E {
		return TransformUtil.unsafeTransformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> long[] unsafeTransformToLongArray(Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) throws E {
		<#if freeMarkerTool.isVersionCompatible(configYAML, 2)>
			return TransformUtil.unsafeTransformToLongArray(collection, unsafeFunction);
		<#else>
			return (long[])_unsafeTransformToPrimitiveArray(collection, unsafeFunction, long[].class);
		</#if>
	}

	protected AcceptLanguage contextAcceptLanguage;

	<#if generateBatch>
		protected UnsafeBiConsumer<Collection<${javaDataType}>, UnsafeFunction<${javaDataType}, ${javaDataType}, Exception>, Exception> contextBatchUnsafeBiConsumer;
		protected UnsafeBiConsumer<Collection<${javaDataType}>, UnsafeConsumer<${javaDataType}, Exception>, Exception> contextBatchUnsafeConsumer;
	</#if>

	protected com.liferay.portal.kernel.model.Company contextCompany;
	protected HttpServletRequest contextHttpServletRequest;
	protected HttpServletResponse contextHttpServletResponse;
	protected Object contextScopeChecker;
	protected UriInfo contextUriInfo;
	protected com.liferay.portal.kernel.model.User contextUser;
	protected ExpressionConvert<com.liferay.portal.kernel.search.filter.Filter> expressionConvert;
	protected FilterParserProvider filterParserProvider;
	protected GroupLocalService groupLocalService;
	protected ResourceActionLocalService resourceActionLocalService;
	protected ResourcePermissionLocalService resourcePermissionLocalService;
	protected RoleLocalService roleLocalService;
	protected SortParserProvider sortParserProvider;

	<#if generateBatch>
		<#if freeMarkerTool.isVersionCompatible(configYAML, 2)>
			protected VulcanBatchEngineExportTaskResource vulcanBatchEngineExportTaskResource;
		</#if>

		protected VulcanBatchEngineImportTaskResource vulcanBatchEngineImportTaskResource;
	</#if>

	<#if !freeMarkerTool.isVersionCompatible(configYAML, 2)>
		private <T, R, E extends Throwable> Object _unsafeTransformToPrimitiveArray(Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction, Class<?> clazz) throws E {
			List<R> list = unsafeTransform(collection, unsafeFunction);

			Object array = clazz.cast(Array.newInstance(clazz.getComponentType(), list.size()));

			for (int i = 0; i < list.size(); i++) {
				Array.set(array, i, list.get(i));
			}

			return array;
		}
	</#if>

	private static final com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(Base${schemaName}ResourceImpl.class);

	<#if generateMultipartBodyClasses?has_content>
		<#list generateMultipartBodyClasses as javaMethodSignatureWithMultipartBody>
			private class ${stringUtil.upperCaseFirstLetter(javaMethodSignatureWithMultipartBody.methodName)}RequestBody {
				<#assign multipartBodySchemas = freeMarkerTool.getMultipartBodySchemas(javaMethodSignatureWithMultipartBody) />

				<#list multipartBodySchemas as schemaName, propertySchema>
					<#if stringUtil.equals(propertySchema.format, "binary") && stringUtil.equals(propertySchema.type, "string")>
						@io.swagger.v3.oas.annotations.media.Schema(
							description = "${stringUtil.upperCaseFirstLetter(schemaName)}", format = "binary", type = "string"
						)
						public String ${schemaName};
				   <#elseif stringUtil.equals(propertySchema.type, "array") && propertySchema.items.reference??>
						public ${freeMarkerTool.getReferenceName(propertySchema.items.reference)}[] ${schemaName};
					<#else>
						public ${stringUtil.upperCaseFirstLetter(schemaName)} ${schemaName};
					</#if>
				</#list>

			}
		</#list>
	</#if>

}

<#macro castParameters
	type
	value
>
	<#if stringUtil.equals(value, "assetLibraryId") || stringUtil.equals(value, "siteId")>
		(Long)parameters.get("${value}")
	<#elseif stringUtil.startsWith(type, "[L")>
		(

		<#if type?contains("java.lang.Boolean")>
			Boolean[]
		<#elseif type?contains("java.util.Date")>
			java.util.Date[]
		<#elseif type?contains("java.lang.Double")>
			Double[]
		<#elseif type?contains("java.lang.Integer")>
			Integer[]
		<#elseif type?contains("java.lang.Long")>
			Long[]
		<#else>
			String[]
		</#if>

		)parameters.get("${value}")
	<#elseif !stringUtil.startsWith(type, "java")>
		(${type})parameters.get("${value}")
	<#else>
		<#if type?contains("java.lang.Boolean")>
			<#assign parserMethodDataTypes = parserMethodDataTypes + ["Boolean"] />

			_parseBoolean(
		<#elseif type?contains("java.util.Date")>
			<#assign parserMethodDataTypes = parserMethodDataTypes + ["Date"] />

			_parseDate(
		<#elseif type?contains("java.lang.Double")>
			<#assign parserMethodDataTypes = parserMethodDataTypes + ["Double"] />

			_parseDouble(
		<#elseif type?contains("java.lang.Integer")>
			<#assign parserMethodDataTypes = parserMethodDataTypes + ["Integer"] />

			_parseInteger(
		<#elseif type?contains("java.lang.Long")>
			<#assign parserMethodDataTypes = parserMethodDataTypes + ["Long"] />

			_parseLong(
		</#if>

		(String)parameters.get("${value}")

		<#if !type?contains("java.lang.String")>
			)
		</#if>
	</#if>
</#macro>

<#macro getActions
	resourceId
	resourceName
	source
>
	HashMapBuilder.put(
		"get", addAction(ActionKeys.PERMISSIONS, "get${source}PermissionsPage", ${resourceName}, ${resourceId})
	).put(
		"replace", addAction(ActionKeys.PERMISSIONS, "put${source}PermissionsPage", ${resourceName}, ${resourceId})
	).build()
</#macro>

<#macro getDELETEBatchJavaMethodParameters
	javaMethodParameters
>
	<#list javaMethodParameters as javaMethodParameter>
		<#if stringUtil.equals(javaMethodParameter.parameterName, "externalReferenceCode") || stringUtil.equals(javaMethodParameter.parameterName, schemaVarName + "ExternalReferenceCode")>
			${schemaVarName}.getExternalReferenceCode()
		<#else>
			<@castParameters
				type = javaMethodParameter.parameterType
				value = javaMethodParameter.parameterName
			/>
		</#if>

		<#sep>, </#sep>
	</#list>
</#macro>

<#macro getGETBatchJavaMethodParameters
	javaMethodParameters
>
	<#list javaMethodParameters as javaMethodParameter>
		<#if stringUtil.equals(javaMethodParameter.parameterName, "aggregation")>
			null
		<#elseif stringUtil.equals(javaMethodParameter.parameterName, "filter") || stringUtil.equals(javaMethodParameter.parameterName, "pagination") || stringUtil.equals(javaMethodParameter.parameterName, "search") || stringUtil.equals(javaMethodParameter.parameterName, "sorts") || stringUtil.equals(javaMethodParameter.parameterName, "user")>
			${javaMethodParameter.parameterName}
		<#else>
			<@castParameters
				type = javaMethodParameter.parameterType
				value = javaMethodParameter.parameterName
			/>
		</#if>

		<#sep>, </#sep>
	</#list>
</#macro>

<#macro getPOSTBatchJavaMethodParameters
	javaMethodParameters
	schemaVarName
>
	<#list javaMethodParameters as javaMethodParameter>
		<#if stringUtil.equals(javaMethodParameter.parameterName, schemaVarName)>
			${schemaVarName}
		<#else>
			<@castParameters
				type = javaMethodParameter.parameterType
				value = javaMethodParameter.parameterName
			/>
		</#if>

		<#sep>, </#sep>
	</#list>
</#macro>

<#macro updateResourcePermissions
	groupId
	resourceId
	resourceName
>
	PermissionServiceUtil.checkPermission(${groupId}, ${resourceName}, ${resourceId});

	ModelPermissions modelPermissions = ModelPermissionsUtil.toModelPermissions(contextCompany.getCompanyId(), permissions, ${resourceId}, ${resourceName}, resourceActionLocalService, resourcePermissionLocalService, roleLocalService);

	Collection<String> roleNames = modelPermissions.getRoleNames();

	for (ResourcePermission resourcePermission : resourcePermissionLocalService.getResourcePermissions(contextCompany.getCompanyId(), ${resourceName}, ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(${resourceId}))) {
		com.liferay.portal.kernel.model.Role role = roleLocalService.fetchRole(resourcePermission.getRoleId());

		if ((role == null) || roleNames.contains(role.getName())) {
			continue;
		}

		for (ResourceAction resourceAction : resourceActionLocalService.getResourceActions(${resourceName})) {
			resourcePermissionLocalService.removeResourcePermission(contextCompany.getCompanyId(), ${resourceName}, ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(${resourceId}), role.getRoleId(), resourceAction.getActionId());
		}
	}

	resourcePermissionLocalService.updateResourcePermissions(contextCompany.getCompanyId(), ${groupId}, ${resourceName}, String.valueOf(${resourceId}), modelPermissions);

	return toPermissionPage(<#nested>, ${resourceId}, ${resourceName}, null);
</#macro>