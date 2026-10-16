<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/wiki/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

WikiEngineRenderer wikiEngineRenderer = (WikiEngineRenderer)request.getAttribute(WikiWebKeys.WIKI_ENGINE_RENDERER);
WikiNode node = (WikiNode)request.getAttribute(WikiWebKeys.WIKI_NODE);

WikiPage wikiPage = (WikiPage)request.getAttribute(WikiWebKeys.WIKI_PAGE);

long nodeId = BeanParamUtil.getLong(wikiPage, request, "nodeId");
String title = BeanParamUtil.getString(wikiPage, request, "title");

boolean editTitle = ParamUtil.getBoolean(request, "editTitle");

String selectedFormat = BeanParamUtil.getString(wikiPage, request, "format", siteWikiGroupServiceConfiguration.defaultFormat());

Collection<String> formats = wikiEngineRenderer.getFormats();

if (!formats.contains(selectedFormat)) {
	Iterator<String> iterator = formats.iterator();

	if (iterator.hasNext()) {
		selectedFormat = iterator.next();
	}
}

String parentTitle = BeanParamUtil.getString(wikiPage, request, "parentTitle");

boolean editable = false;

if (wikiPage != null) {
	if (WikiPagePermission.contains(permissionChecker, wikiPage, ActionKeys.UPDATE)) {
		editable = true;
	}
}
else if ((wikiPage == null) && editTitle) {
	editable = true;

	wikiPage = WikiPageLocalServiceUtil.createWikiPage(0);

	wikiPage.setNodeId(node.getNodeId());
	wikiPage.setFormat(selectedFormat);
	wikiPage.setParentTitle(parentTitle);
}

if (Validator.isNotNull(title)) {
	try {
		WikiPageTitleValidator wikiPageTitleValidator = (WikiPageTitleValidator)request.getAttribute(WikiWebKeys.WIKI_PAGE_TITLE_VALIDATOR);

		wikiPageTitleValidator.validate(title);

		editable = true;
	}
	catch (PageTitleException pte) {
		editTitle = true;
	}
}

long templateNodeId = ParamUtil.getLong(request, "templateNodeId");
String templateTitle = ParamUtil.getString(request, "templateTitle");

WikiPage templatePage = null;

if ((templateNodeId > 0) && Validator.isNotNull(templateTitle)) {
	try {
		templatePage = WikiPageServiceUtil.getPage(templateNodeId, templateTitle);

		if (Validator.isNull(parentTitle)) {
			parentTitle = templatePage.getParentTitle();

			if (wikiPage.isNew()) {
				selectedFormat = templatePage.getFormat();

				wikiPage.setContent(templatePage.getContent());
				wikiPage.setFormat(selectedFormat);
				wikiPage.setParentTitle(parentTitle);
			}
		}
	}
	catch (Exception e) {
	}
}

WikiURLHelper wikiURLHelper = new WikiURLHelper(wikiRequestHelper, renderResponse, wikiGroupServiceConfiguration);

PortletURL backToViewPagesURL = wikiURLHelper.getBackToViewPagesURL(node);

if (Validator.isNull(redirect)) {
	redirect = backToViewPagesURL.toString();
}

String headerTitle = ((wikiPage == null) || wikiPage.isNew()) ? LanguageUtil.get(request, "new-wiki-page") : wikiPage.getTitle();

boolean portletTitleBasedNavigation = GetterUtil.getBoolean(portletConfig.getInitParameter("portlet-title-based-navigation"));

if (portletTitleBasedNavigation) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(backToViewPagesURL.toString());

	renderResponse.setTitle(headerTitle);
}
%>

<c:if test="<%= portletTitleBasedNavigation && (wikiPage != null) && !wikiPage.isNew() %>">
	<div class="management-bar management-bar-light navbar navbar-expand-md">
		<clay:container-fluid>
			<ul class="m-auto navbar-nav"></ul>

			<ul class="middle navbar-nav">
				<li class="nav-item">
					<aui:workflow-status markupView="lexicon" showHelpMessage="<%= false %>" showIcon="<%= false %>" showLabel="<%= false %>" status="<%= wikiPage.getStatus() %>" version="<%= String.valueOf(wikiPage.getVersion()) %>" />
				</li>
			</ul>

			<ul class="end m-auto navbar-nav"></ul>
		</clay:container-fluid>
	</div>
</c:if>

<portlet:actionURL name="/wiki/edit_page" var="editPageActionURL">
	<portlet:param name="mvcRenderCommandName" value="/wiki/edit_page" />
</portlet:actionURL>

<portlet:renderURL var="editPageRenderURL">
	<portlet:param name="mvcRenderCommandName" value="/wiki/edit_page" />
</portlet:renderURL>

<div <%= portletTitleBasedNavigation ? "class=\"container-fluid container-fluid-max-xl container-form-lg\"" : StringPool.BLANK %> id='<%= liferayPortletResponse.getNamespace() + "wikiEditPageContainer" %>'>
	<aui:form action="<%= editPageActionURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" />
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		<aui:input name="editTitle" type="hidden" value="<%= editTitle %>" />
		<aui:input name="nodeId" type="hidden" value="<%= nodeId %>" />
		<aui:input name="parentTitle" type="hidden" value="<%= parentTitle %>" />
		<aui:input name="workflowAction" type="hidden" value="<%= WorkflowConstants.ACTION_SAVE_DRAFT %>" />

		<c:if test="<%= wikiPage != null %>">
			<aui:input name="version" type="hidden" value="<%= wikiPage.getVersion() %>" />
		</c:if>

		<c:if test="<%= templatePage != null %>">
			<aui:input name="templateNodeId" type="hidden" value="<%= String.valueOf(templateNodeId) %>" />
			<aui:input name="templateTitle" type="hidden" value="<%= templateTitle %>" />
		</c:if>

		<liferay-ui:error exception="<%= DuplicatePageException.class %>" message="there-is-already-a-page-with-the-specified-title" />
		<liferay-ui:error exception="<%= PageContentException.class %>" message="the-content-is-not-valid" />
		<liferay-ui:error exception="<%= PageTitleException.class %>" message="please-enter-a-valid-title" />
		<liferay-ui:error exception="<%= PageVersionException.class %>" message="another-user-has-made-changes-since-you-started-editing-please-copy-your-changes-and-try-again" />

		<liferay-asset:asset-categories-error />

		<liferay-asset:asset-tags-error />

		<aui:model-context bean="<%= ((wikiPage != null) && !wikiPage.isNew()) ? wikiPage : templatePage %>" model="<%= WikiPage.class %>" />

		<c:choose>
			<c:when test="<%= !editable %>">
				<c:if test="<%= (wikiPage == null) || wikiPage.isNew() %>">
					<div class="alert alert-info">
						<liferay-ui:message key="this-page-does-not-exist-yet-and-the-title-is-not-valid" />
					</div>

					<aui:button href="<%= HtmlUtil.escape(PortalUtil.escapeRedirect(redirect)) %>" value="cancel" />
				</c:if>

				<c:if test="<%= (wikiPage != null) && !wikiPage.isApproved() %>">
					<div class="alert alert-info">

						<%
						Format messageDateTimeFormat = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
						%>

						<liferay-ui:message arguments="<%= new Object[] {HtmlUtil.escape(wikiPage.getUserName()), messageDateTimeFormat.format(wikiPage.getModifiedDate())} %>" key="this-page-cannot-be-edited-because-user-x-is-modifying-it-and-the-results-have-not-been-published-yet" translateArguments="<%= false %>" />
					</div>
				</c:if>
			</c:when>
			<c:otherwise>
				<div class="sheet">
					<div class="panel-group panel-group-flush">
						<aui:fieldset>
							<c:if test="<%= !portletTitleBasedNavigation && (wikiPage != null) && !wikiPage.isNew() %>">
								<div class="text-center">
									<aui:workflow-status markupView="lexicon" showIcon="<%= false %>" showLabel="<%= false %>" status="<%= wikiPage.getStatus() %>" version="<%= String.valueOf(wikiPage.getVersion()) %>" />
								</div>
							</c:if>

							<c:if test="<%= ((wikiPage == null) || wikiPage.isNew()) && Validator.isNotNull(title) %>">
								<div class="alert alert-info">
									<liferay-ui:message key="this-page-does-not-exist-yet-use-the-form-below-to-create-it" />
								</div>
							</c:if>

							<c:choose>
								<c:when test="<%= editTitle %>">
									<div class="entry-title">
										<aui:input label='<%= LanguageUtil.get(request, "title") %>' name="title" type="text" value="<%= HtmlUtil.escape(title) %>" />
									</div>
								</c:when>
								<c:otherwise>
									<div class="entry-title">
										<h1 class="sheet-title"><%= HtmlUtil.escape(title) %></h1>
									</div>

									<aui:input name="title" type="hidden" value="<%= title %>" />
								</c:otherwise>
							</c:choose>

							<div>

								<%
								try {
									if ((templatePage != null) && (wikiPage != null) && wikiPage.isNew()) {
										WikiUtil.renderEditPageHTML(wikiEngineRenderer, selectedFormat, pageContext, node, templatePage);
									}
									else {
										WikiUtil.renderEditPageHTML(wikiEngineRenderer, selectedFormat, pageContext, node, wikiPage);
									}
								}
								catch (WikiFormatException wfe) {
								%>

									<div class="alert alert-danger">
										<liferay-ui:message key="the-format-of-this-page-is-not-supported-the-page-content-will-be-shown-unformatted" />
									</div>

									<aui:input name="content" type="textarea" value="<%= wikiPage.getContent() %>" />

								<%
								}
								%>

							</div>
						</aui:fieldset>

						<c:if test="<%= ((wikiPage != null) && (wikiPage.getPageId() > 0)) || ((templatePage != null) && WikiNodePermission.contains(permissionChecker, node, ActionKeys.ADD_ATTACHMENT)) %>">
							<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="attachments">
								<c:if test="<%= !wikiPage.isNew() && WikiNodePermission.contains(permissionChecker, node, ActionKeys.ADD_ATTACHMENT) %>">
									<liferay-util:include page="/wiki/edit_page_attachment.jsp" servletContext="<%= application %>" />
								</c:if>

								<liferay-util:include page="/wiki/edit_page_view_attachments.jsp" servletContext="<%= application %>" />
							</aui:fieldset>
						</c:if>

						<%
						long resourcePrimKey = 0;

						if ((wikiPage != null) && !wikiPage.isNew()) {
							resourcePrimKey = wikiPage.getResourcePrimKey();
						}
						else if (templatePage != null) {
							resourcePrimKey = templatePage.getResourcePrimKey();
						}

						long assetEntryId = 0;
						long classPK = resourcePrimKey;

						if ((wikiPage != null) && !wikiPage.isNew() && !wikiPage.isApproved() && (wikiPage.getVersion() != WikiPageConstants.VERSION_DEFAULT)) {
							AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(WikiPage.class.getName(), wikiPage.getPrimaryKey());

							if (assetEntry != null) {
								assetEntryId = assetEntry.getEntryId();
								classPK = wikiPage.getPrimaryKey();
							}
						}
						%>

						<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="categorization">
							<liferay-asset:asset-categories-selector
								className="<%= WikiPage.class.getName() %>"
								classPK="<%= classPK %>"
								visibilityTypes="<%= AssetVocabularyConstants.VISIBILITY_TYPES %>"
							/>

							<liferay-asset:asset-tags-selector
								className="<%= WikiPage.class.getName() %>"
								classPK="<%= classPK %>"
							/>
						</aui:fieldset>

						<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="related-assets">
							<liferay-asset:input-asset-links
								assetEntryId="<%= assetEntryId %>"
								className="<%= WikiPage.class.getName() %>"
								classPK="<%= classPK %>"
							/>
						</aui:fieldset>

						<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="configuration">
							<aui:input name="summary" />

							<c:if test="<%= (wikiPage == null) || wikiPage.isNew() || wikiPage.isApproved() %>">
								<aui:model-context bean="<%= WikiPageLocalServiceUtil.createWikiPage(0) %>" model="<%= WikiPage.class %>" />
							</c:if>

							<c:choose>
								<c:when test="<%= !formats.isEmpty() %>">
									<aui:select changesContext="<%= true %>" name="format">

										<%
										for (String format : formats) {
										%>

											<aui:option label="<%= WikiUtil.getFormatLabel(wikiEngineRenderer, format, locale) %>" selected="<%= selectedFormat.equals(format) %>" value="<%= format %>" />

										<%
										}
										%>

										<c:if test="<%= !formats.contains(selectedFormat) %>">
											<aui:option label="<%= selectedFormat %>" selected="<%= true %>" value="<%= selectedFormat %>" />
										</c:if>
									</aui:select>
								</c:when>
								<c:otherwise>
									<aui:input name="format" type="hidden" value="<%= selectedFormat %>" />
								</c:otherwise>
							</c:choose>

							<c:if test="<%= (wikiPage != null) && !wikiPage.isNew() %>">
								<aui:input label="this-is-a-minor-edit" name="minorEdit" />
							</c:if>
						</aui:fieldset>

						<c:if test="<%= wikiPage != null %>">
							<liferay-expando:custom-attributes-available
								className="<%= WikiPage.class.getName() %>"
							>
								<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="custom-fields">
									<liferay-expando:custom-attribute-list
										className="<%= WikiPage.class.getName() %>"
										classPK="<%= (templatePage != null) ? templatePage.getPrimaryKey() : wikiPage.getPrimaryKey() %>"
										editable="<%= true %>"
										label="<%= true %>"
									/>
								</aui:fieldset>
							</liferay-expando:custom-attributes-available>
						</c:if>

						<c:if test="<%= (wikiPage == null) || wikiPage.isNew() %>">
							<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="permissions">
								<liferay-ui:input-permissions
									modelName="<%= WikiPage.class.getName() %>"
								/>
							</aui:fieldset>
						</c:if>

						<%
						boolean pending = false;

						if (wikiPage != null) {
							pending = wikiPage.isPending();
						}
						%>

						<c:if test="<%= pending %>">
							<div class="alert alert-info">
								<liferay-ui:message key="there-is-a-publication-workflow-in-process" />
							</div>
						</c:if>

						<%
						String saveButtonLabel = "save";

						if ((wikiPage == null) || wikiPage.isDraft() || wikiPage.isApproved()) {
							saveButtonLabel = "save-as-draft";
						}

						String publishButtonLabel = "publish";

						if (WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(themeDisplay.getCompanyId(), scopeGroupId, WikiPage.class.getName())) {
							publishButtonLabel = "submit-for-workflow";
						}
						%>

						<div class="sheet-footer">
							<aui:button disabled="<%= pending %>" name="publishButton" primary="<%= true %>" value="<%= publishButtonLabel %>" />

							<aui:button name="saveButton" primary="<%= false %>" type="submit" value="<%= saveButtonLabel %>" />

							<aui:button href="<%= redirect %>" type="cancel" />
						</div>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</aui:form>
</div>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"constants",
			HashMapBuilder.<String, Object>put(
				"ACTION_PUBLISH", WorkflowConstants.ACTION_PUBLISH
			).put(
				"ACTION_SAVE_DRAFT", WorkflowConstants.ACTION_SAVE_DRAFT
			).put(
				"CMD", Constants.CMD
			).build()
		).put(
			"currentAction", (wikiPage == null) || wikiPage.isNew() ? Constants.ADD : Constants.UPDATE
		).put(
			"renderUrl", editPageRenderURL
		).put(
			"rootNodeId", liferayPortletResponse.getNamespace() + "wikiEditPageContainer"
		).build()
	%>'
	module="{WikiPortlet} from wiki-web"
/>

<%
if ((wikiPage != null) && !wikiPage.isNew()) {
	PortletURL viewPageURL = wikiURLHelper.getViewPageURL(node, title);

	PortalUtil.addPortletBreadcrumbEntry(request, wikiPage.getTitle(), viewPageURL.toString());

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "edit"), currentURL);
}
else {
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "add-page"), currentURL);
}
%>