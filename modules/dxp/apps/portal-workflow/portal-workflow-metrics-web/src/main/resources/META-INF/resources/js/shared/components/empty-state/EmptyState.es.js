/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React from 'react';

const EmptyState = ({
	actionButton,
	className = 'pb-5 pt-6 sheet text-center',
	filtered,
	filteredMessage = Liferay.Language.get('no-results-were-found'),
	hideAnimation = false,
	message = Liferay.Language.get('there-is-no-data-at-the-moment'),
	messageClassName,
	title = null,
}) => {
	const symbol = filtered ? 'search-state' : 'empty-state';

	return (
		<div className={className}>
			<ClayEmptyState
				className={classNames({'text-center': hideAnimation})}
				description={filtered ? filteredMessage : message}
				image={
					hideAnimation ? undefined : (
						<ClayIcon
							className={`c-empty-state-icon c-empty-state-icon-${symbol}`}
							illustration
							symbol={symbol}
						/>
					)
				}
				small={messageClassName === 'small'}
				title={title}
			>
				{actionButton}
			</ClayEmptyState>
		</div>
	);
};

export default EmptyState;
