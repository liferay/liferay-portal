/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {fetch} from 'frontend-js-web';
import React, {useState} from 'react';

import {downloadBlob} from '../../../../common/utils/downloadBlob';
import {displayErrorToast} from '../../../../common/utils/toastUtil';

export function DownloadButton({href}: {href: string}) {
	const [loading, setLoading] = useState(false);

	async function handleClick() {
		setLoading(true);

		try {
			const response = await fetch(href);

			if (response.ok) {
				await downloadBlob(response);
			}
			else {
				displayErrorToast();
			}
		}
		catch (error) {
			displayErrorToast();
		}

		setLoading(false);
	}

	return (
		<ClayButtonWithIcon
			aria-label={Liferay.Language.get('download')}
			borderless
			disabled={loading}
			displayType="secondary"
			monospaced
			onClick={handleClick}
			size="sm"
			symbol="download"
		/>
	);
}
