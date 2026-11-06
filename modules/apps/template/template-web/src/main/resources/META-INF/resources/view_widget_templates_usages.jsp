<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
WidgetTemplatesTemplateViewUsagesDisplayContext widgetTemplatesTemplateViewUsagesDisplayContext = (WidgetTemplatesTemplateViewUsagesDisplayContext)request.getAttribute(WidgetTemplatesTemplateViewUsagesDisplayContext.class.getName());

DDMTemplate ddmTemplate = widgetTemplatesTemplateViewUsagesDisplayContext.getDDMTemplate();

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(widgetTemplatesTemplateViewUsagesDisplayContext.getRedirect());

renderResponse.setTitle(HtmlUtil.escape(ddmTemplate.getName(locale)));
%>

<clay:container-fluid
	cssClass="container-form-lg"
>
	<clay:row>
		<clay:col
			lg="3"
		>
			<div class="c-mb-3 h5 text-uppercase">
				<liferay-ui:message key="usages" />
			</div>

			<clay:vertical-nav
				verticalNavItems="<%= widgetTemplatesTemplateViewUsagesDisplayContext.getVerticalItemList() %>"
			/>
		</clay:col>

		<clay:col
			lg="9"
		>
			<clay:sheet
				size="full"
			>
				<h2 class="sheet-title">
					<clay:content-row
						verticalAlign="center"
					>
						<clay:content-col>
							<liferay-ui:message arguments="<%= widgetTemplatesTemplateViewUsagesDisplayContext.getUsagesCount() %>" key="all-x" />
						</clay:content-col>
					</clay:content-row>
				</h2>

				<liferay-ui:search-container
					searchContainer="<%= widgetTemplatesTemplateViewUsagesDisplayContext.getWidgetTemplatesUsagesSearchContainer() %>"
				>
					<liferay-ui:search-container-row
						className="com.liferay.portal.kernel.model.PortletPreferences"
						keyPropertyName="portletPreferencesId"
						modelVar="curPortletPreferences"
					>

						<%
						Layout curLayout = LayoutLocalServiceUtil.fetchLayout(curPortletPreferences.getPlid());
						%>

						<liferay-ui:search-container-column-text
							cssClass="table-title"
							name="name"
							value="<%= HtmlUtil.escape(widgetTemplatesTemplateViewUsagesDisplayContext.getDDMTemplateUsageName(curLayout)) %>"
						/>

						<liferay-ui:search-container-column-text
							name="type"
							translate="<%= true %>"
							value="<%= widgetTemplatesTemplateViewUsagesDisplayContext.getDDMTemplateUsageType(curLayout) %>"
						/>

						<%
						Portlet portlet = PortletLocalServiceUtil.fetchPortletById(company.getCompanyId(), curPortletPreferences.getPortletId());
						%>

						<c:if test="<%= (portlet != null) && portlet.isInstanceable() %>">
							<liferay-ui:search-container-column-text
								name="instance-id"
								value="<%= PortletIdCodec.decodeInstanceId(curPortletPreferences.getPortletId()) %>"
							/>
						</c:if>
					</liferay-ui:search-container-row>

					<liferay-ui:search-iterator
						displayStyle="list"
						markupView="lexicon"
					/>
				</liferay-ui:search-container>
			</clay:sheet>
		</clay:col>
	</clay:row>
</clay:container-fluid>