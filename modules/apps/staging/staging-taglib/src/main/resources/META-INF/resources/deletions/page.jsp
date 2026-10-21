<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/deletions/init.jsp" %>

<c:if test="<%= cmd.equals(Constants.EXPORT) || cmd.equals(Constants.IMPORT) || cmd.equals(Constants.PUBLISH) %>">
	<div aria-labelledby="<portlet:namespace />deletions" class="options-group" role="group">
		<clay:sheet-section>
			<span class="sheet-subtitle" id="<portlet:namespace />deletions">
				<liferay-ui:message key="deletions" />
			</span>

			<%
			StagingGroupHelper stagingGroupHelper = StagingGroupHelperUtil.getStagingGroupHelper();
			%>

			<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-44771") && !cmd.equals(Constants.EXPORT) && !stagingGroupHelper.isCompanyGroup(group) %>'>
				<c:if test="<%= cmd.equals(Constants.IMPORT) %>">
					<clay:alert
						cssClass="hide"
						displayType="warning"
						id='<%= liferayPortletResponse.getNamespace() + "deletePortletDataAlert" %>'
						message="this-option-does-not-apply-to-object-entries"
						title="delete-application-data-before-importing"
					/>
				</c:if>

				<liferay-staging:checkbox
					checked="<%= MapUtil.getBoolean(parameterMap, PortletDataHandlerKeys.DELETE_PORTLET_DATA, false) %>"
					disabled="<%= disableInputs %>"
					label="delete-application-data-before-importing"
					name="<%= PortletDataHandlerKeys.DELETE_PORTLET_DATA %>"
					suggestion="delete-content-before-importing-suggestion"
					warning="delete-content-before-importing-warning"
				/>
			</c:if>

			<%
			ExportImportServiceConfiguration exportImportServiceConfiguration = ConfigurationProviderUtil.getCompanyConfiguration(ExportImportServiceConfiguration.class, company.getCompanyId());
			%>

			<liferay-staging:checkbox
				checked="<%= MapUtil.getBoolean(parameterMap, PortletDataHandlerKeys.DELETIONS, exportImportServiceConfiguration.replicateIndividualDeletionsByDefault()) %>"
				disabled="<%= disableInputs %>"
				label="<%= individualDeletionsTitle %>"
				name="<%= PortletDataHandlerKeys.DELETIONS %>"
				warning="<%= individualDeletionsWarning %>"
			/>
		</clay:sheet-section>
	</div>
</c:if>