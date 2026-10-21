package ${configYAML.apiPackagePath}.internal.resource.${escapedVersion};

import com.liferay.portal.vulcan.resource.OpenAPIResource;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import ${configYAML.javaEEPackage}.annotation.Generated;

import ${configYAML.javaEEPackage}.servlet.http.HttpServletRequest;

import ${configYAML.javaEEPackage}.ws.rs.GET;
import ${configYAML.javaEEPackage}.ws.rs.Path;
import ${configYAML.javaEEPackage}.ws.rs.PathParam;
import ${configYAML.javaEEPackage}.ws.rs.Produces;
import ${configYAML.javaEEPackage}.ws.rs.core.Context;
import ${configYAML.javaEEPackage}.ws.rs.core.MediaType;
import ${configYAML.javaEEPackage}.ws.rs.core.Response;
import ${configYAML.javaEEPackage}.ws.rs.core.UriInfo;

import java.lang.reflect.Method;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author ${configYAML.author}
 * @generated
 */
@Component(
	<#if configYAML.liferayEnterpriseApp>enabled = false,</#if>
	properties = "OSGI-INF/liferay/rest/${escapedVersion}/openapi.properties",
	service = OpenAPIResourceImpl.class
)
<#if openAPIYAML.info.featureFlag?has_content>
	@com.liferay.portal.vulcan.feature.flag.FeatureFlag("${openAPIYAML.info.featureFlag}")
</#if>
@Generated("")
@OpenAPIDefinition(
	info = @Info(
		<#if openAPIYAML.info?? && openAPIYAML.info.description??>
			description = "${openAPIYAML.info.description}",
		</#if>
		<#if configYAML.licenseName?? && configYAML.licenseURL??>
			license = @License(name = "${configYAML.licenseName}", url = "${configYAML.licenseURL}"),
		</#if>

		title = "${openAPIYAML.info.title}",
		version = "${openAPIYAML.info.version}"
	)
)
<#if configYAML.application??>
	@Path("/${openAPIYAML.info.version}")
</#if>
public class OpenAPIResourceImpl {

	@GET
	@Path("/openapi.{type:json|yaml}")
	@Produces({MediaType.APPLICATION_JSON, "application/yaml"})
	public Response getOpenAPI(@Context HttpServletRequest httpServletRequest, @PathParam("type") String type, @Context UriInfo uriInfo) throws Exception {
		Class<? extends OpenAPIResource> clazz = _openAPIResource.getClass();

		try {
			Method method = clazz.getMethod("getOpenAPI", HttpServletRequest.class, Set.class, String.class, UriInfo.class);

			return (Response)method.invoke(_openAPIResource, httpServletRequest, _resourceClasses, type, uriInfo);
		}
		catch (NoSuchMethodException noSuchMethodException1) {
			try {
				Method method = clazz.getMethod("getOpenAPI", Set.class, String.class, UriInfo.class);

				return (Response)method.invoke(_openAPIResource, _resourceClasses, type, uriInfo);
			}
			catch (NoSuchMethodException noSuchMethodException2) {
				return _openAPIResource.getOpenAPI(_resourceClasses, type);
			}
		}
	}

	@Reference
	private OpenAPIResource _openAPIResource;

	private final Set<Class<?>> _resourceClasses = new HashSet<Class<?>>() {
		{
			<#list freeMarkerTool.getAllSchemas(null, openAPIYAML, freeMarkerTool.getSchemas(openAPIYAML))?keys as schemaName>
				<#assign javaMethodSignatures = freeMarkerTool.getResourceJavaMethodSignatures(configYAML, openAPIYAML, schemaName) />

				<#if javaMethodSignatures?has_content>
					add(${schemaName}ResourceImpl.class);
				</#if>
			</#list>

			add(OpenAPIResourceImpl.class);
		}
	};

}