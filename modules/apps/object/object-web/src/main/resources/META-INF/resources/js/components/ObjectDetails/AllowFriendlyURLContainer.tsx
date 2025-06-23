/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React from 'react';

import {hasLegacySeparator} from './SeparatorContainer';

import './AllowFriendlyURLContainer.scss';

interface AllowFriendlyURLContainerProps {
	disabled?: boolean;
	onSubmit?: (editedObjectDefinition?: Partial<ObjectDefinition>) => void;
	setValues: (values: Partial<ObjectDefinition>) => void;
	values: Partial<ObjectDefinition>;
}

export function AllowFriendlyURLContainer({
	disabled,
	onSubmit,
	setValues,
	values,
}: AllowFriendlyURLContainerProps) {
	return (
		<>
			<div className="lfr-objects__seo-container-allow-friendly-url-container">
				<ClayCheckbox
					checked={!!values.enableFriendlyURLCustomization}
					className="lfr-objects__seo-container-checkbox"
					disabled={
						disabled ||
						hasLegacySeparator(values.friendlyURLSeparator)
					}
					label={Liferay.Language.get(
						"allow-overriding-an-entry's-friendly-url"
					)}
					onBlur={(event) => {
						event.stopPropagation();

						if (onSubmit) {
							onSubmit();
						}
					}}
					onChange={({target: {checked}}) => {
						setValues({
							enableFriendlyURLCustomization: checked,
						});
					}}
				/>

				<ClayTooltipProvider>
					<span
						title={Liferay.Language.get(
							'this-is-only-allowed-for-object-definitions-that-do-not-use-the-l-separator'
						)}
					>
						<ClayIcon
							className="lfr-objects__seo-container-tooltip-icon"
							symbol="question-circle-full"
						/>
					</span>
				</ClayTooltipProvider>
			</div>

			<div className="c-mb-sm-4 lfr-objects__seo-container-help-text">
				<Text color="secondary" size={3}>
					{Liferay.Language.get(
						"when-enabled,-users-can-override-an-entry's-friendly-url"
					)}
				</Text>
			</div>
		</>
	);
}
