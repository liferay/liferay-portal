<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String backURL = ParamUtil.getString(request, "backURL", redirect);

MySavedContentDisplayContext mySavedContentDisplayContext = new MySavedContentDisplayContext(liferayPortletRequest, liferayPortletResponse);

if (Validator.isNotNull(backURL)) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(backURL);
}
%>

<clay:container-fluid>
	<liferay-ui:search-container
		emptyResultsMessage="no-saved-content-was-found"
		searchContainer="<%= mySavedContentDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.saved.content.model.SavedContentEntry"
			escapedModel="<%= true %>"
			keyPropertyName="savedContentEntryId"
			modelVar="savedContentEntry"
		>

			<%
			AssetRenderer<?> assetRenderer = mySavedContentDisplayContext.getAssetRenderer(savedContentEntry.getClassName(), savedContentEntry.getClassPK());

			String assetTitle = mySavedContentDisplayContext.getAssetTitle(assetRenderer);
			%>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
			>
				<p class="list-group-title"><%= assetTitle %></p>
				<p class="list-group-subtitle"><%= ResourceActionsUtil.getModelResource(locale, savedContentEntry.getClassName()) %></p>

				<c:if test="<%= !mySavedContentDisplayContext.isVisible(assetRenderer) %>">
					<liferay-portal-workflow:status
						showStatusLabel="<%= false %>"
						status="<%= assetRenderer.getStatus() %>"
					/>
				</c:if>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-smallest table-column-text-end"
			>
				<c:if test="<%= mySavedContentDisplayContext.isVisible(assetRenderer) %>">
					<clay:link
						aria-label='<%= LanguageUtil.format(request, "open-x-in-a-new-tab", HtmlUtil.escapeAttribute(assetTitle)) %>'
						borderless="<%= true %>"
						cssClass="lfr-portal-tooltip mr-2"
						displayType="secondary"
						href="<%= mySavedContentDisplayContext.getURL(assetRenderer) %>"
						icon="shortcut"
						monospaced="<%= true %>"
						small="<%= true %>"
						target="_blank"
						title='<%= LanguageUtil.format(request, "open-x-in-a-new-tab", HtmlUtil.escapeAttribute(assetTitle)) %>'
						type="button"
					/>
				</c:if>

				<clay:link
					aria-label='<%= LanguageUtil.format(request, "remove-x", HtmlUtil.escapeAttribute(assetTitle)) %>'
					borderless="<%= true %>"
					cssClass="lfr-portal-tooltip"
					displayType="secondary"
					href="<%= mySavedContentDisplayContext.getRemoveSavedContentURL(savedContentEntry.getClassName(), savedContentEntry.getClassPK()) %>"
					icon="trash"
					monospaced="<%= true %>"
					small="<%= true %>"
					title='<%= LanguageUtil.format(request, "remove-x", HtmlUtil.escapeAttribute(assetTitle)) %>'
					type="button"
				/>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>