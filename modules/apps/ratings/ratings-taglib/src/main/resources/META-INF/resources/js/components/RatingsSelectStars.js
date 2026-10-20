/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import React, {useState} from 'react';

import Lang from '../utils/lang';

const ID_PREFIX = 'rsd_';

export default function RatingsSelectStars({
	averageScore,
	disabled,
	getSrAverageMessage,
	getTitle,
	numberOfStars,
	onVote,
	score,
	starScores,
	totalEntries,
}) {
	const [isDropdownOpen, setIsDropdownOpen] = useState(false);
	const [focusId, setFocusId] = useState();

	const handleOnClick = (index) => {
		setIsDropdownOpen(false);
		onVote(index);
	};

	const handleInitialFocus = () => {
		if (!focusId) {
			setFocusId(`${ID_PREFIX}0`);
		}
	};

	return (
		<ClayLayout.ContentRow
			className="ratings-stars"
			noGutters
			verticalAlign="center"
		>
			<ClayLayout.ContentCol>
				<ClayDropDown
					active={isDropdownOpen}
					menuElementAttrs={{
						className: 'ratings-stars-dropdown',
					}}
					onActiveChange={(isActive) => setIsDropdownOpen(isActive)}
					onFocus={handleInitialFocus}
					role="listbox"
					trigger={
						<ClayButton
							aria-expanded={isDropdownOpen}
							aria-haspopup="listbox"
							aria-label={getTitle()}
							borderless
							className="ratings-stars-dropdown-toggle"
							disabled={disabled}
							displayType="secondary"
							small
							title={getTitle()}
							value={score}
						>
							<span className="inline-item inline-item-before">
								<ClayIcon
									aria-label={Liferay.Language.get('votes')}
									symbol={score ? 'star' : 'star-o'}
								/>
							</span>

							<span className="inline-item ratings-stars-button-text">
								{score || '-'}
							</span>
						</ClayButton>
					}
				>
					<ClayDropDown.ItemList
						aria-activedescendant={focusId}
						role="listbox"
					>
						{starScores.map(({label}, index) => {
							const srMessage =
								index === 0
									? Liferay.Language.get(
											'rate-this-x-star-out-of-x'
										)
									: Liferay.Language.get(
											'rate-this-x-stars-out-of-x'
										);

							return (
								<ClayDropDown.Item
									active={label === score}
									id={`${ID_PREFIX}index`}
									key={index}
									onClick={() => {
										handleOnClick(index);
									}}
									onFocus={() => {
										setFocusId(`${ID_PREFIX}index`);
									}}
									roleItem="option"
								>
									{label}

									<span className="sr-only">
										{Lang.sub(srMessage, [
											index + 1,
											numberOfStars,
										])}
									</span>
								</ClayDropDown.Item>
							);
						})}

						<ClayDropDown.Item
							disabled={score === 0}
							id={`${ID_PREFIX}${Liferay.Language.get('delete')}`}
							onClick={handleOnClick}
							onFocus={() => {
								setFocusId(
									`${ID_PREFIX}${Liferay.Language.get(
										'delete'
									)}`
								);
							}}
							roleItem="option"
						>
							{Liferay.Language.get('delete')}
						</ClayDropDown.Item>
					</ClayDropDown.ItemList>
				</ClayDropDown>
			</ClayLayout.ContentCol>

			<ClayLayout.ContentCol>
				<span className="ratings-stars-average">
					<span className="inline-item inline-item-before">
						<ClayIcon
							aria-label={Liferay.Language.get('average')}
							className="ratings-stars-average-icon"
							symbol="star"
						/>
					</span>

					<span className="inline-item ratings-stars-average-text">
						{averageScore.toFixed(1)}

						{!!totalEntries &&
							` (${totalEntries} ${
								totalEntries === 1
									? Liferay.Language.get('vote[noun]')
									: Liferay.Language.get('votes')
							})`}
					</span>

					<span className="sr-only">{getSrAverageMessage()}</span>
				</span>
			</ClayLayout.ContentCol>
		</ClayLayout.ContentRow>
	);
}
