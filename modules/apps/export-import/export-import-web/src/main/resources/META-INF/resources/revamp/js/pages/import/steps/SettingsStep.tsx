/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayRadio} from '@clayui/form';
import ClayLayout from '@clayui/layout';
import React from 'react';

import SectionHeader from '../../../components/SectionHeader';
import {FormikFieldRadioGroup} from '../../../components/forms/formik';
import {
	DATA_STRATEGIES,
	DataStrategy,
	USER_ID_STRATEGIES,
} from '../../../types/exportImportProcess';
import {SCOPES, Scope} from '../../../types/scope';

const noop = () => {};

export const SETTINGS_STEP_INITIAL_VALUES = {
	dataStrategy: DATA_STRATEGIES.MIRROR,
	userIdStrategy: USER_ID_STRATEGIES.CURRENT_USER_ID,
};

const DATA_STRATEGY_LABELS: Record<
	DataStrategy,
	{description: string; label: string}
> = {
	[DATA_STRATEGIES.MIRROR]: {
		description: Liferay.Language.get('import-data-strategy-mirror-help'),
		label: Liferay.Language.get('mirror'),
	},
	[DATA_STRATEGIES.MIRROR_OVERWRITE]: {
		description: Liferay.Language.get(
			'import-data-strategy-mirror-with-overwriting-help'
		),
		label: Liferay.Language.get('mirror-with-overwriting'),
	},
};

const DATA_STRATEGY_OPTIONS: Record<Scope, DataStrategy[]> = {
	[SCOPES.COMPANY]: [DATA_STRATEGIES.MIRROR],
	[SCOPES.DEPOT]: [DATA_STRATEGIES.MIRROR, DATA_STRATEGIES.MIRROR_OVERWRITE],
	[SCOPES.PORTLET]: [
		DATA_STRATEGIES.MIRROR,
		DATA_STRATEGIES.MIRROR_OVERWRITE,
	],
	[SCOPES.SITE]: [DATA_STRATEGIES.MIRROR, DATA_STRATEGIES.MIRROR_OVERWRITE],
};

export default function SettingsStep({scope}: {scope: Scope}) {
	const dataStrategyOptions = (
		DATA_STRATEGY_OPTIONS[scope] ?? DATA_STRATEGY_OPTIONS[SCOPES.SITE]
	).map((value) => ({
		...DATA_STRATEGY_LABELS[value],
		value,
	}));

	return (
		<>
			<SectionHeader
				id="userIdStrategy-label"
				title={Liferay.Language.get('authorship-of-the-content')}
			/>

			<FormikFieldRadioGroup
				aria-labelledby="userIdStrategy-label"
				name="userIdStrategy"
				options={[
					{
						description: Liferay.Language.get(
							'use-the-original-author-help'
						),
						label: Liferay.Language.get('use-the-original-author'),
						value: USER_ID_STRATEGIES.CURRENT_USER_ID,
					},
					{
						description: Liferay.Language.get(
							'use-the-current-user-as-author-help'
						),
						label: Liferay.Language.get(
							'use-the-current-user-as-author'
						),
						value: USER_ID_STRATEGIES.ALWAYS_CURRENT_USER_ID,
					},
				]}
			/>

			<SectionHeader
				id="dataStrategy-label"
				title={Liferay.Language.get('update-data')}
			/>

			{dataStrategyOptions.length === 1 ? (
				<ReadOnlyOption option={dataStrategyOptions[0]} />
			) : (
				<FormikFieldRadioGroup
					aria-labelledby="dataStrategy-label"
					name="dataStrategy"
					options={dataStrategyOptions}
				/>
			)}
		</>
	);
}

function ReadOnlyOption({
	option,
}: {
	option: {description: string; label: string};
}) {
	return (
		<ClayLayout.ContentRow className="mb-2">
			<ClayLayout.ContentCol
				aria-hidden="true"
				className="invisible pr-2"
				expand={false}
			>
				<ClayRadio checked={false} disabled onChange={noop} value="" />
			</ClayLayout.ContentCol>

			<ClayLayout.ContentCol expand>
				<span className="d-block font-weight-semi-bold text-dark">
					{option.label}
				</span>

				<span className="d-block small text-secondary">
					{option.description}
				</span>
			</ClayLayout.ContentCol>
		</ClayLayout.ContentRow>
	);
}
