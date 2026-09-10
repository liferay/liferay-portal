/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {StrictMode} from 'react';
import {createRoot} from 'react-dom/client';

import App from './App.jsx';

class WebComponent extends HTMLElement {
	connectedCallback() {
		this.root = createRoot(this);

		this.root.render(
			<StrictMode>
				<App
					title={this.getAttribute('title')}
					datasetLabel={this.getAttribute('dataset-label')}
					aggregationField={this.getAttribute('aggregation-field')}
					aggregationType={this.getAttribute('aggregation-type')}
					restContextPath={this.getAttribute('rest-context-path')}
					color={this.getAttribute('color')}
				/>
			</StrictMode>
		);
	}

	disconnectedCallback() {
		this.root.unmount();

		delete this.root;
	}
}

const ELEMENT_ID = 'clarity-solution-bar-chart';

if (!customElements.get(ELEMENT_ID)) {
	customElements.define(ELEMENT_ID, WebComponent);
}
