/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useId} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React from 'react';

import isSameMasterLayout from '../../../app/utils/isSameMasterLayout';
import {DesignLibrarySubtitle, Option, OptionsList} from './OptionsList';

function MasterLayoutOption({
	masterLayout,
	masterLayoutPageTemplateEntryERC,
	masterLayoutPageTemplateEntryScopeERC,
	onSelectMasterLayout,
}) {
	const isActive = isSameMasterLayout(masterLayout, {
		masterLayoutPageTemplateEntryERC,
		masterLayoutPageTemplateEntryScopeERC,
	});

	const subtitleId = useId();

	const onClick = () => onSelectMasterLayout(masterLayout);

	if (!masterLayout.masterLayoutPageTemplateEntryScopeERC) {
		return (
			<Option
				{...masterLayout}
				icon="page"
				isActive={isActive}
				onClick={onClick}
			/>
		);
	}

	return (
		<Option
			{...masterLayout}
			ariaDescribedBy={subtitleId}
			icon="page"
			iconClassName="page-editor__sidebar__design-options__design-library-master-layout-icon"
			isActive={isActive}
			onClick={onClick}
			subtitle={
				<DesignLibrarySubtitle
					ariaLabel={sub(
						Liferay.Language.get('x-from-x-design-library'),
						masterLayout.name,
						masterLayout.subtitle
					)}
					designLibraryName={masterLayout.subtitle}
					id={subtitleId}
				/>
			}
		/>
	);
}

export default function MasterLayoutsList({
	masterLayoutPageTemplateEntryERC,
	masterLayoutPageTemplateEntryScopeERC,
	masterLayouts,
	onSelectMasterLayout,
}) {
	return (
		<OptionsList options={masterLayouts}>
			{(masterLayout) => (
				<MasterLayoutOption
					masterLayout={masterLayout}
					masterLayoutPageTemplateEntryERC={
						masterLayoutPageTemplateEntryERC
					}
					masterLayoutPageTemplateEntryScopeERC={
						masterLayoutPageTemplateEntryScopeERC
					}
					onSelectMasterLayout={onSelectMasterLayout}
				/>
			)}
		</OptionsList>
	);
}
