/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {FDS_EVENT} from '@liferay/frontend-data-set-web';
import {navigate, sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useState} from 'react';

import ContentGapMatrixGrid from './ContentGapMatrixGrid';
import ContentGapMatrixHeader from './ContentGapMatrixHeader';
import {ContentCoverageServiceImpl} from './services/ContentCoverageService';
import {MatrixData} from './types';

import './ContentGapMatrix.scss';

interface ContentGapMatrixCardProps {
	assetFDSId: string;
	editProjectURL?: string;
	hasFunnelStagesOrPersonas: boolean;
	projectId: string;
}

const contentCoverageService = ContentCoverageServiceImpl;

export default function ContentGapMatrixCard({
	assetFDSId,
	editProjectURL,
	hasFunnelStagesOrPersonas,
	projectId,
}: ContentGapMatrixCardProps) {
	const [data, setData] = useState<MatrixData | null>(null);
	const [error, setError] = useState(false);
	const [loading, setLoading] = useState(true);

	const fetchMatrix = useCallback(async () => {
		setError(false);
		setLoading(true);

		try {
			setData(await contentCoverageService.getMatrix(projectId));
		}
		catch {
			setError(true);
		}
		finally {
			setLoading(false);
		}
	}, [projectId]);

	useEffect(() => {
		if (hasFunnelStagesOrPersonas) {
			fetchMatrix();
		}
	}, [hasFunnelStagesOrPersonas, fetchMatrix]);

	useEffect(() => {
		if (!hasFunnelStagesOrPersonas) {
			return undefined;
		}

		Liferay.on(FDS_EVENT.DISPLAY_UPDATED, fetchMatrix);

		return () => {
			Liferay.detach(FDS_EVENT.DISPLAY_UPDATED, fetchMatrix);
		};
	}, [hasFunnelStagesOrPersonas, fetchMatrix]);

	if (!hasFunnelStagesOrPersonas) {
		return (
			<div className="lfr-cmp__content-gap-matrix-card">
				<ContentGapMatrixHeader />

				<div className="lfr-cmp__content-gap-matrix-container">
					<div className="empty-state">
						<ClayEmptyState
							description={Liferay.Language.get(
								'define-personas-and-funnel-stages-to-unlock-content-coverage-insights-and-align-your-content-strategy'
							)}
							imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/cmp_empty_state_personas.svg`}
							title={Liferay.Language.get(
								'no-personas-or-funnel-stages-configured'
							)}
						>
							{editProjectURL && (
								<ClayButton
									displayType="secondary"
									onClick={() => {
										navigate(editProjectURL);
									}}
								>
									{Liferay.Language.get('edit-project')}

									<ClayIcon
										className="c-ml-2"
										symbol="shortcut"
									/>
								</ClayButton>
							)}
						</ClayEmptyState>
					</div>
				</div>
			</div>
		);
	}

	if (loading) {
		return (
			<div className="lfr-cmp__content-gap-matrix-card">
				<div className="lfr-cmp__content-gap-matrix-container">
					<ClayLoadingIndicator />
				</div>
			</div>
		);
	}

	if (error || !data) {
		return (
			<div className="lfr-cmp__content-gap-matrix-card">
				<ContentGapMatrixHeader />

				<div className="lfr-cmp__content-gap-matrix-container">
					<div className="empty-state">
						<ClayEmptyState
							title={Liferay.Language.get('an-error-occurred')}
						/>
					</div>
				</div>
			</div>
		);
	}

	return (
		<div className="lfr-cmp__content-gap-matrix-card">
			<ContentGapMatrixHeader data={data} />

			<div className="lfr-cmp__content-gap-matrix-container">
				<div className="lfr-cmp__content-gap-matrix-intro">
					<h5 className="lfr-cmp__content-gap-matrix-title">
						{sub(Liferay.Language.get('amount-of-assets-per-x'), [
							`${Liferay.Language.get(
								'persona'
							)} × ${Liferay.Language.get('funnel-stage')}`,
						])}
					</h5>

					<p className="lfr-cmp__content-gap-matrix-description">
						{Liferay.Language.get(
							'this-report-provides-a-breakdown-of-all-project-assets-by-persona-and-funnel-stage'
						)}
					</p>
				</div>

				<ContentGapMatrixGrid assetFDSId={assetFDSId} data={data} />
			</div>
		</div>
	);
}
