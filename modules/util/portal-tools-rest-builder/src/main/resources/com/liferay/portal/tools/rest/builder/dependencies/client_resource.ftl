package ${configYAML.apiPackagePath}.client.resource.${escapedVersion};

import ${configYAML.apiPackagePath}.client.aggregation.Aggregation;

<#list globalEnumSchemas?keys as globalEnumSchemaName>
	import ${configYAML.apiPackagePath}.client.constant.${escapedVersion}.${globalEnumSchemaName};
</#list>

<#list allExternalSchemas?keys as externalSchemaName>
	import ${configYAML.apiPackagePath}.client.dto.${escapedVersion}.${externalSchemaName};
</#list>

<#list allSchemas?keys as schemaName>
	import ${configYAML.apiPackagePath}.client.dto.${escapedVersion}.${schemaName};
</#list>

import ${configYAML.apiPackagePath}.client.dto.${escapedVersion}.${schemaName};
import ${configYAML.apiPackagePath}.client.http.HttpInvoker;
import ${configYAML.apiPackagePath}.client.pagination.Page;
import ${configYAML.apiPackagePath}.client.pagination.Pagination;
import ${configYAML.apiPackagePath}.client.permission.Permission;
import ${configYAML.apiPackagePath}.client.problem.Problem;

<#list allExternalSchemas?keys as schemaName>
	import ${configYAML.apiPackagePath}.client.serdes.${escapedVersion}.${schemaName}SerDes;
</#list>

<#list allSchemas?keys as schemaName>
	import ${configYAML.apiPackagePath}.client.serdes.${escapedVersion}.${schemaName}SerDes;
</#list>

import jakarta.annotation.Generated;

import java.io.File;

import java.net.URL;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ${configYAML.author}
 * @generated
 */
@Generated("")
public interface ${schemaName}Resource {

	public static Builder builder() {
		return new Builder();
	}

	<#list freeMarkerTool.getResourceTestCaseJavaMethodSignatures(configYAML, openAPIYAML, schemaName) as javaMethodSignature>
		<#assign
			parameters = freeMarkerTool.getClientParameters(javaMethodSignature.javaMethodParameters, schemaName, schemaVarName)
		/>

		public ${javaMethodSignature.returnType?replace(".constant.", ".client.constant.")?replace(".dto.", ".client.dto.")?replace("com.liferay.portal.vulcan.aggregation.", "")?replace("com.liferay.portal.vulcan.pagination.", "")?replace("com.liferay.portal.vulcan.permission.", "")?replace("jakarta.ws.rs.core.Response", "void")} ${javaMethodSignature.methodName}(${parameters}) throws Exception;

		public HttpInvoker.HttpResponse ${javaMethodSignature.methodName}HttpResponse(${parameters}) throws Exception;
	</#list>

	public static class Builder {

		public Builder authentication(String login, String password) {
			_login = login;
			_password = password;

			return this;
		}

		public Builder bearerToken(String token) {
			return header("Authorization", "Bearer " + token);
		}

		public ${schemaName}Resource build() {
			return new ${schemaName}ResourceImpl(this);
		}

		public Builder contextPath(String contextPath) {
			_contextPath = contextPath;

			return this;
		}

		public Builder endpoint(String address, String scheme) {
			String[] addressParts = address.split(":");

			String host = addressParts[0];

			int port = 443;

			if (addressParts.length > 1) {
				String portString = addressParts[1];

				try {
					port = Integer.parseInt(portString);
				}
				catch (NumberFormatException numberFormatException) {
					throw new IllegalArgumentException("Unable to parse port from " + portString);
				}
			}

			return endpoint(host, port, scheme);
		}

		public Builder endpoint(String host, int port, String scheme) {
			_host = host;
			_port = port;
			_scheme = scheme;

			return this;
		}

		public Builder endpoint(URL url) {
			return endpoint(url.getHost(), url.getPort(), url.getProtocol());
		}

		public Builder header(String key, String value) {
			_headers.put(key, value);

			return this;
		}

		public Builder locale(Locale locale) {
			_locale = locale;

			return this;
		}

		public Builder parameter(String key, String value) {
			_parameters.put(key, value);

			return this;
		}

		public Builder parameters(String... parameters) {
			if ((parameters.length % 2) != 0) {
				throw new IllegalArgumentException(
					"Parameters length is not an even number");
			}

			for (int i = 0; i < parameters.length; i += 2) {
				String parameterName = String.valueOf(parameters[i]);
				String parameterValue = String.valueOf(parameters[i + 1]);

				_parameters.put(parameterName, parameterValue);
			}

			return this;
		}

		private Builder() {
		}

		private String _contextPath = "";
		private Map<String, String> _headers = new LinkedHashMap<>();
		private String _host = "localhost";
		private Locale _locale;
		private String _login;
		private String _password;
		private Map<String, String> _parameters = new LinkedHashMap<>();
		private int _port = 8080;
		private String _scheme = "http";

	}

	public static class ${schemaName}ResourceImpl implements ${schemaName}Resource {

		<#list freeMarkerTool.getResourceTestCaseJavaMethodSignatures(configYAML, openAPIYAML, schemaName) as javaMethodSignature>
			<#assign
				arguments = freeMarkerTool.getResourceTestCaseArguments(javaMethodSignature.javaMethodParameters)?replace("aggregation", "aggregations")?replace("filter", "filterString")?replace("sorts", "sortString")?replace("multipartBody", "${schemaVarName}, multipartFiles")
				parameters = freeMarkerTool.getClientParameters(javaMethodSignature.javaMethodParameters, schemaName, schemaVarName)
			/>

			public ${javaMethodSignature.returnType?replace(".constant.", ".client.constant.")?replace(".dto.", ".client.dto.")?replace("com.liferay.portal.vulcan.aggregation.", "")?replace("com.liferay.portal.vulcan.pagination.", "")?replace("com.liferay.portal.vulcan.permission.", "")?replace("jakarta.ws.rs.core.Response", "void")} ${javaMethodSignature.methodName}(${parameters}) throws Exception {
				HttpInvoker.HttpResponse httpResponse = ${javaMethodSignature.methodName}HttpResponse(${arguments});

				String content = httpResponse.getContent();

				if (httpResponse.getStatusCode() / 100 != 2) {
					_logger.log(Level.WARNING, "Unable to process HTTP response content: " + content);
					_logger.log(Level.WARNING, "HTTP response message: " + httpResponse.getMessage());
					_logger.log(Level.WARNING, "HTTP response status code: " + httpResponse.getStatusCode());

					Problem.ProblemException problemException = null;

					if (Objects.equals(httpResponse.getContentType(), "application/json")) {
						problemException = new Problem.ProblemException(Problem.toDTO(content));
					}
					else {
						_logger.log(Level.WARNING, "Unable to process content type: " + httpResponse.getContentType());

						Problem problem = new Problem();

						problem.setStatus(String.valueOf(httpResponse.getStatusCode()));

						problemException = new Problem.ProblemException(problem);
					}

					throw problemException;
				}
				else {
					_logger.fine("HTTP response content: " + content);
					_logger.fine("HTTP response message: " + httpResponse.getMessage());
					_logger.fine("HTTP response status code: " + httpResponse.getStatusCode());
				}

				<#if !javaMethodSignature.returnType?contains("jakarta.ws.rs.core.Response")>
					try {
						<#if javaMethodSignature.returnType?contains("Page<com.liferay.portal.vulcan.permission.Permission>")>
							return Page.of(content, Permission::toDTO);
						<#elseif javaMethodSignature.returnType?contains("Page<")>
							return Page.of(content, ${javaMethodSignature.returnType?keep_after_last('.', '')?replace('>', '')}SerDes::toDTO);
						<#elseif javaMethodSignature.returnType?ends_with("String")>
							return content;
						<#elseif stringUtil.equals(javaMethodSignature.returnType, "java.lang.Boolean") ||
								 stringUtil.equals(javaMethodSignature.returnType, "java.lang.Float") ||
								 stringUtil.equals(javaMethodSignature.returnType, "java.lang.Double") ||
								 stringUtil.equals(javaMethodSignature.returnType, "java.lang.Integer") ||
								 stringUtil.equals(javaMethodSignature.returnType, "java.lang.Long")>

							return ${javaMethodSignature.returnType}.valueOf(content);
						<#elseif stringUtil.equals(javaMethodSignature.returnType, "java.lang.Number")>
							return Double.valueOf(content);
						<#elseif stringUtil.equals(javaMethodSignature.returnType, "java.lang.Object")>
							return (Object)content;
						<#elseif stringUtil.equals(javaMethodSignature.returnType, "java.math.BigDecimal")>
							return new java.math.BigDecimal(content);
						<#elseif stringUtil.equals(javaMethodSignature.returnType, "java.util.Date")>
							return java.text.DateFormat.getInstance().parse(content);
						<#elseif !stringUtil.equals(javaMethodSignature.returnType, "void")>
							return ${javaMethodSignature.returnType?replace(".dto.", ".client.serdes.")}SerDes.toDTO(content);
						<#else>
							return;
						</#if>
					}
					catch (Exception e) {
						_logger.log(Level.WARNING, "Unable to process HTTP response: " + content, e);

						throw new Problem.ProblemException(Problem.toDTO(content));
					}
				</#if>
			}

			public HttpInvoker.HttpResponse ${javaMethodSignature.methodName}HttpResponse(${parameters}) throws Exception {
				HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

				<#if freeMarkerTool.hasHTTPMethod(javaMethodSignature, "delete", "patch", "post", "put")>
					<#if freeMarkerTool.hasRequestBodyMediaType(javaMethodSignature, "multipart/form-data") && freeMarkerTool.hasParameter(javaMethodSignature, "multipartBody")>
						httpInvoker.multipart();

						httpInvoker.part("${schemaVarName}", ${schemaName}SerDes.toJSON(${schemaVarName}));

						for (Map.Entry<String, File> entry : multipartFiles.entrySet()) {
							httpInvoker.part(entry.getKey(), entry.getValue());
						}
					<#else>
						<#assign
							bodyJavaMethodParameters = freeMarkerTool.getBodyJavaMethodParameters(javaMethodSignature)
							requestBodyMediaTypes = javaMethodSignature.getRequestBodyMediaTypes()
						/>

						<#if requestBodyMediaTypes?has_content>
							<#assign requestBodyMediaType = requestBodyMediaTypes[0] />
						<#else>
							<#assign requestBodyMediaType = "application/json" />
						</#if>

						<#if bodyJavaMethodParameters?has_content>
								<#list bodyJavaMethodParameters as javaMethodParameter>
									<#if javaMethodParameter?is_last>
										<#if javaMethodParameter.parameterType?starts_with("[L")>
											List<String> values = new ArrayList<>();

											for (${javaMethodParameter.parameterType?keep_after_last(".")?keep_before(";")} ${javaMethodParameter.parameterName?remove_ending("s")}Value : ${javaMethodParameter.parameterName}) {
												values.add(

												<#if javaMethodParameter.parameterType?contains("String")>
													"\"" + String.valueOf(${javaMethodParameter.parameterName?remove_ending("s")}Value) + "\""
												<#else>
													String.valueOf(${javaMethodParameter.parameterName?remove_ending("s")}Value)
												</#if>

												);
											}

											httpInvoker.body(values.toString(), "${requestBodyMediaType}");
										<#else>
											httpInvoker.body(${javaMethodParameter.parameterName}.toString(), "${requestBodyMediaType}");
										</#if>
									</#if>
								</#list>
						<#elseif freeMarkerTool.hasHTTPMethod(javaMethodSignature, "patch", "post", "put")>
							httpInvoker.body("[]", "${requestBodyMediaType}");
						</#if>
					</#if>
				</#if>

				if (_builder._locale != null) {
					httpInvoker.header("Accept-Language", _builder._locale.toLanguageTag());
				}

				for (Map.Entry<String, String> entry : _builder._headers.entrySet()) {
					httpInvoker.header(entry.getKey(), entry.getValue());
				}

				for (Map.Entry<String, String> entry : _builder._parameters.entrySet()) {
					httpInvoker.parameter(entry.getKey(), entry.getValue());
				}

				httpInvoker.httpMethod(HttpInvoker.HttpMethod.${freeMarkerTool.getHTTPMethod(javaMethodSignature.operation)?upper_case});

				<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
					<#if stringUtil.equals(javaMethodParameter.parameterType, "java.util.Date")>
						DateFormat liferayToJSONDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX");

						<#break>
					</#if>
				</#list>

				<#list javaMethodSignature.javaMethodParameters as javaMethodParameter>
					<#if stringUtil.equals(javaMethodParameter.parameterName, "aggregations")>
						if (aggregations != null) {
							httpInvoker.parameter("aggregationTerms", String.join(",", aggregations));
						}
					<#elseif stringUtil.equals(javaMethodParameter.parameterName, "filter")>
						if (filterString != null) {
							httpInvoker.parameter("filter", filterString);
						}
					<#elseif stringUtil.equals(javaMethodParameter.parameterName, "pagination")>
						if (pagination != null) {
							httpInvoker.parameter("page", String.valueOf(pagination.getPage()));
							httpInvoker.parameter("pageSize", String.valueOf(pagination.getPageSize()));
						}
					<#elseif stringUtil.equals(javaMethodParameter.parameterName, "sorts")>
						if (sortString != null) {
							httpInvoker.parameter("sort", sortString);
						}
					<#elseif freeMarkerTool.isQueryParameter(javaMethodParameter, javaMethodSignature.operation)>
						if (${javaMethodParameter.parameterName} != null) {
							<#if stringUtil.startsWith(javaMethodParameter.parameterType, "[")>
								for (int i = 0; i < ${javaMethodParameter.parameterName}.length; i++) {
									<#if stringUtil.equals(javaMethodParameter.parameterType, "java.util.Date")>
										httpInvoker.parameter("${javaMethodParameter.parameterName}", liferayToJSONDateFormat.format((${javaMethodParameter.parameterName}[i]));
									<#else>
										httpInvoker.parameter("${javaMethodParameter.parameterName}", String.valueOf(${javaMethodParameter.parameterName}[i]));
									</#if>
								}
							<#else>
								<#if stringUtil.equals(javaMethodParameter.parameterType, "java.util.Date")>
									httpInvoker.parameter("${javaMethodParameter.parameterName}", liferayToJSONDateFormat.format(${javaMethodParameter.parameterName}));
								<#else>
									httpInvoker.parameter("${javaMethodParameter.parameterName}", String.valueOf(${javaMethodParameter.parameterName}));
								</#if>
							</#if>
						}
					</#if>
				</#list>

				httpInvoker.path(_builder._scheme + "://" + _builder._host + ":" + _builder._port + _builder._contextPath + "/o${configYAML.application.baseURI}/${openAPIYAML.info.version}${javaMethodSignature.path}");

				<#list javaMethodSignature.pathJavaMethodParameters as javaMethodParameter>
					httpInvoker.path("${javaMethodParameter.parameterName}", ${javaMethodParameter.parameterName});
				</#list>

				if ((_builder._login != null) && (_builder._password != null)) {
					httpInvoker.userNameAndPassword(_builder._login + ":" + _builder._password);
				}

				return httpInvoker.invoke();
			}
		</#list>

		private ${schemaName}ResourceImpl(Builder builder) {
			_builder = builder;
		}

		private static final Logger _logger = Logger.getLogger(${schemaName}Resource.class.getName());

		private Builder _builder;

	}

}