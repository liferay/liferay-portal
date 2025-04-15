package ${configYAML.apiPackagePath}.resource.${escapedVersion};

<#list globalEnumSchemas?keys as globalEnumSchemaName>
	import ${configYAML.apiPackagePath}.constant.${escapedVersion}.${globalEnumSchemaName};
</#list>

<#list allSchemas?keys as schemaName>
	import ${configYAML.apiPackagePath}.dto.${escapedVersion}.${schemaName};
</#list>

import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
<#if configYAML.application??>
 * To access this resource, run:
 *
 *     curl -u your@email.com:yourpassword -D - http://localhost:8080/o${configYAML.application.baseURI}/${openAPIYAML.info.version}
 *
</#if>
 * @author ${configYAML.author}
 * @generated
 */
<#if configYAML.changeTrackingEnabled>
	@CTAware
</#if>
@Generated("")
@ProviderType
public interface ${schemaName}Resource {

	<#assign
		javaDataType = freeMarkerTool.getJavaDataType(configYAML, openAPIYAML, schemaName)!""
		javaMethodSignatures = freeMarkerTool.getResourceJavaMethodSignatures(configYAML, openAPIYAML, schemaName)
		generateBatch = freeMarkerTool.generateBatch(configYAML, javaDataType, javaMethodSignatures, schemaName)
	/>

	<#list javaMethodSignatures as javaMethodSignature>
		public ${javaMethodSignature.returnType} ${javaMethodSignature.methodName}(${freeMarkerTool.getResourceParameters(configYAML, javaMethodSignature.javaMethodParameters, javaMethodSignature.operation, allSchemas, false)}) throws Exception;
	</#list>

	public default void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage) {
	}

	public void setContextCompany(com.liferay.portal.kernel.model.Company contextCompany);

	public default void setContextHttpServletRequest(
		HttpServletRequest contextHttpServletRequest) {
	}

	public default void setContextHttpServletResponse(
		HttpServletResponse contextHttpServletResponse) {
	}

	public default void setContextUriInfo(UriInfo contextUriInfo) {
	}

	public void setContextUser(com.liferay.portal.kernel.model.User contextUser);

	public void setExpressionConvert(ExpressionConvert<com.liferay.portal.kernel.search.filter.Filter> expressionConvert);

	public void setFilterParserProvider(FilterParserProvider filterParserProvider);

	public void setGroupLocalService(GroupLocalService groupLocalService);

	public void setResourceActionLocalService(ResourceActionLocalService resourceActionLocalService);

	public void setResourcePermissionLocalService(ResourcePermissionLocalService resourcePermissionLocalService);

	public void setRoleLocalService(RoleLocalService roleLocalService);

	public void setSortParserProvider(SortParserProvider sortParserProvider);

	<#if generateBatch>
		<#if freeMarkerTool.isVersionCompatible(configYAML, 2)>
			public void setVulcanBatchEngineExportTaskResource(VulcanBatchEngineExportTaskResource vulcanBatchEngineExportTaskResource);
		</#if>

		public void setVulcanBatchEngineImportTaskResource(VulcanBatchEngineImportTaskResource vulcanBatchEngineImportTaskResource);
	</#if>

	public default com.liferay.portal.kernel.search.filter.Filter toFilter(String filterString) {
		return toFilter(filterString, Collections.<String, List<String>>emptyMap());
	}

	public default com.liferay.portal.kernel.search.filter.Filter toFilter(String filterString, Map<String, List<String>> multivaluedMap) {
		return null;
	}

	public default com.liferay.portal.kernel.search.Sort[] toSorts(String sortsString) {
		return new com.liferay.portal.kernel.search.Sort[0];
	}

	@ProviderType
	public interface Builder {

		public ${schemaName}Resource build();

		public Builder checkPermissions(boolean checkPermissions);

		public Builder httpServletRequest(HttpServletRequest httpServletRequest);

		public Builder httpServletResponse(HttpServletResponse httpServletResponse);

		public Builder preferredLocale(Locale preferredLocale);

		public Builder uriInfo(UriInfo uriInfo);

		public Builder user(com.liferay.portal.kernel.model.User user);

	}

	@ProviderType
	public interface Factory {

		public Builder create();

	}

}