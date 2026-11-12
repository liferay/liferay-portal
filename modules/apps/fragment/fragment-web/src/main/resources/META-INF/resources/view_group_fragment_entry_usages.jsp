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

<liferay-ui:error exception="<%= InvalidPropagationTargetGroupsException.class %>" message="the-selected-sites-are-no-longer-connected-to-this-design-library" />

<c:if test='<%= SessionMessages.contains(renderRequest, "sitesSkippedFromPropagation") %>'>
	<clay:alert
		displayType="warning"
		message="some-of-the-selected-sites-are-no-longer-connected-to-this-design-library-and-were-not-updated"
	/>
</c:if>

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
					<portlet:param name="fragmentEntryERC" value="<%= fragmentEntry.getExternalReferenceCode() %>" />
					<portlet:param name="fragmentEntryGroupId" value="<%= String.valueOf(fragmentEntry.getGroupId()) %>" />
				</portlet:actionURL>

				<aui:form action="<%= propagateGroupFragmentEntryChangesURL %>" name="fm">
					<liferay-ui:search-container
						searchContainer="<%= groupFragmentEntryLinkDisplayContext.getSearchContainer() %>"
					>
						<liferay-ui:search-container-row
							className="com.liferay.portal.kernel.model.Group"
							keyPropertyName="groupId"
							modelVar="group"
						>
							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand table-cell-minw-200"
								name="name"
								value="<%= HtmlUtil.escape(group.getDescriptiveName(locale)) %>"
							/>

							<portlet:renderURL var="viewPagesURL">
								<portlet:param name="mvcRenderCommandName" value="/fragment/view_fragment_entry_usages" />
								<portlet:param name="redirect" value="<%= currentURL %>" />
								<portlet:param name="fragmentCollectionId" value="<%= String.valueOf(groupFragmentEntryLinkDisplayContext.getFragmentCollectionId()) %>" />
								<portlet:param name="fragmentEntryId" value="<%= String.valueOf(fragmentEntry.getFragmentEntryId()) %>" />
								<portlet:param name="groupId" value="<%= String.valueOf(group.getGroupId()) %>" />
							</portlet:renderURL>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand-smallest table-cell-minw-100"
								name="usages"
							>
								<clay:link
									aria-label='<%= LanguageUtil.format(request, "view-x-usages", groupFragmentEntryLinkDisplayContext.getFragmentGroupUsageCount(group)) %>'
									cssClass="lfr-portal-tooltip"
									href="<%= viewPagesURL %>"
									label="<%= String.valueOf(groupFragmentEntryLinkDisplayContext.getFragmentGroupUsageCount(group)) %>"
									title='<%= LanguageUtil.get(request, "view-pages") %>'
								/>
							</liferay-ui:search-container-column-text>
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