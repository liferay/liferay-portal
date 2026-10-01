/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React from 'react';

export default function OmniSearchResultRow({
	active,
	id,
	item,
	onClick,
	onDelete,
	onDeleteKeyDown,
}: {
	active: boolean;
	id: string;
	item: {
		description?: string;
		icon: string;
		title: string;
	};
	onClick: () => void;
	onDelete?: () => void;
	onDeleteKeyDown?: (event: React.KeyboardEvent<HTMLButtonElement>) => void;
}) {
	const {description, icon, title} = item;

	return (
		<li className="omni-search-result-item" role="none">
			<button
				aria-selected={active}
				className={classNames('btn btn-unstyled omni-search-result', {
					active,
					focus: active,
				})}
				id={id}
				onClick={onClick}
				role="option"
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

			{onDelete && (
				<ClayButtonWithIcon
					aria-hidden="true"
					className="omni-search-result-delete"
					displayType="unstyled"
					onClick={(event) => {
						event.stopPropagation();
						onDelete();
					}}
					onKeyDown={onDeleteKeyDown}
					size="sm"
					symbol="times-small"
					tabIndex={-1}
					title={sub(Liferay.Language.get('remove-x'), title)}
				/>
			)}
		</li>
	);
}
