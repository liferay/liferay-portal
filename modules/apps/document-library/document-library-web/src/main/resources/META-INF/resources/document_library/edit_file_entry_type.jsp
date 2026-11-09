<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/document_library/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

DLFileEntryType fileEntryType = (DLFileEntryType)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_ENTRY_TYPE);

long fileEntryTypeId = BeanParamUtil.getLong(fileEntryType, request, "fileEntryTypeId");

long dataDefinitionId = BeanParamUtil.getLong(fileEntryType, request, "dataDefinitionId");

String defaultLanguageId = LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault());

if (dataDefinitionId != 0) {
	com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure = DDMStructureLocalServiceUtil.getStructure(dataDefinitionId);

	defaultLanguageId = ddmStructure.getDefaultLanguageId();
}

String languageId = ParamUtil.getString(request, "languageId", defaultLanguageId);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle((fileEntryType == null) ? LanguageUtil.get(request, "new-document-type") : fileEntryType.getName(locale));
%>

<portlet:actionURL name="/document_library/edit_file_entry_type" var="editFileEntryTypeURL">
	<portlet:param name="mvcRenderCommandName" value="/document_library/edit_file_entry_type" />
</portlet:actionURL>

<aui:form action="<%= editFileEntryTypeURL %>" cssClass="edit-metadata-type-form" method="post" name="fm" onSubmit="event.preventDefault(); ">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (fileEntryType == null) ? Constants.ADD : Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="fileEntryTypeId" type="hidden" value="<%= fileEntryTypeId %>" />
	<aui:input name="dataDefinitionId" type="hidden" value="<%= dataDefinitionId %>" />
	<aui:input name="dataDefinition" type="hidden" />
	<aui:input name="dataLayout" type="hidden" />
	<aui:input name="languageId" type="hidden" value="<%= languageId %>" />

	<liferay-ui:error exception="<%= DuplicateFileEntryTypeException.class %>" message="please-enter-a-unique-document-type-name" />
	<liferay-ui:error exception="<%= NoSuchMetadataSetException.class %>" message="please-enter-a-valid-metadata-set-or-enter-a-metadata-field" />
	<liferay-ui:error exception="<%= StorageFieldRequiredException.class %>" message="please-fill-out-all-required-fields" />
	<liferay-ui:error exception="<%= StructureDefinitionException.class %>" message="please-enter-a-valid-definition" />
	<liferay-ui:error exception="<%= StructureDuplicateElementException.class %>" message="please-enter-unique-metadata-field-names-(including-field-names-inherited-from-the-parent)" />
	<liferay-ui:error exception="<%= StructureNameException.class %>" message="please-enter-a-valid-name" />

	<aui:model-context bean="<%= fileEntryType %>" model="<%= DLFileEntryType.class %>" />

	<nav class="component-tbar subnav-tbar-light tbar tbar-metadata-type">
		<clay:container-fluid
			size="xxxl"
		>
			<ul class="tbar-nav">
				<li class="tbar-item tbar-item-expand">
					<aui:input cssClass="form-control-inline" defaultLanguageId="<%= LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()) %>" label='<%= LanguageUtil.get(request, "name") %>' labelCssClass="sr-only" name="name" placeholder='<%= LanguageUtil.format(request, "untitled", "structure") %>' wrapperCssClass="article-content-title mb-0" />
				</li>
				<li class="tbar-item">
					<div class="metadata-type-button-row tbar-section text-right">
						<aui:button cssClass="btn-sm mr-3" href="<%= redirect %>" type="cancel" />

						<aui:button cssClass="btn-sm mr-3" id="submitButton" type="submit" />
					</div>
				</li>
			</ul>
		</clay:container-fluid>
	</nav>

	<div class="contextual-sidebar-content">
		<clay:container-fluid
			cssClass="container-view"
		>

			<%
			DLEditFileEntryTypeDataEngineDisplayContext dlEditFileEntryTypeDataEngineDisplayContext = (DLEditFileEntryTypeDataEngineDisplayContext)request.getAttribute(DLWebKeys.DOCUMENT_LIBRARY_EDIT_FILE_ENTRY_TYPE_DATA_ENGINE_DISPLAY_CONTEXT);
			%>

			<liferay-data-engine:data-layout-builder
				additionalPanels="<%= dlEditFileEntryTypeDataEngineDisplayContext.getAdditionalPanels(npmResolvedPackageName) %>"
				componentId='<%= liferayPortletResponse.getNamespace() + "dataLayoutBuilder" %>'
				contentType="document-library"
				dataDefinitionId="<%= dataDefinitionId %>"
				dataLayoutInputId="dataLayout"
				groupId="<%= scopeGroupId %>"
				namespace="<%= liferayPortletResponse.getNamespace() %>"
				scopes='<%= SetUtil.fromCollection(Arrays.asList("document-library")) %>'
				submitButtonId='<%= liferayPortletResponse.getNamespace() + "submitButton" %>'
			/>
		</clay:container-fluid>
	</div>
</aui:form>

<liferay-frontend:component
	componentId='<%= liferayPortletResponse.getNamespace() + "LocaleChangedHandlerComponent" %>'
	context='<%=
		HashMapBuilder.<String, Object>put(
			"contentTitle", "name"
		).put(
			"defaultLanguageId", defaultLanguageId
		).build()
	%>'
	module="{LocaleChangedHandler} from document-library-web"
	servletContext="<%= application %>"
/>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"contentTitle", "name"
		).put(
			"defaultLanguageId", defaultLanguageId
		).build()
	%>'
	module="{DataEngineLayoutBuilderHandler} from document-library-web"
	servletContext="<%= application %>"
/>

<%
if (fileEntryType == null) {
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "add-document-type"), currentURL);
}
else {
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "edit-document-type"), currentURL);
}
%>