<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-ui:success key="membershipRequestSent" message="your-request-was-sent-you-will-receive-a-reply-by-email" />

<liferay-ui:error embed="<%= false %>" key="membershipAlreadyRequested" message="membership-was-already-requested" />

<clay:navigation-bar
	navigationItems="<%= siteMySitesDisplayContext.getNavigationItems() %>"
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new SiteMySitesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, siteMySitesDisplayContext) %>"
/>

<aui:form action="<%= siteMySitesDisplayContext.getPortletURL() %>" cssClass="container-fluid" method="get" name="fm">
	<liferay-ui:search-container
		searchContainer="<%= siteMySitesDisplayContext.getGroupSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.Group"
			keyPropertyName="groupId"
			modelVar="group"
			rowIdProperty="friendlyURL"
		>

			<%
			String siteImageURL = group.getLogoURL(themeDisplay, false);

			String rowURL = StringPool.BLANK;

			if (group.getPublicLayoutsPageCount() > 0) {
				rowURL = group.getDisplayURL(themeDisplay, false);
			}
			else if (Objects.equals(siteMySitesDisplayContext.getTabs1(), "my-sites") && (group.getPrivateLayoutsPageCount() > 0)) {
				rowURL = group.getDisplayURL(themeDisplay, true);
			}

			List<DropdownItem> dropdownItems = siteMySitesDisplayContext.getGroupActionDropdownItems(group);
			%>

			<c:choose>
				<c:when test='<%= Objects.equals(siteMySitesDisplayContext.getDisplayStyle(), "descriptive") %>'>
					<c:choose>
						<c:when test="<%= Validator.isNotNull(siteImageURL) %>">
							<liferay-ui:search-container-column-image
								src="<%= siteImageURL %>"
							/>
						</c:when>
						<c:otherwise>
							<liferay-ui:search-container-column-icon
								icon="sites"
							/>
						</c:otherwise>
					</c:choose>

					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
					>
						<div>
							<c:choose>
								<c:when test="<%= Validator.isNotNull(rowURL) %>">
									<a href="<%= rowURL %>" target="_blank">
										<strong><%= HtmlUtil.escape(group.getDescriptiveName(locale)) %></strong>
									</a>
								</c:when>
								<c:otherwise>
									<strong><%= HtmlUtil.escape(group.getDescriptiveName(locale)) %></strong>
								</c:otherwise>
							</c:choose>
						</div>

						<c:if test='<%= !Objects.equals(siteMySitesDisplayContext.getTabs1(), "my-sites") && Validator.isNotNull(group.getDescription(locale)) %>'>
							<div class="text-default">
								<%= HtmlUtil.escape(group.getDescription(locale)) %>
							</div>
						</c:if>

						<liferay-util:buffer
							var="assetTagsSummary"
						>
							<liferay-asset:asset-tags-summary
								className="<%= Group.class.getName() %>"
								classPK="<%= group.getGroupId() %>"
							/>
						</liferay-util:buffer>

						<c:if test="<%= Validator.isNotNull(assetTagsSummary) %>">
							<div class="text-default">
								<%= assetTagsSummary %>
							</div>
						</c:if>

						<%
						int usersCount = siteMySitesDisplayContext.getGroupUsersCounts(group.getGroupId());
						%>

						<c:if test="<%= usersCount > 0 %>">
							<div class="text-default">
								<strong><liferay-ui:message arguments="<%= usersCount %>" key='<%= (usersCount > 1) ? "x-users" : "x-user" %>' /></strong>
							</div>
						</c:if>

						<%
						int organizationsCount = siteMySitesDisplayContext.getGroupOrganizationsCount(group.getGroupId());
						%>

						<c:if test="<%= organizationsCount > 0 %>">
							<div class="text-default">
								<strong><liferay-ui:message arguments="<%= organizationsCount %>" key='<%= (organizationsCount > 1) ? "x-organizations" : "x-organization" %>' /></strong>
							</div>
						</c:if>

						<%
						int userGroupsCount = siteMySitesDisplayContext.getGroupUserGroupsCount(group.getGroupId());
						%>

						<c:if test="<%= userGroupsCount > 0 %>">
							<div class="text-default">
								<strong><liferay-ui:message arguments="<%= userGroupsCount %>" key='<%= (userGroupsCount > 1) ? "x-user-groups" : "x-user-group" %>' /></strong>
							</div>
						</c:if>

						<c:if test='<%= Objects.equals(siteMySitesDisplayContext.getTabs1(), "my-sites") && PropsValues.LIVE_USERS_ENABLED %>'>
							<div class="text-default">
								<strong><liferay-ui:message key="online-now" /></strong>: <%= String.valueOf(LiveUsers.getGroupUsersCount(company.getCompanyId(), group.getGroupId())) %>
							</div>
						</c:if>
					</liferay-ui:search-container-column-text>

					<c:if test="<%= ListUtil.isNotEmpty(dropdownItems) %>">
						<liferay-ui:search-container-column-text>
							<clay:dropdown-actions
								aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
								dropdownItems="<%= dropdownItems %>"
								propsTransformer="{SiteDropdownDefaultPropsTransformer} from site-my-sites-web"
							/>
						</liferay-ui:search-container-column-text>
					</c:if>
				</c:when>
				<c:when test='<%= Objects.equals(siteMySitesDisplayContext.getDisplayStyle(), "icon") %>'>
					<liferay-ui:search-container-column-text>
						<clay:vertical-card
							verticalCard="<%= new SiteVerticalCard(group, siteMySitesDisplayContext.getGroupOrganizationsCount(group.getGroupId()), siteMySitesDisplayContext.getGroupUserGroupsCount(group.getGroupId()), siteMySitesDisplayContext.getGroupUsersCounts(group.getGroupId()), renderRequest, renderResponse, siteMySitesDisplayContext.getTabs1()) %>"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:when test='<%= Objects.equals(siteMySitesDisplayContext.getDisplayStyle(), "list") %>'>
					<liferay-ui:search-container-column-text
						name="name"
						orderable="<%= true %>"
						truncate="<%= true %>"
					>
						<c:choose>
							<c:when test="<%= Validator.isNotNull(rowURL) %>">
								<a href="<%= rowURL %>" target="_blank">
									<strong><%= HtmlUtil.escape(group.getDescriptiveName(locale)) %></strong>
								</a>
							</c:when>
							<c:otherwise>
								<strong><%= HtmlUtil.escape(group.getDescriptiveName(locale)) %></strong>
							</c:otherwise>
						</c:choose>

						<c:if test='<%= !Objects.equals(siteMySitesDisplayContext.getTabs1(), "my-sites") && Validator.isNotNull(group.getDescription(locale)) %>'>
							<br />

							<em><%= HtmlUtil.escape(group.getDescription(locale)) %></em>
						</c:if>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-small table-cell-minw-100"
						name="members"
					>
						<span onmouseover="Liferay.Portal.ToolTip.show(this, '<liferay-ui:message key="inherited-memberships-are-not-included-in-members-count" unicode="<%= true %>" />');">
							<div>

								<%
								int usersCount = siteMySitesDisplayContext.getGroupUsersCounts(group.getGroupId());
								%>

								<c:if test="<%= usersCount > 0 %>">
									<div class="user-count">
										<%= LanguageUtil.format(request, usersCount > 1 ? "x-users" : "x-user", usersCount, false) %>
									</div>
								</c:if>

								<%
								int organizationsCount = siteMySitesDisplayContext.getGroupOrganizationsCount(group.getGroupId());
								%>

								<c:if test="<%= organizationsCount > 0 %>">
									<div class="organization-count">
										<%= LanguageUtil.format(request, organizationsCount > 1 ? "x-organizations" : "x-organization", organizationsCount, false) %>
									</div>
								</c:if>

								<%
								int userGroupsCount = siteMySitesDisplayContext.getGroupUserGroupsCount(group.getGroupId());
								%>

								<c:if test="<%= userGroupsCount > 0 %>">
									<div class="user-group-count">
										<%= LanguageUtil.format(request, userGroupsCount > 1 ? "x-user-groups" : "x-user-group", userGroupsCount, false) %>
									</div>
								</c:if>

								<c:if test="<%= (usersCount + organizationsCount + userGroupsCount) <= 0 %>">
									0
								</c:if>
							</div>
						</span>
					</liferay-ui:search-container-column-text>

					<c:if test='<%= Objects.equals(siteMySitesDisplayContext.getTabs1(), "my-sites") && PropsValues.LIVE_USERS_ENABLED %>'>
						<liferay-ui:search-container-column-text
							name="online-now"
							value="<%= String.valueOf(LiveUsers.getGroupUsersCount(company.getCompanyId(), group.getGroupId())) %>"
						/>
					</c:if>

					<liferay-ui:search-container-column-text
						name="tags"
					>
						<liferay-asset:asset-tags-summary
							className="<%= Group.class.getName() %>"
							classPK="<%= group.getGroupId() %>"
						/>
					</liferay-ui:search-container-column-text>

					<c:if test="<%= ListUtil.isNotEmpty(dropdownItems) %>">
						<liferay-ui:search-container-column-text>
							<clay:dropdown-actions
								aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
								dropdownItems="<%= dropdownItems %>"
								propsTransformer="{SiteDropdownDefaultPropsTransformer} from site-my-sites-web"
							/>
						</liferay-ui:search-container-column-text>
					</c:if>
				</c:when>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= siteMySitesDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>