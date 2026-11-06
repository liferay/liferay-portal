<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AssetDisplayPageUsagesDisplayContext assetDisplayPageUsagesDisplayContext = (AssetDisplayPageUsagesDisplayContext)request.getAttribute(LayoutPageTemplateAdminWebKeys.ASSET_DISPLAY_PAGE_USAGES_DISPLAY_CONTEXT);

AssetDisplayPageUsagesManagementToolbarDisplayContext assetDisplayPageUsagesManagementToolbarDisplayContext = new AssetDisplayPageUsagesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, assetDisplayPageUsagesDisplayContext.getSearchContainer());

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(assetDisplayPageUsagesDisplayContext.getRedirect());
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

LayoutPageTemplateEntry layoutPageTemplateEntry = LayoutPageTemplateEntryServiceUtil.getLayoutPageTemplateEntry(assetDisplayPageUsagesDisplayContext.getLayoutPageTemplateEntryId());

renderResponse.setTitle(layoutPageTemplateEntry.getName());
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= assetDisplayPageUsagesManagementToolbarDisplayContext %>"
	propsTransformer="{AssetDisplayPageUsagesManagementToolbarPropsTransformer} from layout-page-template-admin-web"
/>

<aui:form cssClass="container-fluid container-fluid-max-xxxl" name="fm">

	<%
	LayoutPageTemplateEntry defaultLayoutPageTemplateEntry = assetDisplayPageUsagesManagementToolbarDisplayContext.getDefaultLayoutPageTemplateEntry();

	String displayPageAssignedMessage = LanguageUtil.get(resourceBundle, "successfully-assigned-to-default-display-page-template");

	if (defaultLayoutPageTemplateEntry != null) {
		displayPageAssignedMessage = LanguageUtil.format(resourceBundle, "successfully-assigned-to-default-display-page-template-x", "<strong>" + defaultLayoutPageTemplateEntry.getName() + "</strong>");
	}
	%>

	<liferay-ui:success key="displayPageAssigned" message="<%= displayPageAssignedMessage %>" />

	<liferay-ui:success key="displayPageUnassigned" message="successfully-unassigned-display-page-template" />

	<liferay-ui:search-container
		id="assetDisplayPageEntries"
		searchContainer="<%= assetDisplayPageUsagesDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.asset.display.page.model.AssetDisplayPageEntry"
			keyPropertyName="assetDisplayPageEntryId"
			modelVar="assetDisplayPageEntry"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand table-cell-minw-200 table-title"
				name="title"
				value="<%= HtmlUtil.escape(assetDisplayPageUsagesDisplayContext.getTitle(assetDisplayPageEntry, themeDisplay.getLocale())) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand table-cell-minw-200 table-title"
				name="author"
				value="<%= HtmlUtil.escape(assetDisplayPageEntry.getUserName()) %>"
			/>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
				name="modified-date"
				value="<%= assetDisplayPageEntry.getModifiedDate() %>"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="list"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>