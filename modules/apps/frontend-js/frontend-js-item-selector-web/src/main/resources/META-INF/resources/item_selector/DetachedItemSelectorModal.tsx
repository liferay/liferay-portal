/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useModal} from '@clayui/modal';
import React, {useEffect} from 'react';

import ItemSelectorModal, {IItemSelectorModalProps} from './ItemSelectorModal';

export type TDetachedItemSelectorModal<T> = Omit<
	IItemSelectorModalProps<T>,
	'observer' | 'onOpenChange' | 'open'
>;

function DetachedItemSelectorModal<T extends Record<string, any>>(
	props: TDetachedItemSelectorModal<T>
) {
	const {observer, onOpenChange, open} = useModal();

	useEffect(() => {
		onOpenChange(true);
	}, [onOpenChange]);

	return (
		<>
			{open && (
				<ItemSelectorModal
					{...props}
					observer={observer}
					onOpenChange={onOpenChange}
					open={open}
				/>
			)}
		</>
	);
}

export default DetachedItemSelectorModal;
