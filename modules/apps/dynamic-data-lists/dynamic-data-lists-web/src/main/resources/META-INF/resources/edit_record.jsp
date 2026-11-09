<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String portletResource = ParamUtil.getString(request, "portletResource");

DDLRecord record = (DDLRecord)request.getAttribute(DDLWebKeys.DYNAMIC_DATA_LISTS_RECORD);

long recordId = BeanParamUtil.getLong(record, request, "recordId");

long recordSetId = BeanParamUtil.getLong(record, request, "recordSetId");

long formDDMTemplateId = ParamUtil.getLong(request, "formDDMTemplateId");

DDLRecordVersion recordVersion = null;

if (record != null) {
	recordVersion = record.getLatestRecordVersion();
}

DDLRecordSet recordSet = DDLRecordSetServiceUtil.getRecordSet(recordSetId);

DDMStructure ddmStructure = recordSet.getDDMStructure();

DDMFormValues ddmFormValues = null;

if (recordVersion != null) {
	ddmFormValues = ddlDisplayContext.getDDMFormValues(recordVersion.getDDMStorageId());
}

String defaultLanguageId = ParamUtil.getString(request, "defaultLanguageId");

if (ddmFormValues != null) {
	defaultLanguageId = LocaleUtil.toLanguageId(ddmFormValues.getDefaultLocale());
}
else if (Validator.isNull(defaultLanguageId)) {
	defaultLanguageId = LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault());
}

String languageId = ParamUtil.getString(request, "languageId", defaultLanguageId);

Locale defaultEditLocale = LocaleUtil.fromLanguageId(ddmStructure.getDefaultLanguageId());

if (ddmFormValues != null) {
	defaultEditLocale = ddmFormValues.getDefaultLocale();

	String[] ddmFormValuesAvailableLocales = LocaleUtil.toLanguageIds(ddmFormValues.getAvailableLocales());

	if (ArrayUtil.contains(ddmFormValuesAvailableLocales, themeDisplay.getLanguageId())) {
		defaultEditLocale = themeDisplay.getLocale();
	}
	else if (ArrayUtil.contains(ddmFormValuesAvailableLocales, user.getLanguageId())) {
		defaultEditLocale = user.getLocale();
	}
}

boolean translating = false;

if (!defaultLanguageId.equals(languageId)) {
	translating = true;
}

if (translating) {
	redirect = currentURL;
}

if (ddlDisplayContext.isAdminPortlet()) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(redirect);

	renderResponse.setTitle((record != null) ? LanguageUtil.format(request, "edit-x", ddmStructure.getName(locale), false) : LanguageUtil.format(request, "new-x", ddmStructure.getName(locale), false));
}
else {
	portletDisplay.setShowBackIcon(false);

	renderResponse.setTitle(recordSet.getName(locale));
}
%>

<portlet:actionURL name="/dynamic_data_lists/add_record" var="addRecordURL">
	<portlet:param name="mvcPath" value="/edit_record.jsp" />
</portlet:actionURL>

<portlet:actionURL name="/dynamic_data_lists/update_record" var="updateRecordURL">
	<portlet:param name="mvcPath" value="/edit_record.jsp" />
</portlet:actionURL>

<c:if test="<%= record != null %>">
	<clay:management-toolbar
		infoPanelId="infoPanelId"
		selectable="<%= false %>"
		showSearch="<%= false %>"
	/>
</c:if>

<%@ include file="/deprecated_warning.jspf" %>

<div class="closed sidenav-container sidenav-right" id="<portlet:namespace />infoPanelId">
	<c:if test="<%= recordVersion != null %>">
		<div class="sidenav-menu-slider">
			<div class="sidebar sidebar-light sidenav-menu">
				<div class="sidebar-header">
					<clay:button
						aria-label='<%= LanguageUtil.get(request, "close") %>'
						cssClass="d-sm-none icon-monospaced lfr-portal-tooltip sidenav-close"
						displayType="unstyled"
						icon="times"
						small="<%= true %>"
						title="close"
					/>
				</div>

				<liferay-ui:tabs
					cssClass="navbar-no-collapse"
					names="details,versions"
					refresh="<%= false %>"
					type="dropdown"
				>
					<liferay-ui:section>
						<div class="sidebar-body">
							<ul class="list-unstyled sidebar-dl sidebar-section">
								<li class="sidebar-dt">
									<liferay-ui:message key="version" />
								</li>
								<li class="sidebar-dd">
									<%= HtmlUtil.escape(recordVersion.getVersion()) %>
								</li>
								<li class="sidebar-dt">
									<aui:model-context bean="<%= recordVersion %>" model="<%= DDLRecordVersion.class %>" />

									<aui:workflow-status model="<%= DDLRecord.class %>" status="<%= recordVersion.getStatus() %>" />
								</li>
								<li class="sidebar-dt">
									<liferay-ui:message key="created" />
								</li>
								<li class="sidebar-dd">

									<%
									Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
									%>

									<liferay-ui:message arguments="<%= new Object[] {HtmlUtil.escape(recordVersion.getUserName()), dateTimeFormat.format(recordVersion.getCreateDate())} %>" key="by-x-on-x" translateArguments="<%= false %>" />
								</li>
							</ul>
						</div>
					</liferay-ui:section>

					<liferay-ui:section>
						<div class="sidebar-body">
							<liferay-util:include page="/view_record_history.jsp" servletContext="<%= application %>">
								<liferay-util:param name="redirect" value="<%= redirect %>" />
							</liferay-util:include>
						</div>
					</liferay-ui:section>
				</liferay-ui:tabs>
			</div>
		</div>
	</c:if>

	<clay:container-fluid
		cssClass="container-form-lg sidenav-content"
	>
		<aui:form action="<%= (record == null) ? addRecordURL : updateRecordURL %>" cssClass="container-fluid container-fluid-max-xl" enctype="multipart/form-data" method="post" name="fm">
			<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
			<aui:input name="portletResource" type="hidden" value="<%= portletResource %>" />
			<aui:input name="recordId" type="hidden" value="<%= recordId %>" />
			<aui:input name="groupId" type="hidden" value="<%= recordSet.getGroupId() %>" />
			<aui:input name="recordSetId" type="hidden" value="<%= recordSetId %>" />
			<aui:input name="formDDMTemplateId" type="hidden" value="<%= formDDMTemplateId %>" />
			<aui:input name="defaultLanguageId" type="hidden" value="<%= defaultLanguageId %>" />
			<aui:input name="languageId" type="hidden" value="<%= languageId %>" />
			<aui:input name="workflowAction" type="hidden" value="<%= WorkflowConstants.ACTION_PUBLISH %>" />

			<div class="lfr-form-content">
				<liferay-ui:error exception="<%= DuplicateFileEntryException.class %>" message="a-file-with-that-name-already-exists" />

				<liferay-ui:error exception="<%= FileSizeException.class %>">

					<%
					FileSizeException fileSizeException = (FileSizeException)errorException;
					%>

					<liferay-ui:message arguments="<%= LanguageUtil.formatStorageSize(fileSizeException.getMaxSize(), locale) %>" key="please-enter-a-file-with-a-valid-file-size-no-larger-than-x" translateArguments="<%= false %>" />
				</liferay-ui:error>

				<liferay-ui:error exception="<%= StorageFieldRequiredException.class %>" message="please-fill-out-all-required-fields" />

				<%
				long classNameId = PortalUtil.getClassNameId(DDMStructure.class);

				long classPK = recordSet.getDDMStructureId();

				if (formDDMTemplateId > 0) {
					classNameId = PortalUtil.getClassNameId(DDMTemplate.class);

					classPK = formDDMTemplateId;
				}
				%>

				<c:choose>
					<c:when test="<%= ddlDisplayContext.isFormView() %>">
						<liferay-ddm:html
							classNameId="<%= classNameId %>"
							classPK="<%= classPK %>"
							ddmFormValues="<%= ddmFormValues %>"
							defaultEditLocale="<%= defaultEditLocale %>"
							defaultLocale="<%= LocaleUtil.fromLanguageId(defaultLanguageId) %>"
							groupId="<%= recordSet.getGroupId() %>"
							repeatable="<%= translating ? false : true %>"
							requestedLocale="<%= locale %>"
							showLanguageSelector="<%= false %>"
						/>
					</c:when>
					<c:otherwise>
						<div class="sheet">
							<div class="panel-group panel-group-flush">
								<aui:fieldset>
									<liferay-ddm:html
										classNameId="<%= classNameId %>"
										classPK="<%= classPK %>"
										ddmFormValues="<%= ddmFormValues %>"
										defaultEditLocale="<%= defaultEditLocale %>"
										defaultLocale="<%= LocaleUtil.fromLanguageId(defaultLanguageId) %>"
										groupId="<%= recordSet.getGroupId() %>"
										repeatable="<%= translating ? false : true %>"
										requestedLocale="<%= locale %>"
									/>
								</aui:fieldset>
							</div>
						</div>
					</c:otherwise>
				</c:choose>

				<%
				boolean pending = false;

				if (recordVersion != null) {
					pending = recordVersion.isPending();
				}
				%>

				<c:if test="<%= pending %>">
					<div class="alert alert-info">
						<liferay-ui:message key="there-is-a-publication-workflow-in-process" />
					</div>
				</c:if>
			</div>

			<aui:button-row>

				<%
				String saveButtonLabel = "save";

				if ((recordVersion == null) || recordVersion.isDraft() || recordVersion.isApproved()) {
					saveButtonLabel = "save-as-draft";
				}

				String publishButtonLabel = "publish";

				if (WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(themeDisplay.getCompanyId(), scopeGroupId, DDLRecordSet.class.getName(), recordSetId)) {
					publishButtonLabel = "submit-for-workflow";
				}

				if (ddlDisplayContext.isFormView()) {
					publishButtonLabel = "submit";
				}
				%>

				<c:if test="<%= ddlDisplayContext.isShowSaveRecordButton() %>">
					<aui:button name="saveButton" onClick='<%= liferayPortletResponse.getNamespace() + "setWorkflowAction(true);" %>' primary="<%= false %>" type="submit" value="<%= saveButtonLabel %>" />
				</c:if>

				<c:if test="<%= ddlDisplayContext.isShowPublishRecordButton() %>">
					<aui:button disabled="<%= pending %>" name="publishButton" onClick='<%= liferayPortletResponse.getNamespace() + "setWorkflowAction(false);" %>' type="submit" value="<%= publishButtonLabel %>" />
				</c:if>

				<c:if test="<%= ddlDisplayContext.isShowCancelButton() %>">
					<aui:button href="<%= redirect %>" name="cancelButton" type="cancel" />
				</c:if>
			</aui:button-row>
		</aui:form>
	</clay:container-fluid>
</div>

<aui:script>
	function <portlet:namespace />setWorkflowAction(draft) {
		if (draft) {
			document.<portlet:namespace />fm.<portlet:namespace />workflowAction.value =
				<%= WorkflowConstants.ACTION_SAVE_DRAFT %>;
		}
		else {
			document.<portlet:namespace />fm.<portlet:namespace />workflowAction.value =
				<%= WorkflowConstants.ACTION_PUBLISH %>;
		}
	}
</aui:script>

<%
PortalUtil.addPortletBreadcrumbEntry(
	request, recordSet.getName(locale),
	PortletURLBuilder.createRenderURL(
		renderResponse
	).setMVCPath(
		"/view_record_set.jsp"
	).setParameter(
		"recordSetId", recordSetId
	).buildString());

if (record != null) {
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.format(request, "edit-x", ddmStructure.getName(locale), false), currentURL);
}
else {
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.format(request, "add-x", ddmStructure.getName(locale), false), currentURL);
}
%>