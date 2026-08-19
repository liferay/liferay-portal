/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function ({namespace}) {
	const assetSelector = document.getElementById(namespace + 'anyAssetType');
	const assetWrapper = document.getElementById(
		namespace + 'assetFilterBuilderWrapper'
	);
	const collectionWrapper = document.getElementById(
		namespace + 'collectionFilterBuilderWrapper'
	);

	if (!assetSelector || !assetWrapper || !collectionWrapper) {
		return;
	}

	const updateVisibility = () => {
		const selectedOption =
			assetSelector.options[assetSelector.selectedIndex];

		const isSingleCMSType =
			!!selectedOption && selectedOption.dataset.cms === 'true';

		const isMultiSelection =
			assetSelector.value === 'false' || assetSelector.value === 'true';

		const showCollection = isSingleCMSType || isMultiSelection;

		assetWrapper.classList.toggle('hide', showCollection);
		collectionWrapper.classList.toggle('hide', !showCollection);

		// A hidden wrapper still submits its fields, so keep the asset filter
		// builder out of the form while the collection filter builder is in
		// use. Never do the same to the collection filter builder: type
		// settings are merged on save, so its input has to keep submitting an
		// empty value to clear the stored filters.

		assetWrapper.disabled = showCollection;
	};

	updateVisibility();

	Liferay.on(namespace + 'sourceChange', updateVisibility);

	return {
		destroy() {
			Liferay.detach(namespace + 'sourceChange', updateVisibility);
		},
	};
}
