<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<div class="frontend-js-item-selector-sample-web">
	<clay:container-fluid>
		<clay:row>
			<clay:col>
				<h2>Item Selector Samples</h2>
			</clay:col>
		</clay:row>

		<div>
			<react:component
				module="{ItemSelectorSamples} from frontend-js-item-selector-sample-web"
			/>
		</div>
	</clay:container-fluid>
</div>