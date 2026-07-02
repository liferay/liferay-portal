/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import {useId} from 'frontend-js-components-web';
import React, {useReducer, useRef, useState} from 'react';

import {initializeConfig} from '../../app/config/index';
import {Config} from '../../types/config';
import AudiencePriority from './AudiencePriority';
import ElementVariationForm from './ElementVariationForm';
import ElementVariationService from './ElementVariationService';
import ElementVariationsList from './ElementVariationsList';
import ElementVariationsPreview, {
	ElementVariationsPreviewRef,
} from './ElementVariationsPreview';
import {
	LoadedElementVariation,
	createElementVariation,
	createInitialState,
	reducer,
} from './elementVariationsReducer';

import './ElementVariations.scss';

interface Props {
	addElementVariationURL: string;
	audiences: Array<{label: string; value: string}>;
	defaultLanguageId: string;
	deleteElementVariationURL: string;
	elementVariations: Array<LoadedElementVariation>;
	experiences: Array<{
		label: string;
		segmentsExperienceERC: string;
		segmentsExperienceId: number;
	}>;
	locales: Array<{id: string; label: string; symbol: string}>;
	plid: number;
	portletNamespace: string;
	previewURL: string;
	selectedSegmentsExperienceId: number;
}

export default function (props: Props) {
	initializeConfig({portletNamespace: props.portletNamespace} as Config);

	return <ElementVariations {...props} />;
}

function ElementVariations({
	addElementVariationURL,
	audiences = [],
	defaultLanguageId,
	deleteElementVariationURL,
	elementVariations: initialElementVariations = [],
	experiences = [],
	locales,
	plid,
	previewURL,
	selectedSegmentsExperienceId,
}: Props) {
	const experienceId = useId();

	const [experienceKey, setExperienceKey] = useState(() => {
		const selectedExperience = experiences.find(
			(experience) =>
				experience.segmentsExperienceId === selectedSegmentsExperienceId
		);

		return (
			selectedExperience?.segmentsExperienceERC ??
			experiences[0]?.segmentsExperienceERC ??
			''
		);
	});

	const [{draftElementVariation, elementVariations, languageId}, dispatch] =
		useReducer(
			reducer,
			{
				defaultLanguageId,
				elementVariations: initialElementVariations,
			},
			createInitialState
		);

	const experienceElementVariations = elementVariations.filter(
		(elementVariation) =>
			elementVariation.segmentsExperienceERC === experienceKey
	);

	const elementVariationsPreviewRef =
		useRef<ElementVariationsPreviewRef>(null);

	return (
		<div className="d-flex element-variations flex-column">
			<div className="d-flex element-variations__content flex-grow-1">
				<div className="bg-white border-right d-flex element-variations__sidebar flex-column flex-shrink-0">
					{draftElementVariation ? (
						<ElementVariationForm
							audiences={audiences}
							defaultLanguageId={defaultLanguageId}
							elementVariation={draftElementVariation}
							key={draftElementVariation.key}
							languageId={languageId}
							locales={locales}
							onCancel={() =>
								dispatch({
									type: 'CANCEL_ELEMENT_VARIATION_DRAFT',
								})
							}
							onChange={(properties) =>
								dispatch({
									properties,
									type: 'UPDATE_ELEMENT_VARIATION_DRAFT',
								})
							}
							onLanguageIdChange={(languageId) =>
								dispatch({
									languageId,
									type: 'SET_LANGUAGE_ID',
								})
							}
							onReloadPreview={() =>
								elementVariationsPreviewRef.current?.reload()
							}
							onSave={() =>
								ElementVariationService.addElementVariation({
									addElementVariationURL,
									elementVariation: draftElementVariation,
									plid,
								}).then(() =>
									dispatch({
										type: 'SAVE_ELEMENT_VARIATION_DRAFT',
									})
								)
							}
						/>
					) : (
						<>
							<div className="border-bottom flex-shrink-0 px-3 py-3">
								<span className="font-weight-bold">
									{Liferay.Language.get('element-variations')}
								</span>
							</div>

							<div className="flex-grow-1">
								<div className="form-group p-3">
									<label htmlFor={experienceId}>
										{Liferay.Language.get('experience')}
									</label>

									<Picker
										aria-label={Liferay.Language.get(
											'experience'
										)}
										className="form-control-sm"
										id={experienceId}
										items={experiences}
										onSelectionChange={(selection) =>
											setExperienceKey(String(selection))
										}
										selectedKey={experienceKey}
									>
										{(item) => (
											<Option
												key={item.segmentsExperienceERC}
											>
												{item.label}
											</Option>
										)}
									</Picker>
								</div>

								<AudiencePriority audiences={audiences} />

								{experienceElementVariations.length ? (
									<ElementVariationsList
										audiences={audiences}
										elementVariations={
											experienceElementVariations
										}
										onDeleteElementVariation={(
											elementVariation
										) =>
											ElementVariationService.deleteElementVariation(
												{
													deleteElementVariationURL,
													externalReferenceCode:
														elementVariation.externalReferenceCode,
													plid,
												}
											).then(() =>
												dispatch({
													key: elementVariation.key,
													type: 'DELETE_ELEMENT_VARIATION',
												})
											)
										}
										onEditElementVariation={(key) =>
											dispatch({
												key,
												type: 'EDIT_ELEMENT_VARIATION',
											})
										}
									/>
								) : (
									<ClayEmptyState
										className="mb-0 px-3"
										description={Liferay.Language.get(
											'you-can-create-page-elements-variations-based-on-audiences'
										)}
										imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/empty_state.svg`}
										small
										title={Liferay.Language.get(
											'no-variations-yet'
										)}
									/>
								)}

								<div className="d-flex justify-content-center mt-2">
									<ClayButton
										displayType="secondary"
										onClick={() =>
											dispatch({
												draftElementVariation:
													createElementVariation(
														experienceKey
													),
												type: 'CREATE_ELEMENT_VARIATION_DRAFT',
											})
										}
										size="sm"
									>
										{Liferay.Language.get('new-variation')}
									</ClayButton>
								</div>
							</div>
						</>
					)}
				</div>

				<ElementVariationsPreview
					defaultLanguageId={defaultLanguageId}
					draftElementVariation={draftElementVariation}
					languageId={languageId}
					previewURL={previewURL}
					ref={elementVariationsPreviewRef}
				/>
			</div>
		</div>
	);
}
