<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" var="baseResourceURL" />

<div class="digital-signature">
	<react:component
		module="{DigitalSignature} from digital-signature-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"allowedFileExtensions", StringUtil.merge(DigitalSignatureConstants.ALLOWED_FILE_EXTENSIONS)
			).put(
				"baseResourceURL", String.valueOf(baseResourceURL)
			).put(
				"embeddedViewEnabled", GetterUtil.getBoolean(request.getAttribute(DigitalSignatureWebKeys.DIGITAL_SIGNATURE_EMBEDDED_VIEW_ENABLED))
			).build()
		%>'
	/>
</div>