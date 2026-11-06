<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/shared_assets/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String backURL = ParamUtil.getString(request, "backURL", redirect);

if (Validator.isNotNull(backURL)) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(backURL);
}

ViewSharedAssetsDisplayContext viewSharedAssetsDisplayContext = (ViewSharedAssetsDisplayContext)renderRequest.getAttribute(ViewSharedAssetsDisplayContext.class.getName());
%>

<clay:navigation-bar
	inverted="<%= layout.isTypeControlPanel() %>"
	navigationItems="<%= viewSharedAssetsDisplayContext.getNavigationItems() %>"
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= viewSharedAssetsDisplayContext.getManagementToolbarDisplayContext() %>"
	propsTransformer="{SharedAssetsManagementToolbarPropsTransformer} from sharing-web"
/>

<clay:container-fluid>
	<liferay-ui:search-container
		id="sharingEntries"
		searchContainer="<%= viewSharedAssetsDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.sharing.model.SharingEntry"
			escapedModel="<%= true %>"
			keyPropertyName="sharingEntryId"
			modelVar="sharingEntry"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				href="<%= viewSharedAssetsDisplayContext.getSharingEntryRowPortletURL(sharingEntry) %>"
				name="title"
				orderable="<%= false %>"
				value="<%= viewSharedAssetsDisplayContext.getSharingEntryTitle(sharingEntry) %>"
			/>

			<liferay-ui:search-container-column-text
				name="asset-type"
				orderable="<%= false %>"
				value="<%= viewSharedAssetsDisplayContext.getSharingEntryAssetTypeTitle(sharingEntry) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-smallest"
				name="status"
				orderable="<%= false %>"
			>
				<c:if test="<%= !viewSharedAssetsDisplayContext.isSharingEntryVisible(sharingEntry) %>">
					<clay:label
						displayType="info"
						label="not-visible"
					/>
				</c:if>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-date
				name="shared-date"
				orderable="<%= false %>"
				value="<%= sharingEntry.getModifiedDate() %>"
			/>

			<liferay-ui:search-container-column-text>
				<clay:dropdown-actions
					aria-label='<%= LanguageUtil.get(request, "actions") %>'
					dropdownItems="<%= viewSharedAssetsDisplayContext.getSharingEntryDropdownItems(sharingEntry) %>"
				/>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="list"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>

<liferay-frontend:component
	module="{SharedAssets} from sharing-web"
/>