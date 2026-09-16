<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
LayoutPageTemplateDisplayContext layoutPageTemplateDisplayContext = new LayoutPageTemplateDisplayContext(request, renderRequest, renderResponse);

List<LayoutPageTemplateCollection> layoutPageTemplateCollections = layoutPageTemplateDisplayContext.getLayoutPageTemplateCollections();
%>

<clay:navigation-bar
	navigationItems="<%= layoutPageTemplatesAdminDisplayContext.getNavigationItems() %>"
/>

<liferay-ui:success key="layoutPageTemplatePublished" message="the-page-template-was-published-successfully" />

<clay:container-fluid
	cssClass="container-view"
>
	<clay:row>
		<c:if test="<%= layoutPageTemplateDisplayContext.isShowCollectionsPanel() %>">
			<clay:col
				lg="3"
			>
				<portlet:renderURL var="editLayoutPageTemplateCollectionURL">
					<portlet:param name="mvcRenderCommandName" value="/layout_page_template_admin/edit_layout_page_template_collection" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
				</portlet:renderURL>

				<c:choose>
					<c:when test="<%= ListUtil.isNotEmpty(layoutPageTemplateCollections) %>">
						<clay:content-row
							verticalAlign="center"
						>
							<clay:content-col
								expand="<%= true %>"
							>
								<strong class="text-uppercase">
									<liferay-ui:message key="page-template-sets" />
								</strong>
							</clay:content-col>

							<clay:content-col
								verticalAlign="end"
							>
								<ul class="navbar-nav">
									<c:if test="<%= layoutPageTemplateDisplayContext.isShowAddButton(LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_COLLECTION) %>">
										<li>
											<clay:link
												borderless="<%= true %>"
												cssClass="component-action"
												href="<%= editLayoutPageTemplateCollectionURL.toString() %>"
												icon="plus"
												type="button"
											/>
										</li>
									</c:if>

									<li>
										<portlet:renderURL var="viewLayoutPageTemplateCollectionURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
											<portlet:param name="mvcRenderCommandName" value="/layout_page_template_admin/select_layout_page_template_collections" />
										</portlet:renderURL>

										<portlet:renderURL var="redirectURL">
											<portlet:param name="tabs1" value="page-templates" />
										</portlet:renderURL>

										<liferay-portlet:actionURL copyCurrentRenderParameters="<%= false %>" name="/layout_page_template_admin/delete_layout_page_template_collection" var="deleteLayoutPageTemplateCollectionURL">
											<portlet:param name="redirect" value="<%= redirectURL %>" />
										</liferay-portlet:actionURL>

										<clay:dropdown-actions
											additionalProps='<%=
												HashMapBuilder.<String, Object>put(
													"deleteLayoutPageTemplateCollectionURL", deleteLayoutPageTemplateCollectionURL.toString()
												).put(
													"viewLayoutPageTemplateCollectionURL", viewLayoutPageTemplateCollectionURL.toString()
												).build()
											%>'
											aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
											dropdownItems="<%= layoutPageTemplateDisplayContext.getCollectionsDropdownItems() %>"
											propsTransformer="{ActionsComponentPropsTransformer} from layout-page-template-admin-web"
										/>
									</li>
								</ul>
							</clay:content-col>
						</clay:content-row>

						<clay:vertical-nav
							verticalNavItems="<%= layoutPageTemplateDisplayContext.getVerticalNavItemList() %>"
						/>
					</c:when>
					<c:otherwise>
						<p class="text-uppercase">
							<strong><liferay-ui:message key="page-template-sets" /></strong>
						</p>

						<liferay-frontend:empty-result-message
							actionDropdownItems="<%= layoutPageTemplateDisplayContext.isShowAddButton(LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_COLLECTION) ? layoutPageTemplateDisplayContext.getActionDropdownItems() : null %>"
							animationType="<%= EmptyResultMessageKeys.AnimationType.NONE %>"
							description='<%= LanguageUtil.get(request, "page-template-sets-are-needed-to-create-page-templates") %>'
							elementType='<%= LanguageUtil.get(request, "page-template-sets") %>'
						/>
					</c:otherwise>
				</c:choose>
			</clay:col>
		</c:if>

		<clay:col
			lg='<%= layoutPageTemplateDisplayContext.isShowCollectionsPanel() ? "9" : "12" %>'
		>

			<%
			LayoutPageTemplateCollection layoutPageTemplateCollection = layoutPageTemplateDisplayContext.getLayoutPageTemplateCollection();
			%>

			<c:if test="<%= layoutPageTemplateCollection != null %>">
				<clay:sheet
					size="full"
				>
					<h2 class="sheet-title">
						<clay:content-row
							verticalAlign="center"
						>
							<clay:content-col>
								<span>
									<%= HtmlUtil.escape(layoutPageTemplateCollection.getName()) %>
								</span>
							</clay:content-col>

							<clay:content-col
								cssClass="inline-item-after"
								verticalAlign="end"
							>

								<%
								LayoutPageTemplateCollectionActionDropdownItem layoutPageTemplateCollectionActionDropdownItem = new LayoutPageTemplateCollectionActionDropdownItem(request, layoutPageTemplateCollection, renderResponse, "page-templates");
								%>

								<clay:dropdown-actions
									aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
									dropdownItems="<%= layoutPageTemplateCollectionActionDropdownItem.getActionDropdownItems() %>"
									propsTransformer="{LayoutPageTemplateCollectionPropsTransformer} from layout-page-template-admin-web"
								/>
							</clay:content-col>
						</clay:content-row>
					</h2>

					<clay:sheet-section>
						<liferay-util:include page="/view_layout_page_template_entries.jsp" servletContext="<%= application %>" />
					</clay:sheet-section>
				</clay:sheet>
			</c:if>
		</clay:col>
	</clay:row>
</clay:container-fluid>