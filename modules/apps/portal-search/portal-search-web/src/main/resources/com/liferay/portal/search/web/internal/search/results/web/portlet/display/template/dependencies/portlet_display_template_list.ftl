<div class="c-mb-4 c-mt-4 search-total-label">
	<#if searchContainer.getTotal() == 1>
		${languageUtil.format(locale, "x-result-for-x", [searchContainer.getTotal(), "<strong>" + htmlUtil.escape(searchResultsPortletDisplayContext.getKeywords()) + "</strong>"], false)}
	<#else>
		${languageUtil.format(locale, "x-results-for-x", [searchContainer.getTotal(), "<strong>" + htmlUtil.escape(searchResultsPortletDisplayContext.getKeywords()) + "</strong>"], false)}
	</#if>
</div>

<div class="display-list">
	<ul class="list-group" id="search-results-display-list">
		<#if entries?has_content>
			<#list entries as entry>
				<li class="list-group-item list-group-item-flex">
					<div class="autofit-col">
						<#if entry.isThumbnailVisible()>
							<span class="sticker">
								<span class="sticker-overlay">
									<img
										alt="${entry.getContent()!entry.getTitle() + ' ' + languageUtil.get(locale, 'thumbnail')}"
										class="sticker-img"
										src="${entry.getThumbnailURLString()}"
									/>
								</span>
							</span>
						<#elseif entry.isUserPortraitVisible() && stringUtil.equals(entry.getClassName(), userClassName)>
							<@liferay_ui["user-portrait"] userId=entry.getAssetEntryUserId() />
						<#elseif entry.isIconVisible()>
							<span class="sticker sticker-rounded sticker-secondary sticker-static">
								<@clay.icon symbol="${entry.getIconId()}" />
							</span>
						</#if>
					</div>

					<div class="autofit-col autofit-col-expand">
						<section class="autofit-section">
							<div class="c-mt-0 list-group-title">
								<a href="${entry.getViewURL()}">
									${entry.getHighlightedTitle()}
								</a>
							</div>

							<div class="search-results-metadata">
								<p class="list-group-subtext">
									<#if entry.isModelResourceVisible()>
										<span class="subtext-item">
											<strong>${entry.getModelResource()}</strong>
										</span>
									</#if>

									<#if entry.isLocaleReminderVisible()>
										<span class="lfr-portal-tooltip" title="${entry.getLocaleReminder()}">
											<@clay["icon"] symbol="${entry.getLocaleLanguageId()?lower_case?replace('_', '-')}" />
										</span>
									</#if>

									<#if entry.isCreatorVisible()>
										<span class="subtext-item">
											&#183;

											<@liferay.language key="by" />

											<strong>${htmlUtil.escape(entry.getCreatorUserName())}</strong>
										</span>
									</#if>

									<#if entry.isCreationDateVisible()>
										<span class="subtext-item">
											<@liferay.language key="on-date" />

											${entry.getCreationDateString()}
										</span>
									</#if>
								</p>

								<#if entry.isContentVisible()>
									<p class="list-group-subtext">
										<span class="subtext-item">
											${entry.getContent()}
										</span>
									</p>
								</#if>

								<#if entry.isFieldsVisible()>
									<p class="list-group-subtext">
										<#assign separate = false />

										<#list entry.getFieldDisplayContexts() as fieldDisplayContext>
											<#if separate>
												&#183;
											</#if>

											<span class="badge">${fieldDisplayContext.getName()}</span>

											<span>${fieldDisplayContext.getValuesToString()}</span>

											<#assign separate = true />
										</#list>
									</p>
								</#if>

								<#if entry.isAssetCategoriesOrTagsVisible()>
									<div class="c-mt-2 h6 search-document-tags text-default">
										<@liferay_asset["asset-tags-summary"]
											className=entry.getClassName()
											classPK=entry.getClassPK()
											paramName=entry.getFieldAssetTagNames()
											portletURL=entry.getPortletURL()
										/>

										<@liferay_asset["asset-categories-summary"]
											className=entry.getClassName()
											classPK=entry.getClassPK()
											paramName=entry.getFieldAssetCategoryIds()
											portletURL=entry.getPortletURL()
										/>
									</div>
								</#if>

								<#if entry.isDocumentFormVisible()>
									<div class="expand-details text-default">
										<span class="list-group-text text-2">
											<a href="javascript:void(0);" role="button">
												<@liferay.language key="details" />...
											</a>
										</span>
									</div>

									<div class="hide search-results-list table-details table-responsive">
										<table class="table table-head-bordered table-hover table-sm table-striped">
											<thead>
												<tr>
													<th class="table-cell-expand-smaller table-cell-text-end">
														<@liferay.language key="key" />
													</th>
													<th class="table-cell-expand">
														<@liferay.language key="value" />
													</th>
												</tr>
											</thead>

											<tbody>
												<#list entry.getDocumentFormFieldDisplayContexts() as fieldDisplayContext>
													<tr>
														<td class="table-cell-expand-smaller table-cell-text-end table-details-content">
															<strong>${htmlUtil.escape(fieldDisplayContext.getName())}</strong>
														</td>
														<td class="table-cell-expand table-details-content">
															<code>
																${fieldDisplayContext.getValuesToString()}
															</code>
														</td>
													</tr>
												</#list>
											</tbody>
										</table>
									</div>
								</#if>
							</div>
						</section>
					</div>

					<#if entry.isAssetRendererURLDownloadVisible()>
						<div class="autofit-col">
							<span
								class="c-mt-2 lfr-portal-tooltip"
								title="${languageUtil.format(locale, 'download-x', ['(' + languageUtil.formatStorageSize(entry.getAssetRendererDownloadSize(), locale) + ')'])}"
							>
								<@clay.link
									aria\-label="${languageUtil.format(locale, 'download-x', [entry.getTitle()])}"
									cssClass="link-monospaced link-outline link-outline-borderless link-outline-secondary"
									displayType="secondary"
									href="${entry.getAssetRendererURLDownload()}"
								>
									<@clay.icon symbol="download" />
								</@clay.link>
							</span>
						</div>
					</#if>
				</li>
			</#list>
		</#if>
	</ul>
</div>

<@liferay_aui.script use="aui-base">
	A.one('#search-results-display-list').delegate(
		'click',
		function(event) {
			var currentTarget = event.currentTarget;

			currentTarget.siblings('.search-results-list').toggleClass('hide');
		},
		'.expand-details'
	);
</@liferay_aui.script>