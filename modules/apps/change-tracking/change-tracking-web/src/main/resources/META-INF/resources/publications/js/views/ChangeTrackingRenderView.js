/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown, {Align, ClayDropDownWithItems} from '@clayui/drop-down';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayNavigationBar from '@clayui/navigation-bar';
import ClayTable from '@clayui/table';
import classNames from 'classnames';
import {
	openConfirmModal,
	openSimpleInputModal,
	openToast,
} from 'frontend-js-components-web';
import {
	createPortletURL,
	fetch,
	navigate as navigateUtil,
} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import ExperienceDropdown from '../components/ExperienceDropdown';
import {WorkflowStatusLabel} from '../components/WorkflowStatusLabel';
import ChangeTrackingWorkflowView from './ChangeTrackingWorkflowView';

const LocalizationDropdown = ({
	currentLocale,
	defaultLocale,
	locales,
	setSelectedLocale,
	spritemap,
}) => {
	const [active, setActive] = useState(false);

	return (
		<div className="autofit-col publications-localization">
			<ClayDropDown
				active={active}
				onActiveChange={setActive}
				trigger={
					<ClayButton
						aria-label="show-available-locales"
						displayType="secondary"
						monospaced
						onClick={() => setActive(!active)}
					>
						<span className="inline-item">
							<ClayIcon
								spritemap={spritemap}
								symbol={currentLocale.symbol}
							/>
						</span>

						<span className="btn-section">
							{currentLocale.label}
						</span>
					</ClayButton>
				}
			>
				<ClayDropDown.ItemList>
					{locales
						.sort((a, b) => {
							if (a.label === defaultLocale.label) {
								return -1;
							}
							else if (b.label === defaultLocale.label) {
								return 1;
							}

							return 0;
						})
						.map((locale) => {
							return (
								<ClayDropDown.Item
									key={locale.label}
									onClick={() => {
										setActive(false);
										setSelectedLocale(locale);
									}}
								>
									<ClayLayout.ContentRow containerElement="span">
										<ClayLayout.ContentCol
											containerElement="span"
											expand
										>
											<ClayLayout.ContentSection>
												<ClayIcon
													className="inline-item inline-item-before"
													spritemap={spritemap}
													symbol={locale.symbol}
												/>

												{locale.label}
											</ClayLayout.ContentSection>
										</ClayLayout.ContentCol>

										<ClayLayout.ContentCol containerElement="span">
											<ClayLayout.ContentSection>
												<ClayLabel
													displayType={
														locale.label ===
														defaultLocale.label
															? 'info'
															: 'success'
													}
												>
													{locale.label ===
													defaultLocale.label
														? Liferay.Language.get(
																'default'
															)
														: Liferay.Language.get(
																'translated'
															)}
												</ClayLabel>
											</ClayLayout.ContentSection>
										</ClayLayout.ContentCol>
									</ClayLayout.ContentRow>
								</ClayDropDown.Item>
							);
						})}
				</ClayDropDown.ItemList>
			</ClayDropDown>
		</div>
	);
};

export default function ChangeTrackingRenderView({
	childEntries,
	defaultLocale,
	description,
	discardURL,
	handleNavigation,
	initialDataURL,
	moveChangesURL,
	namespace,
	parentEntries,
	showDropdown,
	showHeader = true,
	showWorkflow,
	spritemap,
	title,
	workflowStatus,
}) {
	const CHANGE_TYPE_ADDED = 'added';
	const CHANGE_TYPE_DELETED = 'deleted';
	const CHANGE_TYPE_MODIFIED = 'modified';
	const CONTENT_TYPE_CHILDREN = 'children';
	const CONTENT_TYPE_PARENTS = 'parents';
	const CONTENT_TYPE_RENDER = 'data';
	const CONTENT_TYPE_PREVIEW = 'display';
	const CONTENT_TYPE_WORKFLOW = 'workflow';
	const VIEW_LEFT = 'VIEW_LEFT';
	const VIEW_RIGHT = 'VIEW_RIGHT';
	const VIEW_SPLIT = 'VIEW_SPLIT';
	const VIEW_UNIFIED = 'VIEW_UNIFIED';

	const [dataURL, setDataURL] = useState(initialDataURL);
	const [loading, setLoading] = useState(false);
	const [refresh, setRefresh] = useState({});
	const [selectedLocale, setSelectedLocale] = useState(defaultLocale);
	const [selectedSegmentsExperienceId, setSelectedSegmentsExperienceId] =
		useState(null);
	const [showWorkflowSuccessMessage, setShowWorkflowSuccessMessage] =
		useState(false);
	const [state, setState] = useState({
		contentType: CONTENT_TYPE_PREVIEW,
		renderData: null,
		view: VIEW_UNIFIED,
	});

	useEffect(() => {
		setLoading(true);

		fetch(dataURL)
			.then((response) => response.json())
			.then((json) => {
				if (!json.changeType) {
					setLoading(false);
					setState({
						renderData: {
							errorMessage: Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
						},
					});

					return;
				}

				const newState = {
					children: childEntries,
					parents: parentEntries,
					renderData: json,
					view: VIEW_UNIFIED,
				};

				if (
					!Object.prototype.hasOwnProperty.call(
						json,
						'leftPreview'
					) &&
					!Object.prototype.hasOwnProperty.call(
						json,
						'leftLocalizedPreview'
					) &&
					!Object.prototype.hasOwnProperty.call(
						json,
						'rightPreview'
					) &&
					!Object.prototype.hasOwnProperty.call(
						json,
						'rightLocalizedPreview'
					)
				) {
					newState.contentType = CONTENT_TYPE_RENDER;
				}

				if (!Object.prototype.hasOwnProperty.call(json, 'leftTitle')) {
					newState.view = VIEW_RIGHT;
				}
				else if (
					!Object.prototype.hasOwnProperty.call(json, 'rightTitle')
				) {
					newState.view = VIEW_LEFT;
				}

				if (
					newState.view === VIEW_UNIFIED &&
					newState.contentType !== CONTENT_TYPE_WORKFLOW &&
					((newState.contentType === CONTENT_TYPE_RENDER &&
						!Object.prototype.hasOwnProperty.call(
							json,
							'unifiedRender'
						) &&
						!Object.prototype.hasOwnProperty.call(
							json,
							'unifiedLocalizedRender'
						)) ||
						((!newState.contentType ||
							newState.contentType === CONTENT_TYPE_PREVIEW) &&
							!Object.prototype.hasOwnProperty.call(
								json,
								'unifiedPreview'
							) &&
							!Object.prototype.hasOwnProperty.call(
								json,
								'unifiedLocalizedPreview'
							)))
				) {
					newState.view = VIEW_SPLIT;
				}

				setState((prevState) => ({...prevState, ...newState}));

				setLoading(false);
			})
			.catch(() => {
				setLoading(false);
				setState({
					renderData: {
						errorMessage: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
					},
				});
			});
	}, [
		childEntries,
		dataURL,
		parentEntries,
		refresh,
		selectedSegmentsExperienceId,
	]);

	useEffect(() => {
		if (showWorkflowSuccessMessage) {
			Liferay.fire('closeModal');

			setRefresh({});

			openToast({
				message: Liferay.Language.get(
					'your-request-completed-successfully'
				),
				type: 'success',
			});
		}
	}, [showWorkflowSuccessMessage]);

	let currentLocale = selectedLocale;
	let currentTitle = title;

	if (showHeader && state.renderData) {
		if (
			!state.renderData.locales ||
			!state.renderData.locales.find(
				(item) => item.label === currentLocale.label
			)
		) {
			if (state.renderData.defaultLocale) {
				currentLocale = state.renderData.defaultLocale;
			}
			else {
				currentLocale = defaultLocale;
			}
		}

		if (
			state.renderData.localizedTitles &&
			state.renderData.localizedTitles[currentLocale.label]
		) {
			currentTitle =
				state.renderData.localizedTitles[currentLocale.label];
		}
	}

	const setContentType = (contentType) => {
		setState({
			children: state.children,
			contentType,
			parents: state.parents,
			renderData: state.renderData,
			view: state.view,
		});
	};

	const getContentSelectTitle = (view) => {
		if (view === VIEW_LEFT) {
			return state.renderData.leftTitle;
		}
		else if (view === VIEW_RIGHT) {
			return state.renderData.rightTitle;
		}
		else if (view === VIEW_SPLIT) {
			return Liferay.Language.get('split-view');
		}

		return Liferay.Language.get('unified-view');
	};

	const renderPreviewLeft = () => {
		if (
			state.contentType === CONTENT_TYPE_RENDER &&
			Object.prototype.hasOwnProperty.call(state.renderData, 'leftRender')
		) {
			return (
				<div
					dangerouslySetInnerHTML={{
						__html: state.renderData.leftRender,
					}}
				/>
			);
		}
		else if (
			state.contentType === CONTENT_TYPE_RENDER &&
			Object.prototype.hasOwnProperty.call(
				state.renderData,
				'leftLocalizedRender'
			)
		) {
			if (state.renderData.leftLocalizedRender[currentLocale.label]) {
				return (
					<div
						dangerouslySetInnerHTML={{
							__html: state.renderData.leftLocalizedRender[
								currentLocale.label
							],
						}}
					/>
				);
			}

			return (
				<ClayAlert displayType="info" spritemap={spritemap}>
					{Liferay.Language.get('content-is-empty')}
				</ClayAlert>
			);
		}
		else if (
			state.contentType === CONTENT_TYPE_PREVIEW &&
			Object.prototype.hasOwnProperty.call(
				state.renderData,
				'leftPreview'
			)
		) {
			if (state.renderData.leftPreview) {
				return (
					<div
						dangerouslySetInnerHTML={{
							__html: state.renderData.leftPreview,
						}}
					/>
				);
			}

			return (
				<ClayAlert displayType="info" spritemap={spritemap}>
					{Liferay.Language.get('content-is-empty')}
				</ClayAlert>
			);
		}
		else if (
			state.contentType === CONTENT_TYPE_PREVIEW &&
			Object.prototype.hasOwnProperty.call(
				state.renderData,
				'leftLocalizedPreview'
			)
		) {
			if (state.renderData.leftLocalizedPreview[currentLocale.label]) {
				return (
					<div
						dangerouslySetInnerHTML={{
							__html: state.renderData.leftLocalizedPreview[
								currentLocale.label
							],
						}}
					/>
				);
			}

			return (
				<ClayAlert displayType="info" spritemap={spritemap}>
					{Liferay.Language.get('content-is-empty')}
				</ClayAlert>
			);
		}
		else if (loading) {
			return '';
		}
		else if (
			state.renderData.changeType === CHANGE_TYPE_MODIFIED &&
			!Object.prototype.hasOwnProperty.call(
				state.renderData,
				'leftRender'
			) &&
			!Object.prototype.hasOwnProperty.call(
				state.renderData,
				'leftLocalizedRender'
			)
		) {
			return (
				<ClayAlert displayType="danger" spritemap={spritemap}>
					{Liferay.Language.get('this-item-is-missing-or-is-deleted')}
				</ClayAlert>
			);
		}

		return (
			<ClayAlert displayType="danger" spritemap={spritemap}>
				{Liferay.Language.get(
					'unable-to-display-content-due-to-an-unexpected-error'
				)}
			</ClayAlert>
		);
	};

	const renderPreviewRight = () => {
		if (
			state.contentType === CONTENT_TYPE_RENDER &&
			Object.prototype.hasOwnProperty.call(
				state.renderData,
				'rightRender'
			)
		) {
			return (
				<div
					dangerouslySetInnerHTML={{
						__html: state.renderData.rightRender,
					}}
				/>
			);
		}
		else if (
			state.contentType === CONTENT_TYPE_RENDER &&
			Object.prototype.hasOwnProperty.call(
				state.renderData,
				'rightLocalizedRender'
			)
		) {
			if (state.renderData.rightLocalizedRender[currentLocale.label]) {
				return (
					<div
						dangerouslySetInnerHTML={{
							__html: state.renderData.rightLocalizedRender[
								currentLocale.label
							],
						}}
					/>
				);
			}

			return (
				<ClayAlert displayType="info" spritemap={spritemap}>
					{Liferay.Language.get('content-is-empty')}
				</ClayAlert>
			);
		}
		else if (
			state.contentType === CONTENT_TYPE_PREVIEW &&
			Object.prototype.hasOwnProperty.call(
				state.renderData,
				'rightPreview'
			)
		) {
			if (state.renderData.rightPreview) {
				return (
					<div
						dangerouslySetInnerHTML={{
							__html: state.renderData.rightPreview,
						}}
					/>
				);
			}

			return (
				<ClayAlert displayType="info" spritemap={spritemap}>
					{Liferay.Language.get('content-is-empty')}
				</ClayAlert>
			);
		}
		else if (
			state.contentType === CONTENT_TYPE_PREVIEW &&
			Object.prototype.hasOwnProperty.call(
				state.renderData,
				'rightLocalizedPreview'
			)
		) {
			if (state.renderData.rightLocalizedPreview[currentLocale.label]) {
				return (
					<div
						dangerouslySetInnerHTML={{
							__html: state.renderData.rightLocalizedPreview[
								currentLocale.label
							],
						}}
					/>
				);
			}

			return (
				<ClayAlert displayType="info" spritemap={spritemap}>
					{Liferay.Language.get('content-is-empty')}
				</ClayAlert>
			);
		}
		else if (loading) {
			return '';
		}

		return (
			<ClayAlert displayType="danger" spritemap={spritemap}>
				{Liferay.Language.get(
					'unable-to-display-content-due-to-an-unexpected-error'
				)}
			</ClayAlert>
		);
	};

	const renderPreviewUnified = () => {
		if (
			state.contentType === CONTENT_TYPE_RENDER &&
			Object.prototype.hasOwnProperty.call(
				state.renderData,
				'unifiedRender'
			)
		) {
			return (
				<div className="taglib-diff-html">
					<div
						dangerouslySetInnerHTML={{
							__html: state.renderData.unifiedRender,
						}}
					/>
				</div>
			);
		}
		else if (
			state.contentType === CONTENT_TYPE_RENDER &&
			Object.prototype.hasOwnProperty.call(
				state.renderData,
				'unifiedLocalizedRender'
			)
		) {
			if (state.renderData.unifiedLocalizedRender[currentLocale.label]) {
				return (
					<div className="taglib-diff-html">
						<div
							dangerouslySetInnerHTML={{
								__html: state.renderData.unifiedLocalizedRender[
									currentLocale.label
								],
							}}
						/>
					</div>
				);
			}

			return (
				<ClayAlert displayType="info" spritemap={spritemap}>
					{Liferay.Language.get('content-is-empty')}
				</ClayAlert>
			);
		}
		else if (
			state.contentType === CONTENT_TYPE_PREVIEW &&
			Object.prototype.hasOwnProperty.call(
				state.renderData,
				'unifiedPreview'
			)
		) {
			if (state.renderData.unifiedPreview) {
				return (
					<div className="taglib-diff-html">
						<div
							dangerouslySetInnerHTML={{
								__html: state.renderData.unifiedPreview,
							}}
						/>
					</div>
				);
			}

			return (
				<ClayAlert displayType="info" spritemap={spritemap}>
					{Liferay.Language.get('content-is-empty')}
				</ClayAlert>
			);
		}
		else if (
			state.contentType === CONTENT_TYPE_PREVIEW &&
			Object.prototype.hasOwnProperty.call(
				state.renderData,
				'unifiedLocalizedPreview'
			)
		) {
			if (state.renderData.unifiedLocalizedPreview[currentLocale.label]) {
				return (
					<div className="taglib-diff-html">
						<div
							dangerouslySetInnerHTML={{
								__html: state.renderData
									.unifiedLocalizedPreview[
									currentLocale.label
								],
							}}
						/>
					</div>
				);
			}

			return (
				<ClayAlert displayType="info" spritemap={spritemap}>
					{Liferay.Language.get('content-is-empty')}
				</ClayAlert>
			);
		}
		else if (loading) {
			return '';
		}

		return (
			<ClayAlert displayType="danger" spritemap={spritemap}>
				{Liferay.Language.get(
					'unable-to-display-content-due-to-an-unexpected-error'
				)}
			</ClayAlert>
		);
	};

	const openWorkflowAssignModal = (href, label, modalHeight) => {
		Liferay.Util.openModal({
			center: true,
			customEvents: [
				{
					name: `${namespace}workflowTaskUpdated`,
					onEvent() {
						const iframe = document.querySelector(
							'.liferay-modal iframe'
						);

						iframe.contentWindow.location.reload();

						setShowWorkflowSuccessMessage(true);
					},
				},
			],
			height: modalHeight,
			onOpen: () => setShowWorkflowSuccessMessage(false),
			size: 'lg',
			title: label,
			url: href,
		});
	};

	const renderWorkflowView = () => {
		if (
			state.contentType === CONTENT_TYPE_WORKFLOW &&
			Object.prototype.hasOwnProperty.call(
				state.renderData,
				'workflowData'
			)
		) {
			return (
				<ChangeTrackingWorkflowView
					openWorkflowAssignModal={openWorkflowAssignModal}
					workflowData={state.renderData.workflowData}
				/>
			);
		}

		return (
			<ClayAlert displayType="danger" spritemap={spritemap}>
				{Liferay.Language.get(
					'unable-to-display-content-due-to-an-unexpected-error'
				)}
			</ClayAlert>
		);
	};

	const renderDiffLegend = () => {
		if (
			(state.contentType !== CONTENT_TYPE_PREVIEW &&
				state.contentType !== CONTENT_TYPE_RENDER) ||
			state.view !== VIEW_UNIFIED
		) {
			return '';
		}

		const elements = [];

		elements.push(
			<div className="autofit-col row-divider">
				<div />
			</div>
		);

		elements.push(
			<div className="autofit-col">
				<div className="taglib-diff-html">
					<span className="diff-html-added legend-item">
						{Liferay.Language.get('added')}
					</span>

					<span className="diff-html-removed legend-item">
						{Liferay.Language.get('deleted')}
					</span>

					<span className="diff-html-changed">
						{Liferay.Language.get('format-changes')}
					</span>
				</div>
			</div>
		);

		return elements;
	};

	const navigate = (editURL, checkoutURL, confirmationMessage) => {
		const editPortletURL = createPortletURL(editURL, {
			redirect: window.location.pathname + window.location.search,
		});

		if (!checkoutURL) {
			navigateUtil(editPortletURL);

			return;
		}

		const checkoutPortletURL = createPortletURL(checkoutURL, {
			redirect: editPortletURL,
		});

		openConfirmModal({
			message: confirmationMessage,
			onConfirm: (isConfirmed) =>
				isConfirmed && submitForm(document.hrefFm, checkoutPortletURL),
		});
	};

	const renderDropdownMenu = () => {
		if (!showDropdown || !state.renderData) {
			return null;
		}

		let dropdownItems = [];

		if (state.renderData.editInPublication) {
			dropdownItems.push({
				label: state.renderData.editInPublication.label,
				onClick: () =>
					navigate(
						state.renderData.editInPublication.editURL,
						state.renderData.editInPublication.checkoutURL,
						state.renderData.editInPublication.confirmationMessage
					),
				symbolLeft: 'pencil',
			});
		}

		if (state.renderData.editInProduction) {
			dropdownItems.push({
				label: state.renderData.editInProduction.label,
				onClick: () =>
					navigate(
						state.renderData.editInProduction.editURL,
						state.renderData.editInProduction.checkoutURL,
						state.renderData.editInProduction.confirmationMessage
					),
				symbolLeft: 'pencil',
			});
		}

		if (moveChangesURL !== null) {
			dropdownItems.push({
				label: Liferay.Language.get('move-changes'),
				onClick: () => navigateUtil(moveChangesURL),
				symbolLeft: 'move-folder',
			});
		}

		const workflowActionsDropdownItems = [];

		state.renderData.workflowActions?.forEach((workflowAction, i) => {
			if (workflowAction.modalHeight) {
				workflowActionsDropdownItems.push({
					label: workflowAction.label,
					onClick: () =>
						Liferay.Util.openModal({
							center: true,
							customEvents: [
								{
									name: `${namespace}workflowTaskUpdated`,
									onEvent() {
										const iframe = document.querySelector(
											'.liferay-modal iframe'
										);

										iframe.contentWindow.location.reload();

										setShowWorkflowSuccessMessage(true);
									},
								},
							],
							height: workflowAction.modalHeight,
							onOpen: () => setShowWorkflowSuccessMessage(false),
							size: 'lg',
							title: workflowAction.label,
							url: workflowAction.href,
						}),
					symbolLeft: 'workflow',
				});
			}
			else {
				workflowActionsDropdownItems.push({
					id: `${namespace}${i}taskChangeStatusLink`,
					label: workflowAction.label,
					onClick: () => {
						setShowWorkflowSuccessMessage(false);

						openSimpleInputModal({
							buttonSubmitLabel: Liferay.Language.get('done'),
							center: true,
							dialogTitle: workflowAction.label,
							formSubmitURL: workflowAction.href,
							mainFieldComponent: 'textarea',
							mainFieldLabel: Liferay.Language.get('comment'),
							mainFieldName: 'comment',
							mainFieldPlaceholder:
								Liferay.Language.get('comment'),
							namespace,
							onFormSuccess: () =>
								setTimeout(
									() => setShowWorkflowSuccessMessage(true),
									250
								),
							required: false,
							size: 'lg',
						});
					},
					symbolLeft: 'workflow',
				});
			}
		});

		if (workflowActionsDropdownItems.length) {
			dropdownItems = [
				{
					items: dropdownItems,
					label: Liferay.Language.get('publication'),
					type: 'group',
				},
				{type: 'divider'},
				{
					items: workflowActionsDropdownItems,
					label: Liferay.Language.get('workflow'),
					type: 'group',
				},
				{type: 'divider'},
			];
		}

		if (discardURL !== null) {
			dropdownItems.push({
				label: Liferay.Language.get('discard'),
				onClick: () => navigateUtil(discardURL),
				symbolLeft: 'times-circle',
			});
		}

		if (!dropdownItems.length) {
			return null;
		}

		return (
			<div className="autofit-col">
				<ClayDropDownWithItems
					alignmentPosition={Align.BottomLeft}
					items={dropdownItems}
					spritemap={spritemap}
					trigger={
						<ClayButtonWithIcon
							aria-label="more-actions"
							displayType="unstyled"
							small
							spritemap={spritemap}
							symbol="ellipsis-v"
						/>
					}
				/>
			</div>
		);
	};

	const renderViewDropdown = () => {
		if (
			!Object.prototype.hasOwnProperty.call(
				state.renderData,
				'leftTitle'
			) ||
			!Object.prototype.hasOwnProperty.call(
				state.renderData,
				'rightTitle'
			)
		) {
			let title = null;

			if (state.view === VIEW_LEFT) {
				title = state.renderData.leftTitle;

				if (state.renderData.changeType === CHANGE_TYPE_DELETED) {
					title += ' (' + Liferay.Language.get('deleted') + ')';
				}
			}
			else if (state.view === VIEW_RIGHT) {
				title = state.renderData.rightTitle;

				if (state.renderData.changeType === CHANGE_TYPE_ADDED) {
					title += ' (' + Liferay.Language.get('new') + ')';
				}
			}

			return (
				<div>
					<span className="inline-item inline-item-before">
						<ClayIcon spritemap={spritemap} symbol="rectangle" />
					</span>

					{title}
				</div>
			);
		}

		const pushItem = (items, view) => {
			items.push({
				active: state.view === view,
				label: getContentSelectTitle(view),
				onClick: () => {
					setState({
						children: state.children,
						contentType: state.contentType,
						parents: state.parents,
						renderData: state.renderData,
						view,
					});
				},
				symbolLeft:
					view === VIEW_SPLIT ? 'rectangle-split' : 'rectangle',
			});
		};

		const items = [];

		if (
			(state.contentType === CONTENT_TYPE_RENDER &&
				(Object.prototype.hasOwnProperty.call(
					state.renderData,
					'unifiedRender'
				) ||
					Object.prototype.hasOwnProperty.call(
						state.renderData,
						'unifiedLocalizedRender'
					))) ||
			(state.contentType === CONTENT_TYPE_PREVIEW &&
				(Object.prototype.hasOwnProperty.call(
					state.renderData,
					'unifiedPreview'
				) ||
					Object.prototype.hasOwnProperty.call(
						state.renderData,
						'unifiedLocalizedPreview'
					)))
		) {
			pushItem(items, VIEW_UNIFIED);

			items.push({
				type: 'divider',
			});
		}

		pushItem(items, VIEW_LEFT);
		pushItem(items, VIEW_RIGHT);

		items.push({
			type: 'divider',
		});

		pushItem(items, VIEW_SPLIT);

		return (
			<ClayDropDownWithItems
				alignmentPosition={Align.BottomCenter}
				items={items}
				spritemap={spritemap}
				trigger={
					<ClayButton borderless displayType="secondary">
						<span className="inline-item inline-item-before">
							<ClayIcon
								spritemap={spritemap}
								symbol={
									state.view === VIEW_SPLIT
										? 'rectangle-split'
										: 'rectangle'
								}
							/>
						</span>

						{getContentSelectTitle(state.view)}

						<span className="inline-item inline-item-after">
							<ClayIcon
								spritemap={spritemap}
								symbol="caret-bottom"
							/>
						</span>
					</ClayButton>
				}
			/>
		);
	};

	const renderDividers = () => {
		if (state.view === VIEW_SPLIT) {
			return (
				<>
					<tr className="publications-render-view-divider table-divider">
						<td
							className="publications-render-view-divider"
							colSpan={2}
						>
							{renderViewDropdown()}
						</td>
					</tr>
					<tr className="publications-render-view-divider table-divider">
						{
							<td className="publications-render-view-divider">
								{Liferay.Language.get('production')}
							</td>
						}

						{
							<td className="publications-render-view-divider">
								{state.renderData.rightTitle}
							</td>
						}
					</tr>
				</>
			);
		}

		return (
			<tr className="publications-render-view-divider table-divider">
				<td className="publications-render-view-divider">
					{renderViewDropdown()}
				</td>
			</tr>
		);
	};

	const getTableRows = (nodes) => {
		const rows = [];

		if (!nodes) {
			return rows;
		}

		let currentTypeName = '';

		const filteredNodes = nodes.sort((a, b) => {
			const typeNameA = a.typeName.toLowerCase();
			const typeNameB = b.typeName.toLowerCase();

			if (typeNameA < typeNameB) {
				return -1;
			}

			if (typeNameA > typeNameB) {
				return 1;
			}

			const titleA = a.title.toLowerCase();
			const titleB = b.title.toLowerCase();

			if (titleA < titleB) {
				return -1;
			}

			if (titleA > titleB) {
				return 1;
			}

			return 0;
		});

		if (!filteredNodes.length) {
			return (
				<ClayEmptyState
					description={Liferay.Language.get(
						'there-are-no-changes-to-display-in-this-view'
					)}
					imgSrc={`${themeDisplay.getPathThemeImages()}/states/search_state.svg`}
					title={Liferay.Language.get('no-results-found')}
				/>
			);
		}

		for (let i = 0; i < filteredNodes.length; i++) {
			const node = filteredNodes[i];

			if (node.typeName !== currentTypeName) {
				currentTypeName = node.typeName;

				rows.push(
					<ClayTable.Row divider key={node.typeName}>
						<ClayTable.Cell>{node.typeName}</ClayTable.Cell>
					</ClayTable.Row>
				);
			}

			rows.push(
				<ClayTable.Row
					className="cursor-pointer"
					key={node.nodeId}
					onClick={() => handleNavigation(node.nodeId)}
				>
					<ClayTable.Cell>
						<div className="publication-name">{node.title}</div>

						{node.description && (
							<div className="publication-description">
								{node.description}
							</div>
						)}
					</ClayTable.Cell>
				</ClayTable.Row>
			);
		}

		return rows;
	};

	const renderEntry = () => {
		if (!state.renderData) {
			if (loading) {
				return (
					<div>
						<span
							aria-hidden="true"
							className="loading-animation"
						/>
					</div>
				);
			}

			return '';
		}
		else if (
			!state.renderData.changeType ||
			state.renderData.errorMessage
		) {
			return (
				<ClayAlert
					displayType="danger"
					spritemap={spritemap}
					title={Liferay.Language.get('error')}
				>
					{state.renderData.errorMessage
						? state.renderData.errorMessage
						: Liferay.Language.get('an-unexpected-error-occurred')}
				</ClayAlert>
			);
		}

		return (
			<ClayTable
				borderless
				className={classNames('publications-render-view', {
					'publications-table':
						state.contentType === CONTENT_TYPE_PARENTS ||
						state.contentType === CONTENT_TYPE_CHILDREN,
				})}
				hover={
					state.contentType === CONTENT_TYPE_PARENTS ||
					state.contentType === CONTENT_TYPE_CHILDREN
				}
				striped={
					state.contentType !== CONTENT_TYPE_WORKFLOW &&
					state.view !== VIEW_SPLIT
				}
			>
				<ClayTable.Head>{renderToolbar()}</ClayTable.Head>

				<ClayTable.Body>
					{(state.contentType === CONTENT_TYPE_PREVIEW ||
						state.contentType === CONTENT_TYPE_RENDER) && (
						<>
							{renderDividers()}

							<tr>
								{(state.view === VIEW_LEFT ||
									state.view === VIEW_SPLIT) && (
									<td
										className={
											state.view === VIEW_SPLIT
												? 'publications-render-view-content publications-render-view-content-split'
												: 'publications-render-view-content'
										}
									>
										{renderPreviewLeft()}
									</td>
								)}

								{(state.view === VIEW_RIGHT ||
									state.view === VIEW_SPLIT) && (
									<td
										className={
											state.view === VIEW_SPLIT
												? 'publications-render-view-content publications-render-view-content-split'
												: 'publications-render-view-content'
										}
									>
										{renderPreviewRight()}
									</td>
								)}

								{state.view === VIEW_UNIFIED && (
									<td className="publications-render-view-content">
										{renderPreviewUnified()}
									</td>
								)}
							</tr>
						</>
					)}

					{state.contentType === CONTENT_TYPE_PARENTS &&
						getTableRows(state.parents)}

					{state.contentType === CONTENT_TYPE_CHILDREN &&
						getTableRows(state.children)}

					{state.contentType === CONTENT_TYPE_WORKFLOW && (
						<tr>
							<td className="publications-render-view-content">
								{renderWorkflowView()}
							</td>
						</tr>
					)}
				</ClayTable.Body>
			</ClayTable>
		);
	};

	const renderToolbar = () => {
		let columns = 1;

		if (
			state.contentType !== CONTENT_TYPE_PARENTS &&
			state.contentType !== CONTENT_TYPE_CHILDREN &&
			state.view === VIEW_SPLIT
		) {
			columns = 2;
		}

		const items = [];

		items.push(
			<ClayNavigationBar.Item
				active={state.contentType === CONTENT_TYPE_PREVIEW}
				key="display"
			>
				<ClayLink
					className={
						!Object.prototype.hasOwnProperty.call(
							state.renderData,
							'leftPreview'
						) &&
						!Object.prototype.hasOwnProperty.call(
							state.renderData,
							'leftLocalizedPreview'
						) &&
						!Object.prototype.hasOwnProperty.call(
							state.renderData,
							'rightPreview'
						) &&
						!Object.prototype.hasOwnProperty.call(
							state.renderData,
							'rightLocalizedPreview'
						)
							? 'btn-link disabled'
							: undefined
					}
					onClick={() => {
						if (
							state &&
							state.view === VIEW_UNIFIED &&
							state.renderData &&
							!Object.prototype.hasOwnProperty.call(
								state.renderData,
								'unifiedPreview'
							) &&
							!Object.prototype.hasOwnProperty.call(
								state.renderData,
								'unifiedLocalizedPreview'
							)
						) {
							setState({
								children: state.children,
								contentType: CONTENT_TYPE_PREVIEW,
								parents: state.parents,
								renderData: state.renderData,
								view: VIEW_SPLIT,
							});

							return;
						}

						setContentType(CONTENT_TYPE_PREVIEW);
					}}
					title={
						!Object.prototype.hasOwnProperty.call(
							state.renderData,
							'leftPreview'
						) &&
						!Object.prototype.hasOwnProperty.call(
							state.renderData,
							'leftLocalizedPreview'
						) &&
						!Object.prototype.hasOwnProperty.call(
							state.renderData,
							'rightPreview'
						) &&
						!Object.prototype.hasOwnProperty.call(
							state.renderData,
							'rightLocalizedPreview'
						)
							? Liferay.Language.get(
									'item-does-not-have-a-content-display'
								)
							: ''
					}
				>
					{Liferay.Language.get('display')}
				</ClayLink>
			</ClayNavigationBar.Item>
		);

		items.push(
			<ClayNavigationBar.Item
				active={state.contentType === CONTENT_TYPE_RENDER}
				key="data"
			>
				<ClayLink onClick={() => setContentType(CONTENT_TYPE_RENDER)}>
					{Liferay.Language.get('data')}
				</ClayLink>
			</ClayNavigationBar.Item>
		);

		if (
			(state.parents && !!state.parents.length) ||
			(state.children && !!state.children.length)
		) {
			items.push(
				<li className="autofit-col nav-item row-divider" key="divider">
					<div />
				</li>
			);

			items.push(
				<ClayNavigationBar.Item
					active={state.contentType === CONTENT_TYPE_PARENTS}
					key="parents"
				>
					<ClayLink
						className={
							state.parents && !!state.parents.length
								? undefined
								: 'btn-link disabled'
						}
						data-tooltip-align="top"
						onClick={() => setContentType(CONTENT_TYPE_PARENTS)}
						title={
							state.parents && !!state.parents.length
								? ''
								: Liferay.Language.get(
										'item-does-not-have-any-parents'
									)
						}
					>
						{Liferay.Language.get('parents')}
					</ClayLink>
				</ClayNavigationBar.Item>
			);

			items.push(
				<ClayNavigationBar.Item
					active={state.contentType === CONTENT_TYPE_CHILDREN}
					key="children"
				>
					<ClayLink
						className={
							state.children && !!state.children.length
								? undefined
								: 'btn-link disabled'
						}
						data-tooltip-align="top"
						onClick={() => setContentType(CONTENT_TYPE_CHILDREN)}
						title={
							state.children && !!state.children.length
								? ''
								: Liferay.Language.get(
										'item-does-not-have-any-children'
									)
						}
					>
						{Liferay.Language.get('children')}
					</ClayLink>
				</ClayNavigationBar.Item>
			);
		}

		if (workflowStatus !== null && showWorkflow) {
			items.push(
				<ClayNavigationBar.Item
					active={state.contentType === CONTENT_TYPE_WORKFLOW}
					key="workflow"
				>
					<ClayLink
						onClick={() =>
							setState((prevState) => ({
								...prevState,
								contentType: CONTENT_TYPE_WORKFLOW,
							}))
						}
					>
						{Liferay.Language.get('workflow')}
					</ClayLink>
				</ClayNavigationBar.Item>
			);
		}

		return (
			<tr>
				<td
					className="publications-render-view-toolbar"
					colSpan={columns}
				>
					<div className="autofit-row">
						<div className="autofit-col">
							<ClayNavigationBar
								spritemap={spritemap}
								triggerLabel={Liferay.Language.get('display')}
							>
								{items}
							</ClayNavigationBar>
						</div>

						{renderDiffLegend()}
					</div>
				</td>
			</tr>
		);
	};

	if (!showHeader) {
		return renderEntry();
	}

	const updatePreviewRender = (segmentsExperienceId) => {
		if (segmentsExperienceId) {
			const newDataURL = createPortletURL(initialDataURL, {
				segmentsExperienceId,
			});

			setDataURL(newDataURL.toString());
			setSelectedSegmentsExperienceId(segmentsExperienceId);
		}
		else {
			console.error(
				'A SegmentsExperience was selected from ExperienceDropdown but no segmentsExperienceId ' +
					'from the selected option was passed into the onSelectionChange method'
			);
		}
	};

	return (
		<div className={`sheet ${loading ? 'publications-loading' : ''}`}>
			{state.renderData && (
				<div className="autofit-row sheet-title">
					<div className="autofit-col autofit-col-expand">
						<div className="align-items-baseline autofit-row mb-2">
							<h2 className="mr-3">{currentTitle}</h2>

							{state.renderData.segmentsExperiences &&
								!!state.renderData.segmentsExperiences
									.length && (
									<ExperienceDropdown
										activeSegmentsExperience={
											state.renderData.segmentsExperiences.filter(
												(experience) =>
													experience.active
											)[0]
										}
										segmentsExperiences={
											state.renderData.segmentsExperiences
										}
										updatePreviewRender={
											updatePreviewRender
										}
									/>
								)}

							{state.renderData.locales &&
								!!state.renderData.locales.length && (
									<LocalizationDropdown
										currentLocale={currentLocale}
										defaultLocale={
											state.renderData.defaultLocale
										}
										locales={state.renderData.locales}
										setSelectedLocale={setSelectedLocale}
										spritemap={spritemap}
									/>
								)}
						</div>

						<div className="entry-description">
							<span>{description} </span>

							<WorkflowStatusLabel
								workflowStatus={
									state.renderData.workflowData
										? state.renderData.workflowData.status
										: workflowStatus
								}
							/>
						</div>
					</div>

					{renderDropdownMenu()}
				</div>
			)}

			<div className="sheet-section">{renderEntry()}</div>
		</div>
	);
}
