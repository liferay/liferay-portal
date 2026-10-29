<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPPublisherConfigurationDisplayContext cpPublisherConfigurationDisplayContext = (CPPublisherConfigurationDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
PortletURL configurationRenderURL = (PortletURL)request.getAttribute("configuration.jsp-configurationRenderURL");

List<CPCatalogEntry> catalogEntries = cpPublisherConfigurationDisplayContext.getCPCatalogEntries();
%>

<liferay-ui:search-container
	compactEmptyResultsMessage="<%= true %>"
	emptyResultsMessage="none"
	iteratorURL="<%= configurationRenderURL %>"
	total="<%= catalogEntries.size() %>"
>
	<liferay-ui:search-container-results
		calculateStartAndEnd="<%= true %>"
		results="<%= catalogEntries %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.commerce.product.catalog.CPCatalogEntry"
		escapedModel="<%= true %>"
		keyProperty="cpDefinitionId"
		modelVar="cpCatalogEntry"
	>

		<%
		String defaultImageFileURL = cpPublisherConfigurationDisplayContext.getDefaultImageFileURL(cpCatalogEntry);
		%>

		<c:choose>
			<c:when test="<%= Validator.isNotNull(defaultImageFileURL) %>">
				<liferay-ui:search-container-column-image
					name="image"
					src="<%= defaultImageFileURL %>"
				/>
			</c:when>
			<c:otherwise>
				<liferay-ui:search-container-column-icon
					icon="documents-and-media"
					name="image"
				/>
			</c:otherwise>
		</c:choose>

		<liferay-ui:search-container-column-text
			cssClass="font-weight-bold important table-cell-expand"
			name="name"
			value="<%= HtmlUtil.escape(cpCatalogEntry.getName()) %>"
		/>

		<%
		CPSku cpSku = cpPublisherConfigurationDisplayContext.getDefaultCPSku(cpCatalogEntry);
		%>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="sku"
			value="<%= (cpSku == null) ? StringPool.BLANK : HtmlUtil.escape(cpSku.getSku()) %>"
		/>

		<liferay-ui:search-container-column-jsp
			path="/product_publisher/configuration/product_selection_action.jsp"
		/>

		<liferay-ui:search-container-column-jsp
			cssClass="entry-action-column"
			path="/product_publisher/configuration/product_selection_order_action.jsp"
		/>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
		paginate="<%= total > SearchContainer.DEFAULT_DELTA %>"
	/>
</liferay-ui:search-container>

<clay:button
	additionalProps='<%=
		HashMapBuilder.<String, Object>put(
			"currentURL", currentURL
		).build()
	%>'
	displayType="secondary"
	label="select"
	propsTransformer="{ProductEntriesPropsTransformer} from commerce-product-content-web"
/>

<aui:script>
	function <portlet:namespace />moveSelectionDown(productEntryOrder) {
		var form = window.document['<portlet:namespace />fm'];

		form['<portlet:namespace /><%= Constants.CMD %>'].value =
			'move-selection-down';
		form['<portlet:namespace />redirect'].value =
			'<%= HtmlUtil.escapeJS(currentURL) %>';
		form['<portlet:namespace />productEntryOrder'].value = productEntryOrder;

		submitForm(form);
	}

	function <portlet:namespace />moveSelectionUp(productEntryOrder) {
		var form = window.document['<portlet:namespace />fm'];

		form['<portlet:namespace /><%= Constants.CMD %>'].value =
			'move-selection-up';
		form['<portlet:namespace />redirect'].value =
			'<%= HtmlUtil.escapeJS(currentURL) %>';
		form['<portlet:namespace />productEntryOrder'].value = productEntryOrder;

		submitForm(form);
	}
</aui:script>