<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/shared_assets/init.jsp" %>

<%
ViewSharingEntryAssetEntryDisplayContext viewSharingEntryAssetEntryDisplayContext = (ViewSharingEntryAssetEntryDisplayContext)request.getAttribute(ViewSharingEntryAssetEntryDisplayContext.class.getName());

if (viewSharingEntryAssetEntryDisplayContext.isControlPanelGroup()) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(viewSharingEntryAssetEntryDisplayContext.getRedirect());
}
else {
	portletDisplay.setPortletDecorate(false);
	portletDisplay.setShowBackIcon(false);
}
%>

<div class="tbar upper-tbar">
	<clay:container-fluid>
		<ul class="tbar-nav">
			<c:if test="<%= !viewSharingEntryAssetEntryDisplayContext.isControlPanelGroup() %>">
				<li class="d-none d-sm-flex tbar-item">
					<clay:link
						borderless="<%= true %>"
						displayType="secondary"
						href="<%= viewSharingEntryAssetEntryDisplayContext.getRedirect() %>"
						icon="angle-left"
						monospaced="<%= true %>"
						outline="<%= true %>"
						small="<%= true %>"
						type="button"
					/>
				</li>
			</c:if>

			<li class="tbar-item tbar-item-expand">
				<div class="tbar-section text-left">
					<h2 class="my-4 text-truncate-inline upper-tbar-title" title="<%= HtmlUtil.escapeAttribute(viewSharingEntryAssetEntryDisplayContext.getAssetTitle()) %>">
						<span class="text-truncate"><%= HtmlUtil.escape(viewSharingEntryAssetEntryDisplayContext.getAssetTitle()) %></span>
					</h2>
				</div>
			</li>
			<li class="tbar-item">
				<clay:dropdown-actions
					aria-label='<%= LanguageUtil.get(request, "actions") %>'
					dropdownItems="<%= viewSharingEntryAssetEntryDisplayContext.getSharingEntryDropdownItems() %>"
				/>
			</li>
		</ul>
	</clay:container-fluid>
</div>

<liferay-util:buffer
	var="assetContent"
>
	<liferay-asset:asset-display
		renderer="<%= viewSharingEntryAssetEntryDisplayContext.getAssetRenderer() %>"
		showComments="<%= false %>"
		showHeader="<%= false %>"
	/>

	<c:if test="<%= viewSharingEntryAssetEntryDisplayContext.isCommentable() %>">
		<liferay-comment:discussion
			className="<%= viewSharingEntryAssetEntryDisplayContext.getAssetEntryClassName() %>"
			classPK="<%= viewSharingEntryAssetEntryDisplayContext.getAssetEntryClassPK() %>"
			formName='<%= "fm" + viewSharingEntryAssetEntryDisplayContext.getAssetEntryClassPK() %>'
			ratingsEnabled="<%= false %>"
			redirect="<%= currentURL %>"
			userId="<%= viewSharingEntryAssetEntryDisplayContext.getAssetEntryUserId() %>"
		/>
	</c:if>
</liferay-util:buffer>

<clay:container-fluid>
	<c:choose>
		<c:when test="<%= viewSharingEntryAssetEntryDisplayContext.isControlPanelGroup() %>">
			<div class="sheet">
				<div class="panel-group panel-group-flush">
					<aui:fieldset>
						<%= assetContent %>
					</aui:fieldset>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<%= assetContent %>
		</c:otherwise>
	</c:choose>
</clay:container-fluid>