<ul class="adt-navigation">
	<#if entries?has_content>
		<#list entries as navPrimaryItem>
			<#if (navPrimaryItem.getChildren()?size > 0)>
				<div class="adt-nav-item dropdown dropdown-action w-100">
					<button
						aria-expanded="true"
						class="adt-nav-text align-items-center d-flex menu-info"
						data-toggle="liferay-dropdown"
						id="main-menu-id"
						tabindex="4"
					>
						<span class="adt-nav-title text-truncate">
							${navPrimaryItem.getName()}
						</span>
						<span class="adt-nav-caret-bottom-icon align-self-center">
							<svg class="lexicon-icon lexicon-icon-caret-bottom" role="presentation" viewBox="0 0 512 512"><use xlink:href="/o/admin-theme/images/clay/icons.svg#caret-bottom"></use></svg>
						</span>
					</button>

					<@render_navigation_dropdown navPrimaryItem />
				</div>
			<#else>
				<a class="adt-nav-item w-100" href="${navPrimaryItem.getURL()}">
					<div class="adt-nav-text d-flex pr-3" tabindex="4">
						<span class="adt-nav-title text-truncate">
							${navPrimaryItem.getName()}
						</span>
					</div>
				</a>
			</#if>
		</#list>
	</#if>
</ul>

<#macro render_navigation_dropdown
	navPrimaryItem
>
	<div class="adt-submenu dropdown-menu main-menu-dropdown position-absolute pt-2">
		<div class="adt-submenu-outer-wrapper">
			<div class="adt-submenu-inner-wrapper">
				<#list navPrimaryItem.getChildren() as navSecondaryItem>
					<#assign
						secondaryCustomFields = navSecondaryItem.getExpandoAttributes()!{}

						backgroundColor = secondaryCustomFields["Submenu Background"]?first!""
						childColumns = secondaryCustomFields["Submenu Child Columns"]?first!""
						columnSpan = secondaryCustomFields["Submenu Column Span"]!?first!""
						imageURL = getLocalizedExpandoValue(secondaryCustomFields["Menu Item Image URL"])!""
						menuItemType = secondaryCustomFields["Menu Item Type"]?first!""
					/>

					<#if childColumns?has_content>
						<#assign childColumns = (columnSpan?number/childColumns?number)?floor?string />
					</#if>

					<#if columnSpan?has_content>
						<#assign columnSpan = "_" + columnSpan + "-section-span" />
					</#if>

					<ul class="adt-submenu-section ${backgroundColor} ${columnSpan}">
						<li class="adt-submenu-header color-neutral-8 font-size-small-caps">
							<#if stringUtil.equals(menuItemType, "Image") && imageURL?has_content>
								<img class="adt-submenu-header-image" loading="lazy" src="${imageURL}" />
							</#if>
							${navSecondaryItem.getName()}
						</li>

						<#list navSecondaryItem.getChildren() as navTertiaryItem>
							<#assign
								tertiaryCustomFields = navTertiaryItem.getExpandoAttributes()

								descriptionText = getLocalizedExpandoValue(tertiaryCustomFields["Menu Item Description"])!""
								imageURL = getLocalizedExpandoValue(tertiaryCustomFields["Menu Item Image URL"])!""
								menuItemType = tertiaryCustomFields["Menu Item Type"]?first!""
								preheaderText = getLocalizedExpandoValue(tertiaryCustomFields["Menu Item Preheader"])!""
							/>

							<li class="adt-submenu-item-content ${menuItemType?lower_case}-type grid-column-span-${childColumns}">
								<a class="adt-submenu-item-link" href="${navTertiaryItem.getURL()}" tabindex="4">
									<#if stringUtil.equals(menuItemType, "Image") && imageURL?has_content>
										<img class="adt-submenu-item-image" loading="lazy" src="${imageURL}" />
									</#if>

									<div class="adt-submenu-item-text">
										<#if stringUtil.equals(menuItemType, "Image") && preheaderText?has_content>
											<div class="adt-submenu-item-preheader color-neutral-3 font-weight-semi-bold">
												${preheaderText}
											</div>
										</#if>

										<div class="adt-submenu-item-title h5">
											${navTertiaryItem.getName()}
										</div>

										<#if descriptionText?has_content>
											<div class="adt-submenu-item-description">
												${descriptionText}
											</div>
										</#if>
									</div>
								</a>
							</li>
						</#list>
					</ul>
				</#list>
			</div>
		</div>
	</div>
</#macro>

<#function getLocalizedExpandoValue expandoField>
	<#list expandoField as language, expandoValue>
		<#if language == locale>
			<#return expandoValue />
		</#if>
	</#list>
</#function>