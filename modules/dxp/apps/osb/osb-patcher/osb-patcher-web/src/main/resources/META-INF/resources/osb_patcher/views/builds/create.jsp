<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.createPatcherBuild(0);

patcherBuild.setKeyVersion(PatcherBuildConstants.KEY_VERSION_DEFAULT);

long templatePatcherBuildId = ParamUtil.getLong(request, "templatePatcherBuildId");

PatcherBuild templatePatcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(templatePatcherBuildId);

boolean mergeOnly = false;
String patcherBuildAccountEntryCode = StringPool.BLANK;
long patcherProjectVersionId = 0;

if (templatePatcherBuild != null) {
	patcherBuild.setName(templatePatcherBuild.getName());
	patcherBuild.setPatcherAccountId(templatePatcherBuild.getPatcherAccountId());
	patcherBuild.setPatcherProductVersionId(templatePatcherBuild.getPatcherProductVersionId());
	patcherBuild.setPatcherProjectVersionId(templatePatcherBuild.getPatcherProjectVersionId());
	patcherBuild.setType(templatePatcherBuild.getType());

	mergeOnly = PatcherBuildUtil.isMergeOnly(templatePatcherBuild);

	PatcherAccount patcherAccount = PatcherAccountLocalServiceUtil.getPatcherAccount(templatePatcherBuild.getPatcherAccountId());

	patcherBuildAccountEntryCode = patcherAccount.getAccountEntryCode();

	patcherProjectVersionId = templatePatcherBuild.getPatcherProjectVersionId();
}

if (patcherBuild.getType() != PatcherBuildConstants.TYPE_DEBUG) {
	patcherBuild.setType(PatcherBuildConstants.TYPE_OFFICIAL);
}
%>

<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
	<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="tabs1" value="builds" />
	</liferay-util:include>
</c:if>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="create-build" />
	<liferay-util:param name="mvcRenderCommandName" value="/patcher/index_builds" />
	<liferay-util:param name="patcherProductVersionId" value="<%= String.valueOf(patcherProductVersionId) %>" />
</liferay-util:include>

<aui:model-context bean="<%= patcherBuild %>" model="<%= PatcherBuild.class %>" />

<portlet:actionURL name="/patcher/add_builds" var="addPatcherBuildURL" />

<aui:form action="<%= addPatcherBuildURL %>" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "checkForExistingHotfix();" %>'>
	<div class="layout osb-patcher-layout-flex">
		<div class="layout-content">
			<div class="osb-patcher-align-center">
				<clay:col>
					<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

					<aui:input name="useExistingHotfix" type="hidden" value="<%= false %>" />

					<aui:field-wrapper label="version">
						<%= patcherBuild.getKeyVersion() %>
					</aui:field-wrapper>

					<aui:select label="product-version" name="patcherProductVersionId" onChange='<%= liferayPortletResponse.getNamespace() + "productVersionOnChange(this.value);" %>' required="<%= true %>" showEmptyOption="<%= true %>">

						<%
						for (PatcherProductVersion patcherProductVersion : PatcherProductVersionUtil.getPatcherProductVersions()) {
						%>

							<aui:option label="<%= patcherProductVersion.getName() %>" value="<%= patcherProductVersion.getPatcherProductVersionId() %>" />

						<%
						}
						%>

					</aui:select>

					<aui:select label="project-version" name="patcherProjectVersionId" onChange='<%= liferayPortletResponse.getNamespace() + "projectVersionOnChange(this.value);" %>' required="<%= true %>" />

					<aui:input inputCssClass="osb-patcher-input-wide" label="account-code" name="patcherBuildAccountEntryCode" required="<%= true %>" type="text" value="<%= patcherBuildAccountEntryCode %>" />

					<aui:input helpMessage="the-support-ticket-must-contain-only-the-help-center-ticket-id" inputCssClass="osb-patcher-input-wide" name="supportTicket" type="text">
						<aui:validator name="number" />
					</aui:input>

					<aui:select name="type">
						<aui:option label="<%= PatcherBuildConstants.LABEL_OFFICIAL %>" value="<%= PatcherBuildConstants.TYPE_OFFICIAL %>" />
						<aui:option label="<%= PatcherBuildConstants.LABEL_DEBUG %>" value="<%= PatcherBuildConstants.TYPE_DEBUG %>" />
						<aui:option label="<%= PatcherBuildConstants.LABEL_IGNORE %>" value="<%= PatcherBuildConstants.TYPE_IGNORE %>" />
					</aui:select>

					<aui:input name="mergeOnly" type="checkbox" value="<%= mergeOnly %>" />

					<aui:input name="smokeTestOnly" type="checkbox" wrapperCssClass="osb-patcher-display-none" />

					<aui:button-row>
						<aui:button type="submit" value="add" />

						<portlet:renderURL var="viewPatcherBuildsURL">
							<portlet:param name="mvcRenderCommandName" value="/patcher/index_builds" />
						</portlet:renderURL>

						<aui:button href="<%= Validator.isNotNull(redirect) ? redirect : viewPatcherBuildsURL %>" value="cancel" />
					</aui:button-row>
				</clay:col>

				<clay:col
					cssClass="osb-patcher-content-half"
				>
					<aui:input inputCssClass="osb-patcher-input-wide" label="tickets-list" name="patcherBuildName" type="textarea" value="<%= patcherBuild.getName() %>" wrapperCssClass="osb-patcher-max-height" />
				</clay:col>

				<clay:col
					cssClass="mt-1"
				>
					<aui:field-wrapper>
						<aui:input inputCssClass="osb-patcher-input-wide" label="troubleshooting-ticket-suggestions" name="troubleshootingTicketList" type="textarea" />

						<aui:button-row cssClass="osb-patcher-button-row">
							<aui:button cssClass="osb-patcher-button" icon="icon-plus-sign" onClick='<%= liferayPortletResponse.getNamespace() + "troubleshootAddOnClick(this.value);" %>' title="Apply" />
						</aui:button-row>
					</aui:field-wrapper>

					<div class="d-none">
						<aui:field-wrapper>
							<aui:input inputCssClass="osb-patcher-input-wide" label="security-ticket-suggestions" name="securityTicketList" type="textarea" />

							<aui:button-row cssClass="osb-patcher-button-row">
								<aui:button cssClass="osb-patcher-button" icon="icon-plus-sign" onClick='<%= liferayPortletResponse.getNamespace() + "securityAddOnClick(this.value);" %>' title="Apply" />
							</aui:button-row>
						</aui:field-wrapper>

						<aui:field-wrapper>
							<aui:input inputCssClass="osb-patcher-input-wide" label="regression-ticket-suggestions" name="regressionTicketList" type="textarea" />

							<aui:button-row cssClass="osb-patcher-button-row">
								<aui:button cssClass="osb-patcher-button" icon="icon-plus-sign" onClick='<%= liferayPortletResponse.getNamespace() + "regressionAddOnClick(this.value);" %>' title="Apply" />
							</aui:button-row>
						</aui:field-wrapper>
					</div>
				</clay:col>
			</div>
		</div>
	</div>
</aui:form>

<%
Map<Long, List<PatcherProjectVersion>> patcherProjectVersions = PatcherProjectVersionUtil.getPatcherProductVersionIdPatcherProjectVersions();

JSONObject patcherProjectVersionsJSONObject = JSONFactoryUtil.createJSONObject(JSONFactoryUtil.looseSerializeDeep(patcherProjectVersions));
%>

<aui:script>
	var mergeOnly = document.getElementById('<portlet:namespace />mergeOnly');
	var patcherBuildName = document.getElementById(
		'<portlet:namespace />patcherBuildName'
	);
	var patcherProductVersionId = document.getElementById(
		'<portlet:namespace />patcherProductVersionId'
	);
	var patcherProjectVersionId = document.getElementById(
		'<portlet:namespace />patcherProjectVersionId'
	);
	var regressionTextArea = document.getElementById(
		'<portlet:namespace />regressionTicketList'
	);
	var securityTextArea = document.getElementById(
		'<portlet:namespace />securityTicketList'
	);
	var select = document.getElementById(
		'<portlet:namespace />patcherProjectVersionId'
	);
	var troubleshootingTextArea = document.getElementById(
		'<portlet:namespace />troubleshootingTicketList'
	);
	var useExistingHotfix = document.getElementById(
		'<portlet:namespace />useExistingHotfix'
	);

	<portlet:renderURL var="buildsControllerURL">
		<portlet:param name="mvcRenderCommandName" value="/patcher/get_ticket_suggestion_fields_builds" />
	</portlet:renderURL>

	Liferay.provide(
		window,
		'<portlet:namespace />checkForExistingHotfix',
		function () {
			if (
				patcherProductVersionId.value ==
				<%= PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_QUARTERLY_RELEASES) %>
			) {
				getUseExistingHotfixValue();
			}
			else {
				submitForm(document.<portlet:namespace />fm);
			}
		},
		['aui-base']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />productVersionOnChange',
		function (productVersionId) {
			Liferay.Patcher.populateProjectVersionField(
				productVersionId,
				select,
				<%= patcherProjectVersionsJSONObject %>
			);

			getTicketSuggestionFields();
		},
		['aui-base', 'liferay-portlet-url']
	);

	function getTicketSuggestionFields() {
		var projectVersionId = patcherProjectVersionId.value
			? patcherProjectVersionId.value
			: 0;

		const formData = Liferay.Util.objectToFormData({
			tickets: patcherBuildName.value,
			productVersionId: patcherProductVersionId.value,
			projectVersionId: projectVersionId,
		});

		Liferay.Util.fetch(
			'<liferay-portlet:resourceURL id="/patcher/get_ticket_suggestion_fields" />',
			{
				body: formData,
				method: 'POST',
			}
		)
			.then((response) => {
				return response.json();
			})
			.then((data) => {
				regressionTextArea.value = data.regression;
				securityTextArea.value = data.security;
				troubleshootingTextArea.value = String(
					responseData.troubleshooting
				).replaceAll(' ', '');
			});
	}

	function getUseExistingHotfixValue() {
		const formData = Liferay.Util.objectToFormData({
			projectVersionId: patcherProjectVersionId.value,
			tickets: patcherBuildName.value,
		});

		Liferay.Util.fetch(
			'<liferay-portlet:resourceURL id="/patcher/exists_hotfix" />',
			{
				body: formData,
				method: 'POST',
			}
		)
			.then((response) => {
				return response.json();
			})
			.then((data) => {
				if (data.hotfixExists == true) {
					var alertMessage =
						'<liferay-ui:message key="a-hotfix-with-these-parameters-is-already-available-would-you-like-to-use-it-click-ok-to-use-the-existing-hotfix-or-cancel-to-start-the-normal-build-process" />';

					if (confirm(alertMessage)) {
						mergeOnly.value = true;

						useExistingHotfix.value = true;
					}
				}

				submitForm(document.<portlet:namespace />fm);
			});
	}

	Liferay.provide(
		window,
		'<portlet:namespace />projectVersionOnChange',
		function (projectVersionId) {
			getTicketSuggestionFields();
		},
		['aui-base', 'liferay-portlet-url']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />troubleshootAddOnClick',
		function () {
			if (patcherBuildName.value) {
				patcherBuildName.value =
					patcherBuildName.value + ',' + troubleshootingTextArea.value;
			}
			else {
				patcherBuildName.value = troubleshootingTextArea.value;
			}
		},
		['aui-base']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />securityAddOnClick',
		function () {
			patcherBuildName.value =
				patcherBuildName.value + ',' + securityTextArea.value;
		},
		['aui-base']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />regressionAddOnClick',
		function () {
			patcherBuildName.value =
				patcherBuildName.value + ',' + regressionTextArea.value;
		},
		['aui-base']
	);

	AUI().ready(function () {
		var A = AUI();

		var productVersionId = A.one(
			'#<portlet:namespace />patcherProductVersionId'
		).val();

		Liferay.Patcher.populateProjectVersionField(
			productVersionId,
			select,
			<%= patcherProjectVersionsJSONObject %>
		);

		var projectVersionId = <%= patcherProjectVersionId %>;

		if (projectVersionId > 0) {
			A.one('#<portlet:namespace />patcherProjectVersionId').val(
				projectVersionId
			);
		}

		YUI().use('event-valuechange', function (Y) {
			Y.one('#<portlet:namespace />patcherBuildName').on(
				'valuechange',
				function (e) {
					if (patcherProductVersionId && patcherProjectVersionId) {
						getTicketSuggestionFields();
					}
				}
			);
		});
	});

	YUI().ready('aui-popover', function (Y) {
		var align_points = [Y.WidgetPositionAlign.BL, Y.WidgetPositionAlign.BR];
		var tickets = document.getElementById(
			'<portlet:namespace />patcherBuildName'
		);
		var trigger = Y.one('#<portlet:namespace />patcherBuildName');

		Liferay.Patcher.getTicketLinksPopover(Y, align_points, tickets, trigger);
	});

	YUI().ready('aui-popover', function (Y) {
		var align_points = [Y.WidgetPositionAlign.LC, Y.WidgetPositionAlign.RC];
		var tickets = document.getElementById(
			'<portlet:namespace />troubleshootingTicketList'
		);
		var trigger = Y.one('#<portlet:namespace />troubleshootingTicketList');

		Liferay.Patcher.getTicketLinksPopover(Y, align_points, tickets, trigger);
	});
</aui:script>