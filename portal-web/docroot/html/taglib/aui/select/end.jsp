<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/taglib/aui/select/init.jsp" %>

<div class="<%= controlGroupCssClass %>">
	<c:if test='<%= Validator.isNotNull(label) && !inlineLabel.equals("right") %>'>
		<label <%= AUIUtil.buildLabel("select", inlineField, true, namespace + id) %>>
			<liferay-ui:message key="<%= label %>" localizeKey="<%= localizeLabel %>" />

			<c:if test="<%= required && showRequiredLabel %>">
				<aui:icon cssClass="reference-mark text-warning" image="asterisk" markupView="lexicon" />

				<span class="hide-accessible sr-only"><liferay-ui:message key="required" /></span>
			</c:if>

			<c:if test="<%= Validator.isNotNull(helpMessage) %>">
				<liferay-ui:icon-help message="<%= helpMessage %>" />
			</c:if>

			<c:if test="<%= changesContext %>">
				<span class="hide-accessible sr-only">(<liferay-ui:message key="changing-the-value-of-this-field-reloads-the-page" />)</span>
			</c:if>
		</label>
	</c:if>

	<c:if test="<%= Validator.isNotNull(prefix) %>">
		<span class="prefix">
			<liferay-ui:message key="<%= prefix %>" />
		</span>
	</c:if>

	<liferay-ui:csp>
		<select class="<%= fieldCss %>" <%= disabled ? "disabled" : StringPool.BLANK %> id="<%= namespace + id %>" <%= multiple ? "multiple" : StringPool.BLANK %> name="<%= namespace + name %>" <%= Validator.isNotNull(onChange) ? "onChange=\"" + onChange + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(onClick) ? "onClick=\"" + onClick + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(title) ? "title=\"" + LanguageUtil.get(resourceBundle, title) + "\"" : StringPool.BLANK %> <%= AUIUtil.buildData(data) %> <%= InlineUtil.buildDynamicAttributes(dynamicAttributes) %>>
			<c:if test="<%= showEmptyOption %>">
				<aui:option value="<%= (Validator.isNotNull(listType) || numericValue) ? 0 : StringPool.BLANK %>" />
			</c:if>

			<c:if test="<%= Validator.isNotNull(listType) %>">

				<%
				long listTypeId = ParamUtil.getLong(request, name, BeanParamUtil.getLong(bean, request, listTypeFieldName, AUIUtil.getBeanPropertyToLongFunction(listTypeFieldName)));

				List<ListType> listTypeModels = ListTypeServiceUtil.getListTypes(themeDisplay.getCompanyId(), listType);

				for (ListType listTypeModel : listTypeModels) {
				%>

					<aui:option selected="<%= listTypeId == listTypeModel.getListTypeId() %>" value="<%= listTypeModel.getListTypeId() %>"><liferay-ui:message key="<%= listTypeModel.getName() %>" /></aui:option>

				<%
				}
				%>

			</c:if>

			<%= bodyContent %>
		</select>
	</liferay-ui:csp>

	<c:if test="<%= Validator.isNotNull(suffix) %>">
		<span class="suffix">
			<liferay-ui:message key="<%= suffix %>" />
		</span>
	</c:if>

	<c:if test='<%= inlineLabel.equals("right") %>'>
		<label <%= AUIUtil.buildLabel("select", inlineField, true, namespace + id) %>>
			<liferay-ui:message key="<%= label %>" />

			<c:if test="<%= Validator.isNotNull(helpMessage) %>">
				<liferay-ui:icon-help message="<%= helpMessage %>" />
			</c:if>

			<c:if test="<%= changesContext %>">
				<span class="hide-accessible sr-only"><liferay-ui:message key="changing-the-value-of-this-field-reloads-the-page" />)</span>
			</c:if>
		</label>
	</c:if>
</div>