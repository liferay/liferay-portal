<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CookiesBannerDisplayContext cookiesBannerDisplayContext = (CookiesBannerDisplayContext)request.getAttribute(CookiesBannerWebKeys.COOKIES_BANNER_DISPLAY_CONTEXT);
%>

<c:if test="<%= (boolean)request.getAttribute(CookiesBannerWebKeys.FLOATING_ICON_ENABLED) %>">

	<%
	long customFloatingIconImageId = (long)request.getAttribute(CookiesBannerWebKeys.CUSTOM_FLOATING_ICON_IMAGE_ID);
	String floatingIcon = (String)request.getAttribute(CookiesBannerWebKeys.FLOATING_ICON);
	%>

	<c:choose>
		<c:when test='<%= Objects.equals("custom", floatingIcon) %>'>
			<img alt="<liferay-ui:message escapeAttribute="<%= true %>" key="floating-icon" />" class="custom-floating-icon-image d-none ml-3" id="<portlet:namespace />floatingIconButton" name="<portlet:namespace />floatingIconButton" src="<%= (customFloatingIconImageId == 0) ? themeDisplay.getPathThemeImages() + "/spacer.png" : themeDisplay.getPathImage() + "/floating_icon?img_id=" + customFloatingIconImageId %>" />
		</c:when>
		<c:otherwise>
			<clay:button
				cssClass="align-items-center d-none floating-icon-button justify-content-center ml-3 rounded-circle text-white"
				displayType="unstyled"
				id='<%= liferayPortletResponse.getNamespace() + "floatingIconButton" %>'
			>
				<clay:icon
					symbol="<%= floatingIcon %>"
				/>
			</clay:button>
		</c:otherwise>
	</c:choose>
</c:if>

<div aria-labelledby="<portlet:namespace />cookiesBannerTitle" aria-modal="true" class="cookies-banner cookies-banner-bottom" role="dialog">
	<clay:container-fluid
		cssClass="container-view"
	>
		<clay:row>
			<clay:content-row
				cssClass="autofit-float-sm-down px-2 px-md-0"
				noGutters="true"
				verticalAlign="center"
			>
				<clay:content-col
					expand="<%= true %>"
				>
					<h2 class="mb-2 text-5 text-weight-semi-bold" id="<portlet:namespace />cookiesBannerTitle">
						<%= HtmlUtil.escape(cookiesBannerDisplayContext.getTitle(locale)) %>
					</h2>

					<p class="mb-0">
						<%= HtmlUtil.escape(cookiesBannerDisplayContext.getContent(locale)) %>

						<clay:link
							href="<%= HtmlUtil.escapeHREF(cookiesBannerDisplayContext.getPrivacyPolicyLink()) %>"
							label="<%= HtmlUtil.escape(cookiesBannerDisplayContext.getLinkDisplayText(locale)) %>"
						/>
					</p>
				</clay:content-col>

				<clay:content-col>
					<clay:button
						aria-label='<%= LanguageUtil.get(request, "cookie-configuration") %>'
						displayType="link"
						id='<%= liferayPortletResponse.getNamespace() + "configurationButton" %>'
						label='<%= LanguageUtil.get(request, "configuration") %>'
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

				<c:if test="<%= cookiesBannerDisplayContext.isIncludeDeclineAllButton() %>">
					<clay:content-col>
						<clay:button
							aria-label='<%= LanguageUtil.get(request, "decline-all-cookies") %>'
							displayType="secondary"
							id='<%= liferayPortletResponse.getNamespace() + "declineAllButton" %>'
							label='<%= LanguageUtil.get(request, "decline-all") %>'
							small="<%= true %>"
						/>
					</clay:content-col>
				</c:if>

				<c:if test="<%= cookiesBannerDisplayContext.isStoreConsent() %>">
					<clay:content-col>
						<clay:checkbox
							checked="<%= false %>"
							id='<%= liferayPortletResponse.getNamespace() + "storeConsent" %>'
							label="cookie-store-consent"
							name='<%= liferayPortletResponse.getNamespace() + "storeConsent" %>'
						/>
					</clay:content-col>
				</c:if>
			</clay:content-row>
		</clay:row>
	</clay:container-fluid>
</div>

<liferay-frontend:component
	componentId="CookiesBanner"
	context="<%= cookiesBannerDisplayContext.getContext(locale) %>"
	module="{CookiesBanner} from cookies-banner-web"
/>