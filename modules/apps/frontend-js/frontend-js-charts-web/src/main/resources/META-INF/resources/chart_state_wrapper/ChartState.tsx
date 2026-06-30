/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayEmptyState from '@clayui/empty-state';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React from 'react';

import ChartErrorBoundary from './ChartErrorBoundary';
import messageFromError from './messageFromError';

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
		return (
			<div
				aria-live="polite"
				className="align-items-center d-flex justify-content-center"
				role="status"
				style={style}
			>
				<ClayLoadingIndicator
					aria-label={Liferay.Language.get('loading')}
				/>
			</div>
		);
	}

	if (error) {
		return (
			<div style={style}>
				<ClayAlert
					displayType="danger"
					title={Liferay.Language.get('error')}
				>
					{messageFromError(error)}
				</ClayAlert>
			</div>
		);
	}

	if (empty) {
		return (
			<div aria-live="assertive" style={style}>
				<ClayEmptyState
					description={
						emptyStateMessage ??
						Liferay.Language.get('there-is-no-data')
					}
					imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/empty_state.svg`}
					title={Liferay.Language.get('no-data-available')}
				/>
			</div>
		);
	}

	return (
		<ChartErrorBoundary
			fallback={
				<div style={style}>
					<ClayAlert
						displayType="danger"
						title={Liferay.Language.get('error')}
					>
						{Liferay.Language.get('an-error-occurred')}
					</ClayAlert>
				</div>
			}
			onError={fallbackError}
		>
			{children}
		</ChartErrorBoundary>
	);
}
