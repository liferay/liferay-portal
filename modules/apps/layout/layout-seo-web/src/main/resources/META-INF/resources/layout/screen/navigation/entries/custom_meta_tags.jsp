<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
LayoutsSEODisplayContext layoutsSEODisplayContext = (LayoutsSEODisplayContext)request.getAttribute(LayoutSEOWebKeys.LAYOUT_PAGE_LAYOUT_SEO_DISPLAY_CONTEXT);
%>

<h2 class="mb-4 text-7"><liferay-ui:message key="custom-meta-tags" /></h2>

<liferay-frontend:edit-form
	action="<%= layoutsSEODisplayContext.getEditCustomMetaTagsURL() %>"
	cssClass="pt-0"
	method="post"
	name="fm"
	wrappedFormContent="<%= false %>"
>
	<aui:input name="redirect" type="hidden" value="<%= layoutsSEODisplayContext.getRedirectURL() %>" />
	<aui:input name="portletResource" type="hidden" value='<%= ParamUtil.getString(request, "portletResource") %>' />
	<aui:input name="groupId" type="hidden" value="<%= layoutsSEODisplayContext.getGroupId() %>" />
	<aui:input name="privateLayout" type="hidden" value="<%= layoutsSEODisplayContext.isPrivateLayout() %>" />
	<aui:input name="layoutId" type="hidden" value="<%= layoutsSEODisplayContext.getLayoutId() %>" />

	<liferay-frontend:edit-form-body>
		<clay:sheet
			cssClass="ml-0"
		>
			<clay:sheet-section>
				<h3 class="mb-4"><liferay-ui:message key="settings" /></h3>

				<p class="text-secondary">
					<liferay-ui:message key="custom-meta-tags-description" />
				</p>

				<%
				List<LayoutSEOEntryCustomMetaTag> layoutSEOEntryCustomMetaTags = new ArrayList<>();

				LayoutSEOEntry layoutSEOEntry = layoutsSEODisplayContext.getSelLayoutSEOEntry();

				if (layoutSEOEntry != null) {
					layoutSEOEntryCustomMetaTags = LayoutSEOEntryLocalServiceUtil.getLayoutSEOEntryCustomMetaTags(layoutSEOEntry.getGroupId(), layoutSEOEntry.getLayoutSEOEntryId());
				}
				%>

				<div id="<portlet:namespace />properties">
					<c:choose>
						<c:when test="<%= ListUtil.isEmpty(layoutSEOEntryCustomMetaTags) %>">
							<aui:model-context model="<%= LayoutSEOEntryCustomMetaTag.class %>" />

							<div class="field-row lfr-form-row lfr-form-row-inline">
								<div class="row-fields">
									<aui:input fieldParam="property0" id="property0" name="property" />

									<aui:input fieldParam="content0" id="content0" name="content" />
								</div>
							</div>
						</c:when>
						<c:otherwise>

							<%
							int i = 0;

							for (LayoutSEOEntryCustomMetaTag layoutSEOEntryCustomMetaTag : layoutSEOEntryCustomMetaTags) {
							%>

								<aui:model-context bean="<%= layoutSEOEntryCustomMetaTag %>" model="<%= LayoutSEOEntryCustomMetaTag.class %>" />

								<div class="lfr-form-row lfr-form-row-inline">
									<div class="row-fields">
										<aui:input fieldParam='<%= "property" + i %>' id='<%= "property" + i %>' name="property" />

										<aui:input fieldParam='<%= "content" + i %>' id='<%= "content" + i %>' name="content" />
									</div>
								</div>

							<%
								i++;
							}
							%>

						</c:otherwise>
					</c:choose>

					<aui:input name="propertiesIndexes" type="hidden" value="0" />
				</div>
			</clay:sheet-section>
		</clay:sheet>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= layoutsSEODisplayContext.getBackURL() %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<aui:script type="module">
	import {autoFields} from '<%= FrontendESMUtil.buildURL(themeDisplay, "frontend-js-web", "auto_fields") %>';

	autoFields({
		contentBox: '#<portlet:namespace />properties',
		fieldIndexes: '<portlet:namespace />propertiesIndexes',
		namespace: '<portlet:namespace />',
		sortable: true,
		sortableHandle: '.field-row',
	});
</aui:script>