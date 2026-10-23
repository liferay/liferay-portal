import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import InfoPopover from 'shared/components/InfoPopover';
import Input from 'shared/components/Input';
import InputList, {Display} from 'shared/components/InputList';
import Label from 'shared/components/form/Label';
import Loading, {Align} from 'shared/components/Loading';
import inviteURL from '../../../../../images/backgrounds/ac_invite.svg?url';
import React, {useState} from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {connect, ConnectedProps} from 'react-redux';
import {Text} from '@clayui/core';
import {UserRoleNames} from 'shared/util/constants';
import {validateEmail} from 'shared/util/email-validators';

const connector = connect(null, {addAlert});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IInvitePeopleProps extends PropsFromRedux {
	dxpConnected: boolean;
	groupId: string;
	onClose: () => void;
	onNext: (increment?: number) => void;
}

const InvitePeople: React.FC<IInvitePeopleProps> = ({
	addAlert,
	dxpConnected,
	groupId,
	onClose,
	onNext,
}) => {
	const [emails, setEmails] = useState([]);
	const [inputValue, setInputValue] = useState('');
	const [loading, setLoading] = useState(false);
	const [sent, setSent] = useState(false);

	const handleSubmit = () => {
		if (
			(emails.length && !inputValue) ||
			(inputValue && validateEmail(inputValue))
		) {
			setLoading(true);

			API.user
				.inviteMany({
					emailAddresses: emails,
					groupId,
					roleName: UserRoleNames.Member,
				})
				.then(() => {
					setLoading(false);
					setSent(true);
				})
				.catch(() => {
					addAlert({
						alertType: Alert.Types.Error,
						message: Liferay.Language.get(
							'unable-to-send-request.-please-try-again-later'
						),
						timeout: false,
					});

					setLoading(false);
				});
		}
	};

	return (
		<>
			<ClayModal.Header />

			<ClayModal.Body>

				{/* TODO: LRAC-7427 Adjust SVGs with Linear Gradients */}
				<img
					alt=""
					className="d-block mb-4 w-100"
					height={300}
					src={inviteURL}
				/>

				<div className="text-center mb-4">
					<Text size={6} weight="bold">
						{sent
							? Liferay.Language.get('your-invite-was-sent')
							: Liferay.Language.get(
									'invite-people-to-workspace'
								)}
					</Text>
				</div>

				{sent ? (
					<div className="text-center">
						<Text color="secondary" size={4}>
							{Liferay.Language.get(
								'you-can-see-the-new-members-invitation-status-and-role-permissions-under-user-management-in-settings'
							)}
						</Text>
					</div>
				) : (
					<div>
						<div className="mb-2">
							<Label>
								<Text size={6} weight="bold">
									{Liferay.Language.get('add-other-members')}
								</Text>

								<InfoPopover
									className="ml-2"
									content={Liferay.Language.get(
										'each-users-role-can-be-set-under-user-management-in-settings'
									)}
									title={Liferay.Language.get(
										'member-permissions'
									)}
								/>
							</Label>
						</div>

						<Input.Group>
							<Input.GroupItem>
								<InputList
									errorAttr={{
										className: 'has-warning',
										icon: {display: Display.Warning},
									}}
									errorMessage={Liferay.Language.get(
										'please-enter-a-valid-email-address'
									)}
									inputValue={inputValue}
									items={emails}
									onInputChange={setInputValue}
									onItemsChange={setEmails}
									placeholder={Liferay.Language.get(
										'enter-email-address'
									)}
									validateOnBlur
									validationFn={(value?: string) =>
										!!value && validateEmail(value)
									}
								/>
							</Input.GroupItem>
						</Input.Group>

						<div className="mt-1">
							<Text color="secondary" size={4}>
								{Liferay.Language.get(
									'enter-email-addresses-separated-by-spaces-or-commas'
								)}
							</Text>
						</div>
					</div>
				)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							className="button-root"
							disabled={sent}
							displayType="secondary"
							onClick={dxpConnected ? () => onNext() : onClose}
						>
							{Liferay.Language.get('skip')}
						</ClayButton>

						<ClayButton
							className="button-root ml-2"
							disabled={
								(!inputValue && !emails.length) ||
								(!!inputValue && !validateEmail(inputValue))
							}
							displayType="primary"
							onClick={
								sent
									? dxpConnected
										? () => onNext()
										: onClose
									: handleSubmit
							}
						>
							{loading && <Loading align={Align.Left} />}

							{sent
								? dxpConnected
									? Liferay.Language.get('next')
									: Liferay.Language.get('done')
								: Liferay.Language.get('send-invitations')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
};

export default connector(InvitePeople);
