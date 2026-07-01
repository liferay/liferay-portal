/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import ClayList from '@clayui/list';
import {Provider} from '@clayui/provider';
import React from 'react';

import FDSItemTitle from '../components/FDSItemTitle';

import type {IItemsActions} from '@liferay/frontend-data-set-web';

interface IProps {
	actions: IItemsActions[];
	itemData?: {
		active?: boolean;
		description?: string;
		model?: {
			label?: string;
			name?: string;
			providerLabel?: string;
		};
		status?: {
			label?: string;
		};
		system?: boolean;
	};
	itemId: unknown;
	value: unknown;
}

const AgentDefinitionItemTitle = ({
	actions,
	itemData,
	itemId,
	value,
}: IProps) => {
	const {active, description, model, status, system} = itemData ?? {};

	const draft = status?.label === 'draft';

	return (
		<div className="agent-definition-item">
			<div className="agent-definition-item__title">
				<FDSItemTitle
					actions={actions}
					itemData={itemData}
					itemId={itemId}
					value={value || Liferay.Language.get('untitled-agent')}
				/>
			</div>

			{description && (
				<ClayList.ItemText className="agent-definition-item__description">
					{description}
				</ClayList.ItemText>
			)}

			{model?.name && (
				<ClayList.ItemText className="agent-definition-item__model">
					{`${model.providerLabel} - ${model.label}`}
				</ClayList.ItemText>
			)}

			<Provider spritemap={Liferay.Icons.spritemap}>
				<div className="agent-definition-item__labels">
					{system ? (
						<>
							<ClayLabel displayType="info" inverse>
								{Liferay.Language.get('provided-by-liferay')}
							</ClayLabel>

							<ClayLabel displayType="success" inverse>
								{Liferay.Language.get('minimum-risk')}
							</ClayLabel>
						</>
					) : (
						<ClayLabel displayType="info">
							{Liferay.Language.get('custom')}
						</ClayLabel>
					)}

					{draft ? (
						<ClayLabel displayType="secondary">
							{Liferay.Language.get('draft')}
						</ClayLabel>
					) : active ? (
						<ClayLabel displayType="success">
							{Liferay.Language.get('running')}
						</ClayLabel>
					) : (
						<ClayLabel displayType="danger">
							{Liferay.Language.get('stopped')}
						</ClayLabel>
					)}
				</div>
			</Provider>
		</div>
	);
};

export default AgentDefinitionItemTitle;
