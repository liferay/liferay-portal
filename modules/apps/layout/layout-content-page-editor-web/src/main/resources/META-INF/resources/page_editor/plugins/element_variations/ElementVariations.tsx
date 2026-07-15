/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import {useId} from 'frontend-js-components-web';
import React, {useReducer, useRef} from 'react';

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
		audienceEntryERCs: Array<string>;
		label: string;
		segmentsExperienceERC: string;
		segmentsExperienceId: number;
	}>;
	itemNames: Record<string, string>;
	locales: Array<{id: string; label: string; symbol: string}>;
	plid: number;
	portletNamespace: string;
	previewURL: string;
	selectedSegmentsExperienceId: number;
	updateAudiencesPriorityURL: string;
}

export default function (props: Props) {
	initializeConfig({portletNamespace: props.portletNamespace} as Config);

	return <ElementVariations {...props} />;
}

function getOrderedAudiences(
	audiences: Array<{label: string; value: string}>,
	audienceEntryERCs: Array<string>
): Array<{label: string; value: string}> {
	const explicitAudiences = audienceEntryERCs
		.map((audienceEntryERC) =>
			audiences.find(({value}) => value === audienceEntryERC)
		)
		.filter(
			(audience): audience is {label: string; value: string} =>
				audience !== undefined
		);

	const remainingAudiences = audiences.filter(
		({value}) => !audienceEntryERCs.includes(value)
	);

	return [...explicitAudiences, ...remainingAudiences];
}

function ElementVariations({
	addElementVariationURL,
	audiences = [],
	defaultLanguageId,
	deleteElementVariationURL,
	elementVariations: initialElementVariations = [],
	experiences = [],
	itemNames,
	locales,
	plid,
	previewURL,
	selectedSegmentsExperienceId,
	updateAudiencesPriorityURL,
}: Props) {
	const experienceId = useId();

	const [
		{
			draftElementVariation,
			editableElementOptions,
			elementVariations,
			experienceKey,
			highlightedTargetElement,
			languageId,
		},
		dispatch,
	] = useReducer(
		reducer,
		{
			defaultLanguageId,
			elementVariations: initialElementVariations,
			experiences,
			selectedSegmentsExperienceId,
		},
		createInitialState
	);

	const experienceElementVariations = elementVariations.filter(
		(elementVariation) =>
			elementVariation.segmentsExperienceERC === experienceKey
	);

	const selectedExperience = experiences.find(
		(experience) => experience.segmentsExperienceERC === experienceKey
	);

	const elementVariationsPreviewRef =
		useRef<ElementVariationsPreviewRef>(null);

	return (
		<div className="d-flex element-variations flex-column">
			<div className="d-flex element-variations__content flex-grow-1">
				<div className="bg-white d-flex element-variations__sidebar flex-column flex-shrink-0">
					{draftElementVariation ? (
						<ElementVariationForm
							audiences={audiences}
							defaultLanguageId={defaultLanguageId}
							dispatch={dispatch}
							editableElementOptions={editableElementOptions}
							elementVariation={draftElementVariation}
							elementVariations={experienceElementVariations}
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
								<div className="p-3">
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
											dispatch({
												experienceKey:
													String(selection),
												type: 'SET_EXPERIENCE_KEY',
											})
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

								<AudiencePriority
									audiences={getOrderedAudiences(
										audiences,
										selectedExperience?.audienceEntryERCs ??
											[]
									)}
									key={experienceKey}
									segmentsExperienceERC={experienceKey}
									updateAudiencesPriorityURL={
										updateAudiencesPriorityURL
									}
								/>

								<div className="d-flex justify-content-start m-3">
									<ClayButton
										className="w-100"
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
									>
										<ClayIcon
											className="mr-2"
											symbol="plus"
										/>

										{Liferay.Language.get('new-variation')}
									</ClayButton>
								</div>

								<div className="border-top pt-3">
									{experienceElementVariations.length ? (
										<ElementVariationsList
											audiences={audiences}
											editableElementOptions={
												editableElementOptions
											}
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
								</div>
							</div>
						</>
					)}
				</div>

				<ElementVariationsPreview
					defaultLanguageId={defaultLanguageId}
					dispatch={dispatch}
					draftElementVariation={draftElementVariation}
					highlightedTargetElement={highlightedTargetElement}
					itemNames={itemNames}
					languageId={languageId}
					previewURL={previewURL}
					ref={elementVariationsPreviewRef}
				/>
			</div>
		</div>
	);
}
