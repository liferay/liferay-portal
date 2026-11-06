<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/publications/init.jsp" %>

<liferay-portlet:renderURL var="backURL" />

<%
ViewTemplatesDisplayContext viewTemplatesDisplayContext = (ViewTemplatesDisplayContext)request.getAttribute(CTWebKeys.VIEW_TEMPLATES_DISPLAY_CONTEXT);

SearchContainer<CTCollectionTemplate> searchContainer = viewTemplatesDisplayContext.getSearchContainer();

renderResponse.setTitle(LanguageUtil.get(request, "publication-templates"));

portletDisplay.setURLBack(backURL);
portletDisplay.setShowBackIcon(true);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new ViewTemplatesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, searchContainer, viewTemplatesDisplayContext) %>"
/>

<liferay-ui:search-container
	cssClass="publications-templates-table"
	searchContainer="<%= searchContainer %>"
	var="publicationsTemplatesSearchContainer"
>
	<liferay-ui:search-container-row
		className="com.liferay.change.tracking.model.CTCollectionTemplate"
		escapedModel="<%= true %>"
		keyPropertyName="ctCollectionTemplateId"
		modelVar="ctCollectionTemplate"
	>
		<c:choose>
			<c:when test='<%= Objects.equals(viewTemplatesDisplayContext.getDisplayStyle(), "descriptive") %>'>
				<liferay-ui:search-container-column-text>
					<span class="lfr-portal-tooltip" title="<%= HtmlUtil.escape(ctCollectionTemplate.getUserName()) %>">
						<liferay-user:user-portrait
							userId="<%= ctCollectionTemplate.getUserId() %>"
						/>
					</span>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					cssClass="autofit-col-expand"
					href="<%= viewTemplatesDisplayContext.getEditTemplateURL(ctCollectionTemplate.getCtCollectionTemplateId()) %>"
				>
					<div class="publication-template-name">
						<%= ctCollectionTemplate.getName() %>
					</div>

					<c:if test="<%= Validator.isNotNull(ctCollectionTemplate.getDescription()) %>">
						<div class="publication-template-description">
							<%= ctCollectionTemplate.getDescription() %>
						</div>
					</c:if>
				</liferay-ui:search-container-column-text>
			</c:when>
			<c:otherwise>
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					href="<%= viewTemplatesDisplayContext.getEditTemplateURL(ctCollectionTemplate.getCtCollectionTemplateId()) %>"
					name="publication-template"
				>
					<div class="publication-template-name">
						<%= ctCollectionTemplate.getName() %>
					</div>

					<div class="publication-template-description">
						<%= ctCollectionTemplate.getDescription() %>
					</div>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smaller table-column-text-center"
					name="default-template"
				>
					<c:if test="<%= viewTemplatesDisplayContext.isDefaultCTCollectionTemplate(ctCollectionTemplate) %>">
						<clay:icon
							symbol="check"
						/>
					</c:if>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smaller table-column-text-center"
					name="default-sandbox-template"
				>
					<c:if test="<%= viewTemplatesDisplayContext.isDefaultSandboxCTCollectionTemplate(ctCollectionTemplate) %>">
						<clay:icon
							symbol="check"
						/>
					</c:if>
				</liferay-ui:search-container-column-text>

				<%
				Date modifiedDate = ctCollectionTemplate.getModifiedDate();
				%>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smaller"
					name="last-modified"
				>
					<liferay-ui:message arguments="<%= LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - modifiedDate.getTime(), true) %>" key="x-ago" translateArguments="<%= false %>" />
				</liferay-ui:search-container-column-text>

				<%
				Date createDate = ctCollectionTemplate.getCreateDate();
				%>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smaller"
					name="created"
				>
					<liferay-ui:message arguments="<%= LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - createDate.getTime(), true) %>" key="x-ago" translateArguments="<%= false %>" />
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest text-center"
					name="owner"
				>
					<span class="lfr-portal-tooltip" title="<%= HtmlUtil.escape(ctCollectionTemplate.getUserName()) %>">
						<liferay-user:user-portrait
							userId="<%= ctCollectionTemplate.getUserId() %>"
						/>
					</span>
				</liferay-ui:search-container-column-text>
			</c:otherwise>
		</c:choose>

		<liferay-ui:search-container-column-text>
			<div>
				<div class="dropdown">
					<button class="btn btn-monospaced btn-sm btn-unstyled dropdown-toggle hidden" type="button">
						<svg class="lexicon-icon lexicon-icon-ellipsis-v publications-hidden" role="presentation">
							<use xlink:href="<%= themeDisplay.getPathThemeSpritemap() %>#ellipsis-v" />
						</svg>
					</button>
				</div>

				<react:component
					module="{PublicationTemplateDropdownMenu} from change-tracking-web"
					props="<%= viewTemplatesDisplayContext.getDropdownReactData(ctCollectionTemplate) %>"
				/>
			</div>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		displayStyle="<%= viewTemplatesDisplayContext.getDisplayStyle() %>"
		markupView="lexicon"
		searchContainer="<%= searchContainer %>"
	/>
</liferay-ui:search-container>

<%
CTLocalizedException ctLocalizedException = null;

if (MultiSessionErrors.contains(liferayPortletRequest, CTLocalizedException.class.getName())) {
	ctLocalizedException = (CTLocalizedException)MultiSessionErrors.get(liferayPortletRequest, CTLocalizedException.class.getName());
}
%>

<c:if test="<%= ctLocalizedException != null %>">
	<aui:script>
		Liferay.Util.openToast({
			autoClose: 10000,
			message:
				'<%= HtmlUtil.escapeJS(ctLocalizedException.formatMessage(resourceBundle)) %>',
			title: '<liferay-ui:message key="error" />:',
			type: 'danger',
		});
	</aui:script>
</c:if>