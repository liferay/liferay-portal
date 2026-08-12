/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

export const FORM_ERROR_TYPES = {
	deletedFragment: 'deletedFragment',
	draftNotAvailable: 'draftNotAvailable',
	emptySteps: 'emptySteps',
	hiddenFields: 'hiddenFields',
	hiddenFragment: 'hiddenFragment',
	missingFields: 'missingFields',
	missingFragments: 'missingFragments',
	missingLocalizableFields: 'missingLocalizableFields',
	missingNextButton: 'missingNextButton',
	missingPreviousButton: 'missingPreviousButton',
	missingSubmit: 'missingSubmit',
} as const;

type FormErrorType = (typeof FORM_ERROR_TYPES)[keyof typeof FORM_ERROR_TYPES];

type FormError = {
	name?: string;
	steps?: number[];
	type: FormErrorType;
};

export function getFormErrorDescription(error: FormError) {
	const {name = '', type} = error;

	switch (type) {
		case FORM_ERROR_TYPES.deletedFragment:
			return {
				message: Liferay.Language.get(
					'the-deleted-fragment-was-marked-as-required'
				),
			};

		case FORM_ERROR_TYPES.draftNotAvailable:
			return {
				message: sub(
					Liferay.Language.get(
						'x-form-does-not-allow-creating-entries-as-draft'
					),
					name
				),
				title: Liferay.Language.get('save-as-draft-not-available'),
			};

		case FORM_ERROR_TYPES.emptySteps: {
			const steps = [...(error.steps ?? [])];

			const lastStep = sub(Liferay.Language.get('step-x'), steps.pop());

			const firstSteps = steps
				.map((index) => sub(Liferay.Language.get('step-x'), index))
				.join(', ');

			return {
				message: firstSteps.length
					? sub(
							Liferay.Language.get('x-and-x-of-x-form-are-empty'),
							firstSteps,
							lastStep,
							name
						)
					: sub(
							Liferay.Language.get('x-of-x-form-is-empty'),
							lastStep,
							name
						),
				summary: firstSteps.length
					? sub(
							Liferay.Language.get('x-and-x-are-empty'),
							firstSteps,
							lastStep
						)
					: sub(Liferay.Language.get('x-is-empty'), lastStep),
				title: Liferay.Language.get('empty-steps'),
			};
		}

		case FORM_ERROR_TYPES.hiddenFields:
			return {
				message: sub(
					Liferay.Language.get(
						'x-form-contains-one-or-more-hidden-fragments-mapped-to-required-fields'
					),
					name
				),
				summary: Liferay.Language.get(
					'one-or-more-fragments-mapped-to-required-fields-are-hidden'
				),
				title: Liferay.Language.get('required-fields-hidden'),
			};

		case FORM_ERROR_TYPES.hiddenFragment:
			return {
				message: Liferay.Language.get(
					'the-hidden-fragment-contained-required-fields'
				),
			};

		case FORM_ERROR_TYPES.missingFields:
			return {
				message: sub(
					Liferay.Language.get(
						'x-form-has-one-or-more-required-fields-not-mapped-from-the-form'
					),
					name
				),
				summary: Liferay.Language.get(
					'one-or-more-required-fields-are-not-mapped-from-the-form'
				),
				title: Liferay.Language.get('required-fields-missing'),
			};

		case FORM_ERROR_TYPES.missingFragments:
			return {
				message: sub(
					Liferay.Language.get(
						'x-form-has-some-fragments-not-mapped-to-object-fields'
					),
					name
				),
				summary: Liferay.Language.get(
					'some-fragments-are-not-mapped-to-object-fields'
				),
				title: Liferay.Language.get('fragment-mapping-missing'),
			};

		case FORM_ERROR_TYPES.missingNextButton: {
			const steps = [...(error.steps ?? [])];

			const lastStep = sub(Liferay.Language.get('step-x'), steps.pop());

			const firstSteps = steps
				.map((index) => sub(Liferay.Language.get('step-x'), index))
				.join(', ');

			return {
				message: firstSteps.length
					? sub(
							Liferay.Language.get(
								'next-button-is-hidden-or-missing-in-x-and-x-of-x-form'
							),
							firstSteps,
							lastStep,
							name
						)
					: sub(
							Liferay.Language.get(
								'next-button-is-hidden-or-missing-in-x-of-x-form'
							),
							lastStep,
							name
						),
				summary: firstSteps.length
					? sub(
							Liferay.Language.get(
								'next-button-is-hidden-or-missing-in-x-and-x'
							),
							firstSteps,
							lastStep
						)
					: sub(
							Liferay.Language.get(
								'next-button-is-hidden-or-missing-in-x'
							),
							lastStep
						),
				title: Liferay.Language.get('next-button-missing'),
			};
		}

		case FORM_ERROR_TYPES.missingPreviousButton: {
			const steps = [...(error.steps ?? [])];

			const lastStep = sub(Liferay.Language.get('step-x'), steps.pop());

			const firstSteps = steps
				.map((index) => sub(Liferay.Language.get('step-x'), index))
				.join(', ');

			return {
				message: firstSteps.length
					? sub(
							Liferay.Language.get(
								'previous-button-is-hidden-or-missing-in-x-and-x-of-x-form'
							),
							firstSteps,
							lastStep,
							name
						)
					: sub(
							Liferay.Language.get(
								'previous-button-is-hidden-or-missing-in-x-of-x-form'
							),
							lastStep,
							name
						),
				summary: firstSteps.length
					? sub(
							Liferay.Language.get(
								'previous-button-is-hidden-or-missing-in-x-and-x'
							),
							firstSteps,
							lastStep
						)
					: sub(
							Liferay.Language.get(
								'previous-button-is-hidden-or-missing-in-x'
							),
							lastStep
						),
				title: Liferay.Language.get('previous-button-missing'),
			};
		}

		case FORM_ERROR_TYPES.missingSubmit:
			return {
				message: sub(
					Liferay.Language.get(
						'x-form-has-a-hidden-or-missing-submit-button'
					),
					name
				),
				summary: Liferay.Language.get(
					'submit-button-is-hidden-or-missing'
				),
				title: Liferay.Language.get('submit-button-missing'),
			};

		case FORM_ERROR_TYPES.missingLocalizableFields:
			return {
				message: sub(
					Liferay.Language.get(
						'the-page-has-a-localization-select-but-the-localizable-form-fields-are-hidden-or-missing'
					),
					name
				),
				summary: Liferay.Language.get(
					'localizable-fields-are-hidden-or-missing'
				),
				title: Liferay.Language.get(
					'localizable-fields-hidden-or-missing'
				),
			};

		default:
			return;
	}
}
