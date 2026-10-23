import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import propertyBuildingsURL from '../../../../../images/backgrounds/ac_property_buildings.svg?url';
import React from 'react';

interface IWelcomeProps {
	groupId?: string;
	onNext: (increment?: number) => void;
}

const Welcome: React.FC<IWelcomeProps> = ({onNext}) => (
	<div className="pt-4">
		<ClayModal.Body className="d-flex flex-column align-items-center">

			{/* TODO: LRAC-7427 Adjust SVGs with Linear Gradients */}
			<img
				alt=""
				className="mw-100"
				height={150}
				src={propertyBuildingsURL}
				width={700}
			/>

			<h2 className="h4">
				{Liferay.Language.get('introducing-properties')}
			</h2>

			<span className="d-block mb-4 text-secondary">
				{Liferay.Language.get(
					'we-ve-upgraded-your-analytics-cloud-workspace-to-include-properties'
				)}
			</span>

			<ul className="mb-0 text-secondary" style={{maxWidth: 588}}>
				<li>
					<p className="font-weight-semi-bold mb-3 text-dark">
						{Liferay.Language.get(
							'track-user-activities-across-experiences-that-matter'
						)}
					</p>
					<p className="mb-2 text-dark">
						{Liferay.Language.get(
							'scope-user-sessions-to-their-intended-experiences-by-combining-sites-for-each-property'
						)}
					</p>
					<p>
						{Liferay.Language.get(
							'for-existing-workspaces-we-scoped-each-datasource-to-its-own-property-this-may-have-affected-some-of-your-existing-segments'
						)}
					</p>
				</li>
				<li>
					<p className="font-weight-semi-bold mb-3 text-dark">
						{Liferay.Language.get(
							'take-control-of-the-data-access-with-property-permissions'
						)}
					</p>
					<p className="mb-2 text-dark">
						{Liferay.Language.get(
							'flexible-user-access-controls-with-custom-permissioning-for-each-property'
						)}
					</p>
				</li>
			</ul>
		</ClayModal.Body>

		<ClayModal.Footer
			className="d-flex justify-content-end"
			last={
				<ClayButton.Group spaced>
					<ClayButton
						autoFocus
						className="button-root"
						displayType="secondary"
						onClick={() => onNext()}
					>
						{Liferay.Language.get('next')}
					</ClayButton>
				</ClayButton.Group>
			}
		/>
	</div>
);

export default Welcome;
