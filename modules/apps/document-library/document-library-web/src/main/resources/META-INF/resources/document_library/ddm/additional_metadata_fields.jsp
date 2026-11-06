<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/document_library/init.jsp" %>

<%
DLFileEntryAdditionalMetadataSetsDisplayContext dlFileEntryAdditionalMetadataSetsDisplayContext = new DLFileEntryAdditionalMetadataSetsDisplayContext(request, renderResponse);
%>

<liferay-util:buffer
	var="removeStructureIcon"
>
	<clay:icon
		symbol="times-circle"
	/>
</liferay-util:buffer>

<aui:model-context bean="<%= dlFileEntryAdditionalMetadataSetsDisplayContext.getDLFileEntryType() %>" model="<%= DLFileEntryType.class %>" />

<liferay-ui:search-container
	headerNames="name,null"
	total="<%= dlFileEntryAdditionalMetadataSetsDisplayContext.getDDMStructuresCount() %>"
>
	<liferay-ui:search-container-results
		results="<%= dlFileEntryAdditionalMetadataSetsDisplayContext.getDDMStructures() %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.dynamic.data.mapping.model.DDMStructure"
		escapedModel="<%= true %>"
		keyPropertyName="structureId"
		modelVar="curDDMStructure"
	>
		<liferay-ui:search-container-column-text
			name="name"
			value="<%= HtmlUtil.escape(curDDMStructure.getName(locale)) %>"
		/>

		<liferay-ui:search-container-column-text>
			<a class="modify-link" data-rowId="<%= curDDMStructure.getStructureId() %>" href="javascript:void(0);" title="<%= LanguageUtil.get(request, "remove") %>"><%= removeStructureIcon %></a>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
		paginate="<%= false %>"
	/>
</liferay-ui:search-container>

<div class="mt-3">
	<liferay-ui:icon
		cssClass="modify-link select-metadata"
		label="<%= true %>"
		linkCssClass="btn btn-secondary"
		message="select"
		url='<%= "javascript:" + liferayPortletResponse.getNamespace() + "openDDMStructureSelector();" %>'
	/>
</div>

<aui:script>
	function <portlet:namespace />openDDMStructureSelector() {
		Liferay.Util.openSelectionModal({
			id: '<portlet:namespace />selectDDMStructure',
			onSelect: function (selectedItem) {
				const itemValue = JSON.parse(selectedItem.value);

				var searchContainer = Liferay.SearchContainer.get(
					'<portlet:namespace />ddmStructuresSearchContainer'
				);

				var data = searchContainer.getData(false);

				if (!data.includes(itemValue.ddmstructureid)) {
					var ddmStructureLink =
						'<a class="modify-link" data-rowId="' +
						itemValue.ddmstructureid +
						'" href="javascript:void(0);" title="<%= LanguageUtil.get(request, "remove") %>"><%= UnicodeFormatter.toString(removeStructureIcon) %></a>';

					searchContainer.addRow(
						[Liferay.Util.escapeHTML(itemValue.name), ddmStructureLink],
						itemValue.ddmstructureid
					);

					searchContainer.updateDataStore();
				}
			},
			selectEventName: '<portlet:namespace />selectDDMStructure',
			title: '<%= UnicodeLanguageUtil.get(request, "select-metadata-set") %>',
			url: '<%= dlFileEntryAdditionalMetadataSetsDisplayContext.getSelectDDMStructureURL() %>',
		});
	}
</aui:script>

<aui:script use="liferay-search-container">
	var searchContainer = Liferay.SearchContainer.get(
		'<portlet:namespace />ddmStructuresSearchContainer'
	);

	searchContainer.get('contentBox').delegate(
		'click',
		(event) => {
			var link = event.currentTarget;

			var tr = link.ancestor('tr');

			searchContainer.deleteRow(tr, link.getAttribute('data-rowId'));
		},
		'.modify-link'
	);
</aui:script>