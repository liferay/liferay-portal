<div class="sort-filter">
	<div class="form-group-autofit sort-filter-container">
		<div
			aria-controls="sort-filter-items"
			aria-expanded="true"
			class="form-group-item form-group-item-label form-group-item-shrink sort-collapse-trigger sort-group-container"
			data-target="#sort-filter-items"
			data-toggle="collapse"
			onclick="toggleCollapse('sort-filter-items');"
			role="button"
			tabindex="0"
		>
			<span class="text-truncate-inline-sort-filter">
				<span class="sort-title text-truncate">
					${languageUtil.get(locale, "sort-by")}
				</span>
			</span>

			<div class="collapse-icon">
				<div class="collapse-icon-closed">
					<svg
						aria-hidden="true"
						class="lexicon-icon lexicon-icon-angle-right"
						focusable="false"
						viewBox="0 0 512 512"
					>
						<g>
							<path
								class="lexicon-icon-outline"
								d="M375.2,239.2L173.3,37c-23.6-23-59.9,11.9-36,35.1l183,183.9L137.4,439.8c-24,23.5,12.5,58.2,36.1,35.2l201.7-202.1C385.4,262.8,384.5,248.5,375.2,239.2z"
							></path>
						</g>
					</svg>
				</div>

				<div class="collapse-icon-open">
					<svg
						aria-hidden="true"
						class="lexicon-icon lexicon-icon-angle-down"
						focusable="false"
						viewBox="0 0 512 512"
					>
						<g>
							<path
								class="lexicon-icon-outline"
								d="M272.8,375.2L475,173.3c23-23.6-11.9-59.9-35.1-36L256,320.3L72.2,137.4c-23.5-24-58.2,12.5-35.2,36.1l202.1,201.7C249.2,385.4,263.5,384.5,272.8,375.2z"
							></path>
						</g>
					</svg>
				</div>
			</div>
		</div>
	</div>

	<div class="collapse form-check form-group-item show" id="sort-filter-items">
		<#if entries?has_content>
			<#list entries as entry>
				<label>
					<input
						${entry.isSelected()?then("checked","")}
						class="form-check-input"
						name="inlineRadioOptions"
						onchange="handleChangeSort(event);"
						type="radio"
						value="${entry.getField()}"
					/>

					<span class="form-check-label-text">
						${entry.getLanguageLabel()}
					</span>
				</label>
			</#list>
		</#if>
	</div>
</div>

<script>
	function handleChangeSort(event) {
		const urlParams = new URLSearchParams(window.location.search);

		urlParams.set('sort', event.currentTarget.value);
		window.location.search = urlParams;
	}

	function toggleCollapse(dataTarget) {
		const dataTargetElements = document.querySelectorAll(
			'[data-target="#' + dataTarget + '"]'
		);

		dataTargetElements.forEach((element) => {
			element.classList.toggle('collapsed');
			element.setAttribute('aria-expanded', !(element.getAttribute('aria-expanded') === 'true'));
		});

		const dataTargetElement = document.getElementById(dataTarget);

		if (dataTargetElement) {
			dataTargetElement.classList.toggle('show');
		}
	}

	document.addEventListener('DOMContentLoaded', () => {
		const panel = document.querySelector('.portlet-sort');

		if (!panel) return;

		if (window.innerWidth <= 768) {
			const panelBody = panel.querySelector('#sort-filter-items');

			if (panelBody) {
				panelBody.classList.remove('show');
				panelBody.classList.add('collapse');
			}

			const panelHeaderButton = panel.querySelector('.sort-collapse-trigger');

			if (panelHeaderButton) {
				panelHeaderButton.classList.add('collapsed');
				panelHeaderButton.setAttribute('aria-expanded', 'false');
			}
		}
	});
</script>

<style>
	.arrow-sort-filter {
		height: 12px;
		width: 12px;
	}

	.sort-collapse-trigger {
		align-items: center;
		cursor: pointer;
		display: flex;
		justify-content: space-between;
		width: 100%;
	}

	.sort-collapse-trigger .text-truncate-inline-sort-filter {
		flex-grow: 1;
		margin-right: 10px;
	}

	.sort-filter #sort-filter-items.collapse:not(.show) {
		display: none !important;
	}

	.sort-filter-container {
		display: flex;
		flex-direction: column;
		margin-bottom: 0;
	}

	.sort-group-container {
		display: flex;
		flex-direction: row !important;
		padding-bottom: 0 !important;
		width: 100% !important;
	}

	.sort-title {
		color: var(--color-neutral-10, #282934);
		font-size: 18px;
		font-weight: 600;
		margin-left: 8px;
		margin-top: 8px;
	}

	#sort-filter-items {
		align-items: start;
		display: flex;
		flex-direction: column;
		gap: 16px;
		margin-left: 8px;
		margin-top: 12px;
	}

	#sort-filter-items label {
		align-items: center;
		color: var(--color-neutral-10, #282934);
		display: flex;
		font-weight: 400;
		margin-bottom: 0;
	}
</style>