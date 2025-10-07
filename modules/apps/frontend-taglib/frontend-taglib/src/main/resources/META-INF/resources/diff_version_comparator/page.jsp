<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-util:html-top>
	<aui:link hashedFile="<%= true %>" href="frontend-taglib/css/diff_version_comparator.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<div>
	<react:component
		module="{DiffVersionComparator} from frontend-taglib"
		props='<%= (Map<String, Object>)request.getAttribute("liferay-frontend:diff-version-comparator:data") %>'
	/>
</div>

<aui:script>
	function updateOverlays() {
		var images = document.getElementsByTagName('img');

		for (var i = 0; i < images.length; i++) {
			var image = images[i];

			var imageChangeType = image.getAttribute('changeType');

			if (
				imageChangeType == 'diff-removed-image' ||
				imageChangeType == 'diff-added-image'
			) {
				var filter = null;
				var existingDivs = image.parentNode.getElementsByTagName('div');

				if (
					existingDivs.length > 0 &&
					existingDivs[0].className == imageChangeType
				) {
					filter = existingDivs[0];
				}
				else {
					filter = document.createElement('div');

					filter.className = image.getAttribute('changeType');
				}

				filter.style.height = image.offsetHeight - 4 + 'px';
				filter.style.width = image.offsetWidth - 4 + 'px';

				if (image.y && image.x) {

					// Workaround for IE

					filter.style.top = image.y + 'px';
					filter.style.left = image.x - 1 + 'px';
				}

				if (existingDivs.length == 0) {
					image.parentNode.insertBefore(filter, image);
				}
			}
		}
	}
</aui:script>