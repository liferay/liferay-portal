/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

function addOpenSelectionModalEvent({namespace, url}) {
	Liferay.on(namespace + 'addCommercePriceEntry', () => {
		openSelectionModal({
			namespace,
			url,
		});
	});

	Liferay.on('destroyPortlet', () => {
		Liferay.detach(namespace + 'addCommercePriceEntry');
	});
}

function openSelectionModal({namespace, url}) {
	return Liferay.Util.openModal({
		buttons: [
			{
				displayType: 'secondary',
				label: Liferay.Language.get('cancel'),
				type: 'cancel',
			},
			{
				formId: `${namespace}addPriceEntryForm`,
				label: Liferay.Language.get('add'),
				type: 'submit',
			},
		],
		containerProps: {
			className: 'commerce-modal add-price-entry-modal modal-height-lg',
		},
		id: `${namespace}addPriceEntryDialog`,
		iframeBodyCssClass: '',
		size: 'lg',
		title: Liferay.Language.get('add-price'),
		url,
	});
}

export default function (context) {
	addOpenSelectionModalEvent(context);
}
