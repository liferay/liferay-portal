<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
MenuAccessConfigurationDisplayContext menuAccessConfigurationDisplayContext = (MenuAccessConfigurationDisplayContext)request.getAttribute(MenuAccessConfigurationDisplayContext.class.getName());

String textCssClass = menuAccessConfigurationDisplayContext.isShowControlMenuByRole() ? "modify-text" : "modify-text text-muted";
%>

<liferay-util:buffer
	var="removeRoleIcon"
>
	<clay:icon
		symbol="times-circle"
	/>
</liferay-util:buffer>

<liferay-ui:error exception="<%= ConfigurationException.class %>" message="there-was-an-error-processing-one-or-more-of-the-configurations" />

<clay:sheet-section>
	<clay:content-row
		containerElement="h3"
		cssClass="sheet-subtitle"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<span class="heading-text"><liferay-ui:message key="control-menu" /></span>
		</clay:content-col>
	</clay:content-row>

	<clay:content-row>
		<clay:content-col>
			<clay:checkbox
				aria-describedby='<%= liferayPortletResponse.getNamespace() + "showControlMenuByRoleDescription" %>'
				checked="<%= menuAccessConfigurationDisplayContext.isShowControlMenuByRole() %>"
				id='<%= liferayPortletResponse.getNamespace() + "showControlMenuByRole" %>'
				label="show-control-menu-by-role-name"
				name='<%= liferayPortletResponse.getNamespace() + "showControlMenuByRole" %>'
			/>

			<p class="text-secondary" id="<portlet:namespace />showControlMenuByRoleDescription"><liferay-ui:message key="when-checked-the-control-menu-will-be-only-displayed-for-the-specified-roles" /></p>
		</clay:content-col>
	</clay:content-row>
</clay:sheet-section>

<clay:sheet-section>
	<clay:content-row
		containerElement="h3"
		cssClass="sheet-subtitle"
	>
		<clay:content-col
			cssClass="<%= textCssClass %>"
			expand="<%= true %>"
		>
			<span class="heading-text"><liferay-ui:message key="roles-can-see-control-menu-name" /></span>
		</clay:content-col>

		<clay:content-col>
			<clay:button
				aria-label='<%= LanguageUtil.get(request, "select") %>'
				cssClass="modify-link"
				disabled="<%= !menuAccessConfigurationDisplayContext.isShowControlMenuByRole() %>"
				displayType="secondary"
				id='<%= liferayPortletResponse.getNamespace() + "selectRoleLink" %>'
				label="select"
				small="<%= true %>"
				title="select"
			/>
		</clay:content-col>
	</clay:content-row>

	<clay:content-row>
		<clay:content-col
			expand="<%= true %>"
		>
			<liferay-ui:search-container
				compactEmptyResultsMessage="<%= true %>"
				headerNames="title,null"
				id="roleSearchContainer"
				searchContainer="<%= menuAccessConfigurationDisplayContext.getSearchContainer() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.portal.kernel.model.Role"
					escapedModel="<%= true %>"
					keyProperty="roleId"
					modelVar="role"
				>
					<liferay-ui:search-container-column-text
						cssClass="<%= textCssClass %>"
						name="title"
						truncate="<%= true %>"
						value="<%= HtmlUtil.escape(role.getTitle(locale)) %>"
					/>

					<liferay-ui:search-container-column-text>
						<c:if test="<%= menuAccessConfigurationDisplayContext.isShowDeleteButton(role) %>">
							<clay:button
								aria-label='<%= LanguageUtil.get(request, "remove") %>'
								borderless="<%= true %>"
								cssClass="lfr-portal-tooltip modify-link"
								data-rowId="<%= role.getRoleId() %>"
								disabled="<%= !menuAccessConfigurationDisplayContext.isShowControlMenuByRole() %>"
								displayType="secondary"
								icon="times-circle"
								monospaced="<%= true %>"
								title="remove"
								type="button"
							/>
						</c:if>
					</liferay-ui:search-container-column-text>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
					paginate="<%= false %>"
				/>
			</liferay-ui:search-container>
		</clay:content-col>
	</clay:content-row>
</clay:sheet-section>

<liferay-frontend:component
	module="{MenuAccess} from site-admin-web"
/>

<aui:script use="liferay-search-container">
	const bindModifyLink = function (config) {
		const searchContainer = config.searchContainer;

		searchContainer.get('contentBox').delegate(
			'click',
			(event) => {
				const link = event.currentTarget;

				searchContainer.deleteRow(
					link.ancestor('tr'),
					link.getAttribute('data-rowId')
				);
			},
			'.modify-link'
		);
	};

	const bindSelectLink = function (config) {
		const searchContainer = config.searchContainer;

		const selectLink = document.getElementById(config.linkId);

		selectLink.addEventListener('click', (event) => {
			let searchContainerData = searchContainer.getData();

			if (!searchContainerData.length) {
				searchContainerData = [];
			}
			else {
				searchContainerData = searchContainerData.split(',');
			}

			Liferay.Util.openSelectionModal({
				onSelect: function (selectedItems) {
					if (!selectedItems || !selectedItems.length) {
						return;
					}
					selectedItems.map((item) => {
						if (!searchContainerData.includes(item.id)) {
							const rowColumns = [
								Liferay.Util.escape(item.name),
								'<button aria-label="<%= LanguageUtil.get(request, "remove") %>" class="btn btn-monospaced btn-outline-borderless btn-outline-secondary float-right lfr-portal-tooltip modify-link" data-rowId="' +
									item.id +
									'" title="<%= LanguageUtil.get(request, "remove") %>" type="button"><%= UnicodeFormatter.toString(removeRoleIcon) %></button>',
							];

							searchContainer.addRow(rowColumns, item.id);
						}
					});

					searchContainer.updateDataStore();
				},
				selectEventName: config.id,
				selectedData: searchContainerData,
				title: config.title,
				url: config.uri.toString(),
				multiple: true,
			});
		});
	};

	const roleConfig = {
		id: '<%= menuAccessConfigurationDisplayContext.getEventName() %>>',
		idAttr: 'roleid',
		inputId: '<portlet:namespace />roleSearchContainerPrimaryKeys',
		linkId: '<portlet:namespace />selectRoleLink',
		searchContainer: Liferay.SearchContainer.get(
			'<portlet:namespace />roleSearchContainer'
		),
		title: '<liferay-ui:message arguments="role" key="select-x" />',
		titleAttr: 'roletitle',
		uri: '<%= menuAccessConfigurationDisplayContext.getRoleItemSelectorURL() %>',
	};

	bindModifyLink(roleConfig);
	bindSelectLink(roleConfig);
</aui:script>