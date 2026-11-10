/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import React, {useState} from 'react';

import {CMP_MARKETPLACE_URL} from '../../common/utils/constants';
import {getImage} from '../../common/utils/getImage';

import './CMPMarketplaceBanner.scss';

export default function CMPMarketplaceBanner({
	sessionClicksKey,
}: {
	sessionClicksKey: string;
}) {
	const [dismissed, setDismissed] = useState(false);

	if (dismissed) {
		return null;
	}

	const title = Liferay.Language.get(
		'run-smarter-campaigns-with-liferay-content-marketing-platform'
	);

	const onClose = () => {
		setDismissed(true);

		Liferay.Util.Session.set(sessionClicksKey, 'true');
	};

	return (
		<section
			aria-label={title}
			className="align-items-center cms-cmp-marketplace-banner d-flex mb-2 mx-4 p-3 rounded-lg"
		>
			<img
				alt=""
				className="flex-shrink-0 mr-3"
				height={30}
				src={getImage('cmp_icon.svg')}
				width={30}
			/>

			<div className="flex-grow-1 mr-3">
				<p className="mb-1 text-4 text-weight-semi-bold">{title}</p>

				<p className="mb-0 text-3 text-secondary">
					{Liferay.Language.get(
						'give-your-entire-marketing-team-a-unified-platform-to-plan,-manage,-and-deliver-scalable,-personalized-campaigns'
					)}
				</p>
			</div>

			<ClayLink
				button
				className="flex-shrink-0 mr-3"
				displayType="primary"
				href={CMP_MARKETPLACE_URL}
				rel="noopener noreferrer"
				small
				target="_blank"
			>
				<ClayIcon className="mr-2" symbol="marketplace" />

				{Liferay.Language.get('explore-marketplace')}
			</ClayLink>

			<ClayButtonWithIcon
				aria-label={Liferay.Language.get('close')}
				className="flex-shrink-0"
				displayType="unstyled"
				onClick={onClose}
				size="sm"
				symbol="times"
			/>
		</section>
	);
}
