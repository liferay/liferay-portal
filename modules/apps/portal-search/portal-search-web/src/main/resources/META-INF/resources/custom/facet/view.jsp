<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/ddm" prefix="liferay-ddm" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.custom.facet.configuration.CustomFacetPortletInstanceConfiguration" %><%@
page import="com.liferay.portal.search.web.internal.custom.facet.display.context.CustomFacetCalendarDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.custom.facet.display.context.CustomFacetDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.custom.facet.portlet.CustomFacetPortlet" %><%@
page import="com.liferay.portal.search.web.internal.facet.display.context.BucketDisplayContext" %>

<portlet:defineObjects />

<%
CustomFacetDisplayContext customFacetDisplayContext = (CustomFacetDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));

CustomFacetPortletInstanceConfiguration customFacetPortletInstanceConfiguration = customFacetDisplayContext.getCustomFacetPortletInstanceConfiguration();

BucketDisplayContext customRangeBucketDisplayContext = customFacetDisplayContext.getCustomRangeBucketDisplayContext();
CustomFacetCalendarDisplayContext customFacetCalendarDisplayContext = customFacetDisplayContext.getCustomFacetCalendarDisplayContext();

String aggregationType = customFacetDisplayContext.getAggregationType();
%>

<c:choose>
	<c:when test="<%= customFacetDisplayContext.isRenderNothing() %>">
		<aui:input name="<%= HtmlUtil.escapeAttribute(customFacetDisplayContext.getParameterName()) %>" type="hidden" value="<%= customFacetDisplayContext.getParameterValue() %>" />
	</c:when>
	<c:otherwise>
		<aui:form action="#" autocomplete="off" method="get" name="fm">
			<aui:input cssClass="aggregation-type" name="aggregation-type" type="hidden" value="<%= customFacetDisplayContext.getAggregationType() %>" />
			<aui:input cssClass="facet-parameter-name" name="facet-parameter-name" type="hidden" value="<%= HtmlUtil.escapeAttribute(customFacetDisplayContext.getParameterName()) %>" />
			<aui:input name="start-parameter-name" type="hidden" value="<%= customFacetDisplayContext.getPaginationStartParameterName() %>" />

			<liferay-ddm:template-renderer
				className="<%= CustomFacetPortlet.class.getName() %>"
				contextObjects='<%=
					HashMapBuilder.<String, Object>put(
						"customFacetCalendarDisplayContext", customFacetCalendarDisplayContext
					).put(
						"customFacetDisplayContext", customFacetDisplayContext
					).put(
						"customRangeBucketDisplayContext", customRangeBucketDisplayContext
					).put(
						"namespace", liferayPortletResponse.getNamespace()
					).build()
				%>'
				displayStyle="<%= customFacetPortletInstanceConfiguration.displayStyle() %>"
				displayStyleGroupId="<%= customFacetDisplayContext.getDisplayStyleGroupId() %>"
				entries="<%= customFacetDisplayContext.getBucketDisplayContexts() %>"
			>
				<liferay-ui:panel-container
					extended="<%= true %>"
					id='<%= liferayPortletResponse.getNamespace() + "facetCustomPanelContainer" %>'
					markupView="lexicon"
					persistState="<%= true %>"
				>
					<liferay-ui:panel
						collapsible="<%= true %>"
						cssClass="search-facet"
						id='<%= liferayPortletResponse.getNamespace() + "facetCustomPanel" %>'
						markupView="lexicon"
						persistState="<%= true %>"
						title="<%= customFacetDisplayContext.getDisplayCaption() %>"
					>
						<c:if test="<%= !customFacetDisplayContext.isNothingSelected() %>">
							<clay:button
								cssClass="btn-unstyled c-mb-4 facet-clear-btn"
								displayType="link"
								id='<%= liferayPortletResponse.getNamespace() + "facetCustomClear" %>'
								onClick="Liferay.Search.FacetUtil.clearSelections(event);"
							>
								<strong><liferay-ui:message key="clear" /></strong>

								<span class="sr-only">
									<liferay-ui:message arguments="custom-facet-portlet-instance-configuration-name" key="x-filter" />
								</span>
							</clay:button>
						</c:if>

						<ul class="list-unstyled">

							<%
							int i = 0;

							for (BucketDisplayContext bucketDisplayContext : customFacetDisplayContext.getBucketDisplayContexts()) {
								i++;
							%>

								<li class="facet-value">
									<div class="custom-checkbox custom-control">
										<label class="facet-checkbox-label" for="<portlet:namespace />term_<%= i %>">
											<liferay-ui:csp>
												<input class="custom-control-input facet-term" data-term-id="<%= HtmlUtil.escapeAttribute(bucketDisplayContext.getBucketText()) %>" disabled id="<portlet:namespace />term_<%= i %>" name="<portlet:namespace />term_<%= i %>" onChange="Liferay.Search.FacetUtil.changeSelection(event);" type="checkbox" <%= bucketDisplayContext.isSelected() ? "checked" : StringPool.BLANK %>
												/>
											</liferay-ui:csp>

											<span class="custom-control-label term-name <%= bucketDisplayContext.isSelected() ? "facet-term-selected" : "facet-term-unselected" %>">
												<span class="custom-control-label-text">
													<c:choose>
														<c:when test="<%= bucketDisplayContext.isSelected() %>">
															<strong><liferay-ui:message key="<%= HtmlUtil.escape(bucketDisplayContext.getBucketText()) %>" /></strong>
														</c:when>
														<c:otherwise>
															<liferay-ui:message key="<%= HtmlUtil.escape(bucketDisplayContext.getBucketText()) %>" />
														</c:otherwise>
													</c:choose>
												</span>
											</span>

											<c:if test="<%= bucketDisplayContext.isFrequencyVisible() %>">
												<small class="term-count">
													(<%= bucketDisplayContext.getFrequency() %>)
												</small>
											</c:if>
										</label>
									</div>
								</li>

							<%
							}
							%>

							<c:if test="<%= customFacetDisplayContext.isShowInputRange() %>">
								<c:if test='<%= aggregationType.equals("dateRange") || aggregationType.equals("range") %>'>
									<li class="facet-value">
										<div class="custom-checkbox custom-control">
											<label class="facet-checkbox-label" for="<portlet:namespace /><%= customRangeBucketDisplayContext.getBucketText() %>">
												<input
													class="custom-control-input facet-term"
													data-term-id="<%= HtmlUtil.escapeAttribute(customRangeBucketDisplayContext.getBucketText()) %>"
													disabled
													id="<portlet:namespace /><%= customRangeBucketDisplayContext.getBucketText() %>"
													name="<portlet:namespace /><%= customRangeBucketDisplayContext.getBucketText() %>"
													type="checkbox"
													<%= customRangeBucketDisplayContext.isSelected() ? "checked" : StringPool.BLANK %>
												/>

												<aui:script>
													document.getElementById(
														'<portlet:namespace /><%= customRangeBucketDisplayContext.getBucketText() %>'
													).onclick = function (event) {
														event.preventDefault();

														if (
															'<%= customFacetDisplayContext.getAggregationType() %>' == 'dateRange'
														) {
															Liferay.Search.FacetUtil.changeSelection(event);
														}

														if ('<%= customFacetDisplayContext.getAggregationType() %>' == 'range') {
															const customRangeElement = document.getElementById(
																'<portlet:namespace />customRange'
															);

															if (
																customRangeElement &&
																customRangeElement.classList.contains('hide')
															) {
																customRangeElement.classList.remove('hide');
															}
															else if (Liferay.Search.FacetUtil.isCustomRangeValid(event)) {
																Liferay.Search.FacetUtil.changeSelection(event);
															}
														}
													};
												</aui:script>

												<span class="custom-control-label term-name <%= customRangeBucketDisplayContext.isSelected() ? "facet-term-selected" : "facet-term-unselected" %>">
													<span class="custom-control-label-text">
														<c:choose>
															<c:when test="<%= customRangeBucketDisplayContext.isSelected() %>">
																<strong><liferay-ui:message key="<%= HtmlUtil.escape(customRangeBucketDisplayContext.getBucketText()) %>" /></strong>
															</c:when>
															<c:otherwise>
																<liferay-ui:message key="<%= HtmlUtil.escape(customRangeBucketDisplayContext.getBucketText()) %>" />
															</c:otherwise>
														</c:choose>
													</span>
												</span>

												<c:if test="<%= customRangeBucketDisplayContext.isSelected() %>">
													<small class="term-count">
														(<%= customRangeBucketDisplayContext.getFrequency() %>)
													</small>
												</c:if>
											</label>
										</div>
									</li>
								</c:if>

								<c:if test='<%= aggregationType.equals("dateRange") %>'>
									<div class="<%= !customFacetCalendarDisplayContext.isSelected() ? "hide" : StringPool.BLANK %> date-custom-range" id="<portlet:namespace />customRange">
										<clay:col
											id='<%= liferayPortletResponse.getNamespace() + "customRangeFrom" %>'
											md="6"
										>
											<aui:field-wrapper label="from" name="fromInput">
												<liferay-ui:input-date
													cssClass="custom-range-input-date-from"
													dayParam="fromDay"
													dayValue="<%= customFacetCalendarDisplayContext.getFromDayValue() %>"
													disabled="<%= false %>"
													firstDayOfWeek="<%= customFacetCalendarDisplayContext.getFromFirstDayOfWeek() %>"
													monthParam="fromMonth"
													monthValue="<%= customFacetCalendarDisplayContext.getFromMonthValue() %>"
													name="fromInput"
													yearParam="fromYear"
													yearValue="<%= customFacetCalendarDisplayContext.getFromYearValue() %>"
												/>
											</aui:field-wrapper>
										</clay:col>

										<clay:col
											id='<%= liferayPortletResponse.getNamespace() + "customRangeTo" %>'
											md="6"
										>
											<aui:field-wrapper label="to" name="toInput">
												<liferay-ui:input-date
													cssClass="custom-range-input-date-to"
													dayParam="toDay"
													dayValue="<%= customFacetCalendarDisplayContext.getToDayValue() %>"
													disabled="<%= false %>"
													firstDayOfWeek="<%= customFacetCalendarDisplayContext.getToFirstDayOfWeek() %>"
													monthParam="toMonth"
													monthValue="<%= customFacetCalendarDisplayContext.getToMonthValue() %>"
													name="toInput"
													yearParam="toYear"
													yearValue="<%= customFacetCalendarDisplayContext.getToYearValue() %>"
												/>
											</aui:field-wrapper>
										</clay:col>

										<clay:button
											aria-label='<%= LanguageUtil.get(request, "search") %>'
											cssClass="custom-range-filter-button"
											disabled="<%= customFacetCalendarDisplayContext.isRangeBackwards() %>"
											displayType="secondary"
											id='<%= liferayPortletResponse.getNamespace() + "searchCustomRangeButton" %>'
											label="search"
											name='<%= liferayPortletResponse.getNamespace() + "searchCustomRangeButton" %>'
										/>
									</div>
								</c:if>

								<c:if test='<%= aggregationType.equals("range") %>'>
									<div class="<%= !customRangeBucketDisplayContext.isSelected() ? "hide" : StringPool.BLANK %> date-custom-range" id="<portlet:namespace />customRange">
										<div class="col-md-6" id="<portlet:namespace />customRangeFrom">
											<aui:field-wrapper>
												<aui:input id="fromInput" label="from" name="fromInput" type="number" value="<%= customFacetDisplayContext.getFromParameterValue() %>" />
											</aui:field-wrapper>
										</div>

										<div class="col-md-6" id="<portlet:namespace />customRangeTo">
											<aui:field-wrapper>
												<aui:input id="toInput" label="to" name="toInput" type="number" value="<%= customFacetDisplayContext.getToParameterValue() %>" />
											</aui:field-wrapper>
										</div>

										<clay:button
											aria-label='<%= LanguageUtil.get(request, "search") %>'
											cssClass="custom-range-filter-button"
											disabled="<%= (customFacetDisplayContext.getToParameterValue() == null) || (customFacetDisplayContext.getFromParameterValue() == null) || (Float.parseFloat(customFacetDisplayContext.getToParameterValue()) < Float.parseFloat(customFacetDisplayContext.getFromParameterValue())) %>"
											displayType="secondary"
											id='<%= liferayPortletResponse.getNamespace() + "searchCustomRangeButton" %>'
											label="search"
											name='<%= liferayPortletResponse.getNamespace() + "searchCustomRangeButton" %>'
										/>
									</div>
								</c:if>
							</c:if>
						</ul>
					</liferay-ui:panel>
				</liferay-ui:panel-container>
			</liferay-ddm:template-renderer>
		</aui:form>
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

<c:if test='<%= customFacetDisplayContext.isShowInputRange() && (aggregationType.equals("dateRange") || aggregationType.equals("range")) %>'>
	<aui:script use="liferay-search-custom-range-facet">
		new Liferay.Search.CustomRangeFacet({
			aggregationType: '<%= customFacetDisplayContext.getAggregationType() %>',
			form: A.one('#<portlet:namespace />fm'),
			fromInputName: '<portlet:namespace />fromInput',
			namespace: '<portlet:namespace />',
			parameterName:
				'<%= HtmlUtil.escapeAttribute(customFacetDisplayContext.getParameterName()) %>',
			searchCustomRangeButton: A.one(
				'#<portlet:namespace />searchCustomRangeButton'
			),
			searchCustomRangeToggleName:
				'<portlet:namespace /><%= customRangeBucketDisplayContext.getBucketText() %>',
			toInputName: '<portlet:namespace />toInput',
		});
	</aui:script>
</c:if>