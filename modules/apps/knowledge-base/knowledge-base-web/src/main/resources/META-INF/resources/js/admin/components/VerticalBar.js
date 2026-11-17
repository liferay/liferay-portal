/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {VerticalBar} from '@clayui/core';
import {
	hideProductMenuIfPresent,
	onProductMenuVisibleChange,
} from '@liferay/layout-js-components-web';
import {navigate} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useEffect, useState} from 'react';

import NavigationPanel from './NavigationPanel';
import SuggestionsPanel from './SuggestionsPanel';
import TemplatesPanel from './TemplatesPanel';

const CSS_EXPANDED = 'expanded';

const DELAY_ANIMATION = 300;

const SUGGESTION_KEY = 'suggestion';

const VerticalNavigationBar = ({
	items,
	moveKBObjectURL,
	parentContainerId,
	portletNamespace,
	productMenuOpen: initialProductMenuOpen,
}) => {
	const parentContainer = document.getElementById(parentContainerId);

	const [productMenuOpen, setProductMenuOpen] = useState(
		initialProductMenuOpen
	);

	const [activePanel, setActivePanel] = useState(
		items.find(({active}) => active)?.key
	);

	const [verticalBarOpen, setVerticalBarOpen] = useState(
		!initialProductMenuOpen && activePanel !== SUGGESTION_KEY
	);

	useEffect(() => {
		const onProductMenuChange = (open) => {
			setProductMenuOpen(open);

			if (open) {
				setVerticalBarOpen(false);
			}
			else {
				if (activePanel !== SUGGESTION_KEY) {
					setTimeout(() => {
						setVerticalBarOpen(true);
					}, DELAY_ANIMATION);
				}
			}
		};

		const handler = onProductMenuVisibleChange(onProductMenuChange);

		return () => {
			handler.removeListener();
		};
	}, [activePanel]);

	useEffect(() => {
		if (!initialProductMenuOpen) {
			return;
		}

		const timeoutId = setTimeout(() => {

			// The product menu may have closed itself before this listens, as
			// it does on narrow screens, so sync once it is hidden.

			hideProductMenuIfPresent({
				onHide: () => {
					setProductMenuOpen(false);
					setVerticalBarOpen(
						items.find(({active}) => active)?.key !== SUGGESTION_KEY
					);
				},
			});
		}, DELAY_ANIMATION);

		return () => clearTimeout(timeoutId);
	}, [initialProductMenuOpen, items]);

	useEffect(() => {
		parentContainer.classList.toggle(
			CSS_EXPANDED,
			Boolean(
				activePanel !== SUGGESTION_KEY && verticalBarOpen && activePanel
			)
		);

		if (activePanel === SUGGESTION_KEY) {
			parentContainer.classList.add('not-expandable');
		}
	}, [activePanel, parentContainer, verticalBarOpen]);

	const onActiveChange = (currentActivePanelKey) => {
		if (currentActivePanelKey === SUGGESTION_KEY) {
			setVerticalBarOpen(false);
		}
		else {
			setVerticalBarOpen(
				(currenVerticalBarOpen) =>
					Boolean(currentActivePanelKey) &&
					!(
						currentActivePanelKey === activePanel &&
						currenVerticalBarOpen
					)
			);
		}

		if (currentActivePanelKey) {
			const shouldHideProductMenu =
				productMenuOpen && currentActivePanelKey !== SUGGESTION_KEY;

			if (currentActivePanelKey !== activePanel) {
				setActivePanel(currentActivePanelKey);

				const href = items.find(
					({key}) => key === currentActivePanelKey
				)?.href;

				if (shouldHideProductMenu) {
					hideProductMenuIfPresent({onHide: () => navigate(href)});
				}
				else {
					navigate(href);
				}
			}
			else if (shouldHideProductMenu) {
				hideProductMenuIfPresent();
			}
		}
	};

	const VerticalBarPanels = {
		article: NavigationPanel,
		suggestion: SuggestionsPanel,
		template: TemplatesPanel,
	};

	return (
		<VerticalBar
			absolute
			active={verticalBarOpen && activePanel ? activePanel : null}
			onActiveChange={onActiveChange}
			position="left"
		>
			<VerticalBar.Bar displayType="light" items={items}>
				{(item) => (
					<VerticalBar.Item key={item.key}>
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get(item.title)}
							data-tooltip-align="right"
							displayType="unstyled"
							symbol={item.icon}
							title={Liferay.Language.get(item.title)}
						/>
					</VerticalBar.Item>
				)}
			</VerticalBar.Bar>

			<VerticalBar.Content items={items}>
				{(item) => {
					const PanelComponent = VerticalBarPanels[item.key];

					return (
						<VerticalBar.Panel key={item.key}>
							<div className="sidebar-header">
								<div className="component-title">
									{item.title}
								</div>
							</div>

							<div className="sidebar-body">
								<PanelComponent
									items={item.navigationItems}
									moveKBObjectURL={moveKBObjectURL}
									portletNamespace={portletNamespace}
									selectedItemId={item.selectedItemId}
								/>
							</div>
						</VerticalBar.Panel>
					);
				}}
			</VerticalBar.Content>
		</VerticalBar>
	);
};

const itemShape = {
	active: PropTypes.bool,
	href: PropTypes.string.isRequired,
	icon: PropTypes.string.isRequired,
	key: PropTypes.string.isRequired,
	title: PropTypes.string.isRequired,
};

VerticalNavigationBar.propTypes = {
	items: PropTypes.arrayOf(PropTypes.shape(itemShape)),
	moveKBObjectURL: PropTypes.string,
	parentContainerId: PropTypes.string.isRequired,
	portletNamespace: PropTypes.string.isRequired,
	productMenuOpen: PropTypes.bool,
};

export default VerticalNavigationBar;
