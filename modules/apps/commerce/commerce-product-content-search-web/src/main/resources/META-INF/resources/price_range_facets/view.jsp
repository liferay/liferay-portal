<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPPriceRangeFacetsDisplayContext cpPriceRangeFacetsDisplayContext = (CPPriceRangeFacetsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<c:choose>
	<c:when test="<%= !cpPriceRangeFacetsDisplayContext.hasCommerceChannel() && !cpPriceRangeFacetsDisplayContext.isStagingEnabled() %>">
		<div class="alert alert-info mx-auto">
		<liferay-ui:message key="this-site-does-not-have-a-channel" />
		</div>
	</c:when>
	<c:otherwise>

				<c:choose>
					<c:when test="<%= cpPriceRangeFacetsDisplayContext.isFacetVisible() %>">

						<%
						Facet facet = cpPriceRangeFacetsDisplayContext.getFacet();

						String max = ParamUtil.getString(PortalUtil.getOriginalServletRequest(request), "max");

						double maxDouble = ParamUtil.getDouble(PortalUtil.getOriginalServletRequest(request), "max");

						if ((maxDouble == Double.MAX_VALUE) || (maxDouble == 0)) {
							max = StringPool.BLANK;
						}

						String min = StringPool.BLANK;

						double minDouble = ParamUtil.getDouble(PortalUtil.getOriginalServletRequest(request), "min");

						if (minDouble != 0) {
							min = ParamUtil.getString(PortalUtil.getOriginalServletRequest(request), "min");
						}
						%>

						<liferay-ui:panel-container
							extended="<%= true %>"
							markupView="lexicon"
							persistState="<%= true %>"
						>
							<liferay-ui:panel
								collapsible="<%= true %>"
								cssClass="search-facet"
								markupView="lexicon"
								persistState="<%= true %>"
								title="price-range"
							>

						<%
						FacetCollector facetCollector = facet.getFacetCollector();

						List<TermCollector> termCollectors = facetCollector.getTermCollectors();
						%>

						<c:if test="<%= !termCollectors.isEmpty() %>">
							<aui:form action="#" method="post" name='<%= "fm_" + facet.getFieldName() %>'>
								<aui:input cssClass="facet-parameter-name" name="facet-parameter-name" type="hidden" value="<%= facet.getFieldName() %>" />
								<aui:input cssClass="start-parameter-name" name="start-parameter-name" type="hidden" value="<%= cpPriceRangeFacetsDisplayContext.getPaginationStartParameterName() %>" />

								<c:if test="<%= cpPriceRangeFacetsDisplayContext.isShowClear(facet.getFieldName()) %>">
									<aui:button cssClass="btn-link btn-unstyled facet-clear-btn" onClick="Liferay.Search.FacetUtil.clearSelections(event);" value="clear" />
								</c:if>

								<aui:fieldset>
									<ul class="list-unstyled">

									<%
									int i = 0;

									for (TermCollector termCollector : termCollectors) {
										i++;
									%>

									<c:if test="<%= termCollector.getFrequency() > 0 %>">
										<li class="facet-value">
											<div class="custom-checkbox custom-control">
												<label class="facet-checkbox-label" for="<portlet:namespace />term_<%= facet.getFieldName() + i %>">
													<liferay-ui:csp>
														<input
															class="custom-control-input facet-term"
															data-term-id="<%= HtmlUtil.escapeAttribute(termCollector.getTerm()) %>"
															disabled
															id="<portlet:namespace />term_<%= facet.getFieldName() + i %>"
															name="<portlet:namespace />term_<%= facet.getFieldName() + i %>"
															onChange="Liferay.Search.FacetUtil.changeSelection(event);"
															type="checkbox"
															<%= cpPriceRangeFacetsDisplayContext.isCPPriceRangeValueSelected(facet.getFieldName(), termCollector.getTerm()) ? "checked" : "" %>
														/>
													</liferay-ui:csp>

													<span class="custom-control-label term-name <%= cpPriceRangeFacetsDisplayContext.isCPPriceRangeValueSelected(facet.getFieldName(), termCollector.getTerm()) ? "facet-term-selected" : "facet-term-unselected" %>">
														<span class="custom-control-label-text"><%= cpPriceRangeFacetsDisplayContext.getPriceRangeLabel(termCollector.getTerm()) %></span>
													</span>

													<small class="term-count">
														(<%= termCollector.getFrequency() %>)
													</small>
												</label>
											</div>
										</li>
									</c:if>

									<%
									}
									%>

								</aui:fieldset>
							</aui:form>
						</c:if>

						<c:if test="<%= cpPriceRangeFacetsDisplayContext.showInputRange() %>">
							<div class="ml-0 mt-3 row">
								<aui:input cssClass="price-range-input" label="<%= StringPool.BLANK %>" min="0" name="minimum" prefix="<%= HtmlUtil.escape(cpPriceRangeFacetsDisplayContext.getCurrentCommerceCurrencySymbol()) %>" type="number" value="<%= min %>" wrapperCssClass="col-md-5 price-range-input-wrapper" />

								<span class="mt-auto price-range-separator text-center">-</span>

								<aui:input cssClass="price-range-input" label="<%= StringPool.BLANK %>" name="maximum" prefix="<%= HtmlUtil.escape(cpPriceRangeFacetsDisplayContext.getCurrentCommerceCurrencySymbol()) %>" type="number" value="<%= max %>" wrapperCssClass="col-md-5 price-range-input-wrapper" />

								<div class="col-md-3 ml-2 p-0">
									<button class="btn btn-secondary price-range-btn" id="<portlet:namespace />priceRangeButton"><liferay-ui:message key="go" /></button>
								</div>
							</div>
						</c:if>
			</liferay-ui:panel>
		</liferay-ui:panel-container>
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

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"maxValue", Double.MAX_VALUE
		).build()
	%>'
	module="{priceRangeFacetsView} from commerce-product-content-search-web"
/>