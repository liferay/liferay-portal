<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
JournalArticle article = journalDisplayContext.getArticle();

JournalEditArticleDisplayContext journalEditArticleDisplayContext = (JournalEditArticleDisplayContext)request.getAttribute(JournalEditArticleDisplayContext.class.getName());

journalEditArticleDisplayContext.setViewAttributes();
%>

<aui:model-context bean="<%= article %>" model="<%= JournalArticle.class %>" />

<portlet:actionURL var="editArticleActionURL" windowState="<%= WindowState.MAXIMIZED.toString() %>">
	<portlet:param name="mvcRenderCommandName" value="/journal/edit_article" />
	<portlet:param name="ddmStructureId" value="<%= String.valueOf(journalEditArticleDisplayContext.getDDMStructureId()) %>" />
</portlet:actionURL>

<portlet:renderURL var="editArticleRenderURL" windowState="<%= WindowState.MAXIMIZED.toString() %>">
	<portlet:param name="mvcRenderCommandName" value="/journal/edit_article" />
</portlet:renderURL>

<aui:form action="<%= editArticleActionURL %>" cssClass="edit-article-form" enctype="multipart/form-data" method="post" name="fm1" onSubmit="event.preventDefault();">
	<aui:input name="<%= ActionRequest.ACTION_NAME %>" type="hidden" />
	<aui:input name="hideDefaultSuccessMessage" type="hidden" value="<%= journalEditArticleDisplayContext.getClassNameId() == PortalUtil.getClassNameId(DDMStructure.class) %>" />
	<aui:input name="redirect" type="hidden" value="<%= journalEditArticleDisplayContext.getRedirect() %>" />
	<aui:input name="portletResource" type="hidden" value="<%= journalEditArticleDisplayContext.getPortletResource() %>" />
	<aui:input name="refererPlid" type="hidden" value="<%= journalEditArticleDisplayContext.getRefererPlid() %>" />
	<aui:input name="referringPortletResource" type="hidden" value="<%= journalEditArticleDisplayContext.getReferringPortletResource() %>" />
	<aui:input name="groupId" type="hidden" value="<%= journalEditArticleDisplayContext.getGroupId() %>" />
	<aui:input name="folderId" type="hidden" value="<%= journalEditArticleDisplayContext.getFolderId() %>" />
	<aui:input name="classNameId" type="hidden" value="<%= journalEditArticleDisplayContext.getClassNameId() %>" />
	<aui:input name="classPK" type="hidden" value="<%= journalEditArticleDisplayContext.getClassPK() %>" />
	<aui:input name="articleId" type="hidden" value="<%= journalEditArticleDisplayContext.getArticleId() %>" />
	<aui:input name="version" type="hidden" value="<%= ((article == null) || article.isNew()) ? journalEditArticleDisplayContext.getVersion() : article.getVersion() %>" />
	<aui:input name="articleURL" type="hidden" value="<%= editArticleRenderURL %>" />
	<aui:input name="ddmStructureId" type="hidden" value="<%= journalEditArticleDisplayContext.getDDMStructureId() %>" />
	<aui:input name="ddmTemplateId" type="hidden" />
	<aui:input name="availableLocales" type="hidden" />
	<aui:input name="defaultLanguageId" type="hidden" value="<%= journalEditArticleDisplayContext.getDefaultArticleLanguageId() %>" />
	<aui:input name="languageId" type="hidden" value="<%= journalEditArticleDisplayContext.getSelectedLanguageId() %>" />
	<aui:input name="workflowAction" type="hidden" value="<%= String.valueOf(WorkflowConstants.ACTION_SAVE_DRAFT) %>" />

	<nav class="component-tbar subnav-tbar-light tbar tbar-article">

		<%
		DDMStructure ddmStructure = journalEditArticleDisplayContext.getDDMStructure();
		%>

		<clay:container-fluid>
			<ul class="tbar-nav">
				<li class="tbar-item tbar-item-expand">
					<div class="autofit-row sidebar-section">
						<div class="autofit-col d-flex flex-row">
							<span aria-hidden="true" class="loading-animation mx-4 my-2"></span>

							<react:component
								module="{TranslationManager} from journal-web"
								props='<%=
									HashMapBuilder.<String, Object>put(
										"defaultLanguageId", journalEditArticleDisplayContext.getDefaultArticleLanguageId()
									).put(
										"fields", journalEditArticleDisplayContext.getFieldMap()
									).put(
										"locales", journalEditArticleDisplayContext.getLocales()
									).put(
										"namespace", liferayPortletResponse.getNamespace()
									).put(
										"selectedLanguageId", journalEditArticleDisplayContext.getSelectedLanguageId()
									).build()
								%>'
							/>
						</div>

						<c:if test="<%= !JournalUtil.isEditDefaultValues(article) %>">
							<div class="c-ml-2">
								<react:component
									module="{TranslationOptions} from journal-web"
									props='<%=
										HashMapBuilder.<String, Object>put(
											"defaultLanguageId", journalEditArticleDisplayContext.getDefaultArticleLanguageId()
										).put(
											"fields", journalEditArticleDisplayContext.getFieldMap()
										).put(
											"locales", journalEditArticleDisplayContext.getLocales()
										).put(
											"namespace", liferayPortletResponse.getNamespace()
										).put(
											"selectedLanguageId", journalEditArticleDisplayContext.getSelectedLanguageId()
										).build()
									%>'
								/>
							</div>

							<div class="autofit-col c-ml-2">
								<react:component
									module="{TranslationFilter} from journal-web"
									props='<%=
										HashMapBuilder.<String, Object>put(
											"defaultLanguageId", journalEditArticleDisplayContext.getDefaultArticleLanguageId()
										).put(
											"fields", journalEditArticleDisplayContext.getFieldMap()
										).put(
											"locales", journalEditArticleDisplayContext.getLocales()
										).put(
											"namespace", liferayPortletResponse.getNamespace()
										).put(
											"selectedLanguageId", journalEditArticleDisplayContext.getSelectedLanguageId()
										).build()
									%>'
								/>
							</div>
						</c:if>
					</div>
				</li>
				<li class="tbar-item">
					<div class="c-gap-3 form-group-sm journal-article-button-row mb-0 tbar-section text-right">
						<c:choose>
							<c:when test='<%= FeatureFlagManagerUtil.isEnabled("LPD-11228") && !JournalUtil.isEditDefaultValues(article) %>'>
								<div class="align-items-center d-none small" id="<portlet:namespace />lockErrorIndicator">
									<react:component
										module="{LockErrorIndicator} from journal-web"
									/>
								</div>

								<div class="align-items-center d-none small" id="<portlet:namespace />savingChangesIndicator">
									<liferay-ui:message key="saving" />

									<span aria-hidden="true" class="d-inline-block loading-animation loading-animation-sm ml-2 my-0"></span>
								</div>

								<div class="align-items-center d-none small text-success" id="<portlet:namespace />changesSavedIndicator">
									<liferay-ui:message key="saved" />

									<clay:icon
										cssClass="ml-2 mt-0"
										symbol="check-circle"
									/>
								</div>

								<div>
									<react:component
										module="{UndoRedo} from journal-web"
										props='<%=
											HashMapBuilder.<String, Object>put(
												"initialDefaultLanguageId", journalEditArticleDisplayContext.getDefaultArticleLanguageId()
											).put(
												"initialFields", journalEditArticleDisplayContext.getFieldMap()
											).put(
												"languageId", journalEditArticleDisplayContext.getSelectedLanguageId()
											).build()
										%>'
									/>
								</div>
							</c:when>
							<c:otherwise>
								<clay:link
									borderless="<%= true %>"
									displayType="secondary"
									href="<%= journalEditArticleDisplayContext.getBackURL() %>"
									label="cancel"
									type="button"
								/>
							</c:otherwise>
						</c:choose>

						<c:if test="<%= journalEditArticleDisplayContext.getClassNameId() > JournalArticleConstants.CLASS_NAME_ID_DEFAULT %>">
							<portlet:actionURL name="/journal/reset_values_ddm_structure" var="resetValuesDDMStructureURL">
								<portlet:param name="mvcPath" value="/edit_data_definition.jsp" />
								<portlet:param name="redirect" value="<%= currentURL %>" />
								<portlet:param name="groupId" value="<%= String.valueOf(journalEditArticleDisplayContext.getGroupId()) %>" />
								<portlet:param name="articleId" value="<%= journalEditArticleDisplayContext.getArticleId() %>" />
								<portlet:param name="ddmStructureId" value="<%= String.valueOf(ddmStructure.getStructureId()) %>" />
							</portlet:actionURL>

							<clay:button
								data-url="<%= resetValuesDDMStructureURL %>"
								displayType="secondary"
								id='<%= liferayPortletResponse.getNamespace() + "resetValuesButton" %>'
								label="reset-values"
							/>
						</c:if>

						<c:if test="<%= journalEditArticleDisplayContext.hasSavePermission() %>">
							<div>
								<c:if test='<%= !FeatureFlagManagerUtil.isEnabled("LPD-11228") && (journalEditArticleDisplayContext.getClassNameId() == JournalArticleConstants.CLASS_NAME_ID_DEFAULT) %>'>
									<clay:button
										cssClass="mr-3"
										data-actionname='<%= ((article == null) || Validator.isNull(article.getArticleId())) ? "/journal/add_article" : "/journal/update_article" %>'
										displayType="secondary"
										id='<%= liferayPortletResponse.getNamespace() + "saveButton" %>'
										label="<%= journalEditArticleDisplayContext.getSaveButtonLabel() %>"
										type="submit"
									/>
								</c:if>

								<clay:button
									data-actionname="<%= Constants.PUBLISH %>"
									displayType="primary"
									id='<%= liferayPortletResponse.getNamespace() + "publishButton" %>'
									label="<%= journalEditArticleDisplayContext.getPublishButtonLabel() %>"
									type="submit"
								/>

								<react:component
									module="{SaveButtons} from journal-web"
									props="<%= journalEditArticleDisplayContext.getSaveButtonsContext() %>"
								/>
							</div>
						</c:if>

						<div role="tablist">
							<clay:button
								aria-controls='<%= liferayPortletResponse.getNamespace() + "contextualSidebarContainer" %>'
								aria-label='<%= LanguageUtil.get(request, "close-configuration-panel") %>'
								aria-selected="true"
								borderless="<%= true %>"
								cssClass="lfr-portal-tooltip"
								displayType="secondary"
								icon="cog"
								id='<%= liferayPortletResponse.getNamespace() + "contextualSidebarButton" %>'
								role="tab"
								small="<%= true %>"
								title="close-configuration-panel"
								type="button"
							/>
						</div>
					</div>
				</li>
			</ul>
		</clay:container-fluid>
	</nav>

	<div aria-label="<%= LanguageUtil.get(request, "configuration-panel") %>" class="contextual-sidebar edit-article-sidebar sidebar-light sidebar-sm" id="<portlet:namespace />contextualSidebarContainer" role="tabpanel" tabindex="-1">
		<div class="overflow-hidden sidebar-body">
			<div class="d-flex d-sm-none justify-content-end">
				<clay:button
					aria-controls='<%= liferayPortletResponse.getNamespace() + "contextualSidebarContainer" %>'
					aria-label='<%= LanguageUtil.get(request, "close-configuration-panel") %>'
					borderless="<%= true %>"
					cssClass="lfr-portal-tooltip"
					displayType="secondary"
					icon="times"
					monospaced="<%= true %>"
					propsTransformer="{CloseConfigurationPanelPropsTransformer} from journal-web"
					small="<%= true %>"
					title="close-configuration-panel"
					type="button"
				/>
			</div>

			<div class="sheet-row">
				<clay:tabs
					tabsItems="<%= journalEditArticleDisplayContext.getTabsItems() %>"
				>
					<clay:tabs-panel>
						<liferay-frontend:form-navigator
							fieldSetCssClass="panel-group-flush"
							formModelBean="<%= article %>"
							id="<%= FormNavigatorConstants.FORM_NAVIGATOR_ID_JOURNAL %>"
							showButtons="<%= false %>"
						/>
					</clay:tabs-panel>

					<c:if test="<%= (article != null) && (journalEditArticleDisplayContext.getClassNameId() == JournalArticleConstants.CLASS_NAME_ID_DEFAULT) %>">
						<clay:tabs-panel>
							<liferay-layout:layout-classed-model-usages-view
								className="<%= JournalArticle.class.getName() %>"
								classPK="<%= article.getResourcePrimKey() %>"
							/>
						</clay:tabs-panel>
					</c:if>
				</clay:tabs>
			</div>
		</div>
	</div>

	<div class="contextual-sidebar-content">
		<clay:container-fluid
			cssClass="container-view"
		>
			<div class="article-content-content">
				<%@ include file="/edit_article_exceptions.jspf" %>
				<div id="<portlet:namespace />metadata">
					<clay:panel
						displayTitle='<%= LanguageUtil.get(request, "metadata") %>'
						displayType="block"
						expanded="<%= true %>"
					>
						<div class="c-gap-4 d-flex flex-column panel-body">
							<div id="<portlet:namespace />titleMapAsXMLWrapper">
								<label for="<portlet:namespace />titleMapAsXML" id="<portlet:namespace />Aria"><liferay-ui:message key="title" /></label>

								<aui:input cssClass="form-control-inline form-control-sm" defaultLanguageId="<%= journalEditArticleDisplayContext.getDefaultArticleLanguageId() %>" label='<%= LanguageUtil.get(request, "title") %>' labelCssClass="sr-only" languagesDropdownDirection="down" languagesDropdownVisible="<%= false %>" localized="<%= true %>" name="titleMapAsXML" placeholder='<%= LanguageUtil.format(request, "untitled-x", HtmlUtil.escape(ddmStructure.getName(locale))) %>' required="<%= journalEditArticleDisplayContext.getClassNameId() == JournalArticleConstants.CLASS_NAME_ID_DEFAULT %>" selectedLanguageId="<%= journalEditArticleDisplayContext.getSelectedLanguageId() %>" type="text" wrapperCssClass="article-content-title mb-0" />
							</div>

							<div id="<portlet:namespace />descriptionMapAsXMLWrapper">
								<label for="<portlet:namespace />descriptionMapAsXML" id="<portlet:namespace />Aria"><liferay-ui:message key="description" /></label>

								<liferay-ui:input-localized
									availableLocales="<%= journalEditArticleDisplayContext.getAvailableLocales() %>"
									defaultLanguageId="<%= journalEditArticleDisplayContext.getDefaultArticleLanguageId() %>"
									editorName="ckeditor"
									formName="fm"
									ignoreRequestValue="<%= journalEditArticleDisplayContext.isChangeStructure() %>"
									languagesDropdownVisible="<%= false %>"
									name="descriptionMapAsXML"
									selectedLanguageId="<%= journalEditArticleDisplayContext.getSelectedLanguageId() %>"
									type="editor"
									xml="<%= (article != null) ? article.getDescriptionMapAsXML() : StringPool.BLANK %>"
								/>

								<p class="text-3 text-secondary" id="<portlet:namespace />descriptionNotTranslatableMessage" hidden>
									<liferay-ui:message arguments="description" key="the-x-will-not-count-as-a-translatable-field" />
								</p>
							</div>

							<c:if test="<%= !JournalUtil.isEditDefaultValues(article) %>">
								<div id="<portlet:namespace />friendlyURLWrapper">
									<c:if test="<%= Validator.isNotNull(journalEditArticleDisplayContext.getFriendlyURLDuplicatedWarningMessage()) %>">
										<clay:alert
											dismissible="<%= true %>"
											displayType="warning"
											message="<%= journalEditArticleDisplayContext.getFriendlyURLDuplicatedWarningMessage() %>"
										/>
									</c:if>

									<label for="<portlet:namespace />friendlyURL">
										<liferay-ui:message key="friendly-url" />

										<%
										StringBundler sb = new StringBundler(3);

										sb.append(LanguageUtil.get(request, "changing-the-friendly-url-will-affect-all-web-content-article-versions-even-when-autosaving"));
										sb.append(StringPool.SPACE);
										sb.append(LanguageUtil.get(request, "the-friendly-url-may-be-modified-to-ensure-uniqueness"));
										%>

									</label>

									<span class="d-inline-block lfr-portal-tooltip text-4 text-secondary" tabindex="0" title="<%= sb.toString() %>">
										<clay:icon
											symbol="question-circle-full"
										/>
									</span>

									<liferay-friendly-url:input
										className="<%= JournalArticle.class.getName() %>"
										classPK="<%= (article == null) || (article.getPrimaryKey() == 0) ? 0 : article.getResourcePrimKey() %>"
										inputAddon="<%= journalEditArticleDisplayContext.getFriendlyURLBase() %>"
										languagesDropdownVisible="<%= false %>"
										name="friendlyURL"
										showHistory="<%= false %>"
										showLabel="<%= false %>"
									/>

									<p class="text-3 text-secondary" id="<portlet:namespace />friendlyURLNotTranslatableMessage" hidden>
										<liferay-ui:message arguments="friendly-url" key="the-x-will-not-count-as-a-translatable-field" />
									</p>
								</div>
							</c:if>
						</div>
					</clay:panel>
				</div>

				<div id="<portlet:namespace />content">
					<liferay-frontend:fieldset
						collapsed="<%= false %>"
						collapsible="<%= true %>"
						cssClass="edit-article-panel"
						label="fields"
					>
						<div class="c-px-2 panel-body">
							<%@ include file="/article_content.jspf" %>
						</div>
					</liferay-frontend:fieldset>
				</div>

				<div>
					<react:component
						module="{EmptyStatePlaceholder} from journal-web"
					/>
				</div>
			</div>
		</clay:container-fluid>
	</div>
</aui:form>

<liferay-frontend:component
	componentId='<%= liferayPortletResponse.getNamespace() + "JournalPortletComponent" %>'
	context="<%= journalEditArticleDisplayContext.getComponentContext() %>"
	module="{JournalPortlet} from journal-web"
	servletContext="<%= application %>"
/>

<%@ include file="/friendly_url_changed_message.jspf" %>