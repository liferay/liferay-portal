<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.service.GroupLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.servlet.SessionErrors" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.search.tuning.rankings.constants.ResultRankingsConstants" %><%@
page import="com.liferay.portal.search.tuning.rankings.web.internal.constants.ResultRankingsPortletKeys" %><%@
page import="com.liferay.portal.search.tuning.rankings.web.internal.display.context.RankingEntryDisplayContext" %><%@
page import="com.liferay.portal.search.tuning.rankings.web.internal.display.context.RankingPortletDisplayContext" %><%@
page import="com.liferay.portal.search.tuning.rankings.web.internal.exception.DuplicateQueryStringException" %><%@
page import="com.liferay.portal.search.tuning.rankings.web.internal.exception.NotApplicableStatusException" %>

<%@ page import="java.util.List" %><%@
page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<liferay-ui:error embed="<%= false %>" exception="<%= DuplicateQueryStringException.class %>">
	<liferay-ui:message arguments="<%= (List<String>)SessionErrors.get(renderRequest, DuplicateQueryStringException.class) %>" key="the-following-entries-could-not-be-updated-because-there-are-one-or-more-rankings-with-the-same-search-query-and-or-aliases-and-scope-that-are-already-active-x" />
</liferay-ui:error>

<c:if test="<%= SessionErrors.contains(renderRequest, NotApplicableStatusException.class) %>">
	<aui:script>
		Liferay.Util.openToast({
			message:
				'<liferay-ui:message arguments="<%= (List<String>)SessionErrors.get(renderRequest, NotApplicableStatusException.class) %>" key="the-selected-action-could-not-be-performed-on-the-following-rankings-because-they-have-a-not-applicable-status-x" />',
			title: '<liferay-ui:message key="warning" />',
			toastProps: {
				autoClose: 5000,
			},
			type: 'warning',
		});
	</aui:script>
</c:if>

<%
RankingPortletDisplayContext rankingPortletDisplayContext = (RankingPortletDisplayContext)request.getAttribute(ResultRankingsPortletKeys.RESULT_RANKINGS_DISPLAY_CONTEXT);
%>

<portlet:actionURL name="/result_rankings/edit_ranking" var="activateResultsRankingEntryURL">
	<portlet:param name="<%= Constants.CMD %>" value="<%= ResultRankingsConstants.ACTION_ACTIVATE %>" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<portlet:actionURL name="/result_rankings/edit_ranking" var="deactivateResultsRankingEntryURL">
	<portlet:param name="<%= Constants.CMD %>" value="<%= ResultRankingsConstants.ACTION_DEACTIVATE %>" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<portlet:actionURL name="/result_rankings/edit_ranking" var="deleteResultsRankingEntryURL">
	<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.DELETE %>" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<clay:management-toolbar
	actionDropdownItems="<%= rankingPortletDisplayContext.getActionDropdownItems() %>"
	additionalProps='<%=
		HashMapBuilder.<String, Object>put(
			"activateResultsRankingEntryURL", activateResultsRankingEntryURL
		).put(
			"deactivateResultsRankingEntryURL", deactivateResultsRankingEntryURL
		).put(
			"deleteResultsRankingEntryURL", deleteResultsRankingEntryURL
		).build()
	%>'
	clearResultsURL="<%= rankingPortletDisplayContext.getClearResultsURL() %>"
	creationMenu="<%= rankingPortletDisplayContext.getCreationMenu() %>"
	disabled="<%= rankingPortletDisplayContext.isDisabledManagementBar() %>"
	filterDropdownItems="<%= rankingPortletDisplayContext.getFilterItemsDropdownItems() %>"
	filterLabelItems="<%= rankingPortletDisplayContext.getFilterLabelItems() %>"
	itemsTotal="<%= rankingPortletDisplayContext.getTotalItems() %>"
	propsTransformer="{RankingPortletManagementToolbarPropsTransformer} from portal-search-tuning-rankings-web"
	searchActionURL="<%= rankingPortletDisplayContext.getSearchActionURL() %>"
	searchContainerId="resultsRankingEntries"
	searchFormName="fm"
	selectable="<%= true %>"
	showCreationMenu="<%= rankingPortletDisplayContext.isShowCreationMenu() %>"
	showSearch="<%= true %>"
	sortingOrder="<%= rankingPortletDisplayContext.getOrderByType() %>"
	sortingURL="<%= rankingPortletDisplayContext.getSortingURL() %>"
/>

<clay:container-fluid
	size="xxxl"
>
	<aui:form method="post" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

		<liferay-ui:search-container
			id="resultsRankingEntries"
			searchContainer="<%= rankingPortletDisplayContext.getSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.portal.search.tuning.rankings.web.internal.display.context.RankingEntryDisplayContext"
				keyProperty="uid"
				modelVar="rankingEntryDisplayContextModelVar"
			>

				<%
				RankingEntryDisplayContext rankingEntryDisplayContext = rankingEntryDisplayContextModelVar;
				%>

				<portlet:renderURL var="rowURL">
					<portlet:param name="mvcRenderCommandName" value="/result_rankings/edit_results_rankings" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
					<portlet:param name="resultsRankingUid" value="<%= rankingEntryDisplayContext.getUid() %>" />
					<portlet:param name="aliases" value="<%= rankingEntryDisplayContext.getAliases() %>" />
					<portlet:param name="companyId" value="<%= String.valueOf(themeDisplay.getCompanyId()) %>" />
					<portlet:param name="keywords" value="<%= rankingEntryDisplayContext.getKeywords() %>" />
					<portlet:param name="status" value="<%= rankingEntryDisplayContext.getStatus() %>" />
				</portlet:renderURL>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="search-query"
				>
					<div class="list-group-title">
						<a href="<%= rowURL %>">
							<%= HtmlUtil.escape(rankingEntryDisplayContext.getKeywords()) %>
						</a>
					</div>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="aliases"
				>
					<div class="list-group-subtext">
						<%= HtmlUtil.escape(rankingEntryDisplayContext.getAliases()) %>
					</div>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-cell-minw-150"
					name="pinned-results"
					value="<%= rankingEntryDisplayContext.getPinnedResultsCount() %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-cell-minw-150"
					name="hidden-results"
					value="<%= rankingEntryDisplayContext.getHiddenResultsCount() %>"
				/>

				<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-6368") %>'>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smallest table-cell-minw-150"
						name="scope"
					>
						<c:choose>
							<c:when test="<%= Validator.isNotNull(rankingEntryDisplayContext.getGroupExternalReferenceCode()) %>">

								<%
								Group group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(rankingEntryDisplayContext.getGroupExternalReferenceCode(), themeDisplay.getCompanyId());
								%>

								<span class="lfr-portal-tooltip" data-title="<%= Validator.isNotNull(group) ? HtmlUtil.escape(group.getDescriptiveName(locale)) : LanguageUtil.get(request, "the-site-associated-with-this-ranking-was-deleted") %>">
									<liferay-ui:message key="site" />
								</span>
							</c:when>
							<c:when test="<%= Validator.isNotNull(rankingEntryDisplayContext.getSXPBlueprintExternalReferenceCode()) %>">

								<%
								String sxpBlueprintTitle = rankingEntryDisplayContext.getSXPBlueprintTitle();
								%>

								<span class="lfr-portal-tooltip" data-title="<%= Validator.isNotNull(sxpBlueprintTitle) ? HtmlUtil.escape(sxpBlueprintTitle) : LanguageUtil.get(request, "the-blueprint-associated-with-this-ranking-was-deleted") %>">
									<liferay-ui:message key="blueprint" />
								</span>
							</c:when>
							<c:otherwise>
								<liferay-ui:message key="everything" />
							</c:otherwise>
						</c:choose>
					</liferay-ui:search-container-column-text>
				</c:if>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-cell-minw-150"
					name="status"
				>
					<c:choose>
						<c:when test="<%= Objects.equals(rankingEntryDisplayContext.getStatus(), ResultRankingsConstants.STATUS_NOT_APPLICABLE) %>">
							<div class="label label-warning">
								<span class="label-item label-item-expand">
									<liferay-ui:message key="<%= rankingEntryDisplayContext.getStatus() %>" />
								</span>
							</div>
						</c:when>
						<c:otherwise>
							<div class="label <%= Objects.equals(rankingEntryDisplayContext.getStatus(), ResultRankingsConstants.STATUS_ACTIVE) ? "label-success" : "label-secondary" %>">
								<span class="label-item label-item-expand">
									<liferay-ui:message key="<%= rankingEntryDisplayContext.getStatus() %>" />
								</span>
							</div>
						</c:otherwise>
					</c:choose>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-jsp
					cssClass="entry-action-column"
					path="/view_results_rankings_entry_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>