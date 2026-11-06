<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
User selUser = (User)request.getAttribute(UsersAdminWebKeys.SELECTED_USER);

List<AnnouncementsDelivery> deliveries = null;

if (selUser != null) {
	deliveries = AnnouncementsDeliveryLocalServiceUtil.getUserDeliveries(selUser.getUserId());
}
else {
	deliveries = new ArrayList<AnnouncementsDelivery>(AnnouncementsEntryConstants.TYPES.length);

	for (String type : AnnouncementsEntryConstants.TYPES) {
		AnnouncementsDelivery delivery = new AnnouncementsDeliveryImpl();

		delivery.setType(type);
		delivery.setWebsite(true);

		deliveries.add(delivery);
	}
}
%>

<div class="sheet-text"><liferay-ui:message key="select-the-delivery-options-for-alerts-and-announcements" /></div>

<liferay-ui:search-container>
	<liferay-ui:search-container-results
		results="<%= deliveries %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.announcements.kernel.model.AnnouncementsDelivery"
		escapedModel="<%= true %>"
		keyPropertyName="deliveryId"
		modelVar="delivery"
	>
		<liferay-ui:search-container-column-text
			name="type"
			value="<%= LanguageUtil.get(request, delivery.getType()) %>"
		/>

		<liferay-ui:search-container-column-text
			name="email"
		>
			<aui:input label="" name='<%= "announcementsType" + delivery.getType() + "Email" %>' title='<%= LanguageUtil.format(request, "receive-x-announcements-via-email", delivery.getType()) %>' type="checkbox" value="<%= delivery.isEmail() %>" />
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>