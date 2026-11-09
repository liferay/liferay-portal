<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceDiscountRuleDisplayContext commerceDiscountRuleDisplayContext = (CommerceDiscountRuleDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceDiscountRule commerceDiscountRule = commerceDiscountRuleDisplayContext.getCommerceDiscountRule();

String type = BeanParamUtil.getString(commerceDiscountRule, request, "type");
%>

<portlet:actionURL name="/commerce_discount/edit_commerce_discount_rule" var="editCommerceDiscountRuleActionURL" />

<liferay-frontend:side-panel-content
	title='<%= LanguageUtil.get(request, "edit-rule") %>'
>
	<aui:form action="<%= editCommerceDiscountRuleActionURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (commerceDiscountRule == null) ? Constants.ADD : Constants.UPDATE %>" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="commerceDiscountId" type="hidden" value="<%= commerceDiscountRule.getCommerceDiscountId() %>" />
		<aui:input name="commerceDiscountRuleId" type="hidden" value="<%= commerceDiscountRule.getCommerceDiscountRuleId() %>" />
		<aui:input name="commerceDiscountRuleType" type="hidden" value="<%= commerceDiscountRule.getType() %>" />

		<aui:model-context bean="<%= commerceDiscountRule %>" model="<%= CommerceDiscountRule.class %>" />

		<div class="row">
			<div class="col-12">
				<commerce-ui:panel
					bodyClasses="flex-fill"
					title='<%= LanguageUtil.get(request, "details") %>'
				>
					<aui:input name="name" required="<%= true %>" />

					<aui:select disabled="<%= true %>" name="type" required="<%= true %>">

						<%
						for (CommerceDiscountRuleType commerceDiscountRuleType : commerceDiscountRuleDisplayContext.getCommerceDiscountRuleTypes()) {
							String key = commerceDiscountRuleType.getKey();
						%>

							<aui:option label="<%= commerceDiscountRuleType.getLabel(locale) %>" selected="<%= key.equals(commerceDiscountRule.getType()) %>" value="<%= key %>" />

						<%
						}
						%>

					</aui:select>
				</commerce-ui:panel>
			</div>
		</div>

		<div class="row">

			<%
			CommerceDiscountRuleTypeJSPContributor commerceDiscountRuleTypeJSPContributor = commerceDiscountRuleDisplayContext.getCommerceDiscountRuleTypeJSPContributor(type);
			%>

			<c:if test="<%= commerceDiscountRuleTypeJSPContributor != null %>">

				<%
				commerceDiscountRuleTypeJSPContributor.render(commerceDiscountRule.getCommerceDiscountId(), commerceDiscountRule.getCommerceDiscountRuleId(), request, PipingServletResponseFactory.createPipingServletResponse(pageContext));
				%>

			</c:if>
		</div>

		<aui:button-row>
			<aui:button cssClass="btn-lg" type="submit" value="save" />

			<aui:button cssClass="btn-lg" type="cancel" />
		</aui:button-row>
	</aui:form>
</liferay-frontend:side-panel-content>