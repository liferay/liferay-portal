<div class="sort-facet">
	<div class="form-group-item form-group-item-label">
		<h5>
			${languageUtil.get(locale, "sort-by")}
		</h5>
	</div>

	<div class="d-flex flex-column form-group-item">
		<#if entries?has_content>
			<#list entries as entry>
				<label class="d-flex align-items-center" style="cursor: pointer;">
					<input
						type="radio"
						class="sort-term mr-1"
						name="sortSelection"
						value="${entry.getField()}"
						onChange="handleChangeSort(event);"
						${entry.isSelected()?then("checked","")}
					>
						${entry.getLanguageLabel()}
				</label>
			</#list>
		</#if>
	</div>
</div>

<script>
	function handleChangeSort(event) {
		const urlParams = new URLSearchParams(window.location.search);
		const selectedOptionValue = event.currentTarget.value;

		urlParams.set('sort', selectedOptionValue);

		window.location.search = urlParams;
	}
</script>