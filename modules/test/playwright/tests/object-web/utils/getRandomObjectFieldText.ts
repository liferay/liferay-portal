/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectField} from '@liferay/object-admin-rest-client-js/v1_0';

import {getRandomInt} from '../../../utils/getRandomInt';

interface Props {
	objectFieldsQuantity: number;
}

export default function getRandomObjectFieldText({
	objectFieldsQuantity,
}: Props): ObjectField[] {
	const objectFields = [];

	for (let i = 1; i <= objectFieldsQuantity; i++) {
		const objectField = {
			DBType: 'String',
			businessType: 'Text',
			externalReferenceCode: 'customText' + getRandomInt(),
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'customText' + getRandomInt()},
			name: 'customText' + getRandomInt(),
			required: false,
			system: false,
			type: 'String',
		};

		objectFields.push(objectField);
	}

	return objectFields;
}
