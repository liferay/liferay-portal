<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ page import="com.liferay.portal.cluster.multiple.sample.web.internal.ClusterSampleData" %><%@
page import="com.liferay.portal.kernel.servlet.PortalSessionContext" %>

<%@ page import="jakarta.servlet.http.HttpSession" %>

<portlet:defineObjects />

<%
ClusterSampleData clusterSampleData = new ClusterSampleData();
%>

<div class="h4">Server Data:</div>

<p>Following data is from the server that generated this response:</p>

<ul>
	<li>
		<b>Computer Name:</b> <%= clusterSampleData.getComputerName() %>
	</li>
	<li>
		<b>Liferay Home:</b> <%= clusterSampleData.getLiferayHome() %>
	</li>
	<li>
		<b>Current timestamp:</b> <%= clusterSampleData.getTimestamp() %>
	</li>
</ul>

<div class="logged-in-session-count">
	<h4>Logged in Session Count:</h4>

	<%
	int count = 0;

	for (HttpSession httpSession : PortalSessionContext.values()) {
		if (httpSession.getAttribute("USER_ID") != null) {
			count++;
		}
	}

	out.println(count);
	%>

</div>

<div class="h4">Session Data:</div>

<%
ClusterSampleData portletSessionClusterSampleData = (ClusterSampleData)portletSession.getAttribute(ClusterSampleData.class.getName());

if (portletSessionClusterSampleData == null) {
	portletSessionClusterSampleData = clusterSampleData;

	portletSession.setAttribute(ClusterSampleData.class.getName(), clusterSampleData);

	out.println("Generated Cluster Sample Data: ");
}
else {
	out.println("Existing Cluster Sample Data: ");
}
%>

<p>Following data is stored in the portlet session:</p>

<ul>
	<li>
		<b>Stored Data:</b> <p class="stored-data"><%= portletSessionClusterSampleData.getData() %></p>
	</li>
	<li>
		<b>Stored Timestamp:</b> <%= portletSessionClusterSampleData.getTimestamp() %>
	</li>
	<li>
		<b>Session ID: </b> <p class="session-id"><%= portletSession.getId() %></p>
	</li>
</ul>

<p>The data was stored by:</p>

<ul>
	<li>
		<b>Computer Name:</b> <%= portletSessionClusterSampleData.getComputerName() %>
	</li>
	<li>
		<b>Liferay Home:</b> <%= portletSessionClusterSampleData.getLiferayHome() %>
	</li>
</ul>