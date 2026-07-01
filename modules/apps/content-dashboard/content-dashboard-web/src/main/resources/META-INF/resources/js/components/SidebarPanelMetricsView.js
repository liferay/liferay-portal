/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {setCSPNonce} from 'frontend-js-web';
import React, {useEffect, useRef} from 'react';

import Sidebar from './Sidebar';

const SidebarPanelMetricsView = ({html}) => {
	const elementRef = useRef();

	useEffect(() => {
		const fragment = document
			.createRange()
			.createContextualFragment(setCSPNonce(html));

		elementRef.current.innerHTML = '';

		elementRef.current.appendChild(fragment);
	}, [html]);

	return (
		<>
			<Sidebar.Header
				title={Liferay.Language.get('content-performance')}
			/>

			<Sidebar.Body className="c-p-0">
				<div ref={elementRef}></div>
			</Sidebar.Body>
		</>
	);
};

export default SidebarPanelMetricsView;
