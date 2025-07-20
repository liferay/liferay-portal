/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import React, {ReactNode} from 'react';

import {TEmptyState, availablesEmptyStateType} from './types';

const DEFAULT_TITLE = Liferay.Language.get('no-results-found');
const DEFAULT_IMAGE = '/states/search_state.svg';
const DEFAULT_DESCRIPTION = Liferay.Language.get(
	'review-your-filters-or-search-and-try-again'
);
const THEME_IMAGE_PATH = Liferay.ThemeDisplay.getPathThemeImages();

const fdsStates = {
	DEFAULT: {
		title: DEFAULT_TITLE,
		image: DEFAULT_IMAGE,
		description: DEFAULT_DESCRIPTION,
		button: undefined,
	},
	SEARCH: {
		title: DEFAULT_TITLE,
		image: DEFAULT_IMAGE,
		description: DEFAULT_DESCRIPTION,
		button: Liferay.Language.get('clear-search'),
	},
	FILTERS: {
		title: DEFAULT_TITLE,
		image: DEFAULT_IMAGE,
		description: DEFAULT_DESCRIPTION,
		button: Liferay.Language.get('clear-filters'),
	},
	SEARCH_AND_FILTERS: {
		title: DEFAULT_TITLE,
		image: DEFAULT_IMAGE,
		description: DEFAULT_DESCRIPTION,
		button: Liferay.Language.get('clear-search-and-filters'),
	},
};

export type EmptyStateProps = {
	description?: string;
	image?: string;
	title?: string;
	children?: ReactNode;
	type?: TEmptyState;
	clearButton: React.MouseEventHandler<HTMLButtonElement>;
};

const EmptyState: React.FC<EmptyStateProps> = ({
	children,
	description,
	image,
	title,
	type = availablesEmptyStateType.default,
	clearButton,
}) => {
	const states = fdsStates[type];

	return (
		<ClayEmptyState
			description={description || states.description}
			imgSrc={`${THEME_IMAGE_PATH}${image || states.image}`}
			title={title || states.title}
		>
			{states.button && (
				<Button displayType="secondary" onClick={clearButton}>
					{states.button}
				</Button>
			)}

			{children}
		</ClayEmptyState>
	);
};

export default EmptyState;
