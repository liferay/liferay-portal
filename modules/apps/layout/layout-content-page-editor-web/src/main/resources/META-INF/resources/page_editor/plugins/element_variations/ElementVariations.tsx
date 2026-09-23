/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {Option, Picker, SidePanel} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import {
	hideProductMenuIfPresent,
	useMediaQuery,
} from '@liferay/layout-js-components-web';
import {openToast, useId} from 'frontend-js-components-web';
import React, {useEffect, useReducer, useRef, useState} from 'react';

import {initializeConfig} from '../../app/config/index';
import {Config} from '../../types/config';
import AppliedFilters from './AppliedFilters';
import AudiencePriority from './AudiencePriority';
import ElementVariationFilterMenu from './ElementVariationFilterMenu';
import ElementVariationForm from './ElementVariationForm';
import ElementVariationSearch from './ElementVariationSearch';
import ElementVariationService from './ElementVariationService';
import ElementVariationsList from './ElementVariationsList';
import ElementVariationsPreview, {
	ElementVariationsPreviewRef,
} from './ElementVariationsPreview';
import {
	Filter,
	NO_AUDIENCE_VALUE,
	getFilteredVariations,
} from './elementVariationFilters';
import {
	LoadedElementVariation,
	createElementVariation,
	createInitialState,
	reducer,
} from './elementVariationsReducer';

import './ElementVariations.scss';

const LARGE_MEDIA_QUERY = '(min-width: 992px)';

const SIDEBAR_WIDTH = 320;

interface Props {
	addElementVariationURL: string;
	audiences: Array<{label: string; value: string}>;
	createAudienceURL: string;
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
	updateElementVariationURL: string;
}

export default function (props: Props) {
	initializeConfig({portletNamespace: props.portletNamespace} as Config);

	return <ElementVariations {...props} />;
}

function showErrorToast() {
	openToast({
		message: Liferay.Language.get('an-unexpected-error-occurred'),
		type: 'danger',
	});
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
	createAudienceURL,
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
	updateElementVariationURL,
}: Props) {
	const experienceId = useId();

	const [
		{
			draftElementVariation,
			editableElementOptions,
			elementVariations,
			experienceKey,
			filters,
			highlightedTargetElement,
			languageId,
			searchTerm,
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

	const filteredElementVariations = getFilteredVariations({
		audiences,
		editableElementOptions: editableElementOptions ?? [],
		elementVariations: experienceElementVariations,
		filters,
		searchTerm,
	});

	const selectedExperience = experiences.find(
		(experience) => experience.segmentsExperienceERC === experienceKey
	);

	const elementVariationsPreviewRef =
		useRef<ElementVariationsPreviewRef>(null);

	const wrapperRef = useRef<HTMLElement | null>(
		document.getElementById('wrapper')
	);

	const [missingAudiencesAlertVisible, setMissingAudiencesAlertVisible] =
		useState(true);

	const [sidebarOpen, setSidebarOpen] = useState(false);

	const screenLarge = useMediaQuery(LARGE_MEDIA_QUERY);

	const open = sidebarOpen || screenLarge;

	useEffect(() => {
		hideProductMenuIfPresent({onHide: () => setSidebarOpen(true)});
	}, []);

	const createElementVariationDraft = () =>
		dispatch({
			draftElementVariation: createElementVariation(experienceKey),
			type: 'CREATE_ELEMENT_VARIATION_DRAFT',
		});

	return (
		<div className="d-flex element-variations flex-column">
			<SidePanel
				aria-label={Liferay.Language.get('element-variations')}
				className="bg-white element-variations__sidebar overflow-hidden shadow-none"
				closeOnEscape={!screenLarge}
				containerRef={wrapperRef}
				direction="left"
				displayType="light"
				onOpenChange={setSidebarOpen}
				open={open}
				panelWidth={SIDEBAR_WIDTH}
				position="fixed"
			>
				{draftElementVariation ? (
					<ElementVariationForm
						audiences={audiences}
						defaultLanguageId={defaultLanguageId}
						dispatch={dispatch}
						editableElementOptions={editableElementOptions ?? []}
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
							})
								.then(() =>
									dispatch({
										type: 'SAVE_ELEMENT_VARIATION_DRAFT',
									})
								)
								.catch(showErrorToast)
						}
					/>
				) : (
					<>
						<SidePanel.Header
							className="border-bottom flex-shrink-0 px-3 py-3"
							closeButtonProps={{className: 'd-lg-none'}}
							messages={{
								closeAriaLabel: Liferay.Language.get('close'),
							}}
						>
							<span className="font-weight-bold">
								{Liferay.Language.get('element-variations')}
							</span>
						</SidePanel.Header>

						<SidePanel.Body className="flex-grow-1 overflow-auto p-0">
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
											experienceKey: String(selection),
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
									selectedExperience?.audienceEntryERCs ?? []
								)}
								key={experienceKey}
								segmentsExperienceERC={experienceKey}
								updateAudiencesPriorityURL={
									updateAudiencesPriorityURL
								}
							/>

							<Toolbar
								audiences={audiences}
								filters={filters}
								onAddFilter={(filter) =>
									dispatch({filter, type: 'ADD_FILTER'})
								}
								onCreate={createElementVariationDraft}
								onSearch={(searchTerm) =>
									dispatch({
										searchTerm,
										type: 'SET_SEARCH_TERM',
									})
								}
								searchTerm={searchTerm}
							/>

							{missingAudiencesAlertVisible &&
							experienceElementVariations.some(
								(elementVariation) =>
									!elementVariation.audienceEntryERCs.length
							) ? (
								<MissingAudiencesAlert
									onClose={() =>
										setMissingAudiencesAlertVisible(false)
									}
									onShowVariations={() => {
										dispatch({
											filter: {
												exclude: false,
												type: 'audience',
												values: [NO_AUDIENCE_VALUE],
											},
											type: 'ADD_FILTER',
										});

										setMissingAudiencesAlertVisible(false);
									}}
								/>
							) : null}

							{Boolean(filters.length) || searchTerm ? (
								<AppliedFilters
									audiences={audiences}
									filters={filters}
									onAddFilter={(filter) =>
										dispatch({filter, type: 'ADD_FILTER'})
									}
									onClearFilters={() =>
										dispatch({type: 'CLEAR_FILTERS'})
									}
									onClearSearch={() =>
										dispatch({
											searchTerm: '',
											type: 'SET_SEARCH_TERM',
										})
									}
									onDeleteFilter={(filterType) =>
										dispatch({
											filterType,
											type: 'DELETE_FILTER',
										})
									}
									resultsCount={
										filteredElementVariations.length
									}
									searchTerm={searchTerm}
								/>
							) : null}

							<div className="pt-3">
								{!audiences.length &&
								!experienceElementVariations.length ? (
									<NoAudiencesState
										createAudienceURL={createAudienceURL}
									/>
								) : (Boolean(filters.length) || searchTerm) &&
								  !filteredElementVariations.length ? (
									<NoResultsState
										onClearFilters={() =>
											dispatch({type: 'CLEAR_FILTERS'})
										}
									/>
								) : experienceElementVariations.length ? (
									<ElementVariationsList
										audiences={audiences}
										editableElementOptions={
											editableElementOptions
										}
										elementVariations={
											filteredElementVariations
										}
										onDeleteElementVariation={(
											elementVariation
										) =>
											ElementVariationService.deleteElementVariation(
												{
													deleteElementVariationURL,
													externalReferenceCode:
														elementVariation.externalReferenceCode,
												}
											)
												.then(() =>
													dispatch({
														key: elementVariation.key,
														type: 'DELETE_ELEMENT_VARIATION',
													})
												)
												.catch(showErrorToast)
										}
										onEditElementVariation={(key) =>
											dispatch({
												key,
												type: 'EDIT_ELEMENT_VARIATION',
											})
										}
										onUpdateElementVariation={(
											elementVariation
										) =>
											ElementVariationService.updateElementVariation(
												{
													active: !elementVariation.active,
													externalReferenceCode:
														elementVariation.externalReferenceCode,
													updateElementVariationURL,
												}
											)
												.then(() =>
													dispatch({
														active: !elementVariation.active,
														key: elementVariation.key,
														type: 'UPDATE_ELEMENT_VARIATION',
													})
												)
												.catch(showErrorToast)
										}
									/>
								) : (
									<NoVariationsState
										onCreate={createElementVariationDraft}
									/>
								)}
							</div>
						</SidePanel.Body>
					</>
				)}
			</SidePanel>

			{open ? null : (
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get(
						'open-element-variations-panel'
					)}
					className="element-variations__sidebar-trigger position-fixed shadow-sm"
					displayType="secondary"
					onClick={() => setSidebarOpen(true)}
					symbol="angle-double-right"
					title={Liferay.Language.get(
						'open-element-variations-panel'
					)}
				/>
			)}

			<ElementVariationsPreview
				defaultLanguageId={defaultLanguageId}
				dispatch={dispatch}
				draftElementVariation={draftElementVariation}
				highlightedTargetElement={highlightedTargetElement}
				itemNames={itemNames}
				languageId={languageId}
				previewURL={previewURL}
				ref={elementVariationsPreviewRef}
				segmentsExperienceId={
					selectedExperience?.segmentsExperienceId ??
					selectedSegmentsExperienceId
				}
			/>
		</div>
	);
}

interface ToolbarProps {
	audiences: Array<{label: string; value: string}>;
	filters: Filter[];
	onAddFilter: (filter: Filter) => void;
	onCreate: () => void;
	onSearch: (searchTerm: string) => void;
	searchTerm: string;
}

function Toolbar({
	audiences,
	filters,
	onAddFilter,
	onCreate,
	onSearch,
	searchTerm,
}: ToolbarProps) {
	const [open, setOpen] = useState(false);

	if (open) {
		return (
			<div className="align-items-center border-bottom border-top d-flex justify-content-between px-3 py-2">
				<ElementVariationSearch
					onClose={() => setOpen(false)}
					onSearch={onSearch}
					searchTerm={searchTerm}
				/>
			</div>
		);
	}

	return (
		<div className="align-items-center border-bottom border-top d-flex justify-content-between px-3 py-2">
			<ElementVariationFilterMenu
				audiences={audiences}
				filters={filters}
				onAddFilter={onAddFilter}
				trigger={
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('filter')}
						borderless
						displayType="secondary"
						size="sm"
						symbol="filter"
						title={Liferay.Language.get('filter')}
					/>
				}
			/>

			<div className="align-items-center d-flex">
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('search')}
					borderless
					className="mr-2"
					displayType="secondary"
					onClick={() => setOpen(true)}
					size="sm"
					symbol="search"
					title={Liferay.Language.get('search')}
				/>

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('new-variation')}
					displayType="primary"
					onClick={onCreate}
					size="sm"
					symbol="plus"
					title={Liferay.Language.get('new-variation')}
				/>
			</div>
		</div>
	);
}

interface MissingAudiencesAlertProps {
	onClose: () => void;
	onShowVariations: () => void;
}

function MissingAudiencesAlert({
	onClose,
	onShowVariations,
}: MissingAudiencesAlertProps) {
	return (
		<ClayAlert
			closeButtonAriaLabel={Liferay.Language.get('close')}
			displayType="warning"
			onClose={onClose}
			title={`${Liferay.Language.get('warning')}:`}
			variant="stripe"
		>
			{Liferay.Language.get(
				'there-are-missing-audiences-for-some-variations'
			)}

			<ClayAlert.Footer>
				<ClayButton
					displayType="warning"
					onClick={onShowVariations}
					small
				>
					{Liferay.Language.get('show-variations')}
				</ClayButton>
			</ClayAlert.Footer>
		</ClayAlert>
	);
}

function NoAudiencesState({createAudienceURL}: {createAudienceURL: string}) {
	return (
		<ClayEmptyState
			className="mb-0 px-3"
			description={Liferay.Language.get(
				'you-need-at-least-one-audience-to-build-element-variations'
			)}
			imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/empty_state.svg`}
			small
			title={Liferay.Language.get('no-element-variations')}
		>
			<ClayLink
				button
				displayType="secondary"
				href={createAudienceURL}
				small
				target="_blank"
			>
				<ClayIcon className="mr-2" symbol="shortcut" />

				{Liferay.Language.get('create-new-audience')}
			</ClayLink>
		</ClayEmptyState>
	);
}

function NoResultsState({onClearFilters}: {onClearFilters: () => void}) {
	return (
		<ClayEmptyState
			className="mb-0 px-3"
			description={Liferay.Language.get(
				'review-your-filters-and-try-again'
			)}
			imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/search_state.svg`}
			small
			title={Liferay.Language.get('no-results-found')}
		>
			<ClayButton
				displayType="secondary"
				onClick={onClearFilters}
				size="sm"
			>
				{Liferay.Language.get('clear-filters')}
			</ClayButton>
		</ClayEmptyState>
	);
}

function NoVariationsState({onCreate}: {onCreate: () => void}) {
	return (
		<ClayEmptyState
			className="mb-0 px-3"
			description={Liferay.Language.get(
				'you-can-create-page-elements-variations-based-on-audiences'
			)}
			imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/empty_state.svg`}
			small
			title={Liferay.Language.get('no-element-variations')}
		>
			<ClayButton displayType="secondary" onClick={onCreate} size="sm">
				{Liferay.Language.get('new')}
			</ClayButton>
		</ClayEmptyState>
	);
}
