/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import React from 'react';

import CreationMenu from '../management_bar/controls/CreationMenu';
import {IFrontendDataSetProps} from '../utils/types';

interface IEmptyStateProps {
	creationMenu?: IFrontendDataSetProps['creationMenu'];
	emptyStateConfiguration?: IFrontendDataSetProps['emptyState'];
	filters: any[];
	onClearFilters: () => void;
	searchParam: string;
}

const DEFAULT_SEARCH_STATE_IMAGE = '/states/search_state.svg';
const DEFAULT_SEARCH_STATE_REDUCED_MOTION_IMAGE =
	'/states/search_state_reduced_motion.svg';

const getImgSrc = (image: string) =>
	`${Liferay.ThemeDisplay.getPathThemeImages()}${image}`;

const EmptyState = ({
	creationMenu,
	emptyStateConfiguration,
	filters,
	onClearFilters,
	searchParam,
}: IEmptyStateProps) => {
	const hasActiveFilters = filters.some((filter: any) => filter.active);
	const hasSearch = !!searchParam;

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
				imgSrc={getImgSrc(config?.image ?? DEFAULT_SEARCH_STATE_IMAGE)}
				imgSrcReducedMotion={getImgSrc(
					config?.imageReducedMotion ??
						DEFAULT_SEARCH_STATE_REDUCED_MOTION_IMAGE
				)}
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
				imgSrc={getImgSrc(config?.image ?? DEFAULT_SEARCH_STATE_IMAGE)}
				imgSrcReducedMotion={getImgSrc(
					config?.imageReducedMotion ??
						DEFAULT_SEARCH_STATE_REDUCED_MOTION_IMAGE
				)}
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
				imgSrc={getImgSrc(config?.image ?? DEFAULT_SEARCH_STATE_IMAGE)}
				imgSrcReducedMotion={getImgSrc(
					config?.imageReducedMotion ??
						DEFAULT_SEARCH_STATE_REDUCED_MOTION_IMAGE
				)}
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
			imgSrc={getImgSrc(
				emptyStateConfiguration?.image ?? DEFAULT_SEARCH_STATE_IMAGE
			)}
			imgSrcReducedMotion={getImgSrc(
				emptyStateConfiguration?.imageReducedMotion ??
					DEFAULT_SEARCH_STATE_REDUCED_MOTION_IMAGE
			)}
			title={
				emptyStateConfiguration?.title ??
				Liferay.Language.get('no-results-found')
			}
		>
			{creationMenu && <CreationMenu {...creationMenu} inEmptyState />}
		</ClayEmptyState>
	);
};

export default EmptyState;
