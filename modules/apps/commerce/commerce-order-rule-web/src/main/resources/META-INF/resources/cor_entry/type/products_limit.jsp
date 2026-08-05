<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
COREntryDisplayContext corEntryDisplayContext = (COREntryDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<div class="row">
	<div class="col">
		<commerce-ui:panel
			bodyClasses="flex-fill"
			title='<%= LanguageUtil.get(request, "configuration") %>'
		>
			<aui:input label="quantity" name='<%= "type--settings--" + COREntryConstants.TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_QUANTITY + "--" %>' type="text" value="<%= corEntryDisplayContext.getQuantity() %>">
				<aui:validator name="number" />
			</aui:input>

			<aui:input id='<%= "type--settings--" + COREntryConstants.TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_EXTERNAL_REFERENCE_CODES + "--" %>' name='<%= "type--settings--" + COREntryConstants.TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_EXTERNAL_REFERENCE_CODES + "--" %>' type="hidden" value="<%= corEntryDisplayContext.getCProductExternalReferenceCodes() %>" />

			<div class="row">
				<div class="col-12 pt-4">
					<div id="item-finder-root"></div>

					<liferay-frontend:component
						context='<%=
							HashMapBuilder.<String, Object>put(
								"dataSetId", COREntryFDSNames.COR_ENTRY_PRODUCTS_LIMITS
							).put(
								"rootPortletId", portletDisplay.getRootPortletId()
							).put(
								"typeSettingsInputId", "type--settings--" + COREntryConstants.TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_EXTERNAL_REFERENCE_CODES + "--"
							).put(
								"workflowAction", WorkflowConstants.ACTION_PUBLISH
							).build()
						%>'
						module="{productsLimit} from commerce-order-rule-web"
					/>
				</div>

				<div class="col-12">
					<commerce-ui:panel
						bodyClasses="p-0"
						title='<%= LanguageUtil.get(request, "products") %>'
					>
						<frontend-data-set:classic-display
							contextParams='<%=
								HashMapBuilder.<String, String>put(
									"corEntryId", String.valueOf(corEntryDisplayContext.getCOREntryId())
								).build()
							%>'
							dataProviderKey="<%= COREntryFDSNames.COR_ENTRY_PRODUCTS_LIMITS %>"
							formName="fm"
							id="<%= COREntryFDSNames.COR_ENTRY_PRODUCTS_LIMITS %>"
							showSearch="<%= false %>"
						/>
					</commerce-ui:panel>
				</div>
			</div>
		</commerce-ui:panel>
	</div>
</div>