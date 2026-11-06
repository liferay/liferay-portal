<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SiteAdminDisplayContext siteAdminDisplayContext = (SiteAdminDisplayContext)request.getAttribute(SiteAdminDisplayContext.class.getName());
%>

<liferay-ui:search-container
	searchContainer="<%= siteAdminDisplayContext.getGroupSearch() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.Group"
		escapedModel="<%= true %>"
		keyPropertyName="groupId"
		modelVar="curGroup"
		rowIdProperty="friendlyURL"
	>

		<%
		SiteAdminManagementToolbarDisplayContext siteAdminManagementToolbarDisplayContext = (SiteAdminManagementToolbarDisplayContext)request.getAttribute(SiteAdminManagementToolbarDisplayContext.class.getName());

		row.setData(
			HashMapBuilder.<String, Object>put(
				"actions", siteAdminManagementToolbarDisplayContext.getAvailableActions(curGroup)
			).build());

		List<Group> childSites = curGroup.getChildren(true);
		%>

		<portlet:renderURL var="viewSubsitesURL">
			<portlet:param name="backURL" value="<%= currentURL %>" />
			<portlet:param name="groupId" value="<%= String.valueOf(curGroup.getGroupId()) %>" />
		</portlet:renderURL>

		<c:choose>
			<c:when test='<%= Objects.equals(siteAdminDisplayContext.getDisplayStyle(), "descriptive") %>'>

				<%
				String siteImageURL = curGroup.getLogoURL(themeDisplay, false);
				%>

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
					<div class="h5">
						<aui:a href="<%= !curGroup.isCompany() ? viewSubsitesURL : StringPool.BLANK %>" label="<%= HtmlUtil.escape(curGroup.getDescriptiveName(locale)) %>" localizeLabel="<%= false %>" />
					</div>

					<span class="text-secondary">
						<c:choose>
							<c:when test="<%= curGroup.isActive() %>">
								<liferay-ui:message key="active" />
							</c:when>
							<c:otherwise>
								<liferay-ui:message key="not-active" />
							</c:otherwise>
						</c:choose>
					</span>
					<span class="text-secondary">
						<c:if test="<%= !curGroup.isCompany() %>">
							<liferay-ui:message arguments="<%= String.valueOf(childSites.size()) %>" key="x-child-sites" />
						</c:if>
					</span>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text>
					<clay:dropdown-actions
						aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
						dropdownItems="<%= siteAdminDisplayContext.getActionDropdownItems(curGroup) %>"
						propsTransformer="{SiteDropdownDefaultPropsTransformer} from site-admin-web"
					/>
				</liferay-ui:search-container-column-text>
			</c:when>
			<c:when test='<%= Objects.equals(siteAdminDisplayContext.getDisplayStyle(), "icon") %>'>
				<liferay-ui:search-container-column-text>
					<clay:vertical-card
						propsTransformer="{SiteDropdownDefaultPropsTransformer} from site-admin-web"
						verticalCard="<%= new SiteVerticalCard(curGroup, liferayPortletRequest, liferayPortletResponse, searchContainer.getRowChecker(), siteAdminDisplayContext) %>"
					/>
				</liferay-ui:search-container-column-text>
			</c:when>
			<c:otherwise>
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand table-cell-minw-200 table-title"
					name="name"
					orderable="<%= true %>"
				>
					<aui:a href="<%= !curGroup.isCompany() ? viewSubsitesURL : StringPool.BLANK %>" label="<%= HtmlUtil.escape(curGroup.getDescriptiveName(locale)) %>" localizeLabel="<%= false %>" />

					<c:if test="<%= curGroup.isOrganization() %>">

						<%
						Organization organization = OrganizationLocalServiceUtil.getOrganization(curGroup.getOrganizationId());
						%>

						<span class="organization-type">(<liferay-ui:message key="<%= organization.getType() %>" />)</span>
					</c:if>

					<%
					List<String> names = TransformUtil.transform(OrganizationLocalServiceUtil.getGroupUserOrganizations(curGroup.getGroupId(), user.getUserId()), Organization::getName);

					names.addAll(TransformUtil.transform(UserGroupLocalServiceUtil.getGroupUserUserGroups(curGroup.getGroupId(), user.getUserId()), UserGroup::getName));
					%>

					<c:if test="<%= ListUtil.isNotEmpty(names) %>">

						<%
						String message = StringPool.BLANK;

						if (names.size() == 1) {
							message = LanguageUtil.format(request, "you-are-a-member-of-x-because-you-belong-to-x", new Object[] {HtmlUtil.escape(curGroup.getDescriptiveName(locale)), HtmlUtil.escape(names.get(0))}, false);
						}
						else {
							List<String> namesList = names.subList(0, names.size() - 1);

							message = LanguageUtil.format(request, "you-are-a-member-of-x-because-you-belong-to-x-and-x", new Object[] {HtmlUtil.escape(curGroup.getDescriptiveName(locale)), HtmlUtil.escape(StringUtil.merge(namesList.toArray(new String[names.size() - 1]), ", ")), HtmlUtil.escape(names.get(names.size() - 1))}, false);
						}
						%>

						<liferay-ui:icon-help message="<%= message %>" />
					</c:if>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smaller table-cell-ws-nowrap"
					name="child-sites"
				>
					<c:choose>
						<c:when test="<%= !curGroup.isCompany() %>">
							<liferay-ui:message arguments="<%= String.valueOf(childSites.size()) %>" key="x-child-sites" />
						</c:when>
						<c:otherwise>
							-
						</c:otherwise>
					</c:choose>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
					name="membership-type"
					value="<%= LanguageUtil.get(request, curGroup.getTypeLabel()) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-cell-minw-150"
					name="members"
				>
					<span class="lfr-portal-tooltip" title="<liferay-ui:message key="inherited-memberships-are-not-included-in-members-count" />">
						<div>

							<%
							int usersCount = UserLocalServiceUtil.getGroupUsersCount(curGroup.getGroupId(), WorkflowConstants.STATUS_APPROVED);
							%>

							<c:if test="<%= usersCount > 0 %>">
								<div class="user-count">
									<%= LanguageUtil.format(request, usersCount > 1 ? "x-users" : "x-user", usersCount, false) %>
								</div>
							</c:if>

							<%
							int organizationsCount = OrganizationLocalServiceUtil.getGroupOrganizationsCount(curGroup.getGroupId());
							%>

							<c:if test="<%= organizationsCount > 0 %>">
								<div class="organization-count">
									<%= LanguageUtil.format(request, organizationsCount > 1 ? "x-organizations" : "x-organization", organizationsCount, false) %>
								</div>
							</c:if>

							<%
							int userGroupsCount = UserGroupLocalServiceUtil.getGroupUserGroupsCount(curGroup.getGroupId());
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

				<c:if test="<%= PropsValues.LIVE_USERS_ENABLED %>">
					<liferay-ui:search-container-column-text
						name="online-now"
						value="<%= String.valueOf(LiveUsers.getGroupUsersCount(company.getCompanyId(), curGroup.getGroupId())) %>"
					/>
				</c:if>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-cell-ws-nowrap table-column-text-center"
					name="active"
					value='<%= LanguageUtil.get(request, (curGroup.isActive() ? "yes" : "no")) %>'
				/>

				<liferay-ui:search-container-column-text>
					<clay:dropdown-actions
						aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
						dropdownItems="<%= siteAdminDisplayContext.getActionDropdownItems(curGroup) %>"
						propsTransformer="{SiteDropdownDefaultPropsTransformer} from site-admin-web"
					/>
				</liferay-ui:search-container-column-text>
			</c:otherwise>
		</c:choose>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		displayStyle="<%= siteAdminDisplayContext.getDisplayStyle() %>"
		markupView="lexicon"
	/>
</liferay-ui:search-container>