/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {
	createContext,
	useCallback,
	useContext,
	useEffect,
	useRef,
	useState,
} from 'react';

const AnnouncerContext = createContext<(_message: string) => void>(() => {});

export function useAnnouncer() {
	return useContext(AnnouncerContext);
}

export function AnnouncerProvider({children}: {children: React.ReactNode}) {
	const [message, setMessage] = useState('');
	const timeoutRef = useRef<number>();

	useEffect(() => {
		return () => window.clearTimeout(timeoutRef.current);
	}, []);

	const announce = useCallback((next: string) => {
		setMessage('');

		window.clearTimeout(timeoutRef.current);

		timeoutRef.current = window.setTimeout(() => setMessage(next), 50);
	}, []);

	return (
		<AnnouncerContext.Provider value={announce}>
			{children}

			<div
				aria-live="polite"
				className="editor-announcer sr-only"
				role="status"
			>
				{message}
			</div>
		</AnnouncerContext.Provider>
	);
}
