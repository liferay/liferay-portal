<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.search.tuning.synonyms.web.internal.constants.SynonymsPortletKeys" %><%@
page import="com.liferay.portal.search.tuning.synonyms.web.internal.display.context.SynonymsDisplayContext" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<%
SynonymsDisplayContext synonymsDisplayContext = (SynonymsDisplayContext)request.getAttribute(SynonymsPortletKeys.SYNONYMS_DISPLAY_CONTEXT);
%>

<clay:management-toolbar
	actionDropdownItems="<%= synonymsDisplayContext.getActionDropdownMultipleItems() %>"
	creationMenu="<%= synonymsDisplayContext.getCreationMenu() %>"
	disabled="<%= synonymsDisplayContext.isDisabledManagementBar() %>"
	itemsTotal="<%= synonymsDisplayContext.getItemsTotal() %>"
	propsTransformer="{SynonymsManagementToolbarPropsTransformer} from portal-search-tuning-synonyms-web"
	searchContainerId="synonymSetsEntries"
	selectable="<%= true %>"
	showCreationMenu="<%= true %>"
	showSearch="<%= false %>"
/>

<portlet:actionURL name="/synonyms/delete_synonym_sets" var="deleteSynonymSetActionURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<clay:container-fluid
	size="xxxl"
>
	<aui:form action="<%= deleteSynonymSetActionURL %>" method="post" name="fm">
		<aui:input name="deletedSynonymSetsString" type="hidden" value="" />

		<liferay-ui:search-container
			id="synonymSetsEntries"
			searchContainer="<%= synonymsDisplayContext.getSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.portal.search.tuning.synonyms.web.internal.display.context.SynonymSetDisplayContext"
				keyPropertyName="synonymSetId"
				modelVar="synonymSetDisplayContext"
			>
				<liferay-ui:search-container-column-text
					colspan="<%= 2 %>"
					cssClass="table-cell-expand table-title"
				>
					<clay:link
						href="<%= synonymSetDisplayContext.getEditRenderURL() %>"
						label="<%= synonymSetDisplayContext.getDisplayedSynonymSet() %>"
						translated="<%= false %>"
					/>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text>
					<clay:dropdown-actions
						aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
						dropdownItems="<%= synonymSetDisplayContext.getDropdownItems() %>"
						propsTransformer="{SynonymSetsDropdownDefaultPropsTransformer} from portal-search-tuning-synonyms-web"
					/>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>