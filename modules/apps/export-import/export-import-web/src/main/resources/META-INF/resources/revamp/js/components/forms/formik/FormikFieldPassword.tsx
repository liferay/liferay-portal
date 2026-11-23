/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {SyntheticEvent} from 'react';

import FieldText, {FieldTextProps} from '../FieldText';
import {FormikWrapper} from './FormikWrapper';

function preventDefault(event: SyntheticEvent) {
	event.preventDefault();
}

export function FormikFieldPassword(props: Omit<FieldTextProps, 'type'>) {
	return (
		<FormikWrapper name={props.name} required={props.required}>
			{({errorMessage, field}) => (
				<FieldText
					{...props}
					{...field}
					errorMessage={errorMessage}
					onCopy={preventDefault}
					onCut={preventDefault}
					onDragStart={preventDefault}
					type="password"
				/>
			)}
		</FormikWrapper>
	);
}
