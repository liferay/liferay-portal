/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Label from '@clayui/label';
import {DateRenderer} from '@liferay/frontend-data-set-web';
import {displayErrorToast} from '@liferay/site-cms-site-initializer';
import React, {useState} from 'react';

import {patchProjectById} from '../../utils/api';
import {displayStateSuccessToast} from '../../utils/toastUtil';
import {UPDATE_HISTORY} from '../History';
import InfoSummary from '../InfoSummary';
import StateSelector, {State} from '../StateSelector';
import User, {UserProps} from './User';

interface ProjectInfoSummaryProps {
	dueDate: string;
	funnelStages: string[];
	hasUpdatePermission: boolean;
	initialState: string;
	manager: UserProps;
	personas: string[];
	projectId: string;
	sponsor: UserProps;
	states: State[];
	tags: string[];
}

export default function ProjectInfoSummary({
	dueDate,
	funnelStages,
	hasUpdatePermission,
	initialState,
	manager,
	personas,
	projectId,
	sponsor,
	states,
	tags,
}: ProjectInfoSummaryProps) {
	const [selectedStateKey, setSelectedStateKey] = useState(initialState);
	const [stateSelectorDisabled, setStateSelectorDisabled] = useState(false);

	return (
		<InfoSummary
			defaultOpen={true}
			items={[
				{
					label: 'State',
					value: (
						<StateSelector
							disabled={
								!hasUpdatePermission || stateSelectorDisabled
							}
							onChange={async (key: string) => {
								setStateSelectorDisabled(true);

								const {error} = await patchProjectById({
									body: {state: key},
									projectId,
								});

								if (!error) {
									setSelectedStateKey(key);

									displayStateSuccessToast();

									Liferay.fire(UPDATE_HISTORY);
								}
								else {
									displayErrorToast(error);
								}

								setStateSelectorDisabled(false);
							}}
							selectedKey={selectedStateKey}
							small
							states={states}
						/>
					),
				},
				{label: 'Manager', value: <User {...manager} />},
				{label: 'Sponsor', value: <User {...sponsor} />},
				{
					label: 'Due Date',
					value: DateRenderer({value: dueDate}) ?? '',
				},
				{
					label: Liferay.Language.get('personas'),
					value: (
						<div className="lfr-cmp__info-categories">
							{personas.map((persona) => (
								<Label
									displayType="secondary"
									inverse
									key={persona}
								>
									{persona}
								</Label>
							))}
						</div>
					),
				},
				{
					label: Liferay.Language.get('funnel-stages'),
					value: (
						<div className="lfr-cmp__info-categories">
							{funnelStages.map((stage) => (
								<Label
									displayType="secondary"
									inverse
									key={stage}
								>
									{stage}
								</Label>
							))}
						</div>
					),
				},
				{
					label: Liferay.Language.get('tags'),
					value: (
						<div className="lfr-cmp__info-categories">
							{tags.map((tag) => (
								<Label
									displayType="secondary"
									inverse
									key={tag}
								>
									{tag}
								</Label>
							))}
						</div>
					),
				},
			]}
		/>
	);
}
