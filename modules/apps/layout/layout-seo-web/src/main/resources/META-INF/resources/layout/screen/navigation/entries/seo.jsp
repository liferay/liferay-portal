<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
LayoutsSEODisplayContext layoutsSEODisplayContext = (LayoutsSEODisplayContext)request.getAttribute(LayoutSEOWebKeys.LAYOUT_PAGE_LAYOUT_SEO_DISPLAY_CONTEXT);

Layout selLayout = layoutsSEODisplayContext.getSelLayout();

UnicodeProperties layoutTypeSettingsUnicodeProperties = selLayout.getTypeSettingsProperties();
%>

<liferay-util:html-top>
	<aui:link hashedFile="<%= true %>" href="layout-seo-web/css/main.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<portlet:actionURL copyCurrentRenderParameters="<%= true %>" name="/layout/edit_seo" var="editSEOURL">
	<portlet:param name="mvcRenderCommandName" value="/layout/edit_seo" />
</portlet:actionURL>

<h2 class="mb-4 text-7"><liferay-ui:message key="seo" /></h2>

<liferay-frontend:edit-form
	action="<%= editSEOURL %>"
	cssClass="pt-0"
	method="post"
	name="fm"
	wrappedFormContent="<%= false %>"
>
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="backURL" type="hidden" value="<%= layoutsSEODisplayContext.getBackURL() %>" />
	<aui:input name="portletResource" type="hidden" value='<%= ParamUtil.getString(request, "portletResource") %>' />
	<aui:input name="groupId" type="hidden" value="<%= layoutsSEODisplayContext.getGroupId() %>" />
	<aui:input name="privateLayout" type="hidden" value="<%= layoutsSEODisplayContext.isPrivateLayout() %>" />
	<aui:input name="layoutId" type="hidden" value="<%= layoutsSEODisplayContext.getLayoutId() %>" />

	<liferay-frontend:edit-form-body>
		<clay:sheet
			cssClass="ml-0"
		>
			<clay:sheet-section>
				<div aria-labelledby="<portlet:namespace />settingsTitle" role="group">
					<liferay-ui:error-marker
						key="<%= WebKeys.ERROR_SECTION %>"
						value="seo"
					/>

					<h3 class="mb-4" id="<portlet:namespace />settingsTitle">
						<liferay-ui:message key="settings" />
					</h3>

					<aui:model-context bean="<%= selLayout %>" model="<%= Layout.class %>" />

					<c:choose>
						<c:when test="<%= selLayout.isTypeAssetDisplay() %>">
							<clay:alert
								cssClass="mb-4"
								displayType="info"
								message="add-multiple-fields-to-define-how-the-meta-tags-will-be-filled"
							/>

							<div class="dpt-mapping">
								<div class="dpt-mapping-placeholder">
									<aui:model-context bean="<%= null %>" model="<%= null %>" />

									<aui:input disabled="<%= true %>" id="title" label="html-title" localized="<%= false %>" name="title" placeholder="title" />

									<div class="form-text">
										<liferay-ui:message arguments='<%= new String[] {"text", "html-title"} %>' key="map-a-x-field-it-will-be-used-as-x" />
									</div>

									<aui:input disabled="<%= true %>" id="descriptionSEO" localized="<%= false %>" name="description" placeholder="description" type="textarea" />

									<div class="form-text">
										<liferay-ui:message arguments='<%= new String[] {"text", "description"} %>' key="map-a-x-field-it-will-be-used-as-x" />
									</div>

									<aui:model-context bean="<%= selLayout %>" model="<%= Layout.class %>" />
								</div>

								<react:component
									module="{SeoMapping} from layout-seo-web"
									props="<%= layoutsSEODisplayContext.getSEOMappingData() %>"
									servletContext="<%= application %>"
								/>
							</div>
						</c:when>
						<c:otherwise>
							<aui:input helpMessage="html-title-help" id="title" label="html-title" name="title" placeholder="title" />
							<aui:input helpMessage="description-help" id="descriptionSEO" name="description" placeholder="description" />

							<c:if test="<%= !layoutsSEODisplayContext.isLayoutUtilityPageEntry() %>">

								<%
								LayoutSEOEntry selLayoutSEOEntry = layoutsSEODisplayContext.getSelLayoutSEOEntry();
								%>

								<liferay-util:buffer
									var="infoCanonicalURL"
								>
									<clay:alert
										message="use-custom-canonical-url-alert-info"
									/>
								</liferay-util:buffer>

								<c:choose>
									<c:when test="<%= selLayoutSEOEntry != null %>">
										<aui:model-context bean="<%= selLayoutSEOEntry %>" model="<%= LayoutSEOEntry.class %>" />

										<aui:input checked="<%= selLayoutSEOEntry.isCanonicalURLEnabled() %>" helpMessage="use-custom-canonical-url-help" label="use-custom-canonical-url" labelCssClass="font-weight-normal" name="canonicalURLEnabled" type="checkbox" wrapperCssClass="mb-2" />

										<div id="<portlet:namespace />customCanonicalURLSettings">
											<aui:input disabled="<%= !selLayoutSEOEntry.isCanonicalURLEnabled() %>" label="canonical-url" name="canonicalURL" placeholder="<%= layoutsSEODisplayContext.getDefaultCanonicalURL() %>">
												<aui:validator name="url" />
											</aui:input>
										</div>

										<div class="<%= selLayoutSEOEntry.isCanonicalURLEnabled() ? StringPool.BLANK : "hide" %>" id="<portlet:namespace />canonicalURLAlert">
											<%= infoCanonicalURL %>
										</div>

										<aui:model-context bean="<%= selLayout %>" model="<%= Layout.class %>" />
									</c:when>
									<c:otherwise>
										<aui:input checked="<%= false %>" helpMessage="use-custom-canonical-url-help" label="use-custom-canonical-url" name="canonicalURLEnabled" type="checkbox" wrapperCssClass="mb-2" />

										<div id="<portlet:namespace />customCanonicalURLSettings">
											<aui:input disabled="<%= true %>" label="canonical-url" localized="<%= true %>" name="canonicalURL" placeholder="<%= layoutsSEODisplayContext.getDefaultCanonicalURL() %>" type="text">
												<aui:validator name="url" />
											</aui:input>
										</div>

										<div class="hide" id="<portlet:namespace />canonicalURLAlert">
											<%= infoCanonicalURL %>
										</div>
									</c:otherwise>
								</c:choose>

								<liferay-frontend:component
									module="{toggleCanonicalURLFields} from layout-seo-web"
									servletContext="<%= application %>"
								/>

								<aui:input name="keywords" placeholder="keywords" />
							</c:if>

							<div class="form-group">
								<label><liferay-ui:message key="preview" /></label>

								<div>
									<react:component
										module="{PreviewSeo} from layout-seo-web"
										props="<%= layoutsSEODisplayContext.getSEOPreviewSeoProperties() %>"
										servletContext="<%= application %>"
									/>
								</div>
							</div>
						</c:otherwise>
					</c:choose>

					<c:if test="<%= !layoutsSEODisplayContext.isLayoutUtilityPageEntry() %>">
						<aui:input name="robots" placeholder="robots" />
					</c:if>
				</div>
			</clay:sheet-section>
		</clay:sheet>

		<c:if test="<%= PortalUtil.isLayoutSitemapable(selLayout) %>">
			<clay:sheet
				cssClass="ml-0"
			>
				<clay:sheet-section>
					<div aria-labelledby="<portlet:namespace />sitemapTitle" role="group">
						<h3 class="mb-4" id="<portlet:namespace />sitemapTitle">
							<liferay-ui:message key="sitemap" />
						</h3>

						<div class="alert alert-warning layout-prototype-info-message <%= selLayout.isLayoutPrototypeLinkActive() ? StringPool.BLANK : "hide" %>">
							<liferay-ui:message arguments='<%= new String[] {"inherit-changes", "general"} %>' key="some-page-settings-are-unavailable-because-x-is-enabled" />
						</div>

						<liferay-ui:error exception="<%= SitemapChangeFrequencyException.class %>" message="please-select-a-valid-change-frequency" />
						<liferay-ui:error exception="<%= SitemapIncludeException.class %>" message="please-select-a-valid-include-value" />
						<liferay-ui:error exception="<%= SitemapPagePriorityException.class %>" message="please-enter-a-valid-page-priority" />

						<clay:select
							cssClass="propagatable-field"
							disabled="<%= selLayout.isLayoutPrototypeLinkActive() %>"
							id='<%= liferayPortletResponse.getNamespace() + "sitemap-include" %>'
							label="include"
							name="TypeSettingsProperties--sitemap-include--"
							options="<%= layoutsSEODisplayContext.getSitemapIncludeSelectOptions() %>"
						/>

						<aui:input cssClass="propagatable-field" disabled="<%= selLayout.isLayoutPrototypeLinkActive() %>" helpMessage="page-priority-help" label="page-priority" name="TypeSettingsProperties--sitemap-priority--" placeholder="0.0" size="3" type="text" value='<%= layoutTypeSettingsUnicodeProperties.getProperty("sitemap-priority", PropsValues.SITES_SITEMAP_DEFAULT_PRIORITY) %>'>
							<aui:validator name="number" />
							<aui:validator errorMessage="please-enter-a-valid-page-priority" name="range">[0,1]</aui:validator>
						</aui:input>

						<clay:select
							cssClass="propagatable-field"
							disabled="<%= selLayout.isLayoutPrototypeLinkActive() %>"
							id='<%= liferayPortletResponse.getNamespace() + "sitemap-changefreq" %>'
							label="change-frequency"
							name="TypeSettingsProperties--sitemap-changefreq--"
							options="<%= layoutsSEODisplayContext.getSitemapChangeFrequencySelectOptions() %>"
						/>

						<c:if test="<%= layoutsSEODisplayContext.showIncludeChildLayoutsInSitemap() %>">
							<aui:input checked="<%= layoutsSEODisplayContext.isIncludeChildLayoutsInSitemap() %>" label="include-child-page-urls-in-the-xml-sitemap" labelCssClass="font-weight-normal" name="TypeSettingsProperties--sitemap-include-child-layouts--" type="checkbox" wrapperCssClass="mb-2" />

							<p class="c-mb-0 c-mt-2 small text-secondary"><liferay-ui:message key="when-this-configuration-is-enabled,-search-engines-will-be-notified-that-child-page-urls-are-available-for-crawling" /></p>
						</c:if>
					</div>
				</clay:sheet-section>
			</clay:sheet>

			<liferay-frontend:component
				module="{toggleSitemapFields} from layout-seo-web"
				servletContext="<%= application %>"
			/>
		</c:if>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= layoutsSEODisplayContext.getBackURL() %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>