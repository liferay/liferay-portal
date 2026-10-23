import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import welcomeURL from '../../../../../images/analytics_onboarding_welcome.svg?url';
import React from 'react';
import {Text} from '@clayui/core';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';

interface IWelcomeProps {
	groupId?: string;
	onClose: () => void;
	onNext: (increment?: number) => void;
}

const Welcome: React.FC<IWelcomeProps> = ({groupId = '', onNext}) => {
	const LDPEnabled = useLDPEnabled({groupId});

	return (
		<>
			<ClayModal.Header />

			<ClayModal.Body>

				{/* TODO: LRAC-7427 Adjust SVGs with Linear Gradients */}
				<img
					alt=""
					className="d-block mb-4 w-100"
					height={300}
					src={welcomeURL}
				/>

				<div className="text-center">
					<Text size={6} weight="bold">
						{LDPEnabled
							? Liferay.Language.get(
									'welcome-to-liferay-data-platform'
								)
							: Liferay.Language.get(
									'welcome-to-analytics-cloud'
								)}
					</Text>

					<p>
						<Text color="secondary" size={4}>
							{Liferay.Language.get(
								'just-a-few-more-steps-to-set-up-your-workspace'
							)}
						</Text>
					</p>
				</div>
			</ClayModal.Body>

			<ClayModal.Footer
				className="d-flex justify-content-center"
				last={
					<ClayButton.Group spaced>
						<ClayButton
							autoFocus
							className="button-root"
							displayType="primary"
							onClick={() => onNext()}
						>
							{Liferay.Language.get('next')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
};

export default Welcome;
