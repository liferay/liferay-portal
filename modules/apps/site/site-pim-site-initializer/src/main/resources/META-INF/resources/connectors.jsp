<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewPIMConnectorsDisplayContext viewPIMConnectorsDisplayContext = (ViewPIMConnectorsDisplayContext)request.getAttribute(ViewPIMConnectorsDisplayContext.class.getName());
%>

<div class="pim-connectors">
	<div>
		<section aria-label="<liferay-ui:message key="connectors" />" class="autofit-row autofit-row-center cms-breadcrumb px-4">
			<div class="autofit-col">
				<div class="c-gap-2 d-flex">
					<h2 class="font-weight-semi-bold mb-0 text-7 text-dark">
						<liferay-ui:message key="connectors" />
					</h2>
				</div>
			</div>
		</section>
	</div>

	<div class="cms-section custom-empty-state">
		<frontend-data-set:headless-display
			apiURL="<%= viewPIMConnectorsDisplayContext.getAPIURL() %>"
			creationMenu="<%= viewPIMConnectorsDisplayContext.getCreationMenu() %>"
			emptyState="<%= viewPIMConnectorsDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewPIMConnectorsDisplayContext.getFDSActionDropdownItems() %>"
			id="<%= PIMFDSNames.CONNECTORS %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{ConnectorsFDSPropsTransformer} from site-pim-site-initializer"
			selectedItemsKey="id"
		/>
	</div>
</div>