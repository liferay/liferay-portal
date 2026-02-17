<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.site.dsr.site.initializer.internal.constants.DSRSiteInitializerFDSNames" %><%@
page import="com.liferay.site.dsr.site.initializer.internal.display.context.ViewRoomsSectionDisplayContext" %>

<liferay-theme:defineObjects />

<aui:script>
	(function () {
		const sessionKey = 'com.liferay.site.dsr.site.initializer.successMessage';

		const successMessage = Liferay.Util.SessionStorage.getItem(
			sessionKey,
			Liferay.Util.SessionStorage.TYPES.NECESSARY
		);

		if (successMessage) {
			Liferay.Util.openToast({
				message: successMessage,
				type: 'success',
			});

			Liferay.Util.SessionStorage.removeItem(sessionKey);
		}
	})();
</aui:script>