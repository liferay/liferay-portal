/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import React, {useContext} from 'react';

import FrontendDataSetContext from '../FrontendDataSetContext';
import CreationMenu from '../management_bar/controls/CreationMenu';
import {IEmptyState, IFrontendDataSetProps} from '../utils/types';

interface IEmptyStateProps {
	creationMenu?: IFrontendDataSetProps['creationMenu'];
	emptyStateConfiguration?: IFrontendDataSetProps['emptyState'];
	hideManagementBarInEmptyState?: boolean;
	onClearFilters: () => void;
}

const DEFAULT_SEARCH_STATE_IMAGE = '/states/search_state.svg';
const DEFAULT_SEARCH_STATE_REDUCED_MOTION_IMAGE =
	'/states/search_state_reduced_motion.svg';

const getImgSrc = (image: string) =>
	`${Liferay.ThemeDisplay.getPathThemeImages()}${image}`;

const getImageProps = (config?: IEmptyState) =>
	config?.symbol
		? {
				image: (
					<ClayIcon
						className={`c-empty-state-icon c-empty-state-icon-${config.symbol}`}
						illustration
						symbol={config.symbol}
					/>
				),
			}
		: {
				imgSrc: getImgSrc(config?.image ?? DEFAULT_SEARCH_STATE_IMAGE),
				imgSrcReducedMotion: getImgSrc(
					config?.imageReducedMotion ??
						DEFAULT_SEARCH_STATE_REDUCED_MOTION_IMAGE
				),
			};

const EmptyState = ({
	creationMenu,
	emptyStateConfiguration,
	hideManagementBarInEmptyState = false,
	onClearFilters,
}: IEmptyStateProps) => {
	const {globalFDSState} = useContext(FrontendDataSetContext);

	const hasActiveFilters = globalFDSState.filters.some(
		(filter: any) => filter.active
	);
	const hasSearch = !!globalFDSState.search.query;

	if (hasActiveFilters && hasSearch) {
		const config = emptyStateConfiguration?.filtered?.searchAndFilters;

		return (
			<ClayEmptyState
				description={
					config?.description ??
					Liferay.Language.get(
						'review-your-filters-or-search-and-try-again'
					)
				}
				{...getImageProps(config)}
				title={
					config?.title ?? Liferay.Language.get('no-results-found')
				}
			>
				<ClayButton displayType="secondary" onClick={onClearFilters}>
					{Liferay.Language.get('clear-search-and-filters')}
				</ClayButton>
			</ClayEmptyState>
		);
	}
	else if (hasActiveFilters) {
		const config = emptyStateConfiguration?.filtered?.filters;

		return (
			<ClayEmptyState
				description={
					config?.description ??
					Liferay.Language.get('review-your-filters-and-try-again')
				}
				{...getImageProps(config)}
				title={
					config?.title ?? Liferay.Language.get('no-results-found')
				}
			>
				<ClayButton displayType="secondary" onClick={onClearFilters}>
					{Liferay.Language.get('clear-filters')}
				</ClayButton>
			</ClayEmptyState>
		);
	}
	else if (hasSearch) {
		const config = emptyStateConfiguration?.filtered?.search;

		return (
			<ClayEmptyState
				description={
					config?.description ??
					Liferay.Language.get('review-your-search-and-try-again')
				}
				{...getImageProps(config)}
				title={
					config?.title ?? Liferay.Language.get('no-results-found')
				}
			>
				<ClayButton displayType="secondary" onClick={onClearFilters}>
					{Liferay.Language.get('clear-search')}
				</ClayButton>
			</ClayEmptyState>
		);
	}

	return (
		<ClayEmptyState
			description={
				emptyStateConfiguration?.description ??
				Liferay.Language.get('sorry,-no-results-were-found')
			}
			{...getImageProps(emptyStateConfiguration)}
			title={
				emptyStateConfiguration?.title ??
				Liferay.Language.get('no-results-found')
			}
		>
			{creationMenu && (
				<CreationMenu
					{...creationMenu}
					hideManagementBarInEmptyState={
						hideManagementBarInEmptyState
					}
					inEmptyState={true}
				/>
			)}
		</ClayEmptyState>
	);
};

export default EmptyState;
