/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useEffect, useRef, useState} from 'react';

import {
	DateFilterValues,
	NormalizedDateFilter,
	Range,
	normalizeDateFilter,
} from '../components/date_filter';
import {PreviewParams, getPreview} from '../services/getPreview';
import {Preview} from '../types/exportImportPreview';

export function usePreview(previewAPIURL: string, initialPreview?: Preview) {
	const [preview, setPreview] = useState<Preview | undefined>(initialPreview);
	const [error, setError] = useState<string | null>(null);
	const [loading, setLoading] = useState(!initialPreview);
	const initialPreviewRef = useRef<Preview | undefined>(initialPreview);
	const appliedDateFilterRef = useRef<NormalizedDateFilter>({});
	const requestIdRef = useRef(0);

	const loadPreview = useCallback((previewParams: PreviewParams) => {
		const requestId = ++requestIdRef.current;

		setLoading(true);
		setError(null);

		getPreview(previewParams).then((previewResponse) => {
			if (requestId !== requestIdRef.current) {
				return;
			}

			if (previewResponse.error !== null) {
				setError(previewResponse.error);
			}
			else {
				setPreview(previewResponse.data);

				if (!initialPreviewRef.current) {
					initialPreviewRef.current = previewResponse.data;
				}
			}

			setLoading(false);
		});
	}, []);

	useEffect(() => {
		if (initialPreviewRef.current) {
			return;
		}

		loadPreview({url: previewAPIURL});
	}, [loadPreview, previewAPIURL]);

	const handleApplyFilter = useCallback(
		(dateFilterValues: DateFilterValues) => {
			appliedDateFilterRef.current =
				normalizeDateFilter(dateFilterValues);

			if (
				dateFilterValues.range === Range.All &&
				initialPreviewRef.current
			) {
				++requestIdRef.current;

				setError(null);
				setLoading(false);
				setPreview(initialPreviewRef.current);

				return;
			}

			loadPreview({
				query: appliedDateFilterRef.current,
				url: previewAPIURL,
			});
		},
		[loadPreview, previewAPIURL]
	);

	return {
		appliedDateFilterRef,
		error,
		handleApplyFilter,
		loading,
		preview,
		setError,
	};
}
