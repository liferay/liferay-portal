import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import Modal, {useModal} from '@clayui/modal';
import React from 'react';
import {Text} from '@clayui/core';

interface IClearAllButtonProps {
	onClear: () => void;
}

const ClearAllButton: React.FC<IClearAllButtonProps> = ({onClear}) => {
	const {observer, onOpenChange, open} = useModal();

	const handleConfirm = () => {
		onClear();

		onOpenChange(false);
	};

	return (
		<>
			<div className="criteria-builder-clear-all mt-4 text-center">
				<ClayButton
					borderless
					displayType="secondary"
					onClick={() => onOpenChange(true)}
					size="sm"
				>
					<Text weight="semi-bold">
						<ClayIcon className="mr-2" symbol="times-circle" />

						{Liferay.Language.get('clear-all')}
					</Text>
				</ClayButton>
			</div>

			{open && (
				<Modal
					data-testid="clear-all-modal"
					observer={observer}
					size="sm"
					status="warning"
				>
					<Modal.Header>
						{Liferay.Language.get('clear-all')}
					</Modal.Header>

					<Modal.Body>
						{Liferay.Language.get(
							'are-you-sure-you-want-to-clear-all-criteria'
						)}
					</Modal.Body>

					<Modal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									displayType="secondary"
									onClick={() => onOpenChange(false)}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>

								<ClayButton
									displayType="warning"
									onClick={handleConfirm}
								>
									{Liferay.Language.get('continue')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</Modal>
			)}
		</>
	);
};

export default ClearAllButton;
