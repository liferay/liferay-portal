<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditDisplayContext editDisplayContext = (EditDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

portletDisplay.setShowBackIcon(Validator.isNotNull(editDisplayContext.getBackURL()));
portletDisplay.setURLBack(editDisplayContext.getBackURL());

renderResponse.setTitle(editDisplayContext.getPageTitle());
%>

<portlet:actionURL name="editPLOEntry" var="editPLOEntryURL" />

<clay:container-fluid>
	<liferay-frontend:edit-form
		action="<%= editPLOEntryURL %>"
		method="POST"
		name="editPLOEntryFm"
		onSubmit='<%= Validator.isNull(editDisplayContext.getKey()) ? "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "validateForm(event); " : StringPool.BLANK %>'
	>
		<aui:input name="redirect" type="hidden" value="<%= editDisplayContext.getBackURL() %>" />

		<liferay-frontend:edit-form-body>
			<clay:sheet-header>
				<h2 class="sheet-title"><liferay-ui:message key="language-key-information" /></h2>
			</clay:sheet-header>

			<clay:sheet-section>
				<clay:content-row
					containerElement="h3"
					cssClass="sheet-subtitle"
				>
					<clay:content-col
						expand="<%= true %>"
					>
						<span class="heading-text"><liferay-ui:message key="key" /></span>
					</clay:content-col>
				</clay:content-row>

				<clay:content-row
					containerElement="div"
					cssClass=""
				>
					<clay:content-col
						expand="<%= true %>"
					>
						<c:choose>
							<c:when test="<%= Validator.isNotNull(editDisplayContext.getKey()) %>">
								<aui:input name="key" type="hidden" value="<%= editDisplayContext.getKey() %>" />

								<span><%= HtmlUtil.escape(editDisplayContext.getKey()) %></span>
							</c:when>
							<c:otherwise>
								<div class="form-group">
									<aui:input maxlength='<%= ModelHintsUtil.getMaxLength(PLOEntry.class.getName(), "key") %>' name="key" pattern="[^ ]+" required="<%= true %>" value="<%= editDisplayContext.getKey() %>" wrapperCssClass="mb-0" />

									<div class="form-feedback-group">
										<div class="form-text">
											<liferay-ui:message key="a-language-key-cannot-contain-any-whitespace" />
										</div>
									</div>
								</div>
							</c:otherwise>
						</c:choose>
					</clay:content-col>
				</clay:content-row>
			</clay:sheet-section>

			<clay:sheet-section>
				<clay:content-row
					containerElement="h3"
					cssClass="sheet-subtitle"
				>
					<clay:content-col
						expand="<%= true %>"
					>
						<span class="heading-text"><liferay-ui:message key="translation-override" /></span>
					</clay:content-col>

					<c:if test="<%= Validator.isNotNull(editDisplayContext.getKey()) %>">
						<portlet:actionURL name="deletePLOEntries" var="deletePLOEntriesURL">
							<portlet:param name="redirect" value="<%= editDisplayContext.getBackURL() %>" />
							<portlet:param name="key" value="<%= editDisplayContext.getKey() %>" />
						</portlet:actionURL>

						<clay:content-col>
							<span class="heading-end">
								<liferay-ui:icon-delete
									confirmation="are-you-sure-you-want-to-reset-all-translation-overrides"
									label="<%= true %>"
									linkCssClass="btn btn-secondary btn-sm"
									message="clear-all-overrides"
									url="<%= deletePLOEntriesURL %>"
								/>
							</span>
						</clay:content-col>
					</c:if>
				</clay:content-row>

				<clay:content-row>
					<div class="sheet-text">
						<span class="font-weight-bold text-info">
							<clay:icon cssClass="mr-2" symbol="info-circle" /><liferay-ui:message key="please-add-at-least-one-translation-below" />
						</span>
					</div>
				</clay:content-row>

				<clay:content-row>
					<clay:content-col
						expand="<%= true %>"
					>

						<%
						LocalizedValuesMap valuesLocalizedValuesMap = editDisplayContext.getValuesLocalizedValuesMap();

						LocalizedValuesMap originalValuesLocalizedValuesMap = editDisplayContext.getOriginalValuesLocalizedValuesMap();

						for (Locale availableLocale : editDisplayContext.getAvailableLocales()) {
							String languageId = LanguageUtil.getLanguageId(availableLocale);
						%>

							<div class="form-group" dir="<%= LanguageUtil.get(request, "lang.dir") %>" lang="<%= LocaleUtil.toW3cLanguageId(availableLocale) %>">
								<aui:input dir='<%= LanguageUtil.get(availableLocale, "lang.dir") %>' label="<%= TextFormatter.format(languageId, TextFormatter.O) %>" name='<%= "value_" + availableLocale %>' value="<%= valuesLocalizedValuesMap.get(availableLocale) %>" wrappedField="<%= true %>" />

								<c:if test="<%= editDisplayContext.isShowOriginalValues() %>">
									<div class="form-feedback-group">
										<div class="form-text">

											<%
											String[] taglibMessageArguments = {"<span class=\"font-weight-bold\">", "</span>", HtmlUtil.escape(originalValuesLocalizedValuesMap.get(availableLocale))};
											%>

											<liferay-ui:message arguments="<%= taglibMessageArguments %>" key="x-original-value-x-x" />
										</div>
									</div>
								</c:if>
							</div>

						<%
						}
						%>

					</clay:content-col>
				</clay:content-row>
			</clay:sheet-section>
		</liferay-frontend:edit-form-body>

		<liferay-frontend:edit-form-footer>
			<liferay-frontend:edit-form-buttons
				redirect="<%= editDisplayContext.getBackURL() %>"
			/>
		</liferay-frontend:edit-form-footer>
	</liferay-frontend:edit-form>
</clay:container-fluid>

<c:if test="<%= Validator.isNull(editDisplayContext.getKey()) %>">
	<aui:script>
		function <portlet:namespace />validateForm(event) {
			const form = document.getElementById('<portlet:namespace />editPLOEntryFm');

			const inputs = form.querySelectorAll(
				"input[name^='<portlet:namespace />value']"
			);

			for (const input of inputs) {
				if (input.value) {
					submitForm(form);

					return;
				}
			}

			const keyField = form.querySelector(
				"input[name='<portlet:namespace />key']"
			);

			keyField.focus();

			Liferay.Util.openToast({
				message:
					'<%= HtmlUtil.escapeJS(LanguageUtil.get(request, "at-least-one-translation-is-required-with-a-new-language-key")) %>',
				type: 'danger',
			});
		}
	</aui:script>
</c:if>