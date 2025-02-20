/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {Text} from '@clayui/core';
import {
	Card,
	fieldsUtils,
	objectDefinitionUtils,
} from '@liferay/object-js-components-web';
import React, {useState} from 'react';

import {DefinitionOfTerms} from './DefinitionOfTerms';
import {GeneralTerms} from './GeneralTerms';

interface DefinitionOfTermsContainerProps {
	baseResourceURL: string;
	objectDefinitions: ObjectDefinition[];
}

function hasLocalizedField(
	objectDefinitions: ObjectDefinition[],
	entityId: number
): boolean {
	if (!objectDefinitions || !entityId) {
		return false;
	}

	const selectedObjectDefinition =
		objectDefinitionUtils.findObjectDefinitionById(
			objectDefinitions,
			entityId
		);

	if (!selectedObjectDefinition) {
		return false;
	}

	return fieldsUtils.hasLocalizedField(selectedObjectDefinition!);
}

export default function DefinitionOfTermsContainer({
	baseResourceURL,
	objectDefinitions,
}: DefinitionOfTermsContainerProps) {
	const [selectedEntityId, setSelectedEntityId] = useState<number>(0);

	return (
		<Card title={Liferay.Language.get('definition-of-terms')}>
			{hasLocalizedField(objectDefinitions, selectedEntityId) && (
				<ClayAlert
					displayType="info"
					title={`${Liferay.Language.get('info')}:`}
				>
					{Liferay.Language.get(
						'create-validations-using-expressions'
					)}
				</ClayAlert>
			)}

			<Text as="span" color="secondary">
				{Liferay.Language.get(
					'use-terms-to-populate-fields-dynamically-with-the-exception-of-the-freemarker-template-editor'
				)}
			</Text>

			<GeneralTerms baseResourceURL={baseResourceURL} />

			<DefinitionOfTerms
				baseResourceURL={baseResourceURL}
				objectDefinitions={objectDefinitions}
				selectedEntityId={selectedEntityId}
				setSelectedEntityId={setSelectedEntityId}
			/>
		</Card>
	);
}
