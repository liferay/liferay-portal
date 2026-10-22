/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import classNames from 'classnames';
import React, {useEffect, useRef, useState} from 'react';

const COLOR_SCHEME_SESSION_KEY =
	'com_liferay_application_list_taglib_SideNavigationColorScheme';

const REDUCED_MOTION_CLASS_NAME = 'c-prefers-reduced-motion';

function SideNavigationColorSchemeButton({
	className,
	colorScheme: initialColorScheme = 'light',
	colorSchemeSessionKey = COLOR_SCHEME_SESSION_KEY,
}: {
	className?: string;
	colorScheme?: 'dark' | 'light';
	colorSchemeSessionKey?: string;
}) {
	const [colorScheme, setColorScheme] = useState<'dark' | 'light'>(() => {
		const current = document.documentElement.dataset.colorScheme;

		return current === 'dark' || current === 'light'
			? current
			: initialColorScheme;
	});

	const reducedMotionAddedRef = useRef(false);

	useEffect(() => {
		if (document.documentElement.dataset.colorScheme !== colorScheme) {
			document.documentElement.dataset.colorScheme = colorScheme;
		}

		if (!reducedMotionAddedRef.current) {
			return;
		}

		let frame = requestAnimationFrame(() => {
			frame = requestAnimationFrame(() => {
				reducedMotionAddedRef.current = false;

				document.body.classList.remove(REDUCED_MOTION_CLASS_NAME);
			});
		});

		return () => cancelAnimationFrame(frame);
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

		if (!document.body.classList.contains(REDUCED_MOTION_CLASS_NAME)) {
			reducedMotionAddedRef.current = true;

			document.body.classList.add(REDUCED_MOTION_CLASS_NAME);
		}

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
			className={classNames('lfr-portal-tooltip', className)}
			data-title={label}
			displayType="secondary"
			monospaced
			onClick={toggle}
			size="sm"
			symbol={colorScheme === 'dark' ? 'sun' : 'moon'}
		/>
	);
}

export default SideNavigationColorSchemeButton;
