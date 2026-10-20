<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ContentPageEditorDisplayContext contentPageEditorDisplayContext = (ContentPageEditorDisplayContext)request.getAttribute(ContentPageEditorWebKeys.LIFERAY_SHARED_CONTENT_PAGE_EDITOR_DISPLAY_CONTEXT);
%>

<div class="management-bar navbar navbar-expand-md page-editor__toolbar <%= contentPageEditorDisplayContext.isMasterLayout() ? "page-editor__toolbar--master-layout" : StringPool.BLANK %>" id="<%= contentPageEditorDisplayContext.getPortletNamespace() %>pageEditorToolbar">
	<clay:container-fluid
		fullWidth="<%= true %>"
	>
		<ul class="navbar-nav start">
			<li class="nav-item">
				<div class="dropdown">
					<clay:button
						cssClass="dropdown-toggle"
						disabled="<%= true %>"
						displayType="secondary"
						icon="en-us"
						monospaced="<%= true %>"
						small="<%= true %>"
					/>
				</div>
			</li>
		</ul>

		<ul class="middle navbar-nav">
			<li class="nav-item"></li>
		</ul>

		<ul class="end navbar-nav">
			<li class="nav-item"></li>

			<li class="d-lg-flex d-none nav-item">
				<div class="btn-group flex-nowrap" role="group">
					<clay:button
						disabled="<%= true %>"
						displayType="secondary"
						icon="undo"
						monospaced="<%= true %>"
						small="<%= true %>"
						title="undo"
					/>

					<clay:button
						disabled="<%= true %>"
						displayType="secondary"
						icon="redo"
						monospaced="<%= true %>"
						small="<%= true %>"
						title="redo"
					/>
				</div>

				<span class="d-none d-sm-block ml-2">
					<clay:button
						cssClass="dropdown-toggle"
						disabled="<%= true %>"
						displayType="secondary"
						icon="time"
						monospaced="<%= true %>"
						small="<%= true %>"
						title="history[record]"
					/>
				</span>
			</li>
			<li class="d-lg-flex d-none nav-item">
				<div class="dropdown">
					<clay:button
						cssClass="form-control-select"
						disabled="<%= true %>"
						displayType="secondary"
						small="<%= true %>"
					>
						<liferay-ui:message key="page-design" />
					</clay:button>
				</div>
			</li>
			<li class="d-lg-none nav-item">
				<clay:button
					cssClass="form-control-select"
					disabled="<%= true %>"
					displayType="secondary"
					icon="format"
					monospaced="<%= false %>"
					small="<%= true %>"
				/>
			</li>
			<li class="d-lg-flex d-none nav-item">
				<clay:button
					disabled="<%= true %>"
					displayType="secondary"
					icon="view"
					monospaced="<%= true %>"
					small="<%= true %>"
					title="view"
				/>
			</li>
			<li class="d-lg-flex d-none nav-item">
				<clay:button
					disabled="<%= true %>"
					displayType="secondary"
					small="<%= true %>"
				>
					<liferay-ui:message key="discard-draft" />
				</clay:button>
			</li>
			<li class="d-lg-none nav-item">
				<clay:button
					disabled="<%= true %>"
					displayType="secondary"
					icon="ellipsis-v"
					monospaced="<%= true %>"
					small="<%= true %>"
				/>
			</li>

			<c:if test="<%= contentPageEditorDisplayContext.isSingleSegmentsExperienceMode() %>">
				<li class="nav-item">
					<clay:button
						cssClass="mr-3"
						disabled="<%= true %>"
						displayType="secondary"
						small="<%= true %>"
					>
						<liferay-ui:message key="discard-variant" />
					</clay:button>
				</li>
			</c:if>

			<li class="nav-item">
				<clay:button
					disabled="<%= true %>"
					displayType="primary"
					small="<%= true %>"
				>
					<c:choose>
						<c:when test="<%= contentPageEditorDisplayContext.isMasterLayout() %>">
							<liferay-ui:message key="publish-master" />
						</c:when>
						<c:when test="<%= contentPageEditorDisplayContext.isSingleSegmentsExperienceMode() %>">
							<liferay-ui:message key="save-variant" />
						</c:when>
						<c:when test="<%= contentPageEditorDisplayContext.isWorkflowEnabled() %>">
							<liferay-ui:message key="submit-for-workflow" />
						</c:when>
						<c:otherwise>
							<liferay-ui:message key="publish" />
						</c:otherwise>
					</c:choose>
				</clay:button>
			</li>
			<li class="d-md-none nav-item">
				<clay:button
					cssClass="text-secondary"
					disabled="<%= true %>"
					displayType="unstyled"
					icon="cog"
					monospaced="<%= true %>"
					small="<%= true %>"
				/>
			</li>
		</ul>
	</clay:container-fluid>
</div>