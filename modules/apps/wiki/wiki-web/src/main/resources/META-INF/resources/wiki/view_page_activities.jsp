<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/wiki/init.jsp" %>

<liferay-util:include page="/wiki/top_links.jsp" servletContext="<%= application %>" />

<liferay-util:include page="/wiki/page_tabs.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="history" />
</liferay-util:include>

<liferay-util:include page="/wiki/page_tabs_history.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs3" value="activities" />
</liferay-util:include>

<%
WikiNode node = (WikiNode)request.getAttribute(WikiWebKeys.WIKI_NODE);
WikiPage wikiPage = (WikiPage)request.getAttribute(WikiWebKeys.WIKI_PAGE);

PortletURL portletURL = PortletURLBuilder.createActionURL(
	renderResponse
).setParameter(
	"nodeId", node.getNodeId()
).setParameter(
	"title", wikiPage.getTitle()
).buildPortletURL();

PortalUtil.addPortletBreadcrumbEntry(request, wikiPage.getTitle(), portletURL.toString());

portletURL.setParameter(ActionRequest.ACTION_NAME, "/wiki/view_page_history");
portletURL.setParameter("redirect", currentURL);

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "history[record]"), portletURL.toString());
%>

<div class="page-activities">
	<liferay-ui:search-container
		iteratorURL='<%=
			PortletURLBuilder.createRenderURL(
				renderResponse
			).setMVCRenderCommandName(
				"/wiki/view_page_activities"
			).setRedirect(
				currentURL
			).setParameter(
				"nodeId", node.getNodeId()
			).setParameter(
				"title", wikiPage.getTitle()
			).buildPortletURL()
		%>'
	>

		<%
		WikiSocialActivityHelper wikiSocialActivityHelper = new WikiSocialActivityHelper(wikiRequestHelper);
		%>

		<liferay-ui:search-container-results
			results="<%= wikiSocialActivityHelper.getApprovedSocialActivities(wikiPage, searchContainer.getStart(), searchContainer.getEnd()) %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.social.kernel.model.SocialActivity"
			escapedModel="<%= true %>"
			keyProperty="activityId"
			modelVar="socialActivity"
		>

			<%
			JSONObject extraDataJSONObject = JSONFactoryUtil.createJSONObject(socialActivity.getExtraData());

			double version = extraDataJSONObject.getDouble("version", 0);

			WikiPage socialActivityWikiPage = null;

			if (version == 0) {
				socialActivityWikiPage = WikiPageLocalServiceUtil.fetchPage(wikiPage.getNodeId(), wikiPage.getTitle());
			}
			else {
				socialActivityWikiPage = WikiPageLocalServiceUtil.fetchPage(wikiPage.getNodeId(), wikiPage.getTitle(), version);
			}
			%>

			<liferay-ui:search-container-column-text
				name="activity"
			>
				<c:choose>
					<c:when test="<%= socialActivity.getType() == SocialActivityConstants.TYPE_ADD_COMMENT %>">
						<liferay-ui:icon
							label="<%= true %>"
							message="<%= wikiSocialActivityHelper.getSocialActivityDescription(wikiPage, socialActivity, extraDataJSONObject, resourceBundle) %>"
						/>
					</c:when>
					<c:when test="<%= wikiSocialActivityHelper.isSocialActivitySupported(socialActivity) %>">
						<liferay-ui:icon
							icon="<%= wikiSocialActivityHelper.getSocialActivityIcon(socialActivity) %>"
							label="<%= true %>"
							markupView="lexicon"
							message="<%= wikiSocialActivityHelper.getSocialActivityDescription(wikiPage, socialActivity, extraDataJSONObject, resourceBundle) %>"
						/>
					</c:when>
				</c:choose>

				<c:if test="<%= socialActivity.getType() == WikiActivityKeys.UPDATE_PAGE %>">
					<c:if test="<%= (socialActivityWikiPage != null) && (socialActivityWikiPage.getStatus() != WorkflowConstants.STATUS_APPROVED) %>">
						<span class="activity-status"><liferay-ui:message key="<%= WorkflowConstants.getStatusLabel(socialActivityWikiPage.getStatus()) %>" /></span>
					</c:if>

					<c:if test="<%= (socialActivityWikiPage != null) && Validator.isNotNull(socialActivityWikiPage.getSummary()) %>">
						<em class="activity-summary"><%= StringPool.QUOTE + HtmlUtil.escape(socialActivityWikiPage.getSummary()) + StringPool.QUOTE %></em>
					</c:if>
				</c:if>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-date
				name="date"
				value="<%= new Date(socialActivity.getCreateDate()) %>"
			/>

			<%
			String jspPath = null;

			if (wikiSocialActivityHelper.isSocialActivitySupported(socialActivity)) {
				jspPath = wikiSocialActivityHelper.getSocialActivityActionJSP(socialActivity, extraDataJSONObject);
			}
			%>

			<c:choose>
				<c:when test="<%= Validator.isNotNull(jspPath) %>">
					<liferay-ui:search-container-column-jsp
						align="right"
						cssClass="entry-action"
						path="<%= jspPath %>"
					/>
				</c:when>
				<c:otherwise>
					<liferay-ui:search-container-column-text
						name=""
						value=""
					/>
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</div>