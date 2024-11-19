<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<blockquote>
	<p>Toggle provide users with different selection and activation tools.</p>
</blockquote>

<h3>DEFAULT TOGGLE</h3>

<clay:row
	cssClass="mb-3"
>
	<clay:col>
		<clay:toggle />
	</clay:col>
</clay:row>

<h3>TOGGLE INITIALLY SELECTED</h3>

<clay:row
	cssClass="mb-3"
>
	<clay:col>
		<clay:toggle
			toggled="<%= true %>"
		/>
	</clay:col>
</clay:row>

<h3>TOGGLE DISABLED</h3>

<clay:row
	cssClass="mb-3"
>
	<clay:col
		md="2"
	>
		<clay:toggle
			disabled="<%= true %>"
		/>
	</clay:col>

	<clay:col
		md="2"
	>
		<clay:toggle
			disabled="<%= true %>"
			label="Label Text"
		/>
	</clay:col>
</clay:row>

<h3>TOGGLE WITH LABEL</h3>

<clay:row
	cssClass="mb-3"
>
	<clay:col>
		<clay:toggle
			label="Label Text"
		/>
	</clay:col>
</clay:row>

<h3>TOGGLE WITH SYMBOLS</h3>

<clay:row
	cssClass="mb-3"
>
	<clay:col>
		<clay:toggle
			label="Label Text"
			offSymbol="staging"
			onSymbol="live"
		/>
	</clay:col>
</clay:row>

<h3>TOGGLE WITH ON/OFF LABELS</h3>

<clay:row
	cssClass="mb-3"
>
	<clay:col>
		<clay:toggle
			offLabel="Off"
			onLabel="On"
		/>
	</clay:col>
</clay:row>

<h3>TOGGLE WITH HELP TEXT</h3>

<clay:row
	cssClass="mb-3"
>
	<clay:col
		md="2"
	>
		<clay:toggle
			helpText="Help Text"
			label="Label Text"
		/>
	</clay:col>

	<clay:col
		md="2"
	>
		<clay:toggle
			disabled="<%= true %>"
			helpText="Help Text"
			label="Label Text"
		/>
	</clay:col>
</clay:row>

<h3>TOGGLE SIZING SMALL</h3>

<clay:row
	cssClass="mb-3"
>
	<clay:col
		md="2"
	>
		<clay:toggle
			label="Label Text"
			sizing="sm"
		/>
	</clay:col>

	<clay:col
		md="2"
	>
		<clay:toggle
			helpText="Help Text"
			label="Label Text"
			offSymbol="staging"
			onSymbol="live"
			sizing="sm"
		/>
	</clay:col>

	<clay:col
		md="2"
	>
		<clay:toggle
			disabled="<%= true %>"
			label="Label Text"
			sizing="sm"
		/>
	</clay:col>
</clay:row>