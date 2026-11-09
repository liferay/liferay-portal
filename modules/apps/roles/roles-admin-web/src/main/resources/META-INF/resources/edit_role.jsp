<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String backURL = ParamUtil.getString(request, "backURL", redirect);

long roleId = ParamUtil.getLong(request, "roleId");

Role role = RoleServiceUtil.fetchRole(roleId);

String roleName = null;

if (role != null) {
	roleName = role.getName();
}

String subtype = BeanParamUtil.getString(role, request, "subtype");

RoleTypeContributor currentRoleTypeContributor = RoleTypeContributorRetrieverUtil.getCurrentRoleTypeContributor(request);

if (Validator.isNull(subtype)) {
	subtype = currentRoleTypeContributor.getDefaultSubtype();
}

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(backURL);
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

renderResponse.setTitle((role == null) ? LanguageUtil.get(request, "new-role") : role.getTitle(locale));
%>

<c:if test="<%= role != null %>">
	<liferay-util:include page="/edit_role_tabs.jsp" servletContext="<%= application %>" />

	<c:choose>
		<c:when test="<%= currentRoleTypeContributor.getType() == RoleConstants.TYPE_REGULAR %>">
			<liferay-ui:success key="roleCreated" message='<%= LanguageUtil.format(request, "x-was-created-successfully.-you-can-now-define-its-permissions-and-assign-users", HtmlUtil.escape(roleName)) %>' />
		</c:when>
		<c:otherwise>
			<liferay-ui:success key="roleCreated" message='<%= LanguageUtil.format(request, "x-was-created-successfully.-you-can-now-define-its-permissions", HtmlUtil.escape(roleName)) %>' />
		</c:otherwise>
	</c:choose>
</c:if>

<portlet:actionURL name="editRole" var="editRoleURL">
	<portlet:param name="mvcPath" value="/edit_role.jsp" />
	<portlet:param name="backURL" value="<%= backURL %>" />
</portlet:actionURL>

<portlet:renderURL var="editRoleRenderURL">
	<portlet:param name="mvcPath" value="/edit_role.jsp" />
	<portlet:param name="tabs1" value="details" />
	<portlet:param name="backURL" value="<%= backURL %>" />
	<portlet:param name="roleId" value="<%= String.valueOf(roleId) %>" />
	<portlet:param name="roleType" value="<%= String.valueOf(currentRoleTypeContributor.getType()) %>" />
</portlet:renderURL>

<aui:form action="<%= editRoleURL %>" cssClass="container-fluid container-fluid-max-xl container-form-view" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= editRoleRenderURL %>" />
	<aui:input name="roleId" type="hidden" value="<%= roleId %>" />

	<liferay-ui:error exception="<%= DuplicateRoleException.class %>" message="please-enter-a-unique-name" />
	<liferay-ui:error exception="<%= RequiredRoleException.class %>" message="old-role-name-is-a-required-system-role" />
	<liferay-ui:error exception="<%= RoleSubtypeException.class %>" message="please-enter-a-valid-subtype" />

	<aui:model-context bean="<%= role %>" model="<%= Role.class %>" />

	<div class="mt-4 sheet">
		<div class="panel-group panel-group-flush">
			<aui:fieldset>
				<aui:input label="type" name="typeLabel" type="resource" value="<%= LanguageUtil.get(request, currentRoleTypeContributor.getName()) %>" />

				<c:if test="<%= role == null %>">
					<aui:input name="roleType" type="hidden" value="<%= String.valueOf(currentRoleTypeContributor.getType()) %>" />
				</c:if>

				<aui:input helpMessage="title-field-help" name="title" />
				<aui:input name="description" />

				<%
				String[] subtypes = currentRoleTypeContributor.getSubtypes();
				%>

				<c:if test="<%= subtypes.length > 0 %>">
					<aui:select name="subtype">

						<%
						for (String curSubtype : subtypes) {
						%>

							<aui:option label="<%= curSubtype %>" selected="<%= subtype.equals(curSubtype) %>" />

						<%
						}
						%>

					</aui:select>
				</c:if>

				<%
				String nameLabel = LanguageUtil.get(request, "role-key");
				%>

				<liferay-ui:error exception="<%= RoleNameException.class %>">
					<p>
						<liferay-ui:message arguments="<%= new String[] {nameLabel, RoleConstants.getNameGeneralRestrictions(locale, PropsValues.ROLES_NAME_ALLOW_NUMERIC), RoleConstants.NAME_RESERVED_WORDS} %>" key="the-x-cannot-be-x-or-a-reserved-word-such-as-x" />
					</p>

					<p>
						<liferay-ui:message arguments="<%= new String[] {nameLabel, RoleConstants.NAME_INVALID_CHARACTERS} %>" key="the-x-cannot-contain-the-following-invalid-characters-x" />
					</p>
				</liferay-ui:error>

				<c:choose>
					<c:when test="<%= RoleConstants.isUnmodifiable(role) %>">
						<aui:input disabled="<%= true %>" helpMessage="key-field-help" label="key" name="viewNameField" type="text" value="<%= roleName %>" />
						<aui:input name="name" type="hidden" value="<%= roleName %>" />
					</c:when>
					<c:otherwise>
						<aui:input helpMessage="key-field-help" label="key" name="name" />
					</c:otherwise>
				</c:choose>

				<c:if test="<%= (role != null) && roleName.equals(RoleConstants.SITE_ADMINISTRATOR) %>">
					<aui:input helpMessage="allow-subsite-management-help" inlineLabel="right" label="allow-subsite-management" labelCssClass="simple-toggle-switch" name="manageSubgroups" type="toggle-switch" value="<%= ResourcePermissionLocalServiceUtil.hasResourcePermission(company.getCompanyId(), Group.class.getName(), ResourceConstants.SCOPE_GROUP_TEMPLATE, String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID), roleId, ActionKeys.MANAGE_SUBGROUPS) %>" />
				</c:if>

				<%
				ExpandoBridge roleExpandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(company.getCompanyId(), Role.class.getName(), (role != null) ? role.getRoleId() : 0);

				Map<String, Serializable> roleCustomAttributes = roleExpandoBridge.getAttributes();
				%>

				<c:if test="<%= !roleCustomAttributes.isEmpty() %>">
					<div class="sheet">
						<div class="panel-group panel-group-flush">
							<aui:fieldset>
								<liferay-expando:custom-attribute-list
									className="<%= Role.class.getName() %>"
									classPK="<%= (role != null) ? role.getRoleId() : 0 %>"
									editable="<%= true %>"
									label="<%= true %>"
								/>
							</aui:fieldset>
						</div>
					</div>
				</c:if>

				<aui:button-row>
					<aui:button type="submit" />

					<aui:button href="<%= backURL %>" type="cancel" />
				</aui:button-row>
			</aui:fieldset>
		</div>
	</div>
</aui:form>

<c:if test="<%= role == null %>">
	<aui:script sandbox="<%= true %>">
		var form = document.getElementById('<portlet:namespace />fm');

		if (form) {
			var nameInput = form.querySelector('#<portlet:namespace />name');
			var titleInput = form.querySelector('#<portlet:namespace />title');

			if (nameInput && titleInput) {
				var handleOnTitleInput = function (event) {
					var value = event.target.value;

					if (nameInput.hasAttribute('maxLength')) {
						value = value.substring(0, nameInput.getAttribute('maxLength'));
					}

					nameInput.value = value;
				};

				titleInput.addEventListener(
					'input',
					Liferay.Util.debounce(handleOnTitleInput, 200)
				);
			}
		}
	</aui:script>
</c:if>