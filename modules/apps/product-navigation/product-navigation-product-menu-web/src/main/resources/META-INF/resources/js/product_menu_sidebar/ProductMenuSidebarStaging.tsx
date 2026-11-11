/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import classNames from 'classnames';
import React from 'react';

export interface ProductMenuSidebarStagingProps {
	current: 'live' | 'staging';
	liveURL: string;
	remote: boolean;
	stagingURL: string;
}

function ProductMenuSidebarStagingLink({
	active,
	href,
	label,
	qaId,
	title,
}: {
	active: boolean;
	href: string;
	label: string;
	qaId: string;
	title?: string;
}) {
	const className = classNames('btn btn-secondary btn-sm flex-grow-1', {
		active,
		disabled: !active && !href,
	});

	if (active || !href) {
		return (
			<span
				aria-current={active ? 'page' : undefined}
				aria-disabled={!active || undefined}
				className={className}
				data-qa-id={qaId}
				title={title}
			>
				{label}
			</span>
		);
	}

	return (
		<a className={className} data-qa-id={qaId} href={href}>
			{label}
		</a>
	);
}

function ProductMenuSidebarStaging({
	current,
	liveURL,
	remote,
	stagingURL,
}: ProductMenuSidebarStagingProps) {
	return (
		<ClayButton.Group
			aria-label={Liferay.Language.get('environment')}
			className="c-mb-3 c-mx-1 c-px-2 d-flex product-menu-sidebar-staging"
			role="group"
		>
			<ProductMenuSidebarStagingLink
				active={current === 'staging'}
				href={stagingURL}
				label={Liferay.Language.get('staging')}
				qaId="staging"
			/>

			<ProductMenuSidebarStagingLink
				active={current === 'live'}
				href={liveURL}
				label={
					remote
						? Liferay.Language.get('remote-live')
						: Liferay.Language.get('live')
				}
				qaId="live"
				title={
					remote && !liveURL
						? Liferay.Language.get(
								'the-connection-to-the-remote-live-site-cannot-be-established-due-to-a-network-problem'
							)
						: undefined
				}
			/>
		</ClayButton.Group>
	);
}

export default ProductMenuSidebarStaging;
