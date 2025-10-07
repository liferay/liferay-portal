<style ${nonceAttribute}>
	.minium-news {
		display: inline-flex;
	}

	.minium-news .description {
		color: #7F848D;
		font-size: 12px;
	}

	.minium-news a {
		color: inherit;
		text-decoration: none;
	}

	.minium-news .thumbnail {
		height: 60px;
		margin-right: 10px;
		min-width: 60px;
		width: 60px;
	}

	.minium-news .title {
		color: #5C5E5E;
		font-size: 16px;
		margin-bottom: 10;
	}
</style>

<div class="panel">
	<div class="panel-body py-4">
		<div class="row">
			<#if entries?has_content>
				<#list entries as curEntry>
					<#assign imageURL = curEntry.getCoverImageURL(themeDisplay) />

					<div class="col-12 col-md-3 minium-news">
						<#if imageURL??>
							<style ${nonceAttribute}>
								#minium-news-image-${curEntry?index} {
									background-image: url(${imageURL})
								}
							</style>

							<div
								class="thumbnail aspect-ratio-bg-cover cover-image"
								id="minium-news-image-${curEntry?index}"
							>
							</div>
						</#if>
						<div>
							<#assign viewEntryPortletURL = renderResponse.createRenderURL() />

							${viewEntryPortletURL.setParameter("mvcRenderCommandName", "/blogs/view_entry")}

							<#if validator.isNotNull(curEntry.getUrlTitle())>
								${viewEntryPortletURL.setParameter("urlTitle", curEntry.getUrlTitle())}
							<#else>
								${viewEntryPortletURL.setParameter("entryId", curEntry.getEntryId()?string)}
							</#if>

							<a href="${viewEntryPortletURL}">
								<div class="h5 title">
									${htmlUtil.escape(blogsEntryUtil.getDisplayTitle(resourceBundle, curEntry))}
								</div>
							</a>

							<#assign publishedDate = languageUtil.getTimeDescription(locale, .now?long - curEntry.getCreateDate()?long, true) />

							<span class="description">
								<a href="#">${curEntry.getUserName()}</a> ● ${languageUtil.format(locale, "x-ago", publishedDate)}
							</span>
						</div>
					</div>
				</#list>
			</#if>
		</div>
	</div>
</div>