<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ObjectEntryDisplayContext objectEntryDisplayContext = (ObjectEntryDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

String backURL = objectEntryDisplayContext.getBackURL();
ObjectDefinition objectDefinition = objectEntryDisplayContext.getObjectDefinition1();
ObjectEntry objectEntry = objectEntryDisplayContext.getObjectEntry();
String portletNamespace = portletDisplay.getNamespace();

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(backURL);
%>

<portlet:actionURL name="/object_entries/edit_object_entry" var="editObjectEntryURL" />

<liferay-frontend:edit-form
	action="<%= editObjectEntryURL %>"
	name="fm"
>
	<liferay-frontend:edit-form-body>
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (objectEntry == null) ? Constants.ADD : Constants.UPDATE %>" />
		<aui:input name="externalReferenceCode" type="hidden" value='<%= (objectEntry == null) ? "" : objectEntry.getExternalReferenceCode() %>' />
		<aui:input name="objectDefinitionId" type="hidden" value="<%= objectDefinition.getObjectDefinitionId() %>" />
		<aui:input name="ddmFormValues" type="hidden" value="" />

		<clay:sheet-section>
			<clay:row>
				<clay:col
					md="12"
				>
					<%= objectEntryDisplayContext.renderDDMForm(pageContext) %>
				</clay:col>
			</clay:row>

			<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-21926") && objectDefinition.isEnableFriendlyURLCustomization() && (objectEntryDisplayContext.getObjectLayoutTab() == null) %>'>
				<clay:panel-group>
					<clay:panel
						collapsable="<%= true %>"
						displayTitle='<%= LanguageUtil.get(request, "seo") %>'
						displayType="secondary"
						expanded="<%= true %>"
					>
						<div class="panel-body">
							<div class="ddm-row">
								<div class="ddm-field-container">
									<liferay-friendly-url:input
										className="<%= objectDefinition.getClassName() %>"
										classPK="<%= (objectEntry == null) ? 0 : objectEntry.getObjectEntryId() %>"
										helpMessage='<%= LanguageUtil.get(request, "the-friendly-url-is-automatically-generated-based-on-the-entry-title-field") %>'
										inputAddon="<%= objectEntryDisplayContext.getURLSeparator() %>"
										name="friendlyURL"
									/>
								</div>
							</div>
						</div>
					</clay:panel>
				</clay:panel-group>
			</c:if>

			<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-17564") && (objectEntryDisplayContext.getObjectLayoutTab() == null) %>'>
				<div>
					<react:component
						module="{ScheduleContainer} from object-web"
						props='<%=
							HashMapBuilder.<String, Object>put(
								"portletNamespace", portletNamespace
							).put(
								"scheduleProperties", objectEntryDisplayContext.getScheduleProperties()
							).put(
								"submitRef", portletNamespace + "submitObjectEntry"
							).build()
						%>'
					/>
				</div>
			</c:if>
		</clay:sheet-section>

		<%@ include file="/object_entries/object_entry/categorization.jspf" %>
	</liferay-frontend:edit-form-body>

	<c:if test="<%= !objectEntryDisplayContext.isReadOnly() %>">
		<c:choose>
			<c:when test='<%= FeatureFlagManagerUtil.isEnabled("LPD-17564") && (objectEntryDisplayContext.getObjectLayoutTab() == null) %>'>
				<div>
					<react:component
						module="{ObjectEntryFooter} from object-web"
						props='<%=
							HashMapBuilder.<String, Object>put(
								"backURL", backURL
							).put(
								"submitRef", portletNamespace + "submitObjectEntry"
							).build()
						%>'
					/>
				</div>
			</c:when>
			<c:otherwise>
				<liferay-frontend:edit-form-footer>
					<liferay-frontend:edit-form-buttons
						redirect="<%= backURL %>"
						submitOnClick='<%= "event.preventDefault(); " + portletNamespace + "submitObjectEntry();" %>'
					/>
				</liferay-frontend:edit-form-footer>
			</c:otherwise>
		</c:choose>
	</c:if>
</liferay-frontend:edit-form>

<c:if test="<%= !objectEntryDisplayContext.isReadOnly() %>">
	<aui:script sandbox="<%= true %>">
		const hasObjectLayout =
			<%= objectEntryDisplayContext.getObjectLayoutTab() != null %>;

		function <portlet:namespace />getExternalReferenceCode() {
			return String(
				'<%= (objectEntry == null) ? "" : objectEntry.getExternalReferenceCode() %>'
			);
		}

		function <portlet:namespace />getInputValues(element, selector) {
			return Array.from(element.querySelectorAll(selector)).map(
				(item) => item.value
			);
		}

		function <portlet:namespace />getPath(externalReferenceCode) {
			const scope = '<%= objectDefinition.getScope() %>';
			const contextPath = '/o<%= objectDefinition.getRESTContextPath() %>';
			const pathScopedBySite = contextPath.concat(
				`/scopes/\${themeDisplay.getSiteGroupId()}`
			);

			let path = scope === 'site' ? pathScopedBySite : contextPath;

			if (!externalReferenceCode) {
				return path;
			}

			return path.concat(
				'/by-external-reference-code/',
				`\${externalReferenceCode}`
			);
		}

		function <portlet:namespace />getValues(fields) {
			return fields.reduce((obj, field) => {
				if (field.readOnly) {
					return obj;
				}

				let value = field.value;
				if (
					field.type === 'select' &&
					!field.multiple &&
					!field.localizedObjectField
				) {
					value = {key: value.length ? field.value[0] : ''};
				}

				let fieldName = field.fieldName;

				if (value && field.localizable) {
					fieldName += '_i18n';

					if (typeof value == 'string') {
						value = JSON.parse(value);
					}
				}

				return Object.assign(obj, {[fieldName]: value});
			}, {});
		}

		function hasEmptyString(object) {
			return Object.values(object).some((value) => value === '');
		}

		function isPastDate(date) {
			if (!date) {
				return false;
			}

			const localDate = date.replace(/Z$/, '');

			const currentDateTime = new Date();

			const dateTime = new Date(localDate);

			return currentDateTime >= dateTime;
		}

		Liferay.provide(window, '<portlet:namespace />submitObjectEntry', () => {
			const form = document.getElementById('<portlet:namespace />fm');

			const DDMFormInstance = Liferay.component('editObjectEntry');

			const current = DDMFormInstance.reactComponentRef.current;

			const loadingElement = document.createElement('span');

			loadingElement.className =
				'loading-animation loading-animation-secondary loading-animation-sm';

			loadingElement.ariaHidden = 'true';

			form.insertAdjacentElement('afterbegin', loadingElement);

			current.validate().then((result) => {
				if (result) {
					const fields = current.getFields();
					let shouldSubmitForm = true;

					fields.forEach((field) => {
						if (
							field.displayStyle === 'singleline' &&
							field.type === 'text' &&
							field.value.length > 280
						) {
							shouldSubmitForm = false;

							loadingElement.remove();

							Liferay.Util.openToast({
								message: Liferay.Util.sub(
									'<liferay-ui:message key="the-entry-value-exceeds-the-maximum-length-of-x-characters-for-object-field-x" />',
									'280',
									'"' + field.fieldName + '"'
								),
								type: 'danger',
							});

							return false;
						}
					});

					let scheduleContainerInputValue;

					const scheduleContainerInput = document.getElementById(
						'<portlet:namespace />scheduleContainer'
					);

					if (Liferay.FeatureFlags['LPD-17564'] && scheduleContainerInput) {
						scheduleContainerInputValue = JSON.parse(
							scheduleContainerInput.value
						);

						if (
							hasEmptyString(scheduleContainerInputValue) ||
							isPastDate(scheduleContainerInputValue.expirationDate)
						) {
							shouldSubmitForm = false;

							loadingElement.remove();

							return false;
						}
					}

					if (shouldSubmitForm) {
						let values = <portlet:namespace />getValues(fields);
						const categoriesContent = document.getElementById(
							'<portlet:namespace />categorization'
						);
						const externalReferenceCode =
							<portlet:namespace />getExternalReferenceCode();
						const path = <portlet:namespace />getPath(
							externalReferenceCode
						);

						if (categoriesContent) {
							values = Object.assign(
								values,
								{
									['keywords']: <portlet:namespace />getInputValues(
										categoriesContent,
										'input[name^="<portlet:namespace />assetTagNames"]'
									),
								},
								{
									['taxonomyCategoryIds']:
										<portlet:namespace />getInputValues(
											categoriesContent,
											'input[name^="<portlet:namespace />assetCategoryIds"]'
										),
								}
							);
						}

						const autoRelatedValue = {
							['relationshipField']:
								'<%= objectEntryDisplayContext.getObjectRelationshipERCObjectFieldName() %>',
							['parentObjectEntryERC']:
								'<%= objectEntryDisplayContext.getParentObjectEntryId() %>',
						};

						if (autoRelatedValue['relationshipField'] !== 'null') {
							values = Object.assign(values, {
								[autoRelatedValue['relationshipField']]:
									autoRelatedValue['parentObjectEntryERC'],
							});
						}

						const friendlyURLInputs = document.querySelectorAll(
							'[data-field-name="friendlyURL"]'
						);

						if (friendlyURLInputs) {
							const friendlyURLValues = {};

							friendlyURLInputs.forEach((input) => {
								friendlyURLValues[input.dataset.languageid] =
									input.value;
							});

							values = Object.assign(values, {
								['friendlyUrlPath']: '',
								['friendlyUrlPath_i18n']: friendlyURLValues,
							});
						}

						if (
							Liferay.FeatureFlags['LPD-17564'] &&
							scheduleContainerInputValue
						) {
							values = {
								...values,
								...scheduleContainerInputValue,
							};
						}

						const method = !externalReferenceCode
							? 'POST'
							: hasObjectLayout
								? 'PATCH'
								: 'PUT';

						Liferay.Util.fetch(path, {
							body: JSON.stringify(values),
							headers: new Headers({
								'Accept': 'application/json',
								'Accept-Language':
									'<%= LanguageUtil.getBCP47LanguageId(request) %>',
								'Content-Type': 'application/json',
							}),
							method: method,
						})
							.then((response) => {
								Liferay.fire('submitButtonClicked');

								if (response.status === 401) {
									window.location.reload();
								}
								else if (response.ok) {
									Liferay.Util.openToast({
										message:
											'<%=
													HtmlUtil.escapeJS(LanguageUtil.get(
														LocaleUtil.fromLanguageId(LanguageUtil.getBCP47LanguageId(request)), "your-request-completed-successfully")) %>',
										type: 'success',
									});

									response.json().then((payload) => {
										const portletURL =
											Liferay.Util.PortletURL.createPortletURL(
												'<%= currentURLObj %>',
												{
													externalReferenceCode:
														payload.externalReferenceCode,
												}
											);

										Liferay.Util.navigate(portletURL.toString());
									});
								}
								else {
									return response.json();
								}
							})
							.then((response) => {
								if (response && response.detail) {
									const errorMessageArray = JSON.parse(
										response.detail
									);

									const alertClassName = '<portlet:namespace />alert';

									const alertElements =
										document.getElementsByClassName(alertClassName);

									for (let i = 0; i < alertElements.length; i++) {
										alertElements[i].remove();
									}

									for (const error of errorMessageArray) {
										const portletBody =
											document.querySelector('.portlet-body');

										const existingAlert =
											portletBody.querySelector('.alert');

										if (existingAlert) {
											existingAlert.remove();
										}

										const alertElement =
											document.createElement('div');

										alertElement.className =
											'alert alert-danger ' + alertClassName;
										alertElement.setAttribute('role', 'alert');
										alertElement.style.bottom = '20px';
										alertElement.style.margin = '2rem auto 0';
										alertElement.style.width = '800px';

										alertElement.insertAdjacentHTML(
											'afterbegin',
											"<span class='alert-indicator'><svg class='lexicon-icon lexicon-icon-exclamation-full' focusable='false' role='presentation'><use xlink:href='/o/admin-theme/images/clay/icons.svg#exclamation-full'/></svg> <strong class='lead'>Error:</strong></span>"
										);

										alertElement.insertAdjacentHTML(
											'beforeend',
											error.errorMessage
										);

										const closeButton =
											document.createElement('button');
										closeButton.classList.add('close');
										closeButton.setAttribute('aria-label', 'Close');
										closeButton.setAttribute('type', 'button');
										closeButton.style.fontSize = '32px';
										closeButton.style.fontWeight = '300';
										closeButton.innerHTML = '&times;';
										closeButton.onclick = () => {
											alertElement.remove();
										};

										alertElement.appendChild(closeButton);

										form.insertAdjacentElement(
											'afterbegin',
											alertElement
										);
									}
									scroll(0, 0);
								}
								else if (response && response.title) {
									Liferay.Util.openToast({
										message: response.title,
										type: 'danger',
									});
								}

								loadingElement.remove();
							});
					}
				}
				else {
					current.updateLocalesDropdownToDefaultLanguage();

					loadingElement.remove();
				}
			});
		});
	</aui:script>
</c:if>