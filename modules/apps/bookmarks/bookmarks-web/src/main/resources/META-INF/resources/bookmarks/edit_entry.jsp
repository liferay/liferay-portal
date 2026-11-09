<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/bookmarks/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");
String backURL = ParamUtil.getString(request, "backURL");

String referringPortletResource = ParamUtil.getString(request, "referringPortletResource");

BookmarksEntry entry = (BookmarksEntry)request.getAttribute(BookmarksWebKeys.BOOKMARKS_ENTRY);

long entryId = BeanParamUtil.getLong(entry, request, "entryId");

long folderId = BeanParamUtil.getLong(entry, request, "folderId");

if (entry != null) {
	BookmarksUtil.addPortletBreadcrumbEntries(entry, request, renderResponse);

	if (!layout.isTypeControlPanel()) {
		PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "edit"), currentURL);
	}
}
else {
	BookmarksUtil.addPortletBreadcrumbEntries(folderId, request, renderResponse);

	if (!layout.isTypeControlPanel()) {
		PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "add-bookmark"), currentURL);
	}
}

boolean showFolderSelector = ParamUtil.getBoolean(request, "showFolderSelector");

String headerTitle = (entry == null) ? LanguageUtil.get(request, "add-bookmark") : LanguageUtil.format(request, "edit-x", entry.getName(), false);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(headerTitle);
%>

<clay:container-fluid
	cssClass="container-form-lg"
	size="lg"
>
	<portlet:actionURL name="/bookmarks/edit_entry" var="editEntryURL">
		<portlet:param name="mvcRenderCommandName" value="/bookmarks/edit_entry" />
	</portlet:actionURL>

	<aui:form action="<%= editEntryURL %>" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "saveEntry();" %>'>
		<aui:input name="<%= Constants.CMD %>" type="hidden" />
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		<aui:input name="backURL" type="hidden" value="<%= backURL %>" />
		<aui:input name="portletResource" type="hidden" value='<%= ParamUtil.getString(request, "portletResource") %>' />
		<aui:input name="referringPortletResource" type="hidden" value="<%= referringPortletResource %>" />
		<aui:input name="entryId" type="hidden" value="<%= entryId %>" />
		<aui:input name="folderId" type="hidden" value="<%= folderId %>" />
		<aui:input name="showFolderSelector" type="hidden" value="<%= showFolderSelector %>" />

		<div class="lfr-form-content">
			<liferay-ui:error exception="<%= EntryURLException.class %>" message="please-enter-a-valid-url" />
			<liferay-ui:error exception="<%= NoSuchFolderException.class %>" message="please-enter-a-valid-folder" />

			<liferay-asset:asset-categories-error />

			<liferay-asset:asset-tags-error />

			<aui:model-context bean="<%= entry %>" model="<%= BookmarksEntry.class %>" />

			<div class="sheet">
				<div class="panel-group panel-group-flush">
					<aui:fieldset>
						<c:if test="<%= showFolderSelector %>">

							<%
							String folderName = StringPool.BLANK;

							if (folderId > 0) {
								BookmarksFolder folder = BookmarksFolderServiceUtil.getFolder(folderId);

								folderId = folder.getFolderId();
								folderName = folder.getName();
							}
							%>

							<liferay-frontend:resource-selector
								inputLabel='<%= LanguageUtil.get(request, "folder") %>'
								inputName="newFolderId"
								modalTitle='<%= LanguageUtil.get(request, "select-folder") %>'
								resourceName="<%= folderName %>"
								resourceValue="<%= String.valueOf(folderId) %>"
								selectEventName="selectFolder"
								selectResourceURL='<%=
									PortletURLBuilder.createRenderURL(
										renderResponse
									).setMVCRenderCommandName(
										"/bookmarks/select_folder"
									).setWindowState(
										LiferayWindowState.POP_UP
									).buildString()
								%>'
								showRemoveButton="<%= true %>"
							/>
						</c:if>

						<aui:input name="name" />

						<aui:input name="url" />

						<aui:input name="description" />
					</aui:fieldset>

					<liferay-expando:custom-attributes-available
						className="<%= BookmarksEntry.class.getName() %>"
					>
						<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="custom-fields">
							<liferay-expando:custom-attribute-list
								className="<%= BookmarksEntry.class.getName() %>"
								classPK="<%= entryId %>"
								editable="<%= true %>"
								label="<%= true %>"
							/>
						</aui:fieldset>
					</liferay-expando:custom-attributes-available>

					<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="categorization">
						<liferay-asset:asset-categories-selector
							className="<%= BookmarksEntry.class.getName() %>"
							classPK="<%= entryId %>"
							visibilityTypes="<%= AssetVocabularyConstants.VISIBILITY_TYPES %>"
						/>

						<liferay-asset:asset-tags-selector
							className="<%= BookmarksEntry.class.getName() %>"
							classPK="<%= entryId %>"
						/>
					</aui:fieldset>

					<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="related-assets">
						<liferay-asset:input-asset-links
							className="<%= BookmarksEntry.class.getName() %>"
							classPK="<%= entryId %>"
						/>
					</aui:fieldset>

					<c:if test="<%= entry == null %>">
						<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="permissions">
							<liferay-ui:input-permissions
								modelName="<%= BookmarksEntry.class.getName() %>"
							/>
						</aui:fieldset>
					</c:if>

					<div class="sheet-footer">
						<aui:button type="submit" />

						<aui:button href="<%= redirect %>" type="cancel" />
					</div>
				</div>
			</div>
		</div>
	</aui:form>
</clay:container-fluid>

<aui:script>
	function <portlet:namespace />saveEntry() {
		var form = document.getElementById('<portlet:namespace />fm');

		if (form) {
			var cmd = form.querySelector(
				'#<portlet:namespace /><%= Constants.CMD %>'
			);

			if (cmd) {
				cmd.setAttribute(
					'value',
					'<%= (entry == null) ? Constants.ADD : Constants.UPDATE %>'
				);

				submitForm(form);
			}
		}
	}
</aui:script>