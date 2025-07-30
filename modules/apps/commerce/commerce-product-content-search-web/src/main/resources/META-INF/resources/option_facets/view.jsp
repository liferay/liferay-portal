<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPOptionsSearchFacetDisplayContext cpOptionsSearchFacetDisplayContext = (CPOptionsSearchFacetDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<c:choose>
	<c:when test="<%= !cpOptionsSearchFacetDisplayContext.hasCommerceChannel() %>">
		<div class="alert alert-info mx-auto">
			<liferay-ui:message key="this-site-does-not-have-a-channel" />
		</div>
	</c:when>
	<c:otherwise>

		<%
		List<Facet> facets = cpOptionsSearchFacetDisplayContext.getFacets();

		long companyId = company.getCompanyId();
		%>

		<c:choose>
			<c:when test="<%= !facets.isEmpty() %>">

				<%
				for (Facet facet : facets) {
					FacetCollector facetCollector = facet.getFacetCollector();

					List<TermCollector> termCollectors = facetCollector.getTermCollectors();
				%>

					<c:if test="<%= !termCollectors.isEmpty() %>">
						<aui:form action="#" method="post" name='<%= "fm_" + facet.getFieldName() %>'>
							<aui:input cssClass="facet-parameter-name" name="facet-parameter-name" type="hidden" value="<%= cpOptionsSearchFacetDisplayContext.getCPOptionKey(companyId, facet.getFieldName()) %>" />
							<aui:input cssClass="start-parameter-name" name="start-parameter-name" type="hidden" value="<%= cpOptionsSearchFacetDisplayContext.getPaginationStartParameterName() %>" />

							<liferay-ddm:template-renderer
								className="<%= CPOptionsSearchFacetDisplayContext.class.getName() %>"
								contextObjects='<%=
									HashMapBuilder.<String, Object>put(
										"companyId", companyId
									).put(
										"cpOptionsSearchFacetDisplayContext", cpOptionsSearchFacetDisplayContext
									).put(
										"fieldName", facet.getFieldName()
									).put(
										"name", liferayPortletResponse.getNamespace() + "term_" + facet.getFieldName()
									).put(
										"namespace", liferayPortletResponse.getNamespace()
									).put(
										"showFrequencies", GetterUtil.getBoolean(portletPreferences.getValue("frequenciesVisible", null), true)
									).put(
										"title", HtmlUtil.escape(cpOptionsSearchFacetDisplayContext.getCPOptionName(companyId, facet.getFieldId()))
									).build()
								%>'
								displayStyle='<%= portletPreferences.getValue("displayStyle", "") %>'
								displayStyleGroupId="<%= cpOptionsSearchFacetDisplayContext.getDisplayStyleGroupId() %>"
								entries="<%= cpOptionsSearchFacetDisplayContext.getTermDisplayContexts() %>"
							>

							<liferay-ui:panel-container
								extended="<%= true %>"
								id='<%= liferayPortletResponse.getNamespace() + "facetCPOptionsPanelContainer" %>'
								markupView="lexicon"
								persistState="<%= true %>"
							>
							<liferay-ui:panel
								collapsible="<%= true %>"
								cssClass="search-facet"
								id='<%= liferayPortletResponse.getNamespace() + "facetCPOptionsPanel" %>'
								markupView="lexicon"
								persistState="<%= true %>"
								title="<%= HtmlUtil.escape(cpOptionsSearchFacetDisplayContext.getCPOptionName(companyId, facet.getFieldId())) %>"
							>
								<c:if test="<%= cpOptionsSearchFacetDisplayContext.isShowClear(companyId, facet.getFieldName()) %>">
									<aui:button cssClass="btn-link btn-unstyled facet-clear-btn" onClick="Liferay.Search.FacetUtil.clearSelections(event);" value="clear" />
								</c:if>

							<aui:fieldset>
								<ul class="list-unstyled" data-qa-id="<%= HtmlUtil.escape(cpOptionsSearchFacetDisplayContext.getCPOptionName(companyId, facet.getFieldId())) %>">

								<%
								int i = 0;

								for (TermCollector termCollector : termCollectors) {
									i++;
								%>

								<li class="facet-value">
									<div class="custom-checkbox custom-control">
										<label for="<portlet:namespace />term_<%= facet.getFieldName() + i %>">
											<liferay-ui:csp>
												<input
													class="custom-control-input facet-term"
													data-term-id="<%= HtmlUtil.escapeAttribute(termCollector.getTerm()) %>"
													disabled
													id="<portlet:namespace />term_<%= facet.getFieldName() + i %>"
													name="<portlet:namespace />term_<%= facet.getFieldName() + i %>"
													onChange="Liferay.Search.FacetUtil.changeSelection(event);"
													type="checkbox"
													<%= cpOptionsSearchFacetDisplayContext.isCPOptionValueSelected(companyId, facet.getFieldName(), termCollector.getTerm()) ? "checked" : "" %>
												/>
											</liferay-ui:csp>

											<span class="custom-control-label term-name <%= cpOptionsSearchFacetDisplayContext.isCPOptionValueSelected(companyId, facet.getFieldName(), termCollector.getTerm()) ? "facet-term-selected" : "facet-term-unselected" %>">
												<span class="custom-control-label-text"><%= HtmlUtil.escape(termCollector.getTerm()) %></span>
											</span>

											<c:if test='<%= GetterUtil.getBoolean(portletPreferences.getValue("frequenciesVisible", null), true) %>'>
												<small class="term-count">
													(<%= termCollector.getFrequency() %>)
												</small>
											</c:if>
										</label>
									</div>
								</li>

								<%
								}
								%>

							</aui:fieldset>
							</liferay-ui:panel>
							</liferay-ui:panel-container>
							</liferay-ddm:template-renderer>
						</aui:form>
					</c:if>

				<%
				}
				%>

			</c:when>
			<c:otherwise>
				<div class="alert alert-info">
					<liferay-ui:message key="no-facets-were-found" />
				</div>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"namespace", liferayPortletResponse.getNamespace()
		).build()
	%>'
	module="{FacetUtil} from portal-search-web"
/>