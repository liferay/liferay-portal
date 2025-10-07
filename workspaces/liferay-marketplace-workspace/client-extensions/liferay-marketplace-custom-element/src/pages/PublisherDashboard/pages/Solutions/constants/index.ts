/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SolutionInitialState} from '../../../../../context/SolutionContext';
import i18n from '../../../../../i18n';
import zodSchema from '../../../../../schema/zod';

export const MAX_IMAGE_QUANTITY = 5;
export const MAX_SIZE_5MBS = 5_000_000;

export const SOLUTION_FLOW_ITEMS = [
	{
		description: i18n.translate(
			'review-and-accept-the-legal-agreement-between-you-and-liferay-before-proceeding-you-are-about-to-create-a-new-solution-submission'
		),
		label: i18n.translate('create'),
		path: '',
		title: i18n.translate('create-template'),
	},
	{
		description: i18n.translate(
			'enter-your-solution-details-this-information-will-be-used-for-submission-presentation-customer-support-and-search-capabilities'
		),
		label: i18n.translate('profile'),
		parseSchema: (context: SolutionInitialState) =>
			zodSchema.solutionPublishing.profile.safeParse(context.profile),
		path: 'profile',
		title: i18n.translate('define-the-solution-profile'),
	},
	{
		description: i18n.translate(
			'design-the-storefront-for-your-solution-this-will-set-the-information-displayed-on-the-solutions-page-this-section-is-dedicated-to-creating-the-solutions-header'
		),
		label: i18n.translate('solution-header'),
		parseSchema: (context: SolutionInitialState) =>
			zodSchema.solutionPublishing.header.safeParse(context.header),
		path: 'header',
		title: i18n.translate('customize-solution-header'),
	},
	{
		description: i18n.translate(
			'design-the-storefront-for-your-solution-this-will-set-the-information-displayed-on-the-solutions-page-this-section-is-dedicated-to-creating-the-solutions-detail-content'
		),
		label: i18n.translate('solution-details'),
		parseSchema: (context: SolutionInitialState) =>
			zodSchema.solutionPublishing.details.safeParse(context.details),
		path: 'details',
		title: i18n.translate('customize-storefront-solutions-details'),
	},
	{
		description: i18n.translate(
			'define-company-profile-information-for-your-solution-this-will-inform-users-about-this-versions-updates-on-the-storefront'
		),
		label: i18n.translate('company-profile'),
		parseSchema: (context: SolutionInitialState) =>
			zodSchema.solutionPublishing.company.safeParse(context.company),
		path: 'company',
		title: i18n.translate('provide-company-profile-details'),
	},
	{
		description: i18n.translate(
			'define-contact-information-for-your-solution-this-will-inform-users-about-this-versions-updates-on-the-storefront'
		),
		label: i18n.translate('contact-us'),
		parseSchema: (context: SolutionInitialState) =>
			zodSchema.solutionPublishing.contactUs.safeParse(context.contactUs),
		path: 'contact',
		title: i18n.translate('provide-contact-us-details'),
	},
	{
		description: i18n.translate(
			'please-review-before-submitting-once-sent-you-will-not-be-able-to-edit-any-information-until-this-submission-is-completely-reviewed-by-liferay'
		),
		label: i18n.translate('submit'),
		parseSchema: (context: SolutionInitialState) =>
			zodSchema.solutionPublishing.termsAndConditions.safeParse(
				context.termsAndConditions
			),
		path: 'submit',
		title: 'Review and submit solution',
	},
];
