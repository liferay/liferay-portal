<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String className = (String)request.getAttribute("contact_information.jsp-className");
long classPK = (long)request.getAttribute("contact_information.jsp-classPK");

String emptyResultsMessage = ParamUtil.getString(request, "emptyResultsMessage");

List<Website> websites = WebsiteServiceUtil.getWebsites(className, classPK);
%>

<clay:content-row
	cssClass="sheet-subtitle"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<span class="heading-text"><liferay-ui:message key="websites" /></span>
	</clay:content-col>

	<clay:content-col>
		<span class="heading-end">
			<clay:link
				aria-label='<%= LanguageUtil.format(request, "add-x", "websites") %>'
				cssClass="add-website-link btn btn-secondary btn-sm"
				displayType="null"
				href='<%=
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCPath(
						"/common/edit_website.jsp"
					).setRedirect(
						currentURL
					).setParameter(
						"className", className
					).setParameter(
						"classPK", classPK
					).buildString()
				%>'
				label="add"
				role="button"
			/>
		</span>
	</clay:content-col>
</clay:content-row>

<liferay-ui:search-container
	compactEmptyResultsMessage="<%= true %>"
	cssClass="lfr-search-container-wrapper"
	curParam="websitesCur"
	deltaParam="websitesDelta"
	emptyResultsMessage="<%= emptyResultsMessage %>"
	headerNames="website,type,"
	id="websitesSearchContainer"
	iteratorURL="<%= currentURLObj %>"
	total="<%= websites.size() %>"
>
	<liferay-ui:search-container-results
		calculateStartAndEnd="<%= true %>"
		results="<%= websites %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.Website"
		escapedModel="<%= true %>"
		keyPropertyName="websiteId"
		modelVar="website"
	>
		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="website"
			property="url"
		/>

		<%
		ListType listType = website.getListType();
		%>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand-small"
			name="type"
			value="<%= LanguageUtil.get(request, listType.getName()) %>"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand-smaller"
		>
			<c:if test="<%= website.isPrimary() %>">
				<clay:label
					displayType="primary"
					label="primary"
				/>
			</c:if>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-jsp
			cssClass="entry-action-column"
			path="/common/website_action.jsp"
		/>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>