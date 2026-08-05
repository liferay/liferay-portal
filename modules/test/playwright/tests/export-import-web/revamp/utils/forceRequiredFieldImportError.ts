/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectFieldAPI} from '@liferay/object-admin-rest-client-js';

import {DataApiHelpers} from '../../../../helpers/ApiHelpers';

export async function forceRequiredFieldImportError(
	apiHelpers: DataApiHelpers,
	objectDefinitionId: number
) {
	const objectFieldAPIClient =
		await apiHelpers.buildRestClient(ObjectFieldAPI);

	await objectFieldAPIClient.postObjectDefinitionObjectField(
		objectDefinitionId,
		{
			DBType: 'String',
			businessType: 'Text',
			label: {en_US: 'mandatoryField'},
			name: 'mandatoryField',
			required: true,
		}
	);
}
