/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayPanel from '@clayui/panel';
import {openToast} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React, {useCallback} from 'react';

import CustomTokenIcon from './CustomTokenIcon';
import {config} from './config';
import {FRONTEND_TOKEN_TYPES} from './constants/frontendTokenTypes';
import {
	useFrontendTokensValues,
	useSaveTokenValue,
} from './contexts/StyleBookEditorContext';
import BooleanFrontendToken from './frontend_tokens/BooleanFrontendToken';
import ColorFrontendToken from './frontend_tokens/ColorFrontendToken';
import LengthFrontendToken from './frontend_tokens/LengthFrontendToken';
import SelectFrontendToken from './frontend_tokens/SelectFrontendToken';
import TextFrontendToken from './frontend_tokens/TextFrontendToken';

export default function FrontendTokenSet({
	frontendTokens,
	label,
	open,
	tokenValues,
}) {
	const frontendTokensValues = useFrontendTokensValues();
	const saveTokenValue = useSaveTokenValue();

	const updateFrontendTokensValues = useCallback(
		(frontendToken, value) => {
			const {
				label,
				mappings = [],
				name,
				tokenDefinitionId,
			} = frontendToken;

			const cssVariableMapping = mappings.find(
				(mapping) => mapping.type === 'cssVariable'
			);

			if (!value) {
				return;
			}

			if (!cssVariableMapping) {
				openToast({
					message: Liferay.Language.get(
						'unable-to-save-due-to-invalid-or-missing-configuration-values'
					),
					type: 'danger',
				});

				return;
			}

			saveTokenValue({
				label,
				name,
				value: {
					cssVariableMapping: cssVariableMapping.value,
					name: tokenValues[value]?.name,
					tokenDefinitionId,
					value: tokenValues[value]?.value || value,
				},
			});
		},
		[saveTokenValue, tokenValues]
	);

	return (
		<ClayPanel
			collapsable
			defaultExpanded={open}
			displayTitle={label}
			displayType="unstyled"
			showCollapseIcon
		>
			<ClayPanel.Body>
				{frontendTokens.map((frontendToken) => {
					const FrontendTokenComponent =
						getFrontendTokenComponent(frontendToken);

					const props = {
						frontendToken,
						frontendTokensValues,
						labelAfter: frontendToken.tokenDefinitionId ===
							config.customTokenDefinitionId && (
							<CustomTokenIcon />
						),
						onValueSelect: (value) =>
							updateFrontendTokensValues(frontendToken, value),
						tokenValues,
						value:
							frontendTokensValues[frontendToken.name]?.name ||
							frontendTokensValues[frontendToken.name]?.value ||
							frontendToken.defaultValue,
					};

					return (
						<FrontendTokenComponent
							key={frontendToken.name}
							{...props}
						/>
					);
				})}
			</ClayPanel.Body>
		</ClayPanel>
	);
}

function getFrontendTokenComponent(frontendToken) {
	if (frontendToken.editorType === 'ColorPicker') {
		return ColorFrontendToken;
	}

	if (frontendToken.editorType === 'Length') {
		return LengthFrontendToken;
	}

	if (frontendToken.validValues) {
		return SelectFrontendToken;
	}

	if (frontendToken.type === FRONTEND_TOKEN_TYPES.boolean) {
		return BooleanFrontendToken;
	}

	return TextFrontendToken;
}

FrontendTokenSet.propTypes = {
	frontendTokens: PropTypes.arrayOf(
		PropTypes.shape({
			name: PropTypes.string.isRequired,
		})
	),
	name: PropTypes.string,
};
