<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewCategoryUsagesDisplayContext viewCategoryUsagesDisplayContext = (ViewCategoryUsagesDisplayContext)request.getAttribute(ViewCategoryUsagesDisplayContext.class.getName());
%>

<div class="cms-section">
	<div class="categorization-section">
		<div>
			<react:component
				module="{Breadcrumb} from site-cms-site-initializer"
				props="<%= viewCategoryUsagesDisplayContext.getBreadcrumbProps() %>"
			/>
		</div>

		<frontend-data-set:headless-display
			apiURL="<%= viewCategoryUsagesDisplayContext.getAPIURL() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.CATEGORY_USAGES %>"
			propsTransformer="{CategoryUsagesFDSPropsTransformer} from site-cms-site-initializer"
		/>
	</div>
</div>