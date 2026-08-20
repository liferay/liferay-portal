<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/personal_menu/init.jsp" %>

<%
String namespace = StringUtil.randomId() + StringPool.UNDERLINE;

long color = (long)request.getAttribute("liferay-product-navigation:personal-menu:color");
String label = (String)request.getAttribute("liferay-product-navigation:personal-menu:label");
String size = (String)request.getAttribute("liferay-product-navigation:personal-menu:size");
User user2 = (User)request.getAttribute("liferay-product-navigation:personal-menu:user");

String userStickerCssClasses = "sticker";

if (size != null) {
	userStickerCssClasses += " sticker-" + size;
}

String impersonateStickerCssClasses = "sticker";

if (size != null) {
	impersonateStickerCssClasses += " sticker-sm";
}
%>

<aui:link hashedFile="<%= true %>" href="product-navigation-taglib/css/main.css" rel="stylesheet" type="text/css" />

<div class="personal-menu-dropdown" id="<%= namespace %>personal_menu_dropdown">
	<c:choose>
		<c:when test="<%= Validator.isNotNull(label) %>">
			<div><%= label %></div>
		</c:when>
		<c:otherwise>
			<clay:button
				cssClass="dropdown-toggle"
				displayType="unstyled"
				id='<%= namespace + "personal_menu_dropdown_toggle" %>'
			>
				<span class="<%= userStickerCssClasses %>">
					<liferay-user:user-portrait
						size="<%= size %>"
						user="<%= user2 %>"
					/>

					<c:if test="<%= themeDisplay.isImpersonated() %>">
						<span class="<%= impersonateStickerCssClasses %> sticker-bottom-right sticker-circle sticker-outside sticker-user-icon" id="impersonate-user-sticker">
							<span class="sticker-overlay">
								<clay:icon
									id="impersonate-user-icon"
									symbol="user"
								/>
							</span>
						</span>
					</c:if>
				</span>
			</clay:button>
		</c:otherwise>
	</c:choose>

	<%
	Map<String, Object> props = HashMapBuilder.<String, Object>put(
		"color", color
	).put(
		"isImpersonated", themeDisplay.isImpersonated()
	).put(
		"itemsURL",
		PortletURLBuilder.create(
			PortletURLFactoryUtil.create(request, PersonalMenuPortletKeys.PERSONAL_MENU, PortletRequest.RESOURCE_PHASE)
		).setParameter(
			"currentURL", themeDisplay.getURLCurrent()
		).setParameter(
			"p_p_resource_id", "/product_navigation_personal_menu/get_personal_menu_items"
		).setParameter(
			"portletId", themeDisplay.getPpid()
		).setWindowState(
			LiferayWindowState.EXCLUSIVE
		).buildString()
	).put(
		"label", label
	).put(
		"size", size
	).put(
		"userName", HtmlUtil.escape(user2.getFullName())
	).build();

	if (user2.getPortraitId() > 0) {
		props.put("userPortraitURL", user2.getPortraitURL(themeDisplay));
	}
	%>

	<react:component
		module="{PersonalMenu} from product-navigation-taglib"
		props="<%= props %>"
	/>
</div>