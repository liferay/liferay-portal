<#assign
	portlet_display = portletDisplay
	portlet_back_url = htmlUtil.escapeHREF(portlet_display.getURLBack())
	portlet_content_css_class = "portlet-content"
	portlet_display_name = htmlUtil.escape(portlet_display.getPortletDisplayName())
	portlet_display_root_portlet_id = htmlUtil.escapeAttribute(portlet_display.getRootPortletId())
	portlet_id = htmlUtil.escapeAttribute(portlet_display.getId())
	portlet_title = htmlUtil.escape(portlet_display.getTitle())
/>

<section class="portlet" id="portlet_${portlet_id}">
	<#if portlet_display.getPortletConfigurationIconMenu()?? && portlet_display.getPortletToolbar()?? && portlet_display.isPortletDecorate() && portlet_display.isShowPortletTopper() && !portlet_display.isStateMax()>
		<#assign
			portlet_configuration_icon_menu = portlet_display.getPortletConfigurationIconMenu()
			portlet_toolbar = portlet_display.getPortletToolbar()

			portlet_configuration_icons = portlet_configuration_icon_menu.getPortletConfigurationIcons(portlet_display_root_portlet_id, renderRequest, renderResponse)

			portlet_title_menus = portlet_toolbar.getPortletTitleMenus(portlet_display_root_portlet_id, renderRequest, renderResponse)
		/>

		<#if (portlet_configuration_icons?has_content || portlet_title_menus?has_content)>
			<header class="cadmin portlet-topper">
				<#if portletDisplay.isShowMoveIcon()>
					<@clay["icon"]
						cssClass="mr-2 mt-0"
						symbol="drag"
					/>
				</#if>

				<div class="portlet-title-default">
					<span class="portlet-name-text">${portlet_display_name}</span>
				</div>

				<#foreach portletTitleMenu in portlet_title_menus>
					<menu class="portlet-topper-toolbar" id="portlet-title-menu_${portlet_id}_${portletTitleMenu_index}" type="toolbar">
						${portletTitleMenu.setDirection("right cadmin")}

						<@liferay_ui["menu"] menu=portletTitleMenu />
					</menu>
				</#foreach>

				<#if portlet_configuration_icons?has_content>
					<#if (portlet_configuration_icons?size > 1)>
						<menu class="portlet-topper-toolbar" id="portlet-topper-toolbar_${portlet_id}" type="toolbar">
							<@liferay_frontend["icon-options"]
								direction="right cadmin"
								portletConfigurationIcons=portlet_configuration_icons
							/>
						</menu>
					<#else>
						<menu class="portlet-topper-toolbar" id="portlet-topper-toolbar_${portlet_id}" type="toolbar">
							<#assign portletConfigurationIcon = portlet_configuration_icons[0] />

							<#if portletConfigurationIcon.getIconCssClass()?? && portletConfigurationIcon.getOnClick(renderRequest, renderResponse)??>
								<@liferay_ui["icon"]
									icon="${portletConfigurationIcon.getIconCssClass()}"
									markupView="lexicon"
									onClick="${portletConfigurationIcon.getOnClick(renderRequest, renderResponse)}"
									url="javascript:void(0);"
								/>
							<#else>
								<@liferay_frontend["icon-options"]
									direction="right cadmin"
									portletConfigurationIcons=portlet_configuration_icons
								/>
							</#if>
						</menu>
					</#if>
				</#if>
			</header>

			<#assign portlet_content_css_class = portlet_content_css_class + " portlet-content-editable" />
		</#if>
	</#if>

	<div class="${portlet_content_css_class}">
		<@liferay_util["buffer"] var="portlet_header">
			<@liferay_util["dynamic-include"] key="portlet_header_${portlet_display_root_portlet_id}" />
		</@>

		<#if portlet_display.isShowBackIcon() || portlet_display.isShowPortletTitle() || portlet_header?has_content>
			<div class="autofit-float autofit-row portlet-header">
				<#if portlet_display.isShowBackIcon()>
					<div class="autofit-col">
						<div class="autofit-section">
							<a class="icon-monospaced portlet-icon-back text-default" href="${portlet_back_url}" title="<@liferay.language key="return-to-full-page" />">
								<@liferay_ui["icon"]
									icon="angle-left"
									markupView="lexicon"
								/>
							</a>
						</div>
					</div>
				</#if>

				<#if portlet_display.isShowPortletTitle()>
					<div class="autofit-col autofit-col-expand">
						<h2 class="portlet-title-text">${portlet_title}</h2>
					</div>
				</#if>

				<#if portlet_header?has_content>
					<div class="autofit-col autofit-col-end">
						<div class="autofit-section">
							${portlet_header}
						</div>
					</div>
				</#if>
			</div>
		</#if>

		${portlet_display.writeContent(writer)}
	</div>
</section>