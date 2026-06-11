<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String cmd = ParamUtil.getString(request, Constants.CMD, "auth");

LocalEntityManager.CertificateUsage certificateUsage = LocalEntityManager.CertificateUsage.valueOf(ParamUtil.getString(request, "certificateUsage"));

X509Certificate x509Certificate = (X509Certificate)request.getAttribute(SamlWebKeys.SAML_X509_CERTIFICATE);
%>

<aui:script>
	window['<portlet:namespace />requestCloseDialog'] = function (stateChange) {
		parent.window.<portlet:namespace />closeDialog(
			'<portlet:namespace />certificateDialog',
			stateChange
		);
	};
</aui:script>

<c:if test='<%= cmd.equals("replace") || cmd.equals("import") %>'>
	<clay:navigation-bar
		navigationItems='<%=
			new JSPNavigationItemList(pageContext) {
				{
					PortletURL portletURL = PortletURLBuilder.createRenderURL(
						renderResponse
					).setMVCRenderCommandName(
						"/admin/update_certificate"
					).setCMD(
						"replace"
					).setParameter(
						"certificateUsage", certificateUsage.name()
					).buildPortletURL();

					add(
						navigationItem -> {
							navigationItem.setActive(cmd.equals("replace"));
							navigationItem.setHref(portletURL.toString());
							navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "create-certificate"));
						});

					portletURL.setParameter(Constants.CMD, "import");

					add(
						navigationItem -> {
							navigationItem.setActive(cmd.equals("import"));
							navigationItem.setHref(portletURL.toString());
							navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "import-certificate"));
						});
				}
			}
		%>'
	/>
</c:if>

<liferay-portlet:actionURL name="/admin/update_certificate" var="updateCertificateURL">
	<portlet:param name="mvcRenderCommandName" value="/admin/update_certificate" />
	<portlet:param name="<%= Constants.CMD %>" value="<%= cmd %>" />
	<portlet:param name="certificateUsage" value="<%= certificateUsage.name() %>" />
</liferay-portlet:actionURL>

<aui:form action="<%= updateCertificateURL %>" cssClass="" method="post" name="fm1">
	<c:choose>
		<c:when test='<%= cmd.equals("import") && (x509Certificate == null) %>'>
			<liferay-util:include page="/admin/import_certificate.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test="<%= x509Certificate == null %>">

			<%
			String certificateKeyAlgorithm = ParamUtil.getString(request, "certificateKeyAlgorithm", "RSA");
			String certificateKeySize = ParamUtil.getString(request, "certificateKeySize", "2048");
			%>

			<div class="lfr-form-content" id="<portlet:namespace />certificateForm">
				<div class="inline-alert-container lfr-alert-container"></div>

				<liferay-ui:error exception="<%= CertificateException.class %>" message="please-enter-a-valid-algorithm-and-key-size" />
				<liferay-ui:error exception="<%= CertificateKeyPasswordException.class %>" message="please-enter-a-valid-key-password" />
				<liferay-ui:error exception="<%= InvalidParameterException.class %>" message="please-enter-a-valid-algorithm-and-key-size" />
				<liferay-ui:error key="certificateValidityDays" message="please-enter-a-valid-certificate-validity" />

				<c:choose>
					<c:when test='<%= cmd.equals("replace") %>'>
						<aui:input label="common-name" name="certificateCommonName" required="<%= true %>" value='<%= ParamUtil.getString(request, "certificateCommonName") %>' />

						<aui:input label="organization" name="certificateOrganization" value='<%= ParamUtil.getString(request, "certificateOrganization") %>' />

						<aui:input label="organization-unit" name="certificateOrganizationUnit" value='<%= ParamUtil.getString(request, "certificateOrganizationUnit") %>' />

						<aui:input label="locality" name="certificateLocality" value='<%= ParamUtil.getString(request, "certificateLocality") %>' />

						<aui:input label="state" name="certificateState" value='<%= ParamUtil.getString(request, "certificateState") %>' />

						<aui:input label="country" name="certificateCountry" value='<%= ParamUtil.getString(request, "certificateCountry") %>' />

						<aui:input label="validity-days" name="certificateValidityDays" value='<%= ParamUtil.getString(request, "certificateValidityDays", "356") %>' />

						<c:choose>
							<c:when test="<%= certificateUsage == LocalEntityManager.CertificateUsage.SIGNING %>">
								<aui:select label="key-algorithm" name="certificateKeyAlgorithm" required="<%= true %>">

									<%
									String[] keyAlgorithms = PropsValues.FIPS_ENABLED ? new String[] {"RSA"} : new String[] {"RSA", "DSA"};

									for (String keyAlgorithm : keyAlgorithms) {
									%>

										<aui:option label="<%= keyAlgorithm.toLowerCase() %>" selected="<%= certificateKeyAlgorithm.equals(keyAlgorithm) %>" value="<%= keyAlgorithm %>" />

									<%
									}
									%>

								</aui:select>
							</c:when>
							<c:otherwise>
								<aui:input disabled="<%= true %>" label="key-algorithm" name="certificateKeyAlgorithm" value="RSA" />
								<aui:input label="key-algorithm" name="certificateKeyAlgorithm" type="hidden" value="RSA" />
							</c:otherwise>
						</c:choose>

						<aui:select label="key-size-bits" name="certificateKeySize" required="<%= true %>">

							<%
							String[] keySizes = PropsValues.FIPS_ENABLED ? new String[] {"4096", "3072", "2048"} : new String[] {"4096", "2048", "1024", "512"};

							for (String keySize : keySizes) {
							%>

								<aui:option label="<%= keySize %>" selected="<%= certificateKeySize.equals(keySize) %>" value="<%= keySize %>" />

							<%
							}
							%>

						</aui:select>
					</c:when>
				</c:choose>

				<c:choose>
					<c:when test="<%= certificateUsage == LocalEntityManager.CertificateUsage.SIGNING %>">
						<aui:input label="key-password" name='<%= "settings--" + PortletPropsKeys.SAML_KEYSTORE_CREDENTIAL_PASSWORD + "--" %>' required="<%= true %>" type="password" value="" />
					</c:when>
					<c:when test="<%= certificateUsage == LocalEntityManager.CertificateUsage.ENCRYPTION %>">
						<aui:input label="key-password" name='<%= "settings--" + PortletPropsKeys.SAML_KEYSTORE_ENCRYPTION_CREDENTIAL_PASSWORD + "--" %>' required="<%= true %>" type="password" value="" />
					</c:when>
				</c:choose>
			</div>

			<aui:button-row>
				<aui:button cssClass="btn-lg" type="submit" value="save" />
				<aui:button cssClass="btn-lg" onClick='<%= liferayPortletResponse.getNamespace() + "requestCloseDialog(false);" %>' type="cancel" value="cancel" />
			</aui:button-row>
		</c:when>
		<c:otherwise>
			<aui:script>
				<portlet:namespace />requestCloseDialog(true);
			</aui:script>
		</c:otherwise>
	</c:choose>
</aui:form>