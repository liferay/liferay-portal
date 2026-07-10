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
import React, {useReducer} from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import AttributesSidebar from './components/AttributesSidebar';
import ConditionsPanel from './components/ConditionsPanel';
import GeneralSettings from './components/GeneralSettings';
import DragPreviewWrapper from './keyboard_movement/DragPreviewWrapper';
import {KeyboardMovementContextProvider} from './keyboard_movement/KeyboardMovementContext';
import {initState, reducer, serializeCriteria} from './reducer';
import {AudiencesCriteriaRulesGroup, AudiencesCriteriaType} from './types';

import './AudienceBuilder.scss';

const NAME_MAX_LENGTH = 75;

const DragAndDropProvider = DndProvider as unknown as React.FC<
	React.PropsWithChildren<{backend: typeof HTML5Backend}>
>;

interface IProps {
	audiencesCriteriaTypes?: AudiencesCriteriaType[];
	backURL?: string;
	backURLTitle?: string;
	externalReferenceCode?: string;
	name?: string;
	namespace?: string;
	rulesGroup?: AudiencesCriteriaRulesGroup;
}

export default function AudienceBuilder({
	audiencesCriteriaTypes = [],
	backURL,
	backURLTitle,
	externalReferenceCode,
	name,
	namespace = '',
	rulesGroup,
}: IProps) {
	const [state, dispatch] = useReducer(
		reducer,
		{externalReferenceCode, name, rulesGroup},
		initState
	);

	return (
		<ScreenReaderAnnouncerContextProvider>
			<KeyboardMovementContextProvider>
				<DragAndDropProvider backend={HTML5Backend}>
					<DragPreviewWrapper />

					<div className="d-flex flex-column overflow-hidden">
						<ClayToolbar>
							<ClayLayout.ContainerFluid size={false}>
								<ClayToolbar.Nav>
									<ClayToolbar.Item>
										<ClayLink
											aria-label={Liferay.Language.get(
												'back'
											)}
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
												{state.name ||
													Liferay.Language.get(
														'new-audience'
													)}
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
											displayType="primary"
											size="sm"
											type="submit"
										>
											{Liferay.Language.get('save')}
										</ClayButton>
									</ClayToolbar.Item>
								</ClayToolbar.Nav>
							</ClayLayout.ContainerFluid>
						</ClayToolbar>

						<div className="audience-builder-content d-flex">
							<div className="audience-builder-sidebar border-right d-flex flex-column flex-shrink-0 px-4">
								<AttributesSidebar
									audiencesCriteriaTypes={
										audiencesCriteriaTypes
									}
								/>
							</div>

							<div className="d-flex flex-column flex-grow-1 overflow-auto p-4">
								<ClayForm.Group>
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
										onChange={(event) =>
											dispatch({
												name: event.target.value,
												type: 'SET_NAME',
											})
										}
										placeholder={Liferay.Language.get(
											'new-audience'
										)}
										required
										type="text"
										value={state.name}
									/>
								</ClayForm.Group>

								<GeneralSettings
									externalReferenceCode={
										state.externalReferenceCode
									}
									namespace={namespace}
									onExternalReferenceCodeChange={(
										newExternalReferenceCode
									) =>
										dispatch({
											externalReferenceCode:
												newExternalReferenceCode,
											type: 'SET_EXTERNAL_REFERENCE_CODE',
										})
									}
								/>

								<input
									name={`${namespace}externalReferenceCode`}
									type="hidden"
									value={state.externalReferenceCode}
								/>

								<input
									name={`${namespace}json`}
									type="hidden"
									value={serializeCriteria(state)}
								/>

								<ConditionsPanel
									audiencesCriteriaTypes={
										audiencesCriteriaTypes
									}
									conjunction={state.conjunction}
									dispatch={dispatch}
									rules={state.rules}
								/>
							</div>
						</div>
					</div>
				</DragAndDropProvider>
			</KeyboardMovementContextProvider>
		</ScreenReaderAnnouncerContextProvider>
	);
}
