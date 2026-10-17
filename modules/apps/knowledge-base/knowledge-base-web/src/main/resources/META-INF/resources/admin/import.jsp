<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
long parentKBFolderId = ParamUtil.getLong(request, "parentKBFolderId");

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);
portletDisplay.setURLBackTitle(portletDisplay.getTitle());

renderResponse.setTitle(LanguageUtil.get(resourceBundle, "import"));
%>

<portlet:actionURL name="/knowledge_base/import_file" var="importFileURL">
	<portlet:param name="redirect" value="<%= redirect %>" />
</portlet:actionURL>

<clay:container-fluid
	size="lg"
>
	<aui:form action="<%= importFileURL %>" class="lfr-dynamic-form" enctype="multipart/form-data" method="post" name="fm">
		<aui:input name="mvcPath" type="hidden" value="/admin/import.jsp" />
		<aui:input name="parentKBFolderId" type="hidden" value="<%= String.valueOf(parentKBFolderId) %>" />

		<div class="mt-4">
			<liferay-ui:error exception="<%= KBArticleImportException.MustHaveACategory.class %>">
				<%= LanguageUtil.format(request, "an-unexpected-error-occurred-while-importing-articles-x", LanguageUtil.get(request, "there-are-one-or-more-mandatory-vocabularies-assigned-to-the-knowledge-base-article")) %>
			</liferay-ui:error>

			<liferay-ui:error exception="<%= KBArticleImportException.class %>">

				<%
				KBArticleImportException kbaie = (KBArticleImportException)errorException;
				%>

				<%= LanguageUtil.format(request, "an-unexpected-error-occurred-while-importing-articles-x", HtmlUtil.escape(kbaie.getLocalizedMessage())) %>
			</liferay-ui:error>

			<liferay-ui:error exception="<%= UploadRequestSizeException.class %>">
				<liferay-ui:message arguments="<%= LanguageUtil.formatStorageSize(UploadServletRequestConfigurationProviderUtil.getMaxSize(), locale) %>" key="request-is-larger-than-x-and-could-not-be-processed" translateArguments="<%= false %>" />
			</liferay-ui:error>
		</div>

		<div class="sheet">
			<div class="panel-group panel-group-flush">
				<aui:fieldset>
					<aui:field-wrapper>
						<div class="alert alert-info">
							<liferay-ui:message arguments="<%= HtmlUtil.escape(StringUtil.merge(kbGroupServiceConfiguration.markdownImporterArticleExtensions(), StringPool.COMMA_AND_SPACE)) %>" key="upload-your-zip-file-help" />
						</div>
					</aui:field-wrapper>

					<aui:input id="file" label="upload-your-zip-file" name="file" required="<%= true %>" type="file">
						<aui:validator name="acceptFiles">
							'zip'
						</aui:validator>
					</aui:input>

					<aui:input helpMessage="apply-numerical-prefixes-of-article-files-as-priorities-help" label="apply-numerical-prefixes-of-article-files-as-priorities" name="prioritizeByNumericalPrefix" type="checkbox" value="true" />
				</aui:fieldset>

				<aui:button-row>
					<aui:button name="submit" type="submit" />

					<aui:button href="<%= redirect %>" type="cancel" />
				</aui:button-row>
			</div>
		</div>
	</aui:form>
</clay:container-fluid>