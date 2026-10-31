<#assign
	show_footer = getterUtil.getBoolean(theme_settings["show-footer"]!)
	show_header = getterUtil.getBoolean(theme_settings["show-header"]!)
	show_header_search = getterUtil.getBoolean(theme_settings["show-header-search"]!)
	wrap_widget_page_content = getterUtil.getBoolean(theme_settings["wrap-widget-page-content"]!)
/>

<#if wrap_widget_page_content && ((layout.isTypeContent() && themeDisplay.isStateMaximized()) || (layout.getType() == "portlet"))>
	<#assign portal_content_css_class = "container" />
<#else>
	<#assign portal_content_css_class = "" />
</#if>