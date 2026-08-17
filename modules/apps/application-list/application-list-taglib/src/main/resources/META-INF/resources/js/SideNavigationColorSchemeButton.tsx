/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import React, {useEffect, useState} from 'react';

function SideNavigationColorSchemeButton({
	className,
	colorScheme: initialColorScheme,
	colorSchemeSessionKey,
}: {
	className?: string;
	colorScheme: 'dark' | 'light';
	colorSchemeSessionKey: string;
}) {
	const [colorScheme, setColorScheme] = useState<'dark' | 'light'>(() => {
		const current = document.documentElement.dataset.colorScheme;

		return current === 'dark' || current === 'light'
			? current
			: initialColorScheme;
	});

	useEffect(() => {
		if (document.documentElement.dataset.colorScheme !== colorScheme) {
			document.documentElement.dataset.colorScheme = colorScheme;
		}
	}, [colorScheme]);

	useEffect(() => {
		const observer = new MutationObserver((mutations) => {
			mutations.forEach((mutation) => {
				if (
					mutation.type === 'attributes' &&
					mutation.attributeName === 'data-color-scheme'
				) {
					const newValue =
						document.documentElement.dataset.colorScheme;
					if (newValue === 'dark' || newValue === 'light') {
						setColorScheme(newValue);
					}
				}
			});
		});

		observer.observe(document.documentElement, {
			attributeFilter: ['data-color-scheme'],
			attributes: true,
		});

		return () => observer.disconnect();
	}, []);

	const toggle = async () => {
		const nextColorScheme = colorScheme === 'dark' ? 'light' : 'dark';

		setColorScheme(nextColorScheme);

		await Liferay.Util.Session.set(colorSchemeSessionKey, nextColorScheme);
	};

	const label =
		colorScheme === 'dark'
			? Liferay.Language.get('switch-to-light-mode')
			: Liferay.Language.get('switch-to-dark-mode');

	return (
		<ClayButtonWithIcon
			aria-label={label}
			aria-pressed={colorScheme === 'dark'}
			borderless
			className={className}
			displayType="secondary"
			monospaced
			onClick={toggle}
			size="sm"
			symbol={colorScheme === 'dark' ? 'sun' : 'moon'}
			title={label}
		/>
	);
}

export default SideNavigationColorSchemeButton;
