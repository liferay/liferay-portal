<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/publications/init.jsp" %>

<%
ViewChangesDisplayContext viewChangesDisplayContext = (ViewChangesDisplayContext)request.getAttribute(CTWebKeys.VIEW_CHANGES_DISPLAY_CONTEXT);

if (!user.isOnDemandUser()) {
	portletDisplay.setURLBack(viewChangesDisplayContext.getBackURL());
	portletDisplay.setShowBackIcon(true);
}
else {
	portletDisplay.setShowBackIcon(false);
}

renderResponse.setTitle(LanguageUtil.get(request, "review-changes"));
%>

<div class="publications-view-changes-wrapper">
	<div>
		<react:component
			module="{ChangeTrackingChangesToolbar} from change-tracking-web"
			props="<%= viewChangesDisplayContext.getToolbarReactData() %>"
		/>
	</div>

	<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-20131") %>'>
		<div>
			<react:component
				module="{ChangeTrackingOverview} from change-tracking-web"
				props="<%= viewChangesDisplayContext.getItemsOverview() %>"
			/>
		</div>
	</c:if>

	<clay:navigation-bar
		navigationItems="<%= viewChangesDisplayContext.getViewNavigationItems() %>"
	/>

	<aui:form action="<%= viewChangesDisplayContext.getBackURL() %>" method="post" name="fm">
		<frontend-data-set:headless-display
			apiURL="<%= viewChangesDisplayContext.getAPIURL() %>"
			bulkActionDropdownItems="<%= viewChangesDisplayContext.getBulkActionDropdownItems() %>"
			fdsActionDropdownItems="<%= viewChangesDisplayContext.getFDSActionDropdownItems() %>"
			fdsFilters="<%= viewChangesDisplayContext.getFDSFilters() %>"
			fdsSortItemList="<%= viewChangesDisplayContext.getFDSSortItemList() %>"
			formName="fm"
			id="<%= PublicationsFDSNames.PUBLICATIONS_CHANGES %>"
			selectedItemsKey="id"
			selectionType="multiple"
			style="fluid"
		/>
	</aui:form>
</div>