<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommercePriceListDisplayContext commercePriceListDisplayContext = (CommercePriceListDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommercePriceList commercePriceList = commercePriceListDisplayContext.getCommercePriceList();
CommercePriceModifier commercePriceModifier = commercePriceListDisplayContext.getCommercePriceModifier();
long commercePriceModifierId = commercePriceListDisplayContext.getCommercePriceModifierId();

CommerceCurrency commerceCurrency = commercePriceList.getCommerceCurrency();

boolean neverExpire = ParamUtil.getBoolean(request, "neverExpire", true);

if ((commercePriceModifier != null) && (commercePriceModifier.getExpirationDate() != null)) {
	neverExpire = false;
}

String amountSuffix = HtmlUtil.escape(commerceCurrency.getCode());

String modifierType = ParamUtil.getString(request, "modifierType", commercePriceModifier.getModifierType());

if (modifierType.equals(CommercePriceModifierConstants.MODIFIER_TYPE_PERCENTAGE)) {
	amountSuffix = StringPool.PERCENT;
}
%>

<portlet:actionURL name="/commerce_price_list/edit_commerce_price_modifier" var="editCommercePriceModifierActionURL" />

<aui:form action="<%= editCommercePriceModifierActionURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="commercePriceListId" type="hidden" value="<%= commercePriceListDisplayContext.getCommercePriceListId() %>" />
	<aui:input name="commercePriceModifierId" type="hidden" value="<%= commercePriceModifierId %>" />

	<liferay-ui:error exception="<%= CommercePriceModifierAmountException.class %>" message="please-enter-a-valid-amount" />

	<aui:model-context bean="<%= commercePriceModifier %>" model="<%= CommercePriceModifier.class %>" />

	<commerce-ui:panel
		title='<%= LanguageUtil.get(request, "details") %>'
	>
		<aui:input label="name" name="title" required="<%= true %>" />

		<aui:select name="target" required="<%= true %>" showEmptyOption="<%= true %>">

			<%
			for (String target : CommercePriceModifierConstants.TARGETS) {
			%>

				<aui:option label="<%= target %>" selected="<%= target.equals(commercePriceModifier.getTarget()) %>" value="<%= target %>" />

			<%
			}
			%>

		</aui:select>

		<aui:select label='<%= LanguageUtil.get(request, "modifier") %>' name="modifierType" onChange='<%= liferayPortletResponse.getNamespace() + "selectType();" %>' showEmptyOption="<%= true %>">

			<%
			for (CommercePriceModifierType commercePriceModifierType : commercePriceListDisplayContext.getCommercePriceModifierTypes()) {
				String commercePriceModifierTypeKey = commercePriceModifierType.getKey();
			%>

				<aui:option label="<%= commercePriceModifierType.getLabel(locale) %>" selected="<%= commercePriceModifierTypeKey.equals(commercePriceModifier.getModifierType()) %>" value="<%= commercePriceModifierTypeKey %>" />

			<%
			}
			%>

		</aui:select>

		<aui:input ignoreRequestValue="<%= true %>" label='<%= LanguageUtil.get(request, "amount") %>' name="modifierAmount" suffix="<%= amountSuffix %>" type="text" value="<%= commerceCurrency.round(commercePriceModifier.getModifierAmount()) %>">
			<aui:validator name="number" />
		</aui:input>

		<aui:input name="priority" />

		<aui:input checked="<%= commercePriceModifier.isActive() %>" inlineLabel="right" labelCssClass="simple-toggle-switch" name="active" type="toggle-switch" />
	</commerce-ui:panel>

	<commerce-ui:panel
		title='<%= LanguageUtil.get(request, "schedule[noun]") %>'
	>
		<liferay-ui:error exception="<%= CommercePriceModifierExpirationDateException.class %>" message="please-enter-a-valid-expiration-date" />

		<aui:fieldset>
			<aui:input formName="fm" label="publish-date" name="displayDate" />

			<aui:input dateTogglerCheckboxLabel="never-expire" disabled="<%= neverExpire %>" formName="fm" name="expirationDate" />
		</aui:fieldset>
	</commerce-ui:panel>

	<c:if test="<%= commercePriceListDisplayContext.hasCustomAttributesAvailable(CommercePriceModifier.class.getName(), commercePriceModifierId) %>">
		<commerce-ui:panel
			title='<%= LanguageUtil.get(request, "custom-attributes") %>'
		>
			<liferay-expando:custom-attribute-list
				className="<%= CommercePriceModifier.class.getName() %>"
				classPK="<%= commercePriceModifierId %>"
				editable="<%= true %>"
				label="<%= true %>"
			/>
		</commerce-ui:panel>
	</c:if>

	<aui:button-row cssClass="price-modifier-button-row">
		<aui:button cssClass="btn-lg" type="submit" />

		<aui:button cssClass="btn-lg" type="cancel" />
	</aui:button-row>
</aui:form>

<aui:script sandbox="<%= true %>">
	Liferay.provide(window, '<portlet:namespace />selectType', () => {
		const portletURL = Liferay.Util.PortletURL.createPortletURL(
			'<%= currentURLObj %>',
			{
				modifierType: document.getElementById(
					'<portlet:namespace />modifierType'
				).value,
			}
		);

		window.location.replace(portletURL.toString());
	});
</aui:script>