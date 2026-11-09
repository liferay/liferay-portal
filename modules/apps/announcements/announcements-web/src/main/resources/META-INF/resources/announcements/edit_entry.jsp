<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/announcements/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

AnnouncementsEntry entry = (AnnouncementsEntry)request.getAttribute(AnnouncementsWebKeys.ANNOUNCEMENTS_ENTRY);

long entryId = BeanParamUtil.getLong(entry, request, "entryId", AnnouncementsEntry::getEntryId);

String title = BeanParamUtil.getString(entry, request, "title", AnnouncementsEntry::getTitle);
String content = BeanParamUtil.getString(entry, request, "content", AnnouncementsEntry::getContent);

boolean alert = BeanParamUtil.getBoolean(entry, request, "alert", AnnouncementsEntry::isAlert);

boolean displayImmediately = ParamUtil.getBoolean(request, "displayImmediately", entry == null);

String headerTitle = null;

if (entry != null) {
	headerTitle = entry.getTitle();
}
else {
	if (alert) {
		headerTitle = LanguageUtil.get(resourceBundle, "new-alert");
	}
	else {
		headerTitle = LanguageUtil.get(resourceBundle, "new-announcement");
	}
}

boolean portletTitleBasedNavigation = GetterUtil.getBoolean(portletConfig.getInitParameter("portlet-title-based-navigation"));

if (portletTitleBasedNavigation) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(redirect);

	renderResponse.setTitle(headerTitle);
}
%>

<div <%= portletTitleBasedNavigation ? "class=\"container-fluid container-fluid-max-xl\"" : StringPool.BLANK %>>
	<aui:form method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "saveEntry();" %>'>
		<aui:input name="<%= Constants.CMD %>" type="hidden" />
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		<aui:input name="entryId" type="hidden" value="<%= entryId %>" />
		<aui:input name="alert" type="hidden" value="<%= alert %>" />

		<c:if test="<%= !portletTitleBasedNavigation %>">
			<liferay-ui:header
				backURL="<%= redirect %>"
				title="entry"
			/>
		</c:if>

		<liferay-ui:error exception="<%= EntryContentException.class %>" message="please-enter-valid-content" />
		<liferay-ui:error exception="<%= EntryDisplayDateException.class %>" message="please-enter-a-valid-display-date" />
		<liferay-ui:error exception="<%= EntryExpirationDateException.class %>" message="please-enter-a-valid-expiration-date" />
		<liferay-ui:error exception="<%= EntryTitleException.class %>" message="please-enter-a-valid-title" />
		<liferay-ui:error exception="<%= EntryURLException.class %>" message="please-enter-a-valid-url" />

		<aui:model-context bean="<%= entry %>" model="<%= AnnouncementsEntry.class %>" />

		<div class="sheet">
			<div class="panel-group panel-group-flush">
				<aui:fieldset>
					<aui:input autocomplete="off" id="titleEditor" label='<%= LanguageUtil.get(request, "title") %>' name="title" required="<%= true %>" title="" type="text" value="<%= HtmlUtil.escape(title) %>">
						<aui:validator name="maxLength"><%= ModelHintsUtil.getMaxLength(AnnouncementsEntry.class.getName(), "title") %></aui:validator>
					</aui:input>

					<liferay-editor:editor
						contents="<%= content %>"
						editorName='<%= PropsUtil.get("editor.wysiwyg.portal-web.docroot.html.portlet.announcements.edit_entry.jsp") %>'
						name="contentEditor"
						placeholder="content"
						required="<%= true %>"
					/>

					<aui:input name="content" type="hidden" />
				</aui:fieldset>

				<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="configuration">
					<c:choose>
						<c:when test="<%= entry != null %>">
							<%@ include file="/announcements/entry_scope.jspf" %>
						</c:when>
						<c:otherwise>

							<%
							String distributionScope = ParamUtil.getString(request, "distributionScope");

							long classNameId = 0;
							long classPK = 0;

							String[] distributionScopeArray = StringUtil.split(distributionScope);

							if (distributionScopeArray.length == 2) {
								classNameId = GetterUtil.getLong(distributionScopeArray[0]);
								classPK = GetterUtil.getLong(distributionScopeArray[1]);
							}

							boolean submitOnChange = false;
							%>

							<%@ include file="/announcements/entry_select_scope.jspf" %>
						</c:otherwise>
					</c:choose>

					<aui:input name="url" />

					<aui:select name="type">

						<%
						for (String curType : AnnouncementsEntryConstants.TYPES) {
						%>

							<aui:option label="<%= curType %>" selected="<%= (entry != null) && curType.equals(entry.getType()) %>" />

						<%
						}
						%>

					</aui:select>

					<c:choose>
						<c:when test="<%= alert %>">
							<aui:select disabled="<%= true %>" name="priority">
								<aui:option label="important" selected="<%= true %>" value="1" />
							</aui:select>
						</c:when>
						<c:otherwise>
							<aui:select name="priority">
								<aui:option label="normal[priority]" selected="<%= (entry != null) && (entry.getPriority() == 0) %>" value="0" />
								<aui:option label="important" selected="<%= (entry != null) && (entry.getPriority() == 1) %>" value="1" />
							</aui:select>
						</c:otherwise>
					</c:choose>

					<aui:input dateTogglerCheckboxLabel="display-immediately" disabled="<%= displayImmediately %>" name="displayDate" />

					<aui:input name="expirationDate" />
				</aui:fieldset>

				<div class="sheet-footer">
					<aui:button primary="<%= true %>" type="submit" />

					<aui:button href="<%= redirect %>" type="cancel" />
				</div>
			</div>
		</div>
	</aui:form>
</div>

<aui:script>
	function <portlet:namespace />saveEntry() {
		var form = document.getElementById('<portlet:namespace />fm');

		if (form) {
			form.action =
				'<portlet:actionURL name="/announcements/edit_entry"><portlet:param name="mvcRenderCommandName" value="/announcements/edit_entry" /></portlet:actionURL>';
			form.target = '';

			var cmd = form.querySelector(
				'#<portlet:namespace /><%= Constants.CMD %>'
			);

			if (cmd) {
				cmd.setAttribute(
					'value',
					'<%= (entry == null) ? Constants.ADD : Constants.UPDATE %>'
				);
			}

			var content = form.querySelector('#<portlet:namespace />content');

			if (content) {
				content.setAttribute(
					'value',
					window.<portlet:namespace />contentEditor.getHTML()
				);
			}

			submitForm(form);
		}
	}
</aui:script>