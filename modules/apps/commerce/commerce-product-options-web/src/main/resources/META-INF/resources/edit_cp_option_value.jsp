<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPOptionValueDisplayContext cpOptionValueDisplayContext = (CPOptionValueDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPOptionValue cpOptionValue = (CPOptionValue)request.getAttribute(CPWebKeys.CP_OPTION_VALUE);

long cpOptionValueId = BeanParamUtil.getLong(cpOptionValue, request, "CPOptionValueId");

long cpOptionId = ParamUtil.getLong(request, "cpOptionId");

String defaultLanguageId = LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault());

Set<Locale> availableLocalesSet = new HashSet<>();

availableLocalesSet.add(LocaleUtil.fromLanguageId(defaultLanguageId));

if (cpOptionValue != null) {
	for (String languageId : cpOptionValue.getAvailableLanguageIds()) {
		availableLocalesSet.add(LocaleUtil.fromLanguageId(languageId));
	}
}
%>

<portlet:actionURL name="/cp_options/edit_cp_option_value" var="editProductOptionValueActionURL" />

<liferay-frontend:side-panel-content
	title='<%= LanguageUtil.format(request, "edit-x", cpOptionValue.getName(locale), false) %>'
>
	<aui:form action="<%= editProductOptionValueActionURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (cpOptionValue == null) ? Constants.ADD : Constants.UPDATE %>" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="cpOptionId" type="hidden" value="<%= cpOptionId %>" />
		<aui:input name="cpOptionValueId" type="hidden" value="<%= cpOptionValueId %>" />

		<liferay-ui:error exception="<%= CPOptionValueKeyException.class %>" message="price-type-cannot-be-changed-for-the-current-option-value-setup" />

		<commerce-ui:panel
			title='<%= LanguageUtil.get(request, "details") %>'
		>
			<liferay-ui:error-marker
				key="<%= WebKeys.ERROR_SECTION %>"
				value="product-option-value-details"
			/>

			<aui:model-context bean="<%= cpOptionValue %>" model="<%= CPOptionValue.class %>" />

			<liferay-ui:error exception="<%= CPOptionValueKeyException.class %>" focusField="key" message="that-key-is-already-being-used" />

			<aui:fieldset>
				<c:choose>
					<c:when test="<%= cpOptionValueDisplayContext.isCPOptionSelectDateType() %>">

						<%
						Calendar calendar = cpOptionValueDisplayContext.getCalendar();
						TimeZone cpOptionValueTimeZone = cpOptionValueDisplayContext.getTimeZone();
						String durationType = cpOptionValueDisplayContext.getDurationType();
						%>

						<div class="row">
							<div class="col-4">
								<div class="form-group input-date-wrapper">
									<label for="date"><liferay-ui:message key="date" /></label>

									<liferay-ui:input-date
										dayParam="day"
										dayValue="<%= calendar.get(Calendar.DAY_OF_MONTH) %>"
										disabled="<%= false %>"
										monthParam="month"
										monthValue="<%= calendar.get(Calendar.MONTH) %>"
										name="date"
										required="<%= true %>"
										yearParam="year"
										yearValue="<%= calendar.get(Calendar.YEAR) %>"
									/>
								</div>
							</div>

							<div class="col-4">
								<div class="form-group input-date-wrapper">
									<label for="time"><liferay-ui:message key="time" /></label>

									<liferay-ui:input-time
										amPmParam="amPm"
										amPmValue="<%= calendar.get(Calendar.AM_PM) %>"
										disabled="<%= false %>"
										hourParam="hour"
										hourValue="<%= calendar.get(Calendar.HOUR) %>"
										minuteParam="minute"
										minuteValue="<%= calendar.get(Calendar.MINUTE) %>"
										name="time"
									/>
								</div>
							</div>

							<div class="col-4">
								<div class="form-group input-date-wrapper">
									<label for="timeZone"><liferay-ui:message key="time-zone" /></label>

									<liferay-ui:input-time-zone
										name="timeZone"
										value="<%= cpOptionValueTimeZone.getID() %>"
									/>
								</div>
							</div>
						</div>

						<div class="row">
							<div class="col-6">
								<aui:input name="duration" required="<%= true %>" type="text" value="<%= cpOptionValueDisplayContext.getDuration() %>">
									<aui:validator name="min">1</aui:validator>
									<aui:validator name="digits" />
								</aui:input>
							</div>

							<div class="col-6">
								<aui:select label="duration-type" name="durationType">
									<aui:option label="hours" selected="<%= durationType.equals(CPConstants.HOURS_DURATION_TYPE) %>" value="<%= CPConstants.HOURS_DURATION_TYPE %>" />
									<aui:option label="days" selected="<%= durationType.equals(CPConstants.DAYS_DURATION_TYPE) %>" value="<%= CPConstants.DAYS_DURATION_TYPE %>" />
								</aui:select>
							</div>
						</div>

						<aui:input id="optionValueSelectDateLabel" name="label" readonly="<%= true %>" type="text" value="<%= cpOptionValue.getName(locale) %>" />

						<aui:input label="position" name="priority" />
					</c:when>
					<c:otherwise>
						<aui:input helpMessage="key-help" id="key" name="key" />

						<aui:input id="name" name="name" wrapperCssClass="commerce-product-option-value-title" />

						<aui:input label="position" name="priority" />
					</c:otherwise>
				</c:choose>
			</aui:fieldset>

			<c:if test="<%= CustomAttributesUtil.hasCustomAttributes(company.getCompanyId(), CPOptionValue.class.getName(), cpOptionValueId, null) %>">
				<aui:fieldset>
					<liferay-expando:custom-attribute-list
						className="<%= CPOptionValue.class.getName() %>"
						classPK="<%= (cpOptionValue != null) ? cpOptionValue.getCPOptionValueId() : 0 %>"
						editable="<%= true %>"
						label="<%= true %>"
					/>
				</aui:fieldset>
			</c:if>

			<liferay-frontend:component
				context='<%=
					HashMapBuilder.<String, Object>put(
						"bcp47LanguageId", LocaleUtil.toBCP47LanguageId(locale)
					).put(
						"isCPOptionSelectDate", cpOptionValueDisplayContext.isCPOptionSelectDateType()
					).build()
				%>'
				module="{editCpOptionAndValue} from commerce-product-options-web"
			/>
		</commerce-ui:panel>

		<aui:button-row>
			<aui:button cssClass="btn-lg" type="submit" value="save" />

			<aui:button cssClass="btn-lg" type="cancel" />
		</aui:button-row>
	</aui:form>
</liferay-frontend:side-panel-content>