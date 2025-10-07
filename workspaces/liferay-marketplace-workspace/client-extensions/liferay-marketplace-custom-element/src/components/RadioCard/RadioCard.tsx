/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayToggle} from '@clayui/form';
import classNames from 'classnames';

import radioChecked from '../../assets/icons/radio_button_checked_2_icon.svg';
import radioUnchecked from '../../assets/icons/radio_button_unchecked_icon.svg';

import './RadioCard.scss';

import ClayIcon from '@clayui/icon';
import {ReactElement} from 'react';

import {Tooltip} from '../Tooltip/Tooltip';

interface RadioCardProps {
	className?: string;
	content?: ReactElement | string;
	description?: string;
	disabled?: boolean;
	icon?: string;
	image?: ReactElement;
	onChange: (value?: boolean) => void;
	position?: string;
	selected: boolean;
	small?: boolean;
	title?: string;
	toggle?: boolean;
	tooltip?: string;
}

export function RadioCard({
	className = '',
	content,
	description,
	disabled = false,
	icon,
	image,
	onChange,
	position = 'left',
	selected,
	small,
	title,
	toggle = false,
	tooltip,
}: RadioCardProps) {
	return (
		<label
			className={classNames(
				'radio-card radio-card-container',
				className,
				{
					'radio-card-container-disabled': disabled,
					'radio-card-container-selected': selected,
					'radio-card-container-small': small,
				}
			)}
			htmlFor={title}
		>
			<div className="radio-card-main-info">
				<div className="radio-card-title">
					{position === 'right' && icon && (
						<ClayIcon
							aria-label="Icon"
							className="radio-card-title-icon-rounded"
							symbol={icon}
						/>
					)}

					{position === 'left' &&
						(toggle ? (
							<ClayToggle
								onToggle={(toggleValue) =>
									onChange(toggleValue)
								}
								toggled={selected}
							/>
						) : (
							<button
								className={classNames('radio-card-button', {
									'radio-card-button-disabled': disabled,
								})}
								id={title}
								onClick={() => !disabled && onChange()}
							>
								<img
									alt={
										selected
											? 'Radio Checked'
											: 'Radio unchecked'
									}
									className="mb-0 radio-card-button-icon"
									src={
										selected ? radioChecked : radioUnchecked
									}
								/>
							</button>
						))}

					{small ? (
						<div className="radio-card-main-info-small">
							{image && (
								<div className="radio-card-main-info-small-background">
									{image}
								</div>
							)}

							{title && (
								<span className="radio-card-main-info-small-text-small">
									{title}
								</span>
							)}

							{content && <span className="">{content}</span>}
						</div>
					) : (
						title && (
							<span
								className={classNames('radio-card-title-text', {
									'radio-card-title-text-selected': selected,
								})}
							>
								{title}
							</span>
						)
					)}

					{position === 'left' && icon && (
						<ClayIcon
							aria-label="Icon"
							className={classNames('radio-card-title-icon', {
								'radio-card-title-icon-selected': selected,
							})}
							symbol={icon}
						/>
					)}
				</div>

				{tooltip && (
					<div className="radio-card-title-tooltip">
						<Tooltip tooltip={tooltip} />
					</div>
				)}

				{position === 'right' &&
					(toggle ? (
						<ClayToggle
							id={title}
							onToggle={(toggleValue) => onChange(toggleValue)}
							toggled={selected}
						/>
					) : (
						<button
							className={classNames('radio-card-button', {
								'radio-card-button-disabled': disabled,
							})}
							onClick={() => !disabled && onChange()}
						>
							<img
								alt={
									selected
										? 'Radio Checked'
										: 'Radio unchecked'
								}
								className="radio-card-button-icon"
								src={selected ? 'live' : 'radio-button'}
							/>
						</button>
					))}
			</div>

			<span className="radio-card-description">{description}</span>
		</label>
	);
}
