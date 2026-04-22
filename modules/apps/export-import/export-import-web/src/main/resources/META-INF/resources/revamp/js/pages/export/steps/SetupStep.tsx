/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {sub} from 'frontend-js-web';
import React from 'react';

import {
	FormikFieldMultiCheckbox,
	FormikFieldText,
} from '../../../components/forms/formik';
import {mockPorletDataHandlerSections} from '../../../utils/mockPorletDataHandlerSections';

export default function SetupStep() {
	return (
		<>
			<ClayLayout.Sheet>
				<ClayLayout.SheetHeader className="mb-0">
					<div className="mb-2 sheet-title">
						{sub(
							Liferay.Language.get('x-details'),
							Liferay.Language.get('export')
						)}
					</div>

					<div className="sheet-text text-3">
						{Liferay.Language.get(
							'provide-a-descriptive-name-for-your-export'
						)}
					</div>
				</ClayLayout.SheetHeader>

				<FormikFieldText
					label={Liferay.Language.get('name')}
					name="name"
					required
				/>
			</ClayLayout.Sheet>

			<ClayLayout.Sheet>
				<ClayLayout.SheetHeader className="mb-0">
					<div
						className="mb-2 sheet-title"
						id="selectedSectionIds-label"
					>
						{Liferay.Language.get('what-would-you-like-to-export')}
					</div>

					<div
						className="sheet-text text-3"
						id="selectedSectionIds-description"
					>
						{Liferay.Language.get(
							'select-all-the-entity-types-that-you-want-to-export.-please-select-at-least-one-to-continue'
						)}
					</div>
				</ClayLayout.SheetHeader>

				<FormikFieldMultiCheckbox
					aria-describedby="selectedSectionIds-description"
					aria-labelledby="selectedSectionIds-label"
					name="selectedSectionIds"
					options={mockPorletDataHandlerSections.map(
						({name, portletEntries}) => ({
							description: portletEntries
								.map(({portletTitle}) => portletTitle)
								.join(', '),
							label: name,
							value: name,
						})
					)}
				/>
			</ClayLayout.Sheet>
		</>
	);
}
