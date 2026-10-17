/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PropTypes from 'prop-types';
import React from 'react';

import {SelectField} from '../../../../../../app/components/fragment_configuration_fields/SelectField';

const ALIGN_OPTIONS = [
	{
		label: Liferay.Language.get('start'),
		value: 'align-items-start',
	},
	{
		label: Liferay.Language.get('center[verb]'),
		value: 'align-items-center',
	},
	{
		label: Liferay.Language.get('end'),
		value: 'align-items-end',
	},
	{
		label: Liferay.Language.get('stretch'),
		value: '',
	},
	{
		label: Liferay.Language.get('baseline'),
		value: 'align-items-baseline',
	},
];

const FLEX_WRAP_OPTIONS = [
	{
		label: Liferay.Language.get('nowrap'),
		value: '',
	},
	{
		label: Liferay.Language.get('wrap'),
		value: 'flex-wrap',
	},
	{
		label: Liferay.Language.get('wrap-reverse'),
		value: 'flex-wrap-reverse',
	},
];

const JUSTIFY_OPTIONS = [
	{
		label: Liferay.Language.get('start'),
		value: '',
	},
	{
		label: Liferay.Language.get('center[verb]'),
		value: 'justify-content-center',
	},
	{
		label: Liferay.Language.get('end'),
		value: 'justify-content-end',
	},
	{
		label: Liferay.Language.get('between'),
		value: 'justify-content-between',
	},
	{
		label: Liferay.Language.get('around'),
		value: 'justify-content-around',
	},
];

export function FlexOptions({itemConfig, onConfigChange}) {
	return (
		<>
			<SelectField
				field={{
					label: Liferay.Language.get('flex-wrap'),
					name: 'flexWrap',
					typeOptions: {
						validValues: FLEX_WRAP_OPTIONS,
					},
				}}
				onValueSelect={onConfigChange}
				value={itemConfig.flexWrap || ''}
			/>

			<div className="d-flex justify-content-between">
				<SelectField
					className="page-editor__sidebar__fieldset__field-small"
					field={{
						label: Liferay.Language.get('align-items'),
						name: 'align',
						typeOptions: {
							validValues: ALIGN_OPTIONS,
						},
					}}
					onValueSelect={onConfigChange}
					value={itemConfig.align || ''}
				/>

				<SelectField
					className="page-editor__sidebar__fieldset__field-small"
					field={{
						label: Liferay.Language.get('justify-content'),
						name: 'justify',
						typeOptions: {
							validValues: JUSTIFY_OPTIONS,
						},
					}}
					onValueSelect={onConfigChange}
					value={itemConfig.justify || ''}
				/>
			</div>
		</>
	);
}

FlexOptions.propTypes = {
	itemConfig: PropTypes.object.isRequired,
	onConfigChange: PropTypes.func.isRequired,
};
