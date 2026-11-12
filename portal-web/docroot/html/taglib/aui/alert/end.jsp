<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/taglib/aui/alert/init.jsp" %>

</div>

<aui:script use="aui-alert">
	var alertNode = A.one('#<%= id %>');

	new A.Alert({
		animated: <%= animated %>,
		closeable: <%= closeable %>,
		cssClass: null,
		destroyOnHide: <%= destroyOnHide %>,
		duration: <%= duration %>,
		render: true,
		srcNode: alertNode,
	});

	if (<%= animated %>) {
		alertNode.show();
	}
</aui:script>