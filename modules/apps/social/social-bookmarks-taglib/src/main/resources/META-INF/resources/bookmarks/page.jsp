<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/bookmarks/init.jsp" %>

<liferay-util:html-top
	outputKey="com.liferay.social.bookmarks.taglib#/bookmarks/page.jsp"
>
	<aui:link hashedFile="<%= true %>" href="social-bookmarks-taglib/css/main.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<div class="taglib-social-bookmarks" id="<%= PortalUtil.generateRandomKey(request, "taglib_ui_social_bookmarks_page") + StringPool.UNDERLINE %>socialBookmarks">
	<c:choose>
		<c:when test='<%= displayStyle.equals("menu") || BrowserSnifferUtil.isMobile(request) %>'>
			<clay:dropdown-menu
				borderless="<%= true %>"
				displayType="secondary"
				dropdownItems="<%= SocialBookmarksTagUtil.getDropdownItems(request.getLocale(), types, className, classPK, title, url) %>"
				icon="share"
				label='<%= BrowserSnifferUtil.isMobile(request) ? null : "share" %>'
				propsTransformer="{SocialBookmarksDropdownPropsTransformer} from social-bookmarks-taglib"
				small="<%= true %>"
			/>
		</c:when>
		<c:otherwise>
			<ul class="list-unstyled <%= displayStyle %>">

				<%
				for (int i = 0; i < Math.min(types.length, maxInlineItems); i++) {
					SocialBookmark socialBookmark = SocialBookmarksRegistryUtil.getSocialBookmark(types[i]);
				%>

					<li class="taglib-social-bookmark taglib-social-bookmark-<%= types[i] %>">
						<liferay-social-bookmarks:bookmark
							additionalProps='<%=
								HashMapBuilder.<String, Object>put(
									"className", HtmlUtil.escapeJS(className)
								).put(
									"classPK", String.valueOf(classPK)
								).put(
									"postURL", socialBookmark.getPostURL(title, url)
								).put(
									"type", types[i]
								).put(
									"url", HtmlUtil.escapeJS(url)
								).build()
							%>'
							displayStyle="<%= displayStyle %>"
							target="<%= target %>"
							title="<%= title %>"
							type="<%= types[i] %>"
							url="<%= url %>"
						/>
					</li>

				<%
				}
				%>

			</ul>

			<c:if test="<%= types.length > maxInlineItems %>">

				<%
				String[] remainingTypes = ArrayUtil.subset(types, maxInlineItems, types.length);
				%>

				<clay:dropdown-menu
					borderless="<%= true %>"
					displayType="secondary"
					dropdownItems="<%= SocialBookmarksTagUtil.getDropdownItems(request.getLocale(), remainingTypes, className, classPK, title, url) %>"
					icon="share"
					monospaced="<%= true %>"
					propsTransformer="{SocialBookmarksDropdownPropsTransformer} from social-bookmarks-taglib"
					small="<%= true %>"
					title="share"
				/>
			</c:if>
		</c:otherwise>
	</c:choose>
</div>