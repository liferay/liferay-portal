/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayCard from '@clayui/card';
import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import classNames from 'classnames';
import React from 'react';

export function Option({
	ariaDescribedBy,
	icon,
	iconClassName,
	imagePreviewURL,
	isActive,
	name,
	onClick,
	subtitle,
}) {
	return (
		<ClayCard
			aria-describedby={ariaDescribedBy}
			aria-label={name}
			className={classNames({
				'page-editor__sidebar__design-options__tab-card--active':
					isActive,
			})}
			displayType="file"
			onClick={() => {
				if (!isActive) {
					onClick();
				}
			}}
			onKeyDown={(event) => {
				if (event.key === 'Enter' && !isActive) {
					onClick();
				}
			}}
			role="button"
			selectable
			tabIndex="0"
		>
			<ClayCard.AspectRatio
				className="card-item-first"
				containerAspectRatio="16/9"
			>
				{imagePreviewURL ? (
					<img
						alt="thumbnail"
						className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-fluid"
						src={imagePreviewURL}
					/>
				) : (
					<div className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-fluid card-type-asset-icon">
						<ClayIcon className={iconClassName} symbol={icon} />
					</div>
				)}

				{isActive && (
					<ClaySticker displayType="primary" position="bottom-left">
						<ClayIcon symbol="check-circle" />
					</ClaySticker>
				)}
			</ClayCard.AspectRatio>

			<ClayCard.Body>
				<ClayCard.Row>
					<div className="autofit-col autofit-col-expand">
						<section className="autofit-section">
							<ClayCard.Description displayType="title">
								{name}
							</ClayCard.Description>

							{typeof subtitle === 'string' ? (
								<ClayCard.Description displayType="subtitle">
									{subtitle}
								</ClayCard.Description>
							) : (
								subtitle
							)}
						</section>
					</div>
				</ClayCard.Row>
			</ClayCard.Body>
		</ClayCard>
	);
}

export function OptionsList({banner, children, options = []}) {
	return (
		<>
			{banner}

			<ul className="list-unstyled mt-4">
				{options.map((option, index) => (
					<li key={index}>{children(option)}</li>
				))}
			</ul>
		</>
	);
}
