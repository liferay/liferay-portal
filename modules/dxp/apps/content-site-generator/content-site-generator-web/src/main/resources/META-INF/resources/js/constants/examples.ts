/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {Example} from '../types/Example';

export const EXAMPLES: Example[] = [
	{
		icon: 'document',
		label: Liferay.Language.get(
			'build-25-blog-articles-about-low-code-development'
		),
	},
	{
		icon: 'pencil',
		label: Liferay.Language.get(
			'create-5-blog-articles-about-industry-trends'
		),
	},
	{
		icon: 'home',
		label: Liferay.Language.get(
			'build-a-landing-page-with-hero-section-and-features'
		),
	},
	{
		icon: 'books',
		label: Liferay.Language.get(
			'create-75-glossary-pages-explaining-technical-terms-related-to-digital-experience-platforms'
		),
	},
];
