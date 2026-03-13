/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import {useId} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React, {useEffect, useState} from 'react';

import {CONTENT_DISPLAY_OPTIONS} from '../../../../../../../app/config/constants/contentDisplayOptions';
import InfoItemService from '../../../../../../../app/services/InfoItemService';

const DEFAULT_LIST_STYLES = [
	{
		label: Liferay.Language.get('grid'),
		value: '',
	},
	{
		label: Liferay.Language.get('flex-row'),
		value: CONTENT_DISPLAY_OPTIONS.flexRow,
	},
	{
		label: Liferay.Language.get('flex-column'),
		value: CONTENT_DISPLAY_OPTIONS.flexColumn,
	},
];

export function StyleDisplaySelector({
	collectionItemType,
	handleConfigurationChanged,
	key,
	listStyle,
}) {
	const [availableListStyles, setAvailableListStyles] =
		useState(DEFAULT_LIST_STYLES);

	const listStyleId = useId();

	useEffect(() => {
		if (collectionItemType) {
			InfoItemService.getAvailableListRenderers({
				className: collectionItemType,
				key,
			})
				.then((response) => {
					if (response && !!response.length) {
						setAvailableListStyles([
							...DEFAULT_LIST_STYLES,
							{
								label: Liferay.Language.get('templates'),
								options: response,
								type: 'group',
							},
						]);
					}
					else {
						setAvailableListStyles(DEFAULT_LIST_STYLES);
					}
				})
				.catch(() => {
					setAvailableListStyles(DEFAULT_LIST_STYLES);
				});
		}
	}, [collectionItemType, key]);

	return (
		<ClayForm.Group className="mt-3" small>
			<label htmlFor={listStyleId}>
				{Liferay.Language.get('style-display')}
			</label>

			<ClaySelectWithOption
				aria-label={Liferay.Language.get('style-display')}
				id={listStyleId}
				onChange={(event) =>
					handleConfigurationChanged({
						listStyle: event.target.value,
					})
				}
				options={availableListStyles}
				value={listStyle}
			/>
		</ClayForm.Group>
	);
}

StyleDisplaySelector.propTypes = {
	collectionItemType: PropTypes.string,
	handleConfigurationChanged: PropTypes.func.isRequired,
	key: PropTypes.string,
	listStyle: PropTypes.string,
};
