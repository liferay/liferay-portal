<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceNotificationTemplatesDisplayContext commerceNotificationTemplatesDisplayContext = (CommerceNotificationTemplatesDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceNotificationTemplate commerceNotificationTemplate = commerceNotificationTemplatesDisplayContext.getCommerceNotificationTemplate();

String name = BeanParamUtil.getString(commerceNotificationTemplate, renderRequest, "name");
String description = BeanParamUtil.getString(commerceNotificationTemplate, renderRequest, "description");
String from = BeanParamUtil.getString(commerceNotificationTemplate, renderRequest, "from");
String fromName = BeanParamUtil.getString(commerceNotificationTemplate, renderRequest, "fromName");
String cc = BeanParamUtil.getString(commerceNotificationTemplate, renderRequest, "cc");
String bcc = BeanParamUtil.getString(commerceNotificationTemplate, renderRequest, "bcc");

String type = BeanParamUtil.getString(commerceNotificationTemplate, renderRequest, "type");

CommerceNotificationType commerceNotificationType = commerceNotificationTemplatesDisplayContext.getCommerceNotificationType(type);

Map<String, String> definitionTerms = null;

if (commerceNotificationType != null) {
	definitionTerms = commerceNotificationTemplatesDisplayContext.getDefinitionTerms(CommerceDefinitionTermConstants.RECIPIENT_DEFINITION_TERMS_CONTRIBUTOR, commerceNotificationType.getKey(), locale);
}

String title = LanguageUtil.get(resourceBundle, "add-notification-template");

if (commerceNotificationTemplate != null) {
	title = LanguageUtil.format(request, "edit-x", commerceNotificationTemplate.getName(), false);
}
%>

<liferay-frontend:side-panel-content
	title="<%= title %>"
>
	<portlet:actionURL name="/commerce_channels/edit_commerce_notification_template" var="editCommerceNotificationTemplateActionURL" />

	<aui:form action="<%= editCommerceNotificationTemplateActionURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (commerceNotificationTemplate == null) ? Constants.ADD : Constants.UPDATE %>" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="commerceChannelId" type="hidden" value="<%= commerceNotificationTemplatesDisplayContext.getCommerceChannelId() %>" />
		<aui:input name="commerceNotificationTemplateId" type="hidden" value="<%= (commerceNotificationTemplate == null) ? 0 : commerceNotificationTemplate.getCommerceNotificationTemplateId() %>" />

		<liferay-ui:error exception="<%= CommerceNotificationTemplateFromException.class %>" message="please-enter-a-valid-email-address" />
		<liferay-ui:error exception="<%= CommerceNotificationTemplateNameException.class %>" message="please-enter-a-valid-name" />
		<liferay-ui:error exception="<%= CommerceNotificationTemplateTypeException.class %>" message="please-select-a-valid-type" />

		<aui:model-context bean="<%= commerceNotificationTemplate %>" model="<%= CommerceNotificationTemplate.class %>" />

		<commerce-ui:panel
			title='<%= LanguageUtil.get(request, "details") %>'
		>
			<div class="row">
				<div class="col-12">
					<aui:input name="name" value="<%= name %>" />

					<aui:input name="description" value="<%= description %>" />
				</div>

				<div class="col-6">
					<aui:select name="type" onChange='<%= liferayPortletResponse.getNamespace() + "selectType();" %>' showEmptyOption="<%= true %>">

						<%
						for (CommerceNotificationType curCommerceNotificationType : commerceNotificationTemplatesDisplayContext.getCommerceNotificationTypes()) {
							String commerceNotificationTypeKey = curCommerceNotificationType.getKey();
						%>

							<aui:option label="<%= curCommerceNotificationType.getLabel(locale) %>" selected="<%= (commerceNotificationType != null) && commerceNotificationTypeKey.equals(type) %>" value="<%= commerceNotificationTypeKey %>" />

						<%
						}
						%>

					</aui:select>
				</div>

				<div class="col-6">
					<aui:input checked="<%= (commerceNotificationTemplate == null) ? false : commerceNotificationTemplate.getEnabled() %>" name="enabled" type="toggle-switch" />
				</div>
			</div>
		</commerce-ui:panel>

		<commerce-ui:panel
			title='<%= LanguageUtil.get(resourceBundle, "email-settings") %>'
		>
			<div class="row">
				<div class="col-12">
					<label for="<portlet:namespace />toFieldWrapper"><liferay-ui:message key="to[recipient]" /></label>

					<aui:field-wrapper label="" name="toFieldWrapper">
						<liferay-ui:input-localized
							name="to"
							xml="<%= (commerceNotificationTemplate == null) ? StringPool.BLANK : commerceNotificationTemplate.getTo() %>"
						/>
					</aui:field-wrapper>
				</div>

				<div class="col-6">
					<aui:input name="cc" value="<%= cc %>" />

					<aui:input label="from-address" name="from" value="<%= from %>" />
				</div>

				<div class="col-6">
					<aui:input name="bcc" value="<%= bcc %>" />

					<aui:input name="fromName" value="<%= fromName %>" />
				</div>

				<c:if test="<%= (definitionTerms != null) && !definitionTerms.isEmpty() %>">
					<div class="col-12">
						<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="definition-of-terms" markupView="lexicon">
							<dl>

								<%
								for (Map.Entry<String, String> entry : definitionTerms.entrySet()) {
								%>

									<dt>
										<%= HtmlUtil.escape(entry.getKey()) %>
									</dt>
									<dd>
										<%= HtmlUtil.escape(entry.getValue()) %>
									</dd>

								<%
								}
								%>

							</dl>
						</aui:fieldset>
					</div>
				</c:if>
			</div>
		</commerce-ui:panel>

		<commerce-ui:panel
			title='<%= LanguageUtil.get(resourceBundle, "email-content") %>'
		>
			<label for="<portlet:namespace />subjectFieldWrapper"><liferay-ui:message key="subject" /></label>

			<aui:field-wrapper label="" name="subjectFieldWrapper">
				<liferay-ui:input-localized
					name="subject"
					xml="<%= (commerceNotificationTemplate == null) ? StringPool.BLANK : commerceNotificationTemplate.getSubject() %>"
				/>
			</aui:field-wrapper>

			<%
			if (commerceNotificationType != null) {
				definitionTerms = commerceNotificationTemplatesDisplayContext.getDefinitionTerms(CommerceDefinitionTermConstants.BODY_AND_SUBJECT_DEFINITION_TERMS_CONTRIBUTOR, commerceNotificationType.getKey(), locale);
			}
			%>

			<label for="<portlet:namespace />bodyFieldWrapper"><liferay-ui:message key="body" /></label>

			<aui:field-wrapper label="" name="bodyFieldWrapper">
				<liferay-ui:input-localized
					editorName='<%= PropsUtil.get("editor.wysiwyg.portal-web.docroot.html.taglib.ui.email_notification_settings.jsp") %>'
					name="body"
					toolbarSet="email"
					type="editor"
					xml="<%= (commerceNotificationTemplate == null) ? StringPool.BLANK : commerceNotificationTemplate.getBody() %>"
				/>
			</aui:field-wrapper>

			<c:if test="<%= (definitionTerms != null) && !definitionTerms.isEmpty() %>">
				<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="definition-of-terms" markupView="lexicon">
					<dl>

						<%
						for (Map.Entry<String, String> entry : definitionTerms.entrySet()) {
						%>

							<dt>
								<%= HtmlUtil.escape(entry.getKey()) %>
							</dt>
							<dd>
								<%= HtmlUtil.escape(entry.getValue()) %>
							</dd>

						<%
						}
						%>

					</dl>
				</aui:fieldset>
			</c:if>
		</commerce-ui:panel>

		<aui:button-row>
			<aui:button cssClass="btn-lg" type="submit" />
		</aui:button-row>
	</aui:form>
</liferay-frontend:side-panel-content>

<aui:script sandbox="<%= true %>">
	Liferay.provide(window, '<portlet:namespace />selectType', () => {
		const portletURL = Liferay.Util.PortletURL.createPortletURL(
			'<%= currentURLObj %>',
			{
				bcc: document.getElementById('<portlet:namespace />bcc').value,
				cc: document.getElementById('<portlet:namespace />cc').value,
				description: document.getElementById(
					'<portlet:namespace />description'
				).value,
				from: document.getElementById('<portlet:namespace />from').value,
				fromName: document.getElementById('<portlet:namespace />fromName')
					.value,
				name: document.getElementById('<portlet:namespace />name').value,
				type: document.getElementById('<portlet:namespace />type').value,
			}
		);

		window.location.replace(portletURL.toString());
	});
</aui:script>