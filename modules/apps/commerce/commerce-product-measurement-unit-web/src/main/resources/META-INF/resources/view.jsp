<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPMeasurementUnitsDisplayContext cpMeasurementUnitsDisplayContext = (CPMeasurementUnitsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<c:if test="<%= cpMeasurementUnitsDisplayContext.hasAddPermission() %>">
	<clay:navigation-bar
		inverted="<%= false %>"
		navigationItems="<%= cpMeasurementUnitsDisplayContext.getNavigationItems() %>"
	/>

	<clay:management-toolbar
		managementToolbarDisplayContext="<%= new CPMeasurementUnitsManagementToolbarDisplayContext(cpMeasurementUnitsDisplayContext, request, liferayPortletRequest, liferayPortletResponse) %>"
		propsTransformer="{CPMeasurementUnitsManagementToolbarPropsTransformer} from commerce-product-measurement-unit-web"
	/>

	<div class="container-fluid container-fluid-max-xl">
		<portlet:actionURL name="/cp_measurement_unit/edit_cp_measurement_unit" var="editCPMeasurementUnitActionURL" />

		<aui:form action="<%= editCPMeasurementUnitActionURL %>" method="post" name="fm">
			<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.DELETE %>" />
			<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

			<liferay-ui:search-container
				id="cpMeasurementUnits"
				searchContainer="<%= cpMeasurementUnitsDisplayContext.getSearchContainer() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.commerce.product.model.CPMeasurementUnit"
					keyPropertyName="CPMeasurementUnitId"
					modelVar="cpMeasurementUnit"
				>
					<liferay-ui:search-container-column-text
						cssClass="font-weight-bold important table-cell-expand"
						href='<%=
							PortletURLBuilder.createRenderURL(
								renderResponse
							).setMVCRenderCommandName(
								"/cp_measurement_unit/edit_cp_measurement_unit"
							).setParameter(
								"cpMeasurementUnitId", cpMeasurementUnit.getCPMeasurementUnitId()
							).setParameter(
								"type", cpMeasurementUnitsDisplayContext.getType()
							).buildPortletURL()
						%>'
						name="name"
						value="<%= HtmlUtil.escape(cpMeasurementUnit.getName(locale)) %>"
					/>

					<liferay-ui:search-container-column-text
						name="key"
						value="<%= HtmlUtil.escape(cpMeasurementUnit.getKey()) %>"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						name="ratio-to-primary"
						property="rate"
					/>

					<liferay-ui:search-container-column-text
						name="primary"
					>
						<c:choose>
							<c:when test="<%= cpMeasurementUnit.isPrimary() %>">
								<clay:icon
									cssClass="text-success"
									symbol="check"
								/>
							</c:when>
							<c:otherwise>
								<clay:icon
									cssClass="text-danger"
									symbol="times"
								/>
							</c:otherwise>
						</c:choose>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text
						property="priority"
					/>

					<liferay-ui:search-container-column-jsp
						cssClass="entry-action-column"
						path="/measurement_unit_action.jsp"
					/>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</aui:form>
	</div>

	<aui:script>
		function <portlet:namespace />deleteCPMeasurementUnits() {
			Liferay.Util.openConfirmModal({
				message:
					'<liferay-ui:message key="are-you-sure-you-want-to-delete-the-selected-measurement-units" />',
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						var form = window.document['<portlet:namespace />fm'];

						form['<portlet:namespace />deleteCPMeasurementUnitIds'].value =
							Liferay.Util.getCheckedCheckboxes(
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