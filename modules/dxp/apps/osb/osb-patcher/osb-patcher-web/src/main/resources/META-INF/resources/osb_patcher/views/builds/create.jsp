<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
	<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="tabs1" value="builds" />
	</liferay-util:include>
</c:if>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="create-build" />
	<liferay-util:param name="controller" value="builds" />
	<liferay-util:param name="action" value="index" />
	<liferay-util:param name="patcherProductVersionId" value="${patcherProductVersionId}" />
</liferay-util:include>

<aui:model-context bean="${patcherBuild}" model="<%= PatcherBuild.class %>" />

<portlet:actionURL var="addPatcherBuildURL">
	<portlet:param name="controller" value="builds" />
	<portlet:param name="action" value="add" />
</portlet:actionURL>

<aui:form action="${addPatcherBuildURL}" method="post" name="fm" onSubmit="event.preventDefault(); ${renderResponse.namespace}checkForExistingHotfix();">
	<aui:layout cssClass="osb-patcher-layout-flex" styleClass="osb-patcher-layout-flex">
		<div class="osb-patcher-align-center">
			<aui:column>
				<aui:input name="redirect" type="hidden" value="${redirect}" />

				<aui:input name="useExistingHotfix" type="hidden" value="${false}" />

				<aui:field-wrapper name="version">
					${patcherBuild.keyVersion}
				</aui:field-wrapper>

				<aui:select label="product-version" name="patcherProductVersionId" onChange="${renderResponse.namespace}productVersionOnChange(this.value);" required="${true}" showEmptyOption="${true}">
					<c:forEach items="${patcherProductVersions}" var="patcherProductVersion">
						<aui:option label="${patcherProductVersion.getName()}" value="${patcherProductVersion.getPatcherProductVersionId()}" />
					</c:forEach>
				</aui:select>

				<aui:select label="project-version" name="patcherProjectVersionId" onChange="${renderResponse.namespace}projectVersionOnChange(this.value);" required="${true}" />

				<aui:input inputCssClass="osb-patcher-input-wide" label="account-code" name="patcherBuildAccountEntryCode" required="${true}" type="text" value="${patcherBuildAccountEntryCode}" />

				<aui:input helpMessage="the-support-ticket-must-contain-only-the-help-center-ticket-id" inputCssClass="osb-patcher-input-wide" name="supportTicket" type="text">
					<aui:validator name="number" />
				</aui:input>

				<aui:select name="type">
					<aui:option label="${PatcherBuildConstants.LABEL_OFFICIAL}" value="${PatcherBuildConstants.TYPE_OFFICIAL}" />
					<aui:option label="${PatcherBuildConstants.LABEL_DEBUG}" value="${PatcherBuildConstants.TYPE_DEBUG}" />
					<aui:option label="${PatcherBuildConstants.LABEL_IGNORE}" value="${PatcherBuildConstants.TYPE_IGNORE}" />
				</aui:select>

				<aui:input name="mergeOnly" type="checkbox" value="${patcherBuildMergeOnly}" />

				<aui:input name="smokeTestOnly" type="checkbox" wrapperCssClass="osb-patcher-display-none" />

				<aui:button-row>
					<aui:button type="submit" value="add" />

					<portlet:renderURL var="viewPatcherBuildsURL">
						<portlet:param name="controller" value="builds" />
						<portlet:param name="action" value="index" />
					</portlet:renderURL>

					<aui:button href="${(not empty redirect) ? redirect : viewPatcherBuildsURL}" value="cancel" />
				</aui:button-row>
			</aui:column>

			<aui:column cssClass="osb-patcher-content-half">
				<aui:input inputCssClass="osb-patcher-input-wide" label="tickets-list" name="patcherBuildName" style="height: 100%;" type="textarea" value="${patcherBuild.name}" wrapperCssClass="osb-patcher-max-height" />
			</aui:column>

			<aui:column style="margin-top: 5%;">
				<aui:field-wrapper>
					<aui:input inputCssClass="osb-patcher-input-wide" label="troubleshooting-ticket-suggestions" name="troubleshootingTicketList" type="textarea" />

					<aui:button-row cssClass="osb-patcher-button-row">
						<aui:button cssClass="osb-patcher-button" icon="icon-plus-sign" onClick="${renderResponse.namespace}troubleshootAddOnClick(this.value);" title="Apply" />
					</aui:button-row>
				</aui:field-wrapper>

				<div style="display: none;">
					<aui:field-wrapper>
						<aui:input inputCssClass="osb-patcher-input-wide" label="security-ticket-suggestions" name="securityTicketList" type="textarea" />

						<aui:button-row cssClass="osb-patcher-button-row">
							<aui:button cssClass="osb-patcher-button" icon="icon-plus-sign" onClick="${renderResponse.namespace}securityAddOnClick(this.value);" title="Apply" />
						</aui:button-row>
					</aui:field-wrapper>

					<aui:field-wrapper>
						<aui:input inputCssClass="osb-patcher-input-wide" label="regression-ticket-suggestions" name="regressionTicketList" type="textarea" />

						<aui:button-row cssClass="osb-patcher-button-row">
							<aui:button cssClass="osb-patcher-button" icon="icon-plus-sign" onClick="${renderResponse.namespace}regressionAddOnClick(this.value);" title="Apply" />
						</aui:button-row>
					</aui:field-wrapper>
				</div>
			</aui:column>
		</div>
	</aui:layout>
</aui:form>

<aui:script>
	var mergeOnly = document.getElementById("<portlet:namespace />mergeOnly");
	var patcherBuildName = document.getElementById("<portlet:namespace />patcherBuildName");
	var patcherProductVersionId = document.getElementById("<portlet:namespace />patcherProductVersionId");
	var patcherProjectVersionId = document.getElementById("<portlet:namespace />patcherProjectVersionId");
	var regressionTextArea = document.getElementById("<portlet:namespace />regressionTicketList");
	var securityTextArea = document.getElementById("<portlet:namespace />securityTicketList");
	var select = document.getElementById("<portlet:namespace />patcherProjectVersionId");
	var troubleshootingTextArea = document.getElementById("<portlet:namespace />troubleshootingTicketList");
	var useExistingHotfix = document.getElementById("<portlet:namespace />useExistingHotfix");

	<portlet:renderURL var="buildsControllerURL">
		<portlet:param name="controller" value="builds" />
		<portlet:param name="action" value="getTicketSuggestionFields" />
	</portlet:renderURL>

	Liferay.provide(
		window,
		'<portlet:namespace />checkForExistingHotfix',
		function() {
			if (patcherProductVersionId.value == ${PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_QUARTERLY_RELEASES)}) {
				_getUseExistingHotfixValue();
			} else {
				submitForm(document.<portlet:namespace />fm);
			}
		},
		['aui-base']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />productVersionOnChange',
		function(productVersionId) {
			Liferay.Patcher.populateProjectVersionField(productVersionId, select, ${patcherProjectVersionsJSON});

			_getTicketSuggestionFields();
		},
		['aui-base', 'liferay-portlet-url']
	);

	function _getTicketSuggestionFields() {
		var projectVersionId = patcherProjectVersionId.value ? patcherProjectVersionId.value : 0;

		Liferay.Service(
			'/osb-patcher-portlet.builds/getTicketSuggestionFields',
			{
				limit: 1000,
				tickets: patcherBuildName.value,
				productVersionId: patcherProductVersionId.value,
				projectVersionId: projectVersionId
			},
			function(obj) {
				var responseData = obj.data;

				regressionTextArea.value = responseData.regression
				securityTextArea.value = responseData.security
				troubleshootingTextArea.value = String(responseData.troubleshooting).replaceAll(' ', '')
			}
		);
	}

	function _getUseExistingHotfixValue() {
		Liferay.Service('/osb-patcher-portlet.builds/hotfixExists',
			{
				projectVersionId: patcherProjectVersionId.value,
				tickets: patcherBuildName.value
			},
			function(obj) {
				var responseData = obj.data;

				if (responseData.hotfixExists[0] == true) {
					var alertMessage = '<liferay-ui:message key="a-hotfix-with-these-parameters-is-already-available-would-you-like-to-use-it-click-ok-to-use-the-existing-hotfix-or-cancel-to-start-the-normal-build-process" />';

					if (confirm(alertMessage)) {
						mergeOnly.value = true;

						useExistingHotfix.value = true;
					}
				}

				submitForm(document.<portlet:namespace />fm);
			}
		);
	}

	Liferay.provide(
		window,
		'<portlet:namespace />projectVersionOnChange',
		function(projectVersionId) {
			_getTicketSuggestionFields();
		},
		['aui-base', 'liferay-portlet-url']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />troubleshootAddOnClick',
		function() {
			if (patcherBuildName.value) {
				patcherBuildName.value = patcherBuildName.value + "," + troubleshootingTextArea.value
			} else {
				patcherBuildName.value = troubleshootingTextArea.value
			}
		},
		['aui-base']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />securityAddOnClick',
		function() {
			patcherBuildName.value = patcherBuildName.value + "," + securityTextArea.value
		},
		['aui-base']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />regressionAddOnClick',
		function() {
			patcherBuildName.value = patcherBuildName.value + "," + regressionTextArea.value
		},
		['aui-base']
	);

	AUI().ready(
		function() {
			var A = AUI();

			var productVersionId = A.one('#<portlet:namespace />patcherProductVersionId').val();

			Liferay.Patcher.populateProjectVersionField(productVersionId, select, ${patcherProjectVersionsJSON});

			var projectVersionId = ${not empty patcherProjectVersionId ? patcherProjectVersionId : 0};

			if (projectVersionId > 0) {
				A.one('#<portlet:namespace />patcherProjectVersionId').val(projectVersionId);
			}

			YUI().use('event-valuechange', function(Y) {
				Y.one('#<portlet:namespace />patcherBuildName').on('valuechange', function(e) {
					if (patcherProductVersionId && patcherProjectVersionId) {
						_getTicketSuggestionFields();
					}
				});
			});
		}
	);

	YUI().ready(
		'aui-popover',
		function(Y) {
			var align_points = [Y.WidgetPositionAlign.BL, Y.WidgetPositionAlign.BR];
			var tickets = document.getElementById('_1_WAR_osbpatcherportlet_patcherBuildName');
			var trigger = Y.one('#_1_WAR_osbpatcherportlet_patcherBuildName');

			Liferay.Patcher.getTicketLinksPopover(Y, align_points, tickets, trigger)
		}
	);

	YUI().ready(
		'aui-popover',
		function(Y) {
			var align_points = [Y.WidgetPositionAlign.LC, Y.WidgetPositionAlign.RC];
			var tickets = document.getElementById('_1_WAR_osbpatcherportlet_troubleshootingTicketList');
			var trigger = Y.one('#_1_WAR_osbpatcherportlet_troubleshootingTicketList');

			Liferay.Patcher.getTicketLinksPopover(Y, align_points, tickets, trigger)
		}
	);
</aui:script>