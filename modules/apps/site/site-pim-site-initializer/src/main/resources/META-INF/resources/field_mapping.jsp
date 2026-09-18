<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
PIMConnectorFieldMappingDisplayContext pimConnectorFieldMappingDisplayContext = (PIMConnectorFieldMappingDisplayContext)request.getAttribute(PIMConnectorFieldMappingDisplayContext.class.getName());
%>

<div class="pim-field-mapping">
	<div>
		<react:component
			module="{FieldMappingBreadcrumb} from site-pim-site-initializer"
			props="<%= pimConnectorFieldMappingDisplayContext.getBreadcrumbProps() %>"
		/>
	</div>

	<div class="cms-section custom-empty-state">
		<frontend-data-set:classic-display
			contextParams="<%= pimConnectorFieldMappingDisplayContext.getContextParams() %>"
			dataProviderKey="<%= PIMFDSNames.FIELD_MAPPINGS %>"
			emptyState="<%= pimConnectorFieldMappingDisplayContext.getEmptyState() %>"
			id="<%= PIMFDSNames.FIELD_MAPPINGS %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{FieldMappingsFDSPropsTransformer} from site-pim-site-initializer"
			showSearch="<%= true %>"
		/>
	</div>
</div>