<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
PIMConnectorFieldMappingsDisplayContext pimConnectorFieldMappingsDisplayContext = (PIMConnectorFieldMappingsDisplayContext)request.getAttribute(PIMConnectorFieldMappingsDisplayContext.class.getName());
%>

<div class="pim-field-mappings">
	<div>
		<react:component
			module="{Breadcrumb} from site-cms-site-initializer"
			props="<%= pimConnectorFieldMappingsDisplayContext.getBreadcrumbProps() %>"
		/>
	</div>

	<div class="cms-section custom-empty-state">
		<frontend-data-set:classic-display
			contextParams="<%= pimConnectorFieldMappingsDisplayContext.getContextParams() %>"
			dataProviderKey="<%= PIMFDSNames.FIELD_MAPPINGS %>"
			emptyState="<%= pimConnectorFieldMappingsDisplayContext.getEmptyState() %>"
			id="<%= PIMFDSNames.FIELD_MAPPINGS %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{FieldMappingsFDSPropsTransformer} from site-pim-site-initializer"
			showSearch="<%= true %>"
		/>
	</div>
</div>