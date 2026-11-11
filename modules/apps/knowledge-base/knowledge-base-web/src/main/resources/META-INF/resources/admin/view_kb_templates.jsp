<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
ViewKBTemplatesDisplayContext viewKBTemplatesDisplayContext = (ViewKBTemplatesDisplayContext)request.getAttribute(ViewKBTemplatesDisplayContext.class.getName());
%>

<liferay-util:include page="/admin/common/vertical_menu.jsp" servletContext="<%= application %>" />

<div class="knowledge-base-admin-content">
	<clay:management-toolbar
		managementToolbarDisplayContext="<%= viewKBTemplatesDisplayContext.getManagementToolbarDisplayContext() %>"
		propsTransformer="{TemplatesManagementToolbarPropsTransformer} from knowledge-base-web"
		searchContainerId="kbTemplates"
	/>

	<clay:container-fluid
		size="xxxl"
	>
		<aui:form action="<%= viewKBTemplatesDisplayContext.getSearchURL() %>" method="get" name="fm">
			<aui:input name="kbTemplateIds" type="hidden" />

			<c:choose>
				<c:when test="<%= viewKBTemplatesDisplayContext.hasKBTemplates() %>">
					<liferay-ui:search-container
						id="kbTemplates"
						searchContainer="<%= viewKBTemplatesDisplayContext.getSearchContainer() %>"
					>
						<liferay-ui:search-container-row
							className="com.liferay.knowledge.base.model.KBTemplate"
							keyProperty="kbTemplateId"
							modelVar="kbTemplate"
						>

							<%
							row.setData(
								HashMapBuilder.<String, Object>put(
									"actions", StringUtil.merge(viewKBTemplatesDisplayContext.getAvailableActions(kbTemplate))
								).build());
							%>

							<liferay-ui:search-container-column-user
								showDetails="<%= false %>"
								userId="<%= kbTemplate.getUserId() %>"
							/>

							<liferay-ui:search-container-column-text
								colspan="<%= 2 %>"
							>
								<h2 class="h5">
									<aui:a href="<%= viewKBTemplatesDisplayContext.getEditKBTemplateURL(kbTemplate) %>">
										<%= HtmlUtil.escape(kbTemplate.getTitle()) %>
									</aui:a>
								</h2>

								<span class="text-default">
									<liferay-ui:message arguments="<%= viewKBTemplatesDisplayContext.getKBTemplateModifiedDateDescription(kbTemplate) %>" key="modified-x-ago" />
								</span>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text>
								<clay:dropdown-actions
									aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
									dropdownItems="<%= viewKBTemplatesDisplayContext.getKBTemplateDropdownItems(kbTemplate) %>"
									propsTransformer="{KBDropdownPropsTransformer} from knowledge-base-web"
								/>
							</liferay-ui:search-container-column-text>
						</liferay-ui:search-container-row>

						<liferay-ui:search-iterator
							displayStyle="descriptive"
							markupView="lexicon"
						/>
					</liferay-ui:search-container>
				</c:when>
				<c:otherwise>
					<liferay-frontend:empty-result-message
						actionDropdownItems="<%= viewKBTemplatesDisplayContext.getEmptyStateActionDropdownItems() %>"
						animationType="<%= AnimationType.EMPTY %>"
						buttonCssClass="secondary"
						title='<%= LanguageUtil.get(request, "there-are-no-article-templates") %>'
					/>
				</c:otherwise>
			</c:choose>
		</aui:form>
	</clay:container-fluid>
</div>