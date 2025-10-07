/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import React from 'react';

import TYPES from '../RATINGS_TYPES';

// @ts-ignore

import BaseRatings from './BaseRatings';

import '../../css/main.scss';

type Props = {
	className: string;
	classPK: string;
	contentTitle?: string;
	enabled?: boolean;
	inTrash?: boolean;
	initialNegativeVotes?: number;
	initialPositiveVotes?: number;
	signedIn?: boolean;
	size?: React.ComponentProps<typeof ClayButton>['size'];
	thumbDown?: boolean;
	thumbUp?: boolean;
	type: (typeof TYPES)[keyof typeof TYPES];
	url?: string;
};

export default function Ratings(props: Props) {
	return <BaseRatings {...props} />;
}
