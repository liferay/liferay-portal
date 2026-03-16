<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
Group siteGroup = themeDisplay.getSiteGroup();

Group liveGroup = null;

if (siteGroup.isStagingGroup()) {
	liveGroup = siteGroup.getLiveGroup();
}
else {
	liveGroup = siteGroup;
}

long parentGroupId = ParamUtil.getLong(request, "parentGroupId", siteGroup.getParentGroupId());

if (parentGroupId <= 0) {
	parentGroupId = GroupConstants.DEFAULT_PARENT_GROUP_ID;
}

if (liveGroup != null) {
	parentGroupId = liveGroup.getParentGroupId();
}

Group parentGroup = null;

if (parentGroupId != GroupConstants.DEFAULT_PARENT_GROUP_ID) {
	parentGroup = GroupLocalServiceUtil.fetchGroup(parentGroupId);
}
%>

<aui:model-context bean="<%= liveGroup %>" model="<%= Group.class %>" />

<liferay-ui:error exception="<%= DuplicateGroupException.class %>" message="please-enter-a-unique-name" />
<liferay-ui:error exception="<%= GroupInheritContentException.class %>" message="this-site-cannot-inherit-content-from-its-parent-site" />

<liferay-ui:error exception="<%= GroupKeyException.class %>">
	<p>
		<liferay-ui:message arguments="<%= new String[] {SiteConstants.NAME_LABEL, SiteConstants.getNameGeneralRestrictions(locale), SiteConstants.NAME_RESERVED_WORDS} %>" key="the-x-cannot-be-x-or-a-reserved-word-such-as-x" />
	</p>

	<p>
		<liferay-ui:message arguments="<%= new String[] {SiteConstants.NAME_LABEL, SiteConstants.NAME_INVALID_CHARACTERS} %>" key="the-x-cannot-contain-the-following-invalid-characters-x" />
	</p>
</liferay-ui:error>

<liferay-ui:error exception="<%= GroupNameException.class %>" message="site-name-is-required-for-the-default-language" />
<liferay-ui:error exception="<%= GroupParentException.MustNotBeOwnParent.class %>" message="the-site-cannot-be-its-own-parent-site" />
<liferay-ui:error exception="<%= GroupParentException.MustNotHaveChildParent.class %>" message="the-site-cannot-have-a-child-as-its-parent-site" />
<liferay-ui:error exception="<%= GroupParentException.MustNotHaveStagingParent.class %>" message="the-site-cannot-have-a-staging-site-as-its-parent-site" />
<liferay-ui:error exception="<%= PendingBackgroundTaskException.class %>" message="the-site-cannot-be-deleted-because-it-has-background-tasks-in-progress" />
<liferay-ui:error exception="<%= RequiredGroupException.MustNotDeleteCurrentGroup.class %>" message="the-site-cannot-be-deleted-or-deactivated-because-you-are-accessing-the-site" />
<liferay-ui:error exception="<%= RequiredGroupException.MustNotDeleteGroupThatHasChild.class %>" message="you-cannot-delete-sites-that-have-subsites" />
<liferay-ui:error exception="<%= RequiredGroupException.MustNotDeleteSystemGroup.class %>" message="the-site-cannot-be-deleted-or-deactivated-because-it-is-a-required-system-site" />

<liferay-ui:error key="resetMergeFailCountAndMerge" message="unable-to-reset-the-failure-counter-and-propagate-the-changes" />

<c:if test="<%= liveGroup != null %>">
	<aui:input name="siteId" type="resource" value="<%= String.valueOf(liveGroup.getGroupId()) %>" />

	<aui:input disabled="<%= true %>" name="externalReferenceCode" value="<%= String.valueOf(liveGroup.getExternalReferenceCode()) %>" />
</c:if>

<c:choose>
	<c:when test="<%= (liveGroup != null) && liveGroup.isOrganization() %>">
		<aui:input helpMessage="the-name-of-this-site-cannot-be-edited-because-it-belongs-to-an-organization" name="name" placeholder="name" type="resource" value="<%= liveGroup.getDescriptiveName(locale) %>" />
	</c:when>
	<c:when test="<%= (liveGroup == null) || (!liveGroup.isCompany() && !PortalUtil.isSystemGroup(liveGroup.getGroupKey())) %>">
		<aui:input name="name" placeholder="name" />
	</c:when>
</c:choose>

<aui:input name="description" placeholder="description" />

<c:if test="<%= !siteGroup.isCompany() && !siteGroup.isGuest() %>">
	<aui:input inlineLabel="right" labelCssClass="simple-toggle-switch" name="active" type="toggle-switch" value="<%= liveGroup.isActive() %>" />

	<aui:input helpMessage="when-enabled-non-admin-users-will-see-a-maintenance-page-instead-of-the-site-content" inlineLabel="right" label="maintenance-mode" labelCssClass="simple-toggle-switch" name="maintenanceMode" type="toggle-switch" value="<%= liveGroup.isMaintenanceMode() %>" />
</c:if>

<c:if test="<%= (parentGroupId != GroupConstants.DEFAULT_PARENT_GROUP_ID) && PropsValues.SITES_SHOW_INHERIT_CONTENT_SCOPE_FROM_PARENT_SITE %>">

	<%
	boolean disabled = false;
	boolean value = siteGroup.isInheritContent();

	if ((parentGroup != null) && parentGroup.isInheritContent()) {
		disabled = true;
		value = false;
	}
	%>

	<aui:input disabled="<%= disabled %>" helpMessage='<%= disabled ? "this-site-cannot-inherit-the-content-from-its-parent-site-since-the-parent-site-is-already-inheriting-the-content-from-its-parent" : StringPool.BLANK %>' inlineLabel="right" labelCssClass="simple-toggle-switch" name="inheritContent" type="toggle-switch" value="<%= value %>" />
</c:if>

<c:if test="<%= !siteGroup.isCompany() %>">

	<%
	String title = StringPool.BLANK;

	if (parentGroup != null) {
		title = HtmlUtil.escape(parentGroup.getDescriptiveName(locale)) + " (" + LanguageUtil.get(request, parentGroup.getTypeLabel()) + ")";
	}
	%>

	<label class="control-label"><liferay-ui:message key="parent-site" /></label>

	<div class="input-group mb-3">
		<div class="input-group-item">
			<input class="field form-control lfr-input-text" id="<portlet:namespace />parentSiteTitle" name="<portlet:namespace />parentSiteTitle" readonly="<%= true %>" value="<%= title %>" />

			<aui:input name="parentGroupSearchContainerPrimaryKeys" type="hidden" value="<%= parentGroupId %>" />
		</div>

		<div class="input-group-item input-group-item-shrink">
			<clay:button
				cssClass="c-mr-1"
				displayType="secondary"
				id='<%= liferayPortletResponse.getNamespace() + "clearParentSiteLink" %>'
				label="clear"
			/>

			<clay:button
				displayType="secondary"
				id='<%= liferayPortletResponse.getNamespace() + "changeParentSiteLink" %>'
				label="change"
			/>
		</div>
	</div>

	<div class="<%= (parentGroup == null) ? "membership-restriction-container hide" : "membership-restriction-container" %>" id="<portlet:namespace />membershipRestrictionContainer">

		<%
		boolean membershipRestriction = false;

		if ((liveGroup != null) && (liveGroup.getMembershipRestriction() == GroupConstants.MEMBERSHIP_RESTRICTION_TO_PARENT_SITE_MEMBERS)) {
			membershipRestriction = true;
		}
		%>

		<aui:input inlineLabel="right" label="limit-membership-to-members-of-the-parent-site" labelCssClass="simple-toggle-switch" name="membershipRestriction" type="toggle-switch" value="<%= membershipRestriction %>" />
	</div>

	<aui:select label="membership-type" name="type">
		<aui:option label="open" value="<%= GroupConstants.TYPE_SITE_OPEN %>" />
		<aui:option label="restricted" value="<%= GroupConstants.TYPE_SITE_RESTRICTED %>" />
		<aui:option label="private" value="<%= GroupConstants.TYPE_SITE_PRIVATE %>" />
	</aui:select>

	<%
	boolean manualMembership = true;

	if (liveGroup != null) {
		manualMembership = GetterUtil.getBoolean(liveGroup.isManualMembership(), true);
	}
	%>

	<aui:input inlineLabel="right" label="allow-manual-membership-management" labelCssClass="simple-toggle-switch" name="manualMembership" type="toggle-switch" value="<%= manualMembership %>" />

	<%
	ItemSelector itemSelector = (ItemSelector)request.getAttribute(ItemSelector.class.getName());

	SiteItemSelectorCriterion siteItemSelectorCriterion = new SiteItemSelectorCriterion();

	siteItemSelectorCriterion.setDesiredItemSelectorReturnTypes(new GroupItemSelectorReturnType());
	siteItemSelectorCriterion.setExcludedGroupIds(new long[] {siteGroup.getGroupId()});
	siteItemSelectorCriterion.setIncludeCompany(false);
	siteItemSelectorCriterion.setIncludeRecentSites(false);
	%>

	<liferay-frontend:component
		componentId='<%= liferayPortletResponse.getNamespace() + "details" %>'
		context='<%=
			HashMapBuilder.<String, Object>put(
				"defaultParentGroupId", GroupConstants.DEFAULT_PARENT_GROUP_ID
			).put(
				"portletURL", String.valueOf(itemSelector.getItemSelectorURL(RequestBackedPortletURLFactoryUtil.create(request), liferayPortletResponse.getNamespace() + "selectGroup", siteItemSelectorCriterion))
			).build()
		%>'
		module="{Details} from site-admin-web"
	/>
</c:if>