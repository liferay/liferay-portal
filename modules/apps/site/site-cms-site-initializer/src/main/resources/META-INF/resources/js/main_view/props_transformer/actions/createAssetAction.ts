/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import {navigate} from 'frontend-js-web';

import {AssetLibrary} from '../../../common/types/AssetLibrary';
import CreationModalContent from '../../modal/CreationModalContent';

export type AssetData = {
	action: 'createAsset';
	assetLibraries: AssetLibrary[];
	redirect: string;
	title: string;
};

export default function createAssetAction(data: AssetData) {
	if (data.assetLibraries.length === 1) {
		const url = new URL(data.redirect);

		url.searchParams.set('name', '');
		url.searchParams.set('groupId', String(data.assetLibraries[0].groupId));

		navigate(url.pathname + url.search);

		return;
	}

	openModal({
		center: true,
		contentComponent: ({closeModal}: {closeModal: () => void}) =>
			CreationModalContent({
				...data,
				closeModal,
			}),
		size: 'sm',
	});
}
