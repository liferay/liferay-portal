/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import React from 'react';

import {getImage} from '../../common/utils/getImage';

const AssetTypeInfoPanelEmptyState = ({selected = 0}: {selected: number}) => {
	const emptyStateProps: {
		className: string;
		description?: string | undefined;
		imgSrc: string;
		imgSrcReducedMotion: string;
		small: boolean;
		title: string | null;
	} = {
		className:
			'justify-content-center text-secondary structure-builder__empty-state',
		description: '',
		imgSrc: '',
		imgSrcReducedMotion: '',
		small: true,
		title: null,
	};

	if (!selected) {
		emptyStateProps.description = Liferay.Language.get(
			'click-on-an-asset-to-see-its-details'
		);
		emptyStateProps.imgSrc = getImage('empty_selection_state.svg');
		emptyStateProps.imgSrcReducedMotion = getImage(
			'empty_selection_state.svg'
		);
	}
	else if (selected > 1) {
		emptyStateProps.className = `${emptyStateProps.className} asset-multi-selection w-100`;
		emptyStateProps.imgSrc = getImage('multiselection_state.svg');
		emptyStateProps.imgSrcReducedMotion = getImage(
			'multiselection_state.svg'
		);
	}

	return (
		<div className="asset-type-default-view autofit-col">
			<ClayEmptyState {...emptyStateProps} />
		</div>
	);
};

export default AssetTypeInfoPanelEmptyState;
