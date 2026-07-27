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
import React, {useMemo, useReducer, useRef, useState} from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import AttributesSidebar from './components/AttributesSidebar';
import ConditionsPanel from './components/ConditionsPanel';
import GeneralSettings from './components/GeneralSettings';
import DragPreviewWrapper from './keyboard_movement/DragPreviewWrapper';
import {KeyboardMovementContextProvider} from './keyboard_movement/KeyboardMovementContext';
import {initState, reducer} from './reducer';
import {AudiencesCriteriaRulesGroup, AudiencesCriteriaType} from './types';
import {getAudiencesCriteriasByKey} from './util/getAudiencesCriteriasByKey';
import {serializeCriteria} from './util/tree/serializeCriteria';

import './AudienceBuilder.scss';

const NAME_MAX_LENGTH = 75;

const DragAndDropProvider = DndProvider as unknown as React.FC<
	React.PropsWithChildren<{backend: typeof HTML5Backend}>
>;

function showSaveErrorToast(message?: string) {
	Liferay.Util.openToast({
		message:
			message || Liferay.Language.get('an-unexpected-error-occurred'),
		type: 'danger',
	});
}

interface SaveError {
	field?: string;
	message: string;
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

	const formRef = useRef<HTMLFormElement>(null);

	const [saveError, setSaveError] = useState<SaveError | null>(null);
	const [saving, setSaving] = useState(false);

	const handleSave = () => {
		const form = formRef.current;

		if (!form || !form.reportValidity()) {
			return;
		}

		setSaveError(null);
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
			.then((response) => {
				if (response.ok) {
					navigate(redirect);

					return;
				}

				return response.json().then(({errorField, errorMessage}) => {
					setSaving(false);

					if (errorField) {
						setSaveError({
							field: errorField,
							message: errorMessage,
						});
					}
					else {
						showSaveErrorToast(errorMessage);
					}
				});
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

					<form
						className="d-flex flex-column overflow-hidden"
						onSubmit={(event) => event.preventDefault()}
						ref={formRef}
					>
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
										'has-error':
											saveError?.field === 'name',
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
										className="bg-white border-0 font-weight-semi-bold h-auto mb-0 p-0 text-8"
										id={`${namespace}name`}
										maxLength={NAME_MAX_LENGTH}
										name={`${namespace}name`}
										onChange={(event) => {
											if (saveError?.field === 'name') {
												setSaveError(null);
											}

											dispatch({
												name: event.target.value,
												type: 'SET_NAME',
											});
										}}
										placeholder={Liferay.Language.get(
											'new-audience'
										)}
										required
										type="text"
										value={state.name}
									/>

									{saveError?.field === 'name' && (
										<ClayForm.FeedbackGroup>
											<ClayForm.FeedbackItem>
												<ClayForm.FeedbackIndicator symbol="exclamation-full" />

												{saveError.message}
											</ClayForm.FeedbackItem>
										</ClayForm.FeedbackGroup>
									)}
								</ClayForm.Group>

								<GeneralSettings
									errorMessage={
										saveError?.field ===
										'externalReferenceCode'
											? saveError.message
											: undefined
									}
									externalReferenceCode={
										state.externalReferenceCode
									}
									namespace={namespace}
									onExternalReferenceCodeChange={(
										newExternalReferenceCode
									) => {
										if (
											saveError?.field ===
											'externalReferenceCode'
										) {
											setSaveError(null);
										}

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
					</form>
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
