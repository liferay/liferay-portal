/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import React, {useContext} from 'react';

import {getImage} from '../../common/utils/getImage';
import {AssetTypeInfoPanelContext} from './context';

const AssetTypeInfoPanelDefaultView = () => {
	const {objectEntries = []} = useContext(AssetTypeInfoPanelContext);

	const props: {
		className: string;
		description?: string | undefined;
		imgSrc: string;
		imgSrcReducedMotion: string;
		small: boolean;
		title: string | null;
	} = {
		className: 'justify-content-center structure-builder__empty-state',
		description: '',
		imgSrc: '',
		imgSrcReducedMotion: '',
		small: true,
		title: null,
	};

	if (!objectEntries.length) {
		props.description = Liferay.Language.get(
			'click-on-an-asset-to-see-its-details'
		);
		props.imgSrc = getImage('empty_selection_state.svg');
		props.imgSrcReducedMotion = getImage('empty_selection_state.svg');
	}
	else if (objectEntries.length > 1) {
		props.className = `${props.className} asset-multi-selection`;
		props.imgSrc = getImage('multiselection_state.svg');
		props.imgSrcReducedMotion = getImage('multiselection_state.svg');
	}

	return (
		<div className="asset-type-default-view autofit-col">
			<ClayEmptyState {...props} />
		</div>
	);
};

export default AssetTypeInfoPanelDefaultView;
