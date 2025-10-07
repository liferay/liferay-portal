<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String type = GetterUtil.getString((String)request.getAttribute("liferay-ratings:ratings:type"));
%>

<c:choose>
	<c:when test="<%= themeDisplay.isIsolated() %>">
		<div class="ratings-edit-page">
			<aui:link href='<%= PortalUtil.getStaticResourceURL(request, PortalUtil.getPathProxy() + application.getContextPath() + "/css/main.css") %>' rel="stylesheet" type="text/css" />
	</c:when>
	<c:otherwise>
		<liferay-util:html-top
			outputKey="com.liferay.ratings.taglib#/page.jsp"
		>
			<aui:link href='<%= PortalUtil.getStaticResourceURL(request, PortalUtil.getPathProxy() + application.getContextPath() + "/css/main.css") %>' rel="stylesheet" type="text/css" />
		</liferay-util:html-top>
	</c:otherwise>
</c:choose>

<div class="ratings">
	<c:choose>
		<c:when test="<%= type.equals(RatingsType.LIKE.getValue()) %>">
			<div class="ratings-like">
				<clay:button
					borderless="<%= true %>"
					disabled="<%= true %>"
					displayType="secondary"
					small="<%= true %>"
				>
					<clay:icon
						symbol="heart"
					/>
				</clay:button>
			</div>
		</c:when>
		<c:when test="<%= type.equals(RatingsType.THUMBS.getValue()) %>">
			<div class="ratings-thumbs">
				<clay:button
					borderless="<%= true %>"
					disabled="<%= true %>"
					displayType="secondary"
					small="<%= true %>"
				>
					<clay:icon
						symbol="thumbs-up"
					/>
				</clay:button>

				<clay:button
					borderless="<%= true %>"
					disabled="<%= true %>"
					displayType="secondary"
					icon="thumbs-down"
					small="<%= true %>"
				>
					<clay:icon
						symbol="thumbs-down"
					/>
				</clay:button>
			</div>
		</c:when>
		<c:when test="<%= type.equals(RatingsType.STARS.getValue()) %>">
			<clay:content-row
				cssClass="ratings-stars"
				verticalAlign="center"
			>
				<clay:content-col>
					<div class="dropdown">
						<clay:button
							borderless="<%= true %>"
							cssClass="dropdown-toggle"
							disabled="<%= true %>"
							displayType="secondary"
							small="<%= true %>"
						>
							<span class="inline-item inline-item-before">
								<clay:icon
									symbol="star-o"
								/>
							</span>
							<span class="inline-item ratings-stars-button-text">-</span>
						</clay:button>
					</div>
				</clay:content-col>

				<clay:content-col>
					<clay:icon
						cssClass="ratings-stars-average-icon"
						symbol="star"
					/>
				</clay:content-col>
			</clay:content-row>
		</c:when>
		<c:when test="<%= type.equals(RatingsType.STACKED_STARS.getValue()) %>">
			<div class="ratings-stacked-stars ratings-stars">
				<div class="disabled ratings-stars-average">
					<span class="inline-item inline-item-before">
						<clay:icon
							cssClass="ratings-stars-average-icon"
							symbol="star"
						/>

						<clay:icon
							cssClass="ratings-stars-average-icon"
							symbol="star"
						/>

						<clay:icon
							cssClass="ratings-stars-average-icon"
							symbol="star"
						/>

						<clay:icon
							cssClass="ratings-stars-average-icon"
							symbol="star"
						/>

						<clay:icon
							cssClass="ratings-stars-average-icon"
							symbol="star"
						/>
					</span>
				</div>

				<div class="disabled ratings-stars-average">
					<span class="inline-item inline-item-before">
						<clay:icon
							cssClass="ratings-stars-average-icon"
							symbol="star"
						/>

						<clay:icon
							cssClass="ratings-stars-average-icon"
							symbol="star"
						/>

						<clay:icon
							cssClass="ratings-stars-average-icon"
							symbol="star"
						/>

						<clay:icon
							cssClass="ratings-stars-average-icon"
							symbol="star"
						/>

						<clay:icon
							cssClass="ratings-stars-average-icon"
							symbol="star"
						/>
					</span>
				</div>
			</div>
		</c:when>
	</c:choose>

	<react:component
		module="{BaseRatings} from ratings-taglib"
		props='<%= (Map<String, Object>)request.getAttribute("liferay-ratings:ratings:data") %>'
	/>
</div>

<c:if test="<%= themeDisplay.isIsolated() %>">
	</div>
</c:if>