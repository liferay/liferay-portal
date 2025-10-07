<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditJournalFeedDisplayContext editJournalFeedDisplayContext = new EditJournalFeedDisplayContext(request, liferayPortletResponse);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(editJournalFeedDisplayContext.getRedirect());
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

renderResponse.setTitle(editJournalFeedDisplayContext.getTitle());
%>

<portlet:actionURL var="editFeedURL">
	<portlet:param name="mvcPath" value="/edit_feed.jsp" />
</portlet:actionURL>

<liferay-frontend:edit-form
	action="<%= editFeedURL %>"
	enctype="multipart/form-data"
	method="post"
	name="fm"
>
	<aui:input name="<%= ActionRequest.ACTION_NAME %>" type="hidden" value="" />
	<aui:input name="redirect" type="hidden" value="<%= editJournalFeedDisplayContext.getRedirect() %>" />
	<aui:input name="groupId" type="hidden" value="<%= editJournalFeedDisplayContext.getGroupId() %>" />
	<aui:input name="feedId" type="hidden" value="<%= editJournalFeedDisplayContext.getFeedId() %>" />
	<aui:input name="ddmRendererTemplateKey" type="hidden" value="<%= editJournalFeedDisplayContext.getDDMRendererTemplateKey() %>" />
	<aui:input name="contentField" type="hidden" value="<%= editJournalFeedDisplayContext.getContentField() %>" />

	<liferay-frontend:edit-form-body>
		<liferay-ui:error exception="<%= DuplicateFeedIdException.class %>" message="please-enter-a-unique-id" />
		<liferay-ui:error exception="<%= FeedContentFieldException.class %>" message="please-select-a-valid-feed-item-content" />
		<liferay-ui:error exception="<%= FeedIdException.class %>" message="please-enter-a-valid-id" />
		<liferay-ui:error exception="<%= FeedNameException.class %>" message="please-enter-a-valid-name" />
		<liferay-ui:error exception="<%= FeedTargetLayoutFriendlyUrlException.class %>" message="please-enter-a-valid-target-layout-friendly-url" />
		<liferay-ui:error exception="<%= FeedTargetPortletIdException.class %>" message="please-enter-a-valid-widget-id" />

		<aui:model-context bean="<%= editJournalFeedDisplayContext.getJournalFeed() %>" model="<%= JournalFeed.class %>" />

		<liferay-frontend:fieldset
			collapsed="<%= false %>"
			collapsible="<%= true %>"
			label="details"
		>
			<c:choose>
				<c:when test="<%= editJournalFeedDisplayContext.getJournalFeed() == null %>">
					<c:choose>
						<c:when test="<%= journalWebConfiguration.journalFeedForceAutogenerateId() %>">
							<aui:input name="newFeedId" type="hidden" />
							<aui:input name="autoFeedId" type="hidden" value="<%= true %>" />
						</c:when>
						<c:otherwise>
							<aui:input cssClass="lfr-input-text-container" field="feedId" fieldParam="newFeedId" label="id" name="newFeedId" value="<%= editJournalFeedDisplayContext.getNewFeedId() %>" />

							<aui:input label="autogenerate-id" name="autoFeedId" type="checkbox" />
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<aui:input name="id" type="resource" value="<%= editJournalFeedDisplayContext.getFeedId() %>" />
				</c:otherwise>
			</c:choose>

			<aui:input cssClass="lfr-input-text-container" name="name" />

			<aui:input cssClass="lfr-textarea-container" name="description" />

			<aui:input cssClass="lfr-input-text-container" helpMessage="journal-feed-target-layout-friendly-url-help" name="targetLayoutFriendlyUrl" />

			<aui:input cssClass="lfr-input-text-container" helpMessage="journal-feed-target-widget-id-help" label="target-widget-id" name="targetPortletId" />

			<c:if test="<%= editJournalFeedDisplayContext.getJournalFeed() != null %>">
				<aui:input name="url" type="resource" value="<%= editJournalFeedDisplayContext.getFeedURL() %>" />

				<aui:a href="<%= editJournalFeedDisplayContext.getFeedURL() %>" label="preview" target="_blank" />
			</c:if>
		</liferay-frontend:fieldset>

		<c:if test="<%= editJournalFeedDisplayContext.getJournalFeed() == null %>">
			<liferay-frontend:fieldset
				collapsed="<%= true %>"
				collapsible="<%= true %>"
				label="permissions"
			>
				<liferay-ui:input-permissions
					modelName="<%= JournalFeed.class.getName() %>"
				/>
			</liferay-frontend:fieldset>
		</c:if>

		<%
		List<DDMTemplate> ddmTemplates = editJournalFeedDisplayContext.getDDMTemplates();
		%>

		<liferay-frontend:fieldset
			collapsed="<%= true %>"
			collapsible="<%= true %>"
			label="web-content-constraints"
		>
			<div class="form-group">
				<aui:input name="ddmStructureId" type="hidden" value="<%= editJournalFeedDisplayContext.getDDMStructureId() %>" />

				<aui:input name="structure" type="resource" value="<%= editJournalFeedDisplayContext.getDDMStructureName() %>" />

				<div class="c-gap-1 d-flex">
					<clay:button
						additionalProps='<%=
							HashMapBuilder.<String, Object>put(
								"description", JournalFeedConstants.WEB_CONTENT_DESCRIPTION
							).put(
								"selectDDMStructurePropsTransformerURL", journalDisplayContext.getSelectDDMStructureURL()
							).build()
						%>'
						displayType="secondary"
						label="select"
						propsTransformer="{SelectDDMStructureButtonPropsTransformer} from journal-web"
					/>

					<clay:button
						disabled="<%= editJournalFeedDisplayContext.getDDMStructureId() == 0 %>"
						displayType="secondary"
						id='<%= liferayPortletResponse.getNamespace() + "removeDDMStructureButton" %>'
						label="remove"
						onClick='<%= liferayPortletResponse.getNamespace() + "removeDDMStructure();" %>'
					/>
				</div>
			</div>

			<c:choose>
				<c:when test="<%= ddmTemplates.isEmpty() %>">
					<aui:input name="ddmTemplateKey" type="hidden" value="<%= editJournalFeedDisplayContext.getDDMTemplateKey() %>" />
				</c:when>
				<c:otherwise>
					<aui:select label="template" name="ddmTemplateKey" showEmptyOption="<%= true %>">

						<%
						for (DDMTemplate ddmTemplate : ddmTemplates) {
						%>

							<aui:option label="<%= HtmlUtil.escape(ddmTemplate.getName(locale)) %>" selected="<%= Objects.equals(editJournalFeedDisplayContext.getDDMTemplateKey(), ddmTemplate.getTemplateKey()) %>" value="<%= ddmTemplate.getTemplateKey() %>" />

						<%
						}
						%>

					</aui:select>
				</c:otherwise>
			</c:choose>

			<div class="form-group">
				<aui:input name="assetCategoryIds" type="hidden" value="<%= editJournalFeedDisplayContext.getAssetCategoryIds() %>" />

				<aui:input name="assetCategory" type="resource" value="<%= editJournalFeedDisplayContext.getAssetCategoryName() %>" />

				<div class="c-gap-1 d-flex">
					<clay:button
						additionalProps='<%=
							HashMapBuilder.<String, Object>put(
								"selectAssetCategoryPropsTransformerURL", editJournalFeedDisplayContext.getAssetCategoriesSelectorURL()
							).build()
						%>'
						displayType="secondary"
						label="select"
						propsTransformer="{SelectAssetCategoryButtonPropsTransformer} from journal-web"
					/>

					<clay:button
						disabled="<%= editJournalFeedDisplayContext.getAssetCategoryId() == 0 %>"
						displayType="secondary"
						id='<%= liferayPortletResponse.getNamespace() + "removeAssetCategoryButton" %>'
						label="remove"
						onClick='<%= liferayPortletResponse.getNamespace() + "removeAssetCategory();" %>'
					/>
				</div>
			</div>
		</liferay-frontend:fieldset>

		<liferay-frontend:fieldset
			collapsed="<%= true %>"
			collapsible="<%= true %>"
			label="presentation-settings"
		>
			<aui:select label="feed-item-content" name="contentFieldSelector">
				<aui:option label="<%= JournalFeedConstants.WEB_CONTENT_DESCRIPTION %>" selected="<%= Objects.equals(editJournalFeedDisplayContext.getContentField(), JournalFeedConstants.WEB_CONTENT_DESCRIPTION) %>" />

				<optgroup label="<liferay-ui:message key="<%= JournalFeedConstants.RENDERED_WEB_CONTENT %>" />">
					<aui:option data-contentField="<%= JournalFeedConstants.RENDERED_WEB_CONTENT %>" label="use-default-template" selected="<%= Objects.equals(editJournalFeedDisplayContext.getContentField(), JournalFeedConstants.RENDERED_WEB_CONTENT) %>" value="" />

					<c:if test="<%= ddmTemplates.size() > 1 %>">

						<%
						for (DDMTemplate curTemplate : ddmTemplates) {
						%>

							<aui:option data-contentField="<%= JournalFeedConstants.RENDERED_WEB_CONTENT %>" label='<%= LanguageUtil.format(request, "use-template-x", HtmlUtil.escape(curTemplate.getName(locale)), false) %>' selected="<%= Objects.equals(editJournalFeedDisplayContext.getDDMRendererTemplateKey(), curTemplate.getTemplateKey()) %>" value="<%= curTemplate.getTemplateKey() %>" />

						<%
						}
						%>

					</c:if>
				</optgroup>

				<%
				DDMForm ddmForm = editJournalFeedDisplayContext.getDDMForm();
				%>

				<c:if test="<%= ddmForm != null %>">
					<optgroup label="<liferay-ui:message key="content-structure-fields" />">

						<%
						Map<String, DDMFormField> ddmFormFieldsMap = ddmForm.getDDMFormFieldsMap(true);

						for (DDMFormField ddmFormField : ddmFormFieldsMap.values()) {
							String ddmFormFieldType = ddmFormField.getType();
						%>

							<c:choose>
								<c:when test='<%= ddmFormFieldType.equals("radio") || ddmFormFieldType.equals("select") %>'>

									<%
									DDMFormFieldOptions ddmFormFieldOptions = ddmFormField.getDDMFormFieldOptions();

									for (String optionValue : ddmFormFieldOptions.getOptionsValues()) {
										LocalizedValue optionLabels = ddmFormFieldOptions.getOptionLabels(optionValue);

										optionValue = ddmFormField.getName() + StringPool.UNDERLINE + optionValue;
									%>

										<aui:option label='<%= TextFormatter.format(optionLabels.getString(locale), TextFormatter.J) + "(" + LanguageUtil.get(request, ddmFormFieldType) + ")" %>' selected="<%= Objects.equals(editJournalFeedDisplayContext.getContentField(), optionValue) %>" value="<%= optionValue %>" />

									<%
									}
									%>

								</c:when>
								<c:when test='<%= !ddmFormFieldType.equals("checkbox") %>'>
									<aui:option label='<%= TextFormatter.format(ddmFormField.getName(), TextFormatter.J) + "(" + LanguageUtil.get(request, ddmFormFieldType) + ")" %>' selected="<%= Objects.equals(editJournalFeedDisplayContext.getContentField(), ddmFormField.getName()) %>" value="<%= ddmFormField.getName() %>" />
								</c:when>
							</c:choose>

						<%
						}
						%>

					</optgroup>
				</c:if>
			</aui:select>

			<aui:select name="feedType">

				<%
				for (String curFeedType : RSSUtil.FEED_TYPES) {
				%>

					<aui:option label="<%= RSSUtil.getFeedTypeName(curFeedType) %>" selected="<%= Objects.equals(editJournalFeedDisplayContext.getFeedType(), curFeedType) %>" value="<%= curFeedType %>" />

				<%
				}
				%>

			</aui:select>

			<aui:input label="maximum-items-to-display" name="delta" value="10" />

			<aui:select label="order-by-column" name="orderByCol">
				<aui:option label="modified-date" />
				<aui:option label="display-date" />
			</aui:select>

			<aui:select name="orderByType">
				<aui:option label="ascending" value="asc" />
				<aui:option label="descending" value="desc" />
			</aui:select>
		</liferay-frontend:fieldset>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= editJournalFeedDisplayContext.getRedirect() %>"
			submitDisabled="<%= !editJournalFeedDisplayContext.hasSavePermission() %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"isNewJournalFeed", editJournalFeedDisplayContext.getJournalFeed() == null
		).put(
			"renderedWebContent", JournalFeedConstants.RENDERED_WEB_CONTENT
		).build()
	%>'
	module="{EditFeed} from journal-web"
/>

<aui:script>
	function <portlet:namespace />removeAssetCategory() {
		document.<portlet:namespace />fm.<portlet:namespace />assetCategoryIds.value =
			'';
		document.<portlet:namespace />fm.<portlet:namespace />assetCategory.value =
			'';
		document.<portlet:namespace />fm.<portlet:namespace />removeAssetCategoryButton.disabled = true;
	}

	function <portlet:namespace />removeDDMStructure() {
		document.<portlet:namespace />fm.<portlet:namespace />ddmStructureId.value =
			'0';
		document.<portlet:namespace />fm.<portlet:namespace />ddmTemplateKey.value =
			'';
		document.<portlet:namespace />fm.<portlet:namespace />ddmRendererTemplateKey.value =
			'';
		document.<portlet:namespace />fm.<portlet:namespace />contentField.value =
			'<%= JournalFeedConstants.WEB_CONTENT_DESCRIPTION %>';

		submitForm(document.<portlet:namespace />fm, null, false, false);
	}
</aui:script>