/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import serviceFetch from '../../app/services/serviceFetch';
import {ElementVariation} from './elementVariationsReducer';

interface AddElementVariationParameters {
	addElementVariationURL: string;
	elementVariation: ElementVariation;
	plid: number;
}

interface DeleteElementVariationParameters {
	deleteElementVariationURL: string;
	externalReferenceCode: string;
	plid: number;
}

export default {
	addElementVariation({
		addElementVariationURL,
		elementVariation,
		plid,
	}: AddElementVariationParameters) {
		return serviceFetch<void>(addElementVariationURL, {
			body: {
				elementVariation: JSON.stringify({
					active: elementVariation.active,
					audienceEntryERCs: elementVariation.audienceEntryERCs,
					externalReferenceCode:
						elementVariation.externalReferenceCode,
					hide: String(elementVariation.hide),
					htmlMap: elementVariation.html,
					jsMap: elementVariation.js,
					name: elementVariation.name,
					segmentsExperienceERC:
						elementVariation.segmentsExperienceERC,
					targetElement: elementVariation.targetElement,
				}),
				plid: String(plid),
			},
		});
	},

	deleteElementVariation({
		deleteElementVariationURL,
		externalReferenceCode,
		plid,
	}: DeleteElementVariationParameters) {
		return serviceFetch<void>(deleteElementVariationURL, {
			body: {
				externalReferenceCode,
				plid: String(plid),
			},
		});
	},
};
