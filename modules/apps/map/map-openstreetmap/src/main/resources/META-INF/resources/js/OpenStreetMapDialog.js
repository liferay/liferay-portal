/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import L from 'leaflet';

import 'leaflet/dist/leaflet.css';

/**
 * OpenStreetMapDialog
 * @review
 */
export default class OpenStreetMapDialog {

	/**
	 * Creates a new map dialog using OpenStreetMap's API
	 * @review
	 */
	constructor(args) {
		this._dialog = L.popup({
			className: 'leaflet-popup',
			minWidth: 400,
		});

		this.map = args.map;
	}

	/**
	 * Opens the dialog with the given map attribute and passes
	 * the given configuration to the dialog object.
	 * @param {Object} cfg
	 * @review
	 */
	open(cfg) {
		this._dialog.setContent(cfg.content);
		this._dialog.setLatLng(cfg.position);

		this._dialog.options.offset = cfg.marker.options.icon.options
			.popupAnchor || [0, 0];

		this._dialog.openOn(this.map);
	}
}
