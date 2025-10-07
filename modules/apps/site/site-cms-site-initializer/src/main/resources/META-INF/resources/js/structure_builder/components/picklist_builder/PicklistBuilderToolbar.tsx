/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from '@liferay/layout-js-components-web';
import {openToast} from 'frontend-js-components-web';
import React, {useMemo} from 'react';

import Toolbar from '../../../common/components/Toolbar';
import PicklistService from '../../../common/services/PicklistService';
import focusInvalidElement from '../../../common/utils/focusInvalidElement';
import getLocalizedValue from '../../../common/utils/getLocalizedValue';
import {useStaleCache} from '../../contexts/CacheContext';
import {
	useDeletedOptions,
	useErc,
	useId,
	useName,
	useOptions,
	useSetId,
} from '../../contexts/PicklistBuilderContext';
import AsyncButton from '../AsyncButton';

export default function PicklistBuilderToolbar() {
	const deletedOptions = useDeletedOptions();
	const erc = useErc();
	const id = useId();
	const name = useName();
	const options = useOptions();
	const setId = useSetId();
	const staleCache = useStaleCache();

	const localizedName = useMemo(() => getLocalizedValue(name), [name]);

	const onSave = async () => {
		if (deletedOptions) {
			if (
				!(await openConfirmModal({
					buttonLabel: Liferay.Language.get('Save'),
					center: true,
					status: 'danger',
					text: Liferay.Language.get(
						'you-deleted-one-or-more-options-from-the-picklist'
					),
					title: Liferay.Language.get('save-picklist-changes'),
				}))
			) {
				return;
			}
		}

		try {
			if (!localizedName || !erc) {
				focusInvalidElement();

				return;
			}

			const params = {
				erc,
				name,
				options,
			};

			if (!id) {
				const {data, error} =
					await PicklistService.createPicklist(params);

				if (error) {
					throw new Error(error);
				}
				else if (data) {
					setId(data.id);
				}
			}
			else {
				const {error} = await PicklistService.updatePicklist({
					...params,
					id,
				});

				if (error) {
					throw new Error(error);
				}
			}

			staleCache('picklists');

			openToast({
				message: Liferay.Util.sub(
					Liferay.Language.get('x-was-saved-successfully'),
					localizedName
				),
				type: 'success',
			});
		}
		catch (error) {
			const {message} = error as Error;

			openToast({
				message:
					message ||
					Liferay.Language.get(
						'an-unexpected-error-occurred-while-saving-or-publishing-the-picklist'
					),
				type: 'danger',
			});
		}
	};

	return (
		<Toolbar title={Liferay.Language.get('new-picklist')}>
			<Toolbar.Item>
				<AsyncButton
					displayType="primary"
					label={Liferay.Language.get('save')}
					onClick={onSave}
				/>
			</Toolbar.Item>
		</Toolbar>
	);
}
