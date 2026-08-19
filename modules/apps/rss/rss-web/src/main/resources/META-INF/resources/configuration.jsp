<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />
<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<liferay-frontend:edit-form
	action="<%= configurationActionURL %>"
	method="post"
	name="fm"
	novalidate=""
>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />

	<liferay-frontend:edit-form-body>
		<div class="sheet-row">
			<clay:tabs
				tabsItems="<%= rssDisplayContext.getTabsItems() %>"
			>
				<clay:tabs-panel>
					<liferay-ui:error exception="<%= ValidatorException.class %>">
						<liferay-ui:message key="the-following-are-invalid-urls" />

						<%
						ValidatorException ve = (ValidatorException)errorException;

						Enumeration<String> enumeration = ve.getFailedKeys();

						while (enumeration.hasMoreElements()) {
							String url = enumeration.nextElement();
						%>

							<strong><%= HtmlUtil.escape(url) %></strong><%= enumeration.hasMoreElements() ? ", " : "." %>

						<%
						}
						%>

					</liferay-ui:error>

					<div id="<portlet:namespace />subscriptions">

						<%
						String[] urls = rssPortletInstanceConfiguration.urls();

						if (urls.length == 0) {
							urls = new String[1];

							urls[0] = StringPool.BLANK;
						}

						String[] titles = rssPortletInstanceConfiguration.titles();

						for (int i = 0; i < urls.length; i++) {
							String title = StringPool.BLANK;

							if (i < titles.length) {
								title = titles[i];
							}
						%>

							<div class="field-row lfr-form-row lfr-form-row-inline">
								<div class="row-fields">
									<aui:input cssClass="lfr-input-text-container" label="title" name='<%= "title" + i %>' value="<%= title %>" />

									<aui:input cssClass="lfr-input-text-container" label="url" name='<%= "url" + i %>' value="<%= urls[i] %>" />
								</div>
							</div>

						<%
						}
						%>

					</div>

					<aui:script type="module">
						import {autoFields} from '<%= FrontendESMUtil.buildURL(themeDisplay, "frontend-js-web", "auto_fields") %>';

						autoFields({
							contentBox: '#<portlet:namespace />subscriptions',
							fieldIndexes: '<portlet:namespace />subscriptionIndexes',
							namespace: '<portlet:namespace />',
							sortable: true,
							sortableHandle: '.field-row',
						});
					</aui:script>
				</clay:tabs-panel>

				<clay:tabs-panel>
					<div class="display-template">
						<liferay-template:template-selector
							className="<%= RSSFeed.class.getName() %>"
							displayStyle="<%= rssPortletInstanceConfiguration.displayStyle() %>"
							displayStyleGroupKey="<%= rssDisplayContext.getDisplayStyleGroupKey() %>"
							label="display-template"
							refreshURL="<%= configurationRenderURL.toString() %>"
							showEmptyOption="<%= true %>"
						/>
					</div>

					<aui:input label="num-of-entries-per-feed" name="preferences--entriesPerFeed--" type="number" value="<%= rssPortletInstanceConfiguration.entriesPerFeed() %>">
						<aui:validator errorMessage='<%= LanguageUtil.get(request, "only-integers-are-allowed") %>' name="digits" />
						<aui:validator errorMessage='<%= LanguageUtil.format(request, "only-integers-greater-than-or-equal-to-x-are-allowed", 1) %>' name="min">1</aui:validator>
					</aui:input>

					<aui:input label="num-of-expanded-entries-per-feed" name="preferences--expandedEntriesPerFeed--" type="number" value="<%= rssPortletInstanceConfiguration.expandedEntriesPerFeed() %>">
						<aui:validator errorMessage='<%= LanguageUtil.get(request, "only-integers-are-allowed") %>' name="digits" />
						<aui:validator errorMessage='<%= LanguageUtil.format(request, "only-integers-greater-than-or-equal-to-x-are-allowed", 0) %>' name="min">0</aui:validator>
					</aui:input>

					<aui:select disabled="<%= !rssPortletInstanceConfiguration.showFeedImage() %>" name="preferences--feedImageAlignment--">
						<aui:option label="left" selected='<%= rssPortletInstanceConfiguration.feedImageAlignment().equals("left") %>' />
						<aui:option label="right" selected='<%= rssPortletInstanceConfiguration.feedImageAlignment().equals("right") %>' />
					</aui:select>

					<clay:row>
						<clay:col
							md="6"
						>
							<aui:input name="preferences--showFeedTitle--" type="checkbox" value="<%= rssPortletInstanceConfiguration.showFeedTitle() %>" />

							<aui:input name="preferences--showFeedPublishedDate--" type="checkbox" value="<%= rssPortletInstanceConfiguration.showFeedPublishedDate() %>" />

							<aui:input name="preferences--showFeedDescription--" type="checkbox" value="<%= rssPortletInstanceConfiguration.showFeedDescription() %>" />
						</clay:col>

						<clay:col
							md="6"
						>

							<%
							String taglibShowFeedImageOnClick = "if (this.checked) {document." + liferayPortletResponse.getNamespace() + "fm." + liferayPortletResponse.getNamespace() + "feedImageAlignment.disabled = '';} else {document." + liferayPortletResponse.getNamespace() + "fm." + liferayPortletResponse.getNamespace() + "feedImageAlignment.disabled = 'disabled';}";
							%>

							<aui:input name="preferences--showFeedImage--" onClick="<%= taglibShowFeedImageOnClick %>" type="checkbox" value="<%= rssPortletInstanceConfiguration.showFeedImage() %>" />

							<aui:input name="preferences--showFeedItemAuthor--" type="checkbox" value="<%= rssPortletInstanceConfiguration.showFeedItemAuthor() %>" />
						</clay:col>
					</clay:row>
				</clay:tabs-panel>
			</clay:tabs>
		</div>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>