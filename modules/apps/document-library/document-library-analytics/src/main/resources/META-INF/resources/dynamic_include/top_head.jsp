<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<aui:script senna="temporary" type="text/javascript">
	if (window.Analytics) {
		window.<%= DocumentLibraryAnalyticsConstants.JS_PREFIX %>isViewFileEntry = false;
	}
</aui:script>

<aui:script>
	function <portlet:namespace />getValueByAttribute(node, attr) {
		return (
			node.dataset[attr] ||
			(node.parentElement && node.parentElement.dataset[attr])
		);
	}

	function <portlet:namespace />sendDocumentDownloadedAnalyticsEvent(anchor) {
		var fileEntryId = <portlet:namespace />getValueByAttribute(
			anchor,
			'analyticsFileEntryId'
		);
		var title = <portlet:namespace />getValueByAttribute(
			anchor,
			'analyticsFileEntryTitle'
		);
		var version = <portlet:namespace />getValueByAttribute(
			anchor,
			'analyticsFileEntryVersion'
		);
		var externalReferenceCode = <portlet:namespace />getValueByAttribute(
			anchor,
			'analyticsExternalReferenceCode'
		);

		if (fileEntryId) {
			Analytics.send('documentDownloaded', 'Document', {
				externalReferenceCode,
				fileEntryId,
				groupId: themeDisplay.getScopeGroupId(),
				preview:
					!!window.<%= DocumentLibraryAnalyticsConstants.JS_PREFIX %>isViewFileEntry,
				title,
				version,
			});
		}
	}

	function <portlet:namespace />handleDownloadClick(event) {
		if (window.Analytics) {
			if (event.target.nodeName.toLowerCase() === 'a') {
				<portlet:namespace />sendDocumentDownloadedAnalyticsEvent(
					event.target
				);
			}
			else if (
				event.target.parentNode &&
				event.target.parentNode.nodeName.toLowerCase() === 'a'
			) {
				<portlet:namespace />sendDocumentDownloadedAnalyticsEvent(
					event.target.parentNode
				);
			}
			else {
				var target = event.target;
				var matchTextContent =
					target.textContent &&
					target.textContent.toLowerCase() ===
						'<%= StringUtil.toLowerCase(LanguageUtil.get(request, "download")) %>';
				var matchTitle =
					target.title && target.title.toLowerCase() === 'download';
				var matchAction = target.action === 'download';
				var matchLexiconIcon = !!target.querySelector(
					'.lexicon-icon-download'
				);
				var matchLexiconClassName = target.classList.contains(
					'lexicon-icon-download'
				);
				var matchParentTitle =
					target.parentNode &&
					target.parentNode.title &&
					target.parentNode.title.toLowerCase() === 'download';
				var matchParentLexiconClassName =
					target.parentNode &&
					target.parentNode.classList.contains('lexicon-icon-download');

				if (
					matchTextContent ||
					matchTitle ||
					matchParentTitle ||
					matchAction ||
					matchLexiconIcon ||
					matchLexiconClassName ||
					matchParentLexiconClassName
				) {
					var selectedFiles = document.querySelectorAll(
						'.form .custom-control-input:checked'
					);

					selectedFiles.forEach(({value}) => {
						var selectedFile = document.querySelector(
							'[data-analytics-file-entry-id="' + value + '"]'
						);

						<portlet:namespace />sendDocumentDownloadedAnalyticsEvent(
							selectedFile
						);
					});
				}
			}
		}
	}

	Liferay.once('destroyPortlet', () => {
		document.body.removeEventListener(
			'click',
			<portlet:namespace />handleDownloadClick
		);
	});

	Liferay.once('portletReady', () => {
		document.body.addEventListener(
			'click',
			<portlet:namespace />handleDownloadClick
		);
	});
</aui:script>