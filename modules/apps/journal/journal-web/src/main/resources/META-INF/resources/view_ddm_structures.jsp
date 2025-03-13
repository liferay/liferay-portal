<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
JournalDDMStructuresDisplayContext journalDDMStructuresDisplayContext = new JournalDDMStructuresDisplayContext(renderRequest, renderResponse);

JournalDDMStructuresManagementToolbarDisplayContext journalDDMStructuresManagementToolbarDisplayContext = new JournalDDMStructuresManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, journalDDMStructuresDisplayContext);
%>

<clay:navigation-bar
	inverted="<%= true %>"
	navigationItems='<%= journalDisplayContext.getNavigationItems("structures") %>'
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= journalDDMStructuresManagementToolbarDisplayContext %>"
	propsTransformer="{DDMStructuresManagementToolbarPropsTransformer} from journal-web"
/>

<portlet:actionURL copyCurrentRenderParameters="<%= true %>" name="/journal/delete_data_definition" var="deleteDataDefinitionURL">
	<portlet:param name="mvcPath" value="/view_ddm_structures.jsp" />
</portlet:actionURL>

<div>
	<react:component
		module="{ImportAndOverrideDataDefinitionModal} from journal-web"
	/>
</div>

<aui:form action="<%= deleteDataDefinitionURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

	<liferay-ui:success key="importDataDefinitionSuccessMessage" message="the-structure-was-successfully-imported" />

	<liferay-ui:error embed="<%= false %>" key="importDataDefinitionErrorMessage">
		<c:choose>
			<c:when test="<%= errorException instanceof DataDefinitionValidationException.MustNotDuplicateFieldName %>">

				<%
				DataDefinitionValidationException.MustNotDuplicateFieldName mndfn = (DataDefinitionValidationException.MustNotDuplicateFieldName)errorException;
				%>

				<liferay-ui:message arguments="<%= HtmlUtil.escape(StringUtil.merge(mndfn.getDuplicatedFieldNames(), StringPool.COMMA_AND_SPACE)) %>" key="the-definition-field-name-x-was-defined-more-than-once" translateArguments="<%= false %>" />
			</c:when>
			<c:when test="<%= errorException instanceof DataDefinitionValidationException.MustSetFields %>">
				<liferay-ui:message key="at-least-one-field-must-be-added" />
			</c:when>
			<c:when test="<%= errorException instanceof DataDefinitionValidationException.MustSetOptionsForField %>">

				<%
				DataDefinitionValidationException.MustSetOptionsForField msoff = (DataDefinitionValidationException.MustSetOptionsForField)errorException;
				%>

				<liferay-ui:message arguments="<%= HtmlUtil.escape(msoff.getFieldLabel()) %>" key="at-least-one-option-should-be-set-for-field-x" translateArguments="<%= false %>" />
			</c:when>
			<c:when test="<%= errorException instanceof DataDefinitionValidationException.MustSetValidCharactersForFieldName %>">

				<%
				DataDefinitionValidationException.MustSetValidCharactersForFieldName msvcffn = (DataDefinitionValidationException.MustSetValidCharactersForFieldName)errorException;
				%>

				<liferay-ui:message arguments="<%= HtmlUtil.escape(msvcffn.getFieldName()) %>" key="invalid-characters-were-defined-for-field-name-x" translateArguments="<%= false %>" />
			</c:when>
			<c:when test="<%= errorException instanceof DataDefinitionValidationException.MustSetValidName %>">
				<liferay-ui:message key="please-enter-a-valid-name" />
			</c:when>
			<c:when test="<%= errorException instanceof DataDefinitionValidationException %>">
				<liferay-ui:message key="please-enter-a-valid-form-definition" />
			</c:when>
			<c:when test="<%= errorException instanceof DataLayoutValidationException.MustNotDuplicateFieldName %>">

				<%
				DataLayoutValidationException.MustNotDuplicateFieldName mndfn = (DataLayoutValidationException.MustNotDuplicateFieldName)errorException;
				%>

				<liferay-ui:message arguments="<%= HtmlUtil.escape(StringUtil.merge(mndfn.getDuplicatedFieldNames(), StringPool.COMMA_AND_SPACE)) %>" key="the-definition-field-name-x-was-defined-more-than-once" translateArguments="<%= false %>" />
			</c:when>
			<c:when test="<%= errorException instanceof DataLayoutValidationException %>">
				<liferay-ui:message key="please-enter-a-valid-form-layout" />
			</c:when>
			<c:when test="<%= errorException instanceof PrincipalException.MustHavePermission %>">
				<liferay-ui:message key="you-do-not-have-the-required-permissions" />
			</c:when>
			<c:otherwise>

				<%
				Exception exception = (Exception)errorException;
				%>

				<c:choose>
					<c:when test="<%= Validator.isNotNull(exception.getMessage()) %>">
						<liferay-ui:message key="<%= exception.getMessage() %>" />
					</c:when>
					<c:otherwise>
						<liferay-ui:message key="the-structure-failed-to-import" />
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</liferay-ui:error>

	<liferay-ui:error exception="<%= RequiredStructureException.MustNotDeleteStructureReferencedByStructureLinks.class %>" message="the-structure-cannot-be-deleted-because-it-is-required-by-one-or-more-structure-links" />
	<liferay-ui:error exception="<%= RequiredStructureException.MustNotDeleteStructureReferencedByTemplates.class %>" message="the-structure-cannot-be-deleted-because-it-is-required-by-one-or-more-templates" />
	<liferay-ui:error exception="<%= RequiredStructureException.MustNotDeleteStructureThatHasChild.class %>" message="the-structure-cannot-be-deleted-because-it-has-one-or-more-substructures" />

	<c:if test="<%= !journalDisplayContext.isNavigationMine() && !journalDisplayContext.isNavigationRecent() %>">
		<liferay-site-navigation:breadcrumb
			breadcrumbEntries="<%= new ArrayList<>() %>"
		/>
	</c:if>

	<liferay-ui:search-container
		id="ddmStructures"
		searchContainer="<%= journalDDMStructuresDisplayContext.getDDMStructureSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.dynamic.data.mapping.model.DDMStructure"
			keyProperty="structureId"
			modelVar="ddmStructure"
		>
			<liferay-ui:search-container-column-text
				name="id"
				property="structureId"
			/>

			<%
			String rowHREF = StringPool.BLANK;

			if (DDMStructurePermission.contains(permissionChecker, ddmStructure, ActionKeys.UPDATE)) {
				rowHREF = PortletURLBuilder.createRenderURL(
					renderResponse
				).setMVCPath(
					"/edit_data_definition.jsp"
				).setRedirect(
					currentURL
				).setParameter(
					"ddmStructureId", ddmStructure.getStructureId()
				).buildString();
			}

			row.setData(
				HashMapBuilder.<String, Object>put(
					"actions", journalDDMStructuresManagementToolbarDisplayContext.getAvailableActions(ddmStructure)
				).build());
			%>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand table-cell-minw-200 table-title"
				href="<%= rowHREF %>"
				name="name"
				value="<%= HtmlUtil.escape(ddmStructure.getName(locale, true)) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand table-cell-minw-200"
				name="description"
				truncate="<%= true %>"
				value="<%= HtmlUtil.escape(ddmStructure.getDescription(locale, true)) %>"
			/>

			<%
			Group group = GroupLocalServiceUtil.getGroup(ddmStructure.getGroupId());
			%>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-minw-150"
				name="scope"
				value="<%= LanguageUtil.get(request, group.getScopeLabel(themeDisplay)) %>"
			/>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-ws-nowrap"
				name="modified-date"
				value="<%= ddmStructure.getModifiedDate() %>"
			/>

			<liferay-ui:search-container-column-text>

				<%
				DDMStructureActionDropdownItemsProvider ddmStructureActionDropdownItemsProvider = new DDMStructureActionDropdownItemsProvider(ddmStructure, liferayPortletRequest, liferayPortletResponse);
				%>

				<clay:dropdown-actions
					aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
					dropdownItems="<%= ddmStructureActionDropdownItemsProvider.getActionDropdownItems() %>"
					propsTransformer="{DDMStructrureElementsDefaultPropsTransformer} from journal-web"
				/>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="list"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>