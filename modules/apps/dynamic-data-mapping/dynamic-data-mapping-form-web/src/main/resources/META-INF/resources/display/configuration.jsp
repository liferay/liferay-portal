<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/display/init.jsp" %>

<%
Group scopeGroup = themeDisplay.getScopeGroup();

if (scopeGroup.isStagingGroup() && !scopeGroup.isInStagingPortlet(DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN)) {
	scopeGroupId = scopeGroup.getLiveGroupId();
}

int cur = ParamUtil.getInteger(request, SearchContainer.DEFAULT_CUR_PARAM);

String keywords = ParamUtil.getString(request, "keywords");

long formInstanceId = PrefsParamUtil.getLong(PortletPreferencesFactoryUtil.getPortletSetup(renderRequest), renderRequest, "formInstanceId");

DDMFormInstance selFormInstance = DDMFormInstanceServiceUtil.fetchFormInstance(formInstanceId);
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" varImpl="configurationRenderURL" />

<aui:form action="<%= configurationRenderURL %>" method="post" name="fm1">
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL.toString() %>" />

	<div class="portlet-configuration-body-content">
		<clay:container-fluid>
			<div class="alert alert-info">
				<span class="displaying-help-message-holder <%= (selFormInstance == null) ? StringPool.BLANK : "hide" %>">
					<liferay-ui:message key="please-select-a-form-from-the-list-below" />
				</span>
				<span class="displaying-form-instance-id-holder <%= (selFormInstance == null) ? "hide" : StringPool.BLANK %>">
					<liferay-ui:message key="displaying-form" />: <span class="displaying-form-instance-id"><%= (selFormInstance != null) ? HtmlUtil.escape(selFormInstance.getName(locale)) : StringPool.BLANK %></span>
				</span>
			</div>

			<aui:fieldset>
				<div class="lfr-form-content" id="<portlet:namespace />formInstances">
					<clay:sheet>
						<liferay-ui:search-container
							emptyResultsMessage="no-forms-were-found"
							iteratorURL="<%= configurationRenderURL %>"
							total="<%= DDMFormInstanceServiceUtil.searchCount(company.getCompanyId(), scopeGroupId, keywords, WorkflowConstants.STATUS_APPROVED) %>"
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
								results="<%= DDMFormInstanceServiceUtil.search(company.getCompanyId(), scopeGroupId, keywords, WorkflowConstants.STATUS_APPROVED, searchContainer.getStart(), searchContainer.getEnd(), searchContainer.getOrderByComparator()) %>"
							/>

							<liferay-ui:search-container-row
								className="com.liferay.dynamic.data.mapping.model.DDMFormInstance"
								keyProperty="formInstanceId"
								modelVar="formInstance"
							>

								<%
								row.setData(
									HashMapBuilder.<String, Object>put(
										"form-instance-id", formInstance.getFormInstanceId()
									).put(
										"form-instance-name", formInstance.getName(locale)
									).build());
								%>

								<liferay-ui:search-container-column-text
									href="#"
									name="name"
									orderable="<%= false %>"
									value="<%= HtmlUtil.escape(formInstance.getName(locale)) %>"
								/>

								<liferay-ui:search-container-column-text
									buffer="buffer"
									href="#"
									name="description"
									orderable="<%= false %>"
								>

									<%
									buffer.append(StringUtil.shorten(HtmlUtil.escape(formInstance.getDescription(locale)), 100));
									%>

								</liferay-ui:search-container-column-text>

								<liferay-ui:search-container-column-date
									href="#"
									name="modified-date"
									orderable="<%= false %>"
									value="<%= formInstance.getModifiedDate() %>"
								/>
							</liferay-ui:search-container-row>

							<hr class="separator" />

							<liferay-ui:search-iterator
								searchResultCssClass="show-quick-actions-on-hover table table-autofit"
							/>
						</liferay-ui:search-container>
					</clay:sheet>
				</div>
			</aui:fieldset>
		</clay:container-fluid>
	</div>
</aui:form>

<aui:form action="<%= configurationActionURL %>" data-senna-off="<%= true %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value='<%= configurationRenderURL.toString() + StringPool.AMPERSAND + liferayPortletResponse.getNamespace() + "cur" + cur %>' />
	<aui:input name="preferences--formInstanceId--" type="hidden" value="<%= formInstanceId %>" />
	<aui:input name="preferences--groupId--" type="hidden" value="<%= scopeGroupId %>" />

	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>
</aui:form>

<aui:script>
	if (!window['<portlet:namespace />selectFormInstance']) {
		window['<portlet:namespace />selectFormInstance'] = function (event) {
			var rowElement = event.target.closest(
				'#<portlet:namespace />formInstances [data-form-instance-id]'
			);

			if (!rowElement) {
				return;
			}

			event.preventDefault();

			var formInstanceIdInput = document.getElementById(
				'<portlet:namespace />formInstanceId'
			);

			if (formInstanceIdInput) {
				formInstanceIdInput.value = rowElement.dataset.formInstanceId;
			}

			var formInstanceHolder = document.querySelector(
				'.displaying-form-instance-id-holder'
			);

			if (formInstanceHolder) {
				formInstanceHolder.classList.remove('hide');
			}

			var messageHolder = document.querySelector(
				'.displaying-help-message-holder'
			);

			if (messageHolder) {
				messageHolder.classList.add('hide');
			}

			var displayFormInstanceId = document.querySelector(
				'.displaying-form-instance-id'
			);

			if (displayFormInstanceId) {
				displayFormInstanceId.textContent =
					rowElement.dataset.formInstanceName +
					' (<%= UnicodeLanguageUtil.get(request, "modified") %>)';

				displayFormInstanceId.classList.add('modified');
			}
		};

		document.addEventListener(
			'click',
			window['<portlet:namespace />selectFormInstance']
		);
	}
</aui:script>