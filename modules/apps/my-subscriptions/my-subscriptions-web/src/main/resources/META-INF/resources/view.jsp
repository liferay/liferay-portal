<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<portlet:actionURL name="unsubscribe" var="unsubscribeURL" />

<%
String redirect = ParamUtil.getString(request, "redirect");

String backURL = ParamUtil.getString(request, "backURL", redirect);

MySubscriptionsManagementToolbarDisplayContext mySubscriptionsManagementToolbarDisplayContext = new MySubscriptionsManagementToolbarDisplayContext(request, liferayPortletResponse, user);

int subscriptionsCount = mySubscriptionsManagementToolbarDisplayContext.getTotalItems();

if (Validator.isNotNull(backURL)) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(backURL);
}
%>

<clay:management-toolbar
	actionDropdownItems="<%= mySubscriptionsManagementToolbarDisplayContext.getActionDropdownItems() %>"
	disabled="<%= mySubscriptionsManagementToolbarDisplayContext.isDisabled() %>"
	itemsTotal="<%= subscriptionsCount %>"
	propsTransformer="{MySubscriptionsManagementToolbarPropsTransformer} from my-subscriptions-web"
	searchContainerId="subscriptions"
	selectable="<%= mySubscriptionsManagementToolbarDisplayContext.isSelectable() %>"
	showSearch="<%= mySubscriptionsManagementToolbarDisplayContext.isShowSearch() %>"
/>

<clay:container-fluid>
	<aui:form action="<%= unsubscribeURL %>" method="get" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "unsubscribe();" %>'>
		<liferay-portlet:renderURLParams varImpl="portletURL" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="subscriptionIds" type="hidden" />

		<liferay-ui:error exception="<%= NoSuchSubscriptionException.class %>" message="the-subscription-could-not-be-found" />

		<liferay-ui:error-principal />

		<aui:fieldset>
			<liferay-portlet:renderURL varImpl="iteratorURL" />

			<liferay-ui:search-container
				deltaConfigurable="<%= true %>"
				emptyResultsMessage="no-subscriptions-were-found"
				id="subscriptions"
				iteratorURL="<%= iteratorURL %>"
				rowChecker="<%= new EmptyOnClickRowChecker(renderResponse) %>"
				total="<%= subscriptionsCount %>"
			>
				<liferay-ui:search-container-results
					results="<%= SubscriptionLocalServiceUtil.getUserSubscriptions(user.getUserId(), searchContainer.getStart(), searchContainer.getEnd(), SubscriptionClassNameIdComparator.getInstance(true)) %>"
				/>

				<liferay-ui:search-container-row
					className="com.liferay.subscription.model.Subscription"
					escapedModel="<%= true %>"
					keyProperty="subscriptionId"
					modelVar="subscription"
				>

					<%
					AssetRenderer<?> assetRenderer = MySubscriptionsUtil.getAssetRenderer(subscription.getClassName(), subscription.getClassPK());

					String rowURL = null;

					if (assetRenderer != null) {
						rowURL = assetRenderer.getURLViewInContext((LiferayPortletRequest)renderRequest, (LiferayPortletResponse)renderResponse, currentURL);
					}
					else {
						rowURL = MySubscriptionsUtil.getAssetURLViewInContext(themeDisplay, subscription.getClassName(), subscription.getClassPK());
					}
					%>

					<liferay-ui:search-container-column-text
						href="<%= rowURL %>"
						name="title"
						value="<%= HtmlUtil.escape(MySubscriptionsUtil.getTitleText(locale, subscription.getClassName(), subscription.getClassPK(), ((assetRenderer != null) ? assetRenderer.getTitle(locale) : null))) %>"
					/>

					<liferay-ui:search-container-column-text
						href="<%= rowURL %>"
						name="asset-type"
						value="<%= MySubscriptionsUtil.getAssetTypeDescription(locale, subscription.getClassName()) %>"
					/>

					<liferay-ui:search-container-column-date
						href="<%= rowURL %>"
						name="create-date"
						value="<%= subscription.getCreateDate() %>"
					/>

					<liferay-ui:search-container-column-jsp
						align="right"
						cssClass="entry-action"
						path="/subscription_action.jsp"
					/>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
					resultRowSplitter="<%= new MySubscriptionsResultRowSplitter(locale) %>"
				/>
			</liferay-ui:search-container>
		</aui:fieldset>
	</aui:form>
</clay:container-fluid>

<aui:script>
	window['<portlet:namespace />displayPopup'] = function (url, title) {
		Liferay.Util.openModal({
			iframeBodyCssClass: 'portlet-my-subscription',
			size: 'full-screen',
			title: title,
			url: url,
		});
	};
</aui:script>