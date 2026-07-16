/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import {ScopeIndicator} from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/fragments_and_widgets/components/ScopeIndicator';

describe('ScopeIndicator', () => {
	it('renders the global sticker with the global-site tooltip', () => {
		const {container} = render(
			<ScopeIndicator
				scope={{id: '1', label: 'Global', type: 'global'}}
			/>
		);

		const sticker = container.querySelector(
			'.page-editor__scope-indicator--global'
		);

		expect(sticker).toBeInTheDocument();
		expect(sticker).toHaveClass('lfr-portal-tooltip');
		expect(sticker).toHaveAttribute(
			'title',
			Liferay.Language.get('global-site')
		);
		expect(
			container.querySelector('.lexicon-icon-globe-lines')
		).toBeInTheDocument();
	});

	it('renders the design library sticker with its name as tooltip', () => {
		const {container} = render(
			<ScopeIndicator
				scope={{id: '2', label: 'Gerardo DL', type: 'design-library'}}
			/>
		);

		const sticker = container.querySelector(
			'.page-editor__scope-indicator--design-library'
		);

		expect(sticker).toBeInTheDocument();
		expect(sticker).toHaveAttribute('title', 'Gerardo DL');
		expect(
			container.querySelector('.lexicon-icon-books-brush')
		).toBeInTheDocument();
	});

	it('renders nothing for a regular site scope', () => {
		const {container} = render(
			<ScopeIndicator scope={{id: '3', label: 'My Site', type: 'site'}} />
		);

		expect(
			container.querySelector('.page-editor__scope-indicator')
		).not.toBeInTheDocument();
	});
});
