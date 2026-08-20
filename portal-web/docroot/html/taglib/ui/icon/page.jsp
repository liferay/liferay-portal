<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/taglib/ui/icon/init.jsp" %>

<%
boolean urlIsNotNull = Validator.isNotNull(url);

String listItemAnchorAriaHasPopup = "false";
String listItemAnchorAriaRole = "menuitem";
String listItemAriaRole = "presentation";

if (!linkCssClass.contains("keep-aria-attributes") && (useDialog || (urlIsNotNull && url.startsWith("javascript:")))) {
	listItemAnchorAriaHasPopup = "dialog";
	listItemAnchorAriaRole = null;
	listItemAriaRole = "";
}
%>

<c:choose>
	<c:when test="<%= (iconListIconCount != null) && ((iconListSingleIcon == null) || iconListShowWhenSingleIcon) %>">
		<li class="<%= cssClass %>" role="<%= listItemAriaRole %>">
			<c:choose>
				<c:when test="<%= urlIsNotNull %>">
					<aui:a aria-haspopup="<%= listItemAnchorAriaHasPopup %>" ariaRole="<%= listItemAnchorAriaRole %>" cssClass="<%= linkCssClass %>" data="<%= data %>" href="<%= url %>" icon="<%= icon %>" id="<%= id %>" lang="<%= lang %>" onClick="<%= onClick %>" target="<%= target %>">
						<%@ include file="/html/taglib/ui/icon/link_content.jspf" %>
					</aui:a>
				</c:when>
				<c:otherwise>
					<%@ include file="/html/taglib/ui/icon/link_content.jspf" %>
				</c:otherwise>
			</c:choose>
		</li>
	</c:when>
	<c:when test="<%= (iconMenuIconCount != null) && ((iconMenuSingleIcon == null) || iconMenuShowWhenSingleIcon) %>">
		<li class="<%= cssClass %>" role="<%= listItemAriaRole %>">
			<c:choose>
				<c:when test="<%= urlIsNotNull %>">
					<aui:a aria-haspopup="<%= listItemAnchorAriaHasPopup %>" ariaRole="<%= listItemAnchorAriaRole %>" cssClass="<%= linkCssClass %>" data="<%= data %>" href="<%= url %>" icon="<%= icon %>" id="<%= id %>" lang="<%= lang %>" onClick="<%= onClick %>" target="<%= target %>">
						<%@ include file="/html/taglib/ui/icon/link_content.jspf" %>
					</aui:a>
				</c:when>
				<c:otherwise>
					<span class="taglib-icon">
						<%@ include file="/html/taglib/ui/icon/link_content.jspf" %>
					</span>
				</c:otherwise>
			</c:choose>
		</li>
	</c:when>
	<c:otherwise>
		<span
			class="<%= cssClass %>"

			<c:if test="<%= toolTip && !urlIsNotNull %>">
				tabindex="0"
			</c:if>

			<c:if test="<%= !label && Validator.isNotNull(message) %>">
				title="<%= HtmlUtil.escapeAttribute(LanguageUtil.get(resourceBundle, HtmlUtil.stripHtml(message))) %>"
			</c:if>
		>
			<c:choose>
				<c:when test="<%= urlIsNotNull %>">
					<aui:a ariaRole="<%= ariaRole %>" cssClass="<%= linkCssClass %>" data="<%= data %>" href="<%= url %>" icon="<%= icon %>" id="<%= id %>" lang="<%= lang %>" onClick="<%= onClick %>" target="<%= target %>">
						<%@ include file="/html/taglib/ui/icon/link_content.jspf" %>
					</aui:a>
				</c:when>
				<c:otherwise>
					<%@ include file="/html/taglib/ui/icon/link_content.jspf" %>
				</c:otherwise>
			</c:choose>
		</span>
	</c:otherwise>
</c:choose>

<c:if test="<%= Validator.isNotNull(srcHover) || forcePost || useDialog %>">
	<aui:script type="module">
		import {registerIcon} from '<%= FrontendESMUtil.buildURL(themeDisplay, "frontend-js-web", "legacy") %>';

		registerIcon({
			forcePost: <%= forcePost %>,
			id: '<portlet:namespace /><%= id %>',

			<c:if test="<%= Validator.isNotNull(srcHover) %>">
				src: '<%= HtmlUtil.escapeJS(src) %>',
				srcHover: '<%= HtmlUtil.escapeJS(srcHover) %>',
			</c:if>

			useDialog: <%= useDialog %>,
		});
	</aui:script>
</c:if>