/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SingleSelect} from '@liferay/object-js-components-web';
import React from 'react';

interface ObjectRelationshipDeletionTypeSelect {
	autoSave?: boolean;
	objectRelationshipDeletionTypes: LabelValueObject[];
	onSubmit: (values?: Partial<ObjectRelationship>) => void;
	readOnly?: boolean;
	setValues: (values: Partial<ObjectRelationship>) => void;
	values: Partial<ObjectRelationship>;
}

export function ObjectRelationshipDeletionTypeSelect({
	autoSave,
	objectRelationshipDeletionTypes,
	onSubmit,
	readOnly,
	setValues,
	values,
}: ObjectRelationshipDeletionTypeSelect) {
	return (
		<SingleSelect
			disabled={readOnly || values.edge}
			id="lfr-objects__object-relationship-deletion-type"
			items={objectRelationshipDeletionTypes}
			label={Liferay.Language.get('deletion-type')}
			onSelectionChange={(value) => {
				setValues({deletionType: value as string});

				if (autoSave) {
					onSubmit({
						...values,
						deletionType: value as string,
					});
				}
			}}
			required
			selectedKey={values.deletionType}
		/>
	);
}
