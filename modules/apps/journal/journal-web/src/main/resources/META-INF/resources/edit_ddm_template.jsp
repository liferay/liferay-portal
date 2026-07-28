<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
JournalEditDDMTemplateDisplayContext journalEditDDMTemplateDisplayContext = (JournalEditDDMTemplateDisplayContext)request.getAttribute(JournalEditDDMTemplateDisplayContext.class.getName());

DDMTemplate ddmTemplate = journalEditDDMTemplateDisplayContext.getDDMTemplate();

if (ddmTemplate != null) {
	String script = ddmTemplate.getScript();

	ddmTemplate.setScript(Base64.encode(script.getBytes(StringPool.UTF8)));
}

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(journalEditDDMTemplateDisplayContext.getRedirect());
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

renderResponse.setTitle(journalEditDDMTemplateDisplayContext.getTitle());
%>

<portlet:actionURL name="/journal/add_ddm_template" var="addDDMTemplateURL">
	<portlet:param name="mvcRenderCommandName" value="/journal/edit_ddm_template" />
</portlet:actionURL>

<portlet:actionURL name="/journal/update_ddm_template" var="updateDDMTemplateURL">
	<portlet:param name="mvcRenderCommandName" value="/journal/edit_ddm_template" />
</portlet:actionURL>

<aui:form action="<%= (ddmTemplate == null) ? addDDMTemplateURL : updateDDMTemplateURL %>" cssClass="edit-article-form" enctype="multipart/form-data" method="post" name="fm" onSubmit="event.preventDefault();">
	<aui:input name="redirect" type="hidden" value="<%= journalEditDDMTemplateDisplayContext.getRedirect() %>" />
	<aui:input name="ddmTemplateId" type="hidden" value="<%= journalEditDDMTemplateDisplayContext.getDDMTemplateId() %>" />
	<aui:input name="groupId" type="hidden" value="<%= journalEditDDMTemplateDisplayContext.getGroupId() %>" />
	<aui:input name="classPK" type="hidden" value="<%= journalEditDDMTemplateDisplayContext.getClassPK() %>" />
	<aui:input name="saveAndContinue" type="hidden" value="<%= false %>" />

	<aui:model-context bean="<%= ddmTemplate %>" model="<%= DDMTemplate.class %>" />

	<nav class="component-tbar subnav-tbar-light tbar tbar-article">
		<clay:container-fluid
			fullWidth="<%= true %>"
		>
			<ul class="tbar-nav">
				<li class="tbar-item tbar-item-expand">
					<aui:input cssClass="form-control-inline" defaultLanguageId="<%= (ddmTemplate == null) ? LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()): ddmTemplate.getDefaultLanguageId() %>" label='<%= LanguageUtil.get(request, "name") %>' labelCssClass="sr-only" name="name" placeholder='<%= LanguageUtil.format(request, "untitled-x", "template") %>' wrapperCssClass="article-content-title mb-0" />
				</li>
				<li class="tbar-item">
					<div class="c-gap-3 c-mb-0 form-group-sm journal-article-button-row mb-0 tbar-section text-right">
						<clay:link
							borderless="<%= true %>"
							displayType="secondary"
							href="<%= PortalUtil.escapeRedirect(journalEditDDMTemplateDisplayContext.getRedirect()) %>"
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
				module="{TemplateEditor} from template-web"
				props="<%= journalEditDDMTemplateDisplayContext.getDDMTemplateEditorContext(scopeGroupId) %>"
			/>
		</div>
	</div>
</aui:form>