<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
RedirectEntriesDisplayContext redirectEntriesDisplayContext = (RedirectEntriesDisplayContext)request.getAttribute(RedirectEntriesDisplayContext.class.getName());
%>

<c:if test="<%= !redirectEntriesDisplayContext.isStagingGroup() %>">
	<clay:management-toolbar
		managementToolbarDisplayContext="<%= redirectEntriesDisplayContext.getRedirectManagementToolbarDisplayContext() %>"
		propsTransformer="{RedirectManagementToolbarPropsTransformer} from redirect-web"
	/>
</c:if>

<div class="closed redirect-entries sidenav-container sidenav-right" id="<portlet:namespace />infoPanelId">
	<liferay-frontend:sidebar-panel
		resourceURL="<%= redirectEntriesDisplayContext.getSidebarPanelURL() %>"
		searchContainerId="<%= redirectEntriesDisplayContext.getSearchContainerId() %>"
	>
		<liferay-util:include page="/info_panel.jsp" servletContext="<%= application %>" />
	</liferay-frontend:sidebar-panel>

	<clay:container-fluid
		cssClass="sidenav-content"
		fullWidth="<%= true %>"
	>
		<c:if test="<%= redirectEntriesDisplayContext.isStagingGroup() %>">
			<div class="lfr-search-container">
				<clay:alert
					displayType="info"
					message="redirections-are-unavailable-in-staged-sites"
				/>
			</div>
		</c:if>

		<c:if test="<%= redirectEntriesDisplayContext.isLiveGroup() %>">
			<div class="lfr-search-container">
				<clay:alert
					displayType="warning"
					message="redirect-functionality-may-not-work-as-expected-in-the-staging-environment"
				/>
			</div>
		</c:if>

		<aui:form action="<%= redirectEntriesDisplayContext.getActionURL() %>" cssClass="container-fluid" name="fm">
			<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

			<liferay-ui:search-container
				id="<%= redirectEntriesDisplayContext.getSearchContainerId() %>"
				searchContainer="<%= redirectEntriesDisplayContext.getSearchContainer() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.redirect.model.RedirectEntry"
					keyPropertyName="redirectEntryId"
					modelVar="redirectEntry"
				>

					<%
					row.setData(
						HashMapBuilder.<String, Object>put(
							"actions", redirectEntriesDisplayContext.getAvailableActions(redirectEntry)
						).build());
					%>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-cell-text-truncate-reverse"
						name="source-url"
					>

						<%
						String sourceURL = URLCodec.decodeURL(HtmlUtil.escape(redirectEntriesDisplayContext.getSourceURL(redirectEntry)));
						%>

						<bdi data-title="<%= HtmlUtil.escapeAttribute(sourceURL) %>">
							<%= sourceURL %>
						</bdi>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-cell-text-truncate-reverse"
						name="destination-url"
					>

						<%
						String destinationURL = HtmlUtil.escape(redirectEntry.getDestinationURL());
						%>

						<bdi data-title="<%= HtmlUtil.escapeAttribute(destinationURL) %>">
							<%= destinationURL %>
						</bdi>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smallest"
						name="type"
					>
						<liferay-ui:message key='<%= redirectEntry.isPermanent() ? "permanent" : "temporary" %>' />
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smallest"
						name="expiration"
					>
						<c:choose>
							<c:when test="<%= Validator.isNull(redirectEntry.getExpirationDate()) %>">
								<%= StringPool.DASH %>
							</c:when>
							<c:when test="<%= DateUtil.compareTo(redirectEntry.getExpirationDate(), DateUtil.newDate()) <= 0 %>">
								<strong><liferay-ui:message key="expired" /></strong>
							</c:when>
							<c:otherwise>
								<%= redirectEntriesDisplayContext.formatExpirationDate(redirectEntry.getExpirationDate()) %>
							</c:otherwise>
						</c:choose>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text>
						<clay:dropdown-actions
							aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
							dropdownItems="<%= redirectEntriesDisplayContext.getActionDropdownItems(redirectEntry) %>"
						/>
					</liferay-ui:search-container-column-text>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</aui:form>
	</clay:container-fluid>
</div>

<aui:script sandbox="<%= true %>">
	Liferay.Util.delegate(
		document.querySelector('#<portlet:namespace />fm'),
		'click',
		'.icon-shortcut',
		(event) => {
			var delegateTarget = event.delegateTarget;

			var destinationURL = delegateTarget.dataset.href;

			if (destinationURL) {
				window.open(destinationURL, '_blank');
			}
		}
	);
</aui:script>