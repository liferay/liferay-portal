<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CookiesBannerConfigurationDisplayContext cookiesBannerConfigurationDisplayContext = (CookiesBannerConfigurationDisplayContext)request.getAttribute(CookiesBannerWebKeys.COOKIES_BANNER_CONFIGURATION_DISPLAY_CONTEXT);
%>

<clay:row>

	<%
	String alertMessage = ParamUtil.getString(request, "alertMessage");
	%>

	<c:if test="<%= alertMessage != StringPool.BLANK %>">
		<clay:col
			size="12"
		>
			<clay:alert
				displayType='<%= ParamUtil.getString(request, "alertDisplayType", "info") %>'
				message="<%= alertMessage %>"
			/>
		</clay:col>
	</c:if>

	<clay:col
		cssClass="mb-3"
		size="12"
	>
		<p>
			<%= cookiesBannerConfigurationDisplayContext.getDescription(locale) %>

			<clay:link
				href="<%= cookiesBannerConfigurationDisplayContext.getCookiePolicyLink() %>"
				label="<%= cookiesBannerConfigurationDisplayContext.getLinkDisplayText(locale) %>"
				target="_blank"
			/>
		</p>
	</clay:col>

	<clay:col
		size="12"
	>

		<%
		for (ConsentCookieType requiredConsentCookieType : cookiesBannerConfigurationDisplayContext.getRequiredConsentCookieTypes()) {
		%>

			<clay:content-row
				noGutters="true"
				verticalAlign="center"
			>
				<clay:content-col
					expand="<%= true %>"
				>
					<h2><%= cookiesBannerConfigurationDisplayContext.getCookieTitle(requiredConsentCookieType.getName(), request) %></h2>
				</clay:content-col>

				<clay:content-col>
					<span class="pr-2 text-primary"><liferay-ui:message key="always-active" /></span>
				</clay:content-col>
			</clay:content-row>

			<clay:content-row
				cssClass="mb-3"
			>
				<p><%= requiredConsentCookieType.getDescription(locale) %></p>
			</clay:content-row>

		<%
		}

		for (ConsentCookieType optionalConsentCookieType : cookiesBannerConfigurationDisplayContext.getOptionalConsentCookieTypes()) {
			if (FeatureFlagManagerUtil.isEnabled("LPD-51356") && optionalConsentCookieType.isHideFromEndUser()) {
				continue;
			}
		%>

			<clay:content-row
				noGutters="true"
				verticalAlign="center"
			>
				<clay:content-col
					expand="<%= true %>"
				>
					<h2><%= cookiesBannerConfigurationDisplayContext.getCookieTitle(optionalConsentCookieType.getName(), request) %></h2>
				</clay:content-col>

				<clay:content-col>
					<label class="toggle-switch">
						<span class="toggle-switch-check-bar">
							<input class="toggle-switch-check" data-cookie-key="<%= optionalConsentCookieType.getName() %>" data-prechecked="<%= optionalConsentCookieType.isPrechecked() %>" disabled type="checkbox" />

							<span aria-hidden="true" class="toggle-switch-bar">
								<span class="toggle-switch-handle"></span>
							</span>
						</span>
					</label>
				</clay:content-col>
			</clay:content-row>

			<clay:content-row
				cssClass="mb-3"
			>
				<p><%= optionalConsentCookieType.getDescription(locale) %></p>
			</clay:content-row>

		<%
		}
		%>

	</clay:col>
</clay:row>

<c:if test="<%= cookiesBannerConfigurationDisplayContext.isShowButtons() %>">
	<clay:row
		cssClass="d-flex justify-content-end"
	>
		<clay:content-row
			noGutters="true"
			verticalAlign="center"
		>
			<c:if test="<%= cookiesBannerConfigurationDisplayContext.isIncludeDeclineAllButton() %>">
				<clay:content-col>
					<clay:button
						displayType="secondary"
						id='<%= liferayPortletResponse.getNamespace() + "useNecessaryCookiesOnlyButton" %>'
						label='<%= LanguageUtil.get(request, "use-necessary-cookies-only") %>'
						small="<%= true %>"
					/>
				</clay:content-col>
			</c:if>

			<clay:content-col>
				<clay:button
					displayType="secondary"
					id='<%= liferayPortletResponse.getNamespace() + "acceptSelectedButton" %>'
					label='<%= LanguageUtil.get(request, "accept-selected") %>'
					small="<%= true %>"
				/>
			</clay:content-col>

			<clay:content-col>
				<clay:button
					displayType="secondary"
					id='<%= liferayPortletResponse.getNamespace() + "acceptAllButton" %>'
					label='<%= LanguageUtil.get(request, "accept-all") %>'
					small="<%= true %>"
				/>
			</clay:content-col>
		</clay:content-row>
	</clay:row>
</c:if>