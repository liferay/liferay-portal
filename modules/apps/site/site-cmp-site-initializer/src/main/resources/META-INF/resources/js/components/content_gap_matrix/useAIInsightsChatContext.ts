/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectField,
	getObjectFields,
	getSpaces,
} from '@liferay/ai-hub-cell-js-components-web';
import {useEffect, useState} from 'react';

import {SpaceOption} from './types';

const CMS_BASIC_WEB_CONTENT_EXTERNAL_REFERENCE_CODE = 'L_CMS_BASIC_WEB_CONTENT';

const CMS_BASIC_WEB_CONTENT_NAME = 'CMSBasicWebContent';

export function useAIInsightsChatContext({
	cmpProjectObjectEntryId,
	cmpProjectScopeKey,
}: {
	cmpProjectObjectEntryId?: string;
	cmpProjectScopeKey?: string;
}) {
	const [objectFields, setObjectFields] = useState<ObjectField[]>();
	const [spaces, setSpaces] = useState<SpaceOption[]>();

	useEffect(() => {
		if (!Liferay.FeatureFlags['LPD-62272']) {
			return;
		}

		const makeFetch = async () => {
			const {items: objectFields} = await getObjectFields(
				CMS_BASIC_WEB_CONTENT_EXTERNAL_REFERENCE_CODE
			);
			const spaces = (await getSpaces()).map((space) => ({
				externalReferenceCode: space.externalReferenceCode,
				id: String(space.siteId),
				label: space.name,
			}));

			setObjectFields(objectFields);
			setSpaces(spaces);
		};

		makeFetch().catch(() => {});
	}, []);

	return () => ({
		focusScope: 'full-matrix',
		objectDefinitionName: CMS_BASIC_WEB_CONTENT_NAME,
		objectFields,
		projectId: cmpProjectObjectEntryId,
		projectScopeKey: cmpProjectScopeKey,
		spacesJSONArray: spaces,
	});
}
