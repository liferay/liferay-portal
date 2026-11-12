<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CMSLoginDisplayContext cmsLoginDisplayContext = new CMSLoginDisplayContext(request);
%>

<div class="align-items-center cms-standalone-login d-flex min-vh-100">
	<div class="container-fluid container-fluid-max-xl">
		<div class="align-items-center no-gutters px-3 row w-100">
			<div class="col-12 col-lg-5 col-md-5 login-form m-auto pr-md-3">
				<h1 class="mb-2 text-9">
					<span class="d-block font-weight-normal mb-1"><liferay-ui:message key="welcome-to-liferay" /></span>
					<span class="d-block font-weight-bold"><liferay-ui:message key="headless-cms" /></span>
				</h1>

				<p class="mb-4 pb-3 text-5 text-secondary"><liferay-ui:message key="start-your-experience-by-signing-in" /></p>

				<liferay-portlet:runtime
					portletName="com_liferay_login_web_portlet_LoginPortlet"
					queryString="<%= cmsLoginDisplayContext.getQueryString() %>"
				/>
			</div>

			<div class="col-12 col-lg-7 col-md-7 d-md-block d-none justify-content-center pl-4 pl-lg-5">
				<react:component
					module="{LoginCarousel} from site-cms-standalone-site-initializer"
				/>
			</div>
		</div>
	</div>
</div>