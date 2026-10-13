/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import PropTypes from 'prop-types';
import React from 'react';

export default function SuggestionsPanel({items}) {
	return items?.length ? null : (
		<ClayEmptyState
			description=""
			image={
				<ClayIcon
					className="c-empty-state-icon c-empty-state-icon-empty-state"
					illustration
					symbol="empty-state"
				/>
			}
			small
			title={Liferay.Language.get('there-are-no-suggestions')}
		/>
	);
}

SuggestionsPanel.propTypes = {
	items: PropTypes.array,
};
