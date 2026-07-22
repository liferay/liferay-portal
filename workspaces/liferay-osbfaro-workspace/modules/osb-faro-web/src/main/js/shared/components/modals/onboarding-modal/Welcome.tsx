import ClayButton from '@clayui/button';
import Modal from 'shared/components/modal';
import React from 'react';
import {Text} from '@clayui/core';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';

interface IWelcomeProps {
	groupId?: string;
	onClose: () => void;
	onNext: (increment?: number) => void;
}

const Welcome: React.FC<IWelcomeProps> = ({groupId = '', onClose, onNext}) => {
	const LDPEnabled = useLDPEnabled({groupId});

	return (
		<>
			<Modal.Header onClose={onClose} />

			<Modal.Body>

				{/* TODO: LRAC-7427 Adjust SVGs with Linear Gradients */}
				<div className="icon analytics-onboarding-welcome-usage-icon" />

				<div className="text-center">
					<Text size={10} weight="bold">
						{LDPEnabled
							? Liferay.Language.get(
									'welcome-to-liferay-data-platform'
								)
							: Liferay.Language.get(
									'welcome-to-analytics-cloud'
								)}
					</Text>

					<p>
						<Text color="secondary" size={6}>
							{Liferay.Language.get(
								'just-a-few-more-steps-to-set-up-your-workspace'
							)}
						</Text>
					</p>
				</div>
			</Modal.Body>

			<Modal.Footer className="d-flex justify-content-center">
				<ClayButton
					autoFocus
					className="button-root"
					displayType="primary"
					onClick={() => onNext()}
				>
					{Liferay.Language.get('next')}
				</ClayButton>
			</Modal.Footer>
		</>
	);
};

export default Welcome;
