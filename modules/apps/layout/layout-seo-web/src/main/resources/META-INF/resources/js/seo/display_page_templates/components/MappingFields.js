/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {PropTypes} from 'prop-types';
import React from 'react';

import {SUPPORTED_FIELD_TYPES} from '../constants';
import MappingInput from './MappingInput';
import MappingSelector from './MappingSelector';

function MappingFields({fields, inputs, selectedSource}) {
	return inputs.map((props) => {
		const filteredFields = fields.filter(({type}) =>
			props.fieldTypes.includes(type)
		);

		return props.fieldTypes.includes(SUPPORTED_FIELD_TYPES.TEXT) ? (
			<MappingInput
				fieldTypes={props.fieldTypes}
				fields={filteredFields}
				key={props.name}
				selectedSource={selectedSource}
				{...props}
			/>
		) : (
			<MappingSelector
				fieldTypes={props.fieldTypes}
				fields={filteredFields}
				key={props.name}
				selectedSource={selectedSource}
				{...props}
			/>
		);
	});
}

MappingFields.propTypes = {
	fields: PropTypes.arrayOf(
		PropTypes.shape({
			key: PropTypes.string,
			label: PropTypes.string,
		})
	).isRequired,
	inputs: PropTypes.arrayOf(
		PropTypes.shape({
			fieldTypes: PropTypes.array,
			label: PropTypes.string,
			name: PropTypes.string,
			selectedFieldKey: PropTypes.string,
			value: PropTypes.string,
		})
	).isRequired,
};

export default MappingFields;
