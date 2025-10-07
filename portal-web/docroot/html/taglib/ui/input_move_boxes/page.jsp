<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/taglib/init.jsp" %>

<%
String randomNamespace = PortalUtil.generateRandomKey(request, "taglib_ui_input_move_boxes_page") + StringPool.UNDERLINE;

String cssClass = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-move-boxes:cssClass"));

String leftTitle = LanguageUtil.get(resourceBundle, (String)request.getAttribute("liferay-ui:input-move-boxes:leftTitle"));
String rightTitle = LanguageUtil.get(resourceBundle, (String)request.getAttribute("liferay-ui:input-move-boxes:rightTitle"));

Integer leftBoxMaxItems = (Integer)request.getAttribute("liferay-ui:input-move-boxes:leftBoxMaxItems");
Integer rightBoxMaxItems = (Integer)request.getAttribute("liferay-ui:input-move-boxes:rightBoxMaxItems");

String leftBoxName = (String)request.getAttribute("liferay-ui:input-move-boxes:leftBoxName");
String rightBoxName = (String)request.getAttribute("liferay-ui:input-move-boxes:rightBoxName");

String leftOnChange = (String)request.getAttribute("liferay-ui:input-move-boxes:leftOnChange");
String rightOnChange = (String)request.getAttribute("liferay-ui:input-move-boxes:rightOnChange");

boolean leftReorder = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:input-move-boxes:leftReorder"));
boolean rightReorder = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:input-move-boxes:rightReorder"));

List leftList = (List)request.getAttribute("liferay-ui:input-move-boxes:leftList");
List rightList = (List)request.getAttribute("liferay-ui:input-move-boxes:rightList");

Map<String, Object> data = new HashMap<String, Object>();
%>

<div class="taglib-move-boxes <%= cssClass %> <%= leftReorder ? "left-reorder" : StringPool.BLANK %> <%= rightReorder ? "right-reorder" : StringPool.BLANK %>" id="<%= randomNamespace %>input-move-boxes">
	<div class="row selector-container">
		<div class="col-md-4 left-selector-column">
			<aui:select cssClass="choice-selector left-selector" label="<%= leftTitle %>" multiple="<%= true %>" name="<%= leftBoxName %>" onChange="<%= Validator.isNotNull(leftOnChange) ? leftOnChange : StringPool.BLANK %>">

				<%
				data.put("selected", true);

				for (int i = 0; i < leftList.size(); i++) {
					KeyValuePair kvp = (KeyValuePair)leftList.get(i);
				%>

					<aui:option data="<%= data %>" label="<%= kvp.getValue() %>" value="<%= kvp.getKey() %>" />

				<%
				}
				%>

			</aui:select>
		</div>

		<div class="col-md-1 move-arrow-buttons"></div>

		<div class="col-md-4 right-selector-column">
			<aui:select cssClass="choice-selector right-selector" label="<%= rightTitle %>" multiple="<%= true %>" name="<%= rightBoxName %>" onChange="<%= Validator.isNotNull(rightOnChange) ? rightOnChange : StringPool.BLANK %>">

				<%
				data.put("selected", false);

				for (int i = 0; i < rightList.size(); i++) {
					KeyValuePair kvp = (KeyValuePair)rightList.get(i);
				%>

					<aui:option data="<%= data %>" label="<%= kvp.getValue() %>" value="<%= kvp.getKey() %>" />

				<%
				}
				%>

			</aui:select>
		</div>
	</div>
</div>

<aui:script type="module">
	import React from '<%= FrontendESMUtil.buildExportsURL(themeDisplay, "frontend-js-react-web", "react") %>';
	import {ClayDualListBox} from '<%= FrontendESMUtil.buildExportsURL(themeDisplay, "frontend-taglib-clay", "@clayui/form") %>';
	import {render} from '<%= FrontendESMUtil.buildURL(themeDisplay, "frontend-js-react-web") %>';

	function main({initialItems}) {
		const [items, setItems] = React.useState(initialItems);

		return React.createElement(
			ClayDualListBox,
			{
				ariaLabels: {
					transferRTL: '<%= LanguageUtil.format(request, "move-selected-items-from-x-to-x", new Object[] {rightTitle, leftTitle}, false) %>',
					transferLTR: '<%= LanguageUtil.format(request, "move-selected-items-from-x-to-x", new Object[] {leftTitle, rightTitle}, false) %>'
				},
				items: items,
				left: {
					id: '<portlet:namespace /><%= leftBoxName %>',
					label: '<%= leftTitle %>'
				},
				leftMaxItems: <%= leftBoxMaxItems %>,
				onItemsChange: (newItems) => {
					const initialLeftItemsLength = items[0].length;
					const newLeftItemsLength = newItems[0].length;

					let fromBox = document.getElementById('<portlet:namespace /><%= leftBoxName %>');
					let toBox = document.getElementById('<portlet:namespace /><%= rightBoxName %>');

					if (initialLeftItemsLength > newLeftItemsLength) {
						fromBox = document.getElementById('<portlet:namespace /><%= rightBoxName %>');
						toBox = document.getElementById('<portlet:namespace /><%= leftBoxName %>');
					}

					setItems(newItems);

					Liferay.fire('inputmoveboxes:moveItem', {fromBox, toBox});
					Liferay.fire('inputmoveboxes:orderItem');
				},
				right: {
					id: '<portlet:namespace /><%= rightBoxName %>',
					label: '<%= rightTitle %>'
				},
				rightMaxItems: <%= rightBoxMaxItems %>,
				size: 10,
			}
		);
	}

	render(
		main,
		{
			initialItems: [
				[
					<%
					for (int i = 0; i < leftList.size(); i++) {
						KeyValuePair kvp = (KeyValuePair)leftList.get(i);
					%>

						{
							label: '<%= HtmlUtil.escapeJS(kvp.getValue()) %>',
							value: '<%= HtmlUtil.escapeJS(kvp.getKey()) %>'
						},
					<%
					}
					%>

				],
				[
					<%
					for (int i = 0; i < rightList.size(); i++) {
						KeyValuePair kvp = (KeyValuePair)rightList.get(i);
					%>

						{
							label: '<%= HtmlUtil.escapeJS(kvp.getValue()) %>',
							value: '<%= HtmlUtil.escapeJS(kvp.getKey()) %>'
						},
					<%
					}
					%>

				]
			]
		},
		document.querySelector('#<%= randomNamespace %>input-move-boxes')
	);
</aui:script>