/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput, ClaySelect} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {LearnMessage} from 'frontend-js-components-web';
import React from 'react';

import InputSetItemHeader from './InputSetItemHeader';
import CharacterThresholdInput from './inputs/CharacterThresholdInput';
import DisplayGroupNameInput from './inputs/DisplayGroupNameInput';
import FieldsInput from './inputs/FieldsInput';
import SXPBlueprintSelectorInput from './inputs/SXPBlueprintSelectorInput';
import SizeInput from './inputs/SizeInput';

function SXPBlueprint({index, onBlur, onInputSetItemChange, touched, value}) {
	const _handleChangeAttribute = (property) => (event) => {
		onInputSetItemChange(index, {
			attributes: {
				...value.attributes,
				[property]: event.target.value,
			},
		});
	};

	const _handleChangeFields = (fields) => {
		onInputSetItemChange(index, {
			attributes: {...value.attributes, fields},
		});
	};

	const _handleChangeSXPBlueprint = (externalReferenceCode) => {
		onInputSetItemChange(index, {
			attributes: {
				...value.attributes,
				sxpBlueprintExternalReferenceCode: externalReferenceCode,
			},
		});
	};

	return (
		<>
			<InputSetItemHeader>
				<InputSetItemHeader.Title>
					{Liferay.Language.get('blueprint-suggestions-contributor')}
				</InputSetItemHeader.Title>

				<InputSetItemHeader.Description>
					{Liferay.Language.get(
						'blueprint-suggestions-contributor-help'
					)}

					<LearnMessage
						className="c-ml-1"
						resource="portal-search-web"
						resourceKey="search-bar-suggestions-blueprints"
					/>
				</InputSetItemHeader.Description>
			</InputSetItemHeader>

			<div className="c-mb-3 form-group-autofit">
				<DisplayGroupNameInput
					index={index}
					onBlur={onBlur('displayGroupName')}
					onChange={onInputSetItemChange(index, 'displayGroupName')}
					touched={touched.displayGroupName}
					value={value.displayGroupName}
				/>

				<SizeInput
					index={index}
					onBlur={onBlur('size')}
					onChange={onInputSetItemChange(index, 'size')}
					touched={touched.size}
					value={value.size}
				/>
			</div>

			<div className="c-mb-3 form-group-autofit">
				<SXPBlueprintSelectorInput
					index={index}
					onBlur={onBlur(
						'attributes.sxpBlueprintExternalReferenceCode'
					)}
					onSubmit={_handleChangeSXPBlueprint}
					sxpBlueprintExternalReferenceCode={
						value.attributes?.sxpBlueprintExternalReferenceCode
					}
					touched={
						touched['attributes.sxpBlueprintExternalReferenceCode']
					}
				/>
			</div>

			<div className="c-mb-3 form-group-autofit">
				<CharacterThresholdInput
					index={index}
					onBlur={onBlur('attributes.characterThreshold')}
					onChange={_handleChangeAttribute('characterThreshold')}
					touched={touched['attributes.characterThreshold']}
					value={value.attributes?.characterThreshold}
				/>

				<ClayInput.GroupItem>
					<label htmlFor={`includeAssetURL${index}`}>
						{Liferay.Language.get('include-asset-url')}

						<ClayTooltipProvider>
							<span
								className="c-ml-2"
								data-tooltip-align="top"
								tabIndex={0}
								title={Liferay.Language.get(
									'include-asset-url-help'
								)}
							>
								<ClayIcon symbol="question-circle-full" />
							</span>
						</ClayTooltipProvider>
					</label>

					<ClaySelect
						aria-label={Liferay.Language.get('include-asset-url')}
						id={`includeAssetURL${index}`}
						onChange={_handleChangeAttribute('includeAssetURL')}
						value={value.attributes?.includeAssetURL}
					>
						<ClaySelect.Option
							label={Liferay.Language.get('true')}
							value={true}
						/>

						<ClaySelect.Option
							label={Liferay.Language.get('false')}
							value={false}
						/>
					</ClaySelect>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem>
					<label htmlFor={`includeAssetSummary${index}`}>
						{Liferay.Language.get('include-asset-summary')}

						<ClayTooltipProvider>
							<span
								className="c-ml-2"
								data-tooltip-align="top"
								tabIndex={0}
								title={Liferay.Language.get(
									'include-asset-summary-help'
								)}
							>
								<ClayIcon symbol="question-circle-full" />
							</span>
						</ClayTooltipProvider>
					</label>

					<ClaySelect
						aria-label={Liferay.Language.get(
							'include-asset-summary'
						)}
						id={`includeAssetSummary${index}`}
						onChange={_handleChangeAttribute(
							'includeAssetSearchSummary'
						)}
						value={value.attributes?.includeAssetSearchSummary}
					>
						<ClaySelect.Option
							label={Liferay.Language.get('true')}
							value={true}
						/>

						<ClaySelect.Option
							label={Liferay.Language.get('false')}
							value={false}
						/>
					</ClaySelect>
				</ClayInput.GroupItem>
			</div>

			<div className="c-mb-0 form-group-autofit">
				<FieldsInput
					fields={value.attributes?.fields}
					index={index}
					onBlur={onBlur('attributes.fields')}
					onChange={_handleChangeFields}
					touched={touched['attributes.fields']}
				/>
			</div>
		</>
	);
}

export default SXPBlueprint;
