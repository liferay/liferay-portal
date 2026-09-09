/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayModal from '@clayui/modal';
import React from 'react';

import {t} from '../i18n';

interface Props {
	onZoom: (direction: -1 | 1) => void;
	onZoomFit: () => void;
	zoom: number;
}

export function BottomBar({onZoom, onZoomFit, zoom}: Props) {
	return (
		<ClayModal.Footer
			className="editor-bottom-bar"
			middle={
				<div className="editor-bar-group">
					<ClayButtonWithIcon
						aria-label={t('zoom-out')}
						borderless
						className="editor-bar-button"
						displayType="secondary"
						onClick={() => onZoom(-1)}
						symbol="minus-circle"
						title={t('zoom-out')}
					/>

					<span className="editor-zoom-level">
						{t('zoom-percent', Math.round(zoom * 100))}
					</span>

					<ClayButtonWithIcon
						aria-label={t('zoom-in')}
						borderless
						className="editor-bar-button"
						displayType="secondary"
						onClick={() => onZoom(1)}
						symbol="plus-circle-full"
						title={t('zoom-in')}
					/>

					<ClayButtonWithIcon
						aria-label={t('zoom-fit')}
						borderless
						className="editor-bar-button"
						displayType="secondary"
						onClick={onZoomFit}
						symbol="autosize"
						title={t('zoom-fit')}
					/>
				</div>
			}
		/>
	);
}
