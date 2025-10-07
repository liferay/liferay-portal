<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ProductAnalyticsBannerDisplayContext productAnalyticsBannerDisplayContext = (ProductAnalyticsBannerDisplayContext)request.getAttribute(ProductAnalyticsWebKeys.PRODUCT_ANALYTICS_BANNER_DISPLAY_CONTEXT);
%>

<clay:container-fluid
	cssClass="container-view"
>
	<clay:row>
		<clay:content-row
			cssClass="autofit-float-sm-down px-2 px-md-0 text-white"
			noGutters="true"
			verticalAlign="center"
		>
			<clay:content-col
				expand="<%= true %>"
			>
				<p class="mb-2 text-5 text-weight-semi-bold">
					<liferay-ui:message key="product-analytics-banner-title" />
				</p>

				<p class="mb-0">
					<liferay-ui:message key="product-analytics-banner-content" />

					<clay:link
						href="<%= productAnalyticsBannerDisplayContext.getPrivacyPolicyLink() %>"
						label='<%= LanguageUtil.get(request, "visit-our-privacy-policy") %>'
					/>
				</p>
			</clay:content-col>

			<clay:content-col>
				<clay:button
					displayType="secondary"
					id='<%= liferayPortletResponse.getNamespace() + "customizeButton" %>'
					label='<%= LanguageUtil.get(request, "action.CUSTOMIZE") %>'
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

			<clay:content-col>
				<clay:button
					displayType="secondary"
					id='<%= liferayPortletResponse.getNamespace() + "declineAllButton" %>'
					label='<%= LanguageUtil.get(request, "decline-all") %>'
					small="<%= true %>"
				/>
			</clay:content-col>
		</clay:content-row>
	</clay:row>
</clay:container-fluid>

<liferay-frontend:component
	componentId="ProductAnalyticsBanner"
	context="<%= productAnalyticsBannerDisplayContext.getContext(locale) %>"
	module="{ProductAnalyticsBanner} from product-analytics-web"
/>