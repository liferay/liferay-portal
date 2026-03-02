<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SegmentsDisplayContext segmentsDisplayContext = (SegmentsDisplayContext)request.getAttribute(SegmentsDisplayContext.class.getName());

String eventName = liferayPortletResponse.getNamespace() + "assignSiteRoles";

request.setAttribute("view.jsp-eventName", eventName);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new SegmentsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, renderResponse, segmentsDisplayContext.getSearchContainer()) %>"
	propsTransformer="{SegmentsManagementToolbarPropsTransformer} from segments-web"
	selectable='<%= FeatureFlagManagerUtil.isEnabled(CompanyConstants.SYSTEM, "LPD-78863") %>'
/>

<c:if test="<%= !segmentsDisplayContext.isSegmentationEnabled(themeDisplay.getCompanyId()) %>">
	<clay:stripe
		defaultTitleDisabled="<%= true %>"
		dismissible="<%= true %>"
		displayType="warning"
	>
		<strong><liferay-ui:message key="segmentation-is-disabled" /></strong>

		<%
		String segmentsConfigurationURL = segmentsDisplayContext.getSegmentsCompanyConfigurationURL(request);
		%>

		<c:choose>
			<c:when test="<%= segmentsConfigurationURL != null %>">
				<clay:link
					href="<%= segmentsConfigurationURL %>"
					label="to-enable,-go-to-instance-settings"
				/>
			</c:when>
			<c:otherwise>
				<span><%=
				LanguageUtil.get(
					request, "contact-your-system-administrator-to-enable-it") %></span>
			</c:otherwise>
		</c:choose>
	</clay:stripe>
</c:if>

<portlet:actionURL name="/segments/delete_segments_entry" var="deleteSegmentsEntryURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= deleteSegmentsEntryURL %>" cssClass="c-p-0 container-fluid container-fluid-max-xl h-100" method="post" name="fmSegmentsEntries">
	<c:if test='<%= !FeatureFlagManagerUtil.isEnabled(CompanyConstants.SYSTEM, "LPD-78863") %>'>
		<clay:alert
			cssClass="mt-4"
			displayType="info"
			title="info"
		>
			<liferay-ui:message key="segments-deprecation-info-message" />

			<liferay-learn:message
				key="analytics-cloud"
				resource="segments-web"
			/>
		</clay:alert>
	</c:if>

	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

	<liferay-ui:error exception="<%= RequiredSegmentsEntryException.MustNotDeleteSegmentsEntryReferencedBySegmentsExperiences.class %>" message="the-segment-cannot-be-deleted-because-it-is-required-by-one-or-more-experiences" />

	<liferay-ui:search-container
		id="segmentsEntries"
		searchContainer="<%= segmentsDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.segments.model.SegmentsEntry"
			keyProperty="segmentsEntryId"
			modelVar="segmentsEntry"
		>

			<%
			row.setData(
				HashMapBuilder.<String, Object>put(
					"actions", segmentsDisplayContext.getAvailableActions(segmentsEntry)
				).build());
			%>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand table-title"
				href="<%= segmentsDisplayContext.getSegmentsEntryURL(segmentsEntry) %>"
				name="name"
				target="<%= segmentsDisplayContext.getSegmentsEntryURLTarget(segmentsEntry) %>"
				value="<%= HtmlUtil.escape(segmentsEntry.getName(locale)) %>"
			/>

			<c:if test="<%= segmentsDisplayContext.isAsahEnabled(themeDisplay.getCompanyId()) %>">
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-cell-minw-150"
					name="source"
				>

					<%
					SegmentsSourceDetailsProvider segmentsSourceDetailsProvider = SegmentsSourceDetailsProviderUtil.getSegmentsSourceDetailsProvider(segmentsEntry);
					%>

					<c:if test="<%= segmentsSourceDetailsProvider != null %>">
						<span class="lfr-portal-tooltip" tabindex="0" title="<%= segmentsSourceDetailsProvider.getLabel(locale) %>">
							<img alt="<%= segmentsSourceDetailsProvider.getLabel(locale) %>" src="<%= segmentsSourceDetailsProvider.getIconSrc() %>" title="<%= segmentsSourceDetailsProvider.getLabel(locale) %>" />

							<span class="sr-only"><%= segmentsSourceDetailsProvider.getLabel(locale) %></span>
						</span>
					</c:if>
				</liferay-ui:search-container-column-text>
			</c:if>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-smallest table-cell-minw-150"
				name="scope"
				value="<%= segmentsDisplayContext.getScopeName(segmentsEntry) %>"
			>

			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-expand-smallest table-cell-minw-150 table-cell-ws-nowrap"
				name="modified-date"
				value="<%= segmentsEntry.getModifiedDate() %>"
			/>

			<liferay-ui:search-container-column-text>

				<%
				SegmentsEntryActionDropdownItemsProvider segmentsEntryActionDropdownItemsProvider = new SegmentsEntryActionDropdownItemsProvider(request, segmentsDisplayContext, segmentsEntry);
				%>

				<clay:dropdown-actions
					aria-label='<%= LanguageUtil.format(request, "show-more-options-for-x", HtmlUtil.escape(segmentsEntry.getName(locale))) %>'
					dropdownItems="<%= segmentsEntryActionDropdownItemsProvider.getActionDropdownItems() %>"
					propsTransformer="{SegmentsEntryDropdownDefaultPropsTransformer} from segments-web"
				/>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>

<portlet:actionURL name="/segments/update_segments_entry_site_roles" var="updateSegmentsEntrySiteRolesURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= updateSegmentsEntrySiteRolesURL %>" cssClass="hide" method="post" name="updateSegmentsEntrySiteRolesFm">
	<aui:input name="segmentsEntryId" type="hidden" />
	<aui:input name="siteRoleIds" type="hidden" />
</aui:form>