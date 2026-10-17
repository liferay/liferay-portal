<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/announcements/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", "sites");

List<Group> groups = GroupLocalServiceUtil.getUserGroups(user.getUserId(), true);
List<Organization> organizations = OrganizationLocalServiceUtil.getUserOrganizations(user.getUserId());
List<Role> roles = RoleLocalServiceUtil.getRoles(PortalUtil.getCompanyId(renderRequest));
List<UserGroup> userGroups = UserGroupLocalServiceUtil.getUserGroups(themeDisplay.getCompanyId());

String tabs1Names = "sites";

if (!organizations.isEmpty()) {
	tabs1Names = tabs1Names.concat(",organizations");
}

if (!userGroups.isEmpty()) {
	tabs1Names = tabs1Names.concat(",user-groups");
}

if (!roles.isEmpty()) {
	tabs1Names = tabs1Names.concat(",roles");
}

announcementsPortletInstanceConfiguration = ParameterMapUtil.setParameterMap(AnnouncementsPortletInstanceConfiguration.class, announcementsPortletInstanceConfiguration, request.getParameterMap(), "preferences--", "--");
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL">
	<portlet:param name="tabs1" value="<%= tabs1 %>" />
</liferay-portlet:renderURL>

<liferay-frontend:edit-form
	action="<%= configurationActionURL %>"
	method="post"
	name="fm"
	onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "saveConfigurations();" %>'
>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />

	<liferay-frontend:edit-form-body>
		<liferay-frontend:fieldset
			collapsible="<%= true %>"
			id="displaySettingsPanel"
			label="display-settings"
		>
			<aui:select label="maximum-items-to-display" name="preferences--pageDelta--" value="<%= announcementsPortletInstanceConfiguration.pageDelta() %>">

				<%
				for (int pageDeltaValue : PropsValues.ANNOUNCEMENTS_ENTRY_PAGE_DELTA_VALUES) {
				%>

					<aui:option label="<%= pageDeltaValue %>" selected="<%= announcementsDisplayContext.getPageDelta() == pageDeltaValue %>" />

				<%
				}
				%>

			</aui:select>
		</liferay-frontend:fieldset>

		<liferay-frontend:fieldset
			collapsible="<%= true %>"
			id="announcementsDisplayedPanel"
			label="announcements-displayed"
		>
			<aui:input cssClass="customize-announcements-displayed" id="customizeAnnouncementsDisplayed" name="preferences--customizeAnnouncementsDisplayed--" title="customize-announcements-displayed" type="checkbox" value="<%= announcementsDisplayContext.isCustomizeAnnouncementsDisplayed() %>" />

			<div class="<%= announcementsDisplayContext.isCustomizeAnnouncementsDisplayed() ? "" : "hide" %>" id="<portlet:namespace />announcementsDisplayed">
				<div class="alert alert-info">
					<liferay-ui:message key="general-annnouncements-will-always-be-shown-select-any-other-distribution-scopes-you-would-like-to-display" />
				</div>

				<liferay-ui:tabs
					names="<%= tabs1Names %>"
					param="tabs1"
					refresh="<%= false %>"
				>
					<c:if test="<%= !groups.isEmpty() %>">
						<liferay-ui:section>

							<%
							List<KeyValuePair> leftList = new ArrayList<KeyValuePair>();
							List<KeyValuePair> rightList = new ArrayList<KeyValuePair>();

							for (Group curGroup : groups) {
								if (!curGroup.isSite()) {
									continue;
								}

								String descriptiveName = curGroup.isOrganization() ? String.format("%s (%s)", curGroup.getDescriptiveName(locale), LanguageUtil.get(request, OrganizationConstants.TYPE_ORGANIZATION)) : curGroup.getDescriptiveName(locale);

								KeyValuePair keyValuePair = new KeyValuePair(HtmlUtil.escape(curGroup.getExternalReferenceCode()), descriptiveName);

								if (!announcementsDisplayContext.isScopeGroupSelected(curGroup)) {
									leftList.add(keyValuePair);
								}
								else {
									rightList.add(keyValuePair);
								}
							}
							%>

							<aui:input name="preferences--selectedScopeGroupExternalReferenceCodes--" type="hidden" />

							<div id="<portlet:namespace />ScopeGroupExternalReferenceCodesBoxes">
								<liferay-ui:input-move-boxes
									leftBoxName="availableScopeGroupExternalReferenceCodes"
									leftList="<%= leftList %>"
									leftTitle="available"
									rightBoxName="currentScopeGroupExternalReferenceCodes"
									rightList="<%= rightList %>"
									rightReorder="<%= Boolean.TRUE.toString() %>"
									rightTitle="in-use"
								/>
							</div>
						</liferay-ui:section>
					</c:if>

					<c:if test="<%= !organizations.isEmpty() %>">
						<liferay-ui:section>

							<%
							List<KeyValuePair> leftList = new ArrayList<KeyValuePair>();
							List<KeyValuePair> rightList = new ArrayList<KeyValuePair>();

							for (Organization organization : organizations) {
								KeyValuePair keyValuePair = new KeyValuePair(HtmlUtil.escape(organization.getExternalReferenceCode()), organization.getName());

								if (!announcementsDisplayContext.isScopeOrganizationSelected(organization)) {
									leftList.add(keyValuePair);
								}
								else {
									rightList.add(keyValuePair);
								}
							}
							%>

							<aui:input name="preferences--selectedScopeOrganizationExternalReferenceCodes--" type="hidden" />

							<div id="<portlet:namespace />ScopeOrganizationExternalReferenceCodesBoxes">
								<liferay-ui:input-move-boxes
									leftBoxName="availableScopeOrganizationExternalReferenceCodes"
									leftList="<%= leftList %>"
									leftTitle="available"
									rightBoxName="currentScopeOrganizationExternalReferenceCodes"
									rightList="<%= rightList %>"
									rightReorder="<%= Boolean.TRUE.toString() %>"
									rightTitle="in-use"
								/>
							</div>
						</liferay-ui:section>
					</c:if>

					<c:if test="<%= !userGroups.isEmpty() %>">
						<liferay-ui:section>

							<%
							List<KeyValuePair> leftList = new ArrayList<KeyValuePair>();
							List<KeyValuePair> rightList = new ArrayList<KeyValuePair>();

							for (UserGroup userGroup : userGroups) {
								KeyValuePair keyValuePair = new KeyValuePair(HtmlUtil.escape(userGroup.getExternalReferenceCode()), userGroup.getName());

								if (!announcementsDisplayContext.isScopeUserGroupSelected(userGroup)) {
									leftList.add(keyValuePair);
								}
								else {
									rightList.add(keyValuePair);
								}
							}
							%>

							<aui:input name="preferences--selectedScopeUserGroupExternalReferenceCodes--" type="hidden" />

							<div id="<portlet:namespace />ScopeUserGroupExternalReferenceCodesBoxes">
								<liferay-ui:input-move-boxes
									leftBoxName="availableScopeUserGroupExternalReferenceCodes"
									leftList="<%= leftList %>"
									leftTitle="available"
									rightBoxName="currentScopeUserGroupExternalReferenceCodes"
									rightList="<%= rightList %>"
									rightReorder="<%= Boolean.TRUE.toString() %>"
									rightTitle="in-use"
								/>
							</div>
						</liferay-ui:section>
					</c:if>

					<c:if test="<%= !roles.isEmpty() %>">
						<liferay-ui:section>

							<%
							List<KeyValuePair> leftList = new ArrayList<KeyValuePair>();
							List<KeyValuePair> rightList = new ArrayList<KeyValuePair>();

							for (Role role : roles) {
								KeyValuePair keyValuePair = new KeyValuePair(HtmlUtil.escape(role.getExternalReferenceCode()), role.getTitle(locale));

								if (!announcementsDisplayContext.isScopeRoleSelected(role)) {
									leftList.add(keyValuePair);
								}
								else {
									rightList.add(keyValuePair);
								}
							}
							%>

							<aui:input name="preferences--selectedScopeRoleExternalReferenceCodes--" type="hidden" />

							<div id="<portlet:namespace />ScopeRoleExternalReferenceCodesBoxes">
								<liferay-ui:input-move-boxes
									leftBoxName="availableScopeRoleExternalReferenceCodes"
									leftList="<%= leftList %>"
									leftTitle="available"
									rightBoxName="currentScopeRoleExternalReferenceCodes"
									rightList="<%= rightList %>"
									rightReorder="<%= Boolean.TRUE.toString() %>"
									rightTitle="in-use"
								/>
							</div>
						</liferay-ui:section>
					</c:if>
				</liferay-ui:tabs>
			</div>
		</liferay-frontend:fieldset>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<aui:script sandbox="<%= true %>">
	var form = document.getElementById('<portlet:namespace />fm');

	if (form) {
		var <portlet:namespace />modified = function (panel) {
			var modifiedNotice = panel.querySelector(
				'.panel-heading .sheet-subtitle .modified-notice'
			);

			if (!modifiedNotice) {
				var displayTitle = panel.querySelector(
					'.panel-heading .sheet-subtitle'
				);

				displayTitle.append(
					'<span class="modified-notice"> (<liferay-ui:message key="modified" />) </span>'
				);
			}
		};

		var customizeAnnouncementsDisplayedCheckbox = form.querySelector(
			'#<portlet:namespace />customizeAnnouncementsDisplayed'
		);

		if (customizeAnnouncementsDisplayedCheckbox) {
			customizeAnnouncementsDisplayedCheckbox.addEventListener(
				'change',
				() => {
					<portlet:namespace />modified(
						document.getElementById(
							'<portlet:namespace />announcementsDisplayedPanel'
						)
					);

					var announcementsDisplayed = form.querySelector(
						'#<portlet:namespace />announcementsDisplayed'
					);

					if (announcementsDisplayed) {
						announcementsDisplayed.classList.toggle('hide');
					}
				}
			);
		}
	}
</aui:script>

<aui:script>
	var <portlet:namespace />form = document.getElementById(
		'<portlet:namespace />fm'
	);

	if (<portlet:namespace />form) {
		var selected = <portlet:namespace />form.querySelectorAll('.left-selector');

		var selectedHTML = '';

		for (var i = selected.length - 1; i >= 0; --i) {
			selectedHTML = selectedHTML.concat(selected[i].innerHTML);
		}

		Liferay.on('inputmoveboxes:moveItem', () => {
			setTimeout(() => {
				var currSelectedHTML = '';

				for (var i = selected.length - 1; i >= 0; --i) {
					currSelectedHTML = currSelectedHTML.concat(
						selected[i].innerHTML
					);
				}

				if (selectedHTML != currSelectedHTML) {
					var announcementsDisplayedPanel = document.getElementById(
						'<portlet:namespace />announcementsDisplayedPanel'
					);

					if (announcementsDisplayedPanel) {
						modified(announcementsDisplayedPanel);
					}
				}
			});
		});

		var pageDeltaInput = <portlet:namespace />form.querySelector(
			'select[name=<portlet:namespace />preferences--pageDelta--]'
		);

		if (pageDeltaInput) {
			pageDeltaInput.addEventListener('change', (event) => {
				var displaySettingsPanel = document.getElementById(
					'<portlet:namespace />displaySettingsPanel'
				);

				if (displaySettingsPanel) {
					modified(displaySettingsPanel);
				}
			});
		}

		function <portlet:namespace />saveConfigurations() {
			var currentScopeGroupExternalReferenceCodes =
				<portlet:namespace />form.querySelector(
					'#<portlet:namespace />currentScopeGroupExternalReferenceCodes'
				);
			var selectedScopeGroupExternalReferenceCodes =
				<portlet:namespace />form.querySelector(
					'#<portlet:namespace />selectedScopeGroupExternalReferenceCodes'
				);

			if (
				currentScopeGroupExternalReferenceCodes &&
				selectedScopeGroupExternalReferenceCodes
			) {
				selectedScopeGroupExternalReferenceCodes.setAttribute(
					'value',
					getSelectedOptionValues(currentScopeGroupExternalReferenceCodes)
				);
			}

			var currentScopeOrganizationExternalReferenceCodes =
				<portlet:namespace />form.querySelector(
					'#<portlet:namespace />currentScopeOrganizationExternalReferenceCodes'
				);
			var selectedScopeOrganizationExternalReferenceCodes =
				<portlet:namespace />form.querySelector(
					'#<portlet:namespace />selectedScopeOrganizationExternalReferenceCodes'
				);

			if (
				currentScopeOrganizationExternalReferenceCodes &&
				selectedScopeOrganizationExternalReferenceCodes
			) {
				selectedScopeOrganizationExternalReferenceCodes.setAttribute(
					'value',
					getSelectedOptionValues(
						currentScopeOrganizationExternalReferenceCodes
					)
				);
			}

			var currentScopeRoleExternalReferenceCodes =
				<portlet:namespace />form.querySelector(
					'#<portlet:namespace />currentScopeRoleExternalReferenceCodes'
				);
			var selectedScopeRoleExternalReferenceCodes =
				<portlet:namespace />form.querySelector(
					'#<portlet:namespace />selectedScopeRoleExternalReferenceCodes'
				);

			if (
				currentScopeRoleExternalReferenceCodes &&
				selectedScopeRoleExternalReferenceCodes
			) {
				selectedScopeRoleExternalReferenceCodes.setAttribute(
					'value',
					getSelectedOptionValues(currentScopeRoleExternalReferenceCodes)
				);
			}

			var currentScopeUserGroupExternalReferenceCodes =
				<portlet:namespace />form.querySelector(
					'#<portlet:namespace />currentScopeUserGroupExternalReferenceCodes'
				);
			var selectedScopeUserGroupExternalReferenceCodes =
				<portlet:namespace />form.querySelector(
					'#<portlet:namespace />selectedScopeUserGroupExternalReferenceCodes'
				);

			if (
				currentScopeUserGroupExternalReferenceCodes &&
				selectedScopeUserGroupExternalReferenceCodes
			) {
				selectedScopeUserGroupExternalReferenceCodes.setAttribute(
					'value',
					getSelectedOptionValues(
						currentScopeUserGroupExternalReferenceCodes
					)
				);
			}

			submitForm(<portlet:namespace />form);
		}

		function getSelectedOptionValues(select) {
			return JSON.stringify(
				Array.from(select.getElementsByTagName('option')).map(
					(item) => item.value
				)
			);
		}
	}
</aui:script>