/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {
	MarketplaceRest,
	useMarketplaceConfiguration,
} from '@liferay/marketplace-js-components-web';
import PropTypes from 'prop-types';
import React, {useEffect, useState} from 'react';

import {useSelector} from '../../../app/contexts/StoreContext';
import MarketplaceSearchResults from './MarketplaceSearchResults';
import TabCollection from './TabCollection';

export default function SearchResultsPanel({
	filteredTabs,
	loading = false,
	searchValue,
}) {
	const baseResourceURL = MarketplaceRest.getBaseResourceURL();

	const marketplaceConfiguration =
		useMarketplaceConfiguration(baseResourceURL);

	const permissions = useSelector((state) => state.permissions);

	const [showResults, setShowResults] = useState(false);

	useEffect(() => {
		setShowResults(false);
	}, [searchValue, setShowResults]);

	if (loading) {
		return <ClayLoadingIndicator className="mt-3" size="sm" />;
	}

	return (
		<div className="overflow-auto">
			{filteredTabs.length ? (
				<div>
					{filteredTabs.map((tab, index) => (
						<div key={index}>
							<div className="font-weight-semi-bold page-editor__fragments-widgets__search-results-panel__filter-subtitle px-3 py-2">
								{tab.label}
							</div>

							<ul
								aria-orientation="vertical"
								className="list-unstyled panel-group-sm px-3"
								role="menubar"
							>
								{tab.collections.map((collection, index) => (
									<TabCollection
										collection={collection}
										initialOpen
										isSearchResult
										key={index}
									/>
								))}
							</ul>
						</div>
					))}
				</div>
			) : (
				<ClayEmptyState
					description={Liferay.Language.get(
						'try-again-with-a-different-search'
					)}
					imgSrc={`${themeDisplay.getPathThemeImages()}/states/search_state.svg`}
					small
					title={Liferay.Language.get('no-results-found')}
				/>
			)}

			{Liferay.FeatureFlags['LPD-34938'] &&
				marketplaceConfiguration.authorized &&
				permissions.VIEW_MARKETPLACE && (
					<div className="page-editor__fragments-widgets__search-results-panel__marketplace-results">
						{showResults ? (
							<MarketplaceSearchResults
								baseResourceURL={baseResourceURL}
								marketplaceConfiguration={
									marketplaceConfiguration
								}
								searchValue={searchValue}
							/>
						) : (
							<ClayButton
								aria-label={Liferay.Language.get(
									'see-marketplace-results'
								)}
								className="p-3"
								displayType="link"
								onClick={() => {
									setShowResults(true);
								}}
								size="sm"
							>
								{Liferay.Language.get(
									'see-marketplace-results'
								)}
							</ClayButton>
						)}
					</div>
				)}
		</div>
	);
}

SearchResultsPanel.proptypes = {
	filteredTabs: PropTypes.object.isRequired,
	loading: PropTypes.bool,
	searchValue: PropTypes.string.isRequired,
};
