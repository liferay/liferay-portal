<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
FragmentCollectionsDisplayContext fragmentCollectionsDisplayContext = (FragmentCollectionsDisplayContext)request.getAttribute(FragmentCollectionsDisplayContext.class.getName());
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new FragmentCollectionsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, fragmentCollectionsDisplayContext) %>"
/>

<aui:form cssClass="container-fluid container-fluid-max-xxxl" name="fm">
	<liferay-ui:search-container
		id="fragmentCollections"
		searchContainer="<%= fragmentCollectionsDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.fragment.model.FragmentCollection"
			keyProperty="fragmentCollectionId"
			modelVar="fragmentCollection"
		>
			<liferay-ui:search-container-column-text
				name="name"
				truncate="<%= true %>"
				value="<%= HtmlUtil.escape(fragmentCollection.getName()) %>"
			/>

			<c:if test="<%= scopeGroupId != themeDisplay.getCompanyGroupId() %>">

				<%
				Group group = GroupLocalServiceUtil.fetchGroup(fragmentCollection.getGroupId());
				%>

				<liferay-ui:search-container-column-text
					name="scope"
					value="<%= HtmlUtil.escape(group.getDescriptiveName(locale)) %>"
				/>
			</c:if>

			<liferay-ui:search-container-column-date
				name="create-date"
				property="createDate"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="list"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>