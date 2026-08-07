/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import {Root, createRoot} from 'react-dom/client';

import App from './App';

// The "Delegated Filters" tab of the Frontend Data Set Sample widget: a data
// set that declares filters and shows no filter UI, so that this element
// provides it.

const DEFAULT_FDS_NAME =
	'com_liferay_frontend_data_set_sample_web_internal_portlet_FDSSamplePortlet-delegatedFilters';

const ELEMENT_NAME = 'liferay-sample-custom-element-7';

class LiferaySampleCustomElement extends HTMLElement {
	private _root?: Root;

	connectedCallback() {
		const fdsName = this.getAttribute('fds-name') || DEFAULT_FDS_NAME;

		this._root = createRoot(this);
		this._root.render(<App fdsName={fdsName} />);
	}

	disconnectedCallback() {
		this._root?.unmount();
	}
}

if (!customElements.get(ELEMENT_NAME)) {
	customElements.define(ELEMENT_NAME, LiferaySampleCustomElement);
}
