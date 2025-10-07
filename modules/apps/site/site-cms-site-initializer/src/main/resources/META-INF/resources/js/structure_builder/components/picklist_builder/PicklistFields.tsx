/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayForm from '@clayui/form';
import classNames from 'classnames';
import {
	ILearnResourceContext,
	InputLocalized,
	LearnMessage,
	LearnResourcesContext,
} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React from 'react';

import getLocalizedValue from '../../../common/utils/getLocalizedValue';
import {
	useErc,
	useId,
	useName,
	useSetErc,
	useSetName,
} from '../../contexts/PicklistBuilderContext';
import ERCInput from '../ERCInput';

export default function PicklistFields({
	learnResources,
}: {
	learnResources: ILearnResourceContext;
}) {
	const erc = useErc();
	const id = useId();
	const name = useName();
	const setErc = useSetErc();
	const setName = useSetName();

	return (
		<>
			{id ? (
				<ClayAlert
					className="mb-4"
					displayType="info"
					role={null}
					title="Info"
				>
					<span className="mr-1">
						{Liferay.Language.get(
							'picklists-are-shared-resources,-so-changes-to-a-picklist-affect-all-content-structures-that-use-it'
						)}
					</span>

					<LearnResourcesContext.Provider value={learnResources}>
						<LearnMessage
							resource="site-cms-site-initializer"
							resourceKey="picklist-builder"
						/>
					</LearnResourcesContext.Provider>
				</ClayAlert>
			) : null}

			<ClayForm.Group
				className={classNames('pt-2', {'has-error': !name})}
			>
				<InputLocalized
					aria-label={Liferay.Language.get('picklist-name')}
					error={
						getLocalizedValue(name)
							? ''
							: Liferay.Language.get('this-field-is-required')
					}
					label={Liferay.Language.get('name')}
					onBlur={() => setName(name)}
					onChange={(name) => setName(name)}
					required
					translations={
						name as Liferay.Language.LocalizedValue<string>
					}
				/>

				<ERCInput
					helpText={sub(
						Liferay.Language.get(
							'unique-key-for-referencing-the-x'
						),
						Liferay.Language.get('picklist')
					)}
					onValueChange={(erc) => setErc(erc)}
					value={erc}
				/>
			</ClayForm.Group>
		</>
	);
}
