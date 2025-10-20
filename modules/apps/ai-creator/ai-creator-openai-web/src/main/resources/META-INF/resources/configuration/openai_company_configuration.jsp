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

<clay:content-row
	cssClass="ai-creator-config-checkbox c-my-5"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<clay:checkbox
			checked="<%= aiCreatorOpenAICompanyConfigurationDisplayContext.isDALLEEnabled() %>"
			id='<%= liferayPortletResponse.getNamespace() + "enableDALLE" %>'
			label='<%= LanguageUtil.get(request, "enable-dalle-to-create-images") %>'
			name='<%= liferayPortletResponse.getNamespace() + "enableDALLE" %>'
		/>
	</clay:content-col>
</clay:content-row>

<%@ include file="/configuration/error_ai_creator_openai_client_exception.jspf" %>