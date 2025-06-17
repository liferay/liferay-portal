<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/custom_attribute/init.jsp" %>

<%
String randomNamespace = PortalUtil.generateRandomKey(request, "taglib_ui_custom_attribute_page") + StringPool.UNDERLINE;

Set<Locale> availableLocales = (Set<Locale>)request.getAttribute("liferay-expando:custom-attribute:availableLocales");
String className = (String)request.getAttribute("liferay-expando:custom-attribute:className");
long classPK = GetterUtil.getLong((String)request.getAttribute("liferay-expando:custom-attribute:classPK"));
boolean editable = GetterUtil.getBoolean((String)request.getAttribute("liferay-expando:custom-attribute:editable"));
boolean label = GetterUtil.getBoolean((String)request.getAttribute("liferay-expando:custom-attribute:label"));
String name = (String)request.getAttribute("liferay-expando:custom-attribute:name");

ExpandoBridge expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(company.getCompanyId(), className, classPK);
%>

<c:if test="<%= expandoBridge.hasAttribute(name) %>">

	<%
	Serializable defaultValue = expandoBridge.getAttributeDefault(name);
	int type = expandoBridge.getAttributeType(name);
	Serializable value = expandoBridge.getAttribute(name);

	UnicodeProperties unicodeProperties = expandoBridge.getAttributeProperties(name);

	String propertyDisplayType = GetterUtil.getString(unicodeProperties.getProperty(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE), ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX);
	int propertyHeight = GetterUtil.getInteger(unicodeProperties.getProperty(ExpandoColumnConstants.PROPERTY_HEIGHT));
	boolean propertyHidden = GetterUtil.getBoolean(unicodeProperties.get(ExpandoColumnConstants.PROPERTY_HIDDEN));
	boolean propertyLocalizeFieldName = GetterUtil.getBoolean(unicodeProperties.get(ExpandoColumnConstants.PROPERTY_LOCALIZE_FIELD_NAME), true);
	boolean propertySecret = GetterUtil.getBoolean(unicodeProperties.getProperty(ExpandoColumnConstants.PROPERTY_SECRET));
	boolean propertyVisibleWithUpdatePermission = GetterUtil.getBoolean(unicodeProperties.get(ExpandoColumnConstants.PROPERTY_VISIBLE_WITH_UPDATE_PERMISSION));
	int propertyWidth = GetterUtil.getInteger(unicodeProperties.getProperty(ExpandoColumnConstants.PROPERTY_WIDTH));

	if (editable && propertyVisibleWithUpdatePermission) {
		propertyHidden = !ExpandoColumnPermissionUtil.contains(permissionChecker, company.getCompanyId(), className, ExpandoTableConstants.DEFAULT_TABLE_NAME, name, ActionKeys.UPDATE);
	}

	String localizedName = name;

	if (propertyLocalizeFieldName) {
		localizedName = LanguageUtil.get(resourceBundle, name);

		if (name.equals(localizedName)) {
			localizedName = HtmlUtil.escape(TextFormatter.format(name, TextFormatter.J));
		}
	}

	Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
	%>

	<c:if test="<%= !propertyHidden && ExpandoColumnPermissionUtil.contains(permissionChecker, company.getCompanyId(), className, ExpandoTableConstants.DEFAULT_TABLE_NAME, name, ActionKeys.VIEW) %>">
		<c:choose>
			<c:when test="<%= editable && ExpandoColumnPermissionUtil.contains(permissionChecker, company.getCompanyId(), className, ExpandoTableConstants.DEFAULT_TABLE_NAME, name, ActionKeys.UPDATE) %>">
				<aui:field-wrapper inlineLabel='<%= (type == ExpandoColumnConstants.BOOLEAN)? "right":StringPool.BLANK %>' label="<%= label ? localizedName : StringPool.BLANK %>" localizeLabel="<%= propertyLocalizeFieldName %>" name="<%= randomNamespace + name %>">
					<input name="<portlet:namespace />ExpandoAttributeName--<%= HtmlUtil.escapeAttribute(name) %>--" type="hidden" value="<%= HtmlUtil.escapeAttribute(name) %>" />

					<c:choose>
						<c:when test="<%= type == ExpandoColumnConstants.BOOLEAN %>">

							<%
							Boolean curValue = (Boolean)value;

							curValue = ParamUtil.getBoolean(request, "ExpandoAttribute--" + name + "--", curValue);
							%>

							<aui:input inlineLabel="right" label="" labelCssClass="simple-toggle-switch" name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>' type="toggle-switch" value="<%= curValue %>" />
						</c:when>
						<c:when test="<%= type == ExpandoColumnConstants.BOOLEAN_ARRAY %>">
						</c:when>
						<c:when test="<%= type == ExpandoColumnConstants.DATE %>">
							<span id="<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>">

								<%
								Calendar valueDate = CalendarFactoryUtil.getCalendar(timeZone, locale);

								if (value != null) {
									valueDate.setTime((Date)value);
								}
								else {
									valueDate.setTime(new Date());
								}

								String fieldParam = "ExpandoAttribute--" + name + "--";

								int day = ParamUtil.getInteger(request, fieldParam + "Day", -1);

								if ((day == -1) && (valueDate != null)) {
									day = valueDate.get(Calendar.DATE);
								}

								int month = ParamUtil.getInteger(request, fieldParam + "Month", -1);

								if ((month == -1) && (valueDate != null)) {
									month = valueDate.get(Calendar.MONTH);
								}

								int year = ParamUtil.getInteger(request, fieldParam + "Year", -1);

								if ((year == -1) && (valueDate != null)) {
									year = valueDate.get(Calendar.YEAR);
								}

								int amPm = ParamUtil.getInteger(request, fieldParam + "AmPm", -1);

								if ((amPm == -1) && (valueDate != null)) {
									amPm = Calendar.AM;

									if (DateUtil.isFormatAmPm(locale)) {
										amPm = valueDate.get(Calendar.AM_PM);
									}
								}

								int hour = ParamUtil.getInteger(request, fieldParam + "Hour", -1);

								if ((hour == -1) && (valueDate != null)) {
									hour = valueDate.get(Calendar.HOUR_OF_DAY);

									if (DateUtil.isFormatAmPm(locale)) {
										hour = valueDate.get(Calendar.HOUR);
									}
								}

								int minute = ParamUtil.getInteger(request, fieldParam + "Minute", -1);

								if ((minute == -1) && (valueDate != null)) {
									minute = valueDate.get(Calendar.MINUTE);
								}
								%>

								<liferay-ui:input-date
									dayParam='<%= fieldParam + "Day" %>'
									dayValue="<%= day %>"
									disabled="<%= false %>"
									firstDayOfWeek="<%= valueDate.getFirstDayOfWeek() - 1 %>"
									monthParam='<%= fieldParam + "Month" %>'
									monthValue="<%= month %>"
									name='<%= fieldParam + "Date" %>'
									yearParam='<%= fieldParam + "Year" %>'
									yearValue="<%= year %>"
								/>

								<liferay-ui:input-time
									amPmParam='<%= fieldParam + "AmPm" %>'
									amPmValue="<%= amPm %>"
									disabled="<%= false %>"
									hourParam='<%= fieldParam + "Hour" %>'
									hourValue="<%= hour %>"
									minuteParam='<%= fieldParam + "Minute" %>'
									minuteValue="<%= minute %>"
									name='<%= fieldParam + "Time" %>'
								/>
							</span>
						</c:when>
						<c:when test="<%= type == ExpandoColumnConstants.DATE_ARRAY %>">
						</c:when>
						<c:when test="<%= type == ExpandoColumnConstants.DOUBLE_ARRAY %>">

							<%
							double[] curValue = ParamUtil.getDoubleValues(request, "ExpandoAttribute--" + name + "--", (double[])value);
							%>

							<c:choose>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_CHECKBOX) %>">

									<%
									for (double curDefaultValue : (double[])defaultValue) {
									%>

										<aui:input checked="<%= (curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue) %>" id="<%= StringUtil.randomId() %>" label="<%= String.valueOf(curDefaultValue) %>" name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>' type="checkbox" value="<%= curDefaultValue %>" />

									<%
									}
									%>

								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_RADIO) %>">

									<%
									for (double curDefaultValue : (double[])defaultValue) {
									%>

										<aui:input checked="<%= (curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue) %>" label="<%= String.valueOf(curDefaultValue) %>" name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>' type="radio" value="<%= curDefaultValue %>" />

									<%
									}
									%>

								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_SELECTION_LIST) %>">
									<select class="form-control" id="<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>" name="<portlet:namespace />ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--">

										<%
										for (double curDefaultValue : (double[])defaultValue) {
										%>

											<option <%= ((curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue)) ? "selected" : "" %>><%= curDefaultValue %></option>

										<%
										}
										%>

									</select>
								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) %>">

									<%
									if (curValue.length == 0) {
										curValue = (double[])defaultValue;
									}
									%>

									<textarea class="field form-control lfr-textarea" id="<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>" name="<portlet:namespace />ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--"><%= StringUtil.merge(curValue, StringPool.NEW_LINE) %></textarea>
								</c:when>
							</c:choose>
						</c:when>
						<c:when test="<%= type == ExpandoColumnConstants.FLOAT_ARRAY %>">

							<%
							float[] curValue = ParamUtil.getFloatValues(request, "ExpandoAttribute--" + name + "--", (float[])value);
							%>

							<c:choose>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_CHECKBOX) %>">

									<%
									for (float curDefaultValue : (float[])defaultValue) {
									%>

										<aui:input checked="<%= (curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue) %>" id="<%= StringUtil.randomId() %>" label="<%= String.valueOf(curDefaultValue) %>" name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>' type="checkbox" value="<%= curDefaultValue %>" />

									<%
									}
									%>

								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_RADIO) %>">

									<%
									for (float curDefaultValue : (float[])defaultValue) {
									%>

										<aui:input checked="<%= (curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue) %>" label="<%= String.valueOf(curDefaultValue) %>" name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>' type="radio" value="<%= curDefaultValue %>" />

									<%
									}
									%>

								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_SELECTION_LIST) %>">
									<select class="form-control" id="<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>" name="<portlet:namespace />ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--">

										<%
										for (float curDefaultValue : (float[])defaultValue) {
										%>

											<option <%= ((curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue)) ? "selected" : "" %>><%= curDefaultValue %></option>

										<%
										}
										%>

									</select>
								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) %>">

									<%
									if (curValue.length == 0) {
										curValue = (float[])defaultValue;
									}
									%>

									<textarea class="field form-control lfr-textarea" id="<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>" name="<portlet:namespace />ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--"><%= StringUtil.merge(curValue, StringPool.NEW_LINE) %></textarea>
								</c:when>
							</c:choose>
						</c:when>
						<c:when test="<%= type == ExpandoColumnConstants.GEOLOCATION %>">
							<div id="<portlet:namespace />CoordinatesContainer">
								<div class="glyphicon glyphicon-map-marker" id="<%= portletDisplay.getNamespace() %>ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--Location">
								</div>

								<%
								JSONObject geolocationJSONObject = JSONFactoryUtil.createJSONObject(value.toString());
								String mapDisplayName = HtmlUtil.getAUICompatibleId(name);
								%>

								<liferay-map:map-display
									geolocation="<%= true %>"
									latitude='<%= geolocationJSONObject.getDouble("latitude", 0) %>'
									longitude='<%= geolocationJSONObject.getDouble("longitude", 0) %>'
									name='<%= "ExpandoAttribute--" + mapDisplayName +"--" %>'
								/>
							</div>

							<liferay-frontend:component
								componentId="GeoLocationField"
								context='<%=
									HashMapBuilder.<String, Object>put(
										"inputName", portletDisplay.getNamespace() + "ExpandoAttribute--" + HtmlUtil.escapeJS(name) + "--"
									).put(
										"mapName", portletDisplay.getNamespace() + "ExpandoAttribute--" + mapDisplayName + "--"
									).build()
								%>'
								module="{GeoLocationField} from expando-taglib"
							/>

							<aui:input name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>' type="hidden" value="<%= HtmlUtil.escape(value.toString()) %>" />
						</c:when>
						<c:when test="<%= type == ExpandoColumnConstants.INTEGER_ARRAY %>">

							<%
							int[] curValue = ParamUtil.getIntegerValues(request, "ExpandoAttribute--" + name + "--", (int[])value);
							%>

							<c:choose>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_CHECKBOX) %>">

									<%
									for (int curDefaultValue : (int[])defaultValue) {
									%>

										<aui:input checked="<%= (curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue) %>" id="<%= StringUtil.randomId() %>" label="<%= String.valueOf(curDefaultValue) %>" name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>' type="checkbox" value="<%= curDefaultValue %>" />

									<%
									}
									%>

								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_RADIO) %>">

									<%
									for (int curDefaultValue : (int[])defaultValue) {
									%>

										<aui:input checked="<%= (curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue) %>" label="<%= String.valueOf(curDefaultValue) %>" name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>' type="radio" value="<%= curDefaultValue %>" />

									<%
									}
									%>

								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_SELECTION_LIST) %>">
									<select class="form-control" id="<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>" name="<portlet:namespace />ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--">

										<%
										for (int curDefaultValue : (int[])defaultValue) {
										%>

											<option <%= ((curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue)) ? "selected" : "" %>><%= curDefaultValue %></option>

										<%
										}
										%>

									</select>
								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) %>">

									<%
									if (curValue.length == 0) {
										curValue = (int[])defaultValue;
									}
									%>

									<textarea class="field form-control lfr-textarea" id="<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>" name="<portlet:namespace />ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--"><%= StringUtil.merge(curValue, StringPool.NEW_LINE) %></textarea>
								</c:when>
							</c:choose>
						</c:when>
						<c:when test="<%= type == ExpandoColumnConstants.LONG_ARRAY %>">

							<%
							long[] curValue = ParamUtil.getLongValues(request, "ExpandoAttribute--" + name + "--", (long[])value);
							%>

							<c:choose>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_CHECKBOX) %>">

									<%
									for (long curDefaultValue : (long[])defaultValue) {
									%>

										<aui:input checked="<%= (curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue) %>" id="<%= StringUtil.randomId() %>" label="<%= String.valueOf(curDefaultValue) %>" name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>' type="checkbox" value="<%= curDefaultValue %>" />

									<%
									}
									%>

								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_RADIO) %>">

									<%
									for (long curDefaultValue : (long[])defaultValue) {
									%>

										<aui:input checked="<%= (curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue) %>" label="<%= String.valueOf(curDefaultValue) %>" name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>' type="radio" value="<%= curDefaultValue %>" />

									<%
									}
									%>

								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_SELECTION_LIST) %>">
									<select class="form-control" id="<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>" name="<portlet:namespace />ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--">

										<%
										for (long curDefaultValue : (long[])defaultValue) {
										%>

											<option <%= ((curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue)) ? "selected" : "" %>><%= curDefaultValue %></option>

										<%
										}
										%>

									</select>
								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) %>">

									<%
									if (curValue.length == 0) {
										curValue = (long[])defaultValue;
									}
									%>

									<textarea class="field form-control lfr-textarea" id="<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>" name="<portlet:namespace />ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--"><%= StringUtil.merge(curValue, StringPool.NEW_LINE) %></textarea>
								</c:when>
							</c:choose>
						</c:when>
						<c:when test="<%= type == ExpandoColumnConstants.NUMBER_ARRAY %>">

							<%
							Number[] curValue = ParamUtil.getNumberValues(request, "ExpandoAttribute--" + name + "--", (Number[])value);
							%>

							<c:choose>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_CHECKBOX) %>">

									<%
									for (Number curDefaultValue : (Number[])defaultValue) {
									%>

										<aui:input checked="<%= (curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue) %>" id="<%= StringUtil.randomId() %>" label="<%= String.valueOf(curDefaultValue) %>" name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>' type="checkbox" value="<%= curDefaultValue %>" />

									<%
									}
									%>

								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_RADIO) %>">

									<%
									for (Number curDefaultValue : (Number[])defaultValue) {
									%>

										<aui:input checked="<%= (curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue) %>" label="<%= String.valueOf(curDefaultValue) %>" name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>' type="radio" value="<%= curDefaultValue %>" />

									<%
									}
									%>

								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_SELECTION_LIST) %>">
									<select class="form-control" id="<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>" name="<portlet:namespace />ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--">

										<%
										for (Number curDefaultValue : (Number[])defaultValue) {
										%>

											<option <%= ((curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue)) ? "selected" : "" %>><%= curDefaultValue %></option>

										<%
										}
										%>

									</select>
								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) %>">

									<%
									if (curValue.length == 0) {
										curValue = (Number[])defaultValue;
									}
									%>

									<textarea class="field form-control lfr-textarea" id="<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>" name="<portlet:namespace />ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--"><%= StringUtil.merge(curValue, StringPool.NEW_LINE) %></textarea>
								</c:when>
							</c:choose>
						</c:when>
						<c:when test="<%= type == ExpandoColumnConstants.SHORT_ARRAY %>">

							<%
							short[] curValue = ParamUtil.getShortValues(request, "ExpandoAttribute--" + name + "--", (short[])value);
							%>

							<c:choose>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_CHECKBOX) %>">

									<%
									for (short curDefaultValue : (short[])defaultValue) {
									%>

										<aui:input checked="<%= (curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue) %>" id="<%= StringUtil.randomId() %>" label="<%= String.valueOf(curDefaultValue) %>" name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>' type="checkbox" value="<%= curDefaultValue %>" />

									<%
									}
									%>

								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_RADIO) %>">

									<%
									for (short curDefaultValue : (short[])defaultValue) {
									%>

										<aui:input checked="<%= (curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue) %>" label="<%= String.valueOf(curDefaultValue) %>" name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>' type="radio" value="<%= curDefaultValue %>" />

									<%
									}
									%>

								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_SELECTION_LIST) %>">
									<select class="form-control" id="<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>" name="<portlet:namespace />ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--">

										<%
										for (short curDefaultValue : (short[])defaultValue) {
										%>

											<option <%= ((curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue)) ? "selected" : "" %>><%= curDefaultValue %></option>

										<%
										}
										%>

									</select>
								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) %>">

									<%
									if (curValue.length == 0) {
										curValue = (short[])defaultValue;
									}
									%>

									<textarea class="field form-control lfr-textarea" id="<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>" name="<portlet:namespace />ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--"><%= StringUtil.merge(curValue, StringPool.NEW_LINE) %></textarea>
								</c:when>
							</c:choose>
						</c:when>
						<c:when test="<%= type == ExpandoColumnConstants.STRING_ARRAY %>">

							<%
							String[] curValue = ParamUtil.getStringValues(request, "ExpandoAttribute--" + name + "--", (String[])value);
							%>

							<c:choose>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_CHECKBOX) %>">

									<%
									for (String curDefaultValue : (String[])defaultValue) {
									%>

										<aui:input checked="<%= (curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue) %>" id="<%= StringUtil.randomId() %>" label="<%= HtmlUtil.escape(String.valueOf(curDefaultValue)) %>" name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>' type="checkbox" value="<%= curDefaultValue %>" />

									<%
									}
									%>

								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_RADIO) %>">

									<%
									for (String curDefaultValue : (String[])defaultValue) {
									%>

										<aui:input checked="<%= (curValue.length > 0) && ArrayUtil.contains(curValue, curDefaultValue) %>" label="<%= HtmlUtil.escape(curDefaultValue) %>" name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>' type="radio" value="<%= curDefaultValue %>" />

									<%
									}
									%>

								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_SELECTION_LIST) %>">
									<aui:select id="<%= randomNamespace + HtmlUtil.escapeAttribute(name) %>" label="" name='<%= "ExpandoAttribute--" + HtmlUtil.escapeAttribute(name) + "--" %>'>

										<%
										for (String curDefaultValue : (String[])defaultValue) {
										%>

											<aui:option label="<%= curDefaultValue %>" selected="<%= (curValue.length > 0) && ArrayUtil.contains(curValue, HtmlUtil.escape(curDefaultValue)) %>" value="<%= HtmlUtil.escape(curDefaultValue) %>" />

										<%
										}
										%>

									</aui:select>
								</c:when>
								<c:when test="<%= propertyDisplayType.equals(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) %>">

									<%
									if (curValue.length == 0) {
										curValue = (String[])defaultValue;
									}
									%>

									<textarea class="field form-control lfr-textarea" id="<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>" name="<portlet:namespace />ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--"><%= HtmlUtil.escape(StringUtil.merge(curValue, StringPool.NEW_LINE)) %></textarea>
								</c:when>
							</c:choose>
						</c:when>
						<c:when test="<%= type == ExpandoColumnConstants.STRING_LOCALIZED %>">

							<%
							String xml = ParamUtil.getString(request, "ExpandoAttribute--" + name + "--");

							if (Validator.isNull(xml) && (value != null)) {
								xml = LocalizationUtil.updateLocalization((Map<Locale, String>)value, StringPool.BLANK, "Data", LocaleUtil.toLanguageId(locale));
							}

							if (Validator.isNull(xml) && (defaultValue != null)) {
								xml = LocalizationUtil.updateLocalization((Map<Locale, String>)defaultValue, StringPool.BLANK, "Data", LocaleUtil.toLanguageId(locale));
							}
							%>

							<liferay-ui:input-localized
								availableLocales="<%= availableLocales %>"
								cssClass="lfr-input-text"
								id="<%= randomNamespace + name %>"
								name='<%= "ExpandoAttribute--" + name + "--" %>'
								type='<%= (propertyHeight > 0) ? "textarea" : "input" %>'
								xml="<%= xml %>"
							/>
						</c:when>
						<c:otherwise>

							<%
							value = ParamUtil.getString(request, "ExpandoAttribute--" + name + "--", String.valueOf(value));
							%>

							<c:choose>
								<c:when test="<%= propertyHeight > 0 %>">
									<aui:style type="text/css">
										#<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>  {
											height: <%= propertyHeight %>px;
											width: <%= propertyWidth %>px;
										}
									</aui:style>

									<textarea
										class="field form-control lfr-input-text"
										id="<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>"
										name="<portlet:namespace />ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--"><%= HtmlUtil.escape(String.valueOf(value)) %></textarea
									>
								</c:when>
								<c:otherwise>
									<aui:style type="text/css">
										#<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %> {
											width: <%= propertyWidth %>px;
										}
									</aui:style>

									<input class="field form-control lfr-input-text" id="<portlet:namespace /><%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>" name="<portlet:namespace />ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--" type="<%= propertySecret ? "password" : "text" %>" value="<%= HtmlUtil.escape(String.valueOf(value)) %>" />
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</aui:field-wrapper>
			</c:when>
			<c:otherwise>

				<%
				StringBundler sb = new StringBundler();

				if (type == ExpandoColumnConstants.BOOLEAN) {
					sb.append((Boolean)value);
				}
				else if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
					if (!Arrays.equals((boolean[])value, (boolean[])defaultValue)) {
						sb.append(StringUtil.merge((boolean[])value));
					}
				}
				else if (type == ExpandoColumnConstants.DATE) {
					sb.append(dateTimeFormat.format((Date)value));
				}
				else if (type == ExpandoColumnConstants.DATE_ARRAY) {
					if (!Arrays.deepEquals((Date[])value, (Date[])defaultValue)) {
						Date[] dates = (Date[])value;

						for (int i = 0; i < dates.length; i++) {
							if (i != 0) {
								sb.append(StringPool.COMMA_AND_SPACE);
							}

							sb.append(dateTimeFormat.format(dates[i]));
						}
					}
				}
				else if (type == ExpandoColumnConstants.DOUBLE) {
					sb.append((Double)value);
				}
				else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
					sb.append(StringUtil.merge((double[])value));
				}
				else if (type == ExpandoColumnConstants.FLOAT) {
					sb.append((Float)value);
				}
				else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
					sb.append(StringUtil.merge((float[])value));
				}
				else if (type == ExpandoColumnConstants.GEOLOCATION) {
					sb.append(value.toString());
				}
				else if (type == ExpandoColumnConstants.INTEGER) {
					sb.append((Integer)value);
				}
				else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
					sb.append(StringUtil.merge((int[])value));
				}
				else if (type == ExpandoColumnConstants.LONG) {
					sb.append((Long)value);
				}
				else if (type == ExpandoColumnConstants.LONG_ARRAY) {
					sb.append(StringUtil.merge((long[])value));
				}
				else if (type == ExpandoColumnConstants.NUMBER) {
					sb.append((Number)value);
				}
				else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
					sb.append(StringUtil.merge((Number[])value));
				}
				else if (type == ExpandoColumnConstants.SHORT) {
					sb.append((Short)value);
				}
				else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
					sb.append(StringUtil.merge((short[])value));
				}
				else if (type == ExpandoColumnConstants.STRING_ARRAY) {
					sb.append(StringUtil.merge((String[])value));
				}
				else if (type == ExpandoColumnConstants.STRING_LOCALIZED) {
					Map<Locale, String> values = (Map<Locale, String>)value;

					sb.append(values.get(locale));
				}
				else {
					sb.append((String)value);
				}
				%>

				<c:if test="<%= (type != ExpandoColumnConstants.GEOLOCATION) && (editable || Validator.isNotNull(sb.toString())) %>">
					<aui:field-wrapper label="<%= label ? localizedName : StringPool.BLANK %>">
						<span id="<%= randomNamespace %><%= HtmlUtil.getAUICompatibleId(name) %>"><%= HtmlUtil.escape(sb.toString()) %></span>
					</aui:field-wrapper>
				</c:if>

				<c:if test="<%= (type == ExpandoColumnConstants.GEOLOCATION) && (editable || Validator.isNotNull(sb.toString())) %>">
					<div id="<portlet:namespace />CoordinatesContainer">
						<div class="glyphicon glyphicon-map-marker" id="<%= portletDisplay.getNamespace() %>ExpandoAttribute--<%= HtmlUtil.escapeAttribute(name) %>--Location">
						</div>

						<%
						JSONObject geolocationJSONObject = JSONFactoryUtil.createJSONObject(value.toString());
						String mapDisplayName = HtmlUtil.getAUICompatibleId(name);
						%>

						<liferay-map:map-display
							geolocation="<%= true %>"
							latitude='<%= geolocationJSONObject.getDouble("latitude", 0) %>'
							longitude='<%= geolocationJSONObject.getDouble("longitude", 0) %>'
							name='<%= "ExpandoAttribute--" + mapDisplayName +"--" %>'
						/>

						<liferay-frontend:component
							componentId="GeoLocationField"
							context='<%=
								HashMapBuilder.<String, Object>put(
									"inputName", portletDisplay.getNamespace() + "ExpandoAttribute--" + HtmlUtil.escapeJS(name) + "--"
								).put(
									"mapName", portletDisplay.getNamespace() + "ExpandoAttribute--" + mapDisplayName + "--"
								).build()
							%>'
							module="{GeoLocationField} from expando-taglib"
						/>
					</div>
				</c:if>
			</c:otherwise>
		</c:choose>
	</c:if>
</c:if>