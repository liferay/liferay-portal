<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DefaultUserAssociationsDisplayContext defaultUserAssociationsDisplayContext = (DefaultUserAssociationsDisplayContext)request.getAttribute(DefaultUserAssociationsDisplayContext.class.getName());
%>

<liferay-util:buffer
	var="removeRoleIcon"
>
	<clay:icon
		symbol="times-circle"
	/>
</liferay-util:buffer>

<p class="small text-secondary">
	<liferay-ui:message key="select-the-default-roles-and-teams-for-new-members" />
</p>

<clay:content-row
	containerElement="h3"
	cssClass="sheet-subtitle"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<span class="heading-text"><liferay-ui:message key="site-roles" /></span>
	</clay:content-col>

	<clay:content-col>
		<clay:button
			aria-label='<%= LanguageUtil.get(request, "select") %>'
			cssClass="modify-link"
			displayType="secondary"
			id='<%= liferayPortletResponse.getNamespace() + "selectSiteRoleLink" %>'
			label="select"
			small="<%= true %>"
			title="select"
		/>
	</clay:content-col>
</clay:content-row>

<liferay-ui:search-container
	compactEmptyResultsMessage="<%= true %>"
	emptyResultsMessage="none"
	headerNames="title,null"
	id="siteRolesSearchContainer"
	searchContainer="<%= defaultUserAssociationsDisplayContext.getSiteRolesSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.Role"
		keyPropertyName="roleId"
		modelVar="role"
	>
		<liferay-ui:search-container-column-text
			name="title"
			truncate="<%= true %>"
			value="<%= HtmlUtil.escape(role.getTitle(locale)) %>"
		/>

		<liferay-ui:search-container-column-text>
			<clay:button
				aria-label='<%= LanguageUtil.get(request, "remove") %>'
				borderless="<%= true %>"
				cssClass="lfr-portal-tooltip modify-link"
				data-rowId="<%= role.getRoleId() %>"
				displayType="secondary"
				icon="times-circle"
				monospaced="<%= true %>"
				title="remove"
				type="button"
			/>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
		paginate="<%= false %>"
	/>
</liferay-ui:search-container>

<clay:content-row
	containerElement="h3"
	cssClass="sheet-subtitle"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<span class="heading-text"><liferay-ui:message key="teams" /></span>
	</clay:content-col>

	<clay:content-col>
		<clay:button
			aria-label='<%= LanguageUtil.get(request, "select") %>'
			cssClass="modify-link"
			displayType="secondary"
			id='<%= liferayPortletResponse.getNamespace() + "selectTeamLink" %>'
			label="select"
			small="<%= true %>"
			title="select"
		/>
	</clay:content-col>
</clay:content-row>

<liferay-ui:search-container
	compactEmptyResultsMessage="<%= true %>"
	emptyResultsMessage="none"
	headerNames="title,null"
	id="teamsSearchContainer"
	searchContainer="<%= defaultUserAssociationsDisplayContext.getTeamsSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.Team"
		keyPropertyName="teamId"
		modelVar="team"
	>
		<liferay-ui:search-container-column-text
			name="title"
			truncate="<%= true %>"
			value="<%= HtmlUtil.escape(team.getName()) %>"
		/>

		<liferay-ui:search-container-column-text>
			<clay:button
				aria-label='<%= LanguageUtil.get(request, "remove") %>'
				borderless="<%= true %>"
				cssClass="lfr-portal-tooltip modify-link"
				data-rowId="<%= team.getTeamId() %>"
				displayType="secondary"
				icon="times-circle"
				monospaced="<%= true %>"
				title="remove"
				type="button"
			/>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
		paginate="<%= false %>"
	/>
</liferay-ui:search-container>

<aui:script use="liferay-search-container">
	const siteRolesSearchContainer = Liferay.SearchContainer.get(
		'<portlet:namespace />siteRolesSearchContainer'
	);

	siteRolesSearchContainer.get('contentBox').delegate(
		'click',
		(event) => {
			const link = event.currentTarget;

			siteRolesSearchContainer.deleteRow(
				link.ancestor('tr'),
				link.getAttribute('data-rowId')
			);
		},
		'.modify-link'
	);

	const selectSiteRoleLink = document.getElementById(
		'<portlet:namespace />selectSiteRoleLink'
	);

	selectSiteRoleLink.addEventListener('click', (event) => {
		let searchContainerData = siteRolesSearchContainer.getData();

		if (!searchContainerData.length) {
			searchContainerData = [];
		}
		else {
			searchContainerData = searchContainerData.split(',');
		}

		const ids = document.getElementById(
			'<portlet:namespace />siteRolesSearchContainerPrimaryKeys'
		).value;

		const uri = new URL(
			'<%= defaultUserAssociationsDisplayContext.getSelectSiteRoleURL() %>'
		);

		uri.searchParams.set(
			'<%= defaultUserAssociationsDisplayContext.getSelectSiteRolePortletNamespace() %>roleIds',
			ids
		);

		Liferay.Util.openSelectionModal({
			onSelect: function (event) {
				const entityId = event.entityid;

				const rowColumns = [
					Liferay.Util.escape(event.entityname),
					'<button aria-label="<%= LanguageUtil.get(request, "remove") %>" class="btn btn-monospaced btn-outline-borderless btn-outline-secondary float-right lfr-portal-tooltip modify-link" data-rowId="' +
						entityId +
						'" title="<%= LanguageUtil.get(request, "remove") %>" type="button"><%= UnicodeFormatter.toString(removeRoleIcon) %></button>',
				];

				siteRolesSearchContainer.addRow(rowColumns, entityId);

				siteRolesSearchContainer.updateDataStore();
			},
			selectEventName: '<portlet:namespace />selectSiteRole',
			selectedData: searchContainerData,
			title: '<liferay-ui:message arguments="site-role" key="select-x" />',
			url: uri.toString(),
		});
	});

	const teamsSearchContainer = Liferay.SearchContainer.get(
		'<portlet:namespace />teamsSearchContainer'
	);

	teamsSearchContainer.get('contentBox').delegate(
		'click',
		(event) => {
			const link = event.currentTarget;

			teamsSearchContainer.deleteRow(
				link.ancestor('tr'),
				link.getAttribute('data-rowId')
			);
		},
		'.modify-link'
	);

	const selectTeamLink = document.getElementById(
		'<portlet:namespace />selectTeamLink'
	);

	selectTeamLink.addEventListener('click', (event) => {
		let searchContainerData = teamsSearchContainer.getData();

		if (!searchContainerData.length) {
			searchContainerData = [];
		}
		else {
			searchContainerData = searchContainerData.split(',');
		}

		const ids = document.getElementById(
			'<portlet:namespace />teamsSearchContainerPrimaryKeys'
		).value;

		Liferay.Util.openSelectionModal({
			id: '<portlet:namespace />selectTeam',
			onSelect: function (event) {
				const valueJSON = JSON.parse(event.value);

				const rowColumns = [
					Liferay.Util.escape(valueJSON.name),
					'<button aria-label="<%= LanguageUtil.get(request, "remove") %>" class="btn btn-monospaced btn-outline-borderless btn-outline-secondary float-right lfr-portal-tooltip modify-link" data-rowId="' +
						valueJSON.teamId +
						'" title="<%= LanguageUtil.get(request, "remove") %>" type="button"><%= UnicodeFormatter.toString(removeRoleIcon) %></button>',
				];

				teamsSearchContainer.addRow(rowColumns, valueJSON.teamId);

				teamsSearchContainer.updateDataStore();
			},
			selectEventName: '<portlet:namespace />selectTeam',
			selectedData: searchContainerData,
			title: '<liferay-ui:message arguments="team" key="select-x" />',
			url: '<%= defaultUserAssociationsDisplayContext.getSelectTeamURL() %>',
		});
	});
</aui:script>