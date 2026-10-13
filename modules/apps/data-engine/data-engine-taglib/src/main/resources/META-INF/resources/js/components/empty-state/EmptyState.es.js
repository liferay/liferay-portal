/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import React from 'react';

import {sub} from '../../utils/lang.es';

const EmptyState = ({emptyState, keywords = '', small = false}) => {
	const defaultEmpty = {
		description: null,
		title: Liferay.Language.get('there-are-no-entries'),
	};

	const defaultSearch = {
		description: sub(Liferay.Language.get('there-are-no-results-for-x'), [
			keywords,
		]),
		title: Liferay.Language.get('no-results-were-found'),
	};

	emptyState = {
		...defaultEmpty,
		...emptyState,
	};

	const search = {
		...defaultSearch,
		...emptyState.search,
	};

	const isSearch = keywords !== '';
	const symbol = isSearch ? 'search-state' : 'empty-state';
	const {button, description, title} = isSearch ? search : emptyState;

	return (
		<ClayEmptyState
			description={description}
			image={
				<ClayIcon
					className={`c-empty-state-icon c-empty-state-icon-${symbol}`}
					illustration
					symbol={symbol}
				/>
			}
			small={small}
			title={title}
		>
			{button && button()}
		</ClayEmptyState>
	);
};

export function withEmpty(Component) {
	const Wrapper = ({emptyState, isEmpty, keywords, ...restProps}) => {
		if (isEmpty) {
			return <EmptyState emptyState={emptyState} keywords={keywords} />;
		}

		return <Component {...restProps} />;
	};

	return Wrapper;
}

export default EmptyState;
