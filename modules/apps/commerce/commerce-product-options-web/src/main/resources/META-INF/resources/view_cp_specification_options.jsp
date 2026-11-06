<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String specificationNavbarItemKey = ParamUtil.getString(request, "specificationNavbarItemKey", "specification-labels");

CPSpecificationOptionDisplayContext cpSpecificationOptionDisplayContext = (CPSpecificationOptionDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

PortletURL portletURL = PortletURLBuilder.create(
	cpSpecificationOptionDisplayContext.getPortletURL()
).setParameter(
	"searchContainerId", "cpSpecificationOptions"
).buildPortletURL();

request.setAttribute("view.jsp-portletURL", portletURL);

renderResponse.setTitle(LanguageUtil.get(request, "specifications"));
%>

<%@ include file="/navbar_specifications.jspf" %>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new CPSpecificationOptionManagementToolbarDisplayContext(cpSpecificationOptionDisplayContext, request, liferayPortletRequest, liferayPortletResponse) %>"
	propsTransformer="{CPSpecificationOptionManagementToolbarPropsTransformer} from commerce-product-options-web"
/>

<div id="<portlet:namespace />productSpecificationOptionsContainer">
	<div class="closed sidenav-container sidenav-right" id="<portlet:namespace />infoPanelId">
		<c:if test="<%= cpSpecificationOptionDisplayContext.isShowInfoPanel() %>">
			<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/cp_specification_options/cp_specification_option_info_panel" var="sidebarPanelURL" />

			<liferay-frontend:sidebar-panel
				resourceURL="<%= sidebarPanelURL %>"
				searchContainerId="cpSpecificationOptions"
			>
				<liferay-util:include page="/cp_specification_option_info_panel.jsp" servletContext="<%= application %>" />
			</liferay-frontend:sidebar-panel>
		</c:if>

		<div class="sidenav-content">
			<clay:container-fluid>
				<portlet:actionURL name="/cp_specification_options/edit_cp_specification_option" var="editCPSpecificationOptionURL" />

				<aui:form action="<%= editCPSpecificationOptionURL %>" method="post" name="fm">
					<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.DELETE %>" />
					<aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />
					<aui:input name="deleteCPSpecificationOptionIds" type="hidden" />

					<div class="product-specification-options-container" id="<portlet:namespace />entriesContainer">
						<liferay-ui:search-container
							id="cpSpecificationOptions"
							iteratorURL="<%= portletURL %>"
							searchContainer="<%= cpSpecificationOptionDisplayContext.getSearchContainer() %>"
						>
							<liferay-ui:search-container-row
								className="com.liferay.commerce.product.model.CPSpecificationOption"
								keyPropertyName="CPSpecificationOptionId"
								modelVar="cpSpecificationOption"
							>

								<%
								PortletURL rowURL = PortletURLBuilder.createRenderURL(
									renderResponse
								).setMVCRenderCommandName(
									"/cp_specification_options/edit_cp_specification_option"
								).setRedirect(
									currentURL
								).setParameter(
									"cpSpecificationOptionId", cpSpecificationOption.getCPSpecificationOptionId()
								).setParameter(
									"toolbarItem", "specification-labels"
								).buildPortletURL();
								%>

								<liferay-ui:search-container-column-text
									cssClass="font-weight-bold important table-cell-expand"
									href="<%= rowURL %>"
									name="label"
									value="<%= HtmlUtil.escape(cpSpecificationOption.getTitle(locale)) %>"
								/>

								<liferay-ui:search-container-column-text
									cssClass="table-cell-expand"
									name="default-group"
									value="<%= HtmlUtil.escape(cpSpecificationOptionDisplayContext.getCPOptionCategoryTitle(cpSpecificationOption)) %>"
								/>

								<liferay-ui:search-container-column-text
									cssClass="table-cell-expand"
									name="use-in-faceted-navigation"
									value='<%= LanguageUtil.get(request, cpSpecificationOption.isFacetable() ? "yes" : "no") %>'
								/>

								<liferay-ui:search-container-column-text
									cssClass="table-cell-expand"
									property="priority"
								/>

								<liferay-ui:search-container-column-date
									cssClass="table-cell-expand"
									name="modified-date"
									property="modifiedDate"
								/>

								<liferay-ui:search-container-column-jsp
									cssClass="entry-action-column"
									path="/specification_option_action.jsp"
								/>
							</liferay-ui:search-container-row>

							<liferay-ui:search-iterator
								displayStyle="list"
								markupView="lexicon"
							/>
						</liferay-ui:search-container>
					</div>
				</aui:form>
			</clay:container-fluid>
		</div>
	</div>
</div>