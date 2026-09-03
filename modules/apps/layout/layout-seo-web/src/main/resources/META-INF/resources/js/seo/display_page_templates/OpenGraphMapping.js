/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import {PropTypes} from 'prop-types';
import React from 'react';

import MappingFields from './components/MappingFields';
import {SUPPORTED_FIELD_TYPES, TEXT_FIELD_TYPES} from './constants';

export default function OpenGraphMapping({
	fields,
	openGraphDescription,
	openGraphImage,
	openGraphImageAlt,
	openGraphTitle,
	portletNamespace,
	selectedSource,
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
						Liferay.Language.get('title')
					),
					label: Liferay.Language.get('title'),
					name: `${portletNamespace}TypeSettingsProperties--mapped-openGraphTitle--`,
					value: openGraphTitle,
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
					name: `${portletNamespace}TypeSettingsProperties--mapped-openGraphDescription--`,
					value: openGraphDescription,
				},
				{
					fieldTypes: [SUPPORTED_FIELD_TYPES.IMAGE],
					helpMessage: sub(
						Liferay.Language.get(
							'map-a-x-field-it-will-be-used-as-x'
						),
						Liferay.Language.get('image'),
						Liferay.Language.get('image')
					),
					label: Liferay.Language.get('image'),
					name: `${portletNamespace}TypeSettingsProperties--mapped-openGraphImage--`,
					selectedFieldKey: openGraphImage,
				},
				{
					component: 'textarea',
					fieldTypes: TEXT_FIELD_TYPES,
					helpMessage: sub(
						Liferay.Language.get(
							'map-a-x-field-it-will-be-used-as-x'
						),
						Liferay.Language.get('text'),
						Liferay.Language.get('open-graph-image-alt-description')
					),
					label: Liferay.Language.get(
						'open-graph-image-alt-description'
					),
					name: `${portletNamespace}TypeSettingsProperties--mapped-openGraphImageAlt--`,
					value: openGraphImageAlt,
				},
			]}
			selectedSource={selectedSource}
		/>
	);
}

OpenGraphMapping.propTypes = {
	fields: PropTypes.arrayOf(
		PropTypes.shape({
			key: PropTypes.string,
			label: PropTypes.string,
		})
	).isRequired,
	openGraphDescription: PropTypes.string,
	openGraphImage: PropTypes.string,
	openGraphImageAlt: PropTypes.string,
	openGraphTitle: PropTypes.string,
	selectedSource: PropTypes.shape({
		classNameLabel: PropTypes.string,
		classTypeLabel: PropTypes.string,
	}).isRequired,
};
