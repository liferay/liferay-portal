<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
RulesEngine rulesEngine = (RulesEngine)request.getAttribute(RulesEngine.class.getName());
%>

<c:choose>
	<c:when test="<%= themeDisplay.isSignedIn() %>">
		<c:choose>
			<c:when test="<%= rulesEngine != null %>">

				<%
				List<Fact<?>> facts = new ArrayList<Fact<?>>();

				facts.add(new Fact<KeyValuePair>("classNameIds", new KeyValuePair("classNameIds", StringUtil.merge(classNameIds))));
				facts.add(new Fact<KeyValuePair>("parentGroupId", new KeyValuePair("parentGroupId", String.valueOf(themeDisplay.getSiteGroupId()))));
				facts.add(new Fact<User>("user", user));
				facts.add(new Fact<KeyValuePair>("userCustomAttributeNames", new KeyValuePair("userCustomAttributeNames", userCustomAttributeNames)));
				facts.add(new Fact<List<AssetEntry>>("results", new ArrayList<AssetEntry>()));

				if (!rulesEngine.containsRuleDomain(domainName)) {
					RulesResourceRetriever rulesResourceRetriever = new RulesResourceRetriever(new StringResourceRetriever(rules), String.valueOf(RulesLanguage.DROOLS_RULE_LANGUAGE));

					rulesEngine.update(domainName, rulesResourceRetriever);
				}

				Map<String, ?> results = rulesEngine.execute(domainName, facts, Query.createStandardQuery());

				List<AssetEntry> assetEntries = (List<AssetEntry>)results.get("results");
				%>

				<%= user.getGreeting() %>

				<hr class="separator" />

				<c:choose>
					<c:when test="<%= !assetEntries.isEmpty() %>">

						<%
						for (AssetEntry assetEntry : assetEntries) {
							AssetRendererFactory<?> assetRendererFactory = AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(assetEntry.getClassName());

							if (assetRendererFactory == null) {
								continue;
							}

							AssetRenderer<?> assetRenderer = assetRendererFactory.getAssetRenderer(assetEntry.getClassPK());

							request.setAttribute(WebKeys.ASSET_RENDERER, assetRenderer);

							request.setAttribute(WebKeys.ASSET_RENDERER_FACTORY, assetRendererFactory);
						%>

							<strong><%= HtmlUtil.escape(assetRenderer.getTitle(locale)) %></strong>

							<br /><br />

							<liferay-asset:asset-display
								assetRenderer="<%= assetRenderer %>"
								template="<%= AssetRenderer.TEMPLATE_FULL_CONTENT %>"
							/>

							<hr class="separator" />

						<%
						}
						%>

					</c:when>
					<c:otherwise>
						<liferay-ui:message key="there-are-no-results" />
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<div class="portlet-msg-info">
					<liferay-ui:message key="no-rules-engine-is-deployed" />
				</div>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<div class="portlet-msg-info">
			<aui:a href="<%= themeDisplay.getURLSignIn() %>" label="sign-in-to-your-account" />
		</div>
	</c:otherwise>
</c:choose>