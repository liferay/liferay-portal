<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = PortalUtil.escapeRedirect(ParamUtil.getString(request, "redirect"));

long ddmTemplateId = ParamUtil.getLong(request, "ddmTemplateId");

DDMTemplate ddmTemplate = DDMTemplateLocalServiceUtil.fetchDDMTemplate(ddmTemplateId);

long classNameId = BeanParamUtil.getLong(ddmTemplate, request, "classNameId");
long classPK = BeanParamUtil.getLong(ddmTemplate, request, "classPK");
long resourceClassNameId = BeanParamUtil.getLong(ddmTemplate, request, "resourceClassNameId");

long templateEntryId = ParamUtil.getLong(request, "templateEntryId");

EditDDMTemplateDisplayContext editDDMTemplateDisplayContext = (EditDDMTemplateDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

if (ddmTemplate != null) {
	renderResponse.setTitle(LanguageUtil.format(request, "edit-x", HtmlUtil.escape(ddmTemplate.getName(locale))));
}
else {
	renderResponse.setTitle(LanguageUtil.format(request, "add-x", HtmlUtil.escape(editDDMTemplateDisplayContext.getTemplateTypeLabel())));
}
%>

<aui:form action="<%= editDDMTemplateDisplayContext.getUpdateDDMTemplateURL() %>" enctype="multipart/form-data" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="ddmTemplateId" type="hidden" value="<%= ddmTemplateId %>" />
	<aui:input name="groupId" type="hidden" value="<%= scopeGroupId %>" />
	<aui:input name="classPK" type="hidden" value="<%= classPK %>" />
	<aui:input name="classNameId" type="hidden" value="<%= classNameId %>" />
	<aui:input name="resourceClassNameId" type="hidden" value="<%= resourceClassNameId %>" />
	<aui:input name="templateEntryId" type="hidden" value="<%= templateEntryId %>" />
	<aui:input name="saveAndContinue" type="hidden" value="<%= false %>" />

	<liferay-ui:error exception="<%= TemplateNameException.class %>" message="please-enter-a-valid-name" />
	<liferay-ui:error exception="<%= TemplateScriptException.class %>" message="please-enter-a-valid-script" />
	<liferay-ui:error exception="<%= TemplateSmallImageContentException.class %>" message="the-small-image-file-could-not-be-saved" />

	<liferay-ui:error exception="<%= TemplateSmallImageNameException.class %>">

		<%
		String[] imageExtensions = editDDMTemplateDisplayContext.smallImageExtensions();
		%>

		<liferay-ui:message key="image-names-must-end-with-one-of-the-following-extensions" /> <%= HtmlUtil.escape(StringUtil.merge(imageExtensions, StringPool.COMMA)) %>.
	</liferay-ui:error>

	<liferay-ui:error exception="<%= TemplateSmallImageSizeException.class %>">

		<%
		long imageMaxSize = editDDMTemplateDisplayContext.smallImageMaxSize();
		%>

		<liferay-ui:message arguments="<%= LanguageUtil.formatStorageSize(imageMaxSize, locale) %>" key="please-enter-a-small-image-with-a-valid-file-size-no-larger-than-x" translateArguments="<%= false %>" />
	</liferay-ui:error>

	<aui:model-context bean="<%= ddmTemplate %>" model="<%= DDMTemplate.class %>" />

	<nav class="component-tbar subnav-tbar-light tbar tbar-template">
		<clay:container-fluid
			fullWidth="<%= true %>"
		>
			<ul class="tbar-nav">
				<li class="tbar-item tbar-item-expand">
					<aui:input cssClass="form-control-inline" defaultLanguageId="<%= (ddmTemplate == null) ? LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()): ddmTemplate.getDefaultLanguageId() %>" label='<%= LanguageUtil.get(request, "name") %>' labelCssClass="sr-only" name="name" placeholder='<%= LanguageUtil.format(request, "untitled-x", "template") %>' wrapperCssClass="mb-0" />
				</li>
				<li class="tbar-item">
					<div class="c-gap-3 c-mb-0 form-group-sm tbar-section text-right">
						<clay:link
							borderless="<%= true %>"
							displayType="secondary"
							href="<%= redirect %>"
							label="cancel"
							type="button"
						/>

						<clay:button
							displayType="secondary"
							id='<%= liferayPortletResponse.getNamespace() + "saveAndContinueButton" %>'
							label="save-and-continue"
							type="submit"
						/>

						<clay:button
							displayType="primary"
							id='<%= liferayPortletResponse.getNamespace() + "saveButton" %>'
							label="save"
							type="submit"
						/>
					</div>
				</li>
			</ul>
		</clay:container-fluid>
	</nav>

	<div>
		<div id="<portlet:namespace />ddmTemplateEditor">
			<div class="inline-item my-5 p-5 w-100">
				<span aria-hidden="true" class="loading-animation"></span>
			</div>

			<react:component
				componentId="ddmTemplateEditor"
				data="<%= editDDMTemplateDisplayContext.getDDMTemplateEditorContext() %>"
				module="{TemplateEditor} from template-web"
			/>
		</div>
	</div>
</aui:form>