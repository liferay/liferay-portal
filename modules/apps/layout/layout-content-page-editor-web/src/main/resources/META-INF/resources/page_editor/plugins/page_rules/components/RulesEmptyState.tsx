/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import React from 'react';

import {useRulesModal} from '../../../app/contexts/RulesModalContext';

export default function RulesEmptyState({isSearching}: {isSearching: boolean}) {
	const {openRulesModal} = useRulesModal();

	return (
		<>
			<div className="align-items-center d-flex flex-column justify-content-between px-3">
				{isSearching ? (
					<ClayEmptyState
						description={Liferay.Language.get(
							'try-again-with-a-different-search'
						)}
						image={
							<ClayIcon
								className="c-empty-state-icon c-empty-state-icon-search-state"
								illustration
								symbol="search-state"
							/>
						}
						small
						title={Liferay.Language.get('no-results-found')}
					/>
				) : (
					<>
						<ClayEmptyState
							className="mb-0"
							description={Liferay.Language.get(
								'fortunately-it-is-very-easy-to-add-new-ones'
							)}
							image={
								<ClayIcon
									className="c-empty-state-icon c-empty-state-icon-empty-state"
									illustration
									symbol="empty-state"
								/>
							}
							small
							title={Liferay.Language.get('no-rules-yet')}
						/>

						<ClayButton
							className="mt-2"
							displayType="secondary"
							onClick={() => openRulesModal()}
							size="sm"
						>
							{Liferay.Language.get('new-rule')}
						</ClayButton>
					</>
				)}
			</div>
		</>
	);
}
