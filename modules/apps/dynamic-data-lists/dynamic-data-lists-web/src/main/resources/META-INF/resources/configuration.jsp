<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
int cur = ParamUtil.getInteger(request, SearchContainer.DEFAULT_CUR_PARAM);

String keywords = ParamUtil.getString(request, "keywords");

long displayDDMTemplateId = PrefsParamUtil.getLong(PortletPreferencesFactoryUtil.getPortletSetup(renderRequest), renderRequest, "displayDDMTemplateId");
boolean editable = PrefsParamUtil.getBoolean(PortletPreferencesFactoryUtil.getPortletSetup(renderRequest), renderRequest, "editable", true);
boolean formView = PrefsParamUtil.getBoolean(PortletPreferencesFactoryUtil.getPortletSetup(renderRequest), renderRequest, "formView", false);
long formDDMTemplateId = PrefsParamUtil.getLong(PortletPreferencesFactoryUtil.getPortletSetup(renderRequest), renderRequest, "formDDMTemplateId");
long recordSetId = PrefsParamUtil.getLong(PortletPreferencesFactoryUtil.getPortletSetup(renderRequest), renderRequest, "recordSetId");
String scopeType = PrefsParamUtil.getString(PortletPreferencesFactoryUtil.getPortletSetup(renderRequest), renderRequest, "lfrScopeType");
boolean spreadsheet = PrefsParamUtil.getBoolean(PortletPreferencesFactoryUtil.getPortletSetup(renderRequest), renderRequest, "spreadsheet");

Group scopeGroup = themeDisplay.getScopeGroup();

if (scopeType.equals("company")) {
	scopeGroup = GroupLocalServiceUtil.getGroup(themeDisplay.getCompanyGroupId());

	scopeGroupId = scopeGroup.getGroupId();
}

if (scopeGroup.isStagingGroup() && !scopeGroup.isInStagingPortlet(DDLPortletKeys.DYNAMIC_DATA_LISTS)) {
	scopeGroupId = scopeGroup.getLiveGroupId();
}

long[] templateGroupIds = PortalUtil.getCurrentAndAncestorSiteGroupIds(scopeGroupId);

DDLRecordSet selRecordSet = DDLRecordSetServiceUtil.fetchRecordSet(recordSetId);

String orderByCol = ParamUtil.getString(request, "orderByCol", "modified-date");
String orderByType = ParamUtil.getString(request, "orderByType", "asc");
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" varImpl="configurationRenderURL" />

<div class="portlet-configuration-body-content">
	<liferay-ui:tabs
		names='<%= (selRecordSet == null) ? "lists" : "lists,optional-configuration" %>'
		refresh="<%= false %>"
	>
		<aui:form action="<%= configurationRenderURL %>" method="post" name="fm1">
			<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL.toString() %>" />

			<liferay-ui:section>
				<clay:container-fluid>
					<div class="alert alert-info">
						<span class="displaying-help-message-holder <%= (selRecordSet == null) ? StringPool.BLANK : "hide" %>">
							<liferay-ui:message key="please-select-a-list-entry-from-the-list-below" />
						</span>
						<span class="displaying-record-set-id-holder <%= (selRecordSet == null) ? "hide" : StringPool.BLANK %>">
							<liferay-ui:message key="displaying-list" />: <span class="displaying-record-set-id"><%= (selRecordSet != null) ? HtmlUtil.escape(selRecordSet.getName(locale)) : StringPool.BLANK %></span>
						</span>
					</div>

					<aui:fieldset>
						<div class="lfr-ddl-content" id="<portlet:namespace />recordSets">
							<clay:sheet>
								<liferay-ui:search-container
									emptyResultsMessage="no-lists-were-found"
									iteratorURL="<%= configurationRenderURL %>"
									total="<%= DDLRecordSetServiceUtil.searchCount(company.getCompanyId(), scopeGroupId, keywords, DDLRecordSetConstants.SCOPE_DYNAMIC_DATA_LISTS) %>"
								>
									<div class="form-search input-append">
										<div class="input-group">
											<div class="input-group-item">
												<input aria-label="<%= LanguageUtil.get(request, "search") %>" class="form-control input-group-inset input-group-inset-after search-query" data-qa-id="searchInput" id="<portlet:namespace />keywords" name="<portlet:namespace />keywords" placeholder="<%= LanguageUtil.get(request, "keywords") %>" title="<%= LanguageUtil.get(request, "search") %>" type="text" value="<%= HtmlUtil.escapeAttribute(ParamUtil.getString(request, "keywords")) %>" />

												<div class="input-group-inset-item input-group-inset-item-after">
													<clay:button
														data-qa-id="searchButton"
														displayType="unstyled"
														icon="search"
														monospaced="<%= false %>"
														type="submit"
													/>
												</div>
											</div>
										</div>
									</div>

									<liferay-ui:search-container-results
										results="<%= DDLRecordSetServiceUtil.search(company.getCompanyId(), scopeGroupId, keywords, DDLRecordSetConstants.SCOPE_DYNAMIC_DATA_LISTS, searchContainer.getStart(), searchContainer.getEnd(), getDDLRecordSetOrderByComparator(orderByCol, orderByType)) %>"
									/>

									<liferay-ui:search-container-row
										className="com.liferay.dynamic.data.lists.model.DDLRecordSet"
										escapedModel="<%= true %>"
										keyProperty="recordSetId"
										modelVar="recordSet"
									>

										<%
										row.setData(
											HashMapBuilder.<String, Object>put(
												"record-set-id", recordSet.getRecordSetId()
											).put(
												"record-set-name", HtmlUtil.unescape(recordSet.getName(locale))
											).build());
										%>

										<liferay-ui:search-container-column-text
											href="#"
											name="name"
											orderable="<%= false %>"
											value="<%= recordSet.getName(locale) %>"
										/>

										<liferay-ui:search-container-column-text
											href="#"
											name="description"
											orderable="<%= false %>"
											value="<%= StringUtil.shorten(recordSet.getDescription(locale), 100) %>"
										/>

										<liferay-ui:search-container-column-date
											href="#"
											name="modified-date"
											orderable="<%= false %>"
											value="<%= recordSet.getModifiedDate() %>"
										/>
									</liferay-ui:search-container-row>

									<hr class="separator" />

									<liferay-ui:search-iterator
										markupView="lexicon"
										searchResultCssClass="show-quick-actions-on-hover table table-autofit"
									/>
								</liferay-ui:search-container>
							</clay:sheet>
						</div>
					</aui:fieldset>
				</clay:container-fluid>
			</liferay-ui:section>
		</aui:form>

		<aui:form action="<%= configurationActionURL %>" data-senna-off="<%= true %>" method="post" name="fm">
			<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
			<aui:input name="redirect" type="hidden" value='<%= configurationRenderURL.toString() + StringPool.AMPERSAND + liferayPortletResponse.getNamespace() + "cur" + cur %>' />
			<aui:input name="preferences--recordSetId--" type="hidden" value="<%= recordSetId %>" />

			<c:if test="<%= selRecordSet != null %>">
				<liferay-ui:section>
					<clay:container-fluid>
						<div class="alert alert-info">
							<span class="displaying-record-set-id-holder <%= (selRecordSet == null) ? "hide" : StringPool.BLANK %>">
								<liferay-ui:message key="displaying-list" />: <span class="displaying-record-set-id"><%= (selRecordSet != null) ? HtmlUtil.escape(selRecordSet.getName(locale)) : StringPool.BLANK %></span>
							</span>
						</div>

						<aui:fieldset>
							<div class="lfr-ddl-content">
								<clay:sheet>
									<aui:select helpMessage="select-the-display-template-used-to-display-the-list-records" label="display-template" name="preferences--displayDDMTemplateId--">
										<aui:option label="default" value="<%= 0 %>" />

										<%
										List<DDMTemplate> templates = new ArrayList<DDMTemplate>();

										for (long templateGroupId : templateGroupIds) {
											List<DDMTemplate> displayTemplates = DDMTemplateLocalServiceUtil.getTemplates(templateGroupId, PortalUtil.getClassNameId(DDMStructure.class), selRecordSet.getDDMStructureId(), DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY);

											templates.addAll(displayTemplates);
										}

										for (DDMTemplate template : templates) {
											if (!DDMTemplatePermission.contains(permissionChecker, template.getTemplateId(), ActionKeys.VIEW)) {
												continue;
											}

											boolean selected = false;

											if (displayDDMTemplateId == template.getTemplateId()) {
												selected = true;
											}
										%>

											<aui:option label="<%= HtmlUtil.escape(template.getName(locale)) %>" selected="<%= selected %>" value="<%= template.getTemplateId() %>" />

										<%
										}
										%>

									</aui:select>

									<aui:select helpMessage="select-the-form-template-used-to-add-records-to-the-list" label="form-template" name="preferences--formDDMTemplateId--">
										<aui:option label="default" value="<%= 0 %>" />

										<%
										List<DDMTemplate> templates = new ArrayList<DDMTemplate>();

										for (long templateGroupId : templateGroupIds) {
											List<DDMTemplate> formTemplates = DDMTemplateLocalServiceUtil.getTemplates(templateGroupId, PortalUtil.getClassNameId(DDMStructure.class), selRecordSet.getDDMStructureId(), DDMTemplateConstants.TEMPLATE_TYPE_FORM, DDMTemplateConstants.TEMPLATE_MODE_CREATE);

											templates.addAll(formTemplates);
										}

										for (DDMTemplate template : templates) {
											if (!DDMTemplatePermission.contains(permissionChecker, template.getTemplateId(), ActionKeys.VIEW)) {
												continue;
											}

											boolean selected = false;

											if (formDDMTemplateId == template.getTemplateId()) {
												selected = true;
											}
										%>

											<aui:option label="<%= HtmlUtil.escape(template.getName(locale)) %>" selected="<%= selected %>" value="<%= template.getTemplateId() %>" />

										<%
										}
										%>

									</aui:select>

									<aui:input helpMessage="check-to-allow-users-to-add-records-to-the-list" name="preferences--editable--" type="checkbox" value="<%= editable %>" />

									<aui:input helpMessage="check-to-display-the-form-entry-view" label="form-view" name="preferences--formView--" type="checkbox" value="<%= formView %>" />

									<aui:input helpMessage="check-to-view-the-list-records-in-a-spreadsheet" label="spreadsheet-view" name="preferences--spreadsheet--" type="checkbox" value="<%= spreadsheet %>" />
								</clay:sheet>
							</div>
						</aui:fieldset>
					</clay:container-fluid>
				</liferay-ui:section>
			</c:if>
		</aui:form>
	</liferay-ui:tabs>
</div>

<aui:button-row>
	<aui:button id="fm_submit" type="submit" />
	<aui:button type="cancel" />
</aui:button-row>

<aui:script use="aui-base">
	var form = A.one('#<portlet:namespace />fm');
	var submitButton = A.one('#<portlet:namespace />fm_submit');

	if (submitButton) {
		submitButton.on('click', (event) => {
			if (form) {
				form.submit();
			}
		});
	}
</aui:script>

<aui:script>
	if (!window['<portlet:namespace />selectRecordSet']) {
		window['<portlet:namespace />selectRecordSet'] = function (event) {
			var rowElement = event.target.closest(
				'#<portlet:namespace />recordSets [data-record-set-id]'
			);

			if (!rowElement) {
				return;
			}

			event.preventDefault();

			var recordSetIdInput = document.getElementById(
				'<portlet:namespace />recordSetId'
			);

			if (recordSetIdInput) {
				recordSetIdInput.value = rowElement.dataset.recordSetId;
			}

			var displayingRecordSetIdHolder = document.querySelector(
				'.displaying-record-set-id-holder'
			);

			if (displayingRecordSetIdHolder) {
				displayingRecordSetIdHolder.classList.remove('hide');
				displayingRecordSetIdHolder.removeAttribute('hidden');
				displayingRecordSetIdHolder.style.display = '';
			}

			var displayingHelpMessageHolder = document.querySelector(
				'.displaying-help-message-holder'
			);

			if (displayingHelpMessageHolder) {
				displayingHelpMessageHolder.classList.add('hide');
				displayingHelpMessageHolder.setAttribute('hidden', 'hidden');
				displayingHelpMessageHolder.style.display = 'none';
			}

			var displayRecordSetId = document.querySelector(
				'.displaying-record-set-id'
			);

			if (displayRecordSetId) {
				displayRecordSetId.textContent =
					rowElement.dataset.recordSetName +
					' (<%= UnicodeLanguageUtil.get(request, "modified") %>)';

				displayRecordSetId.classList.add('modified');
			}
		};

		document.addEventListener(
			'click',
			window['<portlet:namespace />selectRecordSet']
		);
	}
</aui:script>

<%!
private OrderByComparator<DDLRecordSet> getDDLRecordSetOrderByComparator(String orderByCol, String orderByType) {
	boolean orderByAsc = false;

	if (orderByType.equals("asc")) {
		orderByAsc = true;
	}

	OrderByComparator<DDLRecordSet> orderByComparator = null;

	if (orderByCol.equals("create-date")) {
		orderByComparator = DDLRecordSetCreateDateComparator.getInstance(orderByAsc);
	}
	else if (orderByCol.equals("modified-date")) {
		orderByComparator = new DDLRecordSetModifiedDateComparator(orderByAsc);
	}
	else if (orderByCol.equals("name")) {
		orderByComparator = DDLRecordSetNameComparator.getInstance(orderByAsc);
	}

	return orderByComparator;
}
%>