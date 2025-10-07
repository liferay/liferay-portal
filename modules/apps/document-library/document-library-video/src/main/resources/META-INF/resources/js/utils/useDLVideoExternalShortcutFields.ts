/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {type Fields, updateDLVideoFields, validateUrl} from '..';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {debounce} from 'frontend-js-web';
import {useEffect, useRef, useState} from 'react';

export function useDLVideoExternalShortcutFields({
	getDLVideoExternalShortcutFieldsURL,
	namespace,
	url,
}: {
	getDLVideoExternalShortcutFieldsURL: string;
	namespace: string;
	url: string;
}) {
	const [error, setError] = useState<string | null>(null);
	const [fields, setFields] = useState<Fields | null>(null);
	const [loading, setLoading] = useState(false);
	const isMounted = useIsMounted();

	const getFieldsRef = useRef<(url: string) => void>();

	useEffect(() => {
		getFieldsRef.current = debounce(
			(dlVideoExternalShortcutURL: string) => {
				updateDLVideoFields({
					getVideoFieldsURL: getDLVideoExternalShortcutFieldsURL,
					namespace,
					onError: () => {
						if (isMounted()) {
							setLoading(false);
							setFields(null);
							setError(
								Liferay.Language.get(
									'sorry,-this-platform-is-not-supported'
								)
							);
						}
					},
					onUpdate: (newFields: Fields) => {
						if (isMounted()) {
							setLoading(false);
							setFields(newFields);
						}
					},
					videoURL: dlVideoExternalShortcutURL,
				});
			},
			500
		);
	}, [getDLVideoExternalShortcutFieldsURL, namespace, isMounted]);

	useEffect(() => {
		setError(null);

		if (url && validateUrl(url)) {
			setLoading(true);
			getFieldsRef.current?.(url);
		}
		else {
			setLoading(false);
			setFields(null);
		}
	}, [url]);

	return {error, fields, loading};
}
