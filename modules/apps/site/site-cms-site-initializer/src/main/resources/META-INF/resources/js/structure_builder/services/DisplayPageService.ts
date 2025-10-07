/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import ApiHelper from '../../common/services/ApiHelper';
import {config} from '../config';
import {Structure} from '../types/Structure';

async function resetDisplayPage({erc}: {erc: Structure['erc']}) {
	const resetStructureDisplayPageURL = addParams(
		{
			objectDefinitionExternalReferenceCode: erc,
		},
		config.resetStructureDisplayPageURL
	);

	return await ApiHelper.post(resetStructureDisplayPageURL);
}

async function resetTranslationDisplayPage({erc}: {erc: Structure['erc']}) {
	const resetStructureDisplayPageURL = addParams(
		{
			objectDefinitionExternalReferenceCode: erc,
		},
		config.resetTranslationDisplayPageURL
	);

	return await ApiHelper.post(resetStructureDisplayPageURL);
}

export default {
	resetDisplayPage,
	resetTranslationDisplayPage,
};
