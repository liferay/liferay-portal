<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
GroupFragmentEntryLinkDisplayContext groupFragmentEntryLinkDisplayContext = (GroupFragmentEntryLinkDisplayContext)request.getAttribute(GroupFragmentEntryLinkDisplayContext.class.getName());

FragmentEntry fragmentEntry = groupFragmentEntryLinkDisplayContext.getFragmentEntry();

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(groupFragmentEntryLinkDisplayContext.getRedirect());
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

renderResponse.setTitle(LanguageUtil.format(request, "usages-and-propagation-x", fragmentEntry.getName()));
%>

<clay:container-fluid
	cssClass="container-form-lg"
	size="xxxl"
>
	<clay:sheet>
		<clay:row>
			<clay:col
				lg="12"
			>
				<clay:management-toolbar
					managementToolbarDisplayContext="<%= new GroupFragmentEntryUsageManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, groupFragmentEntryLinkDisplayContext.getSearchContainer()) %>"
					propsTransformer="{FragmentEntryUsagesManagementToolbarPropsTransformer} from fragment-web"
				/>

				<portlet:actionURL name="/fragment/propagate_group_fragment_entry_changes" var="propagateGroupFragmentEntryChangesURL">
					<portlet:param name="redirect" value="<%= currentURL %>" />
					<portlet:param name="fragmentEntryERC" value="<%= String.valueOf(fragmentEntry.getExternalReferenceCode()) %>" />
					<portlet:param name="fragmentEntryScopeERC" value="<%= String.valueOf(fragmentEntry.getScopeERC()) %>" />
				</portlet:actionURL>

				<aui:form action="<%= propagateGroupFragmentEntryChangesURL %>" name="fm">
					<liferay-ui:search-container
						searchContainer="<%= groupFragmentEntryLinkDisplayContext.getSearchContainer() %>"
					>
						<liferay-ui:search-container-row
							className="com.liferay.portal.kernel.model.Group"
							keyProperty="groupId"
							modelVar="group"
						>
							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand table-cell-minw-200"
								name="name"
								value="<%= group.getDescriptiveName(locale) %>"
							/>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand-smallest table-cell-minw-100"
								name="usages"
								translate="<%= true %>"
								value="<%= String.valueOf(groupFragmentEntryLinkDisplayContext.getFragmentGroupUsageCount(group)) %>"
							/>
						</liferay-ui:search-container-row>

						<liferay-ui:search-iterator
							displayStyle="list"
							markupView="lexicon"
							paginate="<%= false %>"
							searchResultCssClass="show-quick-actions-on-hover table table-autofit"
						/>
					</liferay-ui:search-container>
				</aui:form>
			</clay:col>
		</clay:row>
	</clay:sheet>
</clay:container-fluid>