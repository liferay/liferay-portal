/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {SidePanel} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayPanel from '@clayui/panel';
import {createPortletURL, fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

const DELTA = 20;

export default function ChangeTrackingLayoutChangesSidePanel({
	containerRef,
	handleOpenSidePanel,
	isOpen,
	layoutContentChangesURL,
	namespace,
	spritemap,
}) {
	const [cur, setCur] = useState(0);
	const [error, setError] = useState('');
	const [isExpanded, setIsExpanded] = useState(true);
	const [newPanels, setNewPanels] = useState([]);
	const [panels, setPanels] = useState([]);
	const [total, setTotal] = useState(0);

	useEffect(() => {
		const dataURL =
			layoutContentChangesURL +
			'&' +
			namespace +
			'cur' +
			'=' +
			encodeURIComponent(cur);

		fetch(dataURL)
			.then((response) => {
				if (response.ok) {
					return response.json();
				}
			})
			.then((json) => {
				if (json === null) {
					return;
				}
				else if (json.errorMessage) {
					setError(json.errorMessage);

					return;
				}

				setNewPanels(json.layoutContentChanges);
				setTotal(json.total);
			})
			.catch(() => {
				setError(Liferay.Language.get('an-unexpected-error-occurred'));
			});
	}, [cur, layoutContentChangesURL, namespace]);

	useEffect(() => {
		if (!newPanels.length) {
			return;
		}

		const updatedPanels = [];

		for (const newPanel of newPanels) {
			updatedPanels.push({...newPanel, expanded: isExpanded});
		}

		setPanels((prevPanels) => prevPanels.concat(updatedPanels));
		setNewPanels([]);
	}, [isExpanded, newPanels]);

	const handleExpanded = (id) => {
		const updatedPanels = panels.map((panel) => {
			if (panel.ctEntryId === id) {
				return {...panel, expanded: !panel.expanded};
			}

			return panel;
		});

		setPanels(updatedPanels);
	};

	const renderContent = () => {
		if (error) {
			return (
				<ClayAlert displayType="danger" spritemap={spritemap}>
					{error}
				</ClayAlert>
			);
		}
		else if (panels && panels.length) {
			return (
				<>
					<ClayButton.Group spaced>
						<ClayButton
							aria-label={Liferay.Language.get('expand-all')}
							className="c-mr-2 text-2 text-secondary text-weight-bold"
							displayType="unstyled"
							onClick={() => setExpandedAll(true)}
							size="xs"
						>
							{Liferay.Language.get('expand-all')}
						</ClayButton>

						<ClayButton
							aria-label={Liferay.Language.get('collapse-all')}
							className="c-mr-2 text-2 text-secondary text-weight-bold"
							displayType="unstyled"
							onClick={() => setExpandedAll(false)}
							size="xs"
						>
							{Liferay.Language.get('collapse-all')}
						</ClayButton>
					</ClayButton.Group>
					{panels.map((panel) => {
						const viewChangeURL = createPortletURL(
							panel.viewChangeURL,
							{
								backURL:
									window.location.pathname +
									window.location.search,
							}
						).toString();

						return (
							<ClayPanel
								collapsable
								displayTitle={
									<ClayPanel.Title className="panel-title text-secondary">
										{panel.title}
									</ClayPanel.Title>
								}
								displayType="unstyled"
								expanded={panel.expanded}
								key={panel.ctEntryId}
								onExpandedChange={() => {
									handleExpanded(panel.ctEntryId);
								}}
								showCollapseIcon={true}
							>
								<ClayPanel.Body>
									<div
										className="mb-4 mt-3 taglib-diff-html"
										dangerouslySetInnerHTML={{
											__html: panel.preview,
										}}
									/>

									<a
										className="btn btn-secondary btn-xs"
										href={viewChangeURL}
									>
										{Liferay.Language.get('view-details')}
									</a>
								</ClayPanel.Body>
							</ClayPanel>
						);
					})}
					{panels.length < total ? (
						<div className="align-items-center d-flex justify-content-center">
							<ClayButton
								aria-label={Liferay.Language.get('view-more')}
								className="text-weight-bold"
								displayType="secondary"
								onClick={() => setCur(cur + DELTA)}
								style={{margin: '24px'}}
							>
								{Liferay.Language.get('view-more')}
							</ClayButton>
						</div>
					) : null}
				</>
			);
		}
		else {
			return (
				<ClayEmptyState
					className="align-items-center mt-n4 px-4"
					description=""
					small
					title={Liferay.Language.get(
						'no-content-changes-were-found-on-this-page'
					)}
				/>
			);
		}
	};

	const setExpandedAll = (expanded) => {
		const updatedPanels = panels.map((panel) => {
			return {...panel, expanded};
		});

		setPanels(updatedPanels);
		setIsExpanded(expanded);
	};

	return (
		<div className="publications-layout-changes-sidebar">
			<SidePanel
				aria-label="Layout Changes Side Panel"
				containerRef={containerRef}
				onOpenChange={handleOpenSidePanel}
				open={isOpen}
				panelWidth={472}
			>
				<SidePanel.Header>
					<SidePanel.Title>
						<span>
							<ClayIcon
								className="mr-2"
								spritemap={spritemap}
								symbol="columns"
							/>
						</span>

						<span>
							{Liferay.Language.get('content-changes') +
								' (' +
								total +
								')'}
						</span>
					</SidePanel.Title>
				</SidePanel.Header>

				<SidePanel.Body>{renderContent()}</SidePanel.Body>
			</SidePanel>
		</div>
	);
}
