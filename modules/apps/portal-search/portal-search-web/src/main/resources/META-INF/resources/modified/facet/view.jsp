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
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.facet.display.context.BucketDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.modified.facet.configuration.ModifiedFacetPortletInstanceConfiguration" %><%@
page import="com.liferay.portal.search.web.internal.modified.facet.display.context.ModifiedFacetCalendarDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.modified.facet.display.context.ModifiedFacetDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.modified.facet.portlet.ModifiedFacetPortlet" %>

<portlet:defineObjects />

<%
ModifiedFacetDisplayContext modifiedFacetDisplayContext = (ModifiedFacetDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));

if (modifiedFacetDisplayContext.isRenderNothing()) {
	return;
}

BucketDisplayContext customRangeBucketDisplayContext = modifiedFacetDisplayContext.getCustomRangeBucketDisplayContext();
ModifiedFacetCalendarDisplayContext modifiedFacetCalendarDisplayContext = modifiedFacetDisplayContext.getModifiedFacetCalendarDisplayContext();
ModifiedFacetPortletInstanceConfiguration modifiedFacetPortletInstanceConfiguration = modifiedFacetDisplayContext.getModifiedFacetPortletInstanceConfiguration();
%>

<c:if test="<%= !modifiedFacetDisplayContext.isRenderNothing() %>">
	<aui:form action="#" autocomplete="off" method="get" name="fm">
		<aui:input name="inputFacetName" type="hidden" value="modified" />
		<aui:input cssClass="facet-parameter-name" name="facet-parameter-name" type="hidden" value="<%= HtmlUtil.escapeAttribute(modifiedFacetDisplayContext.getParameterName()) %>" />
		<aui:input name="start-parameter-name" type="hidden" value="<%= modifiedFacetDisplayContext.getPaginationStartParameterName() %>" />

		<liferay-ddm:template-renderer
			className="<%= ModifiedFacetPortlet.class.getName() %>"
			contextObjects='<%=
				HashMapBuilder.<String, Object>put(
					"customRangeBucketDisplayContext", customRangeBucketDisplayContext
				).put(
					"customRangeModifiedFacetTermDisplayContext", customRangeBucketDisplayContext
				).put(
					"modifiedFacetCalendarDisplayContext", modifiedFacetCalendarDisplayContext
				).put(
					"modifiedFacetDisplayContext", modifiedFacetDisplayContext
				).put(
					"namespace", liferayPortletResponse.getNamespace()
				).build()
			%>'
			displayStyle="<%= modifiedFacetPortletInstanceConfiguration.displayStyle() %>"
			displayStyleGroupId="<%= modifiedFacetDisplayContext.getDisplayStyleGroupId() %>"
			entries="<%= modifiedFacetDisplayContext.getBucketDisplayContexts() %>"
		>
			<liferay-ui:panel-container
				extended="<%= true %>"
				id='<%= liferayPortletResponse.getNamespace() + "facetModifiedPanelContainer" %>'
				markupView="lexicon"
				persistState="<%= true %>"
			>
				<liferay-ui:panel
					collapsible="<%= true %>"
					cssClass="search-facet"
					id='<%= liferayPortletResponse.getNamespace() + "facetModifiedPanel" %>'
					markupView="lexicon"
					persistState="<%= true %>"
					title="last-modified"
				>
					<c:if test="<%= !modifiedFacetDisplayContext.isNothingSelected() %>">
						<clay:button
							cssClass="btn-unstyled c-mb-4 facet-clear-btn"
							displayType="link"
							id='<%= liferayPortletResponse.getNamespace() + "facetModifiedClear" %>'
							onClick="Liferay.Search.FacetUtil.clearSelections(event);"
						>
							<strong><liferay-ui:message key="clear" /></strong>

							<span class="sr-only">
								<liferay-ui:message arguments="modified-facet-portlet-instance-configuration-name" key="x-filter" />
							</span>
						</clay:button>
					</c:if>

					<ul class="list-unstyled modified">

						<%
						for (BucketDisplayContext bucketDisplayContext : modifiedFacetDisplayContext.getBucketDisplayContexts()) {
						%>

							<li class="facet-value">
								<a href="<%= HtmlUtil.escapeHREF(bucketDisplayContext.getFilterValue()) %>">
									<span class="term-name <%= bucketDisplayContext.isSelected() ? "facet-term-selected" : "facet-term-unselected" %>">
										<c:choose>
											<c:when test="<%= bucketDisplayContext.isSelected() %>">
												<strong><liferay-ui:message key="<%= HtmlUtil.escape(bucketDisplayContext.getBucketText()) %>" /></strong>
											</c:when>
											<c:otherwise>
												<liferay-ui:message key="<%= HtmlUtil.escape(bucketDisplayContext.getBucketText()) %>" />
											</c:otherwise>
										</c:choose>
									</span>

									<c:if test="<%= bucketDisplayContext.isFrequencyVisible() %>">
										<small class="term-count">
											(<%= bucketDisplayContext.getFrequency() %>)
										</small>
									</c:if>
								</a>
							</li>

						<%
						}
						%>

						<li class="facet-value">
							<a href="<%= HtmlUtil.escapeHREF(customRangeBucketDisplayContext.getFilterValue()) %>" id="<portlet:namespace /><%= customRangeBucketDisplayContext.getBucketText() %>-toggleLink">
								<span class="term-name <%= customRangeBucketDisplayContext.isSelected() ? "facet-term-selected" : "facet-term-unselected" %>">
									<c:choose>
										<c:when test="<%= customRangeBucketDisplayContext.isSelected() %>">
											<strong><liferay-ui:message key="<%= HtmlUtil.escape(customRangeBucketDisplayContext.getBucketText()) %>" />&hellip;</strong>
										</c:when>
										<c:otherwise>
											<liferay-ui:message key="<%= HtmlUtil.escape(customRangeBucketDisplayContext.getBucketText()) %>" />&hellip;
										</c:otherwise>
									</c:choose>
								</span>

								<c:if test="<%= customRangeBucketDisplayContext.isSelected() %>">
									<small class="term-count">
										(<%= customRangeBucketDisplayContext.getFrequency() %>)
									</small>
								</c:if>
							</a>
						</li>
						<li class="<%= !modifiedFacetCalendarDisplayContext.isSelected() ? "hide" : StringPool.BLANK %> modified-custom-range" id="<portlet:namespace />customRange">
							<clay:col
								id='<%= liferayPortletResponse.getNamespace() + "customRangeFrom" %>'
								md="6"
							>
								<aui:field-wrapper label="from" name="fromInput">
									<liferay-ui:input-date
										cssClass="modified-facet-custom-range-input-date-from"
										dayParam="fromDay"
										dayValue="<%= modifiedFacetCalendarDisplayContext.getFromDayValue() %>"
										disabled="<%= false %>"
										firstDayOfWeek="<%= modifiedFacetCalendarDisplayContext.getFromFirstDayOfWeek() %>"
										monthParam="fromMonth"
										monthValue="<%= modifiedFacetCalendarDisplayContext.getFromMonthValue() %>"
										name="fromInput"
										yearParam="fromYear"
										yearValue="<%= modifiedFacetCalendarDisplayContext.getFromYearValue() %>"
									/>
								</aui:field-wrapper>
							</clay:col>

							<clay:col
								id='<%= liferayPortletResponse.getNamespace() + "customRangeTo" %>'
								md="6"
							>
								<aui:field-wrapper label="to" name="toInput">
									<liferay-ui:input-date
										cssClass="modified-facet-custom-range-input-date-to"
										dayParam="toDay"
										dayValue="<%= modifiedFacetCalendarDisplayContext.getToDayValue() %>"
										disabled="<%= false %>"
										firstDayOfWeek="<%= modifiedFacetCalendarDisplayContext.getToFirstDayOfWeek() %>"
										monthParam="toMonth"
										monthValue="<%= modifiedFacetCalendarDisplayContext.getToMonthValue() %>"
										name="toInput"
										yearParam="toYear"
										yearValue="<%= modifiedFacetCalendarDisplayContext.getToYearValue() %>"
									/>
								</aui:field-wrapper>
							</clay:col>

							<clay:button
								cssClass="modified-facet-custom-range-filter-button"
								disabled="<%= modifiedFacetCalendarDisplayContext.isRangeBackwards() %>"
								displayType="secondary"
								id='<%= liferayPortletResponse.getNamespace() + "searchCustomRangeButton" %>'
								label="search"
								name='<%= liferayPortletResponse.getNamespace() + "searchCustomRangeButton" %>'
							/>
						</li>
					</ul>
				</liferay-ui:panel>
			</liferay-ui:panel-container>
		</liferay-ddm:template-renderer>
	</aui:form>

	<liferay-frontend:component
		context='<%=
			HashMapBuilder.<String, Object>put(
				"namespace", liferayPortletResponse.getNamespace()
			).build()
		%>'
		module="{FacetUtil} from portal-search-web"
	/>

	<aui:script use="liferay-search-custom-range-facet">
		new Liferay.Search.CustomRangeFacet({
			form: A.one('#<portlet:namespace />fm'),
			fromInputName: '<portlet:namespace />fromInput',
			namespace: '<portlet:namespace />',
			parameterName: 'modified',
			searchCustomRangeButton: A.one(
				'#<portlet:namespace />searchCustomRangeButton'
			),
			toInputName: '<portlet:namespace />toInput',
		});
	</aui:script>
</c:if>