<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ProductionReadinessDisplayContext productionReadinessDisplayContext = new ProductionReadinessDisplayContext(renderRequest);

Map<String, List<ProductionReadinessDisplayContext.RuleResult>> groupedRuleResults = productionReadinessDisplayContext.getGroupedRuleResults();

String filter = productionReadinessDisplayContext.getFilter();
%>

<div class="container-fluid container-fluid-max-xl mt-3">
	<div class="row mb-4">
		<div class="col-md-6">
			<h6 class="text-secondary text-uppercase"><liferay-ui:message key="summary" /></h6>
			<div class="d-flex align-items-baseline">
				<span class="display-4 mr-2"><%= productionReadinessDisplayContext.getTotalCount() %></span>
				<span class="text-secondary mr-4"><liferay-ui:message key="validations" /></span>
				<span class="h2 mr-2"><%= productionReadinessDisplayContext.getPassedCount() %></span>
				<span class="text-success mr-4"><liferay-ui:message key="passed" /></span>
				<span class="h2 mr-2"><%= productionReadinessDisplayContext.getFailedCount() %></span>
				<span class="text-danger"><liferay-ui:message key="failed" /></span>
			</div>
		</div>
		<div class="col-md-6">
			<h6 class="text-secondary text-uppercase"><liferay-ui:message key="filters" /></h6>
			<div class="btn-group">
				<portlet:renderURL var="allURL">
					<portlet:param name="tabs1" value="production-readiness" />
					<portlet:param name="filter" value="all" />
				</portlet:renderURL>
				<a class="btn btn-sm btn-outline-secondary <%= filter.equals("all") ? "active" : "" %>" href="<%= allURL %>">
					<liferay-ui:message key="all-validations" />
				</a>

				<portlet:renderURL var="passedURL">
					<portlet:param name="tabs1" value="production-readiness" />
					<portlet:param name="filter" value="passed" />
				</portlet:renderURL>
				<a class="btn btn-sm btn-outline-secondary <%= filter.equals("passed") ? "active" : "" %>" href="<%= passedURL %>">
					<clay:icon symbol="circle" /> <liferay-ui:message key="passed" />
				</a>

				<portlet:renderURL var="failedURL">
					<portlet:param name="tabs1" value="production-readiness" />
					<portlet:param name="filter" value="failed" />
				</portlet:renderURL>
				<a class="btn btn-sm btn-outline-secondary <%= filter.equals("failed") ? "active" : "" %>" href="<%= failedURL %>">
					<clay:icon symbol="circle" /> <liferay-ui:message key="failed" />
				</a>

				<portlet:renderURL var="ignoredURL">
					<portlet:param name="tabs1" value="production-readiness" />
					<portlet:param name="filter" value="ignored" />
				</portlet:renderURL>
				<a class="btn btn-sm btn-outline-secondary <%= filter.equals("ignored") ? "active" : "" %>" href="<%= ignoredURL %>">
					<clay:icon symbol="circle" /> <liferay-ui:message key="ignored" />
				</a>
			</div>
		</div>
	</div>

	<c:forEach items="<%= groupedRuleResults %>" var="entry" varStatus="loop">
		<clay:panel
			collapsable="<%= true %>"
			displayTitle="${entry.key}"
		>
			<div class="table-responsive">
				<table class="table table-autofit">
					<tbody>
						<c:forEach items="${entry.value}" var="ruleResult">
							<%
							ProductionReadinessDisplayContext.RuleResult ruleResult = (ProductionReadinessDisplayContext.RuleResult)pageContext.getAttribute("ruleResult");
							Result result = ruleResult.getResult();
							%>

							<tr class="<%= ruleResult.isIgnored() ? "opacity-50" : "" %>">
								<td class="table-column-content">
									<div class="d-flex align-items-center">
										<div class="mr-3">
											<c:choose>
												<c:when test="<%= ruleResult.isIgnored() %>">
													<clay:sticker displayType="secondary" shape="circle">
														<clay:icon symbol="circle" />
													</clay:sticker>
												</c:when>
												<c:when test="<%= result.getStatus() == Result.Status.PASS %>">
													<clay:sticker displayType="success" shape="circle">
														<clay:icon symbol="circle" />
													</clay:sticker>
												</c:when>
												<c:otherwise>
													<clay:sticker displayType="danger" shape="circle">
														<clay:icon symbol="exclamation-full" />
													</clay:sticker>
												</c:otherwise>
											</c:choose>
										</div>
										<div>
											<strong><liferay-ui:message key="<%= result.getMessageKey() %>" arguments="<%= result.getMessageParameters() %>" /></strong>
											<c:if test="<%= result.getStatus() == Result.Status.FAIL && !ruleResult.isIgnored() %>">
												<div class="text-danger small">
													<liferay-ui:message key="recommended-value" />: <%= result.getRecommendedValue() %>
												</div>
											</c:if>
										</div>
									</div>
								</td>
								<td class="table-column-text text-right">
									<c:choose>
										<c:when test="<%= ruleResult.isIgnored() %>">
											<clay:label displayType="secondary" label="ignored" />
										</c:when>
										<c:when test="<%= result.getStatus() == Result.Status.PASS %>">
											<clay:label displayType="success" label="pass" />
										</c:when>
										<c:otherwise>
											<clay:label displayType="danger" label="failed" />
										</c:otherwise>
									</c:choose>
								</td>
								<td class="table-column-text text-right">
									<portlet:actionURL name="/server_admin/edit_production_readiness_ignore" var="ignoreURL">
										<portlet:param name="ruleKey" value="<%= ruleResult.getRule().getKey() %>" />
										<portlet:param name="ignore" value="<%= String.valueOf(!ruleResult.isIgnored()) %>" />
										<portlet:param name="redirect" value="<%= currentURL %>" />
									</portlet:actionURL>

									<a class="text-secondary" href="<%= ignoreURL %>">
										<clay:icon symbol="<%= ruleResult.isIgnored() ? "view" : "hidden" %>" />
									</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</clay:panel>
	</c:forEach>
</div>