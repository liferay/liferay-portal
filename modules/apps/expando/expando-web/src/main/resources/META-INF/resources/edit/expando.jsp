<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String modelResource = ParamUtil.getString(request, "modelResource");

String modelResourceName = ResourceActionsUtil.getModelResource(request, modelResource);

long columnId = ParamUtil.getLong(request, "columnId");

ExpandoColumn expandoColumn = null;

if (columnId > 0) {
	expandoColumn = ExpandoColumnServiceUtil.fetchExpandoColumn(columnId);
}

int type = ParamUtil.getInteger(request, "type");

if (expandoColumn != null) {
	type = expandoColumn.getType();
}

ExpandoBridge expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(company.getCompanyId(), modelResource);

Serializable defaultValue = null;
UnicodeProperties unicodeProperties = new UnicodeProperties(true);

if (expandoColumn != null) {
	defaultValue = expandoBridge.getAttributeDefault(expandoColumn.getName());
	unicodeProperties = expandoBridge.getAttributeProperties(expandoColumn.getName());
}

int propertyHeight = GetterUtil.getInteger(unicodeProperties.get(ExpandoColumnConstants.PROPERTY_HEIGHT), ExpandoColumnConstants.PROPERTY_HEIGHT_DEFAULT);
boolean propertyHidden = GetterUtil.getBoolean(unicodeProperties.get(ExpandoColumnConstants.PROPERTY_HIDDEN));
int propertyIndexType = GetterUtil.getInteger(unicodeProperties.get(ExpandoColumnConstants.INDEX_TYPE));
boolean propertyLocalizeFieldName = GetterUtil.getBoolean(unicodeProperties.get(ExpandoColumnConstants.PROPERTY_LOCALIZE_FIELD_NAME), true);
boolean propertySecret = GetterUtil.getBoolean(unicodeProperties.get(ExpandoColumnConstants.PROPERTY_SECRET));
boolean propertyVisibleWithUpdatePermission = GetterUtil.getBoolean(unicodeProperties.get(ExpandoColumnConstants.PROPERTY_VISIBLE_WITH_UPDATE_PERMISSION));
int propertyWidth = GetterUtil.getInteger(unicodeProperties.get(ExpandoColumnConstants.PROPERTY_WIDTH));

String propertyDisplayType = ParamUtil.getString(request, "displayType", ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_INPUT_FIELD);
boolean readOnly = false;

if (expandoColumn != null) {
	propertyDisplayType = GetterUtil.getString(unicodeProperties.get(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE));

	if (Validator.isNull(propertyDisplayType)) {
		propertyDisplayType = ExpandoColumnConstants.getDefaultDisplayTypeProperty(type, unicodeProperties);
	}

	readOnly = !ExpandoColumnPermissionUtil.contains(permissionChecker, expandoColumn, ActionKeys.UPDATE);
}

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/expando/view_attributes"
).setRedirect(
	redirect
).setParameter(
	"modelResource", modelResource
).buildPortletURL();

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(modelResourceName + ": " + ((expandoColumn == null) ? LanguageUtil.get(request, "new-custom-field") : expandoColumn.getName()));

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "custom-field"), String.valueOf(renderResponse.createRenderURL()));

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "view-attributes"), portletURL.toString());

if (expandoColumn != null) {
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.format(request, "edit-x", new Object[] {expandoColumn.getName()}, false), null);
}
else {
	PortalUtil.addPortletBreadcrumbEntry(
		request, LanguageUtil.get(request, "new-custom-field"),
		PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCPath(
			"/edit/select_field_type.jsp"
		).setRedirect(
			redirect
		).setParameter(
			"modelResource", modelResource
		).buildString());

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.format(request, "new-x", new Object[] {propertyDisplayType}), null);
}
%>

<liferay-ui:error exception="<%= ColumnNameException.class %>" message="please-enter-a-valid-name" />

<liferay-ui:error exception="<%= ColumnNameException.MustValidate.class %>">

	<%
	String name = LanguageUtil.get(request, "field-name");
	%>

	<liferay-ui:message arguments='<%= new String[] {StringUtil.toLowerCase(name), ",.#/*_"} %>' key="the-x-cannot-contain-the-following-invalid-characters-x" />
</liferay-ui:error>

<liferay-ui:error exception="<%= ColumnTypeException.class %>" message="please-select-a-valid-type" />
<liferay-ui:error exception="<%= DuplicateColumnNameException.class %>" message="please-enter-a-unique-name" />
<liferay-ui:error exception="<%= ValueDataException.MismatchColumnType.class %>" message="please-enter-a-valid-value" />
<liferay-ui:error exception="<%= ValueDataException.MustInformDefaultLocale.class %>" message="please-enter-a-value-for-the-default-locale" />
<liferay-ui:error exception="<%= ValueDataException.UnsupportedColumnType.class %>" message="please-enter-a-valid-value" />

<portlet:actionURL name='<%= (expandoColumn == null) ? "addExpando" : "updateExpando" %>' var="editExpandoURL">
	<portlet:param name="mvcPath" value="/edit/expando.jsp" />
</portlet:actionURL>

<clay:container-fluid
	cssClass="container-view"
>
	<liferay-site-navigation:breadcrumb
		breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, true, true) %>"
	/>

	<liferay-frontend:edit-form
		action="<%= editExpandoURL %>"
	>
		<aui:input name="redirect" type="hidden" value="<%= portletURL %>" />
		<aui:input name="columnId" type="hidden" value="<%= columnId %>" />
		<aui:input name="modelResource" type="hidden" value="<%= modelResource %>" />
		<aui:input name="type" type="hidden" value="<%= type %>" />

		<aui:input name="Property--display-type--" type="hidden" value="<%= propertyDisplayType %>" />

		<liferay-frontend:edit-form-body>
			<h2 class="sheet-title">
				<%= HtmlUtil.escape(LanguageUtil.format(request, expandoColumn != null ? "edit-x" : "new-x", new Object[] {propertyDisplayType})) %>
			</h2>

			<liferay-frontend:fieldset
				disabled="<%= readOnly %>"
			>
				<aui:field-wrapper cssClass="form-group lfr-input-text-container">
					<c:choose>
						<c:when test="<%= expandoColumn != null %>">
							<aui:input name="name" type="hidden" value="<%= expandoColumn.getName() %>" />

							<aui:input label="field-name" name="key" type="resource" value="<%= expandoColumn.getName() %>" />
						</c:when>
						<c:otherwise>
							<aui:input label="field-name" maxlength='<%= ModelHintsUtil.getMaxLength(ExpandoColumn.class.getName(), "name") %>' name="name" required="<%= true %>" />
						</c:otherwise>
					</c:choose>

					<div class="form-text">
						<liferay-ui:message arguments="&lt;liferay-expando:custom-attribute /&gt;" key="custom-field-key-help" translateArguments="<%= false %>" />
					</div>
				</aui:field-wrapper>

				<%@ include file="/edit/default_value_input.jspf" %>
			</liferay-frontend:fieldset>

			<liferay-frontend:fieldset
				collapsed="<%= !readOnly %>"
				collapsible="<%= !readOnly %>"
				disabled="<%= readOnly %>"
				label="advanced-properties"
			>
				<%@ include file="/edit/advanced_properties.jspf" %>
			</liferay-frontend:fieldset>
		</liferay-frontend:edit-form-body>

		<liferay-frontend:edit-form-footer>
			<liferay-frontend:edit-form-buttons
				redirect="<%= redirect %>"
				submitDisabled="<%= readOnly %>"
			/>
		</liferay-frontend:edit-form-footer>
	</liferay-frontend:edit-form>
</clay:container-fluid>