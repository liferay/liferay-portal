/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import Toolbar from '../../common/components/Toolbar';
import VerticalNavLayout from '../../common/components/VerticalNavLayout';
import SpaceService from '../../common/services/SpaceService';
import {LabelValueObject, Space} from '../../common/types/Space';
import SpaceGeneralSettings from './SpaceGeneralSettings';
import SpaceLanguageSettings from './SpaceLanguageSettings';

interface SpaceSettingsProps {
	backURL: string;
	companyAvailableLanguages: LabelValueObject[];
	externalReferenceCode: string;
	groupId: string;
}

export default function SpaceSettings({
	backURL,
	companyAvailableLanguages,
	externalReferenceCode,
	groupId,
}: SpaceSettingsProps) {
	const [space, setSpace] = useState<Space | null>(null);

	useEffect(() => {
		SpaceService.getSpace(externalReferenceCode).then((space) => {
			setSpace(space);
		});
	}, [externalReferenceCode]);

	if (!space) {
		return null;
	}

	const verticalNavItems = [
		{
			component: (
				<SpaceGeneralSettings
					backURL={backURL}
					groupId={groupId}
					setSpace={setSpace}
					space={space}
				/>
			),
			id: 'general',
			label: Liferay.Language.get('general'),
		},
		{
			component: (
				<SpaceLanguageSettings
					backURL={backURL}
					companyAvailableLanguages={companyAvailableLanguages}
					setSpace={setSpace}
					space={space}
				/>
			),
			id: 'languages',
			label: Liferay.Language.get('languages'),
		},
	];

	return (
		<>
			<Toolbar
				backURL={backURL}
				title={sub(Liferay.Language.get('x-settings'), space.name)}
			/>

			<VerticalNavLayout items={verticalNavItems} />
		</>
	);
}
