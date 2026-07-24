<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.site.cmp.site.initializer.internal.constants.CMPSiteInitializerFDSNames" %><%@
page import="com.liferay.site.cmp.site.initializer.internal.display.context.ViewAllTasksSectionDisplayContext" %><%@
page import="com.liferay.site.cmp.site.initializer.internal.display.context.ViewAssigneeSectionDisplayContext" %><%@
page import="com.liferay.site.cmp.site.initializer.internal.display.context.ViewProjectInfoSummarySectionDisplayContext" %><%@
page import="com.liferay.site.cmp.site.initializer.internal.display.context.ViewProjectManagerAssigneeSectionDisplayContext" %><%@
page import="com.liferay.site.cmp.site.initializer.internal.display.context.ViewProjectMembersSummarySectionDisplayContext" %><%@
page import="com.liferay.site.cmp.site.initializer.internal.display.context.ViewProjectSponsorAssigneeSectionDisplayContext" %><%@
page import="com.liferay.site.cmp.site.initializer.internal.display.context.ViewProjectTasksSectionDisplayContext" %><%@
page import="com.liferay.site.cmp.site.initializer.internal.display.context.ViewProjectsSectionDisplayContext" %><%@
page import="com.liferay.site.cmp.site.initializer.internal.display.context.ViewTaskInfoSummarySectionDisplayContext" %><%@
page import="com.liferay.site.cmp.site.initializer.internal.display.context.ViewWorkflowTasksSectionDisplayContext" %>

<liferay-theme:defineObjects />

<aui:script>
	(function () {
		const sessionKey = 'com.liferay.site.cmp.site.initializer.successMessage';

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