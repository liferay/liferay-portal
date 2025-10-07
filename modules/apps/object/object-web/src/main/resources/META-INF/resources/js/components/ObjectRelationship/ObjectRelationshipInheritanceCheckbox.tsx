/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayPopover from '@clayui/popover';
import React, {useState} from 'react';

import './ObjectRelationshipInheritanceCheckbox.scss';

import {
	ILearnResourceContext,
	LearnMessage,
	LearnResourcesContext,
} from 'frontend-js-components-web';

interface ObjectRelationshipInheritanceCheckbox {
	learnResources: ILearnResourceContext;
	onChange: (
		event: React.ChangeEvent<HTMLInputElement>
	) => Promise<void> | void;
	values: Partial<ObjectRelationship>;
}

export function ObjectRelationshipInheritanceCheckbox({
	learnResources,
	onChange,
	values,
}: ObjectRelationshipInheritanceCheckbox) {
	const [showPopover, setShowPopover] = useState(false);

	return (
		<>
			<div className="form-group lfr__object-relationship-inheritance-container">
				<ClayCheckbox
					checked={!!values.edge}
					label={Liferay.Language.get('enable-inheritance')}
					onChange={onChange}
				/>

				<ClayPopover
					alignPosition="top"
					disableScroll
					header={Liferay.Language.get('inheritance')}
					onMouseLeave={() => setShowPopover(false)}
					onShowChange={setShowPopover}
					show={showPopover}
					trigger={
						<ClayIcon
							className="field-base-tooltip-icon"
							onFocus={() => setShowPopover(true)}
							onMouseOver={() => setShowPopover(true)}
							symbol="question-circle-full"
						/>
					}
				>
					{Liferay.Language.get(
						'enable-inheritance-to-share-settings-between-related-data-models'
					)}
				</ClayPopover>
			</div>

			<ClayAlert
				displayType="info"
				title={`${Liferay.Language.get('info')}:`}
			>
				{Liferay.Language.get(
					'when-enabled-and-the-relationship-field-is-filled-permissions-are-inherited'
				)}
				&nbsp;
				<LearnResourcesContext.Provider value={learnResources}>
					<LearnMessage
						className="alert-link"
						resource="object-web"
						resourceKey="inheritance-relationships"
					/>
				</LearnResourcesContext.Provider>
			</ClayAlert>
		</>
	);
}
