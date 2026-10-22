<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/adaptive_media/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

AMImageConfigurationEntry amImageConfigurationEntry = null;

if (row != null) {
	amImageConfigurationEntry = (AMImageConfigurationEntry)row.getObject();
}
else {
	amImageConfigurationEntry = (AMImageConfigurationEntry)request.getAttribute("info_panel.jsp-amImageConfigurationEntry");
}

boolean optimizeImagesEnabled = true;

List<BackgroundTask> optimizeImageSingleBackgroundTasks = (List<BackgroundTask>)request.getAttribute("view.jsp-optimizeImageSingleBackgroundTasks");

if (optimizeImageSingleBackgroundTasks != null) {
	for (BackgroundTask optimizeImageSingleBackgroundTask : optimizeImageSingleBackgroundTasks) {
		Map<String, Serializable> taskContextMap = optimizeImageSingleBackgroundTask.getTaskContextMap();

		String configurationEntryUuid = (String)taskContextMap.get("configurationEntryUuid");

		if (configurationEntryUuid.equals(amImageConfigurationEntry.getUUID())) {
			optimizeImagesEnabled = false;

			break;
		}
	}
}

String entryUuid = String.valueOf(amImageConfigurationEntry.getUUID());
%>

<liferay-ui:icon-menu
	direction="left-side"
	icon="<%= StringPool.BLANK %>"
	markupView="lexicon"
	message="actions"
	showWhenSingleIcon="<%= true %>"
>
	<liferay-portlet:renderURL var="editImageConfigurationEntryURL">
		<portlet:param name="mvcRenderCommandName" value="/adaptive_media/edit_image_configuration_entry" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
		<portlet:param name="entryUuid" value="<%= entryUuid %>" />
	</liferay-portlet:renderURL>

	<liferay-ui:icon
		message="edit"
		url="<%= editImageConfigurationEntryURL %>"
	/>

	<c:choose>
		<c:when test="<%= amImageConfigurationEntry.isEnabled() %>">
			<portlet:actionURL name="/adaptive_media/disable_image_configuration_entry" var="disableImageConfigurationEntryURL">
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="amImageConfigurationEntryUuid" value="<%= entryUuid %>" />
			</portlet:actionURL>

			<%
			String cssClass = StringPool.BLANK;

			if (!optimizeImagesEnabled) {
				cssClass = "disabled";
			}
			%>

			<liferay-ui:icon
				cssClass="<%= cssClass %>"
				id='<%= "icon-disable-" + entryUuid %>'
				message="disable"
				url="<%= disableImageConfigurationEntryURL %>"
			/>
		</c:when>
		<c:otherwise>
			<portlet:actionURL name="/adaptive_media/enable_image_configuration_entry" var="enableImageConfigurationEntryURL">
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="amImageConfigurationEntryUuid" value="<%= entryUuid %>" />
			</portlet:actionURL>

			<liferay-ui:icon
				message="enable"
				url="<%= enableImageConfigurationEntryURL %>"
			/>
		</c:otherwise>
	</c:choose>

	<portlet:actionURL name="/adaptive_media/optimize_images" var="optimizeImagesURL">
		<portlet:param name="redirect" value="<%= currentURL %>" />
		<portlet:param name="entryUuid" value="<%= entryUuid %>" />
	</portlet:actionURL>

	<%
	boolean adaptationDelegated = AMImageAdaptationUtil.isAdaptationDelegated(amImageConfigurationEntry, themeDisplay.getCompanyId());
	int percentage = AMImageEntryLocalServiceUtil.getPercentage(themeDisplay.getCompanyId(), entryUuid, GetterUtil.getInteger(request.getAttribute(AMWebKeys.TOTAL_IMAGES)));

	boolean adaptRemainingDisabled = adaptationDelegated || !amImageConfigurationEntry.isEnabled() || (percentage == 100) || !optimizeImagesEnabled;

	String onClick = liferayPortletResponse.getNamespace() + "adaptRemaining('" + entryUuid + "', '" + optimizeImagesURL.toString() + "');";
	%>

	<liferay-ui:icon
		cssClass='<%= adaptRemainingDisabled ? "disabled" : StringPool.BLANK %>'
		id='<%= "icon-adapt-remaining" + entryUuid %>'
		linkCssClass='<%= adaptRemainingDisabled ? "disabled" : StringPool.BLANK %>'
		message="adapt-remaining"
		onClick="<%= adaptRemainingDisabled ? StringPool.BLANK : onClick %>"
		url="javascript:void(0);"
	/>

	<portlet:actionURL name="/adaptive_media/delete_image_configuration_entry" var="deleteImageConfigurationEntryURL">
		<portlet:param name="redirect" value="<%= currentURL %>" />
		<portlet:param name="rowIdsAMImageConfigurationEntry" value="<%= entryUuid %>" />
	</portlet:actionURL>

	<c:choose>
		<c:when test="<%= amImageConfigurationEntry.isEnabled() %>">
			<liferay-ui:icon
				cssClass="disabled"
				message="delete"
				url="javascript:void(0);"
			/>
		</c:when>
		<c:otherwise>
			<liferay-ui:icon-delete
				trash="<%= false %>"
				url="<%= deleteImageConfigurationEntryURL %>"
			/>
		</c:otherwise>
	</c:choose>
</liferay-ui:icon-menu>