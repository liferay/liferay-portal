/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import React, {useCallback, useReducer, useState} from 'react';

import AnimatedCounter from './AnimatedCounter';

const PRESSED_DOWN = 'DOWN';
const PRESSED_UP = 'UP';
const SCORE_DOWN = 0;
const SCORE_UNVOTE = -1;
const SCORE_UP = 1;

const VOTE_DOWN = 'VOTE_DOWN';
const VOTE_UP = 'VOTE_UP';
const UPDATE_VOTES = 'VOTES_UPDATE';

function reducer(state, action) {
	switch (action.type) {
		case VOTE_UP:
			return {
				negativeVotes:
					state.pressed !== PRESSED_DOWN
						? state.negativeVotes
						: state.negativeVotes - 1,
				positiveVotes:
					state.pressed === PRESSED_UP
						? state.positiveVotes - 1
						: state.positiveVotes + 1,
				pressed: state.pressed !== PRESSED_UP ? PRESSED_UP : null,
			};
		case VOTE_DOWN:
			return {
				negativeVotes:
					state.pressed !== PRESSED_DOWN
						? state.negativeVotes + 1
						: state.negativeVotes - 1,
				positiveVotes:
					state.pressed !== PRESSED_UP
						? state.positiveVotes
						: state.positiveVotes - 1,
				pressed: state.pressed !== PRESSED_DOWN ? PRESSED_DOWN : null,
			};
		case UPDATE_VOTES:
			return {
				...state,
				negativeVotes: action.payload.negativeVotes,
				positiveVotes: action.payload.positiveVotes,
			};
		default:
			return state;
	}
}

const RatingsThumbs = ({
	disabled = true,
	initialNegativeVotes = 0,
	initialPositiveVotes = 0,
	inititalTitle,
	sendVoteRequest,
	size = 'sm',
	thumbDown = false,
	thumbUp = false,
}) => {
	const [state, dispatch] = useReducer(reducer, {
		negativeVotes: initialNegativeVotes,
		positiveVotes: initialPositiveVotes,
		pressed: thumbDown ? PRESSED_DOWN : thumbUp ? PRESSED_UP : null,
	});

	const {negativeVotes, positiveVotes, pressed} = state;
	const [animatedButtonUp, setAnimatedButtonUp] = useState(false);
	const [animatedButtonDown, setAnimatedButtonDown] = useState(false);

	const isMounted = useIsMounted();

	const handleAnimationEndUp = () => {
		setAnimatedButtonUp(false);
	};

	const handleAnimationEndDown = () => {
		setAnimatedButtonDown(false);
	};

	const handleSendVoteRequest = useCallback(
		(score) => {
			sendVoteRequest(score).then(({totalEntries, totalScore} = {}) => {
				if (isMounted() && totalEntries && totalScore) {
					const positiveVotes = Math.round(totalScore);

					dispatch({
						payload: {
							negativeVotes: totalEntries - positiveVotes,
							positiveVotes,
						},
						type: UPDATE_VOTES,
					});
				}
			});
		},
		[isMounted, sendVoteRequest]
	);

	const voteUp = useCallback(() => {
		if (pressed !== PRESSED_UP) {
			setAnimatedButtonUp(true);
		}

		dispatch({type: VOTE_UP});

		const score = pressed !== PRESSED_UP ? SCORE_UP : SCORE_UNVOTE;
		handleSendVoteRequest(score);
	}, [handleSendVoteRequest, pressed]);

	const voteDown = useCallback(() => {
		if (pressed !== PRESSED_DOWN) {
			setAnimatedButtonDown(true);
		}

		dispatch({type: VOTE_DOWN});

		const score = pressed !== PRESSED_DOWN ? SCORE_DOWN : SCORE_UNVOTE;
		handleSendVoteRequest(score);
	}, [handleSendVoteRequest, pressed]);

	const getTitleThumbsUp = useCallback(() => {
		if (inititalTitle !== undefined) {
			return inititalTitle;
		}

		if (pressed === PRESSED_UP) {
			return Liferay.Language.get('you-have-rated-this-as-good');
		}
		else {
			return Liferay.Language.get('rate-this-as-good');
		}
	}, [inititalTitle, pressed]);

	const getTitleThumbsDown = useCallback(() => {
		if (inititalTitle !== undefined) {
			return inititalTitle;
		}

		if (pressed === PRESSED_DOWN) {
			return Liferay.Language.get('you-have-rated-this-as-bad');
		}
		else {
			return Liferay.Language.get('rate-this-as-bad');
		}
	}, [inititalTitle, pressed]);

	return (
		<div className="ratings-thumbs">
			<ClayButton
				aria-pressed={pressed === PRESSED_UP}
				borderless
				className={classNames('btn-thumbs-up', {
					'btn-animated': animatedButtonUp,
				})}
				disabled={disabled}
				displayType="secondary"
				onClick={voteUp}
				size={size}
				title={getTitleThumbsUp()}
				value={positiveVotes}
			>
				<span className="inline-item inline-item-before">
					<span className="off">
						<ClayIcon symbol="thumbs-up" />
					</span>

					<span className="on" onAnimationEnd={handleAnimationEndUp}>
						<ClayIcon symbol="thumbs-up-full" />
					</span>
				</span>

				<span className="inline-item">
					<AnimatedCounter counter={positiveVotes} />
				</span>
			</ClayButton>

			<ClayButton
				aria-pressed={pressed === PRESSED_DOWN}
				borderless
				className={classNames('btn-thumbs-down', {
					'btn-animated': animatedButtonDown,
				})}
				disabled={disabled}
				displayType="secondary"
				onClick={voteDown}
				size={size}
				title={getTitleThumbsDown()}
				value={negativeVotes}
			>
				<span className="inline-item inline-item-before">
					<span className="off">
						<ClayIcon symbol="thumbs-down" />
					</span>

					<span
						className="on"
						onAnimationEnd={handleAnimationEndDown}
					>
						<ClayIcon symbol="thumbs-down-full" />
					</span>
				</span>

				<span className="inline-item">
					<AnimatedCounter counter={negativeVotes} />
				</span>
			</ClayButton>
		</div>
	);
};

RatingsThumbs.propTypes = {
	disabled: PropTypes.bool,
	initialNegativeVotes: PropTypes.number,
	initialPositiveVotes: PropTypes.number,
	inititalTitle: PropTypes.string,
	positiveVotes: PropTypes.number,
	sendVoteRequest: PropTypes.func.isRequired,
	thumbDown: PropTypes.bool,
	thumbUp: PropTypes.bool,
};

export default function (props) {
	return <RatingsThumbs {...props} />;
}
