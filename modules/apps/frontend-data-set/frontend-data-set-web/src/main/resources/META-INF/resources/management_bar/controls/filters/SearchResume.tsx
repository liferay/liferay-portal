/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import classNames from 'classnames';
import React, {useContext} from 'react';

import FrontendDataSetContext from '../../../FrontendDataSetContext';

function SearchResume() {
	const {onClearSearch, searchParam} = useContext(FrontendDataSetContext);

	return (
		<span
			className={classNames(
				'c-ml-2',
				'component-label',
				'label',
				'label-dismissible',
				'label-lg',
				'label-secondary',
				'search-resume',
				'tbar-label'
			)}
			role="group"
		>
			<ClayLabel.ItemExpand>
				<span className="label-section">
					<span className="mr-1">
						{Liferay.Language.get('search-colon')}
					</span>

					<strong>{searchParam}</strong>
				</span>
			</ClayLabel.ItemExpand>

			<ClayLabel.ItemAfter>
				<button
					aria-label={Liferay.Language.get('clear-search')}
					className="close"
					onClick={onClearSearch}
					title={Liferay.Language.get('clear-search')}
					type="button"
				>
					<ClayIcon symbol="times-small" />
				</button>
			</ClayLabel.ItemAfter>
		</span>
	);
}

export default SearchResume;
