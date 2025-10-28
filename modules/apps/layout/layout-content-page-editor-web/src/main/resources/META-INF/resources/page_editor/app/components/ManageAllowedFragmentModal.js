/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import PropTypes from 'prop-types';
import React, {useCallback, useState} from 'react';

import {useDispatch} from '../contexts/StoreContext';
import updateItemConfig from '../thunks/updateItemConfig';
import AllowedFragmentSelectorTree from './AllowedFragmentSelectorTree';

const ManageAllowedFragmentModal = ({item, observer, onClose}) => {
	const dispatch = useDispatch();

	const [allowNewFragmentEntries, setAllowNewFragmentEntries] =
		useState(true);
	const [selectedFragments, setSelectedFragments] = useState(new Set([]));
	const [loading, setLoading] = useState();

	const handleSaveClick = () => {
		setLoading(true);

		dispatch(
			updateItemConfig({
				itemConfig: {
					allowNewFragmentEntries,
					fragmentEntryKeys: [...selectedFragments],
				},
				itemIds: [item.itemId],
			})
		).then(() => {
			setLoading(false);
			onClose();
		});
	};

	const onSelectedFragment = useCallback(
		({allowNewFragmentEntries, selectedFragments}) => {
			setAllowNewFragmentEntries(allowNewFragmentEntries);
			setSelectedFragments(selectedFragments);
		},
		[]
	);

	return (
		<ClayModal
			className="page-editor__allowed-fragment__modal"
			containerProps={{className: 'cadmin'}}
			observer={observer}
			size="md"
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('allowed-fragments')}
			</ClayModal.Header>

			<ClayModal.Body className="p-0">
				<p className="m-4 small text-secondary">
					{Liferay.Language.get(
						'specify-which-fragments-a-page-author-is-allowed-to-use-within-the-drop-zone-when-creating-a-page-from-this-master'
					)}
				</p>

				<AllowedFragmentSelectorTree
					dropZoneConfig={item.config}
					onSelectedFragment={onSelectedFragment}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							onClick={handleSaveClick}
						>
							{loading && (
								<span className="inline-item inline-item-before">
									<span
										aria-hidden="true"
										className="loading-animation"
									></span>
								</span>
							)}

							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			></ClayModal.Footer>
		</ClayModal>
	);
};

ManageAllowedFragmentModal.propTypes = {
	item: PropTypes.shape({
		config: PropTypes.object.isRequired,
		itemId: PropTypes.string.isRequired,
	}).isRequired,
	observer: PropTypes.object.isRequired,
	onClose: PropTypes.func.isRequired,
};

export default ManageAllowedFragmentModal;
