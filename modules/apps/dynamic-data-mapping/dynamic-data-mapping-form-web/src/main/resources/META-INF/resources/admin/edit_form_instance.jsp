<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

DDMFormInstance formInstance = ddmFormAdminDisplayContext.getDDMFormInstance();

long formInstanceId = BeanParamUtil.getLong(formInstance, request, "formInstanceId");
long groupId = BeanParamUtil.getLong(formInstance, request, "groupId", scopeGroupId);
long ddmStructureId = BeanParamUtil.getLong(formInstance, request, "structureId");

boolean disableCopyButton = false;

if (!ddmFormAdminDisplayContext.isFormPublished()) {
	disableCopyButton = true;
}

JSONObject formBuilderContextJSONObject = ddmFormAdminDisplayContext.getFormBuilderContextJSONObject();

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle((formInstance == null) ? LanguageUtil.get(request, "new-form") : LanguageUtil.get(request, "edit-form"));
%>

<portlet:actionURL name="/dynamic_data_mapping_form/save_form_instance" var="saveFormInstanceURL">
	<portlet:param name="mvcRenderCommandName" value="/admin/edit_form_instance" />
</portlet:actionURL>

<portlet:actionURL name="/dynamic_data_mapping_form/publish_form_instance" var="publishFormInstanceURL">
	<portlet:param name="mvcRenderCommandName" value="/admin/edit_form_instance" />
</portlet:actionURL>

<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/dynamic_data_mapping_form/save_form_instance" var="autoSaveFormInstanceURL" />

<div class="lfr-alert-container">
	<clay:container-fluid cssClass="lfr-alert-wrapper"></clay:container-fluid>
</div>

<div class="portlet-forms" id="<portlet:namespace />formContainer">
	<div class="forms-navigation-bar">
		<clay:navigation-bar
			id="formsNavigationBar"
			navigationItems="<%= ddmFormAdminDisplayContext.getFormBuilderNavigationItems() %>"
		/>
	</div>

	<nav class="hide management-bar management-bar-light navbar navbar-expand-md toolbar-group-field" id="<portlet:namespace />managementToolbar">
		<clay:container-fluid
			cssClass="autosave-bar d-flex justify-content-between toolbar"
		>
			<div class="autosave-feedback-container navbar-form navbar-form-autofit navbar-overlay toolbar-group-content">
				<span class="autosave-feedback management-bar-text" id="<portlet:namespace />autosaveMessage"></span>
			</div>

			<ul class="navbar-nav toolbar-group-field">
				<li class="nav-item pr-3">
					<button class="btn btn-monospaced btn-outline-borderless btn-outline-secondary btn-sm lfr-ddm-button lfr-ddm-settings-button" title="<%= LanguageUtil.get(request, "settings") %>">
						<svg class="lexicon-icon">
							<use xlink:href="<%= ddmFormAdminDisplayContext.getLexiconIconsPath() %>cog" />
						</svg>
					</button>
				</li>
				<li class="nav-item pr-2">
					<c:choose>
						<c:when test="<%= disableCopyButton %>">
							<button class="btn btn-secondary btn-sm disabled lfr-ddm-button lfr-ddm-share-url-button share-form-icon" data-original-title="<liferay-ui:message key="share" />" id="<portlet:namespace />publishIcon" title="<%= disableCopyButton ? LanguageUtil.get(request, "publish-the-form-to-get-its-shareable-link") : "" %>" type="button">
								<liferay-ui:message key="share" />
							</button>
						</c:when>
						<c:otherwise>
							<button class="btn btn-secondary btn-sm lfr-ddm-button lfr-ddm-share-url-button share-form-icon" id="<portlet:namespace />publishIcon" type="button">
								<liferay-ui:message key="share" />
							</button>
						</c:otherwise>
					</c:choose>
				</li>
				<li class="nav-item pr-2">
					<button class="btn btn-secondary btn-sm lfr-ddm-button lfr-ddm-preview-button" title="<%= LanguageUtil.get(request, "a-form-draft-will-be-saved-before-the-preview") %>">
						<liferay-ui:message key="preview" />
					</button>
				</li>
				<li class="nav-item pl-2 pr-2">
					<button class="btn btn-secondary btn-sm lfr-ddm-button lfr-ddm-save-button">
						<liferay-ui:message key="save" />
					</button>
				</li>
				<li class="nav-item pr-2">
					<button class="btn <%= ddmFormAdminDisplayContext.isFormPublished() ? "btn-secondary" : "btn-primary" %> btn-sm lfr-ddm-button lfr-ddm-publish-button">
						<%= ddmFormAdminDisplayContext.isFormPublished() ? LanguageUtil.get(request, "unpublish") : LanguageUtil.get(request, "publish") %>
					</button>
				</li>
				<li class="nav-item">
					<button class="btn btn-primary btn-sm lfr-ddm-add-field lfr-ddm-plus-button nav-btn nav-btn-monospaced" id="addFieldButton" title="<%= LanguageUtil.get(request, "add-elements") %>">
						<svg class="lexicon-icon">
							<use xlink:href="<%= ddmFormAdminDisplayContext.getLexiconIconsPath() %>plus" />
						</svg>
					</button>
				</li>
			</ul>
		</clay:container-fluid>
	</nav>

	<div class="contextual-sidebar-content contextual-sidebar-content--sidebar-open">
		<clay:container-fluid
			cssClass="ddm-translation-manager hide"
		>
			<liferay-frontend:translation-manager
				availableLocales="<%= ddmFormAdminDisplayContext.getAvailableLocales() %>"
				changeableDefaultLanguage="<%= false %>"
				defaultLanguageId="<%= ddmFormAdminDisplayContext.getDefaultLanguageId() %>"
				id="translationManager"
			/>
		</clay:container-fluid>

		<aui:form action="<%= saveFormInstanceURL %>" cssClass="ddm-form-builder-form" enctype="multipart/form-data" method="post" name="editForm">
			<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
			<aui:input name="formInstanceId" type="hidden" value="<%= formInstanceId %>" />
			<aui:input name="groupId" type="hidden" value="<%= groupId %>" />
			<aui:input name="ddmStructureId" type="hidden" value="<%= ddmStructureId %>" />
			<aui:input name="name" type="hidden" value="<%= ddmFormAdminDisplayContext.getFormLocalizedNameJSONObject(formInstance) %>" />
			<aui:input name="description" type="hidden" value="<%= ddmFormAdminDisplayContext.getFormLocalizedDescriptionJSONObject() %>" />
			<aui:input name="serializedFormBuilderContext" type="hidden" value="<%= formBuilderContextJSONObject %>" />
			<aui:input name="serializedSettingsContext" type="hidden" value="" />

			<%@ include file="/admin/exceptions.jspf" %>
			<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/dynamic_data_mapping_form/get_form_report_data" var="formReportDataURL">
				<portlet:param name="formInstanceId" value="<%= String.valueOf(formInstanceId) %>" />
			</liferay-portlet:resourceURL>

			<div id="<portlet:namespace />-container">
				<react:component
					module="{App} from dynamic-data-mapping-form-web"
					props='<%=
						HashMapBuilder.<String, Object>put(
							"autocompleteUserURL", ddmFormAdminDisplayContext.getAutocompleteUserURL()
						).put(
							"autosaveInterval", ddmFormAdminDisplayContext.getAutosaveInterval()
						).put(
							"autosaveURL", autoSaveFormInstanceURL.toString()
						).put(
							"availableLanguageIds", ddmFormAdminDisplayContext.getAvailableLanguageIdsJSONArray()
						).put(
							"availableLocales", ddmFormAdminDisplayContext.getAvailableLocalesJSONArray()
						).put(
							"context", formBuilderContextJSONObject
						).put(
							"dataEngineModule", ddmFormAdminDisplayContext.getDataEngineModule()
						).put(
							"dataProviderInstanceParameterSettingsURL", dataProviderInstanceParameterSettingsURL
						).put(
							"dataProviderInstancesURL", dataProviderInstancesURL
						).put(
							"defaultLanguageId", ddmFormAdminDisplayContext.getDefaultLanguageId()
						).put(
							"displayChartAsTable", ddmFormAdminDisplayContext.isDisplayChartAsTable()
						).put(
							"elementSets", ddmFormAdminDisplayContext.getFieldSetsJSONArray()
						).put(
							"fieldSetDefinitionURL", ddmFormAdminDisplayContext.getFieldSetDefinitionURL()
						).put(
							"fieldTypes", ddmFormAdminDisplayContext.getDDMFormFieldTypesJSONArray()
						).put(
							"formInstanceId", formInstanceId
						).put(
							"formReportDataURL", formReportDataURL.toString()
						).put(
							"formSettingsContext", ddmFormAdminDisplayContext.getDDMFormSettingsContext(pageContext)
						).put(
							"functionsMetadata", functionsMetadataJSONObject
						).put(
							"functionsURL", functionsURL
						).put(
							"groupId", groupId
						).put(
							"localizedDescription", ddmFormAdminDisplayContext.getFormLocalizedDescriptionJSONObject()
						).put(
							"localizedName", ddmFormAdminDisplayContext.getFormLocalizedNameJSONObject(formInstance)
						).put(
							"portletNamespace", liferayPortletResponse.getNamespace()
						).put(
							"published", ddmFormAdminDisplayContext.isFormPublished()
						).put(
							"publishFormInstanceURL", publishFormInstanceURL.toString()
						).put(
							"rolesURL", rolesURL
						).put(
							"rules", ddmFormRulesJSONArray
						).put(
							"saved", formInstance != null
						).put(
							"sharedFormURL", ddmFormAdminDisplayContext.getSharedFormURL()
						).put(
							"shareFormInstanceURL", ddmFormAdminDisplayContext.getShareFormInstanceURL(formInstance)
						).put(
							"showPublishAlert", ddmFormAdminDisplayContext.isShowPublishAlert()
						).put(
							"spritemap", themeDisplay.getPathThemeSpritemap()
						).put(
							"view", "formBuilder"
						).build()
					%>'
				/>
			</div>
		</aui:form>
	</div>
</div>

<aui:script>
	var clearPortletHandlers = function (event) {
		if (event.portletId === '<%= portletDisplay.getRootPortletId() %>') {
			var translationManager = Liferay.component(
				'<portlet:namespace />translationManager'
			);

			Liferay.destroyComponents((component) => {
				var destroy = false;

				if (component === translationManager) {
					destroy = true;
				}

				return destroy;
			});

			Liferay.detach('destroyPortlet', clearPortletHandlers);
		}
	};

	Liferay.on('destroyPortlet', clearPortletHandlers);
</aui:script>