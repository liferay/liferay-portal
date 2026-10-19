<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AICreatorOpenAICompanyConfigurationDisplayContext aiCreatorOpenAICompanyConfigurationDisplayContext = (AICreatorOpenAICompanyConfigurationDisplayContext)request.getAttribute(AICreatorOpenAICompanyConfigurationDisplayContext.class.getName());
%>

<liferay-util:html-top
	outputKey="com.liferay.ai.creator.openai.web#/configuration/openai_company_configuration.jsp"
>
	<aui:link hashedFile="<%= true %>" href="ai-creator-openai-web/css/configuration.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<clay:content-row
	cssClass="c-mt-4"
>
	<clay:content-col>
		<span>
			<liferay-ui:message key="set-the-api-key-for-authentication" />

			<clay:link
				href="https://platform.openai.com/docs/api-reference/authentication"
				label="how-do-i-get-an-api-key"
				target="_blank"
			/>
		</span>
	</clay:content-col>
</clay:content-row>

<clay:content-row
	cssClass="c-my-4"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<aui:input label="api-key" name="apiKey" type="text" value="<%= aiCreatorOpenAICompanyConfigurationDisplayContext.getAPIKey() %>" />
	</clay:content-col>
</clay:content-row>

<clay:content-row
	cssClass="c-mb-4"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<aui:input helpMessage="image-model-help" label="image-model" name="imageModel" type="text" value="<%= aiCreatorOpenAICompanyConfigurationDisplayContext.getImageModel() %>" />
	</clay:content-col>
</clay:content-row>

<clay:content-row
	cssClass="c-mb-4"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<liferay-ui:error key="imageSizesInvalid" message='<%= LanguageUtil.format(request, "each-image-size-must-be-a-width-and-height-in-pixels,-separated-by-x-(e.g.-x)", new Object[] {"x", "1536x1024"}, false) %>' />

		<aui:input helpMessage='<%= StringBundler.concat(LanguageUtil.get(request, "image-sizes-help"), StringPool.SPACE, LanguageUtil.format(request, "each-image-size-must-be-a-width-and-height-in-pixels,-separated-by-x-(e.g.-x)", new Object[] {"x", "1536x1024"}, false)) %>' label="image-sizes" name="imageSizes" type="text" value="<%= aiCreatorOpenAICompanyConfigurationDisplayContext.getImageSizes() %>" />
	</clay:content-col>
</clay:content-row>

<c:if test='<%= FeatureFlagManagerUtil.isEnabled(themeDisplay.getCompanyId(), "LPD-93786") %>'>
	<clay:content-row>
		<clay:content-col
			cssClass="ai-creator-config-checkbox"
			expand="<%= true %>"
		>
			<clay:checkbox
				checked="<%= aiCreatorOpenAICompanyConfigurationDisplayContext.isChatGPTEnabled() %>"
				id='<%= liferayPortletResponse.getNamespace() + "enableChatGPT" %>'
				label='<%= LanguageUtil.get(request, "enable-chatgpt-to-create-content") %>'
				name='<%= liferayPortletResponse.getNamespace() + "enableChatGPT" %>'
			/>
		</clay:content-col>
	</clay:content-row>
</c:if>

<clay:content-row
	cssClass="ai-creator-config-checkbox c-my-5"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<clay:checkbox
			checked="<%= aiCreatorOpenAICompanyConfigurationDisplayContext.isOpenAIImageGenerationEnabled() %>"
			id='<%= liferayPortletResponse.getNamespace() + "enableOpenAIImageGeneration" %>'
			label='<%= LanguageUtil.get(request, "enable-openai-image-generation") %>'
			name='<%= liferayPortletResponse.getNamespace() + "enableOpenAIImageGeneration" %>'
		/>
	</clay:content-col>
</clay:content-row>

<%@ include file="/configuration/error_ai_creator_openai_client_exception.jspf" %>