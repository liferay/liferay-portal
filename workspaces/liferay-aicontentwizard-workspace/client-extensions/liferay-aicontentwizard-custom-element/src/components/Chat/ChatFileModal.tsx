/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';

import ChatFileModalExplorer from './ChatFileModalExplorer';

type Props = {
	items: any[];
	modal: ReturnType<typeof useModal>;
	onChoose: (data: any) => void;
	selectedTree: any;
	setSelectedTree: React.Dispatch<any>;
};

const ChatFileModal: React.FC<Props> = ({
	items,
	modal,
	onChoose,
	selectedTree,
	setSelectedTree,
}) => (
	<ClayModal disableAutoClose observer={modal.observer}>
		<ClayModal.Header closeButtonAriaLabel={Liferay.Language.get('close')}>
			<ClayModal.Title>
				Choose the Documents and Media Files
			</ClayModal.Title>
		</ClayModal.Header>
		<ClayModal.Body>
			<ChatFileModalExplorer
				items={items}
				setSelectedTree={setSelectedTree}
			/>
		</ClayModal.Body>
		<ClayModal.Footer
			last={
				<>
					<ClayButton
						className="mr-2"
						displayType="secondary"
						onClick={modal.onClose}
					>
						Cancel
					</ClayButton>

					<ClayButton disabled={!selectedTree} onClick={onChoose}>
						Choose
					</ClayButton>
				</>
			}
		/>
	</ClayModal>
);

export default ChatFileModal;
