<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceAvailabilityEstimateDisplayContext commerceAvailabilityEstimateDisplayContext = (CommerceAvailabilityEstimateDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<c:if test="<%= commerceAvailabilityEstimateDisplayContext.hasViewCommerceAvailabilityEstimatesPermission() %>">
	<clay:management-toolbar
		managementToolbarDisplayContext="<%= new CommerceAvailabilityEstimateManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, commerceAvailabilityEstimateDisplayContext.getSearchContainer()) %>"
		propsTransformer="{CommerceAvailabilityEstimateManagementToolbarPropsTransformer} from commerce-availability-estimate-web"
	/>

	<div class="container-fluid container-fluid-max-xl">
		<portlet:actionURL name="/commerce_availability_estimate/edit_commerce_availability_estimate" var="editCommerceAvailabilityEstimateActionURL" />

		<aui:form action="<%= editCommerceAvailabilityEstimateActionURL %>" method="post" name="fm">
			<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.DELETE %>" />
			<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
			<aui:input name="deleteCommerceAvailabilityEstimateIds" type="hidden" />

			<liferay-ui:search-container
				id="commerceAvailabilityEstimates"
				searchContainer="<%= commerceAvailabilityEstimateDisplayContext.getSearchContainer() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.commerce.model.CommerceAvailabilityEstimate"
					keyPropertyName="commerceAvailabilityEstimateId"
					modelVar="commerceAvailabilityEstimate"
				>
					<liferay-ui:search-container-column-text
						cssClass="font-weight-bold important table-cell-expand"
						href='<%=
							PortletURLBuilder.createRenderURL(
								renderResponse
							).setMVCRenderCommandName(
								"/commerce_availability_estimate/edit_commerce_availability_estimate"
							).setParameter(
								"commerceAvailabilityEstimateId", commerceAvailabilityEstimate.getCommerceAvailabilityEstimateId()
							).buildPortletURL()
						%>'
						name="title"
						value="<%= HtmlUtil.escape(commerceAvailabilityEstimate.getTitle(languageId)) %>"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						property="priority"
					/>

					<liferay-ui:search-container-column-date
						cssClass="table-cell-expand"
						name="modified-date"
						property="modifiedDate"
					/>

					<liferay-ui:search-container-column-jsp
						cssClass="entry-action-column"
						path="/availability_estimate_action.jsp"
					/>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</aui:form>
	</div>

	<aui:script>
		function <portlet:namespace />deleteCommerceAvailabilityEstimates() {
			Liferay.Util.openConfirmModal({
				message:
					'<liferay-ui:message key="are-you-sure-you-want-to-delete-the-selected-availability-estimates" />',
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						var form = window.document['<portlet:namespace />fm'];

						form[
							'<portlet:namespace />deleteCommerceAvailabilityEstimateIds'
						].value = Liferay.Util.getCheckedCheckboxes(
							form,
							'<portlet:namespace />allRowIds'
						);

						submitForm(form);
					}
				},
			});
		}
	</aui:script>
</c:if>