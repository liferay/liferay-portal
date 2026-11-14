import BundleRouter from 'route-middleware/BundleRouter';
import IndividualsDashboard from 'individual/dashboard/pages';
import IndividualsDashboardCDP from 'individual/dashboard/pages/IndividualsDashboardCDP';
import React from 'react';
import SectionApp from '../SectionApp';
import {LDPElement} from 'shared/components/LDPLayout';

interface IIndividualsAppProps {
	channelId: string;
	groupId: string;
}

const IndividualsApp: React.FC<IIndividualsAppProps> = (props) => (
	<SectionApp
		{...props}
		path="contacts/individuals"
		routes={[
			{
				element: (
					<LDPElement
						fallback={
							<BundleRouter
								data={IndividualsDashboard}
								destructured={false}
							/>
						}
					>
						<BundleRouter
							data={IndividualsDashboardCDP}
							destructured={false}
						/>
					</LDPElement>
				),
				path: 'contacts/individuals/*',
			},
		]}
	/>
);

export default IndividualsApp;
