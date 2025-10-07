/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render} from '@testing-library/react';
import React from 'react';

import {GlobalContextFrame} from '../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/GlobalContext';

function renderComponent() {
	const {container} = render(
		<GlobalContextFrame useIframe>
			<div>hello</div>
		</GlobalContextFrame>
	);

	const iframe = container.querySelector('iframe');

	iframe.contentWindow.requestAnimationFrame = (fn) => {
		fn();
	};

	return iframe;
}

describe('GlobalContext', () => {
	it('load iframe correctly if the content has a div with master-layout-wrapper id', async () => {
		const iframe = renderComponent();

		iframe.contentWindow.document.open();
		iframe.contentWindow.document.write(
			'<html><head></head><body><div id="master-layout-wrapper"></div></body></html>'
		);

		await act(async () => {
			fireEvent.load(iframe);
		});

		expect(
			iframe.classList.contains(
				'page-editor__global-context-iframe--loading'
			)
		).toBe(false);
	});

	it('load iframe correctly if the content has a div with main-content id', async () => {
		const iframe = renderComponent();

		iframe.contentWindow.document.open();
		iframe.contentWindow.document.write(
			'<html><head></head><body><div id="main-content"></div></body></html>'
		);

		await act(async () => {
			fireEvent.load(iframe);
		});

		expect(
			iframe.classList.contains(
				'page-editor__global-context-iframe--loading'
			)
		).toBe(false);
	});
});
