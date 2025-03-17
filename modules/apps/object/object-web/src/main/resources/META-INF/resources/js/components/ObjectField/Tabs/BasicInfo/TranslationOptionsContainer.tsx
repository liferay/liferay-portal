/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {Toggle} from '@liferay/object-js-components-web';
import classNames from 'classnames';
import React from 'react';

import './TranslationOptionsContainer.scss';

interface TranslationOptionsContainerProps {
	modelBuilder?: boolean;
	objectDefinition?: ObjectDefinition;
	onSubmit?: () => void;
	published: boolean;
	setValues: (values: Partial<ObjectField>) => void;
	values: Partial<ObjectField>;
}

export function TranslationOptionsContainer({
	modelBuilder,
	objectDefinition,
	onSubmit,
	published,
	setValues,
	values,
}: TranslationOptionsContainerProps) {
	const translatableField =
		(values.businessType === 'LongText' ||
			values.businessType === 'RichText' ||
			values.businessType === 'Text' ||
			(Liferay.FeatureFlags['LPD-32050'] &&
				(values.businessType === 'Attachment' ||
					values.businessType === 'Boolean' ||
					values.businessType === 'Date' ||
					values.businessType === 'DateTime' ||
					values.businessType === 'Decimal' ||
					values.businessType === 'Integer' ||
					values.businessType === 'LongInteger' ||
					values.businessType === 'MultiselectPicklist' ||
					values.businessType === 'Picklist' ||
					values.businessType === 'PrecisionDecimal'))) &&
		!values.system;

	return (
		<div
			className={classNames({
				'lfr-objects__edit-object-field-card-content':
					modelBuilder === false,
				'lfr-objects__edit-object-field-model-builder-panel':
					modelBuilder,
			})}
		>
			{!translatableField && (
				<ClayAlert
					displayType="info"
					title={`${Liferay.Language.get('info')}:`}
				>
					{`${Liferay.Language.get(
						'this-field-type-does-not-support-translations'
					)} `}

					<ClayLink href="#" target="_blank" weight="semi-bold">
						{Liferay.Language.get('click-here-for-documentation')}
					</ClayLink>
				</ClayAlert>
			)}

			<div className="lfr__objects-translation-options-container">
				<Toggle
					disabled={
						published ||
						!translatableField ||
						!objectDefinition?.enableLocalization ||
						(!Liferay.FeatureFlags['LPD-32050'] && values.required)
					}
					label={Liferay.Language.get('enable-entry-translations')}
					onBlur={(event) => {
						event.stopPropagation();

						if (onSubmit) {
							onSubmit();
						}
					}}
					onToggle={(localized) =>
						setValues({
							localized,
						})
					}
					toggled={values.localized}
				/>

				<ClayTooltipProvider>
					<span
						title={Liferay.Language.get(
							'users-will-be-able-to-add-translations-for-the-entries-of-this-field'
						)}
					>
						<ClayIcon
							className="lfr__objects-translation-options-container-icon"
							symbol="question-circle-full"
						/>
					</span>
				</ClayTooltipProvider>
			</div>
		</div>
	);
}
