<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

if (Validator.isNull(redirect)) {
	PortletURL portletURL = renderResponse.createRenderURL();

	redirect = portletURL.toString();
}

AssetListEntryUsagesDisplayContext assetListEntryUsagesDisplayContext = new AssetListEntryUsagesDisplayContext(request, renderRequest, renderResponse);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

renderResponse.setTitle(assetListDisplayContext.getAssetListEntryTitle());
%>

<clay:container-fluid
	cssClass="container-form-lg"
>
	<clay:row>
		<clay:col
			lg="3"
		>
			<div class="c-mb-3 h5 text-uppercase">
				<liferay-ui:message key="usages" />
			</div>

			<clay:vertical-nav
				verticalNavItems="<%= assetListEntryUsagesDisplayContext.getVerticalNavItemList() %>"
			/>
		</clay:col>

		<clay:col
			lg="9"
		>
			<clay:sheet
				size="full"
			>
				<h3 class="sheet-title">
					<c:choose>
						<c:when test='<%= Objects.equals(assetListEntryUsagesDisplayContext.getNavigation(), "pages") %>'>
							<liferay-ui:message arguments="<%= assetListEntryUsagesDisplayContext.getPagesUsageCount() %>" key="pages-x" />
						</c:when>
						<c:when test='<%= Objects.equals(assetListEntryUsagesDisplayContext.getNavigation(), "page-templates") %>'>
							<liferay-ui:message arguments="<%= assetListEntryUsagesDisplayContext.getPageTemplatesUsageCount() %>" key="page-templates-x" />
						</c:when>
						<c:when test='<%= Objects.equals(assetListEntryUsagesDisplayContext.getNavigation(), "display-page-templates") %>'>
							<liferay-ui:message arguments="<%= assetListEntryUsagesDisplayContext.getDisplayPagesUsageCount() %>" key="display-page-templates-x" />
						</c:when>
						<c:otherwise>
							<liferay-ui:message arguments="<%= assetListEntryUsagesDisplayContext.getAllUsageCount() %>" key="all-x" />
						</c:otherwise>
					</c:choose>
				</h3>

				<clay:management-toolbar
					managementToolbarDisplayContext="<%= new AssetListEntryUsagesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, assetListEntryUsagesDisplayContext.getSearchContainer()) %>"
				/>

				<liferay-ui:search-container
					id="assetListEntryUsages"
					searchContainer="<%= assetListEntryUsagesDisplayContext.getSearchContainer() %>"
				>
					<liferay-ui:search-container-row
						className="com.liferay.asset.list.model.AssetListEntryUsage"
						keyPropertyName="assetListEntryUsageId"
						modelVar="assetListEntryUsage"
					>
						<liferay-ui:search-container-column-text
							name="name"
							value="<%= HtmlUtil.escape(assetListEntryUsagesDisplayContext.getAssetListEntryUsageName(assetListEntryUsage)) %>"
						/>

						<liferay-ui:search-container-column-text
							name="type"
							translate="<%= true %>"
							value="<%= assetListEntryUsagesDisplayContext.getAssetListEntryUsageTypeLabel(assetListEntryUsage) %>"
						/>

						<liferay-ui:search-container-column-date
							name="modified-date"
							value="<%= assetListEntryUsage.getModifiedDate() %>"
						/>
					</liferay-ui:search-container-row>

					<liferay-ui:search-iterator
						displayStyle="list"
						markupView="lexicon"
						searchResultCssClass="show-quick-actions-on-hover table table-autofit"
					/>
				</liferay-ui:search-container>
			</clay:sheet>
		</clay:col>
	</clay:row>
</clay:container-fluid>