<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

CPMeasurementUnitsDisplayContext cpMeasurementUnitsDisplayContext = (CPMeasurementUnitsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPMeasurementUnit cpMeasurementUnit = cpMeasurementUnitsDisplayContext.getCPMeasurementUnit();
CPMeasurementUnit primaryCPMeasurementUnit = cpMeasurementUnitsDisplayContext.getPrimaryCPMeasurementUnit();

int type = cpMeasurementUnitsDisplayContext.getType();

boolean defaultPrimary = false;

if (primaryCPMeasurementUnit == null) {
	defaultPrimary = true;
}

boolean primary = BeanParamUtil.getBoolean(cpMeasurementUnit, request, "primary", defaultPrimary);
%>

<portlet:actionURL name="/cp_measurement_unit/edit_cp_measurement_unit" var="editCPMeasurementUnitActionURL" />

<aui:form action="<%= editCPMeasurementUnitActionURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "saveCPMeasurementUnit();" %>'>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (cpMeasurementUnit == null) ? Constants.ADD : Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="cpMeasurementUnitId" type="hidden" value="<%= (cpMeasurementUnit == null) ? 0 : cpMeasurementUnit.getCPMeasurementUnitId() %>" />
	<aui:input name="type" type="hidden" value="<%= type %>" />

	<div class="lfr-form-content">
		<liferay-ui:error exception="<%= DuplicateCPMeasurementUnitKeyException.class %>" message="please-enter-a-valid-key" />

		<aui:model-context bean="<%= cpMeasurementUnit %>" model="<%= CPMeasurementUnit.class %>" />

		<div class="sheet">
			<div class="panel-group panel-group-flush">
				<aui:fieldset>
					<aui:input name="name" />

					<aui:input name="key" />

					<aui:input inlineLabel="right" labelCssClass="simple-toggle-switch" name="primary" type="toggle-switch" value="<%= primary %>" />

					<%
					String taglibLabel = "ratio-to-primary";

					if (primaryCPMeasurementUnit != null) {
						taglibLabel = LanguageUtil.format(request, "ratio-to-x", HtmlUtil.escape(primaryCPMeasurementUnit.getName(locale)), false);
					}
					%>

					<div class="<%= primary ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />rateOptions">
						<aui:input label="<%= taglibLabel %>" name="rate" />
					</div>

					<aui:input name="priority" />
				</aui:fieldset>
			</div>
		</div>
	</div>

	<aui:button-row>
		<aui:button cssClass="btn-lg" type="submit" />

		<aui:button cssClass="btn-lg" href="<%= portletDisplay.getURLBack() %>" type="cancel" />
	</aui:button-row>
</aui:form>

<c:if test="<%= cpMeasurementUnit == null %>">
	<liferay-frontend:component
		context='<%=
			HashMapBuilder.<String, Object>put(
				"namespace", liferayPortletResponse.getNamespace()
			).build()
		%>'
		module="{debounceInput} from commerce-product-measurement-unit-web"
	/>
</c:if>

<aui:script>
	function <portlet:namespace />saveCPMeasurementUnit() {
		submitForm(document.<portlet:namespace />fm);
	}

	Liferay.Util.toggleBoxes(
		'<portlet:namespace />primary',
		'<portlet:namespace />rateOptions',
		true
	);
</aui:script>