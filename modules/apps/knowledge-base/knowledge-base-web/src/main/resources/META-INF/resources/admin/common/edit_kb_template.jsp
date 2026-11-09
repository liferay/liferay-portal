<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/common/init.jsp" %>

<%
KBTemplate kbTemplate = (KBTemplate)request.getAttribute(KBWebKeys.KNOWLEDGE_BASE_KB_TEMPLATE);

long kbTemplateId = BeanParamUtil.getLong(kbTemplate, request, "kbTemplateId");

String title = BeanParamUtil.getString(kbTemplate, request, "title");
String content = BeanParamUtil.getString(kbTemplate, request, "content");

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);
portletDisplay.setURLBackTitle(portletDisplay.getTitle());

renderResponse.setTitle((kbTemplate == null) ? LanguageUtil.get(request, "new-template") : kbTemplate.getTitle());
%>

<liferay-portlet:actionURL name="/knowledge_base/update_kb_template" var="updateKBTemplateURL" />

<clay:container-fluid
	cssClass="container-form-lg"
	size="lg"
>
	<aui:form action="<%= updateKBTemplateURL %>" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "updateKBTemplate();" %>'>
		<aui:input name="mvcPath" type="hidden" value="/admin/common/edit_kb_template.jsp" />
		<aui:input name="<%= Constants.CMD %>" type="hidden" />
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		<aui:input name="kbTemplateId" type="hidden" value="<%= String.valueOf(kbTemplateId) %>" />

		<liferay-ui:error exception="<%= KBTemplateContentException.class %>" message="please-enter-valid-content" />
		<liferay-ui:error exception="<%= KBTemplateTitleException.class %>" message="please-enter-a-valid-title" />

		<aui:model-context bean="<%= kbTemplate %>" model="<%= KBTemplate.class %>" />

		<div class="sheet">
			<div class="panel-group panel-group-flush">
				<aui:fieldset>
					<aui:input autocomplete="off" label='<%= LanguageUtil.get(request, "title") %>' name="title" required="<%= true %>" type="text" value="<%= HtmlUtil.escape(title) %>" />

					<liferay-editor:editor
						contents="<%= content %>"
						editorName="ckeditor"
						name="contentEditor"
						placeholder="content"
						required="<%= true %>"
					>
						<aui:validator errorMessage='<%= LanguageUtil.format(resourceBundle, "the-x-field-is-required", "content", true) %>' name="required" />
					</liferay-editor:editor>

					<aui:input name="content" type="hidden" />
				</aui:fieldset>

				<c:if test="<%= kbTemplate == null %>">
					<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="permissions">
						<liferay-ui:input-permissions
							modelName="<%= KBTemplate.class.getName() %>"
						/>
					</aui:fieldset>
				</c:if>

				<div class="sheet-footer">
					<aui:button type="submit" value="publish" />

					<aui:button href="<%= redirect %>" type="cancel" />
				</div>
			</div>
		</div>
	</aui:form>
</clay:container-fluid>

<aui:script>
	function <portlet:namespace />updateKBTemplate() {
		Liferay.Util.postForm(document.<portlet:namespace />fm, {
			data: {
				<%= Constants.CMD %>:
					'<%= (kbTemplate == null) ? Constants.ADD : Constants.UPDATE %>',
				title: document.getElementById('<portlet:namespace />title').value,
				content: window.<portlet:namespace />contentEditor.getHTML(),
			},
		});
	}
</aui:script>