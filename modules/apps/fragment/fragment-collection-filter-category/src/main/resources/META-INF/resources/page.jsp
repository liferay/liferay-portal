<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<div class="form-group form-group-sm">
	<span class="control-label <%= fragmentCollectionFilterCategoryDisplayContext.isShowLabel() ? "" : "sr-only" %>">
		<%= HtmlUtil.escape(fragmentCollectionFilterCategoryDisplayContext.getLabel()) %>
	</span>

	<div>
		<clay:button
			cssClass="dropdown-toggle form-control form-control-select form-control-sm text-left w-100"
			disabled="<%= true %>"
			displayType="secondary"
			label="select"
			small="<%= true %>"
		/>

		<react:component
			module="{SelectCategory} from fragment-collection-filter-category"
			props="<%= fragmentCollectionFilterCategoryDisplayContext.getProps() %>"
		/>
	</div>
</div>