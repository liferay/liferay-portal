<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/display/init.jsp" %>

<%
long formInstanceId = ddmFormDisplayContext.getFormInstanceId();
boolean limitToOneSubmissionPerUser = DDMFormInstanceSubmissionLimitStatusUtil.isLimitToOneSubmissionPerUser(ddmFormDisplayContext.getFormInstance());
%>

<c:choose>
	<c:when test="<%= formInstanceId == 0 %>">
		<div class="alert alert-info">
			<liferay-ui:message key="select-an-existing-form-or-add-a-form-to-be-displayed-in-this-application" />
		</div>
	</c:when>
	<c:when test="<%= ddmFormDisplayContext.getFormInstance() == null %>">
		<div class="ddm-form-basic-info">
			<clay:container-fluid>
				<clay:alert
					displayType="warning"
					message="this-form-not-available-or-it-was-not-published"
				/>
			</clay:container-fluid>
		</div>
	</c:when>
	<c:when test="<%= !ddmFormDisplayContext.hasAddFormInstanceRecordPermission() && !ddmFormDisplayContext.hasViewPermission() %>">
		<div class="ddm-form-basic-info">
			<clay:container-fluid>
				<clay:alert
					displayType="warning"
					message="you-do-not-have-the-permission-to-view-this-form"
				/>
			</clay:container-fluid>
		</div>
	</c:when>
	<c:when test="<%= (!ddmFormDisplayContext.isLoggedUser() && limitToOneSubmissionPerUser) || ddmFormDisplayContext.isRequireAuthentication() %>">
		<div class="ddm-form-basic-info">
			<clay:container-fluid>
				<clay:alert
					displayType="warning"
					message="you-need-to-be-signed-in-to-view-this-form"
				/>
			</clay:container-fluid>
		</div>
	</c:when>
	<c:otherwise>
		<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/dynamic_data_mapping_form/get_form_report_data" var="formReportDataURL">
			<portlet:param name="formInstanceId" value="<%= String.valueOf(formInstanceId) %>" />
		</liferay-portlet:resourceURL>

		<%
		DDMFormInstance formInstance = ddmFormDisplayContext.getFormInstance();

		boolean expired = DDMFormInstanceExpirationStatusUtil.isFormExpired(formInstance, timeZone);

		boolean formAvailable = ddmFormDisplayContext.isFormAvailable();
		boolean formShared = ddmFormDisplayContext.isFormShared();
		boolean preview = ddmFormDisplayContext.isPreview();
		boolean showSuccessPage = ddmFormDisplayContext.isShowSuccessPage();

		String languageId = ddmFormDisplayContext.getDefaultLanguageId();

		Locale displayLocale = LocaleUtil.fromLanguageId(languageId);
		%>

		<c:choose>
			<c:when test="<%= !preview && (ddmFormDisplayContext.isSubmissionLimitReached() || expired || showSuccessPage) %>">

				<%
				String pageDescription = null;
				String pageTitle = null;

				if (expired) {
					pageDescription = LanguageUtil.get(request, "this-form-has-an-expiration-date");
					pageTitle = LanguageUtil.get(request, "this-form-is-no-longer-available");
				}
				else if (showSuccessPage) {
					pageDescription = ddmFormDisplayContext.getSuccessPageDescription(displayLocale);
					pageTitle = ddmFormDisplayContext.getSuccessPageTitle(displayLocale);
				}
				else {
					Map<String, String> limitToOneSubmissionPerUserMap = ddmFormDisplayContext.getLimitToOneSubmissionPerUserMap();

					pageDescription = limitToOneSubmissionPerUserMap.get("limitToOneSubmissionPerUserBody");
					pageTitle = limitToOneSubmissionPerUserMap.get("limitToOneSubmissionPerUserHeader");
				}
				%>

				<react:component
					module="{DefaultPage} from dynamic-data-mapping-form-web"
					props='<%=
						HashMapBuilder.<String, Object>put(
							"dataEngineModule", ddmFormDisplayContext.getDataEngineModule()
						).put(
							"displayChartAsTable", ddmFormDisplayContext.isDisplayChartAsTable()
						).put(
							"formDescription", formInstance.getDescription(displayLocale)
						).put(
							"formReportDataURL", formReportDataURL.toString()
						).put(
							"formTitle", formInstance.getName(displayLocale)
						).put(
							"pageDescription", pageDescription
						).put(
							"pageTitle", pageTitle
						).put(
							"showPartialResultsToRespondents", ddmFormDisplayContext.isShowPartialResultsToRespondents()
						).put(
							"showSubmitAgainButton", !expired && !limitToOneSubmissionPerUser
						).build()
					%>'
				/>
			</c:when>
			<c:when test="<%= formAvailable || preview %>">
				<portlet:actionURL name="/dynamic_data_mapping_form/add_form_instance_record" var="addFormInstanceRecordActionURL" />

				<div class="portlet-forms">
					<aui:form action="<%= addFormInstanceRecordActionURL %>" data-DDMFormInstanceId="<%= formInstanceId %>" data-senna-off="true" method="post" name="fm">
						<aui:input name="currentURL" type="hidden" value="<%= currentURL %>" />

						<%
						String redirectURL = ddmFormDisplayContext.getRedirectURL();
						%>

						<c:if test="<%= Validator.isNull(redirectURL) %>">
							<aui:input name="redirect" type="hidden" value='<%= ParamUtil.getString(request, "redirect", currentURL) %>' />
						</c:if>

						<aui:input name="groupId" type="hidden" value="<%= formInstance.getGroupId() %>" />
						<aui:input name="formInstanceId" type="hidden" value="<%= formInstance.getFormInstanceId() %>" />
						<aui:input name="languageId" type="hidden" value="<%= languageId %>" />
						<aui:input name="workflowAction" type="hidden" value="<%= WorkflowConstants.ACTION_PUBLISH %>" />

						<liferay-ui:error exception="<%= CaptchaException.class %>" message="captcha-verification-failed" />
						<liferay-ui:error exception="<%= CaptchaTextException.class %>" message="text-verification-failed" />
						<liferay-ui:error exception="<%= DDMFormRenderingException.class %>" message="unable-to-render-the-selected-form" />
						<liferay-ui:error exception="<%= DDMFormValuesValidationException.class %>" message="field-validation-failed" />

						<liferay-ui:error exception="<%= DDMFormValuesValidationException.MustSetValidValue.class %>">

							<%
							DDMFormValuesValidationException.MustSetValidValue msvv = (DDMFormValuesValidationException.MustSetValidValue)errorException;

							String fieldLabelValue = msvv.getFieldLabelValue(displayLocale);

							if (Validator.isNull(fieldLabelValue)) {
								fieldLabelValue = msvv.getFieldName();
							}
							%>

							<liferay-ui:message arguments="<%= HtmlUtil.escape(fieldLabelValue) %>" key="validation-failed-for-field-x" translateArguments="<%= false %>" />
						</liferay-ui:error>

						<liferay-ui:error exception="<%= DDMFormValuesValidationException.RequiredValue.class %>">

							<%
							DDMFormValuesValidationException.RequiredValue rv = (DDMFormValuesValidationException.RequiredValue)errorException;

							String fieldLabelValue = rv.getFieldLabelValue(displayLocale);

							if (Validator.isNull(fieldLabelValue)) {
								fieldLabelValue = rv.getFieldName();
							}
							%>

							<liferay-ui:message arguments="<%= HtmlUtil.escape(fieldLabelValue) %>" key="no-value-is-defined-for-field-x" translateArguments="<%= false %>" />
						</liferay-ui:error>

						<liferay-ui:error exception="<%= NoSuchFormInstanceException.class %>" message="the-selected-form-no-longer-exists" />
						<liferay-ui:error exception="<%= NoSuchStructureException.class %>" message="unable-to-retrieve-the-definition-of-the-selected-form" />
						<liferay-ui:error exception="<%= NoSuchStructureLayoutException.class %>" message="unable-to-retrieve-the-layout-of-the-selected-form" />

						<liferay-ui:error exception="<%= ObjectEntryCountException.class %>">

							<%
							ObjectEntryCountException oece = (ObjectEntryCountException)errorException;
							%>

							<liferay-ui:message arguments="<%= oece.getObjectDefinitionLabel() %>" key="the-limit-of-guest-entries-for-object-definition-has-been-reached-and-will-no-longer-be-accepted" translateArguments="<%= false %>" />
						</liferay-ui:error>

						<liferay-ui:error exception="<%= ObjectEntryValuesException.ExceedsIntegerSize.class %>" message="object-entry-value-exceeds-integer-field-allowed-size" />
						<liferay-ui:error exception="<%= ObjectEntryValuesException.ExceedsLongMaxSize.class %>" message="object-entry-value-exceeds-maximum-long-field-allowed-size" />
						<liferay-ui:error exception="<%= ObjectEntryValuesException.ExceedsLongMinSize.class %>" message="object-entry-value-falls-below-minimum-long-field-allowed-size" />
						<liferay-ui:error exception="<%= ObjectEntryValuesException.ExceedsLongSize.class %>" message="object-entry-value-exceeds-long-field-allowed-size" />

						<liferay-ui:error exception="<%= ObjectEntryValuesException.ExceedsTextMaxLength.class %>">

							<%
							ObjectEntryValuesException.ExceedsTextMaxLength etml = (ObjectEntryValuesException.ExceedsTextMaxLength)errorException;
							%>

							<liferay-ui:message arguments="<%= new String[] {String.valueOf(etml.getMaxLength()), etml.getObjectFieldName()} %>" key="the-entry-value-exceeds-the-maximum-length-of-x-characters-for-object-field-x" translateArguments="<%= false %>" />
						</liferay-ui:error>

						<liferay-ui:error exception="<%= ObjectValidationRuleEngineException.class %>">

							<%
							ObjectValidationRuleEngineException objectValidationRuleEngineException = (ObjectValidationRuleEngineException)errorException;
							%>

							<liferay-ui:message key="<%= objectValidationRuleEngineException.getMessage() %>" />
						</liferay-ui:error>

						<liferay-ui:error exception="<%= StorageException.class %>" message="there-was-an-error-when-accessing-the-data-storage" />

						<liferay-ui:error-principal />

						<c:if test="<%= formShared || preview %>">
							<clay:container-fluid>
								<div class="locale-actions">
									<c:choose>
										<c:when test="<%= ddmFormDisplayContext.isPropagateLanguageSelection() %>">
											<liferay-site-navigation:language
												languageId="<%= languageId %>"
												languageIds="<%= ddmFormDisplayContext.getAvailableLanguageIds() %>"
												useNamespace="<%= false %>"
											/>
										</c:when>
										<c:otherwise>
											<liferay-site-navigation:language
												formAction="<%= currentURL %>"
												languageId="<%= languageId %>"
												languageIds="<%= ddmFormDisplayContext.getAvailableLanguageIds() %>"
											/>
										</c:otherwise>
									</c:choose>
								</div>
							</clay:container-fluid>
						</c:if>

						<c:if test="<%= !ddmFormDisplayContext.hasAddFormInstanceRecordPermission() %>">
							<div class="ddm-form-basic-info">
								<clay:container-fluid>
									<clay:alert
										displayType="warning"
										message="you-do-not-have-the-permission-to-submit-this-form"
									/>
								</clay:container-fluid>
							</div>
						</c:if>

						<c:if test="<%= !ddmFormDisplayContext.hasValidStorageType(formInstance) %>">
							<div class="ddm-form-basic-info">
								<clay:container-fluid>
									<clay:alert
										displayType="danger"
										message='<%= LanguageUtil.format(request, "this-form-was-created-using-a-storage-type-x-that-is-not-available-for-this-liferay-dxp-installation.-install-x-to-make-it-available-for-editing", formInstance.getStorageType()) %>'
									/>
								</clay:container-fluid>
							</div>
						</c:if>

						<div class="ddm-form-upload-permission-message hide mt-4">
							<clay:container-fluid>
								<clay:alert
									displayType="warning"
									message="you-do-not-have-the-permission-to-access-the-upload-fields-on-this-form"
								/>
							</clay:container-fluid>
						</div>

						<clay:container-fluid>
							<react:component
								module="{ShowPartialResultsAlert} from dynamic-data-mapping-form-web"
								props='<%=
									HashMapBuilder.<String, Object>put(
										"dismissible", true
									).put(
										"showPartialResultsToRespondents", ddmFormDisplayContext.isShowPartialResultsToRespondents()
									).build()
								%>'
							/>
						</clay:container-fluid>

						<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/dynamic_data_mapping_form/get_form_report_data" var="formReportDataURL">
							<portlet:param name="formInstanceId" value="<%= String.valueOf(formInstanceId) %>" />
						</liferay-portlet:resourceURL>

						<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/dynamic_data_mapping_form/validate_csrf_token" var="validateCSRFTokenURL" />

						<clay:container-fluid
							cssClass="ddm-form-builder-app ddm-form-builder-app-not-ready"
							id="<%= ddmFormDisplayContext.getContainerId() %>"
						>
							<react:component
								module="{FormView} from dynamic-data-mapping-form-web"
								props='<%=
									HashMapBuilder.<String, Object>put(
										"dataEngineModule", ddmFormDisplayContext.getDataEngineModule()
									).put(
										"description", StringUtil.trim(formInstance.getDescription(displayLocale))
									).put(
										"displayChartAsTable", ddmFormDisplayContext.isDisplayChartAsTable()
									).put(
										"formReportDataURL", formReportDataURL.toString()
									).put(
										"title", formInstance.getName(displayLocale)
									).put(
										"validateCSRFTokenURL", validateCSRFTokenURL.toString()
									).putAll(
										ddmFormDisplayContext.getDDMFormContext()
									).build()
								%>'
							/>
						</clay:container-fluid>

						<aui:input name="empty" type="hidden" value="" />
					</aui:form>
				</div>

				<aui:script use="aui-base">
					var <portlet:namespace />form;

					function <portlet:namespace />clearInterval(intervalId) {
						if (intervalId) {
							clearInterval(intervalId);
						}
					}

					var <portlet:namespace />intervalId;

					function <portlet:namespace />clearPortletHandlers(event) {
						<portlet:namespace />clearInterval(<portlet:namespace />intervalId);

						Liferay.detach('destroyPortlet', <portlet:namespace />clearPortletHandlers);
					}

					Liferay.on('destroyPortlet', <portlet:namespace />clearPortletHandlers);

					<c:if test="<%= formShared %>">
						document.title =
							'<%= HtmlUtil.escapeJS(formInstance.getName(displayLocale)) %>';
					</c:if>

					function <portlet:namespace />fireFormView() {
						Liferay.fire('ddmFormView', {
							formId: '<%= formInstanceId %>',
							title: '<%= HtmlUtil.escapeJS(formInstance.getName(displayLocale)) %>',
						});
					}

					<c:choose>
						<c:when test="<%= ddmFormDisplayContext.isAutosaveEnabled() %>">
							<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/dynamic_data_mapping_form/add_form_instance_record" var="autoSaveFormInstanceRecordURL">
								<portlet:param name="autoSave" value="<%= Boolean.TRUE.toString() %>" />
								<portlet:param name="languageId" value="<%= languageId %>" />
								<portlet:param name="preview" value="<%= String.valueOf(preview) %>" />
							</liferay-portlet:resourceURL>

							Liferay.on('sessionExpired', (event) => {
								<portlet:namespace />clearInterval(<portlet:namespace />intervalId);
							});

							function <portlet:namespace />autoSave() {
								var form = <portlet:namespace />form;
								var isRendered = form.reactComponentRef && form.reactComponentRef.current;
								var data = new URLSearchParams({
									<portlet:namespace />formInstanceId: <%= formInstanceId %>,
									<portlet:namespace />serializedDDMFormValues: JSON.stringify(
										isRendered ? form.reactComponentRef.current.toJSON() : {}
									),
								});

								Liferay.Util.fetch('<%= autoSaveFormInstanceRecordURL.toString() %>', {
									body: data,
									method: 'POST',
								});
							}

							function <portlet:namespace />startAutoSave() {
								<portlet:namespace />clearInterval(<portlet:namespace />intervalId);

								window.<portlet:namespace />intervalId = setInterval(
									<portlet:namespace />autoSave,
									<%= ddmFormDisplayContext.getAutosaveInterval() %>
								);
							}
						</c:when>
						<c:otherwise>
							function <portlet:namespace />startAutoExtendSession() {
								<portlet:namespace />clearInterval(<portlet:namespace />intervalId);

								var tenSeconds = 10000;

								var time = Liferay.Session.sessionLength || tenSeconds;

								window.<portlet:namespace />intervalId = setInterval(
									<portlet:namespace />extendSession,
									time / 2
								);
							}

							function <portlet:namespace />extendSession() {
								Liferay.Session.extend();
							}
						</c:otherwise>
					</c:choose>

					function <portlet:namespace />enableForm() {
						var container = document.querySelector(
							'#<%= ddmFormDisplayContext.getContainerId() %>'
						);

						container.classList.remove('ddm-form-builder-app-not-ready');
					}

					function <portlet:namespace />initForm() {
						<portlet:namespace />enableForm();
						<portlet:namespace />fireFormView();

						<c:choose>
							<c:when test="<%= ddmFormDisplayContext.isAutosaveEnabled() %>">
								var container = document.querySelector(
									'#<%= ddmFormDisplayContext.getContainerId() %>'
								);

								container.onclick = function (event) {
									<portlet:namespace />startAutoSave();

									container.onclick = null;
									container.onkeypress = null;
								};

								container.onkeypress = function (event) {
									<portlet:namespace />startAutoSave();

									container.onclick = null;
									container.onkeypress = null;
								};
							</c:when>
							<c:otherwise>
								<portlet:namespace />startAutoExtendSession();
							</c:otherwise>
						</c:choose>
					}

					var rememberMe = <%= ddmFormDisplayContext.isRememberMe() %>;

					window.<portlet:namespace />sessionIntervalId = setInterval(() => {
						if (Liferay.Session || rememberMe) {
							clearInterval(<portlet:namespace />sessionIntervalId);

							<portlet:namespace />form = Liferay.component(
								'<%= ddmFormDisplayContext.getContainerId() %>'
							);

							if (<portlet:namespace />form) {
								<portlet:namespace />initForm();
							}
							else {
								Liferay.componentReady(
									'<%= ddmFormDisplayContext.getContainerId() %>'
								).then((component) => {
									<portlet:namespace />form = component;

									if (component) {
										<portlet:namespace />initForm();
									}
								});
							}
						}
					}, 1000);
				</aui:script>
			</c:when>
			<c:otherwise>
				<div class="alert alert-warning">
					<liferay-ui:message key="this-form-not-available-or-it-was-not-published" />
				</div>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>

<c:if test="<%= ddmFormDisplayContext.isShowConfigurationIcon() %>">
	<div class="c-mt-3 icons-container">
		<div class="btn-group lfr-icon-actions">
			<liferay-ui:icon
				icon="cog"
				label="<%= true %>"
				markupView="lexicon"
				message="select-form"
				method="get"
				onClick="<%= portletDisplay.getURLConfigurationJS() %>"
				url="<%= portletDisplay.getURLConfiguration() %>"
			/>
		</div>
	</div>
</c:if>