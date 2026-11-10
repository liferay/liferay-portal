<#macro buildChildrenNavItems
	displayDepth
	navItem
	menuItem = true
	navItemLevel = 2
>
	<#assign
		portletDisplay = themeDisplay.getPortletDisplay()
	/>

	<#list navItem.getChildren() as childNavigationItem>
		<#assign
			hasBrowsableChildren = childNavigationItem.hasBrowsableChildren()
		/>

		<#if hasBrowsableChildren || childNavigationItem.isBrowsable()>
			<#assign
				nav_child_css_class = ""
			/>

			<#if !childNavigationItem.isChildSelected() && childNavigationItem.isSelected()>
				<#assign
					nav_child_css_class = "active selected"
				/>
			</#if>

			<li class="${nav_child_css_class}" id="layout_${portletDisplay.getId()}_${childNavigationItem.getLayoutId()}" role="presentation">
				<#if childNavigationItem.isBrowsable()>
					<#if menuItem>
						<a class="dropdown-item" href="${childNavigationItem.getURL()}" ${childNavigationItem.getTarget()} role="menuitem">${childNavigationItem.getName()}</a>
					<#else>
						<a class="dropdown-item" href="${childNavigationItem.getURL()}" ${childNavigationItem.getTarget()}>${childNavigationItem.getName()}</a>
					</#if>
				<#else>
					<span class="dropdown-item font-weight-semi-bold navigation-menu__submenu">${childNavigationItem.getName()}</span>
				</#if>
			</li>

			<#if hasBrowsableChildren && ((displayDepth == 0) || (navItemLevel < displayDepth))>
				<ul class="list-unstyled pl-3">
					<@buildChildrenNavItems
						displayDepth = displayDepth
						menuItem = false
						navItem = childNavigationItem
						navItemLevel = (navItemLevel + 1)
					/>
				</ul>
			</#if>
		</#if>
	</#list>
</#macro>

<#if !entries?has_content>
	<#if themeDisplay.isSignedIn()>
		<div class="alert alert-info">
			<@liferay.language key="there-are-no-menu-items-to-display" />
		</div>
	</#if>
<#else>
	<#assign
		portletDisplay = themeDisplay.getPortletDisplay()
	/>

	<#if validator.isNull(portletDisplay.getId())>
		<#assign navbarId = "navbar_" + stringUtil.randomId() />
	<#else>
		<#assign navbarId = "navbar_" + portletDisplay.getId() />
	</#if>

	<div id="${navbarId}">
		<ul aria-label="<@liferay.language key="site-pages" />" class="navbar-blank navbar-nav navbar-site" role="menubar">
			<#assign navItems = entries />

			<#list navItems as navItem>
				<#assign
					displayIcon = navItem.getDisplayIcon()
					showChildrenNavItems = (displayDepth != 1) && navItem.hasBrowsableChildren()
				/>

				<#if navItem.isBrowsable() || showChildrenNavItems>
					<#assign
						nav_item_attr_has_popup = ""
						nav_item_caret = ""
						nav_item_css_class = "lfr-nav-item nav-item"
						nav_item_href_link = ""
						nav_item_link_css_class = "nav-link text-truncate"
					/>

					<#if showChildrenNavItems>
						<#assign nav_item_attr_has_popup = "aria-haspopup='true' data-toggle='liferay-dropdown'" />

						<#assign nav_item_caret>
							<span class="lfr-nav-child-toggle">
								<@clay["icon"] symbol="angle-down" />
							</span>
						</#assign>

						<#assign
							nav_item_css_class = "${nav_item_css_class} dropdown"
							nav_item_link_css_class = "${nav_item_link_css_class} dropdown-toggle"
						/>
					</#if>

					<#if navItem.isBrowsable()>
						<#assign nav_item_href_link = "href='${navItem.getURL()}'" />
					</#if>

					<#if !navItem.isChildSelected() && navItem.isSelected()>
						<#assign
							nav_item_css_class = "${nav_item_css_class} selected active"
						/>
					</#if>

					<li class="${nav_item_css_class}" id="layout_${portletDisplay.getId()}_${navItem.getLayoutId()}" role="presentation">
						<a ${nav_item_attr_has_popup} class="${nav_item_link_css_class}" ${nav_item_href_link} ${navItem.getTarget()} role="menuitem">
							<span class="text-truncate">
								<#if validator.isNull(displayIcon)>
									<#if navItem.getLayout()??>
										<@liferay_theme["layout-icon"] layout=navItem.getLayout() />
									</#if>
								<#else>
									<@clay["icon"] symbol="${displayIcon}" />
								</#if>

								${navItem.getName()} ${nav_item_caret}
							</span>
						</a>

						<#if showChildrenNavItems>
							<ul aria-expanded="false" class="child-menu dropdown-menu" role="menu">
								<@buildChildrenNavItems
									displayDepth = displayDepth
									navItem = navItem
								/>
							</ul>
						</#if>
					</li>
				</#if>
			</#list>
		</ul>
	</div>
</#if>