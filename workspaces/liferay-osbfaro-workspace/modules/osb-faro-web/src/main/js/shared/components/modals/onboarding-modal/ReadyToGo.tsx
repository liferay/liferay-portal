import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import readyToUseURL from '../../../../../images/backgrounds/ac_ready_to_use.svg?url';
import React from 'react';
import {Text} from '@clayui/core';

interface IReadyToGoProps {
	onClose: () => void;
}

const ReadyToGo: React.FC<IReadyToGoProps> = ({onClose}) => (
	<>
		<ClayModal.Header />

		<ClayModal.Body>

			{/* TODO: LRAC-7427 Adjust SVGs with Linear Gradients */}
			<img
				alt=""
				className="d-block mb-4 w-100"
				height={300}
				src={readyToUseURL}
			/>

			<div className="text-center mb-4">
				<Text size={6} weight="bold">
					{Liferay.Language.get('you-are-ready-to-go')}
				</Text>

				<p>
					<Text color="secondary" size={4}>
						{Liferay.Language.get('your-workspace-is-all-set-up')}
					</Text>
				</p>
			</div>

			<div className="text-center">
				<p>
					<Text color="secondary">
						{Liferay.Language.get(
							'tracking-will-start-immediately-however-it-may-take-some-time-for-data-to-appear-in-your-workspace'
						)}
					</Text>
				</p>

				<p>
					<Text color="secondary">
						{Liferay.Language.get(
							'make-sure-to-set-your-time-period-to-last-24-hours-to-see-if-your-data-is-coming-in-correctly'
						)}
					</Text>
				</p>
			</div>
		</ClayModal.Body>

		<ClayModal.Footer
			className="d-flex justify-content-end"
			last={
				<ClayButton.Group spaced>
					<ClayButton
						className="button-root"
						displayType="primary"
						onClick={onClose}
					>
						{Liferay.Language.get('done')}
					</ClayButton>
				</ClayButton.Group>
			}
		/>
	</>
);

export default ReadyToGo;
