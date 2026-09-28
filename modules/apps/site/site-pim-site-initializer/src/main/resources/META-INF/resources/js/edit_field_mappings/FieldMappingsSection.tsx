/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import ClayPanel from '@clayui/panel';
import React from 'react';

import FieldMappingRow from './FieldMappingRow';
import newFieldMapping from './newFieldMapping';
import {IFieldMapping, IObjectDefinition} from './types';

interface IProps {
	fieldMappings: IFieldMapping[];
	objectDefinitions: IObjectDefinition[];
	setFieldMappings: React.Dispatch<React.SetStateAction<IFieldMapping[]>>;
	spritemap: string;
}

export default function FieldMappingsSection({
	fieldMappings,
	objectDefinitions,
	setFieldMappings,
	spritemap,
}: IProps) {
	const handleAdd = () =>
		setFieldMappings((previousFieldMappings) => [
			...previousFieldMappings,
			newFieldMapping(),
		]);

	const handleChange = (index: number, fieldMapping: IFieldMapping) =>
		setFieldMappings((previousFieldMappings) =>
			previousFieldMappings.map((previousFieldMapping, previousIndex) =>
				previousIndex === index ? fieldMapping : previousFieldMapping
			)
		);

	const handleDelete = (index: number) =>
		setFieldMappings((previousFieldMappings) =>
			previousFieldMappings.filter(
				(previousFieldMapping, previousIndex) => previousIndex !== index
			)
		);

	return (
		<div className="container-fluid container-fluid-max-md p-0 p-md-4">
			<ClayPanel
				aria-label={Liferay.Language.get('destination')}
				className="mb-4"
				collapsable={false}
				displayType="secondary"
				role="group"
			>
				<ClayForm.Group className="c-gap-4 d-flex flex-column p-4">
					<h2 className="mb-0 py-2 text-6 text-dark">
						{Liferay.Language.get('destination')}
					</h2>

					<div className="text-secondary">
						{Liferay.Language.get(
							'this-channel-field-takes-its-value-from-the-selected-sources'
						)}
					</div>

					{fieldMappings.map((fieldMapping, index) => (
						<FieldMappingRow
							deletable={fieldMappings.length > 1}
							fieldMapping={fieldMapping}
							index={index}
							key={fieldMapping.key}
							objectDefinitions={objectDefinitions}
							onAdd={handleAdd}
							onChange={handleChange}
							onDelete={handleDelete}
							spritemap={spritemap}
						/>
					))}
				</ClayForm.Group>
			</ClayPanel>
		</div>
	);
}
