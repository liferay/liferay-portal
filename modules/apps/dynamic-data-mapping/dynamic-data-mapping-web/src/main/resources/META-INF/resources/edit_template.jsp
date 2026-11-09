<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String closeRedirect = ParamUtil.getString(request, "closeRedirect");

String portletResource = ParamUtil.getString(request, "portletResource");

String portletResourceNamespace = ParamUtil.getString(request, "portletResourceNamespace");

DDMTemplate template = (DDMTemplate)request.getAttribute(DDMWebKeys.DYNAMIC_DATA_MAPPING_TEMPLATE);

long templateId = BeanParamUtil.getLong(template, request, "templateId");

long groupId = BeanParamUtil.getLong(template, request, "groupId", PortalUtil.getScopeGroupId(request, refererPortletName));
long classNameId = BeanParamUtil.getLong(template, request, "classNameId");
long classPK = BeanParamUtil.getLong(template, request, "classPK");
long resourceClassNameId = BeanParamUtil.getLong(template, request, "resourceClassNameId");

boolean cacheable = BeanParamUtil.getBoolean(template, request, "cacheable", true);
boolean smallImage = BeanParamUtil.getBoolean(template, request, "smallImage");

DDMStructure structure = (DDMStructure)request.getAttribute(DDMWebKeys.DYNAMIC_DATA_MAPPING_STRUCTURE);

if ((structure == null) && (template != null)) {
	structure = ddmDisplayContext.fetchStructure(template);
}

String type = BeanParamUtil.getString(template, request, "type", DDMTemplateConstants.TEMPLATE_TYPE_FORM);
String mode = BeanParamUtil.getString(template, request, "mode", DDMTemplateConstants.TEMPLATE_MODE_CREATE);
String language = BeanParamUtil.getString(template, request, "language", ddmDisplay.getDefaultTemplateLanguage());
String script = BeanParamUtil.getString(template, request, "script");

if (Validator.isNull(script) && type.equals(DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY)) {
	TemplateHandler templateHandler = TemplateHandlerRegistryUtil.getTemplateHandler(classNameId);

	if ((templateHandler == null) && (structure != null)) {
		templateHandler = TemplateHandlerRegistryUtil.getTemplateHandler(structure.getClassNameId());
	}

	if (templateHandler != null) {
		script = templateHandler.getTemplatesHelpContent(language);
	}
	else {
		script = StringUtil.read(DDMWebConfigurationUtil.class.getClassLoader(), DDMWebConfigurationUtil.get(DDMWebConfigurationKeys.DYNAMIC_DATA_MAPPING_TEMPLATE_LANGUAGE_CONTENT, new Filter(language)));
	}
}

DDMTemplateVersion templateVersion = null;

if (template != null) {
	templateVersion = template.getLatestTemplateVersion();
}

String structureAvailableFields = ParamUtil.getString(request, "structureAvailableFields");

if (Validator.isNotNull(structureAvailableFields)) {
	scopeAvailableFields = structureAvailableFields;
}

boolean showBackURL = ParamUtil.getBoolean(request, "showBackURL", true);
%>

<portlet:actionURL name="/dynamic_data_mapping/add_template" var="addTemplateURL">
	<portlet:param name="mvcPath" value="/edit_template.jsp" />
</portlet:actionURL>

<portlet:actionURL name="/dynamic_data_mapping/update_template" var="updateTemplateURL">
	<portlet:param name="mvcPath" value="/edit_template.jsp" />
</portlet:actionURL>

<clay:container-fluid>
	<aui:form action="<%= (template == null) ? addTemplateURL : updateTemplateURL %>" cssClass="container-fluid container-fluid-max-xl" enctype="multipart/form-data" method="post" name="fm" onSubmit="event.preventDefault();">
		<aui:input name="redirect" type="hidden" value="<%= ddmDisplay.getEditTemplateBackURL(liferayPortletRequest, liferayPortletResponse, classNameId, classPK, resourceClassNameId, portletResource) %>" />
		<aui:input name="closeRedirect" type="hidden" value="<%= closeRedirect %>" />
		<aui:input name="portletResource" type="hidden" value="<%= portletResource %>" />
		<aui:input name="portletResourceNamespace" type="hidden" value="<%= portletResourceNamespace %>" />
		<aui:input name="templateId" type="hidden" value="<%= templateId %>" />
		<aui:input name="groupId" type="hidden" value="<%= groupId %>" />
		<aui:input name="classNameId" type="hidden" value="<%= classNameId %>" />
		<aui:input name="classPK" type="hidden" value="<%= classPK %>" />
		<aui:input name="resourceClassNameId" type="hidden" value="<%= resourceClassNameId %>" />
		<aui:input name="type" type="hidden" value="<%= type %>" />
		<aui:input name="status" type="hidden" value="<%= String.valueOf(WorkflowConstants.STATUS_APPROVED) %>" />
		<aui:input name="structureAvailableFields" type="hidden" value="<%= structureAvailableFields %>" />
		<aui:input name="saveAndContinue" type="hidden" value="<%= false %>" />

		<div class="lfr-form-content">
			<liferay-ui:error exception="<%= TemplateNameException.class %>" message="please-enter-a-valid-name" />
			<liferay-ui:error exception="<%= TemplateScriptException.class %>" message="please-enter-a-valid-script" />
			<liferay-ui:error exception="<%= TemplateSmallImageContentException.class %>" message="the-small-image-file-could-not-be-saved" />

			<liferay-ui:error exception="<%= TemplateSmallImageNameException.class %>">

				<%
				String[] imageExtensions = ddmDisplayContext.smallImageExtensions();
				%>

				<liferay-ui:message key="image-names-must-end-with-one-of-the-following-extensions" /> <%= HtmlUtil.escape(StringUtil.merge(imageExtensions, StringPool.COMMA)) %>.
			</liferay-ui:error>

			<liferay-ui:error exception="<%= TemplateSmallImageSizeException.class %>">

				<%
				long imageMaxSize = ddmDisplayContext.smallImageMaxSize();
				%>

				<liferay-ui:message arguments="<%= LanguageUtil.formatStorageSize(imageMaxSize, locale) %>" key="please-enter-a-small-image-with-a-valid-file-size-no-larger-than-x" translateArguments="<%= false %>" />
			</liferay-ui:error>

			<c:if test='<%= ParamUtil.getBoolean(request, "showHeader", true) %>'>

				<%
				String title = StringPool.BLANK;

				if ((structure != null) || (template != null)) {
					title = ddmDisplay.getEditTemplateTitle(structure, template, locale);
				}
				else {
					title = ddmDisplay.getEditTemplateTitle(classNameId, locale);
				}
				%>

				<c:choose>
					<c:when test="<%= ddmDisplay.isShowBackURLInTitleBar() %>">

						<%
						portletDisplay.setShowBackIcon(true);
						portletDisplay.setURLBack(PortalUtil.escapeRedirect(ddmDisplay.getEditTemplateBackURL(liferayPortletRequest, liferayPortletResponse, classNameId, classPK, resourceClassNameId, portletResource)));

						renderResponse.setTitle(title);
						%>

					</c:when>
					<c:otherwise>
						<liferay-ui:header
							backURL="<%= ddmDisplay.getEditTemplateBackURL(liferayPortletRequest, liferayPortletResponse, classNameId, classPK, resourceClassNameId, portletResource) %>"
							localizeTitle="<%= false %>"
							showBackURL="<%= showBackURL %>"
							title="<%= title %>"
						/>
					</c:otherwise>
				</c:choose>
			</c:if>

			<aui:model-context bean="<%= template %>" model="<%= DDMTemplate.class %>" />

			<c:if test="<%= (templateVersion != null) && ddmDisplay.isVersioningEnabled() %>">
				<aui:workflow-status model="<%= DDMTemplate.class %>" status="<%= templateVersion.getStatus() %>" version="<%= templateVersion.getVersion() %>" />

				<div class="template-history-toolbar" id="<portlet:namespace />templateHistoryToolbar"></div>

				<aui:script use="aui-dialog-iframe-deprecated,aui-toolbar,liferay-util-window">
					var toolbarChildren = [
						<portlet:renderURL var="viewHistoryURL">
							<portlet:param name="mvcPath" value="/view_template_history.jsp" />
							<portlet:param name="redirect" value="<%= ddmDisplay.getEditTemplateBackURL(liferayPortletRequest, liferayPortletResponse, classNameId, classPK, resourceClassNameId, portletResource) %>" />
							<portlet:param name="templateId" value="<%= String.valueOf(template.getTemplateId()) %>" />
						</portlet:renderURL>

						{
							label: '<%= UnicodeLanguageUtil.get(request, "view-history") %>',
							on: {
								click: function (event) {
									event.domEvent.preventDefault();

									window.location.href = '<%= viewHistoryURL %>';
								},
							},
						},
					];

					new A.Toolbar({
						boundingBox: '#<portlet:namespace />templateHistoryToolbar',
						children: toolbarChildren,
					}).render();
				</aui:script>
			</c:if>

			<div class="sheet">
				<div class="panel-group panel-group-flush">
					<aui:fieldset>
						<c:if test="<%= (template != null) && (groupId != PortalUtil.getScopeGroupId(request, refererPortletName)) %>">
							<aui:field-wrapper>
								<div class="alert alert-warning">
									<liferay-ui:message key="this-template-does-not-belong-to-this-site.-you-may-affect-other-sites-if-you-edit-this-template" />
								</div>
							</aui:field-wrapper>
						</c:if>

						<aui:input name="name" />

						<liferay-ui:panel-container
							cssClass="lfr-structure-entry-details-container"
							extended="<%= false %>"
							id="templateDetailsPanelContainer"
							persistState="<%= true %>"
						>
							<liferay-ui:panel
								collapsible="<%= true %>"
								defaultState="closed"
								extended="<%= false %>"
								id="templateDetailsSectionPanel"
								markupView="lexicon"
								persistState="<%= true %>"
								title="details"
							>
								<c:if test="<%= type.equals(DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY) %>">
									<aui:select changesContext="<%= true %>" helpMessage='<%= (template == null) ? StringPool.BLANK : "changing-the-language-does-not-automatically-translate-the-existing-template-script" %>' label="language" name="language">

										<%
										for (String languageType : ddmDisplay.getTemplateLanguageTypes()) {
											StringBundler sb = new StringBundler(6);

											sb.append(LanguageUtil.get(request, languageType + "[stands-for]"));
											sb.append(StringPool.SPACE);
											sb.append(StringPool.OPEN_PARENTHESIS);
											sb.append(StringPool.PERIOD);
											sb.append(languageType);
											sb.append(StringPool.CLOSE_PARENTHESIS);
										%>

											<aui:option label="<%= sb.toString() %>" selected="<%= language.equals(languageType) %>" value="<%= languageType %>" />

										<%
										}
										%>

									</aui:select>
								</c:if>

								<c:if test="<%= !ddmDisplayContext.autogenerateTemplateKey() %>">
									<aui:input disabled="<%= (template != null) ? true : false %>" name="templateKey" />
								</c:if>

								<aui:input name="description" />

								<c:if test="<%= template != null %>">
									<aui:input helpMessage="template-key-help" name="templateKey" type="resource" value="<%= template.getTemplateKey() %>" />

									<portlet:resourceURL id="/dynamic_data_mapping/get_template" var="getTemplateURL">
										<portlet:param name="templateId" value="<%= String.valueOf(templateId) %>" />
									</portlet:resourceURL>

									<aui:input name="url" type="resource" value="<%= getTemplateURL.toString() %>" />

									<c:if test="<%= Validator.isNotNull(refererWebDAVToken) %>">
										<aui:input name="webDavURL" type="resource" value="<%= template.getWebDavURL(themeDisplay, refererWebDAVToken) %>" />
									</c:if>
								</c:if>

								<c:choose>
									<c:when test="<%= type.equals(DDMTemplateConstants.TEMPLATE_TYPE_FORM) %>">
										<aui:select helpMessage="only-allow-deleting-required-fields-in-edit-mode" label="mode" name="mode">
											<aui:option label="create" />
											<aui:option label="edit" />
										</aui:select>
									</c:when>
									<c:otherwise>
										<c:if test='<%= ParamUtil.getBoolean(request, "showCacheableInput") %>'>
											<aui:input helpMessage="journal-template-cacheable-help" name="cacheable" value="<%= cacheable %>" />
										</c:if>

										<div id="<portlet:namespace />smallImageContainer">
											<div class="lfr-ddm-small-image-header">
												<aui:input name="smallImage" />
											</div>

											<div class="lfr-ddm-small-image-content toggler-content-collapsed">
												<clay:row>
													<c:if test="<%= smallImage && (template != null) %>">
														<clay:col
															md="6"
														>
															<img alt="<liferay-ui:message escapeAttribute="<%= true %>" key="preview" />" class="lfr-ddm-small-image-preview" src="<%= HtmlUtil.escapeAttribute(template.getTemplateImageURL(themeDisplay)) %>" />
														</clay:col>
													</c:if>

													<clay:col
														md="<%= (smallImage && (template != null)) ? String.valueOf(6) : String.valueOf(12) %>"
													>
														<aui:fieldset>
															<aui:input cssClass="lfr-ddm-small-image-type" inlineField="<%= true %>" label="small-image-url" name="type" type="radio" />

															<aui:input cssClass="lfr-ddm-small-image-value" inlineField="<%= true %>" label="" name="smallImageURL" title="small-image-url" />
														</aui:fieldset>

														<aui:fieldset>
															<aui:input cssClass="lfr-ddm-small-image-type" inlineField="<%= true %>" label="small-image" name="type" type="radio" />

															<aui:input cssClass="lfr-ddm-small-image-value" inlineField="<%= true %>" label="" name="smallImageFile" type="file" />
														</aui:fieldset>
													</clay:col>
												</clay:row>
											</div>
										</div>
									</c:otherwise>
								</c:choose>
							</liferay-ui:panel>
						</liferay-ui:panel-container>

						<c:choose>
							<c:when test="<%= type.equals(DDMTemplateConstants.TEMPLATE_TYPE_FORM) %>">
								<%@ include file="/edit_template_form.jspf" %>
							</c:when>
							<c:otherwise>
								<%@ include file="/edit_template_display.jspf" %>
							</c:otherwise>
						</c:choose>
					</aui:fieldset>
				</div>
			</div>
		</div>
	</aui:form>

	<c:if test="<%= type.equals(DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY) %>">
		<aui:script use="aui-toggler">
			var container = A.one('#<portlet:namespace />smallImageContainer');

			var types = container.all('.lfr-ddm-small-image-type');
			var values = container.all('.lfr-ddm-small-image-value');

			var selectSmallImageType = function (index) {
				types.attr('checked', false);

				types.item(index).attr('checked', true);

				values.attr('disabled', true);

				values.item(index).attr('disabled', false);
			};

			container.delegate(
				'change',
				(event) => {
					var index = types.indexOf(event.currentTarget);

					selectSmallImageType(index);
				},
				'.lfr-ddm-small-image-type'
			);

			new A.Toggler({
				animated: true,
				content:
					'#<portlet:namespace />smallImageContainer .lfr-ddm-small-image-content',
				expanded: <%= smallImage %>,
				header: '#<portlet:namespace />smallImageContainer .lfr-ddm-small-image-header',
				on: {
					animatingChange: function (event) {
						var instance = this;

						var expanded = !instance.get('expanded');

						A.one('#<portlet:namespace />smallImage').attr('checked', expanded);

						if (expanded) {
							types.each((item, index) => {
								if (item.get('checked')) {
									values.item(index).attr('disabled', false);
								}
							});
						}
						else {
							values.attr('disabled', true);
						}
					},
				},
			});

			selectSmallImageType(
				'<%= ((template != null) && Validator.isNotNull(template.getSmallImageURL())) ? 0 : 1 %>'
			);
		</aui:script>
	</c:if>

	<aui:button-row>
		<aui:script>
			Liferay.after('<portlet:namespace />saveTemplate', () => {
				submitForm(document.<portlet:namespace />fm);
			});

			function <portlet:namespace />saveDraftTemplate() {
				var form = document.<portlet:namespace />fm;

				var statusInput = Liferay.Util.getFormElement(form, 'status');

				if (statusInput) {
					Liferay.Util.setFormValues(form, {
						statusInput: <%= String.valueOf(WorkflowConstants.STATUS_DRAFT) %>,
					});
				}

				Liferay.fire(
					'<%= liferayPortletResponse.getNamespace() + "saveTemplate" %>'
				);
			}

			function <portlet:namespace />saveAndContinueTemplate() {
				document.<portlet:namespace />fm.<portlet:namespace />saveAndContinue.value =
					'1';

				Liferay.fire('<portlet:namespace />saveTemplate');
			}

			var onDestroyPortlet = function (event) {
				Liferay.detach('destroyPortlet', onDestroyPortlet);
				Liferay.detach('<portlet:namespace />saveTemplate');
			};

			Liferay.on('destroyPortlet', onDestroyPortlet);
		</aui:script>

		<%
		String taglibOnClick = "Liferay.fire('" + liferayPortletResponse.getNamespace() + "saveTemplate');";
		%>

		<aui:button onClick="<%= taglibOnClick %>" primary="<%= true %>" value='<%= LanguageUtil.get(request, "save") %>' />

		<aui:button onClick='<%= liferayPortletResponse.getNamespace() + "saveAndContinueTemplate();" %>' value='<%= LanguageUtil.get(resourceBundle, "save-and-continue") %>' />

		<c:if test="<%= ddmDisplay.isVersioningEnabled() %>">
			<aui:button onClick='<%= liferayPortletResponse.getNamespace() + "saveDraftTemplate();" %>' value='<%= LanguageUtil.get(request, "save-draft") %>' />
		</c:if>

		<aui:button href="<%= ddmDisplay.getEditTemplateBackURL(liferayPortletRequest, liferayPortletResponse, classNameId, classPK, resourceClassNameId, portletResource) %>" type="cancel" />
	</aui:button-row>
</clay:container-fluid>