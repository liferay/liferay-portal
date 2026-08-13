<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<aui:style>
	.tag-filter-input {
		min-height: 2.125rem;
	}
</aui:style>

<div class="form-group form-group-sm mb-0">
	<div role="presentation">
		<span class="control-label <%= fragmentCollectionFilterTagsDisplayContext.isShowLabel() ? "" : "sr-only" %>">
			<%= HtmlUtil.escape(fragmentCollectionFilterTagsDisplayContext.getLabel()) %>
		</span>

		<input class="form-control form-control-sm tag-filter-input w-100" type="text" />

		<c:choose>
			<c:when test="<%= fragmentCollectionFilterTagsDisplayContext.isShowHelpText() %>">
				<p class="m-0 mt-1 small text-secondary">
					<%= HtmlUtil.escape(fragmentCollectionFilterTagsDisplayContext.getHelpText()) %>
				</p>
			</c:when>
		</c:choose>
	</div>

	<react:component
		module="{SelectTags} from fragment-collection-filter-tags"
		props="<%= fragmentCollectionFilterTagsDisplayContext.getProps() %>"
	/>
</div>