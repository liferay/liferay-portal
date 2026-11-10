<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/taglib/aui/input/init.jsp" %>

<c:if test="<%= Validator.isNotNull(helpMessage) %>">
	<liferay-util:buffer
		var="helpMessageContent"
	>
		<liferay-ui:icon-help message="<%= helpMessage %>" />
	</liferay-util:buffer>
</c:if>

<liferay-util:buffer
	var="toggleSwitchLabelContent"
>
	<c:if test="<%= Validator.isNotNull(label) %>">
		<span class="toggle-switch-label">
			<liferay-ui:message key="<%= label %>" localizeKey="<%= localizeLabel %>" />
		</span>

		<c:if test="<%= required && showRequiredLabel %>">
			<span class="toggle-switch-text">
				<liferay-ui:message key="required" />

				<aui:icon cssClass="reference-mark text-warning" image="asterisk" markupView="lexicon" />
			</span>
		</c:if>
	</c:if>
</liferay-util:buffer>

<liferay-util:buffer
	var="labelContent"
>
	<c:if test="<%= Validator.isNotNull(label) %>">
		<c:if test='<%= !type.equals("toggle-switch") %>'>
			<liferay-ui:message key="<%= label %>" localizeKey="<%= localizeLabel %>" />

			<c:if test='<%= required && showRequiredLabel && !type.equals("radio") %>'>
				<aui:icon cssClass="reference-mark text-warning" image="asterisk" markupView="lexicon" />

				<span class="hide-accessible sr-only"><liferay-ui:message key="required" /></span>
			</c:if>

			<c:if test="<%= Validator.isNotNull(helpMessage) %>">
				<%= pageContext.getAttribute("helpMessageContent") %>
			</c:if>
		</c:if>

		<c:if test="<%= changesContext %>">
			<span class="hide-accessible sr-only">(<liferay-ui:message key="changing-the-value-of-this-field-reloads-the-page" />)</span>
		</c:if>
	</c:if>
</liferay-util:buffer>

<c:if test='<%= !type.equals("hidden") && !wrappedField && useInputWrapper %>'>
	<div class="<%= inputWrapperClass %>">
</c:if>

<%
boolean choiceField = checkboxField || radioField;
%>

<c:if test='<%= !type.equals("hidden") && Validator.isNotNull(labelContent) %>'>
	<label <%= labelTag %>>
		<c:if test='<%= !choiceField && !inlineLabel.equals("right") %>'>
				<%= labelContent %>
			</label>
		</c:if>
</c:if>

<c:if test="<%= Validator.isNotNull(prefix) || Validator.isNotNull(suffix) %>">
	<div class="<%= addOnCssClass %>">
		<c:if test="<%= Validator.isNotNull(prefix) %>">
			<div class="input-group-item input-group-item-shrink input-group-prepend">
				<span class="input-group-text <%= helpTextCssClass %>"><liferay-ui:message key="<%= prefix %>" /></span>
			</div>
		</c:if>

		<div class="input-group-item input-group-prepend">
</c:if>

<c:choose>
	<c:when test="<%= (model != null) && Validator.isNull(type) %>">
		<liferay-ui:input-field
			activeLanguageIds="<%= activeLanguageIds %>"
			adminMode="<%= adminMode %>"
			autoComplete='<%= GetterUtil.getString(dynamicAttributes.get("autocomplete")) %>'
			autoFocus="<%= autoFocus %>"
			bean="<%= bean %>"
			cssClass="<%= fieldCssClass %>"
			dateTogglerCheckboxLabel="<%= dateTogglerCheckboxLabel %>"
			defaultLanguageId="<%= defaultLanguageId %>"
			defaultValue="<%= value %>"
			disabled="<%= disabled %>"
			field="<%= field %>"
			fieldParam="<%= fieldParam %>"
			format='<%= (Format)dynamicAttributes.get("format") %>'
			formName="<%= formName %>"
			id="<%= id %>"
			ignoreRequestValue="<%= ignoreRequestValue %>"
			languageId="<%= languageId %>"
			languagesDropdownDirection="<%= languagesDropdownDirection %>"
			model="<%= model %>"
			placeholder="<%= placeholder %>"
			timeFormat='<%= GetterUtil.getString(dynamicAttributes.get("timeFormat")) %>'
		/>
	</c:when>
	<c:when test='<%= baseType.equals("checkbox") %>'>

		<%
		String valueString = null;

		if (value != null) {
			valueString = value.toString();

			if (Validator.isBoolean(valueString)) {
				checked = GetterUtil.getBoolean(valueString);

				valueString = null;
			}
		}

		if (!ignoreRequestValue && Validator.isNotNull(ParamUtil.getString(request, "checkboxNames"))) {
			if (Validator.isNotNull(valueString)) {
				String[] requestValues = ParamUtil.getParameterValues(request, name);

				checked = ArrayUtil.contains(requestValues, valueString);
			}
			else {
				checked = ParamUtil.getBoolean(request, name, checked);
			}
		}
		%>

		<c:if test='<%= type.equals("toggle-switch") %>'>
			<label <%= labelTag %>>
				<c:if test='<%= inlineLabel.equals("left") %>'>
					<%= toggleSwitchLabelContent %>
				</c:if>

				<span class="toggle-switch-check-bar">
		</c:if>

		<liferay-ui:csp>
			<input <%= checked ? "checked" : StringPool.BLANK %> class="<%= fieldCssClass %>" <%= disabled ? "disabled" : StringPool.BLANK %> id="<%= namespace + id %>" name="<%= namespace + name %>" <%= Validator.isNotNull(onChange) ? "onChange=\"" + onChange + "\"" : StringPool.BLANK %> onClick="<%= onClick %>" <%= Validator.isNotNull(title) ? "title=\"" + LanguageUtil.get(resourceBundle, title) + "\"" : StringPool.BLANK %> type="checkbox" <%= Validator.isNotNull(valueString) ? ("value=\"" + HtmlUtil.escapeAttribute(valueString)) + "\"" : StringPool.BLANK %> <%= AUIUtil.buildData(data) %> <%= InlineUtil.buildDynamicAttributes(dynamicAttributes) %> />
		</liferay-ui:csp>

		<c:if test='<%= type.equals("toggle-switch") %>'>

				<%
				String labelOff = (String)dynamicAttributes.get("labelOff");
				String labelOn = (String)dynamicAttributes.get("labelOn");
				%>

				<span class="toggle-switch-bar">
					<span class="toggle-switch-handle" data-label-off="<%= Validator.isNotNull(labelOff) ? HtmlUtil.escapeAttribute(LanguageUtil.get(resourceBundle, labelOff)) : StringPool.BLANK %>" data-label-on="<%= Validator.isNotNull(labelOn) ? HtmlUtil.escapeAttribute(LanguageUtil.get(resourceBundle, labelOn)) : StringPool.BLANK %>">
						<c:if test="<%= Validator.isNotNull(buttonIconOn) %>">
							<span class="button-icon button-icon-on toggle-switch-icon">
								<%= buttonIconOn %>
							</span>
						</c:if>

						<c:if test="<%= Validator.isNotNull(buttonIconOff) %>">
							<span class="button-icon button-icon-off toggle-switch-icon">
								<%= buttonIconOff %>
							</span>
						</c:if>

						<c:if test="<%= Validator.isNotNull(iconOn) %>">
							<span class="toggle-switch-icon toggle-switch-icon-on">
								<%= iconOn %>
							</span>
						</c:if>

						<c:if test="<%= Validator.isNotNull(iconOff) %>">
							<span class="toggle-switch-icon toggle-switch-icon-off">
								<%= iconOff %>
							</span>
						</c:if>
					</span>
				</span>
				</span>

				<c:if test='<%= inlineLabel.equals("right") %>'>
					<%= toggleSwitchLabelContent %>
				</c:if>

				<c:if test="<%= Validator.isNotNull(helpMessage) %>">
					<span class="toggle-switch-text-right"><%= pageContext.getAttribute("helpMessageContent") %></span>
				</c:if>

				<c:if test="<%= changesContext %>">
					<span class="hide-accessible sr-only">(<liferay-ui:message key="changing-the-value-of-this-field-reloads-the-page" />)</span>
				</c:if>
			</label>
		</c:if>
	</c:when>
	<c:when test='<%= type.equals("radio") %>'>

		<%
		String valueString = String.valueOf(checked);

		if (value != null) {
			valueString = value.toString();
		}

		if (!ignoreRequestValue) {
			String requestValue = ParamUtil.getString(request, name);

			if (Validator.isNotNull(requestValue)) {
				checked = valueString.equals(requestValue);
			}
		}
		%>

		<liferay-ui:csp>
			<input <%= checked ? "checked" : StringPool.BLANK %> class="<%= fieldCssClass %>" <%= disabled ? "disabled" : StringPool.BLANK %> id="<%= namespace + id %>" name="<%= namespace + name %>" <%= Validator.isNotNull(onChange) ? "onChange=\"" + onChange + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(onClick) ? "onClick=\"" + onClick + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(title) ? "title=\"" + LanguageUtil.get(resourceBundle, title) + "\"" : StringPool.BLANK %> type="radio" value="<%= HtmlUtil.escapeAttribute(valueString) %>" <%= AUIUtil.buildData(data) %> <%= InlineUtil.buildDynamicAttributes(dynamicAttributes) %> />
		</liferay-ui:csp>
	</c:when>
	<c:when test='<%= type.equals("resource") %>'>
		<liferay-ui:input-resource
			id="<%= id %>"
			title="<%= title %>"
			url="<%= String.valueOf(value) %>"
		/>
	</c:when>
	<c:when test='<%= type.equals("timeZone") %>'>

		<%
		int displayStyle = TimeZone.LONG;

		if (dynamicAttributes.get("displayStyle") != null) {
			displayStyle = GetterUtil.getInteger((String)dynamicAttributes.get("displayStyle"));
		}

		if (Validator.isNull(value)) {
			TimeZone defaultTimeZone = TimeZoneUtil.getDefault();

			value = BeanPropertiesUtil.getStringSilent(bean, field, defaultTimeZone.getID());
		}
		%>

		<liferay-ui:input-time-zone
			autoFocus="<%= autoFocus %>"
			daylight='<%= GetterUtil.getBoolean((String)dynamicAttributes.get("daylight")) %>'
			disabled="<%= disabled %>"
			displayStyle="<%= displayStyle %>"
			name="<%= name %>"
			nullable='<%= GetterUtil.getBoolean((String)dynamicAttributes.get("nullable")) %>'
			value="<%= value.toString() %>"
		/>
	</c:when>
	<c:otherwise>

		<%
		String valueString = StringPool.BLANK;

		if (value != null) {
			valueString = value.toString();
		}

		if (type.equals("hidden") && (value == null)) {
			valueString = BeanPropertiesUtil.getStringSilent(bean, name);
		}
		else if (!ignoreRequestValue && (Validator.isNull(type) || _TYPES.contains(type))) {
			valueString = BeanParamUtil.getString(bean, request, name, AUIUtil.getBeanPropertyFunction(name, valueString), valueString);

			if (Validator.isNotNull(fieldParam)) {
				valueString = ParamUtil.getString(request, fieldParam, valueString);
			}
		}
		%>

		<c:choose>
			<c:when test='<%= localized && (type.equals("editor") || type.equals("text") || type.equals("textarea")) %>'>
				<liferay-ui:input-localized
					activeLanguageIds="<%= activeLanguageIds %>"
					adminMode="<%= adminMode %>"
					autoFocus="<%= autoFocus %>"
					availableLocales='<%= LanguageUtil.getAvailableLocales(BeanPropertiesUtil.getLongSilent(bean, "groupId", themeDisplay.getSiteGroupId())) %>'
					cssClass="<%= fieldCssClass %>"
					defaultLanguageId="<%= defaultLanguageId %>"
					disabled="<%= disabled %>"
					formName="<%= formName %>"
					helpMessage="<%= helpMessage %>"
					id="<%= id %>"
					ignoreRequestValue="<%= ignoreRequestValue %>"
					languageId="<%= languageId %>"
					languagesDropdownDirection="<%= languagesDropdownDirection %>"
					languagesDropdownVisible="<%= languagesDropdownVisible %>"
					name="<%= name %>"
					onChange="<%= onChange %>"
					onClick="<%= onClick %>"
					placeholder="<%= placeholder %>"
					selectedLanguageId="<%= selectedLanguageId %>"
					type='<%= type.equals("text") ? "input" : type %>'
					xml="<%= valueString %>"
				/>
			</c:when>
			<c:when test='<%= type.equals("editor") %>'>
				<liferay-ui:input-editor
					contents="<%= valueString %>"
					contentsLanguageId="<%= languageId %>"
					cssClass="<%= cssClass %>"
					editorName="ckeditor"
					name="<%= fieldParam %>"
					toolbarSet="simple"
				/>
			</c:when>
			<c:when test='<%= type.equals("textarea") %>'>

				<%
				String[] storedDimensions = resizable ? StringUtil.split(SessionClicks.get(request, _TEXTAREA_WIDTH_HEIGHT_PREFIX + namespace + id, StringPool.BLANK)) : StringPool.EMPTY_ARRAY;
				%>

				<liferay-ui:csp>
					<textarea class="<%= fieldCssClass %>" <%= disabled ? "disabled" : StringPool.BLANK %> id="<%= namespace + id %>" <%= multiple ? "multiple" : StringPool.BLANK %> name="<%= namespace + (Validator.isBlank(fieldParam) ? name : fieldParam) %>" <%= Validator.isNotNull(onChange) ? "onChange=\"" + onChange + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(onClick) ? "onClick=\"" + onClick + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(placeholder) ? "placeholder=\"" + LanguageUtil.get(resourceBundle, placeholder) + "\"" : StringPool.BLANK %> <%= (storedDimensions.length > 1) ? "style=\"height: " + storedDimensions[0] + "; width: " + storedDimensions[1] + ";" + title + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(title) ? "title=\"" + LanguageUtil.get(resourceBundle, title) + "\"" : StringPool.BLANK %> <%= AUIUtil.buildData(data) %> <%= InlineUtil.buildDynamicAttributes(dynamicAttributes) %>><%= HtmlUtil.escape(valueString) %></textarea>
				</liferay-ui:csp>

				<c:if test="<%= autoSize %>">
					<aui:script sandbox="<%= true %>">
						var inputElement = document.getElementById('<%= namespace + id %>');
						new Liferay.Util.AutoSize(inputElement);
					</aui:script>
				</c:if>

				<c:if test="<%= resizable %>">
					<aui:script use="resize-base">
						var textareaNode = A.one('#<%= namespace + id %>');

						var resizeInstance = new A.Resize(
							{
								after: {
									end: function (event) {
										Liferay.Util.Session.set(
											'<%= _TEXTAREA_WIDTH_HEIGHT_PREFIX %><%= namespace + id %>',
											textareaNode.getStyle('height') +
												',' +
												textareaNode.getStyle('width')
										);
									},
								},
								autoHide: true,
								handles: 'r, br, b',
								node: textareaNode,
							});

						textareaNode.setData('resizeInstance', resizeInstance);
					</aui:script>
				</c:if>
			</c:when>
			<c:otherwise>
				<liferay-ui:csp>
					<input <%= type.equals("image") ? "alt=\"" + LanguageUtil.get(resourceBundle, title) + "\"" : StringPool.BLANK %> class="<%= fieldCssClass %>" <%= disabled ? "disabled" : StringPool.BLANK %> id="<%= namespace + id %>" <%= (max != null) ? "max=\"" + max + "\"": StringPool.BLANK %> <%= (min != null) ? "min=\"" + min + "\"": StringPool.BLANK %> <%= multiple ? "multiple" : StringPool.BLANK %> name="<%= namespace + name %>" <%= Validator.isNotNull(onChange) ? "onChange=\"" + onChange + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(onClick) ? "onClick=\"" + onClick + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(placeholder) ? "placeholder=\"" + LanguageUtil.get(resourceBundle, placeholder) + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(title) ? "title=\"" + LanguageUtil.get(resourceBundle, title) + "\"" : StringPool.BLANK %> type="<%= Validator.isNull(type) ? "text" : type %>" <%= !type.equals("image") ? "value=\"" + HtmlUtil.escapeAttribute(valueString) + "\"" : StringPool.BLANK %> <%= AUIUtil.buildData(data) %> <%= InlineUtil.buildDynamicAttributes(dynamicAttributes) %> />
				</liferay-ui:csp>
			</c:otherwise>
		</c:choose>

		<c:if test="<%= autoFocus %>">
			<aui:script>
				Liferay.Util.focusFormField('#<%= namespace + id %>');
			</aui:script>
		</c:if>
	</c:otherwise>
</c:choose>

<c:if test="<%= Validator.isNotNull(prefix) || Validator.isNotNull(suffix) %>">
		</div>

		<c:if test="<%= Validator.isNotNull(suffix) %>">
			<div class="input-group-append input-group-item-shrink">
				<span class="input-group-text <%= helpTextCssClass %>"><liferay-ui:message key="<%= suffix %>" /></span>
			</div>
		</c:if>
	</div>
</c:if>

<c:if test='<%= !type.equals("hidden") && Validator.isNotNull(labelContent) %>'>
	<c:if test='<%= choiceField || inlineLabel.equals("right") %>'>
			<%= labelContent %>
		</label>
	</c:if>
</c:if>

<c:if test='<%= !type.equals("hidden") && !wrappedField && useInputWrapper %>'>
	</div>
</c:if>

<%!
private static final String _TEXTAREA_WIDTH_HEIGHT_PREFIX = "liferay_resize_";

private static final Set<String> _TYPES = new HashSet<>(Arrays.asList("color", "email", "number", "range", "tel", "text", "textarea"));
%>