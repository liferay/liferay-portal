/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import ClayList from '@clayui/list';
import {ClayModalProvider, Context as ClayModalContext} from '@clayui/modal';
import ClayProgressBar from '@clayui/progress-bar';
import {openToast} from 'frontend-js-components-web';
import {fetch, localStorage} from 'frontend-js-web';
import React, {
	useCallback,
	useContext,
	useEffect,
	useRef,
	useState,
} from 'react';

import {
	EXECUTION_MODES,
	INTERVAL_RENDER_IN_PROGRESS,
	PORTAL_TOOLTIP_TRIGGER_CLASS,
	SCOPES,
} from './constants';
import ConfirmationModalBody from './execution_options/ConfirmationModalBody';
import ExecutionOptions from './execution_options/index';

function IndexerListItem({
	className,
	cmd = 'reindex',
	disabled = false,
	displayName,
	id,
	onClick,
	progressPercentage,
}) {
	const _handleClick = () => {
		const data = {cmd, displayName, id};

		if (className) {
			data.className = className;
		}

		onClick(data);
	};

	return (
		<ClayList.Item flex>
			<ClayList.ItemField expand>
				<ClayList.ItemTitle>
					<span style={{wordBreak: 'break-word'}}>
						{className
							? `${displayName} (${className})`
							: displayName}
					</span>
				</ClayList.ItemTitle>
			</ClayList.ItemField>

			<ClayList.ItemField className="index-action-wrapper">
				{typeof progressPercentage === 'number' &&
				progressPercentage >= 0 ? (
					<ClayProgressBar
						messages={{
							ariaLabelAttention: Liferay.Language.get(
								'attention-value-is-at-x'
							),
							ariaLabelComplete: Liferay.Language.get('complete'),
							ariaLabelInProgress:
								Liferay.Language.get('progress-x'),
						}}
						value={progressPercentage}
					/>
				) : (
					<ClayButton
						className="save-server-button"
						disabled={disabled}
						displayType="secondary"
						onClick={_handleClick}
					>
						{Liferay.Language.get('reindex')}
					</ClayButton>
				)}
			</ClayList.ItemField>
		</ClayList.Item>
	);
}

function getFailedReindexBackgroundTasksCount(htmlDocument, portletNamespace) {
	const failedReindexBackgroundTasksCount = htmlDocument.documentElement
		.querySelector(`#${portletNamespace}failedReindexBackgroundTasksCount`)
		?.innerHTML?.trim();

	return parseInt(failedReindexBackgroundTasksCount, 10) || 0;
}

function IndexActions({
	controlMenuCategoryKey,
	indexersMap = {},
	initialCompanyIds,
	initialExecutionMode,
	initialScope,
	concurrentModeSupported = true,
	virtualInstances = [],
	indexReindexerNames = [],
	omniadmin,
	portletNamespace,
	redirectURL = '',
	reindexURL = '',
	searchEngineDiskSpace = {
		availableDiskSpace: 0,
		isLowOnDiskSpace: false,
		usedDiskSpace: 0,
	},
}) {
	const [backgroundTaskMap, setBackgroundTaskMap] = useState({});
	const [executionMode, setExecutionMode] = useState(
		initialExecutionMode || EXECUTION_MODES.FULL.value
	);
	const [executionScope, setExecutionScope] = useState(
		initialScope || SCOPES.ALL
	);
	const [selectedCompanyIds, setSelectedCompanyIds] = useState(
		initialCompanyIds || []
	);

	const [state, dispatch] = useContext(ClayModalContext);

	const failedReindexBackgroundTasksCountRef = useRef(
		getFailedReindexBackgroundTasksCount(document, portletNamespace)
	);

	const intervalRef = useRef(null);

	/*
	 * Returns the list of virtual instances to reindex, depending on the
	 * `executionScope` and `selectedCompanyIds`.
	 */
	const _getCompanyIds = () => {
		if (!omniadmin) {
			return [Liferay.ThemeDisplay.getCompanyId()];
		}

		return executionScope === SCOPES.ALL
			? virtualInstances.map(({id}) => id)
			: selectedCompanyIds;
	};

	/*
	 * Appends sync icon to the control menu, if not already there.
	 * Called after the reindex fetch.
	 */
	const _handleSyncIconAppend = useCallback(() => {
		const currentControlMenu = document.getElementById(
			`${portletNamespace}controlMenu`
		);

		const currentControlMenuCategory = currentControlMenu.querySelector(
			`.${controlMenuCategoryKey}-control-group .control-menu-nav`
		);

		if (
			!currentControlMenuCategory?.querySelector(
				`.control-menu-nav-item .lexicon-icon-reload`
			)
		) {
			const controlMenuNavItem = document.createElement('div');

			controlMenuNavItem.className = 'control-menu-nav-item';

			const syncIcon = document.createElement('span');
			syncIcon.className = PORTAL_TOOLTIP_TRIGGER_CLASS;
			syncIcon.innerHTML = `
			<svg class="lexicon-icon lexicon-icon-reload" focusable="false">
				<use href="${Liferay.Icons.spritemap}#reload" />
			</svg>`;

			syncIcon.setAttribute(
				'data-title',
				Liferay.Language.get('the-portal-is-currently-reindexing')
			);

			controlMenuNavItem.appendChild(syncIcon);

			currentControlMenuCategory.appendChild(controlMenuNavItem);
		}
	}, [controlMenuCategoryKey, portletNamespace]);

	/*
	 * Removes sync icon to the control menu, if not already removed.
	 * Called after the background tasks complete.
	 */
	const _handleSyncIconRemove = useCallback(() => {
		const currentControlMenu = document.getElementById(
			`${portletNamespace}controlMenu`
		);

		const syncIcon = currentControlMenu.querySelector(
			`.${controlMenuCategoryKey}-control-group .control-menu-nav-item .lexicon-icon-reload`
		);

		const controlMenuNavItem = syncIcon?.closest('.control-menu-nav-item');

		if (controlMenuNavItem?.parentNode) {
			controlMenuNavItem.parentNode.removeChild(controlMenuNavItem);
		}
	}, [controlMenuCategoryKey, portletNamespace]);

	/*
	 * Executed after performing the reindex call. Continuously fetches the
	 * view of the current URL to get status of the background task.
	 * Calls `_handleBackgroundTaskStop` within when there are no more
	 * background tasks running.
	 */
	const _handleBackgroundTaskStart = useCallback(() => {
		const fetchRedirectURL = new URL(redirectURL);

		if (!intervalRef.current) {
			intervalRef.current = setInterval(() => {
				fetch(fetchRedirectURL, {method: 'GET'})
					.then((response) => response.text())
					.then((text) => {
						const parser = new DOMParser();
						const htmlDocument = parser.parseFromString(
							text,
							'text/html'
						);
						const backgroundTaskMapString =
							htmlDocument.documentElement
								.querySelector(
									`#${portletNamespace}classNameToBackgroundTaskMap`
								)
								?.innerHTML?.trim();

						const newBackgroundTaskMap =
							JSON.parse(backgroundTaskMapString) || {};

						if (Object.keys(newBackgroundTaskMap).length) {
							setBackgroundTaskMap(newBackgroundTaskMap);
						}
						else {
							_handleBackgroundTaskStop();

							_handleSyncIconRemove();

							const failedReindexBackgroundTasksCount =
								getFailedReindexBackgroundTasksCount(
									htmlDocument,
									portletNamespace
								);

							if (
								failedReindexBackgroundTasksCount >
								failedReindexBackgroundTasksCountRef.current
							) {
								openToast({
									message:
										Liferay.Language.get(
											'reindexing-failed'
										),
									type: 'danger',
								});
							}
							else {
								openToast({
									message: Liferay.Language.get(
										'reindexing-finished-successfully'
									),
									type: 'success',
								});
							}

							failedReindexBackgroundTasksCountRef.current =
								failedReindexBackgroundTasksCount;
						}
					})
					.catch(() => {
						_handleBackgroundTaskStop();

						_handleSyncIconRemove();

						openToast({
							message: Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
							type: 'danger',
						});
					});
			}, INTERVAL_RENDER_IN_PROGRESS);
		}
	}, [_handleSyncIconRemove, portletNamespace, redirectURL]);

	/*
	 * Clears the interval involved with continuously fetching the background task
	 * status. Also clears the background task map.
	 */
	const _handleBackgroundTaskStop = () => {
		clearInterval(intervalRef.current);

		intervalRef.current = null;

		setBackgroundTaskMap({});
	};

	/*
	 * Fired after clicking on Confirmation Modal's "Execute" button.
	 * If the "Do not show me this again" checkbox is checked, this
	 * preference setting is saved in the local storage.
	 */
	const _handleConfirmationModalVisibilitySave = () => {
		if (
			document.getElementById(`${portletNamespace}hideModalCheckbox`)
				?.checked
		) {
			localStorage.setItem(
				`${portletNamespace}${Liferay.ThemeDisplay.getUserId()}_hideReindexConfirmationModal`,
				true,
				localStorage.TYPES.FUNCTIONAL
			);
		}
	};

	const _handleExecutionModeChange = (value) => {
		setExecutionMode(value);
	};

	const _handleExecutionScopeChange = (value) => {
		setExecutionScope(value);
	};

	/*
	 * First step when clicking on one of the indexer's action button (labeled
	 * "Reindex"). Validates for virtual instances and shows the confirmation modal,
	 * if expected. If ok, then calls `_handleReindex`.
	 */
	const _handleIndexerItemClick = (data) => {
		if (!_getCompanyIds().length) {
			openToast({
				message: Liferay.Language.get('missing-instance-error'),
				type: 'danger',
			});

			return;
		}

		// Determine whether confirmation modal should be shown

		const isConcurrentMode =
			executionMode === EXECUTION_MODES.CONCURRENT.value;

		const status =
			isConcurrentMode && searchEngineDiskSpace.isLowOnDiskSpace
				? 'warning'
				: 'info';

		const hideModal = localStorage?.getItem(
			`${portletNamespace}${Liferay.ThemeDisplay.getUserId()}_hideReindexConfirmationModal`,
			localStorage.TYPES.FUNCTIONAL
		);

		if (
			!hideModal ||
			(isConcurrentMode && searchEngineDiskSpace.isLowOnDiskSpace)
		) {
			dispatch({
				payload: {
					body: (
						<ConfirmationModalBody
							availableDiskSpace={
								searchEngineDiskSpace.availableDiskSpace
							}
							cmd={data.cmd}
							executionMode={executionMode}
							isLowOnDiskSpace={
								searchEngineDiskSpace.isLowOnDiskSpace
							}
							portletNamespace={portletNamespace}
							usedDiskSpace={searchEngineDiskSpace.usedDiskSpace}
						/>
					),
					footer: [
						<></>,
						<></>,
						<ClayButton.Group key={0} spaced>
							<ClayButton
								displayType="secondary"
								key={2}
								onClick={state.onClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								displayType="primary"
								key={3}
								onClick={() => {
									_handleConfirmationModalVisibilitySave();

									state.onClose();

									_handleReindex(data);
								}}
							>
								{Liferay.Language.get('execute')}
							</ClayButton>
						</ClayButton.Group>,
					],
					header:
						isConcurrentMode &&
						searchEngineDiskSpace.isLowOnDiskSpace
							? Liferay.Language.get('reindex-disk-space-warning')
							: data.id === 'spellCheckDictionaries'
								? Liferay.Language.get(
										'reindex-spell-check-dictionaries'
									)
								: data.id === 'portal'
									? Liferay.Language.get(
											'reindex-search-indexes'
										)
									: Liferay.Util.sub(
											Liferay.Language.get(
												'reindex-type-x'
											),
											'<' + data.displayName + '>'
										),
					size: 'md',
					status,
				},
				type: 1,
			});
		}
		else {
			_handleReindex(data);
		}
	};

	/*
	 * Performs the reindex fetch call. Appends the necessary parameters and
	 * sets up the properties to start the background task visuals.
	 * Fired inside `_handleIndexerItemClick`.
	 */
	const _handleReindex = (data) => {
		const fetchReindexURL = new URL(reindexURL);

		const {className, cmd, id} = data;

		const submissionData = className ? {className, cmd} : {cmd};

		Object.entries({
			...submissionData,
			companyIds: _getCompanyIds(),
			executionMode,
		}).forEach(([property, value]) => {
			if (value) {
				fetchReindexURL.searchParams.append(
					`${portletNamespace}${property}`,
					value
				);
			}
		});

		setBackgroundTaskMap({
			...backgroundTaskMap,
			[id]: 0,
		});

		_handleSyncIconAppend();

		fetch(fetchReindexURL, {method: 'POST'})
			.then((response) => response.text())
			.then(() => {
				if (id !== 'spellCheckDictionaries') {
					_handleBackgroundTaskStart();
				}
				else {
					setBackgroundTaskMap({});

					_handleSyncIconRemove();

					openToast({
						message: Liferay.Language.get(
							'reindexing-finished-successfully'
						),
						type: 'success',
					});
				}
			})
			.catch(() => {
				setBackgroundTaskMap({});

				_handleSyncIconRemove();

				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				});
			});
	};

	const _handleSelectedCompanyIdsChange = (value) => {
		setSelectedCompanyIds(value);
	};

	/*
	 * Returns true if any background task is running for the list of indexers.
	 * If no list is provided, returns true if there are any background tasks
	 * running.
	 */
	const _isBackgroundTaskRunning = (indexers = []) => {
		if (indexers.length) {
			return indexers.some(
				(type) => typeof backgroundTaskMap[type] !== 'undefined'
			);
		}

		return !!Object.keys(backgroundTaskMap).length;
	};

	useEffect(() => {
		const backgroundTaskMapString = document
			.querySelector(`#${portletNamespace}classNameToBackgroundTaskMap`)
			?.innerHTML?.trim();

		const newBackgroundTaskMap = JSON.parse(backgroundTaskMapString) || {};

		if (Object.keys(newBackgroundTaskMap).length) {
			_handleSyncIconAppend();

			setBackgroundTaskMap(newBackgroundTaskMap);

			_handleBackgroundTaskStart();
		}
	}, [_handleBackgroundTaskStart, _handleSyncIconAppend, portletNamespace]);

	return (
		<ClayLayout.Container
			className="search-admin-index-actions-container"
			fluidSize="xl"
			formSize="lg"
			id={`${portletNamespace}adminSearchAdminIndexActionsPanel`}
		>
			<ClayLayout.ContainerFluid view>
				<ClayLayout.Row>
					<ClayLayout.Col size={4}>
						<ExecutionOptions
							concurrentModeSupported={concurrentModeSupported}
							executionMode={executionMode}
							executionScope={executionScope}
							omniadmin={omniadmin}
							onExecutionModeChange={_handleExecutionModeChange}
							onExecutionScopeChange={_handleExecutionScopeChange}
							onSelectedCompanyIdsChange={
								_handleSelectedCompanyIdsChange
							}
							portletNamespace={portletNamespace}
							selectedCompanyIds={selectedCompanyIds}
							virtualInstances={virtualInstances}
						/>
					</ClayLayout.Col>

					<ClayLayout.Col size={8}>
						<div className="index-actions-sheet sheet sheet-lg">
							<h2 className="sheet-title">
								{Liferay.Language.get('actions')}
							</h2>

							<ClayList>
								<ClayList.Header>
									{Liferay.Language.get('global')}
								</ClayList.Header>

								<IndexerListItem
									disabled={_isBackgroundTaskRunning()}
									displayName={Liferay.Language.get(
										'all-search-indexes'
									)}
									id="portal"
									onClick={_handleIndexerItemClick}
									progressPercentage={
										backgroundTaskMap['portal']
									}
								/>

								<IndexerListItem
									cmd="reindexDictionaries"
									disabled={
										executionMode ===
											EXECUTION_MODES.CONCURRENT.value ||
										_isBackgroundTaskRunning([
											'portal',
											'spellCheckDictionaries',
										])
									}
									displayName={Liferay.Language.get(
										'all-spell-check-dictionaries'
									)}
									id="spellCheckDictionaries"
									onClick={_handleIndexerItemClick}
								/>

								{Object.keys(indexersMap)
									.sort()
									.map((category) => (
										<React.Fragment key={category}>
											<ClayList.Header>
												{category}
											</ClayList.Header>

											{indexersMap[category].map(
												({
													className,
													displayName,
													enabled,
												}) => (
													<IndexerListItem
														className={className}
														disabled={
															!enabled ||
															executionMode ===
																EXECUTION_MODES
																	.CONCURRENT
																	.value ||
															_isBackgroundTaskRunning(
																[
																	'portal',
																	'spellCheckDictionaries',
																]
															)
														}
														displayName={
															displayName
														}
														id={className}
														key={className}
														onClick={
															_handleIndexerItemClick
														}
														progressPercentage={
															backgroundTaskMap[
																className
															]
														}
													/>
												)
											)}
										</React.Fragment>
									))}

								{Object.keys(indexReindexerNames)
									.sort()
									.map((category) => (
										<React.Fragment key={category}>
											<ClayList.Header>
												{category}
											</ClayList.Header>

											{indexReindexerNames[category].map(
												({className, displayName}) => (
													<IndexerListItem
														className={className}
														cmd="reindexIndexReindexer"
														disabled={
															executionMode ===
																EXECUTION_MODES
																	.CONCURRENT
																	.value ||
															_isBackgroundTaskRunning(
																[
																	'portal',
																	'spellCheckDictionaries',
																]
															)
														}
														displayName={
															displayName
														}
														id={className}
														key={className}
														onClick={
															_handleIndexerItemClick
														}
														progressPercentage={
															backgroundTaskMap[
																className
															]
														}
													/>
												)
											)}
										</React.Fragment>
									))}
							</ClayList>
						</div>
					</ClayLayout.Col>
				</ClayLayout.Row>
			</ClayLayout.ContainerFluid>
		</ClayLayout.Container>
	);
}

export default function ({
	data = {},
	portletNamespace,
	redirectURL = '',
	reindexURL = '',
}) {
	const {
		concurrentModeSupported,
		controlMenuCategoryKey,
		indexReindexerNames,
		indexersMap,
		initialCompanyIds,
		initialExecutionMode,
		initialScope,
		omniadmin,
		searchEngineDiskSpace,
		virtualInstances,
	} = data;

	return (
		<ClayModalProvider>
			<IndexActions
				concurrentModeSupported={concurrentModeSupported}
				controlMenuCategoryKey={controlMenuCategoryKey}
				indexReindexerNames={indexReindexerNames}
				indexersMap={indexersMap}
				initialCompanyIds={initialCompanyIds}
				initialExecutionMode={initialExecutionMode}
				initialScope={initialScope}
				omniadmin={omniadmin}
				portletNamespace={portletNamespace}
				redirectURL={redirectURL}
				reindexURL={reindexURL}
				searchEngineDiskSpace={searchEngineDiskSpace}
				virtualInstances={virtualInstances}
			/>
		</ClayModalProvider>
	);
}
