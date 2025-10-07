/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import React from 'react';

type DLVideoExternalShortcutPreviewProps = {
	labelTooltip?: string;
	onChange: (value: string) => void;
	url?: string;
};

const DLVideoExternalShortcutPreview: React.FC<
	DLVideoExternalShortcutPreviewProps
> = ({labelTooltip, onChange, url = ''}) => {
	const inputName = 'dlVideoExternalShortcutURLInput';

	return (
		<>
			<label htmlFor={inputName}>
				{Liferay.Language.get('video-url')}

				{labelTooltip && (
					<ClayIcon
						className="lfr-portal-tooltip ml-1 text-secondary"
						data-title={labelTooltip}
						role="tooltip"
						symbol="question-circle-full"
						tabIndex={0}
					/>
				)}
			</label>
			<ClayInput
				id={inputName}
				onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
					onChange(event.target.value.trim())
				}
				placeholder="http://"
				type="text"
				value={url}
			/>
			<p className="form-text">
				{Liferay.Language.get('video-url-help')}
			</p>
		</>
	);
};

export default DLVideoExternalShortcutPreview;
