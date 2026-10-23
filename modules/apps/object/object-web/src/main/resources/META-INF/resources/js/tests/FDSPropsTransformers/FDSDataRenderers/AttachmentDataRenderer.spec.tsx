/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import AttachmentDataRenderer from '../../../components/FDSPropsTransformer/FDSDataRenderers/AttachmentDataRenderer';

const ATTACHMENT = {
	link: {
		href: '/documents/report.pdf',
		label: 'report.pdf',
	},
	size: '94 KB',
};

describe('The AttachmentDataRenderer component', () => {
	it('links the file name to the download URL', () => {
		render(<AttachmentDataRenderer value={ATTACHMENT} />);

		expect(screen.getByRole('link', {name: /report\.pdf/})).toHaveAttribute(
			'href',
			'/documents/report.pdf'
		);
	});

	it('renders nothing when the entry has no attachment', () => {
		const {container} = render(<AttachmentDataRenderer />);

		expect(container).toBeEmptyDOMElement();
	});

	it('shows the file size', () => {
		render(<AttachmentDataRenderer value={ATTACHMENT} />);

		expect(screen.getByText('94 KB')).toBeInTheDocument();
	});
});
