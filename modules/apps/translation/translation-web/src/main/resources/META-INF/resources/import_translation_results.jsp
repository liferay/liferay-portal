<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ImportTranslationResultsDisplayContext importTranslationResultsDisplayContext = (ImportTranslationResultsDisplayContext)request.getAttribute(ImportTranslationResultsDisplayContext.class.getName());

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(importTranslationResultsDisplayContext.getRedirect(request));

renderResponse.setTitle(LanguageUtil.get(resourceBundle, "import-translation"));
%>

<div class="translation">
	<div class="translation-import">
		<nav class="management-bar management-bar-light navbar navbar-expand-md">
			<clay:container-fluid>
				<ul class="tbar-nav">
					<li class="tbar-item tbar-item-expand">
						<div class="pl-2 tbar-section text-left">
							<div class="h4 mb-0 text-truncate-inline" title="<%= HtmlUtil.escapeAttribute(importTranslationResultsDisplayContext.getTitle()) %>">
								<span class="text-truncate"><%= HtmlUtil.escape(importTranslationResultsDisplayContext.getTitle()) %></span>
							</div>
						</div>
					</li>

					<%
					String redirectURL = importTranslationResultsDisplayContext.getRedirect(request);
					%>

					<c:if test="<%= Validator.isNotNull(redirectURL) %>">
						<li class="tbar-item">
							<div class="tbar-section text-right">
								<clay:link
									displayType="primary"
									href="<%= redirectURL %>"
									label="done"
									small="<%= true %>"
									type="button"
								/>
							</div>
						</li>
					</c:if>
				</ul>
			</clay:container-fluid>
		</nav>

		<clay:container-fluid
			cssClass="container-view"
			size="lg"
		>
			<div>

				<%
				boolean importTranslationResultsErrors = false;

				if (importTranslationResultsDisplayContext.getFailureMessagesCount() > 0) {
					importTranslationResultsErrors = true;
				}
				%>

				<c:if test="<%= importTranslationResultsDisplayContext.getSuccessMessagesCount() > 0 %>">
					<div>
						<div class="panel panel-secondary" role="tablist">
							<button aria-expanded="<%= !importTranslationResultsErrors %>" class="<%= importTranslationResultsErrors ? "collapsed" : StringPool.BLANK %> btn btn-unstyled collapse-icon collapse-icon-middle panel-header panel-header-link" role="tab" type="button">
								<liferay-util:whitespace-remover>
									<div class="h4 mb-0 text-success">
										<span class="mr-2">
											<clay:icon
												symbol="check-circle-full"
											/>
										</span>
										<%= importTranslationResultsDisplayContext.getSuccessMessageLabel(locale) %>
									</div>
								</liferay-util:whitespace-remover>

								<span class="collapse-icon-closed">
									<clay:icon
										symbol="angle-right"
									/>
								</span>
								<span class="collapse-icon-open">
									<clay:icon
										symbol="angle-down"
									/>
								</span>
							</button>

							<div class="<%= importTranslationResultsErrors ? "collapse" : StringPool.BLANK %> panel-collapse" role="tabpanel">
								<div class="panel-body">
									<ul class="list-group list-group-no-bordered mb-0">

										<%
										for (String successMessage : importTranslationResultsDisplayContext.getSuccessMessages()) {
										%>

											<li class="align-items-center list-group-item">
												<div class="list-group-title"><%= successMessage %></div>
											</li>

										<%
										}
										%>

									</ul>
								</div>
							</div>
						</div>

						<react:component
							module="{ImportTranslationResultsPanelSuccess} from translation-web"
							props='<%=
								HashMapBuilder.<String, Object>put(
									"defaultExpanded", !importTranslationResultsErrors
								).put(
									"files", importTranslationResultsDisplayContext.getSuccessMessages()
								).put(
									"title", importTranslationResultsDisplayContext.getSuccessMessageLabel(locale)
								).build()
							%>'
						/>
					</div>
				</c:if>

				<c:if test="<%= importTranslationResultsDisplayContext.getFailureMessagesCount() > 0 %>">
					<clay:sheet
						size="full"
					>
						<clay:content-row
							noGutters="true"
						>
							<clay:content-col
								cssClass="align-self-center"
								expand="<%= true %>"
							>
								<liferay-util:whitespace-remover>
									<div class="h4 mb-0 text-danger">
										<span class="mr-2">
											<clay:icon
												symbol="exclamation-full"
											/>
										</span>

										<liferay-ui:message arguments="<%= importTranslationResultsDisplayContext.getFailureMessagesCount() %>" key="<%= importTranslationResultsDisplayContext.getFailureMessageKey() %>" />
									</div>
								</liferay-util:whitespace-remover>
							</clay:content-col>

							<clay:content-col>
								<div class="btn-group" role="group">
									<div class="btn-group-item">
										<clay:link
											displayType="secondary"
											href="<%= importTranslationResultsDisplayContext.getImportTranslationURL(request, liferayPortletResponse) %>"
											label="upload-another-file"
											small="<%= true %>"
											type="button"
										/>
									</div>

									<div class="btn-group-item">
										<clay:link
											displayType="secondary"
											download='<%= StringUtil.randomString() + ".csv" %>'
											href="<%= importTranslationResultsDisplayContext.getFailureMessagesCSVDataURL(locale) %>"
											label="download-csv-error-report"
											small="<%= true %>"
											target="_blank"
											type="button"
										/>
									</div>
								</div>
							</clay:content-col>
						</clay:content-row>

						<ul class="list-group list-group-no-bordered">

							<%
							for (Map<String, String> failureMessage : importTranslationResultsDisplayContext.getFailureMessages()) {
							%>

								<li class="list-group-item">
									<clay:content-row
										cssClass="mb-2"
									>
										<clay:content-col
											cssClass="lfr-portal-tooltip list-group-title mt-0"
											expand="<%= true %>"
										>
											<div class="text-truncate" data-title="<%= failureMessage.get("fileName") %>">
												<%= failureMessage.get("fileName") %>
											</div>
										</clay:content-col>

										<c:if test='<%= Validator.isNotNull(failureMessage.get("container")) %>'>
											<clay:content-col
												cssClass="lfr-portal-tooltip ml-2 text-right"
												expand="<%= true %>"
											>
												<div class="text-truncate" data-title="<%= failureMessage.get("container") %>">
													<%= failureMessage.get("container") %>
												</div>
											</clay:content-col>
										</c:if>
									</clay:content-row>

									<div class="text-danger"><%= failureMessage.get("errorMessage") %></div>
								</li>

							<%
							}
							%>

						</ul>
					</clay:sheet>
				</c:if>
			</div>
		</clay:container-fluid>
	</div>
</div>