import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import Input from 'shared/components/Input';
import InputList from 'shared/components/InputList';
import React, {useState} from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import {validateEmail} from 'shared/util/email-validators';

interface IInviteUsersModalProps {
	className?: string;
	observer: Observer;
	onClose: () => void;
	onSubmit: (emails: string[]) => Promise<any>;
}

const InviteUsersModal: React.FC<IInviteUsersModalProps> = ({
	className,
	observer,
	onClose,
	onSubmit,
	...otherProps
}) => {
	const [emails, setEmails] = useState<string[]>([]);
	const [inputValue, setInputValue] = useState('');

	const handleSubmit = () => {
		if (
			(emails.length && !inputValue) ||
			(inputValue && validateEmail(inputValue))
		) {
			onSubmit(emails);
		}
	};

	return (
		<ClayModal
			observer={observer}
			{...otherProps}
			className={className}
			size="lg"
		>
			<ClayModal.Header>
				{Liferay.Language.get('invite-users')}
			</ClayModal.Header>

			<ClayModal.Body>
				<div className="form-text mb-4">
					{Liferay.Language.get(
						'enter-the-email-addresses-of-the-people-you-would-like-to-invite-to-analytics-cloud.-separate-each-address-by-space-or-comma'
					)}
				</div>

				<Input.Group>
					<InputList
						errorMessage={Liferay.Language.get(
							'please-enter-a-valid-email-address'
						)}
						inputValue={inputValue}
						items={emails}
						onInputChange={(value) => setInputValue(value.trim())}
						onItemsChange={setEmails}
						placeholder={Liferay.Language.get(
							'enter-email-address'
						)}
						validateOnBlur
						validationFn={(value?: string) =>
							!!value && validateEmail(value)
						}
					/>
				</Input.Group>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							className="button-root"
							displayType="secondary"
							onClick={onClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							className="button-root"
							disabled={
								(!inputValue && !emails.length) ||
								(!!inputValue && !validateEmail(inputValue))
							}
							displayType="primary"
							onClick={handleSubmit}
						>
							{Liferay.Language.get('send')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

export default InviteUsersModal;
