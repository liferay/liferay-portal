/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {NewAppInitialState} from '../../../../../context/NewAppContext';
import i18n from '../../../../../i18n';
import zodSchema from '../../../../../schema/zod';

export const NEW_APP_BUILD_FLOW_ITEMS = [
	{
		alertText: i18n.translate(
			'please-be-aware-that-since-you-are-adding-a-new-version-of-the-app-the-only-section-visible-is-the-build'
		),
		description: () =>
			i18n.translate(
				'use-one-of-the-following-methods-to-provide-your-app-builds'
			),
		label: i18n.translate('build'),
		parseSchema: (context: NewAppInitialState) =>
			zodSchema.appPublishing.build.safeParse(context.build),
		path: '',
		title: i18n.translate('provide-app-build'),
	},

	{
		description: () =>
			i18n.translate(
				'please-review-before-submitting-once-sent-you-will-not-be-able-to-edit-any-information-until-this-submission-is-completely-reviewed-by-liferay'
			),
		label: i18n.translate('submit'),
		path: 'submit',
		title: i18n.translate('review-and-submit-app'),
	},
];
