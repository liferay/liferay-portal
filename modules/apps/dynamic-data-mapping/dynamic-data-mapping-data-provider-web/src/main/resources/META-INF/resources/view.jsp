<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String backURL = ParamUtil.getString(request, "backURL");

String displayStyle = ddmDataProviderDisplayContext.getDisplayStyle();

PortletURL portletURL = PortletURLBuilder.create(
	ddmDataProviderDisplayContext.getPortletURL()
).setParameter(
	"displayStyle", displayStyle
).buildPortletURL();

portletDisplay.setShowBackIcon(ddmDataProviderDisplayContext.isShowBackIcon());
portletDisplay.setURLBack(backURL);

renderResponse.setTitle(ddmDataProviderDisplayContext.getTitle());
%>

<liferay-ui:error exception="<%= RequiredDataProviderInstanceException.MustNotDeleteDataProviderInstanceReferencedByDataProviderInstanceLinks.class %>" message="the-data-provider-cannot-be-deleted-because-it-is-required-by-one-or-more-forms" />

<liferay-util:include page="/navigation_bar.jsp" servletContext="<%= application %>" />

<liferay-util:include page="/management_bar.jsp" servletContext="<%= application %>" />

<clay:container-fluid
	id='<%= liferayPortletResponse.getNamespace() + "formContainer" %>'
>
	<aui:form action="<%= portletURL %>" method="post" name="searchContainerForm">
		<aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />
		<aui:input name="deleteDataProviderInstanceIds" type="hidden" />

		<c:choose>
			<c:when test="<%= ddmDataProviderDisplayContext.hasResults() %>">
				<liferay-ui:search-container
					id="<%= ddmDataProviderDisplayContext.getSearchContainerId() %>"
					rowChecker="<%= new DDMDataProviderInstanceRowChecker(renderResponse) %>"
					searchContainer="<%= ddmDataProviderDisplayContext.getSearchContainer() %>"
				>
					<liferay-ui:search-container-row
						className="com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance"
						keyPropertyName="dataProviderInstanceId"
						modelVar="dataProviderInstance"
					>
						<portlet:renderURL var="rowURL">
							<portlet:param name="mvcPath" value="/edit_data_provider.jsp" />
							<portlet:param name="redirect" value="<%= currentURL %>" />
							<portlet:param name="dataProviderInstanceId" value="<%= String.valueOf(dataProviderInstance.getDataProviderInstanceId()) %>" />
							<portlet:param name="displayStyle" value="<%= displayStyle %>" />
						</portlet:renderURL>

						<c:choose>
							<c:when test='<%= displayStyle.equals("descriptive") %>'>
								<liferay-ui:search-container-column-icon
									cssClass="asset-icon"
									icon="repository"
								/>

								<liferay-ui:search-container-column-jsp
									colspan="<%= 2 %>"
									href="<%= rowURL %>"
									path="/data_provider_descriptive.jsp"
								/>

								<liferay-ui:search-container-column-jsp
									path="/data_provider_action.jsp"
								/>
							</c:when>
							<c:otherwise>
								<liferay-ui:search-container-column-text
									cssClass="table-cell-expand"
									href="<%= rowURL %>"
									name="name"
									value="<%= HtmlUtil.escape(dataProviderInstance.getName(locale)) %>"
								/>

								<liferay-ui:search-container-column-text
									cssClass="table-cell-expand"
									name="description"
									value="<%= HtmlUtil.escape(dataProviderInstance.getDescription(locale)) %>"
								/>

								<liferay-ui:search-container-column-date
									name="modified-date"
									value="<%= dataProviderInstance.getModifiedDate() %>"
								/>

								<liferay-ui:search-container-column-jsp
									path="/data_provider_action.jsp"
								/>
							</c:otherwise>
						</c:choose>
					</liferay-ui:search-container-row>

					<liferay-ui:search-iterator
						displayStyle="<%= displayStyle %>"
						markupView="lexicon"
					/>
				</liferay-ui:search-container>
			</c:when>
			<c:otherwise>
				<liferay-frontend:empty-result-message
					actionDropdownItems="<%= ddmDataProviderDisplayContext.getEmptyResultMessageActionItemsDropdownItems() %>"
					animationType="<%= ddmDataProviderDisplayContext.getEmptyResultMessageAnimationType() %>"
					buttonCssClass="secondary"
					description="<%= ddmDataProviderDisplayContext.getEmptyResultMessageDescription() %>"
					title="<%= ddmDataProviderDisplayContext.getEmptyResultsMessage() %>"
				/>
			</c:otherwise>
		</c:choose>
	</aui:form>
</clay:container-fluid>