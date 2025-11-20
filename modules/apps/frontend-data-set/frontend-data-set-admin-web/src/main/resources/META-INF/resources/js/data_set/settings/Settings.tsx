/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker, Text} from '@clayui/core';
import DropDown from '@clayui/drop-down';
import {ClayToggle} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {fetch, navigate} from 'frontend-js-web';
import React, {useCallback, useEffect, useState} from 'react';

import {
	DEFAULT_FETCH_HEADERS,
	DEFAULT_VISUALIZATION_MODES,
	OBJECT_RELATIONSHIP,
} from '../../utils/constants';
import getDataSetResourceURL from '../../utils/getDataSetResourceURL';
import openDefaultFailureToast from '../../utils/openDefaultFailureToast';
import openDefaultSuccessToast from '../../utils/openDefaultSuccessToast';
import {TVisualizationMode} from '../../utils/types';
import {IDataSetSectionProps} from '../DataSet';

const NOT_CONFIGURED_VISUALIZATION_MODE = {
	label: Liferay.Language.get('go-to-visualization-modes'),
	thumbnail: 'plus',
	type: Liferay.Language.get('not-configured'),
};

const Settings = ({
	backURL,
	dataSet,
	onActiveSectionChange,
	onDataSetUpdate,
	spritemap,
}: IDataSetSectionProps) => {
	const [snapshotsEnabled, setSnapshotsEnabled] = useState<boolean>(
		dataSet.snapshotsEnabled
	);
	const [defaultVisualizationMode, setDefaultVisualizationMode] = useState<
		string | undefined
	>(NOT_CONFIGURED_VISUALIZATION_MODE.type);
	const [hideManagementBarInEmptyState, setHideManagementBarInEmptyState] =
		useState(dataSet.hideManagementBarInEmptyState ?? true);
	const [loading, setLoading] = useState(true);
	const [visualizationModes, setVisualizationModes] = useState<
		Array<TVisualizationMode>
	>([]);

	const handleToggleChange = useCallback(
		() => setHideManagementBarInEmptyState(!hideManagementBarInEmptyState),
		[hideManagementBarInEmptyState]
	);

	const updateFDSViewSettings = async () => {
		const body = {
			defaultVisualizationMode,
			hideManagementBarInEmptyState,
			snapshotsEnabled,
		};

		const url = getDataSetResourceURL({
			dataSetERC: dataSet.externalReferenceCode,
		});

		const response = await fetch(url, {
			body: JSON.stringify(body),
			headers: DEFAULT_FETCH_HEADERS,
			method: 'PATCH',
		});

		if (!response.ok) {
			openDefaultFailureToast();

			return;
		}

		const responseJSON = await response.json();

		if (responseJSON?.id) {
			openDefaultSuccessToast();

			onDataSetUpdate(responseJSON);
		}
		else {
			openDefaultFailureToast();
		}
	};

	useEffect(() => {
		const fetchSettings = async () => {
			const fields = [
				OBJECT_RELATIONSHIP.DATA_SET_CARDS_SECTIONS,
				OBJECT_RELATIONSHIP.DATA_SET_LIST_SECTIONS,
				OBJECT_RELATIONSHIP.DATA_SET_TABLE_SECTIONS,
			].join(',');

			const url = getDataSetResourceURL({
				dataSetERC: dataSet.externalReferenceCode,
				params: {
					fields: `${fields},hideManagementBarInEmptyState`,
					nestedFields: fields,
				},
			});

			try {
				const response = await fetch(url, {
					headers: DEFAULT_FETCH_HEADERS,
				});

				if (!response.ok) {
					openDefaultFailureToast();

					setVisualizationModes([]);

					setLoading(false);

					setHideManagementBarInEmptyState(true);

					return;
				}

				const responseJSON = await response.json();

				const {
					hideManagementBarInEmptyState:
						persistedHideManagementBarInEmptyState,
					[OBJECT_RELATIONSHIP.DATA_SET_CARDS_SECTIONS]: cards,
					[OBJECT_RELATIONSHIP.DATA_SET_LIST_SECTIONS]: list,
					[OBJECT_RELATIONSHIP.DATA_SET_TABLE_SECTIONS]: table,
				} = responseJSON;

				const activeViews: Array<TVisualizationMode> = [];

				(
					DEFAULT_VISUALIZATION_MODES as Array<TVisualizationMode>
				).forEach((view) => {
					if (view.mode === 'cards' && cards && cards.length) {
						activeViews.push(view);
					}
					if (view.mode === 'list' && list && list.length) {
						activeViews.push(view);
					}
					if (view.mode === 'table' && table && table.length) {
						activeViews.push(view);
					}
				});

				setVisualizationModes(activeViews);

				setDefaultVisualizationMode(() => {
					if (
						activeViews.find(
							(view: TVisualizationMode) =>
								view.mode === dataSet.defaultVisualizationMode
						)
					) {
						return dataSet.defaultVisualizationMode;
					}
					else {
						return activeViews.length
							? activeViews[0].mode
							: NOT_CONFIGURED_VISUALIZATION_MODE.type;
					}
				});

				const serverHideManagementBarValue =
					persistedHideManagementBarInEmptyState || false;

				if (
					serverHideManagementBarValue !==
					hideManagementBarInEmptyState
				) {
					setHideManagementBarInEmptyState(
						serverHideManagementBarValue
					);
				}

				setLoading(false);
			}
			catch (error) {
				openDefaultFailureToast();
				setVisualizationModes([]);
				setLoading(false);
			}
		};

		fetchSettings();

		// eslint-disable-next-line react-compiler/react-compiler
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	return (
		<ClayLayout.ContainerFluid className="mt-3" size="lg">
			<ClayLayout.Sheet>
				<ClayLayout.SheetHeader className="mb-4">
					<h2 className="sheet-title">
						{Liferay.Language.get('settings')}
					</h2>
				</ClayLayout.SheetHeader>

				<ClayLayout.SheetSection>
					<h3 className="sheet-subtitle">
						{Liferay.Language.get('fragment-defaults')}
					</h3>

					<ClayLayout.Row className="align-items-center justify-content-between">
						<ClayLayout.Col size={8}>
							<div>
								<label
									htmlFor="view-mode-picker"
									id="view-mode"
								>
									{Liferay.Language.get(
										'default-visualization-mode'
									)}
								</label>

								<ClayTooltipProvider>
									<span
										className="ml-1 text-secondary"
										data-tooltip-align="top"
										title={Liferay.Language.get(
											'default-visualization-mode-tooltip'
										)}
									>
										<ClayIcon
											spritemap={spritemap}
											symbol="question-circle-full"
										/>
									</span>
								</ClayTooltipProvider>
							</div>

							<div>
								{Liferay.Language.get(
									'default-visualization-mode-help'
								)}
							</div>
						</ClayLayout.Col>

						<ClayLayout.Col size={4}>
							{!loading && (
								<Picker
									aria-labelledby="view-mode"
									className="mb-2"
									disabled={!visualizationModes.length}
									id="view-mode-picker"
									items={visualizationModes}
									messages={{
										itemDescribedby: Liferay.Language.get(
											'you-are-currently-on-a-text-element,-inside-of-a-list-box'
										),
										itemSelected:
											Liferay.Language.get('x-selected'),
										scrollToBottomAriaLabel:
											Liferay.Language.get(
												'scroll-to-bottom'
											),
										scrollToTopAriaLabel:
											Liferay.Language.get(
												'scroll-to-top'
											),
									}}
									onSelectionChange={(option: React.Key) => {
										if (
											option !==
											NOT_CONFIGURED_VISUALIZATION_MODE.type
										) {
											setDefaultVisualizationMode(
												option as string
											);
										}
									}}
									placeholder={
										NOT_CONFIGURED_VISUALIZATION_MODE.type
									}
									selectedKey={defaultVisualizationMode}
								>
									{visualizationModes.length ? (
										({label, mode, thumbnail}) => (
											<Option
												key={mode}
												textValue={label}
											>
												<ClayIcon
													className="mr-3"
													symbol={thumbnail}
												/>

												{label}
											</Option>
										)
									) : (
										<DropDown.Group
											header={Liferay.Language.get(
												'not-configured'
											)}
										>
											<Option
												key={
													NOT_CONFIGURED_VISUALIZATION_MODE.type
												}
												textValue={
													NOT_CONFIGURED_VISUALIZATION_MODE.type
												}
											>
												<ClayLayout.Row>
													<ClayLayout.Col>
														<Text size={3}>
															{
																NOT_CONFIGURED_VISUALIZATION_MODE.label
															}
														</Text>
													</ClayLayout.Col>
												</ClayLayout.Row>
											</Option>
										</DropDown.Group>
									)}
								</Picker>
							)}

							{!loading && !visualizationModes.length && (
								<ClayLink
									borderless
									onClick={() => onActiveSectionChange(1)}
									onKeyPress={() => onActiveSectionChange(1)}
									tabIndex={0}
									weight="semi-bold"
								>
									<span className="inline-item inline-item-before">
										<ClayIcon
											spritemap={spritemap}
											symbol="shortcut"
										/>
									</span>

									{Liferay.Language.get(
										'go-to-visualization-modes'
									)}
								</ClayLink>
							)}
						</ClayLayout.Col>
					</ClayLayout.Row>
				</ClayLayout.SheetSection>

				<ClayLayout.SheetSection>
					<h3 className="sheet-subtitle">
						{Liferay.Language.get('user-customization')}
					</h3>

					<ClayLayout.Row className="align-items-center justify-content-between mb-4">
						<ClayLayout.Col size={11}>
							<div>
								<label
									htmlFor="hide-management-bar-in-empty-state"
									id="hide-management-bar-in-empty-state"
								>
									{Liferay.Language.get(
										'hide-management-bar-in-empty-state'
									)}
								</label>
							</div>

							<div>
								{Liferay.Language.get(
									'hide-management-bar-in-empty-state-help'
								)}
							</div>
						</ClayLayout.Col>

						<ClayLayout.Col size={1}>
							<div className="d-flex form-group justify-content-end mr-2">
								<ClayToggle
									disabled={loading}
									onToggle={handleToggleChange}
									toggled={hideManagementBarInEmptyState}
								/>
							</div>
						</ClayLayout.Col>
					</ClayLayout.Row>

					{Liferay.FeatureFlags['LPD-10683'] && (
						<ClayLayout.Row className="align-items-center justify-content-between mb-4">
							<ClayLayout.Col size={11}>
								<div>
									<label htmlFor="user-views-toggle">
										{Liferay.Language.get(
											'enable-user-views'
										)}
									</label>
								</div>

								<div>
									{Liferay.Language.get('user-views-help')}
								</div>
							</ClayLayout.Col>

							<ClayLayout.Col
								className="align-self-start"
								size={1}
							>
								<ClayToggle
									id="user-views-toggle"
									onToggle={setSnapshotsEnabled}
									toggled={snapshotsEnabled}
								/>
							</ClayLayout.Col>
						</ClayLayout.Row>
					)}
				</ClayLayout.SheetSection>

				<ClayLayout.SheetFooter>
					<ClayButton.Group spaced>
						<ClayButton onClick={updateFDSViewSettings}>
							{Liferay.Language.get('save')}
						</ClayButton>

						<ClayButton
							displayType="secondary"
							onClick={() => navigate(backURL)}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>
					</ClayButton.Group>
				</ClayLayout.SheetFooter>
			</ClayLayout.Sheet>
		</ClayLayout.ContainerFluid>
	);
};

export default Settings;
