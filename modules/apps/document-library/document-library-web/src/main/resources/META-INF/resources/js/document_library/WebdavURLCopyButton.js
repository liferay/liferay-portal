/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClipboardJS from 'clipboard';
import {openToast} from 'frontend-js-components-web';
import React, {useEffect} from 'react';

const WebdavURLCopyButton = ({id, url}) => {
	useEffect(() => {
		const clipboard = new ClipboardJS('.dm-copy-clipboard', {
			text: () => url,
		});

		clipboard.on('success', () => {
			openToast({
				message: Liferay.Language.get('copied-link-to-the-clipboard'),
			});
		});

		clipboard.on('error', () => {
			openToast({
				message: Liferay.Language.get('an-error-occurred'),
				title: Liferay.Language.get('error'),
				type: 'warning',
			});
		});

		return () => clipboard.destroy();
	}, [url]);

	return (
		<div className="webdav-url-copy-button">
			<ClayForm.Group small>
				<ClayInput.Group>
					<ClayInput.GroupItem prepend>
						<ClayInput id={id} readOnly type="text" value={url} />
					</ClayInput.GroupItem>

					<ClayInput.GroupItem append shrink>
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get('copy-link')}
							className="dm-copy-clipboard"
							displayType="secondary"
							symbol="paste"
							title={Liferay.Language.get('copy-link')}
						/>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</ClayForm.Group>
		</div>
	);
};

export default WebdavURLCopyButton;
