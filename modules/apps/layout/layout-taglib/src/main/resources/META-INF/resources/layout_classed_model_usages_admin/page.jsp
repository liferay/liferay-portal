<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/layout_classed_model_usages_admin/init.jsp" %>

<%
long classPK = GetterUtil.getLong((String)request.getAttribute("liferay-layout:layout-classed-model-usages-admin:classPK"));
String className = GetterUtil.getString(request.getAttribute("liferay-layout:layout-classed-model-usages-admin:className"));

LayoutClassedModelUsagesDisplayContext layoutClassedModelUsagesDisplayContext = new LayoutClassedModelUsagesDisplayContext(request, renderRequest, renderResponse, className, classPK);
%>

<clay:container-fluid
	cssClass="container-form-lg"
	size="xxxl"
>
	<clay:row>
		<clay:col
			lg="3"
		>
			<strong class="text-uppercase">
				<liferay-ui:message key="usages" />
			</strong>

			<clay:vertical-nav
				verticalNavItems="<%= layoutClassedModelUsagesDisplayContext.getVerticalNavItemList() %>"
			/>
		</clay:col>

		<clay:col
			lg="9"
		>
			<div class="sheet">
				<h3 class="sheet-title">
					<c:choose>
						<c:when test='<%= Objects.equals(layoutClassedModelUsagesDisplayContext.getNavigation(), "pages") %>'>
							<liferay-ui:message arguments="<%= layoutClassedModelUsagesDisplayContext.getPagesUsageCount() %>" key="pages-x" />
						</c:when>
						<c:when test='<%= Objects.equals(layoutClassedModelUsagesDisplayContext.getNavigation(), "page-templates") %>'>
							<liferay-ui:message arguments="<%= layoutClassedModelUsagesDisplayContext.getPageTemplatesUsageCount() %>" key="page-templates-x" />
						</c:when>
						<c:when test='<%= Objects.equals(layoutClassedModelUsagesDisplayContext.getNavigation(), "display-page-templates") %>'>
							<liferay-ui:message arguments="<%= layoutClassedModelUsagesDisplayContext.getDisplayPagesUsageCount() %>" key="display-page-templates-x" />
						</c:when>
						<c:otherwise>
							<liferay-ui:message arguments="<%= layoutClassedModelUsagesDisplayContext.getAllUsageCount() %>" key="all-x" />
						</c:otherwise>
					</c:choose>
				</h3>

				<clay:management-toolbar
					managementToolbarDisplayContext="<%= new LayoutClassedModelUsagesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, layoutClassedModelUsagesDisplayContext.getSearchContainer()) %>"
				/>

				<liferay-ui:search-container
					id="layoutClassedModelUsages"
					searchContainer="<%= layoutClassedModelUsagesDisplayContext.getSearchContainer() %>"
				>
					<liferay-ui:search-container-row
						className="com.liferay.layout.model.LayoutClassedModelUsage"
						keyPropertyName="layoutClassedModelUsageId"
						modelVar="layoutClassedModelUsage"
					>
						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand table-cell-minw-200 table-title"
							name="name"
							value="<%= HtmlUtil.escape(layoutClassedModelUsagesDisplayContext.getLayoutClassedModelUsageName(layoutClassedModelUsage)) %>"
						/>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
							name="type"
							translate="<%= true %>"
							value="<%= layoutClassedModelUsagesDisplayContext.getLayoutClassedModelUsageTypeLabel(layoutClassedModelUsage) %>"
						/>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand"
							name="where"
							translate="<%= true %>"
							value="<%= layoutClassedModelUsagesDisplayContext.getLayoutClassedModelUsageWhereLabel(layoutClassedModelUsage) %>"
						/>

						<liferay-ui:search-container-column-date
							cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
							name="modified-date"
							value="<%= layoutClassedModelUsage.getModifiedDate() %>"
						/>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand-smallest table-column-text-center"
						>

							<%
							List<DropdownItem> dropdownItems = layoutClassedModelUsagesDisplayContext.getLayoutClassedModelUsageActionDropdownItems(layoutClassedModelUsage);
							%>

							<c:if test="<%= ListUtil.isNotEmpty(dropdownItems) %>">
								<clay:dropdown-actions
									aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
									dropdownItems="<%= dropdownItems %>"
								/>
							</c:if>
						</liferay-ui:search-container-column-text>
					</liferay-ui:search-container-row>

					<liferay-ui:search-iterator
						displayStyle="list"
						markupView="lexicon"
						searchResultCssClass="show-quick-actions-on-hover table table-autofit"
					/>
				</liferay-ui:search-container>
			</div>
		</clay:col>
	</clay:row>
</clay:container-fluid>