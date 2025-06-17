<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/panel/init.jsp" %>

<%
String collapseSwitchId = Validator.isNotNull(collapseSwitchName) ? collapseSwitchName : (randomNamespace + "toggle-switch-check");
%>

<div class="card d-flex flex-column<%= Validator.isNotNull(elementClasses) ? StringPool.SPACE + elementClasses : StringPool.BLANK %>">
	<c:if test="<%= Validator.isNotNull(actionLabel) || Validator.isNotNull(actionIcon) || Validator.isNotNull(title) %>">
		<div class="align-items-center card-header d-flex h4 justify-content-between py-3">
			<%= HtmlUtil.escape(title) %>

			<c:choose>
				<c:when test="<%= Validator.isNotNull(actionLabel) %>">

					<%
					String href = Validator.isNotNull(actionUrl) ? actionUrl : "#";
					%>

					<c:if test="<%= actionContext != null %>">

						<%
						href = "#";
						%>

						<liferay-frontend:component
							context='<%=
								HashMapBuilder.<String, Object>put(
									"title", title
								).put(
									"url", actionUrl
								).putAll(
									actionContext
								).put(
									"linkId", linkId
								).build()
							%>'
							module="{ModalActionContextHandler} from commerce-frontend-taglib"
						/>
					</c:if>

					<clay:link
						href="<%= href %>"
						id="<%= linkId %>"
						label="<%= HtmlUtil.escape(actionLabel) %>"
					/>
				</c:when>
				<c:when test="<%= Validator.isNotNull(actionIcon) %>">
					<clay:link
						cssClass="btn btn-monospaced btn-primary btn-sm text-white"
						href='<%= (Validator.isNotNull(actionUrl) && Validator.isNull(actionTargetId)) ? actionUrl : "#" %>'
						icon="<%= HtmlUtil.escapeAttribute(actionIcon) %>"
						id="<%= HtmlUtil.escape(linkId) %>"
					/>
				</c:when>
				<c:when test="<%= collapsible || Validator.isNotNull(collapseLabel) || Validator.isNotNull(collapseSwitchName) %>">
					<aui:script>
						(function () {
							var toggleSwitch = document.getElementById(
								'<%= HtmlUtil.escapeJS(randomNamespace) %>toggle-switch'
							);
							var toggleLabel = document.getElementById(
								'<%= HtmlUtil.escapeJS(randomNamespace) %>toggle-label'
							);
							var toggleCheckbox = document.getElementById(
								'<%= HtmlUtil.escapeJS(collapseSwitchId) %>'
							);
							var collapseClickable = true;
							var collapsableElement = document.getElementById(
								'<%= HtmlUtil.escapeJS(randomNamespace) %>collapse'
							);

							[toggleSwitch, toggleLabel].forEach((el) => {
								el.addEventListener('click', (e) => {
									e.preventDefault();

									if (collapseClickable) {
										toggleCheckbox.click();
										collapsableElement.classList[
											toggleCheckbox.checked ? 'remove' : 'add'
										]('show');
										toggleCheckbox.checked = !toggleCheckbox.checked;
									}

									collapseClickable = false;

									setTimeout(() => {
										collapseClickable = true;
									}, 400);
								});
							});
						})();
					</aui:script>

					<span class="d-flex mr-n2">
						<c:if test="<%= Validator.isNotNull(collapseLabel) %>">
							<label for="<%= HtmlUtil.escapeAttribute(collapseSwitchId) %>" id="<%= HtmlUtil.escapeAttribute(randomNamespace) %>toggle-label">
								<div class="h5 mb-0 mr-3">
									<%= HtmlUtil.escape(collapseLabel) %>
								</div>
							</label>
						</c:if>

						<span class="my-lg-n2 toggle-switch" id="<%= HtmlUtil.escapeAttribute(randomNamespace) %>toggle-switch">
							<input
								aria-expanded="<%= !collapsed %>"
								<%= collapsed ? StringPool.BLANK : "checked" %>
								data-target="#<%= HtmlUtil.escapeAttribute(randomNamespace) %>collapse"
								data-toggle="collapse"
								class="toggle-switch-check d-none"
								id="<%= HtmlUtil.escapeAttribute(collapseSwitchId) %>"
								<c:if test="<%= Validator.isNotNull(collapseSwitchName) %>">
									name="<%= HtmlUtil.escapeAttribute(collapseSwitchName) %>"
								</c:if>
								type="checkbox"
							/>

							<span aria-hidden="true" class="toggle-switch-bar">
								<span class="toggle-switch-handle"></span>
							</span>
						</span>
					</span>
				</c:when>
			</c:choose>
		</div>
	</c:if>

	<div class="collapse<%= collapsed ? StringPool.BLANK : " show" %>" id="<%= randomNamespace %>collapse">
		<div class="card-body<%= Validator.isNotNull(bodyClasses) ? StringPool.SPACE + bodyClasses : StringPool.BLANK %>">