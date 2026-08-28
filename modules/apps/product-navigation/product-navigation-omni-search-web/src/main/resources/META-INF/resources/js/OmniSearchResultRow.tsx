/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import classNames from 'classnames';
import React from 'react';

export default function OmniSearchResultRow({
	active,
	id,
	item,
	onClick,
}: {
	active: boolean;
	id: string;
	item: {
		description?: string;
		icon: string;
		title: string;
	};
	onClick: () => void;
}) {
	const {description, icon, title} = item;

	return (
		<li className="omni-search-result-item">
			<button
				aria-selected={active}
				className={classNames('btn btn-unstyled omni-search-result', {
					active,
					focus: active,
				})}
				id={id}
				onClick={onClick}
				role="option"
				tabIndex={-1}
				type="button"
			>
				<ClaySticker
					className="omni-search-result-sticker"
					displayType="secondary"
				>
					<ClayIcon symbol={icon} />
				</ClaySticker>

				<span className="omni-search-result-text">
					<span className="omni-search-result-title text-dark">
						{title}
					</span>

					{description && (
						<span className="omni-search-result-source text-secondary">
							{description}
						</span>
					)}
				</span>
			</button>
		</li>
	);
}
