/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import ChartErrorBoundary from './ChartErrorBoundary';
import ChartEmptyState from './states/ChartEmptyState';
import ChartErrorState from './states/ChartErrorState';
import ChartLoadingState from './states/ChartLoadingState';

const DEFAULT_HEIGHT = 320;

export interface ChartStateProps {
	empty?: boolean;
	emptyStateMessage?: string;
	error?: Error | string | null;
	fallbackError?: (error: Error) => void;
	loading?: boolean;
}

interface Props extends ChartStateProps {
	children: React.ReactNode;
	height?: number | string;
	width?: number | string;
}

export default function ChartState({
	children,
	empty,
	emptyStateMessage,
	error,
	fallbackError,
	height,
	loading,
	width,
}: Props) {
	const style = {
		minHeight: height ?? DEFAULT_HEIGHT,
		width: width ?? '100%',
	};

	if (loading) {
		return <ChartLoadingState style={style} />;
	}

	if (error) {
		return <ChartErrorState error={error} style={style} />;
	}

	if (empty) {
		return <ChartEmptyState message={emptyStateMessage} style={style} />;
	}

	return (
		<ChartErrorBoundary
			fallback={<ChartErrorState style={style} />}
			onError={fallbackError}
		>
			{children}
		</ChartErrorBoundary>
	);
}
