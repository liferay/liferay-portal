/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import 'leaflet/dist/leaflet.css';
import {StrictMode} from 'react';
import {createRoot} from 'react-dom/client';

import Map from './components/Map.jsx';
import './index.css';

class WebComponent extends HTMLElement {
	connectedCallback() {
		this.root = createRoot(this);

		this.root.render(
			<StrictMode>
				<Map />
			</StrictMode>
		);
	}

	disconnectedCallback() {
		this.root.unmount();

		delete this.root;
	}
}

const ELEMENT_ID = 'clarity-solution-custom-element-distributors-map-osm';

if (!customElements.get(ELEMENT_ID)) {
	customElements.define(ELEMENT_ID, WebComponent);
}
