<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<div class="form-group form-group-sm">
	<label class="control-label <%= fragmentCollectionFilterKeywordDisplayContext.isShowLabel() ? "" : "sr-only" %>" for="<%= fragmentCollectionFilterKeywordDisplayContext.getFragmentEntryLinkNamespace() %>keywordsInput">
		<%= HtmlUtil.escape(fragmentCollectionFilterKeywordDisplayContext.getLabel()) %>
	</label>

	<div class="input-group">
		<div class="input-group-item">
			<input aria-label="<%= LanguageUtil.get(request, "search") %>" class="form-control form-control-sm input-group-inset input-group-inset-after" id="<%= fragmentCollectionFilterKeywordDisplayContext.getFragmentEntryLinkNamespace() %>keywordsInput" placeholder="<%= LanguageUtil.get(request, "search") %>" type="text" value="" />

			<div class="input-group-inset-item input-group-inset-item-after">
				<clay:button
					aria-label='<%= LanguageUtil.get(request, "search") %>'
					disabled="<%= fragmentCollectionFilterKeywordDisplayContext.isDisabled() %>"
					displayType="unstyled"
					icon="search"
					id='<%= fragmentCollectionFilterKeywordDisplayContext.getFragmentEntryLinkNamespace() + "keywordsButton" %>'
				/>
			</div>
		</div>
	</div>

	<liferay-frontend:component
		context="<%= fragmentCollectionFilterKeywordDisplayContext.getProps() %>"
		module="{FragmentCollectionFilterKeyword} from fragment-collection-filter-keyword"
	/>
</div>