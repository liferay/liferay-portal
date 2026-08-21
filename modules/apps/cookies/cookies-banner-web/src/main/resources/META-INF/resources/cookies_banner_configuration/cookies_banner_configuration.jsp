<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CookiesBannerConfigurationDisplayContext cookiesBannerConfigurationDisplayContext = (CookiesBannerConfigurationDisplayContext)request.getAttribute(CookiesBannerWebKeys.COOKIES_BANNER_CONFIGURATION_DISPLAY_CONTEXT);

if (!portletName.equals(UsersAdminPortletKeys.MY_ACCOUNT)) {
	PortletURL viewURL = renderResponse.createRenderURL();

	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(ParamUtil.getString(request, "backURL", viewURL.toString()));
	portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

	User selUser = PortalUtil.getSelectedUser(request);

	renderResponse.setTitle((selUser == null) ? LanguageUtil.get(request, "add-user") : LanguageUtil.format(request, "edit-user-x", selUser.getFullName(), false));
}
%>

<clay:row>

	<%
	String alertMessage = ParamUtil.getString(request, "alertMessage");
	%>

	<c:if test="<%= Validator.isNotNull(alertMessage) %>">
		<clay:col
			size="12"
		>
			<clay:alert
				displayType='<%= HtmlUtil.escapeAttribute(ParamUtil.getString(request, "alertDisplayType", "info")) %>'
				message="<%= HtmlUtil.escape(alertMessage) %>"
			/>
		</clay:col>
	</c:if>

	<clay:col
		cssClass="mb-3"
		size="12"
	>
		<p>
			<%= HtmlUtil.escape(cookiesBannerConfigurationDisplayContext.getDescription(locale)) %>

			<clay:link
				href="<%= HtmlUtil.escapeHREF(cookiesBannerConfigurationDisplayContext.getCookiePolicyLink()) %>"
				label="<%= HtmlUtil.escape(cookiesBannerConfigurationDisplayContext.getLinkDisplayText(locale)) %>"
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
					<h3><%= HtmlUtil.escape(cookiesBannerConfigurationDisplayContext.getCookieTitle(requiredConsentCookieType.getName(), request)) %></h3>
				</clay:content-col>

				<clay:content-col>
					<span class="pr-2 text-primary" role="status">
						<span class="sr-only"><liferay-ui:message key="status" />: </span>
						<liferay-ui:message key="always-active" />
					</span>
				</clay:content-col>
			</clay:content-row>

			<clay:content-row
				cssClass="mb-3"
			>
				<p><%= HtmlUtil.escape(requiredConsentCookieType.getDescription(locale)) %></p>
			</clay:content-row>

		<%
		}
		%>

		<fieldset>
			<legend class="sr-only"><liferay-ui:message key="optional-cookies" /></legend>

			<%
			for (ConsentCookieType optionalConsentCookieType : cookiesBannerConfigurationDisplayContext.getOptionalConsentCookieTypes()) {
				if (optionalConsentCookieType.isHideFromEndUser()) {
					continue;
				}

				String optionalCookieTitle = HtmlUtil.escape(cookiesBannerConfigurationDisplayContext.getCookieTitle(optionalConsentCookieType.getName(), request));
			%>

				<clay:content-row
					noGutters="true"
					verticalAlign="center"
				>
					<clay:content-col
						expand="<%= true %>"
					>
						<h3><%= optionalCookieTitle %></h3>
					</clay:content-col>

					<clay:content-col>
						<label class="toggle-switch">
							<span class="toggle-switch-check-bar">
								<input aria-label="<%= optionalCookieTitle %>" class="toggle-switch-check" data-cookie-key="<%= optionalConsentCookieType.getName() %>" data-prechecked="<%= optionalConsentCookieType.isPrechecked() %>" disabled type="checkbox" />

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
					<p><%= HtmlUtil.escape(optionalConsentCookieType.getDescription(locale)) %></p>
				</clay:content-row>

			<%
			}
			%>

		</fieldset>

		<c:if test="<%= cookiesBannerConfigurationDisplayContext.isStoreConsent() %>">
			<clay:content-row
				noGutters="true"
				verticalAlign="center"
			>
				<clay:content-col
					expand="<%= true %>"
				>
					<h3>
						<liferay-ui:message key="cookie-store-consent" />
					</h3>
				</clay:content-col>

				<clay:content-col>
					<label class="toggle-switch">
						<span class="toggle-switch-check-bar">
							<input aria-label="<liferay-ui:message key="cookie-store-consent" />" class="toggle-switch-check" disabled id="<portlet:namespace />storeConsent" name="<portlet:namespace />storeConsent" type="checkbox" />

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
				<p><liferay-ui:message key="cookie-store-consent-help" /></p>
			</clay:content-row>
		</c:if>
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
					aria-label='<%= LanguageUtil.get(request, "accept-selected-cookies") %>'
					displayType="secondary"
					id='<%= liferayPortletResponse.getNamespace() + "acceptSelectedButton" %>'
					label='<%= LanguageUtil.get(request, "accept-selected") %>'
					small="<%= true %>"
				/>
			</clay:content-col>

			<clay:content-col>
				<clay:button
					aria-label='<%= LanguageUtil.get(request, "accept-all-cookies") %>'
					displayType="secondary"
					id='<%= liferayPortletResponse.getNamespace() + "acceptAllButton" %>'
					label='<%= LanguageUtil.get(request, "accept-all") %>'
					small="<%= true %>"
				/>
			</clay:content-col>
		</clay:content-row>
	</clay:row>
</c:if>