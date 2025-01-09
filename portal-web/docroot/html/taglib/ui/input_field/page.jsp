<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/taglib/init.jsp" %>

<%
List<String> activeLanguageIds = (List<String>)request.getAttribute("liferay-ui:input-field:activeLanguageIds");
boolean adminMode = GetterUtil.getBoolean(String.valueOf(request.getAttribute("liferay-ui:input-field:adminMode")));
String autoComplete = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-field:autoComplete"));
boolean autoFocus = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:input-field:autoFocus"));
boolean autoSize = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:input-field:autoSize"));
Object bean = request.getAttribute("liferay-ui:input-field:bean");
String cssClass = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-field:cssClass"));
String dateTogglerCheckboxLabel = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-field:dateTogglerCheckboxLabel"));
String defaultLanguageId = (String)request.getAttribute("liferay-ui:input-field:defaultLanguageId");
Object defaultValue = request.getAttribute("liferay-ui:input-field:defaultValue");
boolean disabled = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:input-field:disabled"));
Map<String, Object> dynamicAttributes = (Map<String, Object>)request.getAttribute("liferay-ui:input-field:dynamicAttributes");
String field = (String)request.getAttribute("liferay-ui:input-field:field");
String fieldParam = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-field:fieldParam"));
Format format = (Format)request.getAttribute("liferay-ui:input-field:format");
String formName = (String)request.getAttribute("liferay-ui:input-field:formName");
String id = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-field:id"));
boolean ignoreRequestValue = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:input-field:ignoreRequestValue"));
String languageId = (String)request.getAttribute("liferay-ui:input-field:languageId");
String languagesDropdownDirection = (String)request.getAttribute("liferay-ui:input-field:languagesDropdownDirection");
String model = (String)request.getAttribute("liferay-ui:input-field:model");
String placeholder = (String)request.getAttribute("liferay-ui:input-field:placeholder");

String methodName = field;
String type = ModelHintsUtil.getType(model, field);

Map<String, String> hints = ModelHintsUtil.getHints(model, field);

if (hints != null) {
	methodName = GetterUtil.getString(hints.get("method-name"), methodName);
	type = GetterUtil.getString(hints.get("type"), type);
}
%>

<liferay-util:buffer
	var="infoHTML"
>
	<svg class="lexicon-icon lexicon-icon-exclamation-full">
		<use xlink:href="<%= themeDisplay.getPathThemeSpritemap() %>#exclamation-full"></use>
	</svg>
</liferay-util:buffer>

<liferay-util:buffer
	var="successHTML"
>
	<svg class="lexicon-icon lexicon-icon-check">
		<use xlink:href="<%= themeDisplay.getPathThemeSpritemap() %>#check"></use>
	</svg>
</liferay-util:buffer>

<c:if test="<%= type != null %>">
	<c:choose>
		<c:when test='<%= type.equals("boolean") %>'>

			<%
			boolean defaultBoolean = GetterUtil.DEFAULT_BOOLEAN;

			if (defaultValue != null) {
				Boolean defaultValueBoolean = (Boolean)defaultValue;

				defaultBoolean = defaultValueBoolean.booleanValue();
			}
			else {
				if (hints != null) {
					defaultBoolean = GetterUtil.getBoolean(hints.get("default-value"));
				}
			}

			boolean value = BeanPropertiesUtil.getBooleanSilent(bean, methodName, defaultBoolean);

			if (!ignoreRequestValue && Validator.isNotNull(ParamUtil.getString(request, "checkboxNames"))) {
				value = ParamUtil.getBoolean(request, fieldParam, value);
			}
			%>

			<liferay-ui:input-checkbox
				autoComplete="<%= autoComplete %>"
				cssClass="<%= cssClass %>"
				defaultValue="<%= value %>"
				disabled="<%= disabled %>"
				formName="<%= formName %>"
				id="<%= namespace + id %>"
				param="<%= fieldParam %>"
			/>
		</c:when>
		<c:when test='<%= type.equals("Date") %>'>

			<%
			boolean checkDefaultDelta = false;

			Calendar cal = null;

			if (defaultValue != null) {
				cal = (Calendar)defaultValue;
			}
			else {
				cal = CalendarFactoryUtil.getCalendar(timeZone, locale);

				Date date = (Date)BeanPropertiesUtil.getObject(bean, methodName);

				if (date == null) {
					checkDefaultDelta = true;

					date = new Date();
				}

				cal.setTime(date);
			}

			boolean updateFromDefaultDelta = false;

			int month = -1;

			if (!ignoreRequestValue) {
				month = ParamUtil.getInteger(request, fieldParam + "Month", month);
			}

			if ((month == -1) && (cal != null)) {
				month = cal.get(Calendar.MONTH);

				if (checkDefaultDelta && (hints != null)) {
					int defaultMonthDelta = GetterUtil.getInteger(hints.get("default-month-delta"));

					cal.add(Calendar.MONTH, defaultMonthDelta);

					updateFromDefaultDelta = true;
				}
			}

			int day = -1;

			if (!ignoreRequestValue) {
				day = ParamUtil.getInteger(request, fieldParam + "Day", day);
			}

			if ((day == -1) && (cal != null)) {
				day = cal.get(Calendar.DATE);

				if (checkDefaultDelta && (hints != null)) {
					int defaultDayDelta = GetterUtil.getInteger(hints.get("default-day-delta"));

					cal.add(Calendar.DATE, defaultDayDelta);

					updateFromDefaultDelta = true;
				}
			}

			int year = -1;

			if (!ignoreRequestValue) {
				year = ParamUtil.getInteger(request, fieldParam + "Year", year);
			}

			if ((year == -1) && (cal != null)) {
				year = cal.get(Calendar.YEAR);

				if (checkDefaultDelta && (hints != null)) {
					int defaultYearDelta = GetterUtil.getInteger(hints.get("default-year-delta"));

					cal.add(Calendar.YEAR, defaultYearDelta);

					updateFromDefaultDelta = true;
				}
			}

			if (updateFromDefaultDelta) {
				month = cal.get(Calendar.MONTH);
				day = cal.get(Calendar.DATE);
				year = cal.get(Calendar.YEAR);
			}

			int firstDayOfWeek = Calendar.SUNDAY - 1;

			if (cal != null) {
				firstDayOfWeek = cal.getFirstDayOfWeek() - 1;
			}

			int hour = -1;

			if (!ignoreRequestValue) {
				hour = ParamUtil.getInteger(request, fieldParam + "Hour", hour);
			}

			if ((hour == -1) && (cal != null)) {
				hour = cal.get(Calendar.HOUR_OF_DAY);

				if (DateUtil.isFormatAmPm(locale)) {
					hour = cal.get(Calendar.HOUR);
				}
			}

			int minute = -1;

			if (!ignoreRequestValue) {
				minute = ParamUtil.getInteger(request, fieldParam + "Minute", minute);
			}

			if ((minute == -1) && (cal != null)) {
				minute = cal.get(Calendar.MINUTE);
			}

			int amPm = -1;

			if (!ignoreRequestValue) {
				amPm = ParamUtil.getInteger(request, fieldParam + "AmPm", amPm);
			}

			if ((amPm == -1) && (cal != null)) {
				amPm = Calendar.AM;

				if (DateUtil.isFormatAmPm(locale)) {
					amPm = cal.get(Calendar.AM_PM);
				}
			}

			boolean showTime = true;

			if (hints != null) {
				showTime = GetterUtil.getBoolean(hints.get("show-time"), showTime);
			}
			%>

			<div class="form-group-autofit">
				<div class="form-group-item">
					<liferay-ui:input-date
						autoComplete="<%= autoComplete %>"
						autoFocus="<%= autoFocus %>"
						cssClass="<%= cssClass %>"
						dayParam='<%= fieldParam + "Day" %>'
						dayValue="<%= day %>"
						disabled="<%= disabled %>"
						firstDayOfWeek="<%= firstDayOfWeek %>"
						formName="<%= formName %>"
						monthParam='<%= fieldParam + "Month" %>'
						monthValue="<%= month %>"
						name="<%= fieldParam %>"
						yearParam='<%= fieldParam + "Year" %>'
						yearValue="<%= year %>"
					/>
				</div>

				<c:if test="<%= showTime %>">
					<div class="form-group-item">
						<liferay-ui:input-time
							amPmParam='<%= fieldParam + "AmPm" %>'
							amPmValue="<%= amPm %>"
							autoComplete="<%= autoComplete %>"
							cssClass="<%= cssClass %>"
							disabled="<%= disabled %>"
							hourParam='<%= fieldParam + "Hour" %>'
							hourValue="<%= hour %>"
							minuteParam='<%= fieldParam + "Minute" %>'
							minuteValue="<%= minute %>"
							name='<%= fieldParam + "Time" %>'
							timeFormat='<%= GetterUtil.getString((String)dynamicAttributes.get("timeFormat")) %>'
						/>
					</div>
				</c:if>
			</div>

			<c:if test="<%= Validator.isNotNull(dateTogglerCheckboxLabel) %>">
				<div class="clearfix">
					<aui:input id="<%= formName + fieldParam %>" label="<%= dateTogglerCheckboxLabel %>" name="<%= TextFormatter.format(dateTogglerCheckboxLabel, TextFormatter.M) %>" type="checkbox" value="<%= disabled %>" />
				</div>

				<aui:script use="event-base">
					var checkbox = A.one('#<portlet:namespace /><%= formName + fieldParam %>');

					if (checkbox) {
						checkbox.once(
							'click',
							function () {
								Liferay.component('<portlet:namespace /><%= fieldParam %>DatePicker');
							}
						);

						var form = document.<portlet:namespace /><%= formName %>;

						checkbox.on(
							'click',
							function(event) {
								var checked = checkbox.get('checked');

								var elements = [
									Liferay.Util.getFormElement(form, '<%= fieldParam %>'),
									Liferay.Util.getFormElement(form, '<%= fieldParam %>Day'),
									Liferay.Util.getFormElement(form, '<%= fieldParam %>Month'),
									Liferay.Util.getFormElement(form, '<%= fieldParam %>Year'),
									Liferay.Util.getFormElement(form, '<%= fieldParam %>Time'),
									Liferay.Util.getFormElement(form, '<%= fieldParam %>Hour'),
									Liferay.Util.getFormElement(form, '<%= fieldParam %>Minute'),
									Liferay.Util.getFormElement(form, '<%= fieldParam %>AmPm')
								].filter(Boolean);

								elements.forEach(
									function(element) {
										if (checked) {
											element.setAttribute('disabled', '');
										}
										else {
											element.removeAttribute('disabled');
										}

										A.one(element).toggleClass('disabled', checked);
									}
								);

								var label = A.one('label[for="<portlet:namespace /><%= fieldParam %>"]');

								if (label) {
									label.toggleClass('disabled', checked);
								}
							}
						);
					}
				</aui:script>
			</c:if>
		</c:when>
		<c:when test='<%= type.equals("double") || type.equals("int") || type.equals("long") || type.equals("String") %>'>

			<%
			String defaultString = GetterUtil.DEFAULT_STRING;

			if (defaultValue != null) {
				defaultString = (String)defaultValue;
			}

			String value = null;

			if (type.equals("double")) {
				double doubleValue = BeanPropertiesUtil.getDoubleSilent(bean, methodName, GetterUtil.getDouble(defaultString));

				if (!ignoreRequestValue) {
					doubleValue = ParamUtil.getDouble(request, fieldParam, doubleValue, locale);
				}

				if (format != null) {
					value = format.format(doubleValue);
				}
				else {
					value = String.valueOf(doubleValue);
				}
			}
			else if (type.equals("int")) {
				int intValue = BeanPropertiesUtil.getIntegerSilent(bean, methodName, GetterUtil.getInteger(defaultString));

				if (!ignoreRequestValue) {
					intValue = ParamUtil.getInteger(request, fieldParam, intValue);
				}

				if (format != null) {
					value = format.format(intValue);
				}
				else {
					value = String.valueOf(intValue);
				}
			}
			else if (type.equals("long")) {
				long longValue = BeanPropertiesUtil.getLongSilent(bean, methodName, GetterUtil.getLong(defaultString));

				if (!ignoreRequestValue) {
					longValue = ParamUtil.getLong(request, fieldParam, longValue);
				}

				if (format != null) {
					value = format.format(longValue);
				}
				else {
					value = String.valueOf(longValue);
				}
			}
			else {
				value = BeanPropertiesUtil.getStringSilent(bean, methodName, defaultString);

				if (!ignoreRequestValue) {
					value = ParamUtil.getString(request, fieldParam, value);
				}
			}

			boolean autoEscape = true;

			if (hints != null) {
				autoEscape = GetterUtil.getBoolean(hints.get("auto-escape"), true);
			}

			boolean checkTab = false;
			String displayHeight = ModelHintsConstants.TEXT_DISPLAY_HEIGHT;
			boolean editor = false;
			String maxLength = ModelHintsConstants.TEXT_MAX_LENGTH;
			boolean secret = false;
			boolean upperCase = false;

			if (hints != null) {
				autoSize = GetterUtil.getBoolean(hints.get("autoSize"), autoSize);
				checkTab = GetterUtil.getBoolean(hints.get("check-tab"), checkTab);
				displayHeight = GetterUtil.getString(hints.get("display-height"), displayHeight);
				editor = GetterUtil.getBoolean(hints.get("editor"), editor);
				maxLength = GetterUtil.getString(hints.get("max-length"), maxLength);
				secret = GetterUtil.getBoolean(hints.get("secret"), secret);
				upperCase = GetterUtil.getBoolean(hints.get("upper-case"), upperCase);
			}

			if (autoSize) {
				displayHeight = "auto";
			}

			cssClass += " form-control";

			if (editor) {
				cssClass += " lfr-input-editor";
			}

			boolean localized = ModelHintsUtil.isLocalized(model, field);

			Set<Locale> availableLocales = null;

			String xml = StringPool.BLANK;

			if (localized) {
				if (ModelHintsUtil.hasField(model, "groupId")) {
					availableLocales = LanguageUtil.getAvailableLocales(BeanPropertiesUtil.getLongSilent(bean, "groupId", themeDisplay.getSiteGroupId()));
				}
				else {
					availableLocales = LanguageUtil.getAvailableLocales();
				}

				if (Validator.isNotNull(bean)) {
					xml = BeanPropertiesUtil.getString(bean, methodName);
				}
			}
			%>

			<c:choose>
				<c:when test="<%= editor %>">
					<c:choose>
						<c:when test="<%= localized %>">
							<liferay-ui:input-localized
								activeLanguageIds="<%= activeLanguageIds %>"
								adminMode="<%= adminMode %>"
								autoFocus="<%= autoFocus %>"
								availableLocales="<%= availableLocales %>"
								cssClass="<%= cssClass %>"
								defaultLanguageId="<%= defaultLanguageId %>"
								disabled="<%= disabled %>"
								formName="<%= formName %>"
								id="<%= id %>"
								ignoreRequestValue="<%= ignoreRequestValue %>"
								languageId="<%= languageId %>"
								languagesDropdownDirection="<%= languagesDropdownDirection %>"
								maxLength="<%= maxLength %>"
								name="<%= fieldParam %>"
								placeholder="<%= placeholder %>"
								style='<%= upperCase ? "text-transform: uppercase;" : "" %>'
								type="editor"
								xml="<%= xml %>"
							/>
						</c:when>
						<c:otherwise>
							<liferay-ui:input-editor
								contents="<%= value %>"
								contentsLanguageId="<%= languageId %>"
								cssClass="<%= cssClass %>"
								editorName="ckeditor"
								name="<%= fieldParam %>"
								placeholder="<%= placeholder %>"
								toolbarSet="simple"
							/>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:when test="<%= displayHeight.equals(ModelHintsConstants.TEXT_DISPLAY_HEIGHT) %>">

					<%
					if (Validator.isNotNull(value)) {
						int maxLengthInt = GetterUtil.getInteger(maxLength);

						if (value.length() > maxLengthInt) {
							value = value.substring(0, maxLengthInt);
						}
					}
					%>

					<c:choose>
						<c:when test="<%= localized %>">
							<liferay-ui:input-localized
								activeLanguageIds="<%= activeLanguageIds %>"
								adminMode="<%= adminMode %>"
								autoFocus="<%= autoFocus %>"
								availableLocales="<%= availableLocales %>"
								cssClass='<%= cssClass + " lfr-input-text" %>'
								defaultLanguageId="<%= defaultLanguageId %>"
								disabled="<%= disabled %>"
								formName="<%= formName %>"
								id="<%= id %>"
								ignoreRequestValue="<%= ignoreRequestValue %>"
								languageId="<%= languageId %>"
								languagesDropdownDirection="<%= languagesDropdownDirection %>"
								maxLength="<%= maxLength %>"
								name="<%= fieldParam %>"
								placeholder="<%= placeholder %>"
								style='<%= upperCase ? "text-transform: uppercase;" : "" %>'
								xml="<%= xml %>"
							/>
						</c:when>
						<c:otherwise>
							<c:if test="<%= upperCase %>">
								<aui:style type="text/css">
									#<%= namespace %><%= id %> {
										text-transform: uppercase;
									}
								</aui:style>
							</c:if>

							<input <%= Validator.isNotNull(autoComplete) ? "autocomplete=\"" + autoComplete + "\"" : StringPool.BLANK %> class="<%= cssClass %> lfr-input-text" <%= disabled ? "disabled=\"disabled\"" : StringPool.BLANK %> id="<%= namespace %><%= id %>" name="<%= namespace %><%= fieldParam %>" <%= Validator.isNotNull(placeholder) ? "placeholder=\"" + LanguageUtil.get(resourceBundle, placeholder) + "\"" : StringPool.BLANK %> maxLength="<%= maxLength %>" type="<%= secret ? "password" : "text" %>" value="<%= autoEscape ? HtmlUtil.escape(value) : value %>" />
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="<%= localized %>">
							<liferay-ui:input-localized
								activeLanguageIds="<%= activeLanguageIds %>"
								adminMode="<%= adminMode %>"
								autoFocus="<%= autoFocus %>"
								autoSize="<%= autoSize %>"
								availableLocales="<%= availableLocales %>"
								cssClass='<%= cssClass + " lfr-input-text" %>'
								defaultLanguageId="<%= defaultLanguageId %>"
								disabled="<%= disabled %>"
								formName="<%= formName %>"
								id="<%= id %>"
								ignoreRequestValue="<%= ignoreRequestValue %>"
								languageId="<%= languageId %>"
								languagesDropdownDirection="<%= languagesDropdownDirection %>"
								maxLength="<%= maxLength %>"
								name="<%= fieldParam %>"
								onKeyDown="Liferay.Util.disableEsc();"
								placeholder="<%= placeholder %>"
								style='<%= !autoSize ? "height: " + displayHeight + (Validator.isDigit(displayHeight) ? "px" : StringPool.BLANK) + ";" : StringPool.BLANK %>'
								type="textarea"
								wrap="soft"
								xml="<%= xml %>"
							/>
						</c:when>
						<c:otherwise>
							<c:if test="<%= !autoSize %>">
								<aui:style type="text/css">
									#<%= namespace %><%= id %> {
										height: <%= displayHeight + (Validator.isDigit(displayHeight) ? "px" : StringPool.BLANK) %>;
									}
								</aui:style>
							</c:if>

							<liferay-ui:csp>
								<textarea maxLength="<%= maxLength %>" aria-labelledby="<%= namespace + id %> <%= namespace + id %>_maxCharacters" class="<%= cssClass %> lfr-textarea" <%= disabled ? "disabled=\"disabled\"" : StringPool.BLANK %> id="<%= namespace %><%= id %>" name="<%= namespace %><%= fieldParam %>" onKeyDown="Liferay.Util.disableEsc();" <%= Validator.isNotNull(placeholder) ? "placeholder=\"" + LanguageUtil.get(resourceBundle, placeholder) + "\"" : StringPool.BLANK %> wrap="soft"><%= autoEscape ? HtmlUtil.escape(value) : value %></textarea>
							</liferay-ui:csp>

							<span class="sr-only" id="<%= namespace + id %>_maxCharacters">
								<liferay-ui:message key="characters-maximum" />: <%= maxLength %>
							</span>
						</c:otherwise>
					</c:choose>

					<c:if test="<%= Validator.isNotNull(maxLength) %>">
						<div class="form-feedback-item">
							<span class="label-secondary" id="<%= namespace + id %>_counterWrapper">
								<span class="form-feedback-indicator"></span>
								<span aria-live="polite" class="form-feedback-message"></span>
								<span class="form-feedback-counter">
									0/<%= maxLength %>
								</span>
							</span>
						</div>
					</c:if>

					<c:if test="<%= autoSize && !localized %>">
						<aui:script use="aui-autosize">
							A.one('#<%= namespace %><%= id %>').plug(
								A.Plugin.Autosize,
								{
									<c:if test="<%= Validator.isDigit(displayHeight) %>">
										minHeight: <%= displayHeight %>
									</c:if>
								}
							);
						</aui:script>
					</c:if>
				</c:otherwise>
			</c:choose>

			<c:if test="<%= !localized %>">
				<c:if test="<%= autoFocus %>">
					<aui:script>
						Liferay.Util.focusFormField('#<%= namespace %><%= id %>');
					</aui:script>
				</c:if>
			</c:if>
		</c:when>
	</c:choose>
</c:if>

<aui:script sandbox="<%= true %>">
	var state = null;
	var textarea = document.querySelector('textarea#<portlet:namespace /><%= id %>')
	var counterWrapper = document.querySelector('#<portlet:namespace /><%= id %>_counterWrapper')

	var setCounter = (textarea) => {
		var counter = counterWrapper.querySelector('.form-feedback-counter')

		counter.innerHTML = textarea.value.length + "/" + maxLength;
	}

	if (textarea) {
		var maxLength = parseInt(textarea.getAttribute('maxLength'));

		setCounter(textarea);
	}

	var onKeydownHandler = Liferay.Util.delegate(
		document.body,
		'keyup',
		'textarea[id=<portlet:namespace /><%= id %>]',
		({target}) => {
			var indicator = counterWrapper.querySelector('.form-feedback-indicator');
			var message = counterWrapper.querySelector('.form-feedback-message');

			var feedback = {
				success: {
					message: "<%= LanguageUtil.get(resourceBundle, "the-characters-are-under-the-limit") %>",
					indicator: "<%= UnicodeFormatter.toString(successHTML) %>"
				},
				info: {
					message: "<%= LanguageUtil.get(resourceBundle, "the-character-limit-has-been-reached-you-cannot-continue-typing") %>",
					indicator: "<%= UnicodeFormatter.toString(infoHTML) %>"
				},
			}

			var setStatus = (nextState) => {
				if (state !== nextState) {
					counterWrapper.removeAttribute('class')
					counterWrapper.classList.add('label-' + nextState);

					indicator.innerHTML = feedback[nextState].indicator;
					message.innerHTML = feedback[nextState].message;

					state = nextState;
				}
			}

			if (state !== 'info' && target.value.length === maxLength) {
				setStatus('info');
			}
			else if (state === 'info' && target.value.length < maxLength) {
				setStatus('success');
			}

			setCounter(target);
		}
	);

	const onDestroyPortlet = () => {
		onKeydownHandler.dispose();

		Liferay.detach('destroyPortlet', onDestroyPortlet);
	};

	Liferay.once('destroyPortlet', onDestroyPortlet);
</aui:script>