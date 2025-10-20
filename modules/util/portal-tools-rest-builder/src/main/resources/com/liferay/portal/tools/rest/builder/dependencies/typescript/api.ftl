import {ObjectSerializer} from '../utils/SerDes';

<#if importClasses??>
	<#list importClasses?sort as import>
		import {${import}} from '../models/${import}';
	</#list>
</#if>

/**
 * @author ${configYAML.author}
 * @generated
 */

export class ${className} {
	protected _basePath: string;
	protected _defaultHeaders: any = {};

	constructor(basePath?: string) {
		if (basePath) {
			this._basePath = basePath;
		}
	}

	set defaultHeaders(defaultHeaders: any) {
		this._defaultHeaders = defaultHeaders;
	}

	<#list operationDatas?sort_by("operationId") as operationData>
		/**
		 * ${operationData.description!}
		 <#if operationData.parameters??>
		 	<#list operationData.parameters as parameter>
				 * @param ${parameter.name}
			 </#list>
		 </#if>
		 <#if operationData.bodyParameters?has_content>
		 	<#if (operationData.bodyParameters?keys?size == 1)>
			 	<#list operationData.bodyParameters?keys as requestBodyContentType>
					<#assign firstRequestBodyContentType = requestBodyContentType />
			 	</#list>
			 	<#list operationData.bodyParameters[firstRequestBodyContentType] as bodyParameter>
				 	* @param ${bodyParameter.name}
			 	</#list>
		 	<#else>
		 		* @param requestBody Request body that can be one of multiple content types
		 	</#if>
		 </#if>
		 * @param headers Optional custom request headers
		 */
		public async ${operationData.operationId}<#if operationData.bodyParameters?has_content && (operationData.bodyParameters?keys?size > 1)>WithContentType</#if>(
			<#if operationData.parameters??>
				<#list operationData.parameters as parameter>
					<#if parameter.required>
						${parameter.name}: ${parameter.dataType},
					</#if>
				</#list>
			</#if>
			<#if operationData.bodyParameters?has_content>
				<#if (operationData.bodyParameters?keys?size == 1)>
					<#list operationData.bodyParameters[firstRequestBodyContentType] as bodyParameter>
						${bodyParameter.name}${bodyParameter.required?then("", "?")}: ${bodyParameter.dataType},
					</#list>
				<#else>
					requestBody:
						<#list operationData.bodyParameters?keys?sort as requestBodyContentType>
							{
								parameters: {
									<#list operationData.bodyParameters[requestBodyContentType] as bodyParameter>
										${bodyParameter.name}${bodyParameter.required?then("", "?")}: ${bodyParameter.dataType}<#if bodyParameter?has_next>,</#if>
									</#list>
								},
								type: "${requestBodyContentType}"
							}
							<#if requestBodyContentType?has_next>
								|
							<#else>
								,
							</#if>
						</#list>
				</#if>
			</#if>
			<#if operationData.parameters??>
				<#list operationData.parameters as parameter>
					<#if !parameter.required>
						${parameter.name}?: ${parameter.dataType},
					</#if>
				</#list>
			</#if>
			headers?: {[name: string]: string},
		): Promise<{
			<#if operationData.returnDataType??>
				body: ${operationData.returnDataType};
			<#else>
				body?: any;
			</#if>
			response: Response;
		}> {
			<#if operationData.bodyParameters?has_content>
				let body;
				<#if (operationData.bodyParameters?keys?size > 1)>
					<#assign hasMultipartContentType = false />
					<#list operationData.bodyParameters?keys?sort as requestBodyContentType>
						<#if requestBodyContentType == "multipart/form-data">
							<#assign hasMultipartContentType = true />
						</#if>
						if (requestBody.type === "${requestBodyContentType}") {
							<#if requestBodyContentType == "multipart/form-data">
								const formData = new FormData();
								<#list operationData.bodyParameters[requestBodyContentType] as bodyParameter>
									<#if stringUtil.equals(bodyParameter.dataType, "File")>
										formData.append("${bodyParameter.name}", requestBody.parameters.${bodyParameter.name});
									<#else>
										formData.append("${bodyParameter.name}", JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.${bodyParameter.name}, "${bodyParameter.dataType}")));
									</#if>
								</#list>
								body = formData;
							<#else>
								body = JSON.stringify(ObjectSerializer.serialize(requestBody.parameters.${operationData.bodyParameters[requestBodyContentType][0].name}, "${operationData.bodyParameters[requestBodyContentType][0].dataType}"));
							</#if>
						}
					</#list>
				<#else>
					<#if firstRequestBodyContentType == "multipart/form-data">
						const formData = new FormData();
						<#list operationData.bodyParameters[firstRequestBodyContentType] as bodyParameter>
							<#if stringUtil.equals(bodyParameter.dataType, "File")>
								formData.append("${bodyParameter.name}", ${bodyParameter.name});
							<#else>
								formData.append("${bodyParameter.name}", JSON.stringify(ObjectSerializer.serialize(${bodyParameter.name}, "${bodyParameter.dataType}")));
							</#if>
						</#list>
						body = formData;
					<#else>
						body = JSON.stringify(ObjectSerializer.serialize(${operationData.bodyParameters[firstRequestBodyContentType][0].name}, "${operationData.bodyParameters[firstRequestBodyContentType][0].dataType}"));
					</#if>
				</#if>
			</#if>

			const path = this._basePath + "${operationData.path}"
				<#list operationData.parameters as parameter>
					<#if stringUtil.equals(parameter.type, "path")>
						.replace("{${parameter.name}}",encodeURIComponent(${parameter.name}))
					</#if>
				</#list>;

			const queryParameters: any = {};
			<#if operationData.parameters??>
				<#list operationData.parameters as parameter>
					<#if parameter.required>

						if (${parameter.name} === null || ${parameter.name} === undefined) {
							throw new Error("Required parameter ${parameter.name} was null or undefined when calling ${operationData.operationId}.");
						}
					</#if>
					<#if stringUtil.equals(parameter.type, "query")>

						if (${parameter.name} !== undefined) {
							queryParameters["${parameter.name}"] = ObjectSerializer.serialize(${parameter.name}, "${parameter.dataType}");
						}
					</#if>
				</#list>
			</#if>

			const queryString = Object.keys(queryParameters).length ?
				"?" + new URLSearchParams(queryParameters).toString() :
					"";

			const response = await fetch(path + queryString, {
				<#if operationData.bodyParameters?has_content>
					body: body,
				</#if>
				headers:
					Object.assign({}, this._defaultHeaders
					<#if operationData.responseContentTypes?? && operationData.responseContentTypes?has_content>
						,{
							<#if operationData.responseContentTypes?seq_contains("application/json")>
								Accept: "application/json"
							<#else>
								Accept: "${operationData.responseContentTypes[0]}"
							</#if>
						}
					</#if>
					<#if operationData.bodyParameters?has_content>
						<#if (operationData.bodyParameters?keys?size > 1)>
							<#if hasMultipartContentType>
								,(requestBody.type !== "multipart/form-data") ?
									{"Content-Type": requestBody.type} : {}
							<#else>
								,{"Content-Type": requestBody.type}
							</#if>
						<#elseif firstRequestBodyContentType != "multipart/form-data">
							,{"Content-Type": ${firstRequestBodyContentType}}
						</#if>
					</#if>
					,headers || {}
					),
				method: "${operationData.httpMethod}",
			});

			if (response.ok) {
				const contentType = response.headers.get("content-type") || "";

				<#if operationData.returnDataType??>
					if (contentType.includes("application/json")) {
						return {body: ObjectSerializer.deserialize(await response.json(), "${operationData.returnDataType}"), response};
					}
					else {
						return {body: await response.text() as any, response};
					}
				<#else>
					if (contentType.includes("application/json")) {
						return {body: await response.json(), response};
					}
					else {
						return {body: await response.text(), response};
					}
				</#if>
			}
			else {
				throw new Error("HTTP Error " + response.status + ": " + response.statusText + ". " + await response.text());
			}
		}

		<#if operationData.bodyParameters?has_content && (operationData.bodyParameters?keys?size > 1)>
			<#list operationData.bodyParameters?keys as requestBodyContentType>
				<#if requestBodyContentType == "application/json">
					/**
					 * ${operationData.description!} - Default method for JSON body
					 <#if operationData.parameters??>
						 <#list operationData.parameters as parameter>
							 * @param ${parameter.name}
						 </#list>
					 </#if>
					 <#list operationData.bodyParameters[requestBodyContentType] as bodyParameter>
						 * @param ${bodyParameter.name}
					 </#list>
					 */
					public async ${operationData.operationId}(
						<#if operationData.parameters??>
							<#list operationData.parameters as parameter>
								<#if parameter.required>
									${parameter.name}: ${parameter.dataType},
								</#if>
							</#list>
						</#if>
						<#list operationData.bodyParameters[requestBodyContentType] as bodyParameter>
							${bodyParameter.name}${bodyParameter.required?then("", "?")}: ${bodyParameter.dataType},
						</#list>
						<#if operationData.parameters??>
							<#list operationData.parameters as parameter>
								<#if !parameter.required>
									${parameter.name}?: ${parameter.dataType},
								</#if>
							</#list>
						</#if>
						headers?: {[name: string]: string}
					): Promise<{
						<#if operationData.returnDataType??>
							body: ${operationData.returnDataType};
						<#else>
							body?: any;
						</#if>
						response: Response;
					}> {
						return this.${operationData.operationId}WithContentType(
							<#if operationData.parameters??>
								<#list operationData.parameters as parameter>
									<#if parameter.required>
										${parameter.name},
									</#if>
								</#list>
							</#if>
							{
								parameters: {
									<#list operationData.bodyParameters[requestBodyContentType] as bodyParameter>
										${bodyParameter.name}: ${bodyParameter.name}<#if bodyParameter?has_next>,</#if>
									</#list>
								},
								type: "application/json"
							},
							<#if operationData.parameters??>
								<#list operationData.parameters as parameter>
									<#if !parameter.required>
										${parameter.name},
									</#if>
								</#list>
							</#if>
							headers
						);
					}
				</#if>
			</#list>
		</#if>
	</#list>
}