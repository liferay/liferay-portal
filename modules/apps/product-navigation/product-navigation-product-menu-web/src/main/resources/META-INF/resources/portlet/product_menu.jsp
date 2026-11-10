<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/portlet/init.jsp" %>

<%
ProductMenuDisplayContext productMenuDisplayContext = new ProductMenuDisplayContext(liferayPortletRequest);
%>

<c:if test="<%= productMenuDisplayContext.isShowProductMenu() %>">

	<%
	List<PanelCategory> childPanelCategories = productMenuDisplayContext.getChildPanelCategories();

	request.setAttribute("product_menu.jsp-childPanelCategoriesSize", childPanelCategories.size());

	boolean singleChildCategory = childPanelCategories.size() == 1;
	%>

	<div class="panel-group" data-qa-id="productMenuBody" id="<portlet:namespace />Accordion" role="<%= singleChildCategory ? StringPool.BLANK : "tablist" %>">
		<c:choose>
			<c:when test="<%= !singleChildCategory %>">

				<%
				for (PanelCategory childPanelCategory : childPanelCategories) {
				%>

					<div class="panel panel-secondary">
						<div class="panel-header panel-heading" id="<portlet:namespace /><%= AUIUtil.normalizeId(childPanelCategory.getKey()) %>Heading" role="tab">
							<div class="panel-title">
								<c:if test="<%= !childPanelCategory.includeHeader(request, PipingServletResponseFactory.createPipingServletResponse(pageContext)) %>">

									<%
									Class<?> childPanelCategoryClass = childPanelCategory.getClass();
									%>

									<a aria-controls="<portlet:namespace /><%= AUIUtil.normalizeId(childPanelCategory.getKey()) %>Collapse" aria-expanded="<%= Objects.equals(childPanelCategory.getKey(), productMenuDisplayContext.getRootPanelCategoryKey()) %>" class="collapse-icon collapse-icon-middle panel-toggler panel-header-link <%= Objects.equals(childPanelCategory.getKey(), productMenuDisplayContext.getRootPanelCategoryKey()) ? StringPool.BLANK : "collapsed" %>" data-parent="#<portlet:namespace />Accordion" data-qa-id="productMenu<%= childPanelCategoryClass.getSimpleName() %>" data-toggle="liferay-collapse" href="#<portlet:namespace /><%= AUIUtil.normalizeId(childPanelCategory.getKey()) %>Collapse" role="button">
										<%@ include file="/portlet/product_menu_title.jspf" %>

										<clay:icon
											cssClass="collapse-icon-closed"
											symbol="angle-right"
										/>

										<clay:icon
											cssClass="collapse-icon-open"
											symbol="angle-down"
										/>
									</a>
								</c:if>
							</div>
						</div>

						<div aria-expanded="<%= Objects.equals(childPanelCategory.getKey(), productMenuDisplayContext.getRootPanelCategoryKey()) %>" aria-labelledby="<portlet:namespace /><%= AUIUtil.normalizeId(childPanelCategory.getKey()) %>Heading" class="collapse panel-collapse <%= Objects.equals(childPanelCategory.getKey(), productMenuDisplayContext.getRootPanelCategoryKey()) ? "show" : StringPool.BLANK %>" data-parent="#<portlet:namespace />Accordion" id="<portlet:namespace /><%= AUIUtil.normalizeId(childPanelCategory.getKey()) %>Collapse" role="tabpanel">
							<div class="panel-body">

								<%
								boolean include = childPanelCategory.include(request, PipingServletResponseFactory.createPipingServletResponse(pageContext));
								%>

								<c:if test="<%= !include %>">
									<liferay-application-list:panel
										panelCategory="<%= childPanelCategory %>"
									/>
								</c:if>
							</div>
						</div>
					</div>

				<%
				}
				%>

			</c:when>
			<c:otherwise>

				<%
				PanelCategory childPanelCategory = childPanelCategories.get(0);
				%>

				<div class="panel panel-secondary">
					<div class="panel-header panel-heading" id="<portlet:namespace /><%= AUIUtil.normalizeId(childPanelCategory.getKey()) %>Heading">
						<div class="panel-title">
							<c:if test="<%= !childPanelCategory.includeHeader(request, PipingServletResponseFactory.createPipingServletResponse(pageContext)) %>">

								<%
								Class<?> childPanelCategoryClass = childPanelCategory.getClass();
								%>

								<span data-qa-id="productMenu<%= childPanelCategoryClass.getSimpleName() %>">
									<%@ include file="/portlet/product_menu_title.jspf" %>
								</span>
							</c:if>
						</div>
					</div>

					<div class="panel-body">

						<%
						boolean include = childPanelCategory.include(request, PipingServletResponseFactory.createPipingServletResponse(pageContext));
						%>

						<c:if test="<%= !include %>">
							<liferay-application-list:panel
								panelCategory="<%= childPanelCategory %>"
							/>
						</c:if>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</c:if>