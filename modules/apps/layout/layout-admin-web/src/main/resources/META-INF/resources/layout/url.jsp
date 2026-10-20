<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
Group group = layoutsAdminDisplayContext.getGroup();

Layout selLayout = layoutsAdminDisplayContext.getSelLayout();

LayoutType selLayoutType = selLayout.getLayoutType();

String friendlyURLBase = StringPool.BLANK;

if (!group.isLayoutPrototype() && selLayoutType.isURLFriendliable() && !layoutsAdminDisplayContext.isDraft() && (!selLayout.isSystem() || selLayout.isTypeAssetDisplay() || selLayout.isTypeUtility())) {
	friendlyURLBase = layoutsAdminDisplayContext.getFriendlyURLBase();
}
%>

<liferay-ui:error-marker
	key="<%= WebKeys.ERROR_SECTION %>"
	value="url"
/>

<aui:model-context bean="<%= selLayout %>" model="<%= Layout.class %>" />

<liferay-friendly-url:input
	className="<%= Layout.class.getName() %>"
	classPK="<%= selLayout.getPlid() %>"
	helpMessage='<%= selLayout.isTypeAssetDisplay() ? LanguageUtil.get(request, "this-friendly-url-will-only-be-used-when-specifically-mapping-this-display-page-template-from-the-page-editor") : StringPool.BLANK %>'
	inputAddon="<%= friendlyURLBase %>"
	name="friendlyURL"
/>

<c:if test="<%= layoutsAdminDisplayContext.isShowFriendlyURLWarningMessage() %>">
	<clay:alert
		dismissible="<%= true %>"
		displayType="warning"
		message="<%= layoutsAdminDisplayContext.getFriendlyURLWarningMessage() %>"
	/>
</c:if>

<c:if test="<%= layoutsAdminDisplayContext.isURLAdvancedSettingsVisible() %>">

	<%
	UnicodeProperties layoutTypeSettingsUnicodeProperties = selLayout.getTypeSettingsProperties();
	%>

	<c:if test="<%= !group.isLayoutPrototype() %>">
		<clay:alert
			cssClass='<%= selLayout.isLayoutPrototypeLinkActive() ? "layout-prototype-info-message" : "layout-prototype-info-message hide" %>'
			displayType="warning"
		>
			<liferay-ui:message arguments='<%= new String[] {"inherit-changes", "general"} %>' key="some-page-settings-are-unavailable-because-x-is-enabled" translateArguments="<%= true %>" />
		</clay:alert>

		<aui:input cssClass="propagatable-field" disabled="<%= selLayout.isLayoutPrototypeLinkActive() %>" helpMessage="query-string-help" label="query-string" name="TypeSettingsProperties--query-string--" size="30" type="text" value='<%= GetterUtil.getString(layoutTypeSettingsUnicodeProperties.getProperty("query-string")) %>' />
	</c:if>

	<%
	String targetType = GetterUtil.getString(layoutTypeSettingsUnicodeProperties.getProperty("targetType"));
	%>

	<div class="d-flex">
		<aui:select cssClass="propagatable-field" id="targetType" label="target-type" name="TypeSettingsProperties--targetType--" wrapperCssClass="mr-3 w-50">
			<aui:option label="specific-frame" selected='<%= !Objects.equals(targetType, "useNewTab") %>' value="" />
			<aui:option label="new-tab" selected='<%= Objects.equals(targetType, "useNewTab") %>' value="useNewTab" />
		</aui:select>

		<aui:input cssClass="propagatable-field" disabled="<%= selLayout.isLayoutPrototypeLinkActive() %>" id="target" label="target[frame]" name="TypeSettingsProperties--target--" size="15" type="text" value='<%= GetterUtil.getString(layoutTypeSettingsUnicodeProperties.getProperty("target")) %>' wrapperCssClass='<%= Objects.equals(targetType, "useNewTab") ? "hide" : "w-50" %>' />
	</div>

	<liferay-frontend:component
		componentId='<%= liferayPortletResponse.getNamespace() + "addLayout" %>'
		context='<%=
			HashMapBuilder.<String, Object>put(
				"defaultTarget", Objects.equals(targetType, "useNewTab") ? StringPool.BLANK : GetterUtil.getString(layoutTypeSettingsUnicodeProperties.getProperty("target"))
			).put(
				"namespace", liferayPortletResponse.getNamespace()
			).build()
		%>'
		module="{Advanced} from layout-admin-web"
	/>
</c:if>