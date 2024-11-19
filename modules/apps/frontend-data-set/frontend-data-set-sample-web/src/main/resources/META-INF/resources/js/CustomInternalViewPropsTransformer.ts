/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';

import CarouselView from './CarouselView';

export default function propsTransformer({...otherProps}: any) {
	const carouselViewRenderer: IInternalRenderer = {
		component: CarouselView,
		default: true,
		label: 'My Carousel View',
		name: 'carouselViewRenderer',
		symbol: 'rotate',
		type: 'internal',
	};

	return {
		...otherProps,
		customRenderers: {
			view: [carouselViewRenderer],
		},
		views: [
			{
				contentRenderer: 'table',
				label: 'My Table View',
				name: 'table',
				schema: {
					fields: [
						{
							fieldName: 'title',
							label: 'Title',
						},
						{
							fieldName: 'color',
							label: 'Color',
						},
					],
				},
				thumbnail: 'table',
			},
		],
	};
}
