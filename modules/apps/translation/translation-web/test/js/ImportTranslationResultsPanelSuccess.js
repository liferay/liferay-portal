/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import ImportTranslationResultsPanelSuccess from '../../src/main/resources/META-INF/resources/js/ImportTranslationResultsPanelSuccess';

const baseProps = {
	defaultExpanded: false,
	files: [
		'filename-en_US-ar_SA.xlf',
		'filename-en_US-ca_ES.xlf',
		'filename-en_US-de_DE.xlf',
		'filename-en_US-es_ES.xlf',
		'filename-en_US-fi_FI.xlf',
		'filename-en_US-fr_FR.xlf',
		'filename-en_US-hu_HU.xlf',
		'filename-en_US-ja_JP.xlf',
		'filename-en_US-nl_NL.xlf',
		'filename-en_US-pt_BR.xlf',
		'filename-en_US-sv_SE.xlf',
		'filename-en_US-zh_CN.xlf',
	],
	title: 'All Files Published',
};

const renderComponent = (props) =>
	render(<ImportTranslationResultsPanelSuccess {...props} />);

describe('ImportTranslationResultsPanelSuccess', () => {
	afterEach(cleanup);

	it('renders closed (default)', () => {
		const {asFragment} = renderComponent(baseProps);

		expect(asFragment()).toMatchSnapshot();
	});

	it('renders expanded', () => {
		const {asFragment} = renderComponent({
			...baseProps,
			defaultExpanded: true,
		});

		expect(asFragment()).toMatchSnapshot();
	});
});
