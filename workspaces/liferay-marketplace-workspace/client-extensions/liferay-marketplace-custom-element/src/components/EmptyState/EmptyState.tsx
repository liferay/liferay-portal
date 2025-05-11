/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import React, {ReactNode} from 'react';

import i18n from '../../i18n';
import {Liferay} from '../../liferay/liferay';
export const States = {
	BLANK: '',

	/**
	 * When the filters or search results return zero results.
	 */

	EMPTY_SEARCH: `${Liferay.ThemeDisplay.getPathThemeImages()}/states/search_state.gif`,

	/**
	 * When there are no elements in the data set at a certain level
	 */

	EMPTY_STATE: `${Liferay.ThemeDisplay.getPathThemeImages()}/states/empty_state.gif`,

	/**
	 * When there no permission to access the module
	 */

	NO_ACCESS: `${Liferay.ThemeDisplay.getPathThemeImages()}/app_builder/illustration_locker.svg`,

	NOT_FOUND:
		'https://www.liferay.com/documents/10182/501717/404-Illustration-v2.svg',

	/**
	 * The user has emptied the dataset for a good cause.
	 * For example, all the notifications have been addressed, resulting in a clean state.
	 */
	SUCCESS: `${Liferay.ThemeDisplay.getPathThemeImages()}/states/success_state.gif`,
};

export type EmptyStateProps = {
	children?: ReactNode;
	className?: string;
	description?: string;
	imgSrc?: string;
	title?: string;
	type?: keyof typeof States;
};

const EmptyState: React.FC<EmptyStateProps> = ({
	children,
	className,
	description,
	imgSrc,
	title,
	type,
}) => (
	<ClayEmptyState
		className={className}
		description={
			description ?? i18n.translate('sorry-there-are-no-results-found')
		}
		imgSrc={imgSrc ?? (type ? States[type] : States.EMPTY_STATE)}
		title={title || i18n.translate('no-results-found')}
	>
		{children}
	</ClayEmptyState>
);

export default EmptyState;
