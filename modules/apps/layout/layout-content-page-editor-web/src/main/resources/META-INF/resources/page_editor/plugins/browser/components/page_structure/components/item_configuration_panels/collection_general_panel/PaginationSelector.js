/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import PropTypes from 'prop-types';
import React from 'react';

const PAGINATION_TYPE_OPTIONS = [
	{label: Liferay.Language.get('none'), value: 'none'},
	{label: Liferay.Language.get('numeric[pagination]'), value: 'numeric'},
	{label: Liferay.Language.get('simple'), value: 'simple'},
];

export function PaginationSelector({
	collectionPaginationTypeId,
	handleConfigurationChanged,
	value,
}) {
	return (
		<ClayForm.Group small>
			<label htmlFor={collectionPaginationTypeId}>
				{Liferay.Language.get('pagination')}
			</label>

			<ClaySelectWithOption
				aria-label={Liferay.Language.get('pagination')}
				id={collectionPaginationTypeId}
				onChange={(event) =>
					handleConfigurationChanged({
						paginationType: event.target.value,
					})
				}
				options={PAGINATION_TYPE_OPTIONS}
				value={value}
			/>
		</ClayForm.Group>
	);
}

PaginationSelector.propTypes = {
	collectionPaginationTypeId: PropTypes.string.isRequired,
	handleConfigurationChanged: PropTypes.func.isRequired,
	value: PropTypes.string.isRequired,
};
