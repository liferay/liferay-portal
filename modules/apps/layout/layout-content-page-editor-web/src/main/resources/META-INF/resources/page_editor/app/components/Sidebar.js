/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ReactPortal, useStateSafe} from '@liferay/frontend-js-react-web';
import {Resizer} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {useId, useSessionState} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useRef} from 'react';

import BrowserSidebar from '../../plugins/browser/components/BrowserSidebar';
import CommentsSidebar from '../../plugins/comments/components/CommentsSidebar';
import FragmentsSidebar from '../../plugins/fragments_and_widgets/components/FragmentsSidebar';
import MappingSidebar from '../../plugins/mapping/components/MappingSidebar';
import ContentsSidebar from '../../plugins/page_content/components/ContentsSidebar';
import PageDesignOptionsSidebar from '../../plugins/page_design_options/components/PageDesignOptionsSidebar';
import RulesSidebar from '../../plugins/page_rules/components/RulesSidebar';
import {VIEWPORT_SIZES} from '../config/constants/viewportSizes';
import {config} from '../config/index';
import {useSelectItem} from '../contexts/ControlsContext';
import {useSetOpenShortcutModal} from '../contexts/ShortcutContext';
import {useDispatch, useSelector} from '../contexts/StoreContext';
import selectAvailablePanels from '../selectors/selectAvailablePanels';
import selectItemConfigurationOpen from '../selectors/selectItemConfigurationOpen';
import selectSidebarIsOpened from '../selectors/selectSidebarIsOpened';
import switchSidebarPanel from '../thunks/switchSidebarPanel';
import {useDropClear} from '../utils/drag_and_drop/useDragAndDrop';
import isSmallResolution from '../utils/isSmallResolution';

const {useEffect} = React;

export const MAX_SIDEBAR_WIDTH = 500;
export const MIN_SIDEBAR_WIDTH = 280;
export const SIDEBAR_WIDTH_RESIZE_STEP = 20;

function getActiveSidebarPanel({
	sidebarPanelId,
	sidebarPanels,
	sidebarPanelsMap,
}) {
	if (sidebarPanelsMap[sidebarPanelId]) {
		return {sidebarPanel: sidebarPanelsMap[sidebarPanelId], sidebarPanelId};
	}

	const panel = sidebarPanels[0];

	return {sidebarPanel: panel, sidebarPanelId: panel.sidebarPanelId};
}

const getOpenShortcutModalTooltipMarkup = () =>
	`
	<div>${Liferay.Language.get('open-keyboard-shortcuts')}</div>
	<kbd class="c-kbd c-kbd-dark mt-1">
		<kbd class="c-kbd">⇧</kbd>

		<span class="c-kbd-separator">+</span>

		<kbd class="c-kbd">?</kbd>
	</kbd>
`
		.replaceAll('\n', '')
		.replaceAll('\t', '');

export default function Sidebar() {
	const dropClearRef = useDropClear();
	const [hasError, setHasError] = useStateSafe(false);
	const dispatch = useDispatch();
	const selectItem = useSelectItem();
	const setOpenShortcutModal = useSetOpenShortcutModal();
	const shortcutButtonTitleId = useId();
	const sidebarContentId = useId();
	const sidebarId = useId();
	const sidebar = useSelector((state) => state.sidebar);

	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const [sidebarWidth, setSidebarWidth] = useSessionState(
		`${config.portletNamespace}_sidebar-width`,
		MIN_SIDEBAR_WIDTH
	);

	const shouldFocusSidebarContentRef = useRef(false);
	const sidebarContentRef = useRef();
	const tabListRef = useRef();

	const sidebarPanels = useSelector(
		selectAvailablePanels(config.sidebarPanels)
	);
	const sidebarHidden = sidebar.hidden;
	const sidebarOpen = selectSidebarIsOpened({sidebar});
	const itemConfigurationOpen = selectItemConfigurationOpen({sidebar});

	const {sidebarPanel, sidebarPanelId} = getActiveSidebarPanel({
		sidebarPanelId: sidebar.panelId,
		sidebarPanels,
		sidebarPanelsMap: config.sidebarPanelsMap,
	});

	useEffect(() => {
		const wrapper = document.getElementById('wrapper');

		if (!wrapper) {
			return;
		}

		wrapper.classList.add('page-editor__wrapper');

		wrapper.classList.toggle(
			'page-editor__wrapper--padded-start',
			sidebarOpen
		);

		wrapper.classList.toggle(
			'page-editor__wrapper--sidebar--hidden',
			sidebarHidden
		);

		wrapper.classList.toggle(
			'page-editor__wrapper--padded-end',
			itemConfigurationOpen
		);

		return () => {
			wrapper.classList.remove('page-editor__wrapper');
			wrapper.classList.remove('page-editor__wrapper--padded-start');
			wrapper.classList.remove('page-editor__wrapper--padded-end');
		};
	}, [sidebarHidden, sidebarOpen, itemConfigurationOpen]);

	useEffect(() => {
		if (sidebarOpen && shouldFocusSidebarContentRef.current) {
			shouldFocusSidebarContentRef.current = false;

			sidebarContentRef.current?.focus({preventScroll: true});
		}
	}, [sidebarOpen, sidebarPanelId]);

	useEffect(() => {
		const wrapper = document.getElementById('wrapper');

		if (!wrapper || selectedViewportSize === VIEWPORT_SIZES.desktop) {
			return;
		}

		wrapper.classList.add('overflow-hidden');

		return () => {
			if (wrapper) {
				wrapper.classList.remove('overflow-hidden');
			}
		};
	}, [selectedViewportSize]);

	const deselectItem = (event) => {
		if (event.target === event.currentTarget) {
			selectItem(null);
		}
	};

	const handleClick = (panel) => {
		const open =
			panel.sidebarPanelId === sidebarPanelId ? !sidebarOpen : true;

		const smallResolution = isSmallResolution();

		dispatch(
			switchSidebarPanel({
				itemConfigurationOpen: smallResolution
					? false
					: sidebar.itemConfigurationOpen,
				sidebarOpen: open,
				sidebarPanelId: panel.sidebarPanelId,
			})
		);

		if (open) {
			shouldFocusSidebarContentRef.current = true;
		}
	};

	const handleTabKeyDown = (event) => {
		if (event.key === 'ArrowUp' || event.key === 'ArrowDown') {
			const tabs = Array.from(
				tabListRef.current.querySelectorAll('button')
			);

			const positionActiveTab = tabs.indexOf(document.activeElement);

			const activeTab =
				tabs[
					event.key === 'ArrowUp'
						? positionActiveTab - 1
						: positionActiveTab + 1
				];

			if (activeTab) {
				activeTab.focus();
			}
		}
	};

	const shortcutButtonTitle = getOpenShortcutModalTooltipMarkup();

	return (
		<ReactPortal className="cadmin">
			<div
				className="page-editor__sidebar page-editor__theme-adapter-forms"
				ref={dropClearRef}
				style={{'--sidebar-content-width': `${sidebarWidth}px`}}
			>
				<div
					className={classNames('page-editor__sidebar__buttons', {
						'page-editor__sidebar__buttons--hidden': sidebarHidden,
					})}
				>
					<div
						aria-orientation="vertical"
						onClick={deselectItem}
						onKeyDown={handleTabKeyDown}
						ref={tabListRef}
						role="tablist"
					>
						{sidebarPanels.map((panel) => {
							const active =
								sidebarOpen &&
								panel.sidebarPanelId === sidebarPanelId;
							const {icon, label} = panel;

							return (
								<ClayButtonWithIcon
									aria-controls={sidebarContentId}
									aria-label={panel.label}
									aria-selected={active}
									className={classNames({active})}
									data-panel-id={panel.sidebarPanelId}
									data-tooltip-align="left"
									displayType="unstyled"
									id={`${sidebarId}${panel.sidebarPanelId}`}
									key={panel.sidebarPanelId}
									onClick={() => handleClick(panel)}
									role="tab"
									size="sm"
									symbol={icon}
									tabIndex={
										panel.sidebarPanelId !== sidebarPanelId
											? '-1'
											: null
									}
									title={label}
								/>
							);
						})}
					</div>

					<ClayButtonWithIcon
						aria-haspopup="dialog"
						aria-labelledby={shortcutButtonTitleId}
						className="mt-auto"
						data-title={shortcutButtonTitle}
						data-title-set-as-html
						data-tooltip-align="left"
						displayType="unstyled"
						id={`${sidebarId}keyboard_shortcuts`}
						onClick={() => setOpenShortcutModal(true)}
						size="sm"
						symbol="question-circle-full"
					/>
				</div>

				<div className="sr-only" id={shortcutButtonTitleId}>
					{shortcutButtonTitle}
				</div>

				<div
					aria-label={sub(
						Liferay.Language.get('x-panel'),
						sidebarPanel.label
					)}
					className={classNames({
						'page-editor__sidebar__content': true,
						'page-editor__sidebar__content--open': sidebarOpen,
						'rtl':
							Liferay.Language.direction?.[
								themeDisplay?.getLanguageId()
							] === 'rtl',
					})}
					id={sidebarContentId}
					onClick={deselectItem}
					ref={sidebarContentRef}
					role="tabpanel"
					tabIndex="-1"
				>
					{hasError ? (
						<div>
							<ClayButton
								block
								displayType="secondary"
								onClick={() => {
									dispatch(
										switchSidebarPanel({
											sidebarOpen: false,
											sidebarPanelId:
												config.sidebarPanels[0],
										})
									);
									setHasError(false);
								}}
								size="sm"
							>
								{Liferay.Language.get('refresh')}
							</ClayButton>
						</div>
					) : (
						<ErrorBoundary
							handleError={() => {
								setHasError(true);
							}}
						>
							<SidebarPanel
								sidebarPanelId={sidebarPanel.sidebarPanelId}
							/>
						</ErrorBoundary>
					)}
				</div>

				<Resizer
					ariaControls={sidebarContentId}
					ariaLabel={Liferay.Language.get('resize-sidebar')}
					className="page-editor__sidebar__resizer"
					maxWidth={MAX_SIDEBAR_WIDTH}
					minWidth={MIN_SIDEBAR_WIDTH}
					resizeStep={SIDEBAR_WIDTH_RESIZE_STEP}
					setWidth={setSidebarWidth}
					targetRef={sidebarContentRef}
					width={sidebarWidth}
				/>
			</div>
		</ReactPortal>
	);
}

const PANEL_COMPONENTS = {
	browser: BrowserSidebar,
	comments: CommentsSidebar,
	fragments_and_widgets: FragmentsSidebar,
	mapping: MappingSidebar,
	page_content: ContentsSidebar,
	page_design_options: PageDesignOptionsSidebar,
	page_rules: RulesSidebar,
};

const SidebarPanel = React.memo(({sidebarPanelId}) => {
	const Component = PANEL_COMPONENTS[sidebarPanelId];

	if (Component !== null) {
		return <Component />;
	}

	return null;
});

class ErrorBoundary extends React.Component {
	static getDerivedStateFromError(_error) {
		return {hasError: true};
	}

	constructor(props) {
		super(props);

		this.state = {hasError: false};
	}

	componentDidCatch(error) {
		if (this.props.handleError) {
			this.props.handleError(error);
		}
	}

	render() {
		if (this.state.hasError) {
			return null;
		}
		else {
			return this.props.children;
		}
	}
}
