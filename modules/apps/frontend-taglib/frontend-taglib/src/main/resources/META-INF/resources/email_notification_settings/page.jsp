<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String emailBody = (String)request.getAttribute("liferay-frontend:email-notification-settings:emailBody");
Map<String, String> emailDefinitionTerms = (Map<String, String>)request.getAttribute("liferay-frontend:email-notification-settings:emailDefinitionTerms");
String emailParam = (String)request.getAttribute("liferay-frontend:email-notification-settings:emailParam");
String emailSubject = (String)request.getAttribute("liferay-frontend:email-notification-settings:emailSubject");
String fieldPrefix = (String)request.getAttribute("liferay-frontend:email-notification-settings:fieldPrefix");
String fieldPrefixSeparator = (String)request.getAttribute("liferay-frontend:email-notification-settings:fieldPrefixSeparator");
%>

<aui:fieldset markupView="lexicon">
	<c:if test='<%= GetterUtil.getBoolean(request.getAttribute("liferay-frontend:email-notification-settings:showEmailEnabled")) %>'>
		<aui:input label="enabled" name='<%= fieldPrefix + fieldPrefixSeparator + emailParam + "Enabled" + fieldPrefixSeparator %>' type="checkbox" value='<%= GetterUtil.getBoolean((String)request.getAttribute("liferay-frontend:email-notification-settings:emailEnabled")) %>' />
	</c:if>

	<c:if test='<%= GetterUtil.getBoolean(request.getAttribute("liferay-frontend:email-notification-settings:showSubject")) %>'>
		<c:choose>
			<c:when test="<%= Validator.isNotNull(emailSubject) && Validator.isXml(emailSubject) %>">
				<aui:field-wrapper label="subject">
					<liferay-ui:input-localized
						fieldPrefix="<%= fieldPrefix %>"
						fieldPrefixSeparator="<%= fieldPrefixSeparator %>"
						name='<%= emailParam + "Subject" %>'
						xml="<%= emailSubject %>"
					/>
				</aui:field-wrapper>
			</c:when>
			<c:otherwise>
				<aui:input cssClass="lfr-input-text-container" label="subject" name='<%= fieldPrefix + fieldPrefixSeparator + emailParam + "Subject" + fieldPrefixSeparator %>' value="<%= emailSubject %>" />
			</c:otherwise>
		</c:choose>
	</c:if>

	<aui:field-wrapper helpMessage='<%= (String)request.getAttribute("liferay-frontend:email-notification-settings:helpMessage") %>' label='<%= (String)request.getAttribute("liferay-frontend:email-notification-settings:bodyLabel") %>'>
		<c:choose>
			<c:when test="<%= Validator.isNotNull(emailBody) && Validator.isXml(emailBody) %>">
				<c:choose>
					<c:when test='<%= !FeatureFlagManagerUtil.isEnabled("LPD-11235") %>'>
						<liferay-editor:input-localized
							defaultLanguageId="<%= themeDisplay.getLanguageId() %>"
							fieldPrefix="<%= fieldPrefix %>"
							fieldPrefixSeparator="<%= fieldPrefixSeparator %>"
							name='<%= emailParam + "Body" %>'
							xml="<%= emailBody %>"
						/>
					</c:when>
					<c:otherwise>
						<liferay-ui:input-localized
							editorName='<%= PropsUtil.get("editor.wysiwyg.portal-web.docroot.html.taglib.ui.email_notification_settings.jsp") %>'
							fieldPrefix="<%= fieldPrefix %>"
							fieldPrefixSeparator="<%= fieldPrefixSeparator %>"
							name='<%= emailParam + "Body" %>'
							toolbarSet="email"
							type="editor"
							xml="<%= emailBody %>"
						/>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test='<%= !FeatureFlagManagerUtil.isEnabled("LPD-11235") %>'>
						<liferay-editor:editor
							contents="<%= emailBody %>"
							editorName="ckeditor5_classic"
							formInputEnabled="<%= true %>"
							formInputName='<%= fieldPrefix + fieldPrefixSeparator + emailParam + "Body" + fieldPrefixSeparator %>'
							name="<%= emailParam %>"
						/>
					</c:when>
					<c:otherwise>
						<liferay-editor:editor
							contents="<%= emailBody %>"
							editorName='<%= PropsUtil.get("editor.wysiwyg.portal-web.docroot.html.taglib.ui.email_notification_settings.jsp") %>'
							name="<%= emailParam %>"
						/>

						<aui:input name='<%= fieldPrefix + fieldPrefixSeparator + emailParam + "Body" + fieldPrefixSeparator %>' type="hidden" />
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</aui:field-wrapper>
</aui:fieldset>

<c:if test="<%= (emailDefinitionTerms != null) && !emailDefinitionTerms.isEmpty() %>">
	<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="definition-of-terms" markupView="lexicon">
		<dl>

			<%
			for (Map.Entry<String, String> entry : emailDefinitionTerms.entrySet()) {
			%>

				<dt>
					<%= entry.getKey() %>
				</dt>
				<dd>
					<%= entry.getValue() %>
				</dd>

			<%
			}
			%>

		</dl>
	</aui:fieldset>
</c:if>