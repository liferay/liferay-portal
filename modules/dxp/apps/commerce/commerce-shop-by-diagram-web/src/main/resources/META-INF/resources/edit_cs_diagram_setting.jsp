<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CSDiagramSettingDisplayContext csDiagramSettingDisplayContext = (CSDiagramSettingDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPDefinition cpDefinition = csDiagramSettingDisplayContext.getCPDefinition();

CSDiagramSetting csDiagramSetting = csDiagramSettingDisplayContext.fetchCSDiagramSetting();

String type = BeanParamUtil.getString(csDiagramSetting, renderRequest, "type", DefaultCSDiagramType.KEY);

CSDiagramType csDiagramType = csDiagramSettingDisplayContext.getCSDiagramType(type);
%>

<aui:link hashedFile="<%= true %>" href="commerce-shop-by-diagram-web/css/shop-by-diagram-edit-page.css" rel="stylesheet" />

<portlet:actionURL name="/cp_definitions/edit_cs_diagram_setting" var="editProductDefinitionDiagramSettingActionURL" />

<aui:form action="<%= editProductDefinitionDiagramSettingActionURL %>" cssClass="mt-4" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="updateCSDiagramSetting" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="cpDefinitionId" type="hidden" value="<%= cpDefinition.getCPDefinitionId() %>" />
	<aui:input name="publish" type="hidden" value="<%= true %>" />

	<liferay-ui:error exception="<%= NoSuchCPAttachmentFileEntryException.class %>" message="please-select-an-existing-file" />
	<liferay-ui:error exception="<%= NoSuchFileEntryException.class %>" message="please-select-an-existing-file" />

	<div class="row">
		<div class="col-lg-8 d-flex flex-column">
			<commerce-ui:panel
				elementClasses="flex-fill"
				title='<%= LanguageUtil.get(resourceBundle, "diagram-settings") %>'
			>
				<aui:select bean="<%= csDiagramSetting %>" label="type" model="<%= CSDiagramSetting.class %>" name="type">

					<%
					for (CSDiagramType curCSDiagramType : csDiagramSettingDisplayContext.getCSDiagramTypes()) {
						String csDiagramTypeKey = curCSDiagramType.getKey();
					%>

						<aui:option label="<%= curCSDiagramType.getLabel(locale) %>" selected="<%= (csDiagramSetting != null) && csDiagramTypeKey.equals(type) %>" value="<%= curCSDiagramType.getKey() %>" />

					<%
					}
					%>

				</aui:select>
			</commerce-ui:panel>
		</div>

		<div class="col-lg-4">
			<commerce-ui:panel
				bodyClasses="p-0 preview-container"
				elementClasses="flex-fill"
				title='<%= LanguageUtil.get(resourceBundle, "diagram-file") %>'
			>

				<%
				FileEntry fileEntry = csDiagramSettingDisplayContext.fetchFileEntry();
				%>

				<aui:model-context bean="<%= fileEntry %>" model="<%= FileEntry.class %>" />

				<div class="lfr-attachment-cover-image-selector">
					<portlet:actionURL name="/cp_definitions/upload_cs_diagram_setting_image" var="uploadCSDiagramSettingImageActionURL" />

					<liferay-item-selector:image-selector
						draggableImage="vertical"
						fileEntryId='<%= BeanParamUtil.getLong(fileEntry, request, "fileEntryId") %>'
						itemSelectorEventName="addFileEntry"
						itemSelectorURL="<%= csDiagramSettingDisplayContext.getImageItemSelectorURL() %>"
						maxFileSize="<%= csDiagramSettingDisplayContext.getImageMaxSize() %>"
						paramName="fileEntry"
						uploadURL="<%= uploadCSDiagramSettingImageActionURL %>"
						validExtensions="<%= StringUtil.merge(csDiagramSettingDisplayContext.getImageExtensions(), StringPool.COMMA_AND_SPACE) %>"
					/>
				</div>
			</commerce-ui:panel>
		</div>
	</div>

	<%
	if (csDiagramSetting != null) {
		csDiagramType.render(csDiagramSetting, request, PipingServletResponseFactory.createPipingServletResponse(pageContext));
	}
	else {
	%>

		<div class="row">
			<div class="col-lg-8 d-flex flex-column">
				<commerce-ui:panel
					bodyClasses="p-0"
					elementClasses="flex-fill"
					title='<%= LanguageUtil.get(resourceBundle, "diagram-mapping") %>'
				>
					<div class="p-3 text-center">
						<liferay-ui:message key="please-upload-a-file" />
					</div>
				</commerce-ui:panel>
			</div>
		</div>

	<%
	}
	%>

</aui:form>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"diagramId", (csDiagramSetting != null) ? csDiagramSetting.getCSDiagramSettingId() : 0
		).build()
	%>'
	module="{editCsDiagramSetting} from commerce-shop-by-diagram-web"
/>