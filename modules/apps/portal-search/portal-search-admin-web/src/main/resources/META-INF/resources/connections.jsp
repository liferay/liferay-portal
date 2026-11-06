<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/learn" prefix="liferay-learn" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.learn.LearnMessage" %><%@
page import="com.liferay.learn.LearnMessageUtil" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.search.admin.web.internal.constants.SearchAdminWebKeys" %><%@
page import="com.liferay.portal.search.admin.web.internal.display.context.SearchEngineDisplayContext" %><%@
page import="com.liferay.portal.search.engine.ConnectionInformation" %><%@
page import="com.liferay.portal.search.engine.NodeInformation" %>

<%@ page import="java.util.List" %>

<liferay-theme:defineObjects />

<%
SearchEngineDisplayContext searchEngineDisplayContext = (SearchEngineDisplayContext)request.getAttribute(SearchAdminWebKeys.SEARCH_ENGINE_DISPLAY_CONTEXT);
%>

<clay:container-fluid
	cssClass="container-form-lg search-engine-page-container"
	size="lg"
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
					<c:if test="<%= searchEngineDisplayContext.isWarnAboutDeprecatedConnection() %>">
						<div class="alert alert-inline alert-warning">
							<div class="autofit-row">
								<div class="autofit-col">
									<div class="autofit-section c-mr-2">
										<clay:icon
											class="text-warning"
											symbol="warning-full"
										/>
									</div>
								</div>

								<div class="autofit-col autofit-col-expand">
									<div class="autofit-section">
										<strong><liferay-ui:message key="warning" /></strong>: <liferay-ui:message key="compatibility-with-elasticsearch-7-deprecation-warning" />

										<liferay-learn:message
											key="elasticsearch-end-of-life"
											resource="portal-search-web"
										/>
									</div>
								</div>

								<div class="autofit-col">
									<div class="autofit-section">

										<%
										LearnMessage searchEngineCompatibilityLearnMessage = LearnMessageUtil.getLearnMessage("search-engine-compatibility", themeDisplay.getLanguageId(), "portal-search-web");
										%>

										<c:if test="<%= searchEngineCompatibilityLearnMessage.getURL() != StringPool.BLANK %>">
											<div class="btn-group-item">
												<clay:link
													cssClass="alert-btn btn btn-sm btn-warning"
													displayType="warning"
													href="<%= searchEngineCompatibilityLearnMessage.getURL() %>"
													label='<%= LanguageUtil.get(request, "view-compatibility") %>'
													target="_blank"
													type="button"
												/>
											</div>
										</c:if>
									</div>
								</div>
							</div>
						</div>
					</c:if>

					<c:if test="<%= searchEngineDisplayContext.isWarnAboutSidecarConnection() %>">
						<div class="alert alert-inline alert-warning">
							<div class="autofit-row">
								<div class="autofit-col">
									<div class="autofit-section c-mr-2">
										<clay:icon
											class="text-warning"
											symbol="warning-full"
										/>
									</div>
								</div>

								<div class="autofit-col autofit-col-expand">
									<div class="autofit-section">
										<strong><liferay-ui:message key="warning" /></strong>: <liferay-ui:message key="sidecar-connection-not-supported-warning" />

										<liferay-learn:message
											key="sidecar-not-supported"
											resource="portal-search-web"
										/>
									</div>
								</div>

								<div class="autofit-col">
									<div class="autofit-section">

										<%
										LearnMessage installElasticsearchLearnMessage = LearnMessageUtil.getLearnMessage("install-elasticsearch", themeDisplay.getLanguageId(), "portal-search-web");
										LearnMessage installOpenSearchLearnMessage = LearnMessageUtil.getLearnMessage("install-opensearch", themeDisplay.getLanguageId(), "portal-search-web");
										%>

										<c:if test="<%= installElasticsearchLearnMessage.getURL() != StringPool.BLANK %>">
											<div class="btn-group-item">
												<clay:link
													cssClass="alert-btn btn btn-sm btn-warning"
													displayType="warning"
													href="<%= installElasticsearchLearnMessage.getURL() %>"
													label='<%= LanguageUtil.get(request, "install-elasticsearch") %>'
													target="_blank"
													type="button"
												/>
											</div>
										</c:if>

										<c:if test="<%= installOpenSearchLearnMessage.getURL() != StringPool.BLANK %>">
											<div class="btn-group-item">
												<clay:link
													cssClass="alert-btn btn btn-sm btn-warning"
													displayType="warning"
													href="<%= installOpenSearchLearnMessage.getURL() %>"
													label='<%= LanguageUtil.get(request, "install-opensearch") %>'
													target="_blank"
													type="button"
												/>
											</div>
										</c:if>
									</div>
								</div>
							</div>
						</div>
					</c:if>

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

													<c:choose>
														<c:when test='<%= label.equals("deprecated") || label.equals("not-supported") %>'>
															<clay:label
																displayType="warning"
																label="<%= label %>"
															/>
														</c:when>
														<c:otherwise>
															<clay:label
																label="<%= label %>"
															/>
														</c:otherwise>
													</c:choose>

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

									<c:if test="<%= !searchEngineDisplayContext.isWarnAboutSidecarConnection() %>">

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
												id="<%= connectionInformation.getConnectionId() %>"
												total="<%= nodeInformationList.size() %>"
											>
												<liferay-ui:search-container-results
													results="<%= nodeInformationList %>"
												/>

												<liferay-ui:search-container-row
													className="com.liferay.portal.search.engine.NodeInformation"
													escapedModel="<%= true %>"
													keyPropertyName="name"
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