/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import {createRoot} from 'react-dom/client';

import App from './App';

import './styles/index.css';

class LiferaySampleCustomElement extends HTMLElement {
	connectedCallback() {
		createRoot(this).render(<App />);
	}
}

if (!customElements.get('liferay-sample-custom-element-6')) {
	customElements.define(
		'liferay-sample-custom-element-6',
		LiferaySampleCustomElement
	);
}
