<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SelectLayoutPageTemplateEntryDisplayContext selectLayoutPageTemplateEntryDisplayContext = (SelectLayoutPageTemplateEntryDisplayContext)request.getAttribute(SelectLayoutPageTemplateEntryDisplayContext.class.getName());
%>

<div class="lfr-search-container-wrapper" id="<portlet:namespace />layoutTypes">
	<ul class="card-page card-page-equal-height">

		<%
		for (LayoutPageTemplateEntry masterLayoutPageTemplateEntry : ListUtil.concat(selectLayoutPageTemplateEntryDisplayContext.getMasterLayoutPageTemplateEntries(), selectLayoutPageTemplateEntryDisplayContext.getDesignLibraryMasterLayoutPageTemplateEntries())) {
		%>

			<li class="card-page-item card-page-item-asset">
				<clay:vertical-card
					verticalCard="<%= new SelectBasicTemplatesVerticalCard(masterLayoutPageTemplateEntry, renderRequest, renderResponse) %>"
				/>
			</li>

		<%
		}
		%>

	</ul>

	<c:if test="<%= selectLayoutPageTemplateEntryDisplayContext.getTypesCount() > 0 %>">
		<h3 class="h6 sheet-subtitle">
			<liferay-ui:message key="other" />
		</h3>

		<ul class="card-page card-page-equal-height">

			<%
			for (String type : selectLayoutPageTemplateEntryDisplayContext.getTypes()) {
				SelectBasicTemplatesNavigationCard selectBasicTemplatesNavigationCard = new SelectBasicTemplatesNavigationCard(type, renderRequest, renderResponse);
			%>

				<li class="card-page-item card-page-item-directory" data-qa-id="cardPageItemDirectory">
					<c:choose>
						<c:when test="<%= selectBasicTemplatesNavigationCard.isDeprecated() %>">

							<%
							Map<String, String> dynamicAttributes = selectBasicTemplatesNavigationCard.getDynamicAttributes();
							%>

							<div class="add-layout-action-option card card-interactive card-interactive-primary card-type-template template-card-horizontal" data-add-layout-url="<%= HtmlUtil.escapeAttribute(dynamicAttributes.get("data-add-layout-url")) %>" role="button" tabindex="0">
								<span class="card-body">
									<span class="card-row">
										<div class="autofit-col">
											<clay:sticker
												icon="page"
												inline="<%= true %>"
											/>
										</div>

										<div class="autofit-col autofit-col-expand">
											<div class="autofit-section">
												<p class="card-title">
													<%= HtmlUtil.escape(selectBasicTemplatesNavigationCard.getTitle()) %>

													<clay:badge
														cssClass="c-ml-1 text-uppercase"
														displayType="warning"
														label='<%= LanguageUtil.get(request, "deprecated") %>'
														translucent="<%= true %>"
													/>
												</p>
											</div>
										</div>
									</span>
								</span>
							</div>
						</c:when>
						<c:otherwise>
							<clay:navigation-card
								navigationCard="<%= selectBasicTemplatesNavigationCard %>"
							/>
						</c:otherwise>
					</c:choose>
				</li>

			<%
			}
			%>

		</ul>
	</c:if>
</div>