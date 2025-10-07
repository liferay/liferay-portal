<#assign
	announcementImageTypes = restClient.get("/c/p2s3announcementimagetypes/?fields=id,image.link.href&nestedFields=r_p2S3AnnouncementImageType_c_p2s3AnnouncementImageType")
	announcementImageTypesMap = {}
/>

<#if announcementImageTypes?has_content && announcementImageTypes.items?has_content>
	<#list announcementImageTypes.items as announcementImageType>
		<#assign announcementImageTypesMap = announcementImageTypesMap + {announcementImageType.id : announcementImageType.image.link.href} />
	</#list>
</#if>

<#assign entriesMap = {} />

<#list entries as entry>
	<#assign entriesMap = entriesMap + {(entry.classPK) : entry.getViewURL()} />
</#list>

<#assign
	taxonomyVocabylaryId = restClient.get("/headless-admin-taxonomy/v1.0/sites/${themeDisplay.getCompanyGroupId()}/taxonomy-vocabularies/by-external-reference-code/ANNOUCEMENT-TYPES?fields=id").id!0

	taxonomyCategories = restClient.get("/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/${taxonomyVocabylaryId}/taxonomy-categories?fields=id,name")
	taxonomyCategoriesMap = {}
/>

<#if taxonomyCategories?has_content && taxonomyCategories.items?has_content>
	<#list taxonomyCategories.items as taxonomyCategory>
		<#assign taxonomyCategoriesMap = taxonomyCategoriesMap + {taxonomyCategory.id : taxonomyCategory.name} />
	</#list>
</#if>

<#function getValue contentString end start>
	<#assign startIndex = contentString?index_of(start) />

	<#if startIndex == -1>
		<#return "" />
	</#if>

	<#assign
		substring = contentString?substring(startIndex + start?length)

		endIndex = substring?index_of(end)
	/>

	<#if endIndex == -1>
		<#return substring?trim />
	</#if>

	<#return substring?substring(0, endIndex)?trim />
</#function>

<#assign searchResults = searchContainer.getResults() />

<#list searchResults as search>
	<#assign
		contentString = search.objectEntryContent?string

		announcementImageTypeId = getValue(contentString, ", title:", "r_p2S3AnnouncementImageType_c_p2s3AnnouncementImageTypeId:")
	/>

	<div class="announcement-main-container">
		<div class="announcement-group-container">
			<div class="announcement-group-top">
				<div class="announcement-categories">
					<#list search.getValues("assetCategoryIds") as taxonomyCategoryId>
						<#if taxonomyCategoriesMap[taxonomyCategoryId]??>
							<span>${taxonomyCategoriesMap[taxonomyCategoryId]}</span>
						</#if>
					</#list>
				</div>

				<div class="announcement-title">
					<span>
						${(search.objectEntryTitle!"")?replace("null", "")}
					</span>
				</div>

				<div class="announcement-date-created">
					<#if stringUtil.equals(locale.language, "en")>
						${search.modified?datetime["yyyyMMddHHmmss"]?string("dd MMM yyyy")}
					<#else>
						${search.modified?datetime["yyyyMMddHHmmss"]?string("dd/MM/yyyy")}
					</#if>
				</div>
			</div>

			<div class="announcement-description">
				<#assign description = getValue(contentString, ", image:", "description:") />

				<#if description?has_content>
					<span>${description}</span>
				</#if>
			</div>

			<div class="announcement-button">
				<a href="${entriesMap[search.entryClassPK]!""}">
					${languageUtil.get(locale, "read-more", "Read More")}
				</a>
			</div>
		</div>

		<div class="announcement-image-container">
			<#if announcementImageTypeId?has_content && announcementImageTypesMap[announcementImageTypeId]??>
				<img alt="Announcement Image" src="${announcementImageTypesMap[announcementImageTypeId]}" />
			</#if>
		</div>
	</div>
</#list>

<style>
	.announcement-button a {
		align-items: center;
		color: #0B5FFF !important;
		cursor: pointer;
		display: flex;
		font-size: 0.875rem;
		font-weight: 600;
		gap: 4px;
		line-height: 1rem;
		max-width: 108px;
		padding: 8px 0 8px 12px;
		text-align: center;
	}

	.announcement-button a:after {
		background-image: url("data:image/svg+xml,%3Csvg width='16' height='16' viewBox='0 0 16 16' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cmask id='mask0_141_2460' style='mask-type:alpha' maskUnits='userSpaceOnUse' x='1' y='4' width='13' height='8'%3E%3Cpath d='M11.4998 11.1703L13.7395 8.76752C14.0773 8.36817 14.0898 7.66344 13.7395 7.23053L11.4998 4.82771C10.4488 3.82765 9.15688 5.35794 10.0671 6.36471L10.5895 6.92514H2.99462C1.6652 6.92514 1.6652 9.07291 2.99462 9.07291H10.5895L10.0671 9.63334C9.13186 10.7005 10.5332 12.167 11.4998 11.1703Z' fill='%230B5FFF'/%3E%3C/mask%3E%3Cg mask='url(%23mask0_141_2460)'%3E%3Crect width='16' height='16' fill='%230B5FFF'/%3E%3C/g%3E%3C/svg%3E%0A");
		background-repeat: no-repeat;
		background-size: contain;
		content: '';
		display: inline-block;
		height: 1rem;
		width: 1rem;
	}

	.announcement-button a:hover {
		background-color: #EDF3FE;
		border: 1px solid none;
		border-radius: 6px;
	}

	.announcement-categories {
		display: flex;
		gap: 8px;
	}

	.announcement-categories span {
		background: #E6EBF5;
		border: 1px solid #E6EBF5;
		border-radius: 4px;
		color: #1C3667;
		font-size: 13px;
		font-weight: 400;
		line-height: 16px;
		padding: 4px 8px;
	}

	.announcement-date-created {
		color: #282934;
		font-size: 0.875rem;
		font-weight: 300;
		line-height: 1rem;
	}

	.announcement-description {
		color: #54555F;
		font-size: 1rem;
		font-weight: 400;
		line-height: 24px;
		width: 95%;
	}

	.announcement-group-container {
		display: flex;
		flex-direction: column;
		gap: 1.5rem;
		justify-content: space-between;
		width: 70%;
	}

	.announcement-group-top {
		display: flex;
		flex-direction: column;
		gap: 8px;
	}

	.announcement-image-container {
		display: flex;
		flex-direction: row-reverse;
	}

	.announcement-image-type {
		max-height: 250px;
		max-width: 350px;
		object-fit: contain;
	}

	.announcement-main-container {
		align-items: start;
		border-bottom: 1px solid #E2E2E4;
		display: flex;
		justify-content: space-between;
		padding-bottom: 2.5rem;
		padding-top: 2.5rem;
		width: 100% !important;
	}

	.announcement-title {
		color: #282934;
		font-size: 1.75rem;
		font-weight: 700;
		line-height: 2rem;
	}
</style>