<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String rootMenuItemType = siteNavigationMenuDisplayContext.getRootMenuItemType();
SiteNavigationMenu siteNavigationMenu = siteNavigationMenuDisplayContext.getSiteNavigationMenu();
SiteNavigationMenuConfigurationDisplayContext siteNavigationMenuConfigurationDisplayContext = new SiteNavigationMenuConfigurationDisplayContext(request, siteNavigationMenuDisplayContext);
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<liferay-frontend:edit-form
	action="<%= configurationActionURL %>"
	method="post"
	name="fm"
>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />

	<liferay-frontend:edit-form-body>
		<clay:row>
			<clay:col
				md="6"
			>
				<liferay-frontend:fieldset
					cssClass="c-p-3"
					label="navigation-menu"
				>
					<aui:input id="siteNavigationMenuExternalReferenceCode" name="preferences--siteNavigationMenuExternalReferenceCode--" type="hidden" value="<%= siteNavigationMenuDisplayContext.getSiteNavigationMenuExternalReferenceCode() %>" />
					<aui:input id="siteNavigationMenuGroupExternalReferenceCode" name="preferences--siteNavigationMenuGroupExternalReferenceCode--" type="hidden" value="<%= siteNavigationMenuDisplayContext.getSiteNavigationMenuGroupExternalReferenceCode() %>" />
					<aui:input id="siteNavigationMenuType" name="preferences--siteNavigationMenuType--" type="hidden" value="<%= siteNavigationMenuDisplayContext.getSiteNavigationMenuType() %>" />

					<aui:input checked="<%= !siteNavigationMenuDisplayContext.isSiteNavigationMenuSelected() %>" cssClass="select-navigation" label="select-navigation" name="selectNavigation" type="radio" value="0" />

					<aui:select disabled="<%= siteNavigationMenuDisplayContext.isSiteNavigationMenuSelected() %>" label="" name="selectSiteNavigationMenuType" value="<%= siteNavigationMenuDisplayContext.getSelectSiteNavigationMenuType() %>">

						<%
						String layoutsLabel = siteNavigationMenuConfigurationDisplayContext.getLayoutsLabel();
						%>

						<c:if test="<%= Validator.isNotNull(layoutsLabel) %>">
							<aui:option label="<%= layoutsLabel %>" selected="<%= siteNavigationMenuConfigurationDisplayContext.isLayoutsSelected() %>" value="<%= siteNavigationMenuConfigurationDisplayContext.getLayoutsValue() %>" />
						</c:if>

						<aui:option label="primary-navigation" selected="<%= siteNavigationMenuDisplayContext.getSelectSiteNavigationMenuType() == SiteNavigationConstants.TYPE_PRIMARY %>" value="<%= SiteNavigationConstants.TYPE_PRIMARY %>" />
						<aui:option label="secondary-navigation" selected="<%= siteNavigationMenuDisplayContext.getSelectSiteNavigationMenuType() == SiteNavigationConstants.TYPE_SECONDARY %>" value="<%= SiteNavigationConstants.TYPE_SECONDARY %>" />
						<aui:option label="social-navigation" selected="<%= siteNavigationMenuDisplayContext.getSelectSiteNavigationMenuType() == SiteNavigationConstants.TYPE_SOCIAL %>" value="<%= SiteNavigationConstants.TYPE_SOCIAL %>" />
					</aui:select>

					<aui:input checked="<%= siteNavigationMenuDisplayContext.isSiteNavigationMenuSelected() %>" cssClass="select-navigation" label="choose-menu" name="selectNavigation" type="radio" value="-1" />

					<div class="c-mb-2 text-muted">
						<span id="<portlet:namespace />navigationMenuName">
							<c:if test="<%= siteNavigationMenuDisplayContext.isSiteNavigationMenuSelected() && (siteNavigationMenu != null) %>">
								<%= siteNavigationMenuDisplayContext.getSiteNavigationMenuName() %>
							</c:if>
						</span>
						<span class="c-mt-1 <%= (siteNavigationMenuDisplayContext.isSiteNavigationMenuSelected() && (siteNavigationMenu != null)) ? StringPool.BLANK : "hide" %>" id="<portlet:namespace />removeSiteNavigationMenu" role="button">
							<clay:icon
								monospaced="<%= true %>"
								symbol="times-circle"
							/>
						</span>
					</div>

					<aui:button disabled="<%= !siteNavigationMenuDisplayContext.isSiteNavigationMenuSelected() %>" name="chooseSiteNavigationMenu" value="select" />

					<div class="c-mt-4 display-template">
						<liferay-template:template-selector
							className="<%= NavItem.class.getName() %>"
							displayStyle="<%= siteNavigationMenuDisplayContext.getDisplayStyle() %>"
							displayStyleGroupId="<%= siteNavigationMenuDisplayContext.getDisplayStyleGroupId() %>"
							refreshURL="<%= configurationRenderURL %>"
						/>
					</div>
				</liferay-frontend:fieldset>

				<liferay-frontend:fieldset
					cssClass="c-p-3"
					label="menu-items-to-show"
				>
					<div id="<portlet:namespace />customDisplayOptions">
						<clay:row>
							<clay:col
								md="9"
							>
								<aui:select id="rootMenuItemType" label="start-with-menu-items-in" name="preferences--rootMenuItemType--" value="<%= rootMenuItemType %>">
									<aui:option label="level" value="absolute" />
									<aui:option label="level-relative-to-the-current-menu-item" value="relative" />
									<aui:option label="select-parent" value="select" />
								</aui:select>
							</clay:col>

							<clay:col
								md="3"
							>
								<div class="c-mt-4 c-pt-1 <%= (rootMenuItemType.equals("parent-at-level") || rootMenuItemType.equals("relative-parent-up-by")) ? StringPool.BLANK : "hide" %>" id="<portlet:namespace />rootMenuItemLevel">
									<aui:select label="" name="preferences--rootMenuItemLevel--">

										<%
										for (int i = 0; i <= 4; i++) {
										%>

											<aui:option label="<%= i %>" selected="<%= siteNavigationMenuDisplayContext.getRootMenuItemLevel() == i %>" />

										<%
										}
										%>

									</aui:select>
								</div>
							</clay:col>
						</clay:row>

						<clay:row>
							<clay:col
								md="10"
							>
								<div class="c-mb-3 <%= rootMenuItemType.equals("select") ? StringPool.BLANK : "hide" %>" id="<portlet:namespace />rootMenuItemExternalReferenceCodePanel">
									<aui:input id="rootMenuItemExternalReferenceCode" ignoreRequestValue="<%= true %>" name="preferences--rootMenuItemExternalReferenceCode--" type="hidden" value="<%= siteNavigationMenuDisplayContext.getRootMenuItemExternalReferenceCode() %>" />

									<%
									String rootMenuItemName = siteNavigationMenuDisplayContext.getSiteNavigationMenuName();

									if (siteNavigationMenuDisplayContext.isSiteNavigationMenuSelected()) {
										SiteNavigationMenuItem siteNavigationMenuItem = SiteNavigationMenuItemLocalServiceUtil.fetchSiteNavigationMenuItem(GetterUtil.getLong(siteNavigationMenuDisplayContext.getRootMenuItemId()));

										if (siteNavigationMenuItem != null) {
											SiteNavigationMenuItemTypeRegistry siteNavigationMenuItemTypeRegistry = (SiteNavigationMenuItemTypeRegistry)request.getAttribute(SiteNavigationMenuWebKeys.SITE_NAVIGATION_MENU_ITEM_TYPE_REGISTRY);

											SiteNavigationMenuItemType siteNavigationMenuItemType = siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(siteNavigationMenuItem.getType());

											rootMenuItemName = siteNavigationMenuItemType.getTitle(siteNavigationMenuItem, locale);
										}
									}
									else {
										Layout rootLayout = LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(siteNavigationMenuDisplayContext.getRootMenuItemId(), themeDisplay.getScopeGroupId(), false);

										if (rootLayout == null) {
											rootLayout = LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(siteNavigationMenuDisplayContext.getRootMenuItemId(), themeDisplay.getScopeGroupId(), true);
										}

										if (rootLayout != null) {
											rootMenuItemName = rootLayout.getName(locale);
										}
									}
									%>

									<div class="card card-horizontal card-type-directory">
										<div class="card-body">
											<clay:content-row
												verticalAlign="center"
											>
												<clay:content-col>
													<clay:sticker
														cssClass="sticker-static"
														displayType="secondary"
														icon="blogs"
													/>
												</clay:content-col>

												<clay:content-col
													expand="<%= true %>"
													gutters="<%= true %>"
												>
													<h3 class="card-title">
														<span class="text-truncate-inline">
															<span class="text-truncate" id="<portlet:namespace />rootMenuItemName"><%= HtmlUtil.escape(rootMenuItemName) %></span>
														</span>
													</h3>
												</clay:content-col>
											</clay:content-row>
										</div>
									</div>

									<aui:button name="chooseRootMenuItem" value="menu-item" />
								</div>
							</clay:col>
						</clay:row>

						<clay:row>
							<clay:col
								md="6"
							>
								<aui:select label="levels-to-display" name="preferences--displayDepth--">
									<aui:option label="unlimited" value="0" />

									<%
									for (int i = 1; i <= 20; i++) {
									%>

										<aui:option label="<%= i %>" selected="<%= siteNavigationMenuDisplayContext.getDisplayDepth() == i %>" />

									<%
									}
									%>

								</aui:select>
							</clay:col>

							<clay:col
								md="6"
							>
								<aui:select label="expand-sublevels" name="preferences--expandedLevels--" value="<%= siteNavigationMenuDisplayContext.getExpandedLevels() %>">
									<aui:option label="auto" />
									<aui:option label="all" />
								</aui:select>
							</clay:col>
						</clay:row>
					</div>
				</liferay-frontend:fieldset>
			</clay:col>

			<clay:col
				md="6"
			>
				<liferay-portlet:preview
					portletName="<%= portletResource %>"
					showBorders="<%= true %>"
				/>
			</clay:col>
		</clay:row>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"itemSelectorNamespace", PortalUtil.getPortletNamespace(ItemSelectorPortletKeys.ITEM_SELECTOR)
		).put(
			"portletResource", HtmlUtil.escapeJS(portletResource)
		).put(
			"rootMenuItemEventName", siteNavigationMenuDisplayContext.getRootMenuItemEventName()
		).put(
			"rootMenuItemSelectorURL", siteNavigationMenuDisplayContext.getRootMenuItemSelectorURL()
		).put(
			"siteNavigationMenuEventName", siteNavigationMenuDisplayContext.getSiteNavigationMenuEventName()
		).put(
			"siteNavigationMenuItemSelectorURL", siteNavigationMenuDisplayContext.getSiteNavigationMenuItemSelectorURL()
		).build()
	%>'
	module="{NavigationMenuConfiguration} from site-navigation-menu-web"
/>