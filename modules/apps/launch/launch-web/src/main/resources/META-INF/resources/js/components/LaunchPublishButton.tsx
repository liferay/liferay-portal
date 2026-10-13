/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {openConfirmModal} from 'frontend-js-components-web';
import React, {useRef} from 'react';

interface Props {
	publishLaunchURL: string;
	published: boolean;
}

export default function LaunchPublishButton({
	publishLaunchURL,
	published,
}: Props) {
	const formRef = useRef<HTMLFormElement>(null);

	function handleClick() {
		openConfirmModal({
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-publish-this-launch'
			),
			onConfirm: (confirmed: boolean) => {
				if (confirmed) {
					formRef.current?.submit();
				}
			},
		});
	}

	return (
		<form action={publishLaunchURL} method="post" ref={formRef}>
			<ClayButton
				disabled={published}
				displayType="primary"
				onClick={handleClick}
				small
				type="button"
			>
				<ClayIcon
					className="inline-item inline-item-before"
					symbol="rocket"
				/>

				{published
					? Liferay.Language.get('published')
					: Liferay.Language.get('publish')}
			</ClayButton>
		</form>
	);
}
