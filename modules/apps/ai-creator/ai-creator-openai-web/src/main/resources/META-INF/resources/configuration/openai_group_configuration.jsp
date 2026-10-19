<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AICreatorOpenAIGroupConfigurationDisplayContext aiCreatorOpenAIGroupConfigurationDisplayContext = (AICreatorOpenAIGroupConfigurationDisplayContext)request.getAttribute(AICreatorOpenAIGroupConfigurationDisplayContext.class.getName());

boolean aiCreatorFeatureFlagEnabled = FeatureFlagManagerUtil.isEnabled(themeDisplay.getCompanyId(), "LPD-93786");
boolean companyChatGPTEnabled = aiCreatorOpenAIGroupConfigurationDisplayContext.isCompanyChatGPTEnabled();
boolean companyOpenAIImageGenerationEnabled = aiCreatorOpenAIGroupConfigurationDisplayContext.isCompanyOpenAIImageGenerationEnabled();
%>

<liferay-util:html-top
	outputKey="com.liferay.ai.creator.openai.web#/configuration/openai_group_configuration.jsp"
>
	<aui:link hashedFile="<%= true %>" href="ai-creator-openai-web/css/configuration.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<clay:content-row>
	<clay:content-col
		expand="<%= true %>"
	>
		<c:choose>
			<c:when test="<%= aiCreatorFeatureFlagEnabled && !companyChatGPTEnabled && !companyOpenAIImageGenerationEnabled %>">
				<clay:alert
					message="to-enable-openai-in-this-site,-it-must-also-be-enabled-from-instance-settings"
				/>
			</c:when>
			<c:when test="<%= aiCreatorFeatureFlagEnabled && !companyChatGPTEnabled && companyOpenAIImageGenerationEnabled %>">
				<clay:alert
					message="to-enable-chatgpt-for-this-site,-first-enable-it-for-your-instance"
				/>
			</c:when>
			<c:when test="<%= (!aiCreatorFeatureFlagEnabled || companyChatGPTEnabled) && !companyOpenAIImageGenerationEnabled %>">
				<clay:alert
					message="to-enable-openai-image-generation-for-this-site,-first-enable-it-for-your-instance"
				/>
			</c:when>
		</c:choose>
	</clay:content-col>
</clay:content-row>

<clay:content-row>
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
		<aui:input label="api-key" name="apiKey" type="text" value="<%= aiCreatorOpenAIGroupConfigurationDisplayContext.getAPIKey() %>" />
	</clay:content-col>
</clay:content-row>

<clay:content-row
	cssClass="c-mb-4"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<aui:input helpMessage="image-model-site-help" label="image-model" name="imageModel" type="text" value="<%= aiCreatorOpenAIGroupConfigurationDisplayContext.getImageModel() %>" />
	</clay:content-col>
</clay:content-row>

<clay:content-row
	cssClass="c-mb-4"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<liferay-ui:error key="imageSizesInvalid" message='<%= LanguageUtil.format(request, "each-image-size-must-be-a-width-and-height-in-pixels,-separated-by-x-(e.g.-x)", new Object[] {"x", "1536x1024"}, false) %>' />

		<aui:input helpMessage='<%= StringBundler.concat(LanguageUtil.get(request, "image-sizes-site-help"), StringPool.SPACE, LanguageUtil.format(request, "each-image-size-must-be-a-width-and-height-in-pixels,-separated-by-x-(e.g.-x)", new Object[] {"x", "1536x1024"}, false)) %>' label="image-sizes" name="imageSizes" type="text" value="<%= aiCreatorOpenAIGroupConfigurationDisplayContext.getImageSizes() %>" />
	</clay:content-col>
</clay:content-row>

<c:if test="<%= aiCreatorFeatureFlagEnabled %>">
	<clay:content-row>
		<clay:content-col
			cssClass="ai-creator-config-checkbox"
			expand="<%= true %>"
		>
			<c:choose>
				<c:when test="<%= companyChatGPTEnabled %>">
					<clay:checkbox
						checked="<%= aiCreatorOpenAIGroupConfigurationDisplayContext.isChatGPTEnabled() %>"
						id='<%= liferayPortletResponse.getNamespace() + "enableChatGPT" %>'
						label='<%= LanguageUtil.get(request, "enable-chatgpt-to-create-content") %>'
						name='<%= liferayPortletResponse.getNamespace() + "enableChatGPT" %>'
					/>
				</c:when>
				<c:otherwise>
					<clay:checkbox
						checked="<%= false %>"
						disabled="<%= true %>"
						id='<%= liferayPortletResponse.getNamespace() + "enableChatGPT" %>'
						label='<%= LanguageUtil.get(request, "enable-chatgpt-to-create-content") %>'
						name='<%= liferayPortletResponse.getNamespace() + "enableChatGPT" %>'
					/>
				</c:otherwise>
			</c:choose>
		</clay:content-col>
	</clay:content-row>
</c:if>

<clay:content-row
	cssClass="c-mt-5"
>
	<clay:content-col
		cssClass="ai-creator-config-checkbox"
		expand="<%= true %>"
	>
		<c:choose>
			<c:when test="<%= companyOpenAIImageGenerationEnabled %>">
				<clay:checkbox
					checked="<%= aiCreatorOpenAIGroupConfigurationDisplayContext.isOpenAIImageGenerationEnabled() %>"
					id='<%= liferayPortletResponse.getNamespace() + "enableOpenAIImageGeneration" %>'
					label='<%= LanguageUtil.get(request, "enable-openai-image-generation") %>'
					name='<%= liferayPortletResponse.getNamespace() + "enableOpenAIImageGeneration" %>'
				/>
			</c:when>
			<c:otherwise>
				<clay:checkbox
					checked="<%= false %>"
					disabled="<%= true %>"
					id='<%= liferayPortletResponse.getNamespace() + "enableOpenAIImageGeneration" %>'
					label='<%= LanguageUtil.get(request, "enable-openai-image-generation") %>'
					name='<%= liferayPortletResponse.getNamespace() + "enableOpenAIImageGeneration" %>'
				/>
			</c:otherwise>
		</c:choose>
	</clay:content-col>
</clay:content-row>

<%@ include file="/configuration/error_ai_creator_openai_client_exception.jspf" %>