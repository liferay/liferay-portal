<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.search.admin.web.internal.constants.SearchAdminWebKeys" %><%@
page import="com.liferay.portal.search.admin.web.internal.display.context.SearchEngineDisplayContext" %><%@
page import="com.liferay.portal.search.engine.ConnectionInformation" %><%@
page import="com.liferay.portal.search.engine.NodeInformation" %>

<%@ page import="java.util.List" %>

<%
SearchEngineDisplayContext searchEngineDisplayContext = (SearchEngineDisplayContext)request.getAttribute(SearchAdminWebKeys.SEARCH_ENGINE_DISPLAY_CONTEXT);
%>

<clay:container-fluid
	cssClass="container-form-lg search-engine-page-container"
>
	<c:choose>
		<c:when test="<%= searchEngineDisplayContext.isMissingSearchEngine() %>">
			<div class="alert alert-warning">
				<liferay-ui:message key="no-search-engine-detected-help" />
			</div>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="<%= searchEngineDisplayContext.getConnectionInformationList() == null %>">
					<div class="alert alert-info">
						<liferay-ui:message key="search-engine-vendor" />: <strong><%= searchEngineDisplayContext.getVendorString() %></strong>,
						<liferay-ui:message key="client-version" />: <strong><%= searchEngineDisplayContext.getClientVersionString() %></strong>,
						<liferay-ui:message key="nodes" />: <strong><%= searchEngineDisplayContext.getNodesString() %></strong>
					</div>
				</c:when>
				<c:otherwise>
					<clay:sheet
						cssClass="connection-info-item connection-info-item-header"
					>
						<div class="connection-info-data-container">
							<div class="data-item">
								<div class="key"><liferay-ui:message key="search-engine-vendor" /></div>
								<div class="value"><%= searchEngineDisplayContext.getVendorString() %></div>
							</div>

							<div class="data-item">
								<div class="key"><liferay-ui:message key="client-version" /></div>
								<div class="value"><%= searchEngineDisplayContext.getClientVersionString() %></div>
							</div>
						</div>
					</clay:sheet>

					<%
					List<ConnectionInformation> connectionInformationList = searchEngineDisplayContext.getConnectionInformationList();
					%>

					<h3 class="search-engine-page-title sheet-lg">
						<liferay-ui:message key="active-connections" />

						<clay:badge
							displayType="secondary"
							label="<%= String.valueOf((connectionInformationList == null) ? 0 : connectionInformationList.size()) %>"
						/>
					</h3>

					<c:choose>
						<c:when test="<%= (connectionInformationList != null) && (connectionInformationList.size() > 0) %>">

							<%
							for (ConnectionInformation connectionInformation : connectionInformationList) {
							%>

								<clay:sheet
									cssClass="connection-info-item"
								>
									<div class="connection-info-item-header">
										<div class="connection-info-item-header-block">
											<div class="connection-id h4">
												<%= connectionInformation.getConnectionId() %>

												<%
												for (String label : connectionInformation.getLabels()) {
												%>

													<clay:label
														label="<%= label %>"
													/>

												<%
												}
												%>

											</div>

											<c:if test="<%= Validator.isNotNull(connectionInformation.getClusterName()) %>">
												<span class="connection-cluster-name text-secondary"><%= connectionInformation.getClusterName() %></span>
											</c:if>
										</div>

										<c:if test="<%= Validator.isNotNull(connectionInformation.getHealth()) %>">
											<div class="connection-info-item-header-block">
												<div class="connection-health-indicator <%= StringUtil.lowerCase(connectionInformation.getHealth()) %>">
													<div class="indicator-item">
														<clay:icon
															symbol="simple-circle"
														/>
													</div>

													<div class="connection-health-indicator-text indicator-item">
														<liferay-ui:message arguments="<%= connectionInformation.getHealth() %>" key="health-x" />
													</div>
												</div>
											</div>
										</c:if>
									</div>

									<%
									List<NodeInformation> nodeInformationList = connectionInformation.getNodeInformationList();
									%>

									<liferay-frontend:fieldset
										collapsible="<%= nodeInformationList.size() > 0 %>"
										cssClass="connection-info-node-list"
										label='<%= LanguageUtil.format(request, "nodes-x", nodeInformationList.size(), false) %>'
									>
										<liferay-ui:search-container
											deltaConfigurable="<%= false %>"
											headerNames="name,version"
											total="<%= nodeInformationList.size() %>"
										>
											<liferay-ui:search-container-results
												results="<%= nodeInformationList %>"
											/>

											<liferay-ui:search-container-row
												className="com.liferay.portal.search.engine.NodeInformation"
												escapedModel="<%= true %>"
												keyProperty="name"
												modelVar="nodeInformation"
											>
												<liferay-ui:search-container-column-text
													property="name"
												/>

												<liferay-ui:search-container-column-text
													property="version"
												/>
											</liferay-ui:search-container-row>

											<liferay-ui:search-iterator
												markupView="lexicon"
												paginate="<%= false %>"
											/>
										</liferay-ui:search-container>
									</liferay-frontend:fieldset>

									<%
									String errorMessage = connectionInformation.getError();
									%>

									<c:if test="<%= Validator.isNotNull(errorMessage) %>">
										<clay:alert
											displayType="danger"
											message="<%= errorMessage %>"
										/>
									</c:if>
								</clay:sheet>

							<%
							}
							%>

						</c:when>
						<c:otherwise>
							<clay:alert
								message="no-active-connections"
							/>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</clay:container-fluid>