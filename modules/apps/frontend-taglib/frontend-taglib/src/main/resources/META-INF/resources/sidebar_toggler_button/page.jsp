<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/sidebar_toggler_button/init.jsp" %>

<clay:link
	aria-label='<%= LanguageUtil.get(request, Validator.isNotNull(label) ? label : "toggle-info-panel") %>'
	cssClass="<%= cssClass %>"
	href='<%= "#" + sidenavId %>'
	icon="<%= icon %>"
	id="<%= StringUtil.randomId() %>"
	label="<%= label %>"
	type="button"
/>

<aui:script>
	var sidenavToggle = document.querySelector('[href="#<%= sidenavId %>"]');

	if (!Liferay.SideNavigation.instance(sidenavToggle)) {
		var sidenavInstance = Liferay.SideNavigation.initialize(sidenavToggle, {
			position: 'right',
			type: 'relative',
			typeMobile: '<%= typeMobile %>',
			width: '320px',
		});

		sidenavInstance.on('closed.lexicon.sidenav', (event) => {
			Liferay.Util.Session.set(
				'com.liferay.info.panel_<%= sidenavId %>',
				'closed'
			);
		});

		sidenavInstance.on('open.lexicon.sidenav', (event) => {
			Liferay.Util.Session.set(
				'com.liferay.info.panel_<%= sidenavId %>',
				'open'
			);
		});
	}
</aui:script>