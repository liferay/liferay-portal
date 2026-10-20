/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React, {Fragment} from 'react';

import Lang from '../utils/lang';

export default function RatingsStackedStars({
	averageScore,
	disabled,
	getSrAverageMessage,
	getTitle,
	numberOfStars,
	onVote,
	randomNamespace,
	score,
	starScores,
	totalEntries,
}) {
	return (
		<div className="ratings-stacked-stars ratings-stars">
			<div
				className="ratings-stars-average"
				title={averageScore.toFixed(1)}
			>
				<span className="inline-item inline-item-before">
					{starScores.map(({label: score}, index, array) => {
						const previousScore =
							array[index - 1] && array[index - 1].label;
						let symbol = 'star-o';

						if (averageScore >= score) {
							symbol = 'star';
						}
						else if (averageScore > previousScore) {
							symbol = 'star-half';
						}

						return (
							<ClayIcon
								aria-label={score}
								className="ratings-stars-average-icon"
								key={score}
								symbol={symbol}
							/>
						);
					})}
				</span>

				<span className="inline-item ratings-stars-average-text">
					{!!totalEntries &&
						` (${totalEntries} ${
							totalEntries === 1
								? Liferay.Language.get('vote[noun]')
								: Liferay.Language.get('votes')
						})`}
				</span>

				<span className="sr-only">{getSrAverageMessage()}</span>
			</div>

			<div
				className={classNames({disabled}, 'ratings-stacked-stars-vote')}
			>
				<fieldset title={getTitle()}>
					<div className="ratings-stacked-stars-vote-stars">
						{starScores.reverse().map(({label, value}, index) => {
							const id = `${randomNamespace}star${label}`;
							const srMessage =
								index === 0
									? Liferay.Language.get(
											'rate-this-x-star-out-of-x'
										)
									: Liferay.Language.get(
											'rate-this-x-stars-out-of-x'
										);
							const full = label <= score;

							return (
								<Fragment key={value}>
									<input
										checked={label === score}
										className="sr-only"
										disabled={disabled}
										id={id}
										name={`${randomNamespace}rating`}
										onChange={() => {
											onVote(index);
										}}
										type="radio"
										value={value}
									/>

									<label
										className={
											full
												? 'ratings-stars-star-full'
												: ''
										}
										htmlFor={id}
									>
										<ClayIcon
											className="ratings-stars-icon-full"
											symbol="star"
										/>

										<ClayIcon
											className="ratings-stars-icon-empty"
											symbol="star-o"
										/>

										<span className="sr-only">
											{Lang.sub(srMessage, [
												label,
												numberOfStars,
											])}
										</span>
									</label>
								</Fragment>
							);
						})}
					</div>
				</fieldset>

				{score !== 0 && (
					<ClayButton
						className="ratings-stacked-stars-delete"
						disabled={disabled}
						displayType="unstyled"
						onClick={onVote}
						title={Liferay.Language.get('delete')}
					>
						<ClayIcon
							aria-label={Liferay.Language.get('delete')}
							className="lexicon-icon-vertical-align"
							symbol="times-circle"
						/>
					</ClayButton>
				)}
			</div>
		</div>
	);
}
