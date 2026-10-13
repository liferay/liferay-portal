/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {openSelectionModal} from 'frontend-js-components-web';
import React from 'react';

interface Props {
	itemSelectedEventName: string;
	itemSelectorURL: string;
	launchSetId: number;
}

export default function LaunchPreviewButton({
	itemSelectedEventName,
	itemSelectorURL,
	launchSetId,
}: Props) {
	function handleClick() {
		openSelectionModal({
			onSelect: (selectedItem: {[key: string]: unknown}) => {
				const pageURL = String(
					selectedItem?.value ?? selectedItem?.url ?? ''
				);

				if (!pageURL) {
					return;
				}

				const url = new URL(pageURL, window.location.origin);

				url.searchParams.set('p_l_mode', 'preview');
				url.searchParams.set('previewLaunchSetId', String(launchSetId));

				window.open(url.toString(), '_blank');
			},
			selectEventName: itemSelectedEventName,
			title: Liferay.Language.get('select-page-to-preview'),
			url: itemSelectorURL,
		});
	}

	return (
		<ClayButton displayType="secondary" onClick={handleClick} small>
			<ClayIcon
				className="inline-item inline-item-before"
				symbol="view"
			/>

			{Liferay.Language.get('preview-this-launch')}
		</ClayButton>
	);
}
