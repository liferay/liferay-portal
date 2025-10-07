<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<h2>Default Input Localized</h2>

<div id="defaultInputLocalized">
	<liferay-editor:input-localized
		name="default"
		xml=""
	/>
</div>

<h2 class="mt-3">Input Localized with Content</h2>

<div id="contentInputLocalized">
	<liferay-editor:input-localized
		name="content"
		xml='<?xml version="1.0" ?><root available-locales="en_US,es_ES" default-locale="en_US"><Description language-id="en_US">English</Description><Description language-id="es_ES">Spanish</Description></root>'
	/>
</div>

<h2 class="mt-3">Input Localized without Languages Dropdown</h2>

<div id="inputOnlyInputLocalized">
	<liferay-editor:input-localized
		languagesDropdownVisible="<%= false %>"
		name="inputOnly"
		xml=""
	/>
</div>