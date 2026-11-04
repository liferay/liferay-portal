/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import {useModal} from '@clayui/modal';
import {ClayTooltipProvider} from '@clayui/tooltip';
import PropTypes from 'prop-types';
import React from 'react';

import AddAudienceVariationModal from './AddAudienceVariationModal';
import SortableList from './SortableList';

const AudienceVariationsNav = ({
	addVariationURL,
	assetListEntryValid,
	audiences,
	defaultVariation,
	portletNamespace,
	updateVariationsPriorityURL,
	variations,
}) => {
	const showAddVariationButton = assetListEntryValid && !!audiences.length;

	const {observer, onOpenChange, open} = useModal();

	const handleAddVariation = () => onOpenChange(true);

	return (
		<>
			{open && (
				<AddAudienceVariationModal
					addVariationURL={addVariationURL}
					audiences={audiences}
					observer={observer}
					onClose={() => onOpenChange(false)}
					portletNamespace={portletNamespace}
				/>
			)}

			<div className="align-items-center d-flex justify-content-between mb-3">
				<p className="font-weight-semi-bold h5 mb-0 text-uppercase">
					{Liferay.Language.get('personalized-variations')}
				</p>

				{showAddVariationButton && (
					<ClayTooltipProvider>
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get(
								'create-variation'
							)}
							data-tooltip-align="top"
							displayType="unstyled"
							onClick={handleAddVariation}
							size="sm"
							symbol="plus"
							title={Liferay.Language.get('create-variation')}
						/>
					</ClayTooltipProvider>
				)}
			</div>

			{variations.length ? (
				<>
					<p className="mb-3 small text-secondary">
						{Liferay.Language.get(
							'create-personalized-variations-of-the-collections-for-different-audiences'
						)}
					</p>

					<SortableList
						defaultItem={defaultVariation}
						items={variations}
						namespace={portletNamespace}
						savePriorityURL={updateVariationsPriorityURL}
					/>
				</>
			) : (
				<ClayEmptyState
					description={Liferay.Language.get(
						'no-personalized-variations-were-found-please-create-a-personalized-variation-by-selecting-an-audience'
					)}
					title={Liferay.Language.get(
						'no-personalized-variations-yet'
					)}
				>
					{showAddVariationButton && (
						<ClayButton
							displayType="primary"
							onClick={handleAddVariation}
							size="sm"
						>
							{Liferay.Language.get('add-personalized-variation')}
						</ClayButton>
					)}
				</ClayEmptyState>
			)}
		</>
	);
};

AudienceVariationsNav.propTypes = {
	addVariationURL: PropTypes.string.isRequired,
	assetListEntryValid: PropTypes.bool.isRequired,
	audiences: PropTypes.array.isRequired,
	defaultVariation: PropTypes.object.isRequired,
	updateVariationsPriorityURL: PropTypes.string.isRequired,
	variations: PropTypes.array.isRequired,
};

export default AudienceVariationsNav;
