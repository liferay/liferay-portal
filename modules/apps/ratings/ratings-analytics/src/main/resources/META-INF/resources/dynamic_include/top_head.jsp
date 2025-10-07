<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<aui:script sandbox="<%= true %>">
	var onVote = function (event) {
		if (window.Analytics) {
			let title = event.contentTitle;

			if (!title) {
				const dmNode = document.querySelector(
					'[data-analytics-file-entry-id="' + event.classPK + '"]'
				);

				if (dmNode) {
					title = dmNode.dataset.analyticsFileEntryTitle;
				}
			}

			Analytics.send('VOTE', 'Ratings', {
				className: event.className,
				classPK: event.classPK,
				externalReferenceCode: event.externalReferenceCode,
				ratingType: event.ratingType,
				score: event.score,
				title,
			});
		}
	};

	var onDestroyPortlet = function () {
		Liferay.detach('ratings:vote', onVote);
		Liferay.detach('destroyPortlet', onDestroyPortlet);
	};

	Liferay.on('ratings:vote', onVote);
	Liferay.on('destroyPortlet', onDestroyPortlet);
</aui:script>