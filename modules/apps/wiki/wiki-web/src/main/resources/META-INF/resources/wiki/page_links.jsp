<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/wiki/init.jsp" %>

<%
WikiPageInfoPanelDisplayContext wikiPageInfoPanelDisplayContext = new WikiPageInfoPanelDisplayContext(request);

WikiPage wikiPage = wikiPageInfoPanelDisplayContext.getFirstPage();

List<WikiPage> incomingLinkPages = WikiPageLocalServiceUtil.getIncomingLinks(wikiPage.getNodeId(), wikiPage.getTitle());
List<WikiPage> outgoingLinkPages = WikiPageLocalServiceUtil.getOutgoingLinks(wikiPage.getNodeId(), wikiPage.getTitle());

boolean hasIncomingLinkPages = ListUtil.isNotEmpty(incomingLinkPages);
boolean hasOutgoingLinkPages = ListUtil.isNotEmpty(outgoingLinkPages);
%>

<div>
	<c:choose>
		<c:when test="<%= hasIncomingLinkPages || hasOutgoingLinkPages %>">
			<clay:panel-group>
				<clay:panel
					collapsable="<%= true %>"
					displayTitle='<%= LanguageUtil.get(request, "incoming-links") %>'
					expanded="<%= hasIncomingLinkPages %>"
				>
					<div class="panel-body">
						<c:choose>
							<c:when test="<%= hasIncomingLinkPages %>">
								<dl>

									<%
									for (WikiPage incomingLinkPage : incomingLinkPages) {
										WikiNode wikiNode = incomingLinkPage.getNode();
									%>

										<dt class="h5">
											<div class="h4">
												<a
													class="text-default" href="<%=
	PortletURLBuilder.createRenderURL(
											liferayPortletResponse
										).setMVCRenderCommandName(
											"/wiki/view"
										).setRedirect(
											currentURL
										).setParameter(
											"nodeName", wikiNode.getName()
										).setParameter(
											"title", incomingLinkPage.getTitle()
										).buildString() %>"><%= HtmlUtil.escape(incomingLinkPage.getTitle()) %></a
												>
											</div>
										</dt>
										<dd>
											<small>
												<liferay-portal-workflow:status
													showStatusLabel="<%= false %>"
													status="<%= incomingLinkPage.getStatus() %>"
												/>
											</small>
										</dd>

									<%
									}
									%>

								</dl>
							</c:when>
							<c:otherwise>
								<div class="alert alert-info">
									<liferay-ui:message key="there-are-no-pages-that-link-to-this-page" />
								</div>
							</c:otherwise>
						</c:choose>
					</div>
				</clay:panel>

				<clay:panel
					collapsable="<%= true %>"
					displayTitle='<%= LanguageUtil.get(request, "outgoing-links") %>'
					expanded="<%= hasOutgoingLinkPages %>"
				>
					<div class="panel-body">
						<c:choose>
							<c:when test="<%= hasOutgoingLinkPages %>">
								<dl>

									<%
									for (WikiPage outgoingLinkPage : outgoingLinkPages) {
									%>

										<c:choose>
											<c:when test="<%= outgoingLinkPage.isNew() %>">
												<dt class="h5">
													<div class="h4 text-truncate">
														<a class="text-default" href="<%= HtmlUtil.escapeHREF(outgoingLinkPage.getTitle()) %>"><%= HtmlUtil.escape(outgoingLinkPage.getTitle()) %></a>
													</div>
												</dt>
											</c:when>
											<c:otherwise>

												<%
												WikiNode wikiNode = outgoingLinkPage.getNode();
												%>

												<dt class="h5">
													<div class="h4 text-truncate">
														<a
															class="text-default" href="<%=
	PortletURLBuilder.createRenderURL(
													liferayPortletResponse
												).setMVCRenderCommandName(
													"/wiki/view"
												).setRedirect(
													currentURL
												).setParameter(
													"nodeName", wikiNode.getName()
												).setParameter(
													"title", outgoingLinkPage.getTitle()
												).buildString() %>"><%= HtmlUtil.escape(outgoingLinkPage.getTitle()) %></a
														>
													</div>
												</dt>
												<dd>
													<small>
														<liferay-portal-workflow:status
															showStatusLabel="<%= false %>"
															status="<%= outgoingLinkPage.getStatus() %>"
														/>
													</small>
												</dd>
											</c:otherwise>
										</c:choose>

									<%
									}
									%>

								</dl>
							</c:when>
							<c:otherwise>
								<div class="alert alert-info">
									<liferay-ui:message key="this-page-has-no-links" />
								</div>
							</c:otherwise>
						</c:choose>
					</div>
				</clay:panel>
			</clay:panel-group>
		</c:when>
		<c:otherwise>
			<div class="alert alert-info">
				<liferay-ui:message key="this-page-has-no-links" />
			</div>
		</c:otherwise>
	</c:choose>
</div>