<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String backURL = ParamUtil.getString(request, "backURL", redirect);

if (Validator.isNull(backURL)) {
	backURL = PortalUtil.getLayoutFullURL(layoutsAdminDisplayContext.getSelLayout(), themeDisplay);
}

String portletResource = ParamUtil.getString(request, "portletResource");

Group selGroup = (Group)request.getAttribute(WebKeys.GROUP);

Group group = layoutsAdminDisplayContext.getGroup();

Layout selLayout = layoutsAdminDisplayContext.getSelLayout();

LayoutType selLayoutType = selLayout.getLayoutType();

portletDisplay.setURLBackTitle(ParamUtil.getString(request, "backURLTitle"));
%>

<portlet:actionURL name="/layout_admin/edit_layout" var="editLayoutURL">
	<portlet:param name="mvcRenderCommandName" value="/layout_admin/edit_layout" />
</portlet:actionURL>

<liferay-frontend:edit-form
	action="<%= editLayoutURL %>"
	cssClass="c-pt-0"
	enctype="multipart/form-data"
	method="post"
	name="editLayoutFm"
	onSubmit="event.preventDefault();"
	title='<%= LanguageUtil.get(request, "general") %>'
	wrappedFormContent="<%= false %>"
>
	<aui:input name="redirect" type="hidden" value="<%= String.valueOf(layoutsAdminDisplayContext.getLayoutScreenNavigationPortletURL(selLayout.getPlid())) %>" />
	<aui:input name="backURL" type="hidden" value="<%= backURL %>" />
	<aui:input name="portletResource" type="hidden" value="<%= portletResource %>" />
	<aui:input name="groupId" type="hidden" value="<%= layoutsAdminDisplayContext.getGroupId() %>" />
	<aui:input name="liveGroupId" type="hidden" value="<%= layoutsAdminDisplayContext.getLiveGroupId() %>" />
	<aui:input name="stagingGroupId" type="hidden" value="<%= layoutsAdminDisplayContext.getStagingGroupId() %>" />
	<aui:input name="selPlid" type="hidden" value="<%= layoutsAdminDisplayContext.getSelPlid() %>" />
	<aui:input name="type" type="hidden" value="<%= selLayout.getType() %>" />

	<c:if test="<%= group.isLayoutPrototype() || !(selLayoutType.isURLFriendliable() && !layoutsAdminDisplayContext.isDraft() && (!selLayout.isSystem() || selLayout.isTypeAssetDisplay() || selLayout.isTypeUtility())) %>">
		<aui:input name="friendlyURL" type="hidden" value="<%= HttpComponentsUtil.decodeURL(selLayout.getFriendlyURL()) %>" />
	</c:if>

	<%
	Locale defaultLocale = LocaleUtil.getDefault();

	String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);
	%>

	<c:if test="<%= group.isLayoutPrototype() %>">
		<aui:input name='<%= "nameMapAsXML_" + defaultLanguageId %>' type="hidden" value="<%= selLayout.getName(defaultLocale) %>" />
	</c:if>

	<c:if test="<%= layoutsAdminDisplayContext.isLayoutPageTemplateEntry() || selLayout.isTypeUtility() || ((selLayout.isTypeAssetDisplay() || selLayout.isTypeContent()) && layoutsAdminDisplayContext.isDraft()) %>">

		<%
		for (Locale availableLocale : LanguageUtil.getAvailableLocales(group.getGroupId())) {
		%>

			<aui:input name='<%= "nameMapAsXML_" + LocaleUtil.toLanguageId(availableLocale) %>' type="hidden" value="<%= selLayout.getName(availableLocale) %>" />

		<%
		}
		%>

	</c:if>

	<h2 class="c-mb-4 text-7"><liferay-ui:message key="general" /></h2>

	<liferay-frontend:edit-form-body>
		<liferay-ui:success key="layoutAdded" message="the-page-was-created-successfully" />

		<c:if test="<%= !group.isLayoutPrototype() && selLayoutType.isURLFriendliable() && !layoutsAdminDisplayContext.isDraft() && !selLayout.isSystem() %>">
			<liferay-ui:error exception="<%= DuplicateFriendlyURLEntryException.class %>" message="the-friendly-url-is-already-in-use.-please-enter-a-unique-friendly-url" />

			<liferay-ui:error exception="<%= LayoutFriendlyURLException.class %>" focusField="friendlyURL">

				<%
				Locale exceptionLocale = null;
				LayoutFriendlyURLException lfurle = (LayoutFriendlyURLException)errorException;
				%>

				<%@ include file="/error_friendly_url_exception.jspf" %>
			</liferay-ui:error>

			<liferay-ui:error exception="<%= LayoutFriendlyURLsException.class %>" focusField="friendlyURL">

				<%
				LayoutFriendlyURLsException lfurlse = (LayoutFriendlyURLsException)errorException;

				Map<Locale, Exception> localizedExceptionsMap = lfurlse.getLocalizedExceptionsMap();

				for (Map.Entry<Locale, Exception> entry : localizedExceptionsMap.entrySet()) {
					Locale exceptionLocale = entry.getKey();
					LayoutFriendlyURLException lfurle = (LayoutFriendlyURLException)entry.getValue();
				%>

					<%@ include file="/error_friendly_url_exception.jspf" %>

				<%
				}
				%>

			</liferay-ui:error>
		</c:if>

		<liferay-ui:error exception="<%= LayoutTypeException.class %>">

			<%
			LayoutTypeException lte = (LayoutTypeException)errorException;

			String type = BeanParamUtil.getString(selLayout, request, "type");
			%>

			<c:if test="<%= lte.getType() == LayoutTypeException.FIRST_LAYOUT %>">
				<liferay-ui:message arguments='<%= Validator.isNull(lte.getLayoutType()) ? HtmlUtil.escape(type) : "layout.types." + HtmlUtil.escape(lte.getLayoutType()) %>' key="the-first-page-cannot-be-of-type-x" />
			</c:if>

			<c:if test="<%= lte.getType() == LayoutTypeException.FIRST_LAYOUT_PERMISSION %>">
				<liferay-ui:message key="you-cannot-delete-this-page-because-the-next-page-is-not-viewable-by-unauthenticated-users-and-so-cannot-be-the-first-page" />
			</c:if>

			<c:if test="<%= lte.getType() == LayoutTypeException.NOT_INSTANCEABLE %>">
				<liferay-ui:message arguments="<%= HtmlUtil.escape(type) %>" key="pages-of-type-x-cannot-be-selected" />
			</c:if>

			<c:if test="<%= lte.getType() == LayoutTypeException.NOT_PARENTABLE %>">
				<liferay-ui:message arguments="<%= HtmlUtil.escape(type) %>" key="pages-of-type-x-cannot-have-child-pages" />
			</c:if>
		</liferay-ui:error>

		<liferay-ui:error exception="<%= LayoutNameException.class %>" message="please-enter-a-valid-name" />

		<liferay-ui:error exception="<%= RequiredLayoutException.class %>">

			<%
			RequiredLayoutException rle = (RequiredLayoutException)errorException;
			%>

			<c:if test="<%= rle.getType() == RequiredLayoutException.AT_LEAST_ONE %>">
				<liferay-ui:message key="you-must-have-at-least-one-page" />
			</c:if>
		</liferay-ui:error>

		<liferay-ui:error exception="<%= RequiredSegmentsExperienceException.MustNotDeleteSegmentsExperienceReferencedBySegmentsExperiments.class %>" message="this-page-cannot-be-deleted-because-it-has-ab-tests-in-progress" />

		<liferay-ui:error key="resetMergeFailCountAndMerge" message="unable-to-reset-the-failure-counter-and-propagate-the-changes" />

		<%
		LayoutRevision layoutRevision = LayoutStagingUtil.getLayoutRevision(selLayout);
		%>

		<c:if test="<%= layoutRevision != null %>">
			<aui:input name="layoutSetBranchId" type="hidden" value="<%= layoutRevision.getLayoutSetBranchId() %>" />
		</c:if>

		<c:if test="<%= !group.isLayoutPrototype() %>">
			<%@ include file="/error_layout_prototype_exception.jspf" %>
		</c:if>

		<liferay-frontend:form-navigator
			formModelBean="<%= selLayout %>"
			id="<%= FormNavigatorConstants.FORM_NAVIGATOR_ID_LAYOUT %>"
			showButtons="<%= false %>"
			type="<%= FormNavigatorConstants.FormNavigatorType.SHEET_SECTIONS %>"
		/>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<c:if test="<%= layoutsAdminDisplayContext.isShowButtons() %>">
			<liferay-frontend:edit-form-buttons
				redirect="<%= backURL %>"
			/>
		</c:if>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<liferay-frontend:component
	componentId='<%= liferayPortletResponse.getNamespace() + "editLayout" %>'
	context="<%= layoutsAdminDisplayContext.getProps() %>"
	module="{EditLayout} from layout-admin-web"
/>