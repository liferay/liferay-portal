/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import classNames from 'classnames';
import React from 'react';

export default function ObjectDefinitionInheritanceDataRenderer({
	itemData,
}: {
	itemData: ObjectDefinition;
}) {
	const {externalReferenceCode, objectDefinitionSettings} = itemData;

	const rootObjectDefinitionExternalReferenceCodes =
		objectDefinitionSettings?.find(
			(setting) =>
				setting.name === 'rootObjectDefinitionExternalReferenceCodes'
		)?.value;

	const isRootNode = !!rootObjectDefinitionExternalReferenceCodes
		?.split(',')
		.includes(externalReferenceCode);

	return (
		<ClayLabel
			className={classNames('label-inverse-secondary', {
				'label-inverse-info':
					rootObjectDefinitionExternalReferenceCodes,
			})}
		>
			{rootObjectDefinitionExternalReferenceCodes
				? isRootNode
					? Liferay.Language.get('root-object')
					: Liferay.Language.get('inherited')
				: Liferay.Language.get('standard')}
		</ClayLabel>
	);
}
