/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {sub} from 'frontend-js-web';
import React, {useMemo, useState} from 'react';

import {PageTreePickerSelectionEntry} from '../../types/PageTreePicker';
import PageTreePickerPanel from './PageTreePickerPanel';
import SitePageTreeDataSource, {
	ROOT_ITEM_ID,
	SitePage,
	SitePageTreeSelection,
} from './SitePageTreeDataSource';

interface PageTreePickerModalProps {
	initialSelection?: SitePageTreeSelection | null;
	onClose: () => void;
	onSubmit: (selection: SitePageTreeSelection | null) => void;
	pageSize?: number;
	privateLayout: boolean;
	siteExternalReferenceCode: string;
	title?: string;
}

export default function PageTreePickerModal({
	initialSelection,
	onClose,
	onSubmit,
	pageSize = 50,
	privateLayout,
	siteExternalReferenceCode,
	title = sub(
		Liferay.Language.get('select-x'),
		Liferay.Language.get('pages')
	),
}: PageTreePickerModalProps) {
	const {observer} = useModal({onClose});

	const dataSource = useMemo(
		() =>
			new SitePageTreeDataSource({
				pageSize,
				privateLayout,
				siteExternalReferenceCode,
			}),
		[pageSize, privateLayout, siteExternalReferenceCode]
	);

	const [defaultSelectedEntries] = useState(() =>
		dataSource.toEntries(initialSelection)
	);

	const [entries, setEntries] = useState<
		Array<PageTreePickerSelectionEntry<SitePage | null>>
	>(defaultSelectedEntries);

	return (
		<ClayModal observer={observer} size="lg">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ClayModal.Body className="cadmin p-0 page-tree-picker">
				<PageTreePickerPanel<SitePage | null>
					dataSource={dataSource}
					defaultExpandedIds={[ROOT_ITEM_ID]}
					defaultRegisteredItems={[dataSource.getRootItem()]}
					defaultSelectedEntries={defaultSelectedEntries}
					onSelectionChange={setEntries}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							onClick={() =>
								onSubmit(dataSource.toSelection(entries))
							}
						>
							{Liferay.Language.get('select')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
