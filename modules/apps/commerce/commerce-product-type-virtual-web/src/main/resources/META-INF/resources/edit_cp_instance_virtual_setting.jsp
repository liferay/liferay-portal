<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPDefinitionVirtualSettingDisplayContext cpDefinitionVirtualSettingDisplayContext = (CPDefinitionVirtualSettingDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPDefinitionVirtualSetting cpDefinitionVirtualSetting = cpDefinitionVirtualSettingDisplayContext.getCPDefinitionVirtualSetting();
CPInstance cpInstance = cpDefinitionVirtualSettingDisplayContext.getCPInstance();

long cpInstanceId = cpDefinitionVirtualSettingDisplayContext.getCPInstanceId();

String className = CPInstance.class.getName();
long classPK = cpInstanceId;

if (cpDefinitionVirtualSetting != null) {
	className = cpDefinitionVirtualSetting.getClassName();
	classPK = cpDefinitionVirtualSetting.getClassPK();
}

boolean override = BeanParamUtil.getBoolean(cpDefinitionVirtualSetting, request, "override", CPDefinitionVirtualSetting::isOverride, false);
%>

<portlet:actionURL name="/cp_definitions/edit_cp_definition_virtual_setting" var="editProductDefinitionVirtualSettingActionURL" />

<aui:form action="<%= editProductDefinitionVirtualSettingActionURL %>" method="post" name="fm1">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (cpDefinitionVirtualSetting == null) ? Constants.ADD : Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="className" type="hidden" value="<%= CPInstance.class.getName() %>" />
	<aui:input name="classPK" type="hidden" value="<%= cpInstanceId %>" />
	<aui:input name="cpDefinitionId" type="hidden" value="<%= cpInstance.getCPDefinitionId() %>" />
	<aui:input name="cpInstanceId" type="hidden" value="<%= cpInstanceId %>" />
	<aui:input name="sampleFileEntryId" type="hidden" value="<%= (cpDefinitionVirtualSetting == null) ? StringPool.BLANK : cpDefinitionVirtualSetting.getSampleFileEntryId() %>" />
	<aui:input name="termsOfUseJournalArticleResourcePrimKey" type="hidden" value="<%= (cpDefinitionVirtualSetting == null) ? StringPool.BLANK : cpDefinitionVirtualSetting.getTermsOfUseJournalArticleResourcePrimKey() %>" />

	<div class="sheet">
		<div class="panel-group panel-group-flush">
			<aui:fieldset>
				<aui:input checked="<%= override %>" inlineLabel="right" labelCssClass="simple-toggle-switch" name="override" type="toggle-switch" value="<%= override %>" />
			</aui:fieldset>

			<div class="<%= !override ? "hide" : "" %>" id="<portlet:namespace />cpDefinitionVirtualSettingContainer">
				<aui:fieldset collapsible="<%= true %>" label="details">
					<frontend-data-set:classic-display
						contextParams='<%=
							HashMapBuilder.<String, String>put(
								"className", className
							).put(
								"classPK", String.valueOf(classPK)
							).build()
						%>'
						creationMenu="<%= cpDefinitionVirtualSettingDisplayContext.getCreationMenu() %>"
						dataProviderKey="<%= CPDefinitionVirtualSettingFDSNames.VIRTUAL_SETTING_FILES %>"
						formName="fm"
						id="<%= CPDefinitionVirtualSettingFDSNames.VIRTUAL_SETTING_FILES %>"
						selectedItemsKey="cpDefinitionVirtualSettingFileId"
					/>
				</aui:fieldset>

				<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="base-information">

					<%
					boolean durationDisabled = true;

					if (cpInstance.getCPSubscriptionInfo() == null) {
						durationDisabled = false;
					}

					long durationDays = 0;

					if ((cpDefinitionVirtualSetting != null) && (cpDefinitionVirtualSetting.getDuration() > 0)) {
						durationDays = cpDefinitionVirtualSetting.getDuration() / Time.DAY;
					}
					%>

					<%@ include file="/base_information.jspf" %>
				</aui:fieldset>

				<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="sample">

					<%
					FileEntry sampleFileEntry = cpDefinitionVirtualSettingDisplayContext.getSampleFileEntry();

					long sampleFileEntryId = BeanParamUtil.getLong(cpDefinitionVirtualSetting, request, "sampleFileEntryId", CPDefinitionVirtualSetting::getSampleFileEntryId);

					String textCssClass = "text-default ";

					boolean useSampleFileEntry = false;

					if (sampleFileEntryId > 0) {
						textCssClass += "hide";

						useSampleFileEntry = true;
					}
					%>

					<%@ include file="/sample.jspf" %>
				</aui:fieldset>

				<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="terms-of-use">

					<%
					JournalArticle journalArticle = cpDefinitionVirtualSettingDisplayContext.getJournalArticle();

					long termsOfUseJournalArticleResourcePrimKey = BeanParamUtil.getLong(cpDefinitionVirtualSetting, request, "termsOfUseJournalArticleResourcePrimKey", CPDefinitionVirtualSetting::getTermsOfUseJournalArticleResourcePrimKey);

					boolean useTermsOfUseJournal = false;

					if (termsOfUseJournalArticleResourcePrimKey > 0) {
						useTermsOfUseJournal = true;
					}
					%>

					<%@ include file="/terms_of_use.jspf" %>
				</aui:fieldset>
			</div>
		</div>
	</div>

	<aui:button-row>
		<aui:button cssClass="btn-lg" type="submit" />
	</aui:button-row>
</aui:form>

<aui:script>
	Liferay.Util.toggleBoxes(
		'<portlet:namespace />override',
		'<portlet:namespace />cpDefinitionVirtualSettingContainer'
	);
</aui:script>