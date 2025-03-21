<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %>

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.search.web.internal.search.bar.portlet.display.context.SearchBarPortletInstanceConfigurationDisplayContext" %>

<portlet:defineObjects />

<%
SearchBarPortletInstanceConfigurationDisplayContext searchBarPortletInstanceConfigurationDisplayContext = (SearchBarPortletInstanceConfigurationDisplayContext)request.getAttribute(SearchBarPortletInstanceConfigurationDisplayContext.class.getName());
%>

<aui:input name="displayStyleGroupId" value="<%= searchBarPortletInstanceConfigurationDisplayContext.getDisplayStyleGroupId() %>" />

<aui:input name="displayStyle" value="<%= searchBarPortletInstanceConfigurationDisplayContext.getDisplayStyle() %>" />

<c:if test="<%= searchBarPortletInstanceConfigurationDisplayContext.isSuggestionsConfigurationVisible() %>">
	<aui:input name="enableSuggestions" type="checkbox" value="<%= searchBarPortletInstanceConfigurationDisplayContext.isEnableSuggestions() %>" />

	<div>
		<span aria-hidden="true" class="loading-animation loading-animation-sm"></span>

		<react:component
			module="{SystemSettingsFieldList} from portal-search-web"
			props='<%=
				HashMapBuilder.<String, Object>put(
					"fieldHelp", LanguageUtil.get(request, "suggestions-contributor-configuration-system-settings-help")
				).put(
					"fieldLabel", LanguageUtil.get(request, "suggestions-contributor-configuration")
				).put(
					"fieldName", "suggestionsContributorConfigurations"
				).put(
					"initialValue", searchBarPortletInstanceConfigurationDisplayContext.getSuggestionsContributorConfigurations()
				).put(
					"namespace", liferayPortletResponse.getNamespace()
				).build()
			%>'
		/>
	</div>

	<aui:input helpMessage="character-threshold-for-displaying-suggestions-help" label="character-threshold-for-displaying-suggestions" name="suggestionsDisplayThreshold" value="<%= searchBarPortletInstanceConfigurationDisplayContext.getSuggestionsDisplayThreshold() %>" />
</c:if>