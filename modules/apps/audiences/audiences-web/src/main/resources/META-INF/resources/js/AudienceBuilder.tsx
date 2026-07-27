/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayToolbar from '@clayui/toolbar';
import {ScreenReaderAnnouncerContextProvider} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {fetch, navigate} from 'frontend-js-web';
import React, {useEffect, useMemo, useReducer, useRef, useState} from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import AttributesSidebar from './components/AttributesSidebar';
import ConditionsPanel from './components/ConditionsPanel';
import GeneralSettings from './components/GeneralSettings';
import DragPreviewWrapper from './keyboard_movement/DragPreviewWrapper';
import {KeyboardMovementContextProvider} from './keyboard_movement/KeyboardMovementContext';
import {initState, reducer} from './reducer';
import {
	AudiencesCriteriaRulesGroup,
	AudiencesCriteriaType,
	SaveErrorField,
	SaveErrors,
} from './types';
import {getAudiencesCriteriasByKey} from './util/getAudiencesCriteriasByKey';
import {serializeCriteria} from './util/tree/serializeCriteria';

import './AudienceBuilder.scss';

const NAME_MAX_LENGTH = 75;

const DragAndDropProvider = DndProvider as unknown as React.FC<
	React.PropsWithChildren<{backend: typeof HTML5Backend}>
>;

const SAVE_ERROR_FIELDS: SaveErrorField[] = ['name', 'externalReferenceCode'];

function getSaveErrors({
	externalReferenceCode,
	name,
}: {
	externalReferenceCode: string;
	name: string;
}): SaveErrors {
	const saveErrors: SaveErrors = {};

	if (!name.trim()) {
		saveErrors.name = Liferay.Language.get('please-enter-a-valid-name');
	}

	if (!externalReferenceCode.trim()) {
		saveErrors.externalReferenceCode = Liferay.Language.get(
			'this-field-is-required'
		);
	}

	return saveErrors;
}

function getServerSaveErrors(error: Record<string, string>): SaveErrors {
	return SAVE_ERROR_FIELDS.reduce<SaveErrors>((saveErrors, field) => {
		if (error[field]) {
			saveErrors[field] = error[field];
		}

		return saveErrors;
	}, {});
}

function showSaveErrorToast(message?: string) {
	Liferay.Util.openToast({
		message:
			message || Liferay.Language.get('an-unexpected-error-occurred'),
		type: 'danger',
	});
}

interface IProps {
	audiencesCriteriaTypes?: AudiencesCriteriaType[];
	audiencesEntryId?: number;
	backURL?: string;
	backURLTitle?: string;
	externalReferenceCode?: string;
	name?: string;
	namespace?: string;
	redirect?: string;
	rulesGroup?: AudiencesCriteriaRulesGroup;
	updateAudiencesEntryActionURL?: string;
}

export default function AudienceBuilder({
	audiencesCriteriaTypes = [],
	audiencesEntryId = 0,
	backURL,
	backURLTitle,
	externalReferenceCode,
	name,
	namespace = '',
	redirect = '',
	rulesGroup,
	updateAudiencesEntryActionURL = '',
}: IProps) {
	const [state, dispatch] = useReducer(
		reducer,
		{externalReferenceCode, name, rulesGroup},
		initState
	);

	const audiencesCriteriasByKey = useMemo(
		() => getAudiencesCriteriasByKey(audiencesCriteriaTypes),
		[audiencesCriteriaTypes]
	);

	const [generalSettingsExpanded, setGeneralSettingsExpanded] =
		useState(false);
	const [saveErrors, setSaveErrors] = useState<SaveErrors>({});
	const [saving, setSaving] = useState(false);

	const externalReferenceCodeInputRef = useRef<HTMLInputElement>(null);
	const nameInputRef = useRef<HTMLInputElement>(null);

	useEffect(() => {
		if (saveErrors.name) {
			nameInputRef.current?.focus();
		}
		else if (saveErrors.externalReferenceCode) {
			externalReferenceCodeInputRef.current?.focus();
		}
	}, [saveErrors]);

	const showSaveErrors = (nextSaveErrors: SaveErrors) => {
		setSaveErrors(nextSaveErrors);

		if (nextSaveErrors.externalReferenceCode) {
			setGeneralSettingsExpanded(true);
		}
	};

	const clearSaveError = (field: SaveErrorField) =>
		setSaveErrors((previousSaveErrors) => {
			if (!previousSaveErrors[field]) {
				return previousSaveErrors;
			}

			const nextSaveErrors = {...previousSaveErrors};

			delete nextSaveErrors[field];

			return nextSaveErrors;
		});

	const handleSave = () => {
		const nextSaveErrors = getSaveErrors(state);

		if (Object.keys(nextSaveErrors).length) {
			showSaveErrors(nextSaveErrors);

			return;
		}

		setSaveErrors({});
		setSaving(true);

		const formData = new FormData();

		formData.append(
			`${namespace}audiencesEntryId`,
			String(audiencesEntryId)
		);
		formData.append(
			`${namespace}externalReferenceCode`,
			state.externalReferenceCode
		);
		formData.append(
			`${namespace}json`,
			serializeCriteria(state.root, audiencesCriteriasByKey)
		);
		formData.append(`${namespace}name`, state.name);

		fetch(updateAudiencesEntryActionURL, {
			body: formData,
			method: 'POST',
		})
			.then((response) => response.json())
			.then(({error}) => {
				if (!error) {
					navigate(redirect);

					return;
				}

				setSaving(false);

				const serverSaveErrors = getServerSaveErrors(error);

				if (Object.keys(serverSaveErrors).length) {
					showSaveErrors(serverSaveErrors);
				}
				else {
					showSaveErrorToast(error.other);
				}
			})
			.catch(() => {
				setSaving(false);

				showSaveErrorToast();
			});
	};

	return (
		<ScreenReaderAnnouncerContextProvider>
			<KeyboardMovementContextProvider>
				<DragAndDropProvider backend={HTML5Backend}>
					<DragPreviewWrapper />

					<div className="d-flex flex-column overflow-hidden">
						<AudienceBuilderToolbar
							backURL={backURL}
							backURLTitle={backURLTitle}
							name={state.name}
							onSave={handleSave}
							saving={saving}
						/>

						<div className="audience-builder-content d-flex">
							<div className="audience-builder-sidebar border-right d-flex flex-column flex-shrink-0 px-4">
								<AttributesSidebar
									audiencesCriteriaTypes={
										audiencesCriteriaTypes
									}
								/>
							</div>

							<div className="d-flex flex-column flex-grow-1 overflow-auto p-4">
								<ClayForm.Group
									className={classNames({
										'has-error': !!saveErrors.name,
									})}
								>
									<label
										className="font-weight-semi-bold text-3"
										htmlFor={`${namespace}name`}
									>
										{Liferay.Language.get('name')}

										<span className="reference-mark">
											<ClayIcon symbol="asterisk" />
										</span>
									</label>

									<ClayInput
										aria-describedby={
											saveErrors.name &&
											`${namespace}nameError`
										}
										aria-invalid={!!saveErrors.name}
										aria-required
										className="bg-white border-0 font-weight-semi-bold h-auto mb-0 p-0 text-8"
										id={`${namespace}name`}
										maxLength={NAME_MAX_LENGTH}
										name={`${namespace}name`}
										onChange={(event) => {
											clearSaveError('name');

											dispatch({
												name: event.target.value,
												type: 'SET_NAME',
											});
										}}
										placeholder={Liferay.Language.get(
											'new-audience'
										)}
										ref={nameInputRef}
										type="text"
										value={state.name}
									/>

									{saveErrors.name && (
										<ClayForm.FeedbackGroup role="alert">
											<ClayForm.FeedbackItem
												id={`${namespace}nameError`}
											>
												<ClayForm.FeedbackIndicator symbol="exclamation-full" />

												{saveErrors.name}
											</ClayForm.FeedbackItem>
										</ClayForm.FeedbackGroup>
									)}
								</ClayForm.Group>

								<GeneralSettings
									errorMessage={
										saveErrors.externalReferenceCode
									}
									expanded={generalSettingsExpanded}
									externalReferenceCode={
										state.externalReferenceCode
									}
									externalReferenceCodeInputRef={
										externalReferenceCodeInputRef
									}
									namespace={namespace}
									onExpandedChange={
										setGeneralSettingsExpanded
									}
									onExternalReferenceCodeChange={(
										newExternalReferenceCode
									) => {
										clearSaveError('externalReferenceCode');

										dispatch({
											externalReferenceCode:
												newExternalReferenceCode,
											type: 'SET_EXTERNAL_REFERENCE_CODE',
										});
									}}
								/>

								<ConditionsPanel
									audiencesCriteriaTypes={
										audiencesCriteriaTypes
									}
									dispatch={dispatch}
									root={state.root}
								/>
							</div>
						</div>
					</div>
				</DragAndDropProvider>
			</KeyboardMovementContextProvider>
		</ScreenReaderAnnouncerContextProvider>
	);
}

interface AudienceBuilderToolbarProps {
	backURL?: string;
	backURLTitle?: string;
	name: string;
	onSave: () => void;
	saving: boolean;
}

function AudienceBuilderToolbar({
	backURL,
	backURLTitle,
	name,
	onSave,
	saving,
}: AudienceBuilderToolbarProps) {
	return (
		<ClayToolbar>
			<ClayLayout.ContainerFluid size={false}>
				<ClayToolbar.Nav>
					<ClayToolbar.Item>
						<ClayLink
							aria-label={Liferay.Language.get('back')}
							button
							displayType="unstyled"
							href={backURL}
							monospaced
							title={backURLTitle}
						>
							<ClayIcon symbol="angle-left" />
						</ClayLink>
					</ClayToolbar.Item>

					<ClayToolbar.Item expand>
						<ClayToolbar.Section className="text-left">
							<span className="font-weight-bold text-dark text-truncate">
								{name || Liferay.Language.get('new-audience')}
							</span>
						</ClayToolbar.Section>
					</ClayToolbar.Item>

					<ClayToolbar.Item>
						<ClayLink
							button
							displayType="secondary"
							href={backURL}
							small
						>
							{Liferay.Language.get('cancel')}
						</ClayLink>
					</ClayToolbar.Item>

					<ClayToolbar.Item>
						<ClayButton
							disabled={saving}
							displayType="primary"
							onClick={onSave}
							size="sm"
							type="button"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayToolbar.Item>
				</ClayToolbar.Nav>
			</ClayLayout.ContainerFluid>
		</ClayToolbar>
	);
}
