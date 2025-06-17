/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SidePanel} from '@clayui/core';
import React from 'react';

const InfoPanelFooter = () => <SidePanel.Footer>Footer</SidePanel.Footer>;

const InfoPanelHeader = () => (
	<SidePanel.Header>
		<SidePanel.Title>Title from the sample</SidePanel.Title>
	</SidePanel.Header>
);

const SampleInfoPanel = function ({items}: {items: any}) {
	if (!items.length) {
		return (
			<>
				<InfoPanelHeader />
				<SidePanel.Body>
					Content from propsTransformer: <b>No items selected</b>
				</SidePanel.Body>
				<InfoPanelFooter />
			</>
		);
	}
	else if (items.length === 1) {
		return (
			<>
				<InfoPanelHeader />
				<SidePanel.Body>
					<h2>Content from propsTransformer</h2>

					<dl className="property-list">
						<dt>Creator:</dt>

						<dd>{items[0].creator.name}</dd>

						<dt>Description:</dt>

						<dd>{items[0].description}</dd>

						<dt>Date</dt>

						<dd>{items[0].dateCreated}</dd>
					</dl>
				</SidePanel.Body>
				<InfoPanelFooter />
			</>
		);
	}
	else {
		return (
			<>
				<InfoPanelHeader />
				<SidePanel.Body>
					Content from propsTransformer. <b>Items selected:</b>{' '}

					{items.length}
				</SidePanel.Body>
				<InfoPanelFooter />
			</>
		);
	}
};

export default SampleInfoPanel;
