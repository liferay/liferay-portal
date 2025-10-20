<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/social_activities/init.jsp" %>

<liferay-util:html-top>
	<aui:link hashedFile="<%= true %>" href="social-activities-taglib/css/main.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<div class="taglib-social-activities">

	<%
	ServiceContext serviceContext = ServiceContextFactory.getInstance(request);

	boolean hasActivities = false;

	Date date = new Date();

	int daysBetween = -1;

	for (SocialActivityDescriptor activityDescriptor : activityDescriptors) {
		SocialActivityFeedEntry activityFeedEntry = activityDescriptor.interpret(selector, serviceContext);

		if (activityFeedEntry == null) {
			continue;
		}

		Date activityDate = new Date(activityDescriptor.getCreateDate());

		int curDaysBetween = DateUtil.getDaysBetween(activityDate, date, timeZone);
	%>

		<c:if test="<%= curDaysBetween > daysBetween %>">

			<%
			daysBetween = curDaysBetween;
			%>

			<c:if test="<%= hasActivities %>">
				</ul>
			</c:if>

			<div class="autofit-row mb-1">
				<div class="autofit-col autofit-col-expand">
					<div class="component-title">
						<c:choose>
							<c:when test="<%= curDaysBetween == 0 %>">
								<liferay-ui:message key="today" />
							</c:when>
							<c:when test="<%= curDaysBetween == 1 %>">
								<liferay-ui:message key="yesterday" />
							</c:when>
							<c:when test="<%= DateUtil.getYear(activityDate) == DateUtil.getYear(date) %>">
								<%= dateFormat.format(activityDescriptor.getCreateDate()) %>
							</c:when>
							<c:otherwise>
								<%= yearDateFormat.format(activityDescriptor.getCreateDate()) %>
							</c:otherwise>
						</c:choose>
					</div>
				</div>

				<c:if test="<%= feedEnabled && !activityDescriptors.isEmpty() %>">
					<div class="autofit-col">
						<div class="link-outline link-outline-borderless link-outline-primary">
							<liferay-rss:rss
								delta="<%= feedDelta %>"
								displayStyle="<%= feedDisplayStyle %>"
								feedType="<%= feedType %>"
								message="<%= feedURLMessage %>"
								name="<%= feedTitle %>"
								resourceURL="<%= feedResourceURL %>"
								url="<%= feedURL %>"
							/>
						</div>
					</div>
				</c:if>
			</div>

			<ul class="list-unstyled">
		</c:if>

		<li>
			<div class="card card-horizontal">
				<div class="card-body">
					<div class="autofit-padded-no-gutters card-row">
						<div class="autofit-col">
							<liferay-user:user-portrait
								userId="<%= activityDescriptor.getUserId() %>"
							/>
						</div>

						<div class="autofit-col autofit-col-expand">
							<div class="component-subtitle">
								<%= timeFormat.format(activityDescriptor.getCreateDate()) %>
							</div>

							<div>
								<%= activityFeedEntry.getTitle() %>

								<%= activityFeedEntry.getBody() %>
							</div>
						</div>
					</div>
				</div>
			</div>
		</li>

	<%
		if (!hasActivities) {
			hasActivities = true;
		}
	}
	%>

	<c:choose>
		<c:when test="<%= hasActivities %>">
			</ul>
		</c:when>
		<c:otherwise>
			<liferay-ui:message key="there-are-no-recent-activities" />
		</c:otherwise>
	</c:choose>
</div>