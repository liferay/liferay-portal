/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import {PropTypes} from 'prop-types';
import React from 'react';

import MappingFields from './components/MappingFields';
import {TEXT_FIELD_TYPES} from './constants';

export default function SeoMapping({
	description,
	fields,
	portletNamespace,
	selectedSource,
	title,
}) {
	return (
		<MappingFields
			fields={fields}
			inputs={[
				{
					fieldTypes: TEXT_FIELD_TYPES,
					helpMessage: sub(
						Liferay.Language.get(
							'map-a-x-field-it-will-be-used-as-x'
						),
						Liferay.Language.get('text'),
						Liferay.Language.get('html-title')
					),
					label: Liferay.Language.get('html-title'),
					name: `${portletNamespace}TypeSettingsProperties--mapped-title--`,
					value: title,
				},
				{
					component: 'textarea',
					fieldTypes: TEXT_FIELD_TYPES,
					helpMessage: sub(
						Liferay.Language.get(
							'map-a-x-field-it-will-be-used-as-x'
						),
						Liferay.Language.get('text'),
						Liferay.Language.get('description')
					),
					label: Liferay.Language.get('description'),
					name: `${portletNamespace}TypeSettingsProperties--mapped-description--`,
					value: description,
				},
			]}
			selectedSource={selectedSource}
		/>
	);
}

SeoMapping.propTypes = {
	description: PropTypes.string,
	fields: PropTypes.arrayOf(
		PropTypes.shape({
			key: PropTypes.string,
			label: PropTypes.string,
		})
	).isRequired,
	selectedSource: PropTypes.shape({
		classNameLabel: PropTypes.string,
		classTypeLabel: PropTypes.string,
	}).isRequired,
	title: PropTypes.string,
};
