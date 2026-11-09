<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = PortalUtil.escapeRedirect(ParamUtil.getString(request, "redirect"));

JournalEditDDMStructuresDisplayContext journalEditDDMStructuresDisplayContext = (JournalEditDDMStructuresDisplayContext)request.getAttribute(JournalEditDDMStructuresDisplayContext.class.getName());

DDMStructure ddmStructure = journalEditDDMStructuresDisplayContext.getDDMStructure();

long groupId = BeanParamUtil.getLong(ddmStructure, request, "groupId", scopeGroupId);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

renderResponse.setTitle((ddmStructure != null) ? LanguageUtil.format(request, "edit-x", ddmStructure.getName(locale), false) : LanguageUtil.get(request, "new-structure"));

DDMForm ddmForm = null;
long ddmStructureId = 0L;
String ddmStructureKey = StringPool.BLANK;

if (ddmStructure != null) {
	ddmForm = ddmStructure.getDDMForm();
	ddmStructureId = ddmStructure.getStructureId();
	ddmStructureKey = ddmStructure.getStructureKey();
}

PortletURL editDDMStructureURL = renderResponse.createActionURL();

if (ddmStructure == null) {
	editDDMStructureURL.setParameter(ActionRequest.ACTION_NAME, "/journal/add_data_definition");
}
else {
	editDDMStructureURL.setParameter(ActionRequest.ACTION_NAME, "/journal/update_data_definition");
}

editDDMStructureURL.setParameter("mvcPath", "/edit_data_definition.jsp");
editDDMStructureURL.setParameter("ddmStructureId", String.valueOf(ddmStructureId));
editDDMStructureURL.setParameter("structureKey", String.valueOf(ddmStructureKey));
%>

<aui:form action="<%= editDDMStructureURL %>" cssClass="edit-article-form" enctype="multipart/form-data" method="post" name="fm" onSubmit="event.preventDefault();">
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="groupId" type="hidden" value="<%= groupId %>" />
	<aui:input name="dataDefinition" type="hidden" />
	<aui:input name="dataLayout" type="hidden" />
	<aui:input name="dataDefinitionId" type="hidden" value="<%= journalEditDDMStructuresDisplayContext.getDDMStructureId() %>" />
	<aui:input name="languageId" type="hidden" value="<%= journalEditDDMStructuresDisplayContext.getDefaultLanguageId() %>" />

	<aui:model-context bean="<%= ddmStructure %>" model="<%= DDMStructure.class %>" />

	<nav class="component-tbar subnav-tbar-light tbar tbar-article tbar-structure">
		<clay:container-fluid
			fullWidth="<%= true %>"
		>
			<ul class="tbar-nav">
				<li class="tbar-item tbar-item-expand">
					<aui:input activeLanguageIds="<%= journalEditDDMStructuresDisplayContext.getAvailableLanguageIds() %>" adminMode="<%= true %>" cssClass="form-control-inline" defaultLanguageId="<%= (ddmForm == null) ? LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()): LocaleUtil.toLanguageId(ddmForm.getDefaultLocale()) %>" label='<%= LanguageUtil.get(request, "name") %>' labelCssClass="sr-only" languagesDropdownDirection="down" localized="<%= true %>" name="name" placeholder='<%= LanguageUtil.format(request, "untitled-x", "structure") %>' required="<%= true %>" type="text" wrapperCssClass="article-content-title c-mb-0" />
				</li>
				<li class="tbar-item">
					<div class="c-gap-3 form-group-sm journal-article-button-row mb-0 tbar-section text-right">
						<clay:link
							borderless="<%= true %>"
							displayType="secondary"
							href="<%= redirect %>"
							label="cancel"
							type="button"
						/>

						<clay:button
							displayType="primary"
							id='<%= liferayPortletResponse.getNamespace() + "submitButton" %>'
							label="save"
							type="submit"
						/>
					</div>
				</li>
			</ul>
		</clay:container-fluid>
	</nav>

	<div class="contextual-sidebar-content contextual-sidebar-content--sidebar-open">
		<clay:container-fluid
			cssClass="container-view journal-edit-structure-content"
		>
			<liferay-ui:error exception="<%= DDMStructureValidationModelListenerException.class %>" message="the-structure-key-cannot-be-modified" />

			<c:if test="<%= (ddmStructure != null) && (DDMStorageLinkLocalServiceUtil.getStructureStorageLinksCount(journalEditDDMStructuresDisplayContext.getDDMStructureId()) > 0) %>">
				<clay:alert
					displayType="warning"
					message="there-are-content-references-to-this-structure.-you-may-lose-data-if-a-field-name-is-renamed-or-removed"
				/>
			</c:if>

			<c:if test="<%= (journalEditDDMStructuresDisplayContext.getDDMStructureId() > 0) && (DDMTemplateLocalServiceUtil.getTemplatesCount(null, PortalUtil.getClassNameId(DDMStructure.class), journalEditDDMStructuresDisplayContext.getDDMStructureId()) > 0) %>">
				<clay:alert
					displayType="info"
					message="there-are-template-references-to-this-structure.-please-update-them-if-a-field-name-is-renamed-or-removed"
				/>
			</c:if>

			<c:if test="<%= (ddmStructure != null) && (groupId != scopeGroupId) %>">
				<clay:alert
					displayType="warning"
					message="this-structure-does-not-belong-to-this-site.-you-may-affect-other-sites-if-you-edit-this-structure"
				/>
			</c:if>

			<div>
				<liferay-data-engine:data-layout-builder
					additionalPanels="<%= journalEditDDMStructuresDisplayContext.getAdditionalPanels() %>"
					componentId='<%= liferayPortletResponse.getNamespace() + "dataLayoutBuilder" %>'
					contentType="journal"
					dataDefinitionId="<%= ddmStructureId %>"
					displayFieldName="<%= journalEditDDMStructuresDisplayContext.getDisplayFieldName() %>"
					groupId="<%= groupId %>"
					namespace="<%= liferayPortletResponse.getNamespace() %>"
					scopes='<%= SetUtil.fromCollection(Arrays.asList("journal")) %>'
					searchableFieldsDisabled="<%= !journalEditDDMStructuresDisplayContext.isStructureFieldIndexableEnable() %>"
					singlePage="<%= true %>"
					submitButtonId='<%= liferayPortletResponse.getNamespace() + "submitButton" %>'
				/>
			</div>
		</clay:container-fluid>
	</div>
</aui:form>

<liferay-frontend:component
	componentId='<%= liferayPortletResponse.getNamespace() + "DataEngineLayoutBuilderHandler" %>'
	context="<%= journalEditDDMStructuresDisplayContext.getDataEngineLayoutBuilderHandlerContext() %>"
	module="{DataEngineLayoutBuilderHandler} from journal-web"
	servletContext="<%= application %>"
/>

<liferay-frontend:component
	componentId='<%= liferayPortletResponse.getNamespace() + "LocaleChangedHandlerComponent" %>'
	context="<%= journalEditDDMStructuresDisplayContext.getLocaleChangedHandlerContext() %>"
	module="{LocaleChangedHandler} from journal-web"
	servletContext="<%= application %>"
/>