<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
OrganizationScreenNavigationDisplayContext organizationScreenNavigationDisplayContext = (OrganizationScreenNavigationDisplayContext)request.getAttribute(UsersAdminWebKeys.ORGANIZATION_SCREEN_NAVIGATION_DISPLAY_CONTEXT);

long organizationId = organizationScreenNavigationDisplayContext.getOrganizationId();

List<OrgLabor> orgLabors = OrgLaborServiceUtil.getOrgLabors(organizationId);
%>

<clay:sheet-header>
	<clay:content-row
		containerElement="h3"
		cssClass="sheet-title"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<span class="heading-text"><%= organizationScreenNavigationDisplayContext.getFormLabel() %></span>
		</clay:content-col>

		<clay:content-col>
			<span class="heading-end">
				<liferay-ui:icon
					label="<%= true %>"
					linkCssClass="add-opening-hours-link btn btn-secondary btn-sm"
					message="add"
					url='<%=
						PortletURLBuilder.createRenderURL(
							liferayPortletResponse
						).setMVCPath(
							"/organization/edit_opening_hours.jsp"
						).setRedirect(
							currentURL
						).setParameter(
							"className", Organization.class.getName()
						).setParameter(
							"classPK", organizationId
						).buildString()
					%>'
				/>
			</span>
		</clay:content-col>
	</clay:content-row>
</clay:sheet-header>

<c:if test="<%= orgLabors.isEmpty() %>">
	<div class="contact-information-empty-results-message-wrapper">
		<liferay-frontend:empty-result-message
			animationType="<%= AnimationType.EMPTY %>"
			title='<%= LanguageUtil.get(resourceBundle, "this-organization-does-not-have-any-opening-hours") %>'
		/>
	</div>
</c:if>

<div
	class="<%=
		CSSClasses.builder(
			"opening-hours-wrapper"
		).add(
			"hide", orgLabors.isEmpty()
		).build()
	%>"
>

	<%
	for (OrgLabor orgLabor : orgLabors) {
		OrgLaborDisplay orgLaborDisplay = new OrgLaborDisplay(locale, orgLabor);
	%>

		<div class="opening-hours-entry">
			<clay:content-row
				cssClass="opening-hours-header"
			>
				<clay:content-col>
					<div class="h5"><%= orgLaborDisplay.getTitle() %></div>
				</clay:content-col>

				<clay:content-col
					cssClass="lfr-search-container-wrapper"
				>
					<div data-qa-id="editOrgLaborIconMenu">
						<liferay-util:include page="/organization/opening_hours_action.jsp" servletContext="<%= application %>">
							<liferay-util:param name="orgLaborId" value="<%= String.valueOf(orgLabor.getOrgLaborId()) %>" />
						</liferay-util:include>
					</div>
				</clay:content-col>
			</clay:content-row>

			<div class="table-responsive">
				<table class="table table-autofit">
					<tbody>

						<%
						for (KeyValuePair dayKeyValuePair : orgLaborDisplay.getDayKeyValuePairs()) {
						%>

							<tr>
								<td class="table-cell-expand">
									<span class="table-title"><%= dayKeyValuePair.getKey() %></span>
								</td>
								<td class="table-cell-expand">
									<span><%= dayKeyValuePair.getValue() %></span>
								</td>
							</tr>

						<%
						}
						%>

					</tbody>
				</table>
			</div>
		</div>

	<%
	}
	%>

</div>