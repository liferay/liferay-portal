<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/publications/init.jsp" %>

<%
ViewRelatedEntriesDisplayContext viewRelatedEntriesDisplayContext = (ViewRelatedEntriesDisplayContext)request.getAttribute(CTWebKeys.VIEW_RELATED_ENTRIES_DISPLAY_CONTEXT);

String backURL = ParamUtil.getString(request, "backURL", viewRelatedEntriesDisplayContext.getRedirectURL());

portletDisplay.setURLBack(backURL);

portletDisplay.setShowBackIcon(true);

renderResponse.setTitle(LanguageUtil.get(request, "discard-changes"));
%>

<div class="publications-related-entries-container">
	<div class="sheet">
		<clay:sheet-section>
			<clay:content-row>
				<clay:content-col
					expand="<%= true %>"
				>
					<h2 class="sheet-title"><liferay-ui:message key="discarded-changes" /></h2>
				</clay:content-col>

				<clay:content-col>
					<aui:input id="showHideable" inlineLabel="right" label='<%= LanguageUtil.get(request, "show-all-items") %>' name="show-hideable" onChange='<%= liferayPortletResponse.getNamespace() + "handleShowAllItemsToggleChange();" %>' type="toggle-switch" value="<%= viewRelatedEntriesDisplayContext.isShowHideable() %>" />
				</clay:content-col>
			</clay:content-row>

			<div class="sheet-text">
				<liferay-ui:message key="the-following-changes-will-be-discarded" />
			</div>

			<div>
				<react:component
					data="<%= viewRelatedEntriesDisplayContext.getReactData() %>"
					module="{ChangeTrackingRelatedEntriesView} from change-tracking-web"
				/>
			</div>
		</clay:sheet-section>

		<clay:sheet-footer>
			<aui:button href="<%= viewRelatedEntriesDisplayContext.getSubmitDiscardURL() %>" primary="true" value="discard" />

			<aui:button href="<%= backURL %>" type="cancel" />
		</clay:sheet-footer>
	</div>
</div>

<aui:script>
	function <portlet:namespace />handleShowAllItemsToggleChange() {
		var showHideableToggleState =
			'<%= !viewRelatedEntriesDisplayContext.isShowHideable() %>';

		let url = new URL(window.location.href);

		url.searchParams.set(
			'<portlet:namespace />showHideable',
			showHideableToggleState
		);

		window.location.href = url;
	}
</aui:script>