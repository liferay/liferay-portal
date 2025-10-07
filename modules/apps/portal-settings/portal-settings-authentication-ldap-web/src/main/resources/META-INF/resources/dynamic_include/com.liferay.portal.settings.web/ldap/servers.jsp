<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<%
ConfigurationProvider<LDAPAuthConfiguration> ldapAuthConfigurationProvider = ConfigurationProviderUtil.getLDAPAuthConfigurationProvider();

long companyId = 0L;

String portletId = PortalUtil.getPortletId(request);

if (portletId.equals(ConfigurationAdminPortletKeys.INSTANCE_SETTINGS)) {
	companyId = themeDisplay.getCompanyId();
}

LDAPAuthConfiguration ldapAuthConfiguration = ldapAuthConfigurationProvider.getConfiguration(companyId);

ConfigurationProvider<LDAPServerConfiguration> ldapServerConfigurationProvider = ConfigurationProviderUtil.getLDAPServerConfigurationProvider();

List<LDAPServerConfiguration> ldapServerConfigurations = ldapServerConfigurationProvider.getConfigurations(companyId, false);

String authenticationURL = currentURL + "#_LFR_FN_authentication";

boolean ldapAuthEnabled = ldapAuthConfiguration.enabled();
%>

<c:if test="<%= ldapAuthEnabled && ldapServerConfigurations.isEmpty() %>">
	<div class="alert alert-info">
		<liferay-ui:message key="default-ldap-server-settings-are-in-use-please-add-an-ldap-server-to-override-the-default-settings" />
	</div>
</c:if>

<aui:button-row>
	<aui:button
		href='<%=
			PortletURLBuilder.createRenderURL(
				renderResponse
			).setMVCRenderCommandName(
				"/portal_settings_authentication_ldap/edit_ldap_server"
			).setRedirect(
				authenticationURL
			).buildString()
		%>'
		name="addButton"
		value="add"
	/>
</aui:button-row>

<aui:fieldset>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= LDAPSettingsConstants.CMD_UPDATE_SERVER %>" />

	<aui:input name='<%= "ldap--" + LDAPConstants.AUTH_SERVER_PRIORITY + "--" %>' type="hidden" />

	<c:if test="<%= !ldapServerConfigurations.isEmpty() %>">
		<br /><br />

		<div class="ldap-servers searchcontainer-content">
			<table class="table table-bordered table-hover table-striped">
				<thead class="table-columns">
					<tr>
						<th class="table-header">
							<liferay-ui:message key="ldap-server-id" />
						</th>
						<th class="table-header">
							<liferay-ui:message key="name" />
						</th>
						<th class="table-header"></th>
					</tr>
				</thead>

				<tbody class="table-data">

					<%
					for (LDAPServerConfiguration ldapServerConfiguration : ldapServerConfigurations) {
						long ldapServerId = ldapServerConfiguration.ldapServerId();

						String ldapServerName = ldapServerConfiguration.serverName();
					%>

						<tr data-ldapServerId="<%= ldapServerId %>">
							<td class="table-cell">
								<%= ldapServerId %>
							</td>
							<td class="table-cell">
								<%= HtmlUtil.escape(ldapServerName) %>
							</td>
							<td align="right" class="table-cell">
								<div class="control">
									<c:if test="<%= ldapServerConfigurations.size() > 1 %>">
										<liferay-ui:icon
											icon="order-arrow-up"
											markupView="lexicon"
											message="up"
											url='<%= "javascript:" + liferayPortletResponse.getNamespace() + "raiseLDAPServerPriority(" + ldapServerId + ");" %>'
										/>

										<liferay-ui:icon
											icon="order-arrow-down"
											markupView="lexicon"
											message="down"
											url='<%= "javascript:" + liferayPortletResponse.getNamespace() + "lowerLDAPServerPriority(" + ldapServerId + ");" %>'
										/>
									</c:if>

									<portlet:renderURL var="editURL">
										<portlet:param name="mvcRenderCommandName" value="/portal_settings_authentication_ldap/edit_ldap_server" />
										<portlet:param name="redirect" value="<%= authenticationURL %>" />
										<portlet:param name="ldapServerId" value="<%= String.valueOf(ldapServerId) %>" />
									</portlet:renderURL>

									<liferay-ui:icon
										icon="pencil"
										markupView="lexicon"
										message="edit"
										url="<%= editURL %>"
									/>

									<portlet:actionURL name="/portal_settings_authentication_ldap/edit_ldap_server" var="deleteURL">
										<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.DELETE %>" />
										<portlet:param name="redirect" value="<%= authenticationURL %>" />
										<portlet:param name="ldapServerId" value="<%= String.valueOf(ldapServerId) %>" />
									</portlet:actionURL>

									<liferay-ui:icon-delete
										showIcon="<%= true %>"
										url="<%= deleteURL %>"
									/>
								</div>
							</td>
						</tr>

					<%
					}
					%>

				</tbody>
			</table>
		</div>
	</c:if>
</aui:fieldset>

<aui:script>
	function <portlet:namespace />changeLDAPServerPriority(ldapServerId, action) {
		var ldapServer = document.querySelector(
			'.ldap-servers tr[data-ldapServerId="' + ldapServerId + '"]'
		);

		if (ldapServer) {
			var swapLdapServer = ldapServer.nextElementSibling;

			if (action === 'raise') {
				swapLdapServer = ldapServer.previousElementSibling;
			}

			if (swapLdapServer) {
				var parentNode = ldapServer.parentNode;

				if (action === 'raise') {
					parentNode.insertBefore(ldapServer, swapLdapServer);
				}
				else {
					parentNode.insertBefore(swapLdapServer, ldapServer);
				}
			}
		}

		<portlet:namespace />saveLdap();
	}

	function <portlet:namespace />lowerLDAPServerPriority(ldapServerId) {
		<portlet:namespace />changeLDAPServerPriority(ldapServerId, 'lower');
	}

	function <portlet:namespace />raiseLDAPServerPriority(ldapServerId) {
		<portlet:namespace />changeLDAPServerPriority(ldapServerId, 'raise');
	}

	function <portlet:namespace />saveLdap() {
		var ldapServerIdsNodes = document.querySelectorAll(
			'.ldap-servers .table-data tr'
		);

		var ldapServerIds = Array.prototype.map.call(
			ldapServerIdsNodes,
			(ldapServerIdsNode) => {
				return ldapServerIdsNode.dataset.ldapserverid;
			}
		);

		Liferay.Util.setFormValues(document.<portlet:namespace />fm, {
			'ldap--<%= LDAPConstants.AUTH_SERVER_PRIORITY %>--':
				ldapServerIds.join(','),
		});
	}

	Liferay.Util.toggleBoxes(
		'<portlet:namespace />ldapImportEnabled',
		'<portlet:namespace />importEnabledSettings'
	);
</aui:script>