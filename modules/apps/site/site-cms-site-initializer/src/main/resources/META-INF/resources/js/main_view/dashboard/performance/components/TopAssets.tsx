/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import React, {useContext} from 'react';

import {BaseCard} from '../../common/BaseCard';
import {PerformanceContext} from '../PerformanceContext';
import PerformanceService from '../PerformanceService';
import {DownloadButton} from './DownloadButton';

export function TopAssets() {
	const {range, space} = useContext(PerformanceContext);

	const depotEntryIds = space.value === 'all' ? undefined : [space.value];

	return (
		<BaseCard
			Preferences={
				<DownloadButton
					href={PerformanceService.getTopAssetsExportURL({
						depotEntryIds,
						rangeKey: range.rangeKey,
					})}
				/>
			}
			description={Liferay.Language.get(
				'list-of-assets-with-the-most-visitors-interactions'
			)}
			title={Liferay.Language.get('top-assets')}
			uppercaseTitle={false}
		>
			<ClayEmptyState
				description={Liferay.Language.get(
					'there-are-no-assets-created-in-the-space'
				)}
				imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/cms_empty_state.svg`}
				title={Liferay.Language.get('no-assets-yet')}
			/>
		</BaseCard>
	);
}
