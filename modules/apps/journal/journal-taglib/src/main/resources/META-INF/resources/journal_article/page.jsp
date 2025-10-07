<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/journal_article/init.jsp" %>

<liferay-util:dynamic-include key="com.liferay.journal.taglib#/journal_article/page.jsp#pre" />

<%
JournalArticle article = (JournalArticle)request.getAttribute("liferay-journal:journal-article:article");
JournalArticleDisplay articleDisplay = (JournalArticleDisplay)request.getAttribute("liferay-journal:journal-article:articleDisplay");
boolean dataAnalyticsTrackingEnabled = GetterUtil.getBoolean(request.getAttribute("liferay-journal:journal-article:dataAnalyticsTrackingEnabled"));
PortletURL paginationURL = (PortletURL)request.getAttribute("liferay-journal:journal-article:paginationURL");
String viewMode = ParamUtil.getString(PortalUtil.getOriginalServletRequest(request), "p_l_mode", Constants.VIEW);
String wrapperCssClass = (String)request.getAttribute("liferay-journal:journal-article:wrapperCssClass");
%>

<c:choose>
	<c:when test="<%= (article != null) && article.isExpired() && !viewMode.equals(Constants.PREVIEW) %>">
		<div class="alert alert-warning">
			<liferay-ui:message arguments="<%= HtmlUtil.escape(article.getTitle(locale)) %>" key="x-is-expired" />
		</div>
	</c:when>
	<c:when test="<%= articleDisplay == null %>">
		<div class="alert alert-warning">
			<liferay-ui:message key="article-is-not-displayable" />
		</div>
	</c:when>
	<c:otherwise>
		<div class="journal-content-article <%= Validator.isNotNull(wrapperCssClass) ? wrapperCssClass : StringPool.BLANK %>" <%= dataAnalyticsTrackingEnabled ? String.format("data-analytics-asset-id=\"%s\" data-analytics-asset-title=\"%s\" data-analytics-asset-type=\"web-content\" data-analytics-external-reference-code=\"%s\" data-analytics-web-content-resource-pk=\"%s\"", articleDisplay.getArticleId(), HtmlUtil.escapeAttribute(articleDisplay.getTitle()), articleDisplay.getExternalReferenceCode(), articleDisplay.getResourcePrimKey()) : "" %>>
			<c:if test='<%= GetterUtil.getBoolean((String)request.getAttribute("liferay-journal:journal-article:showTitle")) %>'>
				<clay:row>
					<clay:col>
						<h3 class="m-0"><%= HtmlUtil.escape(articleDisplay.getTitle()) %></h3>
					</clay:col>
				</clay:row>

				<hr class="mb-4 separator" />
			</c:if>

			<%= articleDisplay.getContent() %>

			<c:if test="<%= articleDisplay.isPaginate() && (paginationURL != null) %>">
				<div>
					<react:component
						module="{JournalArticlePagination} from journal-taglib"
						props='<%=
							HashMapBuilder.<String, Object>put(
								"activePage", articleDisplay.getCurrentPage()
							).put(
								"namespace", liferayPortletResponse.getNamespace()
							).put(
								"paginationURL", String.valueOf(paginationURL)
							).put(
								"totalPages", articleDisplay.getNumberOfPages()
							).build()
						%>'
					/>
				</div>
			</c:if>
		</div>

		<%
		List<AssetTag> assetTags = AssetTagLocalServiceUtil.getTags(JournalArticleDisplay.class.getName(), articleDisplay.getResourcePrimKey());

		PortalUtil.setPageKeywords(ListUtil.toString(assetTags, AssetTag.NAME_ACCESSOR), request);
		%>

	</c:otherwise>
</c:choose>

<liferay-util:dynamic-include key="com.liferay.journal.taglib#/journal_article/page.jsp#post" />