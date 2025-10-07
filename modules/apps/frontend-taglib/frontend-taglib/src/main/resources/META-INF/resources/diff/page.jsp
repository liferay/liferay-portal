<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/diff/init.jsp" %>

<%
String sourceName = (String)request.getAttribute("liferay-frontend:diff:sourceName");
String targetName = (String)request.getAttribute("liferay-frontend:diff:targetName");

List<DiffResult>[] diffResults = (List<DiffResult>[])request.getAttribute("liferay-frontend:diff:diffResults");

List<DiffResult> sourceResults = diffResults[0];
List<DiffResult> targetResults = diffResults[1];
%>

<liferay-util:html-top>
	<aui:link hashedFile="<%= true %>" href="frontend-taglib/css/diff.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<clay:container-fluid>
	<c:choose>
		<c:when test="<%= !sourceResults.isEmpty() %>">
			<table class="table table-bordered table-hover table-striped" id="taglib-diff-results">
				<tr>
					<td>
						<%= HtmlUtil.escape(sourceName) %>
					</td>
					<td>
						<%= HtmlUtil.escape(targetName) %>
					</td>
				</tr>

				<%
				for (int i = 0; i < sourceResults.size(); i++) {
					DiffResult sourceResult = sourceResults.get(i);
					DiffResult targetResult = targetResults.get(i);
				%>

					<tr>
						<th class="table-header">
							<liferay-ui:message key="line" /> <%= sourceResult.getLineNumber() %>
						</th>
						<th class="table-header">
							<liferay-ui:message key="line" /> <%= targetResult.getLineNumber() %>
						</th>
					</tr>
					<tr>
						<td width="50%">
							<table class="taglib-diff-table">

								<%
								for (String changedLine : sourceResult.getChangedLines()) {
								%>

									<tr class="align-top">
										<%= _processColumn(changedLine) %>
									</tr>

								<%
								}
								%>

							</table>
						</td>
						<td class="align-top" width="50%">
							<table class="taglib-diff-table">

								<%
								for (String changedLine : targetResult.getChangedLines()) {
								%>

									<tr class="align-top">
										<%= _processColumn(changedLine) %>
									</tr>

								<%
								}
								%>

							</table>
						</td>
					</tr>

				<%
				}
				%>

			</table>
		</c:when>
		<c:otherwise>
			<liferay-ui:message arguments="<%= new Object[] {HtmlUtil.escape(sourceName), HtmlUtil.escape(targetName)} %>" key="there-are-no-differences-between-x-and-x" translateArguments="<%= false %>" />
		</c:otherwise>
	</c:choose>
</clay:container-fluid>

<%!
private static String _processColumn(String changedLine) {
	changedLine = StringUtil.replace(changedLine, ' ', "&nbsp;");
	changedLine = StringUtil.replace(changedLine, '\t', "&nbsp;&nbsp;&nbsp;");

	String column = "<td>" + changedLine + "</td>";

	if (changedLine.equals(StringPool.BLANK)) {
		return "<td>&nbsp;</td>";
	}
	else if (changedLine.equals(Diff.CONTEXT_LINE)) {
		return "<td class=\"taglib-diff-context\">&nbsp;</td>";
	}
	else if (changedLine.equals(Diff.OPEN_INS + Diff.CLOSE_INS)) {
		return "<td class=\"taglib-diff-addedline\">&nbsp;</td>";
	}
	else if (changedLine.equals(Diff.OPEN_DEL + Diff.CLOSE_DEL)) {
		return "<td class=\"taglib-diff-deletedline\">&nbsp;</td>";
	}

	return column;
}
%>