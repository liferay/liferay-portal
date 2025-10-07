/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch, objectToFormData} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback} from 'react';

import TYPES from '../RATINGS_TYPES';
import {errorToast} from '../utils/toast';
import RatingsLike from './RatingsLike';
import RatingsStars from './RatingsStars';
import RatingsThumbs from './RatingsThumbs';

const RATE_ENTRY_URL = '/c/portal/rate_entry';

const BaseRatings = ({
	className,
	classPK,
	contentTitle,
	enabled = false,
	externalReferenceCode = '',
	inTrash = false,
	signedIn,
	type,
	url = RATE_ENTRY_URL,
	...restProps
}) => {
	const getDefaultTitle = () => {
		if (!signedIn) {
			return '';
		}

		if (inTrash) {
			return Liferay.Language.get(
				'ratings-are-disabled-because-this-entry-is-in-the-recycle-bin'
			);
		}
		else if (!enabled) {
			return Liferay.Language.get('ratings-are-disabled-in-staging');
		}
	};

	const sendVoteRequest = useCallback(
		(score) => {
			if (Liferay.Session.sessionState === 'expired') {
				errorToast(
					`${Liferay.Language.get('you-must-be-signed-in-to-rate')}`
				);

				return Promise.resolve();
			}

			Liferay.fire('ratings:vote', {
				className,
				classPK,
				contentTitle: contentTitle || '',
				externalReferenceCode,
				ratingType: type,
				score,
			});

			const body = objectToFormData({
				className,
				classPK,
				p_auth: Liferay.authToken,
				p_l_id: themeDisplay.getPlid(),
				score,
			});

			return fetch(url, {
				body,
				method: 'POST',
			})
				.then((response) => response.json())
				.catch(() => {
					errorToast();
				});
		},
		[className, classPK, contentTitle, externalReferenceCode, type, url]
	);

	const RatingsTypes = {
		[TYPES.LIKE]: RatingsLike,
		[TYPES.STARS]: RatingsStars,
		[TYPES.STACKED_STARS]: RatingsStars,
		[TYPES.THUMBS]: RatingsThumbs,
	};

	const RatingsComponent = RatingsTypes[type];

	return (
		<RatingsComponent
			{...restProps}
			disabled={!signedIn || !enabled}
			inititalTitle={getDefaultTitle()}
			sendVoteRequest={sendVoteRequest}
			type={type}
		/>
	);
};

BaseRatings.propTypes = {
	className: PropTypes.string.isRequired,
	classPK: PropTypes.string.isRequired,
	enabled: PropTypes.bool,
	externalReferenceCode: PropTypes.string,
	inTrash: PropTypes.bool,
	signedIn: PropTypes.bool.isRequired,
	type: PropTypes.string.isRequired,
	url: PropTypes.string.isRequired,
};

export default BaseRatings;
