<style ${nonceAttribute}>
	.app-search-count-text {
		color: #282934;
	}
</style>

<#assign
	cpDataSourceResult = cpSearchResultsDisplayContext.getCPDataSourceResult()
/>

<span class="app-search-count-text">
	<strong class="app-search-count-text mr-1">
		${cpDataSourceResult.length}
	</strong>
	Applications Available
</span>