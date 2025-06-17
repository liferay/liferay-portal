import ClayButton from '@clayui/button';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import {sub} from 'shared/util/lang';

class NoResultsDisplayKit extends React.Component {
	render() {
		return (
			<div
				className={
					this.props.className ? ` ${this.props.className}` : ''
				}
			>
				<NoResultsDisplay title='data sources' />

				<hr />

				<NoResultsDisplay
					icon={{symbol: 'ac_individual'}}
					title={sub(Liferay.Language.get('there-are-no-x-found'), [
						Liferay.Language.get('individuals')
					])}
				/>

				<hr />

				<NoResultsDisplay
					description='This is a description'
					icon={{symbol: 'ac_individual'}}
					title='No Results Title'
				/>

				<hr />

				<NoResultsDisplay
					description='This is a description'
					icon={{symbol: 'ac_individual'}}
					primary
					title='No Results Title'
				>
					<ClayButton className='button-root' displayType='secondary'>
						{'Action Button'}
					</ClayButton>
				</NoResultsDisplay>

				<hr />

				<div className='align-items-center d-flex'>
					<NoResultsDisplay
						description='This is a description'
						icon={{border: false, size: 'sm', symbol: 'home'}}
						title='No Resuls Title'
					>
						<ClayButton
							className='button-root'
							displayType='secondary'
							size='sm'
						>
							{'click'}
						</ClayButton>
					</NoResultsDisplay>
					<NoResultsDisplay
						description='This is a description'
						icon={{border: false, size: 'md', symbol: 'home'}}
						title='No Resuls Title'
					>
						<ClayButton
							className='button-root'
							displayType='secondary'
							size='sm'
						>
							{'click'}
						</ClayButton>
					</NoResultsDisplay>
					<NoResultsDisplay
						description='This is a description'
						icon={{border: false, size: 'lg', symbol: 'home'}}
						title='No Resuls Title'
					>
						<ClayButton
							className='button-root'
							displayType='secondary'
							size='sm'
						>
							{'click'}
						</ClayButton>
					</NoResultsDisplay>
				</div>
				<div className='align-items-center d-flex'>
					<NoResultsDisplay
						description='This is a description'
						icon={{border: false, size: 'xl', symbol: 'home'}}
						title='No Resuls Title'
					>
						<ClayButton
							className='button-root'
							displayType='secondary'
							size='sm'
						>
							{'click'}
						</ClayButton>
					</NoResultsDisplay>
					<NoResultsDisplay
						description='This is a description'
						icon={{border: false, size: 'xxl', symbol: 'home'}}
						title='No Resuls Title'
					>
						<ClayButton
							className='button-root'
							displayType='secondary'
							size='sm'
						>
							{'click'}
						</ClayButton>
					</NoResultsDisplay>
					<NoResultsDisplay
						description='This is a description'
						icon={{border: false, size: 'xxxl', symbol: 'home'}}
						title='No Resuls Title'
					>
						<ClayButton
							className='button-root'
							displayType='secondary'
							size='sm'
						>
							{'click'}
						</ClayButton>
					</NoResultsDisplay>
				</div>
			</div>
		);
	}
}

export default NoResultsDisplayKit;
