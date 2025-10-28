<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceContext commerceContext = (CommerceContext)request.getAttribute(CommerceWebKeys.COMMERCE_CONTEXT);
CPDefinition cpDefinition = (CPDefinition)request.getAttribute(CPWebKeys.CP_DEFINITION);
%>

<liferay-util:html-top
	outputKey="com.liferay.commerce.product.definitions.web#/asset/full_content.jsp"
>
	<aui:link hashedFile="<%= true %>" href="commerce-product-definitions-web/css/main.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<div class="portlet-commerce-product-definitions">
	<div class="entry-body">
		<h1><%= HtmlUtil.escape(cpDefinition.getName(languageId)) %></h1>

		<div class="lfr-asset-categories">
			<liferay-asset:asset-categories-summary
				className="<%= cpDefinition.getModelClassName() %>"
				classPK="<%= cpDefinition.getCPDefinitionId() %>"
			/>
		</div>

		<liferay-ui:tabs
			names="details,specs,skus"
			param="<%= String.valueOf(cpDefinition.getCPDefinitionId()) %>"
			refresh="<%= false %>"
			type="nav-tabs tabs"
		>
			<liferay-ui:section>
				<dl>

					<%
					String defaultImageThumbnailSrc = cpDefinition.getDefaultImageThumbnailSrc(CommerceUtil.getCommerceAccountId(commerceContext));
					%>

					<c:if test="<%= Validator.isNotNull(defaultImageThumbnailSrc) %>">
						<div class="default-image">
							<img src="<%= defaultImageThumbnailSrc %>" />
						</div>
					</c:if>

					<%
					String description = cpDefinition.getDescription(languageId);
					%>

					<c:if test="<%= Validator.isNotNull(description) %>">
						<dt class="h5 uppercase">
							<liferay-ui:message key="description" />
						</dt>
						<dd class="h6">
							<%= description %>
						</dd>
					</c:if>

					<dt class="h5 uppercase">
						<liferay-ui:message key="created" />
					</dt>
					<dd class="h6">
						<%= HtmlUtil.escape(cpDefinition.getUserName()) %>
					</dd>
				</dl>

				<div class="lfr-asset-tags">
					<liferay-asset:asset-tags-summary
						className="<%= cpDefinition.getModelClassName() %>"
						classPK="<%= cpDefinition.getCPDefinitionId() %>"
						message="tags"
					/>
				</div>
			</liferay-ui:section>

			<liferay-ui:section>
				<dl>

					<%
					for (CPDefinitionSpecificationOptionValue cpDefinitionSpecificationOptionValue : cpDefinition.getCPDefinitionSpecificationOptionValues()) {
						CPSpecificationOption cpSpecificationOption = cpDefinitionSpecificationOptionValue.getCPSpecificationOption();
					%>

						<dt class="h5 uppercase">
							<%= HtmlUtil.escape(cpSpecificationOption.getTitle(languageId)) %>
						</dt>
						<dd class="h6 sidebar-caption">
							<%= HtmlUtil.escape(cpDefinitionSpecificationOptionValue.getValue(languageId)) %>
						</dd>

					<%
					}
					%>

				</dl>
			</liferay-ui:section>

			<liferay-ui:section>

				<%
				for (CPInstance cpInstance : cpDefinition.getCPInstances()) {
				%>

					<dt class="h5">
						<%= HtmlUtil.escape(cpInstance.getSku()) %>
					</dt>

				<%
				}
				%>

			</liferay-ui:section>
		</liferay-ui:tabs>
	</div>
</div>