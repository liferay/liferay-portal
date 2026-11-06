<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
TrashContainerModelDisplayContext trashContainerModelDisplayContext = new TrashContainerModelDisplayContext(liferayPortletRequest, liferayPortletResponse);

List<BreadcrumbEntry> breadcrumbEntries = trashDisplayContext.getContainerModelBreadcrumbEntries(trashContainerModelDisplayContext.getContainerModelClassName(), trashContainerModelDisplayContext.getContainerModelId(), trashContainerModelDisplayContext.getContainerURL());

String lastElementBreadcrumbTitle = StringUtil.upperCaseFirstLetter(trashDisplayContext.getLastElementBreadcrumbTitle(breadcrumbEntries));
%>

<portlet:actionURL name="viewContainerModel" var="viewContainerModelURL">
	<portlet:param name="mvcPath" value="/view_container_model.jsp" />
</portlet:actionURL>

<p class="c-px-4 c-py-3 text-secondary">
	<liferay-ui:message arguments="<%= trashContainerModelDisplayContext.getMissingContainerMessageArguments() %>" key="the-original-x-of-this-file-does-not-exist-anymore" translateArguments="<%= false %>" />
</p>

<liferay-frontend:edit-form
	action="<%= viewContainerModelURL %>"
	cssClass="c-px-4 container-fluid container-fluid-max-xl"
	method="post"
	name="selectContainerFm"
>
	<liferay-site-navigation:breadcrumb
		breadcrumbEntries="<%= breadcrumbEntries %>"
	/>

	<liferay-ui:search-container
		emptyResultsMessage='<%= LanguageUtil.format(request, "no-folders-were-found-in-x", lastElementBreadcrumbTitle) %>'
		searchContainer="<%= trashContainerModelDisplayContext.getSearchContainer() %>"
		total="<%= trashContainerModelDisplayContext.getContainerModelsCount() %>"
	>
		<liferay-ui:search-container-results
			results="<%= trashContainerModelDisplayContext.getContainerModels() %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.ContainerModel"
			keyPropertyName="containerModelId"
			modelVar="curContainerModel"
		>

			<%
			long curContainerModelId = curContainerModel.getContainerModelId();

			PortletURL containerURL = PortletURLBuilder.create(
				trashContainerModelDisplayContext.getContainerURL()
			).setParameter(
				"containerModelId", curContainerModelId
			).buildPortletURL();
			%>

			<liferay-ui:search-container-column-text
				name="<%= LanguageUtil.get(request, trashContainerModelDisplayContext.getContainerModelName()) %>"
			>
				<c:choose>
					<c:when test="<%= curContainerModel.getContainerModelId() > 0 %>">
						<clay:link
							cssClass="text-dark"
							displayType="primary"
							href="<%= containerURL.toString() %>"
							label="<%= curContainerModel.getContainerModelName() %>"
							weight="semi-bold"
						/>
					</c:when>
					<c:otherwise>
						<%= curContainerModel.getContainerModelName() %>
					</c:otherwise>
				</c:choose>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				cssClass="table-column-text-end"
			>
				<clay:button
					cssClass="selector-button"
					data-classname="<%= trashContainerModelDisplayContext.getClassName() %>"
					data-classpk="<%= trashContainerModelDisplayContext.getClassPK() %>"
					data-containermodelid="<%= curContainerModelId %>"
					data-redirect="<%= trashContainerModelDisplayContext.getRedirect() %>"
					displayType="secondary"
					label="select"
				/>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>

		<aui:button-row cssClass="position-fixed">
			<clay:button
				cssClass="selector-button"
				data-classname="<%= trashContainerModelDisplayContext.getClassName() %>"
				data-classpk="<%= trashContainerModelDisplayContext.getClassPK() %>"
				data-containermodelid="<%= trashContainerModelDisplayContext.getContainerModelId() %>"
				data-redirect="<%= trashContainerModelDisplayContext.getRedirect() %>"
				displayType="secondary"
				label='<%= LanguageUtil.format(request, "select-x", lastElementBreadcrumbTitle) %>'
			/>
		</aui:button-row>
	</liferay-ui:search-container>
</liferay-frontend:edit-form>